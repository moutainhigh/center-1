package com.cmall.newscenter.webfunc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

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

public class FuncEditAdvForWgs extends RootFunc {
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		// 定义插入数据库
		MDataMap mInsertMap = new MDataMap();
		// 定义组件判断标记
		boolean bFlagComponent = false;

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
				}
			}
			if (bFlagComponent) {
				for (MWebField mField : mPage.getPageFields()) {
					if (mField.getFieldTypeAid().equals("104005003")) {
						WebUp.upComponent(mField.getSourceCode()).inAdd(mField,mDataMap);
					}
				}
			}
		}

		if (mResult.upFlagTrue()) {
			//获取”广告位编号“,"广告名称","uid","开始时间","结束时间","权值"
			String placeCode=mAddMaps.get("place_code");
			String adName=mAddMaps.get("ad_name");
			String uid = mAddMaps.get("uid");
			String sortNum=mAddMaps.get("sort_num");
			String adImgUrl = mAddMaps.get("adImg_url");

			String urlTypeCode=mDataMap.get("urlTypeCode");		//图片链接类型“code”，“url”
			//检验商品ID是否有对应的商品
			if ( "code".equals(urlTypeCode)) {
				if (!checkIsExistProduct(adImgUrl)) {
					//返回提示信息"不存在此编号的商品，请重新填写！"
					mResult.setResultCode(941901084);
					mResult.setResultMessage(bInfo(941901084)); 
					return mResult;
				}
			}
			//检验广告位中是否以存在相同名称的广告
			if (checkInfo(placeCode,adName,uid)) {
				//返回提示信息"该广告位下已存在相同名称的广告，添加失败！"
				mResult.setResultCode(941901081);
				mResult.setResultMessage(bInfo(941901081));
				return mResult;
			}
			
			//编码自动生成
			mInsertMap.put("ad_code", WebHelper.upCode("Ad"));

			mInsertMap.put("adImg_url",(urlTypeCode+"@@"+adImgUrl));
			//创建时间为当年系统时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
			mInsertMap.put("update_time", df.format(new Date()));   // new Date()为获取当前系统时间
			
			//先判断登录是否有效
			if(UserFactory.INSTANCE.create().getLoginName() == null || UserFactory.INSTANCE.create().getLoginName().equals("")){
				mResult.inErrorMessage(941901073);
				return mResult;
			}else{
				mInsertMap.put("update_user", UserFactory.INSTANCE.create().getLoginName());  //获取当前登录名
			}
			
			//查询出栏目所属页面插入到数据库里
//			MDataMap AdPParam = new MDataMap();
//			AdPParam.put("column_code", mAddMaps.get("column_code"));
//			AdPParam.put("app_code", mAddMaps.get("app_code"));
//			 List<MDataMap> list = DbUp.upTable("nc_app_column").queryAll("", "","",AdPParam);
//			if (null != list && list.size() > 0) {
//				mInsertMap.put("page_code", list.get(0).get("page_code"));			//获取page_code
//			}else{
//				//返回提示信息
//				mResult.setResultCode(941901078);
//				mResult.setResultMessage(bInfo(941901078,"该APP下没有对应栏目！"));
//				return mResult;
//			}
			//如果权值被修改，则执行权值修改流程
			MDataMap dataMap = DbUp.upTable("nc_advertise").one("uid",uid);
			if (!dataMap.get("sort_num").equals(sortNum)) {
				String adWhere = " place_code = '"+placeCode+"' ";
				//得到该广告位下最大的权值+1
				//List<MDataMap> adResultMap = DbUp.upTable("nc_advertise").queryAll("(MAX(sort_num)+1) as sort_num","",adWhere,null);
				
				//该广告位下最大的权值+1
				//String maxSortNumAddOne = adResultMap.get(0).get("sort_num"); 
				//如果输入的权值比最大权值+1还要大，则会把最大权值+1保存到数据库当中，否则插入数据库中以后把这个权值以后的值都进行+1
				//判断当前序号是否没被占用
				int userNum = DbUp.upTable("nc_advertise").count("sort_num",sortNum,"place_code",placeCode);
				if (userNum!=0) {
					MDataMap paramsMap = new MDataMap();
					paramsMap.put("sort_num", "sort_num +1");
					//更新数据，权值以后的数据都进行+1操作
					String sql = "update nc_advertise set sort_num = sort_num +1 where place_code = '"+placeCode +"' and sort_num >= "+sortNum;
					DbUp.upTable(mPage.getPageTable()).dataExec(sql,null);
					
				}
				
			}
			
			//此处插入一个页面编码
//			MDataMap placeMap = DbUp.upTable("nc_advertise_place").one("place_code",placeCode);
//			mInsertMap.put("page_code", mAddMaps.get("page_code"));
			
			//插入数据
			DbUp.upTable(mPage.getPageTable()).dataUpdate(mInsertMap, "", "uid");
		}

		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;

	}
	/**
	 * 校验此广告位中是否已经存在相同名称的广告
	 * @param placeCode,adName
	 * @return 
	 */
	private boolean checkInfo(String placeCode,String adName,String uid) {
		StringBuffer strBuffer = new StringBuffer();						//拼接where条件
		strBuffer.append(" place_code = '"+ placeCode);
		strBuffer.append("' and ad_name = '"+adName);
		strBuffer.append("' and uid != '"+uid+"'");
		
		//判断数据库中是否存在相同记录
		 List<MDataMap> list = DbUp.upTable("nc_advertise").queryAll("", "",strBuffer.toString(),null);		
		 if (list.size() > 0) {
				return true;
			}
			return false;
	}
	/**
	 * 检验商品ID是否有对应的商品
	 * @param adImgUrl
	 * @return 
	 */
	private boolean checkIsExistProduct(String adImgUrl) {
		
		//判断数据库中是否存在相同记录
		int count= DbUp.upTable("pc_productinfo").count("product_code", adImgUrl);		
		 if (count >  0) {
				return true;
			}
			return false;
	}
}
