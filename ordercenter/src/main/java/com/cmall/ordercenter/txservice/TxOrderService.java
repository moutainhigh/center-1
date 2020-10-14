package com.cmall.ordercenter.txservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;




import com.cmall.dborm.txmapper.OcOrderinfoMapper;
import com.cmall.dborm.txmapper.OcOrderinfoUpperMapper;
import com.cmall.dborm.txmodel.LcOrderstatus;
import com.cmall.dborm.txmodel.LcStockchange;
import com.cmall.dborm.txmodel.OcOrderActivity;
import com.cmall.dborm.txmodel.OcOrderPay;
import com.cmall.dborm.txmodel.OcOrderadress;
import com.cmall.dborm.txmodel.OcOrderdetail;
import com.cmall.dborm.txmodel.OcOrderinfo;
import com.cmall.dborm.txmodel.OcOrderinfoUpper;
import com.cmall.dborm.txmodel.PcSkuinfoExample;
import com.cmall.dborm.txmodel.PcSkuinfoWithBLOBs;
import com.cmall.dborm.txmodel.ScStoreSkunum;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.OrderAddress;
import com.cmall.ordercenter.model.OrderDetail;
import com.cmall.productcenter.common.SkuCommon;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.enumer.JmsNameEnumer;
import com.cmall.systemcenter.jms.JmsNoticeSupport;
import com.cmall.systemcenter.txservice.TxStockService;
import com.srnpr.xmassystem.support.PlusSupportStock;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbface.ITxService;
import com.srnpr.zapweb.helper.WebHelper;

public class TxOrderService extends BaseClass implements ITxService {
	
	public void insertOrder(List<Order> list,RootResult ret,String operator) throws Exception{
		insertOrder_(list, ret, operator, null);
	}

