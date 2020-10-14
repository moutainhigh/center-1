package com.cmall.groupcenter.userinfo.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class UserInfoResult extends RootResultWeb{

	@ZapcomApi(value="用户信息")
	private UserInfo userInfo = new UserInfo();
	
	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public class UserInfo{
		
		public void setRongyunTonken(String rongyunTonken) {
			this.rongyunTonken = rongyunTonken;
		}
		@ZapcomApi(value="会员名称")
		private String memberName = "";
		@ZapcomApi(value="会员编号")
		private String memberCode = "";
		@ZapcomApi(value="头像url")
		private String headIconUrl = "";
		@ZapcomApi(value="昵称")
		private String nickName = "";
		@ZapcomApi(value="生日")
		private String birthday = "";
		@ZapcomApi(value="性别",remark="男：4497465100010002 女：4497465100010003 未知：4497465100010001")
		private String gender = "4497465100010001";
		@ZapcomApi(value="地区")
		private String region = "";

		@ZapcomApi(value="创建时间")
		private String createTime="";
		
		@ZapcomApi(value="二维码url",remark="/cgroup/web/grouppageSecond/showqrcode.html?mem_code=MI150205100001")
		private String qrCodeUrl = "";
		
		@ZapcomApi(value="二维码流url",remark="/cgroup/getTwoDimensionCode?superiorMobileNo=&web_api_key=betagroup&mem=")
		private String qrCodeFlowUrl = "";
		
		@ZapcomApi(value = "融云token")
		private String rongyunTonken="";

		public String getCreateTime() {
			return createTime;
		}

		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}

		public String getRongyunTonken() {
			return rongyunTonken;
		}
		public void headIconMap(String rongyunTonken) {
			this.rongyunTonken = rongyunTonken;
		}
		public String getQrCodeFlowUrl() {
			return qrCodeFlowUrl;
		}
		public void setQrCodeFlowUrl(String qrCodeFlowUrl) {
			this.qrCodeFlowUrl = qrCodeFlowUrl;
		}
		public String getMemberName() {
			return memberName;
		}
		public void setMemberName(String memberName) {
			this.memberName = memberName;
		}
		public String getMemberCode() {
			return memberCode;
		}
		public void setMemberCode(String memberCode) {
			this.memberCode = memberCode;
		}
		public String getHeadIconUrl() {
			return headIconUrl;
		}
		public void setHeadIconUrl(String headIconUrl) {
			this.headIconUrl = headIconUrl;
		}
		public String getNickName() {
			return nickName;
		}
		public void setNickName(String nickName) {
			this.nickName = nickName;
		}
		public String getBirthday() {
			return birthday;
		}
		public void setBirthday(String birthday) {
			this.birthday = birthday;
		}
		public String getGender() {
			return gender;
		}
		public void setGender(String gender) {
			this.gender = gender;
		}
		public String getRegion() {
			return region;
		}
		public void setRegion(String region) {
			this.region = region;
		}
		public String getQrCodeUrl() {
			return qrCodeUrl;
		}
		public void setQrCodeUrl(String qrCodeUrl) {
			this.qrCodeUrl = qrCodeUrl;
		}
	}
}
