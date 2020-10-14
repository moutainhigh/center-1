package com.cmall.groupcenter.job;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.srnpr.xmassystem.enumer.EPlusScheduler;
import com.srnpr.xmassystem.top.PlusConfigScheduler;
import com.srnpr.xmassystem.top.PlusTopScheduler;
import com.srnpr.xmassystem.very.PlusVeryImage;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webface.IKvSchedulerConfig;

/**
 * 预加载商品的相关图片宽高信息，提高商品首次的访问速度
 */
public class JobProductImageWidth extends PlusTopScheduler {
	
	PlusVeryImage plusVeryImage = new PlusVeryImage();

	public IBaseResult execByInfo(String sInfo) {
		RootResultWeb result = new RootResultWeb();
		
		String mainpicUrl = (String)DbUp.upTable("pc_productinfo").dataGet("mainpic_url", "", new MDataMap("product_code", sInfo));
		if(StringUtils.isNotBlank(mainpicUrl)) {
			// 主图压缩两种常用规格
			zoom(mainpicUrl, 400);
			zoom(mainpicUrl, 570);
		}
		
		List<MDataMap> picList = DbUp.upTable("pc_productpic").queryAll("pic_url", "", "", new MDataMap("product_code", sInfo));
		for(MDataMap map : picList) {
			// 轮播图
			zoom(map.get("pic_url"), 750);
		}
		
		String descPic = (String)DbUp.upTable("pc_productdescription").dataGet("description_pic", "", new MDataMap("product_code", sInfo));
		if(StringUtils.isNotBlank(descPic)) {
			String[] picArr = descPic.split("\\|");
			for(String pic : picArr) {
				// 详情图
				zoom(pic, 750);
			}
		}
		
		return result;
	}
	
	private void zoom(String url, int width) {
		if(StringUtils.isBlank(url)) return;
		try {
			plusVeryImage.upImageZoom(url, width);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final static PlusConfigScheduler plusConfigScheduler = new PlusConfigScheduler(EPlusScheduler.ProductImageWidth);

	public IKvSchedulerConfig getConfig() {

		return plusConfigScheduler;
	}
}
