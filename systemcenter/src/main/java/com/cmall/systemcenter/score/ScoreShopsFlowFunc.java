package com.cmall.systemcenter.score;

import java.text.SimpleDateFormat;

import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;

public class ScoreShopsFlowFunc implements IFlowFunc
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
		// TODO Auto-generated method stub
		//添加日志
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		MDataMap mp  = new MDataMap();
		mp.put("create_time", df.format(new java.util.Date()));
		mp.put("create_user",  UserFactory.INSTANCE.create().getLoginName());
		mp.put("info", "客服审批");
		DbUp.upTable("lc_web_log").dataInsert(mp);
		
		return null;
	}

}
