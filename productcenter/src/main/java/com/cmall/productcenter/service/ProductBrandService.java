package com.cmall.productcenter.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmodel.PcQualificationInfo;
import com.cmall.productcenter.model.PcSellerQualification;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 商品品牌
 * 
 * @author ligj
 *
 */
public class ProductBrandService extends BaseClass {

	/**
	 * 环取出最早过期的资质信息
	 * 
	 * @param pcQualificationList
	 * @return
	 */
	public PcQualificationInfo getMinEndTime(List<PcQualificationInfo> pcQualificationList) {
		PcQualificationInfo pcQualification = new PcQualificationInfo();
		String tempEndTime = "";
		if (null != pcQualificationList && !pcQualificationList.isEmpty()) {
			for (PcQualificationInfo pcQualificationInfo : pcQualificationList) {
				if ("".equals(tempEndTime)) {
					tempEndTime = pcQualificationInfo.getEndTime();
					pcQualification = pcQualificationInfo;
				} else if (tempEndTime.compareTo(pcQualificationInfo.getEndTime()) > 0) {
					tempEndTime = pcQualificationInfo.getEndTime();
					pcQualification = pcQualificationInfo;
				}
			}
		}
		return pcQualification;
	}

	/**
	 * 插入资质信息,草稿箱(要判断草稿箱是否已经存在)
	 * 
	 * @param sellerQualificationDraft
	 * @param 草稿:4497478200010001,审批被驳回:4497478200010002,新增待审核:4497478200010003,
	 *            修改待审核:4497478200010004,审核通过:4497478200010005
	 * @return
	 */
	public String insertPualificationDraft(PcSellerQualification sellerQualification, String flowStatus,
			String remark) {

		if (null == sellerQualification || "".equals(sellerQualification.getSellerQualificationCode())) {
			return "";
		}
		JsonHelper<PcSellerQualification> pHelper = new JsonHelper<PcSellerQualification>();
		MDataMap mInsertMap = new MDataMap();
		mInsertMap.put("seller_qualification_code", sellerQualification.getSellerQualificationCode());
		mInsertMap.put("small_seller_code", sellerQualification.getSmallSellerCode());
		mInsertMap.put("brand_code", sellerQualification.getBrandCode());
		mInsertMap.put("category_code", sellerQualification.getCategoryCode());

		mInsertMap.put("qualification_json", pHelper.ObjToString(sellerQualification));
		mInsertMap.put("flow_status", flowStatus);
		mInsertMap.put("remark", remark);
		if ("4497478200010001".equals(flowStatus)) {
			DbUp.upTable("pc_seller_qualification_draftbox").dataInsert(mInsertMap);
		} else {
			MDataMap draftMap = DbUp.upTable("pc_seller_qualification_draftbox").one("seller_qualification_code",
					sellerQualification.getSellerQualificationCode());
			if (null == draftMap || draftMap.isEmpty()) {
				DbUp.upTable("pc_seller_qualification_draftbox").dataInsert(mInsertMap);
			} else {
				mInsertMap.put("zid", draftMap.get("zid"));
				mInsertMap.put("uid", draftMap.get("uid"));
				DbUp.upTable("pc_seller_qualification_draftbox").update(mInsertMap);
			}
		}

		return "";
	}

	/**
	 * 更新资质信息,草稿箱
	 * 
	 * @param sellerQualification
	 * @param 草稿箱数据uid
	 * @return
	 */
	public int updatePualificationDraft(PcSellerQualification sellerQualification, String uid) {
		if (null == sellerQualification || StringUtils.isBlank(sellerQualification.getSellerQualificationCode())) {
			return 0;
		}

		MDataMap mUpdateMap = new MDataMap();
		mUpdateMap.put("small_seller_code", sellerQualification.getSmallSellerCode());
		mUpdateMap.put("brand_code", sellerQualification.getBrandCode());
		mUpdateMap.put("category_code", sellerQualification.getCategoryCode());
		mUpdateMap.put("qualification_json", new JsonHelper<PcSellerQualification>().ObjToString(sellerQualification));
		mUpdateMap.put("uid", uid);

		return DbUp.upTable("pc_seller_qualification_draftbox").dataUpdate(mUpdateMap,
				"brand_code,category_code,qualification_json", "uid");

	}

