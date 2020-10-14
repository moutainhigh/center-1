package com.cmall.newscenter.webfunc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.service.StoreService;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webexport.RootExport;
import com.srnpr.zapweb.webmodel.MPageData;

/**
 * 导出所需的商品信息
 * 
 * @author yangrong
 * 
 */
public class ProductForEExport extends RootExport {

	public void export(String sOperateId, HttpServletRequest request,
			HttpServletResponse response) {

		exportExcel(sOperateId, request, response);
		setExportName("product" + FormatHelper.upDateTime(new Date(), "yyMMddHHmmss"));// 修改文件名

		// 修改数据
		MPageData pageData = getPageData();

		List<String> head_list = pageData.getPageHead();
		// 重新写入头
		head_list.clear();
		head_list.add("商品编码");
		head_list.add("商品名称");
		head_list.add("sku编号");
		head_list.add("sku名称");
		head_list.add("售价");
		head_list.add("状态");
		head_list.add("市场价");
		head_list.add("可售库存");
		head_list.add("货号");


		// 重写数据
		List<List<String>> pd = pageData.getPageData();
		List<List<String>> data = new ArrayList<List<String>>();

		for (List<String> ppd : pd) {

			String product_code = ppd.get(0);// 商品编号
		

			// 查询订单详情
			List<MDataMap> product_detail_list = DbUp.upTable("pc_productinfo").queryAll("market_price,sell_productcode,product_name,seller_code", "","product_code=:product_code",new MDataMap("product_code", product_code));
			
			if (product_detail_list == null || product_detail_list.size() < 1) {
				continue;
			}

			
			for (MDataMap product_detail : product_detail_list) {

				String product_name =  product_detail_list.get(0).get("product_name");// 商品名称
				
				String status = ppd.get(3);// 状态
				
				String sku_code = "";
				String sell_price = "";
				String market_price = "";
				market_price = product_detail_list.get(0).get("market_price");
				
				String seller_code = product_detail_list.get(0).get("seller_code");
				
				String sku_name = "";
				MDataMap whereMap = new MDataMap();
				whereMap.put("product_code", product_code);
				List<MDataMap> skucodelist = DbUp.upTable("pc_skuinfo")
						.queryAll("sku_code,sell_price,market_price,sku_name", "", "",
								whereMap);
				if (skucodelist != null && !"".equals(skucodelist)
						&& skucodelist.size() != 0) {
					
					for(int i=0;i<skucodelist.size();i++){
						
						int stock  = 0;
						
						sku_code = skucodelist.get(i).get("sku_code");
						sell_price = skucodelist.get(i).get("sell_price");
						
						sku_name = skucodelist.get(i).get("sku_name");
						StoreService storeService = new StoreService();
						
						if(AppConst.MANAGE_CODE_CAPP.equals(seller_code)){
							stock = storeService.getStockNumByMaxFor7(sku_code);
						}else if(AppConst.MANAGE_CODE_CYOUNG.equals(seller_code)){
							stock = storeService.getStockNumByMaxFor13(sku_code);
						}
						
						

						List<String> dd = new ArrayList<String>(34);
						dd.add(product_code);// 商品编码
						dd.add(product_name);// 商品名称
						dd.add(sku_code);//sku编号
						dd.add(sku_name);
						dd.add(sell_price);// 售价
						dd.add(status);// 状态
						dd.add(market_price);// 市场价
						dd.add(String.valueOf(stock));// 库存
						dd.add(product_detail.get("sell_productcode"));// 货号

						data.add(dd);
					}
					
				}

			}

		}

		pageData.setPageData(data);

		doExport();
	}


}
