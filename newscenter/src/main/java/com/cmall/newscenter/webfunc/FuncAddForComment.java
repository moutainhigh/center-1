package com.cmall.newscenter.webfunc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.newscenter.model.CommentdityAppPhotos;
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
 * 发布商品评论
 * @author houwen
 *
 */
public class FuncAddForComment extends RootFunc {
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
					
					if(mField.getColumnName().equals("oder_photos")){
						
			/*			对图片进行缩略图处理
						List<CommentdityAppPhotos> photos = new ArrayList<CommentdityAppPhotos>(); 
						
					    String phos[]	= sValue.split("\\|");
						
						//photos = sValue.split("\\|");
						
						CommentdityAppPhotos photo = new CommentdityAppPhotos();
						
						String large = "";
						
						String thumb = "";
						
						if(photos.size()>1){
							for(int i=0;i<photos.size();i++){
								
							   photo = photos.get(i);
								
							   String largephoto = photo.getLarge();
							   
							   String thumbphoto = photo.getThumb();
							   
							   large += largephoto+"|";
							   
							   thumb += thumbphoto+"|";
								
							}
							
							mDataMap.put("oder_photos", large.substring(0,large.length()-1));
							
							mDataMap.put("order_smallphotos",thumb.substring(0,thumb.length()-1));
							
						}else if(photos.size()==1) {
							
							 photo = photos.get(0);
							
							mDataMap.put("oder_photos", photo.getLarge());
							
							mDataMap.put("order_smallphotos", photo.getThumb());
							
						}*/
						
						
						mInsertMap.put("order_smallphotos",sValue);
					}
					
			
				}

			}
		}
		
		
		/*//编码自动生成
		mInsertMap.put("configuration_id", WebHelper.upCode(""));*/
		
		//创建时间为当年系统时间
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
		
		mInsertMap.put("oder_creattime", df.format(new Date()));   // new Date()为获取当前系统时间
		
				
		mInsertMap.put("manage_code",UserFactory.INSTANCE.create().getManageCode()); // app
		 
		
		if (mResult.upFlagTrue()) {
			
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
	
}
