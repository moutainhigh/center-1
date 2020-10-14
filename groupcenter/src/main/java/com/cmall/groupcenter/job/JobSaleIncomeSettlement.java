package com.cmall.groupcenter.job;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.quartz.JobExecutionContext;

import com.cmall.dborm.txmodel.groupcenter.GcTraderDepositBalance;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.webmethod.WebUpload;
import com.srnpr.zapweb.webmodel.MPageData;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * @author huangs
 *
 */
public class JobSaleIncomeSettlement extends RootJob {
	
	public static final String DATE_FORMAT_DATETIME = "yyyy-MM-dd 00:00:00"; // 年/月/日
	public static final SimpleDateFormat dateTime = new SimpleDateFormat(DATE_FORMAT_DATETIME);
	
	public static final String DATE_FORMAT_YEARMONTH = "yyyy-MM"; // 年/月/日
	public static final SimpleDateFormat yearMonth = new SimpleDateFormat(DATE_FORMAT_YEARMONTH);

	@Override
	public void doExecute(JobExecutionContext context) {
		
		// 当前时间减一个月获取上月时间
		Timestamp time = DateUtil.addMonths(DateUtil.getSysDateTimestamp(), -1);
		// 上个月第一天
		String startDate=DateUtil.toString(DateUtil.getFirstDayOfMonth(time), dateTime);
//		String startDate="2015-08 00:00:00";
		// 这个月第一天
		String endDate=DateUtil.toString(DateUtil.getFirstDayOfMonth(DateUtil.getSysDateTimestamp()), dateTime);
//		String endDate="2015-09 00:00:00";
		// 上个月(年-月)
		String balanceDate = DateUtil.toString(time,yearMonth);
//		String balanceDate = "2015-08";
		// 获取所有商家信息
		List<MDataMap> traderInfoList = DbUp.upTable("gc_trader_info").queryAll("", "", "", new MDataMap());

		for (MDataMap trader : traderInfoList) {
			
			// 该商户是否有数据或是否可再生成数据
			MDataMap dataMap = DbUp.upTable("gc_trader_vpay_balance").one("trader_code", trader.get("trader_code"), "blance_date",balanceDate, "blance_status", "1");
			
			if (dataMap == null || dataMap.get("create_flag").equals("1")) {
				//查询的map
				MDataMap balanceMap = new MDataMap();
				balanceMap.put("start_date", startDate);
				balanceMap.put("end_date", endDate);
				balanceMap.put("trader_code", trader.get("trader_code"));
				balanceMap.put("trader_name", trader.get("trader_name"));
				// 详情数据
				String link=getDataDetail(balanceMap);
				
				if(link!=null){
					//结算数据DataMap	
					MDataMap mDataMap = new MDataMap();
					//初始化数据
					int paySuccessCount = 0;// 支付成功量
					int backGoodsSuccessCount = 0;// 退货成功量
					BigDecimal paySuccessAmount = BigDecimal.ZERO;// 支付金额
					BigDecimal backGoodsSuccessAmount = BigDecimal.ZERO;// 退款金额
					BigDecimal paySumMoney = BigDecimal.ZERO;// 支付总金额
					
					// 查询支付成功量：(结算周期内余额支付成功数量总和);支付金额：(结算周期内支付成功总金额)
					String paySuccessSql = "select count(trade_code) as count,ifnull(sum(trade_money),0) as money from gc_vpay_order where  trade_status='4497465200190001' "
							+ " and create_time >= :start_date and create_time < :end_date and business_code = :trader_code order by create_time";
					Map<String, Object> paySuccess = DbUp.upTable("gc_vpay_order").dataSqlOne(paySuccessSql, balanceMap);
					paySuccessCount += Integer.parseInt(paySuccess.get("count").toString());
					paySuccessAmount = paySuccessAmount.add(new BigDecimal(paySuccess.get("money").toString()).abs());
					
					mDataMap.put("pay_order_count", paySuccessCount + "");
					mDataMap.put("pay_money", paySuccessAmount + "");
	
					// 查询退货成功量(结算周期内余额退款成功数量总和，标记为负数);退款金额(结算周期内退款成功总金额)。
					String backGoodsSuccessSql = "select count(DISTINCT(business_order_code)) as count,ifnull(sum(trade_money),0) as money from gc_vpay_order where  trade_status='4497465200190002' "
							+ " and refund_time >= :start_date and refund_time < :end_date "
							+ " and business_code= :trader_code order by refund_time";
					Map<String, Object> backGoodsSuccess = DbUp.upTable("gc_vpay_order").dataSqlOne(backGoodsSuccessSql,balanceMap);
					backGoodsSuccessCount += Integer.parseInt(backGoodsSuccess.get("count").toString());
					backGoodsSuccessAmount = backGoodsSuccessAmount.add(new BigDecimal(backGoodsSuccess.get("money").toString()).abs());
					
					mDataMap.put("return_order_count", backGoodsSuccessCount + "");
					mDataMap.put("return_money", backGoodsSuccessAmount+"");
	
					// 查询支付总金额：(结算周期内支付成功总金额和退款成功总金额)。
					paySumMoney = paySuccessAmount.subtract(backGoodsSuccessAmount);
					
					mDataMap.put("pay_sum_money", paySumMoney.toString());
					mDataMap.put("create_time", DateUtil.getSysDateTimeString());
					mDataMap.put("trader_code", trader.get("trader_code"));
					mDataMap.put("trader_name", trader.get("trader_name"));
					mDataMap.put("blance_date", balanceDate);
					mDataMap.put("detail_link", link);
					DbUp.upTable("gc_trader_vpay_balance").dataInsert(mDataMap);
				
					//如有旧有效数据，更改状态为无效
					if(dataMap!=null){
						MDataMap mDataMap2 = new MDataMap();
						mDataMap2.put("zid",dataMap.get("zid"));
						mDataMap2.put("blance_status","0");
						mDataMap2.put("create_flag","0");
						DbUp.upTable("gc_trader_vpay_balance").dataUpdate(mDataMap2, "blance_status,create_flag", "zid");
					}
				}
			}
		}
	}
	
