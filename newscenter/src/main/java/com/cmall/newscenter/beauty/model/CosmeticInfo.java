package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.PicAllInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 帖子详情-》化妆包信息  输出类
 * @author houwen
 * date 2015-01-21
 * @version 1.0
 */
public class CosmeticInfo {
	
	@ZapcomApi(value = "化妆包编码")
	private String  cosmetic_code= "";
	
	@ZapcomApi(value = "名称")
	private String cosmetic_name = "";
	
	@ZapcomApi(value = "价格")
	private String cosmetic_price ="";
	
	@ZapcomApi(value = "失效日期")
	private String disabled_time = "";
	
	@ZapcomApi(value = "数量")
	private String count = ""  ;

	@ZapcomApi(value = "单位")
	private String unit = ""  ;
	
	@ZapcomApi(value="图片")
	private List<PicAllInfo> photo = new ArrayList<PicAllInfo>();
	
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

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public List<PicAllInfo> getPhoto() {
		return photo;
	}

	public void setPhoto(List<PicAllInfo> photo) {
		this.photo = photo;
	}
	
}
