package com.cmall.groupcenter.job;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.jsoup.helper.StringUtil;
import org.quartz.JobExecutionContext;
import com.cmall.productcenter.common.DateUtil;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.load.LoadWebTemplete;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuInfo;
import com.srnpr.xmassystem.modelwebtemplete.PlusModelWebTempleteQuery;
import com.srnpr.xmassystem.modelwebtemplete.WebCommodity;
import com.srnpr.xmassystem.modelwebtemplete.WebTemplete;
import com.srnpr.xmassystem.modelwebtemplete.WebTempletePage;
import com.srnpr.xmassystem.support.PlusSupportProduct;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.xmassystem.very.ImageCompressRun;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时刷新商品所属的专题缓存数据
 * 
 * @remark
 * @author zhangbo
 * @date 2019年4月16日
 */
public class JobForRefreshTemplateProData extends RootJob {

	private final int VALID_DAY = -30;
	private static ExecutorService service;
	
	public void doExecute(JobExecutionContext context) {

		String v = WebHelper.addLock(15*60, "JobForRefreshTemplateProData");
		
		if (StringUtils.isNotEmpty(v)) {
			// 1.查询商品下架记录表
			List<Map<String, Object>> xiaJiaProList = DbUp.upTable("pc_product_xiajia_recording_task").dataSqlList("select product_code from pc_product_xiajia_recording_task where is_refresh='0'",null);
			// List<Map<String, Object>>  xiaJiaProList =  DbUp.upTable("pc_productinfo").dataSqlList("select product_code from pc_productinfo where product_status='4497153900060003'", null);

			if(xiaJiaProList!=null&&xiaJiaProList.size()>0) {
				// 维护近30天内创建的专题
				String createTime = DateUtil.getTimeCompareSomeDay(VALID_DAY);
				String sql = "select * from fh_data_page where create_time >= '" + createTime + "'";
				List<Map<String, Object>> validDataPageList = DbUp.upTable("fh_data_page").dataSqlList(sql, null);

				List<String> xJProList = new ArrayList<>();
				if (xiaJiaProList != null && xiaJiaProList.size() > 0) {
					for (Map map : xiaJiaProList) {
						xJProList.add(map.get("product_code").toString());
					}
				}

				// 记录下架商品关联的模板编号
				HashSet<String> templeteSet = new HashSet<>();
				// 2.查询下架商品所属的专题
				if (validDataPageList != null && validDataPageList.size() > 0) {
					for (Map<String, Object> map : validDataPageList) {
						if (!templeteSet.contains(map.get("page_number").toString())
								&& queryDataPageForPro(map.get("page_number").toString(), xJProList)) {
							templeteSet.add(map.get("page_number").toString());
							
						} else {
							continue;
						}
					}
				}

				// 3.刷新所对应的专题数据
				if (templeteSet.size() > 0) {
					this.refreshTemplete(templeteSet);
				//4.更新商品的刷新状态
					StringBuffer sb = new StringBuffer();
					for (String st : xJProList) {
						sb.append("'"+st+"',");
					}
					
					String sSql = "update pc_product_xiajia_recording_task set is_refresh='1' where is_refresh='0' and product_code in ("+sb.substring(0,sb.length()-1).toString()+")";
					DbUp.upTable("pc_product_xiajia_recording_task").dataExec(sSql, null);
				}
			}
	
			// 解鎖
			WebHelper.unLock(v);
		}

	}

	
	
