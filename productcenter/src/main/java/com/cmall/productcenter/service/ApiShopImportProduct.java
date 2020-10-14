package com.cmall.productcenter.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.cmall.productcenter.model.ShopImportProductImput;
import com.cmall.productcenter.model.ShopImportProductResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webmethod.WebMethod;

/**
 * 商户售后地址导入商品
 * @author lgx
 *
 */
public class ApiShopImportProduct extends RootApi<ShopImportProductResult, ShopImportProductImput> {

	public ShopImportProductResult Process(ShopImportProductImput inputParam,
			MDataMap mRequestMap) {
		
		ShopImportProductResult result = new ShopImportProductResult();
		String fileRemoteUrl = inputParam.getUpload_show();
		if(!StringUtils.endsWith(fileRemoteUrl, ".xls")){
			result.setResultMessage("文件格式不对，请核实！");
			return result;
		}
		String uid = inputParam.getAfter_sale_address_uid();
	    if(!StringUtils.isEmpty(fileRemoteUrl) && !StringUtils.isEmpty(uid)) {
	    	java.net.URL resourceUrl;
			InputStream instream = null;
			
			try {
				resourceUrl = new java.net.URL(fileRemoteUrl);
				instream = (InputStream) resourceUrl.getContent();
				if(null != instream) {
					String readExcelResult = readExcel(instream,uid);
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
	public String readExcel(InputStream input,String uid) {
		String result = "";
		try {
			Workbook wb = null;
			wb = new HSSFWorkbook(input);
			Sheet sheet = wb.getSheetAt(0);// 第一个工作表
			result = importProduct(sheet,uid);
		} catch (FileNotFoundException e) {
			result = "导入商品失败！未找到上传文件";
			e.printStackTrace();
		} catch (IOException e) {
			result = "导入商品失败！" + e.getLocalizedMessage();
			e.printStackTrace();
		}
		return result;
	}
	
	
	private String importProduct(Sheet sheet,String uid) {
		int firstRowIndex = sheet.getFirstRowNum();
		int lastRowIndex = sheet.getLastRowNum();
		// 返回字符串
		StringBuffer sb =  new StringBuffer();
		// 导入的数据不在该商户商品列表（格式、商品编号错误）
		StringBuffer sbNoProduct =  new StringBuffer();
		// 导入的商品已经有售后地址
		StringBuffer sbHaveAddress =  new StringBuffer();
		// 记录可以导入的商品id
		StringBuffer sbProductCodes =  new StringBuffer();
		
		WebMethod webMethod = new WebMethod();
		MUserInfo upUserInfo = webMethod.upUserInfo();
		// 商户id
		String manageCode = upUserInfo.getManageCode();
		
		for (int rIndex = firstRowIndex + 1; rIndex <= lastRowIndex; rIndex++) {
			Row row = sheet.getRow(rIndex);
			//商品编号
			String productCode = getCellValue(row.getCell(0));
			//商品名称
			String productName = getCellValue(row.getCell(1));
			
			if(!"".equals(productCode)) {
				//int count = DbUp.upTable("pc_productinfo").dataCount(" small_seller_code='"+manageCode+"'  AND product_code = '"+productCode+"'", null);
				Map<String, Object> prod = DbUp.upTable("pc_productinfo").dataSqlOne("SELECT * FROM pc_productinfo WHERE  small_seller_code='"+manageCode+"'  AND product_code = '"+productCode+"'", null);
				if(prod != null && prod.size() > 0) {
					if(null != prod.get("after_sale_address_uid") && !"".equals(prod.get("after_sale_address_uid"))) {
						if(!sbHaveAddress.toString().contains(productCode)) {
							// 导入商品有售后地址
							sbHaveAddress.append(productCode+",");
						}
					}else {
						if(!sbProductCodes.toString().contains(productCode)) {
							sbProductCodes.append("'"+productCode+"',");
						}
					}
				}else {
					if(!sbNoProduct.toString().contains(productCode)) {
						// 查询为空则该商户没有该商品
						sbNoProduct.append(productCode+",");
					}
				}
			}
		}
		
		if(sbNoProduct.toString().trim().length()>0) {
			// 存在该商户没有的商品
			sb.append("导入数据错误，请检查导入数据信息:"+sbNoProduct.toString().trim().substring(0,sbNoProduct.toString().trim().length()-1));
		}else if(sbHaveAddress.toString().trim().length()>0) {
			// 导入商品有售后地址
			sb.append("以下商品已经存在售后地址:"+sbHaveAddress.toString().trim().substring(0,sbHaveAddress.toString().trim().length()-1));
		}else {
			if(sbProductCodes.toString().trim().length()>0) {
				// 都是正确数据
				String productCodes = sbProductCodes.toString().trim().substring(0,sbProductCodes.toString().trim().length()-1);
				int dataExec = DbUp.upTable("pc_productinfo").dataExec("UPDATE pc_productinfo SET after_sale_address_uid = '"+uid+"' WHERE product_code in("+productCodes+")", null);
				if(dataExec > 0) {
					sb.append("操作成功");
				}else {
					sb.append("操作失败");					
				}
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