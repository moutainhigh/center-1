package com.cmall.groupcenter.mlg;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.ParseException;
import org.json.JSONObject;

import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.groupcenter.mlg.config.RsyncConfigGetMlgOrderDetailById;
import com.cmall.groupcenter.mlg.model.RsyncModelGetMlgOrderDetailById;
import com.cmall.groupcenter.mlg.request.RsyncRequestGetMlgOrderdetailById;
import com.cmall.groupcenter.mlg.response.RsyncResponseGetMlgOrderDetailById;
import com.cmall.groupcenter.service.OrderForKJT;
import com.cmall.systemcenter.bill.HexUtil;
import com.cmall.systemcenter.bill.MD5Util;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;


/** 
* @ClassName: RsyncGetMlgOrderDetailById 
* @Description: 同步麦乐购订单物流信息
* @author 张海生
* @date 2015-12-29 上午11:14:49 
*  
*/
public class RsyncGetMlgOrderDetailById extends RsyncMlg<RsyncConfigGetMlgOrderDetailById, RsyncRequestGetMlgOrderdetailById, RsyncResponseGetMlgOrderDetailById>{

	private final static RsyncConfigGetMlgOrderDetailById RSYNC_CONFIG_MLG_TRACE_ORDER = new RsyncConfigGetMlgOrderDetailById();
	
	private RsyncRequestGetMlgOrderdetailById rsyncRequestMlgTraceOrder =new  RsyncRequestGetMlgOrderdetailById();
	
	private RsyncResponseGetMlgOrderDetailById processResult = null;
	
	
	@Override
	public RsyncConfigGetMlgOrderDetailById upConfig() {
		return RSYNC_CONFIG_MLG_TRACE_ORDER;
	}

	@Override
	public RsyncRequestGetMlgOrderdetailById upRsyncRequest() {
		return rsyncRequestMlgTraceOrder;
	}
	
	/**
	 * 调用处理逻辑 返回操作
	 * 
	 * @param sRequestString
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 */
	private String getHttps(String sUrl)
			throws Exception {

		String mrequest=getsignMap();
		WebClientSupport support = new WebClientSupport();
		String sResponseString = support.doGet(sUrl+mrequest);
		return sResponseString;
	}

