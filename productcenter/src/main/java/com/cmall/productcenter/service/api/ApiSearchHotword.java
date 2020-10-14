package com.cmall.productcenter.service.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.productcenter.model.HotWord;
import com.cmall.productcenter.model.api.ApiSearchHotwordInput;
import com.cmall.productcenter.model.api.ApiSearchHotwordResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 热搜词汇接口
 * @author zhouguohui
 *
 */
public class ApiSearchHotword extends RootApiForManage<ApiSearchHotwordResult,ApiSearchHotwordInput> {

	public ApiSearchHotwordResult Process(ApiSearchHotwordInput inputParam,
			MDataMap mRequestMap) {
		ApiSearchHotwordResult re = new ApiSearchHotwordResult();
		List<HotWord> hotword = new ArrayList<HotWord>();
		String  sellerCode =  getManageCode();
		HotWord hw = null;
		//获取当前参数
		int num  = inputParam.getNum();
		if(num==0 || num<0){
			num=10;
		}
       if(re.upFlagTrue()){
			/*根据该数值对应前台显示位置；数值相同或为空时按照发布时间排序，新发布显示靠前*/
			String sSql = "select top_keyword from pc_hot_word where top_appcode='"+sellerCode+"' order by top_num asc ,top_time desc limit "+num;
			List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
			mapList = DbUp.upTable("pc_hot_word").dataSqlList(sSql, new MDataMap());
          if(!mapList.isEmpty()){
        	  
				for(int i =0;i<mapList.size();i++){
					hw = new HotWord();
					hw.setHotWord(String.valueOf(mapList.get(i).get("top_keyword")));
					hotword.add(hw);
				}
          }
          re.setHotwordList(hotword);
       }
		return re;
	}

}
