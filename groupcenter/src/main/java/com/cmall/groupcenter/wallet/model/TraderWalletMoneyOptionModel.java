package com.cmall.groupcenter.wallet.model;

/**
 * Created with cgroup
 * 商户钱包存钱和取钱时的相关model
 * @author lipengfei
 * @date 2015-11-06
 * @time 14:49
 * @email:lipengfei217@163.com
 */
public class TraderWalletMoneyOptionModel {



//    商户编码
    private String traderCode="";
    
    //用户编号
    private String memberCode="";

//    账户编号
    private String accountCode="";

//    金额发生额
    private String moneyOccured="";

//    日志流水类型
    private String changeLogType="";

//    相关编码
    private String relationCode="";

//  摘要
    private String logHisNotes="";

//    备注
    private String remark="";


    public String getTraderCode() {
        return traderCode;
    }

    public void setTraderCode(String traderCode) {
        this.traderCode = traderCode;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getMoneyOccured() {
        return moneyOccured;
    }

    public void setMoneyOccured(String moneyOccured) {
        this.moneyOccured = moneyOccured;
    }

    public String getChangeLogType() {
        return changeLogType;
    }

    public void setChangeLogType(String changeLogType) {
        this.changeLogType = changeLogType;
    }

    public String getRelationCode() {
        return relationCode;
    }

    public void setRelationCode(String relationCode) {
        this.relationCode = relationCode;
    }

    public String getLogHisNotes() {
        return logHisNotes;
    }

    public void setLogHisNotes(String logHisNotes) {
        this.logHisNotes = logHisNotes;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
}
