/**
 * Project Name:usercenter
 * File Name:ChangeReturnSellerStatus.java
 * Package Name:com.cmall.usercenter.service.sellerinfo
 * Date:2013年10月14日上午9:54:40
 *
 */

package com.cmall.usercenter.service.sellerinfo;

import com.cmall.systemcenter.systemface.IFlowFunc;
import com.cmall.usercenter.common.UserCenterConst;
import com.cmall.usercenter.service.UcShopTemplateService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * ClassName:ChangeReturnSellerStatus <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2013年10月14日 上午9:54:40 <br/>
 * 
 * @author Administrator
 * @version
 * @since JDK 1.6
 * @see
 */
public class ChangeReturnSellerStatus extends BaseClass implements IFlowFunc {

	public RootResult BeforeFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus, MDataMap mSubMap) {
		RootResult result = new RootResult();
		try {

			if (fromStatus.equals("4497172300040002")
					&& toStatus.equals("4497172300040004")) {

				UcShopTemplateService ucShopTemplateService = new UcShopTemplateService();

				ucShopTemplateService.refreshTemplate();

				ucShopTemplateService.updateCommonTemplate(outCode);

				MDataMap mWhereMap = new MDataMap();

				mWhereMap.put("seller_code", outCode);

				int count = DbUp.upTable("uc_seller_info_extend").dataCount(
						"seller_code=:seller_code", mWhereMap);

				if (count > 0) {
					return result;
				} else {
					result.setResultCode(959701027);
					result.setResultMessage(bInfo(959701027));
				}
			}
		} catch (Exception e) {
			result.setResultCode(959701014);
		}
		return result;
	}

	/**
	 * 更新商家信息状态 TODO 简单描述该方法的实现功能（可选）.
	 * 
	 * @see com.cmall.systemcenter.systemface.IFlowFunc#afterFlowChange(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      com.srnpr.zapcom.basemodel.MDataMap)
	 */
	public RootResult afterFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus, MDataMap mSubMap) {
		RootResult result = new RootResult();
		MDataMap updateDatamap = new MDataMap();
		updateDatamap.put("seller_code", outCode);
		updateDatamap.put("seller_status", toStatus);

		MDataMap updateDatamap2 = new MDataMap();
		updateDatamap2.put("role_code", UserCenterConst.ADMIN_ROLE_CODE);
		updateDatamap2.put("user_code", getUseridBySellerCode(outCode));

		try {
			DbUp.upTable("uc_sellerinfo").dataUpdate(updateDatamap,
					"seller_status", "seller_code");
			// 4677031800020002
			if (fromStatus.equals("4497172300040002")
					&& toStatus.equals("4497172300040004")) {
				DbUp.upTable("za_userrole").dataUpdate(updateDatamap2,
						"role_code", "user_code");
			}

		} catch (Exception e) {
			result.setResultCode(959701014);
		}
		return result;
	}

	/**
	 * getUseridBySellerCode:(根据sell_code获取user_code). <br/>
	 * 
	 * @author hxd
	 * @param sellerCode
	 * @return
	 * @since JDK 1.6
	 */
	public String getUseridBySellerCode(String sellerCode) {
		MDataMap queryDatamap = DbUp.upTable("za_userinfo").one("manage_code",
				sellerCode);
		return queryDatamap.get("user_code");
	}

}
