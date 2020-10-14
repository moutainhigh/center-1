package com.cmall.groupcenter.txservice;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmapper.OcOrderinfoUpperMapper;
import com.cmall.dborm.txmodel.BcPurchaseDetail;
import com.cmall.dborm.txmodel.BcPurchaseOrder;
import com.cmall.dborm.txmodel.OcOrderPay;
import com.cmall.dborm.txmodel.OcOrderadress;
import com.cmall.dborm.txmodel.OcOrderadressExample;
import com.cmall.dborm.txmodel.OcOrderdetail;
import com.cmall.dborm.txmodel.OcOrderinfo;
import com.cmall.dborm.txmodel.OcOrderinfoExample;
import com.cmall.dborm.txmodel.OcOrderinfoUpper;
import com.cmall.dborm.txmodel.OcOrderinfoUpperExample;
import com.cmall.groupcenter.homehas.model.RsyncModelOrderInfo;
import com.cmall.ordercenter.service.money.CreateMoneyService;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.enumer.HjyBeanExecType;
import com.srnpr.xmassystem.service.HjybeanService;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbface.ITxService;
import com.srnpr.zapweb.helper.JobExecHelper;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;

/**
 * 采购单操作
 * 
 * @author jl
 * 
 */
public class TxPurchaseOrderService extends BaseClass implements ITxService {

