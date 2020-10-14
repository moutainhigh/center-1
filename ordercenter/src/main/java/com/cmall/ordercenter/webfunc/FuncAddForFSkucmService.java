package com.cmall.ordercenter.webfunc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

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

/**
 * 添加闪购商品
 * @author jl
 *
 */
public class FuncAddForFSkucmService extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		// 定义插入数据库
		MDataMap mInsertMap = new MDataMap();

		recheckMapField(mResult, mPage, mAddMaps);

		if (mResult.upFlagTrue()) {

			//判断登陆用户是否为空
			String loginname=UserFactory.INSTANCE.create().getLoginName();
			if(loginname==null||"".equals(loginname)){
				mResult.inErrorMessage(941901073);
				return mResult;
			}
			/*销售价*/
		    BigDecimal sellPrice = 	BigDecimal.valueOf(Double.valueOf(mAddMaps.get("sell_price")));
			/*优惠价*/
		    BigDecimal vipPrice =   BigDecimal.valueOf(Double.valueOf(mAddMaps.get("vip_price")));
		    
		    if(sellPrice.compareTo(vipPrice)==-1){
		    	
		    	mResult.inErrorMessage(941901105);
				return mResult;
		    	
		    }
			/*库存数*/
			int stock_num = Integer.valueOf(mAddMaps.get("stock_num"));
			/*促销库存*/
			int sales_num = Integer.valueOf(mAddMaps.get("sales_num"));
			
//			if(sales_num>stock_num){
//				
//				mResult.inErrorMessage(941901106);
//				return mResult;
//		    	
//				
//			}
			
			//创建时间为当年系统时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
			String now=df.format(new Date());
			
			// 循环所有结构 初始化插入map
			for (MWebField mField : mPage.getPageFields()) {
				
				if("update_time".equals(mField.getColumnName())){
					mInsertMap.put("update_time", now);   // 更新时间
				}
				if("update_user".equals(mField.getColumnName())){
					mInsertMap.put("update_user", loginname);   // 更新的用户
				}
				
				if (mAddMaps.containsKey(mField.getFieldName())
						&& StringUtils.isNotEmpty(mField.getColumnName())) {

					String sValue = mAddMaps.get(mField.getFieldName());

					mInsertMap.put(mField.getColumnName(), sValue);
				}
			}
		}
		
		//校验 activity_code sku_code  唯一约束
		String activity_code=mInsertMap.get("activity_code");
		String sku_code=mInsertMap.get("sku_code");
		
		//查看是否有时间重合的闪购，若有，则添加应该失败
		//查询不重合的情况
		int count=DbUp.upTable(mPage.getPageTable()).count("activity_code",activity_code,"sku_code",sku_code,"status","449746810001");
		if(count>0){
			mResult.setResultMessage(bInfo(939301107));
			mResult.setResultCode(939301107);
		}
		
		
		if (mResult.upFlagTrue()) {
			DbUp.upTable(mPage.getPageTable()).dataInsert(mInsertMap);
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;

	}
	
}
