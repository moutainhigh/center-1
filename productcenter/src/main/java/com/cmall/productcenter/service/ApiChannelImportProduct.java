package com.cmall.productcenter.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.cmall.productcenter.model.ChannelImportProductImput;
import com.cmall.productcenter.model.ChannelImportProductResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * 渠道商品导入
 * @author lgx
 *
 */
public class ApiChannelImportProduct extends RootApi<ChannelImportProductResult, ChannelImportProductImput> {

	public ChannelImportProductResult Process(ChannelImportProductImput inputParam,
			MDataMap mRequestMap) {
		
		ChannelImportProductResult result = new ChannelImportProductResult();
		String fileRemoteUrl = inputParam.getUpload_show();
		if(!StringUtils.endsWith(fileRemoteUrl, ".xls")){
			result.setResultMessage("文件格式不对，请核实！");
			return result;
		}
	    if(!StringUtils.isEmpty(fileRemoteUrl)) {
	    	java.net.URL resourceUrl;
			InputStream instream = null;
			
			try {
				resourceUrl = new java.net.URL(fileRemoteUrl);
				instream = (InputStream) resourceUrl.getContent();
				if(null != instream) {
					String readExcelResult = readExcel(instream);
					if(!"操作成功".equals(readExcelResult)) {
						result.setResultCode(-1);
					}
					result.setResultMessage(readExcelResult);
				}
			} catch (Exception e) {
				result.setResultCode(-1);
				result.setResultMessage("导入商品失败:" + e.getLocalizedMessage());
				e.printStackTrace();
			} finally {
				if(null != instream) try { instream.close(); } catch (IOException e) {}
			}
			
	    }else {
	    	result.setResultCode(-1);
	    	result.setResultMessage("商品导入失败");
	    }
		return result;
	}
	
	
	/**
	 * 读取Excel商品数据
	 * 
	 * @param file
	 */
	public String readExcel(InputStream input) {
		String result = "";
		try {
			Workbook wb = null;
			wb = new HSSFWorkbook(input);
			Sheet sheet = wb.getSheetAt(0);// 第一个工作表
			result = importProduct(sheet);
		} catch (FileNotFoundException e) {
			result = "导入商品失败！未找到上传文件";
			e.printStackTrace();
		} catch (IOException e) {
			result = "导入商品失败！" + e.getLocalizedMessage();
			e.printStackTrace();
		}
		return result;
	}
	
	
	private String importProduct(Sheet sheet) {
		int firstRowIndex = sheet.getFirstRowNum();
		int lastRowIndex = sheet.getLastRowNum();
		// 返回字符串
		StringBuffer sb =  new StringBuffer();
		// 导入的product_code和sku_code查不到商品（格式、编号错误）
		StringBuffer noSku =  new StringBuffer();
		// 导入商品的sku_code重复
		StringBuffer chongfuSku =  new StringBuffer();
		// 导入的product_code和sku_code已经存在
		StringBuffer cunzaiSku =  new StringBuffer();
		// 供货价比例填写有误
		StringBuffer supplyErrorSku =  new StringBuffer();
		// 非 普通商户 商品
		StringBuffer noPutongSku =  new StringBuffer();
		// 全部的sku_code
		StringBuffer allSku =  new StringBuffer();
		// 可以导入的商品map集合
		List<MDataMap> listMap = new ArrayList<MDataMap>();
		// SKU编码没填写
		StringBuffer kongSku =  new StringBuffer();
				
		for (int rIndex = firstRowIndex + 1; rIndex <= lastRowIndex; rIndex++) {
			Row row = sheet.getRow(rIndex);
			// 商品编码
			String product_code = "";
			// SKU编码
			String sku_code = getCellValue(row.getCell(0));
			// 商品名称
			String product_name = "";
			// 供货价比例(%)
			String supply_price_proportion = getCellValue(row.getCell(1));
			
			if(!"".equals(sku_code) && !"".equals(supply_price_proportion)) {
				// 检查product_code和sku_code是否有对应商品
				Map<String, Object> skuMap = DbUp.upTable("pc_skuinfo").dataSqlOne("SELECT * FROM pc_skuinfo WHERE  sku_code = '"+sku_code+"'",new MDataMap());
				if(skuMap == null) {
					noSku.append(sku_code+",");
				}else {
					product_code = MapUtils.getString(skuMap, "product_code");
					product_name = MapUtils.getString(skuMap, "sku_name");
					// 检查导入商品的sku_code是否有重复
					if(allSku.toString().contains(sku_code)) {
						chongfuSku.append(sku_code+",");
					}else {
						// 检查导入的product_code和sku_code是否已经存在
						int dataCount = DbUp.upTable("pc_channel_productinfo").dataCount("sku_code = '"+sku_code+"' and is_delete = 0", new MDataMap());
						if(dataCount > 0) {
							cunzaiSku.append(sku_code+",");
						}else {
							// 检查供货价比例填写是否正确
							Pattern pattern = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){1,2})?$"); // 判断小数点后2位的数字的正则表达式
							Matcher match = pattern.matcher(supply_price_proportion);
							if(!match.matches()) {
								supplyErrorSku.append(sku_code+",");
							}else {
								// 检查导入商品是否是 普通商户 商品
								Map<String, Object> product = DbUp.upTable("pc_productinfo").dataSqlOne("SELECT small_seller_code FROM pc_productinfo WHERE product_code = '"+product_code+"'", new MDataMap());
								if(product != null) {
									if("SI2003".equals(product.get("small_seller_code")) || "SF031JDSC".equals(product.get("small_seller_code")) || "SF03WYKLPT".equals(product.get("small_seller_code"))) {
										// 排除 LD,京东,考拉 商品
										noPutongSku.append(sku_code+",");
									}else {
										Map<String, Object> seller_info = DbUp.upTable("uc_seller_info_extend").dataSqlOne("SELECT uc_seller_type FROM uc_seller_info_extend WHERE small_seller_code = '"+product.get("small_seller_code")+"'", new MDataMap());
										if(seller_info != null) {
											if("4497478100050001".equals(seller_info.get("uc_seller_type"))) {
												Map<String, Object> productinfo_ext = DbUp.upTable("pc_productinfo_ext").dataSqlOne("SELECT delivery_store_type FROM pc_productinfo_ext WHERE product_code = '"+product_code+"'", new MDataMap());
												if(productinfo_ext != null) {
													if("4497471600430001".equals(productinfo_ext.get("delivery_store_type"))) {
														MDataMap channelProdMap = new MDataMap();
														channelProdMap.put("product_code", product_code);
														channelProdMap.put("sku_code", sku_code);
														channelProdMap.put("product_name", product_name);
														channelProdMap.put("supply_price_proportion", supply_price_proportion);
														listMap.add(channelProdMap);
													}else {
														// 排除 多货主 商品
														noPutongSku.append(sku_code+",");
													}
												}
											}else {
												// 排除 跨境商户,跨境直邮,平台入驻,缤纷商户 商品
												noPutongSku.append(sku_code+",");
											}
										}else {
											noPutongSku.append(sku_code+",");
										}
									}
								}
							}
						}
					}
				}
				// 全部的sku_code
				allSku.append(sku_code+",");
			}else if("".equals(sku_code) && !"".equals(supply_price_proportion)){
				if(kongSku.toString().trim().length() == 0) {					
					kongSku.append("文件中有未填写SKU编码对应供货价比例的情况");
				}
			}else if(!"".equals(sku_code) && "".equals(supply_price_proportion)){
				supplyErrorSku.append(sku_code+",");
			}
		}
		
		if(noSku.toString().trim().length()>0) {
			// 导入的product_code和sku_code查不到商品（格式、编号错误）
			sb.append("以下SKU编码根据商品编码查不到对应商品:"+noSku.toString().trim().substring(0,noSku.toString().trim().length()-1)+";");
		}
		if(chongfuSku.toString().trim().length()>0) {
			// 导入商品的sku_code重复
			sb.append("以下SKU编码在文件中重复:"+chongfuSku.toString().trim().substring(0,chongfuSku.toString().trim().length()-1)+";");
		}
		if(cunzaiSku.toString().trim().length()>0) {
			// 导入的product_code和sku_code已经存在
			sb.append("以下SKU编码在商品池已经存在:"+cunzaiSku.toString().trim().substring(0,cunzaiSku.toString().trim().length()-1)+";");
		}
		if(supplyErrorSku.toString().trim().length()>0) {
			// 供货价比例填写有误
			sb.append("以下SKU编码供货价比例填写有误:"+supplyErrorSku.toString().trim().substring(0,supplyErrorSku.toString().trim().length()-1)+";");
		}
		if(noPutongSku.toString().trim().length()>0) {
			// 非 普通商户 商品
			sb.append("以下SKU编码不是普通商户商品:"+noPutongSku.toString().trim().substring(0,noPutongSku.toString().trim().length()-1)+";");
		}
		if(kongSku.toString().trim().length()>0) {
			// SKU编码没填写
			sb.append(kongSku.toString().trim()+";");
		}
		String upDateTime = FormatHelper.upDateTime();
		if(sb.toString().trim().length()==0) {
			if(listMap.size() > 0) {
				for (MDataMap mDataMap : listMap) {
					mDataMap.put("create_time", upDateTime);
					mDataMap.put("update_time", upDateTime);
					DbUp.upTable("pc_channel_productinfo").dataInsert(mDataMap);
					
					// 往 lc_channel_product_register_log 插入导入日志数据
					MDataMap map = new MDataMap();
					map.put("sku_code", mDataMap.get("sku_code"));
					map.put("product_name", mDataMap.get("product_name"));
					map.put("register_time", upDateTime);
					map.put("register_person", UserFactory.INSTANCE.create().getUserCode());
					map.put("register_name", "导入");
					map.put("remark", UserFactory.INSTANCE.create().getRealName()+"导入商品");
					DbUp.upTable("lc_channel_product_register_log").dataInsert(map);
					
				}
				sb.append("操作成功");
			}else {
				sb.append("导入文件为空");
			}
		}
		
		return sb.toString();
	}
	
	
	private String getCellValue(Cell cell) {
        String cellValue = "";
        DataFormatter formatter = new DataFormatter();
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        cellValue = formatter.formatCellValue(cell);
                    } else {
                        BigDecimal value = new BigDecimal(cell.getNumericCellValue());
                       
                        cellValue = value.toString();
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    cellValue = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    cellValue = String.valueOf(cell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    cellValue = String.valueOf(cell.getCellFormula());
                    break;
                case Cell.CELL_TYPE_BLANK:
                    cellValue = "";
                    break;
                case Cell.CELL_TYPE_ERROR:
                    cellValue = "";
                    break;
                default:
                    cellValue = cell.toString().trim();
                    break;
            }
        }
        return cellValue.trim();
    }
	
}