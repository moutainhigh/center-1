package com.cmall.ordercenter.service.goods;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

/**
 * ClassName:CreateReturnMoneyProcess <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2013-9-17 下午8:34:45 <br/>
 * @author   hexd
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class CreateReturnGoodsProcess extends RootApi<CreateReturnGoodsResult, CreateReturnGoodsInput>
{
	private final static ReturnGoodsApi ps = new ReturnGoodsApi();
	public CreateReturnGoodsResult Process(CreateReturnGoodsInput inputParam, MDataMap mRequestMap)
	{
		CreateReturnGoodsResult result = new CreateReturnGoodsResult();
		if (inputParam == null)
		{
			result.setResultMessage(bInfo(939301017));
			result.setResultCode(939301017);
			return result;
		} 
		//{"version":1,"detailList":[{"count":"1","serial_number":"101913"}],"order_code":"DD140212100027","return_reason":"xxxxxxxxxxxx","description":"","pic_url":"","contacts":"","transport_money":0,"mobile":"","address":""}		
		
		return ps.createRetuGoodList(inputParam);
	}
}

