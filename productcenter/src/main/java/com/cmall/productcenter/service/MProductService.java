package com.cmall.productcenter.service;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.MProduct;
import com.cmall.productcenter.model.MProductProperty;
import com.cmall.productcenter.model.PcProductproperty;
import com.cmall.productcenter.txservice.TxProductService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;

/**   
*    
* 项目名称：productcenter   
* 类名称：MProductService   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2014-4-10 上午10:27:05   
* 修改人：yanzj
* 修改时间：2014-4-10 上午10:27:05   
* 修改备注：   
* @version    
*    
*/
public class MProductService  extends BaseClass {
	
	/**
	 * 
	 * @param productCode
	 * @return
	 */
	public List<MProduct> GetMProductList(String productCode){
		
		if(productCode == null || productCode.equals(""))
			return null;
		
		List<MProduct> ret = new ArrayList<MProduct>();
	
		
		// 取得商品属性信息
		MDataMap pcProductpropertyListMapParam = new MDataMap();
		pcProductpropertyListMapParam.put("product_code", productCode);
		
		List<MDataMap> pcProductpropertyListMap = DbUp.upTable(
				"pc_productproperty").query("", "property_type,small_sort desc",
				"product_code=:product_code and (property_type='449736200001' or property_type='449736200002')",
				pcProductpropertyListMapParam, -1, -1);
		
		List<MProductProperty> ppList = new ArrayList<MProductProperty>();
		MDataMap md = new MDataMap();
		int size = pcProductpropertyListMap.size();
		if (pcProductpropertyListMap != null) {
			for (int i = 0; i < size; i++) {
				
				if(!md.containsKey(pcProductpropertyListMap.get(i).get("property_keycode"))){
					md.put(pcProductpropertyListMap.get(i).get("property_keycode"), pcProductpropertyListMap.get(i).get("property_type"));
				}
			}
		}
		
		List<String> list = md.upKeys();
		for(int i=0;i<list.size();i++){
			if(md.get(list.get(i)).equals("449736200001")){
				
				MProduct mm = new MProduct();
				for (int j = 0; j < size; j++) {
					
					if(list.get(i).equals(pcProductpropertyListMap.get(j).get("property_keycode")))
					{
						mm.setPropertyKeycode(pcProductpropertyListMap.get(j).get("property_keycode"));
						mm.setProductCode(pcProductpropertyListMap.get(j).get("product_code"));
						mm.setBigSortName(pcProductpropertyListMap.get(j).get("property_key"));
						
						MProductProperty mp = new MProductProperty();
						mp.setProductCode(pcProductpropertyListMap.get(j).get("product_code"));
						mp.setPropertyCode(pcProductpropertyListMap.get(j).get("property_code"));
						mp.setPropertyKey(pcProductpropertyListMap.get(j).get("property_key"));
						mp.setPropertyKeycode(pcProductpropertyListMap.get(j).get("property_keycode"));
						mp.setPropertyType(pcProductpropertyListMap.get(j).get("property_type"));
						mp.setPropertyValue(pcProductpropertyListMap.get(j).get("property_value"));
						mm.getmProductPropertyList().add(mp);
					}
				}
				ret.add(mm);
			}
		}
		
		for(int i=0;i<list.size();i++){
			if(md.get(list.get(i)).equals("449736200002")){
				
				MProduct mm = new MProduct();
				for (int j = 0; j < size; j++) {
					
					if(list.get(i).equals(pcProductpropertyListMap.get(j).get("property_keycode")))
					{
						mm.setPropertyKeycode(pcProductpropertyListMap.get(j).get("property_keycode"));
						mm.setProductCode(pcProductpropertyListMap.get(j).get("product_code"));
						mm.setBigSortName(pcProductpropertyListMap.get(j).get("property_key"));
						
						MProductProperty mp = new MProductProperty();
						mp.setProductCode(pcProductpropertyListMap.get(j).get("product_code"));
						mp.setPropertyCode(pcProductpropertyListMap.get(j).get("property_code"));
						mp.setPropertyKey(pcProductpropertyListMap.get(j).get("property_key"));
						mp.setPropertyKeycode(pcProductpropertyListMap.get(j).get("property_keycode"));
						mp.setPropertyType(pcProductpropertyListMap.get(j).get("property_type"));
						mp.setPropertyValue(pcProductpropertyListMap.get(j).get("property_value"));
						mm.getmProductPropertyList().add(mp);
					}
				}
				
				ret.add(mm);
			}
		}
		
		return ret;
	}
	
	
	public RootResult SaveProductPropertySort(MProduct p){
		
		RootResult ret =new RootResult();
		
		
		if(p == null || p.getmProductPropertyList() == null || p.getmProductPropertyList().size() ==0){
			//没有要保存的内容!
			ret.setResultCode(941901069);
			ret.setResultMessage(bInfo(941901069));
			return ret;
		}else{
			TxProductService txs = BeansHelper.upBean("bean_com_cmall_productcenter_txservice_TxProductService");

			try {
				txs.updateProductProperty(p.getmProductPropertyList());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ret.setResultCode(941901049);
				ret.setResultMessage(bInfo(941901049, e.getMessage()));
			}
		}
		
		try{
			ProductService ps = new ProductService();
			ps.genarateJmsStaticPageForProductCode(p.getProductCode());
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		return ret;
	}
	

}
