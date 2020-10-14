package com.cmall.groupcenter.job;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncCancelOrder;
import com.cmall.groupcenter.homehas.RsyncControlGiftVoucher;
import com.cmall.groupcenter.homehas.model.RsyncModelGiftVoucher;
import com.cmall.groupcenter.homehas.model.RsyncRequestCancelOrder;
import com.cmall.groupcenter.third.model.GroupRefundInput;
import com.cmall.groupcenter.third.model.GroupRefundResult;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.ordercenter.model.api.GiftVoucherInfo;
import com.cmall.ordercenter.service.money.CreateMoneyService;
import com.cmall.ordercenter.service.money.ReturnMoneyResult;
import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.JobExecHelper;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.websupport.ApiCallSupport;

/***
 * 定时任务取消家有订单
 * @author jlin
 *
 */
public class JobCancelOrderForHome extends RootJob {

	public void doExecute(JobExecutionContext context) {
		List<MDataMap> list=DbUp.upTable("oc_order_cancel_h").queryAll("order_code,out_order_code,update_time,cancel_count,notice_flag,canceler,reason_code", "", "call_flag=:call_flag and out_order_code<>'' and notice_flag=1 ", new MDataMap("call_flag","1"));
		
		if(list!=null&&list.size()>0){
			
			List<Corder> noticeList=new ArrayList<Corder>();
			RsyncCancelOrder cancelOrder=new RsyncCancelOrder();
			
			for (MDataMap mDataMap : list) {
				
				String order_code=mDataMap.get("order_code");
				String out_order_code=mDataMap.get("out_order_code");
				String update_time=mDataMap.get("update_time");
				String canceler=mDataMap.get("canceler");
				int cancel_count=Integer.valueOf(mDataMap.get("cancel_count"));
				int notice_flag=Integer.valueOf(mDataMap.get("notice_flag"));//通知标示，1未通知 0通知
				String reason_code = mDataMap.get("reason_code");;//C05,C07,C08,C0A,C0B,C0E,C0F,C0R,C0S,C0T
				
//				订单写入LD失败，需要实时调用LD接口，取消订单。取消失败，每隔10分钟再调用家有接口取消订单， 20次调用取消失败，则系统发预警（以邮件形式）信息通知运维人员（运维人员可配置）
				if(cancel_count>=20){
					if(notice_flag==1){
						//以邮件的形式通知运维人员   这种情况基本上不会出现，所以不需要再考虑性能问题做其他处理了
						Corder corder= new Corder();
						corder.setOrder_code(order_code);
						corder.setOut_order_code(out_order_code);
						corder.setMessage("达到最大重试次数");
						noticeList.add(corder);
					}
					
					continue;
				}
				
				MDataMap order = new MDataMap();
				String is_pay = "N";
				//非TV品订单
				if("DD".equals(order_code.substring(0, 2)) || "OS".equals(order_code.substring(0, 2)) || "HH".equals(order_code.substring(0, 2))) {
					order = DbUp.upTable("oc_orderinfo").one("order_code",order_code);
					// 排除非LD的订单
					if(StringUtils.isNotBlank(order.get("small_seller_code")) && !order.get("small_seller_code").startsWith("SI")){
						continue;
					}
					
					Map<String, Object> payO = DbUp.upTable("oc_orderinfo").dataSqlOne("select order_code,payed_money,due_money from oc_orderinfo where order_code=:order_code", new MDataMap("order_code",order_code));
					if(payO != null && payO.get("payed_money") != null && payO.get("due_money") != null) {
						//实付款与应付款相等时，表示已支付
						if((new BigDecimal(payO.get("payed_money").toString())).compareTo(new BigDecimal(payO.get("due_money").toString())) == 0) {
							is_pay = "Y";
						}
					}
					
					if(cancel_count>0&&!differMinutes(update_time, 10)){//已经取消过的，判断时间，每隔10分钟调用一次
						continue;
					}
				} else {
					//如果是TV品下单，默认传N
					//is_pay = "N";
				}				
				
				if (out_order_code!=null&&!"".equals(out_order_code)) {
					RsyncRequestCancelOrder request = cancelOrder.upRsyncRequest();
//					request.setCan_rsn_cd("C4"); //取消原因  系统未记取消原因，所以统一使用 取消_顾客_改变心意
					//TV品取消发货和取消订单是同一个接口					
					if("".equals(reason_code)) {
						request.setCan_rsn_cd("system".equals(canceler)?"C0O":"C0T");
					} else {
						request.setCan_rsn_cd(reason_code);
					}					
					request.setMdf_id("app");
					request.setOrd_id(out_order_code);
					request.setSubsystem("app");					
					request.setIs_pay(is_pay);
						
					if(order != null && order.get("seller_code") != null && order.get("order_channel") != null) {
						if (MemberConst.MANAGE_CODE_HPOOL.equals(order.get("seller_code"))||"449747430004".equals(order.get("order_channel"))) {
							request.setMdf_id("web");
							request.setSubsystem("001");
						}
					}
					
					try {
						cancelOrder.doRsync();
						if(cancelOrder.getResponseObject() != null) {
							if(cancelOrder.getResponseObject().isSuccess()) { //App下的TV品订单取消成功的话，生成退款单
								if("DD".equals(order_code.substring(0, 2)) || "OS".equals(order_code.substring(0, 2)) || "HH".equals(order_code.substring(0, 2))) {
									//取消成功的话，订单状态改为交易关闭
									String small_seller_code = order.get("small_seller_code");
									if("SI2003".equals(small_seller_code)) {
										if("".equals(reason_code)) {
											reason_code = "system".equals(canceler)?"C0O":"C0T";
										}
										String remark=(String)DbUp.upTable("oc_return_goods_reason").dataGet("return_reson", "return_reson_code=:return_reson_code", new MDataMap("return_reson_code",reason_code));
										remark="[取消发货]"+remark;
										RootResult res = cancelOrder(order,remark);
										if(res.getResultCode()==1){
											//退返微公社部分
											MDataMap payInfo=DbUp.upTable("oc_order_pay").one("order_code",order_code,"pay_type","449746280009");
											if(payInfo!=null&&!payInfo.isEmpty()){
												GroupRefundInput groupRefundInput = new GroupRefundInput();
												groupRefundInput.setTradeCode(payInfo.get("pay_sequenceid"));
												groupRefundInput.setMemberCode(order.get("buyer_code"));
												groupRefundInput.setRefundMoney(payInfo.get("payed_money"));
												groupRefundInput.setOrderCode(order_code);
												groupRefundInput.setRefundTime(DateUtil.getSysDateTimeString());
												groupRefundInput.setRemark("取消发货");
												groupRefundInput.setBusinessTradeCode(payInfo.get("pay_sequenceid"));//一个流水值退一次
												ApiCallSupport<GroupRefundInput, GroupRefundResult> apiCallSupport=new ApiCallSupport<GroupRefundInput, GroupRefundResult>();
												try {
													apiCallSupport.doCallApi(bConfig("xmassystem.group_pay_url"),bConfig("xmassystem.group_pay_refund_face"),bConfig("xmassystem.group_pay_key"),bConfig("xmassystem.group_pay_pass"), groupRefundInput,new GroupRefundResult());
												} catch (Exception e) {
													//此处暂时流程，退款失败，不影响总流程
													e.printStackTrace();
												}
											}
										}
									}
								}
								//此处新增取消订单定时任务。仅限LD自营品取消
								if(DbUp.upTable("fh_agent_order_detail").count("order_code",order_code)>0 && DbUp.upTable("za_exectimer").count("exec_info",order_code,"exec_type","449746990029") <= 0) {
									JobExecHelper.createExecInfo("449746990029", order_code, DateUtil.addMinute(5));//插入定时任务，五分钟后执行
								}
							} else { //LD订单取消失败的话，状态回退到待发货，不在执行取消的定时
								Corder corder= new Corder();
								corder.setOrder_code(order_code);
								corder.setOut_order_code(out_order_code);
								corder.setMessage(cancelOrder.getResponseObject().getMessage());
								noticeList.add(corder);
								if("DD".equals(order_code.substring(0, 2)) || "OS".equals(order_code.substring(0, 2)) || "HH".equals(order_code.substring(0, 2))) {
									if("4497153900010008".equals(order.get("order_status"))) {
										DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_status","4497153900010002","update_time",DateUtil.getSysDateTimeString(),"order_code",order_code), "order_status,update_time", "order_code");
										DbUp.upTable("lc_orderstatus").dataInsert(new MDataMap("code",order_code,"create_time",DateUtil.getSysDateTimeString(),"create_user","system","old_status",order.get("order_status"),"now_status","4497153900010002","info","JobCancelOrderForHome"));
										DbUp.upTable("oc_order_cancel_h").dataExec("delete from oc_order_cancel_h where order_code=:order_code and out_order_code=:out_order_code", new MDataMap("order_code", order_code, "out_order_code", out_order_code));										
									}									
								} else {
									//删除订单在定时中的记录
									DbUp.upTable("oc_order_cancel_h").dataExec("delete from oc_order_cancel_h where order_code=:order_code and out_order_code=:out_order_code", new MDataMap("order_code", order_code, "out_order_code", out_order_code));
								}
							}						
						}
					} catch(Exception e) {
						e.printStackTrace();
						Corder corder= new Corder();
						corder.setOrder_code(order_code);
						corder.setOut_order_code(out_order_code);
						corder.setMessage(String.valueOf(e));
						noticeList.add(corder);
					}
					
				}
			}
			
			//发邮件通知订单取消失败
			if(noticeList.size()>0){
				
				sendMail(noticeList);//发送邮件
				
				//修改数据库
				for (Corder corder : noticeList) {
					String order_code=corder.getOrder_code();
					DbUp.upTable("oc_order_cancel_h").dataUpdate(new MDataMap("order_code",order_code,"notice_flag","0"), "notice_flag", "order_code");
				}
			}
			
		}
		
//		//拉取订单表的外部订单号
//		String sql = "select oc_order_cancel_h.order_code, oc_orderinfo.out_order_code" + 
//					 " from oc_order_cancel_h left join oc_orderinfo" +
//					 " on oc_order_cancel_h.order_code = oc_orderinfo.order_code" +
//					 " where oc_order_cancel_h.call_flag =:call_flag and oc_order_cancel_h.notice_flag = 1" +
//					 " and oc_order_cancel_h.out_order_code = '' and oc_orderinfo.out_order_code <> ''";
//		
//		List<Map<String, Object>> syncList = DbUp.upTable("oc_order_cancel_h").dataSqlList(sql, new MDataMap("call_flag","1"));
//		
//		if(syncList != null && syncList.size() > 0) {
//			for (Map<String, Object> map : syncList) {
//				String order_code = map.get("order_code").toString();
//				String out_order_code = map.get("out_order_code").toString();
//				//APP上下的TV品订单
//				DbUp.upTable("oc_order_cancel_h").dataUpdate(new MDataMap("order_code",order_code,"out_order_code",out_order_code,"cancel_count","0"), "out_order_code,cancel_count", "order_code");
//			}
//		}
	}
	

