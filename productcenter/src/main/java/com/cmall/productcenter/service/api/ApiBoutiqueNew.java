package com.cmall.productcenter.service.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.Boutique;
import com.cmall.productcenter.model.BoutiqueNewInput;
import com.cmall.productcenter.model.BoutiqueResultNew;
import com.cmall.productcenter.model.PcCategoryinfo;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

public class ApiBoutiqueNew extends
		RootApi<BoutiqueResultNew, BoutiqueNewInput> {
	public BoutiqueResultNew Process(BoutiqueNewInput inputParam,
			MDataMap mRequestMap) {
		BoutiqueResultNew result = new BoutiqueResultNew();
		String nowTime = FormatHelper.upDateTime();
		Boutique boutique = new Boutique();
		PcProductinfo pcProductinfo = new PcProductinfo();
		List<Boutique> lisBoutiques = new ArrayList<Boutique>();
		List<PcCategoryinfo> listCate = new ArrayList<PcCategoryinfo>();
		List<PcProductinfo> list2 = new ArrayList<PcProductinfo>();
		String sql = "select * from oc_boutique_market where  start_time < '"
				+ nowTime + "' and end_time >'" + nowTime
				+ "' order by start_time desc limit 1";
		List<Map<String, Object>> list = null;
		try {
			list = DbUp.upTable("oc_boutique_market").dataSqlList(sql,
					new MDataMap());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			result.setResultCode(941901037);
			result.setResultMessage(bInfo(941901037));
			return result;
		}
		for (Map<String, Object> mp1 : list) {
			boutique = new SerializeSupport<Boutique>().serialize(new MDataMap(
					mp1), new Boutique());
			lisBoutiques.add(boutique);
		}
		result.setList(lisBoutiques);
		if (lisBoutiques.size() > 0) {
			String boutCode = lisBoutiques.get(0).getBoutique_code();
			
			MDataMap mpp1 = new MDataMap();
			mpp1.put("boutique_code", boutCode);
			List<MDataMap> mp = DbUp.upTable("oc_boutique_product_rela").queryAll("product_code", "","", mpp1);
			
			
			String sq2 = "select * from pc_productinfo where   "
					+ getInSqlForMap(mp);
			if (StringUtils.isNotBlank(inputParam.getStartPrice())) {
				sq2 = sq2 + " and market_price >= "
						+ inputParam.getStartPrice();
			}
			if (StringUtils.isNotBlank(inputParam.getEndPrice())) {
				sq2 = sq2 + " and market_price <= " + inputParam.getEndPrice();
			}
			if (StringUtils.isNotBlank(inputParam.getCategoryCode())) {
				
				MDataMap dataMap = new MDataMap();
				dataMap.put("category_code", inputParam.getCategoryCode());
				List<MDataMap> lst = DbUp.upTable("pc_productcategory_rel").queryAll("product_code", "", "", dataMap);
				sq2 = sq2 + "and " + getInSqlForMap(lst);
			}
			if(StringUtils.isNotBlank(inputParam.getPrice()))
			{
				sq2 = sq2 +" order by min_sell_price desc";
			}
			else
			{
				sq2 = sq2 +" order by min_sell_price asc";
			}
			List<Map<String, Object>> lst = null;
			try {
				lst = DbUp.upTable("pc_productinfo")
						.dataSqlList(sq2, new MDataMap());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result.setResultCode(941901047);
				result.setResultMessage(bInfo(941901047));
				return result;
			}
			for (Map<String, Object> mp1 : lst) {
				pcProductinfo = new SerializeSupport<PcProductinfo>()
						.serialize(new MDataMap(mp1), new PcProductinfo());
				MDataMap map = DbUp.upTable("pc_productcategory_rel").one(
						"product_code", pcProductinfo.getProductCode());
				MDataMap mp2 = DbUp.upTable("pc_categoryinfo").one(
						"category_code", map.get("category_code"));
				PcCategoryinfo category = new SerializeSupport<PcCategoryinfo>()
						.serialize(mp2, new PcCategoryinfo());
				pcProductinfo.setCategory(category);
				listCate.add(category);
				list2.add(pcProductinfo);
			}
			result.setList2(setSkuList(list2));
			result.setList3(distinctList(listCate));
		}
		return result;
	}
	/**
	 * getInSqlForMap:(拼接sql). <br/>
	 * @author hxd
	 * @param list
	 * @return
	 * @since JDK 1.6
	 */
	private String getInSqlForMap(List<MDataMap> list) {
		String sql = "";
		String strField="product_code";
		if (!list.isEmpty()) 
		{
			for (MDataMap m : list)   
			{
				String code =  m.get(strField);
				if(StringUtils.isBlank(sql))
				{
					sql =  "("+strField + "='" +code+"'";
				}
				else
				{
					sql +=" or " +strField+ "='"+code+"'";
				}
			}
			sql =sql+")";
		}
		else
		{
			sql =strField+ " =''";
		}
		return sql;
	}

	
	/**
	 * 分类去重
	 */
	private List<PcCategoryinfo> distinctList(List<PcCategoryinfo> list)
	{
		 for ( int i = 0 ; i < list.size() - 1 ; i ++ ) {
		     for ( int j = list.size() - 1 ; j > i; j -- ) {
		       if (list.get(j).getCategoryCode().equals(list.get(i).getCategoryCode())) {
		         list.remove(j);
		       }
		      }
		    } 
		return list;
	}
	
/**
 * @param ls
 * @return
 */
	private List<PcProductinfo> setSkuList(List<PcProductinfo>  ls)
	{
		ProductSkuInfo pcSkuinfo = new ProductSkuInfo();
		
		for(int i=0 ;i<ls.size();i++)
		{
			List<ProductSkuInfo> skuList = new ArrayList<ProductSkuInfo>();
			
			MDataMap mDataMap=new MDataMap();
			mDataMap.put("product_code", ls.get(i).getProductCode());
			
			List<MDataMap>  listCode = DbUp.upTable("pc_skuinfo")
					.queryAll("", "", "", mDataMap);
			for(MDataMap lst :listCode)
			{
				pcSkuinfo = new SerializeSupport<ProductSkuInfo>().serialize(lst, new ProductSkuInfo());
				skuList.add(pcSkuinfo);
			}
			ls.get(i).setProductSkuInfoList(skuList);
		}
		return ls;
	}
}
