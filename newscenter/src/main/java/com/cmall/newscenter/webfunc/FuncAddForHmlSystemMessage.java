package com.cmall.newscenter.webfunc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 添加系统消息
 * 
 * @author houwen
 * 
 */
public class FuncAddForHmlSystemMessage extends RootFunc {
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		String app_code = UserFactory.INSTANCE.create().getManageCode();
		
		// 定义插入数据库
		MDataMap mInsertMap = new MDataMap();
		// 定义组件判断标记
		boolean bFlagComponent = false;

		recheckMapField(mResult, mPage, mAddMaps);

		if (mResult.upFlagTrue()) {

			// 循环所有结构 初始化插入map
			for (MWebField mField : mPage.getPageFields()) {

				if (mField.getFieldTypeAid().equals("104005003")) {
					bFlagComponent = true;
				}

				if (mAddMaps.containsKey(mField.getFieldName())
						&& StringUtils.isNotEmpty(mField.getColumnName())) {

					String sValue = mAddMaps.get(mField.getFieldName());

					mInsertMap.put(mField.getColumnName(), sValue);

				}

			}
		}

		// 编码自动生成
		mInsertMap.put("message_code", WebHelper.upCode("XX"));
		// 创建时间为当年系统时间
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 设置日期格式

		mInsertMap.put("create_time", df.format(new Date())); // new
																// Date()为获取当前系统时间
		 mInsertMap.put("manage_code",
		 app_code);
		if (mResult.upFlagTrue()) {

			DbUp.upTable(mPage.getPageTable()).dataInsert(mInsertMap);

			/** 如果是系统消息 */
			if (mInsertMap.get("member_send").equals("")) {

				MDataMap map = new MDataMap();

				map.put("app_code", app_code);

				List<MDataMap> list = DbUp.upTable("mc_extend_info_star")
						.query("member_code", "", "app_code=:app_code", map, 0,
								0);

				if (list.size() != 0) {

					for (int i = 0; i < list.size(); i++) {

						MDataMap mapList = list.get(i);

						MDataMap messDataMap = new MDataMap();

						messDataMap.put("message_code",
								mInsertMap.get("message_code"));

						messDataMap.put("message_info",
								mInsertMap.get("message_info"));

						messDataMap.put("create_time",
								mInsertMap.get("create_time"));

						messDataMap.put("member_send",
								mapList.get("member_code"));

						messDataMap.put("manage_code",
								app_code);

						messDataMap.put("message_type",
								mInsertMap.get("message_type"));

						messDataMap.put("url", mInsertMap.get("url"));

						messDataMap.put("send_time",
								mInsertMap.get("send_time"));

						DbUp.upTable("nc_system_message").dataInsert(
								messDataMap);

					}
				}

			} else {

				MDataMap map = new MDataMap();

				map.put("app_code", app_code);

				map.put("member_send", mInsertMap.get("member_send"));

				MDataMap messDataMap = new MDataMap();

				messDataMap.put("message_code", mInsertMap.get("message_code"));

				messDataMap.put("message_info", mInsertMap.get("message_info"));

				messDataMap.put("create_time", mInsertMap.get("create_time"));

				messDataMap.put("member_send", mInsertMap.get("member_send"));

				messDataMap.put("manage_code", app_code);

				messDataMap.put("message_type", mInsertMap.get("message_type"));

				messDataMap.put("url", mInsertMap.get("url"));

				messDataMap.put("send_time", mInsertMap.get("send_time"));

				DbUp.upTable("nc_system_message").dataInsert(messDataMap);
			}

			if (bFlagComponent) {

				DbUp.upTable(mPage.getPageTable()).dataInsert(mInsertMap);

				for (MWebField mField : mPage.getPageFields()) {

					if (mField.getFieldTypeAid().equals("104005003")) {

						WebUp.upComponent(mField.getSourceCode()).inAdd(mField,
								mDataMap);
					}
				}

			}

		}

		if (mResult.upFlagTrue()) {

			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;

	}

}