	/**
	 * 当前时间是否超过指定时间 minutes 分钟
	 * 时间格式：2014-12-02 20:14:10
	 * @param dateTime
	 * @param minutes
	 * @return
	 */
	private boolean differMinutes(String dateTime,int minutes){
		try {
			long date1=DateUtil.sdfDateTime.parse(dateTime).getTime();
			long date2=new Date().getTime();
			
			if(((date2-date1)/(60*1000))>=minutes){
				return true;
			}
			
		} catch (ParseException e) {
		}
		return false;
	}
	
	private void sendMail(List<Corder> corderList){
		
		String receives[]= bConfig("groupcenter.callOrder_sendMail_receives").split(",");
		String title= bConfig("groupcenter.callOrder_sendMail_title");
		String content= bConfig("groupcenter.callOrder_sendMail_content");
		String content_for= bConfig("groupcenter.callOrder_sendMail_content_for");
		
		StringBuffer sb=new StringBuffer();
		if(corderList!=null&&corderList.size()>0){
			for (Corder corder : corderList) {
				sb.append(FormatHelper.formatString(content_for,corder.getOrder_code(),corder.getOut_order_code(),StringUtils.trimToEmpty(corder.getMessage())));
			}
		}
		
		for (String receive : receives) {
			if(StringUtils.isNotBlank(receive)){
				MailSupport.INSTANCE.sendMail(receive, title, FormatHelper.formatString(content,sb.toString()));
			}
		}
	}
	
	
	private static class Corder {
		private String order_code;
		private String out_order_code;
		private String message;
		public String getOrder_code() {
			return order_code;
		}
		public void setOrder_code(String order_code) {
			this.order_code = order_code;
		}
		public String getOut_order_code() {
			return out_order_code;
		}
		public void setOut_order_code(String out_order_code) {
			this.out_order_code = out_order_code;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		
	}
	
