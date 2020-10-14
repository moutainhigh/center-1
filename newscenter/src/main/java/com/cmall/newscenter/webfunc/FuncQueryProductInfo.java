package com.cmall.newscenter.webfunc;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.membercenter.model.MemberInfo;
import com.cmall.newscenter.model.CommentdityApp;
import com.cmall.newscenter.model.CommentdityAppPhotos;
import com.cmall.newscenter.model.Productinfo;
import com.cmall.newscenter.model.Sale_Product;
import com.cmall.newscenter.model.Trial_product;
import com.cmall.ordercenter.model.Order;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.PcProductpic;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.cmall.systemcenter.model.AppPhoto;
import com.cmall.systemcenter.service.StoreService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;

/**
 * 在售商品及试用商品查询
 * 
 * @author dyc
 * */
public class FuncQueryProductInfo extends BaseClass {

	/**
	 * 根据sku信息和userCode关联在售商品信息
	 * 
	 * @param productSkuInfoList
	 *            sku信息集合
	 * @param userCode
	 *            用户code
	 * 
	 * @return 在售商品类
	 * */
	public List<Sale_Product> queryProductInSale(
			List<PcProductinfo> productSkuInfoList, String userCode,
			String appCode) {
		List<Sale_Product> list = new ArrayList<Sale_Product>();
		List<String> productCodes = new ArrayList<String>();
		for (PcProductinfo p : productSkuInfoList) {

			/* 获取SKU编号，用于查询评论信息 */
			for (ProductSkuInfo sku : p.getProductSkuInfoList()) {

				productCodes.add(sku.getSkuCode());
			}

		}

		StoreService storeService = new StoreService();

		List<MDataMap> collProList = new ArrayList<MDataMap>();
		List<MDataMap> cpnList = new ArrayList<MDataMap>();
		List<MDataMap> commList1 = new ArrayList<MDataMap>();
		List<MDataMap> userData = new ArrayList<MDataMap>();

		// 查询此用户下收藏商品信息
		collProList = DbUp.upTable("nc_num").queryAll("", "",
				"member_code='" + userCode + "'", new MDataMap());
		if (!productCodes.isEmpty()) {
			/* 查询商品统计表有多少人收藏过 */
			cpnList = DbUp.upTable("nc_productfav").queryIn("", "", "",
					new MDataMap(), -1, -1, "product_code",
					StringUtils.join(productCodes, WebConst.CONST_SPLIT_COMMA));

			// 查出评论信息
			List<MDataMap> commListTmp = DbUp.upTable("nc_order_evaluation")
					.queryIn(
							"",
							"-oder_creattime",
							"flag_show='449746530001' and check_flag = '4497172100030002'",
							new MDataMap(),
							-1,
							-1,
							"order_skuid",
							StringUtils.join(productCodes,
									WebConst.CONST_SPLIT_COMMA));
			for (MDataMap c : commListTmp) {
				if (c.get("manage_code").equals(appCode)) {
					String flag_show = c.get("flag_show");
					String check_flag = c.get("check_flag");
					if (!StringUtils.isEmpty(flag_show)
							&& !StringUtils.isEmpty(check_flag)) {
						if (flag_show.equals("449746530001")
								&& check_flag.equals("4497172100030002")) {
							commList1.add(c);
						}
					}
				}
			}
		}

		List<String> members = new ArrayList<String>();
		for (MDataMap m : commList1) {
			members.add(m.get("order_name"));
		}
		if (!members.isEmpty()) {
			// 查出用户(评论人)信息
			userData = DbUp.upTable("mc_extend_info_star").queryIn("", "", "",
					new MDataMap(), -1, -1, "member_code",
					StringUtils.join(members, WebConst.CONST_SPLIT_COMMA));
		}

		// 查询等级对应的中文名
		List<MDataMap> levelMapList = DbUp.upTable("mc_member_level")
				.queryByWhere("manage_code", appCode);
		MDataMap levelMap = new MDataMap();

		for (MDataMap m : levelMapList) {
			levelMap.put(m.get("level_code"), m.get("level_name"));
		}

		for (PcProductinfo p : productSkuInfoList) {

			Sale_Product info = new Sale_Product();

			/* sku编号 */
			if (p.getProductSkuInfoList().size() != 0) {

				info.setId(p.getProductSkuInfoList().get(0).getSkuCode());

				/* 介绍文字 */
				info.setIntro(""); // 介绍文字--广告语

				/* 标题--sku名称 */
				info.setTitle(p.getProductSkuInfoList().get(0).getSkuName());

				/* 库存量 */
				info.setRepo_count(storeService.getStockNumByStore(p
						.getProductSkuInfoList().get(0).getSkuCode()));

				/* 售价 */
				info.setSale_price(p.getProductSkuInfoList().get(0)
						.getSellPrice().doubleValue());

				/* 原价 */
				info.setOrig_price(p.getMarketPrice().doubleValue());

				//销售量
				info.setSale_count(new ProductService().getSellCount(p.getProductSkuInfoList().get(0).getSkuCode()));
				 
				// 图片信息
				List<CommentdityAppPhotos> photoList = new ArrayList<CommentdityAppPhotos>();

				List<PcProductpic> pcPicList = new ArrayList<PcProductpic>();

				pcPicList = p.getPcPicList();

				if (pcPicList.size() != 0) {
					for (int i = 0; i < pcPicList.size(); i++) {

						String large = pcPicList.get(i).getPicUrl();
						CommentdityAppPhotos photo = new CommentdityAppPhotos();

						photo.setLarge(large);
						photo.setThumb(large);

						photoList.add(photo);
					}
				}

				info.setPhotos(photoList);

			}

			info.setProduct_id(p.getProductCode());

			/* 推荐理由 */
			info.setReason(p.getProductSkuInfoList().get(0).getSkuAdv());

			ProductDetails productDetails = new ProductDetails();

			String param_url = "";
			/* 传入商品名称，分类，功效，退货政策，产品规格，原产国家，保质期限，适合肤质，温馨提示，特别说明，产品包装 */
			param_url = productDetails.getDetailUrl(p.getProdutName(), p
					.getCategory().getCategoryName(), p.getProductVolume(), p
					.getPcProductpropertyList());

			// 产品参数
			info.setParam_url(param_url);

			String productDetail = "";

			/* 传入图文详情， */
			productDetail = productDetails.getProductUrl(p.getDescription());

			/* 图文详情 */
			info.setDetail_url(productDetail);

			/* 分享链接 */
			info.setLinkUrl(bConfig("newscenter.shareLink")+"/capp/web/introduction/productDetail?product_code="+p.getProductCode());
			// 关联商品收藏信息
			for (MDataMap collPro : collProList) {
				if (p.getProductSkuInfoList().get(0).getSkuCode()
						.equals(collPro.get("num_code"))) {
					/* 统计商品是否被收藏过 */
					if (collPro.get("flag_enable").equals("")) {
						info.setFaved(0);
					} else {
						info.setFaved(Integer.valueOf(collPro
								.get("flag_enable")));
					}
					break;
				}
			}
			/* 关联商品统计表有多少人收藏过 */
			for (MDataMap cpn : cpnList) {
				if (p.getProductSkuInfoList().get(0).getSkuCode()
						.equals(cpn.get("product_code"))) {
					info.setFav_count(Integer.valueOf(cpn.get("num_fav")));
					break;
				}
			}

			// 关联评论信息
			for (MDataMap com1 : commList1) {
				if (p.getProductSkuInfoList().get(0).getSkuCode()
						.equals(com1.get("order_skuid"))) {
					CommentdityApp comment = new CommentdityApp();
					comment.setId(com1.get("order_skuid"));
					comment.setText(com1.get("order_assessment"));
					comment.setCreated_at(com1.get("oder_creattime"));
					// 图片信息
					List<CommentdityAppPhotos> commPhotoList = new ArrayList<CommentdityAppPhotos>();
					String[] commPhotos = com1.get("oder_photos").split("\\|");
					for (String s : commPhotos) {
						CommentdityAppPhotos photo = new CommentdityAppPhotos();
						photo.setLarge(s);
						photo.setThumb(s);
						commPhotoList.add(photo);
					}
					comment.setPhotos(commPhotoList);
					for (MDataMap user : userData) {// 根据查出来的评论信息匹配评论人信息
						if (com1.get("order_name").equals(
								user.get("member_code"))) {
							// 用户(评论人)信息
							comment.getUser().setMember_code(
									user.get("member_code"));
							comment.getUser().setNickname(user.get("nickname"));
							comment.getUser().setGroup(
									BigInteger.valueOf(Long.valueOf(user
											.get("member_group"))));
							comment.getUser().setGender(
									BigInteger.valueOf(Long.valueOf(user
											.get("member_sex"))));
							comment.getUser().setScore(
									Integer.parseInt(user.get("member_score")));
							comment.getUser().setLevel(
									Integer.valueOf(user.get("member_level")
											.substring(
													user.get("member_level")
															.length() - 4,
													user.get("member_level")
															.length())));
							comment.getUser().setLevel_name(
									levelMap.get(user.get("member_level")));
							comment.getUser().setCreate_time(
									user.get("create_time"));
							comment.getUser().setMobile(
									user.get("mobile_phone"));
							comment.getUser().setScore_unit(
									bConfig("newscenter.Score_unit"));
							
							comment.getUser().getAvatar().setLarge(String.valueOf(user.get("member_avatar")));
							
							comment.getUser().getAvatar().setThumb(String.valueOf(user.get("member_avatar")));
							
							
						}
					}
					info.getComments().add(comment);
				}
			}

			list.add(info);
		}

		return list;
	}

