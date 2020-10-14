package com.cmall.groupcenter.service;

import com.cmall.ordercenter.service.OrderService;

/**
 * 订单同步bean工厂类
 * @author zmm
 *
 */
public class RsyncReturnOrderBeanFactory {
	
	private static RsyncReturnOrderBeanFactory INSTANCE = null;
	
	/**
	 * 获取实例对象
	 * @return
	 */
	public static RsyncReturnOrderBeanFactory getInstance(){
		
		if(INSTANCE == null){
			
			INSTANCE = new RsyncReturnOrderBeanFactory();
			
		}
		
		return INSTANCE;
		
	}
	
	/*service*/
	/*与微公社订单同步返利业务实现*/
	private RsyncReturnOrderService rsyncReturnOrderService = new RsyncReturnOrderService();
	/*订单信息业务实现*/
	private OrderService orderService = new OrderService();
	/*同步日志记录业务实现*/
	private LcRsyncOrderCGroupService lcRsyncOrderCGroupService = new LcRsyncOrderCGroupService();
	
	/*dao*/
	/*与微公社订单同步接口*/
	private RsyncReturnOrderDao rsyncreturnOrderDao = new RsyncReturnOrderDao();

	/**
	 * 获取与微公社订单同步dao
	 * @return rsyncreturnOrderDao
	 */
	public RsyncReturnOrderDao getRsyncReturnOrderDao() {
		return rsyncreturnOrderDao;
	}

	/**
	 * 获取与微公社订单同步返利业务实现
	 * @return rsyncReturnOrderService
	 */
	public RsyncReturnOrderService getRsyncReturnOrderService() {
		return rsyncReturnOrderService;
	}

	/**
	 * 获取订单业务实现
	 * @return orderService
	 */
	public OrderService getOrderService() {
		return orderService;
	}

	/**
	 * 
	 * @return lcRsyncOrderCGroupService
	 */
	public LcRsyncOrderCGroupService getLcRsyncOrderCGroupService() {
		return lcRsyncOrderCGroupService;
	}
	
	

}
