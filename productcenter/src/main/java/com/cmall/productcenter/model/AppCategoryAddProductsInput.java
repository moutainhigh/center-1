package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class AppCategoryAddProductsInput extends RootInput {
	
	    @ZapcomApi(value="APP编号")
        String app_code = "";

		@ZapcomApi(value="私有类目编号")
		private String category_code = "";

		private String product_codes = "";
		public String getApp_code() {
			return app_code;
		}
		
		public void setApp_code(String app_code) {
			this.app_code = app_code;
		}
		
		public String getCategory_code() {
			return category_code;
		}

		public void setCategory_code(String category_code) {
			this.category_code = category_code;
		}

		public String getProduct_codes() {
			return product_codes;
		}

		public void setProduct_codes(String product_codes) {
			this.product_codes = product_codes;
		}
}
