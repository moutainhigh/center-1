package com.cmall.usercenter.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.xmassystem.load.LoadSellerInfo;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.SecrurityHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * com.cmall.usercenter.service.MerchantApproveFlowFunc
 * 
 * @author LHY 商户审批流程回调函数,在erp中配置
 */
public class MerchantApproveFlowFunc extends BaseClass implements IFlowFunc {

	public RootResult BeforeFlowChange(String flowCode, String outCode, String fromStatus, String toStatus,
			MDataMap mSubMap) {
		return null;
	}

	public RootResult afterFlowChange(String flowCode, String outCode, String fromStatus, String toStatus,
			MDataMap mSubMap) {
		RootResult rootResult = new RootResult();
		fromStatus = String.valueOf(fromStatus);
		toStatus = String.valueOf(toStatus);

		String small_seller_code = String.valueOf(outCode);

		if ("4497172300140005".equals(toStatus)) {// 主管审批通过
			Map<String, Object> map = getAccountClearType(small_seller_code);
			finishWork(flowCode, outCode, fromStatus, toStatus, small_seller_code, map, rootResult);
			/**
			 * 将商户信息添加到商户质保金管理表
			 */
			String user_code = UserFactory.INSTANCE.create().getUserCode();
			Map<String, Object> seller = DbUp.upTable("uc_seller_info_extend").dataSqlOne(
					"select * from usercenter.uc_seller_info_extend where small_seller_code=:small_seller_code",
					new MDataMap("small_seller_code", small_seller_code));
			MDataMap oc_seller_retention_money = new MDataMap();
			oc_seller_retention_money.put("small_seller_code", outCode);
			oc_seller_retention_money.put("small_seller_name", seller.get("seller_company_name").toString());
			String settle_type = "";
			String seller_type = seller.get("uc_seller_type").toString();
			/**
			 * 商户类型<br>
			 * 4497478100050001 普通商户<br>
			 * 4497478100050002 跨境商户<br>
			 * 4497478100050003 跨境直邮<br>
			 * 4497478100050004 平台入驻<br>
			 * 结算方式<br>
			 * 4497477900040001 常规结算<br>
			 * 4497477900040002 跨境保税结算<br>
			 * 4497477900040003 跨境直邮结算<br>
			 * 4497477900040004 平台入驻<br>
			 */
			switch (seller_type) {
			case "4497478100050001":
				settle_type = "4497477900040001";
				break;
			case "4497478100050002":
				settle_type = "4497477900040002";
				break;
			case "4497478100050003":
				settle_type = "4497477900040003";
				break;
			case "4497478100050004":
				settle_type = "4497477900040004";
				break;
			case "4497478100050005":  //添加缤纷商户类型结算方式 --rhb
				settle_type = "4497477900040001";
				break;
			default:
				break;
			}
			oc_seller_retention_money.put("settle_type", settle_type);
			oc_seller_retention_money.put("max_retention_money", seller.get("quality_retention_money").toString());
			oc_seller_retention_money.put("money_collection_way", seller.get("money_collection_way").toString());
			oc_seller_retention_money.put("creator", user_code);
			oc_seller_retention_money.put("create_time", DateUtil.getSysDateTimeString());
			oc_seller_retention_money.put("updator", user_code);
			oc_seller_retention_money.put("update_time", DateUtil.getSysDateTimeString());
			DbUp.upTable("oc_seller_retention_money").dataInsert(oc_seller_retention_money);
		} else if ("4497172300140003".equals(toStatus)) {// 专员审批通过
			Map<String, Object> map = getAccountClearType(small_seller_code);
			if (String.valueOf(map.get("account_clear_type")).equals("4497478100030003")) {// 正月结算
				finishWork(flowCode, outCode, fromStatus, toStatus, small_seller_code, map, rootResult);

				MDataMap updateMap = new MDataMap();
				updateMap.put("flow_status", "4497172300140005");
				updateMap.put("small_seller_code", small_seller_code);
				DbUp.upTable("uc_sellerinfo").dataUpdate(updateMap, "", "small_seller_code");

				updateMap.remove("flow_status");
				updateMap.remove("small_seller_code");

				updateMap.put("flow_code", flowCode);
				updateMap.put("current_status", "4497172300140005");
				updateMap.put("flow_isend", "1");
				updateMap.put("last_status", fromStatus);
				updateMap.put("next_operators", "");
				updateMap.put("next_operator_status", "");
				DbUp.upTable("sc_flow_main").dataUpdate(updateMap, "", "flow_code");

				// 插入日志
				MDataMap dataMap = new MDataMap();
				dataMap.put("flow_code", flowCode);
				Map<String, Object> map2 = DbUp.upTable("sc_flow_history").dataSqlOne(
						"select creator from sc_flow_history where flow_code=:flow_code order by create_time desc",
						dataMap);
				dataMap.put("flow_type", "449717230014");
				dataMap.put("creator", String.valueOf(map2.get("creator")));
				dataMap.put("create_time", DateUtil.getSysDateTimeString());
				dataMap.put("flow_remark", "整月结算自动审批通过");
				dataMap.put("current_status", "4497172300140005");
				DbUp.upTable("sc_flow_history").dataInsert(dataMap);
				/**
				 * 将商户信息添加到商户质保金管理表
				 */
				String user_code = UserFactory.INSTANCE.create().getUserCode();
				Map<String, Object> seller = DbUp.upTable("uc_seller_info_extend").dataSqlOne(
						"select * from usercenter.uc_seller_info_extend where small_seller_code=:small_seller_code",
						new MDataMap("small_seller_code", small_seller_code));
				MDataMap oc_seller_retention_money = new MDataMap();
				oc_seller_retention_money.put("small_seller_code", outCode);
				oc_seller_retention_money.put("small_seller_name", seller.get("seller_company_name").toString());
				String settle_type = "";
				String seller_type = seller.get("uc_seller_type").toString();
				/**
				 * 商户类型<br>
				 * 4497478100050001 普通商户<br>
				 * 4497478100050002 跨境商户<br>
				 * 4497478100050003 跨境直邮<br>
				 * 4497478100050004 平台入驻<br>
				 * 结算方式<br>
				 * 4497477900040001 常规结算<br>
				 * 4497477900040002 跨境保税结算<br>
				 * 4497477900040003 跨境直邮结算<br>
				 * 4497477900040004 平台入驻<br>
				 */
				switch (seller_type) {
				case "4497478100050001":
					settle_type = "4497477900040001";
					break;
				case "4497478100050002":
					settle_type = "4497477900040002";
					break;
				case "4497478100050003":
					settle_type = "4497477900040003";
					break;
				case "4497478100050004":
					settle_type = "4497477900040004";
					break;
				case "4497478100050005":  //添加缤纷商户类型结算方式 --rhb
					settle_type = "4497477900040001";
					break;
				default:
					break;
				}
				oc_seller_retention_money.put("settle_type", settle_type);
				oc_seller_retention_money.put("max_retention_money", seller.get("quality_retention_money").toString());
				oc_seller_retention_money.put("money_collection_way", seller.get("money_collection_way").toString());
				oc_seller_retention_money.put("creator", user_code);
				oc_seller_retention_money.put("create_time", DateUtil.getSysDateTimeString());
				oc_seller_retention_money.put("updator", user_code);
				oc_seller_retention_money.put("update_time", DateUtil.getSysDateTimeString());
				DbUp.upTable("oc_seller_retention_money").dataInsert(oc_seller_retention_money);
			}
		} else if ("4497172300140002".equals(toStatus)) {// 专员驳回,插入到草稿箱
			MDataMap mWhereMap = new MDataMap();
			mWhereMap.put("small_seller_code", small_seller_code);
			Map<String, Object> map = DbUp.upTable("uc_seller_info_extend").dataSqlOne(
					"select * from uc_seller_info_extend where small_seller_code=:small_seller_code", mWhereMap);
			MDataMap updateDataMap = new MDataMap(map);
			updateDataMap.remove("zid");

			int count = DbUp.upTable("uc_seller_info_extend_draft").count("small_seller_code", small_seller_code);
			if (count > 0) {
				mWhereMap.put("flag_del", "4497478100040001");
				mWhereMap.put("small_seller_code", small_seller_code);
				mWhereMap.put("flow_status", "4497471600300002");
				mWhereMap.put("flow_remark", mSubMap.get("remark"));
				DbUp.upTable("uc_seller_info_extend_draft").dataUpdate(mWhereMap, "", "small_seller_code");
			} else {
				updateDataMap.put("seller_code", AppConst.MANAGE_CODE_HOMEHAS);
				updateDataMap.put("flow_status", "4497471600300002");
				updateDataMap.put("flow_remark", mSubMap.get("remark"));
				DbUp.upTable("uc_seller_info_extend_draft").dataInsert(updateDataMap);
			}

			MDataMap updateMap = new MDataMap();
			DbUp.upTable("uc_seller_info_extend").delete("small_seller_code", small_seller_code);
			/**
			 * add by ligj 被驳回商户再次提交审核时会提示公司编号已存在
			 */
			DbUp.upTable("uc_sellerinfo").delete("small_seller_code", small_seller_code);

			updateMap.put("flow_code", flowCode);
			updateMap.put("flow_isend", "1");
			DbUp.upTable("sc_flow_main").dataUpdate(updateMap, "", "flow_code");
		}
		return rootResult;
	}

