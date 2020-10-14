package com.cmall.ordercenter.service.api.reconciliation;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.api.reconciliation.input.ImportReconciliationInput;
import com.cmall.ordercenter.model.api.reconciliation.result.ImportReconciliationResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * 
 * 类: ImportReconciliation <br>
 * 描述: 导入对账单处理接口 <br>
 * 作者: zhy<br>
 * 时间: 2017年3月29日 下午4:15:28
 */
public class ImportReconciliation extends RootApi<ImportReconciliationResult, ImportReconciliationInput> {

	@Override
	public ImportReconciliationResult Process(ImportReconciliationInput inputParam, MDataMap mRequestMap) {
		ImportReconciliationResult result = new ImportReconciliationResult();
		int errorSum = 0;
		String fileRemoteUrl = inputParam.getUpload();
		String excelType = fileRemoteUrl.substring(fileRemoteUrl.lastIndexOf(".") + 1, fileRemoteUrl.length());
		if (!StringUtils.isEmpty(fileRemoteUrl)) {
			if (StringUtils.equals("xlsx", excelType) || StringUtils.equals("xls", excelType)) {
				String lock = null;
				// 读取文件中的数据
				java.net.URL resourceUrl;
				InputStream instream = null;
				List<Map<String, Object>> list = null;
				try {
					lock = WebHelper.addLock(60 * 60 * 3, "import_reconciliation");
					if (StringUtils.isBlank(lock)) {
						result.setResultCode(-1);
						result.setResultMessage("正在执行导入对账单操作");
					} else {
						resourceUrl = new java.net.URL(fileRemoteUrl);
						instream = (InputStream) resourceUrl.getContent();
						result = readData(instream, excelType, inputParam);
						if (result.getResultCode() == 1) {
							errorSum = result.getErrorSum();
							if (errorSum > 0) {
								if (result.getErrors() != null && result.getErrors().size() > 0) {
									insertErrorLog(result.getErrors());
								}
							} else {
								if (result.getList() != null && result.getList().size() > 0) {
									result = verifyData(result.getList());
									if (result.getResultCode() == 1) {
										if (result.getList() != null && result.getList().size() > 0) {
											list = result.getList();
											ImportReconciliationResult intsertResult = insertData(list,
													inputParam.getReconciliationType());
											if (intsertResult.getResultCode() != 1) {
												result = intsertResult;
											}
										}
									}
								} else {
									result.setResultCode(-1);
									result.setResultMessage("对账数据为空");
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
		result.setErrorSum(errorSum);
		return result;
	}

	/**
	 * 
	 * 方法: readData <br>
	 * 作者: zhy<br>
	 * 时间: 2017年3月29日 下午5:28:16
	 * 
	 * @param file
	 * @param type
	 * @param inputParam
	 * @return
	 */
	private ImportReconciliationResult readData(InputStream file, String type, ImportReconciliationInput inputParam) {
		ImportReconciliationResult result = new ImportReconciliationResult();
		List<Map<String, Object>> errors = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 遍历数据表数据
		Integer errorSum = 0;
		Workbook wb = null;
		try {
			if (type == "xlsx" || "xlsx".equals(type)) {
				wb = new XSSFWorkbook(file);
			} else {
				wb = new HSSFWorkbook(file);
			}
			// 第一个工作表;
			Sheet sheet = wb.getSheetAt(0);
			// 工作表的起始行编号
			int firstRowIndex = sheet.getFirstRowNum();
			// 工作表的结束行编号
			int lastRowIndex = sheet.getLastRowNum();
			for (int i = firstRowIndex + 1; i <= lastRowIndex; i++) {
				Row row = sheet.getRow(i);
				if (row != null) {
					// 外部订单存储外部订单号，在线订单存储支付流水号
					String out_number = getCellVal(row.getCell(0));
					// 对账金额
					String reconciliation_money = getCellVal(row.getCell(1));
					// 对账日期
					String reconciliation_time = getCellVal(row.getCell(2));
					// 支付类型
					String pay_type = inputParam.getPayType();
					if (StringUtils.isBlank(out_number) && StringUtils.isBlank(reconciliation_money)
							&& StringUtils.isBlank(reconciliation_time)) {
						continue;
					}
					/**
					 * 验证数据是否存在空值
					 */
					if (StringUtils.isBlank(out_number)) {
						result.setResultCode(-1);
						result.setResultMessage("订单编号不能为空");
						break;
					} else if (StringUtils.isBlank(reconciliation_money)) {
						result.setResultCode(-1);
						result.setResultMessage("对账金额不能为空");
						break;
					} else if (StringUtils.isBlank(reconciliation_time)) {
						result.setResultCode(-1);
						result.setResultMessage("对账日期不能为空");
						break;
					} else if (StringUtils.isNotBlank(reconciliation_time) && !isDate(reconciliation_time)) {
						result.setResultCode(-1);
						result.setResultMessage("对账日期格式不正确，对账日期格式应为yyyy-MM-dd");
						break;
					}
					out_number = out_number.replace(" ", "").replace("\'", "");
					reconciliation_money = reconciliation_money.replace(" ", "");
					reconciliation_time = reconciliation_time.replace(" ", "");
					pay_type = pay_type.replace(" ", "");
					/*
					 * 添加对账类型为代收货款的数据集合到list
					 */
					if (StringUtils.equals(inputParam.getReconciliationType(), "4497479900010001")) {
						ImportReconciliationResult importResult = getOutOrderForImport(out_number, reconciliation_money,
								reconciliation_time, pay_type, inputParam.getReconciliationType());
						errorSum += importResult.getErrorSum();
						result.setResultCode(importResult.getResultCode());
						if (importResult.getErrors() != null && importResult.getErrors().size() > 0) {
							errors.addAll(importResult.getErrors());
						}
						List<Map<String, Object>> subList = importResult.getList();
						if (subList != null && subList.size() > 0) {
							list.addAll(subList);
						}
					} else {
						ImportReconciliationResult onLineResult = getorderForOnLine(out_number, reconciliation_money,
								reconciliation_time, pay_type, inputParam.getReconciliationType());
						result.setResultCode(onLineResult.getResultCode());
						errorSum += onLineResult.getErrorSum();
						if (onLineResult.getErrors() != null && onLineResult.getErrors().size() > 0) {
							errors.addAll(onLineResult.getErrors());
						}
						List<Map<String, Object>> subList = onLineResult.getList();
						if (subList != null && subList.size() > 0) {
							list.addAll(subList);
						}
					}
				}
			}
		} catch (Exception e) {
			result.setResultCode(-1);
			result.setResultMessage("读取文件失败，请稍后重试!");
			e.printStackTrace();
		}
		result.setList(list);
		result.setErrorSum(errorSum);
		result.setErrors(errors);
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
				if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
					Date d = cell.getDateCellValue();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					return sdf.format(d);
				} else {
					return big.toString();
				}
			}
		}
		return "";
	}

	/**
	 * 
	 * 方法: getOutOrderForImport <br>
	 * 描述: 根据外部订单编号获取民生和电视宝订单信息 <br>
	 * 作者: zhy<br>
	 * 时间: 2017年3月29日 下午6:11:12
	 * 
	 * @param orderNumber
	 * @param reconciliation_money
	 * @param reconciliation_time
	 * @param pay_type
	 * @return
	 */
	private ImportReconciliationResult getOutOrderForImport(String orderNumber, String reconciliation_money,
			String reconciliation_time, String pay_type, String reconciliation_type) {
		ImportReconciliationResult result = new ImportReconciliationResult();
		String userName = UserFactory.INSTANCE.create().getLoginName();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> errors = new ArrayList<Map<String, Object>>();
		/**
		 * 根据支付类型查询订单列表信息
		 */
		String sql = "select sum(due_money) as due_money from ordercenter.oc_orderinfo where out_order_code=:out_order_code and pay_type=:pay_type group by out_order_code";
		List<Map<String, Object>> list = DbUp.upTable("oc_orderinfo").dataSqlList(sql,
				new MDataMap("out_order_code", orderNumber, "pay_type", pay_type));
		int errorSum = 0;
		BigDecimal reconciliation_money_db = BigDecimal.valueOf(Double.valueOf(reconciliation_money)).setScale(2,
				BigDecimal.ROUND_HALF_UP);
		if (list != null && list.size() > 0) {
			for (Map<String, Object> map : list) {
				String due_money = map.get("due_money").toString();
				BigDecimal due_money_bd = BigDecimal.valueOf(Double.valueOf(due_money)).setScale(2,
						BigDecimal.ROUND_HALF_UP);
				if (due_money_bd.compareTo(reconciliation_money_db) != 0) {
					errorSum++;
					Map<String, Object> error = new HashMap<String, Object>();
					error.put("out_number", orderNumber);
					error.put("reconciliation_type", reconciliation_type);
					error.put("reconciliation_money", reconciliation_money_db.toString());
					error.put("reconciliation_time", reconciliation_time);
					error.put("pay_type", pay_type);
					error.put("error", "金额错误");
					error.put("create_user", userName);
					error.put("create_time", DateUtil.getSysDateTimeString());
					errors.add(error);
				} else {
					map.put("out_number", orderNumber);
					map.put("reconciliation_type", reconciliation_type);
					map.put("reconciliation_money", reconciliation_money_db.toString());
					map.put("reconciliation_time", reconciliation_time);
					map.put("pay_type", pay_type);
					map.put("create_user", userName);
					map.put("create_time", DateUtil.getSysDateTimeString());
					map.put("update_user", userName);
					map.put("update_time", DateUtil.getSysDateTimeString());
					resultList.add(map);
				}
			}
		} else {
			errorSum++;
			Map<String, Object> error = new HashMap<String, Object>();
			error.put("out_number", orderNumber);
			error.put("reconciliation_type", reconciliation_type);
			error.put("reconciliation_money", reconciliation_money_db.toString());
			error.put("reconciliation_time", reconciliation_time);
			error.put("pay_type", pay_type);
			error.put("error", "订单不匹配");
			error.put("create_user", userName);
			error.put("create_time", DateUtil.getSysDateTimeString());
			errors.add(error);
		}
		result.setErrorSum(errorSum);
		result.setList(resultList);
		result.setErrors(errors);
		return result;
	}

	/**
	 * 
	 * 方法: getorderForOnLine <br>
	 * 描述: 导入在线支付流水号订单 <br>
	 * 作者: zhy<br>
	 * 时间: 2017年3月30日 下午4:20:33
	 * 
	 * @param trade_no
	 * @param reconciliation_money
	 * @param reconciliation_time
	 * @param pay_type
	 * @return
	 */
	private ImportReconciliationResult getorderForOnLine(String trade_no, String reconciliation_money,
			String reconciliation_time, String pay_type, String reconciliation_type) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> errors = new ArrayList<Map<String, Object>>();
		ImportReconciliationResult result = new ImportReconciliationResult();
		String userName = UserFactory.INSTANCE.create().getLoginName();
		List<Map<String, Object>> list = DbUp.upTable("oc_orderinfo").dataSqlList(
				"SELECT SUM(o.due_money) as due_money,up.trade_no,up.pay_type FROM ordercenter.oc_orderinfo AS o,ordercenter.oc_orderinfo_upper_payment AS up WHERE o.big_order_code = up.big_order_code AND up.trade_no=:trade_no group by o.big_order_code",
				new MDataMap("trade_no", trade_no));
		int errorSum = 0;
		BigDecimal reconciliation_money_db = BigDecimal.valueOf(Double.valueOf(reconciliation_money)).setScale(2,
				BigDecimal.ROUND_HALF_UP);
		if (list != null && list.size() > 0) {
			for (Map<String, Object> map : list) {
				map.put("out_number", trade_no);
				map.remove("trade_no");
				map.put("reconciliation_type", reconciliation_type);
				map.put("reconciliation_money", reconciliation_money);
				map.put("reconciliation_time", reconciliation_time);
				map.put("pay_type", pay_type);
				String due_money = map.get("due_money").toString();
				BigDecimal due_money_bd = BigDecimal.valueOf(Double.valueOf(due_money)).setScale(2,
						BigDecimal.ROUND_HALF_UP);
				if (due_money_bd.compareTo(reconciliation_money_db) != 0) {
					errorSum++;
					Map<String, Object> error = new HashMap<String, Object>();
					error.put("out_number", trade_no);
					error.put("reconciliation_type", reconciliation_type);
					error.put("reconciliation_money", reconciliation_money_db.toString());
					error.put("reconciliation_time", reconciliation_time);
					error.put("pay_type", pay_type);
					error.put("error", "金额错误");
					error.put("create_user", userName);
					error.put("create_time", DateUtil.getSysDateTimeString());
					errors.add(error);
				}
				map.put("create_user", userName);
				map.put("create_time", DateUtil.getSysDateTimeString());
				map.put("update_user", userName);
				map.put("update_time", DateUtil.getSysDateTimeString());
				resultList.add(map);
			}
		} else {
			errorSum++;
			Map<String, Object> error = new HashMap<String, Object>();
			error.put("out_number", trade_no);
			error.put("reconciliation_type", reconciliation_type);
			error.put("reconciliation_money", reconciliation_money_db.toString());
			error.put("reconciliation_time", reconciliation_time);
			error.put("pay_type", pay_type);
			error.put("error", "订单不匹配");
			error.put("create_user", userName);
			error.put("create_time", DateUtil.getSysDateTimeString());
			errors.add(error);
		}
		result.setErrorSum(errorSum);
		result.setList(resultList);
		result.setErrors(errors);
		return result;
	}

	/**
	 * 
	 * 方法: insertData <br>
	 * 描述: 添加数据到货款对账单 <br>
	 * 作者: zhy<br>
	 * 时间: 2017年3月30日 下午3:17:43
	 * 
	 * @param list
	 * @return
	 */
	private ImportReconciliationResult insertData(List<Map<String, Object>> list, String reconciliationType) {
		ImportReconciliationResult result = new ImportReconciliationResult();
		try {
			if (list != null && list.size() > 0) {
				for (Map<String, Object> map : list) {
					map.remove("due_money");
					MDataMap data = new MDataMap(map);
					List<Map<String, Object>> orders = null;
					if (StringUtils.equals(reconciliationType, "4497479900010001")) {
						orders = DbUp.upTable("oc_orderinfo").dataSqlList(
								"select order_code,due_money from oc_orderinfo where out_order_code=:out_number and pay_type=:pay_type",
								data);
					} else {
						orders = DbUp.upTable("oc_orderinfo").dataSqlList(
								"SELECT o.order_code,o.due_money FROM ordercenter.oc_orderinfo AS o,ordercenter.oc_orderinfo_upper_payment AS up WHERE o.big_order_code = up.big_order_code AND up.trade_no =:out_number AND up.pay_type=:pay_type",
								data);
					}
					if (orders != null && orders.size() > 0) {
						for (Map<String, Object> map2 : orders) {
							data.put("order_code", map2.get("order_code").toString());
							data.put("due_money", map2.get("due_money").toString());  // 小单上面的金额
							BigDecimal due_money = BigDecimal
									.valueOf(Double.parseDouble(map.get("reconciliation_money").toString()))
									.setScale(2, BigDecimal.ROUND_HALF_UP);
							data.put("order_money", due_money.toString());
							Map<String, Object> flagExists = DbUp.upTable("oc_payment_reconciliation").dataSqlOne(
									"select zid,uid from ordercenter.oc_payment_reconciliation where out_number=:out_number AND order_code=:order_code",
									data);
							if (flagExists == null) {
								DbUp.upTable("oc_payment_reconciliation").dataInsert(data);
							}
						}
					}
				}
				result.setResultCode(1);
				result.setResultMessage("操作成功");
			} else {
				result.setResultCode(-1);
				result.setResultMessage("对账数据为空");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setResultCode(-1);
			result.setResultMessage("导入对账单发生错误，请联系技术人员");
		}
		return result;
	}

	/**
	 * 
	 * 方法: verifyData <br>
	 * 描述: 验证订单是否为重复对账单 <br>
	 * 作者: zhy<br>
	 * 时间: 2017年4月7日 下午5:47:08
	 * 
	 * @param list
	 * @return
	 */
	private ImportReconciliationResult verifyData(List<Map<String, Object>> list) {
		ImportReconciliationResult result = new ImportReconciliationResult();
		if (list != null && list.size() > 0) {
			StringBuffer numbers = new StringBuffer();
			for (Map<String, Object> map : list) {
				String out_number = map.get("out_number").toString();
				String pay_type = map.get("pay_type").toString();
				Map<String, Object> data = DbUp.upTable("oc_payment_reconciliation").dataSqlOne(
						"select count(1) as c from ordercenter.oc_payment_reconciliation where out_number =:out_number and pay_type=:pay_type",
						new MDataMap("out_number", out_number, "pay_type", pay_type));
				if (data.get("c") != null && Integer.valueOf(data.get("c").toString()) > 0) {
					numbers.append(out_number).append(",");
				}
			}
			if (numbers != null && numbers.length() > 1) {
				String str = numbers.substring(0, numbers.length() - 1);
				if (StringUtils.isNotBlank(str)) {
					result.setResultCode(-1);
					result.setResultMessage("对账单导入重复，请检查" + str);
				}
			}
		}
		result.setList(list);
		return result;
	}

	/**
	 * 
	 * 方法: insertErrorLog <br>
	 * 描述: 添加错误日志 <br>
	 * 作者: zhy<br>
	 * 时间: 2017年4月26日 上午10:29:51
	 * 
	 * @param list
	 * @return
	 */
	private void insertErrorLog(List<Map<String, Object>> list) {
		Long code = DateUtil.getSysDateLong();
		if (list != null && list.size() > 0) {
			for (Map<String, Object> map : list) {
				MDataMap data = new MDataMap(map);
				data.put("code", code.toString());
				DbUp.upTable("lc_payment_reconciliation").dataInsert(data);
			}
		}
	}

	/**
	 * 
	 * 方法: isDate <br>
	 * 描述: 判断日期格式是否正确 <br>
	 * 作者: zhy<br>
	 * 时间: 2017年5月10日 下午2:18:16
	 * 
	 * @param time
	 * @return
	 */
	private static boolean isDate(String time) {
		boolean flag = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			sdf.parse(time);
			flag = true;
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
}