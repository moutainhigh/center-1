package com.cmall.newscenter.model;



import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 用户 - 我参与的活动
 * @author liqiang
 * date 2014-7-23
 * @version 1.0
 */
public class UpdateUserResult extends RootResultWeb{
	@ZapcomApi(value="性别")
	String gender = "";

	@ZapcomApi(value="姓名")
	String name = "";
	
	@ZapcomApi(value="年龄")
	int   age = 0;

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
}
