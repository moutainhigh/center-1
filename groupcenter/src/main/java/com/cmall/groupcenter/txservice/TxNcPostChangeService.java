package com.cmall.groupcenter.txservice;

import java.util.List;

import com.cmall.dborm.txmapper.newscenter.NcPostMapper;
import com.cmall.dborm.txmodel.newscenter.NcPost;
import com.cmall.dborm.txmodel.newscenter.NcPostExample;
import com.cmall.groupcenter.recommend.model.ApiChangePostCollectBrowseAndShareNumInput;
import com.cmall.groupcenter.recommend.model.ApiChangePostCollectBrowseAndShareNumResult;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.RegexHelper;

/**
 * 更新ncpost
 */
public class TxNcPostChangeService extends BaseClass {
	//更新帖子浏览量、收藏量、分享量
	public ApiChangePostCollectBrowseAndShareNumResult changeNcPostSomeNum(ApiChangePostCollectBrowseAndShareNumInput inputParam, 
			String appCode) {
		ApiChangePostCollectBrowseAndShareNumResult result = new ApiChangePostCollectBrowseAndShareNumResult();
		String pid = inputParam.getPid();
		if(pid.endsWith("#")) {
			pid = pid.substring(0, pid.length()-1);
		}
		String changeCollectNumStr = inputParam.getChangeCollectNum();
		String changeBrowseNumStr = inputParam.getChangeBrowseNum();
		String changeShareNumStr = inputParam.getChangeShareNum();
		//收藏改变量
		int changeCollectNum = 0;
		//浏览改变量
		int changeBrowseNum = 0;
		//分享改变量
		int changeShareNum = 0;
		NcPostMapper ncPostMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_newscenter_NcPostMapper");
		
		NcPostExample ncPostExample = new NcPostExample();
		ncPostExample.createCriteria().andPidEqualTo(pid).andAppCodeEqualTo(appCode);
		List<NcPost> list = ncPostMapper.selectByExample(ncPostExample);
		
		if (list != null && list.size() == 1) {
			NcPost ncPost = list.get(0);
			NcPost toSaveNcPost = new NcPost();
			if(null != changeCollectNumStr && RegexHelper.checkRegexField(changeCollectNumStr, "base=minus_number") 
					&& Integer.parseInt(changeCollectNumStr) != 0) {
				changeCollectNum = Integer.parseInt(changeCollectNumStr);
				toSaveNcPost.setActualCollectNum(ncPost.getActualCollectNum() + changeCollectNum);
			}
			if(null != changeBrowseNumStr && RegexHelper.checkRegexField(changeBrowseNumStr, "base=minus_number") 
					&& Integer.parseInt(changeBrowseNumStr) != 0) {
				changeBrowseNum = Integer.parseInt(changeBrowseNumStr);
				toSaveNcPost.setActualBrowseNum(ncPost.getActualBrowseNum() + changeBrowseNum);
			}
			if(null != changeShareNumStr && RegexHelper.checkRegexField(changeShareNumStr, "base=minus_number") 
					&& Integer.parseInt(changeShareNumStr) != 0) {
				changeShareNum = Integer.parseInt(changeShareNumStr);
				toSaveNcPost.setActualShareNum(ncPost.getActualShareNum() + changeShareNum);
			}
			if(changeCollectNum != 0 || changeBrowseNum != 0 || changeShareNum != 0) {
				ncPostMapper.updateByExampleSelective(toSaveNcPost, ncPostExample);
			}
			
			result.setChangedBrowseNum(ncPost.getActualBrowseNum() + changeBrowseNum + ncPost.getBrowseAddNum());
			result.setChangedCollectNum(ncPost.getActualCollectNum() + changeCollectNum + ncPost.getCollectAddNum());
			result.setChangedShareNum(ncPost.getActualShareNum() + changeShareNum + ncPost.getShareAddNum());
		} else {
			result.setResultCode(918519016);
			result.setResultMessage(bInfo(918519016));
		}
		if(changeCollectNum == 0 
				&& changeBrowseNum == 0 && changeShareNum == 0) {
			result.setResultCode(918519019);
			result.setResultMessage(bInfo(918519019));
		}
		return result;
	}
}
