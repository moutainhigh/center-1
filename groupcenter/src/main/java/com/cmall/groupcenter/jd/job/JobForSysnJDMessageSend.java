package com.cmall.groupcenter.jd.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.helpers.LogLog;
import org.json.JSONObject;
import org.quartz.JobExecutionContext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cmall.groupcenter.jd.JdSyncOrderStatusSupport;
import com.cmall.groupcenter.service.RsyncJDService;
import com.cmall.ordercenter.alipay.util.JsonUtil;
import com.srnpr.xmassystem.homehas.RsyncJingdongSupport;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.webmodel.MWebResult;

public class JobForSysnJDMessageSend extends RootJob {
	final static String successCode = "0000";// 查询成功返回编码

	@Override
	public void doExecute(JobExecutionContext context) {
		LogLog.debug("京东消息推送接口定时方法" + this.getClass().getName() + "启动");

		/*
		 * type=2（代表商品价格变更）：如果商品成本价变更了，直接更改商品成本价，商品做下架处理 
		 * type=4（商品上下架变更消息）：商品下架
		 * type=5（订单已妥投）：更新订单状态为交易成功 
		 * type=6（添加、删除商品池内商品）：删除(删除的sku编码商品做下架处理)
		 * type=10（订单取消）：惠家有订单做取消操作，如果订单已支付，不允许取消 t
		 * type=13 :换货新订单号同步
		 * ype=14（支付失败）：再次调用2.1.4接口   （单独处理）
		 * type=16（商品介绍及规格参数变更）：调用同步商品 （无处理必要）
		 * type=50（京东地址变更）：更新京东地址表，匹配惠家有sc_tmp表地址
		 */

		// 1.同步发起京东支付
		this.confirmJDOrderIsPayed();
		// 2.商品价格变更
		this.sysnChangePrice();
		//3.商品上下架变更
		this.sysnJDProductState();
		//4.订单妥投信息同步
		this.sysnOrderState();
		//5.添加、删除商品池内商品(删除的sku编码商品做下架处理)
		this.sysnDownProduct();
		//6.订单取消
		this.sysnCancelOrder();
		//7.京东地址变更
		this.sysnChangeJDAddress();
		//8.换货新订单号同步
		this.sysnAfterSaleNewOrderId();
		//16.调用同步商品名称
		this.sysnJDProductName();

	}


	private void sysnJDProductName() {
	      // 1.同步获取推送消息
					Set<JSONObject> paramList = new HashSet<JSONObject>();
					paramList = this.getMessageParam("16","skuId");
					String createTime = FormatHelper.upDateTime();
				   //2.消息回参入库
					if(paramList.size()>0) {
						for (JSONObject param : paramList) {
							String skuId = param.get("skuId").toString();
							int count = DbUp.upTable("pc_jingdong_choosed_products").dataCount("jd_sku_id=:jd_sku_id", new MDataMap("jd_sku_id",skuId));
							if(count>0) {
							DbUp.upTable("sc_jd_message").insert("uid",WebHelper.upUuid(),"jd_message_type","16","jd_return_param",param.toString(),"done_flag","0","create_time",createTime,"sendId",param.get("sendId").toString());
						}
							}
					}
					//3.查库进行业务处理
					List<Map<String, Object>> resultList = DbUp.upTable("sc_jd_message").dataSqlList("select * from sc_jd_message where jd_message_type='16' and done_flag='0' and create_time > DATE_SUB(NOW(),INTERVAL 60 MINUTE)", null);
					List<String> uidList = new ArrayList<String>();
					if(resultList!=null&&resultList.size()>0) {
						for (Map<String, Object> map : resultList) {
							net.sf.json.JSONObject jsonObj = net.sf.json.JSONObject.fromObject(map.get("jd_return_param"));
							String skuId = jsonObj.get("skuId").toString();
							MWebResult result = RsyncJDService.sysnSkuProductName(skuId);
							if(result.getResultCode()!=0) {
								uidList.add(map.get("uid").toString());
							}
						}
					}
					if(uidList.size()>0) {
						/*
						StringBuffer uidSB = new StringBuffer();
						for (String uid : uidList) {
							uidSB.append("'" + uid + "',");
						}
						String sql = "update sc_jd_message set done_flag='1' where uid in ("
								+ uidSB.substring(0, uidSB.length() - 1).toString() + ")";
						DbUp.upTable("sc_jd_message").dataExec(sql, null);
						*/
						delJdMessage(uidList);
					}
	}


