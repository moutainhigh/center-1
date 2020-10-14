package com.cmall.groupcenter.comment.api;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.comment.model.PostCommentReportInput;
import com.cmall.groupcenter.comment.model.PostCommentReportResult;
import com.cmall.groupcenter.util.MemberUtil;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 举报功能:
 * 针对帖子的评论进行举报
 * @author LHY
 * 2015年4月24日 下午2:52:59
 */
public class ApiPostCommentReport extends RootApiForToken<PostCommentReportResult, PostCommentReportInput> {

	public PostCommentReportResult Process(PostCommentReportInput inputParam, MDataMap mRequestMap) {
		PostCommentReportResult result = new PostCommentReportResult();
		
		String postCode = inputParam.getPost_code();
		String commentCode = inputParam.getComment_code();
		String userCode = getUserCode();
		
		MDataMap getMap =  new MemberUtil().getPublisherInfo(userCode);
		if(getMap==null || getMap.size()==0) {
			result.setResultCode(-1);
			result.setResultMessage("发送失败了");
			return result;
		}
		
		StringBuffer sb = new StringBuffer(" member_code = '" + userCode + "'");
		if (StringUtils.isNotEmpty(commentCode) && StringUtils.isNotEmpty(postCode)) {
			sb.append(" and report_postId = '" + postCode + "'");
			sb.append(" and reprot_commetId = '" + commentCode + "'");
			int num = DbUp.upTable("nc_report").dataCount(sb.toString(), new MDataMap());

			if (num == 0) {
				MDataMap mDataMap = new MDataMap();
				mDataMap.put("app_code", getManageCode());
				mDataMap.put("member_code", userCode);
				mDataMap.put("report_time", FormatHelper.upDateTime());
				mDataMap.put("report_postId", postCode);
				mDataMap.put("member_name", StringUtils.isEmpty(getMap.get("mobile"))?"":getMap.get("mobile"));
				mDataMap.put("member_mobilephone", StringUtils.isEmpty(getMap.get("mobile"))?"":getMap.get("mobile"));
				mDataMap.put("reprot_name", StringUtils.isEmpty(getMap.get("nickname"))?"":getMap.get("nickname"));
				mDataMap.put("column_code", "4497465200220002");//好物推荐
				mDataMap.put("content_type", "4497465200240002");//4497465200240001:帖子,4497465200240002:评论,4497465200240003:其他

				mDataMap.put("reprot_commetId", commentCode);
				DbUp.upTable("nc_report").dataInsert(mDataMap);
				
				result.setResultCode(969912012);
				result.setResultMessage("感谢举报，我们会尽快处理");
			} else {
				result.setResultCode(-1);
				result.setResultMessage("此评论您已举报过");
			}
		}
		return result;
	}
}