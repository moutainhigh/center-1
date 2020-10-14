
package com.cmall.usercenter.service.sellerinfo;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.productcenter.service.CategoryService;
import com.cmall.systemcenter.model.ScFlowMain;
import com.cmall.systemcenter.service.FlowService;
import com.cmall.usercenter.common.UserCenterConst;
import com.srnpr.zapcom.basehelper.SecrurityHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * ClassName: SellerInfoProcess <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * date: 2013-9-23 下午1:20:47 <br/>
 * 
 * @author hxd
 * @version
 * @since JDK 1.6
 */
// {"version":1,"seller_name":"","seller_description":"","brandCode":"aa","categoryCode":"44971603000100020003","company_name":"111","seller_url":"111","user_name":"11111","user_password":"1111"}
public class SellerInfoProcess extends
		RootApi<SellerInfoResult, SellerInfoInput> {
	public SellerInfoResult Process(SellerInfoInput inputParam,
			MDataMap mRequestMap) {
		SellerInfoResult infoResult = new SellerInfoResult();
		if (inputParam == null) {
			infoResult.setResultMessage(bInfo(959701000));
			infoResult.setResultCode(959701000);
			return infoResult;
		}
		
		if(StringUtils.isNotBlank(inputParam.getAddFlag()))
		{
			
			MDataMap insertDatamap = new MDataMap();
			insertDatamap.put("seller_name", inputParam.getSeller_name());
			insertDatamap.put("seller_descrption",inputParam.getSeller_description());
			insertDatamap.put("seller_account", inputParam.getCompany_name());
			insertDatamap.put("seller_url", inputParam.getSeller_url());
			insertDatamap.put("seller_area", inputParam.getSellerArea());
			insertDatamap.put("seller_telephone", inputParam.getSellerTelephone());
			insertDatamap.put("seller_return_address",inputParam.getSellerReturnAddress());
			insertDatamap.put("seller_return_postcode",inputParam.getSellerReturnPostcode());
			insertDatamap.put("seller_return_contact",inputParam.getSellerReturnContact());
			DbUp.upTable("uc_sellerinfo").dataUpdate(insertDatamap, "seller_descrption,seller_account,seller_url,seller_area"
					+ ",seller_telephone,seller_return_address,seller_return_postcode,seller_return_contact", "seller_name");
			return infoResult;
		}
		
		
		if (StringUtils.isBlank(inputParam.getSeller_name())) {
			infoResult.setResultCode(959701013);
			infoResult.setResultMessage(bInfo(959701013));
			return infoResult;
		}
		else if(querySellerByName(inputParam.getSeller_name().trim()) >0)
		{
			infoResult.setResultCode(959701012);
			infoResult.setResultMessage(bInfo(959701012));
			return infoResult;
		}
		else if (StringUtils.isBlank(inputParam.getUser_name())) {
			infoResult.setResultCode(959701024);
			infoResult.setResultMessage(bInfo(959701024));
			return infoResult;
		}

		else if (StringUtils.isBlank(inputParam.getUser_password())) {
			infoResult.setResultCode(959701025);
			infoResult.setResultMessage(bInfo(959701025));
			return infoResult;
		}
		else if (StringUtils.isNotBlank(inputParam.getUser_name())) {
			infoResult = queryUserByName(inputParam.getUser_name());
			if (1 == infoResult.getResultCode())
			{
				infoResult = createUcSellerList(inputParam);
				if (infoResult.getResultCode() == 1) {
					FlowService flowService = new FlowService();
					ScFlowMain sfm = new ScFlowMain();
					sfm.setCreator(inputParam.getUser_name());
					sfm.setCurrentStatus(UserCenterConst.SELLER_UNAUDITED);
					sfm.setFlowIsend(0);
					sfm.setFlowRemark(inputParam.getCompany_name());
					sfm.setFlowTitle("商家入驻：" + inputParam.getSeller_name() + "需要你审批！");
					sfm.setFlowType(UserCenterConst.SELLER_AUDIT_TYPE);
					sfm.setFlowUrl(inputParam.getSeller_url());
					sfm.setOuterCode(infoResult.getResultMessage());
					RootResult rr = flowService.CreateFlow(sfm);
					if (rr.getResultCode() != 1) {
						infoResult.setResultCode(rr.getResultCode());
						infoResult.setResultMessage(rr.getResultMessage());
					}
				}
			}
			else
			{
				return infoResult;
			}//
				return infoResult;
		}
//		if (StringUtils.isBlank(inputParam.getBrandCode())
//				|| StringUtils.isBlank(inputParam.getCategoryCode())) {
//			infoResult = createUcSellerList_new(inputParam);
//		} 
//		else {
////			String[] c_code = inputParam.getCategoryCode().split(",");
//			// 提前检测分成比例是否存在
////			double temp_cps = 0;
////			for (int i = 0; i < c_code.length; i++) {
////				try {
////					temp_cps = new CategoryService().getCategoryRate(c_code[i]);
////				} catch (NullPointerException e1) {
////					infoResult.setResultCode(959701011);
////					infoResult.setResultMessage(bInfo(959701011));
////					return infoResult;
////				}
////				if (temp_cps == 0.00) {
////					infoResult.setResultCode(959701011);
////					infoResult.setResultMessage(bInfo(959701011));
////					return infoResult;
////				}
////			}
//			infoResult = createUcSellerList(inputParam);
//		}

		// ############################ 流程处理开始
		
		// ############################ 流程处理开始
		return infoResult;
	}

	/**
	 * createUcSellerList:(创建商家信息). <br/>
	 * 
	 * @author hxd
	 * @param info
	 * @return
	 * @since JDK 1.6
	 */
	public SellerInfoResult createUcSellerList(SellerInfoInput inputParam) {
		SellerInfoResult result = new SellerInfoResult();
		MDataMap insertDatamap = new MDataMap();
		String seller_code = WebHelper.upCode("SI");
		String user_code = WebHelper.upCode("UI");
		insertDatamap.put("seller_code", seller_code);
		insertDatamap.put("seller_name", inputParam.getSeller_name());
		insertDatamap.put("seller_descrption",
				inputParam.getSeller_description());
		insertDatamap.put("seller_status", UserCenterConst.SELLER_UNAUDITED);
		insertDatamap.put("seller_account", inputParam.getCompany_name());
		insertDatamap.put("seller_url", inputParam.getSeller_url());
		insertDatamap.put("seller_area", inputParam.getSellerArea());
		insertDatamap.put("seller_telephone", inputParam.getSellerTelephone());
		insertDatamap.put("seller_return_address",
				inputParam.getSellerReturnAddress());
		insertDatamap.put("seller_return_postcode",
				inputParam.getSellerReturnPostcode());
		insertDatamap.put("seller_return_contact",
				inputParam.getSellerReturnContact());
		insertDatamap.put("seller_return_telephone",  ///
				inputParam.getSellerReturnTelephone());
		
		insertDatamap.put("seller_company_name",  ///
				inputParam.getSellerCompanyName());
		
		
		insertDatamap.put("dataId",inputParam.getDataId());
		insertDatamap.put("editId",inputParam.getEditId());

		String brandCode = inputParam.getBrandCode();
		//String categoryCode = inputParam.getCategoryCode();
//		if (StringUtils.isEmpty(inputParam.getBrandCode().trim())) {
//			result.setResultCode(959701002);
//			result.setResultMessage(bInfo(959701002));
//			return result;
//		}
		if (StringUtils.isEmpty(inputParam.getCategoryCode().trim())) {
			result.setResultCode(959701003);
			result.setResultMessage(bInfo(959701003));
			return result;
		} 
		else {
			try {
				DbUp.upTable("uc_sellerinfo").dataInsert(insertDatamap);
			} catch (Exception e) {
				bLogError(959701005);
				result.setResultCode(959701005);
				result.setResultMessage(bInfo(959701005));
				return result;
			}
			String[] bCode = brandCode.split(",");
			//String[] cCode = categoryCode.split(",");
			result = inserRelation(bCode, seller_code, "brand_code",
					"uc_seller_brand_relation");
			if (result.getResultCode() != 1)
				return result;
//			result = inserRelation(cCode, seller_code, "category_code",
//					"uc_seller_category_relation");
//			if (result.getResultCode() != 1)
//				return result;
			//result = updateCpsrate(cCode);
//			if (result.getResultCode() != 1)
//				return result;
			result = insertUserinfo(inputParam, seller_code, user_code);
			if (result.getResultCode() != 1)
				return result;
			result = inserUserRole(user_code, UserCenterConst.ROLE_CODE);
			if (result.getResultCode() != 1)
				return result;

			 result.setResultMessage(seller_code);

			return result;
		}
	}

	/**
	 * 
	 * inserRelation:(向商家品牌或商家分类表插入数据). <br/>
	 * 
	 * @author hxd
	 * @param b
	 *            逗号分隔后的商家品牌或商家分类编码数组
	 * @param sellCode
	 *            卖家编号
	 * @param bcCode
	 *            编码代码
	 * @param tableName
	 *            表名
	 * @since JDK 1.6
	 */
	public SellerInfoResult inserRelation(String[] b, String sellCode,
			String bcCode, String tableName) {
		SellerInfoResult infoResult = new SellerInfoResult();
		MDataMap insertDatamap = new MDataMap();
		for (int i = 0; i < b.length; i++) {
			insertDatamap.put(bcCode, b[i]);
			insertDatamap.put("seller_code", sellCode);
			try {
				DbUp.upTable(tableName).dataInsert(insertDatamap);
			} catch (Exception e) {
				bLogError(959701004);
				infoResult.setResultCode(959701004);
				infoResult.setResultMessage(bInfo(959701004));
				return infoResult;
			}
		}
		return infoResult;
	}

	/**
	 * updateCpsrate:(初始化分类信息对应的分成比率). <br/>
	 * 
	 * @author hxd
	 * @param b
	 * @since JDK 1.6
	 */
	public SellerInfoResult updateCpsrate(String[] b) {
		SellerInfoResult infoResult = new SellerInfoResult();
		MDataMap updateDatamap = new MDataMap();
		double temp_cps = 0;
		for (int i = 0; i < b.length; i++) {
			updateDatamap.put("category_code", b[i]);
			try {
				temp_cps = new CategoryService().getCategoryRate(b[i]);
			} catch (NullPointerException e1) {

				infoResult.setResultCode(959701011);
				infoResult.setResultMessage(bInfo(959701011));
				return infoResult;
			}

			if (temp_cps == 0.00) {
				infoResult.setResultCode(959701011);
				infoResult.setResultMessage(bInfo(959701011));
				return infoResult;
			}
			updateDatamap.put("cpsrate", String.valueOf(temp_cps));
			try {
				DbUp.upTable("uc_seller_category_relation").dataUpdate(
						updateDatamap, "cpsrate", "category_code");
			} catch (Exception e) {
				infoResult.setResultCode(959701006);
				infoResult.setResultMessage(bInfo(959701006));
				return infoResult;
			}
		}
		return infoResult;
	}

	/**
	 * insertUserinfo:(用户注册). <br/>
	 * 
	 * @author hxd
	 * @param inputParam
	 * @param mangerCode
	 * @return
	 * @since JDK 1.6
	 */
	public SellerInfoResult insertUserinfo(SellerInfoInput inputParam,
			String manage_code, String user_code) {
		SellerInfoResult infoResult = new SellerInfoResult();
		String nowTime = DateUtil.getNowTime();
		String user_name = inputParam.getUser_name();
		// String manger_code = mangerCode;
		MDataMap insertDatamap = new MDataMap();
		insertDatamap.put("user_code", user_code);
		insertDatamap.put("manage_code", manage_code);
		insertDatamap.put("create_time", nowTime);
		if (StringUtils.isEmpty(user_name.trim())) {
			infoResult.setResultCode(959701009);
			infoResult.setResultMessage(bInfo(959701009));
			return infoResult;
		}
		insertDatamap.put("user_name", user_name);
		if (StringUtils.isEmpty(inputParam.getUser_password().trim())) {
			infoResult.setResultCode(959701010);
			infoResult.setResultMessage(bInfo(959701010));
			return infoResult;
		}
		insertDatamap.put("user_password",
				SecrurityHelper.MD5Customer(inputParam.getUser_password()));
		// //
		insertDatamap.put("user_type_did", "467721200003");
		try {
			DbUp.upTable("za_userinfo").dataInsert(insertDatamap);
		} catch (Exception e) {
			e.printStackTrace();
			infoResult.setResultCode(959701007);
			infoResult.setResultMessage(bInfo(959701007));
			return infoResult;
		}
		return infoResult;
	}

	/**
	 * inserUserRole:(插入用户角色关系). <br/>
	 * 
	 * @author hxd
	 * @return
	 * @since JDK 1.6
	 */
	public SellerInfoResult inserUserRole(String user_code, String role_code) {
		SellerInfoResult infoResult = new SellerInfoResult();
		MDataMap insertDatamap = new MDataMap();
		insertDatamap.put("user_code", user_code);
		insertDatamap.put("role_code", role_code);
		try {
			DbUp.upTable("za_userrole").dataInsert(insertDatamap);
		} catch (Exception e) {
			e.printStackTrace();
			infoResult.setResultCode(959701008);
			infoResult.setResultMessage(bInfo(959701008));
			return infoResult;
		}
		return infoResult;
	}


	/**
	 * inserUserRole:(根据商家名称查询商家信息). <br/>
	 * 
	 * @author hxd
	 * @param seller_name
	 * @return
	 * @since JDK 1.6
	 */
	public SellerInfoResult queryUserByName(String user_name) {
		SellerInfoResult infoResult = new SellerInfoResult();
		MDataMap queryDatamap = DbUp.upTable("za_userinfo").one("user_name",
				user_name);
		if (null != queryDatamap) {
			infoResult.setResultCode(959701015);
			infoResult.setResultMessage(bInfo(959701015));
			return infoResult;
		}
		return infoResult;
	}

	/**
	 * createUcSellerList:(创建商家信息). <br/>
	 * 
	 * @author hxd
	 * @param info
	 * @return
	 * @since JDK 1.6
	 */
	public SellerInfoResult createUcSellerList_new(SellerInfoInput inputParam) {
		SellerInfoResult result = new SellerInfoResult();
		MDataMap insertDatamap = new MDataMap();
		String seller_code = WebHelper.upCode("SI");
		String user_code = WebHelper.upCode("UI");
		insertDatamap.put("seller_code", seller_code);
		insertDatamap.put("seller_name", inputParam.getSeller_name());
		insertDatamap.put("seller_descrption",
				inputParam.getSeller_description());
		insertDatamap.put("seller_status", UserCenterConst.SELLER_UNAUDITED);
		insertDatamap.put("seller_account", inputParam.getCompany_name());
		insertDatamap.put("seller_url", inputParam.getSeller_url());
		insertDatamap.put("seller_area", inputParam.getSellerArea());
		insertDatamap.put("seller_telephone", inputParam.getSellerTelephone());
		insertDatamap.put("seller_return_address",
				inputParam.getSellerReturnAddress());
		insertDatamap.put("seller_return_postcode",
				inputParam.getSellerReturnPostcode());
		insertDatamap.put("seller_return_contact",
				inputParam.getSellerReturnContact());

		try {
			DbUp.upTable("uc_sellerinfo").dataInsert(insertDatamap);
		} catch (Exception e) {
			bLogError(959701005);
			result.setResultCode(959701005);
			result.setResultMessage(bInfo(959701005));
			return result;
		}
		result = insertUserinfo(inputParam, seller_code, user_code);
		if (result.getResultCode() != 1)
			return result;
		result = inserUserRole(user_code, UserCenterConst.ROLE_CODE);
		if (result.getResultCode() != 1)
			return result;
		
		result.setResultMessage(seller_code);
		
		return result;

	}
	
	
	/**
	 * inserUserRole:(根据商家名称查询商家信息). <br/>
	 * 
	 * @author hxd
	 * @param seller_name
	 * @return
	 * @since JDK 1.6
	 */
	private int querySellerByName(String seller_name) {
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("seller_name", seller_name);
		int count = DbUp.upTable("uc_sellerinfo").dataCount("seller_name=:seller_name", mWhereMap);
		return count;
	}
}
