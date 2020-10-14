package com.cmall.productcenter.webfunc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.cmall.productcenter.common.DateUtil;
import com.cmall.productcenter.service.BrandService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class BrandFunc  extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		MDataMap _mDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		_mDataMap.put("brand_name", StringUtils.trimToEmpty(_mDataMap.get("brand_name")));
		
		BrandService service = new BrandService();
		try {
			if(mResult.upFlagTrue()){
				if((_mDataMap.get("cpsrate")!=null&&!"".equals(_mDataMap.get("cpsrate").trim()))){
					String cpsrate = _mDataMap.get("cpsrate").trim();
					Pattern pattern = Pattern.compile("^0\\.([1-9][0-9]?|[0-9][1-9])$");
					Matcher matcher = pattern.matcher(cpsrate);
					if(!matcher.find()){//是否为数字
						if(cpsrate.matches("^0(.[0]{1,2})?$")&&Double.valueOf(cpsrate)==0){
							_mDataMap.put("cpsrate", "0");
						}else{
							mResult.setResultCode(11);
							mResult.setResultMessage("商城分成比例必须为大于0小于1的两位小数！");
						}
					}
				}else{
					_mDataMap.put("cpsrate", "0");
				}
			}
			
			if (mResult.upFlagTrue() == true) {
				//获取品牌的”中文名称“
				String brandName=_mDataMap.get("brand_name");
				//根据brandName与uid校验在数据库中是否有相同品牌名称存在,如果存在则返回提示信息
				if(checkInfo(brandName.trim())){
					//返回提示信息
					mResult.setResultCode(941901077);
					mResult.setResultMessage(bInfo(941901077));
					return mResult;
				}
				
				
				MUserInfo userInfo = UserFactory.INSTANCE.create();
				String create_usercode = "";
				String create_usenm = "";
				if (null != userInfo) {
					/*获取当前登录人*/
					create_usercode = userInfo.getUserCode();
					create_usenm = userInfo.getRealName();
				}
				_mDataMap.put("create_time", DateUtil.getSysDateTimeString());
				_mDataMap.put("create_usernm", create_usenm);
				_mDataMap.put("create_usercode", create_usercode);
				
				boolean flag = service.addBrand(_mDataMap);
				
				if (flag == false) {
					// 异常处理待定
					mResult.inErrorMessage(939301099);
				}else {
					mResult.setResultMessage(bInfo(969909001));
				}
			}
		} catch (Exception e) {
			mResult.inErrorMessage(939301099);
		}
		return mResult;
	}
	/**
	 * 校验是否与数据库里的品牌中文名称有重复
	 * @param brandName
	 * @return 
	 */
	private boolean checkInfo(String brandName) {
		int atCount = DbUp.upTable("pc_brandinfo").count("brand_name", brandName);		//判断数据库中是否存在品牌名称
		if(atCount >= 1) return true;
		return false;
	}
}
