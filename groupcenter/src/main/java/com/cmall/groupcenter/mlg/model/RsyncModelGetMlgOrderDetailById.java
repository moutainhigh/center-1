package com.cmall.groupcenter.mlg.model;


/** 
* @ClassName: RsyncModelGetMlgOrderDetailById 
* @Description: 根据订单编号查询麦乐购订单物流信息返回实体
* @author 张海生
* @date 2015-12-28 下午4:27:27 
*  
*/
public class RsyncModelGetMlgOrderDetailById {
	
	/**
	 * 订单明细
	 */
	private Details Details = new Details();
	
	/**
	 * 快递公司代号
	 */
	private String DeliveryCode = "";
	
	/**
	 * 麦乐购订单号 
	 */
	private String Gou_OrderId = "";
	
	/**
	 * 快递单号
	 */
	private String DeliveryNumber = "";
	
	/**
	 * 发货时间
	 */
	private String DeliveryTime = "";
	
	/**
	 * 订单状态
	 */
	private int State;
	
	public static class Details{
		
		/**
		 * 商品货号
		 */
		private String GoodsCode = "";
		
		/**
		 * 商品名称
		 */
		private String GoodsName = "";
		
		/**
		 * 数量
		 */
		private int Count;

		public String getGoodsCode() {
			return GoodsCode;
		}

		public void setGoodsCode(String goodsCode) {
			GoodsCode = goodsCode;
		}

		public String getGoodsName() {
			return GoodsName;
		}

		public void setGoodsName(String goodsName) {
			GoodsName = goodsName;
		}

		public int getCount() {
			return Count;
		}

		public void setCount(int count) {
			Count = count;
		}
	}

	public Details getDetails() {
		return Details;
	}

	public void setDetails(Details details) {
		Details = details;
	}

	public String getDeliveryCode() {
		return DeliveryCode;
	}

	public void setDeliveryCode(String deliveryCode) {
		DeliveryCode = deliveryCode;
	}

	public String getGou_OrderId() {
		return Gou_OrderId;
	}

	public void setGou_OrderId(String gou_OrderId) {
		Gou_OrderId = gou_OrderId;
	}

	public String getDeliveryNumber() {
		return DeliveryNumber;
	}

	public void setDeliveryNumber(String deliveryNumber) {
		DeliveryNumber = deliveryNumber;
	}

	public String getDeliveryTime() {
		return DeliveryTime;
	}

	public void setDeliveryTime(String deliveryTime) {
		DeliveryTime = deliveryTime;
	}

	public int getState() {
		return State;
	}

	public void setState(int state) {
		State = state;
	}
} 
