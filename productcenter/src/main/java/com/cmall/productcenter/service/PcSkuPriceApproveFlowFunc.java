package com.cmall.productcenter.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.common.Constants;
import com.cmall.systemcenter.model.FlowNextOperator;
import com.cmall.systemcenter.service.FlowService;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 3.9.4(包含)之前的流程都是在惠家有后台操作,其流程为:商户商品(查看) --> 商户价格审核(财务);<br/>
 * 3.9.4之后的流程中"发起价格审批流程"是在商户后台完成的,其流程为:商户价格修改 --> 商品价格审批(运营) --> 商户价格审核(财务);<br/><br/>
 * 以上流程(菜单)自行查询;由于中间加入一个"商品价格审批(运营)"导致当前系统与ERP中的状态权限变得难易控制,所以现在 <Strong><bold>再次</bold></Strong> 手动更新流程中(sc_flow_main表)的状态改变以及日志的更新;<br/><br/>
 * 为什么再次更新呢？因为在之前的流程中 ({@link com.cmall.systemcenter.service.FlowService}: changeFlow方法)调用存储过程已经更新过一次;<br/><br/>
 * @author update LHY 2016年4月8日 下午5:08:58
 * @see com.cmall.systemcenter.service.FlowService
 */
public class PcSkuPriceApproveFlowFunc implements IFlowFunc {

	public RootResult BeforeFlowChange(String flowCode, String outCode, String fromStatus, String toStatus,
			MDataMap mSubMap) {
		// TODO Auto-generated method stub
		return null;
	}

	public RootResult afterFlowChange(String flowCode, String outCode, String fromStatus, String toStatus,
			MDataMap mSubMap) {
		
		RootResult rootResult = new RootResult();
		//待运营审核
		if(StringUtils.equals(toStatus, Constants.FLOW_STATUS_SKUPRICE_CW)){
			updateFlowStatusAndHistory(outCode, flowCode, fromStatus, toStatus, mSubMap);
		}
		
		/*审核完成*/
		if(StringUtils.equals(toStatus, Constants.FLOW_STATUS_SKUPRICE_FINISH)){
			doFinish(flowCode, outCode, fromStatus, toStatus, mSubMap);
		}
		
		/*审核拒绝*/
		if(Constants.FLOW_STATUS_SKUPRICE_REJECT.equals(toStatus)||Constants.FLOW_STATUS_SKUPRICE_YY_5.equals(toStatus)) {
			if(StringUtils.equals(toStatus, Constants.FLOW_STATUS_SKUPRICE_REJECT)){
				this.updateFlowStatusAndHistory(flowCode, fromStatus, toStatus);
				this.updateSkupriceChangeFlow(flowCode);
			} else if(StringUtils.equals(toStatus, Constants.FLOW_STATUS_SKUPRICE_YY_5)) {
				doReject(flowCode, outCode, fromStatus, toStatus, mSubMap);
			}
			
		}
		
		return rootResult;
	}
	
	/**
	 * 新增功能，当财务驳回时，由于原先的驳回状态无法满足当前功能，所以使用该方法进行转换
	 * @param flowCode
	 * @param fromStatus
	 * @param toStatus
	 */
	public void updateFlowStatusAndHistory(String flowCode, String fromStatus, String toStatus) {
		MDataMap flowMain = DbUp.upTable("sc_flow_main").one("flow_code",flowCode);
		
		FlowNextOperator fq = new FlowService().getNextAll(flowMain.get("flow_type"), Constants.FLOW_STATUS_SKUPRICE_YY);
		
		flowMain.put("flow_isend", "0");
		
		flowMain.put("next_operators", fq.getNextOperator());
		
		flowMain.put("next_operator_status", fq.getNextOperatorStatus());
		
		flowMain.put("current_status", Constants.FLOW_STATUS_SKUPRICE_YY);
		
		DbUp.upTable("sc_flow_main").update(flowMain);
	}
	
	public void updateSkupriceChangeFlow(String flowCode) {
		MDataMap flowMain = DbUp.upTable("pc_skuprice_change_flow").one("flow_code",flowCode);
		flowMain.put("status", Constants.FLOW_STATUS_SKUPRICE_YY);
//		DbUp.upTable("pc_skuprice_change_flow").update(flowMain);
		DbUp.upTable("pc_skuprice_change_flow").dataUpdate(flowMain, "status", "flow_code");
	}
	
