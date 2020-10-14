package com.cmall.productcenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * 项目名称：productcenter 
 * 类名称：     FuncAddShowwindowSku 
 * 类描述：     卖家橱窗产品增加处理
 * 创建人：     GaoYang
 * 创建时间：2013年10月29日下午1:38:43
 * 修改人：     GaoYang
 * 修改时间：2013年10月29日下午1:38:43
 * 修改备注： 
 *
 */
public class FuncAddShowwindowSku extends RootFunc{
	
	//橱窗商品管理表
	public static final String PC_SHOWWINDOW = "pc_showwindow";
	//产品表
	public static final String PC_SKUINFO = "pc_skuinfo";
	
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();

		try{
			//获取页面数据
			MDataMap mWindowMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
			
			if (mResult.upFlagTrue()){
				//卖家编号
				String sellerCode = UserFactory.INSTANCE.create().getManageCode();
				//以卖家编号为查询条件，获取该卖家的橱窗产品数量(SKU数量)
				int pcCount = DbUp.upTable(PC_SHOWWINDOW).count("seller_code", sellerCode);
				//同一个卖家只能添加5个橱窗产品(SKU单位)
				if(pcCount >= 5){
					mResult.setResultCode(941901025);
					mResult.setResultMessage(bInfo(941901025));
				}else{
					//产品编号(添加橱窗产品页面选择的产品,该页面项目是组件,在橱窗商品管理表中sku_name与sku_code是相同的值)
					String skuCode = mWindowMap.get("sku_name");
					//根据产品编号获取商品编号
					MDataMap oneDataMap = DbUp.upTable(PC_SKUINFO).one("sku_code",skuCode);
					if(oneDataMap != null){
						String productCode = oneDataMap.get("product_code");
						if(StringUtils.isBlank(productCode)){
							//商品编号不存在时,返回提示信息
							mResult.setResultCode(941901027);
							mResult.setResultMessage(bInfo(941901027));
						} else {
							//判断该橱窗产品在表中是否已存在，存在则更新，否则添加
							int skuCount = DbUp.upTable(PC_SHOWWINDOW).count("seller_code", sellerCode, "sku_code", skuCode);
							MDataMap insMap = new MDataMap();
							insMap.put("seller_code", sellerCode);
							insMap.put("product_code", productCode);
							insMap.put("sku_code", skuCode);
							insMap.put("sku_name", skuCode);
							insMap.put("create_time", DateUtil.getSysDateTimeString());
							//存在
							if(skuCount > 0){
								//更新已存在的橱窗产品(SKU)
								String updColumn = "product_code,create_time";
								String updWhere = "seller_code,sku_code";
								DbUp.upTable(PC_SHOWWINDOW).dataUpdate(insMap, updColumn, updWhere);
							} else{
								//添加新的橱窗产品(SKU)
								DbUp.upTable(PC_SHOWWINDOW).dataInsert(insMap);
							}
						}
					} else {
						//产品信息不存在时,返回提示信息
						mResult.setResultCode(941901038);
						mResult.setResultMessage(bInfo(941901038,"产品"));
					}
				}
			}
		} catch(Exception e){
			mResult.setResultCode(941901028);
			mResult.setResultMessage(bInfo(941901028));
		}
		return mResult;
	}
}