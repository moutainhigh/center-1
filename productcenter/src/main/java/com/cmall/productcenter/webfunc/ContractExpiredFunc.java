package com.cmall.productcenter.webfunc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class ContractExpiredFunc {

	public int expiredDay(String contractCode) throws Exception {

//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
//		// System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
//		Date dt1 = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
//		Date dt2 = df.parse(Day);
//		Date dt3 = DateUtils.addDays(dt1, 30);
		MDataMap sellerInfoExtend = DbUp.upTable("v_fh_contract_new").one("contract_code",contractCode);
		String expration = sellerInfoExtend.get("expiration");
		if(expration.equals("1")){
			return 1;
		}else if(expration.equals("2")){
			return 1;
		}else{
			return 0;
		}
	}

}
