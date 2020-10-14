package com.cmall.newscenter.beauty.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.newscenter.beauty.model.TimedScareBuying;
import com.cmall.newscenter.beauty.model.TimedScareBuyingListInput;
import com.cmall.newscenter.beauty.model.TimedScareBuyingListResult;
import com.cmall.newscenter.model.PageResults;
import com.cmall.newscenter.util.DateUtil;
import com.cmall.productcenter.model.FlashsalesSkuInfo;
import com.cmall.productcenter.model.PcProductPrice;
import com.cmall.productcenter.model.PicInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 限时抢购列表API
 * 
 * @author yangrong date: 2014-09-17
 * @version1.0
 */
public class TimedScareBuyingListApi extends RootApiForManage<TimedScareBuyingListResult, TimedScareBuyingListInput> {

	public TimedScareBuyingListResult Process(TimedScareBuyingListInput inputParam, MDataMap mRequestMap) {

		TimedScareBuyingListResult result = new TimedScareBuyingListResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			ProductService productService = new ProductService();

			// 正在抢购的商品列表
			List<FlashsalesSkuInfo> skuInfoListNow = productService.getFlashsalesForSkuCodeAll("", getManageCode());

			// 规定时间内的已结束的限时抢购，直接传入天数即可 add by ligj
			int day = 7;
			List<FlashsalesSkuInfo> endSkuInfoList = productService.getFlashsalesForSkuCodeEnd(day, getManageCode());

			// 把因为剩余库存为0已结束的闪购信息已结束列表中
			for (int i = 0; i < skuInfoListNow.size(); i++) {
				if (skuInfoListNow.get(i).getEndTime().compareTo(DateUtil.getSysDateTimeString()) <= 0) {
					endSkuInfoList.add(skuInfoListNow.get(i)); // 放到已结束列表
					skuInfoListNow.remove(i); // 在生效列表中删除
				}
			}
			// 将正在进行闪购的商品列表按照结束时间倒排序
			Collections.sort(endSkuInfoList, new Comparator<Object>() {
				public int compare(Object flashsale1, Object flashsale2) {
					String one = ((FlashsalesSkuInfo) flashsale1).getEndTime();
					String two = ((FlashsalesSkuInfo) flashsale2).getEndTime();
					return two.compareTo(one);
				}
			});
			// 按照结束时间正排序
			Collections.sort(skuInfoListNow, new Comparator<Object>() {
				public int compare(Object flashsale1, Object flashsale2) {
					String one = ((FlashsalesSkuInfo) flashsale1).getEndTime();
					String two = ((FlashsalesSkuInfo) flashsale2).getEndTime();
					return one.compareTo(two);
				}
			});

			// 将结束限购列表拼接到正在抢购列表后
			skuInfoListNow.addAll(endSkuInfoList);

			List<FlashsalesSkuInfo> skuInfoList = new ArrayList<FlashsalesSkuInfo>();
			// 循环去重，因为已经排好序所以活动已结束的有重复的时候保留的是最近的。
			for (FlashsalesSkuInfo flashsalesSkuInfo : skuInfoListNow) {
				boolean flag = false;
				for (FlashsalesSkuInfo flashsalesNew : skuInfoList) {
					if (flashsalesNew.getSkuCode().equals(flashsalesSkuInfo.getSkuCode())) {
						flag = true;
						break;
					}
				}
				if (!flag) {
					skuInfoList.add(flashsalesSkuInfo);
				}
			}
			// 商品总数
			int totalNum = skuInfoList.size();
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

			// 分页信息
			PageResults pageResults = new PageResults();
			pageResults.setTotal(totalNum);
			pageResults.setCount(endNum - startNum);
			pageResults.setMore(more);
			result.setPaged(pageResults);

			if (!flag) {

				// 返回界面商品列表
				List<FlashsalesSkuInfo> subList = skuInfoList.subList(startNum,
						endNum);

				for (int i = 0; i < subList.size(); i++) {

					TimedScareBuying flashSale = new TimedScareBuying();

					flashSale.setSku_code(subList.get(i).getSkuCode());
					flashSale.setName(subList.get(i).getSkuName());

					ProductService product = new ProductService();

					PcProductPrice productPrice = product.getSkuProductPrice(subList.get(i).getSkuCode(), getManageCode());

					flashSale.setOldPrice(productPrice.getMarketPrice().toString());

					// int num =
					// product.salesNumSurplus(subList.get(i).getSkuCode(),
					// subList.get(i).getActivityCode());
					/* 剩余件数 */
					flashSale.setRemaind_count(String.valueOf(subList.get(i)
							.getSurplusNum()));

					// 有活动价格显示活动价格 没有活动价格显示销售价
					if (("").equals(productPrice.getVipPrice())|| null == productPrice.getVipPrice()) {

						flashSale.setNewPrice(productPrice.getSellPrice().toString());
					} else {

						flashSale.setNewPrice(productPrice.getVipPrice());
					}
					flashSale.setRebate(productPrice.getDiscount());

					// 添加列表图片判断 add by ligj time:2014/11/12 18:16:34
					if (StringUtils.isNotEmpty(subList.get(i).getSkuImgReplace())) {
						PicInfo pic = productService.getPicInfo(Integer.valueOf(inputParam.getPicWidth()),subList.get(i).getSkuImgReplace());
						flashSale.setPhotoUrl(pic.getPicNewUrl());
					}

					// 获取系统时间
					flashSale.setSystemTime(DateUtil.getSysDateTimeString());
					flashSale.setEndTime(subList.get(i).getEndTime()); // 结束时间
					result.getProducts().add(flashSale);

				}
			}

		}
		return result;
	}
}
