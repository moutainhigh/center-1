package com.cmall.productcenter.service;

import com.cmall.productcenter.common.Constants;
import com.cmall.systemcenter.service.ScDefineService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

import java.util.List;

/**
 * 商品审批相关接口
 * @author pang_jhui
 *
 */
public class ProductFlowFacade extends BaseClass {
	
	/**
	 * 获取具有招商经理角色的用户信息
	 * @return 招商经理用户信息
	 */
	public static List<MDataMap> getMDUserInfoList(){
		
		String roleCode = getDefineMDRoleCode();
		
		return new ProductFlowService().getUserInfoList(roleCode);
		
	}
	
	/**
	 * 获取定义的招商经理角色code
	 * @return 招商经理角色code
	 */
	public static String getDefineMDRoleCode(){
		
		return ScDefineService.getDefineByCode(Constants.ROLE_MD_PARAM_ID).get("define_name");
		
	}
	
	/**
	 * 获取product json
	 * @param product_code
	 * 		商品编号
	 * @return String
	 * 		json
	 */
	public static String getProductJson(String product_code){
		
		MDataMap mDataMap = DbUp.upTable("pc_productflow").oneWhere("product_json", "", "", "product_code",product_code);
		
		if(mDataMap != null){
			
			mDataMap.get("product_json");
			
		}
		
		return "";
		
	}
	

}