	private void sysnAfterSaleNewOrderId() {
		// TODO Auto-generated method stub
		 // 1.同步获取推送消息
        Set<JSONObject> paramList = new HashSet<JSONObject>();
		paramList = this.getMessageParam("13","afsServiceId","orderId");
		String createTime = FormatHelper.upDateTime();
	   //2.消息回参入库
		if(paramList.size()>0) {
			for (JSONObject param : paramList) {
				//防止有相同的消息Id推送
				//if(DbUp.upTable("sc_jd_message").dataCount("sendId=:sendId",new MDataMap("sendId",param.get("sendId").toString()))==0) {
					int count = DbUp.upTable("oc_order_jd_after_sale").dataCount("afs_service_id=:afs_service_id", new MDataMap("afs_service_id",param.get("afsServiceId").toString()));
					if(count>0) {
						DbUp.upTable("sc_jd_message").insert("uid",WebHelper.upUuid(),"jd_message_type","13","jd_return_param",param.toString(),"done_flag","0","create_time",createTime,"sendId",param.get("sendId").toString());
					}
					
				//}
			}
		}
		//3.新订单号同步
		List<Map<String, Object>> resultList = DbUp.upTable("sc_jd_message").dataSqlList("select * from sc_jd_message where jd_message_type='13' and done_flag='0' and create_time > DATE_SUB(NOW(),INTERVAL 60 MINUTE)", null);
		List<String> uidList = new ArrayList<String>();
		if(resultList!=null&&resultList.size()>0) {
			for (Map<String, Object> map : resultList) {
				net.sf.json.JSONObject jsonObj = net.sf.json.JSONObject.fromObject(map.get("jd_return_param"));
				String afsServiceId = jsonObj.get("afsServiceId").toString();
				String orderId = jsonObj.get("orderId").toString();
				int c=DbUp.upTable("oc_order_jd_after_sale").dataUpdate(new MDataMap("afs_service_id",afsServiceId,"afs_jd_order_id",orderId), "afs_jd_order_id", "afs_service_id");
				if(c>0) {uidList.add(map.get("uid").toString());}
			}
		}
		if(uidList.size()>0) {
			/*
			StringBuffer uidSB = new StringBuffer();
			for (String uid : uidList) {
				uidSB.append("'" + uid + "',");
			}
			String sql = "update sc_jd_message set done_flag='1' where uid in ("
					+ uidSB.substring(0, uidSB.length() - 1).toString() + ")";
			DbUp.upTable("sc_jd_message").dataExec(sql, null);
			*/
			delJdMessage(uidList);
		}
		
		
	}


