package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 按商品编号查看颜色款式接口的响应信息
 * @author jl
 *
 */
public class RsyncResponseSyncGoodbyColor extends RsyncResponseBase {

	private boolean is_open_win;
	private int count;
	private String medi_mclss_nm;
	
	private List<RsyncModelGoodbyColor> result = new ArrayList<RsyncModelGoodbyColor>();

	public List<RsyncModelGoodbyColor> getResult() {
		return result;
	}

	public void setResult(List<RsyncModelGoodbyColor> result) {
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
