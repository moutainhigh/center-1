package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.membercenter.model.ScoredChange;
import com.cmall.membercenter.support.ScoredSupport;
import com.cmall.newscenter.model.CommentdityAppPhotos;
import com.cmall.newscenter.model.OrderRateInput;
import com.cmall.newscenter.model.OrderRateResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 订单-评价
 * @author liqiang
 * date 2014-7-22
 * @version 1.0
 */
public class OrderRateApi extends RootApiForToken<OrderRateResult, OrderRateInput> {

	public OrderRateResult Process(OrderRateInput inputParam,
			MDataMap mRequestMap) {
		
		OrderRateResult result = new OrderRateResult();
		
		MDataMap mDataMap = new MDataMap();
		
		if(result.upFlagTrue()){
			
			
			MDataMap orderMap = DbUp.upTable("nc_order_evaluation").one("order_code",inputParam.getOrder(),"order_skuid",inputParam.getSkuid());
			
			if(("".equals(inputParam.getText())||null==inputParam.getText())&&(inputParam.getPhotos().size()==0)){
				
				if(orderMap!=null){
					
					result.setCommented(1);
				}else {
					
					result.setCommented(0);
				}
				
			}else {
				
			if(orderMap!=null){
			
				ScoredChange scordChange = new ScoredChange();
				
				 String sSql = "select * from mc_extend_info_star ms ,mc_member_level mg where ms.member_code =:member_code and ms.member_level = mg.level_code";
					
					MDataMap  mberMap = new MDataMap();
					
					mberMap.put("member_code", getUserCode());
				
				    Map<String, Object> mMemberMap = DbUp.upTable("mc_extend_info_star").dataSqlOne(sSql, mberMap);
				
				 if(mMemberMap!=null&&!mMemberMap.isEmpty()){
					
				  scordChange.setLevel(Integer.valueOf(mMemberMap.get("level_code").toString().substring(mMemberMap.get("level_code").toString().length()-4, mMemberMap.get("level_code").toString().length())));
				  
				  scordChange.setScore(0);
				  
				  scordChange.setScore_unit(bConfig("membercenter.member_default_scopeunit"));
				  
				  scordChange.setLevel_name(String.valueOf(mMemberMap.get("level_name")));
				  
			  }
				
				    result.setScordChange(scordChange);
					
					result.setCommented(1);
				
				
			}else {
				
			
			mDataMap.put("order_code", inputParam.getOrder());
			
			mDataMap.put("order_skuid", inputParam.getSkuid());
			
			mDataMap.put("order_assessment", inputParam.getText());
			
			mDataMap.put("oder_creattime", FormatHelper.upDateTime());
			
			mDataMap.put("order_name", getUserCode());
			
			mDataMap.put("manage_code", getManageCode());
			
			List<CommentdityAppPhotos> photos = new ArrayList<CommentdityAppPhotos>(); 
			
			photos = inputParam.getPhotos();
			
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
				
			}
			MDataMap skuMap = DbUp.upTable("pc_skuinfo").one("sku_code",inputParam.getSkuid());
			if(skuMap != null){
				mDataMap.put("product_code", skuMap.get("product_code"));
			}
			mDataMap.put("flag_show", "449746530001");
			mDataMap.put("check_flag", "4497172100030002");
			/*插入订单评价信息*/
			DbUp.upTable("nc_order_evaluation").dataInsert(mDataMap);
			
		
		/*积分返回*/
		ScoredChange scordChange = new ScoredChange();
		
		int wordScord = 0;
		
		int photoScord = 0;
		
		
		if("".equals(inputParam.getText())){
			
		}else{
			/*返回文字积分*/
			scordChange = new ScoredSupport().reviewScored(getUserCode(),inputParam.getOrder(),inputParam.getSkuid());
			
			wordScord = scordChange.getScore();
			
		}
		if(inputParam.getPhotos().size()!=0){
			/*获得图片积分*/
			scordChange = new ScoredSupport().reviewPhotoScored(getUserCode(),inputParam.getOrder(),inputParam.getSkuid());
			
			photoScord = scordChange.getScore();
		}
		
		scordChange.setScore(wordScord+photoScord);
		
		result.setScordChange(scordChange);
		
		result.setCommented(1);
		
			}
		}
		}
		return result;
	}

}
