package com.cmall.groupcenter.job;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.groupcenter.homehas.RsyncAlipayMoveInformation;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJobForExec;
import com.srnpr.zapweb.webmodel.ConfigJobExec;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * LD支付信息同步
 * @author jlin
 *
 */
public class JobForLDPaymentSucc extends RootJobForExec {
	
	@Override
	public IBaseResult execByInfo(String orderCode) {

		MWebResult mWebResult = new MWebResult();
		
		if (StringUtils.startsWith(orderCode, "DD")) {
			
			//根据订单号查询是否是拼团单。
			MDataMap groupOrderMap = DbUp.upTable("sc_event_collage_item").one("collage_ord_code",orderCode);
			if(groupOrderMap != null && !groupOrderMap.isEmpty()){//不为空时，证明是拼团单，然后检查是否已经拼团成功。
				String collageCode = groupOrderMap.get("collage_code");
				MDataMap collageInfo = DbUp.upTable("sc_event_collage").one("collage_code",collageCode);
				//判断此团是否拼团成功
				String collageStatus = collageInfo.get("collage_status");
				if(!"449748300002".equals(collageStatus)){//非拼团成功的订单做以下操作
					//将同步次数改为1，防止长时间未拼团成功。此订单通知次数用完之后，不再通知。
					DbUp.upTable("za_exectimer").dataUpdate(new MDataMap("exec_info", orderCode,"exec_type","449746990001" ,"exec_number","1","remark",""),"exec_number,remark","exec_info,exec_type");
					//操作失败标识
					mWebResult.setResultCode(99);
					return mWebResult;
				}
			}

			//订单信息
			MDataMap orderMap = DbUp.upTable("oc_orderinfo").oneWhere("order_status,out_order_code,order_seq,big_order_code", "","order_code=:order_code", "order_code", orderCode);
			String out_order_code = orderMap.get("out_order_code");
			String order_seq = orderMap.get("order_seq");
			String big_order_code = orderMap.get("big_order_code");
			
			if(StringUtils.isBlank(out_order_code)){
				mWebResult.inErrorMessage(918505105);
				return mWebResult;
			}
			
			String payType = null;
			Map<String, Object> payTypeMap = DbUp.upTable("oc_orderinfo_upper_payment").dataSqlOne("SELECT up.pay_type FROM oc_orderinfo_upper_payment up WHERE up.big_order_code = :big_order_code limit 1", new MDataMap("big_order_code",big_order_code));
			if(payTypeMap != null){
				payType = StringUtils.trimToEmpty((String)payTypeMap.get("pay_type"));
			}
			
			//支付信息
			MDataMap payInfoMap = DbUp.upTable("oc_order_pay").oneWhere("pay_sequenceid,payed_money,pay_type,php_code,create_time", "", "", "order_code",orderCode,"pay_type",payType);
			if(payInfoMap == null){
				mWebResult.setResultCode(0);
				mWebResult.setResultMessage("未查询到支付信息["+orderCode+"]["+payType+"]");
				return mWebResult;
			}
			
			String pay_sequenceid = payInfoMap.get("pay_sequenceid");
			String payed_money = payInfoMap.get("payed_money");
			String pay_type = payInfoMap.get("pay_type");
			String php_code = payInfoMap.get("php_code");
			String create_time = payInfoMap.get("create_time");
			String web_pay_no = "";
			
			// 查询收款账户
			MDataMap dataMap = DbUp.upTable("oc_payment_paygate").oneWhere("c_paygate,c_transnum", "", "", "c_order",big_order_code);
			if(dataMap != null){
				web_pay_no = dataMap.get("c_transnum");
				dataMap = DbUp.upTable("oc_paygate").oneWhere("account", "", "", "paygate",dataMap.get("c_paygate"));
				if(dataMap != null){
					php_code = StringUtils.trimToEmpty(dataMap.get("account"));
				}
			}
			
			if(!StringUtils.startsWithAny(pay_type, "449746280003","449746280005","449746280014","449746280020")){
				mWebResult.inErrorMessage(918505106);
				return mWebResult;
			}
			
			String bankCd = "";
			if("449746280003".equals(pay_type)) bankCd = "54";
			if("449746280005".equals(pay_type)) bankCd = "WEC";
			if("449746280014".equals(pay_type)) bankCd = "66";
			if("449746280020".equals(pay_type)) bankCd = "69";
			
			if(!rsyncPay(out_order_code, pay_sequenceid+"#"+order_seq, bankCd, payed_money, create_time, php_code,web_pay_no)){
				mWebResult.inErrorMessage(918507002);
				return mWebResult;
			}
			
		}else{
			mWebResult.inErrorMessage(918507002);
			return mWebResult;
		}

		return mWebResult;
	}

	private static ConfigJobExec config = new ConfigJobExec();
	static {
		config.setExecType("449746990001");
	}

	@Override
	public ConfigJobExec getConfig() {

		return config;
	}

	private boolean rsyncPay(String ordId,String payNo,String bankCd,String payMoney,String payTime,String acctBankNo,String webPayNo){
		
		RsyncAlipayMoveInformation rsyncAlipayMoveInformation = new RsyncAlipayMoveInformation();
		rsyncAlipayMoveInformation.upRsyncRequest().setAcctBankNo(acctBankNo);
		rsyncAlipayMoveInformation.upRsyncRequest().setBankCd(bankCd);
		rsyncAlipayMoveInformation.upRsyncRequest().setOrdId(ordId);
		rsyncAlipayMoveInformation.upRsyncRequest().setPayMoney(payMoney);
		rsyncAlipayMoveInformation.upRsyncRequest().setPayNo(payNo);
		rsyncAlipayMoveInformation.upRsyncRequest().setPayTime(payTime);
		rsyncAlipayMoveInformation.upRsyncRequest().setWebPayNo(webPayNo);
		rsyncAlipayMoveInformation.doRsync();
		return rsyncAlipayMoveInformation.getStatus();
	}
	
	public static void main(String[] args) {
		JobForLDPaymentSucc homehas = new JobForLDPaymentSucc();
		// 测试上述代码
		homehas.execByInfo("DD130311104");
	}
}