	/**
	 * 成本价未发生变化,只修改了销售价
	 * @param flowCode
	 * @param fromStatus
	 */
	public void updateFlowStatusAndHistory(String outCode, String flowCode, String fromStatus, String toStatus, MDataMap mSubMap) {
		String sql = "select cost_price_old, cost_price from pc_skuprice_change_flow pc where pc.flow_code=:flow_code and status=:status";
		MDataMap maDataMap = new MDataMap();
		maDataMap.put("flow_code", flowCode);
		maDataMap.put("status", fromStatus);
		List<Map<String, Object>> list = DbUp.upTable("pc_skuprice_change_flow").dataSqlList(sql, maDataMap);
		boolean flag = true;
		if(list!=null && list.size()>0) {
			for(Map<String, Object> map: list) {
				String cost_price_old = String.valueOf(map.get("cost_price_old"));
				String cost_price = String.valueOf(map.get("cost_price"));
				BigDecimal bd1 = new BigDecimal(cost_price_old);
				BigDecimal bd2 = new BigDecimal(cost_price);
				if(bd1.compareTo(bd2)!=0) {//修改成本价
					flag = false;
					break;
				}
			}
		}
		if(flag) {//成本价未发生变化,只修改了销售价直接结束流程
			MDataMap flowMain = DbUp.upTable("sc_flow_main").one("flow_code",flowCode);
			flowMain.put("flow_isend", "1");
			flowMain.put("next_operators", "");
			flowMain.put("next_operator_status", "");
			flowMain.put("current_status", Constants.FLOW_STATUS_SKUPRICE_FINISH);
			DbUp.upTable("sc_flow_main").update(flowMain);
			String sql_history = "select uid from sc_flow_history sc where sc.flow_code=:flow_code order by create_time desc";
			MDataMap mWhereMap_history = new MDataMap();
			mWhereMap_history.put("flow_code", flowCode);
			Map<String, Object> map = DbUp.upTable("sc_flow_history").dataSqlOne(sql_history, mWhereMap_history);
			
			if(map!=null && map.size()>0) {
				MDataMap updateDataMap = new MDataMap();
				updateDataMap.put("uid", String.valueOf(map.get("uid")));
				updateDataMap.put("current_status", Constants.FLOW_STATUS_SKUPRICE_FINISH);
				String sSql = "update sc_flow_history set current_status=:current_status where uid=:uid";;
				DbUp.upTable("sc_flow_history").dataExec(sSql, updateDataMap);
			}
			doFinish(flowCode, outCode, fromStatus, Constants.FLOW_STATUS_SKUPRICE_FINISH, mSubMap);
		} else {
			this.doReject(flowCode, outCode, fromStatus, toStatus, mSubMap);
		}
	}
	
	/**
	 * 当流程完成时更新价格信息
	 * @param flowCode
	 * 		流程编号
	 * @param outCode
	 * 		产品编号
	 * @param fromStatus
	 * 		起点状态
	 * @param toStatus
	 * 		终点状态
	 * @param mSubMap
	 * 		参数集合
	 */
	public void doFinish(String flowCode, String outCode, String fromStatus, String toStatus,MDataMap mSubMap){
		
		ProductSkuPriceService skuPriceService = new ProductSkuPriceService();		
		/*更新sku价格*/
		skuPriceService.updateSkuPriceAndStatus(flowCode, outCode, fromStatus, toStatus);
		
	}
	
	/**
	 * 当流程拒绝时更新过程信息状态
	 * @param flowCode
	 * 		流程编号
	 * @param outCode
	 * 		产品编号
	 * @param fromStatus
	 * 		起点状态
	 * @param toStatus
	 * 		终点状态
	 * @param mSubMap
	 * 		参数集合
	 */
	public void doReject(String flowCode, String outCode, String fromStatus, String toStatus,MDataMap mSubMap){
		/*更新过程表状态*/
		new ProductSkuPriceService().updateStatus(flowCode, outCode, fromStatus, toStatus);
		
	}
}
