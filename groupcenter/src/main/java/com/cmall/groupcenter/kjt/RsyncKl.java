package com.cmall.groupcenter.kjt;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.http.ParseException;

import com.cmall.groupcenter.groupface.IRsyncConfig;
import com.cmall.groupcenter.groupface.IRsyncDateCheck;
import com.cmall.groupcenter.groupface.IRsyncDo;
import com.cmall.groupcenter.groupface.IRsyncRequest;
import com.cmall.groupcenter.groupface.IRsyncResponse;
import com.cmall.groupcenter.homehas.model.RsyncDateCheck;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.systemcenter.bill.HexUtil;
import com.cmall.systemcenter.bill.MD5Util;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 同步考拉接口的基类
 * 
 */
public abstract class RsyncKl extends BaseClass{

	public boolean doRsync() {

	

		try {

			
			MDataMap mInsertMap = new MDataMap();

			RsyncGetKlProductsInfo rg=new RsyncGetKlProductsInfo();
			RsyncResult rsyncResult = rg.doProcess();


			if (rsyncResult.getResultCode() == 1) {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return false;
	}

	private RsyncResult doProcess() {
		// TODO Auto-generated method stub
		return null;
	}





	
	
}