	/**
	 * 获取详情数据（生成excel上传服务器）
	 * @param balanceMap 查询条件
	 * @return  上传文件的路径
	 */
	private String getDataDetail(MDataMap balanceMap) {
		//查询上月该商户下交易状态为支付成功订单信息
		String payQueryStr = " create_time,business_order_create_time,account_code,member_code,trade_type,trade_code,business_order_code,trade_money,trade_status ";
		String payWhere = " business_code =:trader_code and trade_type='4497465200200001' and create_time >= :start_date and create_time < :end_date ";
		List<Map<String, Object>> payDetailList = DbUp.upTable("gc_vpay_order").dataQuery(payQueryStr, "create_time", payWhere,balanceMap, -1, -1);
		
		//查询上月该商户下交易状态为退款成功订单信息
		String refundQueryStr = " refund_time,business_order_create_time,account_code,member_code,trade_type,trade_code,business_order_code,trade_money,trade_status ";
		String refundWhere = " business_code =:trader_code and trade_type='4497465200200002' and refund_time >= :start_date and refund_time < :end_date ";
		List<Map<String, Object>> refundDetailList = DbUp.upTable("gc_vpay_order").dataQuery(refundQueryStr, "refund_time",refundWhere, balanceMap, -1, -1);
		
		payDetailList.addAll(refundDetailList);
		if(payDetailList!=null&&payDetailList.size()>0){
			String exportName = balanceMap.get("trader_name")+"销售收入结算明细" + "-"
					+ FormatHelper.upDateTime(new Date(), "yyyy-MM-dd-HH-mm-ss");
			MPageData pageData=new MPageData();
			//拼凑表头
			List<String> headList=new ArrayList<String>();
			headList.add("支付时间");
			headList.add("订单创建时间");
			headList.add("账户编号");
			headList.add("用户编号");
			headList.add("类型");
			headList.add("流水号");
			headList.add("订单号");
			headList.add("金额");
			headList.add("状态");
			pageData.setPageHead(headList);
			List<List<String>> newDataList=new ArrayList<List<String>>();
			for(Map<String,Object> detail:payDetailList){
				List<String> oneList=new ArrayList<String>();
				// 支付时间/退款时间
				if(detail.containsKey("create_time") ){
					oneList.add(detail.get("create_time") == null ? "": detail.get("create_time").toString());
				}
				else if(detail.containsKey("refund_time")){
					oneList.add(detail.get("refund_time") == null ? "": detail.get("refund_time").toString());
				}
				// 订单创建时间
				oneList.add(detail.get("business_order_create_time") == null ? "" : detail.get("business_order_create_time").toString());
				// 账户编号
				oneList.add(detail.get("account_code") == null ? "" : detail.get("account_code").toString());
				// 用户编号
				oneList.add(detail.get("member_code") == null ? "" : detail.get("member_code").toString());
				// 类型
				String tradeType = "";
				if ("4497465200200001".equals(detail.get("trade_type"))) {
					tradeType = "支付";
				} else if ("4497465200200002".equals(detail.get("trade_type"))) {
					tradeType = "退款";
				}
				oneList.add(tradeType);
				// 流水号
				oneList.add(detail.get("trade_code") == null ? "" : detail.get("trade_code").toString());
				// 订单号
				oneList.add(detail.get("business_order_code") == null ? "": detail.get("business_order_code").toString());
				// 金额(如果是退款，把金额标记成负数)
				if ("4497465200200002".equals(detail.get("trade_type"))) {
					oneList.add(detail.get("trade_money") == null ? "" : ("-"+detail.get("trade_money")).toString());
				}
				else if("4497465200200001".equals(detail.get("trade_type"))){
					oneList.add(detail.get("trade_money") == null ? "" : detail.get("trade_money").toString());
				}
				// 状态
				String tradeStatus = "";
				if ("4497465200190001".equals(detail.get("trade_status"))) {
					tradeStatus = "支付成功";
				} else if ("4497465200190002".equals(detail.get("trade_status"))) {
					tradeStatus = "退款成功";
				}
				oneList.add(tradeStatus);
				newDataList.add(oneList);
			}
			pageData.setPageData(newDataList);
			return exportExcel(exportName,pageData);
		}
		return null;
	}
	
