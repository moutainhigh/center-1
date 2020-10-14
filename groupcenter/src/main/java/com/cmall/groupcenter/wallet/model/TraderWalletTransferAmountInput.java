package com.cmall.groupcenter.wallet.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 *
 *商户钱包转账接口输入参数
 * @author lipengfei
 * @date 2015-11-04
 * @time 16:12
 * @email:lipengfei217@163.com
 */
public class TraderWalletTransferAmountInput extends RootInput{

    @ZapcomApi(value="用户编号",remark="对接系统中用户的唯一编号",require=1,demo="MI15000")
    private String memberCode = "";

    @ZapcomApi(value="转账编号",remark="唯一编号，每个编号只处理一次",require=1,demo="")
    private String transferLogCode="";

    @ZapcomApi(value="转账金额",remark="转账金额",require=1,demo="1.20")
    private String transferAmount="";

    @ZapcomApi(value="备注",remark="备注",demo="摘要")
    private String hisNotes="";


    public String getTransferLogCode() {
        return transferLogCode;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public String getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(String transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getHisNotes() {
        return hisNotes;
    }

    public void setHisNotes(String hisNotes) {
        this.hisNotes = hisNotes;
    }

}
