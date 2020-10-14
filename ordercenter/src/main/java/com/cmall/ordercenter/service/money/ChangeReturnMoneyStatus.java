/**
 * Project Name:ordercenter
 * File Name:ChangeReturnMoneyStatus.java
 * Package Name:com.cmall.ordercenter.service.money
 * Date:2013年10月10日下午3:57:02
 *
*/

package com.cmall.ordercenter.service.money;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.ReturnMoney;
import com.cmall.ordercenter.model.ReturnMoneyLog;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.xmaspay.process.refund.PayGateRefundReqProcess;
import com.srnpr.xmaspay.util.PayServiceFactory;
import com.srnpr.xmassystem.enumer.HjyBeanExecType;
import com.srnpr.xmassystem.service.HjybeanService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webmodel.MMessage;
import com.srnpr.zapweb.websupport.MessageSupport;

/**
 * ClassName:ChangeReturnMoneyStatus <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2013年10月10日 下午3:57:02 <br/>
 * @author   hxd
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class ChangeReturnMoneyStatus extends BaseClass implements IFlowFunc
{

	public RootResult BeforeFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus,MDataMap mSubMap)
	{
		RootResult rootResult = new RootResult();
		// 进入退款中的状态需要调用网关的退款接口
		if("4497153900040003".equals(fromStatus) && "4497153900040006".equals(toStatus)){
			MDataMap returnMoney = DbUp.upTable("oc_return_money").one("uid", flowCode);
			
			PayGateRefundReqProcess process = PayServiceFactory.getInstance().getBean(PayGateRefundReqProcess.class);
			PayGateRefundReqProcess.PaymentInput input = new PayGateRefundReqProcess.PaymentInput();
			input.returnMoneyCode = returnMoney.get("return_money_code");
			PayGateRefundReqProcess.PaymentResult result = process.process(input);
			
			if(!result.upFlagTrue()){
				rootResult.setResultCode(result.getResultCode());
				rootResult.setResultMessage(result.getResultMessage());
			}
		}
		return rootResult;
	}
	/**
	 * TODO 更新退款日志状态.
	 */
	public RootResult afterFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus,MDataMap mSubMap)
	{
		//状态变更后 记录日志 
		String poundage = mSubMap.get("poundage");
		String return_money = mSubMap.get("return_money");
		
		MUserInfo userInfo = UserFactory.INSTANCE.create();
		String userCode = StringUtils.trimToEmpty(mSubMap.get("userCodex"));
		String loginName = userCode;
		if(loginName == null){
			loginName = userInfo.getLoginName();
			userCode = userInfo.getUserCode();
		}
		
		RootResult result = new RootResult();
		CreateMoneyService service = new CreateMoneyService();
		ReturnMoney money = service.getReturnMoneyCodeByUid(flowCode);
		ReturnMoneyLog log = new ReturnMoneyLog();
		log.setReturn_money_no(money.getReturn_money_code());
		log.setCreate_time(DateUtil.getNowTime());
		log.setStatus(toStatus);
		log.setCreate_user(loginName);
		log.setInfo(mSubMap.get("remark"));
		try
		{
			service.createRetuMoneyLog(log);
		} catch (Exception e)
		{
			//bLogError(iInfoCode, sParms);
			e.printStackTrace();
		}
		
		MDataMap returnMoney=DbUp.upTable("oc_return_money").one("uid",flowCode);
		String return_goods_code=returnMoney.get("return_goods_code");
		if(StringUtils.isNotBlank(return_goods_code)){

			String now=DateUtil.getSysDateTimeString();
			
			DbUp.upTable("oc_order_after_sale").dataUpdate(new MDataMap("asale_code",return_goods_code,"asale_status","4497477800050002","update_time",now), "", "asale_code"); 
			
			MDataMap loasMap=new MDataMap();
			loasMap.put("asale_code", return_goods_code);
			loasMap.put("create_user", userCode);
			loasMap.put("create_time", now);
			loasMap.put("asale_status", "4497477800050002");
			loasMap.put("remark", "[财务退款]"+mSubMap.get("remark"));
			loasMap.put("lac_code", WebHelper.upCode("LAC"));
			DbUp.upTable("lc_order_after_sale").dataInsert(loasMap);
			
			
			MDataMap lsasMap=new MDataMap();
			lsasMap.put("asale_code", return_goods_code);
			lsasMap.put("lac_code", loasMap.get("lac_code"));
			lsasMap.put("create_source", "4497477800070001");
			lsasMap.put("create_time", now);
			
			
			MDataMap returnGoods=DbUp.upTable("oc_return_goods").one("return_code",return_goods_code);
			MDataMap templateMap=DbUp.upTable("oc_order_after_sale_template").one("template_code","OST160312100012");
			lsasMap.put("serial_msg", FormatHelper.formatString(templateMap.get("template_context"),returnGoods.get("expected_return_money"),returnGoods.get("expected_return_money"),returnGoods.get("expected_return_group_money"),"0.00","0"));
			lsasMap.put("serial_title", templateMap.get("template_title"));
			lsasMap.put("template_code", templateMap.get("template_code"));
			DbUp.upTable("lc_serial_after_sale").dataInsert(lsasMap);
			
			MMessage messages = new MMessage();
			messages.setMessageContent(bConfig("ordercenter.ChangeReturnMoney_msm"));
			messages.setMessageReceive(returnMoney.get("mobile"));
			messages.setSendSource("4497467200020006");
			MessageSupport.INSTANCE.sendMessage(messages);
			//消息推送表数据插入NG++ 20190620
			String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
			String message = "您的退款已退到您的支付账户，请注意查收";
			MDataMap saleMap = DbUp.upTable("oc_order_after_sale").one("asale_code",return_goods_code);
			DbUp.upTable("nc_aftersale_push_news").dataInsert(new MDataMap("uid",uuid,"member_code",saleMap.get("buyer_code"),"title",templateMap.get("template_title"),"message",message,"create_time",DateUtil.getSysDateTimeString(),"push_times","0","checker",loginName,"after_sale_code",return_goods_code,"after_sale_status","4497477800050005","to_page","13","if_read","0"));
			
		}
		
		
		//调整退款单价格
		if("4497153900040003".equals(toStatus) && "4497153900040005".equals(fromStatus))
		{
			MDataMap mp = new MDataMap();
			mp.put("uid", mSubMap.get("flow_bussinessid"));
			mp.put("return_money", mSubMap.get("return_money"));
			DbUp.upTable("oc_return_money").dataUpdate(mp, "return_money", "uid");
		}
		
		
		
		//客服调整手续费
		if("4497153900040004".equals(toStatus) && "4497153900040003".equals(fromStatus))
		{
			MDataMap mp = new MDataMap();
			mp.put("uid", mSubMap.get("flow_bussinessid"));
			mp.put("poundage", poundage);
			mp.put("return_money", return_money);
			DbUp.upTable("oc_return_money").dataUpdate(mp, "poundage,return_money", "uid");
		}
		
		
		//正式退款
		//toStatus  4497153900040001     fromStatus   4497153900040004
		if("4497153900040001".equals(toStatus)  && "4497153900040004".equals(fromStatus)) 
		{
			MDataMap mp = new MDataMap();
			mp.put("uid", mSubMap.get("flow_bussinessid"));
			mp.put("poundage", poundage);
			mp.put("return_money", return_money);
			DbUp.upTable("oc_return_money").dataUpdate(mp, "poundage,return_money", "uid");
			result = service.returnMoney(flowCode,log,poundage);
		}
		
		//正式退款
		if("4497153900040001".equals(toStatus)) 
		{
			// 重置退款失败状态
			returnMoney.put("refund_flag", "");
			DbUp.upTable("oc_return_money").dataUpdate(returnMoney, "refund_flag", "zid");
		}
		
		// 订单退款确认时返还使用的惠豆
		if("4497153900040001".equals(toStatus)){
			HjybeanService.addHjyBeanTimer(HjyBeanExecType.RETURN_MONEY, returnMoney.get("return_money_code"),returnMoney.get("order_code"));
		}
		return result;
	}
	/**
	 * 判断订单是否已发货
	 * @return
	 */
	public boolean validateOrderStatus(ReturnMoney money)
	{
		int count = DbUp.upTable("lc_orderstatus").count("code",money.getOrder_code(),"now_status","4497153900010003");
		if(count>0)
			return true;
		else
			return false;
	}
}