	public void insertOrder(RsyncModelOrderInfo rsyncModelOrderInfo,String sMangeCode, String sMemberCode) {
		
		com.cmall.dborm.txmapper.OcOrderadressMapper ocom = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_OcOrderadressMapper");
		com.cmall.dborm.txmapper.OcOrderinfoMapper ocoim = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_OcOrderinfoMapper");
		com.cmall.dborm.txmapper.BcPurchaseOrderMapper bpom = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_BcPurchaseOrderMapper");
		com.cmall.dborm.txmapper.OcOrderdetailMapper ocorm = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_OcOrderdetailMapper");
		com.cmall.dborm.txmapper.BcPurchaseDetailMapper bpdm = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_BcPurchaseDetailMapper");
		OcOrderinfoUpperMapper orderinfoUpperMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_OcOrderinfoUpperMapper");//大订单信息
		com.cmall.dborm.txmapper.OcOrderPayMapper orderPayMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_OcOrderPayMapper");

		String uid = UUID.randomUUID().toString().replace("-", "");// 各表公用一个uid，节省点资源
		
		String orderCode = "";
		
		// 封装订单明细
		OcOrderdetail oddetail = new OcOrderdetail();
		// 对于家有的订单，订单编号之前用ＨＨ作为前缀
		oddetail.setOrderCode("HH" + rsyncModelOrderInfo.getYc_orderform_num());
		// 设置明细的编号为订单号加流水号,以确保唯一约束
		oddetail.setUid(uid);
		oddetail.setDetailCode(rsyncModelOrderInfo.getYc_orderform_num()+ WebConst.CONST_SPLIT_DOWN + rsyncModelOrderInfo.getOrd_seq());
		oddetail.setProductCode(rsyncModelOrderInfo.getYc_goods_num());// 这里Yc_goods_num 暂时用作ProductCode  和 SkuCode

//		oddetail.setSkuCode(rsyncModelOrderInfo.getYc_goods_num());
		oddetail.setSkuCode(getSkuCode(rsyncModelOrderInfo.getYc_goods_num(), rsyncModelOrderInfo.getYc_goods_color(), rsyncModelOrderInfo.getGoods_style()));
		oddetail.setSkuName(rsyncModelOrderInfo.getYc_goods_name()+ (("0".equals(rsyncModelOrderInfo.getYc_goods_color()) || "".equals(rsyncModelOrderInfo.getYc_goods_color())) ? "": "-" + rsyncModelOrderInfo.getYc_goods_color())+ (("0".equals(rsyncModelOrderInfo.getGoods_style()) || "".equals(rsyncModelOrderInfo.getGoods_style())) ? "": "-" + rsyncModelOrderInfo.getGoods_style()));
		oddetail.setSkuNum(rsyncModelOrderInfo.getYc_goods_count());
		oddetail.setSkuPrice(rsyncModelOrderInfo.getYc_after_base_price());
		oddetail.setShowPrice(rsyncModelOrderInfo.getYc_after_base_price());
		oddetail.setGiftCd(StringUtils.trimToEmpty(rsyncModelOrderInfo.getGift_cd()));
		oddetail.setCostPrice(getProductCost(rsyncModelOrderInfo.getYc_goods_num()));
		
		if(StringUtils.isNotBlank(oddetail.getGiftCd()) && !StringUtils.equalsIgnoreCase("null", oddetail.getGiftCd())){
			oddetail.setGiftFlag("0");
		}

		BigDecimal money = rsyncModelOrderInfo.getYc_after_base_price().multiply(new BigDecimal(rsyncModelOrderInfo.getYc_goods_count()));// 该单总金额

		// 保存一下订单明细
		MDataMap mLdDetail = DbUp.upTable("oc_order_ld_detail").oneWhere("zid,uid,orderform_status,sku_code", "", "", "orderform_num",rsyncModelOrderInfo.getYc_orderform_num(),"ord_seq",rsyncModelOrderInfo.getOrd_seq());
		if(mLdDetail == null){
			mLdDetail = new MDataMap();
			mLdDetail.put("order_code", StringUtils.trimToEmpty(rsyncModelOrderInfo.getWeb_ord_id()));
			mLdDetail.put("orderform_num", StringUtils.trimToEmpty(rsyncModelOrderInfo.getYc_orderform_num()));
			mLdDetail.put("ord_seq", rsyncModelOrderInfo.getOrd_seq());
			mLdDetail.put("orderform_time", rsyncModelOrderInfo.getYc_orderform_time());
			mLdDetail.put("orderform_status", rsyncModelOrderInfo.getYc_orderform_status());
			mLdDetail.put("product_code", oddetail.getProductCode());
			mLdDetail.put("sku_code", oddetail.getSkuCode());
			mLdDetail.put("goods_count", rsyncModelOrderInfo.getYc_goods_count()+"");
			mLdDetail.put("goods_color", rsyncModelOrderInfo.getYc_goods_color());
			mLdDetail.put("goods_style", rsyncModelOrderInfo.getGoods_style());
			mLdDetail.put("cost_price", rsyncModelOrderInfo.getYc_cost_price()+"");
			mLdDetail.put("after_base_price", rsyncModelOrderInfo.getYc_after_base_price()+"");
			mLdDetail.put("carriage_money", rsyncModelOrderInfo.getYc_carriage_money());
			mLdDetail.put("use_integral", rsyncModelOrderInfo.getYc_use_integral());
			mLdDetail.put("crdt_apply_amt", rsyncModelOrderInfo.getCrdt_apply_amt());
			mLdDetail.put("ppc_apply_amt", rsyncModelOrderInfo.getPpc_apply_amt());
			mLdDetail.put("accm_apply_amt", rsyncModelOrderInfo.getAccm_apply_amt());
			mLdDetail.put("chg_cd", StringUtils.trimToEmpty(rsyncModelOrderInfo.getChg_cd()));
			mLdDetail.put("medi_mclss_id", StringUtils.trimToEmpty(rsyncModelOrderInfo.getMedi_mclss_id()));
			mLdDetail.put("gift_cd", StringUtils.trimToEmpty(rsyncModelOrderInfo.getGift_cd()));
			mLdDetail.put("create_time", FormatHelper.upDateTime());
			mLdDetail.put("update_time", mLdDetail.get("create_time"));
			
			// 如果换货原订单号字段值存在且和当前订单号不一致，则表示此单是换货单保存一下原订单号
			if(!StringUtils.equals(rsyncModelOrderInfo.getYc_orderform_num(), rsyncModelOrderInfo.getOrg_ord_id())
					&& StringUtils.isNotBlank(rsyncModelOrderInfo.getOrg_ord_id())){
				mLdDetail.put("org_ord_id", rsyncModelOrderInfo.getOrg_ord_id());	
			}
			
			// 如果为空则把LD的订单号加HH前缀作为惠家有订单号
			if(StringUtils.isBlank(mLdDetail.get("order_code"))){
				mLdDetail.put("order_code", "HH" + rsyncModelOrderInfo.getYc_orderform_num());
			}
			
			DbUp.upTable("oc_order_ld_detail").dataInsert(mLdDetail);
		}else if(!mLdDetail.get("orderform_status").equals(rsyncModelOrderInfo.getYc_orderform_status()) || !oddetail.getSkuCode().equals(mLdDetail.get("sku_code"))){ 
			// 明细已存在但是订单状态不一致则更新
			if(StringUtils.isNotBlank(rsyncModelOrderInfo.getYc_orderform_status()) && !"0".equals(oddetail.getGiftFlag())){
				MDataMap updateMap = new MDataMap();
				updateMap.put("zid", mLdDetail.get("zid"));
				updateMap.put("uid", mLdDetail.get("uid"));
				updateMap.put("orderform_status", rsyncModelOrderInfo.getYc_orderform_status());
				updateMap.put("update_time", FormatHelper.upDateTime());
				DbUp.upTable("oc_order_ld_detail").update(updateMap);
			}
		}
		
		long co = (Long) DbUp.upTable("oc_orderinfo").dataSqlOne("SELECT COUNT(*) co from oc_orderinfo where out_order_code=:out_order_code and LEFT(order_code,2)='DD'",new MDataMap("out_order_code", rsyncModelOrderInfo.getYc_orderform_num())).get("co");

		if(rsyncModelOrderInfo.getWeb_ord_id() != null && rsyncModelOrderInfo.getWeb_ord_id().startsWith("DD")){
			co = 1;
		}
		
		if (co > 0) { // 我方下的订单，不再添加
			orderCode = rsyncModelOrderInfo.getWeb_ord_id();
			
			//更新订单状态
			String order_code="";
			String order_status="";
			String buyer_code="";
			List<MDataMap> list=DbUp.upTable("oc_orderinfo").query("buyer_code,order_code,order_status", "", "out_order_code=:out_order_code AND small_seller_code = 'SI2003'", new MDataMap("out_order_code",rsyncModelOrderInfo.getYc_orderform_num()), 0, 1);
			if(list!=null&&list.size()>0){
				order_code=list.get(0).get("order_code");
				order_status=list.get(0).get("order_status");
				buyer_code = list.get(0).get("buyer_code"); 
				
				// 此处使用Last_yc_orderform_status，避免一单多货时明细状态不一致造成的订单状态反复变更
				String state=stateMapper("",rsyncModelOrderInfo.getLast_yc_orderform_status());
				if(!"".equals(state)&&!state.equals(order_status)){ //比对与原来不一样的时候更新，并且在日志表中插入一条记录
					// 如果数据库已经是发货状态则不再更新为未发货
					if("4497153900010002".equals(state) && "4497153900010003".equals(order_status)){
						return;
					}
					// 忽略订单受理的状态，避免因为支付延迟造成订单状态又被更改为未付款
					if("4497153900010001".equals(state)){
						return;
					}
					if(!"4497153900010008".equals(order_status)) {
						DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_status",state,"update_time",DateUtil.getSysDateTimeString(),"order_code",order_code), "order_status,update_time", "order_code");
						DbUp.upTable("lc_orderstatus").dataInsert(new MDataMap("code",order_code,"create_time",DateUtil.getSysDateTimeString(),"create_user","system","old_status",order_status,"now_status",state,"info","TxPurchaseOrderService 1 : 4497153900010002"));
						if("4497153900010006".equals(state) && "4497153900010002".equals(order_status)) {
							//生成退款单
							CreateMoneyService createMoneyService = new CreateMoneyService();
							createMoneyService.creatReturnMoney(order_code,"system","LD系统取消订单");
						}
					}
					if("4497153900010005".equals(order_status)){
						// 订单签收送惠豆
						HjybeanService.addHjyBeanTimer(HjyBeanExecType.SUCCESS, order_code, order_code);
					}
					
					if("4497153900010006".equals(order_status)){
						// 订单变成交易失败时清除用户有效订单缓存
						// PlusSupportMember#ACTIVE_ORDER_SUM
						XmasKv.upFactory(EKvSchema.Member).hdel(buyer_code, "activeOrderSum");
					}
					if("4497153900010006".equals(state)) {
						//取消订单，判断是否是分销单，如果是，写入取消订单分销定时
						if(DbUp.upTable("fh_agent_order_detail").count("order_code",order_code)>0 && DbUp.upTable("za_exectimer").count("exec_info",order_code,"exec_type","449746990029") <= 0) {
							JobExecHelper.createExecInfo("449746990029", order_code, DateUtil.getSysDateTimeString());
						}
					}
				}
				
				String skuCode = StringUtils.isBlank(oddetail.getSkuCode()) ? oddetail.getProductCode() : oddetail.getSkuCode();
				//根据同步订单状态，如果是"4497153900010005"交易成功，则重置oc_orderdetail中的flag_asale 状态值为0，可以在线申请售后
				if("4497153900010005".equals(state)){
					try{
						DbUp.upTable("oc_orderdetail").dataUpdate(new MDataMap("order_code",order_code,"sku_code",skuCode,"flag_asale","0"), "flag_asale", "order_code,sku_code");
					}catch(Exception e){
						e.getStackTrace();
					}
					//订单交易成功，判断是否是分销单，如果是，写入定时计算可提现收入
					if(DbUp.upTable("fh_agent_order_detail").count("order_code",order_code)>0 && DbUp.upTable("za_exectimer").count("exec_info",order_code,"exec_type","449746990028") <= 0) {
						JobExecHelper.createExecInfo("449746990028", order_code,DateUtil.addMinute(28800));
					}
					if(DbUp.upTable("fh_share_order_detail").count("order_code",order_code)>0 && DbUp.upTable("za_exectimer").count("exec_info",order_code,"exec_type","449746990033") <= 0) {
						JobExecHelper.createExecInfo("449746990033", order_code, DateUtil.addMinute(21600));
					}
				}
				//更新状态结束
				if(DbUp.upTable("oc_orderdetail").count("order_code", order_code, "sku_code",skuCode) == 0){
					// 惠家有系统下的单只把同步过来的赠品保存下来，不再保存主品避免主品数据重复
					if(StringUtils.isNotBlank(oddetail.getGiftCd()) && !StringUtils.equalsIgnoreCase("null", oddetail.getGiftCd())){
						// 判断赠品的商品名称不存在时保存赠品
						if(DbUp.upTable("oc_orderdetail").count("order_code", order_code,"product_code", oddetail.getProductCode(), "sku_name",StringUtils.trimToEmpty(rsyncModelOrderInfo.getYc_goods_name())) == 0){
							MDataMap detailMap=new MDataMap();
							detailMap.put("order_code", order_code);
							detailMap.put("sku_code", skuCode);
							detailMap.put("product_code", oddetail.getProductCode());
							detailMap.put("sku_name", rsyncModelOrderInfo.getYc_goods_name());
							detailMap.put("sku_num", String.valueOf(rsyncModelOrderInfo.getYc_goods_count()));
							detailMap.put("detail_code",oddetail.getDetailCode());
							detailMap.put("sku_price",String.valueOf(rsyncModelOrderInfo.getYc_after_base_price()));
							detailMap.put("gift_cd",oddetail.getGiftCd());
							detailMap.put("gift_flag","0");
							detailMap.put("cost_price",oddetail.getCostPrice().toString());
							DbUp.upTable("oc_orderdetail").dataInsert(detailMap);
						}
					}
				}
			}
			
			checkAndUpdateAddress(orderCode, rsyncModelOrderInfo);
			return;
		}
		
		orderCode = oddetail.getOrderCode();
		// 判断订单是否存在，若不存在，则添加新订单+
		if (DbUp.upTable("oc_orderinfo").count("order_code",oddetail.getOrderCode()) == 0) {
			// 如果当前是赠品且库里面没有对应订单则忽略，等主品初始化订单后再插入
			if("0".equals(oddetail.getGiftFlag())) {
				return;
			}
			
			// 新订单只处理初始状态 是已付款的情况
			if(!"20".equals(rsyncModelOrderInfo.getYc_orderform_status())
					&& !"60".equals(rsyncModelOrderInfo.getYc_orderform_status())) {
				return;
			}
			
			// 封装订单信息，插入订单
			OcOrderinfo order = new OcOrderinfo();
			order.setUid(uid);
			order.setOrderCode(oddetail.getOrderCode());
			order.setBuyerCode(sMemberCode);
			order.setDueMoney(money);// 应付款
			order.setFreeTransportMoney(new BigDecimal(rsyncModelOrderInfo.getYc_carriage_money()));// 免运费金额（原始运费）
			order.setOrderMoney(money);// 订单金额
			order.setSellerCode(sMangeCode);
			order.setBigOrderCode("OS"+order.getOrderCode());

			// 如果为 "" 则用家有的状态
			String sm = stateMapper("",rsyncModelOrderInfo.getYc_orderform_status());
//			order.setOrderStatus("".equals(sm) ? rsyncModelOrderInfo.getYc_orderform_status() : sm);// 订单状态
			order.setOrderStatus("".equals(sm) ? "4497153900010002" : sm);// 订单状态 如果没有映射上，则去一律设置为4497153900010005
			
			
			order.setPayedMoney(money);
			order.setProductMoney(money);
			order.setPromotionMoney(new BigDecimal(0));
			order.setCreateTime(DateUtil.getSysDateTimeString());
			order.setUpdateTime(order.getCreateTime());
			order.setProductName(rsyncModelOrderInfo.getYc_goods_name());

			
			String send_bank_cd=rsyncModelOrderInfo.getSend_bank_cd();
			order.setPayType((StringUtils.isBlank(send_bank_cd)||"CD1".equals(send_bank_cd))?"449716200002":"449716200001");// 货到付款
			
			order.setSendType("449715210001");// 快递
			order.setOutOrderCode(rsyncModelOrderInfo.getYc_orderform_num());

			order.setOrderChannel(rsyncModelOrderInfo.getMedi_mclss_id());
			order.setSmallSellerCode("SI2003"); // 同步过来的订单设置默认值

			// 记录下换货单的原订单号，换货的原订单号和当前的订单号不一样
			if(StringUtils.isNotBlank(rsyncModelOrderInfo.getOrg_ord_id()) 
					&& !rsyncModelOrderInfo.getOrg_ord_id().equalsIgnoreCase(rsyncModelOrderInfo.getYc_orderform_num())){
				//DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_code",order.getOrderCode(),"org_ord_id", rsyncModelOrderInfo.getOrg_ord_id()), "org_ord_id", "order_code");
				order.setOrgOrdId(rsyncModelOrderInfo.getOrg_ord_id());
			}
			
			ocoim.insertSelective(order);
			ocorm.insertSelective(oddetail);
			
			OcOrderadress address = new OcOrderadress();
			address.setUid(uid);
			address.setOrderCode(oddetail.getOrderCode());
			address.setAddress(rsyncModelOrderInfo.getDlv_addr_seq());
			address.setAreaCode(rsyncModelOrderInfo.getYc_area());
			address.setReceivePerson(rsyncModelOrderInfo.getYc_claimuser());
			address.setFlagInvoice(0);
			ocom.insertSelective(address);

			BcPurchaseOrder purchaseOrder = new BcPurchaseOrder();
			purchaseOrder.setUid(uid);
			// purchaseOrder.setPurchaseorderCode(order.getCreateTime().substring(0,10).replace("-",
			// "")+"01"+"100022"+WebHelper.upCode("po"));
			// purchaseOrder.setSupplierCode("100022");
			purchaseOrder.setPurchaseorderCode("PO"+ order.getCreateTime().substring(0, 10).replace("-", "")+ "01"+ bConfig("groupcenter.bc_supplier_info_code_homehas")+ WebHelper.upCode("po").substring(2));// 由时间（8位）+业务类型（2位）01+供货商编号（8位）+流水号（4位）组成
			purchaseOrder.setSupplierCode(bConfig("groupcenter.bc_supplier_info_code_homehas"));
			purchaseOrder.setOrderStatus("449746630001");
			purchaseOrder.setCreateTime(order.getCreateTime());
			purchaseOrder.setUpdateTime(order.getCreateTime());
			purchaseOrder.setPurchaseTime(order.getCreateTime().substring(0, 10));
			purchaseOrder.setPurchaseUser(bConfig("groupcenter.bc_supplier_order_user"));
			// purchaseOrder.setPurchaseUser("乾和晟云");
			purchaseOrder.setOrderCode(oddetail.getOrderCode());
			purchaseOrder.setBalanceMoney(money);
			purchaseOrder.setPurchaseMoney(money);
			purchaseOrder.setPurchaseCount(oddetail.getSkuNum());
			bpom.insertSelective(purchaseOrder);

			BcPurchaseDetail purchaseDetail = new BcPurchaseDetail();
			purchaseDetail.setUid(uid);
			purchaseDetail.setPurchaseorderCode(purchaseOrder.getPurchaseorderCode()); // 采购单号
			purchaseDetail.setSupplierCode(purchaseOrder.getSupplierCode());// 供应商编号
			purchaseDetail.setGoodsCode(rsyncModelOrderInfo.getYc_goods_num());
			purchaseDetail.setGoodsName(oddetail.getSkuName());
			purchaseDetail.setGoodsNumber(oddetail.getSkuNum());
			purchaseDetail.setGoodsPrice(oddetail.getSkuPrice());
			purchaseDetail.setCreateTime(DateUtil.getSysDateTimeString());
			purchaseDetail.setSupplierCode(bConfig("groupcenter.bc_supplier_info_code_homehas"));
			// purchaseDetail.setSupplierCode("100022");
			bpdm.insertSelective(purchaseDetail);
			
			OcOrderinfoUpper orderinfoUpper = new OcOrderinfoUpper();
			orderinfoUpper.setBigOrderCode(order.getBigOrderCode());
			orderinfoUpper.setSellerCode(order.getSellerCode());
			orderinfoUpper.setBuyerCode(order.getBuyerCode());
			orderinfoUpper.setOrderMoney(order.getDueMoney());
			orderinfoUpper.setAppVersion(order.getAppVersion());
			orderinfoUpper.setPayedMoney(order.getDueMoney());
			orderinfoUpper.setOrderNum(1);
			orderinfoUpper.setDeleteFlag("0");
			orderinfoUpper.setCreateTime(order.getCreateTime());
			orderinfoUpper.setUpdateTime(order.getCreateTime());
			orderinfoUpper.setAllMoney(order.getOrderMoney());
			orderinfoUpper.setPayType(order.getPayType());
			orderinfoUpperMapper.insertSelective(orderinfoUpper);
			
			if("449716200001".equals(order.getPayType())){
				//在线支付，记录支付方式  目前只记录这些信息
				//OcOrderPay ocOrderPay = new OcOrderPay();
				//ocOrderPay.setOrderCode(order.getOrderCode());
				//ocOrderPay.setCreateTime(order.getCreateTime());
				//ocOrderPay.setPayType(payTypeMapper(send_bank_cd));
				//orderPayMapper.insert(ocOrderPay);
			}
		} else {
			
			// 已存在的订单更正状态，只判断主品的状态
			if(!"0".equals(oddetail.getGiftFlag())) {
				String state=stateMapper("",rsyncModelOrderInfo.getYc_orderform_status());
				String order_status = (String)DbUp.upTable("oc_orderinfo").dataGet("order_status", "", new MDataMap("order_code",oddetail.getOrderCode()));
				if(!"".equals(state)&&!state.equals(order_status)){ //比对与原来不一样的时候更新，并且在日志表中插入一条记录
					// 如果数据库已经是发货状态则不再更新为未发货
					if(!"4497153900010002".equals(state)){
						DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_status",state,"update_time",DateUtil.getSysDateTimeString(),"order_code",oddetail.getOrderCode()), "order_status,update_time", "order_code");
						DbUp.upTable("lc_orderstatus").dataInsert(new MDataMap("code",oddetail.getOrderCode(),"create_time",DateUtil.getSysDateTimeString(),"create_user","system","old_status",order_status,"now_status",state,"info","TxPurchaseOrderService"));
					}
				}
			}
			
			// 订单已经存在，判断插入明细是否存在
			if (DbUp.upTable("oc_orderdetail").count("detail_code",oddetail.getDetailCode(),"order_code", oddetail.getOrderCode()) == 0) {
				// 添加明细
				ocorm.insertSelective(oddetail);

				// 根据 OrderCode 查询 PurchaseorderCode supplier_code
				MDataMap bpoMap = DbUp.upTable("bc_purchase_order").oneWhere("purchaseorder_code,supplier_code", "","order_code=:order_code", "order_code",oddetail.getOrderCode());
				BcPurchaseDetail purchaseDetail = new BcPurchaseDetail();
				purchaseDetail.setUid(uid);
				purchaseDetail.setPurchaseorderCode(bpoMap.get("purchaseorder_code")); // 采购单号
				purchaseDetail.setSupplierCode(bpoMap.get("supplier_code"));// 供应商编号
				purchaseDetail.setGoodsCode(rsyncModelOrderInfo.getYc_goods_num());
				purchaseDetail.setGoodsName(oddetail.getSkuName());
				purchaseDetail.setGoodsNumber(oddetail.getSkuNum());
				purchaseDetail.setGoodsPrice(oddetail.getSkuPrice());
				purchaseDetail.setCreateTime(DateUtil.getSysDateTimeString());
				purchaseDetail.setSupplierCode(bConfig("groupcenter.bc_supplier_info_code_homehas"));
				// purchaseDetail.setSupplierCode("100022");
				bpdm.insertSelective(purchaseDetail);

				// 更新一下钱 和量
				BigDecimal pmoney = new BigDecimal(0);
				BigDecimal orderMoney = oddetail.getSkuPrice().multiply(new BigDecimal(oddetail.getSkuNum()));
				
				
				
				/*
			    set due_money = due_money+#{dueMoney,jdbcType=DECIMAL},
			        	order_money =order_money+ #{orderMoney,jdbcType=DECIMAL},
			          payed_money =payed_money+ #{payedMoney,jdbcType=DECIMAL},
			         product_money =product_money+ #{productMoney,jdbcType=DECIMAL} 
			        where order_code = #{orderCode,jdbcType=VARCHAR};
			        
			        */
			        
			        OcOrderinfoExample ocOrderinfoExample=new OcOrderinfoExample();
			        ocOrderinfoExample.createCriteria().andOrderCodeEqualTo(oddetail.getOrderCode());
			     OcOrderinfo ocOrderinfo=  ocoim.selectByExample(ocOrderinfoExample).get(0);
			        
			     OcOrderinfo ocUpdateOcOrderinfo=new OcOrderinfo();
			     
			     ocUpdateOcOrderinfo.setDueMoney(ocOrderinfo.getDueMoney().add(orderMoney));
			     ocUpdateOcOrderinfo.setOrderMoney(ocOrderinfo.getOrderMoney().add(orderMoney));
			     ocUpdateOcOrderinfo.setPayedMoney(ocOrderinfo.getPayedMoney().add(orderMoney));
			     ocUpdateOcOrderinfo.setProductMoney(ocOrderinfo.getProductMoney().add(orderMoney));
			    
			     
			     ocoim.updateByExampleSelective(ocUpdateOcOrderinfo, ocOrderinfoExample);
				
				/*
				ocoim.updateMoneyByCode(pmoney, orderMoney, orderMoney,
						orderMoney, oddetail.getOrderCode());// 更新订单表
						*/
				bpom.updateMoneyByCode(money, oddetail.getSkuNum(),purchaseDetail.getPurchaseorderCode());// 更新提货单表
				
				
				OcOrderinfoUpperExample orderinfoUpperExample =new OcOrderinfoUpperExample();
				orderinfoUpperExample.createCriteria().andBigOrderCodeEqualTo("OS"+oddetail.getOrderCode());
				
				OcOrderinfoUpper orderinfoUpper = new OcOrderinfoUpper();
				orderinfoUpper.setBigOrderCode("OS"+oddetail.getOrderCode());
				orderinfoUpper.setOrderMoney(ocUpdateOcOrderinfo.getDueMoney());
				orderinfoUpper.setPayedMoney(ocUpdateOcOrderinfo.getDueMoney());
				orderinfoUpper.setUpdateTime(purchaseDetail.getCreateTime());
				orderinfoUpper.setAllMoney(ocUpdateOcOrderinfo.getOrderMoney());
				orderinfoUpperMapper.updateByExampleSelective(orderinfoUpper, orderinfoUpperExample);
				
			}
		}
		
		checkAndUpdateAddress(orderCode, rsyncModelOrderInfo);

	}
	
