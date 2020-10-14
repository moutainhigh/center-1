package com.cmall.newscenter.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.dborm.txmapper.PcSkuinfoMapperForD;
import com.cmall.dborm.txmodel.PcSkuinfoExample;
import com.cmall.dborm.txmodel.PcSkuinfoWithBLOBs;
import com.cmall.membercenter.model.MemberInfo;
import com.cmall.membercenter.support.ScoredSupport;
import com.cmall.newscenter.model.CommentdityApp;
import com.cmall.newscenter.model.CommentdityAppPhotos;
import com.cmall.newscenter.model.Product;
import com.cmall.newscenter.model.ProductGroup;
import com.cmall.newscenter.model.Torder;
import com.cmall.newscenter.model.Trial_product;
import com.cmall.newscenter.webfunc.FuncQueryProductInfo;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.common.OrderConst;
import com.cmall.ordercenter.model.OcOrderActivity;
import com.cmall.ordercenter.model.OcOrderPay;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.OrderAddress;
import com.cmall.ordercenter.model.OrderDetail;
import com.cmall.ordercenter.model.Photo;
import com.cmall.ordercenter.txservice.TxOrderService;
import com.cmall.systemcenter.model.AppPhoto;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbface.ITxService;
import com.srnpr.zapweb.helper.WebHelper;

public class TxTryOrderService extends BaseClass implements ITxService {

