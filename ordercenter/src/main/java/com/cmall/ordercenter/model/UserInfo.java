package com.cmall.ordercenter.model;

public class UserInfo {
	
	/**
	 * 用户id	第三方账号（测试环境请使用@163.com结尾的账户，正式环境不受限）
	 */
	private String accountId;

	/**
	 * 用户姓名	收货人姓名
	 */
	private String name;

	/**
	 * 手机号码
	 */
	private String mobile;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 省份名称
	 */
	private String provinceName;

	/**
	 * 省份代码	如果传入省份代码，并能获取到省份名称, 
	 * 则省份名称根据该代码获取，否则取provinceName
	 */
	private String provinceCode;

	/**
	 * 城市名称
	 */
	private String cityName;

	/**
	 * 城市代码	如果传入城市代码，并能获取到城市名称，
     * 则城市名称根据该代码获取，否则取cityName
	 */
	private String cityCode;

	/**
	 * 县（区）名称
	 */
	private String districtName;

	/**
	 * 县（区）代码	如果传入县（区）代码，并能获取到县（区）名称，
	 * 则县（区）名称根据该代码获取，否则取districtName
	 */
	private String districtCode;

	/**
	 * 街道地址
	 */
	private String address;

	/**
	 * 邮编
	 */
	private String postCode;

	/**
	 * 固定电话
	 */
	private String phoneNum;

	/**
	 * 区号
	 */
	private String phoneAreaNum;

	/**
	 * 分机号
	 */
	private String phoneExtNum;

	/**
	 * 证件号码	实名信息
	 */
	private String identityId;
	
	/**
	 * 身份证正面
	 */
	private String identityPicFront;
	
	/**
	 * 身份证反面
	 */
	private String identityPicBack;

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getPhoneAreaNum() {
		return phoneAreaNum;
	}

	public void setPhoneAreaNum(String phoneAreaNum) {
		this.phoneAreaNum = phoneAreaNum;
	}

	public String getPhoneExtNum() {
		return phoneExtNum;
	}

	public void setPhoneExtNum(String phoneExtNum) {
		this.phoneExtNum = phoneExtNum;
	}

	public String getIdentityId() {
		return identityId;
	}

	public void setIdentityId(String identityId) {
		this.identityId = identityId;
	}

	public String getIdentityPicFront() {
		return identityPicFront;
	}

	public void setIdentityPicFront(String identityPicFront) {
		this.identityPicFront = identityPicFront;
	}

	public String getIdentityPicBack() {
		return identityPicBack;
	}

	public void setIdentityPicBack(String identityPicBack) {
		this.identityPicBack = identityPicBack;
	}
	
	
}
