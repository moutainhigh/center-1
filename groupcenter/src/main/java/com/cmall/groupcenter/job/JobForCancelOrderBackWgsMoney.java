package com.cmall.groupcenter.job;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.service.GroupPayService;
import com.cmall.groupcenter.third.model.GroupRefundInput;
import com.cmall.groupcenter.third.model.GroupRefundResult;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.OcOrderPay;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.service.OrderService;
import com.cmall.systemcenter.util.StringUtility;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapweb.rootweb.RootJobForExec;
import com.srnpr.zapweb.webmodel.ConfigJobExec;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 取消订单返还微公社余额
 * @author xiegj
 *
 */
public class JobForCancelOrderBackWgsMoney extends RootJobForExec {

	@Override
	public IBaseResult execByInfo(String orderCode) {
		
		MWebResult mWebResult = new MWebResult();
		
		
		//查询订单相关信息
		OrderService service = new OrderService();
		
		Order order = service.getOrder(orderCode);
		if(order!=null&&order.getOcOrderPayList()!=null&&!order.getOcOrderPayList().isEmpty()){
			for(int k = 0; k < order.getOcOrderPayList().size(); k++){
				OcOrderPay pay = order.getOcOrderPayList().get(k);
				if("449746280009".equals(pay.getPayType())&&StringUtility.isNotNull(pay.getPayRemark())
						&&!"0".equals(pay.getPayRemark())&&Double.valueOf(pay.getPayRemark())>0){
					GroupRefundInput gri = new GroupRefundInput();
					gri.setMemberCode(order.getBuyerCode());
					gri.setOrderCode(order.getOrderCode());
					gri.setRefundMoney(pay.getPayRemark());
					gri.setRefundTime(DateUtil.getSysDateTimeString());
					gri.setTradeCode(pay.getPaySequenceid());
					GroupRefundResult grr = new GroupPayService().groupRefund(gri, order.getSellerCode());
					if(!grr.upFlagTrue()){
						mWebResult.inErrorMessage(grr.getResultCode());
//						String receives[]= bConfig("groupcenter.wgs_sendMail_receives").split(",");
//						String title= bConfig("groupcenter.wgs_title");
//						String content= bConfig("groupcenter.wgs_content");
//						for (String receive : receives) {
//							if(StringUtils.isNotBlank(receive)){
//								MailSupport.INSTANCE.sendMail(receive, title, FormatHelper.formatString(content,orderCode));
//							}
//						}
						break;
					}
				}
			}
		}
		
		return mWebResult;
	}

	private static ConfigJobExec config = new ConfigJobExec();
	static {
		config.setExecType("449746990004");
	}
	
	@Override
	public ConfigJobExec getConfig() {
		return config;
	}

}
