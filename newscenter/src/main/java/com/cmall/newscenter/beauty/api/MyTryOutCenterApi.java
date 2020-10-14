package com.cmall.newscenter.beauty.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.cmall.newscenter.beauty.model.MyTryOutCenterInput;
import com.cmall.newscenter.beauty.model.MyTryOutCenterResult;
import com.cmall.newscenter.beauty.model.MyTryOutGood;
import com.cmall.newscenter.model.PageResults;
import com.cmall.newscenter.util.DateUtil;
import com.cmall.productcenter.model.PcFreeTryOutGood;
import com.cmall.productcenter.model.PicInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForToken;


/**
 * 我的试用Api   免费：0；付邮：1
 * @author houwen
 * date: 2014-09-17
 * @version1.0
 */
public class MyTryOutCenterApi extends RootApiForToken<MyTryOutCenterResult, MyTryOutCenterInput> {
	

	public MyTryOutCenterResult Process(MyTryOutCenterInput inputParam,
			MDataMap mRequestMap) {
		
		MyTryOutCenterResult result = new MyTryOutCenterResult();
		
		// 设置相关信息
		if (result.upFlagTrue()) {
			
		    ProductService productService = new ProductService();
			if(inputParam.getType().equals("449746930002")){
		    List<PcFreeTryOutGood>  resultMap = productService.getUserFreeTryOutGoodsList(getUserCode(),inputParam.getType(),getManageCode());
			
			//mWhereMapApply.put("member_code", getUserCode());
			
			//商品总数
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
			result.setPaged(pageResults);
			
			if(resultMap.size()!=0){
             if(!flag){
				//返回界面商品列表
				List<PcFreeTryOutGood> subList = resultMap.subList(startNum, endNum);
		    	
		    	for(int i = 0;i<subList.size();i++){
		    		MyTryOutGood freeGood = new MyTryOutGood();
		    		
		    		freeGood.setActivity_code(subList.get(i).getActivityCode());
		    		PicInfo pic = productService.getPicInfo(Integer.valueOf(inputParam.getPicWidth()), subList.get(i).getpInfo().getMainPicUrl());
		    		freeGood.setPhoto(pic.getPicNewUrl());
		    		freeGood.setName(subList.get(i).getSkuName());
		    		freeGood.setPrice(subList.get(i).getpInfo().getMarketPrice().toString());  //市场价值
		    		freeGood.setId(subList.get(i).getSkuCode());
		    		freeGood.setIs_freeShipping(subList.get(i).getIsFreeShipping());
		    		freeGood.setTime(subList.get(i).getEndTime());      //倒计时
		    		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    		freeGood.setSystem_time(sf.format(new Date()));   //当前系统时间
		    		freeGood.setCount(String.valueOf(subList.get(i).getInitInventory()));
		    		freeGood.setDescribe(subList.get(i).getNotice());
		    		freeGood.setTryout_count(String.valueOf(subList.get(i).getApplyNum()));    //商品申请 人数
		    		int surplus_count = subList.get(i).getTryoutInventory();
		    		freeGood.setSurplus_count(String.valueOf(surplus_count)); //商品剩余件数，付邮商品在生成订单的时候就去 oc_tryout_products表里将试用库存减1
		    		freeGood.setPostage(subList.get(i).getPostage());
		    		String dString = DateUtil.getSysDateTimeString();
		    		String endTime = subList.get(i).getEndTime();
					if(dString.compareTo(endTime)>0 || surplus_count<=0){
						freeGood.setStatus("449746890004"); //已结束
					}else {
						freeGood.setStatus("449746890006");  //暂时返回已试用
					}
		    		result.getTryOutGoods().add(freeGood);
		    		
		    	}
		    }
          }
		}else{
			
			List<PcFreeTryOutGood> resultMap = productService.getUserApplyFreeTryOutGoodsList(getUserCode(),getManageCode());
			//商品总数
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
			result.setPaged(pageResults);
			
			if(resultMap.size()!=0){
             if(!flag){
				//返回界面商品列表
				List<PcFreeTryOutGood> subList = resultMap.subList(startNum, endNum);
		    	
		    	for(int i = 0;i<subList.size();i++){
		    		
                    MyTryOutGood freeGood = new MyTryOutGood();
                    freeGood.setActivity_code(subList.get(i).getActivityCode());
		    		freeGood.setPhoto(subList.get(i).getpInfo().getMainPicUrl());
		    		freeGood.setName(subList.get(i).getSkuName());
		    		freeGood.setPrice(subList.get(i).getpInfo().getMarketPrice().toString());  //市场价值
		    		freeGood.setId(subList.get(i).getSkuCode());
		    		freeGood.setIs_freeShipping(subList.get(i).getIsFreeShipping());
		    		freeGood.setTime(subList.get(i).getEndTime());      //倒计时
		    		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    		freeGood.setSystem_time(sf.format(new Date()));   //当前系统时间
		    		freeGood.setCount(String.valueOf(subList.get(i).getInitInventory()));
		    		freeGood.setTryout_count(String.valueOf(subList.get(i).getApplyNum()));    //商品申请 人数
		    		freeGood.setPostage(subList.get(i).getPostage());
		    		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
		    		freeGood.setDescribe(subList.get(i).getNotice());
		    		freeGood.setStatus(subList.get(i).getTryoutStatus());  //申请通过
		    		result.getTryOutGoods().add(freeGood);
    			
    		}
    		}
			}
		}
	}
		return result;
    }
	}
	


