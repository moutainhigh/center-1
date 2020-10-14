package com.cmall.newscenter.beauty.api;

import com.cmall.newscenter.beauty.model.DeleteCosmeticBagInput;
import com.cmall.newscenter.beauty.model.DeleteCosmeticBagResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 惠美丽—妆品删除API
 * 
 * @author yangrong date: 2015-01-29
 * @version1.3.2
 */
public class DeleteCosmeticBagApi extends RootApiForManage<DeleteCosmeticBagResult, DeleteCosmeticBagInput> {

	public DeleteCosmeticBagResult Process(DeleteCosmeticBagInput inputParam, MDataMap mRequestMap) {

		DeleteCosmeticBagResult result = new DeleteCosmeticBagResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			String cosmetic_code = inputParam.getCosmetic_code();

			int count = DbUp.upTable("nc_cosmetic_bag").delete("cosmetic_code",cosmetic_code);

			if (count <= 0) {

				MDataMap map = DbUp.upTable("nc_cosmetic_bag").one("cosmetic_code", cosmetic_code);
				if (map != null) {

					result.setResultCode(934205145);
					result.setResultMessage(bInfo(934205145));
				} else {

					result.setResultCode(934205146);
					result.setResultMessage(bInfo(934205146));
				}

			}
		}
		return result;
	}

}
