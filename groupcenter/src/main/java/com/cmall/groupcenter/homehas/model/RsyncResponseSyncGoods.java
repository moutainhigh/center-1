package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品信息批量查询接口的响应信息
 * @author jl
 *
 */
public class RsyncResponseSyncGoods extends RsyncResponseBase {

	private boolean is_open_win;
	private int count;
	private String medi_mclss_nm;
	
	private List<RsyncModelGoods> result = new ArrayList<RsyncModelGoods>();

	public List<RsyncModelGoods> getResult() {
		return result;
	}

	public void setResult(List<RsyncModelGoods> result) {
		this.result = result;
	}

	public boolean isIs_open_win() {
		return is_open_win;
	}

	public void setIs_open_win(boolean is_open_win) {
		this.is_open_win = is_open_win;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getMedi_mclss_nm() {
		return medi_mclss_nm;
	}

	public void setMedi_mclss_nm(String medi_mclss_nm) {
		this.medi_mclss_nm = medi_mclss_nm;
	}
}