	/**
	 * 根据sku信息和userCode关联试用商品信息
	 * 
	 * @param productSkuInfoList
	 *            sku信息集合
	 * @param appCode
	 * 
	 * @return 试用商品类
	 * */
	public List<Trial_product> queryProductInTry(
			List<PcProductinfo> productSkuInfoList, String appCode) {
		List<Trial_product> list = new ArrayList<Trial_product>();
		List<String> productCodes = new ArrayList<String>();
		for (PcProductinfo p : productSkuInfoList) {
			/* 获取SKU编号，用于查询评论信息 */
			for (ProductSkuInfo sku : p.getProductSkuInfoList()) {

				productCodes.add(sku.getSkuCode());
			}

		}

		List<MDataMap> commList1 = new ArrayList<MDataMap>();
		List<MDataMap> userData = new ArrayList<MDataMap>();

		MDataMap mDataMap = new MDataMap();
		
		mDataMap.put("app_code", appCode);
		
		// 查询此app下试用商品信息
		List<MDataMap> tryProList = DbUp.upTable("oc_tryout_products")
				.queryAll("", "-end_time", "app_code=:app_code", mDataMap);
		if (!productCodes.isEmpty()) {
			// 查出评论信息
			List<MDataMap> commListTmp = DbUp.upTable("nc_order_evaluation")
					.queryIn(
							"",
							"-oder_creattime",
							"flag_show='449746530001' and check_flag = '4497172100030002'",
							new MDataMap(),
							-1,
							-1,
							"order_skuid",
							StringUtils.join(productCodes,
									WebConst.CONST_SPLIT_COMMA));
			for (MDataMap c : commListTmp) {
				if (c.get("manage_code").equals(appCode)) {
					String flag_show = c.get("flag_show");
					String check_flag = c.get("check_flag");
					if (!StringUtils.isEmpty(flag_show)
							&& !StringUtils.isEmpty(check_flag)) {
						if (flag_show.equals("449746530001")
								&& check_flag.equals("4497172100030002")) {
							commList1.add(c);
						}
					}
				}
			}
		}

		List<String> members = new ArrayList<String>();
		for (MDataMap m : commList1) {
			members.add(m.get("order_name"));
		}
		if (!members.isEmpty()) {
			// 查出用户(评论人)信息
			userData = DbUp.upTable("mc_extend_info_star").queryIn("", "", "",
					new MDataMap(), -1, -1, "member_code",
					StringUtils.join(members, WebConst.CONST_SPLIT_COMMA));
		}

		// 查询等级对应的中文名
		List<MDataMap> levelMapList = DbUp.upTable("mc_member_level")
				.queryByWhere("manage_code", appCode);
		MDataMap levelMap = new MDataMap();
		for (MDataMap m : levelMapList) {
			levelMap.put(m.get("level_code"), m.get("level_name"));
		}
		for (PcProductinfo p : productSkuInfoList) {
			Trial_product trialproduct = new Trial_product();
			/* sku编号 */
			if (p.getProductSkuInfoList().size() != 0) {

				trialproduct.setId(p.getProductSkuInfoList().get(0)
						.getSkuCode());

				/* 介绍文字 */
				trialproduct.setIntro(""); // 介绍文字--广告语

				/* 标题--sku名称 */
				trialproduct.setTitle(p.getProductSkuInfoList().get(0)
						.getSkuName());

				// 图片信息
				List<CommentdityAppPhotos> photoList = new ArrayList<CommentdityAppPhotos>();

				List<PcProductpic> pcPicList = new ArrayList<PcProductpic>();

				pcPicList = p.getPcPicList();

				if (pcPicList.size() != 0) {
					for (int i = 0; i < pcPicList.size(); i++) {

						String large = pcPicList.get(i).getPicUrl();
						CommentdityAppPhotos photo = new CommentdityAppPhotos();

						photo.setLarge(large);
						photo.setThumb(large);

						photoList.add(photo);
					}
				}

				trialproduct.setPhotos(photoList);

				// 关联试用信息字段
				for (MDataMap m : tryProList) {
					if (p.getProductSkuInfoList().get(0).getSkuCode()
							.equals(m.get("sku_code"))) {
						trialproduct.setTrial_expires(m.get("end_time")); // 過期時間
						Integer a = Integer.valueOf(m.get("init_inventory"));/* 初始库存 */
						Integer b = Integer.valueOf(m.get("tryout_inventory"));/* 试用库存 */
						trialproduct.setApply_count(Integer.valueOf(String
								.valueOf(a - b))); // 已申请数量
						trialproduct.setSuccess_count(Integer.valueOf(String
								.valueOf(a - b))); // 申请成功数量
						trialproduct.setTrial_price(Double.valueOf(m
								.get("tryout_price"))); // 试用价
						trialproduct.setRepo_count(Integer.valueOf(m
								.get("tryout_inventory"))); // 试用库存
						 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						 	try {
								Date date = dateFormat.parse(m.get("end_time"));
								Long end = (long) date.getTime();
								Long now = (long) new Date().getTime();
								if(end>now){
									Long surTime = (long) ((end - now))/1000;
									trialproduct.setSurplusTime(String.valueOf(Integer.valueOf(surTime.toString())));
								}else{
									trialproduct.setSurplusTime("0");
								}
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						break;
					}
				}

			}
			/*分享链接*/
			trialproduct.setLinkUrl(bConfig("newscenter.shareLink")+"/capp/web/introduction/tryProductDetail.ftl?skuId="+p.getProductSkuInfoList().get(0)
					.getSkuCode());

			/* 推荐理由 */
			trialproduct
					.setReason(p.getProductSkuInfoList().get(0).getSkuAdv());

			ProductDetails productDetails = new ProductDetails();

			String param_url = "";
			/* 传入商品名称，分类，功效，退货政策，产品规格，原产国家，保质期限，适合肤质，温馨提示，特别说明，产品包装 */
			param_url = productDetails.getDetailUrl(p.getProdutName(), p
					.getCategory().getCategoryName(), p.getProductVolume(), p
					.getPcProductpropertyList());

			// 产品参数
			trialproduct.setParam_url(param_url);

			String productDetail = "";

			/* 传入图文详情， */
			productDetail = productDetails.getProductUrl(p.getDescription());

			/* 图文详情 */
			trialproduct.setDetail_url(productDetail);

			// 关联评论信息
			for (MDataMap com1 : commList1) {
				if (p.getProductSkuInfoList().get(0).getSkuCode()
						.equals(com1.get("order_skuid"))) {
					CommentdityApp comments = new CommentdityApp();
					comments.setId(com1.get("order_skuid"));
					comments.setText(com1.get("order_assessment"));
					comments.setCreated_at(com1.get("oder_creattime"));
					// 图片信息
					List<CommentdityAppPhotos> commPhotoList = new ArrayList<CommentdityAppPhotos>();
					String[] commPhotos = com1.get("oder_photos").split("\\|");
					for (String s : commPhotos) {
						CommentdityAppPhotos photo = new CommentdityAppPhotos();
						photo.setLarge(s);
						photo.setThumb(s);
						commPhotoList.add(photo);
					}
					comments.setPhotos(commPhotoList);
					for (MDataMap user : userData) {// 根据查出来的评论信息匹配评论人信息
						if (com1.get("order_name").equals(
								user.get("member_code"))) {
							// 用户(评论人)信息
							comments.getUser().setMember_code(
									user.get("member_code"));
							comments.getUser()
									.setNickname(user.get("nickname"));
							comments.getUser().setGroup(
									BigInteger.valueOf(Long.valueOf(user
											.get("member_group"))));
							comments.getUser().setGender(
									BigInteger.valueOf(Long.valueOf(user
											.get("member_sex"))));
							comments.getUser().setScore(
									Integer.parseInt(user.get("member_score")));
							comments.getUser().setLevel(
									Integer.valueOf(user.get("member_level")
											.substring(
													user.get("member_level")
															.length() - 4,
													user.get("member_level")
															.length())));
							comments.getUser().setLevel_name(
									levelMap.get(user.get("member_level")));
							comments.getUser().setCreate_time(
									user.get("create_time"));
							comments.getUser().setMobile(
									user.get("mobile_phone"));
							comments.getUser().setScore_unit(
									bConfig("newscenter.Score_unit"));
							
							comments.getUser().getAvatar().setLarge(String.valueOf(user.get("member_avatar")));
							
							comments.getUser().getAvatar().setThumb(String.valueOf(user.get("member_avatar")));
						}
					}
					trialproduct.getComments().add(comments);
				}
			}
			list.add(trialproduct);
		}
		return list;
	}

	/**
	 * 根据skuCode和userCode查询在售商品信息
	 * 
	 * @param skuCode
	 * @param userCode
	 * 
	 * @return 在售商品类
	 * */
	public List<Sale_Product> qryProInSaleService(String skuCode,
			String productCode, String userCode, String appCode) {
		List<Sale_Product> list = new ArrayList<Sale_Product>();
		ProductService productService = new ProductService();
		List<PcProductinfo> productSkuInfoList = new ArrayList<PcProductinfo>();
		PcProductinfo productSkuInfo = productService.getskuinfo(skuCode,
				productCode);
		if (productSkuInfo != null) {
			productSkuInfoList.add(productSkuInfo);
			list = queryProductInSale(productSkuInfoList, userCode, appCode);
		}
		return list;
	}

	/**
	 * 根据skuCode查询试用商品信息
	 * 
	 * @param skuCode
	 * @param appCode
	 * @return 试用商品类
	 * */
	public List<Trial_product> qryProInTryService(String skuCode,
			String productCode, String appCode) {
		List<Trial_product> list = new ArrayList<Trial_product>();
		ProductService productService = new ProductService();
		List<PcProductinfo> productSkuInfoList = new ArrayList<PcProductinfo>();
		PcProductinfo productSkuInfo = productService.getskuinfo(skuCode,
				productCode);
		if (productSkuInfo != null) {
			productSkuInfoList.add(productSkuInfo);
			list = queryProductInTry(productSkuInfoList, appCode);
		}
		return list;
	}

	/**
	 * 根据订单号，skuCode查询试用商品信息
	 * 
	 * @param skuCode
	 * @param appCode
	 * @return dingDan试用商品类
	 * */
	public List<Trial_product> qryOrderProInTryService(String skuCode,
			String productCode, String appCode, String orderCode,Order order) {
		List<Trial_product> list = new ArrayList<Trial_product>();
		ProductService productService = new ProductService();
		List<PcProductinfo> productSkuInfoList = new ArrayList<PcProductinfo>();
		PcProductinfo productSkuInfo = productService.getskuinfo(skuCode,
				productCode);
		if (productSkuInfo != null) {
			productSkuInfoList.add(productSkuInfo);
			list = queryOrderProductInTry(productSkuInfoList, appCode,
					orderCode,order);
		}
		return list;
	}

	/**
	 * 根据订单编号，sku信息和userCode关联试用商品信息
	 * 
	 * @param productSkuInfoList
	 *            sku信息集合
	 * @param appCode
	 * 
	 * @return 订单试用商品类
	 * */
	public List<Trial_product> queryOrderProductInTry(
			List<PcProductinfo> productSkuInfoList, String appCode,
			String orderCode,Order order) {
		List<Trial_product> list = new ArrayList<Trial_product>();
		List<String> productCodes = new ArrayList<String>();
		for (PcProductinfo p : productSkuInfoList) {
			/* 获取SKU编号，用于查询评论信息 */
			for (ProductSkuInfo sku : p.getProductSkuInfoList()) {

				productCodes.add("'" + sku.getSkuCode() + "'");
			}

		}

		List<MDataMap> commList1 = new ArrayList<MDataMap>();
		List<MDataMap> userData = new ArrayList<MDataMap>();

		// 查询此app下试用商品信息
		List<MDataMap> tryProList = DbUp.upTable("oc_tryout_products")
				.queryByWhere("app_code", appCode);
		if (!productCodes.isEmpty()) {
			// 查出评论信息
			String whereSql = "flag_show ='449746530001' and check_flag = '4497172100030002' and order_code ='"
					+ orderCode
					+ "' and order_skuid in("
					+ StringUtils
							.join(productCodes, WebConst.CONST_SPLIT_COMMA)
					+ ")  ORDER BY oder_creattime DESC ";
			List<MDataMap> commListTmp = new ArrayList<MDataMap>();
			commListTmp = DbUp.upTable("nc_order_evaluation").queryAll("", "",
					whereSql, new MDataMap());
			for (MDataMap c : commListTmp) {
				if (c.get("manage_code").equals(appCode)) {
					commList1.add(c);
				}
			}
		}

		List<String> members = new ArrayList<String>();
		for (MDataMap m : commList1) {
			members.add(m.get("order_name"));
		}
		if (!members.isEmpty()) {
			// 查出用户(评论人)信息
			userData = DbUp.upTable("mc_extend_info_star").queryIn("", "", "",
					new MDataMap(), -1, -1, "member_code",
					StringUtils.join(members, WebConst.CONST_SPLIT_COMMA));
		}

		// 查询等级对应的中文名
		List<MDataMap> levelMapList = DbUp.upTable("mc_member_level")
				.queryByWhere("manage_code", appCode);
		MDataMap levelMap = new MDataMap();
		for (MDataMap m : levelMapList) {
			levelMap.put(m.get("level_code"), m.get("level_name"));
		}
		for (PcProductinfo p : productSkuInfoList) {
			Trial_product trialproduct = new Trial_product();
			/* sku编号 */
			if (p.getProductSkuInfoList().size() != 0) {

				trialproduct.setId(p.getProductSkuInfoList().get(0)
						.getSkuCode());

				/* 介绍文字 */
				trialproduct.setIntro(""); // 介绍文字--广告语

				/* 标题--sku名称 */
				trialproduct.setTitle(p.getProductSkuInfoList().get(0)
						.getSkuName());

				// 图片信息
				List<CommentdityAppPhotos> photoList = new ArrayList<CommentdityAppPhotos>();

				List<PcProductpic> pcPicList = new ArrayList<PcProductpic>();

				pcPicList = p.getPcPicList();

				if (pcPicList.size() != 0) {
					for (int i = 0; i < pcPicList.size(); i++) {

						String large = pcPicList.get(i).getPicUrl();
						CommentdityAppPhotos photo = new CommentdityAppPhotos();

						photo.setLarge(large);
						photo.setThumb(large);

						photoList.add(photo);
					}
				}

				trialproduct.setPhotos(photoList);

				// 关联试用信息字段
				for (MDataMap m : tryProList) {
					if (p.getProductSkuInfoList().get(0).getSkuCode()
							.equals(m.get("sku_code"))) {
						
						MDataMap activityCodeMap = DbUp.upTable("oc_order_activity").oneWhere("","","","order_code",orderCode);
						
						String activityCode = "";
						
						if (null != activityCodeMap) {
							activityCode = activityCodeMap.get("activity_code");
							
						}
						
						
						//商品申请人数
						String sqlApplyNum = "select COUNT(oi.buyer_code) as applyNum from oc_order_activity oa,oc_orderinfo oi,oc_orderdetail od" +
													" where oa.order_code = oi.order_code and od.order_code = oi.order_code  and oi.order_type='449715200003' and oi.order_status != '4497153900010006'  "+
													" and oa.activity_code='"+activityCode+"' and od.sku_code = '"+p.getProductSkuInfoList().get(0).getSkuCode()+"' and oa.activity_type='449715400005' group by od.sku_code ";
						
						Map<String, Object> mapObj = DbUp.upTable("oc_orderdetail").dataSqlOne(sqlApplyNum,null);
						int applyNum = 0;
						if (null != mapObj && null != mapObj.get("applyNum")) {
							applyNum = Integer.parseInt(String.valueOf(mapObj.get("applyNum")));
						}
						
						//试用商品活动信息
						String whereStr = " sku_code = '"+p.getProductSkuInfoList().get(0).getSkuCode()+"' and app_code = '"+appCode+"' and is_freeShipping = '449746930001' and start_time <= '"+order.getCreateTime()+"' and end_time >='"+order.getCreateTime()+"' ";
						MDataMap activityProduct = DbUp.upTable("oc_tryout_products").oneWhere("","",whereStr);
						if (null != activityProduct && !activityProduct.isEmpty()) {
						trialproduct.setTrial_expires(activityProduct.get("end_time")); // 過期時間
						trialproduct.setApply_count(applyNum); // 已申请数量
						trialproduct.setSuccess_count(applyNum); // 申请成功数量
						trialproduct.setTrial_price(Double.valueOf(activityProduct.get("tryout_price"))); // 试用价
						trialproduct.setRepo_count(Integer.valueOf(activityProduct
								.get("tryout_inventory"))); // 试用库存
						}
					}
				}

			}

			/* 推荐理由 */
			trialproduct
					.setReason(p.getProductSkuInfoList().get(0).getSkuAdv());

			ProductDetails productDetails = new ProductDetails();

			String param_url = "";
			/* 传入商品名称，分类，功效，退货政策，产品规格，原产国家，保质期限，适合肤质，温馨提示，特别说明，产品包装 */
			param_url = productDetails.getDetailUrl(p.getProdutName(), p
					.getCategory().getCategoryName(), p.getProductVolume(), p
					.getPcProductpropertyList());

			// 产品参数
			trialproduct.setParam_url(param_url);

			String productDetail = "";

			/* 传入图文详情， */
			productDetail = productDetails.getProductUrl(p.getDescription());

			/* 图文详情 */
			trialproduct.setDetail_url(productDetail);

			// 关联评论信息
			for (MDataMap com1 : commList1) {
				if (p.getProductSkuInfoList().get(0).getSkuCode()
						.equals(com1.get("order_skuid"))) {
					CommentdityApp comments = new CommentdityApp();
					comments.setId(com1.get("order_skuid"));
					comments.setText(com1.get("order_assessment"));
					comments.setCreated_at(com1.get("oder_creattime"));
					// 图片信息
					List<CommentdityAppPhotos> commPhotoList = new ArrayList<CommentdityAppPhotos>();
					String[] commPhotos = com1.get("oder_photos").split("\\|");
					for (String s : commPhotos) {
						CommentdityAppPhotos photo = new CommentdityAppPhotos();
						photo.setLarge(s);
						photo.setThumb(s);
						commPhotoList.add(photo);
					}
					comments.setPhotos(commPhotoList);
					for (MDataMap user : userData) {// 根据查出来的评论信息匹配评论人信息
						if (com1.get("order_name").equals(
								user.get("member_code"))) {
							// 用户(评论人)信息
							comments.getUser().setMember_code(
									user.get("member_code"));
							comments.getUser()
									.setNickname(user.get("nickname"));
							comments.getUser().setGroup(
									BigInteger.valueOf(Long.valueOf(user
											.get("member_group"))));
							comments.getUser().setGender(
									BigInteger.valueOf(Long.valueOf(user
											.get("member_sex"))));
							comments.getUser().setScore(
									Integer.parseInt(user.get("member_score")));
							comments.getUser().setLevel(
									Integer.valueOf(user.get("member_level")
											.substring(
													user.get("member_level")
															.length() - 4,
													user.get("member_level")
															.length())));
							comments.getUser().setLevel_name(
									levelMap.get(user.get("member_level")));
							comments.getUser().setCreate_time(
									user.get("create_time"));
							comments.getUser().setMobile(
									user.get("mobile_phone"));
							comments.getUser().setScore_unit(
									bConfig("newscenter.Score_unit"));
							
							comments.getUser().getAvatar().setLarge(String.valueOf(user.get("member_avatar")));
							
							comments.getUser().getAvatar().setThumb(String.valueOf(user.get("member_avatar")));

							break;
						}
					}
					trialproduct.getComments().add(comments);
					break;
				}
			}
			list.add(trialproduct);
		}
		return list;
	}

	/**
	 * 根据skuCode和userCode查询在售商品信息
	 * 
	 * @param skuCode
	 * @param userCode
	 * 
	 * @return 在售商品类
	 * */
	public List<Sale_Product> qryOrderProInSaleService(String skuCode,
			String productCode, String userCode, String appCode,
			String orderCode) {
		List<Sale_Product> list = new ArrayList<Sale_Product>();
		ProductService productService = new ProductService();
		List<PcProductinfo> productSkuInfoList = new ArrayList<PcProductinfo>();
		PcProductinfo productSkuInfo = productService.getskuinfo(skuCode,
				productCode);
		if (productSkuInfo != null) {
			productSkuInfoList.add(productSkuInfo);
			list = queryOrderProductInSale(productSkuInfoList, userCode,
					appCode, orderCode);
		}
		return list;
	}

	/**
	 * 根据sku信息和userCode关联在售商品信息
	 * 
	 * @param productSkuInfoList
	 *            sku信息集合
	 * @param userCode
	 *            用户code
	 * 
	 * @return 在售商品类
	 * */
	public List<Sale_Product> queryOrderProductInSale(
			List<PcProductinfo> productSkuInfoList, String userCode,
			String appCode, String orderCode) {
		List<Sale_Product> list = new ArrayList<Sale_Product>();
		List<String> productCodes = new ArrayList<String>();
		for (PcProductinfo p : productSkuInfoList) {

			/* 获取SKU编号，用于查询评论信息 */
			for (ProductSkuInfo sku : p.getProductSkuInfoList()) {

				productCodes.add(sku.getSkuCode());
			}

		}

		List<MDataMap> collProList = new ArrayList<MDataMap>();
		List<MDataMap> cpnList = new ArrayList<MDataMap>();
		List<MDataMap> commList1 = new ArrayList<MDataMap>();
		List<MDataMap> userData = new ArrayList<MDataMap>();

		// 查询此用户下收藏商品信息
		collProList = DbUp.upTable("nc_num").queryAll("", "",
				"member_code='" + userCode + "'", new MDataMap());
		if (!productCodes.isEmpty()) {
			/* 查询商品统计表有多少人收藏过 */
			cpnList = DbUp.upTable("nc_productfav").queryIn("", "", "",
					new MDataMap(), -1, -1, "product_code",
					StringUtils.join(productCodes, WebConst.CONST_SPLIT_COMMA));

			// 查出评论信息
			String whereSql = "flag_show ='449746530001' and check_flag = '4497172100030002' and order_code ='"
					+ orderCode
					+ "' and order_skuid in("
					+ StringUtils
							.join(productCodes, WebConst.CONST_SPLIT_COMMA)
					+ ")  ORDER BY oder_creattime DESC ";
			List<MDataMap> commListTmp = new ArrayList<MDataMap>();
			commListTmp = DbUp.upTable("nc_order_evaluation").queryAll("", "",
					whereSql, new MDataMap());
			for (MDataMap c : commListTmp) {
				if (c.get("manage_code").equals(appCode)) {
					commList1.add(c);
				}
			}
		}

		List<String> members = new ArrayList<String>();
		for (MDataMap m : commList1) {
			members.add(m.get("order_name"));
		}
		if (!members.isEmpty()) {
			// 查出用户(评论人)信息
			userData = DbUp.upTable("mc_extend_info_star").queryIn("", "", "",
					new MDataMap(), -1, -1, "member_code",
					StringUtils.join(members, WebConst.CONST_SPLIT_COMMA));
		}

		// 查询等级对应的中文名
		List<MDataMap> levelMapList = DbUp.upTable("mc_member_level")
				.queryByWhere("manage_code", appCode);
		MDataMap levelMap = new MDataMap();

		for (MDataMap m : levelMapList) {
			levelMap.put(m.get("level_code"), m.get("level_name"));
		}

		for (PcProductinfo p : productSkuInfoList) {

			Sale_Product info = new Sale_Product();

			/* sku编号 */
			if (p.getProductSkuInfoList().size() != 0) {

				info.setId(p.getProductSkuInfoList().get(0).getSkuCode());

				/* 介绍文字 */
				info.setIntro(""); // 介绍文字--广告语

				/* 标题--sku名称 */
				info.setTitle(p.getProductSkuInfoList().get(0).getSkuName());

				/* 库存量 */
				info.setRepo_count(p.getProductSkuInfoList().get(0)
						.getStockNum());

				/* 售价 */
				info.setSale_price(p.getProductSkuInfoList().get(0)
						.getSellPrice().doubleValue());

				/* 原价 */
				info.setOrig_price(p.getMarketPrice().doubleValue());

				
				info.setSale_count(new ProductService().getSellCount(p.getProductSkuInfoList().get(0).getSkuCode()));
				 
				// 图片信息
				List<CommentdityAppPhotos> photoList = new ArrayList<CommentdityAppPhotos>();

				List<PcProductpic> pcPicList = new ArrayList<PcProductpic>();

				pcPicList = p.getPcPicList();

				if (pcPicList.size() != 0) {
					for (int i = 0; i < pcPicList.size(); i++) {

						String large = pcPicList.get(i).getPicUrl();
						CommentdityAppPhotos photo = new CommentdityAppPhotos();

						photo.setLarge(large);
						photo.setThumb(large);

						photoList.add(photo);
					}
				}

				info.setPhotos(photoList);

			}

			info.setProduct_id(p.getProductCode());

			/* 推荐理由 */
			info.setReason(p.getProductSkuInfoList().get(0).getSkuAdv());

			ProductDetails productDetails = new ProductDetails();

			String param_url = "";
			/* 传入商品名称，分类，功效，退货政策，产品规格，原产国家，保质期限，适合肤质，温馨提示，特别说明，产品包装 */
			param_url = productDetails.getDetailUrl(p.getProdutName(), p
					.getCategory().getCategoryName(), p.getProductVolume(), p
					.getPcProductpropertyList());

			// 产品参数
			info.setParam_url(param_url);

			String productDetail = "";

			/* 传入图文详情， */
			productDetail = productDetails.getProductUrl(p.getDescription());

			/* 图文详情 */
			info.setDetail_url(productDetail);

			// 关联商品收藏信息
			for (MDataMap collPro : collProList) {
				if (p.getProductSkuInfoList().get(0).getSkuCode()
						.equals(collPro.get("num_code"))) {
					/* 统计商品是否被收藏过 */
					if (collPro.get("flag_enable").equals("")) {
						info.setFaved(0);
					} else {
						info.setFaved(Integer.valueOf(collPro
								.get("flag_enable")));
					}
					break;
				}
			}
			/* 关联商品统计表有多少人收藏过 */
			for (MDataMap cpn : cpnList) {
				if (p.getProductSkuInfoList().get(0).getSkuCode()
						.equals(cpn.get("product_code"))) {
					info.setFav_count(Integer.valueOf(cpn.get("num_fav")));
					break;
				}
			}

			// 关联评论信息
			for (MDataMap com1 : commList1) {
				if (p.getProductSkuInfoList().get(0).getSkuCode()
						.equals(com1.get("order_skuid"))) {
					CommentdityApp comment = new CommentdityApp();
					comment.setId(com1.get("order_skuid"));
					comment.setText(com1.get("order_assessment"));
					comment.setCreated_at(com1.get("oder_creattime"));
					// 图片信息
					List<CommentdityAppPhotos> commPhotoList = new ArrayList<CommentdityAppPhotos>();
					String[] commPhotos = com1.get("oder_photos").split("\\|");
					for (String s : commPhotos) {
						CommentdityAppPhotos photo = new CommentdityAppPhotos();
						photo.setLarge(s);
						photo.setThumb(s);
						commPhotoList.add(photo);
					}
					comment.setPhotos(commPhotoList);
					for (MDataMap user : userData) {// 根据查出来的评论信息匹配评论人信息
						if (com1.get("order_name").equals(
								user.get("member_code"))) {
							// 用户(评论人)信息
							comment.getUser().setMember_code(
									user.get("member_code"));
							comment.getUser().setNickname(user.get("nickname"));
							comment.getUser().setGroup(
									BigInteger.valueOf(Long.valueOf(user
											.get("member_group"))));
							comment.getUser().setGender(
									BigInteger.valueOf(Long.valueOf(user
											.get("member_sex"))));
							comment.getUser().setScore(
									Integer.parseInt(user.get("member_score")));
							comment.getUser().setLevel(
									Integer.valueOf(user.get("member_level")
											.substring(
													user.get("member_level")
															.length() - 4,
													user.get("member_level")
															.length())));
							comment.getUser().setLevel_name(
									levelMap.get(user.get("member_level")));
							comment.getUser().setCreate_time(
									user.get("create_time"));
							comment.getUser().setMobile(
									user.get("mobile_phone"));
							comment.getUser().setScore_unit(
									bConfig("newscenter.Score_unit"));
							
							
							comment.getUser().getAvatar().setLarge(String.valueOf(user.get("member_avatar")));
							
							comment.getUser().getAvatar().setThumb(String.valueOf(user.get("member_avatar")));

							break;
						}
					}
					info.getComments().add(comment);
					break;
				}
			}

			list.add(info);
		}

		return list;
	}

	/**
	 * 根据sku信息和userCode关联在售商品信息
	 * 
	 * @param productSkuInfoList
	 *            sku信息集合
	 * @param userCode
	 *            用户code
	 * 
	 * @return 在售商品类
	 * */
	public List<Productinfo> queryProductIn(
			List<PcProductinfo> productSkuInfoList, String userCode,
			String appCode) {
		List<Productinfo> list = new ArrayList<Productinfo>();
		List<String> productCodes = new ArrayList<String>();
		for (PcProductinfo p : productSkuInfoList) {

			/* 获取SKU编号，用于查询评论信息 */
			for (ProductSkuInfo sku : p.getProductSkuInfoList()) {

				productCodes.add(sku.getSkuCode());
			}

		}

		List<MDataMap> collProList = new ArrayList<MDataMap>();
		List<MDataMap> cpnList = new ArrayList<MDataMap>();
		List<MDataMap> commList1 = new ArrayList<MDataMap>();
		List<MDataMap> userData = new ArrayList<MDataMap>();

		// 查询此用户下收藏商品信息
		collProList = DbUp.upTable("nc_num").queryAll("", "",
				"member_code='" + userCode + "'", new MDataMap());
		if (!productCodes.isEmpty()) {
			/* 查询商品统计表有多少人收藏过 */
			cpnList = DbUp.upTable("nc_productfav").queryIn("", "", "",
					new MDataMap(), -1, -1, "product_code",
					StringUtils.join(productCodes, WebConst.CONST_SPLIT_COMMA));

			// 查出评论信息
			List<MDataMap> commListTmp = DbUp.upTable("nc_order_evaluation")
					.queryIn(
							"",
							"-oder_creattime",
							"flag_show='449746530001' and check_flag = '4497172100030002'",
							new MDataMap(),
							-1,
							-1,
							"order_skuid",
							StringUtils.join(productCodes,
									WebConst.CONST_SPLIT_COMMA));
			for (MDataMap c : commListTmp) {
				if (c.get("manage_code").equals(appCode)) {
					String flag_show = c.get("flag_show");
					String check_flag = c.get("check_flag");
					if (!StringUtils.isEmpty(flag_show)
							&& !StringUtils.isEmpty(check_flag)) {
						if (flag_show.equals("449746530001")
								&& check_flag.equals("4497172100030002")) {
							commList1.add(c);
						}
					}
				}
			}
		}

		List<String> members = new ArrayList<String>();
		for (MDataMap m : commList1) {
			members.add(m.get("order_name"));
		}
		if (!members.isEmpty()) {
			// 查出用户(评论人)信息
			userData = DbUp.upTable("mc_extend_info_star").queryIn("", "", "",
					new MDataMap(), -1, -1, "member_code",
					StringUtils.join(members, WebConst.CONST_SPLIT_COMMA));
		}

		// 查询等级对应的中文名
		List<MDataMap> levelMapList = DbUp.upTable("mc_member_level")
				.queryByWhere("manage_code", appCode);
		MDataMap levelMap = new MDataMap();

		for (MDataMap m : levelMapList) {
			levelMap.put(m.get("level_code"), m.get("level_name"));
		}

		for (PcProductinfo p : productSkuInfoList) {

			Productinfo info = new Productinfo();

			/* sku编号 */
			if (p.getProductSkuInfoList().size() != 0) {

				info.setId(p.getProductSkuInfoList().get(0).getSkuCode());

				/* 介绍文字 */
				info.setIntro(""); // 介绍文字--广告语

				/* 标题--sku名称 */
				info.setTitle(p.getProductSkuInfoList().get(0).getSkuName());

				/* 库存量 */
				info.setRepo_count(p.getProductSkuInfoList().get(0)
						.getStockNum());

				/* 售价 */
				info.setSale_price(p.getProductSkuInfoList().get(0)
						.getSellPrice().doubleValue());

				/* 原价 */
				info.setOrig_price(p.getProductSkuInfoList().get(0)
						.getMarketPrice().doubleValue());

				info.setSale_count(new ProductService().getSellCount(p.getProductSkuInfoList().get(0).getSkuCode()));

				// 图片信息
				List<CommentdityAppPhotos> photoList = new ArrayList<CommentdityAppPhotos>();

				List<PcProductpic> pcPicList = new ArrayList<PcProductpic>();

				pcPicList = p.getPcPicList();

				if (pcPicList.size() != 0) {
					for (int i = 0; i < pcPicList.size(); i++) {

						String large = pcPicList.get(i).getPicUrl();
						CommentdityAppPhotos photo = new CommentdityAppPhotos();

						photo.setLarge(large);
						photo.setThumb(large);

						photoList.add(photo);
					}
				}

				info.setPhotos(photoList);

			}

			info.setProduct_id(p.getProductCode());

			/* 推荐理由 */
			info.setReason(p.getProductSkuInfoList().get(0).getSkuAdv());

			ProductDetails productDetails = new ProductDetails();

			String param_url = "";
			/* 传入商品名称，分类，功效，退货政策，产品规格，原产国家，保质期限，适合肤质，温馨提示，特别说明，产品包装 */
			param_url = productDetails.getDetailUrl(p.getProdutName(), p
					.getCategory().getCategoryName(), p.getProductVolume(), p
					.getPcProductpropertyList());

			// 产品参数
			info.setParam_url(param_url);

			String productDetail = "";

			/* 传入图文详情， */
			productDetail = productDetails.getProductUrl(p.getDescription());

			/* 图文详情 */
			info.setDetail_url(productDetail);

			// 关联商品收藏信息
			for (MDataMap collPro : collProList) {
				if (p.getProductSkuInfoList().get(0).getSkuCode()
						.equals(collPro.get("num_code"))) {
					/* 统计商品是否被收藏过 */
					if (collPro.get("flag_enable").equals("")) {
						info.setFaved(0);
					} else {
						info.setFaved(Integer.valueOf(collPro
								.get("flag_enable")));
					}
					break;
				}
			}
			/* 关联商品统计表有多少人收藏过 */
			for (MDataMap cpn : cpnList) {
				if (p.getProductSkuInfoList().get(0).getSkuCode()
						.equals(cpn.get("product_code"))) {
					info.setFav_count(Integer.valueOf(cpn.get("num_fav")));
					break;
				}
			}

			// 关联评论信息
			for (MDataMap com1 : commList1) {
				if (p.getProductSkuInfoList().get(0).getSkuCode()
						.equals(com1.get("order_skuid"))) {
					CommentdityApp comment = new CommentdityApp();
					comment.setId(com1.get("order_skuid"));
					comment.setText(com1.get("order_assessment"));
					comment.setCreated_at(com1.get("oder_creattime"));
					// 图片信息
					List<CommentdityAppPhotos> commPhotoList = new ArrayList<CommentdityAppPhotos>();
					String[] commPhotos = com1.get("oder_photos").split("\\|");
					for (String s : commPhotos) {
						CommentdityAppPhotos photo = new CommentdityAppPhotos();
						photo.setLarge(s);
						photo.setThumb(s);
						commPhotoList.add(photo);
					}
					comment.setPhotos(commPhotoList);
					for (MDataMap user : userData) {// 根据查出来的评论信息匹配评论人信息
						if (com1.get("order_name").equals(
								user.get("member_code"))) {
							// 用户(评论人)信息
							comment.getUser().setMember_code(
									user.get("member_code"));
							comment.getUser().setNickname(user.get("nickname"));
							comment.getUser().setGroup(
									BigInteger.valueOf(Long.valueOf(user
											.get("member_group"))));
							comment.getUser().setGender(
									BigInteger.valueOf(Long.valueOf(user
											.get("member_sex"))));
							comment.getUser().setScore(
									Integer.parseInt(user.get("member_score")));
							comment.getUser().setLevel(
									Integer.valueOf(user.get("member_level")
											.substring(
													user.get("member_level")
															.length() - 4,
													user.get("member_level")
															.length())));
							comment.getUser().setLevel_name(
									levelMap.get(user.get("member_level")));
							comment.getUser().setCreate_time(
									user.get("create_time"));
							comment.getUser().setMobile(
									user.get("mobile_phone"));
							comment.getUser().setScore_unit(
									bConfig("newscenter.Score_unit"));
						}
					}
					info.getComments().add(comment);
				}
			}

			list.add(info);
		}

		return list;
	}

}
