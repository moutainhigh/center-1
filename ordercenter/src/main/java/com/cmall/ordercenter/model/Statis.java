package com.cmall.ordercenter.model;

public class Statis {
	
	private int no_pay_num;
	private int no_send_num;
	private int send_num;
	private int success_num;
	public int getNo_pay_num() {
		return no_pay_num;
	}
	public void setNo_pay_num(int no_pay_num) {
		this.no_pay_num = no_pay_num;
	}
	public int getNo_send_num() {
		return no_send_num;
	}
	public void setNo_send_num(int no_send_num) {
		this.no_send_num = no_send_num;
	}
	public int getSend_num() {
		return send_num;
	}
	public void setSend_num(int send_num) {
		this.send_num = send_num;
	}
	public int getSuccess_num() {
		return success_num;
	}
	public void setSuccess_num(int success_num) {
		this.success_num = success_num;
	}
	public int getFailure_num() {
		return failure_num;
	}
	public void setFailure_num(int failure_num) {
		this.failure_num = failure_num;
	}
	private int failure_num;
	
}