	private void sysnChangeJDAddress() {
		 // 1.同步获取推送消息
        Set<JSONObject> paramList = new HashSet<JSONObject>();
		paramList = this.getMessageParam("50","areaId","areaName","parentId","areaLevel","operateType");
		String createTime = FormatHelper.upDateTime();
	   //2.消息回参入库
		if(paramList.size()>0) {
			for (JSONObject param : paramList) {
				//if(DbUp.upTable("sc_jd_message").dataCount("sendId=:sendId",new MDataMap("sendId",param.get("sendId").toString()))==0) {
					DbUp.upTable("sc_jd_message").insert("uid",WebHelper.upUuid(),"jd_message_type","50","jd_return_param",param.toString(),"done_flag","0","create_time",createTime,"sendId",param.get("sendId").toString());
				//}
			}
		}
		//3.查库进行业务处理
		List<Map<String, Object>> resultList = DbUp.upTable("sc_jd_message").dataSqlList("select * from sc_jd_message where jd_message_type='50' and done_flag='0' and create_time > DATE_SUB(NOW(),INTERVAL 60 MINUTE)", null);
		List<String> uidList = new ArrayList<String>();
		if(resultList!=null&&resultList.size()>0) {
			for (Map<String, Object> map : resultList) {
				net.sf.json.JSONObject jsonObj = net.sf.json.JSONObject.fromObject(map.get("jd_return_param"));
				//京东地址信息
				String areaId = jsonObj.get("areaId").toString();
				String areaName = jsonObj.get("areaName").toString();
				String parentId = jsonObj.get("parentId").toString();
				String areaLevel = jsonObj.get("areaLevel").toString();
				String operateType = jsonObj.get("operateType").toString();
				String mailContent = "{地址编码areaId:"+areaId+",地址名称areaName:"+areaName+",地址父Id编码parentId:"+parentId+",地址等级areaLevel:"+areaLevel+"}";
			    //操作类型(插入数据为1，更新时为2，删除时为3)
				boolean flag =false;
				if("1".equals(operateType)) {
					String[] receivers = bConfig("familyhas.mail_receive_JD").split(",");
					for (String receiver : receivers) {
						 flag = MailSupport.INSTANCE.sendMail(receiver,
								"京东地址变化","新增数据"+mailContent);
					}	    	
			    }
			    if("2".equals(operateType)) {
			    	String[] receivers = bConfig("familyhas.mail_receive_JD").split(",");
					for (String receiver : receivers) {
						 flag = MailSupport.INSTANCE.sendMail(receiver,
								"京东地址变化","更新数据"+mailContent);
					}	
			    }
			    if("3".equals(operateType)) {
			    	String[] receivers = bConfig("familyhas.mail_receive_JD").split(",");
					for (String receiver : receivers) {
						 flag = MailSupport.INSTANCE.sendMail(receiver,
								"京东地址变化","删除数据"+mailContent);
					}	
			    }
			    if(flag) {
			    	uidList.add(map.get("uid").toString());
			    }
			}
		}
		if(uidList.size()>0) {
			/*
			StringBuffer uidSB = new StringBuffer();
			for (String uid : uidList) {
				uidSB.append("'" + uid + "',");
			}
			String sql = "update sc_jd_message set done_flag='1' where uid in ("
					+ uidSB.substring(0, uidSB.length() - 1).toString() + ")";
			DbUp.upTable("sc_jd_message").dataExec(sql, null);
			*/
			delJdMessage(uidList);
		}
		
	}

	
	private void sysnCancelOrder() {
		 // 1.同步获取推送消息
        Set<JSONObject> paramList = new HashSet<JSONObject>();
		paramList = this.getMessageParam("10","orderId");
		String createTime = FormatHelper.upDateTime();
	   //2.消息回参入库
		if(paramList.size()>0) {
			for (JSONObject param : paramList) {
				String orderId = param.get("orderId").toString();
				int count = DbUp.upTable("oc_order_jd").dataCount("jd_order_id=:jd_order_id", new MDataMap("jd_order_id",orderId));
				if(count>0) {
					//if(DbUp.upTable("sc_jd_message").dataCount("sendId=:sendId",new MDataMap("sendId",param.get("sendId").toString()))==0) {
					DbUp.upTable("sc_jd_message").insert("uid",WebHelper.upUuid(),"jd_message_type","10","jd_return_param",param.toString(),"done_flag","0","create_time",createTime,"sendId",param.get("sendId").toString());	
				//}
				}
			}
		}
		//3.查库进行业务处理
		List<Map<String, Object>> resultList = DbUp.upTable("sc_jd_message").dataSqlList("select * from sc_jd_message where jd_message_type='10' and done_flag='0' and create_time > DATE_SUB(NOW(),INTERVAL 60 MINUTE)", null);
		List<String> uidList = new ArrayList<String>();
		if(resultList!=null&&resultList.size()>0) {
			for (Map<String, Object> map : resultList) {
				net.sf.json.JSONObject jsonObj = net.sf.json.JSONObject.fromObject(map.get("jd_return_param"));
				//京东订单编号
				String orderId = jsonObj.get("orderId").toString();
				JdSyncOrderStatusSupport support = new JdSyncOrderStatusSupport();
				support.syncOrderStatus(orderId);
				uidList.add(map.get("uid").toString());
				
			}
		}
		if(uidList.size()>0) {
			/*
			StringBuffer uidSB = new StringBuffer();
			for (String uid : uidList) {
				uidSB.append("'" + uid + "',");
			}
			String sql = "update sc_jd_message set done_flag='1' where uid in ("
					+ uidSB.substring(0, uidSB.length() - 1).toString() + ")";
			DbUp.upTable("sc_jd_message").dataExec(sql, null);
			*/
			delJdMessage(uidList);
		}
		
	}

