package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 惠美丽—保存与修改化妆包中的妆品输入类
 * 
 * @author yangrong date: 2015-01-25
 * @version1.3.2
 */
public class AddCosmeticBagInput extends RootInput {

	@ZapcomApi(value = "妆品编码" ,remark="传入妆品编码为空的话是新增,否则输入的妆品编码存在的话是修改,不存在不做操作")
	private String cosmetic_code = "";
	
	@ZapcomApi(value = "妆品名称" , require=1)
	private String cosmetic_name = "";
	
	@ZapcomApi(value = "妆品价格")
	private String cosmetic_price = "";
	
	@ZapcomApi(value = "失效日期")
	private String disabled_time = "";
	
	@ZapcomApi(value = "数量")
	private String count = "";
	
	@ZapcomApi(value = "单位")
	private String unit = "";
	
	@ZapcomApi(value = "是否提醒" , remark="是=449746250001   否=449746250002")
	private String iswarn = "";
	
	@ZapcomApi(value = "提醒时间" ,remark="多个以逗号分隔",demo="一个月=449747140001  三个月=449747140002   半年=449747140003  一年=449747140004")
	private String warn_time = "";
	
	@ZapcomApi(value = "图片")
	private List<String>  photo = new ArrayList<String>();
	
	@ZapcomApi(value = "备注")
	private String remark = "";

	public String getCosmetic_code() {
		return cosmetic_code;
	}

	public void setCosmetic_code(String cosmetic_code) {
		this.cosmetic_code = cosmetic_code;
	}

	public String getCosmetic_name() {
		return cosmetic_name;
	}

	public void setCosmetic_name(String cosmetic_name) {
		this.cosmetic_name = cosmetic_name;
	}

	public String getCosmetic_price() {
		return cosmetic_price;
	}

	public void setCosmetic_price(String cosmetic_price) {
		this.cosmetic_price = cosmetic_price;
	}

	public String getDisabled_time() {
		return disabled_time;
	}

	public void setDisabled_time(String disabled_time) {
		this.disabled_time = disabled_time;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getIswarn() {
		return iswarn;
	}

	public void setIswarn(String iswarn) {
		this.iswarn = iswarn;
	}

	public String getWarn_time() {
		return warn_time;
	}

	public void setWarn_time(String warn_time) {
		this.warn_time = warn_time;
	}

	public List<String> getPhoto() {
		return photo;
	}

	public void setPhoto(List<String> photo) {
		this.photo = photo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	
	
}
