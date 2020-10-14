package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 惠美丽—采集数据信息输入类
 * 
 * @author yangrong date: 2014-12-12
 * @version1.3.0
 */
public class SaveClientInfoInput extends RootInput {

	@ZapcomApi(value = "手机型号", remark = "从系统中读出（如果无法读取，填写undefined）", demo = "iPhone Simulator", require = 1)
	private String model = "";

	@ZapcomApi(value = "设备的唯一标识", remark = "Android中使用IMEI，取不到时采用MAC地址；iOS中7.0版本以下采用MAC地址，7.0版本及以上采用IDFA:advertisingIdentifier 。（如果无法读取，填写undefined）", demo = "BC-30-5B-A4-A5-34", require = 1)
	private String uniqid = "";

	@ZapcomApi(value = "手机mac地址", remark = "硬件地址，wifi和3g时唯一，（如果无法读取，填写undefined）", demo = "BC-30-5B-A4-A5-34", require = 1)
	private String mac = "";

	@ZapcomApi(value = "手机操作系统", remark = "Android填写android，iOS填写ios", demo = "ios", require = 1,verify = "in=ios,android")
	private String os = "";

	@ZapcomApi(value = "手机系统型号及版本", remark = "从系统中读出。如：Android4.0、iPhone OS4.2。如果无法读取，填写undefined", demo = "iPhone OS4.2", require = 1)
	private String os_info = "";

	@ZapcomApi(value = "渠道号", remark = "渠道号。各推广渠道的区分（某些项目没有渠道号，填写undefined）", demo = "", require = 1)
	private String channel_code = "";

	@ZapcomApi(value = "应用版本号", remark = "如：1.0.0", demo = "1.0.0", require = 1)
	private String version_number = "";

	@ZapcomApi(value = "屏幕分辨率", remark = "从系统中读出。如：800x480(中间为英文字母x)。（如果无法读取，填写undefined）", demo = "320x480", require = 1)
	private String screen = "";

	@ZapcomApi(value = "手机型号", remark = "运营商sim卡的国家码和网络码：中国=460，移动=00、联通=01、电信=03，例：中国联通=46001。（如果无法读取，填写undefined）", demo = "46001", require = 1)
	private String op = "";

	@ZapcomApi(value = "网络状态", remark = "WiFi时填写wifi，非WiFi环境时填写cellular。（如果无法读取，填写undefined）", demo = "wifi", require = 1,verify = "in=wifi,cellular,undefined")
	private String net_type = "";

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

	

	public String getChannel_code() {
		return channel_code;
	}

	public void setChannel_code(String channel_code) {
		this.channel_code = channel_code;
	}

	public String getVersion_number() {
		return version_number;
	}

	public void setVersion_number(String version_number) {
		this.version_number = version_number;
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

	public String getNet_type() {
		return net_type;
	}

	public void setNet_type(String net_type) {
		this.net_type = net_type;
	}

}