	private void sysnDownProduct() {
		 // 1.同步获取推送消息
        Set<JSONObject> paramList = new HashSet<JSONObject>();
		paramList = this.getMessageParam("6","skuId","page_num","state");
		String createTime = FormatHelper.upDateTime();
	   //2.消息回参入库
		if(paramList.size()>0) {
			for (JSONObject param : paramList) {
				String skuId = param.get("skuId").toString();
				int count = DbUp.upTable("pc_jingdong_choosed_products").dataCount("jd_sku_id=:jd_sku_id", new MDataMap("jd_sku_id",skuId));
				if(count>0) {
				//if(DbUp.upTable("sc_jd_message").dataCount("sendId=:sendId",new MDataMap("sendId",param.get("sendId").toString()))==0) {
				DbUp.upTable("sc_jd_message").insert("uid",WebHelper.upUuid(),"jd_message_type","6","jd_return_param",param.toString(),"done_flag","0","create_time",createTime,"sendId",param.get("sendId").toString());
			    //}
				}
			}
		}
		//3.查库进行业务处理
		List<Map<String, Object>> resultList = DbUp.upTable("sc_jd_message").dataSqlList("select * from sc_jd_message where jd_message_type='6' and done_flag='0' and create_time > DATE_SUB(NOW(),INTERVAL 60 MINUTE)", null);
		List<String> uidList = new ArrayList<String>();
		if(resultList!=null&&resultList.size()>0) {
			for (Map<String, Object> map : resultList) {
				net.sf.json.JSONObject jsonObj = net.sf.json.JSONObject.fromObject(map.get("jd_return_param"));
				String skuId = jsonObj.get("skuId").toString();
				//1是添加(不处理，需要根据列表清单上的商品为准)，2是删除
				String state = jsonObj.get("state").toString();
				//商品池编号
				//String page_num = jsonObj.get("page_num").toString();
				if("2".equals(state)) {
					MWebResult result = RsyncJDService.sysnJDProductState(skuId);
					if(result.getResultCode()==1) {
						 uidList.add(map.get("uid").toString());
					}
				}
				else {
					//添加商品不处理
					 uidList.add(map.get("uid").toString());
				}
			}
		}
		if(uidList.size()>0) {
			/*
			StringBuffer uidSB = new StringBuffer();
			for (String uid : uidList) {
				uidSB.append("'" + uid + "',");
			}
			String sql = "update sc_jd_message set done_flag='1' where uid in ("
					+ uidSB.substring(0, uidSB.length() - 1).toString() + ")";
			DbUp.upTable("sc_jd_message").dataExec(sql, null);
			*/
			delJdMessage(uidList);
		}
		
	}

	private void sysnOrderState() {
           // 1.同步获取推送消息
        Set<JSONObject> paramList = new HashSet<JSONObject>();
			paramList = this.getMessageParam("5","orderId","state");
			String createTime = FormatHelper.upDateTime();
		   //2.消息回参入库
			if(paramList.size()>0) {
				for (JSONObject param : paramList) {
					String orderId = param.get("orderId").toString();
					int count = DbUp.upTable("oc_order_jd").dataCount("jd_order_id=:jd_order_id", new MDataMap("jd_order_id",orderId));
					if(count>0) {
						//if(DbUp.upTable("sc_jd_message").dataCount("sendId=:sendId",new MDataMap("sendId",param.get("sendId").toString()))==0) {
					DbUp.upTable("sc_jd_message").insert("uid",WebHelper.upUuid(),"jd_message_type","5","jd_return_param",param.toString(),"done_flag","0","create_time",createTime,"sendId",param.get("sendId").toString());
						//}
				    }
					}
			}
			//3.查库进行业务处理
			List<Map<String, Object>> resultList = DbUp.upTable("sc_jd_message").dataSqlList("select * from sc_jd_message where jd_message_type='5' and done_flag='0' and create_time > DATE_SUB(NOW(),INTERVAL 60 MINUTE)", null);
			List<String> uidList = new ArrayList<String>();
			if(resultList!=null&&resultList.size()>0) {
				for (Map<String, Object> map : resultList) {
					net.sf.json.JSONObject jsonObj = net.sf.json.JSONObject.fromObject(map.get("jd_return_param"));
					String orderId = jsonObj.get("orderId").toString();
					//1是妥投，2是拒收
					//String state = jsonObj.get("state").toString();
					JdSyncOrderStatusSupport support = new JdSyncOrderStatusSupport();
					support.syncOrderStatus(orderId);
					uidList.add(map.get("uid").toString());
					
				}
			}
			if(uidList.size()>0) {
				/*
				StringBuffer uidSB = new StringBuffer();
				for (String uid : uidList) {
					uidSB.append("'" + uid + "',");
				}
				String sql = "update sc_jd_message set done_flag='1' where uid in ("
						+ uidSB.substring(0, uidSB.length() - 1).toString() + ")";
				DbUp.upTable("sc_jd_message").dataExec(sql, null);
				*/
				delJdMessage(uidList);
			}
		
	}

