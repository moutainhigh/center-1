package com.cmall.groupcenter.homehas.model;

import java.math.BigDecimal;

/**
 * 违禁品配置信息
 * @author cc
 *
 */
public class RsyncModelContraband {

	private String lrgn_cd;  //大区域编码  350000
	
	private String mrgn_cd;  //中区域编码  350900
	
	private String srgn_cd;  //小区域编码  350922
	
	private String danger_type;  //违禁品属性  枚举值：（A：酒水  B：粉末  C：易燃易爆  D：重量（KG）  E：最大单边距（CM）  F：拆包件  N：无  Y：刀具）
	
	private String toplimit;  //属性值（数字）
	
	private String vl_yn;  //是否有效  Y/N

	public String getLrgn_cd() {
		return lrgn_cd;
	}

	public void setLrgn_cd(String lrgn_cd) {
		this.lrgn_cd = lrgn_cd;
	}

	public String getMrgn_cd() {
		return mrgn_cd;
	}

	public void setMrgn_cd(String mrgn_cd) {
		this.mrgn_cd = mrgn_cd;
	}

	public String getSrgn_cd() {
		return srgn_cd;
	}

	public void setSrgn_cd(String srgn_cd) {
		this.srgn_cd = srgn_cd;
	}

	public String getDanger_type() {
		return danger_type;
	}

	public void setDanger_type(String danger_type) {
		this.danger_type = danger_type;
	}

	public String getToplimit() {
		return toplimit;
	}

	public void setToplimit(String toplimit) {
		this.toplimit = toplimit;
	}

	public String getVl_yn() {
		return vl_yn;
	}

	public void setVl_yn(String vl_yn) {
		this.vl_yn = vl_yn;
	}
	
	
}
