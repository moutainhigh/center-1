package com.cmall.groupcenter.kjt;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.homehas.RsyncKaoLaSupport;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.groupcenter.service.OrderService;
import com.cmall.ordercenter.model.OrderStatusLog;
import com.cmall.ordercenter.service.OrderStatusLogService;
import com.cmall.ordercenter.service.money.CreateMoneyService;
import com.cmall.ordercenter.service.money.ReturnMoneyResult;
import com.cmall.productcenter.common.DateUtil;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.JobExecHelper;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 同步订单状态轨迹
 * 
 * @author zhangbo
 *
 */
public class KLTraceOrder extends RsyncKl {

	final static String noProInfoReturnStr = "\"recCode\":-102";// 无考拉商品信息返回标识字符串

	public RsyncResult doProcess() {
		RsyncResult result = new RsyncResult();
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		// 需要同步的订单状态1:订单同步成功（等待支付）2:订单支付成功（等待发货）4:订单已发货
		String sql = "select order_code from oc_order_kaola_list where status in (1,2,4)";
		List<Map<String, Object>> listMap = DbUp.upTable("oc_order_kaola_list").dataSqlList(sql, null);

		if (listMap != null && listMap.size() > 0) {
			for (Map<String, Object> map : listMap) {
//				if(!"DD229790104".equals(map.get("order_code").toString())) {
//					continue;
//				}
				treeMap.put("thirdPartOrderId", map.get("order_code").toString());
				String returnStr = RsyncKaoLaSupport.doPostRequest("queryOrderStatus", "channelId", treeMap);
				if (!StringUtils.isBlank(returnStr) && !returnStr.contains(noProInfoReturnStr)) {
					JSONObject jo = JSONObject.parseObject(returnStr);
					if(jo == null || jo.getIntValue("recCode") != 200){
						continue;
					}
					Map<String, Object> paramMap = new HashMap<String, Object>();
					// 商品订单编号
					String gorderId = jo.getString("gorderId");
					// 订单支付总金额
					String gpayAmount = jo.getString("gpayAmount");
					// 运费税
					String logisticsTaxAmount = jo.getString("logisticsTaxAmount");
					// 运费
					String totalChinaLogisticsAmount = jo.getString("totalChinaLogisticsAmount");
					// 总税费
					String totalTaxAmount = jo.getString("totalTaxAmount");
					// 结果信息
					String arrayStr = jo.getString("result");
					List<String> ja = JSONArray.parseArray(arrayStr, String.class);
					List<Map<String, Object>> returnList = new ArrayList<>();
										
					// 取参数变量
					String orderId = "";
					String deliverName = "";
									
					// 参数放到外面是要对订单是否更新时间做处理
					MDataMap sMap = new MDataMap();
					// 每次取最新的订单更新时间，有变化就更新，没有就是原来的
					String orderUpdateTime = "";
					List<String> delTraceList = new ArrayList<>();
					
					//同步oc_order_shipments表中的数据（只有订单发货的时候做入库处理）
					MDataMap mapForOrderShip = new MDataMap();
					if (ja != null && ja.size() > 0) {

						for (String obj : ja) {
							// 获取惠家有订单的现状态
							String querSql = "select uid,order_status from oc_orderinfo where order_code=:order_code";
							Map<String, Object> resultMap = DbUp.upTable("oc_orderinfo").dataSqlOne(querSql,
									new MDataMap("order_code", map.get("order_code").toString()));
							String hjyOrderStatus = resultMap.get("order_status").toString();
							// 考拉的订单封装信息
							JSONObject jb = JSONObject.parseObject(obj);
							orderId = jb.getString("orderId");
							deliverName = jb.getString("deliverName");
							sMap.put("order_code", map.get("order_code").toString());
							// gorderId：考拉订单号
							sMap.put("out_order_code", gorderId);
							sMap.put("status", jb.get("status").toString());
							sMap.put("is_limit", Boolean.valueOf(jb.get("isLimit").toString()) == true ? "1" : "0");
							sMap.put("limit_reason", jb.get("limitReason").toString());
							sMap.put("deliver_name", jb.get("deliverName").toString());
							sMap.put("deliver_no", jb.get("deliverNo").toString());
							String sSql = "select update_time,status from oc_order_kaola_list where order_code=:order_code";
							Map<String, Object> m = DbUp.upTable("oc_order_kaola_list").dataSqlOne(sSql,
									new MDataMap("order_code", map.get("order_code").toString()));
							orderUpdateTime = m.get("update_time").toString();
							// 同步过来的考拉订单状态
							String klOrderStatus = jb.get("status").toString();
							// 惠家有订单状态与同步过来的考拉订单状态对比后要确认更新的订单状态,默认为惠家有现有订单状态
							String rsynOrderStatus = hjyOrderStatus;

							// 1:"订单同步成功（等待支付）"
	                   
							// 2: "订单支付成功（等待发货）"
						 if ("2".equals(klOrderStatus)) {
						
								if ("4497153900010003".equals(hjyOrderStatus)) {
									rsynOrderStatus = "4497153900010002";
									delTraceList.add(map.get("order_code").toString());
								}
							}

							// 3: "订单支付失败"
							else if ("3".equals(klOrderStatus)) {
	
									rsynOrderStatus = "4497153900010006";
							}
							// 4:"订单已发货"
							else if ("4".equals(klOrderStatus)) {
								rsynOrderStatus = "4497153900010003";
								if ("4497153900010005".equals(hjyOrderStatus))
									rsynOrderStatus = "4497153900010005";
							}
							// 5:"交易成功"
							else if ("5".equals(klOrderStatus)) {
								rsynOrderStatus = "4497153900010005";
							}
							// 6: "订单交易失败"
							else if ("6".equals(klOrderStatus)) {
								if ("4497153900010002".equals(hjyOrderStatus) || "4497153900010008".equals(hjyOrderStatus)) {
									rsynOrderStatus = "4497153900010006";
								}
							}else if ("7".equals(klOrderStatus)) {
								if ("4497153900010001".equals(hjyOrderStatus)){
									rsynOrderStatus = "4497153900010006";
								}
							}
						 
						 
						 // 考拉状态变更时记录时间
						 if(!klOrderStatus.equals(m.get("status").toString())){
							 orderUpdateTime = DateUtil.getSysDateTimeString();
						 }
						 
						 
						   //判断订单状态是否改变
						   if(!rsynOrderStatus.equals(hjyOrderStatus)) {
							   
							   							   
								FlowBussinessService fs = new FlowBussinessService();
								String flowBussinessUid = resultMap.get("uid")+"";
								String fromStatus = hjyOrderStatus;
								String operater = "system";		
								String toStatus = rsynOrderStatus;
								String flowType = "449715390008";
								
								RootResult ret = fs.ChangeFlow(flowBussinessUid, flowType, fromStatus,toStatus, operater, "", new MDataMap("order_code",map.get("order_code").toString()));
								//DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_code",map.get("order_code").toString(),"order_status",rsynOrderStatus), "order_status", "order_code");
								if(ret.getResultCode() == 1) {
									if("4497153900010006".equals(rsynOrderStatus) && "4497153900010002".equals(hjyOrderStatus)) {
										// 退款单
										CreateMoneyService createMoneyService = new CreateMoneyService();
										ReturnMoneyResult rm = createMoneyService.creatReturnMoney(map.get("order_code").toString(),"system","考拉订单交易失败");
										// 记录异常订单
										//this.rsyncKLExceptionOrder(sMap);
									}
									
									if("4497153900010006".equals(rsynOrderStatus) && "4497153900010008".equals(hjyOrderStatus)) {
										// 退款单
										CreateMoneyService createMoneyService = new CreateMoneyService();
										ReturnMoneyResult rm = createMoneyService.creatReturnMoney(map.get("order_code").toString(),"system","取消发货");
									}
									//取消订单，判断是否是分销单，如果是，写入取消订单分销定时
									if("4497153900010006".equals(rsynOrderStatus) && DbUp.upTable("fh_agent_order_detail").count("order_code",map.get("order_code").toString())>0 && DbUp.upTable("za_exectimer").count("exec_info",map.get("order_code").toString(),"exec_type","449746990029") <= 0) {
										JobExecHelper.createExecInfo("449746990029", map.get("order_code").toString(), DateUtil.getSysDateTimeString());
									}
									//交易成功订单计算可提收益定时
									if("4497153900010005".equals(rsynOrderStatus) && DbUp.upTable("fh_agent_order_detail").count("order_code",map.get("order_code").toString())>0 && DbUp.upTable("za_exectimer").count("exec_info",map.get("order_code").toString(),"exec_type","449746990028") <= 0) {
										JobExecHelper.createExecInfo("449746990028", map.get("order_code").toString(),DateUtil.addMinute(28800));
									}
									
									//交易成功订单发放优惠券定时
									if("4497153900010005".equals(rsynOrderStatus) && DbUp.upTable("fh_share_order_detail").count("order_code",map.get("order_code").toString())>0 && DbUp.upTable("za_exectimer").count("exec_info",map.get("order_code").toString(),"exec_type","449746990033") <= 0) {
										JobExecHelper.createExecInfo("449746990033", map.get("order_code").toString(),DateUtil.addMinute(21600));
									}

								}
						   }
						 			
							
	//-------------------------封装oc_order_shipments数据--------------------------------------------------------										                            
							//订单编号
							mapForOrderShip.put("order_code",map.get("order_code").toString() );
							//物流商家code
							mapForOrderShip.put("logisticse_code","");
							//物流商家name
							mapForOrderShip.put("logisticse_name",jb.get("deliverName").toString());
							//运单号码
							mapForOrderShip.put("waybill", jb.get("deliverNo").toString());
							//创建人
							mapForOrderShip.put("creator", bConfig("familyhas.seller_code_KL"));
							//创建时间
							mapForOrderShip.put("create_time",DateUtil.getSysDateTimeString());
							//发货说明
							mapForOrderShip.put("remark", "");
							//'0：未发 1 ： 已发'
							mapForOrderShip.put("is_send100_flag","1");
							//发送次数
							mapForOrderShip.put("send_count", "0");
							//快递100错误信息
							mapForOrderShip.put("send_remark", "");
							//更新时间
							//mapForOrderShip.put("update_time",DateUtil.getSysDateTimeString() );
							//更新人
							mapForOrderShip.put("update_user",bConfig("familyhas.seller_code_KL") );
							//编码
							//mapForOrderShip.put("shipments_code", );
							
						   //订单状态封装
							mapForOrderShip.put("klOrderStatus", rsynOrderStatus);
	//----------------------------以上------------------------------------------------------					
							
						}
					}
					// 更新物流轨迹信息
					String trackLogistics = jo.getString("trackLogistics");
					JSONObject traObject = JSONObject.parseObject(trackLogistics);
					if (traObject != null && traObject.containsKey(orderId)) {
						List<String> jarr = JSONArray.parseArray(traObject.get(orderId).toString(), String.class);
						// 轨迹集合对象，只有一个
						if (jarr != null && jarr.size() > 0) {
							JSONObject traObj = JSONObject.parseObject(jarr.get(0).toString());
							String state = traObj.getString("state");
							// 考拉的大订单
							String od = gorderId;
							// 运单号
							String billno = traObj.getString("billno");
							// 轨迹列表
							List<String> traObjList = JSONArray.parseArray(traObj.getString("tracks").toString(),
									String.class);
							if (traObjList != null && traObjList.size() > 0) {
								for (String st : traObjList) {
									JSONObject tObj = JSONObject.parseObject(st);
									MDataMap subMap = new MDataMap();

									subMap.put("order_code", map.get("order_code").toString());

									subMap.put("time", tObj.getString("time"));

									subMap.put("waybill", billno);

									subMap.put("logisticse_code", tObj.getString("logisCompanyCode") == null ? ""
											: tObj.getString("logisCompanyCode"));

									subMap.put("context", tObj.getString("context"));
									// 做更新时间对比
//									if (DateUtil.compareTime(orderUpdateTime, tObj.getString("time")) < 0)
//										orderUpdateTime = tObj.getString("time");
									// 因为没有轨迹id,那就根据是否有最新时间的轨迹信息，以时间为条件来判断是否添加该条轨迹内容
									String subSql = "select * from oc_express_detail where order_code=:order_code and time=:time";
									int  num =DbUp.upTable("oc_express_detail").count("order_code",subMap.get("order_code"),"time",subMap.get("time"));
									if (num == 0) {
										String sSql = "insert into  oc_express_detail (order_code,logisticse_code,waybill,context,time) value (:order_code,:logisticse_code,:waybill,:context,:time)";
										DbUp.upTable("oc_express_detail").dataExec(sSql, subMap);
									}
								}
							}
						}
					}
					// 更新订单时间表oc_order_kaola_list信息，而oc_order_kaola_list_detail无需要更新的字段信息，不考虑
					sMap.put("update_time", orderUpdateTime);
					DbUp.upTable("oc_order_kaola_list").dataUpdate(sMap,
							"status,is_limit,limit_reason,update_time,deliver_name,deliver_no",
							"order_code,out_order_code");
					
					//同步oc_order_shipments数据
					mapForOrderShip.put("update_time",orderUpdateTime);
					this.rsyncOrderShipments(mapForOrderShip);
					
					// 删除商户后台改动的订单状态为'已发货'的异常物流信息
					if (delTraceList.size() > 0) {
						StringBuffer tList = new StringBuffer();
						tList.append("(");
						for (String delOrder : delTraceList) {
							tList.append("'" + delOrder + "',");
						}
						String whereSql = tList.toString().substring(0, tList.toString().length() - 1) + ")";
						String delSql = "DELETE FROM oc_express_detail WHERE  order_code in"
								+ whereSql;
						DbUp.upTable("oc_express_detail").dataExec(delSql, null);
						
						String delSql2 = "DELETE FROM oc_order_shipments WHERE  order_code in"
								+ whereSql;
						DbUp.upTable("oc_order_shipments").dataExec(delSql2, null);
					}
					
				}
			}
		}

		return result;

	}

