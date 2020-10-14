package com.cmall.groupcenter.homehas.model;

import java.math.BigDecimal;


/**
 * 客户积分、储值金、暂存款
 */
public class RsyncModelCustRelAmt {

	//积分
	private BigDecimal poss_accm_amt = new BigDecimal(0);
	//储值金
	private BigDecimal poss_ppc_amt = new BigDecimal(0);
	//暂存款
	private BigDecimal poss_crdt_amt = new BigDecimal(0);
	//惠币金额
	private BigDecimal poss_hcoin_amt = new BigDecimal(0);
	//预估惠币金额
	private BigDecimal pre_hcoin_amt = new BigDecimal(0);
	
	public BigDecimal getPoss_hcoin_amt() {
		return poss_hcoin_amt;
	}
	public void setPoss_hcoin_amt(BigDecimal poss_hcoin_amt) {
		this.poss_hcoin_amt = poss_hcoin_amt;
	}
	public BigDecimal getPre_hcoin_amt() {
		return pre_hcoin_amt;
	}
	public void setPre_hcoin_amt(BigDecimal pre_hcoin_amt) {
		this.pre_hcoin_amt = pre_hcoin_amt;
	}
	public BigDecimal getPoss_accm_amt() {
		return poss_accm_amt;
	}
	public void setPoss_accm_amt(BigDecimal poss_accm_amt) {
		this.poss_accm_amt = poss_accm_amt;
	}
	public BigDecimal getPoss_ppc_amt() {
		return poss_ppc_amt;
	}
	public void setPoss_ppc_amt(BigDecimal poss_ppc_amt) {
		this.poss_ppc_amt = poss_ppc_amt;
	}
	public BigDecimal getPoss_crdt_amt() {
		return poss_crdt_amt;
	}
	public void setPoss_crdt_amt(BigDecimal poss_crdt_amt) {
		this.poss_crdt_amt = poss_crdt_amt;
	}

	
}
