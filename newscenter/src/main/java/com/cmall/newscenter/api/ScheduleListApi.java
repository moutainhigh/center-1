package com.cmall.newscenter.api;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.cmall.newscenter.model.CommentdityAppPhotos;
import com.cmall.newscenter.model.Location;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.Schedule;
import com.cmall.newscenter.model.ScheduleListInput;
import com.cmall.newscenter.model.ScheduleListResult;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 行程
 * 
 * @author shiyz date 2014-8-11
 * @version 1.0
 */
public class ScheduleListApi extends
		RootApiForManage<ScheduleListResult, ScheduleListInput> {

	public ScheduleListResult Process(ScheduleListInput inputParam,
			MDataMap mRequestMap) {

		ScheduleListResult result = new ScheduleListResult();
		String app_code = bConfig("newscenter.app_code");

		if (result.upFlagTrue()) {

			MDataMap mWhereMap = new MDataMap();

			MDataMap cateDataMap = new MDataMap();

			cateDataMap = DbUp.upTable("nc_category").one("category_code",
					inputParam.getColumn());

			if (cateDataMap != null) {

				Schedule schedule = new Schedule();

				schedule.getIcon().setLarge(cateDataMap.get("line_head"));

				schedule.getIcon().setThumb(cateDataMap.get("line_head"));

				result.getSchedules().add(schedule);

			}

			mWhereMap.put("info_category", inputParam.getColumn());

			mWhereMap.put("flag_show", "449746530001");
			mWhereMap.put("manage_code", app_code);

			MPageData mPageData = DataPaging.upPageData("nc_info", "",
					"-create_time", mWhereMap, inputParam.getPaging());

			if (mPageData != null) {

				for (MDataMap mDataMap : mPageData.getListData()) {

					// 经纬度
					WebClientSupport webClientSupport = new WebClientSupport();
					String sResponse = "";

					// 修正地址信息 百度如果返回不到结果则屏蔽掉
					Location location = new Location();

					if (StringUtils.isNotEmpty(mDataMap.get("address"))) {
						try {
							sResponse = webClientSupport
									.doGet("http://api.map.baidu.com/geocoder/v2/?address='"
											+ mDataMap.get("address")
											+ "'&output=json&ak=479efee5e54b5d3f6cd724008a81659b");

							// 将location解析出来
							com.alibaba.fastjson.JSONObject parseObject = JSON
									.parseObject(sResponse);
							com.alibaba.fastjson.JSONObject parseObject1 = (com.alibaba.fastjson.JSONObject) parseObject
									.get("result");
							com.alibaba.fastjson.JSONObject parseObject2 = (com.alibaba.fastjson.JSONObject) parseObject1
									.get("location");

							// 转成map
							Map<String, Object> tmap = new HashMap<String, Object>();
							tmap.put("lat", parseObject2.get("lat"));
							tmap.put("lon", parseObject2.get("lng"));

							// 转成实体类
							JsonHelper<Location> jsonHelper = new JsonHelper<Location>();
							location = jsonHelper.StringToObj(
									JSON.toJSONString(tmap), new Location());

						} catch (Exception e) {

							e.printStackTrace();
						}
					}

					// bLogInfo(0, sResponse);

					Schedule schedule = new Schedule();

					schedule.setBegin_at(mDataMap.get("begin_time"));

					schedule.setEnd_at(mDataMap.get("end_time"));

					schedule.setTitle(mDataMap.get("info_title"));

					CommentdityAppPhotos icon = new CommentdityAppPhotos();

					icon.setLarge(mDataMap.get("photos"));

					icon.setThumb(mDataMap.get("photos"));

					schedule.setIcon(icon);

					location.setLat(location.getLat());

					location.setLon(location.getLon());

					location.setName(mDataMap.get("address"));

					schedule.setLocation(location);

					result.getSchedules().add(schedule);

				}

			}

			result.setPaged(mPageData.getPageResults());
		}

		return result;
	}

}