	/**
	 * 插入资质信息（json）
	 * 
	 * @param sellerQualification
	 * @return
	 */
	public String insertPualification(String qualification_json) {
		PcSellerQualification sellerQualification = new PcSellerQualification();
		if (StringUtils.isBlank(qualification_json)) {
			return "";
		}
		JsonHelper<PcSellerQualification> pHelper = new JsonHelper<PcSellerQualification>();
		sellerQualification = pHelper.StringToObj(qualification_json, sellerQualification);

		return this.insertPualification(sellerQualification);
	}

	/**
	 * 插入资质信息
	 * 
	 * @param sellerQualification
	 * @return
	 */
	public String insertPualification(PcSellerQualification sellerQualification) {

		if (null == sellerQualification || "".equals(sellerQualification.getSellerQualificationCode())) {
			return "";
		}
		// 取出最早过期的资质
		PcQualificationInfo pcQualification = this.getMinEndTime(sellerQualification.getQualificationList());
		sellerQualification.setQualificationName(pcQualification.getQualificationName());
		sellerQualification.setEndTime(pcQualification.getEndTime());

		MDataMap mInsertMap = new MDataMap();
		mInsertMap.put("seller_qualification_code", sellerQualification.getSellerQualificationCode());
		mInsertMap.put("small_seller_code", sellerQualification.getSmallSellerCode());
		mInsertMap.put("brand_code", sellerQualification.getBrandCode());
		mInsertMap.put("category_code", sellerQualification.getCategoryCode());
		mInsertMap.put("qualification_name", sellerQualification.getQualificationName());
		mInsertMap.put("end_time", sellerQualification.getEndTime());

		String uid = DbUp.upTable("pc_seller_qualification").dataInsert(mInsertMap);
		List<PcQualificationInfo> pcQualificationList = sellerQualification.getQualificationList();
		if (null != pcQualificationList && !pcQualificationList.isEmpty()) {
			for (PcQualificationInfo pcQualificationInfo : pcQualificationList) {
				MDataMap mInsertItemMap = new MDataMap();
				mInsertItemMap.put("seller_qualification_code", pcQualificationInfo.getSellerQualificationCode());
				mInsertItemMap.put("small_seller_code", pcQualificationInfo.getSmallSellerCode());
				mInsertItemMap.put("qualification_name", pcQualificationInfo.getQualificationName());
				mInsertItemMap.put("qualification_pic", pcQualificationInfo.getQualificationPic());
				mInsertItemMap.put("end_time", pcQualificationInfo.getEndTime());
				DbUp.upTable("pc_qualification_info").dataInsert(mInsertItemMap);
			}
		}

		return uid;
	}

	/**
	 * 更新资质信息(json)
	 * 
	 * @param sellerQualification
	 * @return
	 */
	public int updatePualification(String qualification_json) {
		PcSellerQualification sellerQualification = new PcSellerQualification();
		if (StringUtils.isBlank(qualification_json)) {
			return 0;
		}
		JsonHelper<PcSellerQualification> pHelper = new JsonHelper<PcSellerQualification>();
		sellerQualification = pHelper.StringToObj(qualification_json, sellerQualification);

		return this.updatePualification(sellerQualification);
	}

