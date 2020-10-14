package com.cmall.newscenter.webfunc;

import java.util.List;

import com.cmall.newscenter.service.ScoreFlowBussinessService;
import com.cmall.systemcenter.model.RoleStatus;
import com.cmall.systemcenter.model.ScFlowBussinesstype;
import com.cmall.systemcenter.service.ScFlowBase;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 处理分销商用户审核问题
 * @author shiyz
 * date 2014-12-20
 * @version 1.0
 **/
public class AgencyReviewsLine extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();

		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		if (mResult.upFlagTrue()){
			ScoreFlowBussinessService fs = new ScoreFlowBussinessService();
			String flowType = mDataMap.get("flow_type");
			String flowbussinessid = mDataMap.get("flowbussinessid");
			//获得sc_flow_bussinesstype表中table信息
			MDataMap flowTypeData = DbUp.upTable("sc_flow_bussinesstype").one("flow_type",
					flowType);
			
			String level_number = mDataMap.get("level_number");
			
			ScFlowBussinesstype sfbt = new ScFlowBussinesstype();
			if(flowTypeData == null)
			{//错误信息
				mResult.setResultCode(949701024);
				mResult.setResultMessage(bInfo(949701024));
				return mResult;
			}
			else
			{//将flowTypeData中的信息添加到sfbt中
				SerializeSupport ss = new SerializeSupport<ScFlowBussinesstype>();
				ss.serialize(flowTypeData, sfbt);
			}
			//获得表名
			String tablename = sfbt.getTableName();
			//查询对应评论信息
			MDataMap flowMain = DbUp.upTable(tablename).one("uid",
					flowbussinessid);
			
			String fromStatus= "" ;
			if(flowMain == null)
			{
				mResult.setResultCode(949701025);
				mResult.setResultMessage(bInfo(949701025));
				return mResult;
			} else {
				//获得当前评论状态的id
				fromStatus = flowMain.get(sfbt.getColumnName());
			}
			MDataMap flowNameDataMap = DbUp.upTable("sc_flowstatus").one("define_code",fromStatus);
			//获得当前评论状态
			String fromStatusName = flowNameDataMap.get("define_name");
			
			String buttonHtml = "";
			
			String userCode=UserFactory.INSTANCE.create().getUserCode();
			
			
			List<RoleStatus> roles = fs.getToStatusList(flowType, fromStatus,userCode);
			String buttonValue = "";
			
			for(RoleStatus us : roles)
			{//获得即将跳转到的状态id
				String toStatus = us.getToStatus();
				buttonValue=bInfo(949701014, ScFlowBase.getDefineNameByCode(toStatus));
				buttonHtml+="<input type='button' style='margin-right:5px;' class='btn  btn-primary' zapweb_attr_operate_id='115793e80b35072vvsf5931e0qa50810' zap_tostatus_attr='"+us.getToStatus()+"' value='"+buttonValue+"' level_number='"+level_number+"'>";	
			}
			if(!buttonHtml.equals(""))
				buttonHtml +="<input type='hidden' id='zw_f_from_status' name='zw_f_from_status' value='"+fromStatus+"' /><input type='hidden' id='zw_f_from_statusname' name='zw_f_from_statusname' value='"+fromStatusName+"' />";
			
			mResult.setResultMessage(buttonHtml);
		}
		return mResult;
	}

}
