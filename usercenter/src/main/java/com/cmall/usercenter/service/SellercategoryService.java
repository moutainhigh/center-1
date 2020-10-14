package com.cmall.usercenter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.PcProductinfo;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 商品分类
 * @author jlin
 *
 */
public class SellercategoryService {

	private MDataMap gateGoryMap;
	private MDataMap PcodeMap;
	
	public String getCategorys(String parentCode) {
		JsonHelper<List<MDataMap>> jsonHelper = new JsonHelper<List<MDataMap>>();

		MDataMap map = new MDataMap();
		map.put("flaginable", "449746250001");
		map.put("seller_code", "SI2003");
		if(parentCode == null || "".equals(parentCode)) {
			map.put("parent_code", "44971604");
		}else {
			map.put("parent_code", parentCode);
		}
		
		String whereCondition = "flaginable = :flaginable and seller_code = :seller_code and parent_code = :parent_code";
		List<MDataMap> categoryList = DbUp.upTable("uc_sellercategory").queryAll("", "sort", whereCondition, map);
		return jsonHelper.ObjToString(categoryList);
	}
	
	public MDataMap getCateGoryByProduct(String product_code,String seller_code){
		
		MDataMap ret = new MDataMap();
		if(gateGoryMap==null){
			gateGoryMap=getCateGoryBySeller(seller_code);
		}
		
		if(PcodeMap==null){
			PcodeMap=getPcodeBySeller(seller_code);
		}
		
		//查询
		List<MDataMap> listCode=DbUp.upTable("uc_sellercategory_product_relation").queryAll("category_code", "", "product_code=:product_code and seller_code=:seller_code", new MDataMap("product_code",product_code,"seller_code",seller_code));
		
		for (MDataMap mDataMap : listCode) {
			String category_code_copy=mDataMap.get("category_code");
			String category_code= mDataMap.get("category_code");
			String name="";
			
			while(StringUtils.isNotEmpty(category_code) && category_code.length()>4){
				
				String category_name=gateGoryMap.get(category_code);
				if(category_name==null){
					name=null;
					break;
				}
				name=category_name+"->"+name;
				category_code=PcodeMap.get(category_code);
			}
			
			if(name!=null){
				if(name.length()>1){ //
					name=name.substring(5, name.length()-2);
				}
				ret.put(category_code_copy, name);
			}
		}
		
		return ret;
	}
	
	public String getSoidByProduct(String product_code) {
		MDataMap map = new MDataMap();
		map.put("product_code", product_code);
		return DbUp.upTable("pc_productinfo").dataSqlOne("select * from pc_productinfo where product_code = :product_code", map).get("so_id")+"";
	}
	
	/** 
	* @Description: 根据多个分类编号获取分类名称
	* @param categroyCodes 分类编号
	* @param sellerCode 应用编号
	* @author 张海生
	* @date 2015-6-6 下午2:29:35
	* @return MDataMap 
	* @throws 
	*/
	public MDataMap getCateGoryNmaes(String categroyCodes, String sellerCode) {
		MDataMap ret = new MDataMap();
		if(StringUtils.isNotBlank(categroyCodes)){
			List<String> nameList = new ArrayList<String>();
			String codesArray[] = categroyCodes.split(",");
			for (String code : codesArray) {
				if (StringUtils.isNotBlank(code)) {
					nameList.add(this.getCateGoryShow(code, sellerCode).get("categoryName"));
				}
			}
			ret.put("categoryName", StringUtils.join(nameList, ","));
		}
		return ret;
	}
	
	/** 
	 * @Description: 根据多个分类编号获取分类名称
	 * @param categroyCodes 分类编号
	 * @param sellerCode 应用编号
	 * @return MDataMap 
	 * @throws 
	 */
	public MDataMap getCateGoryNmaes1(String categroyCodes, String sellerCode) {
		MDataMap ret = new MDataMap();
		if(StringUtils.isNotBlank(categroyCodes)){
			List<String> nameList = new ArrayList<String>();
			String codesArray[] = categroyCodes.split(",");
			for (String code : codesArray) {
				if (StringUtils.isNotBlank(code)) {
					nameList.add(this.getCateGoryShow(code, sellerCode).get("categoryName").split("->")[2]);
				}
			}
			ret.put("categoryName", StringUtils.join(nameList, ","));
		}
		return ret;
	}
	
