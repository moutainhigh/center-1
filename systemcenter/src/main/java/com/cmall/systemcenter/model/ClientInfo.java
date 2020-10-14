package com.cmall.systemcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;



/**
 * 终端信息
 * @author xiegj
 *
 */
public class ClientInfo {
	@ZapcomApi(value = "app版本信息", remark = "app版本信息",require=1, demo = "1.0.0")
	private String app_vision = "";
	
	@ZapcomApi(value = "手机型号", remark = "手机型号", demo = "mi3")
	private String model = "";

	@ZapcomApi(value = "设备的唯一标识", remark = "设备的唯一标识", demo = "advertisingIdentifier")
	private String uniqid = "";

	@ZapcomApi(value = "设备的唯一编号", remark = "设备的唯一编号", demo = "advertisingIdentifier")
	private String idfa = "";
	
	@ZapcomApi(value = "mac地址", remark = "mac地址", demo = "mac")
	private String mac = "";

	@ZapcomApi(value = "手机操作系统", remark = "手机操作系统", demo = "ios")
	private String os = "";

	@ZapcomApi(value = "手机操作系统及版本", remark = "手机操作系统及版本", demo = "os_info")
	private String os_info = "";

	@ZapcomApi(value = "渠道号", remark = "渠道号", demo = "9100701")
	private String from = "";

	@ZapcomApi(value = "屏幕分辨率", remark = "屏幕分辨率", demo = "800x480")
	private String screen = "";

	@ZapcomApi(value = "运营商SIM卡国家码和网络码", remark = "运营商SIM卡国家码和网络码", demo = "46001")
	private String op = "";

	@ZapcomApi(value = "产品名称", remark = "产品名称", demo = "56mv_phone")
	private String product = "";

	@ZapcomApi(value = "网络状态", remark = "网络状态", demo = "wifi")
	private String net_type = "";

	public String getApp_vision() {
		return app_vision;
	}

	public void setApp_vision(String app_vision) {
		this.app_vision = app_vision;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getUniqid() {
		return uniqid;
	}

	public void setUniqid(String uniqid) {
		this.uniqid = uniqid;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOs_info() {
		return os_info;
	}

	public void setOs_info(String os_info) {
		this.os_info = os_info;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getScreen() {
		return screen;
	}

	public void setScreen(String screen) {
		this.screen = screen;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getNet_type() {
		return net_type;
	}

	public void setNet_type(String net_type) {
		this.net_type = net_type;
	}

	public String getIdfa() {
		return idfa;
	}

	public void setIdfa(String idfa) {
		this.idfa = idfa;
	}
	
}
