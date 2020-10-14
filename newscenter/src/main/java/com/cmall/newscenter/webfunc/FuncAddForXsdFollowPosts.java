package com.cmall.newscenter.webfunc;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
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
 * 小时代姐妹圈
 * 对主帖进行追帖发布
 * @author houwen
 * 2015-2-5
 */
public class FuncAddForXsdFollowPosts extends RootFunc {
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
				}
			}
		}
		
		if(flag && flag2){
			mResult.inErrorMessage(934205110);
		}
		
		//APP
		String appCode = UserFactory.INSTANCE.create().getManageCode();
		
		  /*根据帖子ID查询帖子列表*/
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
		
		if (mResult.upFlagTrue()) {
			
			//第一个追贴
			DbUp.upTable(mPage.getPageTable()).dataInsert(mInsertMap);
			
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
