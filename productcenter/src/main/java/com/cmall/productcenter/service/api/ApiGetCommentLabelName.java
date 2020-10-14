package com.cmall.productcenter.service.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.productcenter.model.GetFreightNameInput;
import com.cmall.productcenter.model.GetFreightNameResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

public class ApiGetCommentLabelName extends RootApi<GetFreightNameResult, GetFreightNameInput> {

	/**
	 *根据labelcode获取评论label名称  label:标签 
	 * @author lgj
	 */
	public GetFreightNameResult Process(GetFreightNameInput inputParam, MDataMap mRequestMap) {
		GetFreightNameResult result = new GetFreightNameResult();
		String uid = inputParam.getUid();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		if(uid!=null&&!"".equals(uid)){
			String [] input = uid.split(",");
			String sql = "select label_name from pc_comment_labelmanage where label_code in('";
			for(int j = 0;j<input.length;j++){
				if(j==0){
					sql+=input[j]+"'";
				}else {
					sql+=",'"+input[j]+"'";
				}
			}
			list = DbUp.upTable("pc_comment_labelmanage").dataSqlList(sql+")", new MDataMap());
		}
		String labelName = "";
		if(!list.isEmpty()){
			for(int i=0;i<list.size();i++){
				if(i==0){
					labelName = list.get(i).get("label_name").toString();
				}else {
					labelName=labelName+","+list.get(i).get("label_name").toString();
				}
			}
		}
		result.setTplName(labelName);
		return result;
	}
}
