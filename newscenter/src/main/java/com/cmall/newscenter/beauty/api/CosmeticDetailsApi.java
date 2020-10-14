package com.cmall.newscenter.beauty.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.beauty.model.CosmeticDetailsInput;
import com.cmall.newscenter.beauty.model.CosmeticDetailsResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 惠美丽—妆品详情Api
 * 
 * @author yangrong date: 2015-01-25
 * @version1.3.2
 */
public class CosmeticDetailsApi extends RootApiForManage<CosmeticDetailsResult, CosmeticDetailsInput> {

	public CosmeticDetailsResult Process(CosmeticDetailsInput inputParam,MDataMap mRequestMap) {

		CosmeticDetailsResult result = new CosmeticDetailsResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			String cosmetic_code = inputParam.getCosmetic_code();

			MDataMap dataMap = DbUp.upTable("nc_cosmetic_bag").one("cosmetic_code", cosmetic_code);

			if (dataMap != null) {

				result.setCosmetic_code(cosmetic_code);
				result.setCosmetic_name(dataMap.get("cosmetic_name"));
				result.setCosmetic_price(dataMap.get("cosmetic_price"));
				result.setDisabled_time(dataMap.get("disabled_time"));
				result.setCount(dataMap.get("count"));
				result.setUnit(dataMap.get("unit"));
				result.setIswarn(dataMap.get("iswarn"));
				result.setWarn_time(dataMap.get("warn_time"));
				result.setRemark(dataMap.get("remark"));

				String img = dataMap.get("photo");
				List<String> list = new ArrayList<String>();
				String a[] = img.split(",");

				for (int i = 0; i < a.length; i++) {

					list.add(a[i]);
				}
				result.setPhoto(list);

			} else {

				result.setResultCode(934205144);
				result.setResultMessage(bInfo(934205144));

			}

		}
		return result;
	}

}