	/**
	 * 添加订单
	 * @param list
	 * @param ret
	 * @param operator
	 * @param district_code
	 * @throws Exception
	 */
	private void insertOrder_(List<Order> list,RootResult ret,String operator,String district_code) throws Exception {
		
		com.cmall.dborm.txmapper.LcOrderstatusMapper lcom = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_LcOrderstatusMapper");
		com.cmall.dborm.txmapper.LcStockchangeMapper lsom = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_LcStockchangeMapper");
//		com.cmall.dborm.txmapper.PcProductinfoMapper pcpm = 
//				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductinfoMapper");
		com.cmall.dborm.txmapper.PcSkuinfoMapperForD pcsm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcSkuinfoMapperForD");
		com.cmall.dborm.txmapper.OcOrderActivityMapper ocoam = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_OcOrderActivityMapper");
		com.cmall.dborm.txmapper.OcOrderadressMapper ocom = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_OcOrderadressMapper");
		com.cmall.dborm.txmapper.OcOrderdetailMapper ocorm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_OcOrderdetailMapper");
		com.cmall.dborm.txmapper.OcOrderinfoMapper ocoim = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_OcOrderinfoMapper");
		com.cmall.dborm.txmapper.OcOrderPayMapper ocopm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_OcOrderPayMapper");
		
		//循环多个订单信息,插入订单信息
		for(Order order : list){
			
			
			UUID uuid = UUID.randomUUID();
			
			//插入订单基本信息
			OcOrderinfo ooEntity = new OcOrderinfo();

			ooEntity.setUid(UUID.randomUUID().toString().replace("-", ""));
			
			
			String stockInfo="";
			String productName="";
			
			if(order.getProductList()!= null&& order.getProductList().size()>0){
				
				for(OrderDetail od : order.getProductList()){
					
					productName+=od.getSkuName()+"|";
					
					int count =0;
					
					long stockNum = Long.parseLong(String.valueOf(od.getSkuNum()));
					
					if(district_code==null){
						PcSkuinfoWithBLOBs pswb = new PcSkuinfoWithBLOBs();
						PcSkuinfoExample example = new PcSkuinfoExample();
						//赋值
						pswb.setSkuCode(od.getSkuCode());
						pswb.setStockNum(stockNum);
						
						//条件
						//等于
						example.createCriteria().andSkuCodeEqualTo(od.getSkuCode()).andStockNumGreaterThanOrEqualTo(stockNum);
						//大于等于
						//example.createCriteria();
						//减库存
						//如果返回值为 1 则继续，否则抛异常
						count = pcsm.updateByExampleSelective(pswb, example);
					}else{
						//如果存在区域信息，则sku表中的数量 失效，只减仓库中的数量
						
						if("1".equals(od.getGiftFlag())){
							TxStockService txStockService = BeansHelper.upBean("bean_com_cmall_systemcenter_txservice_TxStockService");
							try {
								if(AppConst.MANAGE_CODE_CAPP.equals(order.getSellerCode())){
									stockInfo=txStockService.doChangeStock(ret,stockNum, od.getSkuCode());
								}else if(AppConst.MANAGE_CODE_CYOUNG.equals(order.getSellerCode())){
									stockInfo = txStockService.doChangeStock(ret, stockNum, od.getSkuCode(), AppConst.CYOUNG_STORE_CODE);
								} 
								else{
									stockInfo=txStockService.doReduceStock(district_code, ret, od.getSkuCode(), stockNum);
								}
								count=1;
							} catch (Exception e) {
								count=0;
							}
						}
					}
					
					if(count<=0){
						ret.setResultCode(941901003);
						ret.setResultMessage(bInfo(941901003, od.getSkuCode()));
						throw new Exception(ret.getResultMessage());
					}else{
						
					}
					
					
					//记录库存日志
					LcStockchange lscEntity = new LcStockchange();
					
				
					
					MDataMap insertDatamap = new MDataMap();
					
					lscEntity.setUid(uuid.toString().replace("-", ""));
					lscEntity.setCode(od.getSkuCode());
					lscEntity.setCreateTime(DateUtil.getSysDateTimeString());
//					lscEntity.setCreateUser(operator);
					lscEntity.setCreateUser(order.getBuyerCode());
					lscEntity.setChangeStock(od.getSkuNum());
					lscEntity.setInfo(order.getOrderCode());
					//SkuCommon.SkuStockChangeTypeOrderCommit:SkuCommon.SkuStockChangeTypeOrderRollBack
					lscEntity.setChangeType(SkuCommon.SkuStockChangeTypeOrderCommit);
					
					lsom.insertSelective(lscEntity);
					
					
					//插入订单明细
					OcOrderdetail oddetail = new OcOrderdetail();
					
					oddetail.setUid(UUID.randomUUID().toString().replace("-", ""));
					oddetail.setOrderCode(order.getOrderCode());
					oddetail.setProductCode(od.getProductCode());
					oddetail.setProductPicurl(od.getProductPicUrl());
					oddetail.setSkuName(od.getSkuName());
					oddetail.setSkuNum(od.getSkuNum());
					oddetail.setSkuPrice(od.getSkuPrice());
					oddetail.setSkuCode(od.getSkuCode());
					oddetail.setStoreCode(stockInfo);
					oddetail.setGiftFlag(od.getGiftFlag());
					oddetail.setGiftCd(od.getGift_cd());
					oddetail.setProductCodeOut(od.getProductCodeOut());
					//oddetail.setVirtualMoneyDeduction(BigDecimal.valueOf(od.getVirtualMoneyDeduction()));
					
					ocorm.insertSelective(oddetail);
					
				}
			}
			
			ooEntity.setOrderCode(order.getOrderCode());
			ooEntity.setBuyerCode(order.getBuyerCode());
			ooEntity.setCreateTime(DateUtil.getSysDateTimeString());
			ooEntity.setDueMoney(order.getDueMoney());
			ooEntity.setFreeTransportMoney(order.getFreeTransportMoney());
			ooEntity.setOrderMoney(order.getOrderMoney());
			ooEntity.setOrderSource(order.getOrderSource());
			ooEntity.setOrderStatus(order.getOrderStatus());
			ooEntity.setOrderType(order.getOrderType());
			ooEntity.setPayedMoney(order.getPayedMoney());
			ooEntity.setPayType(order.getPayType());
			ooEntity.setProductMoney(order.getProductMoney());
			ooEntity.setPromotionMoney(order.getPromotionMoney());
			ooEntity.setProductName(productName);
			ooEntity.setSellerCode(order.getSellerCode());
			ooEntity.setSendType(order.getSendType());
			ooEntity.setTransportMoney(order.getTransportMoney());
			ooEntity.setUpdateTime(ooEntity.getCreateTime());
			ooEntity.setOrderChannel(order.getOrderChannel());
			
			ooEntity.setAppVersion(order.getAppVersion());//app 版本
			ooEntity.setOrderSeq(order.getOrderSeq());
			//ooEntity.setOrderPhpcode(order.getOrderPhpcode());
			//ooEntity.setAllVirtualMoneyDeduction(BigDecimal.valueOf(order.getAllVirtualMoneyDeduction()));
			//ooEntity.setVirtualMoneyDeduction(BigDecimal.valueOf(order.getVirtualMoneyDeduction()));
			ocoim.insertSelective(ooEntity);
			
			//插入订单日志信息
			// 调用添加订单的日志。
			LcOrderstatus losEntity = new LcOrderstatus();
			
			losEntity.setCode(order.getOrderCode());
			losEntity.setCreateTime(ooEntity.getCreateTime());
//			losEntity.setCreateUser(operator);
			losEntity.setCreateUser(order.getBuyerCode());
			losEntity.setNowStatus(order.getOrderStatus());
			losEntity.setUid(UUID.randomUUID().toString().replace("-", ""));
			
			lcom.insertSelective(losEntity);
			
			
			//插入订单地址信息
			OcOrderadress address = new  OcOrderadress();
			
			address.setUid(UUID.randomUUID().toString().replace("-", ""));
			address.setAddress(order.getAddress().getAddress());
			address.setAreaCode(order.getAddress().getAreaCode());
			address.setEmail(order.getAddress().getEmail());
			address.setFlagInvoice(Integer.parseInt(order.getAddress().getFlagInvoice()));
			address.setInvoiceContent(order.getAddress().getInvoiceContent());
			address.setInvoiceTitle(order.getAddress().getInvoiceTitle());
			address.setInvoiceType(order.getAddress().getInvoiceType());
			address.setMobilephone(order.getAddress().getMobilephone());
			address.setOrderCode(order.getOrderCode());
			address.setPostcode(order.getAddress().getPostCode());
			address.setReceivePerson(order.getAddress().getReceivePerson());
			address.setRemark(order.getAddress().getRemark());
			address.setTelephone(order.getAddress().getTelephone());
			
			ocom.insertSelective(address);
			//插入订单活动信息
			
			if(order.getActivityList()!= null && order.getActivityList().size()>0){
				
				for(com.cmall.ordercenter.model.OcOrderActivity ooa : order.getActivityList()){

					OcOrderActivity ocativity = new OcOrderActivity();;
					
					ocativity.setActivityCode(ooa.getActivityCode());
					ocativity.setActivityType(ooa.getActivityType());
					ocativity.setOrderCode(order.getOrderCode());
					ocativity.setPreferentialMoney(BigDecimal.valueOf(ooa.getPreferentialMoney()));
					ocativity.setProductCode(ooa.getProductCode());
					ocativity.setSkuCode(ooa.getSkuCode());
					ocativity.setUid(UUID.randomUUID().toString().replace("-", ""));
					ocativity.setOutActiveCode(ooa.getOutActiveCode());
					ocativity.setTicketCode(ooa.getTicketCode());
					ocoam.insertSelective(ocativity);
				}
				
			}
			
			//插入订单支付信息
			if(order.getOcOrderPayList()!=null && order.getOcOrderPayList().size()>0){
				
				for(com.cmall.ordercenter.model.OcOrderPay ooa : order.getOcOrderPayList()){

					OcOrderPay oop = new OcOrderPay();
					
					oop.setCreateTime(ooEntity.getCreateTime());
					oop.setOrderCode(order.getOrderCode());
					oop.setPayedMoney(BigDecimal.valueOf(ooa.getPayedMoney()));
					oop.setPayRemark(ooa.getPayRemark());
					oop.setPaySequenceid(ooa.getPaySequenceid());
					oop.setPayType(ooa.getPayType());
					oop.setUid(UUID.randomUUID().toString().replace("-", ""));
					
					ocopm.insertSelective(oop);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param list
	 * @param ret
	 * @param operator
	 * @param district_code 区域代码，用于减仓库库存 ,若传入为null ，则不按分仓库区域
	 * @throws Exception
	 */
	public void insertOrder(List<Order> list,RootResult ret,String operator,String district_code) throws Exception{
		insertOrder_(list, ret, operator, district_code);
	}
	
	/***
	 * 创建订单记录适<br>
	 * <b>家有汇、惠家有 拆单专用</b>
	 * @param list
	 * @param ret
	 * @param operator
	 */
	public String createOrder(List<Order> list,RootResult ret,String operator) throws Exception {
		
		BigDecimal orderMoneySupper=new BigDecimal(0);//拆单 大订单的总金额
		BigDecimal allMoneySupper=new BigDecimal(0);//拆单 大订单的总的总金额
		
		String bigOrderCode=WebHelper.upCode("OS");//拆单大订单编号
		
		List<OcOrderinfo> orderinfoList = new ArrayList<OcOrderinfo>(list.size());//订单
		List<LcOrderstatus> orderstatusList = new ArrayList<LcOrderstatus>();//订单日志
		List<OcOrderadress> orderadressList = new ArrayList<OcOrderadress>();//订单地址
		List<OcOrderActivity> orderActivityList = new ArrayList<OcOrderActivity>();//订单活动
		List<OcOrderPay> orderPayList = new ArrayList<OcOrderPay>();//订单日志
		List<OrderDetail> orderdetailList = new ArrayList<OrderDetail>();//订单详情
		List<LcStockchange> stockchange = new ArrayList<LcStockchange>();
		
		
		//循环多个订单信息,插入订单信息
		for(Order order : list){
			
			String productName="";
			
			//订单基本信息
			OcOrderinfo orderinfo = new OcOrderinfo();
			orderinfo.setUid(WebHelper.upUuid());
			
			if(order.getProductList()!= null&& order.getProductList().size()>0){
				//订单详情
				for(OrderDetail od : order.getProductList()){
					
					productName+=od.getSkuName()+"|";
					
					orderdetailList.add(od);
					
					//记录库存日志
					LcStockchange lscEntity = new LcStockchange();
					
					lscEntity.setUid(WebHelper.upUuid());
					lscEntity.setCode(od.getSkuCode());
					lscEntity.setCreateTime(DateUtil.getSysDateTimeString());
					lscEntity.setCreateUser(operator);
					lscEntity.setChangeStock(od.getSkuNum());
					lscEntity.setInfo(order.getOrderCode());
					lscEntity.setChangeType(SkuCommon.SkuStockChangeTypeOrderCommit);
					stockchange.add(lscEntity);
					
			}
			
			
			orderinfo.setOrderCode(order.getOrderCode());
			orderinfo.setBuyerCode(order.getBuyerCode());
			orderinfo.setCreateTime(DateUtil.getSysDateTimeString());
			orderinfo.setDueMoney(order.getDueMoney());
			orderinfo.setFreeTransportMoney(order.getFreeTransportMoney());
			orderinfo.setOrderMoney(order.getOrderMoney());
			orderinfo.setOrderSource(order.getOrderSource());
			orderinfo.setOrderStatus(order.getOrderStatus());
			orderinfo.setOrderType(order.getOrderType());
			orderinfo.setPayedMoney(order.getPayedMoney());
			orderinfo.setPayType(order.getPayType());
			orderinfo.setProductMoney(order.getProductMoney());
			orderinfo.setPromotionMoney(order.getPromotionMoney());
			orderinfo.setProductName(productName);
			orderinfo.setSellerCode(order.getSellerCode());
			orderinfo.setSendType(order.getSendType());
			orderinfo.setTransportMoney(order.getTransportMoney());
			orderinfo.setUpdateTime(orderinfo.getCreateTime());
			orderinfo.setOrderChannel(order.getOrderChannel());
			orderinfo.setAppVersion(order.getAppVersion());//app 版本
			orderinfo.setBigOrderCode(bigOrderCode);
			orderinfo.setSmallSellerCode(order.getSmallSellerCode());
			orderinfo.setOrderSeq(order.getOrderSeq());
			orderinfoList.add(orderinfo);
			
			orderMoneySupper=orderMoneySupper.add(orderinfo.getDueMoney());//汇总订单的金额
			allMoneySupper=allMoneySupper.add(orderinfo.getOrderMoney());
			//插入订单日志信息
			// 调用添加订单的日志。
			LcOrderstatus lcOrderstatus = new LcOrderstatus();
			
			lcOrderstatus.setCode(order.getOrderCode());
			lcOrderstatus.setCreateTime(orderinfo.getCreateTime());
//			lcOrderstatus.setCreateUser(operator);
			lcOrderstatus.setCreateUser(order.getBuyerCode());
			lcOrderstatus.setNowStatus(order.getOrderStatus());
			lcOrderstatus.setUid(order.getUid());
			orderstatusList.add(lcOrderstatus);
			
			
			//插入订单地址信息
			OcOrderadress address = new  OcOrderadress();
			OrderAddress orderAddress=order.getAddress();
			address.setUid(WebHelper.upUuid());
			address.setAddress(orderAddress.getAddress());
			address.setAreaCode(orderAddress.getAreaCode());
			address.setEmail(orderAddress.getEmail());
			address.setFlagInvoice(Integer.parseInt(orderAddress.getFlagInvoice()));
			address.setInvoiceContent(orderAddress.getInvoiceContent());
			address.setInvoiceTitle(orderAddress.getInvoiceTitle());
			address.setInvoiceType(orderAddress.getInvoiceType());
			address.setMobilephone(orderAddress.getMobilephone());
			address.setOrderCode(order.getOrderCode());
			address.setPostcode(orderAddress.getPostCode());
			address.setReceivePerson(orderAddress.getReceivePerson());
			address.setRemark(orderAddress.getRemark());
			address.setTelephone(orderAddress.getTelephone());
			orderadressList.add(address);
			
			
			//插入订单活动信息
			if(order.getActivityList()!= null && order.getActivityList().size()>0){
				
				for(com.cmall.ordercenter.model.OcOrderActivity ooa : order.getActivityList()){

					OcOrderActivity ocativity = new OcOrderActivity();
					
					ocativity.setActivityCode(ooa.getActivityCode());
					ocativity.setActivityType(ooa.getActivityType());
					ocativity.setOrderCode(order.getOrderCode());
					ocativity.setPreferentialMoney(BigDecimal.valueOf(ooa.getPreferentialMoney()));
					ocativity.setProductCode(ooa.getProductCode());
					ocativity.setSkuCode(ooa.getSkuCode());
					ocativity.setUid(WebHelper.upUuid());
					ocativity.setOutActiveCode(ooa.getOutActiveCode());
					ocativity.setTicketCode(ooa.getTicketCode());
					orderActivityList.add(ocativity);
				}
			}
			
			//插入订单支付信息
			if(order.getOcOrderPayList()!=null && order.getOcOrderPayList().size()>0){
				
				for(com.cmall.ordercenter.model.OcOrderPay ooa : order.getOcOrderPayList()){

					OcOrderPay oop = new OcOrderPay();
					
					oop.setCreateTime(orderinfo.getCreateTime());
					oop.setOrderCode(order.getOrderCode());
					oop.setPayedMoney(BigDecimal.valueOf(ooa.getPayedMoney()));
					oop.setPayRemark(ooa.getPayRemark());
					oop.setPaySequenceid(ooa.getPaySequenceid());
					oop.setPayType(ooa.getPayType());
					oop.setUid(WebHelper.upUuid());
					orderPayList.add(oop);
					
				}
			}
		}
			
			
			//添加消息监控的东东
			JmsNoticeSupport.INSTANCE.sendToplic(JmsNameEnumer.OnProductMonitor, order.getOrderCode(), new MDataMap());
			
		}
		//进入拆单的大订单流程
		if(list.size()>0){
			Order order=list.get(0);
			String appCode=order.getSellerCode();
			
			//定义大订单信息
			OcOrderinfoUpper orderinfoUpper = new OcOrderinfoUpper();
			orderinfoUpper.setUid(WebHelper.upUuid());
			orderinfoUpper.setSellerCode(appCode);
			orderinfoUpper.setAppVersion(order.getAppVersion());
			orderinfoUpper.setBigOrderCode(bigOrderCode);
			orderinfoUpper.setBuyerCode(order.getBuyerCode());
			orderinfoUpper.setCreateTime(DateUtil.getSysDateTimeString());
			orderinfoUpper.setDeleteFlag("0");
			orderinfoUpper.setUpdateTime(orderinfoUpper.getCreateTime());
			orderinfoUpper.setOrderNum(list.size());
			orderinfoUpper.setOrderMoney(orderMoneySupper);
			orderinfoUpper.setDueMoney(orderinfoUpper.getOrderMoney());
			orderinfoUpper.setPayedMoney(BigDecimal.ZERO);
			orderinfoUpper.setPayType(order.getPayType());
			orderinfoUpper.setAllMoney(allMoneySupper);
			
			saveOrderInfo(orderinfoList, orderstatusList, orderadressList, orderActivityList, orderPayList, orderdetailList, stockchange, orderinfoUpper, ret);
		}
		
		return bigOrderCode;
	}
	
	private void saveOrderInfo(List<OcOrderinfo> orderinfoList,
			List<LcOrderstatus> orderstatusList,
			List<OcOrderadress> orderadressList,
			List<OcOrderActivity> orderActivityList,
			List<OcOrderPay> orderPayList,
			List<OrderDetail> orderdetailList,
			List<LcStockchange> stockchange,OcOrderinfoUpper orderinfoUpper,RootResult ret) throws Exception {

		com.cmall.dborm.txmapper.LcOrderstatusMapper lcom = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_LcOrderstatusMapper");
		com.cmall.dborm.txmapper.LcStockchangeMapper lsom = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_LcStockchangeMapper");
		com.cmall.dborm.txmapper.OcOrderActivityMapper ocoam = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_OcOrderActivityMapper");
		com.cmall.dborm.txmapper.OcOrderadressMapper ocom = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_OcOrderadressMapper");
		com.cmall.dborm.txmapper.OcOrderdetailMapper ocorm = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_OcOrderdetailMapper");
		com.cmall.dborm.txmapper.OcOrderinfoMapper ocoim = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_OcOrderinfoMapper");
		com.cmall.dborm.txmapper.OcOrderPayMapper ocopm = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_OcOrderPayMapper");
		TxStockService txStockService = BeansHelper.upBean("bean_com_cmall_systemcenter_txservice_TxStockService");
		OcOrderinfoUpperMapper orderinfoUpperMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_OcOrderinfoUpperMapper");//大订单信息
		
		for (OrderDetail orderdetail : orderdetailList) {
			
			int count=0;
			String stockInfo="";
			//如果是赠品，不参与减库存
			if("1".equals(orderdetail.getGiftFlag())){
				try {
//					stockInfo=txStockService.doReduceStock(orderdetail.getSkuCode(), orderdetail.getSkuNum(), orderdetail.getValidateFlag(), orderdetail.getPrchType(), orderdetail.getOaSiteNo(), ret);
					//		谢冠杰修改--减库存走缓存
					stockInfo=new PlusSupportStock().subtractSkuStock(orderdetail.getOrderCode(),orderdetail.getSkuCode(), orderdetail.getSkuNum());
					count=1;
				} catch (Exception e) {
				}
				
				if(count<=0||StringUtils.isBlank(stockInfo)){
					ret.setResultCode(941901003);
					ret.setResultMessage(bInfo(941901003, orderdetail.getSkuCode()));
					throw new Exception(ret.getResultMessage());
				}
			}
			
			//订单详情
			OcOrderdetail oddetail = new OcOrderdetail();
			
			oddetail.setUid(WebHelper.upUuid());
			oddetail.setOrderCode(orderdetail.getOrderCode());
			oddetail.setProductCode(orderdetail.getProductCode());
			oddetail.setProductPicurl(orderdetail.getProductPicUrl());
			oddetail.setSkuName(orderdetail.getSkuName());
			oddetail.setSkuNum(orderdetail.getSkuNum());
			oddetail.setSkuPrice(orderdetail.getSkuPrice());
			oddetail.setSkuCode(orderdetail.getSkuCode());
			oddetail.setStoreCode(stockInfo);
			oddetail.setGiftFlag(orderdetail.getGiftFlag());
			oddetail.setGiftCd(orderdetail.getGift_cd());
			oddetail.setSaveAmt(orderdetail.getSaveAmt());
			oddetail.setCostPrice(orderdetail.getCostPrice());
			oddetail.setProductCodeOut(orderdetail.getProductCodeOut());
			oddetail.setGroupPrice(orderdetail.getGroupPrice());
			oddetail.setShowPrice(orderdetail.getShowPrice());
			oddetail.setCouponPrice(orderdetail.getCouponPrice());
			ocorm.insertSelective(oddetail);
		}
		
		//订单信息
		for (OcOrderinfo orderinfo : orderinfoList) {
			ocoim.insertSelective(orderinfo);
		}
		
		for (LcOrderstatus orderstatus : orderstatusList) {
			lcom.insertSelective(orderstatus);
		}
		
		for (OcOrderadress orderadress : orderadressList) {
			ocom.insertSelective(orderadress);
		}
		
		for (OcOrderActivity activity : orderActivityList) {
			ocoam.insertSelective(activity);
		}
		
		for (OcOrderPay orderPay : orderPayList) {
			ocopm.insertSelective(orderPay);
		}
		
		for (LcStockchange lcStockchange : stockchange) {
			lsom.insertSelective(lcStockchange);
		}
		
		orderinfoUpperMapper.insertSelective(orderinfoUpper);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 创建事务，取消订单或下单失败时 回滚区域仓库库存,分库专用[惠美丽 惠家有]
	 * @param list
	 * @throws Exception
	 */
	public void doAddStockNum (List<OrderDetail> list) throws Exception{
		
		com.cmall.dborm.txmapper.ScStoreSkunumMapper scStoreSkunumMapper = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_ScStoreSkunumMapper");
		
		com.cmall.dborm.txmapper.LcStockchangeMapper lcStockchangeMapper = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_LcStockchangeMapper");
		for (OrderDetail orderDetail : list) {
			
			String storeCode = orderDetail.getStoreCode();
			String store_stocks [] =storeCode.split(",");
			
			for (String store_stock : store_stocks) {
				String ss [] = store_stock.split("_");
				String store =ss [0];
				int stockNum = Integer.valueOf(ss[1]);
				
				String skuCode = orderDetail.getSkuCode();
				
				ScStoreSkunum scStoreSkunum=new ScStoreSkunum();
				scStoreSkunum.setSkuCode(skuCode);
				scStoreSkunum.setStockNum(Long.valueOf(stockNum));
				scStoreSkunum.setStoreCode(store);
				scStoreSkunumMapper.addStock_num(scStoreSkunum);//回滚库存
				
				//查询一下 当前库存
				BigDecimal stock_num=(BigDecimal)DbUp.upTable("sc_store_skunum").dataGet("stock_num", "store_code=:store_code and sku_code=:sku_code ", new MDataMap("store_code",store,"sku_code",skuCode));
				int stock_num_now= Integer.valueOf(String.valueOf(stock_num));
				
				LcStockchange lcStockchange=new LcStockchange();
				lcStockchange.setUid(UUID.randomUUID().toString().replace("-", ""));
				lcStockchange.setCode(skuCode); //参照就数据 都是sku编号
				lcStockchange.setInfo(orderDetail.getOrderCode());//参照旧数据 都是订单编号
				lcStockchange.setCreateUser(DateUtil.getSysDateTimeString());
				lcStockchange.setCreateUser("system");
				lcStockchange.setChangeStock(stockNum);
				lcStockchange.setOldStock(stock_num_now-stockNum);
				lcStockchange.setNowStock(stock_num_now);
				lcStockchange.setStockArea(store);
				lcStockchange.setChangeType(SkuCommon.SkuStockChangeTypeOrderRollBack);
				//当前无区域概念
				lcStockchangeMapper.insertSelective(lcStockchange);
				
				
			}
			
		}
	}
	
}
