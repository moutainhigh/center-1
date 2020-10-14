package com.cmall.ordercenter.webfunc.importdefine;

import java.util.Map;

import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * 类: ImportOrderEdit <br>
 * 描述: 编辑函数 <br>
 * 作者: zhy<br>
 * 时间: 2017年4月24日 下午7:17:28
 */
public class ImportOrderEdit extends RootFunc {

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult result = new MWebResult();
		MDataMap map = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String name = map.get("name");
		String flag_able = map.get("flag_able");
		String settlement_cost=map.get("settlement_cost");
		try {
			String code = map.get("code");
			Map<String, Object> data = DbUp.upTable("oc_import_define").dataSqlOne(
					"select order_source,order_type,order_channel,pay_type from oc_import_define where `code` =:code",
					new MDataMap("code", code));
			if (data != null) {
				MDataMap datamap = new MDataMap(data);
				editScDefine(datamap.get("order_source"), name);
				editScDefine(datamap.get("order_type"), name);
				editScDefine(datamap.get("order_channel"), name);
				editScDefine(datamap.get("pay_type"), name+"-代收");
				editScDefine(code, name);

				MDataMap edit = new MDataMap();
				edit.put("settlement_cost", settlement_cost);
				edit.put("code", code);
				edit.put("name", name);
				edit.put("flag_able", flag_able);
				String user = UserFactory.INSTANCE.create().getUserCode();
				String time = DateUtil.getSysDateTimeString();
				edit.put("update_user", user);
				edit.put("update_time", time);
				DbUp.upTable("oc_import_define").dataUpdate(edit, "name,flag_able,update_user,update_time,settlement_cost", "code");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static void editScDefine(String code, String name) {
		MDataMap data = new MDataMap();
		data.put("define_code", code);
		data.put("define_name", name);
		DbUp.upTable("sc_define").dataUpdate(data, "define_name", "define_code");
	}

}
