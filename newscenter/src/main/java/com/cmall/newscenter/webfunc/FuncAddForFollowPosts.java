package com.cmall.newscenter.webfunc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import com.cmall.newscenter.beauty.model.CosmeticBagNew;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
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
 * 对主帖进行追帖发布
 * @author houwen
 *
 */
public class FuncAddForFollowPosts extends RootFunc {
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
		//主贴ID
		String postParentCode = "";
		//主贴标题
		String postTitle = "";
		//是否精华帖
		String issessence = "";
		//是否官方贴
		String isofficial = "";
		//是否火贴
		String ishot = "";
		//标签
		String postLabel = "";
		//发布者
		String publisherCode = "";
		//正文
		String postConcent="";
		//妆品数据
		String[] cosmeticCodes = null;
		//定义追贴插入数据库
		MDataMap subInsertMap = new MDataMap();
		List<MDataMap> subInsertMapList = new ArrayList<MDataMap>();
		
		recheckMapField(mResult, mPage, mAddMaps);
        MDataMap map  = new MDataMap();
        MPageData mPageData = new MPageData();
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
					
					if(mField.getColumnName().equals("post_parent_code")){
						map.put("post_code",sValue);
						postParentCode = sValue;
					}
					
					
				/*	if(mField.getColumnName().equals("post_parent_code")){
						if(sValue!=null && !sValue.equals("")){
							flag2=true;
						}
					}*/
					
					if(mField.getColumnName().equals("post_content")){
						if(sValue.trim()!=null && !sValue.trim().equals("")){
							postConcent = sValue;//获取正文内容
						}
					}
					
