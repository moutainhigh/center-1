package com.cmall.productcenter;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class AppCategoryProductsInput extends RootInput {
	
	    @ZapcomApi(value="APP编号")
        String app_code = "";

		@ZapcomApi(value="私有类目编号")
		private String categoryCode = "";

		public String getApp_code() {
			return app_code;
		}
		
		public void setApp_code(String app_code) {
			this.app_code = app_code;
		}
		
		public String getCategoryCode() {
			return categoryCode;
		}

		public void setCategoryCode(String categoryCode) {
			this.categoryCode = categoryCode;
		}

		public AppCategoryProductsInput(String categoryCode,String app_code) {
			super();
			this.categoryCode = categoryCode;
			this.app_code = app_code;
		}

		public AppCategoryProductsInput() {
			super();
		}


}
