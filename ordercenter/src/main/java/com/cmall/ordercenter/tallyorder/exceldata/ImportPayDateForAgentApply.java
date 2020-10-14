package com.cmall.ordercenter.tallyorder.exceldata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 结算3.0-为提现申请单导入付款日期
 * @author zht
 * 4497484600050005 提现申请单状态，已确认，待付款
 */
public class ImportPayDateForAgentApply extends RootFunc {
//	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
//	private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
//	private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy.MM.dd");
	
	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
	    MDataMap mInputMap = upFieldMap(mDataMap);
	    String fileRemoteUrl = mInputMap.get("pay_time");
		if (mResult.upFlagTrue()) {
			java.net.URL resourceUrl;
			InputStream instream = null;
			try {
				resourceUrl = new java.net.URL(fileRemoteUrl);
				instream = (InputStream) resourceUrl.getContent();
				mResult= readExcel(instream);
			} catch (Exception e) {
				mResult.setResultMessage("导入付款日期失败");
				e.printStackTrace();
			} finally {
				if(null != instream) try { instream.close(); } catch (IOException e) {}
			}
		}
		return mResult;
	}

	/**
	 * 读取Excel数据写入数据库
	 * 
	 * @param file
	 */
	public MWebResult readExcel(InputStream input) {
		MWebResult mWebResult = new MWebResult();
		try {
			Workbook wb = null;
			wb = new HSSFWorkbook(input);
			Sheet sheet = wb.getSheetAt(0);// 第一个工作表
			String result = importDate(sheet);
			mWebResult.setResultMessage(result);
		} catch (FileNotFoundException e) {
			mWebResult.setResultMessage("导入提现申请单模板失败！");
			e.printStackTrace();
		} catch (IOException e) {
			mWebResult.setResultMessage("导入提现申请单模板失败！");
			e.printStackTrace();
		}
		return mWebResult;
	}
	
	private String importDate(Sheet sheet) {
		int firstRowIndex = sheet.getFirstRowNum();
		int lastRowIndex = sheet.getLastRowNum();
		StringBuilder sbSuccess = new StringBuilder();
		StringBuilder sbFailure = new StringBuilder();
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (int rIndex = firstRowIndex + 1; rIndex <= lastRowIndex; rIndex++) {
			//付款编号
			String apply_code = "";
			try {
				Row row = sheet.getRow(rIndex);
				if(row.getCell(0) == null)
					continue;
				if(row.getCell(0).getCellType()==Cell.CELL_TYPE_NUMERIC){
					BigDecimal big = new BigDecimal(row.getCell(0).getNumericCellValue());
					apply_code = big.toString();
				} else if(row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
					apply_code = row.getCell(0).getStringCellValue();
				}
				if(StringUtils.isEmpty(apply_code)) {
					//payCode为空
					continue;
				}
				
				//付款日期(默认已付款)
				String payDate = "";
				if(row.getCell(11).getCellType()==Cell.CELL_TYPE_NUMERIC){
					if (HSSFDateUtil.isCellDateFormatted(row.getCell(11))) {
						Date date = row.getCell(11).getDateCellValue();
						try {
							payDate = sdf.format(date);
						} catch(Exception e) {
							SimpleDateFormat anotherSdf = new SimpleDateFormat("yyyy/MM/dd");
							payDate = anotherSdf.format(date);
						}
					}
				} else if(row.getCell(11).getCellType() == Cell.CELL_TYPE_STRING) {
					payDate = row.getCell(11).getStringCellValue();
				}

				Map<String,Object> map = DbUp.upTable("fh_agent_withdraw").dataSqlOne("select * from "
						+ "fh_agent_withdraw where apply_code='" + apply_code + "'", null);
				if(null != map && map.size() > 0) {
					//只有已确认的申请单才能导入付款日期
					int count = DbUp.upTable("fh_agent_withdraw").dataUpdate(new MDataMap("apply_code", apply_code,"pay_time", payDate, "apply_status", "4497484600050005"),
							"pay_time",	"apply_code,apply_status");
					if(count > 0) {
						sbSuccess.append("提现申请单:").append(apply_code).append(".导入成功!<br>");
					}else {
						if(DbUp.upTable("fh_agent_withdraw").dataUpdate(new MDataMap("apply_code", apply_code,"pay_time", payDate, "apply_status", "4497484600050007"),"pay_time",	"apply_code,apply_status")<=0) {
							sbFailure.append("提现申请单:").append(apply_code).append(".导入失败!该申请单是否已确认?<br>");
						}
					}
				} else {
					//查询不到payCode对应的记录
					sbFailure.append("提现申请单:").append(apply_code).append(".导入失败!查询不到该提现申请单<br>");
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				if(!StringUtils.isEmpty(apply_code))
					sbFailure.append("提现申请单:").append(apply_code).append(".导入失败!").append("<br>");
			}
		}
		return sbSuccess.toString() + sbFailure.toString();
	}
	
	private boolean isEmpty(Object obj) {
		return obj == null || obj.toString().equals("");
	}
}