	private void sysnJDProductState() {
	           // 1.同步获取推送消息
		        Set<JSONObject> paramList = new HashSet<JSONObject>();
				paramList = this.getMessageParam("4","skuId","state");
				String createTime = FormatHelper.upDateTime();
			   //2.消息回参入库
				if(paramList.size()>0) {
					for (JSONObject param : paramList) {
						String skuId = param.get("skuId").toString();
						int count = DbUp.upTable("pc_jingdong_choosed_products").dataCount("jd_sku_id=:jd_sku_id", new MDataMap("jd_sku_id",skuId));
						if(count>0) {
						//if(DbUp.upTable("sc_jd_message").dataCount("sendId=:sendId",new MDataMap("sendId",param.get("sendId").toString()))==0) {
						DbUp.upTable("sc_jd_message").insert("uid",WebHelper.upUuid(),"jd_message_type","4","jd_return_param",param.toString(),"done_flag","0","create_time",createTime,"sendId",param.get("sendId").toString());
						//}
					    }
						}
				}
				//3.查库进行业务处理
				List<Map<String, Object>> resultList = DbUp.upTable("sc_jd_message").dataSqlList("select * from sc_jd_message where jd_message_type='4' and done_flag='0' and create_time > DATE_SUB(NOW(),INTERVAL 60 MINUTE)", null);
				List<String> uidList = new ArrayList<String>();
				if(resultList!=null&&resultList.size()>0) {
					for (Map<String, Object> map : resultList) {
						net.sf.json.JSONObject jsonObj = net.sf.json.JSONObject.fromObject(map.get("jd_return_param"));
						String skuId = jsonObj.get("skuId").toString();
						MWebResult result = RsyncJDService.sysnJDProductState(skuId);
						if(result.getResultCode()!=0) {
							uidList.add(map.get("uid").toString());
						}
					}
				}
				if(uidList.size()>0) {
					/*
					StringBuffer uidSB = new StringBuffer();
					for (String uid : uidList) {
						uidSB.append("'" + uid + "',");
					}
					String sql = "update sc_jd_message set done_flag='1' where uid in ("
							+ uidSB.substring(0, uidSB.length() - 1).toString() + ")";
					DbUp.upTable("sc_jd_message").dataExec(sql, null);
					*/
					delJdMessage(uidList);
				}
	}

