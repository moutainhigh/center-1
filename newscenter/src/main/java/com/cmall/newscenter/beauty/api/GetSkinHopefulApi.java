package com.cmall.newscenter.beauty.api;

import java.util.List;
import java.util.Map;

import com.cmall.newscenter.beauty.model.GetSkinHopefulInput;
import com.cmall.newscenter.beauty.model.GetSkinHopefulResult;
import com.cmall.newscenter.beauty.model.SkinHopeful;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 惠美丽—获取护肤需求Api
 * 
 * @author yangrong date: 2014-12-05
 * @version1.3.0
 */
public class GetSkinHopefulApi extends
		RootApiForManage<GetSkinHopefulResult, GetSkinHopefulInput> {

	public static final String APP_CODE = "SI2007";

	public GetSkinHopefulResult Process(GetSkinHopefulInput inputParam,
			MDataMap mRequestMap) {

		GetSkinHopefulResult result = new GetSkinHopefulResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			MDataMap map = new MDataMap();
			map.put("app_code", APP_CODE);
			map.put("status", "449746600001");
			String sql = "SELECT hopeful_code,hopeful_name from nc_skin_hopeful where app_code=:app_code and status=:status ORDER BY location DESC,create_time DESC";
			List<Map<String, Object>> list = DbUp.upTable("nc_skin_hopeful")
					.dataSqlList(sql, map);

			if (list != null && list.size() != 0) {
				for (int i = 0; i < list.size(); i++) {
					SkinHopeful shf = new SkinHopeful();
					shf.setHopeful_code(list.get(i).get("hopeful_code")
							.toString());
					shf.setHopeful_name(list.get(i).get("hopeful_name")
							.toString());
					result.getHopefulList().add(shf);
				}
			}

		}
		return result;
	}

}
