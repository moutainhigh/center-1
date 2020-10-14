package com.cmall.systemcenter.score;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.cmall.systemcenter.systemface.IFlowFunc;
import com.cmall.systemcenter.util.SystemCenterConst;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webfactory.UserFactory;

public class PrepaymentsFlowFunc implements IFlowFunc
{

	public RootResult BeforeFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus, MDataMap mSubMap)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public RootResult afterFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus, MDataMap mSubMap)
	{
		// 添加日志
		MUserInfo mUserInfo = UserFactory.INSTANCE.create();
		String uid = UUID.randomUUID().toString().replace("-", "");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
		MDataMap mp = new MDataMap();
		Date date = new Date();
		long time = date.getTime();
		String dateline = time + "";
		dateline = dateline.substring(0, 10);
		
		if (fromStatus.equals(SystemCenterConst.PREPARE_TO_ACCOUNT)
				&& toStatus.equals(SystemCenterConst.PREPARE_ADD_SCORE))
		{
			mp.put("create_time", df.format(new java.util.Date()));
			mp.put("create_user", UserFactory.INSTANCE.create().getLoginName());
			mp.put("info", "财务预付款审批");
			DbUp.upTable("lc_web_log").dataInsert(mp);
			MDataMap dataMap = new MDataMap();
			dataMap.put("uid", mSubMap.get("flow_bussinessid"));
			dataMap.put("amount", mSubMap.get("poundage"));
			DbUp.upTable("advance").dataUpdate(dataMap, "amount", "uid");
		}

		else if (fromStatus.equals(SystemCenterConst.PREPARE_ADD_SCORE)
				&& toStatus.equals(SystemCenterConst.FINISH_ADD_SCORE))
		{
			mp.put("create_time", df.format(new java.util.Date()));
			mp.put("create_user",  UserFactory.INSTANCE.create().getLoginName());
			mp.put("info", "积分充值");
			DbUp.upTable("lc_web_log").dataInsert(mp);
			MDataMap dataMap = new MDataMap();
			dataMap.put("object", mSubMap.get("agentcode"));
			int count = DbUp.upTable("jifen_info").dataCount("object=:object", dataMap);
			MDataMap logdata = new MDataMap();
			//查询积分信息
			MDataMap mp1 = DbUp.upTable("jifen_info").one("object",  mSubMap.get("agentcode"));
			if(count > 0)
			{
				MDataMap data = new MDataMap();
				float val = Float.parseFloat(mp1.get("value"));	
				data.put("object", mSubMap.get("agentcode"));
				data.put("value", String.valueOf((val+Float.parseFloat(mSubMap.get("poundage")))));
				DbUp.upTable("jifen_info").dataUpdate(data, "value", "object");
				//获取代理商信息
				insertScoreLog(mSubMap, mUserInfo, uid, df, logdata, val);
			}
			else
			{
				MDataMap data = new MDataMap();  //0是系统
				data.put("uid",  uid);
				data.put("object", mSubMap.get("agentcode"));
				data.put("value", mSubMap.get("poundage"));
				data.put("type", "1");
				data.put("optime", dateline);
				DbUp.upTable("jifen_info").dataInsert(data);
				float val = 0;
				if(null != mp1)
				{
					 val = Float.parseFloat(mp1.get("vlaue"));	
				}
				insertScoreLog(mSubMap, mUserInfo, uid, df1, logdata, val);
			}
			
		}

		return null;
	}
/**
 * 插入积分日志
 * @param mSubMap
 * @param mUserInfo
 * @param uid
 * @param df
 * @param logdata
 * @param val
 */
	private void insertScoreLog(MDataMap mSubMap, MUserInfo mUserInfo,
			String uid, SimpleDateFormat df1, MDataMap logdata, float val)
	{
		MDataMap agentInfo = DbUp.upTable("agent_information").one("agent_code", mSubMap.get("agentcode"));
		logdata.put("uid", uid);
		logdata.put("to_id", mSubMap.get("agentcode"));
		logdata.put("to_name", agentInfo.get("name"));
		logdata.put("from_id", mUserInfo.getManageCode());
		logdata.put("from_name", mUserInfo.getLoginName());
		//变化的积分数量
		logdata.put("value", mSubMap.get("poundage"));
		//你妹的传说中的积分余额，爷我的确看不懂这字段的命名
		logdata.put("to_balance", String.valueOf(val+Float.parseFloat(mSubMap.get("poundage"))));
		logdata.put("action", "0");
		synchronized (this)
		{
			logdata.put("trade_code", new SimpleDateFormat("yyyyMMddHHmmssSS").format(new java.util.Date())  +String.valueOf((int)(Math.random()*900)+100));
		}
		logdata.put("from_type", "0");
		logdata.put("to_type", "1");
		logdata.put("from_type", "1");
		logdata.put("payment", "2");
		logdata.put("status", "0"); //
		logdata.put("status_name", "已发放");
		logdata.put("op_time",  (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new java.util.Date()));
		logdata.put("from_balance", "0");
		DbUp.upTable("jifen_log").dataInsert(logdata);
	}
	

}
