//
//
//package com.cmall.systemcenter.common;
//
//import java.awt.Color;
//import java.awt.Font;
//import java.awt.FontMetrics;
//import java.awt.Graphics2D;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.nio.file.Path;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Hashtable;
//import java.util.List;
//
//import javax.imageio.ImageIO;
//
//import org.apache.commons.lang.StringUtils;
//import org.apache.http.HttpEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.util.EntityUtils;
//import org.quartz.JobExecutionContext;
//
//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.BinaryBitmap;
//import com.google.zxing.DecodeHintType;
//import com.google.zxing.EncodeHintType;
//import com.google.zxing.LuminanceSource;
//import com.google.zxing.MultiFormatReader;
//import com.google.zxing.MultiFormatWriter;
//import com.google.zxing.NotFoundException;
//import com.google.zxing.Result;
//import com.google.zxing.WriterException;
//import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
//import com.google.zxing.client.j2se.MatrixToImageConfig;
//import com.google.zxing.client.j2se.MatrixToImageWriter;
//import com.google.zxing.common.BitMatrix;
//import com.google.zxing.common.HybridBinarizer;
//import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
//import com.srnpr.zapcom.basemodel.MDataMap;
//import com.srnpr.zapcom.basesupport.ImageSupport;
//import com.srnpr.zapcom.basesupport.WebClientSupport;
//import com.srnpr.zapcom.rootclass.RootJob;
//import com.srnpr.zapcom.topdo.TopDir;
//import com.srnpr.zapdata.dbdo.DbUp;
//import com.srnpr.zapweb.webdo.WebConst;
//import com.srnpr.zapweb.webmethod.WebUpload;
//import com.srnpr.zapweb.webmodel.MWebResult;
//
///**
// * 定时生成商品(SKU)和店铺二维码图片并上传到服务器
// * 
// * @author GaoYang
// * 
// */
//public class TimedTaskForUploadQrCode extends RootJob {
//
//	public void doExecute(JobExecutionContext context) {
//		bLogInfo(0, "do qrcode start!!!");
//		TopDir topDir = new TopDir();
//		// 二维码图片宽度（默认700）
//		int width = 1200;
//		// 二维码图片高度（默认700）
//		int height = 1200;
//		// 二维码图片格式（默认JPG）
//		String format = "jpg";
//		// 二维码图片在外层大图中的X坐标
//		int xPoint = -90;
//		// 二维码图片在外层大图中的y坐标
//		int yPoint = -90;
//		// 二维码内容（默认商城网址）
//		String content = "http://www.cctvmall.com";
//		// 二维码图片的外层大图
////		String outerImagePath = "D:/img/big.jpg";
//		String outerImagePath = topDir.upServerletPath("")
//				+ "/resources/qrcode/" + "big.jpg";
//		// 临时存放二维码图片路径
//		String imagePath = topDir.upTempDir("qrcode");
//		// 临时存放下载的二维码图片路径
//		String downImagePath = topDir.upTempDir("downqrcode");
//		// 二维码中LOGO的路径
////		String logoPath = "D:/img/YSWSC.png";
//		String logoPath = topDir.upServerletPath("") + "/resources/qrcode/"
//				+ "YSWSC.png";
//
//		HashMap<String, String> sellerMap = new HashMap<String, String>();
//		// 获取二维码链接为空的店铺
//		File file = new File(imagePath);
//		if (file.exists()) {
//			List<MDataMap> sellerQrcodeList = new ArrayList<MDataMap>();
//			sellerQrcodeList = DbUp
//					.upTable("uc_sellerinfo")
//					.queryAll(
//							"seller_code,seller_short_name,qrcode_link",
//							"",
//							"qrcode_link = '' AND seller_short_name !='' AND seller_status = '4497172300040004'",
//							new MDataMap());
//			for (int i = 0; i < sellerQrcodeList.size(); i++) {
//				String sellerCode = sellerQrcodeList.get(i).get("seller_code");
//				String sellerShortName = sellerQrcodeList.get(i).get(
//						"seller_short_name");
//				// 计算商家简称宽度，截位便于显示到图片中（图片存放文字的最大宽度450）
//				sellerShortName = getShortNameByWidth(sellerShortName);
//				// sellerMap.put(sellerCode, sellerShortName);
//				try {
//					// 二维码内容
////					content = bConfig("systemcenter.qrcode_shop") + sellerCode;
//					content = "http://shop.m.cctvmall.com/" + sellerCode + ".html";
//					// 生成店铺二维码
//					MWebResult mResult = createQRCode(content, width, height,
//							xPoint, yPoint, format, outerImagePath, imagePath,
//							logoPath, "shop", sellerCode, sellerShortName);
//					// 将生成的二维码链接更新到店铺表（uc_sellerinfo）
//					if (mResult.getResultObject() != null
//							&& !"".equals(mResult.getResultObject())) {
//						String qrCodeLink = String.valueOf(mResult
//								.getResultObject());
//
////						// 二维码图片解码
////						bLogInfo(0, "do decode SellerCode [" + sellerCode + "] start!!");
////						String decodeContent = decodeQrcode(downImagePath,
////								qrCodeLink);
////						bLogInfo(0, "do decode SellerCode [" + sellerCode + "] end!!");
////						// 未知原因 会生成有问题的二维码，如图片显示不全（信息不全），此处把出问题的更新为空，定时再次执行时重新生成
////						if (StringUtils.equals(content, decodeContent)) {
////							if (StringUtils.isNotBlank(qrCodeLink)) {
//								updateSellerQrcodeLink(sellerCode, qrCodeLink);
////							}
////						} else {
////							// 二维码链接更新为空
////							updateSellerQrcodeLink(sellerCode, "");
////						}
//
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//					bLogInfo(949701030, sellerCode);
//				}
//			}
//
//			String whereStr = "";
//
//			sellerQrcodeList = DbUp
//					.upTable("uc_sellerinfo")
//					.queryAll(
//							"seller_code,seller_short_name,qrcode_link",
//							"",
//							" seller_short_name !='' AND seller_status = '4497172300040004'",
//							new MDataMap());
//			for (int i = 0; i < sellerQrcodeList.size(); i++) {
//				String sellerCode = sellerQrcodeList.get(i).get("seller_code");
//				String sellerShortName = sellerQrcodeList.get(i).get(
//						"seller_short_name");
//				// 计算商家简称宽度，截位便于显示到图片中（图片存放文字的最大宽度450）
//				sellerShortName = getShortNameByWidth(sellerShortName);
//				sellerMap.put(sellerCode, sellerShortName);
//
//				whereStr += " seller_code='" + sellerCode + "' or";
//			}
//
//			if (whereStr.length() > 2) {
//				whereStr = whereStr.substring(0, whereStr.length() - 2);
//				whereStr = " qrcode_link = '' and (" + whereStr + ")";
//			} else {
//				return;
//			}
//
//			// 获取二维码链接为空的SKU
//			List<MDataMap> skuQrcodeList = new ArrayList<MDataMap>();
//			skuQrcodeList = DbUp.upTable("pc_skuinfo").queryAll(
//					"sku_code,qrcode_link,seller_code", "", whereStr,
//					new MDataMap());
//			for (int i = 0; i < skuQrcodeList.size(); i++) {
//				String sellerCode = skuQrcodeList.get(i).get("seller_code");
//				String skuCode = skuQrcodeList.get(i).get("sku_code");
//				String sellerShortName = "";
//				if (sellerMap.containsKey(sellerCode)) {
//					sellerShortName = sellerMap.get(sellerCode);
//					try {
//						// 二维码内容
////						content = bConfig("systemcenter.qrcode_sku") + skuCode;
//						content = "http://detail.m.cctvmall.com/" + skuCode + ".html";
//						// 生成SKU二维码
//						MWebResult mResult = createQRCode(content, width,
//								height, xPoint, yPoint, format, outerImagePath,
//								imagePath, logoPath, "sku", skuCode,
//								sellerShortName);
//						// 将生成的二维码链接更新到产品表（pc_skuinfo）
//						if (mResult.getResultObject() != null
//								&& !"".equals(mResult.getResultObject())) {
//							String qrCodeLink = String.valueOf(mResult
//									.getResultObject());
//
////							// 二维码图片解码
////							bLogInfo(0, "do decode skuCode [" + skuCode + "] start!!");
////							String decodeContent = decodeQrcode(downImagePath,
////									qrCodeLink);
////							bLogInfo(0, "do decode skuCode [" + skuCode + "] end!!");
////							// 未知原因
////							// 会生成有问题的二维码，如图片显示不全（信息不全），此处把出问题的更新为空，定时再次执行时重新生成
////							if (StringUtils.equals(content, decodeContent)) {
////								if (StringUtils.isNotBlank(qrCodeLink)) {
//									updateSkuQrcodeLink(skuCode, qrCodeLink);
////								}
////							} else {
////								// 二维码链接更新为空
////								updateSkuQrcodeLink(skuCode, "");
////							}
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//						bLogInfo(949701031, skuCode);
//					}
//				}
//			}
//		} else {
//			bLogInfo(949701032);
//		}
//		bLogInfo(0, "do qrcode end!!!");
//	}
//
//	/**
//	 * 解码二维码图片
//	 * 
//	 * @param downImagePath
//	 *            本地保存路径
//	 * @param urlFile
//	 *            远程文件
//	 * @return
//	 * @throws IOException
//	 */
//	public String decodeQrcode(String downImagePath, String urlFile) {
//
//		try {
//			bLogInfo(0, "do decodeQrcode start!!!");
//			// 下载服务器二维码图片到本地，便于解析
//			downImagePath = downloadImageNew(downImagePath, urlFile);
//
//			// 以下为解析二维码逻辑
//			String rtn = "";
//			Result result = null;
//			BufferedImage image = null;
//
////			bLogInfo(0, downImagePath);
//			image = ImageIO.read(new File(downImagePath));
//			if (image == null){
//				return "";
//			}
//			
//			LuminanceSource source = new BufferedImageLuminanceSource(image);
//			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
//			Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
//			hints.put(DecodeHintType.TRY_HARDER, true);
//			hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
////			bLogInfo(0, "begin rtn :" + rtn);
//			result = new MultiFormatReader().decode(bitmap, hints);
//			rtn = result.getText();
////			bLogInfo(0, "rtn msg:" + rtn);
//			// 删除用于解析而下载的二维码图片
//			deleteFile(downImagePath);
//			bLogInfo(0, "do decodeQrcode end!!!");
//			// 解析内容
//			return rtn.toString();
//
//		} catch (NotFoundException  ne) {
//			deleteFile(downImagePath);
//			ne.printStackTrace();
//			return "";
//		} catch (IOException ioe){
//			deleteFile(downImagePath);
//			ioe.printStackTrace();
//			return "";
//		} catch(Exception ex){
//			deleteFile(downImagePath);
//			ex.printStackTrace();
//			return "";
//		}
//	}
//
//	/**
//	 * 删除文件
//	 * 
//	 * @param downImagePath
//	 *            被删除文件的文件名
//	 */
//	private void deleteFile(String fPath) {
//
//		File file = new File(fPath);
//		if (file.isFile() && file.exists()) {
//			// file.delete();
//		}
//	}
//
//	private String downloadImageNew(String downImagePath, String fileUrl) {
//
//		String sReturn = "";
//
//		try {
//			String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
//			downImagePath = downImagePath + "/" + fileName;
//			URL url = new URL(fileUrl);
//
//			HttpClientBuilder hClientBuilder = HttpClientBuilder.create();
//
//			CloseableHttpClient httpclient = hClientBuilder.build();
//
//			// HttpPost httppost = new HttpPost(sUrl);
//
//			HttpGet httpGet = new HttpGet(fileUrl);
//
//			CloseableHttpResponse response = null;
//
//			response = httpclient.execute(httpGet);
//
//			HttpEntity resEntity = response.getEntity();
//
//			if (resEntity != null) {
//
////				bLogInfo(0, "resEntity is not null");
//
//				File fSaveFile = new File(downImagePath);
//				FileOutputStream out = new FileOutputStream(fSaveFile);
//
//				InputStream in = resEntity.getContent();
//
//				byte[] buffer = new byte[1024];
//				int count = 0;
//
////				String sLogCount = "";
//
//				while ((count = in.read(buffer)) != -1) {
//
////					sLogCount = sLogCount + " " + count;
//
//					out.write(buffer, 0, count);
//				}
////				bLogInfo(0, "logcount" + sLogCount);
//				out.flush();
//				out.close();
//
////				File fNewFile = new File(downImagePath);
////				if (fNewFile.exists()) {
////					bLogInfo(0, "exist " + downImagePath);
////
////					bLogInfo(0, fNewFile.length());
////				} else {
////					bLogInfo(0, "not exist " + downImagePath);
////				}
//
//				if (resEntity != null) {
//					EntityUtils.consume(resEntity);
//				}
//			}
//
//			sReturn = downImagePath;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return sReturn;
//
//	}
//
//	/**
//	 * 下载远程图片并保存到本地
//	 * 
//	 * @param downImagePath
//	 *            本地保存路径
//	 * @param fileUrl
//	 *            远程文件
//	 * @return
//	 */
//	private String downloadImage(String downImagePath, String fileUrl) {
//
//		try {
//			String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
//			downImagePath = downImagePath + "/" + fileName;
//			URL url = new URL(fileUrl);
//			HttpURLConnection connection = (HttpURLConnection) url
//					.openConnection();
//			DataInputStream in = new DataInputStream(
//					connection.getInputStream());
//			FileOutputStream out = new FileOutputStream(downImagePath);
//			byte[] buffer = new byte[1024];
//			int count = 0;
//			while ((count = in.read(buffer)) != -1) {
//				out.write(buffer, 0, count);
//			}
//
//			out.flush();
//			// out.close();
//			// in.close();
//			// connection.disconnect();
//
//			return downImagePath;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "";
//		}
//	}
//
//	/**
//	 * 计算商家简称宽度，截位便于显示到图片中（图片最大宽度450）
//	 * 
//	 * @param sellerShortName
//	 *            商家简称
//	 * @return 截位后的商家简称
//	 */
//	public String getShortNameByWidth(String sellerShortName) {
//		// 截位后的商家简称
//		String newName = sellerShortName;
//		// 计算商家简称的宽度
//		int shortWidth = countStrWidth(sellerShortName);
//		if (shortWidth > 950) {
//			// 截位
//			int tempLen = 0;
//			for (int len = 1; len < sellerShortName.length(); len++) {
//				tempLen = countStrWidth(sellerShortName.substring(0, len));
//				if (tempLen <= 950) {
//					newName = sellerShortName.substring(0, len);
//				}
//			}
//		}
//		return newName;
//	}
//
//	/**
//	 * 计算字符串的宽度
//	 * 
//	 * @param sellerShortName
//	 * @return
//	 */
//	private int countStrWidth(String str) {
//		Font f = new Font("simhei", Font.BOLD, 150);
//		@SuppressWarnings("restriction")
//		FontMetrics fm = sun.font.FontDesignMetrics.getMetrics(f);
//		return fm.stringWidth(str);
//	}
//
//	/**
//	 * 将生成的店铺二维码链接更新到店铺表
//	 * 
//	 * @param sellerCode
//	 *            店铺编码
//	 * @param qrCodeLink
//	 *            店铺二维码链接
//	 */
//	public void updateSellerQrcodeLink(String sellerCode, String qrCodeLink) {
//		bLogInfo(0, "do updateSellerQrcodeLink start!!!");
//		MDataMap insMap = new MDataMap();
//		insMap.put("seller_code", sellerCode);
//		insMap.put("qrcode_link", qrCodeLink);
//		// 以"卖家编码"为单位更新店铺表
//		if (StringUtils.isNotBlank(sellerCode)) {
//			DbUp.upTable("uc_sellerinfo").dataUpdate(insMap, "qrcode_link",
//					"seller_code");
//		}
//		bLogInfo(0, "do updateSellerQrcodeLink end!!!");
//	}
//
//	/**
//	 * 将生成的产品二维码链接更新到产品表
//	 * 
//	 * @param skuCode
//	 *            产品编码
//	 * @param qrCodeLink
//	 *            产品二维码链接
//	 */
//	public void updateSkuQrcodeLink(String skuCode, String qrCodeLink) {
//		bLogInfo(0, "do updateSkuQrcodeLink start!!!");
//		MDataMap insMap = new MDataMap();
//		insMap.put("sku_code", skuCode);
//		insMap.put("qrcode_link", qrCodeLink);
//
//		// 以"产品编码"为单位更新产品表
//		if (StringUtils.isNotBlank(skuCode)) {
//			DbUp.upTable("pc_skuinfo").dataUpdate(insMap, "qrcode_link",
//					"sku_code");
//		}
//		bLogInfo(0, "do updateSkuQrcodeLink end!!!");
//	}
//
//	/**
//	 * 根据内容生成二维码数据
//	 * 
//	 * @param content
//	 *            二维码文字内容
//	 * @param width
//	 *            二维码照片宽度
//	 * @param height
//	 *            二维码照片高度
//	 * @param xPoint
//	 *            二维码图片在外层大图中的X坐标
//	 * @param yPoint
//	 *            二维码图片在外层大图中的y坐标
//	 * @param format
//	 *            二维码照片格式
//	 * @param outerImagePath
//	 *            二维码图片的外层大图路径
//	 * @param imagePath
//	 *            存放二维码图片路径
//	 * @param logoPath
//	 *            二维码图片中LOGO的路径
//	 * @param qrFlag
//	 *            区分是店铺还是SKU的二维码
//	 * @param qrCode
//	 *            编号（店铺或是SKU）
//	 * @throws IOException
//	 */
//	public MWebResult createQRCode(String content, int width, int height,
//			int xPoint, int yPoint, String format, String outerImagePath,
//			String imagePath, String logoPath, String qrFlag, String qrCode,
//			String sellerShortName) {
//		bLogInfo(0, "createQRCode start!!!");
//		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
//		MWebResult mResult = new MWebResult();
//		try {
//			if (StringUtils.isBlank(content)) {
//				content = "http://www.cctvmall.com";
//			}
//			if (width <= 0) {
//				width = 1200;
//			}
//
//			if (height <= 0) {
//				height = 1200;
//			}
//
//			// 设置字符编码
//			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
//
//			// 指定纠错等级
//			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
//
//			BitMatrix matrix = new MultiFormatWriter().encode(content,
//					BarcodeFormat.QR_CODE, width, height, hints);
//
//			// 写入二维码
//			mResult = writeToFile(matrix, format, outerImagePath, imagePath,
//					logoPath, xPoint, yPoint, qrFlag, qrCode, sellerShortName);
//
//		} catch (WriterException e) {
//			e.printStackTrace();
//			bLogInfo(949701033);
//		}
//		bLogInfo(0, "createQRCode end!!!");
//		return mResult;
//	}
//
//	/**
//	 * 
//	 * 写入二维码、以及将照片LOGO写入二维码中
//	 * 
//	 * @param matrix
//	 *            要写入的二维码
//	 * @param format
//	 *            二维码照片格式
//	 * @param outerImagePath
//	 *            二维码图片的外层大图路径
//	 * @param imagePath
//	 *            二维码照片保存路径
//	 * @param logoPath
//	 *            LOGO路径
//	 * @param xPoint
//	 *            二维码图片在外层大图中的X坐标
//	 * @param yPoint
//	 *            二维码图片在外层大图中的y坐标
//	 * @param qrFlag
//	 *            区分是店铺还是SKU的二维码
//	 * @param qrCode
//	 *            编号（店铺或是SKU）
//	 * @throws IOException
//	 */
//	private MWebResult writeToFile(BitMatrix matrix, String format,
//			String outerImagePath, String imagePath, String logoPath,
//			int xPoint, int yPoint, String qrFlag, String qrCode,
//			String sellerShortName) {
//
//		MWebResult mResult = new MWebResult();
//		bLogInfo(0, "writeToFile start!!!");
//		try {
//
//			// 默认二维码图片类型是JPG
//			if (StringUtils.isBlank(format)) {
//				format = "jpg";
//			}
//
//			if (!"jpg".equals(format) && !"png".equals(format)
//					&& !"gif".equals(format)) {
//				format = "jpg";
//			}
//
//			// 二维码生成路径存在时
//			if (StringUtils.isNotBlank(imagePath)) {
//				File file = new File(imagePath);
//				if (file.exists()) {
//					// 存放外层大图的目标路径
//					String outerDestImagePath = "";
//					// 生成二维码图片名
//					long imageName = System.currentTimeMillis();
//					if (imagePath.endsWith("/")) {
//						outerDestImagePath = imagePath;
//						imagePath = imagePath + String.valueOf(imageName) + "."
//								+ format;
//					} else {
//						outerDestImagePath = imagePath + "/";
//						imagePath = imagePath + "/" + String.valueOf(imageName)
//								+ "." + format;
//					}
//
//					// 生成二维码
//					bLogInfo(0, "writeToPath start!!!");
//					Path path = new File(imagePath).toPath();
//					MatrixToImageWriter.writeToPath(matrix, format, path);
//					bLogInfo(0, "writeToPath end!!!");
//					// 添加LOGO图片
//					if (StringUtils.isNotBlank(logoPath)) {
//						// 添加LOGO图片, 此处一定需要重新进行读取，而不能直接使用二维码的BufferedImage对象
//						bLogInfo(0, "add LOGO start!!!");
//						BufferedImage img = ImageIO.read(new File(imagePath));
//						overlapImage(img, format, imagePath, logoPath, xPoint,
//								yPoint, "logo");
//						bLogInfo(0, "add LOGO end!!!");
//					}
//
//					// 把生成的二维码图片嵌套到外层大图
//					File outerDestFile = new File(outerDestImagePath);
//					File outerBigFile = new File(outerImagePath);
//					if (StringUtils.isNotBlank(outerDestImagePath)) {
//						if (outerDestFile.exists()) {
//							// 将外层大图拷贝到存放二维码的路径下，以便于读写
//							if (outerBigFile.exists() && outerBigFile.isFile()) {
//								bLogInfo(0, "add outerBigPIC start!!!");
//								outerDestImagePath = outerDestImagePath
//										+ outerBigFile.getName();
//								if (copyFile(outerImagePath, outerDestImagePath)) {
//									// 嵌套到大图
//									BufferedImage outerImg = ImageIO
//											.read(new File(outerDestImagePath));
//									overlapImage(outerImg, format,
//											outerDestImagePath, imagePath, 85,
//											145, "outer");
//								}
//								bLogInfo(0, "add outerBigPIC end!!!");
//							}
//						} else {
//							bLogInfo(949701034);
//							return mResult;
//						}
//					}
//
//					// 将生成并编辑到外层大图的二维码图片上传到服务器
//					if (StringUtils.isNotBlank(outerDestImagePath)
//							&& new File(outerDestImagePath).isFile()) {
//						File destFile = new File(outerDestImagePath);
//						if (destFile.exists()) {
//							// 上传处理
//							bLogInfo(0, "do Upload start!!!");
//							mResult = formUpload(qrFlag, outerDestImagePath,
//									qrCode, sellerShortName);
//							bLogInfo(0, "do Upload end!!!");
//						}
//					} else {
//						// 将生成的二维码图片（没有编辑到外层大图）发送到服务器
//						if (StringUtils.isNotBlank(imagePath)) {
//							File imageFile = new File(imagePath);
//							if (imageFile.exists()) {
//								// 上传处理
//								mResult = formUpload(qrFlag, imagePath, qrCode,
//										sellerShortName);
//							}
//						}
//					}
//				} else {
//					bLogInfo(949701032);
//				}
//			} else {
//				bLogInfo(949701032);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			bLogInfo(949701033);
//		}
//		bLogInfo(0, "writeToFile end!!!");
//		return mResult;
//	}
//
//	/**
//	 * 将外层大图拷贝到存放二维码的路径下，以便于读写
//	 * 
//	 * @param outerImagePath
//	 *            外层大图原路径
//	 * @param outerDestImagePath
//	 *            外层大图目标路径
//	 * @return 如果复制成功，则返回true，否则返回false
//	 */
//	private boolean copyFile(String oldPath, String newPath) {
//		bLogInfo(0, "do copyFileBigPIC start!!!");
//		try {
//			int byteread = 0;
//			File oldfile = new File(oldPath);
//			if (oldfile.exists()) {
//				InputStream inStream = new FileInputStream(oldPath);
//				FileOutputStream fs = new FileOutputStream(newPath);
//				byte[] buffer = new byte[1444];
//				while ((byteread = inStream.read(buffer)) != -1) {
//					fs.write(buffer, 0, byteread);
//				}
//				fs.close();
//				inStream.close();
//			}
//		} catch (Exception e) {
//			return false;
//		}
//		bLogInfo(0, "do copyFileBigPIC end!!!");
//		return true;
//	}
//
//	/**
//	 * 上传二维码图片
//	 * 
//	 * @param qrFlag
//	 *            区分是店铺还是SKU的二维码
//	 * @param imagePath
//	 *            二维码图片路径
//	 * @param qrCode
//	 *            编号（店铺或是SKU）
//	 * @param sellerShortName
//	 *            店铺简称
//	 * @throws FileNotFoundException
//	 */
//	private MWebResult formUpload(String qrFlag, String imagePath,
//			String qrCode, String sellerShortName) throws IOException {
//
//		MWebResult mResult = null;
//		File file = new File(imagePath);
//
//		// 将图片转换为字节数组
//		ByteArrayOutputStream outStream = changeFileToOutStream(file);
//
//		// 根据要求压缩二维码图片并上传到服务器
//		// 二维码基本尺寸设置
//		int qrSize[][] = { { 300, 300 }, { 258, 258 }, { 344, 344 },
//				{ 430, 430 }, { 860, 860 }, { 1280, 1440 } };
//		// 默认宽度
//		int width = 1280;
//		// 默认高度
//		int height = 1440;
//		// 根据商家简称计算文字在大图中的x坐标，便于文字居中显示
//		int tempWidth = countStrWidth(sellerShortName);
//		int x = (1280 - tempWidth) / 2;
//		for (int i = 0; i < qrSize.length; i++) {
//			int tempSize[] = qrSize[i];
//			if (tempSize.length >= 2) {
//				width = tempSize[0];
//				height = tempSize[1];
//
//				ImageSupport imageSupport = new ImageSupport(
//						outStream.toByteArray());
//				// 添加文字
//				imageSupport.pressText(sellerShortName, "simhei", 1, Color.RED,
//						150, x, 1350, 1);
//				// 压缩图片
//				imageSupport.scaleSmall(width, height);
//				imageSupport.setSourceImage(imageSupport.getTargetImage());
//
//				// 上传二维码图片到服务器
//				WebUpload load = new WebUpload();
//				MWebResult scaleResult = null;
//				scaleResult = load.remoteUpload("qrcode"
//						+ WebConst.CONST_SPLIT_ZDOWN + "p" + i
//						+ WebConst.CONST_SPLIT_ZDOWN + qrFlag
//						+ WebConst.CONST_SPLIT_ZDOWN + qrCode, file.getName(),
//						imageSupport.upTargetByte());
//				// 返回第一个二维码图片链接用来更新到数据库中
//				if (i == 0) {
//					mResult = scaleResult;
//				}
//			}
//		}
//		return mResult;
//	}
//
//	/**
//	 * 编辑二维码图片
//	 * 
//	 * @param image
//	 *            生成的二维码照片对象
//	 * @param format
//	 *            二维码照片格式
//	 * @param bigImagePath
//	 *            大图路径
//	 * @param smallImagePath
//	 *            小图路径
//	 * @param xPoint
//	 *            二维码图片在外层大图中的X坐标
//	 * @param yPoint
//	 *            二维码图片在外层大图中的y坐标
//	 * @param flag
//	 */
//	private void overlapImage(BufferedImage image, String format,
//			String bigImagePath, String smallImagePath, int xPoint, int yPoint,
//			String flag) {
//
//		try {
//			File file = new File(smallImagePath);
//			// LOGO图片存在
//			if (file.exists()) {
//				BufferedImage smallImage = ImageIO
//						.read(new File(smallImagePath));
//				Graphics2D g = image.createGraphics();
//				int width = 0;
//				int height = 0;
//				int x = 0;
//				int y = 0;
//				// 将照片LOGO添加到二维码中间
//				if ("logo".equals(flag)) {
//					// 考虑到LOGO照片贴到二维码中，建议大小不要超过二维码的1/5;
//					width = image.getWidth() / 5;
//					height = image.getHeight() / 5;
//					// LOGO起始位置，此目的是为LOGO居中显示
//					x = (image.getWidth() - width) / 2;
//					y = (image.getHeight() - height) / 2;
//					// 绘制图
//					g.drawImage(smallImage, x, y, width, height, null);
//				} else {
//
//					// 将图片转换为字节数组
//					ByteArrayOutputStream outStream = changeFileToOutStream(file);
//					// 裁剪二维码白边,将图片大小由700变为600
//					ImageSupport imageSupport = new ImageSupport(
//							outStream.toByteArray());
//					imageSupport.cute(52, 52, 1100, 1100);
//
//					// 生成的二维码图片添加到外层大图
//					width = imageSupport.getTargetImage().getWidth();
//					height = imageSupport.getTargetImage().getHeight();
//					// 根据传入的坐标将生成的二维码图片编辑到外层大图
//					if (xPoint >= 0 && yPoint >= 0) {
//						// 根据传入的坐标，编辑二维码图片到外层大图中
//						x = xPoint;
//						y = yPoint;
//					} else {
//						// 如果传入的坐标为负数，则让二维码图片在外层大图中居中显示
//						x = (image.getWidth() - width) / 2;
//						y = (image.getHeight() - height) / 2;
//					}
//					// 绘制图
//					g.drawImage(imageSupport.getTargetImage(), x, y, width,
//							height, null);
//				}
//
//				g.dispose();
//
//				// 写入LOGO照片到二维码
//				ImageIO.write(image, format, new File(bigImagePath));
//			} else {
//				bLogInfo(949701035);
//				return;
//			}
//		} catch (Exception e) {
//			bLogInfo(949701036);
//		}
//	}
//
//	/**
//	 * 将图片转换为字节数组
//	 * 
//	 * @param file
//	 *            文件
//	 * @return 转换后的字节数组
//	 * @throws IOException
//	 */
//	private ByteArrayOutputStream changeFileToOutStream(File file)
//			throws IOException {
//
//		// 文件流
//		DataInputStream in = new DataInputStream(new FileInputStream(file));
//
//		// 转换为字节数组
//		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//		byte[] data = new byte[1024];
//		int count = -1;
//		while ((count = in.read(data, 0, 1024)) != -1) {
//			outStream.write(data, 0, count);
//		}
//		in.close();
//		return outStream;
//	}
//}
//*/