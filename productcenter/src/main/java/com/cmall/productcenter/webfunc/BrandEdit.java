package com.cmall.productcenter.webfunc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cmall.productcenter.util.PinYin;
import com.cmall.systemcenter.support.TermChangeSupport;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

public class BrandEdit extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		MDataMap mInsertMap = new MDataMap();

		recheckMapField(mResult, mPage, mAddMaps);

		// 定义组件判断标记
		boolean bFlagComponent = false;

		if (mResult.upFlagTrue()) {

			// 循环所有结构
			for (MWebField mField : mPage.getPageFields()) {

				if (mField.getFieldTypeAid().equals("104005003")) {
					bFlagComponent = true;
				}

				if (mAddMaps.containsKey(mField.getColumnName())) {

					String sValue = mAddMaps.get(mField.getColumnName());

					mInsertMap.put(mField.getColumnName(), sValue);
				} else if (mField.getFieldTypeAid().equals("104005103")) {
					//特殊判断修改时如果没有传值 则自动赋空
					mInsertMap.put(mField.getColumnName(), "");
				}
				
			}
		}

		if(mResult.upFlagTrue()){
			if((mInsertMap.get("cpsrate")!=null&&!"".equals(mInsertMap.get("cpsrate").trim()))){
				String cpsrate = mInsertMap.get("cpsrate").trim();
				Pattern pattern = Pattern.compile("^0\\.([1-9][0-9]?|[0-9][1-9])$");
				Matcher matcher = pattern.matcher(cpsrate);
				if(!matcher.find()){//是否为数字
					if(cpsrate.matches("^0(.[0]{1,2})?$")&&Double.valueOf(cpsrate)==0){
						mInsertMap.put("cpsrate", "0");
					}else{
						mResult.setResultCode(11);
						mResult.setResultMessage("商城分成比例必须为大于0小于1的两位小数！");
					}
				}
			}else{
				mInsertMap.put("cpsrate", "0");
			}
		}
		
		if (mResult.upFlagTrue()) {
			
			//获取品牌的”中文名称“，“品牌编码”
			String brandName=mAddMaps.get("brand_name");
			String uid=mAddMaps.get("uid");
			//根据brandName与uid校验在数据库中是否有相同品牌名称存在,如果存在则返回提示信息
			if(checkInfo(brandName.trim(),uid)){
				//返回提示信息
				mResult.setResultCode(941901077);
				mResult.setResultMessage(bInfo(941901077));
				return mResult;
			}else{
				DbUp.upTable(mPage.getPageTable())
						.dataUpdate(mInsertMap, "", "uid");
			}
			if (bFlagComponent) {

				for (MWebField mField : mPage.getPageFields()) {
					if (mField.getFieldTypeAid().equals("104005003")) {

						WebUp.upComponent(mField.getSourceCode()).inEdit(
								mField, mDataMap);

					}
				}

			}
			
			String term = brandName;
			if(DbUp.upTable("sc_word_term").count("term", term)== 0) {
				String pinyin = PinYin.getFullSpell(term);
				
				// 保存到词库表
				MDataMap insertMap = new MDataMap();
				insertMap.put("term", term);
				insertMap.put("pinyin", pinyin);
				insertMap.put("create_time", FormatHelper.upDateTime());
				insertMap.put("create_user", UserFactory.INSTANCE.create().getLoginName());
				DbUp.upTable("sc_word_term").dataInsert(insertMap);
				
				// 记录日志表
				MDataMap logInsertMap = new MDataMap();
				logInsertMap.put("term", term);
				logInsertMap.put("oper_type", TermChangeSupport.TYPE_ADD+"");
				logInsertMap.put("create_time", insertMap.get("create_time"));
				logInsertMap.put("create_user", insertMap.get("create_user"));
				DbUp.upTable("lc_word_term_log").dataInsert(logInsertMap);
			}

		}

		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;
	}
	/**
	 * 校验是否与数据库里有重复
	 * @param brand_name，uid
	 * @return 
	 */
	private boolean checkInfo(String brand_name,String uid) {
		int atCount = DbUp.upTable("pc_brandinfo").count("brand_name", brand_name);		//判断数据库中是否存在品牌名称
		
		if(atCount >= 1){
			MDataMap brandInfoData = DbUp.upTable("pc_brandinfo").one("uid", uid);		//得到数据库中此品牌的信息
			String brandNameData = brandInfoData.get("brand_name");						//得到数据库中此品牌的brand_name
			//如果两个brand_name相同则说明没有修改品牌的中文名称，可以进行下一步操作，返回false
			if (brandNameData.equals(brand_name)) {						
				return false;
			}else{
				return true;
			}
		}
		return false;
	}
}
