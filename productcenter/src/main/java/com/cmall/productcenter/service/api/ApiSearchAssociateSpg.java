package com.cmall.productcenter.service.api;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cmall.productcenter.model.SearchAssociate;
import com.cmall.productcenter.model.SolrData;
import com.cmall.productcenter.model.api.ApiSearchAssociateInput;
import com.cmall.productcenter.model.api.ApiSearchAssociateResult;
import com.cmall.productcenter.util.Base64Util;
import com.cmall.productcenter.util.SolrQueryUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 联想搜索词及推荐 沙皮狗
 * @author zhouguohui
 *
 */
public class ApiSearchAssociateSpg extends RootApiForManage<ApiSearchAssociateResult,ApiSearchAssociateInput>{

	public ApiSearchAssociateResult Process(ApiSearchAssociateInput inputParam,
			MDataMap mRequestMap) {
		ApiSearchAssociateResult re  = new ApiSearchAssociateResult();
		List<SearchAssociate> listSearchAssociate = new ArrayList<SearchAssociate>();
		SearchAssociate sa = null;
		int num = inputParam.getNum();
		String baseValue= inputParam.getBaseValue();
		if(num<=0){
			num=10;
		}
		
		String keyValue = null;
		/**
		 * 版本控制 3.7.0 以后的数据加密为：base64
		 */
		if(baseValue.equals("base64")){
			keyValue = (null==Base64Util.getFromBASE64(inputParam.getKeyword()))?"": Base64Util.getFromBASE64(inputParam.getKeyword());
		}else{
			keyValue = inputParam.getKeyword();
		}
		
		try {
			/**20150909添加 新版solr5.2.1**/
			if(TopUp.upConfig("productcenter.spgwebclient").equals("yes")){
				
					MDataMap mDataMap = new MDataMap();
					mDataMap.put("keyWord", keyValue);
					mDataMap.put("pageSize", num+"");
					mDataMap.put("lxc", "lxcProductName");
					mDataMap.put("sellercode",getManageCode());
					String lxc = WebClientSupport.upPost(TopUp.upConfig("productcenter.webclienturllxc"), mDataMap);
					if(lxc!=null && !lxc.equals("") && !lxc.equals("[]")){
						List<String> list = JSON.parseObject(lxc,new TypeReference<List<String>>(){});
						for(int i=0;i<list.size();i++){
							sa = new SearchAssociate();
							sa.setAssociateWord(list.get(i));
							sa.setAssociateWordNum(1);
							listSearchAssociate.add(sa);
						}
					}
					re.setSearchList(listSearchAssociate);
				
			}else{
				
				/***集群代码***/
				List<String> list = SolrQueryUtil.getSearchSuggestSpg(keyValue, num,null);
				if(!list.isEmpty() && null!=list && !"".equals(list)){
					for(int i=0;i<list.size();i++){
						sa = new SearchAssociate();
						sa.setAssociateWord(list.get(i));
						sa.setAssociateWordNum(1);
						listSearchAssociate.add(sa);
					}
				}
				re.setSearchList(listSearchAssociate);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return re;
	}
}
