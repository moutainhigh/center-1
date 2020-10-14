/**
 * Project Name:ordercenter
 * File Name:ChangeGoodsStatus.java
 * Package Name:com.cmall.ordercenter.service.goods
 * Date:2013年10月10日下午3:56:33
 *
*/

package com.cmall.ordercenter.service.goods;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.helper.OrderHelper;
import com.cmall.ordercenter.model.ReturnGoods;
import com.cmall.ordercenter.model.ReturnGoodsLog;
import com.cmall.ordercenter.model.api.ApiOrderStatusChangeNoticInput;
import com.cmall.ordercenter.service.api.ApiOrderStatusChangeNotic;
import com.cmall.ordercenter.service.money.CreateMoneyService;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * ClassName:ChangeGoodsStatus <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2013年10月10日 下午3:56:33 <br/>
 * @author   hxd
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class ChangeReturnGoodsStatus extends BaseClass implements IFlowFunc 
{
	public RootResult BeforeFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus,MDataMap mSubMap)
	{
		
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * TODO 更新退货日志状态.
	 * @see com.cmall.systemcenter.systemface.IFlowFunc#afterFlowChange(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public RootResult afterFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus,MDataMap mSubMap)
	{
		RootResult result = new RootResult();
		ReturnGoodsApi api = new ReturnGoodsApi();
		ReturnGoods goods = api.getReturnGoodsCodeByUid(flowCode);
		ReturnGoodsLog log = new ReturnGoodsLog();
		log.setReturn_no(goods.getReturn_code());
		log.setCreate_time(DateUtil.getNowTime());
		log.setStatus(toStatus);
		log.setCreate_user(UserFactory.INSTANCE.create().getUserCode());
		log.setInfo(mSubMap.get("remark"));
		try
		{
			api.insertReturnGoodsLog(log);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		//创建退款单  4497153900040005      4497153900050001
		if("4497153900050001".equals(toStatus))
		{
			try
			{
//				result = new CreateMoneyService().creatReturnMoneyList(goods);
				result = new CreateMoneyService().creatReturnMoneyListNew(goods);
			} catch (Exception e)
			{
				e.printStackTrace();
				result.setResultCode(939301065);
				result.setResultMessage(bInfo(939301065));
				e.printStackTrace();
				return result;
			}
			
			//新增逻辑
			String order_code=goods.getOrder_code();
			String goods_receipt=goods.getGoods_receipt();
			int allNum=0;
			int retNum=0;
			Map<String, Object> detailMap = DbUp.upTable("oc_orderdetail").dataSqlOne("select sum(sku_num) as sku_num from oc_orderdetail where order_code=:order_code", new MDataMap("order_code",order_code));
			if(detailMap!=null&&!detailMap.isEmpty()){
				allNum=(Integer)detailMap.get("sku_num");
			}
			
			Map<String, Object> retMap = DbUp.upTable("oc_return_goods").dataSqlOne("SELECT SUM(d.count) as sku_num from oc_return_goods g RIGHT JOIN  oc_return_goods_detail d on g.return_code=d.return_code where g.`status` in ('4497153900050001','4497153900050003') and g.order_code=:order_code", new MDataMap("order_code",order_code));
			if(retMap!=null&&!retMap.isEmpty()){
				retNum=(Integer)retMap.get("sku_num");
			}
			
			if(retNum>=allNum){//全部退货
				String order_status="4497153900010005";
				if(StringUtils.equals(goods_receipt, "4497476900040002")){
					order_status="4497153900010006";
				}
				
				MDataMap orderInfo=DbUp.upTable("oc_orderinfo").one("order_code",order_code);
				DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_code",order_code,"order_status_ext","4497153900140004","order_status",order_status), "order_status_ext,order_status", "order_code");
				if(!StringUtils.equals(order_status, orderInfo.get("order_status"))){
					DbUp.upTable("lc_orderstatus").dataInsert(new MDataMap("code",order_code,"create_time",DateUtil.getSysDateTimeString(),"create_user","system","old_status",orderInfo.get("order_status"),"now_status",order_status,"info","ChangeReturnGoodsStatus"));
					//订单状态变更 调用多彩宝订单状态变更通知接口 -- rhb
					MDataMap dm = DbUp.upTable("oc_orderinfo").one("order_code", order_code);
					if(dm.containsKey("order_source") && "449715190014".equals(dm.get("order_source"))){
						ApiOrderStatusChangeNoticInput inputParam = new ApiOrderStatusChangeNoticInput();
						inputParam.setJyOrderCode(order_code);
						inputParam.setStatus(OrderHelper.convertStatusCode(toStatus));
						inputParam.setStatusCode(order_status);
						inputParam.setUpdateTime(DateUtil.getSysDateTimeString());
						//添加物流信息
						MDataMap oneWhere = DbUp.upTable("oc_order_shipments").oneWhere("logisticse_name,logisticse_code,waybill", "", "", "order_code", inputParam.getJyOrderCode());
						if(null != oneWhere){
							inputParam.setLogisticseName(oneWhere.get("logisticse_name"));
							inputParam.setLogisticseCode(oneWhere.get("logisticse_code"));
							inputParam.setWaybill(oneWhere.get("waybill"));
						}
						new ApiOrderStatusChangeNotic().Process(inputParam, new MDataMap());
					}
				}
			}else{
				DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_code",order_code,"order_status_ext","4497153900140003"), "order_status_ext", "order_code");
			}
		}
		
		return result;
	}

}

