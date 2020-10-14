package com.cmall.groupcenter.func;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

public class BeautyOrderUtil extends BaseClass {

	public String getOrderStauts() {

		String sCode = WebHelper.upCode("CLOG");

		LinkedHashMap<String, String> apiparamsMap = new LinkedHashMap<String, String>();

		apiparamsMap.put("dbhost", bConfig("groupcenter.dbhost"));// 添加请求参数——主帐号

		apiparamsMap.put("appkey", bConfig("groupcenter.appkey"));// 添加请求参数——appkey

		apiparamsMap.put("method", "edbTradeGet");// 添加请求参数——接口名称

		apiparamsMap.put("format", bConfig("groupcenter.format"));// 添加请求参数——返回格式

		apiparamsMap
				.put("fields",
						"storage_id,tid,transaction_id,customer_id,distributor_id,shop_name,out_tid,out_pay_tid,voucher_id,shopid,serial_num,order_channel,order_from,buyer_id,buyer_name,type,status,abnormal_status,merge_status,receiver_name,receiver_mobile,phone,province,city,district,address,post,email,is_bill,invoice_name,invoice_situation,invoice_title,invoice_type,invoice_content,pro_totalfee,order_totalfee,reference_price_paid,invoice_fee,cod_fee,other_fee,refund_totalfee,discount_fee,discount,channel_disfee,merchant_disfee,order_disfee,commission_fee,is_cod,point_pay,cost_point,point,superior_point,royalty_fee,external_point,express_no,express,express_coding,online_express,sending_type,real_income_freight,real_pay_freight,gross_weight,gross_weight_freight,net_weight_freight,freight_explain,total_weight,tid_net_weight,tid_time,pay_time,get_time,order_creater,business_man,payment_received_operator,payment_received_time,review_orders_operator,review_orders_time,finance_review_operator,finance_review_time,advance_printer,printer,print_time,is_print,adv_distributer,adv_distribut_time,distributer,distribut_time,is_inspection,inspecter,inspect_time,cancel_operator,cancel_time,revoke_cancel_er,revoke_cancel_time,packager,pack_time,weigh_operator,weigh_time,book_delivery_time,delivery_operator,delivery_time,locker,lock_time,book_file_time,file_operator,file_time,finish_time,modity_time,is_promotion,promotion_plan,out_promotion_detail,good_receive_time,receive_time,verificaty_time,enable_inte_sto_time,enable_inte_delivery_time,alipay_id,alipay_status,pay_mothed,pay_status,platform_status,rate,currency,delivery_status,buyer_message,service_remarks,inner_lable,distributor_mark,system_remarks,other_remarks,message,message_time,is_stock,related_orders,related_orders_type,import_mark,delivery_name,is_new_customer,distributor_level,cod_service_fee,express_col_fee,product_num,sku,item_num,single_num,flag_color,is_flag,taobao_delivery_order_status,taobao_delivery_status,taobao_delivery_method,order_process_time,is_break,breaker,break_time,break_explain,plat_send_status,plat_type,is_adv_sale,provinc_code,city_code,area_code,express_code,last_returned_time,last_refund_time,deliver_centre,deliver_station,is_pre_delivery_notice,jd_delivery_time,Sorting_code,cod_settlement_vouchernumber,originCode,destCode,big_marker,total_num,child_storage_id,child_tid,child_pro_detail_code,child_pro_name,child_specification,child_barcode,child_combine_barcode,child_iscancel,child_isscheduled,child_stock_situation,child_isbook_pro,child_iscombination,child_isgifts,child_gift_num,child_book_storage,child_pro_num,child_send_num,child_refund_num,child_refund_renum,child_inspection_num,child_timeinventory,child_cost_price,child_sell_price,child_average_price,child_original_price,child_sys_price,child_ferght,child_item_discountfee,child_inspection_time,child_weight,child_shopid,child_out_tid,child_out_proid,child_out_prosku,child_proexplain,child_buyer_memo,child_seller_remark,child_distributer,child_distribut_time,child_second_barcode,child_product_no,child_brand_number,child_brand_name,child_book_inventory,child_product_specification,child_discount_amount,child_credit_amount,child_MD5_encryption");

		apiparamsMap.put("v", bConfig("groupcenter.v"));// 添加请求参数——版本号（目前只提供2.0版本）

		List<String> list = sing();

		apiparamsMap.put("sign", list.get(0));

		apiparamsMap.put("timestamp", list.get(1));// 添加请求参数——时间戳

		apiparamsMap.put("slencry", bConfig("groupcenter.slencry"));// 添加请求参数——返回结果是否加密（0，为不加密
																	// ，1.加密）
		apiparamsMap.put("ip", bConfig("groupcenter.ip"));// 添加请求参数——IP地址

		apiparamsMap.put("appscret", bConfig("groupcenter.secret"));// 添加请求参数——appscret

		apiparamsMap.put("token", bConfig("groupcenter.token"));// 添加请求参数——token

		apiparamsMap.put("date_type", "发货日期");

		String staticValue = "";

		staticValue = WebHelper.upStaticValue(new BeautyOrderStatic());

		if ("".equals(staticValue)) {

			apiparamsMap.put("begin_time", "2014-12-11 00:00:00");

			apiparamsMap.put("end_time", list.get(3));
			
		} else {

			apiparamsMap.put("begin_time", staticValue);

			apiparamsMap.put("end_time", list.get(3));// 加1小时方法 );

		}

		apiparamsMap.put("order_type", "");

		apiparamsMap.put("payment_status", "");

		apiparamsMap.put("order_status", "已发货");

		apiparamsMap.put("proce_Status", "");

		apiparamsMap.put("platform_status", "");

		apiparamsMap.put("storage_id", "");

		apiparamsMap.put("shopid", bConfig("groupcenter.shop_id"));

		apiparamsMap.put("express_no", "");

		apiparamsMap.put("express", "");

		apiparamsMap.put("out_tid", "");

		apiparamsMap.put("invoice_isprint", "");

		apiparamsMap.put("invoice_isopen", "");

		apiparamsMap.put("page_no", "");// 分页

		apiparamsMap.put("page_size", "");// 页数量

		apiparamsMap.put("import_mark", "");

		apiparamsMap.put("productInfo_type", "");

		StringBuilder param = new StringBuilder();

		for (Iterator<Map.Entry<String, String>> it = apiparamsMap.entrySet()
				.iterator(); it.hasNext();) {
			Map.Entry<String, String> e = it.next();
			if (e.getKey() != "appscret" && e.getKey() != "token") {
				if (e.getKey() == "fields") {
					try {
						param.append("&").append(e.getKey()).append("=")
								.append(JaxbUtil.encodeUri(e.getValue()));
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					param.append("&").append(e.getKey()).append("=")
							.append(e.getValue());
				}
			}
		}

		String PostData = "";

		PostData = param.toString().substring(1);

		String result = "";

		result = JaxbUtil.getResult(bConfig("groupcenter.testUrl"), PostData);

		String flag = "0";

		if (result.contains("error_response")) {

			flag = "1";

			result = "error";

		} else {

			WebHelper.updateStaticValue(new BeautyOrderStatic(),
					apiparamsMap.get("end_time"));
		}

		MDataMap mInsertMap = new MDataMap();
		// 插入日志表调用的日志记录
		mInsertMap.inAllValues("code", sCode, "request_data", PostData,
				"rsync_url", bConfig("groupcenter.testUrl"), "response_data",
				result, "flag_success", flag, "response_time",
				FormatHelper.upDateTime(), "request_time", list.get(2));
		DbUp.upTable("lc_beauty_orderstauts_log").dataInsert(mInsertMap);

		return result;
	}

	public List<String> sing() {

		List<String> mapList = new ArrayList<String>();

		LinkedHashMap<String, String> apiparamsMap = new LinkedHashMap<String, String>();

		apiparamsMap.put("dbhost", bConfig("groupcenter.dbhost"));// 添加请求参数——主帐号

		apiparamsMap.put("appkey", bConfig("groupcenter.appkey"));// 添加请求参数——appkey

		apiparamsMap.put("method", "edbTradeGet");// 添加请求参数——接口名称

		apiparamsMap.put("format", bConfig("groupcenter.format"));// 添加请求参数——返回格式

		apiparamsMap
				.put("fields",
						"storage_id,tid,transaction_id,customer_id,distributor_id,shop_name,out_tid,out_pay_tid,voucher_id,shopid,serial_num,order_channel,order_from,buyer_id,buyer_name,type,status,abnormal_status,merge_status,receiver_name,receiver_mobile,phone,province,city,district,address,post,email,is_bill,invoice_name,invoice_situation,invoice_title,invoice_type,invoice_content,pro_totalfee,order_totalfee,reference_price_paid,invoice_fee,cod_fee,other_fee,refund_totalfee,discount_fee,discount,channel_disfee,merchant_disfee,order_disfee,commission_fee,is_cod,point_pay,cost_point,point,superior_point,royalty_fee,external_point,express_no,express,express_coding,online_express,sending_type,real_income_freight,real_pay_freight,gross_weight,gross_weight_freight,net_weight_freight,freight_explain,total_weight,tid_net_weight,tid_time,pay_time,get_time,order_creater,business_man,payment_received_operator,payment_received_time,review_orders_operator,review_orders_time,finance_review_operator,finance_review_time,advance_printer,printer,print_time,is_print,adv_distributer,adv_distribut_time,distributer,distribut_time,is_inspection,inspecter,inspect_time,cancel_operator,cancel_time,revoke_cancel_er,revoke_cancel_time,packager,pack_time,weigh_operator,weigh_time,book_delivery_time,delivery_operator,delivery_time,locker,lock_time,book_file_time,file_operator,file_time,finish_time,modity_time,is_promotion,promotion_plan,out_promotion_detail,good_receive_time,receive_time,verificaty_time,enable_inte_sto_time,enable_inte_delivery_time,alipay_id,alipay_status,pay_mothed,pay_status,platform_status,rate,currency,delivery_status,buyer_message,service_remarks,inner_lable,distributor_mark,system_remarks,other_remarks,message,message_time,is_stock,related_orders,related_orders_type,import_mark,delivery_name,is_new_customer,distributor_level,cod_service_fee,express_col_fee,product_num,sku,item_num,single_num,flag_color,is_flag,taobao_delivery_order_status,taobao_delivery_status,taobao_delivery_method,order_process_time,is_break,breaker,break_time,break_explain,plat_send_status,plat_type,is_adv_sale,provinc_code,city_code,area_code,express_code,last_returned_time,last_refund_time,deliver_centre,deliver_station,is_pre_delivery_notice,jd_delivery_time,Sorting_code,cod_settlement_vouchernumber,originCode,destCode,big_marker,total_num,child_storage_id,child_tid,child_pro_detail_code,child_pro_name,child_specification,child_barcode,child_combine_barcode,child_iscancel,child_isscheduled,child_stock_situation,child_isbook_pro,child_iscombination,child_isgifts,child_gift_num,child_book_storage,child_pro_num,child_send_num,child_refund_num,child_refund_renum,child_inspection_num,child_timeinventory,child_cost_price,child_sell_price,child_average_price,child_original_price,child_sys_price,child_ferght,child_item_discountfee,child_inspection_time,child_weight,child_shopid,child_out_tid,child_out_proid,child_out_prosku,child_proexplain,child_buyer_memo,child_seller_remark,child_distributer,child_distribut_time,child_second_barcode,child_product_no,child_brand_number,child_brand_name,child_book_inventory,child_product_specification,child_discount_amount,child_credit_amount,child_MD5_encryption");

		apiparamsMap.put("v", bConfig("groupcenter.v"));// 添加请求参数——版本号（目前只提供2.0版本）

		apiparamsMap.put("slencry", bConfig("groupcenter.slencry"));// 添加请求参数——返回结果是否加密（0，为不加密
																	// ，1.加密）

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String timestamp = sdf.format(new Date());

		apiparamsMap.put("timestamp",
				timestamp.replace("-", "").replace(":", "").replace(" ", "")
						.substring(0, 12));// 添加请求参数——时间戳

		apiparamsMap.put("ip", bConfig("groupcenter.ip"));// 添加请求参数——IP地址

		apiparamsMap.put("appscret", bConfig("groupcenter.secret"));// 添加请求参数——appscret

		apiparamsMap.put("token", bConfig("groupcenter.token"));// 添加请求参数——token

		apiparamsMap.put("date_type", "订货日期");

		String staticValue = "";

		staticValue = WebHelper.upStaticValue(new BeautyOrderStatic());
		
		String end_time = FormatHelper.upDateTime();

		if ("".equals(staticValue)) {

			apiparamsMap.put("begin_time", "2014-12-11 00:00:00");

			apiparamsMap.put("end_time", end_time);

		} else {

			apiparamsMap.put("begin_time", staticValue);

			apiparamsMap.put("end_time", end_time);// 加1小时方法 );

		}


		apiparamsMap.put("order_status", "已发货");

		apiparamsMap.put("shopid", bConfig("groupcenter.shop_id"));

		// 获取数字签名
		String sign = JaxbUtil.md5Signature(apiparamsMap,
				bConfig("groupcenter.appkey"));

		mapList.add(sign);

		mapList.add(apiparamsMap.get("timestamp"));

		mapList.add(timestamp);
		
		mapList.add(end_time);

		return mapList;

	}

	public static String addDate(String day, int x) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制
		Date date = null;
		try {
			date = format.parse(day);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (date == null)
			return "";
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, x);// 24小时制
		date = cal.getTime();
		return format.format(date);
	}

}