	/**
	 * 添加订单
	 * @param buyerCode 买家编号
	 * @param skuCode  SKU编号
	 * @param address_id  地址编号
	 * @param amount  商品数量
	 * @param ret
	 */
	public Torder taddOrder(String buyerCode,String skuCode,String address_id,int amount,String appCode,RootResult ret,String areaCode){
		Torder torder=new Torder();
		
		//开始查询SKU 信息
		PcSkuinfoMapperForD pcsm =  BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcSkuinfoMapperForD");
		
		PcSkuinfoExample example = new PcSkuinfoExample();
		example.createCriteria().andSkuCodeEqualTo(skuCode);
		List<PcSkuinfoWithBLOBs> list= pcsm.selectByExampleWithBLOBs(example);
		if(list==null||list.size()<1){// 如果SKU不存在，返回异常
			ret.setResultCode(941901002);
			ret.setResultMessage(bInfo(941901002, skuCode));
			return null;
		}
		PcSkuinfoWithBLOBs pcSkuinfoWithBLOBs=list.get(0);
		
		//设置order 属性
		Order order=new Order();
		order.setBuyerCode(buyerCode);
		order.setOrderCode(WebHelper.upCode(OrderConst.OrderHead));
		order.setDueMoney(new BigDecimal(0));//应付款
		order.setFreeTransportMoney(new BigDecimal(0));//免运费金额（原始运费）
		order.setOrderMoney(new BigDecimal(0));//订单金额
		order.setOrderSource("");//订单来源 无此参数 
		order.setOrderStatus("4497153900010002");//订单状态  下单成功-未发货
		order.setOrderType("449715200003");//订单类型  试用商品
		order.setPayedMoney(new BigDecimal(0));
		order.setPayType("449716200003");//支付方式  积分支付
		order.setProductMoney(new BigDecimal(0));
		order.setPromotionMoney(new BigDecimal(0));
		order.setCreateTime(DateUtil.getSysDateTimeString());
		order.setTransportMoney(new BigDecimal(0));
		order.setSendType("");//配送方式
		order.setUpdateTime(order.getCreateTime());
		order.setSellerCode(appCode);
		
		
		//设置订单详情
		OrderDetail orderDetail=new OrderDetail();
		orderDetail.setSkuCode(skuCode);
		orderDetail.setSkuName(pcSkuinfoWithBLOBs.getSkuName());
		orderDetail.setSkuNum(amount);
		orderDetail.setProductCode(pcSkuinfoWithBLOBs.getProductCode());
		orderDetail.setProductPicUrl(pcSkuinfoWithBLOBs.getSkuPicurl());
		orderDetail.setSkuPrice(new BigDecimal(0));
		orderDetail.setGiftFlag("1");
		
		//查询发送地址并重新设置
		MDataMap adressMap=DbUp.upTable("nc_address").oneWhere("address_name,address_mobile,address_postalcode,address_province,address_city,address_county,address_street", "", "address_id=:address_id", "address_id",address_id);
		
		//插入订单地址信息
		OrderAddress address = new  OrderAddress();
		address.setAddress(adressMap.get("address_province")+" "+adressMap.get("address_city")+" "+adressMap.get("address_county")+" "+adressMap.get("address_street"));//此处无areacode信息，所以将几个地址拼接
		address.setAreaCode(areaCode);
//		address.setEmail(inputParam.getAddress().getEmail());
		address.setFlagInvoice("-1");
//		address.setInvoiceContent(inputParam.getAddress().getInvoiceContent());
//		address.setInvoiceTitle(inputParam.getAddress().getInvoiceTitle());
//		address.setInvoiceType(inputParam.getAddress().getInvoiceType());
		address.setMobilephone(adressMap.get("address_mobile"));
		address.setPostCode(adressMap.get("address_postalcode"));
		address.setReceivePerson(adressMap.get("address_name"));
		address.setRemark("TRY"+address_id);//TRY标记试用商品配送地址的外部配送地址的id 
//		address.setTelephone(inputParam.getAddress().getTelephone());
		
		//从试用商品表中查询积分  appcode  skucode  同一时间 三个条件确定一个活动积分
		MDataMap pdataMap=DbUp.upTable("oc_tryout_products").oneWhere("tryout_price,activity_code", "", "sku_code=:sku_code and app_code=:app_code and start_time<=:orderTime and end_time>=:orderTime", "sku_code",skuCode,"app_code",appCode,"orderTime",order.getCreateTime());
		int tryout_price=Integer.valueOf((String)pdataMap.get("tryout_price"));
		String activity_code=(String)pdataMap.get("activity_code");
		
		//设置支付信息
		OcOrderPay orderPay=new OcOrderPay();
		orderPay.setPayedMoney(tryout_price);//支付的积分
		orderPay.setPayRemark("");//支付备注
		orderPay.setPaySequenceid(activity_code);//流水编号，可以是礼品卡卡号，优惠券号，银行流水号，支付宝流水号, 这里记一个活动编号
		orderPay.setPayType(order.getPayType());
		
		List<OrderDetail> productList=new ArrayList<OrderDetail>(1);
		productList.add(orderDetail);//添加详情
		order.setProductList(productList);//添加商品信息
		order.setAddress(address);//添加地址信息
		List<OcOrderPay> payList=new ArrayList<OcOrderPay>(1);
		payList.add(orderPay);
		order.setOcOrderPayList(payList);//添加支付信息
		
		/*订单活动详情*/
        OcOrderActivity ocOrderActivity = new OcOrderActivity();
		
		ocOrderActivity.setActivityCode(activity_code);
		
		ocOrderActivity.setActivityType("449715400005");
		
		ocOrderActivity.setOrderCode(order.getOrderCode());
		
		ocOrderActivity.setPreferentialMoney(tryout_price);
		
		ocOrderActivity.setProductCode(pcSkuinfoWithBLOBs.getProductCode());
		
		ocOrderActivity.setSkuCode(skuCode);
		
		List<OcOrderActivity> activityList=new ArrayList<OcOrderActivity>(1);
		
		activityList.add(ocOrderActivity);
		
		order.setActivityList(activityList);
		
		
		try {
			
			insertOrderTx(buyerCode,skuCode, appCode, order,tryout_price, ret);
		} catch (Exception e) {
			if(ret.getResultCode()<=1){
				ret.setResultCode(939301082);
				ret.setResultMessage(bInfo(939301082, ret.getResultMessage()));
			}
			return null;
		}
		
		//开始变态的封装返回结果
		setRet(order,pcSkuinfoWithBLOBs,tryout_price, torder,appCode,orderDetail.getProductCode());
				
		return torder;
	}
	
