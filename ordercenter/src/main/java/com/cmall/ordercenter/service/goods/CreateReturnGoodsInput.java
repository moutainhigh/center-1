package com.cmall.ordercenter.service.goods;
import java.util.ArrayList;
import java.util.List;

import com.cmall.ordercenter.model.RetuGoodDetailChild;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * ClassName: ReturnMoneyApiInput <br/>
 * date: 2013-9-16 下午9:25:22 <br/>
 * @author hexd
 * @version 
 * @since JDK 1.6
 */
public class CreateReturnGoodsInput extends RootInput
{
	/**
	 * 退货单详情
	 */
	@ZapcomApi(value="退货单详情")
	List<RetuGoodDetailChild> detailList =new ArrayList<RetuGoodDetailChild>();

	/**
	 * 订单编号
	 */
	@ZapcomApi(value="订单编号")
	private String order_code = "";
	/**
	 * 退货原因
	 */
	@ZapcomApi(value="退货原因")
	private String return_reason = "";


	/**
	 * 联系人
	 */
	@ZapcomApi(value="联系人")
	private String contacts = "";
	/**
	 * 运费
	 */
	@ZapcomApi(value="运费")
	private float transport_money = 0;
	/**
	 * 电话
	 */
	@ZapcomApi(value="电话")
	private String mobile = "";
	/**
	 * 地址
	 */
	@ZapcomApi(value="地址")
	private String address = "";
	/**
	 * 图片链接
	 */
	@ZapcomApi(value="图片链接")
	private String pic_url = "";

	
	/**
	 * 描述
	 */
	@ZapcomApi(value="描述")
	private String description = "";


	public List<RetuGoodDetailChild> getDetailList() {
		return detailList;
	}


	public void setDetailList(List<RetuGoodDetailChild> detailList) {
		this.detailList = detailList;
	}


	public String getOrder_code() {
		return order_code;
	}


	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}


	public String getReturn_reason() {
		return return_reason;
	}


	public void setReturn_reason(String return_reason) {
		this.return_reason = return_reason;
	}


	public String getContacts() {
		return contacts;
	}


	public void setContacts(String contacts) {
		this.contacts = contacts;
	}


	public float getTransport_money() {
		return transport_money;
	}


	public void setTransport_money(float transport_money) {
		this.transport_money = transport_money;
	}


	public String getMobile() {
		return mobile;
	}


	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getPic_url() {
		return pic_url;
	}


	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public CreateReturnGoodsInput(List<RetuGoodDetailChild> detailList,
			String order_code, String return_reason, String contacts,
			float transport_money, String mobile, String address,
			String pic_url, String description) {
		super();
		this.detailList = detailList;
		this.order_code = order_code;
		this.return_reason = return_reason;
		this.contacts = contacts;
		this.transport_money = transport_money;
		this.mobile = mobile;
		this.address = address;
		this.pic_url = pic_url;
		this.description = description;
	}


	public CreateReturnGoodsInput() {
		
		super();
		// TODO Auto-generated constructor stub
		
	}
	
}



