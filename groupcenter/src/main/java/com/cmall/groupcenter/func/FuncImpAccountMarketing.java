package com.cmall.groupcenter.func;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmapper.groupcenter.GcAccountMarketingMapper;
import com.cmall.dborm.txmodel.groupcenter.GcAccountMarketing;
import com.cmall.groupcenter.accountmarketing.util.ReadExcelUtil;
import com.cmall.groupcenter.job.JobHandleImportMarketing;
import com.cmall.groupcenter.support.GroupAccountSupport;
import com.cmall.groupcenter.txservice.TxAccountMarketingService;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.RegexHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncImpAccountMarketing extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mWebResult = new MWebResult();
		MDataMap mInputMap = upFieldMap(mDataMap);
		String fileRemoteUrl=mInputMap.get("file_url");
		String fileName=mInputMap.get("file_name");
		
		if(StringUtils.isBlank(fileRemoteUrl)){
			mWebResult.inErrorMessage(918506009);
		}
		
		List<GcAccountMarketing> dataLists = new ArrayList<GcAccountMarketing>();
		if(mWebResult.upFlagTrue())
		{
			try {
				dataLists = downloadAndAnalysisFile(fileRemoteUrl);
			} catch (Exception e) {
				mWebResult.inErrorMessage(918506010);
			}
		}
		String operator=UserFactory.INSTANCE.create().getLoginName();
		String batchCode=WebHelper.upCode("IMPORT");
		int dataSize=dataLists.size();
		//System.out.println("zuhe start:"+new Date());
		if(mWebResult.upFlagTrue()){
			StringBuilder stringBuffer=new StringBuilder("insert into gc_account_marketing (uid,region,city,site,mobileno,name,statistical_time,create_time,operator,remark,parent_mobile,extend_user,"
					+ "extend_manage,extend_type,member_type,card_no,card_name,bank_name,bank_detail,certificate_no,memo,batch_code,flag_code) values ");
			for(int i=0;i<dataSize;i++){
				GcAccountMarketing gcAccountMarketing=dataLists.get(i);
				String mobile= gcAccountMarketing.getMobileno();
				if(mobile!=null){
					mobile=mobile.trim();
					if(mobile.lastIndexOf(".") != -1){
						mobile = mobile.substring(0, mobile.lastIndexOf("."));
					}
				}
				else{
					mobile="";
				}
				
				String parentMobile=gcAccountMarketing.getParentMobile();
					if(parentMobile!=null&&parentMobile.length()>0){
						parentMobile=parentMobile.trim();
						if(parentMobile.lastIndexOf(".")!=-1){
							parentMobile=parentMobile.substring(0,parentMobile.lastIndexOf("."));
						}
					}
					else{
						parentMobile="";
					}

				if(!StringUtils.isBlank(gcAccountMarketing.getCardNo())){
					if(gcAccountMarketing.getCardNo().lastIndexOf(".")!=-1){
						gcAccountMarketing.setCardNo(gcAccountMarketing.getCardNo().substring(0,gcAccountMarketing.getCardNo().lastIndexOf(".")));
					}
					
				}
				if(!StringUtils.isBlank(gcAccountMarketing.getCertificateNo())){
					if(gcAccountMarketing.getCertificateNo().lastIndexOf(".")!=-1){
						gcAccountMarketing.setCertificateNo(gcAccountMarketing.getCertificateNo().substring(0, gcAccountMarketing.getCertificateNo().lastIndexOf(".")));
					}
					
				}
			    stringBuffer.append("(\"").append(WebHelper.upUuid()).append("\",\"").append(gcAccountMarketing.getRegion()==null?"":gcAccountMarketing.getRegion()).append("\",\"").append(gcAccountMarketing.getCity()==null?"":gcAccountMarketing.getCity()).append("\",\"")
			    .append(gcAccountMarketing.getSite()==null?"":gcAccountMarketing.getSite()).append("\",\"").append(mobile).append("\",\"").append(gcAccountMarketing.getName()==null?"":gcAccountMarketing.getName()).append("\",\"").append(gcAccountMarketing.getStatisticalTime()==null?"":gcAccountMarketing.getStatisticalTime())
			    .append("\",\"").append(FormatHelper.upDateTime()).append("\",\"").append(operator).append("\",\"").append(gcAccountMarketing.getRemark()==null?"":gcAccountMarketing.getRemark()).append("\",\"").append(parentMobile)
			    .append("\",\"").append(gcAccountMarketing.getExtendUser()==null?"":gcAccountMarketing.getExtendUser()).append("\",\"").append(gcAccountMarketing.getExtendManage()==null?"":gcAccountMarketing.getExtendManage()).append("\",\"").append(gcAccountMarketing.getExtendType()==null?"":gcAccountMarketing.getExtendType()).append("\",\"")
			    .append(gcAccountMarketing.getMemberType()==null?"":gcAccountMarketing.getMemberType()).append("\",\"").append(gcAccountMarketing.getCardNo()==null?"":gcAccountMarketing.getCardNo()).append("\",\"").append(gcAccountMarketing.getCardName()==null?"":gcAccountMarketing.getCardName()).append("\",\"")
			    .append(gcAccountMarketing.getBankName()==null?"":gcAccountMarketing.getBankName()).append("\",\"").append(gcAccountMarketing.getBankDetail()==null?"":gcAccountMarketing.getBankDetail()).append("\",\"").append(gcAccountMarketing.getCertificateNo()==null?"":gcAccountMarketing.getCertificateNo()).append("\",\"")
			    .append(gcAccountMarketing.getMemo()==null?"":gcAccountMarketing.getMemo()).append("\",\"").append(batchCode).append("\",\"").append("4497465200130001");
			    if(i==dataLists.size()-1){
			    	stringBuffer.append("\");");
			    }
			    else{
			    	stringBuffer.append("\"),");
			    }
			}
			//System.out.println("zuhe end:"+new Date());
			//System.out.println("zhuanhuan start:"+new Date());
			String sqlString=stringBuffer.toString();
			//System.out.println("zhuanhuan end:"+new Date());
			//System.out.println("insert start:"+new Date());
			DbUp.upTable("gc_account_marketing").dataExec(sqlString, new MDataMap());
			//System.out.println("insert end:"+new Date());
			
		}
		
		/*if(mWebResult.upFlagTrue()){
			Thread t = new Thread(new Runnable(){  
	            public void run(){  
	            JobHandleImportMarketing jobHandleImportMarketing=new JobHandleImportMarketing();
	            jobHandleImportMarketing.doExecute(null);
	            }});  
	        t.start();  
		}*/
		
		if(mWebResult.upFlagTrue()){
			DbUp.upTable("gc_account_marketing_file_info").insert("batch_code",batchCode,"file_name",fileName,"total_count",String.valueOf(dataSize),"unsolved_count",String.valueOf(dataSize),"upload_time",FormatHelper.upDateTime(),"upload_operator",operator,"remark",fileRemoteUrl);
		}
		/*if(mWebResult.upFlagTrue()){
			mWebResult.setResultCode(111111);
			mWebResult.setResultMessage("<a href=\"/cgroup/export/page_chart_v_gc_account_marketing_import?&amp;zw_f_batch_code="+batchCode+"&amp;zw_p_size=-1\" target=\"_blank\">查看导入情况</a>");
		}*/
		
		return mWebResult;
	
	}
		
    private List<GcAccountMarketing> downloadAndAnalysisFile(String fileRemoteUrl) throws Exception{
			
	    String extension = fileRemoteUrl.lastIndexOf(".") == -1 ? "" : fileRemoteUrl.substring(fileRemoteUrl.lastIndexOf(".") + 1);
		java.net.URL resourceUrl = new java.net.URL(fileRemoteUrl);
		InputStream content = (InputStream) resourceUrl.getContent();
		ReadExcelUtil<GcAccountMarketing> readExcelUtil = new ReadExcelUtil<GcAccountMarketing>();
			
		return readExcelUtil.readExcel(false, null, content, new String[]{"mobileno","name","parentMobile","extendUser","statisticalTime","extendManage","extendType","memberType","region","city","site","cardNo","cardName","bankName","bankDetail","certificateNo","remark","memo"},new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class}, GcAccountMarketing.class, extension);
	}	

}
