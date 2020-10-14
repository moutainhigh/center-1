package com.cmall.groupcenter.hserver;

/***
 * 
 * @author jlin
 *
 * @param <TRequest>
 */
public interface IAcceptFunc <TRequest extends IHServerRequest> {

	/**
	 * 逻辑处理
	 * @param request
	 * @return
	 */
	public AcceptResult doProcess(TRequest request);

}