	// 更新订单地址
	private void checkAndUpdateAddress(String orderCode, RsyncModelOrderInfo rsyncModelOrderInfo) {
		// 忽略不是未发货的订单
		if(DbUp.upTable("oc_orderinfo").count("order_code", orderCode, "order_status", "4497153900010002") == 0) return;
		if(StringUtils.isBlank(rsyncModelOrderInfo.getDlv_rec_mobile())) return;
		if(StringUtils.isBlank(rsyncModelOrderInfo.getYc_street())) return;
		
		com.cmall.dborm.txmapper.OcOrderadressMapper ocom = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_OcOrderadressMapper");
		OcOrderadressExample ocAddressExample = new OcOrderadressExample();
		ocAddressExample.createCriteria().andOrderCodeEqualTo(orderCode);
		List<OcOrderadress> list = ocom.selectByExample(ocAddressExample);
		if(list.isEmpty()) return;
		
		OcOrderadress ocAddress = list.get(0);
		
		if(!ocAddress.getAddress().equals(rsyncModelOrderInfo.getAddr_2())
				|| !ocAddress.getAreaCode().equals(rsyncModelOrderInfo.getYc_street())
				|| !ocAddress.getReceivePerson().equals(rsyncModelOrderInfo.getYc_claimuser())
				|| !ocAddress.getMobilephone().equals(rsyncModelOrderInfo.getDlv_rec_mobile())) {
			OcOrderadress addr = new OcOrderadress();
			addr.setZid(ocAddress.getZid());
			addr.setUid(ocAddress.getUid());
			addr.setAddress(rsyncModelOrderInfo.getAddr_2());
			addr.setAreaCode(rsyncModelOrderInfo.getYc_street());
			addr.setMobilephone(rsyncModelOrderInfo.getDlv_rec_mobile());
			addr.setReceivePerson(rsyncModelOrderInfo.getYc_claimuser());
			ocom.updateByPrimaryKeySelective(addr);
		}
	}