	/**
	 * 更新资质信息
	 * 
	 * @param sellerQualification
	 * @return
	 */
	public int updatePualification(PcSellerQualification sellerQualification) {
		if (null == sellerQualification || StringUtils.isBlank(sellerQualification.getSellerQualificationCode())) {
			return 0;
		}
		// 取出最早过期的资质
		PcQualificationInfo pcQualification = this.getMinEndTime(sellerQualification.getQualificationList());
		sellerQualification.setQualificationName(pcQualification.getQualificationName());
		sellerQualification.setEndTime(pcQualification.getEndTime());

		MDataMap mUpdateMap = new MDataMap();
		mUpdateMap.put("seller_qualification_code", sellerQualification.getSellerQualificationCode());
		mUpdateMap.put("small_seller_code", sellerQualification.getSmallSellerCode());
		mUpdateMap.put("brand_code", sellerQualification.getBrandCode());
		mUpdateMap.put("category_code", sellerQualification.getCategoryCode());
		mUpdateMap.put("qualification_name", sellerQualification.getQualificationName());
		mUpdateMap.put("end_time", sellerQualification.getEndTime());

		mUpdateMap.put("uid", sellerQualification.getUid());
		int count = DbUp.upTable("pc_seller_qualification").dataUpdate(mUpdateMap,
				"brand_code,category_code,qualification_name,end_time", "uid");

		// 先清空，后添加
		DbUp.upTable("pc_qualification_info").delete("seller_qualification_code",
				sellerQualification.getSellerQualificationCode());
		List<PcQualificationInfo> pcQualificationList = sellerQualification.getQualificationList();
		if (null != pcQualificationList && !pcQualificationList.isEmpty()) {
			for (PcQualificationInfo pcQualificationInfo : pcQualificationList) {
				MDataMap mInsertItemMap = new MDataMap();
				mInsertItemMap.put("seller_qualification_code", pcQualificationInfo.getSellerQualificationCode());
				mInsertItemMap.put("small_seller_code", pcQualificationInfo.getSmallSellerCode());
				mInsertItemMap.put("qualification_name", pcQualificationInfo.getQualificationName());
				mInsertItemMap.put("qualification_pic", pcQualificationInfo.getQualificationPic());
				mInsertItemMap.put("end_time", pcQualificationInfo.getEndTime());
				DbUp.upTable("pc_qualification_info").dataInsert(mInsertItemMap);
			}
		}

		return count;
	}

	/**
	 * 获取品牌资质信息
	 * 
	 * @param qualificationUid
	 * @return json
	 */
	public String upSellerQualificationJson(String qualificationUid) {
		JsonHelper<PcSellerQualification> jsonHelper = new JsonHelper<PcSellerQualification>();

		if (StringUtils.isNotBlank(qualificationUid)) {
			return jsonHelper.ObjToString(this.getSellerQualification(qualificationUid));
		} else {
			return jsonHelper.ObjToString(new PcSellerQualification());
		}
	}

	/**
	 * 获取品牌资质对象
	 * 
	 * @param qualificationUid
	 * @return
	 */
	public PcSellerQualification getSellerQualification(String qualificationUid) {
		PcSellerQualification sellerQualification = new PcSellerQualification();
		List<PcQualificationInfo> qualificationList = new ArrayList<PcQualificationInfo>();
		if (StringUtils.isNotBlank(qualificationUid)) {
			MDataMap sellerQualificationMap = DbUp.upTable("pc_seller_qualification").one("uid", qualificationUid);
			if (null != sellerQualificationMap && !sellerQualificationMap.isEmpty()) {
				String sellerQualificationCode = sellerQualificationMap.get("seller_qualification_code");
				String smallSellerCode = sellerQualificationMap.get("small_seller_code");
				String brandCode = sellerQualificationMap.get("brand_code");
				String categoryCode = sellerQualificationMap.get("category_code");

				if (StringUtils.isNotBlank(sellerQualificationMap.get("zid"))) {
					sellerQualification.setZid(Integer.parseInt(sellerQualificationMap.get("zid")));
				}
				sellerQualification.setUid(sellerQualificationMap.get("uid"));
				sellerQualification.setSellerQualificationCode(sellerQualificationCode);
				sellerQualification.setSmallSellerCode(smallSellerCode);
				sellerQualification.setBrandCode(brandCode);
				sellerQualification.setCategoryCode(categoryCode);

				if (StringUtils.isNotBlank(sellerQualificationCode)) {
					MDataMap mWhereMap = new MDataMap();
					mWhereMap.put("seller_qualification_code", sellerQualificationCode);
					List<MDataMap> qualificationMapList = DbUp.upTable("pc_qualification_info").queryAll("", "zid", "",
							mWhereMap);
					if (null != qualificationMapList && !qualificationMapList.isEmpty()) {
						for (MDataMap qualificationMap : qualificationMapList) {
							PcQualificationInfo qualification = new PcQualificationInfo();
							SerializeSupport<PcQualificationInfo> serializeSupport = new SerializeSupport<PcQualificationInfo>();
							serializeSupport.serialize(qualificationMap, qualification);

							qualificationList.add(qualification);
						}
						sellerQualification.setQualificationList(qualificationList);
					}
				}
			}
		}
		return sellerQualification;
	}

