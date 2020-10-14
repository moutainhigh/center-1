package com.cmall.newscenter.beauty.api;

import java.util.List;

import com.cmall.newscenter.beauty.model.BeautyAddress;
import com.cmall.newscenter.beauty.model.GetAddressInput;
import com.cmall.newscenter.beauty.model.GetAddressResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.model.PageResults;
import com.cmall.newscenter.service.MemberAuthInfoService;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 惠美丽—获取收货地址Api（默认地址排在第一 其他按更新时间排序 ）
 * 
 * @author yangrong date: 2014-08-20
 * @version1.0
 */
public class GetAddress extends RootApiForToken<GetAddressResult, GetAddressInput> {

	public GetAddressResult Process(GetAddressInput inputParam,
			MDataMap mRequestMap) {

		GetAddressResult result = new GetAddressResult();
		

		// 设置相关信息
		if (result.upFlagTrue()) {
			
			MemberAuthInfoService authInfoService = new MemberAuthInfoService();
			
			

			// 查询地址信息
			MDataMap mWhereMap = new MDataMap();

			mWhereMap.put("address_code", getUserCode());

			mWhereMap.put("app_code", getManageCode());

			//默认地址排在第一
			MDataMap map = DbUp.upTable("nc_address").one("app_code",getManageCode(), "address_code", getUserCode(),"address_default", "1");
			
			if (map != null && inputParam.getPaging().getOffset() == 0 ) {
				BeautyAddress address = new BeautyAddress(); 

				address.setIsdefault(map.get("address_default"));
				address.setPostcode(map.get("address_postalcode"));
				address.setProvinces(map.get("address_province"));
				address.setId(map.get("address_id"));
				address.setName(map.get("address_name"));
				address.setMobile(map.get("address_mobile"));
				address.setStreet(map.get("address_street"));
				address.setAreaCode(map.get("area_code"));
				address.setEmail(map.get("email"));
				
				String idNumber = authInfoService.enIdNumber(authInfoService.getIdNumber(getUserCode(),address.getId()));
				
				address.setIdNumber(idNumber);
				
				result.getAdress().add(address);
			} 
			
			//按更新时间排序
			PageOption pageOption = new PageOption();
			boolean isPage = false;
			if(inputParam.getPaging().getLimit() != 0) {
				pageOption = inputParam.getPaging();
				isPage = true;
			}
			MPageData mPageData = DataPaging.upPageData("nc_address", "","-update_time", mWhereMap, pageOption);
			
			if (mPageData != null) {

				for (MDataMap mDataMap : mPageData.getListData()) {
					//除了默认地址  其他按更新时间排序
					if (!mDataMap.get("address_default").toString().equals("1")) {
						BeautyAddress address = new BeautyAddress();
						
						address.setIsdefault(mDataMap.get("address_default"));
						address.setPostcode(mDataMap.get("address_postalcode"));
						address.setProvinces(mDataMap.get("address_province"));
						address.setId(mDataMap.get("address_id"));
						address.setName(mDataMap.get("address_name"));
						address.setMobile(mDataMap.get("address_mobile"));
						address.setStreet(mDataMap.get("address_street"));
						address.setAreaCode(mDataMap.get("area_code"));
						address.setEmail(mDataMap.get("email"));
						
						String idNumber = authInfoService.enIdNumber(authInfoService.getIdNumber(getUserCode(),address.getId()));
						
						address.setIdNumber(idNumber);
						
						//判断是否分页
						if(isPage && map != null && result.getAdress().size() == inputParam.getPaging().getLimit()) {
							break;
						} 
						result.getAdress().add(address);
					}

				}
				
				//分页信息
				PageResults pageResults = new PageResults();
				/* 查询总共条数 */
				List<MDataMap> mPageDataAll = DbUp.upTable("nc_address").queryAll("", "", "", mWhereMap);
				int totalNum = mPageDataAll.size();
				if(isPage) {
					
					int offset = inputParam.getPaging().getOffset();//起始页
					int limit = inputParam.getPaging().getLimit();//每页条数
					int startNum = limit*offset;//开始条数
					int endNum = startNum+limit;//结束条数
					int more = 1;//有更多数据
					if(endNum>totalNum){
						endNum = totalNum;
						more = 0;
					}
					//如果起始条件大于总数则返回0条数据
					if(startNum>totalNum){
						startNum = 0;
						endNum = 0;
						more = 0;
					}
					pageResults.setCount(endNum-startNum);
					pageResults.setMore(more);
					
				} else {
					pageResults.setCount(totalNum);
					pageResults.setMore(0);
				}
				pageResults.setTotal(totalNum);
				result.setPaged(pageResults);
			}
		}
		return result;
	}

}