	private String mcodeMapper(String homehas_code) {
		String member_code = "";
		try {
			member_code = (String) DbUp.upTable("mc_extend_info_homehas").dataGet("member_code", "homehas_code=:homehas_code",new MDataMap("homehas_code", homehas_code));
		} catch (Exception e) {
			member_code = "h_" + homehas_code;
		}

		if (member_code == null || "".equals(member_code)) {
			member_code = "h_" + homehas_code;
		}

		return member_code;
	}

	/**
	 * LD 与 ERP 订单状态映射<br>
	 * 若映射不到的字段，不修改ERP状态
	 * 
	 * @param yc_orderform_status
	 *            家有订单状态
	 * @param cod_stat_cd
	 *            配送状态
	 * @return
	 */
	private String stateMapper(String cod_stat_cd, String yc_orderform_status) {
		cod_stat_cd = trim(cod_stat_cd);
		yc_orderform_status = trim(yc_orderform_status);
		// ---------------------配送状态----------------------
		// 30 配送中
		// 31 拒收
		// 40 丢失
		// 41 上门取货丢失
		// 90 签收
		// 91 销售退货
		// ---------------------订单状态----------------------
		// 10 订单受理
		// 20 入款确认
		// 30 欠交订单
		// 40 出库指示
		// 50 出库确定
		// 60 完成出库(出库之后是否签收，根据cod_stat_cd物流状态确定)
		// 91 受理后取消
		// 92 入款后取消
		// 93 取消欠交订单
		// 94 出库指示后取消
		// 99 电子自动取消
		// 97 拒收外呼
		// 98 二次配送
		// 69 再配
		// 70 配送完成
		// 96 拒收
		// --------------------------------

		// 编号: 4497153900010001 名称: 下单成功-未付款
		// 编号: 4497153900010002 名称: 下单成功-未发货
		// 编号: 4497153900010003 名称: 已发货
		// 编号: 4497153900010004 名称: 已收货
		// 编号: 4497153900010005 名称: 交易成功
		// 编号: 4497153900010006 名称: 交易失败

		if (!"".equals(cod_stat_cd)) {// 配送状态
			if ("30".equals(cod_stat_cd) || "40".equals(cod_stat_cd)) {
				return "4497153900010003";
			} else if ("90".equals(cod_stat_cd)) {
				return "4497153900010005";
			} else if ("31".equals(cod_stat_cd)||"91".equals(cod_stat_cd)) {
				return "4497153900010006";
			}
		} else if (!"".equals(yc_orderform_status)) {

			if ( "40".equals(yc_orderform_status)
					|| "50".equals(yc_orderform_status)
					) {
				// if("10".equals(yc_orderform_status)||"20".equals(yc_orderform_status)||"40".equals(yc_orderform_status)||"50".equals(yc_orderform_status)||"30".equals(yc_orderform_status)){
				return "4497153900010002";
			} else if ("91".equals(yc_orderform_status)
					|| "92".equals(yc_orderform_status)
					|| "94".equals(yc_orderform_status)
					|| "93".equals(yc_orderform_status)) {
				return "4497153900010006";
			}
		}

		return "";
	}

