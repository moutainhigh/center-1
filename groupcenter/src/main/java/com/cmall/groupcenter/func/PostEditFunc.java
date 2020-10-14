package com.cmall.groupcenter.func;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 微公社修改帖子
 * @author dyc
 * @version 1.0
 **/
public class PostEditFunc extends RootFunc{

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
			String contentStr = mAddMaps.get("content");
			String pid = mAddMaps.get("pid");
			
			mAddMaps.put("last_modifier",publisher);/*获取当前登录人*/
			mAddMaps.put("last_modify_time",create_time);
			mAddMaps.remove("content");			
			DbUp.upTable("nc_post").dataUpdate(mAddMaps, "", "uid");
			
			//查出所有与此贴有关联的pcid
			List<String> pcids = new ArrayList<String>();
			for(MDataMap m : DbUp.upTable("nc_post_content").queryAll("p_cid", "", "pid='"+pid+"'", new MDataMap())){
				if(StringUtils.isNotBlank(m.get("p_cid")))
					pcids.add(m.get("p_cid"));
			}
			
			//删除商品表的商品
			DbUp.upTable("nc_post_products").dataDelete("pcid in('"+StringUtils.join(pcids,"','")+"')", new MDataMap(), "");
			//删除内容详情表的内容
			DbUp.upTable("nc_post_content_detail").dataDelete("p_cid in('"+StringUtils.join(pcids,"','")+"')", new MDataMap(), "");
			//删除帖子内容
			DbUp.upTable("nc_post_content").delete("pid",pid);
			
			PostModel post = new JsonHelper<PostModel>().StringToObj(contentStr,new PostModel());
			PublishPost.saveContentAndProductInfo(pid, post);						
		}
		return mResult;
	}

}
