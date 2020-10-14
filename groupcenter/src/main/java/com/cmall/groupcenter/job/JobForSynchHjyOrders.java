package com.cmall.groupcenter.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncHjyOrders;
import com.cmall.groupcenter.homehas.RsyncHjyRtns;
import com.cmall.groupcenter.homehas.model.HjyOrderInfo;
import com.cmall.groupcenter.homehas.model.HjyRtnInfo;
import com.cmall.groupcenter.homehas.model.RsyncRequestHjyOrders;
import com.cmall.groupcenter.homehas.model.RsyncRequestHjyRtn;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时同步惠家有商户品订单（签收+N天的订单），退货单到LD
 * @remark 
 * @author sunyan
 * @date 2019年8月1日
 */
public class JobForSynchHjyOrders extends RootJob {

	private final int COUNT = 200;
	
	public void doExecute(JobExecutionContext context) {
		//同步惠家有订单
		MDataMap map = DbUp.upTable("za_static").one("uid","ca1117c9c7e011e9abac005056165069");
		String sSql = "";
		if(StringUtils.isBlank(map.get("static_info"))){
			sSql = "SELECT od.order_code,li.login_name,od.product_code,pi.product_name,od.sku_code,od.sku_name,od.sell_price,od.sku_price,od.cost_price,od.sku_price*od.sku_num order_money,od.sku_num,oi.pay_type,oi.order_status,oi.order_status_ext,"+
					" oi.order_source,oi.create_time,los.create_time pay_time,lo.create_time ot_time,lor.update_time,concat(left(oa.area_code,2),'0000') lrgn_cd,concat(left(oa.area_code,4),'00') mrgn_cd,left(oa.area_code,6) srgn_cd"+
					" FROM oc_orderdetail od INNER JOIN oc_orderinfo oi on od.order_code = oi.order_code LEFT JOIN membercenter.mc_login_info li on oi.buyer_code = li.member_code"+
					" LEFT JOIN productcenter.pc_productinfo pi on pi.product_code = od.product_code LEFT JOIN logcenter.lc_orderstatus los ON oi.order_code = los.`code` AND los.now_status = '4497153900010002'"+
					" LEFT JOIN logcenter.lc_orderstatus lo ON oi.order_code = lo.`code` AND lo.now_status = '4497153900010003' LEFT JOIN (SELECT lo.code,min(lo.create_time) update_time from logcenter.lc_orderstatus lo where lo.now_status = '4497153900010005' AND lo.zid > 115112072 GROUP BY lo.code) lor ON oi.order_code = lor.`code` LEFT JOIN oc_orderadress oa on oi.order_code = oa.order_code"+
					" WHERE oi.order_status = '4497153900010005' AND oi.small_seller_code <> 'SI2003' AND lor.update_time >= date_add(CAST(NOW() AS date),INTERVAL - 1 DAY) AND lor.update_time < CAST(NOW() AS date)";
		}else{
			sSql = "SELECT od.order_code,li.login_name,od.product_code,pi.product_name,od.sku_code,od.sku_name,od.sell_price,od.sku_price,od.cost_price,od.sku_price*od.sku_num order_money,od.sku_num,oi.pay_type,oi.order_status,oi.order_status_ext,"+
					" oi.order_source,oi.create_time,los.create_time pay_time,lo.create_time ot_time,lor.update_time,concat(left(oa.area_code,2),'0000') lrgn_cd,concat(left(oa.area_code,4),'00') mrgn_cd,left(oa.area_code,6) srgn_cd"+
					" FROM oc_orderdetail od INNER JOIN oc_orderinfo oi on od.order_code = oi.order_code LEFT JOIN membercenter.mc_login_info li on oi.buyer_code = li.member_code"+
					" LEFT JOIN productcenter.pc_productinfo pi on pi.product_code = od.product_code LEFT JOIN logcenter.lc_orderstatus los ON oi.order_code = los.`code` AND los.now_status = '4497153900010002'"+
					" LEFT JOIN logcenter.lc_orderstatus lo ON oi.order_code = lo.`code` AND lo.now_status = '4497153900010003' LEFT JOIN (SELECT lo.code,min(lo.create_time) update_time from logcenter.lc_orderstatus lo where lo.now_status = '4497153900010005' AND lo.zid > 115112072 GROUP BY lo.code) lor ON oi.order_code = lor.`code` LEFT JOIN oc_orderadress oa on oi.order_code = oa.order_code"+
					" WHERE oi.order_status = '4497153900010005' AND oi.small_seller_code <> 'SI2003' AND lor.update_time >= '"+map.get("static_info")+"' AND lor.update_time < CAST(NOW() AS date)";
		}
		
		List<Map<String, Object>> dataList = DbUp.upTable("oc_orderdetail").dataSqlList(sSql, new MDataMap());
		if(null != dataList && !dataList.isEmpty()) {
			int part = 1;
			if(dataList.size()>COUNT) {
				part = (dataList.size()/COUNT) + (dataList.size()%COUNT==0? 0 : 1);
			}
			for(int i = 0; i < part; i++) {
				List<HjyOrderInfo> integralRelationList = new ArrayList<HjyOrderInfo>();
				int start = i*COUNT;
				int end = 0;
				if(i==(part-1) && dataList.size()%COUNT!=0) {
					end = dataList.size()%COUNT;
				}else {
					end = (i+1)*COUNT;
				}
				for(int j = start; j< end; j ++) {
					Map<String, Object> teamInfo = dataList.get(j);
					HjyOrderInfo info = new HjyOrderInfo();
					info.setOrder_code(teamInfo.get("order_code")+"");
					info.setCust_id(teamInfo.get("login_name")+"");
					info.setGood_id(teamInfo.get("product_code")+"");
					info.setGood_name(teamInfo.get("product_name")+"");
					info.setSku_code(teamInfo.get("sku_code")+"");
					info.setSku_name(teamInfo.get("sku_name")+"");
					info.setGood_org_prc(teamInfo.get("sell_price")+"");
					info.setGood_prc(teamInfo.get("sku_price")+"");
					info.setGood_cost(teamInfo.get("cost_price")+"");
					info.setOrd_amt(teamInfo.get("order_money")+"");
					info.setOrd_qty(teamInfo.get("sku_num")+"");
					info.setPay_type(teamInfo.get("pay_type")+"");
					info.setOrd_stat(teamInfo.get("order_status")+"");
					info.setOrd_stat_ass(teamInfo.get("order_status_ext")+"");
					info.setOrd_resource(teamInfo.get("order_source")+"");
					info.setOrder_time(teamInfo.get("create_time")+"");
					info.setPay_time(teamInfo.get("pay_time")+"");
					info.setOt_time(teamInfo.get("ot_time")+"");
					info.setRcv_time(teamInfo.get("update_time")+"");
					info.setLrgn_cd(teamInfo.get("lrgn_cd")+"");
					info.setMrgn_cd(teamInfo.get("mrgn_cd")+"");
					info.setSrgn_cd(teamInfo.get("srgn_cd")+"");
					
					integralRelationList.add(info);
				}
				
				if(!integralRelationList.isEmpty()) {
					RsyncHjyOrders rsyncHjyOrders = new RsyncHjyOrders();
					RsyncRequestHjyOrders upRsyncRequest = rsyncHjyOrders.upRsyncRequest();
					upRsyncRequest.setParamList(integralRelationList);
					rsyncHjyOrders.doRsync();
				}
			}
		}
		
		/**
		 * 同步惠家有退货单
		 */
		String fSql = "";
		if(StringUtils.isBlank(map.get("static_info"))){
			fSql = "SELECT rd.return_code,rg.buyer_mobile,rg.order_code,pi.product_code,pi.product_name,rd.sku_code,rd.sku_name,rd.return_price,rd.count,lrg.create_time rtn_time,rg.create_time "+
					" from oc_return_goods_detail rd INNER JOIN oc_return_goods rg ON rd.return_code = rg.return_code INNER JOIN logcenter.lc_return_goods_status lrg on rg.return_code = lrg.return_no AND lrg.`status` = '4497153900050001'"+
					" LEFT JOIN productcenter.pc_skuinfo sku on rd.sku_code = sku.sku_code LEFT JOIN productcenter.pc_productinfo pi on sku.product_code = pi.product_code"+
					" WHERE rg.`status` = '4497153900050001' AND rg.small_seller_code <> 'SI2003' AND lrg.create_time >= date_add(CAST(NOW() AS date),INTERVAL - 1 DAY) AND lrg.create_time < CAST(NOW() AS date)";
		}else{
			fSql = "SELECT rd.return_code,rg.buyer_mobile,rg.order_code,pi.product_code,pi.product_name,rd.sku_code,rd.sku_name,rd.return_price,rd.count,lrg.create_time rtn_time,rg.create_time "+
					" from oc_return_goods_detail rd INNER JOIN oc_return_goods rg ON rd.return_code = rg.return_code INNER JOIN logcenter.lc_return_goods_status lrg on rg.return_code = lrg.return_no AND lrg.`status` = '4497153900050001'"+
					" LEFT JOIN productcenter.pc_skuinfo sku on rd.sku_code = sku.sku_code LEFT JOIN productcenter.pc_productinfo pi on sku.product_code = pi.product_code"+
					" WHERE rg.`status` = '4497153900050001' AND rg.small_seller_code <> 'SI2003' AND lrg.create_time >= '"+map.get("static_info")+"' AND lrg.create_time < CAST(NOW() AS date)";
		}
		
		dataList = DbUp.upTable("oc_return_goods_detail").dataSqlList(fSql, new MDataMap());
		if(null != dataList && !dataList.isEmpty()) {
			int part = 1;
			if(dataList.size()>COUNT) {
				part = (dataList.size()/COUNT) + (dataList.size()%COUNT==0? 0 : 1);
			}
			for(int i = 0; i < part; i++) {
				List<HjyRtnInfo> integralRelationList = new ArrayList<HjyRtnInfo>();
				int start = i*COUNT;
				int end = 0;
				if(i==(part-1) && dataList.size()%COUNT!=0) {
					end = dataList.size()%COUNT;
				}else {
					end = (i+1)*COUNT;
				}
				for(int j = start; j< end; j ++) {
					Map<String, Object> teamInfo = dataList.get(j);
					HjyRtnInfo info = new HjyRtnInfo();
					info.setRtn_code(teamInfo.get("return_code")+"");
					info.setCust_id(teamInfo.get("buyer_mobile")+"");
					info.setOrder_code(teamInfo.get("order_code")+"");
					info.setGood_id(teamInfo.get("product_code")+"");
					info.setGood_name(teamInfo.get("product_name")+"");
					info.setSku_code(teamInfo.get("sku_code")+"");
					info.setSku_name(teamInfo.get("sku_name")+"");
					info.setRtn_amt(teamInfo.get("return_price")+"");
					info.setRtn_qty(teamInfo.get("count")+"");
					info.setRtn_time(teamInfo.get("rtn_time")+"");
					info.setRqst_time(teamInfo.get("create_time")+"");
					
					integralRelationList.add(info);
				}
				
				if(!integralRelationList.isEmpty()) {
					RsyncHjyRtns rsyncHjyRtns = new RsyncHjyRtns();
					RsyncRequestHjyRtn upRsyncRequest = rsyncHjyRtns.upRsyncRequest();
					upRsyncRequest.setParamList(integralRelationList);
					rsyncHjyRtns.doRsync();
				}
			}
		}
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dd = sdf.format(c.getTime());
		MDataMap upMap = new MDataMap();
		upMap.put("uid", "ca1117c9c7e011e9abac005056165069");
		upMap.put("static_info", dd);
		upMap.put("update_time", FormatHelper.upDateTime());
		DbUp.upTable("za_static").dataUpdate(upMap, "static_info,update_time", "uid");
	}
	
}
