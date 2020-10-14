package com.cmall.newscenter.webfunc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.ordercenter.model.OrderDetail;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.service.OrderService;
import com.cmall.productcenter.model.PcFreeTryOutGood;
import com.cmall.productcenter.service.ProductService;
import com.cmall.systemcenter.common.AppConst;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

import com.cmall.productcenter.model.PcFreeTryOutGood;

/**
 *  对于试用商品申请试用状态进行修改
 * @author houwen
 *
 */
public class TryOutApplyStatus extends RootFunc {

	
	private static String TABLE_TPL="nc_freetryout_apply"; //免费试用商品申请表
	
	
	/**
	 * 
	 *  (non-Javadoc)
	 * @see com.srnpr.zapweb.webface.IWebFunc#funcDo(java.lang.String, com.srnpr.zapcom.basemodel.MDataMap)
	 */
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
	   MWebResult mResult = new MWebResult();
		mResult.setResultCode(1);
		
		String tplUid = mDataMap.get("zw_f_uid");
		MDataMap dataMap = new MDataMap();
		MDataMap mDataMap2 = new MDataMap();
		MPageData mPageData = new MPageData();
		MDataMap map = new MDataMap();
		MPageData mData = new MPageData();
		ProductService productService = new ProductService();
		OrderService orderService = new OrderService();
		dataMap.put("uid", tplUid);
		mDataMap2.put("uid", tplUid);
		mPageData = DataPaging.upPageData("nc_freetryout_apply", "", "", mDataMap2, new PageOption());
		List<Order> orderList = new ArrayList<Order>();
		Order order = new Order();
		//申请状态 ：未申请：449746890001；已申请：449746890002；申请通过：449746890003；449746890004：已结束
		String isDisable = mPageData.getListData().get(0).get("status");
		if("449746890002".equals(isDisable)){  //如果状态为已申请，状态改为申请通过
			
			map.put("sku_code", mPageData.getListData().get(0).get("sku_code"));
			map.put("status", "449746890003");
			mData = DataPaging.upPageData("nc_freetryout_apply", "", "",map, new PageOption());
			int count = mData.getListData().size();
			List<Map<String, Object>> list = productService.getMyTryOutGoodsForSkuCode(mPageData.getListData().get(0).get("sku_code"), "0", null, mPageData.getListData().get(0).get("app_code"),null);
			PcFreeTryOutGood pcFreeTryOutGood = (PcFreeTryOutGood) list.get(0).get("freeGood");
			int tryoutCount = list.size();
			if(count<tryoutCount){
				dataMap.put("status", "449746890003");
				DbUp.upTable(TABLE_TPL).dataUpdate(dataMap, "status", "uid");
				StringBuffer error = new StringBuffer();
				
				String Order_code = WebHelper.upCode("DD");
				OrderDetail orderDetail = new OrderDetail();
				order.setOrderCode(Order_code);
				orderDetail.setProductCode(pcFreeTryOutGood.getpInfo().getProductCode());
				order.getProductList().add(orderDetail);
				order.setBuyerCode(mPageData.getListData().get(0).get("member_code"));
				order.setOrderType("449715200003");//订单类型  :试用订单
				order.setOrderSource(mPageData.getListData().get(0).get("ordersource"));//订单来源
				order.getProductList().get(0).setProductCode(mPageData.getListData().get(0).get("sku_code"));
				order.getProductList().get(0).setSkuNum(1); 
				
				order.getAddress().setReceivePerson(mPageData.getListData().get(0).get("address_name"));
				order.getAddress().setAreaCode(mPageData.getListData().get(0).get("address_county"));
				order.getAddress().setAddress(mPageData.getListData().get(0).get("address_street"));
				order.getAddress().setMobilephone(mPageData.getListData().get(0).get("address_mobile"));
				order.getAddress().setPostCode(mPageData.getListData().get(0).get("address_postalcode"));
				orderList.add(order);
				orderService.AddOrderListTx(orderList, error, AppConst.CAPP_STORE_CODE);
		}else{
			mResult.setResultCode(934205104);
			mResult.setResultMessage("试用商品申请通过人数已满！");
		}
	   }else{
			mResult.setResultCode(934205104);
			mResult.setResultMessage("状态为空或已修改，不能修改！");
		}
		
		return mResult;
	}
	
}
