package com.cmall.productcenter.service.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import scala.util.Random;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cmall.productcenter.model.SearchAssociate;
import com.cmall.productcenter.model.api.ApiSearchAssociateInput;
import com.cmall.productcenter.model.api.ApiSearchAssociateResult;
import com.cmall.productcenter.util.Base64Util;
import com.cmall.productcenter.util.SolrQueryUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootApiForVersion;

/**
 * 联想搜索词及推荐
 * @author zhouguohui
 *
 */
public class ApiSearchAssociate extends RootApiForVersion<ApiSearchAssociateResult,ApiSearchAssociateInput>{

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
		String associateType = inputParam.getAssociateType();
		if(StringUtils.isEmpty(associateType)){
			associateType="lxcProductName";
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
		
		// 默认修改为联想词推荐  5.6.4
		associateType = "lxc";
		
		MDataMap dataMap = new MDataMap();
		dataMap.put("keyWord", keyValue);
		dataMap.put("pageNo", "0");
		dataMap.put("pageSize", num+"");
		dataMap.put("sellercode",getManageCode());
		if(StringUtils.isNotBlank(associateType) && associateType.equals("lxc")){
			dataMap.put("lxc","lxc");
		}else if(StringUtils.isNotBlank(associateType) && associateType.equals("lxcProductName")){
			dataMap.put("lxc","lxcProductName");
		}else{
			//如果数据参数格式不对直接返回
			return re;
		}
		
		try {
			String associateValue = WebClientSupport.upPost(TopUp.upConfig("productcenter.webclienturllxc"), dataMap);
			List<String> list = JSON.parseObject(associateValue,new TypeReference<List<String>>(){}); 
			if(list!=null&&!list.isEmpty()){
				for(int i=0;i<list.size();i++){
					sa = new SearchAssociate();
					sa.setAssociateWord(list.get(i));
					listSearchAssociate.add(sa);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		re.setSearchList(listSearchAssociate);
		return re;
	}
}
