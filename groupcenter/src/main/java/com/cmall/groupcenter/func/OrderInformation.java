package com.cmall.groupcenter.func;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class OrderInformation {
	
	/*订单编号*/
	String tid = "";
	
	/*外部平台单号*/
	String out_tid = "";
	
	/*店铺编号*/
	String shop_id = "";
	
	/*仓库编号*/
	int storage_id = 0;
	
	/*买家ID*/
	String buyer_id = "";
	
	/*备注，买家留言*/
	String buyer_msg = "";
	
	/*买家邮件地址*/
	String buyer_email = "";
	
	/*买家支付宝账号*/
	String buyer_alipay = "";
	
	/*客服备注*/
	String seller_remark = "";
	
	/*收货人姓名*/
	String consignee = "";
	
	/*收货地址*/
	String address = "";
	
	/*收货人邮编*/
	String postcode = "";
	
	/*联系电话*/
	String telephone ="";

	/*联系人手机*/
	String mobilPhone = "";
	
	/*收货人省份*/
	String privince = "";
	
	/*收货人市*/
	String city = "";
	
	/*收货人区*/
	String area = "";
	
	/*实收运费*/
	double actual_freight_get;
	
	/*实收参考价*/
	double actual_RP;
	
	/*配送方式*/
	String ship_method = "";
	
	/*快递公司名字*/
	String express = "";
	
	/*开具发票情况*/
	int is_invoiceOpened = 0;
	
	/*发票类型*/
	String invoice_type = "";
	
	/*开票金额*/
	double invoice_money;
	
	/*发票抬头*/
	String invoice_title = "";
	
	/*发票内容*/
	String invoice_msg = "";
	
	/*订单类型*/
	String order_type = "";
	
	/*处理状态，未处理*/
	String process_status = "";
	
	/*处理状态，未付款*/
	String pay_status = "";
	
	/*处理状态，未发货*/
	String deliver_status = "";

	/*是否货到付款*/
	int is_COD ;
	
	/*货到付款服务费*/
	double serverCost_COD;
	
	/*订单总金额*/
	double order_totalMoney;
	
	/*产品总金额*/
	double product_totalMoney;
	
	/*支付方式*/
	String pay_method = "";

	/*支付佣金*/
	double pay_commission;
	
	/*支付积分*/
	int pay_score=0;
	
	/*返点积分*/
	int return_score =0;
	
	/*优惠金额*/
	double favorable_money;
	
	/*支付宝交易号*/
	String alipay_transaction_no = "";
	
	/*外部平台付款单号*/
	String out_payNo = "";
	
	/*外部平台快递方式*/
	String out_express_method = "";
	
	/*外部平台订单装态*/
	String out_order_status = "";
	
	/*订货日期*/
	String   order_date = "";
	
	/*付款日期*/
	String  pay_date = "";
	
	/*完成日期*/
	String finish_date = "";
	
	/*平台类型*/
	String plat_type = "";
	
	/*分销商编号*/
	String distributor_no = "";
	
	/*物流公司*/
	String WuLiu = "";
	
	/*物流单号*/
	String WuLiu_no = "";
	
	/*内部标签*/
	String in_memo = "";
	
	/*其他备注*/
    String other_remark = "";
    
    /*实付运费*/
	double actual_freight_pay ;
	
	/*预付货日期*/
	String ship_date_plan = "";
	
	/*预发货日期*/
	String  deliver_date_plan = "";
	
	/*是否积分换购*/
	int is_scorePay=0;
	
	/*是否开具发票*/
	int is_needInvoice=0;
	
	List<OrderProcuct> product_info = new ArrayList<OrderProcuct>();

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getOut_tid() {
		return out_tid;
	}

	public void setOut_tid(String out_tid) {
		this.out_tid = out_tid;
	}

	public String getShop_id() {
		return shop_id;
	}

	public void setShop_id(String shop_id) {
		this.shop_id = shop_id;
	}

	public int getStorage_id() {
		return storage_id;
	}

	public void setStorage_id(int storage_id) {
		this.storage_id = storage_id;
	}

	public String getBuyer_id() {
		return buyer_id;
	}

	public void setBuyer_id(String buyer_id) {
		this.buyer_id = buyer_id;
	}

	public String getBuyer_msg() {
		return buyer_msg;
	}

	public void setBuyer_msg(String buyer_msg) {
		this.buyer_msg = buyer_msg;
	}

	public String getBuyer_email() {
		return buyer_email;
	}

	public void setBuyer_email(String buyer_email) {
		this.buyer_email = buyer_email;
	}

	public String getBuyer_alipay() {
		return buyer_alipay;
	}

	public void setBuyer_alipay(String buyer_alipay) {
		this.buyer_alipay = buyer_alipay;
	}

	public String getSeller_remark() {
		return seller_remark;
	}

	public void setSeller_remark(String seller_remark) {
		this.seller_remark = seller_remark;
	}

	public String getConsignee() {
		return consignee;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getMobilPhone() {
		return mobilPhone;
	}

	public void setMobilPhone(String mobilPhone) {
		this.mobilPhone = mobilPhone;
	}

	public String getPrivince() {
		return privince;
	}

	public void setPrivince(String privince) {
		this.privince = privince;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public double getActual_freight_get() {
		return actual_freight_get;
	}

	public void setActual_freight_get(double actual_freight_get) {
		this.actual_freight_get = actual_freight_get;
	}

	public double getActual_RP() {
		return actual_RP;
	}

	public void setActual_RP(double actual_RP) {
		this.actual_RP = actual_RP;
	}

	public String getShip_method() {
		return ship_method;
	}

	public void setShip_method(String ship_method) {
		this.ship_method = ship_method;
	}

	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
	}

	public int getIs_invoiceOpened() {
		return is_invoiceOpened;
	}

	public void setIs_invoiceOpened(int is_invoiceOpened) {
		this.is_invoiceOpened = is_invoiceOpened;
	}

	public String getInvoice_type() {
		return invoice_type;
	}

	public void setInvoice_type(String invoice_type) {
		this.invoice_type = invoice_type;
	}

	public double getInvoice_money() {
		return invoice_money;
	}

	public void setInvoice_money(double invoice_money) {
		this.invoice_money = invoice_money;
	}

	public String getInvoice_title() {
		return invoice_title;
	}

	public void setInvoice_title(String invoice_title) {
		this.invoice_title = invoice_title;
	}

	public String getInvoice_msg() {
		return invoice_msg;
	}

	public void setInvoice_msg(String invoice_msg) {
		this.invoice_msg = invoice_msg;
	}

	public String getOrder_type() {
		return order_type;
	}

	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}

	public String getProcess_status() {
		return process_status;
	}

	public void setProcess_status(String process_status) {
		this.process_status = process_status;
	}

	public String getPay_status() {
		return pay_status;
	}

	public void setPay_status(String pay_status) {
		this.pay_status = pay_status;
	}

	public String getDeliver_status() {
		return deliver_status;
	}

	public void setDeliver_status(String deliver_status) {
		this.deliver_status = deliver_status;
	}

	public int getIs_COD() {
		return is_COD;
	}

	public void setIs_COD(int is_COD) {
		this.is_COD = is_COD;
	}

	public double getServerCost_COD() {
		return serverCost_COD;
	}

	public void setServerCost_COD(double serverCost_COD) {
		this.serverCost_COD = serverCost_COD;
	}

	public double getOrder_totalMoney() {
		return order_totalMoney;
	}

	public void setOrder_totalMoney(double order_totalMoney) {
		this.order_totalMoney = order_totalMoney;
	}

	public double getProduct_totalMoney() {
		return product_totalMoney;
	}

	public void setProduct_totalMoney(double product_totalMoney) {
		this.product_totalMoney = product_totalMoney;
	}

	public String getPay_method() {
		return pay_method;
	}

	public void setPay_method(String pay_method) {
		this.pay_method = pay_method;
	}

	public double getPay_commission() {
		return pay_commission;
	}

	public void setPay_commission(double pay_commission) {
		this.pay_commission = pay_commission;
	}

	public int getPay_score() {
		return pay_score;
	}

	public void setPay_score(int pay_score) {
		this.pay_score = pay_score;
	}

	public int getReturn_score() {
		return return_score;
	}

	public void setReturn_score(int return_score) {
		this.return_score = return_score;
	}

	public double getFavorable_money() {
		return favorable_money;
	}

	public void setFavorable_money(double favorable_money) {
		this.favorable_money = favorable_money;
	}

	public String getAlipay_transaction_no() {
		return alipay_transaction_no;
	}

	public void setAlipay_transaction_no(String alipay_transaction_no) {
		this.alipay_transaction_no = alipay_transaction_no;
	}

	public String getOut_payNo() {
		return out_payNo;
	}

	public void setOut_payNo(String out_payNo) {
		this.out_payNo = out_payNo;
	}

	public String getOut_express_method() {
		return out_express_method;
	}

	public void setOut_express_method(String out_express_method) {
		this.out_express_method = out_express_method;
	}

	public String getOut_order_status() {
		return out_order_status;
	}

	public void setOut_order_status(String out_order_status) {
		this.out_order_status = out_order_status;
	}


	public String getOrder_date() {
		return order_date;
	}

	public void setOrder_date(String order_date) {
		this.order_date = order_date;
	}

	public String getPay_date() {
		return pay_date;
	}

	public void setPay_date(String pay_date) {
		this.pay_date = pay_date;
	}

	public String getFinish_date() {
		return finish_date;
	}

	public void setFinish_date(String finish_date) {
		this.finish_date = finish_date;
	}

	public String getShip_date_plan() {
		return ship_date_plan;
	}

	public void setShip_date_plan(String ship_date_plan) {
		this.ship_date_plan = ship_date_plan;
	}

	public String getDeliver_date_plan() {
		return deliver_date_plan;
	}

	public void setDeliver_date_plan(String deliver_date_plan) {
		this.deliver_date_plan = deliver_date_plan;
	}

	public String getPlat_type() {
		return plat_type;
	}

	public void setPlat_type(String plat_type) {
		this.plat_type = plat_type;
	}

	public String getDistributor_no() {
		return distributor_no;
	}

	public void setDistributor_no(String distributor_no) {
		this.distributor_no = distributor_no;
	}

	public String getWuLiu() {
		return WuLiu;
	}

	public void setWuLiu(String wuLiu) {
		WuLiu = wuLiu;
	}

	public String getWuLiu_no() {
		return WuLiu_no;
	}

	public void setWuLiu_no(String wuLiu_no) {
		WuLiu_no = wuLiu_no;
	}

	public String getIn_memo() {
		return in_memo;
	}

	public void setIn_memo(String in_memo) {
		this.in_memo = in_memo;
	}

	public String getOther_remark() {
		return other_remark;
	}

	public void setOther_remark(String other_remark) {
		this.other_remark = other_remark;
	}

	public double getActual_freight_pay() {
		return actual_freight_pay;
	}

	public void setActual_freight_pay(double actual_freight_pay) {
		this.actual_freight_pay = actual_freight_pay;
	}


	public int getIs_scorePay() {
		return is_scorePay;
	}

	public void setIs_scorePay(int is_scorePay) {
		this.is_scorePay = is_scorePay;
	}

	public int getIs_needInvoice() {
		return is_needInvoice;
	}

	public void setIs_needInvoice(int is_needInvoice) {
		this.is_needInvoice = is_needInvoice;
	}

	public List<OrderProcuct> getProduct_info() {
		return product_info;
	}

	public void setProduct_info(List<OrderProcuct> product_info) {
		this.product_info = product_info;
	}

}
