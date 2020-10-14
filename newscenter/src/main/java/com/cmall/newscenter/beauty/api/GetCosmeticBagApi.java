package com.cmall.newscenter.beauty.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.cmall.newscenter.beauty.model.CosmeticBag;
import com.cmall.newscenter.beauty.model.GetCosmeticBagResult;
import com.cmall.newscenter.beauty.model.GetCosmeticBagInput;
import com.cmall.newscenter.model.PageResults;
import com.cmall.productcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 惠美丽—获取化妆包中的妆品Api
 * 
 * @author yangrong date: 2015-01-25
 * @version1.3.2
 */
public class GetCosmeticBagApi extends RootApiForToken<GetCosmeticBagResult, GetCosmeticBagInput> {

	public GetCosmeticBagResult Process(GetCosmeticBagInput inputParam,MDataMap mRequestMap) {

		GetCosmeticBagResult result = new GetCosmeticBagResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			if (inputParam.getCosmetic_type().equals("")) {

				// 查出所有
				MDataMap mDataMap = new MDataMap();

				mDataMap.put("member_code", getUserCode());

				String sql = "SELECT * FROM nc_cosmetic_bag WHERE member_code = :member_code ORDER BY update_time DESC";

				List<Map<String, Object>> list = DbUp.upTable("nc_cosmetic_bag").dataSqlList(sql, mDataMap);

				// 总数
				int totalNum = list.size();
				int offset = inputParam.getPaging().getOffset();// 起始页 1
				int limit = inputParam.getPaging().getLimit();// 每页条数 10
				int startNum = limit * offset;// 开始条数 10
				int endNum = startNum + limit;// 结束条数 20
				int more = 1;// 有更多数据
				Boolean flag = true;
				if (startNum < totalNum) {
					flag = false;
				}
				if (endNum >= totalNum) {
					endNum = totalNum;
					more = 0;
				}

				// 分页信息
				PageResults pageResults = new PageResults();
				pageResults.setTotal(totalNum);
				pageResults.setCount(endNum - startNum);
				pageResults.setMore(more);
				result.setPaged(pageResults);

				if (offset > (limit==0?limit:(totalNum / limit))) {
					pageResults.setCount(0);
				} else {

					List<Map<String, Object>> subList = list.subList(startNum,endNum);

					if (!flag) {

						if (subList != null && subList.size() != 0) {

							for (int i = 0; i < subList.size(); i++) {

								Map<String, Object> map = subList.get(i);
								
								CosmeticBag ctb = new CosmeticBag();
								ctb.setCosmetic_code(map.get("cosmetic_code").toString());
								ctb.setCosmetic_name(map.get("cosmetic_name").toString());
								ctb.setCosmetic_price(map.get("cosmetic_price").toString());
								ctb.setDisabled_time(map.get("disabled_time").toString());
								ctb.setCount(map.get("count").toString());
								ctb.setIswarn(map.get("iswarn").toString());
								ctb.setRemark(map.get("remark").toString());
								ctb.setUnit(map.get("unit").toString());
								if(!map.get("disabled_time").toString().equals("")){
									
									try {
										String days = GetCosmeticBagApi.time(map.get("disabled_time").toString());
										ctb.setDays(days);
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
								String img = map.get("photo").toString();
								List<String> photolist = new ArrayList<String>();
								String a[] = img.split(",");

								for (int j = 0; j < a.length; j++) {

									photolist.add(a[j]);
								}
								ctb.setPhoto(photolist);

								result.getCosmetic().add(ctb);
							}
						}
					}
				}

			} else {

				// 根据状态查询
				MDataMap mDataMap = new MDataMap();

				mDataMap.put("member_code", getUserCode());

				mDataMap.put("status", inputParam.getCosmetic_type());

				String sql = "SELECT * FROM nc_cosmetic_bag WHERE member_code = :member_code and status = :status ORDER BY disabled_time";

				List<Map<String, Object>> list = DbUp.upTable("nc_cosmetic_bag").dataSqlList(sql, mDataMap);

				// 总数
				int totalNum = list.size();
				int offset = inputParam.getPaging().getOffset();// 起始页 1
				int limit = inputParam.getPaging().getLimit();// 每页条数 10
				int startNum = limit * offset;// 开始条数 10
				int endNum = startNum + limit;// 结束条数 20
				int more = 1;// 有更多数据
				Boolean flag = true;
				if (startNum < totalNum) {
					flag = false;
				}
				if (endNum >= totalNum) {
					endNum = totalNum;
					more = 0;
				}

				// 分页信息
				PageResults pageResults = new PageResults();
				pageResults.setTotal(totalNum);
				pageResults.setCount(endNum - startNum);
				pageResults.setMore(more);
				result.setPaged(pageResults);

				if (offset > (limit==0?limit:(totalNum / limit))) {
					pageResults.setCount(0);
				} else {

					List<Map<String, Object>> subList = list.subList(startNum,endNum);

					if (!flag) {

						if (subList != null && subList.size() != 0) {

							for (int i = 0; i < subList.size(); i++) {

								Map<String, Object> map = subList.get(i);
								CosmeticBag ctb = new CosmeticBag();
								ctb.setCosmetic_code(map.get("cosmetic_code").toString());
								ctb.setCosmetic_name(map.get("cosmetic_name").toString());
								ctb.setCosmetic_price(map.get("cosmetic_price").toString());
								ctb.setDisabled_time(map.get("disabled_time").toString());
								ctb.setCount(map.get("count").toString());
								ctb.setIswarn(map.get("iswarn").toString());
								ctb.setRemark(map.get("remark").toString());
								ctb.setUnit(map.get("unit").toString());
								if(!map.get("disabled_time").toString().equals("")){
									
									try {
										String days = GetCosmeticBagApi.time(map.get("disabled_time").toString());
										ctb.setDays(days);
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
								String img = map.get("photo").toString();
								List<String> photolist = new ArrayList<String>();
								String a[] = img.split(",");

								for (int j = 0; j < a.length; j++) {

									photolist.add(a[j]);
								}
								ctb.setPhoto(photolist);

								result.getCosmetic().add(ctb);
							}
						}
					}
				}

			}

		}
		return result;
	}
	
	//计算某一天距离今天有多少天
	public static String time(String disabled_time) throws ParseException {
		
		DateUtil.getSysDateString();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long a=sdf.parse(DateUtil.getSysDateString()).getTime();   //今天的年月日 
		long b = sdf.parse(disabled_time).getTime();             //失效日期的年月日
		int success = (int) ((b-a)/(1000*60*60*24));  //1000毫秒*60分钟*60秒*24小时 = 天
		String days = String.valueOf(success+1);
		//System.out.println(days);
		return days;
		
	}
	
}
