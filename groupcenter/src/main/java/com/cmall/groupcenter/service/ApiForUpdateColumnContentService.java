package com.cmall.groupcenter.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.cmall.productcenter.support.AutoSelectProductSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

public class ApiForUpdateColumnContentService implements Runnable{
	/**
	 * 处理数据
	 */
	private String columnCode="";
	private String ruleCode="";
	public ApiForUpdateColumnContentService(String columnCode,String ruleCode) {
		this.columnCode = columnCode;
		this.ruleCode = ruleCode;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(StringUtils.isNotBlank(columnCode)) {
			List<Map<String,Object>> list = DbUp.upTable("fh_apphome_column").dataSqlList("select column_code,rule_code,update_frequency,operate_time,start_time,end_time from fh_apphome_column where column_code=:column_code", new MDataMap("column_code",columnCode));
			Map<String, Object> map = list.get(0);
			if(map!=null) {
				List<String>proCodeList = new ArrayList<String>();
				String rc = ruleCode;
				String cc = map.get("column_code").toString();
				String st = map.get("start_time").toString();
				String et = map.get("end_time").toString();
				//proCodeList=method***;  //调用方法
				proCodeList = new AutoSelectProductSupport().getSelectProduct(rc);
				if(proCodeList.size()>0) {
					String sysDateTimeString = com.cmall.productcenter.common.DateUtil.getSysDateTimeString();
				/*	List<Map<String, Object>> subList = DbUp.upTable("fh_apphome_column_content").dataSqlList("select showmore_linkvalue from fh_apphome_column_content where column_code=:column_code", new MDataMap("column_code",cc));
					int num = subList==null?0:subList.size();
					for (Map<String, Object> ssl : subList) {//排重
						if(proCodeList.contains(ssl.get("showmore_linkvalue").toString())) {proCodeList.remove(ssl.get("showmore_linkvalue").toString());}
					}*/
					int num =0;
					DbUp.upTable("fh_apphome_column_content").delete("column_code",cc);
					for (String code : proCodeList) {
						 MDataMap paramMap = new MDataMap();
						 paramMap.put("uid",WebHelper.upUuid() );
						 paramMap.put("column_code", cc);
						 paramMap.put("start_time",st);
						 paramMap.put("end_time", et);
						 paramMap.put("position",(++num)+"");
						 paramMap.put("title_color", "#333333");
						 paramMap.put("description_color", "#999999");
						 paramMap.put("showmore_linktype", "4497471600020004");
						 paramMap.put("showmore_linkvalue", code);
						 paramMap.put("is_share", "449746250002");
						 paramMap.put("is_delete", "449746250002");
						 paramMap.put("create_time",sysDateTimeString);
						 paramMap.put("update_time", sysDateTimeString);
						 paramMap.put("skip_place", "449746250002");
						 DbUp.upTable("fh_apphome_column_content").dataInsert(paramMap);
						}
					}
			}
		
		}else {
			// 1.查询符合条件的栏目编号
			List<Map<String,Object>> list = DbUp.upTable("fh_apphome_column").dataSqlList("select column_code,rule_code,update_frequency,operate_time,start_time,end_time from fh_apphome_column where product_maintenance='44975017002' and is_delete='449746250002' and release_flag='449746250001' ", null);
			if(list!=null&&list.size()>0) {
				for (Map<String, Object> map : list) {
					List<String>proCodeList = new ArrayList<String>();
					String uf = map.get("update_frequency").toString();//执行频率
					String ot = map.get("operate_time").toString();//上次执行日期
					String nowDate = com.cmall.productcenter.common.DateUtil.getSysDateString();//当前日期
					Date date =  com.cmall.productcenter.common.DateUtil.toDate(ot);//上次执行日期
					Date nextOperateDate = com.cmall.productcenter.common.DateUtil.addDays(date, Integer.parseInt(uf));//下次执行日期
					String nextOperateDateStr = com.cmall.productcenter.common.DateUtil.toString(nextOperateDate);
					if(nextOperateDateStr.equals(nowDate)) {//执行时间到了
						String rc = map.get("rule_code").toString();
						String cc = map.get("column_code").toString();
						String st = map.get("start_time").toString();
						String et = map.get("end_time").toString();
						//proCodeList=method***;  //调用方法
						proCodeList = new AutoSelectProductSupport().getSelectProduct(rc);
						if(proCodeList.size()>0) {
							String sysDateTimeString = com.cmall.productcenter.common.DateUtil.getSysDateTimeString();
							List<Map<String, Object>> subList = DbUp.upTable("fh_apphome_column_content").dataSqlList("select showmore_linkvalue from fh_apphome_column_content where column_code=:column_code", new MDataMap("column_code",cc));
							int num = subList==null?0:subList.size();
							for (Map<String, Object> ssl : subList) {//排重
								if(proCodeList.contains(ssl.get("showmore_linkvalue").toString())) {proCodeList.remove(ssl.get("showmore_linkvalue").toString());}
							}
							for (String code : proCodeList) {
								 MDataMap paramMap = new MDataMap();
								 paramMap.put("uid",WebHelper.upUuid() );
								 paramMap.put("column_code", cc);
								 paramMap.put("start_time",st);
								 paramMap.put("end_time", et);
								 paramMap.put("position",(++num)+"");
								 paramMap.put("title_color", "#333333");
								 paramMap.put("description_color", "#999999");
								 paramMap.put("showmore_linktype", "4497471600020004");
								 paramMap.put("showmore_linkvalue", code);
								 paramMap.put("is_share", "449746250002");
								 paramMap.put("is_delete", "449746250002");
								 paramMap.put("create_time",sysDateTimeString);
								 paramMap.put("update_time", sysDateTimeString);
								 paramMap.put("skip_place", "449746250002");
								 DbUp.upTable("fh_apphome_column_content").dataInsert(paramMap);
							}
						}
						DbUp.upTable("fh_apphome_column").dataUpdate(new MDataMap("column_code",cc,"operate_time",nowDate), "operate_time", "column_code");
					}
				}
			}
		}
	}
}
