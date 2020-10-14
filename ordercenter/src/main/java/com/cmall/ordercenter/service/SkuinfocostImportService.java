package com.cmall.ordercenter.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cmall.productcenter.common.Constants;
import com.cmall.productcenter.common.DateUtil;
import com.cmall.productcenter.service.ProductSkuPriceService;
import com.cmall.systemcenter.model.ScFlowMain;
import com.cmall.systemcenter.service.FlowService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class SkuinfocostImportService extends RootFunc {
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap1) {
		String fileRemoteUrl = mDataMap1.get("uploadFile");
		MWebResult mWebResult = new MWebResult();
		List<Map<String, String>> infoList = new ArrayList<Map<String, String>>();
		try {
			List<Map<String, String>> dataLists = this.downloadAndAnalysisFile(fileRemoteUrl);
			if (dataLists != null && dataLists.size() > 0) {
				Map<String, String> tempMap = dataLists.get(0);
				if(!tempMap.get("sell_productcode").equals("货号")||!tempMap.get("product_code").equals("商品编号")||!tempMap.get("sku_code").equals("sku编号")
						||!tempMap.get("sku_name").equals("sku名称")||!tempMap.get("cost_price_old").equals("原成本价")
						||!tempMap.get("cost_price").equals("变更后成本价")){
					mWebResult.setResultCode(-1);
					mWebResult.setResultMessage("导入数据错误");
					return mWebResult;
				}else{
					dataLists.remove(0);
				}
				
				
				/**
				 * 过滤excel中导入的重复数据
				 */
				Map<String, Map<String, String>> infoMap = new HashMap<String, Map<String, String>>();
				for (int i =0;i<dataLists.size() ; i++) {
					String code = dataLists.get(i).get("sku_code");
					if(infoMap.containsKey(code)) {
						dataLists.remove(i);
						i--;
					} else {
						infoMap.put(code, dataLists.get(i));
					}
				}
				
				for (Entry<String, Map<String, String>> e: infoMap.entrySet()) {
					if (StringUtils.isEmpty(e.getValue().get("cost_price")))
						continue;
					infoList.add(e.getValue());
				}
				
				for(Map<String, String> map:infoList){
					MDataMap parmap = new MDataMap();
					parmap.put("sku_code", map.get("sku_code"));
					String skusql = "select * from pc_skuinfo where sku_code=:sku_code";
					List<Map<String, Object>> skulist = DbUp.upTable("pc_skuinfo").dataSqlList(skusql, parmap);
					if(skulist.size()==0){
						mWebResult.setResultCode(-1);
						mWebResult.setResultMessage("未找到sku编号："+map.get("sku_code"));
						return mWebResult;
					}else{
						String sell_price = skulist.get(0).get("sell_price").toString();
						map.put("sell_price", sell_price);
						map.put("sell_price_old", sell_price);
					}
					try {
						float costprice = Float.parseFloat(map.get("cost_price"));
						float costpriceold = Float.parseFloat(map.get("cost_price_old"));
						if(costprice<0||costprice>costpriceold){
							mWebResult.setResultCode(-1);
							mWebResult.setResultMessage("导入数据错误");
							return mWebResult;
						}
					} catch (Exception e1) {
						mWebResult.setResultCode(-1);
						mWebResult.setResultMessage("导入数据错误");
						return mWebResult;
					}
				}
				
				List<String> templList = new ArrayList();
				for(Map<String, String> map:infoList){
					String product_code = map.get("product_code");
					if(templList.contains(product_code)){
						continue;
					}
					MDataMap param = new MDataMap();
					MDataMap mSubDataMap = new MDataMap();
					mSubDataMap.put("flow_mark", "批量修改成本价操作");					
					param.put("product_code", product_code);
					
					//DbUp.upTable("oc_order_shipments").update(param);
					String updateSql = "SELECT small_seller_code from pc_productinfo where product_code = :product_code";
					String small_seller_code;
					try {
						small_seller_code = DbUp.upTable("pc_productinfo").dataSqlOne(updateSql, param).get("small_seller_code").toString();
					} catch (Exception e1) {
						throw new Exception("商品编码为"+product_code+"的商品小用户编号错误");
					}
					mSubDataMap.put("small_seller_code", small_seller_code);
					List<MDataMap> list = new ArrayList();
					for(Map<String, String> map1:infoList){
						if(map1.get("product_code").equals(product_code)){
							map1.put("start_time", mDataMap1.get("zw_f_start_time"));
							map1.put("end_time", mDataMap1.get("zw_f_end_time"));
							map1.remove("sell_productcode");
							map1.remove("sku_name");
							MDataMap tempMap1 = new MDataMap();
							tempMap1.putAll(map1);
							list.add(tempMap1);
						}else{
							continue;
						}
					}
					list = initDataMap(list);
					if(list != null && !list.isEmpty()){
						/*商品编号*/
						ScFlowMain sfm = new FlowService().getApprovalFlowByOurterCode(list.get(0).get("product_code"), "449717230014");
						if(sfm != null){
							mWebResult.inErrorMessage(941901136);
							return mWebResult;
						}else{
							new ProductSkuPriceService().createProductSkuPriceFlow(list, mSubDataMap, Constants.FLOW_STATUS_SKUPRICE_YY);
						}
					}
					
					templList.add(product_code);
				}				
				
			}else {
				mWebResult.setResultCode(-1);
				mWebResult.setResultMessage("导入文件失败：没有数据");
			}
		} catch (Exception e) {
			e.printStackTrace();
			mWebResult.setResultCode(-1);
			mWebResult.setResultMessage("导入文件失败：" + e.getMessage());
		}		
		return mWebResult;
	}
	

	private List<Map<String, String>> downloadAndAnalysisFile(String fileUrl){
		List<Map<String, String>> list = new ArrayList<>();
		String type = fileUrl.lastIndexOf(".") == -1 ? "" : fileUrl.substring(fileUrl.lastIndexOf(".") + 1);
		Workbook wb = null;
		InputStream fis = null;
		try {
			java.net.URL resourceUrl = new java.net.URL(fileUrl);
			fis = (InputStream) resourceUrl.getContent();
			if ("xlsx".equals(type)) {
				wb = new XSSFWorkbook(fis);
			}else if("xls".equals(type)) {
				wb = new HSSFWorkbook(fis);
			} 
			for (int k = 0; k < wb.getNumberOfSheets(); k++) {
				// 第一个工作表;
				Sheet sheet = wb.getSheetAt(k);
				// 创建集合
				// 工作表的起始行编号
				int firstRowIndex = sheet.getFirstRowNum();
				// 工作表的结束行编号
				int lastRowIndex = sheet.getLastRowNum();
				for (int i = firstRowIndex; i <= lastRowIndex; i++) {
					Row row = sheet.getRow(i);
					if (row != null) {
						Map<String, String> e = new HashMap<>();
						e.put("sell_productcode", getCellValue(row.getCell(0)));
						e.put("product_code", getCellValue(row.getCell(1)));
						e.put("sku_code", getCellValue(row.getCell(2)));
						e.put("sku_name", getCellValue(row.getCell(3)));
						e.put("cost_price_old", getCellValue(row.getCell(4)));
						e.put("cost_price", getCellValue(row.getCell(5)));
						list.add(e);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	
	
	private static String getCellValue(org.apache.poi.ss.usermodel.Cell cell) {
		if (cell != null) {
			// excel表格中数据为字符串
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				return cell.getStringCellValue() != null ? cell.getStringCellValue() : "";
			} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				BigDecimal big = null;
				if (cell.getColumnIndex() == 3) {
					big = new BigDecimal(cell.getNumericCellValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
				} else {
					big = new BigDecimal(cell.getNumericCellValue());
				}

				return big.toString();
			}
		}
		return "";
	}
	
	public List<MDataMap> initDataMap(List<MDataMap> pcFlows) throws Exception {
		List<MDataMap> maps = new ArrayList<MDataMap>();
		for (MDataMap dataMap : pcFlows) {
			BigDecimal costPrice = BigDecimal.valueOf(Double.parseDouble(dataMap.get("cost_price")));
			BigDecimal sellPrice = BigDecimal.valueOf(Double.parseDouble(dataMap.get("sell_price")));
			if(costPrice.compareTo(sellPrice)>=0) {
				throw new Exception("变更后成本价必须小于变更后销售价");
			}
			maps.add(dataMap);
		}
		checkChangeTime(pcFlows);
		return maps;
	}
	
	private void checkChangeTime(List<MDataMap> pcFlows) throws Exception {
		for (MDataMap dataMap : pcFlows) {
			String start_time = String.valueOf(dataMap.get("start_time"));
			String end_time = String.valueOf(dataMap.get("end_time"));
			Date startTime = DateUtil.toDate(start_time);
			Date endTime = DateUtil.toDate(end_time);
			Date nowTime = DateUtil.toDate(DateUtil.getSysDateString());
			if(startTime.compareTo(nowTime)<0) {
				throw new Exception("开始日期必须大于等于当前日期");
			}
			if(endTime.compareTo(nowTime)<0) {
				throw new Exception("结束日期必须大于等于当前日期");
			}
			if(endTime.compareTo(startTime)<0) {
				throw new Exception("开始日期必须小于或等于结束日期");
			}
		}
	}

}