					if(mField.getColumnName().equals("cosmetic_code")){
						if(sValue!=null && !sValue.equals("")){
							flag3=true;
							//获取妆品code
							cosmeticCodes = sValue.split(",");
							if(cosmeticCodes.length > 4){
								mResult.setResultCode(934205136);
								mResult.setResultMessage(bInfo(934205136, "4"));
								return mResult;
							}
						}
					}
					

					
				}

			}
			
			//图片,商品和妆品只能选择其中一项
			if((flag && flag2) || (flag && flag3) || (flag2 && flag3) || (flag && flag2 && flag3)){
				mResult.inErrorMessage(934205134);
				return mResult;
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
		
//		if(flag && flag2){
//			mResult.inErrorMessage(934205110);
//		}
		
		
		
		/*//编码自动生成
		mInsertMap.put("configuration_id", WebHelper.upCode(""));*/
		
		//APP
		String appCode = UserFactory.INSTANCE.create().getManageCode();
		  /*根据帖子ID查询帖子列表*/

	//	map.put("post_code",inputParam.getPost_code());
		map.put("post_catagory", "4497465000020001");
		map.put("post_type", "449746780001"); //类型为主帖
		map.put("status", "449746730001");
		map.put("is_delete", "0");  //未被删除
		map.put("app_code", appCode);
		mPageData = DataPaging.upPageData("nc_posts", "", "", map, new PageOption());
		if(mPageData.getListData().size()!=0){  //默认和主帖子一致
			
			postTitle = mPageData.getListData().get(0).get("post_title");
			issessence = mPageData.getListData().get(0).get("issessence");
			isofficial = mPageData.getListData().get(0).get("isofficial");
			ishot = mPageData.getListData().get(0).get("ishot");
			postLabel = mPageData.getListData().get(0).get("post_label");
			publisherCode = mPageData.getListData().get(0).get("publisher_code");
			
			mInsertMap.put("post_title",postTitle);
			
			mInsertMap.put("issessence",issessence); //是否精华帖 449746770002：否，449746770001：是
			
			mInsertMap.put("isofficial",isofficial); //是否官方帖 449746760002：否，449746760001：是
			
			mInsertMap.put("ishot",ishot); //是否火帖 449746880002：否，449746880001：是
			
			mInsertMap.put("post_label",postLabel); //标签
			
			mInsertMap.put("publisher_code",publisherCode); //发布人
		}
		
		//追贴ID
		String mPostCode = WebHelper.upCode("HML");
		
		//创建时间为当年系统时间
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
		String publishTime = df.format(new Date());// new Date()为获取当前系统时间
		
		mInsertMap.put("publish_time", publishTime);
		
		mInsertMap.put("post_type","449746780002"); // 是否主/追帖 0：主帖，1：追帖
		
		mInsertMap.put("post_catagory","4497465000020001"); // 栏目ID
		
		mInsertMap.put("post_code",mPostCode); // 帖子ID

		mInsertMap.put("status","449746730001"); 
		
		mInsertMap.put("app_code",appCode); 
		
		mInsertMap.put("type", "449747130001");//默认是普通帖子
		
		//mInsertMap.put("",""); // 帖子ID
		
		/*if(UserFactory.INSTANCE.create().getUserCode() == null || UserFactory.INSTANCE.create().getUserCode().equals("")){
			mResult.inErrorMessage(941901073);
		}else{
			mInsertMap.put("publisher_code", UserFactory.INSTANCE.create().getUserCode());  //获取当前登录名
		}
		*/
		
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
			//正文为空
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
					
					//追贴中的第一个妆品为追贴
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
						//剩余的妆品依次排序为追贴
						subInsertMap = new MDataMap();
						
						//页面现有项目
						for(Iterator<String> iter = set.iterator(); iter.hasNext();){
							String key = iter.next();
							String value = mInsertMap.get(key);
							subInsertMap.put(key, value);
						}
						
						//追贴ID
						String subPostCode = WebHelper.upCode("HML");
						
						subInsertMap.put("post_title",postTitle);
						subInsertMap.put("issessence",issessence); //是否精华帖 449746770002：否，449746770001：是
						subInsertMap.put("isofficial",isofficial); //是否官方帖 449746760002：否，449746760001：是
						subInsertMap.put("ishot",ishot); //是否火帖 449746880002：否，449746880001：是
						subInsertMap.put("post_label",postLabel); //标签
						subInsertMap.put("publisher_code",publisherCode); //发布人
						
						subInsertMap.put("publish_time", publishTime); 
						subInsertMap.put("post_type","449746780002"); //追帖
						subInsertMap.put("post_catagory","4497465000020001"); // 栏目ID
						subInsertMap.put("app_code",appCode); // APP
						subInsertMap.put("status","449746730001");
						subInsertMap.put("post_code",subPostCode); // 追贴ID
						
						subInsertMap.put("post_parent_code", postParentCode);//主帖ID
						
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
				
				//将第一个追贴中的妆品编号置空
				mInsertMap.put("cosmetic_code", "");
				//第一个追贴置为妆品帖子
				mInsertMap.put("type", "449747130002");
				
				//正文不为空，正文为追贴的第一条，选择的妆品依次为追贴
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
					
					subInsertMap.put("post_title",postTitle);
					subInsertMap.put("issessence",issessence); //是否精华帖 449746770002：否，449746770001：是
					subInsertMap.put("isofficial",isofficial); //是否官方帖 449746760002：否，449746760001：是
					subInsertMap.put("ishot",ishot); //是否火帖 449746880002：否，449746880001：是
					subInsertMap.put("post_label",postLabel); //标签
					subInsertMap.put("publisher_code",publisherCode); //发布人
					
					subInsertMap.put("publish_time", publishTime); 
					subInsertMap.put("post_type","449746780002"); //追帖
					subInsertMap.put("post_catagory","4497465000020001"); // 栏目ID
					subInsertMap.put("app_code",appCode); // APP
					subInsertMap.put("status","449746730001");
					subInsertMap.put("post_code",subPostCode); // 追贴ID
					
					subInsertMap.put("post_parent_code", postParentCode);//主帖ID
					
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
			
			//第一个追贴
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
		   return str.matches("[\\d]+");
		}
}
