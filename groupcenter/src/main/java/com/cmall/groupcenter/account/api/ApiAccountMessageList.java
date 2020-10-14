package com.cmall.groupcenter.account.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.account.model.AccountMessageListInput;
import com.cmall.groupcenter.account.model.AccountMessageListResult;
import com.cmall.groupcenter.account.model.AccountMessageResult;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.util.CalendarHelper;
import com.cmall.groupcenter.util.StringHelper;
import com.cmall.groupcenter.util.WebUtil;
import com.cmall.groupcenter.wallet.service.WalletWithdrawService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 微公社消息模块-消息列表接口
 *
 * @author lipengfei
 * @date 2015-6-1 email:lipf@ichsy.com
 *
 */
public class ApiAccountMessageList extends
		RootApiForToken<AccountMessageListResult, AccountMessageListInput> {
	public static final String PUSH_TYPE_ID = "b03ea03f3e384a4aad2dc8ab8b829284";

	public AccountMessageListResult Process(AccountMessageListInput input,
			MDataMap arg1) {

		AccountMessageListResult result = new AccountMessageListResult();
		List<AccountMessageResult> accountMessageList = new ArrayList<AccountMessageResult>();

		String userCode = getUserCode();

		MDataMap mWhereMap = new MDataMap();

		mWhereMap.put("userCode", userCode);
		
		MDataMap accountMap=DbUp.upTable("mc_member_info").one("member_code",userCode,"flag_enable","1");
		
		mWhereMap.put("accountCode", accountMap.get("account_code"));
		
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("account_code", accountMap.get("account_code"));
		mDataMap.put("user_code", userCode);
		DbUp.upTable("sc_comment_push_single").dataExec("update sc_comment_push_single set user_code=:user_code where account_code=:account_code and user_code='' and is_read='4497465200180001'and type='44974720000400010003' and is_clear='1'",mDataMap);

		String messageListSql=null;
		String messageQuantitySql=null;

		if ("0".equals(input.getMessageListType())){

			messageListSql = " SELECT "
					+ " temp.content,temp.user_code,temp.type as type,temp.create_time,temp.title "
					+ " FROM "
					+ " (SELECT "
					+ "single.content,single.user_code,single.type,single.create_time,single.title "
					+ " FROM " + " `systemcenter`.sc_comment_push_single single "
					+ " WHERE user_code =:userCode  "
					+ " AND is_clear='1' "
//					+ " AND type!='44974720000400010003' "
					+ " AND LENGTH(type)>0 "
					+ "  ORDER BY single.`create_time` DESC) temp "
					+ " GROUP BY temp.`type` ";

			messageQuantitySql = " SELECT "
					+ " COUNT(single.zid) AS messageQuantity, " + " single.type as type "
					+ " FROM " + " `systemcenter`.`sc_comment_push_single` single "
					+ " WHERE single.user_code =:userCode "
					+ " AND single.is_clear='1' "
//					+ " AND type!='44974720000400010003' "
					+ " AND single.`is_read` = '4497465200180001' "
					+ " GROUP BY single.`type` ";

		}else if ("1".equals(input.getMessageListType())){

			messageListSql = " SELECT "
					+ " temp.content,temp.user_code,1 as type,temp.create_time,temp.title "
					+ " FROM "
					+ " (SELECT "
					+ "single.content,single.user_code,single.type,single.create_time,single.title "
					+ " FROM " + " `systemcenter`.sc_comment_push_single single "
					+ " WHERE user_code =:userCode "
					+ " AND is_clear='1' "
//					+ " AND type!='44974720000400010003' "
					+ " AND LENGTH(type)>0 "
					+ "  ORDER BY single.`create_time` DESC) temp LIMIT 0,1 ";

			 messageQuantitySql = " SELECT "
					+ " COUNT(single.zid) AS messageQuantity, " + " 1 as type "
					+ " FROM " + " `systemcenter`.`sc_comment_push_single` single "
					+ " WHERE single.user_code =:userCode "
					+ " AND single.is_clear='1' "
//					+ " AND type!='44974720000400010003' "
					+ " AND single.`is_read` = '4497465200180001' ";
		}else {
			messageListSql = " SELECT "
					+ " temp.content,temp.user_code,temp.type as type,temp.create_time,temp.title "
					+ " FROM "
					+ " (SELECT "
					+ "single.content,single.user_code,single.type,single.create_time,single.title "
					+ " FROM " + " `systemcenter`.sc_comment_push_single single "
					+ " WHERE user_code =:userCode "
					+ " AND is_clear='1' "
//					+ " AND type!='44974720000400010003' "
					+ " AND LENGTH(type)>0 "
					+ "  ORDER BY single.`create_time` DESC) temp "
					+ " GROUP BY temp.`type` ";

			messageQuantitySql = " SELECT "
					+ " COUNT(single.zid) AS messageQuantity, " + " single.type as type "
					+ " FROM " + " `systemcenter`.`sc_comment_push_single` single "
					+ " WHERE single.user_code =:userCode "
//					+ " or (single.account_code=:accountCode and single.user_code='' and single.type ='44974720000400010003' ))"					
					+ " AND single.is_clear='1' "
//					+ " AND type!='44974720000400010003' "
					+ " AND single.`is_read` = '4497465200180001' "
					+ " GROUP BY single.`type` ";
		}


		// 查询消息列表
		List<Map<String, Object>> messageList = DbUp.upTable(
				"sc_comment_push_single")
				.dataSqlList(messageListSql, mWhereMap);


		// 查询未读消息的条数
		List<Map<String, Object>> messageQuantity = DbUp.upTable(
				"sc_comment_push_single").dataSqlList(messageQuantitySql,
				mWhereMap);

		// 查询账户编号
		WalletWithdrawService walletWithdrawService = new WalletWithdrawService();

		String accountCode = walletWithdrawService.getAccountCode(userCode,
				getManageCode());
		String messageNotifactionStatus = "";
		String push_type_onoff = "";
		if (StringUtils.isNotEmpty(accountCode)) {
			MDataMap pushRecord = DbUp.upTable("gc_account_push_set").one(
					"account_code", accountCode, "push_type_id", PUSH_TYPE_ID);
			if (pushRecord == null) {
				messageNotifactionStatus = "1";// 关闭
			} else {
				push_type_onoff = pushRecord.get("push_type_onoff");
				if ("449747100001".equals(push_type_onoff)) {
					messageNotifactionStatus = "0";// 开启
				} else if ("449747100002".equals(push_type_onoff)) {
					messageNotifactionStatus = "1";// 关闭
				}
			}

		} else {
			result.inErrorMessage(918570003);
		}

		AccountMessageResult messageResult = null;

		for (Map<String, Object> map : messageList) {
			messageResult = new AccountMessageResult();
			messageResult.setMessageContent(StringHelper.getStringFromMap(map,
					"content"));

			messageResult.setMessageTitle(StringHelper.getStringFromMap(map,
					"title"));

			String type = StringHelper.getStringFromMap(map, "type");

			String systemMessageCode = WebUtil.getMessageCodeBySystemCode(type);

//			 将系统消息转换为对外接口的消息类型的值
			messageResult.setMessageType(systemMessageCode);

			String create_time = StringHelper.getStringFromMap(map,
					"create_time");

			Date dateTime = CalendarHelper.String2Date(create_time,
					"yyyy-MM-dd HH:mm:ss");

			// 转换为天
			long formatedTime = dateTime.getTime();

			for (Map<String, Object> map2 : messageQuantity) {
				String typeCode = StringHelper.getStringFromMap(map2, "type");

				if (typeCode.equals(type)) {
					messageResult.setMessageQuantity(StringHelper
							.getStringFromMap(map2, "messageQuantity"));
				}
			}

			// 如果没有，则设置为0
			// if(StringUtils.isEmpty(messageResult.getMessageQuantity())){
			// messageResult.setMessageQuantity("0");
			// }
			messageResult.setMessageDate(String.valueOf(formatedTime));
			messageResult.setHeadPotraitUrl("");
			// 如果含有未读消息才返回，否则不返回
			if (StringUtils.isNotEmpty(messageResult.getMessageQuantity())) {
				accountMessageList.add(messageResult);
			}
			// 设置消息免打扰状态
			messageResult.setMessageNotifactionStatus(messageNotifactionStatus);
		}

		result.setMessageList(accountMessageList);
		return result;
	}

}
