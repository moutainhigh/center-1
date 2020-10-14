package com.cmall.ordercenter.tallyorder.exceldata;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.tallyorder.JoinSql;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webexport.RootExport;

public class TestFinal extends RootExport{
	/**
	 * 导出结算单
	 */
	public void export(String sOperateId, HttpServletRequest request,
			HttpServletResponse response) {
		String uid = request.getParameter("zw_f_uid");
		Map<String, String> skumap = DbUp.upTable("oc_bill_finance_amount").oneWhere("uid,start_time,end_time,tuistart,tuiend", "","uid=:uid", "uid", uid);
		String start_time = skumap.get("start_time").toString();
		String end_time = skumap.get("end_time").toString();
		String tuistart = skumap.get("tuistart").toString();
		String tuiend = skumap.get("tuiend").toString();
		try {
			getPaySuccessCodes(start_time, end_time,tuistart, tuiend, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
/**
 * 生成结算单
 * @param map
 * @throws Exception
 */
	private void getSkuInfoDetail(MDataMap map,String start_time, String end_time,String tuiStart,String tuiEnd,HttpServletResponse response) throws Exception {
		// 根据有效的订单编号查询sku 信息
 		String ordersql = JoinSql.getJoinOrderCode(map);
 		MDataMap returnMap=getReturnCodeMapTest(tuiStart, tuiEnd);
		String skuSql = "select order_code,product_code,sku_code,sku_name,sku_price,sku_num,cost_price from oc_orderdetail where order_code " + ordersql;
		//System.out.println();
 		List<Map<String, Object>> list = DbUp.upTable("oc_orderdetail").dataSqlList(skuSql, null);
		//System.out.println("list------" + list.size());
		int no=1;
		HSSFWorkbook wb = null;
		SimpleDateFormat df = null ;
		double sku_num = 0;
		InputStream in = TestFinal.class.getResourceAsStream("/finalBill.xls");
        wb=new HSSFWorkbook(in); 
        Sheet sheet = wb.getSheetAt(0);
        Row row=sheet.getRow(0);  //获取第一行（excel中的行默认从0开始，所以这就是为什么，一个excel必须有字段列头），即，字段列头，便于赋值   
        FileOutputStream out=new FileOutputStream("finalBill.xls");  //向d://test.xls中写数据  
        df = new SimpleDateFormat("yyyyMMdd");
		for (Map<String, Object> map2 : list) {
			String order_code = map2.get("order_code").toString();  
			String product_code = map2.get("product_code").toString();
			String sku_code = map2.get("sku_code").toString();
			String sku_name=map2.get("sku_name").toString();
			//double sku_price = Double.valueOf(map2.get("sku_price").toString());
			double success_sku_num = Double.valueOf(map2.get("sku_num").toString());
			double cost_price = Double.valueOf(map2.get("cost_price").toString());
			//sku售价
			Map<String, String> skumap=DbUp.upTable("pc_skuinfo").oneWhere("sell_price,sku_code","","sku_code=:sku_code","sku_code",sku_code);
			double sell_price=Double.valueOf(skumap.get("sell_price").toString());
			//商品信息
			Map<String, String> productMap =DbUp.upTable("pc_productinfo").oneWhere("product_code,product_name,tax_rate,small_seller_code", "", "product_code=:product_code", "product_code",product_code);
			String product_name = productMap.get("product_name").toString();
			double tax_rate = Double.valueOf(productMap.get("tax_rate").toString());
			String small_seller_code = productMap.get("small_seller_code").toString();
			//获取商户的信息
			//System.out.println();
			Map<String, String> sellerInfoMap=DbUp.upTable("uc_seller_info_extend").oneWhere("seller_company_name,account_line,bank_account","","small_seller_code=:small_seller_code","small_seller_code",small_seller_code);
			String seller_company_name = sellerInfoMap.get("seller_company_name")==null?"":sellerInfoMap.get("seller_company_name").toString();
			String account_line = sellerInfoMap.get("account_line").toString();
			String bank_account = sellerInfoMap.get("bank_account").toString();
			//获取退款的sku数量
			double return_sku_num ;
			if(returnMap.containsKey(order_code)){
				return_sku_num=sku_num;
			}else{
				return_sku_num =  Double.valueOf(0.00);
			}
			//扣费信息
			double sale_price=0;
			double postage=0;
			double others=0;
			double manage_money=0;
			double add_pay_money=0;
			String other_pay_reason="";
			Map<String, String> feemoneyMap=DbUp.upTable("oc_bill_free_import").oneWhere("product_code,sku_code,small_seller_code,sale_price,postage,manage_money,others,add_pay_money,other_pay_reason","",
					"small_seller_code=:small_seller_code and product_code =:product_code and sku_code=:sku_code ","small_seller_code",small_seller_code,"product_code",product_code,"sku_code",sku_code);
			if(feemoneyMap!=null){
				 sale_price=Double.valueOf(feemoneyMap.get("sale_price").toString());
				 postage=Double.valueOf(feemoneyMap.get("postage").toString());
				 manage_money=Double.valueOf(feemoneyMap.get("manage_money").toString());
				 others=Double.valueOf(feemoneyMap.get("others").toString());
				 add_pay_money=Double.valueOf(feemoneyMap.get("add_pay_money").toString());
				 other_pay_reason=feemoneyMap.get("other_pay_reason").toString();
			}
			//File file = new File("d:/xx1.xls");
	        //FileInputStream fs=new FileInputStream(file);  //获取d://test.xls 
			
	     //   FileOutputStream out1=new FileOutputStream("d:/"+df.format(new Date()).toString()+".xls");
	        //创建一个样式
	        HSSFCellStyle style = wb.createCellStyle();
	        //设置边框样式
	        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
	        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
	        //设置边框颜色
//	        style.setTopBorderColor(HSSFColor.BLACK.index);
//	        style.setBottomBorderColor(HSSFColor.BLACK.index);
//	        style.setLeftBorderColor(HSSFColor.BLACK.index);
//	        style.setRightBorderColor(HSSFColor.BLACK.index);
//	        HSSFCell cell= (HSSFCell) row.createCell(0);
//	        cell.setCellStyle(style);
//	        cell.setCellValue(no); //设置第一个（从0开始）单元格的数据   
	        int columnNum=sheet.getRow(0).getPhysicalNumberOfCells();
	        int lastRowIndex = sheet.getLastRowNum();
	        //System.out.println("总行数-----"+lastRowIndex);
	       // System.out.println("总列数-------"+columnNum);
	        int startrow=lastRowIndex;
	        row=sheet.createRow((short)startrow+1); //在现有行号后追加数据  
	        row.createCell(0).setCellValue(no);
	        no++;
	        row.createCell(1).setCellValue("常规"); //设置第二个（从0开始）单元格的数据   
	        row.createCell(2).setCellValue("1000001");
	        row.createCell(3).setCellValue(product_code);
	        row.createCell(4).setCellValue(product_name);
	        row.createCell(5).setCellValue(sku_code);
	        row.createCell(6).setCellValue(sku_name);
	        row.createCell(7).setCellValue(cost_price);
	        row.createCell(8).setCellValue(sell_price);
	        row.createCell(9).setCellValue("");//商品合同签署
	        row.createCell(10).setCellValue("");//商品调编
	        row.createCell(11).setCellValue(small_seller_code);
	        row.createCell(12).setCellValue(seller_company_name);
	        row.createCell(13).setCellValue(account_line);
	        row.createCell(14).setCellValue(bank_account);
	        row.createCell(15).setCellValue("");//供应商级别
	        row.createCell(16).setCellValue("");//级别比率
	        row.createCell(17).setCellValue("");
	        row.createCell(17).setCellValue(success_sku_num);//妥投数量
	        row.createCell(18).setCellValue("");//妥投金额
	        row.createCell(19).setCellValue(return_sku_num);//销退数量
	        row.createCell(20).setCellValue("");//消退金额
	        row.createCell(21).setCellValue(sku_num);//本单结算总数量
	        row.createCell(22).setCellValue(sku_num * cost_price);//总金额
	        row.createCell(23).setCellValue("");//最大质保
	        row.createCell(24).setCellValue("");//已扣质保
	        row.createCell(25).setCellValue("");//本期质保
	        row.createCell(26).setCellValue(sale_price);//促销费用
	        row.createCell(27).setCellValue(postage);//邮费
	        row.createCell(28).setCellValue(manage_money);//平台
	        row.createCell(29).setCellValue(others);//其他
	        row.createCell(30).setCellValue(add_pay_money);//附加
	        row.createCell(31).setCellValue(tax_rate);//税率
	        row.createCell(32).setCellValue((sku_num * cost_price)/(1+tax_rate)*tax_rate);//j进项税金小计
	        row.createCell(33).setCellValue((sku_num * cost_price)-(sale_price+postage+manage_money+others+add_pay_money));//合计
	        row.createCell(34).setCellValue(other_pay_reason);//其他扣费原因
			//System.out.println("---------------------------------ok---------------");
		} 
		//Date date1=start_time.
		  
		//SimpleDateFormat sdf1=new SimpleDateFormat("yyyyMMdd");//小写的mm表示的是分钟  
		//java.util.Date date1=sdf1.parse(start_time);
		//java.util.Date date2=sdf1.parse(end_time);
		sheet.getRow(0).getCell(0).setCellValue(start_time.substring(0, 10)+"至"+end_time.substring(0, 10)+"商品结算报表");
	    out.flush();  
        wb.write(out); 
        out.close();
		response.setContentType("application/vnd.ms-excel");    
        response.setHeader("Content-disposition", "attachment;filename="+df.format(new Date()).toString()+".xls");    
        OutputStream ouputStream = response.getOutputStream();  
        ouputStream.flush();   
        wb.write(ouputStream);    
        ouputStream.close();
		removeRow();
	}
	
	/**
	 * 清空模板商品保留原始模板
	 * @throws Exception
	 */
	public static void removeRow() throws Exception {  
	   // File file = new File("d:/xx1.xls");
       // FileInputStream fs=new FileInputStream(file);  //获取d://test.xls 
		InputStream in = TestFinal.class.getResourceAsStream("/finalBill.xls");
        HSSFWorkbook wb=new HSSFWorkbook(in); 
        Sheet sheet1 = wb.getSheetAt(0);
        int lastRowIndex = sheet1.getLastRowNum(); 
        for (int i = 3; i <= lastRowIndex; i++) {
			HSSFRow removingRow = (HSSFRow) sheet1.getRow(i);
			sheet1.removeRow(removingRow);
		}
        FileOutputStream os = new FileOutputStream("/finalBill.xls");
		wb.write(os);
		os.close();
	}
	
	//付款成功的订单号
	private MDataMap getPaySuccessCodes(String start_time, String end_time,String tuiStart,String tuiEnd,HttpServletResponse response) throws Exception {
		MDataMap map = new MDataMap();
		try {
			map.put("create_time_from", start_time);
			map.put("create_time_end", end_time);
			map.put("status", "4497153900010005");
			map.put("pay_type", "449716200001");
			List<Map<String, Object>> list = DbUp.upTable("lc_orderstatus")
					.dataSqlList("select a.code,a.create_time from logcenter.lc_orderstatus a,ordercenter.oc_orderinfo b "
									+ "where a.code=b.order_code and b.seller_code='SI2003' and b.small_seller_code like 'SF%' and b.pay_type=:pay_type "
									+ "and a.create_time>=:create_time_from and a.create_time<=:create_time_end and now_status=:status",map);
			map.clear();
			if (!list.isEmpty()) {
				Iterator<Map<String, Object>> iterator = list.iterator();
				while (iterator.hasNext()) {
					Map<String, Object> m = iterator.next();
					map.put(m.get("code").toString(), m.get("create_time").toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//getReturnCodeMap("2014-02-11 00:00:00", "2014-02-23 17:40:31",map);
		getSkuInfoDetail(map, start_time,end_time,tuiStart,tuiEnd,response);
		return map;
	}

	/**
	 * 获取退货成功的单号
	 * @param start_time
	 * @param end_time
	 * @return
	 */
	private MDataMap getReturnCodeMapTest(String start_time, String end_time) {
		MDataMap map = new MDataMap();
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("create_time_from", start_time);
		mDataMap.put("create_time_end", end_time);
		mDataMap.put("status", "4497465200190002");//退款成功
		// 在退货单中 根据订单号查询 已退款的订单
		String sql = "select order_code from oc_return_goods where create_time>=:create_time_from and create_time<=:create_time_end and status=:status ";
		List<Map<String, Object>> list = DbUp.upTable("oc_return_goods").dataSqlList(sql, mDataMap);
		//System.out.println("returnsize-----"+list.size());
		for (Map<String, Object> map1 : list) {
			String order_code = map1.get("order_code").toString();
				map.put(order_code, order_code);
		}
		return map;
	}
	
	public static void main(String[] args) throws Exception {
	FinalStatementExport ff=new FinalStatementExport();
		//ff.getPaySuccessCodes("2014-02-11 00:00:00", "2014-02-23 17:40:31");
	}


}
