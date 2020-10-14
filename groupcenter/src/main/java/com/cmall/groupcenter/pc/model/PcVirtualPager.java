package com.cmall.groupcenter.pc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 虚拟分页
 * @author GaoYang
 *
 * @param <T>
 */
public class PcVirtualPager<T> {
	
	/**
	 * 当前页索引,默认是1.
	 */
	private int pageIndex = 1;
	/**
	 * 每页的记录容量,默认是10.
	 */
	private int pageSize = 10;
	/**
	 * 总页数
	 */
	private int pageCount;
	/**
	 * 分页数据
	 */
	private List<T> data;

	/**
	 * 总记录数，默认是0
	 */
	private int totalResults = 0;

	/**
	 * 构造一个分页器。
	 * 
	 * @param totalResults
	 *            总条数
	 * @param pageIndex
	 *            起始索引
	 */
	public PcVirtualPager(int totalResults, int pageIndex) {
		setTotalResults(totalResults);
		setPageIndex(pageIndex);
	}

	/**
	 * 构造一个分页器。
	 * 
	 * @param totalResults
	 *            总条数
	 * @param pageIndex
	 *            起始索引
	 */
	public PcVirtualPager(int totalResults, int pageIndex, int pageSize) {
		this.pageSize = pageSize;
		setTotalResults(totalResults);
		setPageIndex(pageIndex);
	}
	
	/**
	 * 构造一个分页器。
	 * 
	 * @param totalResults
	 *            总条数
	 * @param pageIndex
	 *            起始索引
	 * @param data
	 *           数据
	 */
	public PcVirtualPager(int totalResults, int pageIndex, int pageSize,List<T> data) {
		this.pageSize = pageSize;
		setTotalResults(totalResults);
		setPageIndex(pageIndex);
		setData(data);
	}

	/**
	 * 设置总记录数
	 */
	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
		if (totalResults % pageSize == 0) {
			pageCount = totalResults / pageSize;
		} else {
			pageCount = totalResults / pageSize + 1;
		}
	}

	/**
	 * 设置分页器当前页。
	 * 
	 * @param pageIndex
	 *            分页器当前索引页。
	 */
	public void setPageIndex(int pageIndex) {
		if (pageIndex > pageCount) {
			this.pageIndex = pageCount;
		} else {
			this.pageIndex = pageIndex;
		}
	}

	/**
	 * 返回跳过的页数
	 * 
	 * @return
	 */
	public int getSkipResults() {
		return (pageIndex - 1) * pageSize;
	}

	/**
	 * 最大记录数
	 * 
	 * @return 返回每页的记录容量
	 */
	public int getMaxResults() {
		return pageSize;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public int getTotalResults() {
		return totalResults;
	}
	
	//得到当前指定页的数据
	public List<T> getCurrentPageData(){
		List<T> result = new ArrayList<T>();
		int endIndex = this.getSkipResults() + this.pageSize;
		if (this.getData().size() < this.getSkipResults() + this.pageSize) {
			endIndex = this.getData().size();
		}
		
//		System.out.println("getSkipResults==="+this.getSkipResults()+"===pageSize===="+this.pageSize+"==this.getData().size()=="+this.getData().size());
		if (this.getSkipResults() >= 0 && this.getData().size() > 0) {
			for (int i = this.getSkipResults(); i < endIndex; i++) {
				result.add(this.getData().get(i));
			}
		}
		return result;
	}
}
