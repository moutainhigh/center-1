package com.cmall.groupcenter.func;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;


import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncAddBlacklist extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mWebResult=new MWebResult();
		MDataMap mInputMap = upFieldMap(mDataMap);
		MDataMap insertMap=new MDataMap();
		MDataMap queryMap=new MDataMap();
        Date beginDate=DateHelper.parseDate(mInputMap.get("begin_time"));
        Date endDate=DateHelper.parseDate(mInputMap.get("end_time"));
		if(beginDate.after(endDate)){
			mWebResult.inErrorMessage(918508003);
		}
		if(mWebResult.upFlagTrue()){
			queryMap.inAllValues("product_code",mInputMap.get("product_code"),"end_time",mInputMap.get("begin_time"),"manage_code",mInputMap.get("manage_code"));
			if(DbUp.upTable("gc_product_blacklist").dataCount(" product_code=:product_code and flag_enable=1 and end_time>:end_time and manage_code=:manage_code ", queryMap)!=0){
				mWebResult.inErrorMessage(918508002);
			}
		}
		if(mWebResult.upFlagTrue()){
			insertMap.inAllValues("product_code",mInputMap.get("product_code"),"product_name",
					mInputMap.get("product_name"),"scale_reckon",mInputMap.get("scale_reckon"),
					"begin_time",mInputMap.get("begin_time"),"end_time",mInputMap.get("end_time"),
					"flag_enable","1","operator",UserFactory.INSTANCE.create().getRealName(),
					"operate_time",FormatHelper.upDateTime(),"manage_code",mInputMap.get("manage_code"),
					"create_time",FormatHelper.upDateTime());
			DbUp.upTable("gc_product_blacklist").dataInsert(insertMap);
		}
		
		return mWebResult;
	}

}
