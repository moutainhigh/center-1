package com.cmall.productcenter.service.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.HighendTypeInfo;
import com.cmall.productcenter.model.api.ApiGetHighendTypeInput;
import com.cmall.productcenter.model.api.ApiGetHighendTypeResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 
 * 项目名称：productcenter 
 * 类名称：     ApiGetShowwindow 
 * 类描述：     获取高端商品分类下具体内容
 * 创建人：     GaoYang
 * 创建时间：2014年2月8日下午4:24:53
 * 修改人：     GaoYang
 * 修改时间：2014年2月8日下午4:24:53
 * 修改备注：
 *
 */
public class ApiGetHighendType extends RootApi<ApiGetHighendTypeResult,ApiGetHighendTypeInput>{

	public ApiGetHighendTypeResult Process(ApiGetHighendTypeInput inputParam,
			MDataMap mRequestMap) {
		
		ApiGetHighendTypeResult result = new ApiGetHighendTypeResult();
		if(inputParam == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			//传入的高端商品分类（现阶段主要是好物产）
			String highendType = inputParam.getHighendType();
			
			//默认分类是好物产
			if(StringUtils.isBlank(highendType)){
				highendType = "449746340001";
			}
			
			//根据传入的高端商品分类查询系统定义表，获取该分类具体信息
			HighendTypeInfo typeInfo = new HighendTypeInfo();
			List<HighendTypeInfo>  hightendTypeList= new ArrayList<HighendTypeInfo>();
			List<MDataMap> hightendTypeData = new ArrayList<MDataMap>();
			
			String sFields = "define_code,define_name";
			String sWhere = "parent_code = '" + highendType +"'";
			hightendTypeData = DbUp.upTable("sc_define").queryAll(sFields, "", sWhere, new MDataMap());
			for(int i = 0;i<hightendTypeData.size();i++){
				typeInfo = new SerializeSupport<HighendTypeInfo>().serialize(hightendTypeData.get(i),new HighendTypeInfo());
				hightendTypeList.add(typeInfo);
			}
			
			if(hightendTypeList != null && hightendTypeList.size() >0){
				result.setHightendTypeList(hightendTypeList);
			}else{
				//高端商品分类详情不存在时，返回提示信息
				result.setResultMessage(bInfo(941901048));
				result.setResultCode(941901048);
			}
		}
		
		return result;
	}
}
