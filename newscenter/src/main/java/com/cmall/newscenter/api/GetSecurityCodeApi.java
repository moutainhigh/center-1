package com.cmall.newscenter.api;

import java.util.List;
import java.util.Map;

import com.cmall.newscenter.model.GetSecurityCodeInput;
import com.cmall.newscenter.model.GetSecurityCodeResult;
import com.cmall.newscenter.util.MemberUtil;
import com.cmall.systemcenter.service.ScDefineService;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForMember;
import com.srnpr.zapweb.webdo.WebTemp;

/**
 * 验证防伪码真伪
 * 
 * @author shiyz date 2014-09-20
 * 
 */
public class GetSecurityCodeApi extends
		RootApiForMember<GetSecurityCodeResult, GetSecurityCodeInput> {

	public GetSecurityCodeResult Process(GetSecurityCodeInput inputParam,
			MDataMap mRequestMap) {

		GetSecurityCodeResult result = new GetSecurityCodeResult();

		MDataMap mDataMap = new MDataMap();

		String userCode = "";

		String securityCode = "";

		if (result.upFlagTrue()) {

			if (inputParam.getSecurityCode().length() > 60) {

				securityCode = inputParam.getSecurityCode();

			} else {

				String shortUrl = inputParam.getSecurityCode().substring(
						inputParam.getSecurityCode().length() - 12);

				String longUrl = new MemberUtil().getLongUrl(shortUrl);

				if ("".equals(longUrl) && null == longUrl) {

					result.setTestResult(2);
				} else {

					securityCode = longUrl;

				}

			}

			MDataMap map = DbUp.upTable("nc_securitycode_details").one(
					"security_code", securityCode);

			if (map != null) {

				mDataMap.put("security_code", securityCode);

				mDataMap.put("security_num", String.valueOf(Integer.valueOf(map
						.get("security_num")) + 1));

				mDataMap.put("security_date", FormatHelper.upDateTime());

				if (getFlagLogin()) {

					userCode = getOauthInfo().getUserCode();

					mDataMap.put("security_user", userCode);

					/* 记录用户扫描详情 */
					DbUp.upTable("nc_security_user").insert("security_user",
							userCode, "security_time",
							FormatHelper.upDateTime(), "security_code",
							securityCode, "equipment_id",
							inputParam.getEquipmentId());

					/* 记录扫描详情 */
					DbUp.upTable("nc_securitycode_details").dataUpdate(
							mDataMap,
							"security_num,security_user,security_date",
							"security_code");

				} else {

					/* 记录用户扫描详情 */
					DbUp.upTable("nc_security_user").insert("security_time",
							FormatHelper.upDateTime(), "security_code",
							securityCode, "equipment_id",
							inputParam.getEquipmentId());

					/* 记录扫描详情 */
					DbUp.upTable("nc_securitycode_details").dataUpdate(
							mDataMap, "security_num,security_date",
							"security_code");

				}

				String sSql = "select sku_name from pc_skuinfo where sku_code = '"
						+ map.get("security_itemnumber") + "'";

				Map<String, Object> productmMap = DbUp.upTable("pc_skuinfo")
						.dataSqlOne(sSql, new MDataMap());

				List<MDataMap> channelMap = DbUp
						.upTable("nc_commodity_channel").queryByWhere("uid",
								map.get("security_source"), "channel_appcode",
								map.get("security_app"));

				if (productmMap.get("sku_name") != null) {

					/* 商品名称 */
					result.setSecrityProduct(String.valueOf(productmMap
							.get("sku_name")));

					result.setTestResult(1);

					/* 查询次数 */
					result.setQueries(Integer.valueOf(map.get("security_num")) + 1);

					/* 渠道图片 */

					if (channelMap.size() != 0) {

						/* 销售渠道 */
						result.getChannel().setChnanel(
								channelMap.get(0).get("channel_code"));

						result.getChannel().setIcon(
								channelMap.get(0).get("channel_pic"));
					}

					String app_code = WebTemp.upTempDataOne("uc_appinfo",
							"app_name", "app_code", map.get("security_app"));
					/* app */
					result.setSecurityAppCode(app_code);

				} else {

					result.setTestResult(3);

				}
			} else {

				result.setTestResult(2);

			}

		}

		return result;
	}

}
