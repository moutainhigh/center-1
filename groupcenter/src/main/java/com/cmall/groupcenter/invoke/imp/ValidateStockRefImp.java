package com.cmall.groupcenter.invoke.imp;

import com.cmall.groupcenter.homehas.RsyncValidateStock;
import com.cmall.groupcenter.homehas.model.RsyncRequestValidateStock;
import com.cmall.groupcenter.homehas.model.RsyncResponseValidateStock;
import com.srnpr.xmassystem.invoke.ref.ValidateStockRef;
import com.srnpr.xmassystem.invoke.ref.model.ValidateStockInput;
import com.srnpr.xmassystem.invoke.ref.model.ValidateStockResult;

public class ValidateStockRefImp implements ValidateStockRef {

	@Override
	public ValidateStockResult stockCheck(ValidateStockInput input) {
		RsyncValidateStock rsync = new RsyncValidateStock();
		
		RsyncRequestValidateStock req = rsync.upRsyncRequest();
		req.setLaddr(input.getLaddr());
		req.setMaddr(input.getMaddr());
		req.setSaddr(input.getSaddr());
		req.setSend_addr(input.getSend_addr());
		req.setSrgn_cd(input.getSrgn_cd());
		req.setZip_no(input.getZip_no());
		req.setPay_type(input.getPay_type());
		
		RsyncRequestValidateStock.GoodInfo gi;
		for(ValidateStockInput.GoodInfo info : input.getGood_info()) {
			gi = new RsyncRequestValidateStock.GoodInfo();
			gi.setGood_id(info.getGood_id());
			gi.setColor_id(info.getColor_id());
			gi.setStyle_id(info.getStyle_id());
			gi.setGood_cnt(info.getGood_cnt());
			req.getGood_info().add(gi);
		}
		
		ValidateStockResult result = new ValidateStockResult();
		if(!rsync.doRsync()){
			result.setSuccess(false);
			result.setMessage("接口调用失败，请排查日志!");
			return result;
		}
		
		RsyncResponseValidateStock resp = rsync.upProcessResult();
		
		result.setSuccess(resp.isSuccess());
		result.setIs_ok(resp.getIs_ok());
		result.setMessage(resp.getMessage());
		result.setMax_cnt(resp.getMax_cnt());
		result.setSite_no(resp.getSite_no());
		
		return result;
	}

}
