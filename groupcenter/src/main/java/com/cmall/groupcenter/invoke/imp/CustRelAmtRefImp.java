package com.cmall.groupcenter.invoke.imp;

import java.math.BigDecimal;

import com.cmall.groupcenter.homehas.RsyncCtrlAccmCrdtPpcServer;
import com.cmall.groupcenter.homehas.RsyncGetCustExpireAccm;
import com.cmall.groupcenter.homehas.RsyncGetCustRelAmt;
import com.cmall.groupcenter.homehas.model.RsyncModelCustRelAmt;
import com.cmall.groupcenter.homehas.model.RsyncRequestCtrlAccmCrdtPpcServer;
import com.cmall.groupcenter.homehas.model.RsyncResponseCtrlAccmCrdtPpcServer;
import com.srnpr.xmassystem.invoke.ref.CustRelAmtRef;
import com.srnpr.xmassystem.invoke.ref.model.GetCustAmtResult;
import com.srnpr.xmassystem.invoke.ref.model.UpdateCustAmtInput;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * 查询用户的积分、储值金、暂存款、惠币
 */
public class CustRelAmtRefImp implements CustRelAmtRef{

	@Override
	public GetCustAmtResult getCustAmt(String custId) {
		RsyncGetCustRelAmt rsync = new RsyncGetCustRelAmt();
		rsync.upRsyncRequest().setCust_id(custId);
		if(!rsync.doRsync()) return null;
		
		RsyncModelCustRelAmt amt = rsync.upProcessResult().getResult();
		if(amt == null) return null;
		
		RsyncGetCustExpireAccm rsync1 = new RsyncGetCustExpireAccm();
		rsync1.upRsyncRequest().setCust_id(custId);
		if(!rsync1.doRsync()) return null;
		
		BigDecimal expireAccm = new BigDecimal(rsync1.upProcessResult().getResult());
		
		GetCustAmtResult model = new GetCustAmtResult();
		model.setPossAccmAmt(amt.getPoss_accm_amt());
		model.setPossCrdtAmt(amt.getPoss_crdt_amt());
		model.setPossPpcAmt(amt.getPoss_ppc_amt());
		model.setPossHcoinAmt(amt.getPoss_hcoin_amt());
		model.setPreHcoinAmt(amt.getPre_hcoin_amt());
		model.setExpireAccm(expireAccm);
		return model;
	}

	@Override
	public RootResult updateCustAmt(UpdateCustAmtInput input) {
		RootResult result = new RootResult();
		
		RsyncCtrlAccmCrdtPpcServer rsync = new RsyncCtrlAccmCrdtPpcServer();
		RsyncRequestCtrlAccmCrdtPpcServer req = rsync.upRsyncRequest();
		req.setSubsystem("app");
		req.setApp_ord_id(input.getBigOrderCode());
		req.setCust_id(input.getCustId());
		req.setCrud_flag(input.getCurdFlag().toString());
		req.setHcoin_stat_cd(input.getHcoinStatCd());
		RsyncRequestCtrlAccmCrdtPpcServer.ChildOrder subOrder = null;
		for(UpdateCustAmtInput.ChildOrder childOrder : input.getOrderList()){
			req.setAccm_amt(req.getAccm_amt().add(childOrder.getChildAccmAmt()));
			req.setCrdt_amt(req.getCrdt_amt().add(childOrder.getChildCrdtAmt()));
			req.setPpc_amt(req.getPpc_amt().add(childOrder.getChildPpcAmt()));
			// 566添加惠币
			req.setHcoin_amt(req.getHcoin_amt().add(childOrder.getChildHcoinAmt()));
			
			subOrder = new RsyncRequestCtrlAccmCrdtPpcServer.ChildOrder();
			subOrder.setApp_child_ord_id(childOrder.getAppChildOrdId());
			subOrder.setChild_accm_amt(childOrder.getChildAccmAmt());
			subOrder.setChild_crdt_amt(childOrder.getChildCrdtAmt());
			subOrder.setChild_ppc_amt(childOrder.getChildPpcAmt());
			subOrder.setChild_hcoin_amt(childOrder.getChildHcoinAmt());
			req.getOrders().add(subOrder);
		}
		
		if(req.getAccm_amt().compareTo(BigDecimal.ZERO) == 0
				&& req.getCrdt_amt().compareTo(BigDecimal.ZERO) == 0 
				&& req.getPpc_amt().compareTo(BigDecimal.ZERO) == 0
				&& req.getHcoin_amt().compareTo(BigDecimal.ZERO) == 0
				&&!"TXHB".equals(input.getCurdFlag().toString())
				&&!"ZZHB".equals(input.getCurdFlag().toString())){  //排除预估转正,提现惠币的情况
			result.setResultCode(99);
			result.setResultMessage("变更金额不能都为0");
			return result;
		}
		
		rsync.doRsync();
		
		RsyncResponseCtrlAccmCrdtPpcServer resp = rsync.upProcessResult();
		if(resp == null){
			result.setResultCode(0);
			result.setResultMessage("接口调用异常: CustRelAmtRefImp.updateCustAmt");
			return result;
		}
		
		if(!resp.getSuccess()){
			result.setResultCode(99);
			result.setResultMessage(""+resp.getMessage());
			return result;
		}
		
		return result;
	}
	
}
