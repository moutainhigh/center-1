package com.cmall.ordercenter.tallyorder.platform;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.service.RetentionMoneyReceiptService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;


/** 
 * 商户结算5.0-付款申请单付款(将付款申请单状态改为已付款)
 * 4497477900060001 : 商品行政待审核
 * 4497477900060002 : 商品行政审核通过
 * 4497477900060003 : 商品行政驳回
 * 4497477900060004 : 财务审核通过
 * 4497477900060005 : 财务驳回
 * 4497477900060006 : 财务已确认
 * 4497477900060007 : 财务已付款
 * 4497477900060008 : 财务反审核
 * 
 * 平台入驻商户结算单状态
 * 4497476900040008 : 未结算
 * 4497476900040009 : 已结算
 * 4497476900040010 : 待审核
 * 4497476900040011 : 已审核
 * @author zht
 */
public class UpdateApplyForPayStatusToPayedPlatform extends RootFunc {
	private static SimpleDateFormat timeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	RetentionMoneyReceiptService receiptService = new RetentionMoneyReceiptService();

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		//记日志(已确认,有付款日期,付款日期不为空,待付金额大于0)
		String sql = "select pay_code,settle_codes,merchant_code,pay_time from oc_bill_apply_payment_pt "
				+ "where flag='4497477900060006' and is_pay='4497477900020001' and pay_time is not null";
		Date now = new Date();
		String creator = UserFactory.INSTANCE.create().getLoginName();
		String ip = WebSessionHelper.create().upIpaddress();
		
		List<Map<String, Object>> list = DbUp.upTable("oc_bill_apply_payment_pt").dataSqlList(sql, new MDataMap());
		if(null != list && list.size() >0) {
			for(Map<String, Object> apply : list) {
				String payCode = apply.get("pay_code") == null ? "" : apply.get("pay_code").toString();
				try{
					MDataMap logMap = new MDataMap();
					logMap.put("pay_code", payCode);
					logMap.put("flag", "4497477900060007");
					logMap.put("ip", ip);
					logMap.put("comment", "平台入驻付款申请单已付款");;
					logMap.put("create_time", timeSdf.format(now));
					logMap.put("creator", creator);
					DbUp.upTable("lc_apply_for_payment").dataInsert(logMap);
					
					// 更新质保金收据的付款状态
					receiptService.updatePaymentStatusByBill(apply.get("settle_codes")+"",apply.get("merchant_code")+"",apply.get("pay_time")+"");
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		MWebResult mResult = new MWebResult();
		sql = "update oc_bill_apply_payment_pt set flag='4497477900060007' "
				+ "where flag='4497477900060006' and is_pay='4497477900020001' and pay_time is not null";
		int count = DbUp.upTable("oc_bill_apply_payment_pt").dataExec(sql, mDataMap);
		//mResult.setResultCode(939303103);
		if(count == 0) {
			mResult.setResultMessage("更新记录数:0.请检查是否有新导入付款日期的记录");
		} else {
			mResult.setResultMessage("操作成功!");
		}
		
		//变更商户结算单状态(已结算)
		try {
			sql = "select merchant_code,settle_codes from oc_bill_apply_payment_pt "
					+ "where flag='4497477900060007' and is_pay='4497477900020001' and pay_time is not null";
			List<Map<String, Object>> applyList = DbUp.upTable("oc_bill_apply_payment_pt").dataSqlList(sql, new MDataMap());
			if(null != applyList && applyList.size() > 0) {
				for(Map<String, Object> apply : applyList) {
					String settleCodes = apply.get("settle_codes") == null ? "" : apply.get("settle_codes").toString();
					String merchant_code = apply.get("merchant_code") == null ? "" : apply.get("merchant_code").toString();
					if(StringUtils.isNotEmpty(merchant_code)) {
						if(StringUtils.isNotEmpty(settleCodes)) {
							settleCodes = settleCodes.replaceAll(",", "','");
							sql = "update oc_bill_merchant_new_spec set flag='4497476900040009' where settle_code in('" + settleCodes + "') and merchant_code='" + merchant_code + "'";
							count = DbUp.upTable("oc_bill_merchant_new_spec").dataExec(sql, new MDataMap());
						}
						mResult.setResultMessage(bInfo(969909001));
						mResult.setResultCode(1);
					} else {
						mResult.setResultCode(939303201);
						mResult.setResultMessage(bInfo(939303201));
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return mResult;
	}
}
