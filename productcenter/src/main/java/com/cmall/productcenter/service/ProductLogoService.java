package com.cmall.productcenter.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.srnpr.xmassystem.load.LoadProductAuthorityLogo;
import com.srnpr.xmassystem.load.LoadProductAuthorityLogoForSeller;
import com.srnpr.xmassystem.load.LoadProductCommonProblem;
import com.srnpr.xmassystem.load.LoadSellerInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelAuthorityLogo;
import com.srnpr.xmassystem.modelproduct.PlusModelAuthorityLogos;
import com.srnpr.xmassystem.modelproduct.PlusModelCommonProblem;
import com.srnpr.xmassystem.modelproduct.PlusModelCommonProblems;
import com.srnpr.xmassystem.modelproduct.PlusModelProductAuthorityLogoForSellerQuery;
import com.srnpr.xmassystem.modelproduct.PlusModelProductAuthorityLogoQuery;
import com.srnpr.xmassystem.modelproduct.PlusModelProductCommonProblemQuery;
import com.srnpr.xmassystem.modelproduct.PlusModelSellerInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelSellerQuery;
import com.srnpr.xmassystem.service.PlusServiceSeller;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;


/** 
 * 商品的一些标志信息
*/
public class ProductLogoService extends BaseClass {

	private final String CDOG = "SI3003";
	private final String CFAMILY = "SI2003";
	/**
	 * 获取商品的权威标志
	 * @param sellerCode
	 * @param smallSellerCode
	 * @return
	 */
	public List<PlusModelAuthorityLogo> getProductAuthorityLogo(String sellerCode,String smallSellerCode , String productCode) {
		List<PlusModelAuthorityLogo> authorityLogo = new ArrayList<PlusModelAuthorityLogo>();
		
		if (StringUtils.isBlank(sellerCode) || StringUtils.isBlank(smallSellerCode)) {
			return authorityLogo;
		}
		
		// 缓存获取商品权威标识
		PlusModelAuthorityLogos plusModelAuthorityLogos = new LoadProductAuthorityLogo().upInfoByCode(new PlusModelProductAuthorityLogoQuery(sellerCode));
		// 缓存获取商品权威标识，根据4.1.1需求文档内容，此处获取的是商户自己设置的标识 - 20170303 - Yangcl
		PlusModelAuthorityLogos sellerAuthorityLogs = new LoadProductAuthorityLogoForSeller().upInfoByCode(new PlusModelProductAuthorityLogoForSellerQuery(productCode));
//		List<PlusModelAuthorityLogo> list = new ArrayList<PlusModelAuthorityLogo>();
//		list.addAll(plusModelAuthorityLogos.getAuthorityLogos());
//		list.addAll(sellerAuthorityLogs.getAuthorityLogos());
//		plusModelAuthorityLogos.setAuthorityLogos(list); 
		
		
		String productType = "";
		PlusModelSellerInfo sellerInfo = new LoadSellerInfo().upInfoByCode(new PlusModelSellerQuery(smallSellerCode));
		
		String seller_type = sellerInfo.getUc_seller_type();
		if (CFAMILY.equals(smallSellerCode) && CFAMILY.equals(sellerCode)) {
			productType = "4497471600150001";		//LD商品
		}else if (new PlusServiceSeller().isKJSeller(smallSellerCode)) {
			productType = "4497471600150003";		//跨境商品
//		}else if (smallSellerCode.startsWith("SF031")) {
		}else if (StringUtils.isNotBlank(seller_type)) {
			productType = "4497471600150002";		//商户商品
		}else if((CDOG.equals(smallSellerCode))&& 
				(CFAMILY.equals(sellerCode)||CDOG.equals(sellerCode))){
			productType = "4497471600150004";//沙皮狗商品
		}
		for (PlusModelAuthorityLogo model : plusModelAuthorityLogos.getAuthorityLogos()) {
			if ("449747110001".equals(model.getAllFlag())) {		//是否全场为否时，判断商品类型
				if (StringUtils.isNotEmpty(model.getShowProductSource())) {
					for (String channelCode : model.getShowProductSource().split(",")) {
						if (productType.equals(channelCode)) {
							authorityLogo.add(model);
							break;
						}
					}
				}
			}else {
				authorityLogo.add(model);
			}
		}
		authorityLogo.addAll(sellerAuthorityLogs.getAuthorityLogos());
		return authorityLogo;
	}
	/**
	 * 获取商品的常见问题（目前只支持跨境商品）
	 * @param sellerCode
	 * @param smallSellerCode
	 * @return
	 */
	public List<PlusModelCommonProblem> getProductCommonProblem(String sellerCode,String smallSellerCode) {
		List<PlusModelCommonProblem> commonProblemList = new ArrayList<PlusModelCommonProblem>();
		
		if (StringUtils.isBlank(sellerCode) || StringUtils.isBlank(smallSellerCode)) {
			return commonProblemList;
		}
		
		if (new PlusServiceSeller().isKJSeller(smallSellerCode)) {
		// 缓存获取商品的常见问题
		PlusModelCommonProblems plusModelCommonProblems = new LoadProductCommonProblem().upInfoByCode(new PlusModelProductCommonProblemQuery(sellerCode));
		commonProblemList = plusModelCommonProblems.getCommonProblems();
		}
		return commonProblemList;
	}
	
	/**
	 * 根据父编号获取sc_define表中子项的编号跟名称
	 * @param parentCode
	 * @return
	 */
	public Map<String,String> getScDefineByparentCode(String parentCode){
		Map<String,String> define = new HashMap<String,String>();
		if (null == parentCode || parentCode.length() == 0) {
			return define;
		}
		String sFields = "define_code,define_name";
		String sWhere = "parent_code = '" + parentCode +"'";
		List<MDataMap> defineMap = DbUp.upTable("sc_define").queryAll(sFields, "", sWhere, new MDataMap());
		for (MDataMap mDataMap : defineMap) {
			define.put(mDataMap.get("define_code"), mDataMap.get("define_name"));
		}
		return define;
	}
}
