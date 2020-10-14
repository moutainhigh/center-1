package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.cmall.newscenter.model.SecurityCode;
import com.cmall.newscenter.model.SecurityCodeInput;
import com.cmall.newscenter.model.SecurityCodeResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.url.ShortUrl;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 家有生成防伪码
 * 
 * @author shiyz date 2015-01-16
 * @version 1.0
 */
public class SecurityCodeApi extends
		RootApiForManage<SecurityCodeResult, SecurityCodeInput> {

	public SecurityCodeResult Process(SecurityCodeInput inputParam,
			MDataMap mRequestMap) {

		SecurityCodeResult result = new SecurityCodeResult();

		if (result.upFlagTrue()) {

			/* 渠道图片 */
			String channelPic = "";

			/* 商品对应 */

			if (inputParam.getProductCode().equals("733959")) {

				inputParam.setProductCode("133296");

			} else if (inputParam.getProductCode().equals("733960")) {

				inputParam.setProductCode("133293");

			}

			/* 防伪批次 */
			String security_batch = "";

			String security_source = "";

			if (inputParam.getSecurityNum() > 0
					&& inputParam.getSecurityNum() <= Integer
							.valueOf(bConfig("newscenter.security_count"))) {

				/* 查询是否存在渠道名称 */
				MDataMap map = DbUp.upTable("nc_commodity_channel").one(
						"channel_remarks", inputParam.getChannelName(),
						"channel_appcode", getManageCode());

				if (map != null) {

					/* 判断渠道是否可用 */
					if (map.get("channel_stats").equals("4497465000060002")) {

						result.setResultCode(934205104);

						result.setResultMessage("该渠道不可用");

						return result;

					} else {

						channelPic = map.get("channel_pic");

						security_source = map.get("uid");

					}

				} else {

					MDataMap mDataMap = DbUp.upTable("nc_commodity_channel")
							.one("channel_code",
									bConfig("newscenter.channel_code"),
									"channel_appcode", getManageCode());

					if (mDataMap != null) {

						/* 判断渠道是否可用 */
						if (mDataMap.get("channel_stats").equals(
								"4497465000060002")) {

							result.setResultCode(934205104);

							result.setResultMessage("该渠道不可用");

							return result;

						} else {

							MDataMap chanDataMap = new MDataMap();

							chanDataMap.put("channel_appcode", getManageCode());

							chanDataMap.put("channel_code", "嘉玲国际-LD");

							chanDataMap.put("channel_time",
									FormatHelper.upDateTime());

							chanDataMap.put("channel_remarks",
									inputParam.getChannelName());

							chanDataMap
									.put("channel_stats", "4497465000060001");

							chanDataMap.put("channel_pic",
									mDataMap.get("channel_pic"));

							DbUp.upTable("nc_commodity_channel").dataInsert(
									chanDataMap);

							MDataMap comDataMap = DbUp.upTable(
									"nc_commodity_channel").one(
									"channel_remarks",
									inputParam.getChannelName(),
									"channel_appcode", getManageCode());

							channelPic = comDataMap.get("channel_pic");

							security_source = comDataMap.get("uid");
						}

					} else {

						result.setResultCode(934205105);

						result.setResultMessage("'"
								+ bConfig("newscenter.channel_code") + "'渠道不存在");

						return result;
					}

				}

				/* 防伪批次 */
				security_batch = FormatHelper.upDateTime().replace("-", "")
						.replace(":", "").trim().replace(" ", "")
						+ Math.round(Math.random() * 9000 + 1000);

				/* 系统当前时间 */
				String create_time = com.cmall.newscenter.util.DateUtil
						.getNowTime();

				MDataMap batchMap = new MDataMap();

				batchMap.put("security_generationtime", create_time);

				batchMap.put("security_batch", security_batch);

				batchMap.put("security_appcode", getManageCode());

				MDataMap dMap = checkIsExistProduct(inputParam.getProductCode());

				if (dMap == null) {
					// 返回提示信息"不存在此编号的商品，请重新填写！"
					result.setResultCode(941901084);

					result.setResultMessage(bInfo(941901084));

					return result;
				} else {

					batchMap.put("security_itemname", dMap.get("sku_name"));
				}

				batchMap.put("security_num",
						String.valueOf(inputParam.getSecurityNum()));

				batchMap.put("security_source", security_source);

				batchMap.put("security_productiontime",
						inputParam.getOrderTime().equals("") ? create_time
								: inputParam.getOrderTime());

				batchMap.put("security_itemnumber", inputParam.getProductCode());

				batchMap.put("customer_number", inputParam.getCustomerNumber());

				batchMap.put("logistics_number",
						inputParam.getLogisticsNumber());

				try {
					/** 将防伪码信息插入nc_security_code表中 */
					DbUp.upTable("nc_security_code").dataInsert(batchMap);
				} catch (Exception e) {

					e.printStackTrace();

					result.setResultCode(934205104);

					result.setResultMessage("生成防伪码有误");

					return result;
				}

				MDataMap securMap = new MDataMap();

				securMap.put("security_batch", batchMap.get("security_batch"));

				securMap.put("security_app", batchMap.get("security_appcode"));

				securMap.put("security_itemnumber",
						batchMap.get("security_itemnumber"));

				securMap.put("security_source", batchMap.get("security_source"));

				securMap.put("security_itemname",
						batchMap.get("security_itemname"));

				int num = 0;

				try {

					int security_num = inputParam.getSecurityNum();

					List<SecurityCode> securityCodeList = new ArrayList<SecurityCode>();

					String sSql = "select * from nc_Link_address where project_type = '4497465000090002' and link_appcode = '"
							+ getManageCode() + "' order by link_time desc ";

					List<Map<String, Object>> listMap = DbUp.upTable(
							"nc_Link_address")
							.dataSqlList(sSql, new MDataMap());

					if (listMap.size() != 0) {

						if (security_num != 0) {

							for (int i = 0; i < security_num; i++) {

								SecurityCode securityCode = new SecurityCode();
								/* 批次内序号 */
								num = Integer
										.valueOf(bConfig("newscenter.security_num"));

								securMap.put("security_batchnum",
										String.valueOf(num + i));

								String security_code = ""
										+ listMap.get(0).get("link_address")
												.toString() + "?code="
										+ getManageCode() + "-"
										+ inputParam.getProductCode() + "-"
										+ UUID.randomUUID()
										+ "&app=liujialing&type=check";

								String short_url = ShortUrl
										.upShortUrl(security_code);

								/* 防伪码 */
								securMap.put("security_code", security_code);

								DbUp.upTable("nc_short_url").insert("long_url",
										security_code, "short_url", short_url);

								DbUp.upTable("nc_securitycode_details")
										.dataInsert(securMap);

								securityCode
										.setSecurityCode(bConfig("newscenter.short_url")
												+ "id="
												+ security_batch
												+ "ul=" + short_url);

								securityCode.setSecurityBatchnum(String
										.valueOf(num + i));

								securityCodeList.add(securityCode);

							}

						}
					} else {

						result.setResultCode(934205104);

						result.setResultMessage("生成防伪码的链接地址不存在");

						return result;

					}

					result.setSecurityBatch(batchMap.get("security_batch"));
					result.setSecurityCodeList(securityCodeList);

				} catch (Exception e) {

					result.inErrorMessage(959701033);

					return result;
				}

			} else {

				result.setResultCode(934205104);

				result.setResultMessage("生成防伪码的数量必须大于零或者最大生成数");

				return result;
			}

		}
		return result;
	}

	/**
	 * 检验商品ID是否有对应的商品
	 * 
	 * @param adImgUrl
	 * @return
	 */
	private MDataMap checkIsExistProduct(String productCdoe) {

		// 判断数据库中是否存在相同记录
		MDataMap map = DbUp.upTable("pc_skuinfo").one("sku_code", productCdoe);
		if (map != null) {
			return map;
		}
		return map;
	}

}
