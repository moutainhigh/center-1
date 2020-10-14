package com.cmall.ordercenter.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.common.OrderConst;
import com.cmall.ordercenter.model.ActivityCategoryResult;
import com.cmall.ordercenter.model.ActivitySkuEntity;
import com.cmall.ordercenter.model.OcActivity;
import com.cmall.ordercenter.model.OcActivityProductRel;
import com.cmall.ordercenter.model.OcActivitySellercategoryRel;
import com.cmall.ordercenter.model.SkuForCache;
import com.cmall.ordercenter.model.VpcProductsku;
import com.cmall.ordercenter.service.cache.ProductCacheManage;
import com.cmall.productcenter.model.PcProductInfoForI;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;

/**   
*    
* 项目名称：ordercenter   
* 类名称：ActivityService   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-10-23 上午9:01:43   
* 修改人：yanzj
* 修改时间：2013-10-23 上午9:01:43   
* 修改备注：   
* @version    
*    
*/
public class ActivityService extends BaseClass {
	
	
	
	public void autoRefreshCacheFroXSXL(){
		List<OcActivity> retList = this.getActivityListForXSXL();
		
		ProductJmsSupport pjs = new ProductJmsSupport();
		for(OcActivity oa : retList){
			List<OcActivityProductRel> list = oa.getProductList();
			if(list!=null){
				for(OcActivityProductRel oapr:list){
					pjs.onChangeForSkuChangePrice(oapr.getSkuCode());
				}
			}
		}
	}
	
	
	/**
	 * 取得所有需要缓存的数据
	 * @return
	 */
	public List<SkuForCache> getSkuForCacheList(){
		List<SkuForCache> retlist =  new ArrayList<SkuForCache>();
		ProductService ps = new ProductService();
		List<ProductSkuInfo> list = ps.getAllProductForCache();
		
		
		List<OcActivity> listActivisty = this.getActivityListForXSXL();
		
		for(ProductSkuInfo psi : list){
			SkuForCache sfc = new SkuForCache();
			
			sfc.setPsi(psi);
			
			ActivitySkuEntity ase = this.getMinSkuPrice(psi, listActivisty);
			
			if(ase == null || ase.getActivity_code().equals("")){
				
			}else{
				sfc.setAse(ase);
			}
		}
		
		return retlist;
	}
	
	/**
	 * 取得单个需要缓存的数据
	 * @param skuCode
	 * @return
	 */
	public SkuForCache getSkuForCache(String skuCode){
		
		SkuForCache sfc = new SkuForCache();
		
		ProductService ps = new ProductService();
		
		List<ProductSkuInfo> listSku= ps.getSkuListForI(skuCode);
		
		if(listSku == null || listSku.size() == 0){
			
		}
		else{
			sfc.setPsi(listSku.get(0));
			
			List<OcActivity> listActivisty = this.getActivityListForXSXLOther(skuCode);
			
			ActivitySkuEntity ase = this.getMinSkuPrice(listSku.get(0), listActivisty);
			
			if(ase == null || ase.getActivity_code().equals("")){
				
			}else{
				sfc.getPsi().setSellPrice(ase.getSell_price());
				sfc.setAse(ase);
			}
		}
		
		return sfc;
		
	}
	