	/***
	 * 取消发货
	 * @param order_code
	 * @param operater
	 * @param remark
	 * @return
	 */
	public RootResult cancelOrder(MDataMap orderInfo,String remark) {
		
		RootResult ret = new RootResult();
		FlowBussinessService fs = new FlowBussinessService();
		String flowBussinessUid = orderInfo.get("uid");
		String fromStatus = orderInfo.get("order_status");
		String operater = orderInfo.get("buyer_code");
		String order_code = orderInfo.get("order_code");		
		String toStatus = "4497153900010006";
		String flowType = "449715390008";
		ret = fs.ChangeFlow(flowBussinessUid, flowType, fromStatus,toStatus, operater, remark, new MDataMap("order_code",order_code));
		
		if (ret.getResultCode() == 1) {			
			//生成退款单
			CreateMoneyService createMoneyService = new CreateMoneyService();
			ReturnMoneyResult rm = createMoneyService.creatReturnMoney(order_code,operater,remark);
			//优惠券一体化 取消一下接口调用 -rhb 20181105
			//if(rm.getList() != null && rm.getList().size() > 0) {
			//	reWriteGiftVoucherToLD(rm.getList(), "R"); //取消发货回写礼金券给LD
			//}
		}else{
			WebHelper.errorMessage(order_code, "cancelOrder", 1,"cancelOrder on ChangeFlow", ret.getResultMessage(),null);
		}
		
		return ret;
	}
	
