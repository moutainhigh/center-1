package com.cmall.groupcenter.homehas;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.groupcenter.homehas.config.RsyncConfigOrderStatus;
import com.cmall.groupcenter.homehas.model.RsyncRequestOrderStatus;
import com.cmall.groupcenter.homehas.model.RsyncResponseOrderStatus;
import com.cmall.groupcenter.homehas.model.RsyncResponseOrderStatus.Result;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.JobExecHelper;

/***
 * 同步订单状态
 * @author jlin
 *
 */
public class RsyncOrderStatus extends RsyncHomeHas<RsyncConfigOrderStatus, RsyncRequestOrderStatus, RsyncResponseOrderStatus> {

	private final static RsyncConfigOrderStatus CONFIG_ORDER_STATUS = new RsyncConfigOrderStatus();

	public RsyncConfigOrderStatus upConfig() {
		return CONFIG_ORDER_STATUS;
	}

	private RsyncRequestOrderStatus requestOrderStatus = new RsyncRequestOrderStatus();

	public RsyncRequestOrderStatus upRsyncRequest() {
		
		return requestOrderStatus;
	}

	public RsyncResult doProcess(RsyncRequestOrderStatus tRequest, RsyncResponseOrderStatus tResponse) {
		RsyncResult mWebResult = new RsyncResult();
		
		if(!tResponse.isSuccess()||tResponse.getResult()==null||tResponse.getResult().size()<1){
			mWebResult.setResultCode(918501003);
			mWebResult.setResultMessage(bInfo(918501003));
			return mWebResult;
		}
		
		//更新订单状态
		String yc_orderform_num=tRequest.getYc_orderform_num();
		
//		String yc_update_time = tResponse.getResult().get(0).getYc_update_time(); //更新时间
//		String cod_stat_cd = tResponse.getResult().get(0).getCod_stat_cd();//配送状态
//		String yc_claim_time = tResponse.getResult().get(0).getYc_claim_time();//配送状态更新时间
//		String yc_orderform_status = tResponse.getResult().get(0).getYc_orderform_status();//订单状态
		
//		if (tResponse.getResult().size()>1) {
//			//循环比对出最新的状态    先比对yc_update_time 再比对 yc_claim_time
//			for (int i = 1; i < tResponse.getResult().size(); i++) {
//				Result result = tResponse.getResult().get(i);
//				
//				if(compare(yc_update_time, result.getYc_update_time())<0){
//					yc_update_time = result.getYc_update_time(); //更新时间
//					cod_stat_cd = result.getCod_stat_cd();//配送状态
//					yc_claim_time = result.getYc_claim_time();//配送状态更新时间
//					yc_orderform_status = result.getYc_orderform_status();//订单状态
//				}else if(compare(yc_update_time, result.getYc_update_time())==0){//若更新时间相同，则比对配送时间
//					
//					if(StringUtils.isBlank(result.getYc_claim_time())||"null".equals(result.getYc_claim_time())){
//						continue;
//					}
//					
//					if(StringUtils.isBlank(yc_claim_time)||"null".equals(yc_claim_time)||compare(yc_claim_time, result.getYc_claim_time())<0){
//						yc_update_time = result.getYc_update_time(); //更新时间
//						cod_stat_cd = result.getCod_stat_cd();//配送状态
//						yc_claim_time = result.getYc_claim_time();//配送状态更新时间
//						yc_orderform_status = result.getYc_orderform_status();//订单状态
//					}
//				}
//			}
//		}
		
//		 新规则：赠品的cod_stat_cd为空 那不为空的就是主品的
		String cod_stat_cd = "";//配送状态
		String yc_orderform_status = "";//订单状态
		for (Result result : tResponse.getResult()) {
			if(StringUtils.isNotBlank(cod_stat_cd)){
				cod_stat_cd = result.getCod_stat_cd();//配送状态
				yc_orderform_status = result.getYc_orderform_status();//订单状态
				break;
			}
		}
		
//		if(StringUtils.isNotBlank(yc_orderform_status)){
//			mWebResult.inErrorMessage(918501005);
//			return mWebResult;
//		}
		
		//如果没有配送状态，则取第一个
		if(StringUtils.isBlank(yc_orderform_status)){
			yc_orderform_status=tResponse.getResult().get(0).getYc_orderform_status();//订单状态
		}
		
		
		String order_code="";
		String order_status="";
		List<MDataMap> list=DbUp.upTable("oc_orderinfo").query("order_code,order_status", "", "out_order_code=:out_order_code", new MDataMap("out_order_code",yc_orderform_num), 0, 1);
		if(list!=null&&list.size()>0){
			order_code=list.get(0).get("order_code");
			order_status=list.get(0).get("order_status");
		}else{
			mWebResult.setResultCode(918501005);
			mWebResult.setResultMessage(bInfo(918501005));
			return mWebResult;
		}
		
		String state=stateMapper(yc_orderform_num,cod_stat_cd, yc_orderform_status);
		if(!"".equals(state)&&!state.equals(order_status)){ //比对与原来不一样的时候更新，并且在日志表中插入一条记录
			DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_status",state,"update_time",DateUtil.getSysDateTimeString(),"order_code",order_code), "order_status,update_time", "order_code");
			if("4497153900010006".equals(order_status)) {
				//取消订单，判断是否是分销单，如果是，写入取消订单分销定时
				if(DbUp.upTable("fh_agent_order_detail").count("order_code",order_code)>0 && DbUp.upTable("za_exectimer").count("exec_info",order_code,"exec_type","449746990029") <= 0) {
					JobExecHelper.createExecInfo("449746990029", order_code, DateUtil.getSysDateTimeString());
				}
			}
			DbUp.upTable("lc_orderstatus").dataInsert(new MDataMap("code",order_code,"create_time",DateUtil.getSysDateTimeString(),"create_user","system","old_status",order_status,"now_status",state,"info","RsyncOrderStatus"));
		}
		
		return mWebResult;
	}