	private void setRet(Order order,PcSkuinfoWithBLOBs sku,int tryout_price,Torder ret,String appCode,String productCode){
		ret.setOrder_id(order.getOrderCode());
		ret.setCreate_time(order.getCreateTime());
		ret.setTotal(tryout_price);
		ret.setState(order.getOrderStatus());
		
		Map<String, Object> map1=DbUp.upTable("oc_orderinfo").dataSqlOne("SELECT count(*) as success_count from oc_orderdetail d LEFT JOIN oc_orderinfo o on d.order_code=o.order_code where o.order_status='4497153900010005' and  d.sku_code=:sku_code ", new MDataMap("sku_code",sku.getSkuCode()));
		Map<String, Object> map2=DbUp.upTable("oc_orderinfo").dataSqlOne("SELECT count(*) as apply_count from oc_orderdetail d LEFT JOIN oc_orderinfo o on d.order_code=o.order_code where o.order_status!='4497153900010005' and  d.sku_code=:sku_code ", new MDataMap("sku_code",sku.getSkuCode()));
		
		Photo photo=new Photo();
		photo.setLarge(sku.getSkuPicurl());
		photo.setThumb(sku.getSkuPicurl());//无所缩略图信息，用原图代替
		
		
		Product product=new Product();
		
		 List<CommentdityApp> comments=new ArrayList<CommentdityApp>();
		 
		 
		 List<MDataMap> mDataMap = new ArrayList<MDataMap>();
		 
		 mDataMap =  DbUp.upTable("nc_order_evaluation").queryByWhere("order_skuid",sku.getSkuCode(),"manage_code",appCode);
		 
		 
		 if(mDataMap.size()!=0){
			 for (MDataMap productDataMap : mDataMap) {
				 
				 CommentdityApp comment = new CommentdityApp();
				 
				 comment.setCreated_at(productDataMap.get("oder_creattime"));
				 
				 comment.setId(productDataMap.get("order_skuid"));
				 
				 comment.setText(productDataMap.get("order_assessment"));
				 
				 String order_name = productDataMap.get("order_name");
				 
				 /*查询评价人信息*/

				String sql = "select * from mc_extend_info_star ms ,mc_member_level mg where ms.member_code =:member_code and ms.member_level = mg.level_code and ms.app_code = mg.manage_code and ms.app_code =:app_code";
					
				MDataMap  plWhereMap = new MDataMap();
					
				plWhereMap.put("member_code",order_name);
				plWhereMap.put("app_code", appCode);
				
				Map<String, Object> plMemberMap = DbUp.upTable("mc_extend_info_star").dataSqlOne(sql, plWhereMap);	
				
				MemberInfo user = new MemberInfo();
				
				user.setMember_code(String.valueOf(plMemberMap.get("member_code").toString()));
				
				user.setNickname(String.valueOf(plMemberMap.get("nickname").toString()));
				
				user.setGroup(BigInteger.valueOf(Long.valueOf(plMemberMap.get("member_group").toString())));
				
				user.setGender(BigInteger.valueOf(Long.valueOf(plMemberMap.get("member_sex").toString())));
				
				user.setLevel(Integer.valueOf(plMemberMap.get("member_level").toString().substring(plMemberMap.get("member_level").toString().length()-4, plMemberMap.get("member_level").toString().length())));
				
				user.setLevel_name(String.valueOf(plMemberMap.get("level_name").toString()));
				
				user.setScore(Integer.valueOf(plMemberMap.get("member_score").toString()));
				
				user.setMobile(String.valueOf(plMemberMap.get("mobile_phone").toString()));
				
	            String Score_unit = bConfig("newscenter.Score_unit");
				
	            user.setScore_unit(Score_unit);
				
	            user.setCreate_time(String.valueOf(plMemberMap.get("create_time").toString()));	
	            
	            AppPhoto avatar = new AppPhoto();
				
				avatar.setLarge(String.valueOf(plMemberMap.get("member_avatar").toString()));
				
				user.setAvatar(avatar);
				
				comment.setUser(user);
					
				List<CommentdityAppPhotos> photos = new ArrayList<CommentdityAppPhotos>();
				
				
                /*获取单图或者多图*/
				
				String album = productDataMap.get("oder_photos");
				
				
				/*判断是否存在图片*/
				if(album!=""){
					
			    String[] str   = album.split("\\|");
			    
				for(int i = 0; i<str.length;i++){
					
					CommentdityAppPhotos comPhoto = new CommentdityAppPhotos();
					
					comPhoto.setLarge(str[i]);
					
					comPhoto.setThumb(str[i]);
					
					photos.add(comPhoto);
					
				}
				comment.setPhotos(photos);
				 
				 
				}
				 comments.add(comment);
			 
			 }
			
			 product.setComments(comments);
		 }
		 
		 
		
		 FuncQueryProductInfo funcQueryProductinfo = new FuncQueryProductInfo();
		
		 
		 List<Trial_product> saleProduct = funcQueryProductinfo.qryProInTryService(sku.getSkuCode(), productCode, appCode);
		
		 Trial_product trial_product = saleProduct.get(0);
		 
		 
		product.setTitle(trial_product.getTitle());
		product.setTrial_price(BigDecimal.valueOf(trial_product.getTrial_price()));
		product.setReason(trial_product.getReason());
		product.setIntro(trial_product.getIntro());
		product.setApply_count(Integer.valueOf(String.valueOf(map2.get("apply_count"))));
		product.setSuccess_count(Integer.valueOf(String.valueOf(map1.get("success_count"))));
		product.setRepo_count(trial_product.getRepo_count());
		product.setDetail_url(trial_product.getDetail_url()); ///// 需要放入的是一个URL，ERP系统中只有详细的描述
		product.setParam_url(trial_product.getParam_url());//产品参数
		product.setTrial_expires(trial_product.getTrial_expires());
		
		product.setPhotos(trial_product.getPhotos());
		ProductGroup productGroup=new ProductGroup();
		productGroup.setAmount(1);
		productGroup.setProduct(product);
		List<ProductGroup> products=new ArrayList<ProductGroup>(1);
		products.add(productGroup);
		
		ret.setProducts(products);
	}
	
