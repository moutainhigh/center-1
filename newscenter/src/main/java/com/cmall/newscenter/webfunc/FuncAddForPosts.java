package com.cmall.newscenter.webfunc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.cmall.newscenter.beauty.model.CosmeticBagNew;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 发布姐妹圈帖
 * @author houwen
 *
 */
public class FuncAddForPosts extends RootFunc {
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		// 定义插入数据库
		MDataMap mInsertMap = new MDataMap();
		// 定义组件判断标记
		boolean bFlagComponent = false;
		boolean flag = false ;
		boolean flag2 = false;
		boolean flag3 = false;
		
		//正文
		String postConcent="";
		//妆品数据
		String[] cosmeticCodes = null;
		//定义追贴插入数据库
		MDataMap subInsertMap = new MDataMap();
		List<MDataMap> subInsertMapList = new ArrayList<MDataMap>();
		recheckMapField(mResult, mPage, mAddMaps);

		if (mResult.upFlagTrue()) {

			// 循环所有结构 初始化插入map
			for (MWebField mField : mPage.getPageFields()) {
				
				if (mField.getFieldTypeAid().equals("104005003")) {
					bFlagComponent = true;
				}
				
				if (mAddMaps.containsKey(mField.getFieldName())
						&& StringUtils.isNotEmpty(mField.getColumnName())) {
					
					String sValue = mAddMaps.get(mField.getFieldName());
					
					mInsertMap.put(mField.getColumnName(), sValue);
					
					if(mField.getColumnName().equals("post_img")){
						if(sValue!=null && !sValue.equals("")){
							flag=true;
						}
					}
					
					if(mField.getColumnName().equals("product_code")){
						if(sValue!=null && !sValue.equals("")){
							flag2=true;
						}
					}
					
					if(mField.getColumnName().equals("cosmetic_code")){
						if(sValue!=null && !sValue.equals("")){
							flag3=true;
							//获取妆品code
							cosmeticCodes = sValue.split(",");
							//从化妆包中最多只能选择9件妆品
							if(cosmeticCodes.length > 9){
								mResult.setResultCode(934205136);
								mResult.setResultMessage(bInfo(934205136, "9"));
								return mResult;
							}
						}
					}
					
					//图片,商品和妆品只能选择其中一项
					if((flag && flag2) || (flag && flag3) || (flag2 && flag3) || (flag && flag2 && flag3)){
						mResult.inErrorMessage(934205134);
						return mResult;
					}
					
					if(mField.getColumnName().equals("sort")){
						if(sValue!=null && !sValue.equals("")){
							if(!isNumber(sValue)){
								mResult.inErrorMessage(934205107);
								return mResult;
							}
						}else{
							mInsertMap.put(mField.getColumnName(), "9999");
						}
						
					}
					if(mField.getColumnName().equals("issessence")){
						if(sValue!=null && !sValue.equals("")){
							if(!isNumber(sValue)){
								mResult.inErrorMessage(934205107);
								return mResult;
							}
						}/*else{
							mInsertMap.put(mField.getColumnName(), "0");
						}*/
						
					}
					if(mField.getColumnName().equals("isofficial")){
						if(sValue!=null && !sValue.equals("")){
							if(!isNumber(sValue)){
								mResult.inErrorMessage(934205107);
								return mResult;
							}
						}/*else{
							mInsertMap.put(mField.getColumnName(), "0");
						}*/
						
					}
					if(mField.getColumnName().equals("post_browse")){
						if(sValue!=null && !sValue.equals("")){
							if(!isNumber(sValue)){
								mResult.inErrorMessage(934205107);
								return mResult;
							}
						}else{
							mInsertMap.put(mField.getColumnName(), "0");
						}
						
					}
					if(mField.getColumnName().equals("post_praise")){
						if(sValue!=null && !sValue.equals("")){
							if(!isNumber(sValue)){
								mResult.inErrorMessage(934205107);
								return mResult;
							}
						}else{
							mInsertMap.put(mField.getColumnName(), "0");
						}
						
					}
					
					if(mField.getColumnName().equals("post_title")){
						if(sValue.trim()!=null && !sValue.trim().equals("")){
							if(sValue.trim().length()<3 || sValue.trim().length()>30){
								mResult.inErrorMessage(934205113);
								return mResult;
							}
						}
					}
					
					if(mField.getColumnName().equals("post_content")){
						if(sValue.trim()!=null && !sValue.trim().equals("")){
							if(sValue.trim().length()<10 || sValue.trim().length()>1000){
								mResult.inErrorMessage(934205114);
								return mResult;
							}else{
								postConcent = sValue;//获取正文内容
							}
						}
					}
					
				}
			}
			
			//如果商品,图片,妆品都没有选择，正文不能为空
			if(!flag2 && !flag && !flag3){
				if(StringUtils.isEmpty(postConcent)){
					mResult.inErrorMessage(934205135);
					return mResult;
				}
			}
			
			//如果选择了商品或是图片，正文不能为空
			if((flag2 || flag) && !flag3){
				if(StringUtils.isEmpty(postConcent)){
					mResult.inErrorMessage(934205135);
					return mResult;
				}
			}
			
		}
		
