//package com.cmall.systemcenter.webfunc;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import org.apache.commons.lang.StringUtils;
//import com.cmall.systemcenter.common.TimedTaskForUploadQrCode;
//import com.srnpr.zapcom.basemodel.MDataMap;
//import com.srnpr.zapcom.topdo.TopDir;
//import com.srnpr.zapdata.dbdo.DbUp;
//import com.srnpr.zapweb.webdo.WebConst;
//import com.srnpr.zapweb.webfunc.RootFunc;
//import com.srnpr.zapweb.webmodel.MWebResult;
//
//public class FuncCreateShopQrCode extends RootFunc{
//
//	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
//		
//		MWebResult mResult = new MWebResult();
//		TimedTaskForUploadQrCode timedTaskForUploadQrCode = new TimedTaskForUploadQrCode();
//		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
//
//		if (mResult.upFlagTrue()) {
////			bLogInfo(0, "do Page FuncCreateShopQrCode start!!!");
//			try{
//				String paramSellerCode=mSubMap.get("seller_code");
//				
//				if (StringUtils.isNotBlank(paramSellerCode)) {
//					//清空卖家的二维码链接
//					updateSellerQrcodeLink(paramSellerCode);
//					
//					//清空与卖家关联SKU的二维码链接
//					updateSkuQrcodeLink(paramSellerCode);
//					
//					//调用二维码定时器的生成方法
//					TopDir topDir = new TopDir();
//					// 二维码图片宽度（默认700）
//					int width = 1200;
//					// 二维码图片高度（默认700）
//					int height = 1200;
//					// 二维码图片格式（默认JPG）
//					String format = "jpg";
//					// 二维码图片在外层大图中的X坐标
//					int xPoint = -90;
//					// 二维码图片在外层大图中的y坐标
//					int yPoint = -90;
//					// 二维码内容（默认商城网址）
//					String content = "http://www.cctvmall.com";
//					// 二维码图片的外层大图
////					String outerImagePath = "D:/img/big.jpg";
//					String outerImagePath = topDir.upServerletPath("")
//							+ "/resources/qrcode/" + "big.jpg";
//					// 临时存放二维码图片路径
//					String imagePath = topDir.upTempDir("pageqrcode");
//					// 临时存放下载的二维码图片路径
//					String downImagePath = topDir.upTempDir("pagedownqrcode");
//					// 二维码中LOGO的路径
////					String logoPath = "D:/img/YSWSC.png";
//					String logoPath = topDir.upServerletPath("") + "/resources/qrcode/"
//							+ "YSWSC.png";
//
//					HashMap<String, String> sellerMap = new HashMap<String, String>();
//					// 获取二维码链接为空的店铺
//					File file = new File(imagePath);
//					if (file.exists()) {
//						List<MDataMap> sellerQrcodeList = new ArrayList<MDataMap>();
//						String sSellerWhere = "qrcode_link = '' AND seller_short_name !='' AND seller_status = '4497172300040004'" + " AND seller_code= '" + paramSellerCode + "'";
//						sellerQrcodeList = DbUp
//								.upTable("uc_sellerinfo")
//								.queryAll(
//										"seller_code,seller_short_name,qrcode_link",
//										"",
//										sSellerWhere,
//										new MDataMap());
//						for (int i = 0; i < sellerQrcodeList.size(); i++) {
//							String sellerCode = sellerQrcodeList.get(i).get("seller_code");
//							String sellerShortName = sellerQrcodeList.get(i).get(
//									"seller_short_name");
//							// 计算商家简称宽度，截位便于显示到图片中（图片存放文字的最大宽度450）
//							sellerShortName = timedTaskForUploadQrCode.getShortNameByWidth(sellerShortName);
//							// sellerMap.put(sellerCode, sellerShortName);
//							try {
//								// 二维码内容
////								content = bConfig("systemcenter.qrcode_shop") + sellerCode;
//								content = "http://shop.m.cctvmall.com/" + sellerCode + ".html";
//								// 生成店铺二维码
//								MWebResult mWebResult = timedTaskForUploadQrCode.createQRCode(content, width, height,
//										xPoint, yPoint, format, outerImagePath, imagePath,
//										logoPath, "shop", sellerCode, sellerShortName);
//								// 将生成的二维码链接更新到店铺表（uc_sellerinfo）
//								if (mWebResult.getResultObject() != null
//										&& !"".equals(mWebResult.getResultObject())) {
//									String qrCodeLink = String.valueOf(mWebResult
//											.getResultObject());
//
////									// 二维码图片解码
//////									bLogInfo(0, "do Page decode SellerCode [" + sellerCode + "] start!!");
////									String decodeContent = timedTaskForUploadQrCode.decodeQrcode(downImagePath,
////											qrCodeLink);
//////									bLogInfo(0, "do Page decode SellerCode [" + sellerCode + "] end!!");
////									// 未知原因 会生成有问题的二维码，如图片显示不全（信息不全），此处把出问题的更新为空，定时再次执行时重新生成
////									if (StringUtils.equals(content, decodeContent)) {
////										if (StringUtils.isNotBlank(qrCodeLink)) {
//											timedTaskForUploadQrCode.updateSellerQrcodeLink(sellerCode, qrCodeLink);
////										}
////									} else {
////										// 二维码链接更新为空
////										timedTaskForUploadQrCode.updateSellerQrcodeLink(sellerCode, "");
////									}
//
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//								bLogInfo(949701030, sellerCode);
//							}
//						}
//
//						String whereStr = "";
//						String sSkuWhere = " seller_short_name !='' AND seller_status = '4497172300040004'" + " AND seller_code= '" + paramSellerCode + "'";
//						sellerQrcodeList = DbUp
//								.upTable("uc_sellerinfo")
//								.queryAll(
//										"seller_code,seller_short_name,qrcode_link",
//										"",
//										sSkuWhere,
//										new MDataMap());
//						for (int i = 0; i < sellerQrcodeList.size(); i++) {
//							String sellerCode = sellerQrcodeList.get(i).get("seller_code");
//							String sellerShortName = sellerQrcodeList.get(i).get(
//									"seller_short_name");
//							// 计算商家简称宽度，截位便于显示到图片中（图片存放文字的最大宽度450）
//							sellerShortName = timedTaskForUploadQrCode.getShortNameByWidth(sellerShortName);
//							sellerMap.put(sellerCode, sellerShortName);
//
//							whereStr += " seller_code='" + sellerCode + "' or";
//						}
//
//						if (whereStr.length() > 2) {
//							whereStr = whereStr.substring(0, whereStr.length() - 2);
//							whereStr = " qrcode_link = '' and (" + whereStr + ")";
//						} else {
//							return mResult;
//						}
//
//						// 获取二维码链接为空的SKU
//						List<MDataMap> skuQrcodeList = new ArrayList<MDataMap>();
//						skuQrcodeList = DbUp.upTable("pc_skuinfo").queryAll(
//								"sku_code,qrcode_link,seller_code", "", whereStr,
//								new MDataMap());
//						for (int i = 0; i < skuQrcodeList.size(); i++) {
//							String sellerCode = skuQrcodeList.get(i).get("seller_code");
//							String skuCode = skuQrcodeList.get(i).get("sku_code");
//							String sellerShortName = "";
//							if (sellerMap.containsKey(sellerCode)) {
//								sellerShortName = sellerMap.get(sellerCode);
//								try {
//									// 二维码内容
////									content = bConfig("systemcenter.qrcode_sku") + skuCode;
//									content = "http://detail.m.cctvmall.com/" + skuCode + ".html";
//									// 生成SKU二维码
//									MWebResult mWebResult = timedTaskForUploadQrCode.createQRCode(content, width,
//											height, xPoint, yPoint, format, outerImagePath,
//											imagePath, logoPath, "sku", skuCode,
//											sellerShortName);
//									// 将生成的二维码链接更新到产品表（pc_skuinfo）
//									if (mWebResult.getResultObject() != null
//											&& !"".equals(mWebResult.getResultObject())) {
//										String qrCodeLink = String.valueOf(mWebResult
//												.getResultObject());
//
////										// 二维码图片解码
//////										bLogInfo(0, "do Page decode skuCode [" + skuCode + "] start!!");
////										String decodeContent = timedTaskForUploadQrCode.decodeQrcode(downImagePath,
////												qrCodeLink);
//////										bLogInfo(0, "do Page decode skuCode [" + skuCode + "] end!!");
////										// 未知原因
////										// 会生成有问题的二维码，如图片显示不全（信息不全），此处把出问题的更新为空，定时再次执行时重新生成
////										if (StringUtils.equals(content, decodeContent)) {
////											if (StringUtils.isNotBlank(qrCodeLink)) {
//												timedTaskForUploadQrCode.updateSkuQrcodeLink(skuCode, qrCodeLink);
////											}
////										} else {
////											// 二维码链接更新为空
////											timedTaskForUploadQrCode.updateSkuQrcodeLink(skuCode, "");
////										}
//									}
//								} catch (Exception e) {
//									e.printStackTrace();
//									bLogInfo(949701031, skuCode);
//								}
//							}
//						}
//					} else {
//						bLogInfo(949701032);
//					}
////					bLogInfo(0, "do Page qrcode end!!!");
//					
////					mResult.setResultCode(949701037);
////					mResult.setResultMessage(bInfo(949701037));
//				}
//
////				bLogInfo(0, "do Page FuncCreateShopQrCode end!!!");
//				
//			}catch(Exception e){
//				e.printStackTrace();
//				mResult.setResultCode(949701033);
//				mResult.setResultMessage(bInfo(949701033));
//				return mResult;
//			}
//		}
//		return mResult;
//	}
//
//	/**
//	 * 清空与卖家关联SKU的二维码链接
//	 * @param sellerCode
//	 */
//	private void updateSkuQrcodeLink(String sellerCode) {
//		
//		MDataMap insMap = new MDataMap();
//		insMap.put("seller_code", sellerCode);
//		insMap.put("qrcode_link", "");
//		// 以"卖家编码"为单位更新
//		DbUp.upTable("pc_skuinfo").dataUpdate(insMap, "qrcode_link",
//				"seller_code");
//	}
//
//	/**
//	 * 清空店铺的二维码链接
//	 * @param sellerCode 店铺编码
//	 */
//	private void updateSellerQrcodeLink(String sellerCode) {
//		
//		MDataMap insMap = new MDataMap();
//		insMap.put("seller_code", sellerCode);
//		insMap.put("qrcode_link", "");
//		// 以"卖家编码"为单位更新
//		DbUp.upTable("uc_sellerinfo").dataUpdate(insMap, "qrcode_link",
//				"seller_code");
//		
//	}
//	
//}
