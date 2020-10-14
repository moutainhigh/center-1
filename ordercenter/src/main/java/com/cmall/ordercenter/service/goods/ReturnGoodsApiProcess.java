package com.cmall.ordercenter.service.goods;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.model.ReturnGoods;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

/**
 * ClassName: ReturnService <br/>
 * date: 2013-9-16 下午8:57:40 <br/>
 * 
 * @author hexd
 * @version
 * @since JDK 1.6
 */
public class ReturnGoodsApiProcess extends
		RootApi<ReturnGoodsApiResult, ReturnGoodsApiInput>
{
	private final static ReturnGoodsApi ps = new ReturnGoodsApi();

	public ReturnGoodsApiResult Process(ReturnGoodsApiInput api, MDataMap mRequestMap)
	{
		ReturnGoodsApiResult aResut = new ReturnGoodsApiResult();
		if (api == null)
		{
			aResut.setResultMessage(bInfo(939301017));
			aResut.setResultCode(939301017);
		} 
		if (StringUtils.isBlank(api.getBuyer_code()))
		{
			aResut.setResultMessage(bInfo(939301035));
			aResut.setResultCode(939301035);
		} 
		else
		{
			List<ReturnGoods> goods = ps.getReturnGoods(api.getBuyer_code());
			
			aResut.setGoods(goods);
			if (null == goods)
			{
				aResut.setResultMessage(bInfo(939301034));
				aResut.setResultCode(939301034);
			}
		}
		return aResut;
	}
}
