package com.cmall.productcenter.service;

import org.apache.commons.lang.StringUtils;
import com.cmall.productcenter.common.Constants;
import com.cmall.productcenter.common.SkuCommon;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.systemcenter.model.FlowNextOperator;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.cmall.systemcenter.service.FlowService;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;

public class FuncFlowForProductChangeService extends BaseClass implements IFlowFunc {

	public RootResult BeforeFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus, MDataMap mSubMap) {
		// TODO Auto-generated method stub
		return null;
	}

	public RootResult afterFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus, MDataMap mSubMap) {
		RootResult rr = new RootResult();
		if(toStatus.equals("4497153900060006")){
			
			
			
		}else if(toStatus.equals("4497153900060008")){//运营审核通过
			PcProductinfo product = new PcProductinfo();
			MDataMap productData = DbUp.upTable("pc_productflow").oneWhere("", "", "product_code='"+outCode+"' and flow_status in ('10','20')");
			if(productData!=null && !productData.isEmpty()){
				String pValue = productData.get("product_json");
				JsonHelper<PcProductinfo> pHelper=new JsonHelper<PcProductinfo>();
				product = pHelper.StringToObj(pValue, product);
				StringBuffer error = new StringBuffer();
				String flstatus = productData.get("flow_status");
				MDataMap changeStatusMap = new MDataMap();
				changeStatusMap.put("flow_bussinessid",outCode);
				changeStatusMap.put("from_status","4497153900060001");
				changeStatusMap.put("to_status","4497153900060002");
				changeStatusMap.put("flow_type","449715390007");
				MDataMap pone = DbUp.upTable("pc_productinfo").one("product_code",outCode);
				if(SkuCommon.ProAddInit.equals(flstatus)){//新增商品处理
					changeStatusMap.put("remark","新增商品终审通过，待编辑负责人编辑上架");
					productData.put("flow_status", SkuCommon.ProAddOr);
				}else if(SkuCommon.ProUpaInit.equals(flstatus)){//修改商品处理
					productData.put("flow_status", SkuCommon.ProUpaOr);
					changeStatusMap.put("remark","修改商品终审通过，待编辑负责人编辑上架");
				
				}
				MDataMap user = DbUp.upTable("za_userinfo").one("manage_code",pone.get("small_seller_code"),"user_type_did","467721200003","flag_enable","0");
				if(user!=null&&!user.isEmpty()&&("4497153900060002".equals(pone.get("product_status"))||"4497153900060001".equals(pone.get("product_status")))){
					changeStatusMap.put("from_status","4497153900060002");
					changeStatusMap.put("to_status","4497153900060004");
					changeStatusMap.put("remark","商户冻结，商品强制下架");
					if(pone!=null&&!pone.isEmpty()){
						new FlowBussinessService().ChangeFlow(pone.get("uid"), "449715390007", "4497153900060002", "4497153900060004", UserFactory.INSTANCE.create().getUserCode(), "商户冻结，商品强制下架", changeStatusMap);
					}
				}
				DbUp.upTable("pc_productflow").dataUpdate(productData, "flow_status", "uid");
				if(error!=null&&StringUtils.isNotBlank(error.toString())&&Integer.valueOf(error.toString()) != 1){
					//更新草稿箱状态
					MDataMap mDataMap = new MDataMap();
					mDataMap.put("flow_status", flstatus);
					mDataMap.put("product_code", outCode);
					DbUp.upTable("pc_productflow").dataUpdate(mDataMap, "flow_status", "product_code");
					rr.setResultCode(Integer.valueOf(error.toString()));
					rr.setResultMessage(error.toString());
				}
			}
		
		}else if (toStatus.equals("4497153900060007")||toStatus.equals("4497153900060009")) {//未通过情况处理
			
			/*招商经理退回至草稿箱*/
			if (StringUtils.equals(toStatus, Constants.FLOW_STATUS_PCAPPROVE_MD_REJECT)) {
				MDataMap productData = DbUp.upTable("pc_productflow").oneWhere("", "",
						"product_code='" + outCode + "' and flow_status in ('10','20')");
				if (productData != null && !productData.isEmpty()) {

					String flstatus = productData.get("flow_status");
					if (SkuCommon.ProAddInit.equals(flstatus)) {// 新增商品处理
						productData.put("flow_status", SkuCommon.ProAddOrRe);
					} else if (SkuCommon.ProUpaInit.equals(flstatus)) {// 修改商品处理
						productData.put("flow_status", SkuCommon.ProUpaOrRe);
					}
					DbUp.upTable("pc_productflow").dataUpdate(productData, "flow_status", "uid");

					new ProductDraftBoxService().addProductToDraftBoxForReject(productData.get("product_json"));

				}

			}
			
			/*编辑负责人驳回至招商经理*/
			if(StringUtils.equals(toStatus, Constants.FLOW_STATUS_PCAPPROVE_BJF_REJECT)){
				
				doReject(flowCode, toStatus, rr);
				
			}
			
		}
		return rr;
	}
	
	/**
	 * 编辑负责人拒绝
	 * @param flowCode
	 * 		流程编号
	 * @param fromStatus
	 * 		开始状态
	 * @param result
	 */
	public void doReject(String flowCode,String fromStatus,RootResult result){
	
		MDataMap flowMain = DbUp.upTable("sc_flow_main").one("flow_code",flowCode);

		if (flowMain == null) {
				result.setResultCode(949701003);
				result.setResultMessage(bInfo(949701003));
				return;
		} else {
			
			FlowNextOperator fq = new FlowService().getNextAll(flowMain.get("flow_type"), fromStatus);
			
			flowMain.put("next_operators", fq.getNextOperator());
			
			flowMain.put("next_operator_status", fq.getNextOperatorStatus());
			
			flowMain.put("current_status", Constants.FLOW_STATUS_PCAPPROVE_MD);
			
			DbUp.upTable("sc_flow_main").update(flowMain);
			
		}

						
	
		
		
	}
	
	

}
