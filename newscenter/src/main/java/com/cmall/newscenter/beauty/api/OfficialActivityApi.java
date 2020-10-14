package com.cmall.newscenter.beauty.api;

import java.util.List;
import java.util.Map;

import com.cmall.newscenter.beauty.model.Activity;
import com.cmall.newscenter.beauty.model.OfficialActivityInput;
import com.cmall.newscenter.beauty.model.OfficialActivityResult;
import com.cmall.newscenter.model.PageResults;
import com.cmall.productcenter.model.PicInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 官方活动Api
 * 
 * @author houwen date: 2014-09-19
 * @version1.0
 */
public class OfficialActivityApi extends
		RootApiForManage<OfficialActivityResult, OfficialActivityInput> {

	public OfficialActivityResult Process(OfficialActivityInput inputParam,
			MDataMap mRequestMap) {

		OfficialActivityResult result = new OfficialActivityResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			MDataMap mWhereMap = new MDataMap();

			// 官方活动列表
			/*
			 * mWhereMap.put("info_category","4497465000030001");
			 * mWhereMap.put("manage_code", getManageCode()); MPageData
			 * mPageData=DataPaging.upPageData("nc_info", "", "-create_time",
			 * mWhereMap, inputParam.getPaging());
			 */
			List<Map<String, Object>> list = null;
			String sql = "select * from nc_info n where n.online_time<now() and n.offline_time>now() and n.info_category in ('4497465000030001') and manage_code='"
					+ getManageCode() + "' order by create_time desc";
			list = DbUp.upTable("nc_posts").dataSqlList(sql, mWhereMap);

			if (list != null) {
				int totalNum = list.size();
				int offset = inputParam.getPaging().getOffset();// 起始页
				int limit = inputParam.getPaging().getLimit();// 每页条数
				int startNum = limit * offset;// 开始条数
				int endNum = startNum + limit;// 结束条数
				int more = 1;// 有更多数据
				Boolean flag = true;
				if (startNum < totalNum) {
					flag = false;
				}
				if (endNum >= totalNum) {
					if (0 == totalNum) {
						startNum = 0;
					}
					endNum = totalNum;
					more = 0;
				}

				ProductService productService = new ProductService();
				
				
				// 分页信息
				PageResults pageResults = new PageResults();
				pageResults.setTotal(totalNum);
				pageResults.setCount(endNum - startNum);
				pageResults.setMore(more);
				result.setPaged(pageResults);
				if (!flag) {
					if (list.size() != 0) {

						List<Map<String, Object>> subList = list.subList(
								startNum, endNum);
						for (int i = 0; i < subList.size(); i++) {

							Activity activity = new Activity();
							
							PicInfo pic = productService.getPicInfo(Integer.valueOf(inputParam.getPicWidth()), subList.get(i).get("photos")
									.toString());
							activity.setPhoto(pic.getPicNewUrl());
							activity.setName(subList.get(i).get("info_title")
									.toString());
							activity.setStart_time(subList.get(i)
									.get("begin_time").toString());
							activity.setEnd_time(subList.get(i).get("end_time")
									.toString());
							activity.setUrl(subList.get(i).get("link_url")
									.toString());
							
							PicInfo pho = productService.getPicInfo(Integer.valueOf(inputParam.getPicWidth()), subList.get(i).get("share_pic")
									.toString());
							activity.setShare_pic(pho.getPicNewUrl());
							
							activity.setInfo_content(subList.get(i).get("info_content")
									.toString());
							result.getActivities().add(activity);
						}

					}
				}
			}

		}
		return result;
	}
}

