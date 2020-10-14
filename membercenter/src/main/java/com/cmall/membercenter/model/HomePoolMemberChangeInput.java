package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class HomePoolMemberChangeInput extends RootInput {

	
	@ZapcomApi(value = "昵称", require = 0, remark = "昵称,最长30位", demo = "123", verify = { "maxlength=30" })
	private String nickname = "";
	
	
	@ZapcomApi(value = "性别", require = 0, remark = "性别,可选值:4497465100010001(保密)，4497465100010002(男),4497465100010003(女)。", demo = "4497465100010001", verify = { "in=4497465100010001,4497465100010002,4497465100010003" })
	private String gender = "";
	
	@ZapcomApi(value = "出生日期", require = 0)
	private String birthday = "";
	
	@ZapcomApi(value = "手机号", require = 0, remark = "手机号", demo = "13333333333", verify = { "base=mobile" })
	private String mobile = "";
	
	@ZapcomApi(value = "邮箱", require = 0, remark = "邮箱")
	private String email = "";
	
	@ZapcomApi(value = "用户头像", require = 0, remark = "用户头像")
	private String headPic = "";

	public String getNickname() {
		return nickname;
	}


	public void setNickname(String nickname) {
		this.nickname = nickname;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	public String getBirthday() {
		return birthday;
	}


	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}


	public String getMobile() {
		return mobile;
	}


	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}

	public String getHeadPic() {
		return headPic;
	}


	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}
	
	
}
