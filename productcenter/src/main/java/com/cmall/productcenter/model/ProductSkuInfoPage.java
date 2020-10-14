package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/**   
*    
* 项目名称：productcenter   
* 类名称：ProductSkuInfoPage   
* 类描述：   商品sku信息包含分页参数
* 创建人：李国杰
* 创建时间：2014-10-09 下午15:22:22   
* 修改备注：   
* @version    
*    
*/
public class ProductSkuInfoPage  {
	
	private List<Map<String,Object>> pcSkuinfoList = new ArrayList<Map<String,Object>>();
	
	private int total = 0;		//总数量
	
	private int count = 0;		//返回数量
	
	private int  more = 0;		//是否还有更多



	public List<Map<String, Object>> getPcSkuinfoList() {
		return pcSkuinfoList;
	}

	public void setPcSkuinfoList(List<Map<String, Object>> pcSkuinfoList) {
		this.pcSkuinfoList = pcSkuinfoList;
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

