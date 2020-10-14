package com.cmall.groupcenter.account.api;

import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.service.OrderShoppingService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForMember;
import com.srnpr.zapweb.webapi.RootResultWeb;
/***
 * 校验库存和商品状态
 * @author xiegj
 *
 */
public class ApiForCheckStockAndStatus extends RootApiForMember<ApiForCheckStockAndStatusResult,ApiForCheckStockAndStatusInput> {

	public ApiForCheckStockAndStatusResult Process(ApiForCheckStockAndStatusInput inputParam, MDataMap mRequestMap) {
		ApiForCheckStockAndStatusResult result=new ApiForCheckStockAndStatusResult();
		OrderShoppingService service = new OrderShoppingService();
		String buyerCode = "";
		if(getFlagLogin()){
			buyerCode = getOauthInfo().getUserCode();
		}
		if(inputParam.getMap()!=null&&!inputParam.getMap().isEmpty()){
			Map<String, Object> rr = service.checkGoodsStockAndStatus(getManageCode(),buyerCode,inputParam.getMap());
			RootResultWeb resultWeb = new RootResultWeb();
			if(rr.containsKey("error")){
				 resultWeb = (RootResultWeb)rr.get("error");
				 result.setResultCode(resultWeb.getResultCode());
				 result.setResultMessage(resultWeb.getResultMessage());
				 return result;
			}
			if(!resultWeb.upFlagTrue()){
				result.setResultCode(resultWeb.getResultCode());
				result.setResultMessage(resultWeb.getResultMessage());
			}else {
				result.setList((List<MDataMap>)rr.get("list"));
			}
			if(rr.containsKey("flag")){
				result.setFlag(rr.get("flag").toString());
			}
		}
		return result;
	}
}
