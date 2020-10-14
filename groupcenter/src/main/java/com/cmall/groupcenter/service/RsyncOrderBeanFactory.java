package com.cmall.groupcenter.service;

import com.cmall.ordercenter.service.OrderService;

/**
 * 订单同步bean工厂类
 * @author pangjh
 *
 */
public class RsyncOrderBeanFactory {
	
	private static RsyncOrderBeanFactory INSTANCE = null;
	
	/**
	 * 获取实例对象
	 * @return
	 */
	public static RsyncOrderBeanFactory getInstance(){
		
		if(INSTANCE == null){
			
			INSTANCE = new RsyncOrderBeanFactory();
			
		}
		
		return INSTANCE;
		
	}
	
	/*service*/
	/*与微公社订单同步返利业务实现*/
	private RsyncSellerOrderService rsyncSellerOrderService = new RsyncSellerOrderService();
	/*订单信息业务实现*/
	private OrderService orderService = new OrderService();
	/*同步日志记录业务实现*/
	private LcRsyncOrderCGroupService lcRsyncOrderCGroupService = new LcRsyncOrderCGroupService();
	
	/*dao*/
	/*与微公社订单同步接口*/
	private RsyncSellerOrderDao rsyncSellerOrderDao = new RsyncSellerOrderDao();

	/**
	 * 获取与微公社订单同步dao
	 * @return rsyncSellerOrderDao
	 */
	public RsyncSellerOrderDao getRsyncSellerOrderDao() {
		return rsyncSellerOrderDao;
	}

	/**
	 * 获取与微公社订单同步返利业务实现
	 * @return rsyncSellerOrderService
	 */
	public RsyncSellerOrderService getRsyncSellerOrderService() {
		return rsyncSellerOrderService;
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
