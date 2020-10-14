package com.cmall.ordercenter.webfunc.importdefine;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.TopDir;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * 类: ImportOrderDel <br>
 * 描述: 删除函数 <br>
 * 作者: zhy<br>
 * 时间: 2017年4月24日 下午7:06:53
 */
public class ImportOrderDel extends RootFunc {

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult result = new MWebResult();
		MDataMap map = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		try {
			if (map != null && StringUtils.isNotBlank(map.get("uid"))) {
				String uid = map.get("uid");
				Map<String, Object> data = DbUp.upTable("oc_import_define").dataSqlOne(
						"select `code`,order_source,order_type,order_channel,pay_type from oc_import_define where uid=:uid",
						new MDataMap("uid", uid));
				if (data != null) {
					MDataMap datamap = new MDataMap(data);
					del(datamap.get("order_source"));
					del(datamap.get("order_type"));
					del(datamap.get("order_channel"));
					del(datamap.get("pay_type"));
					del(datamap.get("code"));
					DbUp.upTable("oc_import_define").dataDelete("`code`=:code",
							new MDataMap("code", datamap.get("code")), "code");
					/**
					 * 删除模板
					 */
					String path = new TopDir().upServerletPath("resources/cfamily/order/");
					File f = new File(path + datamap.get("code") + ".xls");
					if (f.exists()) {
						f.delete();
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setResultCode(-1);
			result.setResultMessage(e.getMessage());
		}
		return result;
	}

	private static void del(String code) {
		DbUp.upTable("sc_define").dataDelete("define_code=:define_code", new MDataMap("define_code", code),
				"define_code");
	}
}