	/**
	 * 获取草稿箱中品牌资质对象
	 * 
	 * @param qualificationUid
	 * @return
	 */
	public String upSellerQualificationDraftJson(String qualificationUid) {
		if (StringUtils.isNotBlank(qualificationUid)) {
			MDataMap sellerQualificationMap = DbUp.upTable("pc_seller_qualification_draftbox").one("uid",
					qualificationUid);
			if (null != sellerQualificationMap && !sellerQualificationMap.isEmpty()) {
				String qualificationJson = sellerQualificationMap.get("qualification_json");
				if (StringUtils.isNotBlank(qualificationJson)) {
					return qualificationJson;
				}
			}
		}
		return "";
	}
	/**
	 * 获取草稿箱中品牌资质对象
	 * 
	 * @param qualificationUid
	 * @return Map
	 */
	public MDataMap upSellerQualificationDraftMap(String qualificationUid) {
		MDataMap result = new MDataMap();
		if (StringUtils.isNotBlank(qualificationUid)) {
			MDataMap sellerQualificationMap = DbUp.upTable("pc_seller_qualification_draftbox").one("uid",
					qualificationUid);
			if (null != sellerQualificationMap && !sellerQualificationMap.isEmpty()) {
				String qualificationJson = sellerQualificationMap.get("qualification_json");
				if (StringUtils.isNotBlank(qualificationJson)) {
					return sellerQualificationMap;
				}
			}
		}
		return result;
	}
	/**
	 * 新增审批通过
	 * 
	 * @return
	 */
	public String addApproveSellerQualification(String sellerQualificationCode, String qualification_json) {
		// 草稿:4497478200010001,审批被驳回:4497478200010002,新增待审核:4497478200010003,修改待审核:4497478200010004,审核通过:4497478200010005
		// 新增待审核-->审核通过
		this.updateQualificationDraftFlowStatus(sellerQualificationCode, "4497478200010003", "4497478200010005", "");
		return this.insertPualification(qualification_json);
	}

	/**
	 * 修改审批通过
	 * 
	 * @return
	 */
	public int editApproveSellerQualification(String sellerQualificationCode, String qualification_json) {
		// 草稿:4497478200010001,审批被驳回:4497478200010002,新增待审核:4497478200010003,修改待审核:4497478200010004,审核通过:4497478200010005
		// 修改待审核-->审核通过
		this.updateQualificationDraftFlowStatus(sellerQualificationCode, "4497478200010004", "4497478200010005", "");
		return this.updatePualification(qualification_json);
	}

