package com.cmall.groupcenter.invoke.imp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.homehas.RsyncControlGiftVoucher;
import com.cmall.groupcenter.homehas.model.RsyncModelGiftVoucher;
import com.srnpr.xmassystem.invoke.ref.RollbackLjqRef;
import com.srnpr.xmassystem.invoke.ref.model.GiftVoucherInfo;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 24小时订单退还礼金券
 * @author cc
 *
 */
public class RollbackLjqRefImp implements RollbackLjqRef{

	@Override
	public RootResult reWriteGiftVoucherToLD(List<GiftVoucherInfo> list) {
		RootResult ret = new RootResult();
		/**此处已停掉 礼金券所有操作都走异步推送项目 -rhb 20180927*/
//		if(list != null && list.size() > 0) {
//			list = RollbackLjqRefImp.reOrgCoupons(list);
//			RsyncControlGiftVoucher rsync = new RsyncControlGiftVoucher();
//			List<RsyncModelGiftVoucher> param = new ArrayList<RsyncModelGiftVoucher>();
//			for(GiftVoucherInfo coupon : list) {
//				RsyncModelGiftVoucher model = new RsyncModelGiftVoucher();
//				model.setHjy_ord_id(coupon.getHjy_ord_id());
//				Map<String, Object> map = DbUp.upTable("oc_coupon_info").dataSqlOne(
//								"SELECT out_coupon_code from oc_coupon_info where coupon_code=:coupon_code ",
//								new MDataMap("coupon_code", coupon.getLj_code().toString()));
//				if(map != null && map.get("out_coupon_code") != null) {
//					model.setLj_code(map.get("out_coupon_code").toString());
//					param.add(model);
//				}				
//			}
//			rsync.upRsyncRequest().setDo_type("R");
//			rsync.upRsyncRequest().setLjqList(param);
//			rsync.doRsync();
//		}
		return ret;
	}

	/**
	 * 将订单号合并
	 * @param reWriteLD
	 * @return
	 */
	public static List<GiftVoucherInfo> reOrgCoupons(List<GiftVoucherInfo> reWriteLD) {
		//按订单号排序
		Collections.sort(reWriteLD, new Comparator<Object>() {
		      public int compare(Object info1, Object info2) {
		    	  String one = ((GiftVoucherInfo)info1).getHjy_ord_id().toString();
		    	  String two = ((GiftVoucherInfo)info2).getHjy_ord_id().toString();
		        return two.compareTo(one);
		      }
		    });
		for(int i=0;i<reWriteLD.size()-1;i++) {
			for(int j=reWriteLD.size()-1;j>i;j--) {
				if(reWriteLD.get(j).getLj_code().toString().equals(reWriteLD.get(i).getLj_code().toString())) {
					String orders = reWriteLD.get(i).getHjy_ord_id().toString() + "," + reWriteLD.get(j).getHjy_ord_id().toString();
					reWriteLD.get(i).setHjy_ord_id(orders);
					reWriteLD.remove(j);
				}
			}
		}		
		return reWriteLD;
	}
}
