package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseclass.BaseClass;

/**   
*    
* 项目名称：productcenter   
* 类名称：StockChangeLog   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-3 下午1:14:51   
* 修改人：yanzj
* 修改时间：2013-9-3 下午1:14:51   
* 修改备注：   
* @version    
*    
*/
public class StockChangeLog extends BaseClass {
	
	 /* `zid` int(11) NOT NULL AUTO_INCREMENT,
	  `uid` char(32) DEFAULT '',
	  `code` varchar(45) DEFAULT '' COMMENT '编码',
	  `info` varchar(8000) DEFAULT '',
	  `create_time` char(19) DEFAULT '',
	  `create_user` varchar(45) DEFAULT '',
	  `change_stock` int(11) DEFAULT '0' COMMENT '变动数量',
	  `change_type` varchar(45) DEFAULT '' COMMENT '变动类型',*/
	
	
	/**
	 * 编码
	 */
	private String code="";
	/**
	 * 外部编码
	 */
	private String info="";
	/**
	 * 创建时间
	 */
	private String createTime="";
	/**
	 * 创建人
	 */
	private String createUser ="";
	/**
	 * 变动数量,以加减计算
	 */
	private int chagneStock= 0;
	/**
	 * 库存变动类型
	 */
	private String changeType="";
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public int getChagneStock() {
		return chagneStock;
	}
	public void setChagneStock(int chagneStock) {
		this.chagneStock = chagneStock;
	}
	public String getChangeType() {
		return changeType;
	}
	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}
}