	public RsyncResponseOrderStatus upResponseObject() {

		return new RsyncResponseOrderStatus();
	}

	/**
	 * LD 与 ERP 订单状态映射<br> 若映射不到的字段，不修改ERP状态
	 * @param yc_orderform_status 家有订单状态
	 * @param cod_stat_cd 配送状态
	 * @return
	 */
	public String stateMapper(String out_order_code,String cod_stat_cd,String yc_orderform_status){
		cod_stat_cd=trim(cod_stat_cd);
		yc_orderform_status=trim(yc_orderform_status);
//		---------------------配送状态----------------------
//		30	配送中
//		31	拒收
//		40	丢失
//		41	上门取货丢失
//		90	签收
//		91	销售退货
//		---------------------订单状态----------------------
//		10	订单受理
//		20	入款确认
//		30	欠交订单
//		40	出库指示
//		50	出库确定
//		60	完成出库(出库之后是否签收，根据cod_stat_cd物流状态确定)
//		91	受理后取消
//		92	入款后取消
//		93	取消欠交订单
//		94	出库指示后取消
//		99	电子自动取消
//		97	拒收外呼
//		98	二次配送
//		69	再配
//		70	配送完成
//		96	拒收
//--------------------------------
		
//		编号: 4497153900010001   名称: 下单成功-未付款 
//		编号: 4497153900010002  名称: 下单成功-未发货
//		编号: 4497153900010003  名称: 已发货 
//		编号: 4497153900010004  名称: 已收货 
//		编号: 4497153900010005  名称: 交易成功 
//		编号: 4497153900010006  名称: 交易失败 
		
		if(!"".equals(cod_stat_cd)){//配送状态
			if("30".equals(cod_stat_cd)||"40".equals(cod_stat_cd)){
				return "4497153900010003";
			}else if("90".equals(cod_stat_cd)) {
				return "4497153900010005";
			} else if("31".equals(cod_stat_cd)||"91".equals(cod_stat_cd)) {
				return "4497153900010006";
			}
		} else if(!"".equals(yc_orderform_status)) {
			
			if("20".equals(yc_orderform_status)||"30".equals(yc_orderform_status)){
				
//				如果LD系统商品缺货，不管是否支付。LD系统均将订单的状态置为“欠交订单”。针对LD此处理流程，系统从LD同步订单状态30时，处理如下：
//				 在线支付订单：如果订单未付款，系统订单状态不变（下单成功-未付款）；订单已付款，订单状态变更为“下单成功-未发货”。
//				 货到付款订单：系统订单状态不变（下单成功-未发货）。
				MDataMap dataMap=DbUp.upTable("oc_orderinfo").one("out_order_code",out_order_code);
				String big_order_code=dataMap.get("big_order_code");
				String order_code=dataMap.get("order_code");
				String sql="SELECT out_trade_no FROM oc_payment where (out_trade_no='"+order_code+"' "+(StringUtils.isBlank(big_order_code)?"":"or out_trade_no='"+big_order_code+"'") +" )  and (trade_status='TRADE_SUCCESS' or trade_status='TRADE_FINISHED') limit 1 ";
				List<Map<String, Object>> list=DbUp.upTable("oc_payment").dataSqlList(sql, null);
				if(list!=null&&list.size()>0){
					return "4497153900010002";
				}
				
			}else if("40".equals(yc_orderform_status)||"50".equals(yc_orderform_status)){
//			if("10".equals(yc_orderform_status)||"20".equals(yc_orderform_status)||"40".equals(yc_orderform_status)||"50".equals(yc_orderform_status)||"30".equals(yc_orderform_status)){
				return "4497153900010002";
			}else if("91".equals(yc_orderform_status)||"92".equals(yc_orderform_status)||"94".equals(yc_orderform_status)||"93".equals(yc_orderform_status)) {
				return "4497153900010006";
			}
		}
		
		return "";
	}
	
	private String trim(Object obj){
		return obj==null?"":obj.toString().trim();
	}
	
	/**
	 * 比较两个时间
	 * 时间格式：2014-12-02 20:14:10
	 * <br>大于结束时间返回正数，等于 0，小于 负数
	 * @param start_time
	 * @param end_time
	 * @return
	 */
	private synchronized int compare(String start_time,String end_time){
		try {
			
			if(StringUtils.isBlank(end_time)){
				return 1;
			}
			if(StringUtils.isBlank(start_time)){
				return -1;
			}
			
			Date date1=DateUtil.sdfDateTime.parse(start_time);
			Date date2=DateUtil.sdfDateTime.parse(end_time);
			return date1.compareTo(date2);
		} catch (ParseException e) {
			return 0;
		}
	}
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		new RsyncOrderStatus().stateMapper("11111111333", "", "30");
	}
}