	/**
	 * 取满减活动，影响的商品
	 * @param oa
	 * @return
	 */
	public List<PcProductInfoForI> getProductsByActivityCodeForMj(OcActivity oa){
		
		List<PcProductInfoForI> ret = new ArrayList<PcProductInfoForI>();
		
		if(oa.getCategoryList() == null || oa.getCategoryList().size() == 0)
			return ret;
		else
		{
			String whereStr = "";
			MDataMap urMapParam = new MDataMap();
			int i = 0;
			for(OcActivitySellercategoryRel osr: oa.getCategoryList()){
				
				urMapParam.put("category_code" + i, osr.getCategoryCode());
				whereStr += " category_code=:category_code" + i + " or";
				i++;
			}
			
			if (whereStr.length() > 2)
			{
				whereStr = whereStr.substring(0, whereStr.length() - 2);
				whereStr=" ("+whereStr+") and seller_code=:seller_code ";
			}
			
			urMapParam.put("seller_code", oa.getSellerCode());
		
			List<MDataMap> listMap = DbUp.upTable("uc_sellercategory_product_relation")
					.query(" distinct product_code ", "", whereStr, urMapParam, -1, -1);
			
			if(listMap != null)
			{
				for(MDataMap mm :listMap)
				{
					PcProductInfoForI ppi = new PcProductInfoForI();
					ppi.setProductCode(mm.get("product_code"));
					ret.add(ppi);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * 根据当前的限时限量活动，计算当前商品的最低价
	 * @param sku 产品
	 * @param list 限时限量的活动的List
	 * @return
	 */
	public ActivitySkuEntity getMinSkuPrice(ProductSkuInfo sku,List<OcActivity> list){
		
		if(sku == null)
		{
			return null;
		}
		ActivitySkuEntity ret = new ActivitySkuEntity();
		
		if(list == null  || list.size()==0)
			return ret;
		else{
			
			BigDecimal minPrice = sku.getSellPrice();
			
			for(OcActivity oa : list)
			{
				
				if(!oa.getSellerCode().equals(sku.getSellerCode()))
					continue;
				
				if(oa.getActivityPriceType().equals(OrderConst.ZJPRICEACTIVITY)){//直降
					
					for(OcActivityProductRel opr: oa.getProductList())
					{
						if(opr.getSkuCode().equals(sku.getSkuCode()))
						{
							if(opr.getSellPrice().compareTo(minPrice)==-1)
							{
								minPrice = opr.getSellPrice();
								
								ret.setActivity_code(oa.getActivityCode());
								ret.setBegin_time(oa.getBeginTime());
								ret.setEnd_time(oa.getEndTime());
								ret.setSell_price(minPrice);
								ret.setSellerCode(oa.getSellerCode());
							}
						}
					}
				}
				else if(oa.getActivityPriceType().equals(OrderConst.ZJJPRICEACTIVITY)){//直减
					for(OcActivityProductRel opr: oa.getProductList())
					{
						if(opr.getSkuCode().equals(sku.getSkuCode()))
						{
							if((sku.getSellPrice().subtract(oa.getSkuSubprice()).doubleValue()) > 0){
								if((sku.getSellPrice().subtract(oa.getSkuSubprice())).compareTo(minPrice)==-1)
								{
									minPrice =sku.getSellPrice().subtract(oa.getSkuSubprice());
									
									minPrice=minPrice.setScale(2,BigDecimal.ROUND_HALF_UP);
									
									ret.setActivity_code(oa.getActivityCode());
									ret.setBegin_time(oa.getBeginTime());
									ret.setEnd_time(oa.getEndTime());
									ret.setSell_price(minPrice);
									ret.setSellerCode(oa.getSellerCode());
								}
							}
						}
					}
				}else if(oa.getActivityPriceType().equals(OrderConst.PRICEPERCENTACTIVITY)){//百分比
					for(OcActivityProductRel opr: oa.getProductList())
					{
						if(opr.getSkuCode().equals(sku.getSkuCode()))
						{
							if((sku.getSellPrice().multiply(oa.getSkuPricepercent())).compareTo(minPrice)==-1)
							{
								minPrice =sku.getSellPrice().multiply(oa.getSkuPricepercent());
								
								ret.setActivity_code(oa.getActivityCode());
								ret.setBegin_time(oa.getBeginTime());
								ret.setEnd_time(oa.getEndTime());
								ret.setSell_price(minPrice);
								ret.setSellerCode(oa.getSellerCode());
							}
						}
					}
				}
			}
		}
		
		if(ret.getActivity_code().equals(""))
			return null;
		else{
			
			BigDecimal b = ret.getSell_price(); 
			ret.setSell_price(b.setScale(2, BigDecimal.ROUND_HALF_UP));
			
			return ret;
		}
	}
	
	
	
	/**
	 * 取得限时限量的所有有效的活动
	 * @return
	 */
	public List<OcActivity> getActivityListForXSXL(){
		
		List<OcActivity> retList = new ArrayList<OcActivity>();
		
		MDataMap mapParam = new MDataMap();
		mapParam.put("activity_type", OrderConst.XSXLACTIVITY);
	
		
		List<MDataMap> listMap = DbUp.upTable("oc_activity")
				.query("", "", "begin_time<=CONCAT(current_timestamp,'') and end_time>=CONCAT(current_timestamp,'') and flag=1 and activity_type=:activity_type", mapParam, -1, -1);
		

		if(listMap!=null){
			
			int size = listMap.size();
			SerializeSupport ss = new SerializeSupport<OcActivity>();
			SerializeSupport sProduct = new SerializeSupport<OcActivityProductRel>();
			
			for(int j=0;j<size;j++)
			{
				OcActivity pic = new OcActivity();
				ss.serialize(listMap.get(j), pic);
				
				//如果是限时限量，取商品子数据，
				if(pic.getActivityType().equals(OrderConst.XSXLACTIVITY)){
					MDataMap productMapParam = new MDataMap();
					productMapParam.put("activity_code", pic.getActivityCode());
					List<MDataMap> productListMap = DbUp.upTable("oc_activity_product_rel")
							.query("", "", "activity_code=:activity_code", productMapParam, -1, -1);
					
					
					if(productListMap!=null)
					{
						int psize = productListMap.size();
						for(int k=0;k<psize;k++)
						{
							OcActivityProductRel  oap= new OcActivityProductRel();
							sProduct.serialize(productListMap.get(k), oap);
							pic.getProductList().add(oap);
						}
					}
					
					String endTime = pic.getEndTime();
					
					SimpleDateFormat simpleDateFormat =new SimpleDateFormat(DateUtil.DATE_FORMAT_DATETIME);
					Date date;
					try {
						date = simpleDateFormat.parse(endTime);
						long timeStemp = date.getTime();
						pic.setRemainingTime(date.getTime()-System.currentTimeMillis());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
				retList.add(pic);
			}
		}
		
		return retList;
		
	}
	
	

	/**
	 * 取得限时限量的单个sku的所有活动
	 * @return
	 */
	public List<OcActivity> getActivityListForXSXLOther(String skuCode){
		
		List<OcActivity> retList = new ArrayList<OcActivity>();
		
		MDataMap mapParam = new MDataMap();
		mapParam.put("activity_type", OrderConst.XSXLACTIVITY);
		mapParam.put("sku_code", skuCode);
	
		
		List<MDataMap> listMap = DbUp.upTable("oc_activity")
				.query("", "", "begin_time<=CONCAT(current_timestamp,'') and end_time>=CONCAT(current_timestamp,'') and flag=1 and activity_type=:activity_type and activity_code in (SELECT activity_code FROM ordercenter.oc_activity_product_rel WHERE sku_code=:sku_code)", mapParam, -1, -1);
		

		if(listMap!=null){
			
			int size = listMap.size();
			SerializeSupport ss = new SerializeSupport<OcActivity>();
			SerializeSupport sProduct = new SerializeSupport<OcActivityProductRel>();
			
			for(int j=0;j<size;j++)
			{
				OcActivity pic = new OcActivity();
				ss.serialize(listMap.get(j), pic);
				
				//如果是限时限量，取商品子数据，
				if(pic.getActivityType().equals(OrderConst.XSXLACTIVITY)){
					MDataMap productMapParam = new MDataMap();
					productMapParam.put("activity_code", pic.getActivityCode());
					productMapParam.put("sku_code", skuCode);
					List<MDataMap> productListMap = DbUp.upTable("oc_activity_product_rel")
							.query("", "", "activity_code=:activity_code and sku_code=:sku_code", productMapParam, -1, -1);
					
					
					if(productListMap!=null)
					{
						int psize = productListMap.size();
						for(int k=0;k<psize;k++)
						{
							OcActivityProductRel  oap= new OcActivityProductRel();
							sProduct.serialize(productListMap.get(k), oap);
							pic.getProductList().add(oap);
						}
					}
					
					String endTime = pic.getEndTime();
					
					SimpleDateFormat simpleDateFormat =new SimpleDateFormat(DateUtil.DATE_FORMAT_DATETIME);
					Date date;
					try {
						date = simpleDateFormat.parse(endTime);
						long timeStemp = date.getTime();
						pic.setRemainingTime(date.getTime()-System.currentTimeMillis());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
				retList.add(pic);
			}
		}
		
		return retList;
		
	}
	
	
	public List<OcActivity> getActivityListForSellers(String sellerCodes){
		List<OcActivity> retList = new ArrayList<OcActivity>();
		
		if(sellerCodes == null || sellerCodes.equals(""))
			return retList;
		
		String[] sellerCodeAry = sellerCodes.split(",");
		MDataMap urMapParam = new MDataMap();
		String whereStr = "";
		int i=0;
		for (String dm : sellerCodeAry) {
			urMapParam.put("seller_code" + i, dm);
			whereStr += " seller_code=:seller_code" + i + " or";
			i++;
		}

		if (whereStr.length() > 2){
			whereStr = whereStr.substring(0, whereStr.length() - 2);
			whereStr="("+whereStr+") and begin_time<=CONCAT(current_timestamp,'') and end_time>=CONCAT(current_timestamp,'') and flag=1 ";
		}
		else{
			return retList;
		}
		
		List<MDataMap> listMap = DbUp.upTable("oc_activity")
				.query("", "", whereStr, urMapParam, -1, -1);
		
		if(listMap!=null){
			
			int size = listMap.size();
			SerializeSupport ss = new SerializeSupport<OcActivity>();
			SerializeSupport sProduct = new SerializeSupport<OcActivityProductRel>();
			SerializeSupport sCategory = new SerializeSupport<OcActivitySellercategoryRel>();
			
			for(int j=0;j<size;j++)
			{
				OcActivity pic = new OcActivity();
				ss.serialize(listMap.get(j), pic);
				
				//如果是非限时限量，取商品子数据，
				if(!pic.getActivityType().equals(OrderConst.XSXLACTIVITY.toString())){
					retList.add(pic);
				}
			}
		}
		
		return retList ;
		
	}
	
	
	/**
	 * 根据卖家Code，取得当前卖家的所有有效的活动
	 * @param sellerCode
	 * @return
	 */
	public List<OcActivity> getActivityList(String sellerCodes){
		List<OcActivity> retList = new ArrayList<OcActivity>();
		

		if(sellerCodes == null || sellerCodes.equals(""))
			return retList;
		
		String[] sellerCodeAry = sellerCodes.split(",");
		MDataMap urMapParam = new MDataMap();
		String whereStr = "";
		int i=0;
		for (String dm : sellerCodeAry) {
			urMapParam.put("seller_code" + i, dm);
			whereStr += " seller_code=:seller_code" + i + " or";
			i++;
		}

		if (whereStr.length() > 2){
			whereStr = whereStr.substring(0, whereStr.length() - 2);
			whereStr="("+whereStr+") and begin_time<=CONCAT(current_timestamp,'') and end_time>=CONCAT(current_timestamp,'') and flag=1 ";
		}
		else{
			return retList;
		}
		
	
		
		List<MDataMap> listMap = DbUp.upTable("oc_activity")
				.query("", "", whereStr, urMapParam, -1, -1);
		

		ProductCacheManage psm = new ProductCacheManage();
		
		if(listMap!=null){
			
			int size = listMap.size();
			SerializeSupport ss = new SerializeSupport<OcActivity>();
			SerializeSupport sProduct = new SerializeSupport<OcActivityProductRel>();
			SerializeSupport sCategory = new SerializeSupport<OcActivitySellercategoryRel>();
			
			for(int j=0;j<size;j++)
			{
				OcActivity pic = new OcActivity();
				ss.serialize(listMap.get(j), pic);
				
				//如果是限时限量，取商品子数据，
				if(pic.getActivityType().equals(OrderConst.XSXLACTIVITY.toString())){
					MDataMap productMapParam = new MDataMap();
					productMapParam.put("activity_code", pic.getActivityCode());
					List<MDataMap> productListMap = DbUp.upTable("oc_activity_product_rel")
							.query("", "", "activity_code=:activity_code", productMapParam, -1, -1);
					
					
					if(productListMap!=null)
					{
						int psize = productListMap.size();
						for(int k=0;k<psize;k++)
						{
							OcActivityProductRel  oap= new OcActivityProductRel();
							sProduct.serialize(productListMap.get(k), oap);
							pic.getProductList().add(oap);
							
							List<SkuForCache> listForSFC = psm.getSkuForCacheList(oap.getSkuCode());
							
							
							if(listForSFC == null || listForSFC.size() == 0){
								
							}else{
								SkuForCache  sfc = listForSFC.get(0);
								
								if(sfc!=null && sfc.getPsi()!=null){
									oap.setSellPrice(sfc.getPsi().getSellPrice());
									oap.setSellStock(sfc.getPsi().getStockNum());
								}else{
									//int a =0;
									//System.out.print(a);
								}
							}
						}
					}
					
					String endTime = pic.getEndTime();
					
					SimpleDateFormat simpleDateFormat =new SimpleDateFormat(DateUtil.DATE_FORMAT_DATETIME);
					Date date;
					try {
						date = simpleDateFormat.parse(endTime);
						long timeStemp = date.getTime();
						pic.setRemainingTime(date.getTime()-System.currentTimeMillis());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(pic.getActivityType().equals(OrderConst.MJACTIVITY)){//如果是满减，取店铺分类子数据
					MDataMap productMapParam = new MDataMap();
					productMapParam.put("activity_code", pic.getActivityCode());
					List<MDataMap> productListMap = DbUp.upTable("oc_activity_sellercategory_rel")
							.query("", "", "activity_code=:activity_code", productMapParam, -1, -1);
					
					
					if(productListMap!=null)
					{
						int psize = productListMap.size();
						for(int k=0;k<psize;k++)
						{
							OcActivitySellercategoryRel  oap= new OcActivitySellercategoryRel();
							sCategory.serialize(productListMap.get(k), oap);
							pic.getCategoryList().add(oap);
						}
						//设置对应的商品
						pic.setMjProductList(getProductsByActivityCodeForMj(pic));
					}
				}
					
				retList.add(pic);
			}
		}
		
		return retList;
		
	}
	
	
	/**
	 * 取得满减活动的 分类
	 * @param uid
	 * @return
	 */
	public ActivityCategoryResult getActivityCategoryResult(String uid){
		ActivityCategoryResult ret = new ActivityCategoryResult();
		
		String activity_code = "";
		
		MDataMap mdm = DbUp.upTable("oc_activity").one("uid",uid);
		
		if(mdm == null)
			return ret;
		else{
			String sellerCode = UserFactory.INSTANCE.create().getManageCode();
			
			activity_code = mdm.get("activity_code");

			MDataMap mapParam = new MDataMap();
			mapParam.put("activity_code", activity_code);
			
			List<MDataMap> listMap = DbUp.upTable("oc_activity_sellercategory_rel")
					.query("", "", "activity_code=:activity_code", mapParam, -1, -1);
			
			String idStr="";
			String nameStr = "";
			String whereStr = "";
			MDataMap urMapParam = new MDataMap();
			int i = 0;
			for (MDataMap mm : listMap) {
				idStr+=mm.get("category_code")+",";
				urMapParam.put("category_code" + i, mm.get("category_code"));
				whereStr += " category_code=:category_code" + i + " or";
				i++;
			}

			if (whereStr.length() > 2)
			{
				whereStr = whereStr.substring(0, whereStr.length() - 2);
				idStr = idStr.substring(0, idStr.length() - 1);
				
				
				List<MDataMap> pListMap = DbUp.upTable("uc_sellercategory")
						.query("", "", whereStr, urMapParam, -1, -1);
				
				
				for (MDataMap mm : pListMap) {
					if(mm.get("seller_code").equals(sellerCode))
						nameStr+=mm.get("category_name")+",";
				}

				if (nameStr.length() > 1)
					nameStr = nameStr.substring(0, nameStr.length() - 1);
				
				ret.setIdStr(idStr);
				ret.setNameStr(nameStr);
			}
			
			
			
		}
		
		
		return ret;
	}
	
	
	/**
	 * 取得限时限量的 商品sku
	 * @param uid
	 * @return
	 */
	public List<VpcProductsku> getProductSkuList(String uid){
		
		List<VpcProductsku> list = new ArrayList<VpcProductsku>();
		
		String activity_code = "";
		
		MDataMap mdm = DbUp.upTable("oc_activity").one("uid",uid);
		
		if(mdm == null)
			return list;
		else{
			
			activity_code = mdm.get("activity_code");

			MDataMap mapParam = new MDataMap();
			mapParam.put("activity_code", activity_code);
			
			List<MDataMap> listMap = DbUp.upTable("oc_activity_product_rel")
					.query("", "", "activity_code=:activity_code", mapParam, -1, -1);
			
			
			MDataMap urMapParam = new MDataMap();

			int i = 0;
			String whereStr = "";
			for (MDataMap mm : listMap) {
				urMapParam.put("sku_code" + i, mm.get("sku_code"));
				whereStr += " sku_code=:sku_code" + i + " or";
				i++;
			}

			if (whereStr.length() > 2){
				whereStr = whereStr.substring(0, whereStr.length() - 2);
			
				
				List<MDataMap> pListMap = DbUp.upTable("v_pc_productsku")
						.query("", "", whereStr, urMapParam, -1, -1);
				
				
				if(pListMap!=null){
					int size = pListMap.size();
					SerializeSupport ss = new SerializeSupport<VpcProductsku>();
					for(int j=0;j<size;j++)
					{
						VpcProductsku pic = new VpcProductsku();
						ss.serialize(pListMap.get(j), pic);
						
						for (MDataMap mm : listMap) {
							if(pic.getSkuCode().equals(mm.get("sku_code"))){
								pic.setActivitySellPrice(new BigDecimal(mm.get("sell_price")));
								pic.setActivityStockNum(Integer.parseInt(mm.get("sell_stock")));
							}
						}
						
						
						list.add(pic);
					}
				}
			}
		}
		
		return list;
		
	}

}
