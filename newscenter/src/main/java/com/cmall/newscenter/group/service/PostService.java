package com.cmall.newscenter.group.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.groupapp.model.ShareModel;
import com.cmall.newscenter.group.model.PostListInput;
import com.cmall.newscenter.group.model.PostListResult;
import com.cmall.newscenter.group.model.PostsList;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.util.DataPaging;
import com.cmall.productcenter.model.PicInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class PostService {

	public PostListResult getPostList(PostListInput inputParam,String manageCode,String weiPrefix){
		PostListResult result = new PostListResult();
		
		List<PostsList> posts = new ArrayList<PostsList>();
		MPageData mPageData = DataPaging.upPageData("nc_post", "", "app_code =:app_code and flag_enable =:flag_enable and start_time <=:now_date and end_time >=:now_date","-sort,-last_modify_time,-publish_time", new MDataMap("app_code", manageCode,"flag_enable", "4497472000010001","now_date",DateHelper.upNow()), inputParam.getPaging());

		//MPageData mPageData = DataPaging.upPageData("nc_post", "", "flag_enable =:flag_enable and start_time <=:now_date and end_time >=:now_date","-sort,-last_modify_time,-publish_time", new MDataMap("flag_enable", "4497472000010001","now_date",DateHelper.upNow()), inputParam.getPaging());
	    
		if(mPageData.getListData().size() >0){
			
			for(MDataMap mDataMap : mPageData.getListData()){
				PostsList postsList = new PostsList();
				postsList.setListImgUrl(mDataMap.get("list_img_url"));
				postsList.setWxListBigImgUrl(mDataMap.get("wx_big_img_url"));
				postsList.setWxListSmallImgUrl(mDataMap.get("wx_small_img_url"));
				postsList.setpId(mDataMap.get("pid"));
				postsList.setpTitle(mDataMap.get("p_title"));
				postsList.setPublishTime(mDataMap.get("publish_time"));
				postsList.setLastUpdateTime(mDataMap.get("last_modify_time"));
				postsList.setShareNum(Integer.valueOf(mDataMap.get("actual_share_num")) + Integer.valueOf(mDataMap.get("share_add_num")) + "");
				if("0.00".equals(mDataMap.get("actual_reckon_money")) && "0.00".equals(mDataMap.get("reckon_add_money"))){
					postsList.setRebateAmount((int)Double.parseDouble(mDataMap.get("actual_reckon_money")) + "");
				}else{
					postsList.setRebateAmount((int)(Double.valueOf(mDataMap.get("actual_reckon_money")) + Double.valueOf(mDataMap.get("reckon_add_money"))) + "");
				}
				postsList.setRebateAmount(convertNum(postsList.getRebateAmount(),1));
				postsList.setShareNum(convertNum(postsList.getShareNum(),1));
				
				int productNum = 0;
				//查找商品条数,先查发布的内容,然后查发布内容对应的商品
				List<MDataMap> postContents = DbUp.upTable("nc_post_content").queryByWhere("pid",mDataMap.get("pid"));
				for(MDataMap postContentMap : postContents){
					productNum += DbUp.upTable("nc_post_products").count("pcid",postContentMap.get("p_cid"));
				}
				postsList.setProductNum(productNum + "");
				
				postsList.setTimeLable(judgeDate(postsList.getLastUpdateTime())+"");
				

				//分享实体
				ShareModel smodel = new ShareModel();
				smodel.setShareContent(mDataMap.get("p_intro"));
				smodel.setShareTitle(mDataMap.get("p_title"));
				smodel.setSharePicUrl(mDataMap.get("list_img_url"));
				smodel.setShareUrl(weiPrefix+"/cgroup/web/grouppageSecond/recommenddetail.ftl?pid="+postsList.getpId()+"&isShare=true");
				postsList.setShareModel(smodel);
				posts.add(postsList);
			}
			result.setPosts(posts);
			result.setPaged(mPageData.getPageResults());
		}else{
			result.inErrorMessage(918519016);
		}
		return result;
	}
	/**
	 * 判断时间
	 * 返回0代表当天，1为昨天，2是当年的 long ago,3为long long ago
	 * @return 
	 */
	private int judgeDate(String dateStr){
		if(StringUtils.isEmpty(dateStr))
			return 3;
		int[] typeArray = new int[]{Calendar.YEAR,Calendar.MONTH,Calendar.DAY_OF_MONTH};
		
		int h = 0;
		int c = 0;
		int differ = 0;
		Calendar cal = Calendar.getInstance();
		for(int i=0;i<typeArray.length;i++){
			cal.setTime(DateHelper.parseDate(dateStr));
			h = cal.get(typeArray[i]);
			cal.setTime(new Date());
			c = cal.get(typeArray[i]);
			differ = c -h;
			if(i == 0 && differ > 0){
				differ = 3;
				break;
			}
			if(i == 1 && differ > 0){
				differ = 2;
				break;
			}
			if(i == 2){
				if(differ > 2){
					differ = 2;
				}
				break;
			}
		}
		return differ;
	}
	/**
	 * 1.千位以内（包括千位），数字全部显示，即 xxxx人；
	*	2.超过千位时，数字显示x.x万人，保留一位小数，向上四舍五入；
	 */
	private String convertNum(String targetNum,int digit){
		String result= "";
		double differ = Double.parseDouble(targetNum)/10000;
		switch((int)Math.floor(differ)){
		case 0:
			result = targetNum + "";
			break;
		default :
			BigDecimal bd = new BigDecimal(differ);  
			result = bd.setScale(digit, BigDecimal.ROUND_HALF_UP).doubleValue() +"万";
			break;
		}
		return result;
	}
	/**
	 * 
	 * @param inputParam
	 * @param manageCode
	 * @return
	 */
	public PostListResult getPostListByCurrentMonth(String manageCode){
		PostListResult result = new PostListResult();
		DecimalFormat df = new DecimalFormat("#.00");  
		
		List<PostsList> posts = new ArrayList<PostsList>();
		
		List<MDataMap> mDateList = DbUp.upTable("nc_post").query("", "-sort,-last_modify_time,-publish_time", "app_code =:app_code and flag_enable =:flag_enable and start_time <=:now_date and end_time >=:now_date and month(last_modify_time) =:currentMonth", new MDataMap("app_code", manageCode,"flag_enable", "4497472000010001","now_date",DateHelper.upNow(),"currentMonth",DateHelper.getTimeEleByType(null, Calendar.MONTH)+""), -1, -1);
		
		if(mDateList.size() >0){
			ProductService productService = new ProductService();
			for(MDataMap mDataMap : mDateList){
				PostsList postsList = new PostsList();
				postsList.setListImgUrl(mDataMap.get("list_img_url"));
				
				List<String> list = new ArrayList<String>();
				list.add(postsList.getListImgUrl());
				//获取小图地址640*480
				List<PicInfo> picInfos = productService.getPicInfoOprBigForMulti(640,list);
				if(null != picInfos && picInfos.size() > 0)
					postsList.setSmallListImgUrl(picInfos.get(0).getPicNewUrl());
				
				postsList.setpId(mDataMap.get("pid"));
				postsList.setpTitle(mDataMap.get("p_title"));
				postsList.setPublishTime(mDataMap.get("publish_time"));
				postsList.setpIntroduction(mDataMap.get("p_intro"));
				postsList.setLastUpdateTime(mDataMap.get("last_modify_time"));
				postsList.setShareNum(Integer.valueOf(mDataMap.get("actual_share_num")) + Integer.valueOf(mDataMap.get("share_add_num")) + "");
				if("0.00".equals(mDataMap.get("actual_reckon_money")) && "0.00".equals(mDataMap.get("reckon_add_money"))){
					postsList.setRebateAmount(mDataMap.get("actual_reckon_money"));
				}else{
					postsList.setRebateAmount(df.format(Double.valueOf(mDataMap.get("actual_reckon_money")) + Double.valueOf(mDataMap.get("reckon_add_money"))) +"");
				}
				int productNum = 0;
				//查找商品条数,先查发布的内容,然后查发布内容对应的商品
				List<MDataMap> postContents = DbUp.upTable("nc_post_content").queryByWhere("pid",mDataMap.get("pid"));
				for(MDataMap postContentMap : postContents){
					productNum += DbUp.upTable("nc_post_products").count("pcid",postContentMap.get("p_cid"));
				}
				postsList.setProductNum(productNum + "");
				
				posts.add(postsList);
			}
			result.setPosts(posts);
		}else{
			result.inErrorMessage(918519016);
		}
		return result;
	}
}
