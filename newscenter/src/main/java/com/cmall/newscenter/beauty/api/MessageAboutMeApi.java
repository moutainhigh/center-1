package com.cmall.newscenter.beauty.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cmall.newscenter.beauty.model.AboutMeMessage;
import com.cmall.newscenter.beauty.model.MessageAboutMeResult;
import com.cmall.newscenter.beauty.model.MessageSystemInput;
import com.cmall.newscenter.model.PageResults;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 消息 - 与我相关
 * @author houwen
 * date 2014-9-16
 * @version 1.0
 */
public class MessageAboutMeApi extends RootApiForToken<MessageAboutMeResult, MessageSystemInput> {
	public MessageAboutMeResult Process(MessageSystemInput inputParam,
			MDataMap mRequestMap) {
		MessageAboutMeResult result = new MessageAboutMeResult();
		
		if(result.upFlagTrue()){ 
			
			MDataMap mWhereMap=new MDataMap();
			
			mWhereMap.put("member_send",getUserCode());
			//mWhereMap.put("message_type", "449746640006,449746640007,449746640008,449746640009");
			
			mWhereMap.put("is_delete","0");
			mWhereMap.put("manage_code",getManageCode());
			//对应评论帖子，赞了帖子，赞了评论，回复评论
		//	message_type = "449746920001,449746920002,449746920003,449746920004";
			//查出系统消息列表          
			List<Map<String, Object>> list = null ;
			String sql = "select * from nc_message_info n where n.is_delete='0' and n.manage_code ='"+getManageCode()+"'  and n.message_type in ('449746920001','449746920002','449746920003','449746920004') and n.member_send in ('"+getUserCode()+ "') and n.member_code not in ('"+getUserCode()+"')  order by is_read asc, create_time desc";
			list = DbUp.upTable("nc_message_info").dataSqlList(sql,mWhereMap);
			if(list!=null){
				int totalNum = list.size();
				int offset = inputParam.getPaging().getOffset();//起始页
				int limit = inputParam.getPaging().getLimit();//每页条数
				int startNum = limit*offset;//开始条数
				int endNum = startNum+limit;//结束条数
				int more = 1;//有更多数据
				Boolean flag = true;
				if(startNum<totalNum){
					flag = false;
				}
				if(endNum>=totalNum){
					if(0==totalNum){
						startNum = 0;
					}
					endNum = totalNum;
					more = 0;
				}
				
				//分页信息
				PageResults pageResults = new PageResults();
				pageResults.setTotal(totalNum);
				pageResults.setCount(endNum-startNum);
				pageResults.setMore(more);
				result.setPaged(pageResults);
				if(!flag){
			    if(list.size()!=0){
				
				List<Map<String, Object>> subList = list.subList(startNum, endNum);
			         
			
				for(int i = 0;i<subList.size();i++){
						
						AboutMeMessage message = new AboutMeMessage();
						message.setMessage_code(subList.get(i).get("message_code").toString());
						message.setMessage_type(subList.get(i).get("message_type").toString());
						message.setMessage_info(subList.get(i).get("message_info").toString());
						PostListApi pApi = new PostListApi();
						SimpleDateFormat sf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
						String time = pApi.transform(subList.get(i).get("create_time").toString(), sf.format(new Date()));
						message.setCreate_time(time);
						//如果点赞等操作的帖子是追帖，则返回主帖的Id
						String postCode = subList.get(i).get("post_code").toString();
						MDataMap postTypeMap = DbUp.upTable("nc_posts").one("post_code",subList.get(i).get("post_code").toString());
						if(postTypeMap!=null){
							if(postTypeMap.get("post_type").equals("449746780002")){
								postCode = postTypeMap.get("post_parent_code");
							}
						}
						message.setPost_code(postCode);
						message.setIs_read(Integer.valueOf(subList.get(i).get("is_read").toString()));
						message.setOld_comment(subList.get(i).get("old_comment").toString());//被评论标题或内容
						//查出用户信息                           
						MDataMap mUserMap = DbUp.upTable("mc_extend_info_star").one("member_code",subList.get(i).get("member_code").toString());
						//用户信息
						message.getMessageUser().setUser_id(subList.get(i).get("member_code").toString());
						if(mUserMap!=null){
						message.getMessageUser().setNickname(mUserMap.get("nickname"));
						message.getMessageUser().setMember_avatar(mUserMap.get("member_avatar"));
						message.getMessageUser().setMobile_phone(mUserMap.get("mobile_phone"));
						message.getMessageUser().setSkin_type(mUserMap.get("skin_type"));
						}
						result.getMessages().add(message);
					}}
				//result.setPaged(mPageData.getPageResults());
			}
			
		}
	}
		return result;

	}
	}