	/**
	 * 取消订单/取消发货回写礼金券给LD
	 * @param reWriteLD
	 */
	public void reWriteGiftVoucherToLD(List<GiftVoucherInfo> reWriteLD, String doType) {
		if(reWriteLD != null && reWriteLD.size() > 0) {
			reWriteLD = reOrgCoupons(reWriteLD); //合并订单号
			RsyncControlGiftVoucher rsync = new RsyncControlGiftVoucher();
			List<RsyncModelGiftVoucher> list = new ArrayList<RsyncModelGiftVoucher>();
			for(GiftVoucherInfo coupon : reWriteLD) {
				RsyncModelGiftVoucher model = new RsyncModelGiftVoucher();
				model.setHjy_ord_id(coupon.getHjy_ord_id());
				Map<String, Object> map = DbUp.upTable("oc_coupon_info").dataSqlOne(
								"SELECT out_coupon_code from oc_coupon_info where coupon_code=:coupon_code ",
								new MDataMap("coupon_code", coupon.getLj_code().toString()));
				if(map != null && map.get("out_coupon_code") != null) {
					model.setLj_code(map.get("out_coupon_code").toString());
					list.add(model);
				}
			}
			rsync.upRsyncRequest().setDo_type(doType);
			rsync.upRsyncRequest().setLjqList(list);
			rsync.doRsync();
		}
	}
	
	/**
	 * 将订单号合并
	 * @param reWriteLD
	 * @return
	 */
	public static List<GiftVoucherInfo> reOrgCoupons(List<GiftVoucherInfo> reWriteLD) {
		//按订单号排序
		Collections.sort(reWriteLD, new Comparator<Object>() {
		      public int compare(Object info1, Object info2) {
		    	  String one = ((GiftVoucherInfo)info1).getHjy_ord_id().toString();
		    	  String two = ((GiftVoucherInfo)info2).getHjy_ord_id().toString();
		        return two.compareTo(one);
		      }
		    });
		for(int i=0;i<reWriteLD.size()-1;i++) {
			for(int j=reWriteLD.size()-1;j>i;j--) {
				if(reWriteLD.get(j).getLj_code().toString().equals(reWriteLD.get(i).getLj_code().toString())) {
					String orders = reWriteLD.get(i).getHjy_ord_id().toString() + "," + reWriteLD.get(j).getHjy_ord_id().toString();
					reWriteLD.get(i).setHjy_ord_id(orders);
					reWriteLD.remove(j);
				}
			}
		}		
		return reWriteLD;
	}
}
