package com.cmall.groupcenter.groupapp.api;

import com.cmall.groupcenter.groupapp.model.SetMessageNotifactionStatusInput;
import com.cmall.groupcenter.groupapp.model.SetMessageNotifactionStatusResult;
import com.cmall.groupcenter.wallet.service.WalletWithdrawService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;
import org.apache.commons.lang.StringUtils;

/**
 * 设置免打扰
 * 本接口用于设置系统消息免打扰
 * @author lpf
 * 
 */
public class SetMessageNotifactionStatusApi extends RootApiForToken<SetMessageNotifactionStatusResult
		, SetMessageNotifactionStatusInput>{

    public static final  String PUSH_TYPE_ID="b03ea03f3e384a4aad2dc8ab8b829284";

	public SetMessageNotifactionStatusResult Process(SetMessageNotifactionStatusInput inputParam,
			MDataMap mRequestMap) {

		SetMessageNotifactionStatusResult result=new SetMessageNotifactionStatusResult();

		String memberCode = getUserCode();

//		0:开启免打扰,1:关闭免打扰
		//总开关设置 449747100001开 449747100002 关闭
		if("0".equals(inputParam.getOperationType())){
            result.inOtherResult(setMessageNotifactionStatus(memberCode,getManageCode(),"449747100001"));
		}else if ("1".equals(inputParam.getOperationType())){
            result.inOtherResult(setMessageNotifactionStatus(memberCode,getManageCode(),"449747100002"));
		}else{
			result.inErrorMessage(918570001);
		}

		return result;
	}

	/**
	 * 总开关设置 449747100001开 449747100002 关闭
	 * @param statusValue
	 * @return
	 */
	private  SetMessageNotifactionStatusResult setMessageNotifactionStatus(String memberCode,String manageCode,String statusValue){

        SetMessageNotifactionStatusResult result= new SetMessageNotifactionStatusResult();
        WalletWithdrawService walletWithdrawService = new WalletWithdrawService();


//        MDataMap memberInfo=DbUp.upTable("mc_member_info").one("member_code", memberCode);

        String accountCode= walletWithdrawService.getAccountCode(memberCode,manageCode);


        if (StringUtils.isNotEmpty(accountCode)){
//            b03ea03f3e384a4aad2dc8ab8b829284
//            总开关设置 449747100001开 449747100002 关闭
            MDataMap mDataMap  = DbUp.upTable("gc_account_push_set").one("account_code",accountCode,"push_type_id",PUSH_TYPE_ID);

            //如果为null 则需要插入一条数据
            if (mDataMap==null){
                DbUp.upTable("gc_account_push_set").insert("account_code",accountCode,"push_type_id",PUSH_TYPE_ID,"push_type_onoff",statusValue,"push_type_usable","449746250001");
            }else {
                //如果相等，则不需要做数据更新操作,如果不相等的话，才需要将数据库的状态更新为当前的 @param statusValue 的值
                if (!statusValue.equals(mDataMap.get("push_type_onoff"))){
                    //数据库的状态和需要更改的状态的值不相等，所以需要更新
                    MDataMap mDataMap1 = new MDataMap();
                    mDataMap1.put("account_code",accountCode);
                    mDataMap1.put("push_type_id",PUSH_TYPE_ID);
                    mDataMap1.put("push_type_onoff",statusValue);
                    DbUp.upTable("gc_account_push_set").dataUpdate(mDataMap1,"push_type_onoff","account_code,push_type_id");
                }
            }
        }else {
            result.inErrorMessage(918570003);
        }
		return result;
	}


	public static void main(String[] args) {
		String s = WebHelper.upUuid();
		System.out.println(s);
	}
}
