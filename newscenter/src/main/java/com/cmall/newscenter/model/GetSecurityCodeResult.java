package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 防伪码输出类
 * @author shiyz
 * date 2014-09-20
 *
 */
public class GetSecurityCodeResult extends RootResultWeb {

	@ZapcomApi(value="所属APP")
	private String securityAppCode="";
	
	@ZapcomApi(value="商品名称")
	private String secrityProduct="";
	
	@ZapcomApi(value="检验结果")
	private int testResult=0; 
	
	@ZapcomApi(value="查询次数")
	private int queries=0; 
	
	@ZapcomApi(value="数量")
	private int num = 0;
	@ZapcomApi(value="商品渠道")
	private SecurityChannel channel = new SecurityChannel();

	public String getSecurityAppCode() {
		return securityAppCode;
	}

	public void setSecurityAppCode(String securityAppCode) {
		this.securityAppCode = securityAppCode;
	}

	public String getSecrityProduct() {
		return secrityProduct;
	}

	public void setSecrityProduct(String secrityProduct) {
		this.secrityProduct = secrityProduct;
	}

	public int getTestResult() {
		return testResult;
	}

	public void setTestResult(int testResult) {
		this.testResult = testResult;
	}

	public int getQueries() {
		return queries;
	}

	public void setQueries(int queries) {
		this.queries = queries;
	}

	public SecurityChannel getChannel() {
		return channel;
	}

	public void setChannel(SecurityChannel channel) {
		this.channel = channel;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
}