	private String getsignMap(){
		String version = bConfig("groupcenter.rsync_mlg_version");
		
		String timestamp = String.valueOf(System.currentTimeMillis()/1000);
		String appKey = bConfig("groupcenter.rsync_mlg_app_key");
		String appKey1 = "";
		try {
			appKey1 = URLEncoder.encode(appKey, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String secrectKey = bConfig("groupcenter.rsync_mlg_secrect_key");
		String orderId = rsyncRequestMlgTraceOrder.getOrder_id();
		String signString = secrectKey + appKey + orderId + timestamp + secrectKey;
		signString = HexUtil.toHexString(MD5Util.md5(signString));
		String requestData = "app_key="+appKey1+"&order_id="+orderId+"&timestamp="+timestamp+"&ver="+version+"&sign="+signString;
		return requestData;
	}
	
	/**
	 * 获取请求的url
	 * 
	 * @return
	 */
	private String upRequestUrl() {
		return bConfig("groupcenter.rsync_mlg_url");
	}
	
	/**
	 * 获取调用接口之后的结果
	 * 
	 * @return
	 */
	public RsyncResponseGetMlgOrderDetailById upProcessResult() {
		return processResult;
	}

	
	public boolean doRsync() {

		String sCode = WebHelper.upCode("MLG");

		try {

			String sUrl = upRequestUrl();

			String sRequest = "";
			
			RsyncRequestGetMlgOrderdetailById tRequest = upRsyncRequest();

			MDataMap mInsertMap = new MDataMap();
			// 插入日志表调用的日志记录
			mInsertMap.inAllValues("code", sCode, "rsync_target", upConfig()
					.getRsyncTarget(), "rsync_url", sUrl, "request_data",
					sRequest, "request_time", FormatHelper.upDateTime());
			// 插入日志记录表
			DbUp.upTable("lc_rsync_mlg_log").dataInsert(mInsertMap);

			String sResponseString = getHttps(sUrl);
			mInsertMap.inAllValues("response_time", FormatHelper.upDateTime(),
					"response_data", sResponseString);

			// 更新响应内容和响应时间
			DbUp.upTable("lc_rsync_mlg_log").dataUpdate(mInsertMap,
					"response_time,response_data", "code");

			// IRsyncResponse iRsyncResponse=null;

			RsyncResponseGetMlgOrderDetailById tResponse = upResponseObject();

			JsonHelper<RsyncResponseGetMlgOrderDetailById> responseJsonHelper = new JsonHelper<RsyncResponseGetMlgOrderDetailById>();
			JSONObject ob = new JSONObject(sResponseString);
			sResponseString = String.valueOf(ob.get("Gou.Channel.Order.Response"));
			tResponse = responseJsonHelper.GsonFromJson(sResponseString,
					tResponse);

			processResult = tResponse;
			RsyncResult rsyncResult = doProcess(tRequest, tResponse);

			// 更新处理完成时间
			mInsertMap.inAllValues("process_time", FormatHelper.upDateTime(),
					"process_data", rsyncResult.upJson(), "status_data",
					rsyncResult.getStatusData(), "flag_success",
					rsyncResult.upFlagTrue() ? "1" : "0", "process_num",
					String.valueOf(rsyncResult.getProcessNum()), "success_num",
					String.valueOf(rsyncResult.getSuccessNum()));
			DbUp.upTable("lc_rsync_mlg_log")
					.dataUpdate(
							mInsertMap,
							"process_time,process_data,status_data,flag_success,process_num,success_num",
							"code");

			if (rsyncResult.getResultCode() == 1) {
//				Thread.sleep(20000);
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();

			// 如果失败更新错误日志信息
			MDataMap mErrorMap = new MDataMap();
			mErrorMap.inAllValues("code", sCode, "flag_success", "0",
					"process_time", FormatHelper.upDateTime(),
					"error_expection", e.getMessage());
			DbUp.upTable("lc_rsync_mlg_log").dataUpdate(mErrorMap,
					"process_time,error_expection,flag_success", "code");
		}
		return false;
	}
	
	@Override
	public RsyncResult doProcess(RsyncRequestGetMlgOrderdetailById tRequest,RsyncResponseGetMlgOrderDetailById tResponse) {
		
		RsyncResult rsyncResult = new RsyncResult();
		
		if(!"m6go.api.success".equals(tResponse.getCode())) {
			rsyncResult.setResultCode(918519135);
			rsyncResult.setResultMessage(tResponse.getMessage());
			return rsyncResult;
		}
		
		List<RsyncModelGetMlgOrderDetailById> list = tResponse.getGou_Orders();
		if(list!=null&&list.size()>0){
			String orderId = tResponse.getOrderId();//惠家有订单id
			int count = DbUp.upTable("oc_order_mlg_list").count("order_code",orderId);//查询表中是否已经存了订单的拆单信息
			String mlgOrderStatus = "";//麦乐购对应的惠家有订单状态
			if(count == 0){
				int orderSeq = 1;
				MDataMap orderMap = new MDataMap();
				for (RsyncModelGetMlgOrderDetailById orderDetail : list) {
					mlgOrderStatus = orderStatusMapper(orderDetail.getState());
					String orderCodeSeq = orderId + "#" + orderSeq;
					orderMap.put("order_code_seq", orderCodeSeq);
					orderMap.put("order_code", orderId);
					orderMap.put("order_code_out", orderDetail.getGou_OrderId());
					orderMap.put("create_time", DateUtil.getSysDateTimeString());
					orderMap.put("update_time", DateUtil.getSysDateTimeString());
					orderMap.put("sostatus", String.valueOf(orderDetail.getState()));
					orderMap.put("local_status", mlgOrderStatus);
					String product_code_out = orderDetail.getDetails().getGoodsCode();
					if(StringUtils.isEmpty(product_code_out)) continue;
					List<MDataMap> skuList = DbUp.upTable("pc_skuinfo").queryAll("product_code,sku_code", "", "sell_productcode=:product_code_old", new MDataMap("product_code_old",product_code_out));
					String productCode = "";
					String skuCode = "";
					String skuPrice = "0.00";

					if(skuList != null && skuList.size() == 1){
						MDataMap proMap = skuList.get(0);
						productCode = proMap.get("product_code");
						skuCode = proMap.get("sku_code");
						if(StringUtils.isNotEmpty(skuCode)){
							MDataMap orderSkuMap = DbUp.upTable("oc_orderdetail").oneWhere("sku_price", "", "", "order_code",orderId,"sku_code",skuCode);
							if(orderSkuMap != null){
								skuPrice = orderSkuMap.get("sku_price");
							}
						}
					}
					DbUp.upTable("oc_order_mlg_list").dataInsert(orderMap);//插入拆单信息
					MDataMap proDetail = new MDataMap();
					proDetail.put("order_code_seq", orderCodeSeq);
					proDetail.put("product_code", productCode);
					proDetail.put("sku_code",  skuCode);
					proDetail.put("sku_price", skuPrice);
					proDetail.put("sku_name", orderDetail.getDetails().getGoodsName());
					proDetail.put("sku_num", String.valueOf(orderDetail.getDetails().getCount()));
					proDetail.put("product_code_out", product_code_out);
					proDetail.put("order_code", orderId);
					DbUp.upTable("oc_order_mlg_detail").dataInsert(proDetail);//插入麦乐购订单明细
					this.saveDelivery(orderDetail, orderId, orderCodeSeq);//插入订单物流信息
					orderSeq++;
				}
			}else{
				for (RsyncModelGetMlgOrderDetailById odModel : list) {
					String odId = odModel.getGou_OrderId();
					int odStatus = odModel.getState();
					mlgOrderStatus =  orderStatusMapper(odStatus);
					MDataMap dataMap=DbUp.upTable("oc_order_mlg_list").oneWhere("order_code_seq, order_code, order_code_out,sostatus", "", "order_code_out=:order_code_out", "order_code_out",String.valueOf(odId));
					if(dataMap == null) continue;
					String now=DateUtil.getSysDateTimeString();
					if(StringUtils.isBlank(dataMap.get("sostatus"))||Integer.valueOf(dataMap.get("sostatus"))!=odStatus){
						DbUp.upTable("oc_order_mlg_list").dataUpdate(new MDataMap("sostatus",String.valueOf(odStatus),"local_status",OrderForKJT.orderStatusMapper(odStatus),"order_code_out",String.valueOf(odId),"update_time",now), "sostatus,update_time,local_status", "order_code_out");
					}
					String orderCodeSeq = dataMap.get("order_code_seq");
					if(StringUtils.isNotEmpty(orderCodeSeq)){
						int shipCount = DbUp.upTable("oc_order_shipments").count("order_code_seq", orderCodeSeq);
						if(shipCount == 0){
							this.saveDelivery(odModel, orderId, orderCodeSeq);//插入订单物流信息
						}
					}
				}
			}
			MDataMap orMap= DbUp.upTable("oc_orderinfo").oneWhere("order_code,order_status", "", "order_code=:order_code", "order_code",orderId);
			if (StringUtils.isNotEmpty(mlgOrderStatus) && orMap != null
					&& !mlgOrderStatus.equals(orMap.get("order_status"))
					&& !"4497153900010005".equals(orMap.get("order_status"))) {
				DbUp.upTable("oc_orderinfo").dataUpdate(
						new MDataMap("order_status", mlgOrderStatus,
								"update_time",DateUtil.getSysDateTimeString(),
								"order_code", orderId),
						"order_status,update_time", "order_code");//更新订单状态
				DbUp.upTable("lc_orderstatus").dataInsert(
						new MDataMap("code", orderId, "create_time",DateUtil.getSysDateTimeString(),"create_user",
								"system", "old_status",orMap.get("order_status"), "now_status",mlgOrderStatus,"info","RsyncGetMlgOrderDetailById"));//插入订单状态变更日志
			}
		}
		
		return rsyncResult;
	}
	
	/** 
	* @Description: 保存物流信息
	* @param orderDetail 物流订单信息
	* @param orderCode   订单编号
	* @param orderCodeSeq 小订单编号
	* @author 张海生
	* @date 2015-12-31 上午9:48:50
	* @return void 
	* @throws 
	*/
	public void saveDelivery(RsyncModelGetMlgOrderDetailById orderDetail, String orderCode, String orderCodeSeq){
		String deliveryNum = orderDetail.getDeliveryNumber();//物流单号
		if(StringUtils.isNotEmpty(deliveryNum)){
			String deliveryCode = delevieryMapping(orderDetail.getDeliveryCode());//快递公司代码
			String deliveryName = delevieryNameMapping(deliveryCode);//物流公司名称
			MDataMap shipMap = new MDataMap();
			shipMap.put("order_code", orderCode);
			shipMap.put("logisticse_code", deliveryCode);
			shipMap.put("logisticse_name", deliveryName);
			shipMap.put("waybill", deliveryNum);
			shipMap.put("creator", "system");
			shipMap.put("create_time", DateUtil.getSysDateTimeString());
			shipMap.put("order_code_seq", orderCodeSeq);
			shipMap.put("update_time", DateUtil.getSysDateTimeString());
			shipMap.put("update_user", "system");
			DbUp.upTable("oc_order_shipments").dataInsert(shipMap);//插入物流信息
		}
	}
	
	/**
	 * 订单状态映射
	 * @param ostatus
	 * @return
	 */
	public static String orderStatusMapper(int ostatus){
		
		String status=null;
		
		switch (ostatus) {
		case -4://拒收
			status="4497153900010006";//交易失败
			break;
		case -1://交易失败
			status="4497153900010006";//交易失败
			break;
		case -10://已取消
			status="4497153900010006";//交易失败
			break;
		case 20://已付款
			status="4497153900010002";//下单成功-未发货
			break;
		case 21://备货中
			status="4497153900010002";//下单成功-未发货
			break;
		case 22://配货中
			status="4497153900010002";//下单成功-未发货
			break;
		case 26://已出库
			status="4497153900010003";//已发货
			break;	
		case 30://已发货
			status="4497153900010003";//已发货
			break;		
		case 40://已完成
			status="4497153900010005";//交易成功
			break;	
		default:
			 status="";
			break;
		}
		return status;
	}
	
	/** 
	* @Description: 物流公司代码映射
	* @param deliveryCode 麦乐购的对应的快递公司代码
	* @author 张海生
	* @date 2015-12-31 上午10:20:28
	* @return String 
	* @throws 
	*/
	public static String delevieryMapping(String deliveryCode){
		String companyCode = "";
		if("STO".equals(deliveryCode)){
			companyCode = "shentong";
		}else if("BESTEX".equals(deliveryCode)){
			companyCode = "huitongkuaidi";
		}else if("EMS".equals(deliveryCode)){
			companyCode = "ems";
		}else if("ZJS".equals(deliveryCode)){
			companyCode = "zhaijisong";
		}else if("SF".equals(deliveryCode)){
			companyCode = "shunfeng";
		}else if("YTO".equals(deliveryCode)){
			companyCode = "yuantong";
		}else if("ZTO".equals(deliveryCode)){
			companyCode = "zhongtong";
		}else if("YUNDAEX".equals(deliveryCode)){
			companyCode = "yunda";
		}
		return companyCode;
	}
	
	/** 
	* @Description: 物流公司代码名称
	* @param deliveryCode 物流公司代码
	* @author 张海生
	* @date 2015-12-31 上午10:20:28
	* @return String 
	* @throws 
	*/
	public static String delevieryNameMapping(String deliveryCode){
		String companyName = "";
		if("shentong".equals(deliveryCode)){
			companyName = "申通";
		}else if("huitongkuaidi".equals(deliveryCode)){
			companyName = "汇通";
		}else if("ems".equals(deliveryCode)){
			companyName = "EMS";
		}else if("zhaijisong".equals(deliveryCode)){
			companyName = "宅急送";
		}else if("shunfeng".equals(deliveryCode)){
			companyName = "顺丰";
		}else if("yuantong".equals(deliveryCode)){
			companyName = "圆通";
		}else if("zhongtong".equals(deliveryCode)){
			companyName = "中通";
		}else if("yunda".equals(deliveryCode)){
			companyName = "韵达";
		}
		return companyName;
	}
	
	@Override
	public RsyncResponseGetMlgOrderDetailById upResponseObject() {
		return new RsyncResponseGetMlgOrderDetailById();
	}
}
