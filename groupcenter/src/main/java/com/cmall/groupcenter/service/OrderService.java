package com.cmall.groupcenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.SerializationUtils;

import com.cmall.groupcenter.homehas.RsyncAddOrder;
import com.cmall.groupcenter.homehas.model.RsyncRequestAddOrder;
import com.cmall.groupcenter.homehas.model.RsyncRequestAddOrder.CouponInfo;
import com.cmall.groupcenter.homehas.model.RsyncRequestAddOrder.Goods;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.ordercenter.model.OcOrderActivity;
import com.cmall.ordercenter.model.OcOrderPay;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.OrderAddress;
import com.cmall.ordercenter.model.OrderDetail;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.xmassystem.load.LoadEventInfo;
import com.srnpr.xmassystem.load.LoadMemberLevel;
import com.srnpr.xmassystem.modelevent.PlusModelEventInfo;
import com.srnpr.xmassystem.modelevent.PlusModelEventQuery;
import com.srnpr.xmassystem.modelevent.PlusModelMemberLevel;
import com.srnpr.xmassystem.modelevent.PlusModelMemberLevelQuery;
import com.srnpr.xmassystem.service.HjycoinService;
import com.srnpr.xmassystem.service.PlusServiceAccm;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.MoneyHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 本地订单与家有订单相关操作
 * 
 * @author jlin
 * 
 */
public class OrderService extends BaseClass {

	private String statusCode = "";// 状态码
	

	/**
	 * 创建订单完成时，同步本地订单到家有
	 * 
	 * @param list
	 */
	public boolean rsyncOrder(String orderCode, String mobileid) {
		return rsyncOrder2(orderCode, mobileid).upFlagTrue();
	}
		
	/**
	 * 返回状态码
	 * 
	 * @return
	 */
	public String getStatusCode() {
		return statusCode;
	}

	private String trim(Object obj) {
		return obj == null ? "" : obj.toString().trim();
	}
	
	private synchronized boolean timeDiffer(String time1,String time2,long sec){
		
		Date date1=DateUtil.toDate(time1,DateUtil.sdfDateTime);
		Date date2=DateUtil.toDate(time2,DateUtil.sdfDateTime);
		if((date1.getTime()-date2.getTime())>sec*1000){
			return true;
		}
		return false;
	}
	
	private boolean isDelay(Order order, RsyncRequestAddOrder request) {
		// 扫码购添加新逻辑
		// 订单创建时间-TV开始时间<80分钟
		String create_time = order.getCreateTime();// 订单创建时间
		List<String> goodids = new ArrayList<String>();
		for (Goods goods : request.getGood_info()) {
			goodids.add("'" + goods.getGood_id() + "'");
		}
		MDataMap tvInfo = DbUp.upTable("pc_tv").oneWhere("form_fr_date,form_end_date", "form_fr_date desc",
				"form_fr_date<:create_time and good_id in (" + StringUtils.join(goodids, ",") + ")", "create_time",
				create_time);
		if (tvInfo != null && !tvInfo.isEmpty()) {
			String form_fr_date = tvInfo.get("form_fr_date");
			if (!timeDiffer(create_time, form_fr_date, 80 * 60)) {
				return true;
			}
		} 

		return false;
	}
	