	private void sysnChangePrice() {
		       // 1.同步获取推送消息
				Set<JSONObject> paramList = new HashSet<JSONObject>();
				paramList = this.getMessageParam("2","skuId");
				String createTime = FormatHelper.upDateTime();
			   //2.消息回参入库
				if(paramList.size()>0) {
					for (JSONObject param : paramList) {
						String skuId = param.get("skuId").toString();
						int count = DbUp.upTable("pc_jingdong_choosed_products").dataCount("jd_sku_id=:jd_sku_id", new MDataMap("jd_sku_id",skuId));
						if(count>0) {
							//if(DbUp.upTable("sc_jd_message").dataCount("sendId=:sendId",new MDataMap("sendId",param.get("sendId").toString()))==0) {
						DbUp.upTable("sc_jd_message").insert("uid",WebHelper.upUuid(),"jd_message_type","2","jd_return_param",param.toString(),"done_flag","0","create_time",createTime,"sendId",param.get("sendId").toString());
							//}
					}
						}
				}
				//3.查库进行业务处理
				List<Map<String, Object>> resultList = DbUp.upTable("sc_jd_message").dataSqlList("select * from sc_jd_message where jd_message_type='2' and done_flag='0' and create_time > DATE_SUB(NOW(),INTERVAL 60 MINUTE)", null);
				List<String> uidList = new ArrayList<String>();
				if(resultList!=null&&resultList.size()>0) {
					for (Map<String, Object> map : resultList) {
						net.sf.json.JSONObject jsonObj = net.sf.json.JSONObject.fromObject(map.get("jd_return_param"));
						String skuId = jsonObj.get("skuId").toString();
						MWebResult result = RsyncJDService.changeSkuPrice(skuId);
						if(result.getResultCode()!=0) {
							uidList.add(map.get("uid").toString());
						}
					}
				}
				if(uidList.size()>0) {
					/*
					StringBuffer uidSB = new StringBuffer();
					for (String uid : uidList) {
						uidSB.append("'" + uid + "',");
					}
					String sql = "update sc_jd_message set done_flag='1' where uid in ("
							+ uidSB.substring(0, uidSB.length() - 1).toString() + ")";
					DbUp.upTable("sc_jd_message").dataExec(sql, null);
					*/
					delJdMessage(uidList);
				}
	}

	private Set<JSONObject> getMessageParam(String messageType,String ...resultParams) {
		
        //返回参数集合和消息id集合
		Set<JSONObject> paramSet = new HashSet<JSONObject>();
		List<String> messageIdList = new ArrayList<String>();
        //调用消息推送接口
		Map<String, Object> paramtMap = new HashMap<String, Object>();
		paramtMap.put("type", messageType);
		String responStr = null;
		String resp = null;
		try {
			resp = RsyncJingdongSupport.callGateway("biz.message.get", paramtMap);
			responStr = RsyncJDService.getJsonValue(resp, "biz_message_get_response");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			LogLog.error("biz.message.get "+messageType+": "+resp);
			return paramSet;
		}
		JSONObject jsonObject = JsonUtil.getJsonValues(responStr);
        //返回参数处理
		if (successCode.equals(jsonObject.get("resultCode"))) {
			JSONArray jsonArray = JSON.parseArray(jsonObject.get("result").toString());
			if (jsonArray != null && jsonArray.size() > 0) {
				for (Object object : jsonArray) {
					net.sf.json.JSONObject jsonObj = net.sf.json.JSONObject.fromObject(object);		
					net.sf.json.JSONObject subObject =net.sf.json.JSONObject.fromObject( jsonObj.get("result"));
					JSONObject paramObj = new JSONObject();
					for (int i = 0;i<resultParams.length;i++) {
						Object obj = subObject.get(resultParams[i]);
						paramObj.put(resultParams[i], subObject.get(resultParams[i]));
					}
					//添加推送id,防止一个推送进行多次推送
					paramObj.put("sendId", jsonObj.getString("id"));
					paramSet.add(paramObj);
					// 消息Id集合
					messageIdList.add(jsonObj.getString("id"));
				}
			}
		}
		// 递归查询
		if (paramSet.size() > 0) {
			this.delMessageId(messageIdList);
			paramSet.addAll(this.getMessageParam(messageType,resultParams));
		}
		return paramSet;
		
	}

	private void delMessageId(List<String> messageIdList) {
		Map<String, Object> paramtMap = new HashMap<String, Object>();
		for (String mId : messageIdList) {
			try {
				paramtMap.put("id", Long.parseLong(mId));
				String retrunStr = null;
				try {
					retrunStr = RsyncJingdongSupport.callGateway("biz.message.del", paramtMap);
				} catch (Exception e) {
					//e.printStackTrace();
					LogLog.error("biz.message.del: "+retrunStr);
					continue;
				}
				
				 net.sf.json.JSONObject returnResult = RsyncJDService.getJSONStrVal(
						 retrunStr, "biz_message_del_response");
				if (!successCode.equals(returnResult.get("resultCode").toString())) {
					LogLog.error("------删除消息方法：delMessageId(List<String> messageIdList)出错-------:\r\n"+retrunStr);
				}
			} catch (Exception e) {
				LogLog.error("------删除消息方法：delMessageId(List<String> messageIdList)出错-------");
				e.printStackTrace();

			} finally {
				paramtMap.clear();
			}
		}
	}
	
