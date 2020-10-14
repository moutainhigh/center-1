package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import com.cmall.newscenter.model.ReceivingScanInput;
import com.cmall.newscenter.model.ReceivingScanResult;
import com.cmall.newscenter.util.MemberUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webdo.WebConst;

/**
 * 记录收货信息
 * 
 * @author shiyz date 2016-03-18
 * 
 */
public class ReceivingScanApi extends
		RootApiForToken<ReceivingScanResult, ReceivingScanInput> {

	public ReceivingScanResult Process(ReceivingScanInput inputParam,
			MDataMap mRequestMap) {

		ReceivingScanResult result = new ReceivingScanResult();

		String code = inputParam.getSecurityCode();

		List<String> list = new ArrayList<String>();
		// 扫描的二维码
		StringTokenizer st = new StringTokenizer(code, ",");

		while (st.hasMoreTokens()) {
			list.add(st.nextToken());
		}

		if (result.upFlagTrue()) {

			MDataMap map = DbUp.upTable("nc_agency").one("level_number",
					inputParam.getAgent_code());

			Map<String, Object> levelMap = new HashMap<String, Object>();

			if (map != null && !map.isEmpty()) {
				levelMap = DbUp
						.upTable("nc_agency_level")
						.dataSqlOne(
								"select agency_level from nc_agency_level where parent_id=(select agency_level from nc_agency_level where parent_id='')",
								new MDataMap());
			}

			if (levelMap != null && !levelMap.isEmpty()) {
				// 判断是否为大区
				if (map.get("agent_level").equals(levelMap.get("agency_level"))) {

					MDataMap whereMap = new MDataMap();
					
					whereMap.put("delivery_code", inputParam.getAgent_code());
					
					whereMap.put("delivery_number", String.valueOf(inputParam.getDelivery_number()));
					
					List<MDataMap> angetMap = DbUp.upTable("nc_delivery").queryAll("", "", "delivery_code=:delivery_code and delivery_number=:delivery_number", whereMap);
					
					if (angetMap.size()!=0) {

						MDataMap nomMap = new MDataMap();
						for (int i = 0; i < angetMap.size(); i++) {
							nomMap.put("delivery_no", angetMap.get(i).get("delivery_no"));
						}
						List<String> members = new ArrayList<String>();
						for (MDataMap m : angetMap) {
							members.add(m.get("delivery_no"));
						}

						List<MDataMap> delivemMap = DbUp.upTable(
								"nc_delivery_details").queryIn("dimensional_code", "", "", new MDataMap(), 0, 0, "delivery_no", StringUtils.join(members, WebConst.CONST_SPLIT_COMMA));

						// 判断扫描的数量是否一致
						if (delivemMap.size() != 0) {
							// 把查询的发货二维码重新组装
							List<Object> listValue = new ArrayList<Object>();
							for (MDataMap dmap : delivemMap) {
								listValue.add(dmap.get("dimensional_code"));
							}

							for (int i = 0; i < list.size(); i++) {
								// 查询每个二维码是否收货
								MDataMap detaMap = DbUp.upTable(
										"nc_delivery_receipt").one(
										"receipt_qrcode", list.get(i));

								if (detaMap != null && !detaMap.isEmpty()) {

									result.inErrorMessage(934205161);
									return result;

								} else {

									if (!listValue.contains(list.toArray()[i])) {

										result.inErrorMessage(934205170);

										return result;

									}
								}

							}

						} else {

							result.inErrorMessage(934205160);

							return result;

						}

						/* 系统当前时间 */
						String create_time = com.cmall.newscenter.util.DateUtil
								.getNowTime();

						if (list.size() != 0) {

							MDataMap recemMap = new MDataMap();

							recemMap.put("anget_code",
									inputParam.getAgent_code());

							recemMap.put("receiving_time", create_time);

							recemMap.put("receiving_num", String
									.valueOf(inputParam.getDelivery_number()));

							DbUp.upTable("nc_receiving_scan").dataInsert(
									recemMap);

							for (int i = 0; i < list.size(); i++) {

								MDataMap codeMap = new MDataMap();

								codeMap.put("receipt_qrcode", list.get(i));

								codeMap.put("receipt_code",
										inputParam.getAgent_code());

								codeMap.put("scan_time", create_time);

								codeMap.put("category_code",
										inputParam.getCategory_code());

								DbUp.upTable("nc_delivery_receipt").dataInsert(
										codeMap);

							}

						}

						MDataMap mWhereMap = new MDataMap();

						mWhereMap.put("level_number",
								inputParam.getAgent_code());

						Map<String, Object> ageMap = DbUp
								.upTable("nc_agency")
								.dataSqlOne(
										"select * from newscenter.nc_agency where level_number = (select nc.parent_id from newscenter.nc_agency  nc where nc.level_number =:level_number)",
										mWhereMap);

						if (ageMap != null || !ageMap.isEmpty()) {

							result.setSuperior_agent(ageMap.get("agent_name")
									.toString());

							result.setAgent_phone(ageMap.get(
									"agent_mobilephone").toString());

							result.setAgent_wx(ageMap.get("agent_wechat")
									.toString());

							result.setNum(delivemMap.size());

						} else {

							result.inErrorMessage(934205156);
							return result;

						}

					} else {

						result.inErrorMessage(934205163);
						return result;

					}
				} else {
					//查询上级代理信息
					MDataMap mWhereMap = new MDataMap();

					mWhereMap.put("level_number", inputParam.getAgent_code());

					Map<String, Object> ageMap = DbUp
							.upTable("nc_agency")
							.dataSqlOne(
									"select * from newscenter.nc_agency where level_number = (select nc.parent_id from newscenter.nc_agency  nc where nc.level_number =:level_number)",
									mWhereMap);
					
                    //判断上级是否收货
					for (int i = 0; i < list.size(); i++) {

						MDataMap map2 = DbUp.upTable("nc_delivery_receipt")
								.one("receipt_qrcode",
										list.get(i),
										"receipt_code",
										new MemberUtil()
												.Agent_parent(inputParam
														.getAgent_code()));

						if (map2 != null && !map2.isEmpty()) {
							
						} else {
							result.inErrorMessage(934205167);
							return result;
						}

					}
					
					//同一级别是否收货
					for (int i = 0; i < list.size(); i++) {

						List<MDataMap> map2 = DbUp.upTable("nc_delivery_receipt")
								.queryByWhere("receipt_qrcode", list.get(i));

						if (map2.size()!=0) {
							
							MDataMap map3 = DbUp.upTable("nc_delivery_receipt")
									.one("receipt_qrcode", list.get(i),"receipt_code",inputParam.getAgent_code());	
							
						 //自己已收货	
					     if(map3!=null&&!map3.isEmpty()){
								
					    	 result.inErrorMessage(934205166);
								return result;	
						}
					     for(int m = 0;m<map2.size();m++){
					     //同一级别的其他代理已收货
							if(new MemberUtil().Agent_name(map2.get(m).get("receipt_code")).get("agent_level").equals(new MemberUtil().Agent_name(inputParam.getAgent_code()).get("agent_level"))){
								
								result.inErrorMessage(934205171);
								return result;
							}
					     }
						}

					}

					/* 系统当前时间 */
					String create_time = com.cmall.newscenter.util.DateUtil
							.getNowTime();

					if (list.size() != 0) {

						MDataMap recemMap = new MDataMap();

						recemMap.put("anget_code", inputParam.getAgent_code());

						recemMap.put("receiving_time", create_time);

						recemMap.put("receiving_num",
								String.valueOf(inputParam.getDelivery_number()));

						DbUp.upTable("nc_receiving_scan").dataInsert(recemMap);

						for (int i = 0; i < list.size(); i++) {

							MDataMap codeMap = new MDataMap();

							codeMap.put("receipt_qrcode", list.get(i));

							codeMap.put("receipt_code",
									inputParam.getAgent_code());

							codeMap.put("scan_time", create_time);

							codeMap.put("category_code",
									inputParam.getCategory_code());

							DbUp.upTable("nc_delivery_receipt").dataInsert(
									codeMap);

						}

					}


					if (ageMap != null || !ageMap.isEmpty()) {

						result.setSuperior_agent(ageMap.get("agent_name")
								.toString());

						result.setAgent_phone(ageMap.get("agent_mobilephone")
								.toString());

						result.setAgent_wx(ageMap.get("agent_wechat")
								.toString());

						result.setNum(list.size());

					} else {

						result.inErrorMessage(934205156);
						return result;

					}
				}

			}

		}

		return result;
	}

}