	private String getSendTimeBaseNow() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		//推迟90分到20小时
		String sdelayHour = bConfig("groupcenter.delay_hour");
		int delayHour = 2;
		try {
			delayHour = Integer.parseInt(sdelayHour);
		} catch(Exception e) {}
		int minute = 90 + (int) (Math.random() * 60 * delayHour);
		c.add(Calendar.MINUTE, minute);
		//跨过凌晨时段
		if(c.get(Calendar.HOUR_OF_DAY) > 1 && c.get(Calendar.HOUR_OF_DAY) < 7)
			c.add(Calendar.HOUR, 7);
		return DateUtil.toString(c.getTime(), DateUtil.DATE_FORMAT_DATETIME);
	}
	
	/**
	 * 检查内部员工
	 * @param memberCode
	 * @return
	 */
	private boolean isVipOrder(String memberCode) {
		boolean result = false;
		List<Map<String, Object>> list = DbUp.upTable("mc_extend_info_homehas").dataSqlList("select vip_type from mc_extend_info_homehas where member_code=:member_code", 
				new MDataMap("member_code", memberCode));
		if(list != null && !list.isEmpty()) {
			for(Map<String, Object> map : list) {
				String vipType = map.get("vip_type") == null ? "" : map.get("vip_type").toString();
				if(StringUtils.equals(vipType, "4497469400050001")) {
					result = true;
					break;
				}
			}
		}
		return result;
	}
	
	private void removeSmgTagInRequest(RsyncRequestAddOrder request) {
		if(StringUtils.isNotEmpty(request.getEvent_id()) && request.getEvent_id().startsWith("IC_SMG_")) {
			//request层级eventid去smg
			request.setEvent_id(request.getEvent_id().substring(7));
		}
		
		for (Goods goods : request.getGood_info()) {
			if(StringUtils.isNotEmpty(goods.getEvent_id()) && goods.getEvent_id().startsWith("IC_SMG_")) {
				//goods层级eventid去smg
				goods.setEvent_id(goods.getEvent_id().substring(7)); 
			}
		}
	}
	
	public static void main(String[] args) {
		OrderService server = new OrderService();
		//server.rsyncOrder("DD128974104", "17660501700");
		Map<String, String> m = server.getAddrCode("442098");
		System.out.println(m);
	}
	
	