	private String trim(Object obj) {
		return obj == null ? "" : obj.toString().trim();
	}
	
	private String getSkuCode(String yc_goods_num,String yc_goods_color,String goods_style){
		MDataMap dataMap = DbUp.upTable("pc_skuinfo").one("product_code",yc_goods_num,"sku_key","color_id="+yc_goods_color+"&style_id="+goods_style,"flag_enable","1");
		if(dataMap!=null){
			return dataMap.get("sku_code");
		}
		return "";
	}
	
	private BigDecimal getProductCost(String yc_goods_num){
		MDataMap dataMap = DbUp.upTable("pc_productinfo").one("product_code",yc_goods_num);
		if(dataMap!=null){
			return new BigDecimal(dataMap.get("cost_price"));
		}
		return new BigDecimal("0");
	}
	
	/**
	 * 在线支付方式映射，对应不上的，原路返回
	 * @param send_bank_cd
	 * @return
	 */
	private String payTypeMapper(String send_bank_cd){
		
		if("54".equals(send_bank_cd)){
			return "449746280003";
		}
		if("WEC".equals(send_bank_cd)){
			return "449746280005";
		}
		return send_bank_cd;
	}
	
	public static void main(String[] args) {
		//System.out.println(new TxPurchaseOrderService().getSkuCode("8016408673", "5", "0"));
		String ld_open_timestamp ="2018-10-17 11:30:00";
		try {
			Date date = DateUtil.convertToDate(ld_open_timestamp, DateUtil.DATE_FORMAT_DATETIME);
			if(date.before(new Date())) {
				System.out.println(ld_open_timestamp);
			}
		} catch (ParseException e) {
		}
	}
}
