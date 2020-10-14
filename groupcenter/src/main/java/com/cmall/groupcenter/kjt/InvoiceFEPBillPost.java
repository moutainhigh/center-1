package com.cmall.groupcenter.kjt;

import java.math.BigDecimal;
import java.util.List;

import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.groupcenter.kjt.config.RsyncConfigInvoiceFEPBillPost;
import com.cmall.groupcenter.kjt.request.RsyncRequestInvoiceFEPBillPost;
import com.cmall.groupcenter.kjt.response.RsyncResponseInvoiceFEPBillPost;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 待购汇账单接收
 * @author jlin
 *
 */
public class InvoiceFEPBillPost extends RsyncKjt<RsyncConfigInvoiceFEPBillPost, RsyncRequestInvoiceFEPBillPost, RsyncResponseInvoiceFEPBillPost> {

	private final static RsyncConfigInvoiceFEPBillPost RSYNC_CONFIG_INVOICE_FEPBILLPOST = new RsyncConfigInvoiceFEPBillPost();
	private RsyncRequestInvoiceFEPBillPost rsyncRequestInvoiceFEPBillPost =new   RsyncRequestInvoiceFEPBillPost();
	
	
	@Override
	public RsyncConfigInvoiceFEPBillPost upConfig() {
		return RSYNC_CONFIG_INVOICE_FEPBILLPOST;
	}

	@Override
	public RsyncRequestInvoiceFEPBillPost upRsyncRequest() {
		return rsyncRequestInvoiceFEPBillPost;
	}
	
	@Override
	public RsyncResult doProcess(RsyncRequestInvoiceFEPBillPost tRequest,RsyncResponseInvoiceFEPBillPost tResponse) {
		
		RsyncResult rsyncResult = new RsyncResult();
		
		if(!"0".equals(tResponse.getCode())) {
			rsyncResult.setResultCode(918519135);
			rsyncResult.setResultMessage(tResponse.getDesc());
			return rsyncResult;
		}
		
		List<Long> orderids=tRequest.getOrderIds();
		String FEPBillId= tResponse.getData().getFEPBillId();
		BigDecimal PurchasingTotalAmount=tResponse.getData().getPurchasingTotalAmount();
		
		DbUp.upTable("oc_order_kjt_fepbill").dataInsert(new MDataMap("order_code_out",orderids.toString(),"fepbillid",FEPBillId,"purchasing_total_amount",PurchasingTotalAmount.toString(),"create_time",DateUtil.getSysDateTimeString()));
		
		return rsyncResult;
	}
	
	@Override
	public RsyncResponseInvoiceFEPBillPost upResponseObject() {
		return new RsyncResponseInvoiceFEPBillPost();
	}
	
}
