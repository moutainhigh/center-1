package com.cmall.systemcenter.api;

import java.util.List;
import java.util.Map;

import com.cmall.systemcenter.model.KafkaServerInput;
import com.cmall.systemcenter.model.KafkaServerResult;
import com.cmall.systemcenter.util.Base64Util;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/***
 * storm往咱们数据库查询方法信息
 * @author zhouguohui
 * @version 1.0
 */
public class KafkaServer extends RootApiForManage<KafkaServerResult, KafkaServerInput> {

	public KafkaServerResult Process(KafkaServerInput inputParam,
			MDataMap mRequestMap) {
		KafkaServerResult resultKafka = new KafkaServerResult();
		String sqlName =  Base64Util.getFromBASE64(inputParam.getSqlName());
		String sqlvalue = Base64Util.getFromBASE64(inputParam.getSqlValue());
		/***判断查询的时间用户想要一条还是多条数据判断***/
		if(inputParam.getQueryListOrOne().trim().equals("O")){
			/**调用系统的查询一条的sql方法**/
			Map<String, Object> map = DbUp.upTable(sqlName).dataSqlOne(sqlvalue, new MDataMap());
			resultKafka.setMap(map);
		}else{
			/**调用系统的查询多条的sql方法**/
			List<Map<String, Object>> list = DbUp.upTable(sqlName).dataSqlList(sqlvalue, new MDataMap());
			resultKafka.setList(list);
		}
		return resultKafka;
	}

}
