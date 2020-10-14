package com.cmall.groupcenter.tongji;


public interface ResultParse<T extends ResultData> {

	T parse(String resultText);
	
	T getObj();
}
