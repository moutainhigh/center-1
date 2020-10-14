package com.cmall.newscenter.webfunc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapweb.webexport.RootExport;
import com.srnpr.zapweb.webmodel.MPageData;

/**
 * 导出所需的会员信息（小时代）
 * 
 * @author yangrong
 * 
 */
public class MemberForEExport extends RootExport {

	public void export(String sOperateId, HttpServletRequest request,
			HttpServletResponse response) {

		exportExcel(sOperateId, request, response);
		setExportName("member" + FormatHelper.upDateTime(new Date(), "yyMMddHHmmss"));// 修改文件名

		// 修改数据
		MPageData pageData = getPageData();

		List<String> head_list = pageData.getPageHead();
		// 重新写入头
		head_list.clear();
		head_list.add("用户编码");
		head_list.add("登陆账号");
		head_list.add("昵称");
		head_list.add("性别");
		head_list.add("手机号");
		head_list.add("是否马甲");
		head_list.add("状态");
		head_list.add("生日");
		head_list.add("地区");
		head_list.add("注册时间");

		// 重写数据
		List<List<String>> pd = pageData.getPageData();
		List<List<String>> data = new ArrayList<List<String>>();

		for (List<String> ppd : pd) {

			String member_code = ppd.get(0);// 用户编号
			String member_name = ppd.get(3);// 登陆账号
			String name = ppd.get(1);// 昵称
			String gender = ppd.get(2);// 性别
			String phone = ppd.get(3);// 手机号
			String isenble = ppd.get(7);// 是否马甲
			String status = ppd.get(6);// 状态
			String brithday = ppd.get(8);// 生日
			String area = ppd.get(9);// 地区
			String time = ppd.get(4);// 注册时间

			List<String> dd = new ArrayList<String>(34);
			dd.add(member_code);
			dd.add(member_name);
			dd.add(name);
			dd.add(gender);
			dd.add(phone);
			dd.add(isenble);
			dd.add(status);
			dd.add(brithday);
			dd.add(area);
			dd.add(time);

			data.add(dd);
		}

		pageData.setPageData(data);

		doExport();
	}

}
