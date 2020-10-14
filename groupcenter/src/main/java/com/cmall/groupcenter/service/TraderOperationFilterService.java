package com.cmall.groupcenter.service;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;
import org.apache.commons.lang.StringUtils;

import javax.jws.WebResult;


/**
 *
 * 拦截用户的行为
 *
 * 如果用户尚未开通支付和钱包的功能，则不能调用相关接口
 *
 * @author lipengfei
 * @date 2015-11-17
 * @time 14:09
 * @email:lipengfei217@163.com
 */
public class TraderOperationFilterService {

    public static final String OPERATION_TYPE_PAY="1";
    public static final String OPERATION_TYPE_WALLET="0";

    /**
     * 通过商户字段下的开通功能，来判断用户是否开通了该功能
     * @param traderCode
     * @param checkType
     * @return
     */
    private MWebResult checkOperationAuthBytraderCode(String traderCode,String checkType){
        MWebResult result = new MWebResult();
        MDataMap dataMap = DbUp.upTable("gc_trader_info").one("trader_code",traderCode);
        if (dataMap!=null){
            String operationActivate = dataMap.get("activate_operation");
            if (StringUtils.isNotEmpty(operationActivate)){
                //不包含该类型，则返回错误信息
                if (operationActivate.indexOf(checkType)<0){
                    result.inOtherResult(errorMessageHelp(checkType));
                }
            }else{
                result.inOtherResult(errorMessageHelp(checkType));
            }

        }else {
            //该商户不存在
            result.inErrorMessage(918549002);
//            result.setResultCode(0);
        }

        return result;
    }


    private MWebResult errorMessageHelp(String checkType){
        MWebResult result = new MWebResult();

        //否则没有开通任何功能
        if (OPERATION_TYPE_PAY.equals(checkType)){
            //提示未开通支付功能
            result.inErrorMessage(918549003);
//            result.setResultCode(0);
        }else if (OPERATION_TYPE_WALLET.equals(checkType)){
//                    提示未开通钱包功能
            result.inErrorMessage(918549004);
//            result.setResultCode(0);
        }

        return result;
    }


    public MWebResult checkOperationAuthByManageCode(String manageCode,String checkType){
        String traderCode=getTraderCode(manageCode);
        if (traderCode!=null){
            return checkOperationAuthBytraderCode(traderCode,checkType);
        }else{
            MWebResult result = new MWebResult();
            result.inErrorMessage(918549002);
//            result.setResultCode(0);
            return  result;
        }
    }


    /**
     *判断用户是否开通支付功能的权限
     * @param traderCode
     * @return
     */
    public MWebResult checkOperationPayBytraderCode(String traderCode){
        return checkOperationAuthBytraderCode(traderCode,OPERATION_TYPE_PAY);
    }


    /**
     *判断用户是否开通支付功能的权限
     * @return
     */
    public MWebResult checkOperationPayByManageCode(String manageCode){
      return checkOperationAuthByManageCode(manageCode, OPERATION_TYPE_PAY);
    }

    /**
     *判断用户是否开通支付功能的权限
     * @return
     */
    public MWebResult checkOperationWalletByManageCode(String manageCode){
      return checkOperationAuthByManageCode(manageCode,OPERATION_TYPE_WALLET);
    }


    //获取商户编号
    public String getTraderCode(String manageCode){
        MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",manageCode);
        if(appMap!=null){
            return appMap.get("trade_code");
        }else{
            return null;
        }

    }

    /**
     * 判断用户是否开通钱包功能
     * @param traderCode
     * @return
     */
    public MWebResult checkOperationWalletBytraderCode(String traderCode){
        return checkOperationAuthBytraderCode(traderCode,OPERATION_TYPE_WALLET);
    }

}