		//主帖ID
		String mainPostCode = WebHelper.upCode("HML");
		//APP
		String appCode = UserFactory.INSTANCE.create().getManageCode();
		//创建时间为当年系统时间
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
		String publishTime = df.format(new Date());// new Date()为获取当前系统时间
		
		mInsertMap.put("publish_time", publishTime);   
		
		mInsertMap.put("post_type","449746780001"); // 是否主/追帖 0：主帖，1：追帖
		
		mInsertMap.put("post_catagory","4497465000020001"); // 栏目ID
		
		mInsertMap.put("app_code",appCode); // APP
		
		mInsertMap.put("post_code",mainPostCode); // 帖子ID
		
		mInsertMap.put("type", "449747130001");//默认是普通帖子
		
		//选择妆品时的逻辑处理
		List<MDataMap> cBagData = new ArrayList<MDataMap>();
		CosmeticBagNew cBag = new CosmeticBagNew();
		//所有妆品信息
		List<CosmeticBagNew> cBagList = new ArrayList<CosmeticBagNew>();

		if(cosmeticCodes != null && cosmeticCodes.length >0){
			
			//获取所有妆品信息
			for(int i=0;i<cosmeticCodes.length;i++){
				//妆品编号
				String cCode = cosmeticCodes[i].trim();
				//货物妆品的详细信息
				String sFields = "cosmetic_code,cosmetic_name,photo,cosmetic_price,disabled_time,count,unit";
				String sWhere = "cosmetic_code ='"+ cCode +"'";
				cBagData = DbUp.upTable("nc_cosmetic_bag").query(sFields, "", sWhere, null, 0, 0);
				
				for(int j = 0;j<cBagData.size();j++){
					cBag = new SerializeSupport<CosmeticBagNew>().serialize(cBagData.get(j),new CosmeticBagNew());
					cBagList.add(cBag);
				}
			}
			
			//获取页面项目，保存到追贴
			Set<String> set = mInsertMap.keySet();
			//正文为空，第一个妆品在主贴中展示，剩余的多个（N）妆品以多个(N)追贴的形式展示
			if(StringUtils.isEmpty(postConcent)){
				for(int k=0;k<cBagList.size();k++){
					
					String cosmeticCode = cBagList.get(k).getCosmetic_code();
					String cosmeticName = cBagList.get(k).getCosmetic_name();
					String postImg = "";
					if(!StringUtils.isEmpty(cBagList.get(k).getPhoto())){
						String img[] = String.valueOf(cBagList.get(k).getPhoto()).split(",");
						postImg = img[0];
					}

					String cosmeticPrice = cBagList.get(k).getCosmetic_price();
					String disabledTime = cBagList.get(k).getDisabled_time();
					String count = cBagList.get(k).getCount();
					String unit = cBagList.get(k).getUnit();
					
					//第一个妆品在主贴中展示
					if(k==0){
						mInsertMap.put("cosmetic_code", cosmeticCode);
						mInsertMap.put("cosmetic_name", cosmeticName);
						mInsertMap.put("post_img", postImg);
						mInsertMap.put("cosmetic_price", cosmeticPrice);
						mInsertMap.put("disabled_time", disabledTime);
						mInsertMap.put("count", count);
						mInsertMap.put("unit", unit);
						mInsertMap.put("type", "449747130002");//妆品帖子
					}else{
						//剩余的多个（N）妆品以多个(N)追贴的形式展示
						subInsertMap = new MDataMap();
						
						//页面现有项目
						for(Iterator<String> iter = set.iterator(); iter.hasNext();){
							String key = iter.next();
							String value = mInsertMap.get(key);
							subInsertMap.put(key, value);
						}
						
						//追贴ID
						String subPostCode = WebHelper.upCode("HML");
						subInsertMap.put("publish_time", publishTime); 
						subInsertMap.put("post_type","449746780002"); //追帖
						subInsertMap.put("post_catagory","4497465000020001"); // 栏目ID
						subInsertMap.put("app_code",appCode); // APP
						subInsertMap.put("post_code",subPostCode); // 追贴ID
						subInsertMap.put("post_parent_code",mainPostCode); // 主贴ID
						subInsertMap.put("cosmetic_code", cosmeticCode);
						subInsertMap.put("cosmetic_name", cosmeticName);
						subInsertMap.put("post_img", postImg);
						subInsertMap.put("cosmetic_price", cosmeticPrice);
						subInsertMap.put("disabled_time", disabledTime);
						subInsertMap.put("count", count);
						subInsertMap.put("unit", unit);
						subInsertMap.put("type", "449747130002");//妆品帖子
						subInsertMapList.add(subInsertMap);
					}
				}
			}else{
				
				//将主贴中的妆品编号置空
				mInsertMap.put("cosmetic_code", "");
				//第一个追贴置为妆品帖子
				mInsertMap.put("type", "449747130002");
				
				//正文不为空，所有的妆品逐个以追贴的形式展示
				for(int k=0;k<cBagList.size();k++){
					
					subInsertMap = new MDataMap();
					
					//页面现有项目
					for(Iterator<String> iter = set.iterator(); iter.hasNext();){
						String key = iter.next();
						String value = mInsertMap.get(key);
						subInsertMap.put(key, value);
					}
					
					//妆品图片
					String postImg = "";
					if(!StringUtils.isEmpty(cBagList.get(k).getPhoto())){
						String img[] = String.valueOf(cBagList.get(k).getPhoto()).split(",");
						postImg = img[0];
					}
					
					//追贴ID
					String subPostCode = WebHelper.upCode("HML");
					subInsertMap.put("publish_time", publishTime); 
					subInsertMap.put("post_type","449746780002"); //追帖
					subInsertMap.put("post_catagory","4497465000020001"); // 栏目ID
					subInsertMap.put("app_code",appCode); // APP
					subInsertMap.put("post_code",subPostCode); // 追贴ID
					subInsertMap.put("post_parent_code",mainPostCode); // 主贴ID
					subInsertMap.put("cosmetic_code", cBagList.get(k).getCosmetic_code());
					subInsertMap.put("cosmetic_name", cBagList.get(k).getCosmetic_name());
					subInsertMap.put("post_img", postImg);
					subInsertMap.put("cosmetic_price", cBagList.get(k).getCosmetic_price());
					subInsertMap.put("disabled_time", cBagList.get(k).getDisabled_time());
					subInsertMap.put("count", cBagList.get(k).getCount());
					subInsertMap.put("unit", cBagList.get(k).getUnit());
					subInsertMap.put("type", "449747130002");//妆品帖子
					subInsertMapList.add(subInsertMap);
				}
			}
		}

		if (mResult.upFlagTrue()) {
			
			//主贴
			DbUp.upTable(mPage.getPageTable()).dataInsert(mInsertMap);
			
			//选择妆品生成追贴
			for(MDataMap dMap : subInsertMapList){
				DbUp.upTable(mPage.getPageTable()).dataInsert(dMap);
			}
			
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
	
	public static boolean isNumber(String str) {//判断整型
		   return str.matches("[0-9]*");
		}
}
