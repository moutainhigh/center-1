package com.cmall.newscenter.beauty.api;

import com.cmall.newscenter.beauty.model.PostPraiseInput;
import com.cmall.newscenter.beauty.model.PostPraiseResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.cmall.productcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 帖子点赞
 * 
 * @author houwen date 2014-09-10
 * @version 1.0
 */

public class PostsPraiseApi extends
		RootApiForToken<PostPraiseResult, PostPraiseInput> {

	public PostPraiseResult Process(PostPraiseInput inputParam,
			MDataMap mRequestMap) {

		PostPraiseResult result = new PostPraiseResult();
		if (result.upFlagTrue()) {

			// MDataMap mDataMap = new MDataMap();

			MDataMap mwhereDataMap = new MDataMap();

			MPageData mPageData = new MPageData();

			MDataMap mpostDataMap = new MDataMap();

			MDataMap minsertDataMap = new MDataMap();

			boolean count = true;
			int praise = 0;
			MDataMap map = new MDataMap();
			String parentPostCode = "";
			MDataMap mWhereMap = new MDataMap();
			map.put("post_code", inputParam.getPost_code());

			MPageData mpostData = DataPaging.upPageData("nc_posts", "", "",
					map, new PageOption());

			if (mpostData.getListData().size() != 0) {
				
				if (mpostData.getListData().get(0).get("post_parent_code") != null && !mpostData.getListData().get(0).get("post_parent_code").equals("")) {
					
					parentPostCode = mpostData.getListData().get(0).get("post_parent_code");
					mWhereMap.put("post_parent_code", parentPostCode);
				}
			}

			mWhereMap.put("operater_code", getUserCode());

			mWhereMap.put("operate_type", "4497464900030006");

			mWhereMap.put("info_code", inputParam.getPost_code());

			mWhereMap.put("app_code", getManageCode());

			MPageData moperatePageData = DataPaging.upPageData(
					"nc_post_operate", "", "", mWhereMap, new PageOption());

			if (moperatePageData.getListData().size() != 0) {
				for (MDataMap mDataMap : moperatePageData.getListData()) {
					if (moperatePageData.getListData().get(0).get("flag").equals("1")) { // 是否取消点赞： 0：是；1：否
						
						mDataMap.put("flag", "0");
						count = false;
					} else {
						mDataMap.put("flag", "1");
					}
					DbUp.upTable("nc_post_operate").update(mDataMap);
				}
			} else {
				// 把点赞这一操作信息插入到表中 nc_post_operate
				minsertDataMap.put("operater_code", getUserCode());

				minsertDataMap.put("info_code", inputParam.getPost_code());

				minsertDataMap.put("post_parent_code", parentPostCode);

				minsertDataMap.put("operate_type", "4497464900030006"); // 操作类型：是执行的点赞还是收藏操作;
																		// 4497464900030006:点赞，4497464900030005：收藏

				minsertDataMap.put("flag", "1");

				minsertDataMap.put("app_code", getManageCode());

				DbUp.upTable("nc_post_operate").dataInsert(minsertDataMap);

				// mpostDataMap.put("ispraise", "449746870001"); //是否被点赞过：
				// 449746870001： 被点赞过 ；未被 点赞过:449746870002
			}

			// 根据帖子ID向nc_posts表中添加点赞数 每操作一次 +1
			mwhereDataMap.put("post_code", inputParam.getPost_code());

			mPageData = DataPaging.upPageData("nc_posts", "", "",mwhereDataMap, new PageOption());

			if (mPageData.getListData().size() != 0) {

				praise = Integer.parseInt(mPageData.getListData().get(0).get("post_praise"));

				if (count) {
					praise = praise + 1;
				} else {
					praise = praise - 1;
				}

				mpostDataMap.put("post_praise", String.valueOf(praise));

				mpostDataMap.put("post_code", inputParam.getPost_code());

				DbUp.upTable("nc_posts").dataUpdate(mpostDataMap, "","post_code");

				result.setPost_praise(praise);
			}

			if (count) { // 如果进行的是点赞操作，则向表中插入信息
				
				/* 将点赞消息插入到消息表中 */
				MessageTemplate.MessageReplyRule(inputParam.getPost_code(),getUserCode(),mPageData.getListData().get(0).get("publisher_code"),"", mPageData.getListData().get(0).get("post_title"),getManageCode(), "449746920002");

				//推送表中插入一条消息
				
				MDataMap dbDataMap = DbUp.upTable("nc_sys_push_configure").one("configure_code","TS449746660004","status","449747090001","app_code",getManageCode());
			
				if(dbDataMap!=null && !dbDataMap.isEmpty()){
					
					String start = dbDataMap.get("push_time_start").toString();
					
					String end = dbDataMap.get("push_time_end").toString();
					
					String now = DateUtil.getSysTimeString();
					
					int num1 = now.compareTo(start);
					
					int num2 = end.compareTo(now);
					
					Boolean flag = num1>=0 && num2>=0;
					
					if(start.equals("全天") || flag ){
						
						MDataMap datamap = DbUp.upTable("mc_extend_info_star").one("member_code", getUserCode());
						
						String content = dbDataMap.get("comment").replace("***", datamap.get("nickname")).replace("%%%", mpostData.getListData().get(0).get("post_title"));
						
						MDataMap insertmap = new MDataMap();
						
						if (datamap != null) {

							insertmap.inAllValues("accept_member",mpostData.getListData().get(0).get("publisher_code"),"comment",content,"push_time", DateUtil.getSysDateTimeString(),"jump_type", "6", "jump_position",inputParam.getPost_code(), "push_status","4497465000070001", "create_time",DateUtil.getSysDateTimeString(), "app_code",getManageCode());

							DbUp.upTable("nc_comment_push_system").dataInsert(insertmap);
						}
					}
					
				}

			}

		}

		return result;
	}

}
