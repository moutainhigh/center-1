package com.cmall.ordercenter.tallyorder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webexport.RootExport;

/**
 * 
 * 类: ExportSkuSettleDetailKj <br>
 * 描述: 跨境相关-导出商品结算明细 <br>
 * 作者: zhy<br>
 * 时间: 2017年5月3日 上午9:38:39
 */
public class ExportSkuSettleDetailKj extends RootExport {
	@Override
	public void export(String sOperateId, HttpServletRequest request, HttpServletResponse response) {
		String payCode = request.getParameter("payCode");
		String settleCodeParam = request.getParameter("settleCodes");
		String sellerCode = request.getParameter("sellerCode");
		String sellerName = request.getParameter("sellerName");
		String createTimeFrom = request.getParameter("createTimeFrom");
		String createTimeTo = request.getParameter("createTimeTo");
		String flag = request.getParameter("flag");
		String settleType = request.getParameter("settleType");
		String payTimeFrom = request.getParameter("payTimeFrom");
		String payTimeTo = request.getParameter("payTimeTo");

		String where = "";
		String sql = "select merchant_code, settle_codes as settle_code from oc_bill_apply_payment_kj a where 1=1  ";
		if (!StringUtils.isEmpty(payCode)) {
			where = "and a.pay_code like '%" + payCode + "%' ";
		}
		if (!StringUtils.isEmpty(settleCodeParam)) {
			where += "and a.settle_codes ='" + settleCodeParam + "'";
		}
		if (!StringUtils.isEmpty(sellerCode)) {
			where += "and a.merchant_code like '%" + sellerCode + "%' ";
		}
		if (!StringUtils.isEmpty(sellerName)) {
			where += "and a.merchant_name like '%" + sellerName + "%' ";
		}
		if (!StringUtils.isEmpty(createTimeFrom) && !StringUtils.isEmpty(createTimeTo)) {
			where += "and a.create_time >= '" + createTimeFrom + "' and a.create_time <= '" + createTimeTo + "' ";
		} else if (!StringUtils.isEmpty(createTimeFrom)) {
			where += "and a.create_time >= '" + createTimeFrom + "' ";
		} else if (!StringUtils.isEmpty(createTimeTo)) {
			where += "and a.create_time <= '" + createTimeTo + "' ";
		}
		if (!StringUtils.isEmpty(flag)) {
			where += "and a.flag ='" + flag + "'";
		}
		if (!StringUtils.isEmpty(settleType)) {
			where += "and a.settle_type='" + settleType + "'";
		}
		if (!StringUtils.isEmpty(payTimeFrom) && !StringUtils.isEmpty(payTimeTo)) {
			where += "and a.pay_time >= '" + payTimeFrom + "' and a.pay_time <= '" + payTimeTo + "' ";
		} else if (!StringUtils.isEmpty(payTimeFrom)) {
			where += "and a.pay_time >= '" + payTimeFrom + "' ";
		} else if (!StringUtils.isEmpty(payTimeTo)) {
			where += "and a.pay_time <= '" + payTimeTo + "' ";
		}
		List<Map<String, String>> condi = new ArrayList<Map<String, String>>();
		List<Map<String, Object>> applyList = DbUp.upTable("oc_bill_apply_payment_kj").dataSqlList(sql + where, null);
		if (null != applyList && applyList.size() > 0) {
			for (Map<String, Object> map : applyList) {
				String settleCode = isEmpty(map.get("settle_code")) ? "" : map.get("settle_code").toString();
				String merchant_code = isEmpty(map.get("merchant_code")) ? "" : map.get("merchant_code").toString();
				if (StringUtils.isEmpty(settleCode) || StringUtils.isEmpty(merchant_code))
					continue;

				Map<String, String> conMap = new HashMap<String, String>();
				settleCode = settleCode.replace(",", "','");
				conMap.put(merchant_code, settleCode);
				condi.add(conMap);
			}
		}

		StringBuffer sb = new StringBuffer();
		for (Map<String, String> conMap : condi) {
			Set<Entry<String, String>> entrys = conMap.entrySet();
			Iterator<Entry<String, String>> it = entrys.iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				String smallSellerCode = entry.getKey();
				String settleCodes = entry.getValue();
				if (sb.length() == 0)
					sb.append("(mc.settle_code IN ('" + settleCodes + "') AND mc.merchant_code='" + smallSellerCode
							+ "'");
				else
					sb.append(" OR (mc.settle_code IN ('" + settleCodes + "') AND mc.merchant_code='" + smallSellerCode
							+ "'");
			}
			if (sb.length() > 0)
				sb.append(")");

		}
		settleToExcel(sb.toString(), response);
	}

	public void settleToExcel(String settleCodes, HttpServletResponse response) {
		InputStream in = null;
		OutputStream outputStream = null;
		try {
			in = ExportApplyPayment.class.getResourceAsStream("/skuDetail_kj.xls");
			HSSFWorkbook wb = new HSSFWorkbook(in);

			HSSFFont font = wb.createFont();
			font.setFontName("微软雅黑");
			font.setFontHeightInPoints((short) 9);// 设置字体大小
			HSSFCellStyle cellStyle = wb.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			HSSFSheet sheet = wb.getSheetAt(0);
			Row row = sheet.getRow(0);
			int startrow = 0;
			String exportSql = "SELECT mc.settle_code AS settle_code,oe.small_seller_code AS small_seller_code,oe.small_seller_name AS small_seller_name,"
					+ "	oe.product_code AS product_code,oe.product_name AS product_name,oe.sku_code AS sku_code,oe.sku_name as sku_name,oe.cost_price,oe.sell_price,oe.settle_num AS settle_num,"
					+ "	oe.settle_amount,oe.service_fee,(oe.settle_amount - oe.service_fee) AS payable_collect_amount,oe.sale_money AS sale_money,"
					+ "	oe.postage AS postage,oe.manage_money AS manage_money,oe.others AS others,"
					+ "	(oe.settle_amount - oe.service_fee - oe.sale_money - oe.postage - oe.manage_money - oe.others) AS settle_collect_amount,"
					+ "oe.period_retention_money ,"
					+ "	(oe.settle_amount - oe.service_fee - oe.sale_money - oe.postage - oe.manage_money - oe.others -oe.period_retention_money) AS actual_pay_amount"
					+ "	FROM oc_bill_final_export oe,oc_bill_merchant_new_spec mc WHERE oe.small_seller_code = mc.merchant_code"
					+ "	AND oe.start_time = mc.start_time AND oe.end_time = mc.end_time AND settle_type IN ('4497477900040002','4497477900040003')"
					+ " AND oe.settle_code = mc.settle_code AND (" + settleCodes + ")";
			List<Map<String, Object>> skuSettleList = DbUp.upTable("oc_bill_final_export").dataSqlList(exportSql,
					new MDataMap());
			if (null != skuSettleList && skuSettleList.size() > 0) {
				for (Map<String, Object> map : skuSettleList) {
					// 结算单
					String settleCode = isEmpty(map.get("settle_code")) ? "" : map.get("settle_code").toString();
					// 商品编码
					String productCode = isEmpty(map.get("product_code")) ? "" : map.get("product_code").toString();
					// 商品名称
					String productName = isEmpty(map.get("product_name")) ? "" : map.get("product_name").toString();
					// 商户编码
					String smallSellerCode = isEmpty(map.get("small_seller_code")) ? ""
							: map.get("small_seller_code").toString();
					// 商户名称
					String smallSellerName = isEmpty(map.get("small_seller_name")) ? ""
							: map.get("small_seller_name").toString();
					//sku编码
					String skuCode = isEmpty(map.get("sku_code")) ? ""
							: map.get("sku_code").toString();
					//sku名称
					String skuName = isEmpty(map.get("sku_name")) ? ""
							: map.get("sku_name").toString();
					// 应付单价
					double cost_price = Double
							.valueOf(isEmpty(map.get("cost_price")) ? "0.0" : map.get("cost_price").toString());
					// 应付代收单价
					double sell_price = Double
							.valueOf(isEmpty(map.get("sell_price")) ? "0.0" : map.get("sell_price").toString());
					// 本期代收数量合计
					int settle_num = Integer
							.valueOf(isEmpty(map.get("settle_num")) ? "0.0" : map.get("settle_num").toString());
					// 本期代收货款合计
					double settle_amount = Double
							.valueOf(isEmpty(map.get("settle_amount")) ? "0.0" : map.get("settle_amount").toString());
					// 平台服务费
					double service_fee = Double
							.valueOf(isEmpty(map.get("service_fee")) ? "0.0" : map.get("service_fee").toString());
					// 应付代收货款
					double payable_collect_amount = Double.valueOf(isEmpty(map.get("payable_collect_amount")) ? "0.0"
							: map.get("payable_collect_amount").toString());
					// 促销费用
					double sale_money = Double
							.valueOf(isEmpty(map.get("sale_money")) ? "0.0" : map.get("sale_money").toString());
					// 邮费
					double postage = Double
							.valueOf(isEmpty(map.get("postage")) ? "0.0" : map.get("postage").toString());
					// 平台管理费
					double manageMoney = Double
							.valueOf(isEmpty(map.get("manage_money")) ? "0.0" : map.get("manage_money").toString());
					// 其他
					double others = Double.valueOf(isEmpty(map.get("others")) ? "0.0" : map.get("others").toString());
					//结算代收货款
					double settle_collect_amount = Double.valueOf(isEmpty(map.get("settle_collect_amount")) ? "0.0" : map.get("settle_collect_amount").toString());
					//本期质保金
					double period_retention_money = Double.valueOf(isEmpty(map.get("period_retention_money")) ? "0.0" : map.get("period_retention_money").toString());
					// 实付代收货款
					double actualPayAmount = Double.valueOf(
							isEmpty(map.get("actual_pay_amount")) ? "0.0" : map.get("actual_pay_amount").toString());
					row = sheet.createRow((short) startrow + 1); // 在现有行号后追加数据
					row.createCell(0).setCellValue(settleCode); // 结算单编号
					row.createCell(1).setCellValue(smallSellerCode); // 商户编号
					row.createCell(2).setCellValue(smallSellerName); // 商户名称
					row.createCell(3).setCellValue(productCode); // 商品编号
					row.createCell(4).setCellValue(productName); // 商品名称
					row.createCell(5).setCellValue(skuCode);//sku编码
					row.createCell(6).setCellValue(skuName);//sku名称
					row.createCell(7).setCellValue(cost_price);// 代收单价
					row.createCell(8).setCellValue(sell_price);// 应付代收单价
					row.createCell(9).setCellValue(settle_num); // 本期代收数量合计
					row.createCell(10).setCellValue(settle_amount); // 本期代收货款合计
					row.createCell(11).setCellValue(service_fee); // 平台服务费
					row.createCell(12).setCellValue(payable_collect_amount); // 应付代收货款
					row.createCell(13).setCellValue(sale_money); // 促销费用
					row.createCell(14).setCellValue(postage); // 邮费
					row.createCell(15).setCellValue(manageMoney); // 平台管理费
					row.createCell(16).setCellValue(others); // 其他
					row.createCell(17).setCellValue(settle_collect_amount); // 结算代收货款
					row.createCell(18).setCellValue(period_retention_money); // 本期质保金
					row.createCell(19).setCellValue(actualPayAmount); // 结算代收货款
					startrow++;
				}
			}
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition",
					"attachment;filename=" + java.net.URLEncoder.encode("商品结算明细", "UTF-8").replace("+", "") + ".xls");
			outputStream = response.getOutputStream();
			wb.write(outputStream);
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != in)
				try {
					in.close();
				} catch (IOException e) {
				}
			if (null != outputStream)
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	private boolean isEmpty(Object obj) {
		return obj == null || obj.toString().equals("");
	}
}