public MWebResult rsyncOrder2(String orderCode, String mobileid) {
		
		MWebResult result = new MWebResult();
		
		//请求报文对象
		RsyncAddOrder addOrder = new RsyncAddOrder();
		RsyncRequestAddOrder request = addOrder.upRsyncRequest();
		List<Goods> good_info = request.getGood_info();//商品信息
		List<Goods> good_info_gift = request.getGift_good_info();//赠品信息
		
		String coupon_type = "";//0=礼金 1=代金券 2=折扣立减
		
		//查询出订单的详细信息
		com.cmall.ordercenter.service.OrderService service = new com.cmall.ordercenter.service.OrderService();
		Order order = service.getOrder(orderCode);
		List<OrderDetail> detailList = order.getProductList();// 订单详情
		OrderAddress address = order.getAddress();// 地址信息
		List<OcOrderActivity> activityList=order.getActivityList();//活动
		
		boolean flag_vipGeneral=false;//员购订单标示
		//boolean flag_flash=false;//闪购订单标示
		BigDecimal use_coupon_amt=new BigDecimal("0");
		boolean flag_validate=false;//是否有虚拟商品标示
		
		//活动分为两种 订单活动  和商品活动   若活动中取不到sku编号，就默认是订单活动
		Map<String, List<OcOrderActivity>> skuActiveMap = null;//商品活动
		if (activityList != null && activityList.size() > 0) {
			skuActiveMap = new HashMap<String, List<OcOrderActivity>>();
			for (OcOrderActivity active : activityList) {				
				String sku_code = active.getSkuCode();
				if (StringUtils.isNotBlank(sku_code)) {					
					List<OcOrderActivity> skuActiveList=skuActiveMap.get(sku_code);
					if(skuActiveList==null){
						skuActiveList=new ArrayList<OcOrderActivity>();
					}
					skuActiveList.add(active);
					skuActiveMap.put(sku_code, skuActiveList);
				} 			
				// -------------------------特殊判断---------------------------
				if ("AT140820100004".equals(active.getActivityType())||"4497472600010006".equals(active.getActivityType())) {// 员工内购
					flag_vipGeneral = true;
				}
				//if (("AT140820100002".equals(active.getActivityType())||"449715400004".equals(active.getActivityType()))) { // 闪购活动
				//	flag_flash=true;
				//}				
			}
		}
		
		int main_and_gift_seq=1;//主品的序号
		Map<String, List<String>> main_seq_map=new HashMap<String, List<String>>();//key:主品的sku_code ,value:seq_已挂赠品LD商品编号
		Map<String, String> gift_seq_map=new HashMap<String, String>();//key:赠品LD商品编号_主品的sku_code   value:主品的sku_code		
		boolean sku_num_0=false;		
		//开始组装商品信息
		for (OrderDetail orderDetail : detailList) {			
			if(orderDetail.getSkuNum()<1){
				sku_num_0=true;
				break;
			}			
			String sku_code=orderDetail.getSkuCode();			
			//查询商品相关信息
			Map<String, Object> skuMap = DbUp.upTable("pc_skuinfo").dataSqlOne("select sku_key,sell_productcode,sell_price,product_code,sku_name from pc_skuinfo where sku_code=:sku_code ",new MDataMap("sku_code", sku_code));			
			if (skuMap == null) {
				continue;
			}			
			String sku_name=(String) skuMap.get("sku_name");
			String product_code = (String) skuMap.get("product_code");
			String sku_key = (String) skuMap.get("sku_key");
			long good_id = Long.valueOf((String) skuMap.get("sell_productcode"));						
			String color_id = "";
			String style_id = "";			
			if (!"".equals(trim(sku_key))) {
				String[] ss = sku_key.split("&");
				for (String s : ss) {
					if (s.contains("color_id=")) {
						color_id = s.replace("color_id=", "");
					}
					if (s.contains("style_id=")) {
						style_id = s.replace("style_id=", "");
					}
				}
			}						
			//判断是否有虚拟商品
			MDataMap productInfo = DbUp.upTable("pc_productinfo").oneWhere("validate_flag", "", "product_code=:product_code", "product_code",product_code);
			String validate_flag=productInfo.get("validate_flag");
			if("Y".equals(validate_flag)){
				 flag_validate=true;
			}
			//封装商品信息
			Goods goods = new Goods();
			goods.setGood_id(good_id);
			goods.setGood_cnt(1);
			goods.setGood_prc(orderDetail.getSkuPrice());
			goods.setColor_id(color_id);
			goods.setStyle_id(style_id);
			goods.setDely_fee("0");//商品运费  目前都是免运费的	
			//根据SKU信息查询是否是推广订单。
			MDataMap tgzOrderDetail = DbUp.upTable("fh_tgz_order_detail").one("order_code",orderCode,"sku_code",sku_code);
			PlusModelMemberLevel levelInfo = new LoadMemberLevel().upInfoByCode(new PlusModelMemberLevelQuery(MapUtils.getString(tgzOrderDetail, "tgz_member_code","")));
			if (levelInfo != null) {
				goods.setTg_cust_id(levelInfo.getCustId());
			}
			if(tgzOrderDetail != null) {
				goods.setTg_hb_amt(new BigDecimal(tgzOrderDetail.get("tgz_money")));
				if("4497471600610002".equals(tgzOrderDetail.get("tgz_type"))) {//买家秀
					goods.setTg_hb_type("M");
				}else {
					goods.setTg_hb_type("T");
				}
			}
			//系统默认发票抬头是收货人 发票类型是 商品名称 
			if(StringUtils.isNotBlank(address.getInvoiceContent())){
				goods.setInv_type(address.getInvoiceContent());
				if("明细".equals(goods.getInv_type())){
					goods.setInv_type(sku_name); // 要把明细替换为商品名称，否则LD无法正常开发票
				}
			}else{
				goods.setInv_type(sku_name); //我方并没有对应的发票类型，所以默认都是 商品名称
			}			
			goods.setInv_yn("N_");
			if(StringUtils.isNotBlank(address.getInvoiceTitle())){
				goods.setInv_yn("Y_");
				goods.setInv_head(address.getInvoiceTitle());
			}else{
				goods.setInv_head(address.getReceivePerson());
			}			
			boolean order_active_flag=false;//是否是订单上的活动
			// 记录每个SKU对应每个优惠券的总使用金额
			Map<String,BigDecimal> couponMoneyMap = new HashMap<String, BigDecimal>();
			// 记录每个SKU对应每个优惠券的明细累计金额
			Map<String,BigDecimal> couponMoneyDetailMap = new HashMap<String, BigDecimal>();
			if (skuActiveMap!=null) {				
				//查看是否参与活动   				
				List<OcOrderActivity> aclist=skuActiveMap.get(sku_code);				
				if(aclist!=null&&aclist.size()>0){
					
					for (OcOrderActivity ocOrderActivity : aclist) {
						if(StringUtils.isNotBlank(ocOrderActivity.getOutActiveCode()) && 
								StringUtils.isNotBlank(ocOrderActivity.getActivityType())) {
							String outActiveCode = StringUtils.trimToEmpty(ocOrderActivity.getOutActiveCode());
							RsyncRequestAddOrder.AddEvent evt = new RsyncRequestAddOrder.AddEvent();
							evt.setEvent_id(outActiveCode);
							// 在线支付立减特殊处理，因为存储的是合计金额需要计算一下单个商品的金额
							if("4497472600010021".equals(ocOrderActivity.getActivityType())) {
								evt.setSave_amt(new BigDecimal(ocOrderActivity.getPreferentialMoney() / orderDetail.getSkuNum()).setScale(2, BigDecimal.ROUND_HALF_UP));
							} else {
								evt.setSave_amt(new BigDecimal(ocOrderActivity.getPreferentialMoney()).setScale(2, BigDecimal.ROUND_HALF_UP));
							}
							
							//打折促销 将IC编号转换成家有活动编号
							if("4497472600010030".equals(ocOrderActivity.getActivityType())) {
								LoadEventInfo loadEventInfo = new LoadEventInfo();
								PlusModelEventQuery query = new PlusModelEventQuery();
								query.setCode(ocOrderActivity.getActivityCode());
								PlusModelEventInfo eventInfo = loadEventInfo.upInfoByCode(query);
								outActiveCode = eventInfo.getOutActiveCode();
								evt.setEvent_id(outActiveCode);
							}
							
							// 判断是否是正常LD活动编号
							if(!outActiveCode.matches("\\d+")) {
								continue;
							}
							
							goods.getEventList().add(evt);
						}
					}					
				}		
				
				List<CouponInfo> clist = new ArrayList<CouponInfo>();
				if(aclist!=null&&aclist.size()>0){						
					for (OcOrderActivity ocOrderActivity : aclist) {
						String couponCode = ocOrderActivity.getTicketCode();
						if(StringUtils.isNotBlank(couponCode)) {
							Map<String, Object> couponmap = DbUp.upTable("oc_coupon_info").dataSqlOne("select ct.creater from oc_coupon_type ct,oc_coupon_info ci where ci.coupon_type_code = ct.coupon_type_code and ci.coupon_code = :couponCode", new MDataMap("couponCode",couponCode));
							//此处更改为 判断创建人 -rhb 20180927
							if(couponmap != null && "ld".equals(couponmap.get("creater"))) {
								coupon_type="0";
								CouponInfo coupon_info1 = new RsyncRequestAddOrder().new CouponInfo();
								Map<String, Object> map = DbUp.upTable("oc_coupon_info").dataSqlOne(
										"SELECT out_coupon_code from oc_coupon_info where coupon_code=:coupon_code ",
										new MDataMap("coupon_code", ocOrderActivity.getTicketCode()));
								if(map != null && map.get("out_coupon_code") != null) {
									coupon_info1.setCoupon_id(map.get("out_coupon_code").toString());
									// 每件预估的金额
									BigDecimal avgCouponAmt = new BigDecimal(ocOrderActivity.getPreferentialMoney()).divide(new BigDecimal(orderDetail.getSkuNum()), 0, BigDecimal.ROUND_HALF_UP);
									coupon_info1.setSave_amt(avgCouponAmt);
									clist.add(coupon_info1);		
									use_coupon_amt = use_coupon_amt.add(avgCouponAmt);
									
									// 每个优惠券使用的总金额
									couponMoneyMap.put(coupon_info1.getCoupon_id(), new BigDecimal(ocOrderActivity.getPreferentialMoney()+"").setScale(0, BigDecimal.ROUND_HALF_UP));
									if(couponMoneyMap.get(coupon_info1.getCoupon_id()).compareTo(avgCouponAmt) < 0) {
										coupon_info1.setSave_amt(couponMoneyMap.get(coupon_info1.getCoupon_id()));
									}
									// 记录第一个序号使用的优惠券金额
									couponMoneyDetailMap.put(coupon_info1.getCoupon_id(), coupon_info1.getSave_amt());
								}
							}
						}
					}
				}
				if(clist != null && clist.size() > 0) {					
					goods.setCouponList(clist);
				} 
				goods.setCoupon_type(coupon_type);
			}	
			if ("0".equals(orderDetail.getGiftFlag())) { // 判断赠品标示
				gift_seq_map.put(goods.getGood_id()+"_"+orderDetail.getProductCode(), orderDetail.getProductCode());//此处的变态规则：订单详情表中，赠品记录的 ProductCode 存储的都是LD的商品编号
				goods.setInv_yn(goods.getInv_yn()+orderDetail.getProductCode());
				good_info_gift.add(goods);// 赠品
			}else{			
				int mseq=main_and_gift_seq++;			
				List<String> seqList=main_seq_map.get(sku_code);
				if(seqList==null){
					seqList=new ArrayList<String>();
					main_seq_map.put(sku_code, seqList);
				}
				seqList.add(mseq+"_");
				goods.setMain_and_gift(mseq);										
				good_info.add(goods);
			}			
			//计算剩余商品数量
			int copy_num =orderDetail.getSkuNum()-1;
			if(copy_num>0){
				for (int i = 0; i < copy_num; i++) {
					Goods goods_copy = SerializationUtils.clone(goods);
					
					// 同一个SKU购买多件时拆分优惠券金额，对预估的金额做修正
					BigDecimal saveAmt,totalAmt,detailAmt;
					for(int j=0;j<goods_copy.getCouponList().size();j++) {
						saveAmt = goods_copy.getCouponList().get(j).getSave_amt();
						totalAmt = couponMoneyMap.get(goods_copy.getCouponList().get(j).getCoupon_id());
						detailAmt = couponMoneyDetailMap.get(goods_copy.getCouponList().get(j).getCoupon_id());
						
						if(totalAmt == null || detailAmt == null) {
							continue;
						}
						
						// 明细合计超过总优惠金额则修正明细的金额
						// 最后一条时把剩余的金额都设置到这个里面
						if(saveAmt.add(detailAmt).compareTo(totalAmt) > 0
								|| (i == (copy_num -1))) {
							saveAmt = totalAmt.subtract(detailAmt);
							saveAmt = saveAmt.compareTo(BigDecimal.ZERO) > 0 ? saveAmt : BigDecimal.ZERO;
							goods_copy.getCouponList().get(j).setSave_amt(saveAmt);
						}
						
						detailAmt = detailAmt.add(saveAmt);
						couponMoneyDetailMap.put(goods_copy.getCouponList().get(j).getCoupon_id(), detailAmt);
					}	
					
					if ("0".equals(orderDetail.getGiftFlag())) { // 判断赠品标示
						good_info_gift.add(goods_copy);// 赠品
					}else{
						int mseq=main_and_gift_seq++;
						List<String> seqList=main_seq_map.get(sku_code);
						seqList.add(mseq+"_");
						goods.setMain_and_gift(mseq);																	
						good_info.add(goods_copy);
					}
					
				}
			}		
		}
		if(sku_num_0){
			statusCode="915805221";
			result.setResultCode(915805221);
			return result;
		}		
		//此处循环设置赠品的seq
		for (Goods goods : good_info_gift) {
			List<String> seqList=main_seq_map.get(gift_seq_map.get(goods.getGood_id()+"_"+goods.getInv_yn().substring(2)));
			for (String seq_ : seqList) {
				String seq_s[]=seq_.split("_");
				String ngood_id="@"+goods.getGood_id()+"@";
				if(!seq_.contains(ngood_id)){
					goods.setMain_and_gift(Integer.valueOf(seq_s[0]));					
					seqList.set(seqList.indexOf(seq_), seq_+seq_+ngood_id);
					break;
				}
			}
			goods.setInv_yn(goods.getInv_yn().substring(0, 1));
		}		
		for (Goods goods : good_info) {
			goods.setInv_yn(goods.getInv_yn().substring(0, 1));
		}		
		String etr_id="app";
		String subsystem="app";
		// 如果是家有汇 写网站渠道
		if (MemberConst.MANAGE_CODE_HPOOL.equals(order.getSellerCode())||"449747430004".equals(order.getOrderChannel())) { 
			subsystem="001";
			etr_id="web";
		}		
		String cust_id = new PlusServiceAccm().getCustId(order.getBuyerCode());
		if(cust_id == null) cust_id = "0";
		
		//开始组装报文
		request.setSubsystem(subsystem);
		request.setAccount("");
		request.setPassword("");
		request.setCust_id(cust_id);
		request.setMembercode(order.getBuyerCode());
		request.setCust_mobile(mobileid);
		request.setHidden_json(null);
		request.setHidden_json_gift(null);
		request.setEtr_id(etr_id);
		//内购测试特殊设置
		if("1".equals(bConfig("groupcenter.VipSpecial_test_flag"))){
			request.setWeb_ord_date("2015-04-05 09:50:54");
		}else{
			request.setWeb_ord_date(order.getCreateTime());
		}
		if(coupon_type.equals("0")) {
			request.setCoupon_type("0");
		}
		request.setWeb_ord_id(order.getOrderCode());
		request.setPay_amt(order.getDueMoney());// 用户实际支付金额
		request.setPay_time(null);
		request.setPay_no("");
		request.setDlv_date("");
		request.setDlv_service("");
		request.setDlv_time("");
		request.setUse_coupon_amt(use_coupon_amt);				
		// 如果是刘嘉玲的商品 则将媒体编号定义为对应的编号
		if (order.getSellerCode().equals(MemberConst.MANAGE_CODE_APP)) {
			request.setMedi_lclss_id("10");
			request.setMedi_mclss_id("41");
		} else if (order.getSellerCode().equals(MemberConst.MANAGE_CODE_HPOOL)||"449747430004".equals(order.getOrderChannel())||"449715190005".equals(order.getOrderSource())) {
			request.setMedi_lclss_id("2");
			request.setMedi_mclss_id("2");			
			if("449747110002".equals(order.getLowOrder())){
				request.setMedi_lclss_id("2");
				request.setMedi_mclss_id("62");
			}
			
		} 
//		else if (order.getSellerCode().equals(MemberConst.MANAGE_CODE_HOMEHAS)&&flag_vipGeneral) {
//			request.setMedi_lclss_id("2");
//			request.setMedi_mclss_id("2");
//		}
		else if (order.getSellerCode().equals(MemberConst.MANAGE_CODE_SPDOG)) {
			request.setMedi_lclss_id("10");
			request.setMedi_mclss_id("49");
		}else {
			request.setMedi_lclss_id("10");
			request.setMedi_mclss_id("34");			
			if("449747110002".equals(order.getLowOrder())){
				request.setMedi_lclss_id("10");
				request.setMedi_mclss_id("61");
			}
		}				
		/**
		微信商城：大通路（大媒体分类）：10  小通路（中媒体分类）：42
	            扫码购：大通路（大媒体分类）：10  小通路（中媒体分类）：39
	           二台扫码购：大通路（大媒体分类）：10  小通路（中媒体分类）：58
	           天鹅扫码购：大通路（大媒体分类）：10  小通路（中媒体分类）：248
		微信订单(449715190006),449715190007(扫码购订单),449715190020(南京二台扫码购订单)
		*/
		String orderSource = order.getOrderSource();
		String ordertype = order.getOrderType();
		if("449715190006".equals(orderSource)){
			request.setMedi_lclss_id("10");
			request.setMedi_mclss_id("42");
			if("449747110002".equals(order.getLowOrder())){
				request.setMedi_lclss_id("10");
				request.setMedi_mclss_id("63");
			}
		}else if("449715190007".equals(orderSource)){
			request.setMedi_lclss_id("10");
			request.setMedi_mclss_id("39");			
		}else if("449715190020".equals(orderSource)){
			request.setMedi_lclss_id("10");
			request.setMedi_mclss_id("58");			
		}else if("449715190025".equals(orderSource)){
			request.setMedi_lclss_id("10");
			request.setMedi_mclss_id("239");			
		}else if("449715190027".equals(orderSource)){
			request.setMedi_lclss_id("10");
			request.setMedi_mclss_id("248");			
		}else if("449715190033".equals(orderSource)){
			request.setMedi_lclss_id("10");
			request.setMedi_mclss_id("39");
		}else if("449715190041".equals(orderSource)){
			request.setMedi_lclss_id("16");
			request.setMedi_mclss_id("201");
		}else if("449715190042".equals(orderSource)){
			request.setMedi_lclss_id("16");
			request.setMedi_mclss_id("202");
		}else if("449715190043".equals(orderSource)){
			request.setMedi_lclss_id("16");
			request.setMedi_mclss_id("203");
		}else if("449715190044".equals(orderSource)){
			request.setMedi_lclss_id("16");
			request.setMedi_mclss_id("244");
		}else if("449715190045".equals(orderSource)){
			request.setMedi_lclss_id("16");
			request.setMedi_mclss_id("245");
		}else if("449715190046".equals(orderSource)){
			request.setMedi_lclss_id("16");
			request.setMedi_mclss_id("303");
		}else if("449715190048".equals(orderSource)){
			request.setMedi_lclss_id("22");
			request.setMedi_mclss_id("184"); // 默认通路
			
			// 设置具体缤纷二台通路
			MDataMap map = DbUp.upTable("oc_order_binfen_smg").one("order_code",order.getOrderCode());
			if(map != null) {
				request.setMedi_mclss_id(map.get("mclass_id"));
			}
		} else {
			// 检查是否单独的渠道码
			MDataMap channelMap = DbUp.upTable("sc_erwei_code_channel").one("order_source", orderSource);
			if(channelMap != null && StringUtils.isNotBlank(channelMap.get("mclass_id"))) {
				request.setMedi_lclss_id(channelMap.get("lclass_id"));
				request.setMedi_mclss_id(channelMap.get("mclass_id"));
			}
		}
		

		// 运费字段 ---------------可能被用作其他含义---------	
		request.setDlv_amt(order.getTransportMoney() == null ? BigDecimal.ZERO : order.getTransportMoney());// 运费		
		if(request.getDlv_amt().compareTo(BigDecimal.ZERO)>=0){
			request.setIs_free_dlv_amt("N");
		}else{
			request.setIs_free_dlv_amt("Y");
		}			
		request.setOrder_amt(order.getOrderMoney());// 订单总金额		
		//---------------------------------此处添加储值金、暂存款、积分 等逻辑-------------------------------------
		 List<OcOrderPay> listPay=order.getOcOrderPayList();
		 if(listPay!=null&&listPay.size()>0){
			 for (OcOrderPay ocOrderPay : listPay) {
				 //449746280006:储值金 (家有汇)449746280007:暂存款(家有汇) 449746280008:积分(家有汇)
				 if("449746280006".equals(ocOrderPay.getPayType())){
					 request.setUse_ppc_amt(BigDecimal.valueOf(ocOrderPay.getPayedMoney()).setScale(2,BigDecimal.ROUND_HALF_UP));//保留两位小数 可能会有问题
				 }else  if("449746280007".equals(ocOrderPay.getPayType())){
					 request.setUse_crdt_amt(BigDecimal.valueOf(ocOrderPay.getPayedMoney()).setScale(2,BigDecimal.ROUND_HALF_UP));//保留两位小数 可能会有问题
				 }else  if("449746280008".equals(ocOrderPay.getPayType())){
					 request.setUse_accm_amt(BigDecimal.valueOf(ocOrderPay.getPayedMoney()));
				 }else if("449746280025".equals(ocOrderPay.getPayType())) {//惠币支付
					 request.setUse_hb_amt(MoneyHelper.round(2,BigDecimal.ROUND_HALF_UP,BigDecimal.valueOf(ocOrderPay.getPayedMoney())));
				 }
			}
		 }		
		//---------------------------------此处添加储值金、暂存款、积分 等逻辑-------------------------------------		
		request.setOrd_lvl_cd("10");
		if(flag_vipGeneral){//员工内购，使用50
			request.setOrd_lvl_cd("50");
		}
		// 449716200001 在线支付 在线支付都是支付宝
		// 449716200002 货到付款		
		//此处添加支付信息的新的判断逻辑		
		String payType = order.getPayType();
		String pre_aft_pay_cd = "10";
		String send_bank_cd = "54";		
		if ("449716200002".equals(payType)) {
			pre_aft_pay_cd = "30";
			send_bank_cd = "CD1";
			request.setIs_free_dlv_amt("Y");
		}else{			
			Map<String, Object> payTypeMap = DbUp.upTable("oc_orderinfo_upper_payment").dataSqlOne("SELECT up.pay_type FROM oc_orderinfo_upper_payment up,oc_orderinfo oi WHERE up.big_order_code = oi.big_order_code AND oi.order_code = :order_code limit 1", new MDataMap("order_code",order.getOrderCode()));
			if(payTypeMap != null){
				payType = (String)payTypeMap.get("pay_type");
			}			
			if ("449746280003".equals(payType)) {
				pre_aft_pay_cd = "10";
				send_bank_cd = "54";
			} else if ("449746280005".equals(payType)) {
				pre_aft_pay_cd = "10";
				send_bank_cd = "WEC";
			} else if ("449746280014".equals(payType)) {
				pre_aft_pay_cd = "10";
				send_bank_cd = "66";
			}
		}		
		request.setPre_aft_pay_cd(pre_aft_pay_cd);// 支付方式
		request.setSend_bank_cd(send_bank_cd);
		request.setExterior_accm(0);		
		if(flag_validate){
			request.setVirtual_ord("Y");
		}else{
			request.setVirtual_ord("N");
		}		
		request.setAccm_integral(1);
		request.setHy_type(1);
		request.setTel1("");
		request.setTel2("");
		request.setRcver_nm(address.getReceivePerson());
		request.setMobile(address.getMobilephone());		
		String area_code = address.getAreaCode();
		
		Map<String, String> addrCodeMap = getAddrCode(area_code);
		request.setLaddr(StringUtils.trimToEmpty(addrCodeMap.get("l1_name")));
		request.setMaddr(StringUtils.trimToEmpty(addrCodeMap.get("l2_name")));
		request.setSaddr(StringUtils.trimToEmpty(addrCodeMap.get("l3_name")));
		
		// 如果明细中有省市区则进行替换掉
		String name = request.getLaddr()+request.getMaddr()+request.getSaddr();
		String adr = address.getAddress();
		int index = adr.indexOf(name);
		if(index >= 0 && adr.length() > name.length()) {
			adr = adr.substring(index+name.length());
		}
			
		request.setSend_addr(adr);
		request.setSrgn_cd(area_code);
		request.setZip_no(address.getPostCode());		
		// 积分商城订单不赋予积分
		if(order.getOrderType().equals("449715200024")){
			request.setIs_fyjf("N");
			request.setMedi_lclss_id("1");
			request.setMedi_mclss_id("149");
		}
		HjycoinService hs = new HjycoinService();
		if (hs.checkFlagEnabled() && hs.checkGiveEnabled(order.getOrderSource())) {
			request.setIs_give_hb("Y");
		}
		
		result = addOrder.doRsync2();
		return result;
	}
	
	private Map<String,String> getAddrCode(String code) {
		Map<String,String> map = new HashMap<String, String>();
		String sCode = code; // 默认编码
		
		MDataMap mData = DbUp.upTable("sc_tmp").one("code",code);
		if(mData == null) {
			return map;
		}
		
		// 默认编码为三级
		map.put("l3_code", sCode);
		map.put("l3_name", mData.get("name"));
		// 如果是四级编码则重设
		if("4".equals(mData.get("code_lvl"))) {
			map.put("l4_code", sCode);
			map.put("l4_name", mData.get("name"));
			
			// 三级区域编码
			sCode = mData.get("p_code"); 
			mData = DbUp.upTable("sc_tmp").one("code",sCode);
			map.put("l3_code", sCode);
			map.put("l3_name", mData.get("name"));
			
		}
		
		// 如果既不是四级也不是三级则不支持
		if(!"3".equals(mData.get("code_lvl")) && !"4".equals(mData.get("code_lvl"))) { 
			return new HashMap<String, String>();
		}
		
		// 设置二级地址
		MDataMap l2Data = DbUp.upTable("sc_tmp").one("code",mData.get("p_code"),"code_lvl","2");
		if(l2Data != null) {
			map.put("l2_code", l2Data.get("code"));
			map.put("l2_name", l2Data.get("name"));
			
			// 设置一级地址
			MDataMap l1Data = DbUp.upTable("sc_tmp").one("code",l2Data.get("p_code"),"code_lvl","1");
			if(l1Data != null) {
				map.put("l1_code", l1Data.get("code"));
				map.put("l1_name", l1Data.get("name"));
			}
		}
		
		return map;
	}
	
}