	/**
	 * 结算数据上传服务器
	 * @param exportName
	 * @param pageData
	 * @return
	 */
	public String exportExcel(String exportName,MPageData pageData){
			
        if (StringUtils.isEmpty(exportName)) {
            exportName = "export-"
                    + FormatHelper
                    .upDateTime(new Date(), "yyyy-MM-dd-HH-mm-ss");
        }
        XSSFWorkbook wb = new XSSFWorkbook();// 建立新HSSFWorkbook对象
        Sheet sheet = wb.createSheet("excel");
        int iNowRow = 0;
        Row headRow = sheet.createRow(iNowRow);
        //定义表头样式
        CellStyle hHeaderStyle=wb.createCellStyle();
        Font font = wb.createFont();
        //加粗
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//加粗
        hHeaderStyle.setFont(font);

        for (int i = 0, j = pageData.getPageHead().size(); i < j; i++) {
            Cell hCell = headRow.createCell(i);
            hCell.setCellValue(pageData.getPageHead().get(i));
            hCell.setCellStyle(hHeaderStyle);

        }

        for (List<String> lRow : pageData.getPageData()) {
            iNowRow++;
            Row hRow = sheet.createRow(iNowRow);
            for (int i = 0, j = lRow.size(); i < j; i++) {
                Cell hCell = hRow.createCell(i);
                hCell.setCellValue(lRow.get(i));
            }
        }
        return uploadExcellFile(wb,exportName);
	}
	/**
	 * 上传excell文件到服务器
	 * @param wb excel
	 * @param exportName excel名称
	 * @return String 上传文件路径
	 */
	private String uploadExcellFile(XSSFWorkbook wb,String exportName) {
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			   wb.write(os);
			   byte[]  b=os.toByteArray();

				String sTarget = "productScale";	
				WebUpload webUpload = new WebUpload();
				String sDate = DateUtil.toString(new Date(), DateUtil.DATE_FORMAT_DATEONLYNOSP);
				MWebResult mBigFile = webUpload.remoteUploadCustom("text.xlsx",b, sTarget, sDate,exportName);
				
		        return mBigFile.getResultObject().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
		}
		return null;
	}

	

	/*public static void main(String[] args) {
		JobSaleIncomeSettlement a = new JobSaleIncomeSettlement();
		a.doExecute(null);
	}*/

}
