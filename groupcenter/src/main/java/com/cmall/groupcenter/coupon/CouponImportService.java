package com.cmall.groupcenter.coupon;

import java.io.InputStream;
import java.util.List;

import com.cmall.groupcenter.accountmarketing.util.ReadExcelUtil;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class CouponImportService extends RootFunc {
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap1) {
		String fileRemoteUrl=mDataMap1.get("zw_f_upload_url");
		MWebResult mWebResult = new MWebResult();
		try {
			List<CouponImportModel> dataLists = downloadAndAnalysisFile(fileRemoteUrl);
			for (CouponImportModel model : dataLists) {
				if(model.getCouponAmount()==null) {
					continue;
				}
				int num = DbUp.upTable("gc_coupon_import").count("coupon_code", model.getCouponCode());
				MDataMap mDataMap = new MDataMap();
				mDataMap.put("source", model.getSource().trim());
				mDataMap.put("name", model.getName().trim());
				mDataMap.put("type_name", model.getTypeName().trim());
				mDataMap.put("coupon_amount", model.getCouponAmount().trim());
				mDataMap.put("limit_down", String.valueOf(model.getLimitDown().trim()));
				mDataMap.put("start_time", model.getStartTime().trim());
				mDataMap.put("end_time", model.getEndTime().trim());
				mDataMap.put("limit_desc", model.getLimitDesc().trim());
				mDataMap.put("coupon_code", model.getCouponCode().trim());
				mDataMap.put("limit_description", model.getLimitDescription().trim());
				mDataMap.put("is_exclusive", model.getIsExclusive().trim());
				mDataMap.put("upload_url", fileRemoteUrl);
				mDataMap.put("fkey_id", model.getFkeyId());
				if(num>0) {
					mDataMap.put("update_time", FormatHelper.upDateTime().trim());
					mDataMap.put("editor", UserFactory.INSTANCE.create().getLoginName());
					DbUp.upTable("gc_coupon_import").dataUpdate(mDataMap, "source,name,type_name,coupon_amount,limit_down,start_time,limit_desc,coupon_code,limit_description,is_exclusive,upload_url,fkey_id,update_time,editor", "coupon_code,fkey_id");
				} else {
					mDataMap.put("import_time", FormatHelper.upDateTime().trim());
					mDataMap.put("update_time", FormatHelper.upDateTime().trim());
					mDataMap.put("creator", UserFactory.INSTANCE.create().getLoginName());
					mDataMap.put("editor", UserFactory.INSTANCE.create().getLoginName());
					DbUp.upTable("gc_coupon_import").dataInsert(mDataMap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mWebResult.setResultCode(-1);
			mWebResult.setResultMessage("导入文件失败："+e.getMessage());
		}
		return mWebResult;
	}

	private List<CouponImportModel> downloadAndAnalysisFile(String fileRemoteUrl)
			throws Exception {
		String extension = fileRemoteUrl.lastIndexOf(".") == -1 ? ""
				: fileRemoteUrl.substring(fileRemoteUrl.lastIndexOf(".") + 1);
		java.net.URL resourceUrl = new java.net.URL(fileRemoteUrl);
		InputStream content = (InputStream) resourceUrl.getContent();
		ReadExcelUtil<CouponImportModel> readExcelUtil = new ReadExcelUtil<CouponImportModel>();
		return readExcelUtil.readExcel(false, null, content, new String[] {
				"source", "name", "typeName", "couponAmount", "limitDown",
				"startTime", "endTime", "limitDesc", "couponCode",
				"limitDescription", "isExclusive", "fkeyId" }, new Class[] {
				String.class, String.class, String.class, String.class,
				String.class, String.class, String.class, String.class,
				String.class, String.class, String.class, String.class },
				CouponImportModel.class, extension);
	}
}