	/**
	 * 开始事务添加试用商品订单
	 * @param inputParam
	 * @param skuinfo
	 * @param ret
	 * @throws Exception
	 */
	private void insertOrderTx(String buyerCode,String skuCode,String appCode,Order order,int tryout_price,RootResult ret) throws Exception {
		
		com.cmall.dborm.txmapper.OcTryoutProductsMapper otpm = 
				BeansHelper.upBean("bean_com_cmall_dborm_txmapper_OcTryoutProductsMapper");
		
		TxOrderService txOrderService = BeansHelper.upBean("bean_com_cmall_ordercenter_txservice_TxOrderService");
		
		List<Order> list=new ArrayList<Order>(1);
		list.add(order);
		txOrderService.insertOrder(list, ret, "system", order.getAddress().getAreaCode());
		//txOrderService.insertOrder(list, ret, "system");
		
		//减库存--products
		int ucount=otpm.stockNumSubByCode(skuCode, appCode, order.getCreateTime(), 1);//库存减1
		
		if(ucount<1){
			ret.setResultCode(941901003);
			ret.setResultMessage(bInfo(941901003, skuCode));
			throw new Exception(ret.getResultMessage());
		}
		//减积分
		ScoredSupport scoredSupport=new ScoredSupport();
		if(!scoredSupport.isNotScored(buyerCode, tryout_price,skuCode)){  //试用商品在线下单扣除用户积分
			ret.setResultCode(939301102);
			ret.setResultMessage(bInfo(939301102));
			throw new Exception(ret.getResultMessage());
		}
	}
}