	private void rsyncKLExceptionOrder(MDataMap sMap) {
		// TODO Auto-generated method stub
		//同步考拉异常订单
		int i =DbUp.upTable("oc_order_sanfang_exception").count("hjy_order_code",sMap.get("order_code"),"sf_order_code",sMap.get("out_order_code"));
		if(i==0) {
			MDataMap paramMap = new MDataMap();
			paramMap.put("uid",UUID.randomUUID().toString().replace("-", ""));
			paramMap.put("hjy_order_code",paramMap.get("order_code") );
			paramMap.put("sf_order_code",paramMap.get("out_order_code") );
			paramMap.put("create_time",DateUtil.getSysDateTimeString());
			paramMap.put("reason", paramMap.get("考拉订单同步交易失败"));
			DbUp.upTable("oc_order_sanfang_exception").dataInsert(paramMap);
		}
		
		
	}

	private void rsyncOrderShipments(MDataMap mapForOrderShip) {
		// TODO Auto-generated method stub
		//获取当前考拉的订单状态
		String orderStatus = mapForOrderShip.get("klOrderStatus");
		String sql = "select * from oc_order_shipments where order_code=:order_code";
		Map<String,Object> map =DbUp.upTable("oc_order_shipments").dataSqlOne(sql, new MDataMap("order_code",mapForOrderShip.get("order_code")));
		if(map!=null) {
			mapForOrderShip.remove("create_time");
			DbUp.upTable("oc_order_shipments").dataUpdate(mapForOrderShip,"update_time","order_code");
		}
		//订单发货
		else if("4497153900010003".equals(orderStatus)){
			mapForOrderShip.remove("klOrderStatus");
			DbUp.upTable("oc_order_shipments").dataInsert(mapForOrderShip);
		}
		
	}

