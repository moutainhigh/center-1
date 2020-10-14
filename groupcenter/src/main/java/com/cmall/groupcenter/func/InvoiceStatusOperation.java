package com.cmall.groupcenter.func;

import java.util.List;

import com.cmall.systemcenter.model.RoleStatus;
import com.cmall.systemcenter.model.ScFlowBussinesstype;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.cmall.systemcenter.service.ScFlowBase;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 家有汇开/退发票功能
 * @author dyc
 * date 2015-6-11
 * @version 1.0
 **/
public class InvoiceStatusOperation extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();

		if (mResult.upFlagTrue()) {
			
			FlowBussinessService fs = new FlowBussinessService();
			String flowType = mDataMap.get("flow_type");
			//filedName和fieldValue是所要更新数据的表中where后面的列名和值
			String filedName = mDataMap.get("fieldname");
			String fieldValue = mDataMap.get("fieldvalue");
			
			
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
			
			
			MDataMap flowMain = DbUp.upTable(sfbt.getTableName()).one(filedName,
					fieldValue);
			
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
				buttonHtml+="<input type='button' style='margin-right:5px;' class='btn  btn-primary' zapweb_attr_operate_id='jsu982349rwerf23894r0werijeoifus' zap_tostatus_attr='"+us.getToStatus()+"' value='"+buttonValue+"'>";	
			}
			if(!buttonHtml.equals(""))
				buttonHtml +="<input type='hidden' id='zw_f_from_status' name='zw_f_from_status' value='"+fromStatus+"' /><input type='hidden' id='zw_f_from_statusname' name='zw_f_from_statusname' value='"+fromStatusName+"' />";
			
			mResult.setResultMessage(buttonHtml);
		}
		
		return mResult;
	}

}
