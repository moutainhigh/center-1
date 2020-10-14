package com.cmall.groupcenter.account.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.account.model.AccountMessageDetailInput;
import com.cmall.groupcenter.account.model.AccountMessageDetailListResult;
import com.cmall.groupcenter.account.model.AccountMessageDetailResult;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.MPageData;
import com.cmall.groupcenter.service.GroupCommonService;
import com.cmall.groupcenter.util.CalendarHelper;
import com.cmall.groupcenter.util.DataPaging;
import com.cmall.groupcenter.util.StringHelper;
import com.cmall.groupcenter.util.WebUtil;
import com.cmall.membercenter.helper.NickNameHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 微公社消息模块-消息详情接口
 *
 * @author lipengfei
 * @date 2015-6-1
 * email:lipf@ichsy.com
 *
 */
public class ApiAccountMessageDetail extends RootApiForToken<AccountMessageDetailListResult,AccountMessageDetailInput>{

	public AccountMessageDetailListResult Process(AccountMessageDetailInput arg0,
			MDataMap arg1) {
		
		AccountMessageDetailListResult result = new AccountMessageDetailListResult();
		List<AccountMessageDetailResult> messageDetailList = new ArrayList<AccountMessageDetailResult>();
		
		
		String userCode = getUserCode();
		GroupCommonService groupCommonService=new GroupCommonService();
		String accountCode=groupCommonService.getAccountCodeByMemberCode(userCode);
		String messageType = arg0.getMessageType();
		MDataMap mWhereMap = new MDataMap();
		
		
		String systemMessageTypeCode = WebUtil.getSystemCodeByMessageCode(messageType);
		//新好友加入不属于系统消息 fengl
//		if(systemMessageTypeCode.equals("44974720000400010003")){
//			result.inErrorMessage(918570008);
//			result.setMessageType(systemMessageTypeCode);
//			return result;
//		}
		mWhereMap.put("user_code", userCode);
		if(systemMessageTypeCode.length()>0 && !"4".equals(systemMessageTypeCode)){
			mWhereMap.put("type", systemMessageTypeCode);
		}
		mWhereMap.put("is_clear", "1");
		MPageData mPageData = DataPaging.upPageData("sc_comment_push_single", "title,content,user_code,create_time,relation_code,type", "-create_time", mWhereMap, arg0.getPageOption());
		
		List<MDataMap>dataList=mPageData.getListData();
		
		AccountMessageDetailResult accountMessageDetail;
		for (MDataMap mDataMap : dataList) {
//			//新好友加入不属于系统消息fengl
//			if("44974720000400010003".equals(mDataMap.get("type"))){
//				continue;
//			}
			accountMessageDetail = new AccountMessageDetailResult();
			
			String title = StringHelper.getStringFromMap(mDataMap,"title");
			String content = StringHelper.getStringFromMap(mDataMap,"content");
			String create_time = StringHelper.getStringFromMap(mDataMap,"create_time");
			
			Date dateTime = CalendarHelper.String2Date(create_time, "yyyy-MM-dd HH:mm:ss");
			
			long formateCreateTime =dateTime.getTime();
			
			accountMessageDetail.setMessageTitle(title);
			accountMessageDetail.setMessageContent(content);
			accountMessageDetail.setMessageDate(String.valueOf(formateCreateTime));
			
			
			String relation_code = StringHelper.getStringFromMap(mDataMap, "relation_code");
			
			if(StringUtils.isNotEmpty(relation_code)){
				MDataMap tempMap = new MDataMap();
				tempMap.put("relation_code", relation_code);
				
				Object value = DbUp.upTable("mc_extend_info_groupcenter").dataGet("head_icon_url", "member_code=:relation_code", tempMap);
				
				String headUrl = "";
				
				if(value!=null){
					headUrl = value.toString();
				}
				
				accountMessageDetail.setRelationMemberCode(relation_code);
				accountMessageDetail.setHeadUrl(headUrl);
				
				//获取用户昵称
				Map<String, String> map = new HashMap<String, String>();
				
				map.put("member_code", relation_code);
				map.put("account_code_wo", accountCode);
				map.put("account_code_ta", groupCommonService.getAccountCodeByMemberCode(relation_code));
				
				String nickName = NickNameHelper.getNickName(map);
				accountMessageDetail.setNickName(nickName);
				if(DbUp.upTable("mc_member_info").count("member_code",relation_code,"manage_code",GroupConst.GROUP_APP_MANAGE_CODE)>0){
					accountMessageDetail.setIsGroup(1);
				}
				else{
					accountMessageDetail.setIsGroup(0);
				}
				 
			}else {//不存在就返回空
				accountMessageDetail.setRelationMemberCode("");
				accountMessageDetail.setHeadUrl("");
			}
			
			
			
			
			
			messageDetailList.add(accountMessageDetail);
		}
		
		result.setMessageType(messageType);
		result.setDetailList(messageDetailList);
		result.setPageResults(mPageData.getPageResults());
		return result;
	}

}
