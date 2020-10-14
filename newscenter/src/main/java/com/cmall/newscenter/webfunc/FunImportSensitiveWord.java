package com.cmall.newscenter.webfunc;

import java.io.InputStream;
import java.util.List;

import com.cmall.groupcenter.accountmarketing.util.ReadExcelUtil;
import com.cmall.newscenter.model.SensitiveWordImport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 导入敏感词
 * @author wei.che
 * date 2015-09-10
 */
public class FunImportSensitiveWord extends RootFunc{
	
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap1) {
		String fileRemoteUrl=mDataMap1.get("zw_f_importFile");
		MWebResult mWebResult = new MWebResult();
		try {
			List<SensitiveWordImport> dataLists = downloadAndAnalysisFile(fileRemoteUrl);
			for (SensitiveWordImport model : dataLists) {
				if(model.getSensitiveWord()==null) {
					continue;
				}
				int num = DbUp.upTable("nc_sensitive_word").count("sensitive_word", model.getSensitiveWord());
				MDataMap mDataMap = new MDataMap();
				mDataMap.put("sensitive_word", model.getSensitiveWord().trim());
				if(num==0) {
					DbUp.upTable("nc_sensitive_word").dataInsert(mDataMap);
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
			mWebResult.setResultCode(-1);
			mWebResult.setResultMessage("导入文件失败："+e.getMessage());
		}
		return mWebResult;
	}
	
	private List<SensitiveWordImport> downloadAndAnalysisFile(String fileRemoteUrl)
			throws Exception {
		String extension = fileRemoteUrl.lastIndexOf(".") == -1 ? ""
				: fileRemoteUrl.substring(fileRemoteUrl.lastIndexOf(".") + 1);
		java.net.URL resourceUrl = new java.net.URL(fileRemoteUrl);
		InputStream content = (InputStream) resourceUrl.getContent();
		ReadExcelUtil<SensitiveWordImport> readExcelUtil = new ReadExcelUtil<SensitiveWordImport>();
		return readExcelUtil.readExcel(false, null, content, new String[] {"sensitiveWord"}, new Class[] {String.class},
				SensitiveWordImport.class, extension);
	}
	
}