	private RootResult finishWork(String flowCode, String outCode, String fromStatus, String toStatus,
			String small_seller_code, Map<String, Object> map, RootResult rootResult) {
		MDataMap mAddMaps = new MDataMap();
		mAddMaps.put("flow_status", toStatus);
		mAddMaps.put("small_seller_code", small_seller_code);
		DbUp.upTable("uc_sellerinfo").dataUpdate(mAddMaps, "", "small_seller_code");
		// 插入登陆用户信息
		String user_code = WebHelper.upCode("UI");
		MDataMap insertDatamap = new MDataMap();
		insertDatamap.put("user_code", user_code);
		insertDatamap.put("manage_code", small_seller_code);
		insertDatamap.put("create_time", DateUtil.getSysDateTimeString());
		insertDatamap.put("user_name", String.valueOf(map.get("user_name")));
		insertDatamap.put("real_name", String.valueOf(map.get("seller_short_name")));
		insertDatamap.put("user_password", SecrurityHelper.MD5Customer(bConfig("familyhas.dsf_password")));
		insertDatamap.put("user_type_did", "467721200003");
		DbUp.upTable("za_userinfo").dataInsert(insertDatamap);

		// 插入登陆用户权限
		MDataMap updateDatamap2 = new MDataMap();
		updateDatamap2.put("role_code", "4677031800020001");
		updateDatamap2.put("user_code", user_code);
		DbUp.upTable("za_userrole").dataInsert(updateDatamap2);

		new LoadSellerInfo().deleteInfoByCode(small_seller_code);

		return rootResult;
	}

	/**
	 * 获得 user_name, seller_short_name结算周期
	 * 
	 * @param small_seller_code
	 * @return
	 */
	private Map<String, Object> getAccountClearType(String small_seller_code) {
		String sql = "select user_name, seller_short_name, account_clear_type from uc_seller_info_extend where small_seller_code=:small_seller_code";
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("small_seller_code", small_seller_code);
		return DbUp.upTable("uc_seller_info_extend").dataSqlOne(sql, mWhereMap);
	}
}
