package com.cmall.productcenter.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jsoup.helper.StringUtil;

import com.cmall.productcenter.model.Category;
import com.cmall.productcenter.model.CategoryBaseInfo;
import com.cmall.productcenter.model.PcBrandinfo;
import com.cmall.productcenter.model.PcSellerQualification;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class CategoryService  extends BaseClass {
	
	/**
	 * 
	 * @param pid
	 * @return
	 */
	public List<Category> getCategory(String pid){
		
		Category c = null;
		List<Category> categoryList = new ArrayList<Category>();
		
		this.bLogInfo(0, "getCategory pid :",pid);
		
		MDataMap map = new MDataMap();
		map.put("parent_code", pid);
		List<MDataMap> list = DbUp.upTable("pc_categoryinfo").query("", "","parent_code=:parent_code",map, -1, -1);
		
		//System.out.println("size xxxxxxxxxxxxxx:"+list.size());
		for(MDataMap m : list){
			c = new Category();
			c.setUid(m.get("uid"));
			c.setId(m.get("zid"));
			c.setCategoryCode(m.get("category_code"));
			c.setCategoryName(m.get("category_name"));
			c.setParentCode(m.get("parent_code"));
			c.setSort(m.get("sort"));
			
			categoryList.add(c);
			
			this.bLogInfo(0, "getCategory zid :",m.get("zid"));
			this.bLogInfo(0, "getCategory uid :",m.get("uid"));
			this.bLogInfo(0, "getCategory category_code :",m.get("category_code"));
			this.bLogInfo(0, "getCategory category_name :",m.get("category_name"));
			this.bLogInfo(0, "getCategory parent_code :",m.get("parent_code"));
			this.bLogInfo(0, "getCategory sort :",m.get("sort"));
		}
		return categoryList;
	}
	/**
	 * 加载所有商品分类
	 * @return
	 */
	public List<Category> getAllCategory(){
		Category c = null;
		List<Category> categoryList = new ArrayList<Category>();
		MDataMap map = new MDataMap();
		List<MDataMap> list = DbUp.upTable("pc_categoryinfo").query("", "","category_code REGEXP '^44971603'",map, -1, -1);
		//System.out.println("list size :"+list.size());
		
		for(MDataMap m : list){
			c = new Category();
			c.setUid(m.get("uid"));
			c.setId(m.get("zid"));
			c.setCategoryCode(m.get("category_code"));
			c.setCategoryName(m.get("category_name"));
			c.setParentCode(m.get("parent_code"));
			c.setSort(m.get("sort"));
			
			categoryList.add(c);
		}
		return categoryList;
	}
	/**
	 * 加载所有商品分类
	 * @return
	 */
	public List<Category> _getAllCategory(String pid){
		Category c = null;
		List<Category> categoryList = new ArrayList<Category>();
		MDataMap map = new MDataMap();
		map.put("parent_code", "^"+pid);
		List<MDataMap> list = DbUp.upTable("pc_categoryinfo").query("", "","parent_code REGEXP :parent_code",map, -1, -1);
		//System.out.println("_getAllCategory list size :"+list.size());
		
		for(MDataMap m : list){
			c = new Category();
			c.setUid(m.get("uid"));
			c.setId(m.get("zid"));
			c.setCategoryCode(m.get("category_code"));
			c.setCategoryName(m.get("category_name"));
			c.setParentCode(m.get("parent_code"));
			c.setSort(m.get("sort"));
			
			categoryList.add(c);
		}
		return categoryList;
	}
	/**
	 * 加载所有商品品牌
	 * @return
	 */
	public List<PcBrandinfo> getBrand(String pid){
		PcBrandinfo c = null;
		List<PcBrandinfo> categoryList = new ArrayList<PcBrandinfo>();
		MDataMap map = new MDataMap();
		map.put("parent_code", "^"+pid);
		List<MDataMap> list = DbUp.upTable("pc_brandinfo").query("", "","parent_code REGEXP :parent_code",map, -1, -1);
		//System.out.println("_getAllCategory list size :"+list.size());
		
		for(MDataMap m : list){
			c = new PcBrandinfo();
			//c.setUid(m.get("uid"));
			//c.setZid(Integer.valueOf(m.get("zid")));
			c.setBrandCode(m.get("brand_code"));
			c.setBrandName(m.get("brand_name"));
			c.setBrandPic(m.get("brand_pic"));
			c.setBrandNameCn(m.get("brand_name_en"));
			
			
			categoryList.add(c);
		}
		return categoryList;
	}
	
	/**
	 * 根据品牌id查询品牌信息
	 * @return
	 */
	public PcBrandinfo getBrandById(String brandCode){
		PcBrandinfo c = new PcBrandinfo();
		Map<String, Object> m = DbUp.upTable("pc_brandinfo").dataSqlOne("select * from pc_brandinfo where brand_code=:brand_code ",  new MDataMap("brand_code",brandCode));
		c.setBrandCode(m.get("brand_code").toString());
		c.setBrandName(m.get("brand_name").toString());
		c.setBrandPic(m.get("brand_pic").toString());
		c.setBrandNameCn(m.get("brand_name_en").toString());	
		return c;
	}
	/**
	 * 根据分类ID取分成
	 * @param id
	 * @return
	 */
	public double getCategoryRate(String id){
		 
		MDataMap categoryRelData = DbUp.upTable("pc_categoryinfo").one("category_code",id);
		String cpsrate = categoryRelData.get("cpsrate");
		if(cpsrate != null && !cpsrate.equals("")){
			return Double.valueOf(cpsrate);
		}
		return 0.00;
	}
	
	
	
	/**
	 * @param sellerCode
	 * @return
	 */
	public List<PcBrandinfo> getBrandListBySellerCode(String sellerCode){
		PcBrandinfo c = null;
		List<PcBrandinfo> brandList = new ArrayList<PcBrandinfo>();
		
		
		
		MDataMap map = new MDataMap();
		map.put("seller_code", sellerCode);
		//先取出商家的关联分类，现在固定为 二级分类
		List<MDataMap> list = DbUp.upTable("uc_seller_brand_relation").query("brand_code", "","seller_code=:seller_code",map, -1, -1);
		
		if(list == null || list.size() == 0)
			return brandList;
		
		MDataMap urMapParam = new MDataMap();
		int i = 0;
		String whereStr = "";
		for (MDataMap dm : list) {
			urMapParam.put("brand_code" + i, dm.get("brand_code"));
			whereStr += " brand_code=:brand_code" + i + " or";
			i++;
		}

		if (whereStr.length() > 2)
			whereStr = whereStr.substring(0, whereStr.length() - 2);
		
		
		List<MDataMap> brandlist = DbUp.upTable("pc_brandinfo").query("", "", whereStr ,urMapParam, -1, -1);
		//System.out.println("_getAllBrand list size :"+brandlist.size());
		
		for(MDataMap m : brandlist){
			c = new PcBrandinfo();
			c.setUid(m.get("uid"));
			c.setZid(Integer.valueOf(m.get("zid")));
			c.setBrandCode(m.get("brand_code"));
			c.setBrandName(m.get("brand_name"));
			c.setBrandPic(m.get("brand_pic"));
			
			
			brandList.add(c);
		}
		return brandList;
	}
	public List<PcBrandinfo> getBrandList(){
		PcBrandinfo c = null;
		List<PcBrandinfo> brandList = new ArrayList<PcBrandinfo>();
		MDataMap mw = new MDataMap();
		mw.put("flag_enable", "1");
		List<MDataMap> brandlist = DbUp.upTable("pc_brandinfo").query("", "", "" ,mw, -1, -1);
		for(MDataMap m : brandlist){
			c = new PcBrandinfo();
			c.setUid(m.get("uid"));
			c.setZid(Integer.valueOf(m.get("zid")));
			c.setBrandCode(m.get("brand_code"));
			c.setBrandName(m.get("brand_name"));
			c.setBrandPic(m.get("brand_pic"));
			brandList.add(c);
		}
		return brandList;
	}
	//品牌中英文放到一个字段，惠家有查询方法
	public List<PcBrandinfo> getBrandListForSpecial(){
		PcBrandinfo c = null;
		List<PcBrandinfo> brandList = new ArrayList<PcBrandinfo>();
		MDataMap mw = new MDataMap();
		mw.put("flag_enable", "1");
		List<MDataMap> brandlist = DbUp.upTable("pc_brandinfo").query("brand_code,brand_name,brand_name_en", "", "" ,mw, -1, -1);
		for(MDataMap m : brandlist){
			c = new PcBrandinfo();
			c.setBrandCode(m.get("brand_code"));
			//中文名称+英文名称
			String brandName = m.get("brand_name");
			if (null != m.get("brand_name_en") && !"".equals(m.get("brand_name_en"))) {
				brandName += "  ("+m.get("brand_name_en")+")";
			}
			c.setBrandName(brandName);
			brandList.add(c);
		}
		return brandList;
	}
	
	//品牌中英文放到一个字段，惠家有查询方法(根据商户编号查询商户的品牌)
	public List<PcBrandinfo> getBrandListForMerchant(String small_seller_code){
		PcBrandinfo c = null;
		List<PcBrandinfo> brandList = new ArrayList<PcBrandinfo>();
		String sSql = "SELECT pc_brandinfo.brand_code, pc_brandinfo.brand_name, pc_brandinfo.brand_name_en FROM  pc_seller_qualification  LEFT JOIN pc_brandinfo  ON pc_seller_qualification.brand_code = pc_brandinfo.brand_code " + 
				" WHERE pc_seller_qualification.small_seller_code = '" + small_seller_code + "' AND pc_seller_qualification.end_time > NOW() AND pc_brandinfo.flag_enable = 1";
		List<Map<String, Object>> brandLsit = DbUp.upTable("pc_brandinfo").dataSqlList(sSql, new MDataMap());
		for(Map<String, Object> m : brandLsit){
			c = new PcBrandinfo();
			c.setBrandCode((String)m.get("brand_code"));
			//中文名称+英文名称
			String brandName = (String)m.get("brand_name");
			if (null != m.get("brand_name_en") && !"".equals(m.get("brand_name_en"))) {
				brandName += "  ("+m.get("brand_name_en")+")";
			}
			c.setBrandName(brandName);
			brandList.add(c);
		}
		return brandList;
	}
	
	//品牌中英文放到一个字段，惠家有查询方法(根据商户编号查询商户全部的品牌)
	public List<PcBrandinfo> getAllBrandBySellCode(String small_seller_code){
		PcBrandinfo c = null;
		List<PcBrandinfo> brandList = new ArrayList<PcBrandinfo>();
		String sSql = "SELECT pc_brandinfo.brand_code, pc_brandinfo.brand_name, pc_brandinfo.brand_name_en FROM  pc_seller_qualification  LEFT JOIN pc_brandinfo  ON pc_seller_qualification.brand_code = pc_brandinfo.brand_code " + 
				" WHERE pc_seller_qualification.small_seller_code = '" + small_seller_code + "'";
		List<Map<String, Object>> brandLsit = DbUp.upTable("pc_brandinfo").dataSqlList(sSql, new MDataMap());
		for(Map<String, Object> m : brandLsit){
			c = new PcBrandinfo();
			c.setBrandCode((String)m.get("brand_code"));
			//中文名称+英文名称
			String brandName = (String)m.get("brand_name");
			if (null != m.get("brand_name_en") && !"".equals(m.get("brand_name_en"))) {
				brandName += "  ("+m.get("brand_name_en")+")";
			}
			c.setBrandName(brandName);
			brandList.add(c);
		}
		return brandList;
	}
	
	/**
	 * 获取卖家的主营分类
	 * @param sellerRelation
	 * @param currentRelationList
	 * @param sellerCode
	 * @return
	 */
	public List<Category> getSellerCategroyList(List<MDataMap> sellerRelation,
			List<Category> currentRelationList,String sellerCode){
		List<Category> categoryList = new ArrayList<Category>();
		
		if(sellerRelation == null || currentRelationList == null)
			return categoryList;
		
		for(Category c:currentRelationList){
			for(MDataMap map : sellerRelation){
				if(map.get("seller_code").equals(sellerCode)){
					if(c.getCategoryCode() == map.get("category_code")){
						categoryList.add(c);
					}
				}
			}
		}
		
		
		return categoryList;
	}
	
	
	/**
	 * 取得当前的这些店铺的关系数据
	 * @param sellerCodes
	 * @return
	 */
	public List<MDataMap> getSellerCategoryListBySellerCodes(String sellerCodes)
	{
		List<MDataMap> categoryList = new ArrayList<MDataMap>();
		
		if(sellerCodes == null || sellerCodes.equals("")){
			return categoryList;
		}else{
			String[] sellerCodeAry = sellerCodes.split(",");
			MDataMap urMapParam = new MDataMap();
			String whereStr = "";
			int i=0;
			for (String dm : sellerCodeAry) {
				urMapParam.put("seller_code" + i, dm);
				whereStr += " seller_code=:seller_code" + i + " or";
				i++;
			}

			if (whereStr.length() > 2)
				whereStr = whereStr.substring(0, whereStr.length() - 2);
			
			categoryList = DbUp.upTable("uc_seller_category_relation")
					.query("category_code,seller_code", "",whereStr,urMapParam, -1, -1);
			
		}
		
		return categoryList;
	}
	
	/**
	 * 取得商品的分类通过卖家的分类关系
	 * @param list
	 * @return
	 */
	public List<Category> getCategoryListBySellerCategoryRelation(List<MDataMap> list){
		
		
		
		List<Category> categoryList = new ArrayList<Category>();
		
		
		if(list == null)
			return categoryList;
		
		MDataMap urMapParam = new MDataMap();
		String whereStr = "";
		int i=0;
		for(MDataMap map : list){
			urMapParam.put("category_code" + i, map.get("category_code"));
			whereStr += " category_code=:category_code" + i + " or";
			i++;
		}
		
		if (whereStr.length() > 2)
			whereStr = whereStr.substring(0, whereStr.length() - 2);
		
		//取出商家的二级分类
		List<MDataMap> categorySecondList = DbUp.upTable("pc_categoryinfo").query("", "",whereStr,urMapParam, -1, -1);
	
		Category c = null;
		for(MDataMap m : categorySecondList){
			c = new Category();
			c.setUid(m.get("uid"));
			c.setId(m.get("zid"));
			c.setCategoryCode(m.get("category_code"));
			c.setCategoryName(m.get("category_name"));
			c.setParentCode(m.get("parent_code"));
			c.setSort(m.get("sort"));
			
			categoryList.add(c);
		}
		
		return categoryList;
		
	}
	
	/**
	 * 取出商家的分类
	 * @param sellerCode	商家的店铺号
	 * @param level			分类的级别 1,2,3
	 * @param parent_code	父code
	 * @return
	 */
	public List<Category> getCategoryListBySellerCode(String sellerCode,int level,String parent_code)
	{
		List<Category> categoryList = new ArrayList<Category>();
		MDataMap urMapParam = new MDataMap();
		urMapParam.put("flaginable", "449746250001");
		if ("SI3003".equals(sellerCode)) {
			urMapParam.put("seller_code", sellerCode);
		}else{
			urMapParam.put("seller_code", "SI2003");
		}
		if(level == 4){
			List<MDataMap> categoryThiredList = DbUp.upTable("uc_sellercategory").query("", "", "LENGTH(category_code)='20' and seller_code=:seller_code and flaginable = :flaginable ",urMapParam, -1, -1);
			for(MDataMap m : categoryThiredList){
				if(m.get("parent_code").equals(parent_code)){
					categoryList.add(this.getCategory(m));
				}
			}
		}else if(level == 3){
			List<MDataMap> categoryThiredList = DbUp.upTable("uc_sellercategory").query("", "", "LENGTH(category_code)='16' and seller_code=:seller_code and flaginable = :flaginable ",urMapParam, -1, -1);
			for(MDataMap m : categoryThiredList){
				if(m.get("parent_code").equals(parent_code)){
					categoryList.add(this.getCategory(m));
				}
			}
		}else if(level == 2){
			//取出二级分类
			List<MDataMap> categorySecondList = DbUp.upTable("uc_sellercategory").query("", "","LENGTH(category_code)='12' and seller_code=:seller_code and flaginable = :flaginable ",urMapParam, -1, -1);
			for(MDataMap m : categorySecondList){
					if(m.get("parent_code").equals(parent_code)){
						categoryList.add(this.getCategory(m));
				}
			}
		}else  if(level == 1){
			List<MDataMap> categoryFirstList = DbUp.upTable("uc_sellercategory").query("", "","LENGTH(category_code)='8' and seller_code=:seller_code and flaginable = :flaginable ",urMapParam, -1, -1);
			if(parent_code.equals("")){
				for(MDataMap m : categoryFirstList){
					categoryList.add(this.getCategory(m));
				}
			}else{
				for(MDataMap m : categoryFirstList){
					if(m.get("parent_code").equals(parent_code)){
						categoryList.add(this.getCategory(m));
					}
				}
			}
		}
		return categoryList;
	}
	/**
	 * 取出商家的分类
	 * @param level			分类的级别 1,2,3
	 * @param parent_code	父code
	 * @return
	 */
	public List<Category> getCategoryListForCm(int level,String parent_code)
	{
		List<Category> categoryList = new ArrayList<Category>();
		MDataMap urMapParam = new MDataMap();
		if(level == 3){
			List<MDataMap> categoryThiredList = DbUp.upTable("pc_categoryinfo").query("", "", "LENGTH(category_code)='20'",urMapParam, -1, -1);
			for(MDataMap m : categoryThiredList){
				if(m.get("parent_code").equals(parent_code)){
					categoryList.add(this.getCategory(m));
				}
			}
		}else if(level == 2){
			urMapParam = new MDataMap();
			//取出二级分类
			List<MDataMap> categorySecondList = DbUp.upTable("pc_categoryinfo").query("", "","LENGTH(category_code)='16'",urMapParam, -1, -1);
			for(MDataMap m : categorySecondList){
					if(m.get("parent_code").equals(parent_code)){
						categoryList.add(this.getCategory(m));
				}
			}
		}else  if(level == 1){
			urMapParam = new MDataMap();
			List<MDataMap> categoryFirstList = DbUp.upTable("pc_categoryinfo").query("", "","LENGTH(category_code)='12'",urMapParam, -1, -1);
			if(parent_code.equals("")){
				for(MDataMap m : categoryFirstList){
					categoryList.add(this.getCategory(m));
				}
			}else{
				for(MDataMap m : categoryFirstList){
					if(m.get("parent_code").equals(parent_code)){
						categoryList.add(this.getCategory(m));
					}
				}
			}
		}
		return categoryList;
	}
	private Category getCategory(MDataMap m){
		Category c = new Category();
		c = new Category();
		c.setUid(m.get("uid"));
		c.setId(m.get("zid"));
		c.setCategoryCode(m.get("category_code"));
		c.setCategoryName(m.get("category_name"));
		c.setParentCode(m.get("parent_code"));
		c.setSort(m.get("sort"));
		return c;
	}
	
	/**
	 * 判断某商品是否在某个店铺分类下
	 * @param sellerCode 店铺编号
	 * @param productCode 商品编号
	 * @param categoryName 分类名称
	 */
	public boolean productInCategory(String sellerCode,String productCode,String categoryName){
		boolean flag = false;
		if(StringUtil.isBlank(sellerCode)||StringUtil.isBlank(productCode)||StringUtil.isBlank(categoryName)){
			return flag;
		}
		MDataMap md = DbUp.upTable("uc_sellercategory").one("seller_code",sellerCode,"category_name",categoryName);
		if(md!=null&&!md.isEmpty()){
			MDataMap mm = DbUp.upTable("uc_sellercategory_product_relation").oneWhere("", "", " category_code like '"+md.get("category_code")+"%' and seller_code=:seller_code and product_code=:product_code", "seller_code",sellerCode,"product_code",productCode);
			if(mm!=null&&!mm.isEmpty()){
				flag = true;			
			}
		}
		return flag;
	}
	
	/** 
	* @Description:获取分类名称
	* @param categoryCodeArr 分类编号
	* @param sellerCode 应用编号
	* @author 张海生
	* @date 2015-5-21 下午2:31:16
	* @return MDataMap 
	* @throws 
	*/
	public MDataMap getCategoryName(List<String> categoryCodeArr, String sellerCode){
		MDataMap resultMap = new MDataMap();
		if (null == categoryCodeArr || categoryCodeArr.size() < 1) {
			return resultMap;
		}
		String sFields = "category_code,category_name";
		String sWhere = " seller_code='"+sellerCode+"' and category_code in ('"+StringUtils.join(categoryCodeArr,"','")+"')";
		List<MDataMap> categoryNameMapList = DbUp.upTable("uc_sellercategory").queryAll(sFields, "", sWhere,null);
		for (MDataMap categoryNameMap : categoryNameMapList) {
			String categoryCode = categoryNameMap.get("category_code");
			String categoryName = categoryNameMap.get("category_name");
			resultMap.put(categoryCode, categoryName);
		}
		return resultMap;
	}/** 
	* @Description:获取传入的分类以及其父类的基本信息
	* @param categoryCodeArr 分类编号
	* @param sellerCode 应用编号
	* @author ligj
	* @date 2016-01-21
	* @return List<CategoryBaseInfo> 
	* @throws 
	*/
	public List<CategoryBaseInfo> getCategoryBaseInfoList(List<String> categoryCodeArr, String sellerCode){
		List<CategoryBaseInfo> result = new ArrayList<CategoryBaseInfo>();
		if (null == categoryCodeArr || categoryCodeArr.size() < 1 || StringUtils.isBlank(sellerCode)) {
			return result;
		}
		Map<String,Integer> allCategoryMap = new HashMap<String, Integer>();
		for (String categoryCode : categoryCodeArr) {
			if (categoryCode.length() == 8) {
				allCategoryMap.put(categoryCode,1);		//一级
			}else if (categoryCode.length() == 12) {
				allCategoryMap.put(categoryCode,1);
				allCategoryMap.put(categoryCode.substring(0, 8),1);
			}else if (categoryCode.length() == 16) {
				allCategoryMap.put(categoryCode,1);
				allCategoryMap.put(categoryCode.substring(0, 12),1);
				allCategoryMap.put(categoryCode.substring(0, 8),1);
			}else  if(categoryCode.length() == 20) {
				allCategoryMap.put(categoryCode,1);
				allCategoryMap.put(categoryCode.substring(0, 16),1);
				allCategoryMap.put(categoryCode.substring(0, 12),1);
				allCategoryMap.put(categoryCode.substring(0, 8),1);
			}
		}
		String sFields = "category_code,category_name,parent_code";
		String sWhere = " seller_code='"+sellerCode+"' and category_code in ('"+StringUtils.join(allCategoryMap.keySet(),"','")+"')";
		List<MDataMap> categoryNameMapList = DbUp.upTable("uc_sellercategory").queryAll(sFields, "", sWhere,null);
		for (MDataMap categoryNameMap : categoryNameMapList) {
			CategoryBaseInfo categoryInfo = new CategoryBaseInfo();
			categoryInfo.setCategoryCode(categoryNameMap.get("category_code"));
			categoryInfo.setCategoryName(categoryNameMap.get("category_name"));
			categoryInfo.setParentCode(categoryNameMap.get("parent_code"));
			result.add(categoryInfo);
		}
		return result;
	}
	
	// 根据商户编号查询商户有效的资质品类
	public List<PcSellerQualification> getQualificationCategoryForMerchant(String sellerCode) {
		PcSellerQualification psq = null;
		List<PcSellerQualification> sellerQualificationList = new ArrayList<PcSellerQualification>();
		String sSql = "SELECT sd.define_code seller_qualification_code, sd.define_name qualification_name FROM systemcenter.sc_define sd " + 
				" WHERE sd.define_code IN ( SELECT DISTINCT(psq.category_code) FROM productcenter.pc_seller_qualification psq " + 
				" WHERE psq.small_seller_code = '"+ sellerCode +"' AND psq.end_time > NOW() )";
		List<Map<String, Object>> qualificationLsit = DbUp.upTable("pc_seller_qualification").dataSqlList(sSql, new MDataMap());
		for(Map<String, Object> m : qualificationLsit){
			psq = new PcSellerQualification();
			psq.setSellerQualificationCode((String) m.get("seller_qualification_code"));
			psq.setQualificationName((String) m.get("qualification_name"));
			sellerQualificationList.add(psq);
		}
		return sellerQualificationList;
	}
	
	//查询商户所有资质品类
	public List<PcSellerQualification> getAllQualificationCategory() {
		PcSellerQualification psq = null;
		List<PcSellerQualification> sellerQualificationList = new ArrayList<PcSellerQualification>();
		String sSql = "SELECT sd.define_code seller_qualification_code,sd.define_name qualification_name " + 
				" FROM systemcenter.sc_define sd WHERE sd.parent_code = '449747160031'";
		List<Map<String, Object>> qualificationLsit = DbUp.upTable("sc_define").dataSqlList(sSql, new MDataMap());
		for(Map<String, Object> m : qualificationLsit){
			psq = new PcSellerQualification();
			psq.setSellerQualificationCode((String) m.get("seller_qualification_code"));
			psq.setQualificationName((String) m.get("qualification_name"));
			sellerQualificationList.add(psq);
		}
		return sellerQualificationList;
	}
}
