package com.cmall.ordercenter.service.api.reconciliation;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cmall.ordercenter.model.api.reconciliation.input.ImportRedDashedInput;
import com.cmall.ordercenter.model.api.reconciliation.result.ImportRedDashedResult;
import com.cmall.productcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 
 * 类: ImportRedDashed <br>
 * 描述: 支付红冲 <br>
 * 作者: zhy<br>
 * 时间: 2017年3月30日 下午4:32:09
 */
public class ImportRedDashed extends RootApi<ImportRedDashedResult, ImportRedDashedInput> {

	@Override
	public ImportRedDashedResult Process(ImportRedDashedInput inputParam, MDataMap mRequestMap) {
		ImportRedDashedResult result = new ImportRedDashedResult();
		String fileRemoteUrl = inputParam.getUpload();
		String excelType = fileRemoteUrl.substring(fileRemoteUrl.lastIndexOf(".") + 1, fileRemoteUrl.length());
		if (!StringUtils.isEmpty(fileRemoteUrl)) {
			if (StringUtils.equals(excelType, "xlsx") || StringUtils.equals(excelType, "xls")) {
				String lock = null;
				// 读取文件中的数据
				java.net.URL resourceUrl;
				InputStream instream = null;
				List<Map<String, Object>> list = null;
				try {
					lock = WebHelper.addLock(60 * 60 * 3, "import_reddashed");
					if (StringUtils.isBlank(lock)) {
						result.setResultCode(-1);
						result.setResultMessage("正在执行支付红冲操作");
					} else {
						resourceUrl = new java.net.URL(fileRemoteUrl);
						instream = (InputStream) resourceUrl.getContent();
						result = readData(instream, excelType, inputParam);
						if (result.getResultCode() == 1) {
							if (result.getList() != null && result.getList().size() > 0) {
								list = result.getList();
								ImportRedDashedResult redDashedResult = redDashed(list);
								if (StringUtils.isNotBlank(redDashedResult.getError())) {
									result.setResultCode(-1);
									result.setResultMessage("支付红冲导入重复，请检查" + redDashedResult.getError());
								} else {
									result.setResultCode(1);
									result.setResultMessage("支付红冲成功");
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (StringUtils.isNotBlank(lock)) {
						WebHelper.unLock(lock);
					}
				}
			} else {
				result.setResultCode(-1);
				result.setResultMessage("文件格式错误，上传文件只支持excel");
			}
		}
		return result;
	}

	private ImportRedDashedResult readData(InputStream file, String type, ImportRedDashedInput inputParam) {
		ImportRedDashedResult result = new ImportRedDashedResult();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Workbook wb = null;
		try {
			list = new ArrayList<Map<String, Object>>();
			if (type == "xlsx" || "xlsx".equals(type)) {
				wb = new XSSFWorkbook(file);
			} else {
				wb = new HSSFWorkbook(file);
			}
			// 第一个工作表;
			Sheet sheet = wb.getSheetAt(0);
			// 创建集合
			list = new ArrayList<Map<String, Object>>();
			// 工作表的起始行编号
			int firstRowIndex = sheet.getFirstRowNum();
			// 工作表的结束行编号
			int lastRowIndex = sheet.getLastRowNum();
			// 遍历数据表数据
			for (int i = firstRowIndex + 1; i <= lastRowIndex; i++) {
				Row row = sheet.getRow(i);
				if (row != null) {
					// 订单号
					String order_code = getCellVal(row.getCell(0));
					// 订单金额
					String order_money = getCellVal(row.getCell(1));
					// 验证
					if (StringUtils.isBlank(order_code) && StringUtils.isBlank(order_money)) {
						continue;
					} else if (StringUtils.isBlank(order_code)) {
						result.setResultCode(-1);
						result.setResultMessage("订单号不能为空");
						break;
					} else if (StringUtils.isBlank(order_money)) {
						result.setResultCode(-1);
						result.setResultMessage("订单金额不能为空");
						break;
					}
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("order_code", order_code);
					map.put("order_money", order_money);
					map.put("reconciliation_type", inputParam.getReconciliationType());
					list.add(map);
					/**
					 * 验证订单是否存在，订单金额是否匹配
					 */
					Map<String, Object> data = DbUp.upTable("oc_payment_reconciliation").dataSqlOne(
							"select order_money,reconciliation_money from oc_payment_reconciliation where order_code =:order_code and reconciliation_type=:reconciliation_type",
							new MDataMap("order_code", order_code, "reconciliation_type",
									inputParam.getReconciliationType()));
					if (data != null) {
						BigDecimal orderMoney = BigDecimal.valueOf(Double.valueOf(order_money)).setScale(2,
								BigDecimal.ROUND_HALF_UP);
						BigDecimal dataOrderMoney = BigDecimal
								.valueOf(Double.valueOf(data.get("order_money").toString()))
								.setScale(2, BigDecimal.ROUND_HALF_UP);
						if (!(orderMoney.compareTo(dataOrderMoney) == 0)) {
							result.setResultCode(-1);
							result.setResultMessage(order_code + "订单金额不匹配");
							break;
						}
					} else {
						result.setResultCode(-1);
						result.setResultMessage(order_code + "订单不存在");
						break;
					}
				}
			}
			result.setList(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (result.getList() == null || result.getList().size() <= 0) {
			result.setResultCode(-1);
			result.setResultMessage("支付红冲数据为空");
		}
		return result;
	}

	/**
	 * 
	 * 方法: redDashed <br>
	 * 描述: 执行支付红冲 <br>
	 * 作者: zhy<br>
	 * 时间: 2017年5月12日 上午10:24:55
	 * 
	 * @return
	 */
	private ImportRedDashedResult redDashed(List<Map<String, Object>> list) {
		ImportRedDashedResult result = new ImportRedDashedResult();
		StringBuffer error = new StringBuffer();
		if (list != null && list.size() > 0) {
			for (Map<String, Object> map : list) {
				MDataMap update = new MDataMap(map);
				String sql = "select zid,uid,out_number,order_code from oc_payment_reconciliation where order_code=:order_code and reconciliation_type=:reconciliation_type and red_dashed_status='4497479900030002'";
				Map<String, Object> data = DbUp.upTable("oc_payment_reconciliation").dataSqlOne(sql, update);
				if (data == null) {
					update.put("red_dashed_status", "4497479900030002");
					update.put("red_dashed_time", DateUtil.getSysDateTimeString());
					DbUp.upTable("oc_payment_reconciliation").dataUpdate(update, "red_dashed_status,red_dashed_time",
							"order_code,reconciliation_type");
				} else {
					error.append(data.get("order_code")).append(",");
				}
			}
			if (StringUtils.isBlank(error.toString())) {
				result.setResultCode(1);
				result.setResultMessage("支付红冲成功");
			}
		} else {
			result.setResultCode(-1);
			result.setResultMessage("支付红冲列表为空");
		}
		if (error != null && error.length() > 0) {
			result.setError(error.substring(0, error.length() - 1));
		}
		return result;
	}

	/**
	 * @descriptions 判断表格中数据类型将类型转换为字符串类型
	 * 
	 * @param cell
	 * @return
	 * 
	 * @author zhy
	 * @date 2016年5月13日-下午6:32:49
	 */
	private static String getCellVal(Cell cell) {
		if (cell != null) {
			// excel表格中数据为字符串
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				return cell.getStringCellValue() != null ? cell.getStringCellValue() : "";
			} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				BigDecimal big = new BigDecimal(cell.getNumericCellValue());
				return big.toString();
			}
		}
		return "";
	}
}