	private void confirmJDOrderIsPayed() {

		// 1.同步获取支付失败订单
		Set<String> orderList = new HashSet<String>();
		orderList = this.syncFailedOrder();
		// 2.失败标记入库
		if (orderList.size() > 0) {
			StringBuffer jdOrders = new StringBuffer();
			for (String order : orderList) {
				int count = DbUp.upTable("oc_order_jd").dataCount("jd_order_id=:jd_order_id", new MDataMap("jd_order_id",order));
				if(count>0) {
				jdOrders.append("'" + order + "',");
				}
			}
			String sql = "update oc_order_jd set payed_flag='0' where jd_order_id in ("
					+ jdOrders.substring(0, jdOrders.length() - 1).toString() + ")";
			DbUp.upTable("oc_order_jd").dataExec(sql, null);
		}

		// 3.对库中的所有支付失败的订单进行"发起支付接口"调用
		List<Map<String, Object>> list = DbUp.upTable("oc_order_jd").dataSqlList(
				"select jd_order_id from oc_order_jd where payed_flag=:payed_flag", new MDataMap("payed_flag", "0"));
		Map<String, Object> paramtMap = new HashMap<String, Object>();
		if (list != null && list.size() > 0) {
			for (Map<String, Object> map : list) {
				// 3.1进行接口调用
				paramtMap.put("jdOrderId", map.get("jd_order_id"));
				String responStr = null;
				String resp = null;
				try {
					resp = RsyncJingdongSupport.callGateway("biz.order.doPay", paramtMap);
					responStr = RsyncJDService.getJsonValue(resp, "biz_order_doPay_respons");;
				} catch (Exception e) {
					//e.printStackTrace();
					LogLog.error("biz.order.doPay: "+resp);
					continue;
				}
				
				String resultCode = RsyncJDService.getJsonValue(responStr, "resultCode");
				if (successCode.equals(resultCode)) {
					DbUp.upTable("oc_order_jd").dataExec(
							"update oc_order_jd set payed_flag='1' where jd_order_id ='" + map.get("jd_order_id") + "'",
							null);
				}
				else {
					String[] receivers = bConfig("familyhas.mail_receive_JD").split(",");
					for (String receiver : receivers) {
						 MailSupport.INSTANCE.sendMail(receiver,
								"京东发起支付失败",",失败京东订单号为:"+ map.get("jd_order_id"));
					}	
				}
				paramtMap.clear();
			}
		}
	}

	private Set<String> syncFailedOrder() {

		// 1.调用京东信息推送接口,获取京东支付失败的订单号
		List<String> messageIdList = new ArrayList<String>();
		Set<String> orderSet = new HashSet<String>();
		Map<String, Object> paramtMap = new HashMap<String, Object>();
		paramtMap.put("type", "14");

		String responStr = null;
		String resp = null;
		try {
			resp = RsyncJingdongSupport.callGateway("biz.message.get", paramtMap);
			responStr = RsyncJDService.getJsonValue(resp, "biz_message_get_response");
		} catch (Exception e) {
			//e.printStackTrace();
			LogLog.error("biz.message.get 14: "+resp);
			return orderSet;
		}
		
		JSONObject jsonObject = JsonUtil.getJsonValues(responStr);

		if (successCode.equals(jsonObject.get("resultCode"))) {
			JSONArray jsonArray = JSON.parseArray(jsonObject.optString("result"));
			if (jsonArray != null && jsonArray.size() > 0) {
				for(int i = 0;i<jsonArray.size();i++) {
					String jdOrdId = jsonArray.getJSONObject(i).getJSONObject("result").getString("orderId");
					orderSet.add(jdOrdId);
					// 消息Id集合
					messageIdList.add(jsonArray.getJSONObject(i).getString("id"));
				}
			}
		}
		// 递归查询
		if (orderSet.size() > 0) {
			this.delMessageId(messageIdList);
			orderSet.addAll(this.syncFailedOrder());
		}
		return orderSet;
	}
	
	private void delJdMessage(List<String> uidList) {
		if(uidList != null && !uidList.isEmpty()) {
			String sql = "DELETE FROM sc_jd_message WHERE uid in ('" + StringUtils.join(uidList,"','") + "')";
			DbUp.upTable("sc_jd_message").dataExec(sql, null);
		}
	}

}
