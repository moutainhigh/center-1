package com.cmall.groupcenter.kjt.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RsyncModelGetKlProductList implements Serializable {

	private List<RsyncModelGetKlProduct> list=new ArrayList<>();

	public List<RsyncModelGetKlProduct> getList() {
		return list;
	}

	public void setList(List<RsyncModelGetKlProduct> list) {
		this.list = list;
	}
	
	
}
