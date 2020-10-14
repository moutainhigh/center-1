package com.cmall.groupcenter.func.trader;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.txservice.TxTraderInfoCreateService;
import com.cmall.groupcenter.util.CalendarHelper;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncTraderAddMoney  extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		MDataMap mWhereMap  = new MDataMap();

		mWhereMap.put("account_code",mAddMaps.get("account_code"));
		mWhereMap.put("login_account", mAddMaps.get("login_account"));
		
		Object value = DbUp.upTable("gc_trader_info").dataGet("uid", null, mWhereMap);
		
		if(value==null){
			
			MDataMap traderInfo = new MDataMap();
			
			//返现范围表
			MDataMap traderRebate = new MDataMap();
			MDataMap traderRebateChangeLog = new MDataMap();
			MDataMap rebateTypeChangeLog = new MDataMap();

            MDataMap traderPreWithdraw = new MDataMap();
			
			TxTraderInfoCreateService createService = BeansHelper
					.upBean("bean_com_cmall_groupcenter_txservice_TxTraderInfoCreateService");

			//准备数据
			prepareData(mAddMaps, traderInfo, traderRebate,traderRebateChangeLog,rebateTypeChangeLog,traderPreWithdraw);
			
			createService.insertTraderInfoMoney(traderInfo, traderRebate, traderRebateChangeLog,rebateTypeChangeLog,traderPreWithdraw,mAddMaps.get("login_account"));
			
		}else {
			mResult.inErrorMessage(918549001);
		}
		
		
		return mResult;
	}

    /**
     * 组合金额返利的范围，变成 1-400,400-500,500-799,600 这种格式。
     * @return
     */
    public static String consistMoneyRebateRange(MDataMap mAddMaps){

//        min和max的值应该完全一样多，否则就出错了。
        String minRanges = mAddMaps.get("range_min");
        String maxRanges = mAddMaps.get("range_max");

        String[] maxRangesArray = maxRanges.split(",");
        String[] minRangesArray = minRanges.split(",");

        StringBuffer buffer = new StringBuffer();
        for (int i=0;i<maxRangesArray.length;i++){
                if (StringUtils.isNotBlank(maxRangesArray[i])){
                    buffer.append(minRangesArray[i]);
                    buffer.append("-");
                    buffer.append(maxRangesArray[i]);
                    buffer.append(",");
                }
        }

            buffer.append(mAddMaps.get("rearRange"));
            return buffer.toString();
    }

	/**
	 * 准备需要存储的数据
	 * @author lipengfei
	 * @date 2015-6-23
	 * @param mAddMaps
	 */
	private void prepareData(MDataMap mAddMaps,MDataMap traderInfo,MDataMap traderRebate,MDataMap traderRebateChangeLog,
			MDataMap rebateTypeChangeLog,MDataMap traderPreWithdraw){

        String moneyRangeString = consistMoneyRebateRange(mAddMaps);
		//商户创建人
        String createUserCode = UserFactory.INSTANCE.create().getUserCode();
		String traderCode = WebHelper.upCode("SG11");
		String createDate = CalendarHelper.Date2String(new Date(),"yyyy-MM-dd HH:mm:ss");
		
		traderInfo.put("uid", WebHelper.upUuid());
		traderInfo.put("trader_name", mAddMaps.get("trader_name"));
		traderInfo.put("trader_code",traderCode);
		traderInfo.put("account_code",mAddMaps.get("account_code"));
		traderInfo.put("login_account", mAddMaps.get("login_account"));
		traderInfo.put("trader_status","4497472500010001");//默认启用状态
		traderInfo.put("create_time",createDate);
		traderInfo.put("create_user",createUserCode);
		traderInfo.put("activate_operation",mAddMaps.get("activate_operation")==null?"":mAddMaps.get("activate_operation"));
//        设置返利方式
        traderInfo.put("rebate_type","4497472500080002");
        traderInfo.put("type_apply_time", FormatHelper.upDateTime());
        
		traderRebate.put("trader_code", traderCode);
		traderRebate.put("account_code", mAddMaps.get("account_code"));
		traderRebate.put("create_user", createUserCode);
		traderRebate.put("create_time",createDate);
		traderRebate.put("delete_flag","0");
        traderRebate.put("money_rebate_grade",moneyRangeString);
        traderRebate.put("money_rebate_range", mAddMaps.get("REBATE_RANGE"));

        String rebateRate  = mAddMaps.get("rebate_rate");
        char a = ',';
        if (rebateRate.charAt(rebateRate.length()-1)==a){
            rebateRate = rebateRate.substring(0,rebateRate.length()-1);
        }

        traderRebate.put("money_rebate_scale",rebateRate);
		traderRebate.put("return_goods_day",mAddMaps.get("return_goods_day"));//默认30天的服务退货时间。
		
		//返利比例变更日志
		traderRebateChangeLog.put("log_code", WebHelper.upCode("TRCL"));
		traderRebateChangeLog.put("trader_code", traderCode);
		traderRebateChangeLog.put("now_rebate_grade", moneyRangeString);
		traderRebateChangeLog.put("now_rebate_range", mAddMaps.get("REBATE_RANGE"));
		traderRebateChangeLog.put("now_rebate_scale", rebateRate);
		traderRebateChangeLog.put("now_rebate_mode", "4497472500080002");
		traderRebateChangeLog.put("create_user", createUserCode);
		traderRebateChangeLog.put("create_time", createDate);
		traderRebateChangeLog.put("remark", "创建商户");
		
		//返利方式变更日志
		rebateTypeChangeLog.put("trader_code", traderCode);
		rebateTypeChangeLog.put("rebate_mode", "4497472500080002");
		rebateTypeChangeLog.put("create_time", createDate);
		rebateTypeChangeLog.put("remark", "创建商户");

//        预存款管理提醒的默认设置
        traderPreWithdraw.put("trader_code",traderCode);
        traderPreWithdraw.put("first_notify","10");
        traderPreWithdraw.put("second_notify","5");
        traderPreWithdraw.put("stop_rebate_notify","2");
        traderPreWithdraw.put("create_time",createDate);

	}

}
