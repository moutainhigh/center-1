package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;



/**   
*    
* 项目名称：productcenter   
* 类名称：PcProductinfoPage   
* 类描述：   商品信息包含分页参数
* 创建人：李国杰
* 创建时间：2014-9-29 下午13:21:22   
* 修改备注：   
* @version    
*    
*/
public class PcProductinfoPage  {
	
	private List<PcProductinfo> pcProducinfoList = new ArrayList<PcProductinfo>();	//商品列表
	
	private int total = 0;		//总数量
	
	private int count = 0;		//返回数量
	
	private int  more = 0;		//是否还有更多

	public List<PcProductinfo> getPcProducinfoList() {
		return pcProducinfoList;
	}

	public void setPcProducinfoList(List<PcProductinfo> pcProducinfoList) {
		this.pcProducinfoList = pcProducinfoList;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getMore() {
		return more;
	}

	public void setMore(int more) {
		this.more = more;
	}
	
}

