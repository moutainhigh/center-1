package com.cmall.newscenter.api;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.api.ApiVersionAppInput;
import com.cmall.productcenter.model.api.ApiVersionAppResult;
import com.cmall.productcenter.service.VersionAppService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 手机版本更新提示接口 2014.9.22
 * 
 * @author zhouguohui
 * @version1.0
 */
public class VersionAppMsg extends
		RootApiForManage<ApiVersionAppResult, ApiVersionAppInput> {

	public ApiVersionAppResult Process(ApiVersionAppInput inputParam,
			MDataMap mRequestMap) {
		VersionAppService vas = new VersionAppService();
		Map<String, Object> map = null;
		/**
		 * 手机号码
		 */
		String phone = inputParam.getPhone();
		/**
		 * 流水号
		 */
		String serialNumber = inputParam.getSerialNumber();
		/**
		 * 渠道号
		 */
		String channelNumber = inputParam.getChannelNumber().trim();

		// 特殊校验规则
		if (StringUtils.isNotBlank(channelNumber)) {

			channelNumber = StringUtils.replaceEach(channelNumber,
					new String[] { " ", "?", "%ef%bb%bf", "%EF%BB%BF",
							"\\xEF\\xBB\\xBF" }, new String[] { "", "", "", "",
							"" });
			
			if(channelNumber.length()>=5)
			{
				channelNumber=StringUtils.right(channelNumber, 5);
			}
			

		}

		/**
		 * 获取当前的手机类型 1代表ios 2代表andriod
		 */
		String iosAndriod = inputParam.getIosAndriod();
		if (null == iosAndriod || "".equals(iosAndriod.trim())) {
			iosAndriod = "2";
		}

		/**
		 * 平台代码
		 */
		String versionCode = inputParam.getVersionCode().trim();
		/**
		 * 版本号
		 */
		String versionApp = inputParam.getVersionApp().trim();

		/**
		 * 版本控制判断代码
		 */
		if (StringUtils.isEmpty(serialNumber)
				&& StringUtils.isEmpty(channelNumber)) {
			map = vas.getVersionAppValuesOld(iosAndriod, versionCode,
					versionApp);
		} else {
			map = vas.getVersionAppValuesNew(serialNumber, channelNumber,
					iosAndriod, versionCode, versionApp);
		}
		
		return (ApiVersionAppResult) map.get("versionApp");
		
		//Zht write directly
//		ApiVersionAppResult result = (ApiVersionAppResult) map.get("versionApp");
//		if(iosAndriod.equals("2") && result.upFlagTrue() && StringUtils.isNotEmpty(versionApp) && versionApp.compareTo("V4.0.9") < 0) {
//			//android
//			if(StringUtils.isEmpty(inputParam.getChannelNumber())) {
//				result.setAppUrl("http://appupload.huijiayou.cn/HJY/4_0_9/Hui_Jia_You_409_1025a.apk");
//			} else {
//				result.setAppUrl("http://appupload.huijiayou.cn/HJY/4_0_9/Hui_Jia_You_409_"+ inputParam.getChannelNumber() + ".apk");
//			}
//			result.setUpgradeSelect("1");
//		} else if(iosAndriod.equals("2") && !result.upFlagTrue()) {
//			result.setResultCode(1);
//		}
//
//		return result;
	}
}