	private void refreshTemplete(HashSet<String> templeteSet) {
		// TODO Auto-generated method stub
		for (String page_num : templeteSet) {
			if (!StringUtil.isBlank(page_num)) {
				MDataMap one = DbUp.upTable("fh_data_page").one("page_number", page_num);
				if (null != one) {
					// 删除专题模板缓存
					XmasKv.upFactory(EKvSchema.WebTemplateCode).del(page_num+"-449747430001");
					XmasKv.upFactory(EKvSchema.WebTemplateCode).del(page_num+"-449747430003");
					XmasKv.upFactory(EKvSchema.WebTemplateCode).del(page_num+"-449747430023");
				}
			}

			/*
			 * 将模板下的图片进行压缩
			 */
			PlusModelWebTempleteQuery tQuery = new PlusModelWebTempleteQuery();
			tQuery.setCode(page_num+"-449747430001");
			WebTempletePage upInfoByCode = new LoadWebTemplete().upInfoByCode(tQuery);
			List<String> picUrlArr = new ArrayList<String>();// 压缩专题广告之类图片
			List<String> pcUrlArr = new ArrayList<String>();// 压缩商品主图

			// 记录需要压缩的图片
			addCompressImage(upInfoByCode.getTempleteList(), picUrlArr, pcUrlArr);
			// 添加关联模板需要压缩的图片
			for (WebTemplete webTemplete : upInfoByCode.getTempleteList()) {
				for (WebCommodity webCommodity : webTemplete.getCommodList()) {
					if (null != webCommodity.getRel_templete() && webCommodity.getRel_templete().size() > 0) {
						addCompressImage(webCommodity.getRel_templete(), picUrlArr, pcUrlArr);
					}
				}
			}

			/*
			 * 进行图片压缩
			 */
			String imageWidthStr = bConfig("productcenter.imageWidth");
			String[] imageWidthArr = imageWidthStr.split(",");

			if (imageWidthArr.length > 0 && picUrlArr.size() > 0) {// 压缩专题之类图片

				int threadSize = initThreadPool(picUrlArr.size());
				for (String width : imageWidthArr) {
					ImageCompressRun runModel = new ImageCompressRun(width + "webTemplete",
							StringUtils.join(picUrlArr, "|"), Integer.valueOf(width), "");
					service.execute(runModel);
				}

				// 等待所有线程执行完毕退出释放锁
				service.shutdown();
				try {
					boolean loop = true;
					do {
						loop = !service.awaitTermination(2, TimeUnit.SECONDS);
					} while (loop);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

			if (imageWidthArr.length > 0 && pcUrlArr.size() > 0) {// 压缩商品主图

				int threadSize = initThreadPool(pcUrlArr.size());
				for (String width : imageWidthArr) {
					ImageCompressRun runModel = new ImageCompressRun(width + "webTemplete",
							StringUtils.join(pcUrlArr, "|"),
							BigDecimal.valueOf(Integer.valueOf(width) * 0.6).setScale(0).intValue(), "");
					service.execute(runModel);
				}

				// 等待所有线程执行完毕退出释放锁
				service.shutdown();
				try {
					boolean loop = true;
					do {
						loop = !service.awaitTermination(2, TimeUnit.SECONDS);
					} while (loop);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}

	}

	public int initThreadPool(int listSize) {
		service = Executors.newFixedThreadPool(listSize);
		return listSize;
	}

	/**
	 * 添加图片链接进行压缩，过滤空链接
	 */
	private void listAddEle(List<String> picArr, String ele) {
		if (StringUtils.isNotBlank(ele) && !picArr.contains(ele)) {
			picArr.add(ele);
		}
	}

	/**
	 * 添加需要压缩的图片
	 */
	private void addCompressImage(List<WebTemplete> templeteList, List<String> picUrlArr, List<String> pcUrlArr) {
		for (WebTemplete webTemplete : templeteList) {
			listAddEle(picUrlArr, webTemplete.getCommodity_buy_picture());
			listAddEle(picUrlArr, webTemplete.getCommodity_picture());
			listAddEle(picUrlArr, webTemplete.getCommodity_text_pic());
			for (WebCommodity webCommodity : webTemplete.getCommodList()) {
				listAddEle(picUrlArr, webCommodity.getCommodity_picture());
				listAddEle(picUrlArr, webCommodity.getPrograma_picture());
				listAddEle(picUrlArr, webCommodity.getImg());
				if (StringUtils.isNotBlank(webCommodity.getCommodity_number())) {
					// 商品主图进行压缩
					PlusModelSkuInfo plusModelSkuInfo = new PlusSupportProduct()
							.upSkuInfoBySkuCode(webCommodity.getCommodity_number());
					listAddEle(pcUrlArr, plusModelSkuInfo.getProductPicUrl());

				}
			}
		}
	}

	// 查询下架商品所属的专题模板
	private boolean queryDataPageForPro(String pageNumbert, List<String> xJProList) {
		// TODO Auto-generated method stub

		List<Map<String, Object>> resultList = DbUp.upTable("fh_page_template").dataSqlList(
				"select template_number from fh_page_template where dal_status='1001' and page_number=:page_number",
				new MDataMap("page_number", pageNumbert));
		if (resultList != null && resultList.size() > 0) {
			String now = FormatHelper.upDateTime();
			String sql = "select * from fh_data_commodity where start_time<'" + now + "' and end_time>'" + now
					+ "' and template_number=:template_number ";
			for (Map<String, Object> subMap : resultList) {
				List<Map<String, Object>> commodityList = DbUp.upTable("fh_data_commodity").dataSqlList(sql,
						new MDataMap("template_number", subMap.get("template_number").toString()));
				if (commodityList != null && commodityList.size() > 0) {
					for (Map<String, Object> map2 : commodityList) {
						/**
						 * skip(商品打开方式）：
						 *  449747550001：URL
						 *  449747550002：商品
						 *  449747550003：关键词
						 *  449747550004：商品分类 449747550005：主播商店 --这个无商品关联配置，不需要考虑
						 * 
						 */
						if ("449747550002".equals(map2.get("skip").toString())
								&& StringUtils.isNotBlank(map2.get("good_number").toString())
								&& xJProList.contains(map2.get("good_number").toString())) {
							return true;
						} else if (("449747550001".equals(map2.get("skip").toString())
								|| "449747550003".equals(map2.get("skip").toString()))
								&& StringUtils.isNotBlank(map2.get("skip_input").toString())) {
							Pattern p = Pattern.compile("(pageCode=ZT[0-9]*)");
							Matcher m = p.matcher(map2.get("skip_input").toString());
                           
							if(m.find()) {
								Map<String, Object> map = DbUp.upTable("sc_special").dataSqlOne(
										"select enevt_code from sc_special where state='449747350001' and img_url like '%"
												+ m.group(1) + "%'",
										null);

								if (map != null) {
									String[] array = StringUtils.split(map.get("enevt_code").toString(), ',');
									if (array != null && array.length > 0) {
										for (String eCode : array) {
											Map<String, Object> em = DbUp.upTable("sc_event_info").dataSqlOne(
													"select event_type_code from sc_event_info where event_code=:event_code",
													new MDataMap("event_code", eCode));
											/**
											 * 活动类型： 4497472600010001：秒杀 4497472600010002：特价 4497472600010003：拍卖
											 * 4497472600010004：扫码购-微信 4497472600010005：闪购 4497472600010006：内购
											 * 4497472600010007：TV专场 4497472600010008：满减 4497472600010010：超值组合
											 * 4497472600010012：扫码渠道 4497472600010013：运费减免 4497472600010014：会员日
											 * 4497472600010015：扫码购-APP 4497472600010016：拼好货 4497472600010017：扫码差价
											 * 4497472600010018：会员日折扣 4497472600010019：小程序-闪购 4497472600010020：会员专享
											 * 4497472600010021：在线支付立减 4497472600010024：拼团
											 */
											if ("4497472600010008".equals(em.get("event_type_code"))) {

												List<Map<String, Object>> sm = DbUp.upTable("sc_full_cut_product")
														.dataSqlList(
																"select product_code from sc_full_cut_product where event_code=:event_code",
																new MDataMap("event_code", eCode));
												for (Map<String, Object> ssm : sm) {
													if (xJProList.contains(ssm.get("product_code").toString())) {
														return true;
													}
												}
											} else {
												List<Map<String, Object>> sm = DbUp.upTable("sc_event_item_product")
														.dataSqlList(
																"select product_code from sc_event_item_product where event_code=:event_code",
																new MDataMap("event_code", eCode));
												for (Map<String, Object> ssm : sm) {
													if (xJProList.contains(ssm.get("product_code").toString())) {
														return true;
													}
												}

											}

										}
									}
								}
							}


						} else if ("449747550004".equals(map2.get("skip").toString())
								&& StringUtils.isNotBlank(map2.get("skip_input").toString())) {

							String[] arr = map2.get("skip_input").toString().split("->");

							String cateName = arr[arr.length - 1];

							List<Map<String, Object>> list = DbUp.upTable("uc_sellercategory").dataSqlList(
									"select category_code from uc_sellercategory where category_name=:category_name ",
									new MDataMap("category_name", cateName));
							for (Map<String, Object> map : list) {
								for (String proCode : xJProList) {
									int count = DbUp.upTable("uc_sellercategory_product_relation").count("product_code",
											proCode, "category_code", map.get("category_code").toString());
									if (count > 0)
										return true;
								}

							}
						}
					}

				}

			}
		}

		return false;
	}
}
