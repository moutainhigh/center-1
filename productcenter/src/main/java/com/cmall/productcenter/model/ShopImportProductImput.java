package com.cmall.productcenter.model;

import com.srnpr.zapcom.topapi.RootInput;

public class ShopImportProductImput extends RootInput {
		//上传excel文件名(先传到文件服务器)
		private String upload_show;
		//售后地址uid
		private String after_sale_address_uid;
		
		public String getUpload_show() {
			return upload_show;
		}
		public void setUpload_show(String upload_show) {
			this.upload_show = upload_show;
		}
		public String getAfter_sale_address_uid() {
			return after_sale_address_uid;
		}
		public void setAfter_sale_address_uid(String after_sale_address_uid) {
			this.after_sale_address_uid = after_sale_address_uid;
		}
		
}
