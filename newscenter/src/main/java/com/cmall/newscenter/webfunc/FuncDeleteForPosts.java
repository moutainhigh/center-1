package com.cmall.newscenter.webfunc;

import java.util.List;
import java.util.Map;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 删除帖子
 * @author houw
 * 已去掉删除帖子功能
 */
public class FuncDeleteForPosts extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mDelMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		mDelMaps.put("is_delete", "1");
		DbUp.upTable(mPage.getPageTable()).dataUpdate(mDelMaps,"is_delete",
				"uid");
		MDataMap list = DbUp.upTable("nc_posts").one("uid", mDelMaps.get("uid"));
		MDataMap mapMDataMap = new MDataMap();
		mapMDataMap.put("post_parent_code", list.get("post_code"));
		List<Map<String,Object>> list2 = DbUp.upTable("nc_posts").dataQuery("", "", "",mapMDataMap , -1,-1);
		if(list2.size()>0){
			MDataMap map = new MDataMap();
			map.put("post_parent_code", list.get("post_code"));
			map.put("is_delete", "1");
			DbUp.upTable("nc_posts").dataUpdate(map,"is_delete", "post_parent_code");
		}
		MDataMap mapMDataMap2 = new MDataMap();
		mapMDataMap2.put("post_code", list.get("post_code"));
		List<Map<String,Object>> list3 = DbUp.upTable("nc_posts_comment").dataQuery("", "", "",mapMDataMap2 , -1,-1);
		if(list3.size()>0){
		MDataMap map2 = new MDataMap();
		map2.put("post_code", list.get("post_code"));
		map2.put("is_delete", "1");
		DbUp.upTable("nc_posts_comment").dataUpdate(map2, "is_delete", "post_code");
		}

		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}

		return mResult;
	}

}
