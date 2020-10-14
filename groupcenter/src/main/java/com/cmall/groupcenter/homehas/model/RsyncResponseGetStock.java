package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;

public class RsyncResponseGetStock implements IRsyncResponse {

	private boolean success;
	private String message;
	private List<Stockinfo> result = new ArrayList<Stockinfo>();
	
	public  static class Stockinfo{
		private String good_id = "";
		private String good_nm = "";
		private String color_id = "";
		private String color_desc = "";
		private String style_id = "";
		private String style_desc = "";
		private String site_no = "";
		private String site_nm = "";
		private String stock_num = "";
		
		public String getGood_id() {
			return good_id;
		}
		public void setGood_id(String good_id) {
			this.good_id = good_id;
		}
		public String getGood_nm() {
			return good_nm;
		}
		public void setGood_nm(String good_nm) {
			this.good_nm = good_nm;
		}
		public String getColor_id() {
			return color_id;
		}
		public void setColor_id(String color_id) {
			this.color_id = color_id;
		}
		public String getColor_desc() {
			return color_desc;
		}
		public void setColor_desc(String color_desc) {
			this.color_desc = color_desc;
		}
		public String getStyle_id() {
			return style_id;
		}
		public void setStyle_id(String style_id) {
			this.style_id = style_id;
		}
		public String getStyle_desc() {
			return style_desc;
		}
		public void setStyle_desc(String style_desc) {
			this.style_desc = style_desc;
		}
		public String getSite_no() {
			return site_no;
		}
		public void setSite_no(String site_no) {
			this.site_no = site_no;
		}
		public String getSite_nm() {
			return site_nm;
		}
		public void setSite_nm(String site_nm) {
			this.site_nm = site_nm;
		}
		public String getStock_num() {
			return stock_num;
		}
		public void setStock_num(String stock_num) {
			this.stock_num = stock_num;
		}
	}

	
	
	
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<Stockinfo> getResult() {
		return result;
	}

	public void setResult(List<Stockinfo> result) {
		this.result = result;
	}
	
	
}
