package com.cmall.groupcenter.jd.job;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.quartz.JobExecutionContext;

import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJobForExclusiveLock;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 定时替换待上架京东商品图片地址
 */
public class JobForUpdateProductImage extends RootJobForExclusiveLock{

	Pattern imgNamePatt = Pattern.compile("\\w+\\.(jpg|png)", Pattern.CASE_INSENSITIVE);
	
	@Override
	public void doExecute(JobExecutionContext context) {
		// 三天内创建的待上架商品
		List<MDataMap> mapList = DbUp.upTable("pc_productinfo").queryAll("", "", "small_seller_code = 'SF031JDSC' AND product_status = '4497153900060001' AND create_time > DATE_SUB(NOW(),INTERVAL 3 DAY)", null);
		String productCode;
		
		ProductService productService = new ProductService();
		
		MDataMap flowMap;
		int flag = 0;
		for(MDataMap map : mapList) {
			if(map.get("product_name").contains("不可卖")) {
				continue;
			}
			productCode = map.get("product_code");
			flowMap = DbUp.upTable("pc_productflow").oneWhere("", "zid desc", "", "product_code", productCode);
			
			flag = 0;
			flag += updateMainImage(map);
			flag += updateSkuImage(productCode);
			flag += updateProductImageList(productCode);
			flag += updateProductDescImage(productCode);
			
			// 如果有图片被替换则同时更新审批记录表
			if(flag > 0 && flowMap != null) {
				PcProductinfo pc = productService.getProduct(productCode);
				if(pc != null) {
					String json = new JsonHelper<PcProductinfo>().ObjToString(pc);
					flowMap.put("product_json", json);
					DbUp.upTable("pc_productflow").dataUpdate(flowMap, "product_json", "zid");
				}
			}
		}
	}
	
	// 主图
	private int updateMainImage(MDataMap map) {
		String img = map.get("mainpic_url");
		// 忽略已经更新为惠家有图片的数据
		if(StringUtils.isBlank(img) || img.contains("huijiayou.cn")) {
			return 0;
		}
		
		MWebResult mResult = uploadImage(img);
		if(StringUtils.isNotBlank((String)mResult.getResultObject())) {
			
			map.put("mainpic_url", (String)mResult.getResultObject());
			DbUp.upTable("pc_productinfo").dataUpdate(map, "mainpic_url", "zid");
			
			return 1;
		}
		
		return 0;
	}
	
	// SKU图片
	private int updateSkuImage(String productCode) {
		List<MDataMap> skuList = DbUp.upTable("pc_skuinfo").queryByWhere("product_code", productCode);
		MWebResult mResult;
		int i = 0;
		for(MDataMap skuMap : skuList) {
			String img = skuMap.get("sku_picurl");
			// 忽略已经更新为惠家有图片的数据
			if(StringUtils.isBlank(img) || img.contains("huijiayou.cn")) {
				continue;
			}
			
			mResult = uploadImage(img);
			
			if(StringUtils.isNotBlank((String)mResult.getResultObject())) {
				skuMap.put("sku_picurl", (String)mResult.getResultObject());
				DbUp.upTable("pc_skuinfo").dataUpdate(skuMap, "sku_picurl", "zid");
				i++;
			}
		}
		
		return i;
	}
	
	// 轮播图
	private int updateProductImageList(String productCode) {
		List<MDataMap> picList = DbUp.upTable("pc_productpic").queryByWhere("product_code", productCode);
		MWebResult mResult;
		int i = 0;
		for(MDataMap skuMap : picList) {
			String img = skuMap.get("pic_url");
			// 忽略已经更新为惠家有图片的数据
			if(StringUtils.isBlank(img) || img.contains("huijiayou.cn")) {
				continue;
			}
			
			mResult = uploadImage(img);
			
			if(StringUtils.isNotBlank((String)mResult.getResultObject())) {
				skuMap.put("pic_url", (String)mResult.getResultObject());
				DbUp.upTable("pc_productpic").dataUpdate(skuMap, "pic_url", "zid");
				i++;
			}
		}
		
		return i;
	}
	
	// 详情图
	private int updateProductDescImage(String productCode) {
		MDataMap descMap = DbUp.upTable("pc_productdescription").one("product_code", productCode);
		String descriptionPic = descMap.get("description_pic");
		if(StringUtils.isBlank(descriptionPic)) {
			return 0;
		}
		
		List<String> picList = Arrays.asList(descriptionPic.split("\\|"));
		MWebResult mResult;
		boolean updateFlag = false;
		int i = 0;
		for(String url : picList) {
			// 忽略已经更新为惠家有图片的数据
			if(StringUtils.isBlank(url) || url.contains("huijiayou.cn")) {
				continue;
			}
			
			mResult = uploadImage(url);
			
			if(StringUtils.isNotBlank((String)mResult.getResultObject())) {
				descriptionPic = replaceUrl(descriptionPic, url, (String)mResult.getResultObject());
				updateFlag = true;
				i++;
			}
		}
		
		if(updateFlag) {
			descMap.put("description_pic", descriptionPic);
			DbUp.upTable("pc_productdescription").dataUpdate(descMap, "description_pic", "zid");
		}
		
		return i;
	}
	
	private MWebResult uploadImage(String imgUrl) {
		MWebResult mResult = new MWebResult();
		try {
			Matcher mat = imgNamePatt.matcher(imgUrl);
			if(!mat.find()) {
				mResult.setResultCode(0);
				return mResult;
			}
			String filename = mat.group(0).toLowerCase();
			
			String sUrl = bConfig("zapweb.upload_remote") + "?" + WebConst.CONST_WEB_FIELD_SET + "target=upload";
			MultipartEntityBuilder mb = MultipartEntityBuilder.create();

			byte[] bs = getContent(imgUrl);
			if(bs == null) {
				mResult.setResultCode(0);
				mResult.setResultMessage("下载图片失败");
				return mResult;
			}
			
			mb.addBinaryBody("file", bs, ContentType.MULTIPART_FORM_DATA, filename);

			String sReturnString = WebClientSupport.create().doRequest(sUrl, mb.build());
			
			mResult = new JsonHelper<MWebResult>().StringToObj(sReturnString, new MWebResult());
			
		} catch (Exception e) {
			e.printStackTrace();
			mResult.setResultCode(2);
		}
		
		return mResult;
	}
	
	private byte[] getContent(String imgUrl) {
		byte[] bs = null;
		HttpURLConnection conn = null;
		InputStream input = null;
		try {
			conn = (HttpURLConnection)new URL(imgUrl).openConnection();
			conn.setReadTimeout(5000);
			conn.connect();
			
			if(conn.getResponseCode() == 200) {
				int len = conn.getContentLength();
				if(len > 0) {
					input = conn.getInputStream();
					bs = IOUtils.toByteArray(input, len);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			bs = null;
		} finally {
			if(conn != null) {
				conn.disconnect();
			}
			
			IOUtils.closeQuietly(input);
		}
		
		return bs;
	}
	
	
	private String replaceUrl(String text, String replaceUrl, String newUrl) {
		try {
			// 做一次编码解决url中包含特殊字符无法替换
			replaceUrl = URLEncoder.encode(replaceUrl, "UTF-8");
			newUrl = URLEncoder.encode(newUrl, "UTF-8");
			String content = URLEncoder.encode(text, "UTF-8");
			
			content = content.replace(replaceUrl, newUrl);
			content = URLDecoder.decode(content, "UTF-8");
			
			return content;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return text;
	}
	
	
}
