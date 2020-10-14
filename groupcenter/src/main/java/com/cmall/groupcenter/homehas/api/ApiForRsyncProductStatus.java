package com.cmall.groupcenter.homehas.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapzero.root.RootJmsListenser;

/**
 * 监听同步商品状态
 * @author ligj
 * 2015/02/04 13:23:00   
 * 
 */
public class ApiForRsyncProductStatus extends RootJmsListenser{
		public boolean onReceiveText(String sMessage, MDataMap mPropMap) {
			boolean retb = true;
			try{
				if (StringUtils.isNotEmpty(sMessage)) {
					List<String> productCodes = new ArrayList<String>();
					String[] productCodeArr = sMessage.split(",");
					for (String productCode : productCodeArr) {
						productCodes.add(productCode);
					}
					ProductService ps = new ProductService();
					ps.rsyncProductStatus(productCodes);
					
				}
			}catch(Exception e){
				retb=false;
				e.printStackTrace();
			}
			return retb;
		}
}

