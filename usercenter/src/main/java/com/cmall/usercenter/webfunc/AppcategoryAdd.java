package com.cmall.usercenter.webfunc;

import java.util.Map;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * ClassName 添加商品虚类
 * @author shiyz
 * Date 2014-06-26
 *
 */
public class AppcategoryAdd extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		synchronized(this){
			MWebResult mResult = new MWebResult();
			/**获取提交新增时页面相关属性的值*/
			MWebOperate mOperate = WebUp.upOperate(sOperateUid);
			/**获取提交新增时相关属性*/
			MWebPage mPage = WebUp.upPage(mOperate.getPageCode());
			/**将页面中的参数名称修改为数据库字段名称*/
			MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
			
			// 定义组件判断标记
						boolean bFlagComponent = false;
				
						recheckMapField(mResult, mPage, mAddMaps);
				
						if (mResult.upFlagTrue()) {
							Map<String, Object> codeMap = DbUp.upTable(mPage.getPageTable()).dataSqlOne("select max(category_code) from uc_sellercategory  where parent_code=:parent_code and seller_code=:seller_code", mAddMaps);
							Map<String, Object> sortMap = DbUp.upTable(mPage.getPageTable()).dataSqlOne("select max(sort) from uc_sellercategory where parent_code=:parent_code and seller_code=:seller_code", mAddMaps);
							if(!codeMap.isEmpty()&&codeMap.get("max(category_code)")!=null&&!"".equals(codeMap.get("max(category_code)"))){
								String four = String.valueOf(Integer.valueOf(codeMap.get("max(category_code)").toString().substring(codeMap.get("max(category_code)").toString().length()-4, codeMap.get("max(category_code)").toString().length()))+1);
								for ( int i = 0, j = (4 - four.length()); i < j; i++) {
									four = "0" + four;

								}
								String ca = codeMap.get("max(category_code)").toString().substring(0, codeMap.get("max(category_code)").toString().length()-4)+four;
								mAddMaps.put("category_code", ca);
							}else{
								String ca = mAddMaps.get("parent_code")+"0001";
								mAddMaps.put("category_code", ca);
							}
							if(!sortMap.isEmpty()&&sortMap.get("max(sort)")!=null&&!"".equals(sortMap.get("max(sort)"))){
								String four = String.valueOf(Integer.valueOf(sortMap.get("max(sort)").toString().substring(sortMap.get("max(sort)").toString().length()-4, sortMap.get("max(sort)").toString().length()))+1);
								for ( int i = 0, j = (4 - four.length()); i < j; i++) {
									four = "0" + four;

								}
								String so = sortMap.get("max(sort)").toString().substring(0, sortMap.get("max(sort)").toString().length()-4)+four;
								mAddMaps.put("sort", so);
							}else{
								Map<String, Object> caMap = DbUp.upTable(mPage.getPageTable()).dataSqlOne("select sort from uc_sellercategory  where category_code=:parent_code and seller_code=:seller_code", mAddMaps);
								mAddMaps.put("sort", caMap.get("sort")+"0001");
							}
							DbUp.upTable(mPage.getPageTable()).dataInsert(mAddMaps);
				
							if (bFlagComponent) {
				
								for (MWebField mField : mPage.getPageFields()) {
									if (mField.getFieldTypeAid().equals("104005003")) {
				
										WebUp.upComponent(mField.getSourceCode()).inAdd(mField,
												mDataMap);
				
									}
								}
				
							}
				
						}
				
						if (mResult.upFlagTrue()) {
							mResult.setResultMessage(bInfo(969909001));
						}
						return mResult;

					}
				}

}
