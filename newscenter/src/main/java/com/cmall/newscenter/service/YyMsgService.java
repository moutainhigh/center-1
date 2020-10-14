package com.cmall.newscenter.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.MapUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class YyMsgService {
	
	public String getImageCount(String wordId) {
		String num = "";
		int count = DbUp.upTable("uc_user_words_img").count("word_id", wordId);
		if(count > 0) {
			num = count + "";
		}
		return num;
	}
	
	public List<String> getImagePaths(String wordId) {
		List<String> items = new ArrayList<String>();
		List<MDataMap> list = DbUp.upTable("uc_user_words_img").queryByWhere("word_id", wordId);
		for(MDataMap map : list) {
			items.add(MapUtils.getString(map, "image_path", ""));
		}
		return items;
	}
	
	public int  changeSeeFlag(String wordId) {
	  int num =  DbUp.upTable("uc_user_words").dataUpdate(new MDataMap("uid",wordId,"see_flag","1"), "see_flag", "uid");
	  return num;
	}
}