	public int updateQualificationDraftFlowStatus(String sellerQualificationCode, String currentFlowStatus,
			String toFlowStatus, String remark) {
		// DbUp.upTable("pc_seller_qualification_draftbox").dataUpdate(mUpdateMap,
		// "brand_code,category_code,qualification_json", "uid");
		if (StringUtils.isNotBlank(currentFlowStatus) && StringUtils.isNotBlank(toFlowStatus)
				&& StringUtils.isNotBlank(sellerQualificationCode)) {
			String sSql = "update productcenter.pc_seller_qualification_draftbox set flow_status='" + toFlowStatus
					+ "',remark='" + remark + "',reject_adjunct='' where flow_status='" + currentFlowStatus
					+ "' and seller_qualification_code='" + sellerQualificationCode + "'";
			return DbUp.upTable("pc_seller_qualification_draftbox").dataExec(sSql, new MDataMap());
		}
		return 0;
	}

	/**
	 * 
	 * 方法: updateQualificationDraftFlowStatus <br>
	 * 描述: 新添加修改方法，驳回时添加附件 <br>
	 * 作者: zhy<br>
	 * 时间: 2017年4月1日 上午10:42:32
	 * @param sellerQualificationCode
	 * @param currentFlowStatus
	 * @param toFlowStatus
	 * @param remark
	 * @param reject_adjunct
	 * @return
	 */
	public int updateQualificationDraftFlowStatus(String sellerQualificationCode, String currentFlowStatus,
			String toFlowStatus, String remark,String reject_adjunct) {
		// DbUp.upTable("pc_seller_qualification_draftbox").dataUpdate(mUpdateMap,
		// "brand_code,category_code,qualification_json", "uid");
		if (StringUtils.isNotBlank(currentFlowStatus) && StringUtils.isNotBlank(toFlowStatus)
				&& StringUtils.isNotBlank(sellerQualificationCode)) {
			String sSql = "update productcenter.pc_seller_qualification_draftbox set flow_status='" + toFlowStatus
					+ "',remark='" + remark + "',reject_adjunct='" + reject_adjunct + "' where flow_status='" + currentFlowStatus
					+ "' and seller_qualification_code='" + sellerQualificationCode + "'";
			return DbUp.upTable("pc_seller_qualification_draftbox").dataExec(sSql, new MDataMap());
		}
		return 0;
	}
	/**
	 * 同一个商户下质检的商品品牌与商品品类两个字段确定唯一，不允许重复
	 * 
	 * @return
	 */
	public int qualificationRepeatCheck(PcSellerQualification sellerQualification) {
		int count = 0;
		if (null == sellerQualification || StringUtils.isBlank(sellerQualification.getSmallSellerCode())) {
			return count;
		}
		String sWhere = "small_seller_code = '" + sellerQualification.getSmallSellerCode() + "' and brand_code='"
				+ sellerQualification.getBrandCode() + "' and category_code='" + sellerQualification.getCategoryCode()
				+ "' and uid != '" + sellerQualification.getUid() + "' ";
		count = DbUp.upTable("pc_seller_qualification").dataCount(sWhere, new MDataMap());
		if (count <= 0) {
			// 此if判断的是提交两个审核时判断审核中流程中的数据是否重复
			sWhere = "small_seller_code = '" + sellerQualification.getSmallSellerCode() + "' and brand_code='"
					+ sellerQualification.getBrandCode() + "' and category_code='"
					+ sellerQualification.getCategoryCode()
					+ "' and flow_status in ('4497478200010003','4497478200010004')";
			count = DbUp.upTable("pc_seller_qualification_draftbox").dataCount(sWhere, new MDataMap());
		}
		return count;

	}
	
	/**
	 * 根据品牌编号获取品牌名称
	 * 
	 * @return
	 */
	public String getBranName(String brandCode) {
		String brandName = "";
		if (StringUtils.isNotBlank(brandCode)) {
			
			MDataMap brandMap = DbUp.upTable("pc_brandinfo").oneWhere("brand_name", "", "","brand_code",brandCode);
			if (null != brandMap && !brandMap.isEmpty()) {
				brandName = brandMap.get("brand_name");
			}
		}
		return brandName;

	}
}
