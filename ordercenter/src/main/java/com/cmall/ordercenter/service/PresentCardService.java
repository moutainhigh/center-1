package com.cmall.ordercenter.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.cmall.ordercenter.common.OrderConst;
import com.cmall.ordercenter.model.CardInfoModel;
import com.cmall.ordercenter.model.CardRequestObject;
import com.cmall.ordercenter.model.OcOrderPay;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.ResponseObject;
import com.cmall.systemcenter.service.UsePresentCardService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.topapi.RootResult;

public class PresentCardService extends BaseClass{
	
	/**
	 * 使用礼品卡
	 * @param order 订单
	 * @param flag 	0 为使用，1 为取消
	 * @return
	 */
	public RootResult usePresentCard(Order order,int flag){
		RootResult error =  new RootResult();
		
		String presentCards = "";
		String passwords = "";
		String usePrices = "";
		String jsonData ="{'list':[{'cardCode':'3120760292763797'}]}";
		List<OcOrderPay> list = order.getOcOrderPayList();
		
		
		if(list == null || list.size() == 0)
			return error;
			
		
		CardRequestObject cro = new CardRequestObject();
		for(OcOrderPay pay : list){
			cro.setChangeCode(order.getOrderCode());
			cro.setChangeRemark("");
			cro.setCreator("system");
			
			if(pay.getPayType().equals(OrderConst.PRESENTCARDTYPE)){
				CardInfoModel cim = new CardInfoModel();
				cim.setCardCode(pay.getPaySequenceid());
				cim.setCardPass(pay.getPassWord());
				if(flag == 0)
					cim.setCardMoney(pay.getPayedMoney());
				else
					cim.setCardMoney(-pay.getPayedMoney());
				
				cro.list.add(cim);
			}
		}
		
		//使用礼品卡
		if(cro.list.size()>0){
			
			JsonHelper<CardRequestObject> pHelper=new JsonHelper<CardRequestObject>();
			String jsonStr = pHelper.ObjToString(cro);
			
			UsePresentCardService upcs = new UsePresentCardService();
			
			String timestp = String.valueOf(System.currentTimeMillis());
			
			timestp = timestp.substring(0, 10);
			
			try {
				String result = "";
				if(flag == 0)
					result = upcs.userPresentCard(jsonStr, timestp);
				else
					result = upcs.cancelPresentCard(jsonStr, timestp);
				
				if(result == null){
					error.setResultCode(939301091);
					error.setResultMessage(bInfo(939301091));
					return error;
				}
				
				
				JsonHelper<ResponseObject> rquestObj=new JsonHelper<ResponseObject>();
				
				ResponseObject ro = new ResponseObject();
				
				ro = rquestObj.StringToObj(result, ro);
				
				if(ro.getResult() != ResponseObject.RESULT_SUCESS){
					error.setResultCode(ro.getResult());
					error.setResultMessage(ro.getMessage());
				}
				
			} catch (Exception e) {
				
				e.printStackTrace();
				error.setResultCode(939301047);
				error.setResultMessage(bInfo(939301047, order.getOrderCode()+":"+e.getMessage()));
				
			}
			
		}
		return error;
	}

}