	/** 
	* @Description:获取栏目对应的分类
	* @param categroyCode 分类编号
	* @param sellerCode 分类编号
	* @author 张海生
	* @date 2015-3-17 下午6:51:46
	* @return MDataMap 
	* @throws 
	*/
	public MDataMap getCateGoryShow(String categroyCode, String sellerCode) {
		MDataMap ret = new MDataMap();
		if (gateGoryMap == null) {
			gateGoryMap = getCateGoryBySeller(sellerCode);
		}
		if (PcodeMap == null) {
			PcodeMap = getPcodeBySeller(sellerCode);
		}
		String name = "";
		try {
			while (categroyCode.length() > 8) {
				name = gateGoryMap.get(categroyCode) + "->" + name;
				categroyCode = PcodeMap.get(categroyCode);
			}
			if (name.length() > 1) { //
				name = name.substring(0, name.length() - 2);
			}
			ret.put("categoryName", name);
		} catch (Exception e) {
			ret.put("categoryName", "<span class='w_regex_need'>该分类找不到，请重新维护！</span>");
		}
		
		return ret;
	}
	
	
	public MDataMap getCateGoryByProductSimple(String product_code,String seller_code){
		
		MDataMap ret = new MDataMap();
		if(gateGoryMap==null){
			gateGoryMap=getCateGoryBySeller(seller_code);
		}
		
		//查询
		List<MDataMap> listCode=DbUp.upTable("uc_sellercategory_product_relation").queryAll("category_code", "", "product_code=:product_code and seller_code=:seller_code", new MDataMap("product_code",product_code,"seller_code",seller_code));
		
		for (MDataMap mDataMap : listCode) {
			String category_code= mDataMap.get("category_code");
			String name=gateGoryMap.get(category_code);
			if(name!=null){//防止变态的修改数据库
				ret.put(category_code, name);
			}
		}
		
		return ret;
	}
	
	
	public MDataMap getCateGoryBySeller(String seller_code){
		MDataMap dataMap = new MDataMap();
		List<MDataMap> listCategory=DbUp.upTable("uc_sellercategory").queryAll("category_code,category_name", "", " seller_code=:seller_code ", new MDataMap("seller_code",seller_code));
		for (MDataMap mDataMap : listCategory) {
			dataMap.put(mDataMap.get("category_code"), mDataMap.get("category_name"));
		}
		return dataMap;
	}

	
	private MDataMap getPcodeBySeller(String seller_code){
		MDataMap dataMap = new MDataMap();
		List<MDataMap> listCategory=DbUp.upTable("uc_sellercategory").queryAll("category_code,parent_code", "", " seller_code=:seller_code ", new MDataMap("seller_code",seller_code));
		for (MDataMap mDataMap : listCategory) {
			dataMap.put(mDataMap.get("category_code"), mDataMap.get("parent_code"));
		}
		return dataMap;
	}
	/**
	 * 根据categoryCode获取名称
	 * @param category_code
	 * @param seller_code
	 * @return
	 */
	public String getCateGoryByCode(String category_code,String seller_code){
		MDataMap category=DbUp.upTable("uc_sellercategory").oneWhere("category_code,category_name", "", " seller_code=:seller_code and category_code=:category_code ","seller_code",seller_code,"category_code",category_code);
		if (null == category || category.isEmpty()) {
			return "";
		}
		return category.get("category_name");
	}
	
	public List<Map<String, Object>> getCateGoryLevel2(){
		
		//查询
		List<Map<String, Object>> dataList=DbUp.upTable("uc_sellercategory_product_relation").dataSqlList("SELECT uc.category_code,uc.category_name FROM usercenter.uc_sellercategory uc where seller_code='SI2003' and `level`=2 and flaginable = '449746250001'", new MDataMap());
		
		return dataList;
	}
}
