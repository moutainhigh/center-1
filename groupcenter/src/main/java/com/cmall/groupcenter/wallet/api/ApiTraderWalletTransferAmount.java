package com.cmall.groupcenter.wallet.api;

import com.cmall.groupcenter.account.model.AccountInfoResult;
import com.cmall.groupcenter.account.model.AccountRecordResult;
import com.cmall.groupcenter.service.TraderOperationFilterService;
import com.cmall.groupcenter.txservice.TxReckonOrderService;
import com.cmall.groupcenter.txservice.TxTraderWalletService;
import com.cmall.groupcenter.wallet.model.TraderWalletTransferAmountInput;
import com.cmall.groupcenter.wallet.model.TraderWalletTransferAmountResult;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webmodel.MWebResult;

import java.util.Map;

/**
 *
 * 商户钱包转账接口
 *
 * @author lipengfei
 * @date 2015-11-04
 * @time 16:07
 * @email:lipengfei217@163.com
 */
public class ApiTraderWalletTransferAmount extends RootApiForManage<TraderWalletTransferAmountResult, TraderWalletTransferAmountInput> {


    public TraderWalletTransferAmountResult Process(TraderWalletTransferAmountInput traderWalletTransferAmountInput, MDataMap mDataMap) {

        TraderWalletTransferAmountResult result = new TraderWalletTransferAmountResult();
        String manageCode = getManageCode();

        String memberCode = traderWalletTransferAmountInput.getMemberCode();

        MDataMap whereMap = new MDataMap();
        whereMap.put("manageCode", manageCode);


        //判断用户是否开通了支付功能
        TraderOperationFilterService traderOperationFilterService = new TraderOperationFilterService();

        MWebResult webResult = traderOperationFilterService.checkOperationWalletByManageCode(getManageCode());

        result.inOtherResult(webResult);


        if (result.upFlagTrue()){
            TxTraderWalletService txTraderWalletService = BeansHelper
                    .upBean("bean_com_cmall_groupcenter_txservice_TxTraderWalletService");

            whereMap.put("relation_code",traderWalletTransferAmountInput.getTransferLogCode());
            Map map = DbUp.upTable("gc_trader_wallet_change_log").dataSqlOne(" SELECT * FROM gc_trader_wallet_change_log WHERE relation_code=:relation_code ", whereMap);

            if (map!=null){
                result.inErrorMessage(918560007);
            }else{
                result =txTraderWalletService.doTransferAmount(traderWalletTransferAmountInput,manageCode);
            }
        }
        return result;
    }




}
