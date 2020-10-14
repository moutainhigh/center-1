package com.cmall.newscenter.beauty.api;

import java.util.List;
import java.util.Map;

import com.cmall.newscenter.beauty.model.GetSkinTypeInput;
import com.cmall.newscenter.beauty.model.GetSkinTypeResult;
import com.cmall.newscenter.beauty.model.SkinType;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 惠美丽—获取皮肤类型Api
 * 
 * @author yangrong date: 2014-12-05
 * @version1.3.0
 */
public class GetSkinTypeApi extends
		RootApiForManage<GetSkinTypeResult, GetSkinTypeInput> {
	public static final String APP_CODE = "SI2007";

	public GetSkinTypeResult Process(GetSkinTypeInput inputParam,
			MDataMap mRequestMap) {

		GetSkinTypeResult result = new GetSkinTypeResult();

		// 设置相关信息
		if (result.upFlagTrue()) {
			MDataMap map = new MDataMap();
			map.put("app_code", APP_CODE);
			map.put("status", "449746600001");
			String sql = "SELECT skin_code,skin_type from nc_skin_type where app_code=:app_code and status=:status ORDER BY location DESC,create_time DESC";
			List<Map<String, Object>> list = DbUp.upTable("nc_skin_type")
					.dataSqlList(sql, map);
			if (list != null && list.size() != 0) {
				for (int i = 0; i < list.size(); i++) {
					SkinType st = new SkinType();
					st.setSkin_code(list.get(i).get("skin_code").toString());
					st.setSkin_name(list.get(i).get("skin_type").toString());
					result.getSkinList().add(st);
				}
			}

		}
		return result;
	}

}
