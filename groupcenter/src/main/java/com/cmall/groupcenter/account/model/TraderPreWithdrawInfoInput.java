package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 *
 *
 * @author lipengfei
 * @date 2015-10-09
 * @time 17:25
 * @email:lipengfei217@163.com
 */
public class TraderPreWithdrawInfoInput extends RootInput {

    @ZapcomApi(value="商户编号",remark="商户编号",require=1)
    private String traderCode;

    public String getTraderCode() {
        return traderCode;
    }

    public void setTraderCode(String traderCode) {
        this.traderCode = traderCode;
    }
}
