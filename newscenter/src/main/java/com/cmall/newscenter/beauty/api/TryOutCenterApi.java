package com.cmall.newscenter.beauty.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.cmall.newscenter.beauty.model.TryOutCenterInput;
import com.cmall.newscenter.beauty.model.TryOutCenterResult;
import com.cmall.newscenter.beauty.model.TryOutGood;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.cmall.newscenter.util.DateUtil;
import com.cmall.productcenter.model.PcFreeTryOutGood;
import com.cmall.productcenter.model.PicInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;


/**
 * 试用中心Api   免费：0；付邮：1
 * @author houwen
 * date: 2014-09-17
 * @version1.0
 */
public class TryOutCenterApi extends RootApiForManage<TryOutCenterResult, TryOutCenterInput> {
	

	public TryOutCenterResult Process(TryOutCenterInput inputParam,
			MDataMap mRequestMap) {
		
		TryOutCenterResult result = new TryOutCenterResult();
		
		int count = 0;
		// 设置相关信息
		if (result.upFlagTrue()) {
			
		    ProductService productService = new ProductService();
		    
		    
		    List<PcFreeTryOutGood>  resultMapOld = productService.getFreeTryOutGoodsList(getManageCode());
		    int day = 15;   //15天内结束的试用商品   add by ligj
		    List<PcFreeTryOutGood> resultMapForEnd = productService.getFreeTryOutGoodsListForEnd(day, getManageCode());
		    resultMapOld.addAll(resultMapForEnd);			//结束试用商品列表添加到生效试用商品列表后
		    
		    //去重试用商品，一个商品不会在试用列表中同时存在两个试用活动。保留生效试用活动，已结束的保留离当前时间最近的那条
			List<PcFreeTryOutGood> resultMap = new ArrayList<PcFreeTryOutGood>();
			for (PcFreeTryOutGood obj : resultMapOld) {
			   boolean flag = false;
			   for (PcFreeTryOutGood objNew : resultMap) {
				   if (objNew.getSkuCode().equals(obj.getSkuCode())) {
						flag = true;
						break;
				   }
			   }
			   if (!flag) {
				   resultMap.add(obj);
			   }
		   }
			//免费试用在前，付邮在后
			Collections.sort(resultMap, new Comparator<Object>() {
			      public int compare(Object free1, Object free2) {
			    	  PcFreeTryOutGood one = (PcFreeTryOutGood)free1;
			    	  PcFreeTryOutGood two = (PcFreeTryOutGood)free2;
			    	  
			    	  // 第一次比较类型
		              int i = two.getIsFreeShipping().compareTo(one.getIsFreeShipping());
		              // 如果类型相同则进行第二次比较
		            if(i==0){
		            	// 第二次比较排序结束时间
		                int j=two.getSortEndTime().compareTo(one.getSortEndTime());
		                 return j;
		             }
		             return i;
			     }
			    });
		    
		/*	//商品总数
			int totalNum = resultMap.size();
			int offset = inputParam.getPaging().getOffset();//起始页
			int limit = inputParam.getPaging().getLimit();//每页条数
			int startNum = limit*offset;//开始条数
			int endNum = startNum+limit;//结束条数
			int more = 1;//有更多数据
			Boolean flag = true;
			if(startNum<totalNum){
				flag = false;
			}
			if(endNum>=totalNum){
				if(0==totalNum){
					startNum = 0;
				}
				endNum = totalNum;
				more = 0;
			}
			
			//分页信息
			PageResults pageResults = new PageResults();
			pageResults.setTotal(totalNum);
			pageResults.setCount(endNum-startNum);
			pageResults.setMore(more);
			result.setPaged(pageResults);*/
			if(resultMap.size()!=0){
				//返回界面商品列表
				//List<PcFreeTryOutGood> subList = resultMap.subList(startNum, endNum);
		    	
		    	for(int i = 0;i<resultMap.size();i++){
		    		TryOutGood freeGood = new TryOutGood();
		    		
		    		freeGood.setActivity_code(resultMap.get(i).getActivityCode());
		    		if(resultMap.get(i).getpInfo().getProductSkuInfoList()!=null&&resultMap.get(i).getpInfo().getProductSkuInfoList().size()!=0){
		    			PicInfo pic = productService.getPicInfo(Integer.valueOf(inputParam.getPicWidth()), resultMap.get(i).getpInfo().getProductSkuInfoList().get(0).getSkuPicUrl());
			    		freeGood.setPhoto(pic.getPicNewUrl());
		    		}
		    		
		    		freeGood.setName(resultMap.get(i).getSkuName());
		    		freeGood.setPrice(resultMap.get(i).getpInfo().getMarketPrice().toString());  //市场价值
		    		freeGood.setId(resultMap.get(i).getSkuCode());
		    		freeGood.setTime(resultMap.get(i).getEndTime());      //倒计时

		    		freeGood.setSystem_time(DateUtil.getSysDateTimeString());   //当前系统时间
		    		freeGood.setCount(String.valueOf(resultMap.get(i).getInitInventory()));
		    		count = resultMap.get(i).getInitInventory()-resultMap.get(i).getTryoutInventory();
		    		//从免费试用申请表里申请
		    		MDataMap mWhereMapApply = new MDataMap();
					MPageData mPageDataApply = new MPageData();
					int size = 0;
					mWhereMapApply.put("sku_code", resultMap.get(i).getSkuCode());
					mWhereMapApply.put("end_time", resultMap.get(i).getEndTime());
					mPageDataApply = DataPaging.upPageData("nc_freetryout_apply", "", "-create_time", mWhereMapApply,new PageOption());
					if(mPageDataApply.getListData().size()!=0){
						size = mPageDataApply.getListData().size();
					}
					freeGood.setTryout_count(String.valueOf(size));    //商品申请人数   
					
					String endTime = resultMap.get(i).getEndTime();
					String sysTime = DateUtil.getSysDateTimeString();
					
					if(resultMap.get(i).getIsFreeShipping().equals("449746930002")){
						if(sysTime.compareTo(endTime)>0){
							freeGood.setSurplus_count("0"); //商品剩余件数 = 试用库存
						}else {
								freeGood.setSurplus_count(String.valueOf(resultMap.get(i).getTryoutInventory())); //商品剩余件数 = 试用库存
							}
					}else {
						freeGood.setSurplus_count(String.valueOf(resultMap.get(i).getTryoutInventory())); //商品剩余件数 = 试用库存
					}
		    							
		    		freeGood.setIs_freeShipping(resultMap.get(i).getIsFreeShipping());
		    		freeGood.setPostage(resultMap.get(i).getPostage());
		    		//freeTryOutGood.setCount(resultMap.get(i).get)
		    		
		    		result.getTryOutGoods().add(freeGood);
		    		
		    	}
		    
          }
		}
		return result;
	}
}


