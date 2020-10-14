package com.cmall.newscenter.beauty.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cmall.newscenter.beauty.model.TryOutApplyInput;
import com.cmall.newscenter.beauty.model.TryOutApplyResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 申请试用接口
 * 
 * @author houwen date 2014-10-14
 * @version 1.0
 */

public class TryOutApplyApi extends
		RootApiForToken<TryOutApplyResult, TryOutApplyInput> {

	static String regEx = "[\u4e00-\u9fa5]";

	static Pattern pat = Pattern.compile(regEx);

	public TryOutApplyResult Process(TryOutApplyInput inputParam,
			MDataMap mRequestMap) {

		TryOutApplyResult result = new TryOutApplyResult();
		if (result.upFlagTrue()) {

			MDataMap mDataMap = new MDataMap();
			MDataMap mWhereMapUser = new MDataMap();
			MPageData mPageDataUser = new MPageData();
			MDataMap mWhereMapApply = new MDataMap();
			MPageData mPageDataApply = new MPageData();

			mWhereMapApply.put("member_code", getUserCode());
			mWhereMapApply.put("sku_code", inputParam.getSku_code());
			mWhereMapApply.put("end_time", inputParam.getEnd_time());
			mPageDataApply = DataPaging.upPageData("nc_freetryout_apply", "",
					"", mWhereMapApply, new PageOption());
			if (mPageDataApply.getListData().size() != 0) {
				result.setStatus("449746890002");// 已申请
			} else {

				mDataMap.put("member_code", getUserCode());

				mWhereMapUser.put("member_code", getUserCode());
				/* 根据发布人ID查询发布人信息列表 */
				mPageDataUser = DataPaging.upPageData("mc_extend_info_star",
						"", "", mWhereMapUser, new PageOption());

				if (mPageDataUser.getListData().size() != 0) {

					mDataMap.put("nickname", mPageDataUser.getListData().get(0)
							.get("nickname"));
					mDataMap.put("member_sex",
							mPageDataUser.getListData().get(0)
									.get("member_sex"));
					mDataMap.put("mobile_phone", mPageDataUser.getListData()
							.get(0).get("mobile_phone"));
					mDataMap.put("member_avatar", mPageDataUser.getListData()
							.get(0).get("member_avatar"));
					mDataMap.put("skin_type", mPageDataUser.getListData()
							.get(0).get("skin_type"));
					mDataMap.put("hopeful", mPageDataUser.getListData().get(0)
							.get("hopeful"));
				}

				mDataMap.put("app_code", getManageCode());

				mDataMap.put("sku_code", inputParam.getSku_code());

				mDataMap.put("sku_name", inputParam.getSku_name());
				// 评论时间 为系统时间
				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss"); // 设置日期格式

				mDataMap.put("create_time", df.format(new Date()));

				mDataMap.put("ordersource", inputParam.getOrderSource());

				mDataMap.put("address_code", inputParam.getAddress_code());

				mDataMap.put("status", "449746890002"); // 申请状态
														// ：未申请：449746890001；已申请：449746890002；申请通过：449746890003；449746890004：已结束

				/* 判断是否存在中文字符 */
				Matcher matcher = pat.matcher(inputParam.getArea_code());

				if (matcher.find()) {

					MDataMap dataMap = DbUp.upTable("nc_order_area").one(
							"area_name", inputParam.getArea_code());

					if (dataMap != null) {
						mDataMap.put("address_county",
								dataMap.get("area_code"));// 收货人地址第三极编号
					}

				} else {

					mDataMap.put("address_county", inputParam.getArea_code());// 收货人地址第三极编号
				}

				mDataMap.put("address_street", inputParam.getBuyer_address());

				mDataMap.put("address_mobile", inputParam.getBuyer_mobile());

				mDataMap.put("address_name", inputParam.getBuyer_name());

				mDataMap.put("address_postalcode", inputParam.getPostCode());

				mDataMap.put("activityCode", inputParam.getActivityCode());

				mDataMap.put("end_time", inputParam.getEnd_time());

				/* 将申请信息放入数据库中 */
				DbUp.upTable("nc_freetryout_apply").dataInsert(mDataMap);

				result.setStatus("449746890002");// 已申请
			}
		}

		return result;
	}

}
