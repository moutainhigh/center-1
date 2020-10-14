package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 发货输入类
 * @author shiyz
 * date 2014-09-20
 */
public class DeliveryManagementInput extends RootInput {

	@ZapcomApi(value="二维码",demo="@http://-",require=1)
	private String securityCode = "";
	@ZapcomApi(value="快递公司",require=1)
	private String express_company = "";
	@ZapcomApi(value="快递单号",require=1)
	private String express_number = "";
	@ZapcomApi(value="发货数量",require=1)
	private int delivery_number  = 0;
	@ZapcomApi(value="代理品牌分类",require=1)
	private String category_code = "";
	@ZapcomApi(value="代理商编号",require=1)
	private String angent_code = "";
	public String getSecurityCode() {
		return securityCode;
	}
	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
	public String getExpress_company() {
		return express_company;
	}
	public void setExpress_company(String express_company) {
		this.express_company = express_company;
	}
	public String getExpress_number() {
		return express_number;
	}
	public void setExpress_number(String express_number) {
		this.express_number = express_number;
	}
	public int getDelivery_number() {
		return delivery_number;
	}
	public void setDelivery_number(int delivery_number) {
		this.delivery_number = delivery_number;
	}
	public String getCategory_code() {
		return category_code;
	}
	public void setCategory_code(String category_code) {
		this.category_code = category_code;
	}
	public String getAngent_code() {
		return angent_code;
	}
	public void setAngent_code(String angent_code) {
		this.angent_code = angent_code;
	}
}
