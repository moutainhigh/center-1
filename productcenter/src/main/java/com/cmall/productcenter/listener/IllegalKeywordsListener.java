package com.cmall.productcenter.listener;

import java.util.List;

import com.cmall.systemcenter.service.ScIllegalKeywordsServices;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapzero.root.RootJmsListenser;

public class IllegalKeywordsListener extends RootJmsListenser{

	public boolean onReceiveText(String sMessage, MDataMap mPropMap) {
		boolean ret = true;
		
		if(sMessage!=null){
			MDataMap prodcutData = DbUp.upTable("pc_productinfo")
					.one("product_code", sMessage);
			
			if(prodcutData == null){
				
				return ret;
				
			}else{
				
				ScIllegalKeywordsServices sks = new ScIllegalKeywordsServices();
				String productName = sks.getLegalKeyWords(prodcutData.get("product_name"));
				MDataMap updateDataMap = new MDataMap();
				updateDataMap.put("product_name", productName);
				updateDataMap.put("product_code", sMessage);
				DbUp.upTable("pc_productinfo").dataUpdate(updateDataMap, "product_name", "product_code");
				
				MDataMap descriptionData = DbUp.upTable("pc_productdescription")
						.one("product_code", sMessage);
				
				if(descriptionData == null)
					return ret;
				else{
					String productDescription = sks.getLegalKeyWords(descriptionData.get("description_info"));
					updateDataMap = new MDataMap();
					updateDataMap.put("product_code", sMessage);
					updateDataMap.put("description_info", productDescription);
					DbUp.upTable("pc_productdescription").dataUpdate(updateDataMap, "description_info", "product_code");
				}
			}
		}
		
		return ret;
	}

}
