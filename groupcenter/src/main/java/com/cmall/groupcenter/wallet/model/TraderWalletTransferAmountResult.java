package com.cmall.groupcenter.wallet.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * Created with cgroup
 *
 * @author lipengfei
 * @date 2015-11-04
 * @time 16:36
 * @email:lipengfei217@163.com
 */
public class TraderWalletTransferAmountResult extends RootResultWeb {

    @ZapcomApi(value="转账流水号",remark="转账唯一标识",demo="MI15000")
    private String transferLogCode="";

    public String getTransferLogCode() {
        return transferLogCode;
    }

    public void setTransferLogCode(String transferLogCode) {
        this.transferLogCode = transferLogCode;
    }
}
