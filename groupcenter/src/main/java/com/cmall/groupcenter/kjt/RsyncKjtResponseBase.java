package com.cmall.groupcenter.kjt;

import com.cmall.groupcenter.groupface.IRsyncResponse;

public class RsyncKjtResponseBase  implements IRsyncResponse{

	private String Code="";

	private String Desc="";

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}
}
