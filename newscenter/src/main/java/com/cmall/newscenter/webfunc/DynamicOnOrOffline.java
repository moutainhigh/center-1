package com.cmall.newscenter.webfunc;

import java.util.List;

import com.cmall.systemcenter.model.RoleStatus;
import com.cmall.systemcenter.model.ScFlowBussinesstype;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.cmall.systemcenter.service.ScFlowBase;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 处理app相关信息上下线问题
 * @author shiyz
 * date 2014-7-21
 * @version 1.0
 **/
public class DynamicOnOrOffline extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		
		MWebResult mResult = new MWebResult();

		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		if (mResult.upFlagTrue()) {
			
			FlowBussinessService fs = new FlowBussinessService();
			String flowType = mDataMap.get("flow_type");
			String flowbussinessid = mDataMap.get("flowbussinessid");
			
			
			MDataMap flowTypeData = DbUp.upTable("sc_flow_bussinesstype").one("flow_type",
					flowType);
			
			ScFlowBussinesstype sfbt = new ScFlowBussinesstype();
			if(flowTypeData == null)
			{
				mResult.setResultCode(949701024);
				mResult.setResultMessage(bInfo(949701024));
				return mResult;
			}
			else
			{
				SerializeSupport ss = new SerializeSupport<ScFlowBussinesstype>();
				ss.serialize(flowTypeData, sfbt);
			}
			
			
			MDataMap flowMain = DbUp.upTable(sfbt.getTableName()).one("uid",
					flowbussinessid);
			
			String fromStatus= "" ;
			if(flowMain == null)
			{
				mResult.setResultCode(949701025);
				mResult.setResultMessage(bInfo(949701025));
				return mResult;
			} else {
				fromStatus = flowMain.get(sfbt.getColumnName());
			}
			MDataMap flowNameDataMap = DbUp.upTable("sc_flowstatus").one("define_code",fromStatus);
			
			String fromStatusName = flowNameDataMap.get("define_name");
			
			String buttonHtml = "";
			
			String userCode=UserFactory.INSTANCE.create().getUserCode();
			
			
			List<RoleStatus> roles = fs.getToStatusList(flowType, fromStatus,userCode);
			String buttonValue = "";
			
			for(RoleStatus us : roles)
			{
				String toStatus = us.getToStatus();
				buttonValue=bInfo(949701014, ScFlowBase.getDefineNameByCode(toStatus));
				buttonHtml+="<input type='button' style='margin-right:5px;' class='btn  btn-primary' zapweb_attr_operate_id='115793e80b38485aaba8223e0ea101b9' zap_tostatus_attr='"+us.getToStatus()+"' value='"+buttonValue+"'>";	
			}
			if(!buttonHtml.equals(""))
				buttonHtml +="<input type='hidden' id='zw_f_from_status' name='zw_f_from_status' value='"+fromStatus+"' /><input type='hidden' id='zw_f_from_statusname' name='zw_f_from_statusname' value='"+fromStatusName+"' />";
			
			mResult.setResultMessage(buttonHtml);
		}
		
		return mResult;
		
	}
}
