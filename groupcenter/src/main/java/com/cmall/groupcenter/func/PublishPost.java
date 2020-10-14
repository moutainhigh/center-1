package com.cmall.groupcenter.func;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 微公社发布帖子
 * @author dyc
 * @version 1.0
 **/
public class PublishPost extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		if (mResult.upFlagTrue()) {
			
			try {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String startTime = mAddMaps.get("start_time");
				String endTime = mAddMaps.get("end_time");
				Date date1 = format.parse(startTime);
				Date date2 = format.parse(endTime);
				if(date1.after(date2)) {
					mResult.setResultCode(-1);
					mResult.setResultMessage("开始时间不能小于结束时间");
					return mResult;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			/*系统当前时间*/
			String create_time = DateUtil.getNowTime();
			MUserInfo user = UserFactory.INSTANCE.create();
			if(user==null){
				mResult.inErrorMessage(918546004);
				return mResult;
			}
			String publisher = user.getUserCode();
			String pid = WebHelper.upCode("TZ");
			String contentStr = mAddMaps.get("content");
			
			//帖子表插入参数
			mAddMaps.put("pid", pid);
			mAddMaps.put("create_time",create_time);
			mAddMaps.put("publish_time",create_time);
			mAddMaps.put("publisher",publisher);/*获取当前登录人*/
			mAddMaps.put("actual_browse_num","0");
			mAddMaps.put("actual_collect_num","0");
			mAddMaps.put("actual_share_num","0");
			mAddMaps.put("comment_num","0");
			mAddMaps.put("actual_buyer_num","0");
			mAddMaps.put("actual_reckon_money","0");
			mAddMaps.put("last_modifier",publisher);
			mAddMaps.put("last_modify_time",create_time);
			mAddMaps.remove("content");
			mAddMaps.put("app_code",UserFactory.INSTANCE.create().getManageCode());
			DbUp.upTable("nc_post").dataInsert(mAddMaps);
			
			PostModel post = new JsonHelper<PostModel>().StringToObj(contentStr,new PostModel());
			saveContentAndProductInfo(pid, post);
		}
		return mResult;
	}

	/**
	 * 保存帖子内容及商品信息
	 * */
	public static void saveContentAndProductInfo(String pid,PostModel post){
		List<String> productCodes = new ArrayList<String>();
		
		for(PostContentModel content : post.getPostContents()){
			
			//帖子内容表插入参数
			MDataMap cMap = new MDataMap();
			String pcid = WebHelper.upCode("TZC");
			cMap.put("pid", pid);
			cMap.put("p_cid", pcid);
			ContentDetail detail = content.getContent();
			cMap.put("p_content", detail.getTitle());//将内容标题放入content表中
			DbUp.upTable("nc_post_content").dataInsert(cMap);
			
			for(TextAndImg ti : detail.getTextandimg()){
				//存放帖子内容图文信息
				MDataMap tiMap = new MDataMap();
				tiMap.put("p_cid", pcid);
				tiMap.put("sort", ti.getSort()+"");
				tiMap.put("content", ti.getText());
				tiMap.put("type", ti.getType());
				DbUp.upTable("nc_post_content_detail").dataInsert(tiMap);
			}
			
			for(ProductModel p : content.getProducts()){
				//帖子内容商品表插入参数
				MDataMap pMap = new MDataMap();
				pMap.put("pcid",pcid);
				pMap.put("product_code",p.getCode());
				pMap.put("product_name",p.getName());
				pMap.put("product_pic",p.getPic());
				pMap.put("product_price",p.getPrice());
				pMap.put("product_desc",p.getDesc());
				pMap.put("product_source",p.getSource());
				pMap.put("product_link",p.getLink());
				DbUp.upTable("nc_post_products").dataInsert(pMap);
				if(StringUtils.isNotBlank(p.getCode()))
					productCodes.add(p.getCode());
			}
		}
		
		//更新帖子表中的实际购买人数和实际返利金额总和
		updateProBuyerAndReckonCount(productCodes, pid);
	}
	
	
	/**
	 * 更新帖子表中所有商品的实际购买人数和实际返利金额总和
	 * 
	 * 购买人数总和=商品清分条目数
	 * 金额总和=正向返利总和
	 * 
	 * @param productCodes 商品编号
	 * @param pid 帖子id
	 * */
	public static void updateProBuyerAndReckonCount(List<String> productCodes,String pid){
		String sql = "";
		if(productCodes==null){//如果productCodes==null则根据pid查出帖子所有的productCode
			productCodes = new ArrayList<String>();
			List<MDataMap> pros = DbUp.upTable("nc_post_products").queryAll("product_code", "", "pcid in (select p_cid from nc_post_content where pid = '"+pid+"')", new MDataMap());
			for(MDataMap tmp : pros){
				if(StringUtils.isNotBlank(tmp.get("product_code"))){
					productCodes.add(tmp.get("product_code"));
				}
			}
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		if(productCodes.size()>0){
			sql = "SELECT count(1) count,IFNULL(sum(reckon_money),0.0) sum FROM gc_reckon_log where reckon_change_type='4497465200030001' and sku_code in ('"+StringUtils.join(productCodes,"','")+"')";
			map = DbUp.upTable("gc_reckon_log").dataSqlOne(sql, new MDataMap());
		}else{
			map.put("count", "0");
			map.put("sum", "0");
		}
		if(map!=null){
			MDataMap param = new MDataMap();
			param.put("pid", pid);
			param.put("actual_buyer_num", map.get("count").toString());
			param.put("actual_reckon_money", map.get("sum").toString());
			DbUp.upTable("nc_post").dataUpdate(param, "", "pid");
		}
	}
	
}
