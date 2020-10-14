package com.cmall.ordercenter.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.Logistics;
import com.cmall.ordercenter.model.OcOrderShipments;
import com.ordercenter.express.service.OrderShipmentsService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class LogisticsImportService extends RootFunc {
	
	static OrderShipmentsService shipmentsService = new OrderShipmentsService();
	
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap1) {
		String fileRemoteUrl = mDataMap1.get("zw_f_upload_url");
		MWebResult mWebResult = new MWebResult();
		List<String> logisticorders = new ArrayList<String>();// 用于存放已经有物流信息的公司
		List<String> noLogisticOrders = new ArrayList<String>();// 用于存放没存放物流信息的公司
		List<String> failOrderList = new ArrayList<String>();// 交易失败的订单编号
		List<String> errorLogistic = new ArrayList<>();  // 用于存放系统不支持的物流公司名称的订单信息 - Yangcl 
		try {
			List<Logistics> dataLists = this.downloadAndAnalysisFile(fileRemoteUrl);
			if (dataLists != null && dataLists.size() > 0) {
				List<String> logisticCompany = new ArrayList<String>();// 存放物流公司名称
				List<String> orderList = new ArrayList<String>();// 订单编号
				
				/**
				 * 过滤excel中导入的重复数据
				 */
				Map<String, Logistics> logisticsMap = new HashMap<String, Logistics>();
				for (int i =0;i<dataLists.size() ; i++) {
					if(!dataLists.get(i).getLogisticsCompany().matches("^[a-z0-9A-Z\u4e00-\u9fa5]+$")){
						throw new RuntimeException("物流公司格式错误，请重新导入");
					}
					if(!dataLists.get(i).getWaybill().matches("[a-zA-Z0-9]+")){
						throw new RuntimeException("物流单号格式错误，请重新导入");
					}
					String code = dataLists.get(i).getOrderCode();
					if(logisticsMap.containsKey(code)) {
						dataLists.remove(i);
						i--;
					} else {
						logisticsMap.put(code, dataLists.get(i));
					}
				}
				
				for (Logistics logistics : dataLists) {
					if (StringUtils.isEmpty(logistics.getLogisticsCompany())
							|| StringUtils.isEmpty(logistics.getOrderCode()))
						continue;
					logisticCompany.add(logistics.getLogisticsCompany());
					orderList.add(logistics.getOrderCode());
				}
				String companyNames = StringUtils.join(logisticCompany, "','");
				//
				//查询所有物流编码对照表
				List<MDataMap> mpList = DbUp.upTable("sc_logisticscompany").queryAll("","","",null);
				//放入map
//				Map<String,String> companyCodeMaps=new HashMap<String, String>();
//				for (MDataMap mDataMap : mpList) {
//					companyCodeMaps.put(mDataMap.get("company_code"), mDataMap.get("company_name"));
//				}

				
				String orderCodes = StringUtils.join(orderList, "','");
				List<MDataMap> shipmentList = DbUp.upTable("oc_order_shipments").queryAll(
								"order_code,logisticse_code,waybill,import_count,create_time,zid,uid",  "",
								"order_code in ('" + orderCodes + "')", null);// 查询订单物流信息
				
				for (MDataMap shipMap : shipmentList) {
					/*if (StringUtils.isNotEmpty(shipMap.get("waybill"))) {
						logisticorders.add(shipMap.get("order_code"));
					}*/
					logisticorders.add(shipMap.get("order_code"));//shipMap.get("waybill")为空的判断暂去，保证一小时之内可以做更新操作  fq++
				}
				String orderCo ="";//订单号
				for (Logistics model : dataLists) {
					orderCo = model.getOrderCode();
					if(StringUtils.isEmpty(orderCo))
						continue;
					int existFlag = 0;
					MDataMap orderStaMap = DbUp.upTable("oc_orderinfo").oneWhere("order_status", "", "", "order_code", orderCo);
					if(orderStaMap !=null && "4497153900010006".equals(orderStaMap.get("order_status"))){
						failOrderList.add(orderCo);
						continue;
					}
					for (String orderCode : logisticorders) {//已经存在物流信息的就跳过
						if(orderCode.equals(orderCo)){
							existFlag = 1;
							break;
						}
					}
					if(existFlag == 1) continue;
					OrderService os = new OrderService();
					OcOrderShipments oos = new OcOrderShipments();

					String waybill = model.getWaybill();
					String companyName = model.getLogisticsCompany();
					if (StringUtils.isEmpty(waybill) || StringUtils.isEmpty(companyName)) {
						noLogisticOrders.add(orderCo);
						continue;
					}
					
					String logisticseCode = "";
					for (MDataMap mDataMap : mpList) {
						if (companyName.toLowerCase().indexOf(mDataMap.get("company_name").toLowerCase())>-1) {
							logisticseCode = mDataMap.get("company_code");
							break;
						}
					}
					if(logisticseCode == ""){  // 商户填写的物流公司名称在惠家有库里没有找到   
						errorLogistic.add(orderCo);
					}
					
					if(waybill.contains(".")){
						waybill = waybill.substring(0, waybill.indexOf("."));
					}
					oos.setLogisticseName(model.getLogisticsCompany());
					oos.setLogisticseCode(logisticseCode);
					oos.setOrderCode(orderCo);
					oos.setRemark("");
					oos.setWaybill(filerlogisticseCode(waybill));

					 os.shipmentForOrder(oos);  
				}
				
				/**
				 * 已经导入的运单号。在一小时之内可以修改一次记录  fq++
				 * shipmentList ：已经存在的订单号
				 */
				for (MDataMap shipMap : shipmentList) {
					String order_code = shipMap.get("order_code");
					String importCount = shipMap.get("import_count");//导入次数
					String createTime = shipMap.get("create_time");//首次导入时间
					String now=DateUtil.getSysDateTimeString();
					
					Integer limitCnt = Integer.valueOf(bConfig("ordercenter.import_order_Logistics_count_limit"));//限制导入的次数
					Integer timeCnt = Integer.valueOf(bConfig("ordercenter.import_order_Logistics_time_limit"));//限制导入后更新的时间范围
					
					
					java.util.Date endTime = DateUtil.addSecond(DateUtil.toDate(createTime,DateUtil.DATE_FORMAT_DATETIME), timeCnt);
					int compareTime = DateUtil.compareTime(DateUtil.sdfDateTime.format(endTime), now ,DateUtil.DATE_FORMAT_DATETIME);
					if(compareTime > 0 && Integer.valueOf(importCount) < limitCnt) {
						
						Logistics logistics = logisticsMap.get(order_code);
						
						
						String waybill = logistics.getWaybill();
						String companyName = logistics.getLogisticsCompany();
						if (StringUtils.isEmpty(waybill) || StringUtils.isEmpty(companyName)) {
							noLogisticOrders.add(orderCo);
							continue;
						}
						
						String logisticseCode = "";
						for (MDataMap mDataMap : mpList) {
							if (companyName.toLowerCase().indexOf(mDataMap.get("company_name").toLowerCase())>-1) {
								logisticseCode = mDataMap.get("company_code");
								break;
							}
						}
						if(logisticseCode == ""){  // 商户填写的物流公司名称在惠家有库里没有找到   
							errorLogistic.add(orderCo);
						}
						
						if(waybill.contains(".")){
							waybill = waybill.substring(0, waybill.indexOf("."));
						}
						
						waybill = filerlogisticseCode(waybill);
						
						//允许更新数据
						MDataMap param = new MDataMap();
//						param.put("zid", shipMap.get("zid"));
						param.put("uid", shipMap.get("uid"));
						param.put("logisticse_name", logistics.getLogisticsCompany());
						param.put("logisticse_code", logisticseCode);
						param.put("waybill", waybill);
						
						//DbUp.upTable("oc_order_shipments").update(param);
						String updateSql = " UPDATE ordercenter.oc_order_shipments SET import_count = import_count + 1,logisticse_name=:logisticse_name,logisticse_code=:logisticse_code,waybill=:waybill WHERE uid = :uid ;";
						DbUp.upTable("oc_order_shipments").dataExec(updateSql, param);
						
						// 记录运单号变更日志
						if(!logisticseCode.equals(shipMap.get("logisticse_code")) || !waybill.equals(shipMap.get("waybill"))) {
							shipmentsService.onChangeShipment(order_code, shipMap.get("logisticse_code"), shipMap.get("waybill"), logisticseCode, waybill);
						}
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			mWebResult.setResultCode(-1);
			mWebResult.setResultMessage("导入文件失败：" + e.getMessage());
		}
		if (logisticorders.size() > 0 && failOrderList.size() == 0 && noLogisticOrders.size() == 0) {// 库中已存在的物流订单信息
			mWebResult.inErrorMessage(939303104, StringUtils.join(logisticorders, "、"));
			
		}else if ((noLogisticOrders.size() > 0 || errorLogistic.size() > 0) && failOrderList.size() == 0 && logisticorders.size() == 0) {// 没有填写物流信息的订单
			if(noLogisticOrders.size() > 0 && errorLogistic.size() == 0){
				mWebResult.inErrorMessage(939303105, StringUtils.join(noLogisticOrders, "、"));
				mWebResult.setResultCode(1);
				// 导入失败:</br>{0}物流公司/号码 为空，请填写后，重新提交！</br>
			}else if(noLogisticOrders.size() == 0 && errorLogistic.size() > 0){
				// 这些订单：{0}所填写的物流公司名称在惠家有系统中不存在，用户可能无法跟踪物流信息!
				mWebResult.inErrorMessage(939303304, StringUtils.join(errorLogistic, "、"));
				mWebResult.setResultCode(1);
			}else if(noLogisticOrders.size() > 0 && errorLogistic.size() > 0){
				// {0}物流公司/号码 为空，请填写后，重新提交!  这些订单：{1}所填写的物流公司名称在惠家有系统中不存在，用户可能无法跟踪物流信息!
				mWebResult.inErrorMessage(939303303, StringUtils.join(noLogisticOrders, "、") , StringUtils.join(errorLogistic, "、"));
				mWebResult.setResultCode(1);
			}
		}else if (failOrderList.size() > 0 && noLogisticOrders.size() == 0 && logisticorders.size() == 0) {// 交易失败的订单
			mWebResult.inErrorMessage(939303106, StringUtils.join(failOrderList, "、"));
			
		}else if (logisticorders.size() > 0 && noLogisticOrders.size() > 0 && failOrderList.size() == 0) {// 库中已存在的物流订单信息和没有填写订单物流信息的
			mWebResult.inErrorMessage(939303107, StringUtils.join(logisticorders, "、"),StringUtils.join(noLogisticOrders, "、"));
			
		}else if (logisticorders.size() > 0 && noLogisticOrders.size() == 0 && failOrderList.size() > 0) {// 库中已存在的物流订单信息和交易失败的订单
			mWebResult.inErrorMessage(939303108, StringUtils.join(logisticorders, "、"), StringUtils.join(failOrderList, "、"));
			
		}else if (logisticorders.size() == 0 && noLogisticOrders.size() > 0 && failOrderList.size() > 0) {// 没有填写物流信息和交易失败的
			mWebResult.inErrorMessage(939303109, StringUtils.join(noLogisticOrders, "、"), StringUtils.join(failOrderList, "、"));
			
		}else if (logisticorders.size() > 0 && noLogisticOrders.size() > 0 && failOrderList.size() > 0) {// 库中已存在的物流订单信息，没有订单物流信息的记忆交易失败的
			
			mWebResult.inErrorMessage(939303110, StringUtils.join(logisticorders, "、"), StringUtils.join(noLogisticOrders, "、"), StringUtils.join(failOrderList, "、"));
		}
		return mWebResult;
	}

//	private List<Logistics> downloadAndAnalysisFile(String fileRemoteUrl)
//			throws Exception {
//		String extension = fileRemoteUrl.lastIndexOf(".") == -1 ? ""
//				: fileRemoteUrl.substring(fileRemoteUrl.lastIndexOf(".") + 1);
//		java.net.URL resourceUrl = new java.net.URL(fileRemoteUrl);
//		InputStream content = (InputStream) resourceUrl.getContent();
//		ReadExcelUtil<Logistics> readExcelUtil = new ReadExcelUtil<Logistics>();
//		return readExcelUtil.readExcel(false, null, content, new String[] {
//				"orderCode", "dealTime", "shipTime" , "dealFinishTime", "buyerMobile",
//				"orderStatus", "orderTotalMoney", "payMoney", "productName", "specification",
//				"buyNum", "productCode", "skuCode", "price", "buyer",
//				"buyAddress", "logisticsCompany", "waybill" , "goodsNum"}, 
//				new Class[] {
//					String.class, String.class, String.class, String.class,
//					String.class, String.class, String.class, String.class,
//					String.class, String.class, String.class, String.class,
//					String.class, String.class, String.class, String.class, 
//					String.class, String.class, String.class 
//				},
//				Logistics.class,
//				extension);
//	}
	
	/**
	 * @description: 抛弃上面的烂代码  
	 *
	 * @author Yangcl
	 * @date 2017年7月6日 下午5:02:55 
	 * @version 1.0.0.1
	 */
	private List<Logistics> downloadAndAnalysisFile(String fileUrl){
		List<Logistics> list = new ArrayList<>();
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
				for (int i = firstRowIndex + 1; i <= lastRowIndex; i++) {
					Row row = sheet.getRow(i);
					if (row != null) {
						Logistics e = new Logistics();
						e.setOrderCode(getCellValue(row.getCell(0))); 
						e.setLogisticsCompany(getCellValue(row.getCell(16))); 						
						e.setWaybill(getCellValue(row.getCell(17)));						
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
	
	
	
	private String filerlogisticseCode(String logisticseCode){
		if(logisticseCode!=null){
			return Pattern.compile("[^0-9a-zA-Z]").matcher(logisticseCode).replaceAll("");
		}
		return "";
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

}















