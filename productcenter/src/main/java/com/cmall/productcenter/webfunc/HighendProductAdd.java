package com.cmall.productcenter.webfunc;

import com.cmall.productcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * 项目名称：productcenter 
 * 类名称：     HighendProductAdd 
 * 类描述：     高端商品增加
 * 创建人：     GaoYang
 * 创建时间：2013年11月16日下午2:00:18
 * 修改人：     GaoYang
 * 修改时间：2013年11月16日下午2:00:18
 * 修改备注： 
 */
public class HighendProductAdd extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		MWebOperate mOperate = WebUp.upOperate(sOperateUid);
		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());
		try{
			//获取页面数据
			MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
			if (mResult.upFlagTrue()) {
				MDataMap inMap = new MDataMap();
				String productCode = mAddMaps.get("product_name");
				//获取此商品的相关信息
				MDataMap oneSellerMap = DbUp.upTable("pc_productinfo").one("product_code",productCode);
				if(oneSellerMap != null){
					//商品编码
					inMap.put("product_code", productCode);
					//商品名称
					//(添加高端商品页面选择的商品,该页面项目是组件,在高端商品管理表中product_code与product_name是相同的值,但在页面显示code对应的汉字名称)
					inMap.put("product_name", productCode);
					//高端商品分类
					//(添加高端商品页面选择的分类,该页面项目是组件,在高端商品管理表中存code值,但在页面显示code对应的汉字名称)
					String highendType = mAddMaps.get("highend_type");
					inMap.put("highend_type", highendType);
					//是否可用标记: "1"为可用 ,"0"为不可用
					inMap.put("flag_usable", "1");
					//创建时间
					inMap.put("create_time", DateUtil.getSysDateTimeString());
					//商品的相关信息
					inMap.put("seller_code", oneSellerMap.get("seller_code"));
					inMap.put("highend_product_name", oneSellerMap.get("product_name"));
					inMap.put("min_sell_price", oneSellerMap.get("min_sell_price"));
					inMap.put("mainpic_url", oneSellerMap.get("mainpic_url"));
					//判断该商品是否已经存在，存在则更新现有数据，否则写入新数据
					int pcCount = DbUp.upTable(mPage.getPageTable()).count("product_code",productCode);
					if(pcCount > 0){
						//更新现有数据
						String updColumn = "highend_type,seller_code,highend_product_name,min_sell_price,mainpic_url,create_time";
						String updWhere = "product_code";
						DbUp.upTable(mPage.getPageTable()).dataUpdate(inMap, updColumn, updWhere);
					}else{
						//写入新数据
						DbUp.upTable(mPage.getPageTable()).dataInsert(inMap);
					}
				} else{
					mResult.setResultCode(941901038);
					mResult.setResultMessage(bInfo(941901038,"商品"));
				}
			}
		}catch (Exception e){
			mResult.setResultCode(941901030);
			mResult.setResultMessage(bInfo(941901030));
		}
		return mResult;
	}
}