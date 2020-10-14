package com.cmall.ordercenter.webfunc.importdefine;

import java.math.BigDecimal;
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
 * 类: ImportOrderAdd <br>
 * 描述: 添加函数 <br>
 * 作者: zhy<br>
 * 时间: 2017年4月24日 下午6:22:22
 */
public class ImportOrderAdd extends RootFunc {

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult result = new MWebResult();
		MDataMap map = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String name = map.get("name");
		String flag_able=map.get("flag_able");
		String settlement_cost=map.get("settlement_cost");
		try {
			// 订单来源
			String orderSource = getMaxCode("44971519");
			// 订单类型
			String orderType = getMaxCode("44971520");
			// 订单渠道
			String orderChannel = getMaxCode("44974743");
			// 支付方式
			String payType = getMaxCode("44971620");
			result = CreateImportTemplate.getInstance().createTemplate(orderSource);
			if (result.getResultCode() == 1) {
				/**
				 * 将数据添加到sc_define中
				 */
				addScDefine(orderSource, name, "44974800");
				addScDefine(orderSource, name, "44971519");
				addScDefine(orderType, name, "44971520");
				addScDefine(orderChannel, name, "44974743");
				addScDefine(payType, name+"-代收", "44971620");
				/**
				 * 添加数据到订单导入相关配置表中
				 */
				MDataMap data = new MDataMap();
				data.put("settlement_cost", settlement_cost);
				data.put("code", orderSource);
				data.put("name", name);
				data.put("flag_able", flag_able);
				data.put("order_source", orderSource);
				data.put("order_type", orderType);
				data.put("order_channel", orderChannel);
				data.put("pay_type", payType);
				String user = UserFactory.INSTANCE.create().getUserCode();
				String time = DateUtil.getSysDateTimeString();
				data.put("create_user", user);
				data.put("create_time", time);
				data.put("update_user", user);
				data.put("update_time", time);
				DbUp.upTable("oc_import_define").dataInsert(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setResultCode(-1);
			result.setResultMessage(e.getMessage());
		}
		return result;
	}

	private static String getMaxCode(String parentCode) {
		String code = "";
		Map<String, Object> define = DbUp.upTable("sc_define").dataSqlOne(
				"select MAX(define_code) as m from systemcenter.sc_define where parent_code=:parent_code",
				new MDataMap("parent_code", parentCode));
		if (define != null && define.get("m") != null) {
			BigDecimal max = BigDecimal.valueOf(Double.parseDouble(define.get("m").toString()));
			code = max.add(BigDecimal.ONE).toString();
		} else {
			code = parentCode + "0001";
		}
		return code;
	}

	private static void addScDefine(String code, String name, String parentCode) {
		MDataMap data = new MDataMap();
		data.put("define_code", code);
		data.put("define_name", name);
		data.put("parent_code", parentCode);
		DbUp.upTable("sc_define").dataInsert(data);
	}
}
