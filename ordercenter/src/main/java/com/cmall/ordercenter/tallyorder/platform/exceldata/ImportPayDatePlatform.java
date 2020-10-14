package com.cmall.ordercenter.tallyorder.platform.exceldata;

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
 * 商户结算5.0-为付款申请单导入付款日期
 * 4497477900060001 : 商品行政待审核
 * 4497477900060002 : 商品行政审核通过
 * 4497477900060003 : 商品行政驳回
 * 4497477900060004 : 财务审核通过
 * 4497477900060005 : 财务驳回
 * 4497477900060006 : 财务已确认
 * 4497477900060007 : 财务已付款
 * 4497477900060008 : 财务反审核
 * 
 * 平台入驻商户结算单状态
 * 4497476900040008 : 未结算
 * 4497476900040009 : 已结算
 * 4497476900040010 : 待审核
 * 4497476900040011 : 已审核
 * @author zht
 */
public class ImportPayDatePlatform extends RootFunc {
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
				mResult.setResultMessage("导入付款日期失败" + e.getLocalizedMessage());
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
			mWebResult.setResultMessage("导入扣费模板失败！");
			e.printStackTrace();
		} catch (IOException e) {
			mWebResult.setResultMessage("导入扣费模板失败！");
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
			String payCode = "";
			try {
				Row row = sheet.getRow(rIndex);
				if(row.getCell(0) == null)
					continue;
				if(row.getCell(0).getCellType()==Cell.CELL_TYPE_NUMERIC){
					BigDecimal big = new BigDecimal(row.getCell(0).getNumericCellValue());
					payCode = big.toString();
				} else if(row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
					payCode = row.getCell(0).getStringCellValue();
				}
				if(StringUtils.isEmpty(payCode)) {
					//payCode为空
					continue;
				}
				
				//付款日期(默认已付款)
				String payDate = "";
				String isPay = "4497477900020001";
				if(row.getCell(4).getCellType()==Cell.CELL_TYPE_NUMERIC){
					if (HSSFDateUtil.isCellDateFormatted(row.getCell(4))) {
						Date date = row.getCell(4).getDateCellValue();
						try {
							payDate = sdf.format(date);
						} catch(Exception e) {
							SimpleDateFormat anotherSdf = new SimpleDateFormat("yyyy/MM/dd");
							payDate = anotherSdf.format(date);
						}
					}
				} else if(row.getCell(4).getCellType() == Cell.CELL_TYPE_STRING) {
					payDate = row.getCell(4).getStringCellValue();
				}

				if(StringUtils.isEmpty(payDate)) {
					//payDate为空,清空付款
					payDate = "";
					isPay = "4497477900020002";
				}
				Map<String,Object> map = DbUp.upTable("oc_bill_apply_payment_pt").dataSqlOne("select settle_codes, pay_code from "
						+ "oc_bill_apply_payment_pt where pay_code='" + payCode + "'", null);
				if(null != map && map.size() > 0) {
					String settleCodes = isEmpty(map.get("settle_codes")) ? "" : map.get("settle_codes").toString();
					//只有已确认的申请单才能导入付款日期
					int count = DbUp.upTable("oc_bill_apply_payment_pt").dataUpdate(new MDataMap("pay_code", payCode, "is_pay",isPay, "pay_time", payDate, "flag", "4497477900060006"),
							"is_pay,pay_time",	"pay_code,flag");
					if(count > 0)
						sbSuccess.append("付款申请单:").append(payCode).append(".导入成功!<br>");
					else
						sbFailure.append("付款申请单:").append(payCode).append(".导入失败!该申请单是否已确认?<br>");
					
				} else {
					//查询不到payCode对应的记录
					sbFailure.append("付款申请单:").append(payCode).append(".导入失败!查询不到该付款申请单<br>");
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				if(!StringUtils.isEmpty(payCode))
					sbFailure.append("付款申请单:").append(payCode).append(".导入失败!").append(e.getLocalizedMessage()).append("<br>");
			}
		}
		return sbSuccess.toString() + sbFailure.toString();
	}
	
	private boolean isEmpty(Object obj) {
		return obj == null || obj.toString().equals("");
	}
}
