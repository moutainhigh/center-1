package com.cmall.groupcenter.txservice;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmapper.groupcenter.GcAccountMarketingMapper;
import com.cmall.dborm.txmodel.groupcenter.GcAccountMarketing;
import com.cmall.dborm.txmodel.groupcenter.GcAccountMarketingExample;
import com.cmall.groupcenter.accountmarketing.util.ReadExcelUtil;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webmodel.MWebResult;

public class TxAccountMarketingService {

	
	/**
	 * 插入营销报表
	 * 
	 * @param sAccountCode
	 * @param sManageCode
	 * @return
	 */
	public MWebResult insertAccountMarketing(String fileRemoteUrl){
		MWebResult mWebResult = new MWebResult();
		
		try {
			if(StringUtils.isBlank(fileRemoteUrl)){
				throw new Exception("下载地址不存在");
			}
			List<GcAccountMarketing> dataLists = downloadAndAnalysisFile(fileRemoteUrl);
			
			GcAccountMarketingMapper gcAccountMarketingMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcAccountMarketingMapper");
			for(GcAccountMarketing gcAccountMarketing : dataLists){
				gcAccountMarketing.setUid(WebHelper.upUuid());
				gcAccountMarketing.setCreateTime(FormatHelper.upDateTime());
				gcAccountMarketing.setOperator(UserFactory.INSTANCE.create().getLoginName());
				
				String mobileNo = gcAccountMarketing.getMobileno().trim();
				if(mobileNo.lastIndexOf(".") != -1){
					mobileNo = mobileNo.substring(0, mobileNo.lastIndexOf("."));
				}
				gcAccountMarketing.setMobileno(mobileNo);
				
				GcAccountMarketingExample gcAccountMarketingExample=new GcAccountMarketingExample();
				gcAccountMarketingExample.createCriteria().andMobilenoEqualTo(mobileNo);
				
				
				
				if(gcAccountMarketingMapper.countByExample(gcAccountMarketingExample) > 0){
					throw new Exception("手机号码【"+mobileNo+"】已存在");
				}else{
					gcAccountMarketingMapper.insertSelective(gcAccountMarketing);
				}
			}
		} catch (Exception e) {
			mWebResult.setResultCode(-1);
			mWebResult.setResultMessage("导入文件失败："+e.getMessage());
		}
		return mWebResult;
	}

	private List<GcAccountMarketing> downloadAndAnalysisFile(String fileRemoteUrl) throws Exception{
		
		String extension = fileRemoteUrl.lastIndexOf(".") == -1 ? "" : fileRemoteUrl.substring(fileRemoteUrl.lastIndexOf(".") + 1);
		java.net.URL resourceUrl = new java.net.URL(fileRemoteUrl);
		InputStream content = (InputStream) resourceUrl.getContent();
		ReadExcelUtil<GcAccountMarketing> readExcelUtil = new ReadExcelUtil<GcAccountMarketing>();
		
		return readExcelUtil.readExcel(false, null, content, new String[]{"province","region","city","site","mobileno","name","statisticalTime","remark"},new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class}, GcAccountMarketing.class, extension);
	}
	
}