	public String addTime(int seconds, String startTime) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			// 预计关单时间（正式下单未支付保留的时间，不用管）
			// long seconds=(long) 0;
			// try {
			// String s = "select create_time from oc_orderinfo where
			// order_code=:order_code";
			// //传递过来的惠家有中存在的orderId(对应的是考拉中的大的gorderId)
			// List<Map<String,Object>> lm = DbUp.upTable("oc_orderinfo").dataSqlList(s,new
			// MDataMap("order_code",map.get("order_code").toString()));
			// String orderCloseTime ="";
			// if(lm!=null&&lm.size()>0) {
			// //预计关闭订单时间 ？
			// orderCloseTime =
			// this.addTime(24*60*60,lm.get(0).get("create_time").toString());
			//
			// }
			// DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// Date time1=format.parse(DateUtil.toSqlTimestampString(new
			// java.sql.Timestamp(System.currentTimeMillis()),"yyyy-MM-dd HH:mm:ss"));
			// Date time2=format.parse(orderCloseTime);
			// Calendar ca1 = Calendar.getInstance();
			// Calendar ca2 = Calendar.getInstance();
			// ca1.setTime(time1);
			// ca2.setTime(time2);
			// seconds =(ca2.getTimeInMillis()-ca1.getTimeInMillis())/(1000);
			// if(seconds>=0)
			// sMap.put("order_close_time",String.valueOf(seconds));
			//
			// } catch (ParseException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			return DateUtil.toString(new java.util.Date(format.parse(startTime).getTime() + seconds * 1000),
					DateUtil.sdfDateTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}