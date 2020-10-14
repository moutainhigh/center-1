package com.cmall.newscenter.service;

import java.util.Map;

import com.cmall.newscenter.model.OrderEvaluation;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class EvaluationService {

	/**
	 * 保存评论
	 * @param orderEvaluation
	 */
	public void saveEvaluation(OrderEvaluation orderEvaluation){
		
		DbUp.upTable("nc_order_evaluation").dataInsert(
				new MDataMap("order_code", orderEvaluation.getOrderCode(),
						"order_assessment", orderEvaluation.getOrderAssessment(),
						"oder_photos",orderEvaluation.getOderPhotos(),
						"oder_creattime",DateUtil.getSysDateTimeString(),
						"order_name",orderEvaluation.getOrderName(),
						"order_smallphotos",orderEvaluation.getOrderSmallphotos(),
						"manage_code",orderEvaluation.getManageCode(),
						"order_skuid",orderEvaluation.getOrderSkuid()
						));
	}
	
	/**
	 * 根据评价uid查询评价图片
	 * @param evaUid
	 * @return
	 */
	public String getEvaPhotos(String evaUid) {
		String photos = "";
		if(evaUid != null && !"".equals(evaUid)) {
			Map<String, Object> photoMap = DbUp.upTable("nc_order_evaluation").dataSqlOne("SELECT oder_photos FROM nc_order_evaluation WHERE uid = '"+evaUid+"'", new MDataMap());
			if(photoMap != null) {
				String oder_photos = (String) photoMap.get("oder_photos");
				if(!"".equals(oder_photos)) {
					photos += oder_photos;
				}
			}
		}
		
		return photos;
	}
	
	/**
	 * 根据评价uid查询评价视频截图
	 * @param evaUid
	 * @return
	 */
	public String getEvaCcImgs(String evaUid) {
		String ccImgs = "";
		if(evaUid != null && !"".equals(evaUid)) {
			Map<String, Object> photoMap = DbUp.upTable("nc_order_evaluation").dataSqlOne("SELECT ccvids, ccpics FROM nc_order_evaluation WHERE uid = '"+evaUid+"'", new MDataMap());
			if(photoMap != null) {
				String ccvids = (String) photoMap.get("ccvids");
				String ccpics = (String) photoMap.get("ccpics");
				if(!"".equals(ccvids) && !"".equals(ccpics)) {
					ccImgs += ccpics;
				}
			}
		}
		
		return ccImgs;
	}
	
	/**
	 * 根据商品编号获取封面图
	 * @param product_code
	 * @return
	 */
	public String getCoverImg(String product_code) {
		String coverImg = "";
		if(product_code != null && !"".equals(product_code)) {
			Map<String, Object> dataSqlOne = DbUp.upTable("fh_apphome_evaluation").dataSqlOne("SELECT cover_img FROM fh_apphome_evaluation WHERE product_code = '"+product_code+"'", new MDataMap());
			if(dataSqlOne != null) {
				coverImg = (String) dataSqlOne.get("cover_img");
			}
		}
		return coverImg;
	}
	
}
