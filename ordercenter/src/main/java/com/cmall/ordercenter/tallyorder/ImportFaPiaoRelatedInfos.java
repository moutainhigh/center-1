package com.cmall.ordercenter.tallyorder;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;


/**
 * 导入发票相关信息
 */

public class ImportFaPiaoRelatedInfos  extends RootApi<ImportFaPiaoResult,ImportFaPiaoInput >  {
	
	@Override
	public ImportFaPiaoResult Process(ImportFaPiaoInput inputParam, MDataMap mRequestMap) {
	    String fileRemoteUrl = inputParam.getUpload_show();
	     JSONObject jsonObject = JSON.parseObject(mRequestMap.get("api_input"));
	     String zb_flag = jsonObject.get("zb_flag").toString();
	    ImportFaPiaoResult result = new ImportFaPiaoResult();
	    
	    Map<String, String > errorMap = new HashMap<String, String >();
	    
	    if(!StringUtils.isEmpty(fileRemoteUrl)) {
			java.net.URL resourceUrl;
			InputStream instream = null;
			try {
				resourceUrl = new java.net.URL(fileRemoteUrl);
				instream = (InputStream) resourceUrl.getContent();
				if(null != instream){
					List<ImportModel> rtnList = readExcel(instream,zb_flag);
					if(rtnList.size() <= 0) {
						result.setResultCode(0);
						result.setResultMessage("没有导入的信息!!");
						return result;
					}
					//参数集合
					List<Map<String,Object>> listMap = new ArrayList<>();
					boolean isExcute = true;
					for (ImportModel importModel : rtnList) {
						//商管导入标识
						if("fp_sg".equals(zb_flag)) {
							if(importModel.isFlag()) {
								Map<String,Object> map =new HashMap<>();
								map.put("document_code",importModel.getDocumentCode());
								map.put("waybill_num", importModel.getWaybillNum());
								listMap.add(map);
							} else {
								isExcute = false;
								if(errorMap.containsKey(importModel.getError_message())) {
									String tempVal = errorMap.get(importModel.getError_message());
									errorMap.put(importModel.getError_message(), tempVal+","+importModel.getDocumentCode());
								} else {
									errorMap.put(importModel.getError_message(),importModel.getDocumentCode());
								}
								
							}
						}
						//财务导入标识
						if("fp_cw".equals(zb_flag)) {
							if(importModel.isFlag()) {
								Map<String,Object> map =new HashMap<>();
								map.put("document_code",importModel.getDocumentCode());
								map.put("bill_time", importModel.getBillTime());
								listMap.add(map);
							} else {
								isExcute = false;
								if(errorMap.containsKey(importModel.getError_message())) {
									String tempVal = errorMap.get(importModel.getError_message());
									errorMap.put(importModel.getError_message(), tempVal+","+importModel.getDocumentCode());
								} else {
									errorMap.put(importModel.getError_message(),importModel.getDocumentCode());
								}
								
							}
						}
						
					}
					if(isExcute) {
						if("fp_cw".equals(zb_flag)) {
							for (Map<String, Object> map : listMap) {
								DbUp.upTable("oc_documents_info").dataUpdate(new MDataMap("document_code",map.get("document_code").toString(),"bill_time",map.get("bill_time").toString(),"bill_time_flag","1"), "bill_time,bill_time_flag", "document_code");
							}
						}
					
						if("fp_sg".equals(zb_flag)) {
							for (Map<String, Object> map : listMap) {
								DbUp.upTable("oc_documents_info").dataUpdate(new MDataMap("document_code",map.get("document_code").toString(),"waybill_num",map.get("waybill_num").toString(),"submit_flow","44975003004","update_time",DateUtil.getSysDateTimeString(),"waybill_num_state","1","document_state","1"), "waybill_num,submit_flow,update_time,waybill_num_state,document_state", "document_code");
							
								//状态变更，添加日志
								Map<String, Object> subMap = DbUp.upTable("oc_documents_info").dataSqlOne("select * from oc_documents_info where document_code=:document_code", new MDataMap("document_code",map.get("document_code").toString()));
								MDataMap logMap = new MDataMap();
								logMap.put("uid",UUID.randomUUID().toString().replace("-", ""));
								logMap.put("document_code", subMap.get("document_code").toString());
								logMap.put("operating_time", FormatHelper.upDateTime());
								logMap.put("operator",UserFactory.INSTANCE.create().getRealName());
								logMap.put("small_seller_type",subMap.get("uc_seller_type").toString());
								logMap.put("remark","维护运单号通过");
								DbUp.upTable("lc_fapiao_log").dataInsert(logMap);
							}
						}
					}
					if(!isExcute) {
						result.setResultMessage(JSON.toJSONString(errorMap));
					}
					
				}
				
			} catch (Exception e) {
				result.setResultMessage("导入失败:单据编号不存在" /*+ e.getLocalizedMessage()*/);
				e.printStackTrace();
			} finally {
				if(null != instream) try { instream.close(); } catch (IOException e) {}
			}
	    }
		return result;
	}

	

	@SuppressWarnings("unused")
	private static class JsonResult {
		private List<String> success = new LinkedList<String>();
		private String notFound = "";
		private String same = "";
		
		public List<String> getSuccess() {
			return success;
		}
		public void setSuccess(List<String> success) {
			this.success = success;
		}
		public String getNotFound() {
			return notFound;
		}
		public void setNotFound(String notFound) {
			this.notFound = notFound;
		}
		public String getSame() {
			return same;
		}
		public void setSame(String same) {
			this.same = same;
		}
	}
	

	
	public static class ImportModel {
       private String documentCode="";
       private String billTime="";
       private String waybillNum="";
       private boolean flag = true;
	   private String error_message = "";
	   
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public String getError_message() {
		return error_message;
	}
	public void setError_message(String error_message) {
		this.error_message = error_message;
	}
	public String getDocumentCode() {
		return documentCode;
	}
	public void setDocumentCode(String documentCode) {
		this.documentCode = documentCode;
	}
	public String getBillTime() {
		return billTime;
	}
	public void setBillTime(String billTime) {
		this.billTime = billTime;
	}
	public String getWaybillNum() {
		return waybillNum;
	}
	public void setWaybillNum(String waybillNum) {
		this.waybillNum = waybillNum;
	}
       

	}

	
	
	private List<ImportModel> importData(Sheet sheet,String zb_flag) {
		
		List<ImportModel> resultModel = new ArrayList<ImportModel>();
		
		int firstRowIndex = sheet.getFirstRowNum();
		int lastRowIndex = sheet.getLastRowNum();
		
		for (int rIndex = firstRowIndex + 1; rIndex <= lastRowIndex; rIndex++) {
			
			ImportModel model = new ImportModel();
			
			String documentCode = "";
			String billTime="";
			String wanbillNum ="";
		    
			try {
				Row row = sheet.getRow(rIndex);
				
				if( null != row.getCell(0) && row.getCell(0).getCellType() != Cell.CELL_TYPE_BLANK) {
					//单据编号
					if(row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
						documentCode = row.getCell(0).getStringCellValue();
					} else if(row.getCell(0).getCellType()==Cell.CELL_TYPE_NUMERIC) {
						Double d = row.getCell(0).getNumericCellValue();
						documentCode = new DecimalFormat("#").format(d); 
					}
				}
				
				if("fp_cw".equals(zb_flag)) {
					//开具时间
					if(null != row.getCell(1) &&row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
						billTime = row.getCell(1).getStringCellValue();
					} else if(null != row.getCell(1) &&row.getCell(1).getCellType()==Cell.CELL_TYPE_NUMERIC) {
						Date dateCellValue = row.getCell(1).getDateCellValue();
						DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
						billTime = format.format(dateCellValue);
					}	
				}
				if("fp_sg".equals(zb_flag)) {
					//运单号
					if(row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
						wanbillNum = row.getCell(1).getStringCellValue();
					} else if(row.getCell(1).getCellType()==Cell.CELL_TYPE_NUMERIC) {
						Double d = row.getCell(1).getNumericCellValue();
						wanbillNum = new DecimalFormat("#").format(d); 
					}	
				}


				if(!StringUtils.isEmpty(documentCode)){
					
					String querSql ="";
					if("fp_sg".equals(zb_flag)) {
						 querSql = "select * from oc_documents_info where document_code=:document_code and submit_flow='44975003003' ";
					}
					else {
						 querSql = "select * from oc_documents_info where document_code=:document_code and submit_flow='44975003002' ";
					}
					Map<String,Object> resultMap = DbUp.upTable("oc_documents_info").dataSqlOne(querSql, new MDataMap("document_code",documentCode));
				   
					if(null != resultMap && resultMap.size() > 0) {
					//商管导入校验标识	
                    if("fp_sg".equals(zb_flag)) {
                    	if(resultMap.get("submit_flag").toString().equals("0")) {
                    		//导入数据异常
							model.setDocumentCode(documentCode);
							model.setError_message("单据未提交");
							model.setFlag(false);
                    	}
                     	if(model.flag) {
                     		model.setDocumentCode(documentCode);
							model.setWaybillNum(wanbillNum);
                     	}
                     	
                    }
                  //财务导入校验标识
                    if("fp_cw".equals(zb_flag)) {
                    	String vertiryResult = this.verifyFormData(billTime);
    					if(StringUtils.isBlank(vertiryResult))
    						{
    							model.setDocumentCode(documentCode);
    							model.setBillTime(billTime);

    						}
    					else {
    							//导入数据异常
    							model.setDocumentCode(documentCode);
    							model.setError_message(vertiryResult);
    							model.setFlag(false);
    					}
                    }
				
					} else {
						//查询不到对应的记录
						model.setDocumentCode(documentCode);
						model.setError_message("单据编号错误");
						model.setFlag(false);
					}
				}
				resultModel.add(model);
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return resultModel;
	}
	
	

	public static String verifyFormData(String billTime) {
		String error = "";
		String regex = "^(-?[1-9]\\d*\\.?\\d*)|(-?0\\.\\d*[1-9])|(-?[0])|(-?[0]\\.\\d*)$";
		String dateRegex = "\\d{4}-\\d{2}-\\d{2}";
		String timeRegex = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
/*		if (sell_price == null || "".equals(sell_price)) {
			error = "销售价格不能为空";
		} else if (!sell_price.matches(regex)) {
			error = "销售价格只能是数字";
		}
*/
		if(billTime.matches(dateRegex)) {
				//Date startDate = DateUtil.toDate(start_date);
				//Date endDate = DateUtil.toDate(end_date);
				//Date nowTime = DateUtil.toDate(DateUtil.getSysDateString());
		/*		if (startDate.compareTo(nowTime) < 0) {
					error = "开始日期必须大于等于当前日期";
				} else if (endDate.compareTo(nowTime) < 0) {
					error = "结束日期必须大于等于当前日期";
				} else if (endDate.compareTo(startDate) < 0) {
					error = "开始日期必须小于或等于结束日期";
				} */
			}
		else {
			error = "时间格式不正确";
		}
		
		return error;
	}
	/**
	 * 读取Excel数据
	 * 
	 * @param file
	 */
	public List<ImportModel> readExcel(InputStream input,String zb_flag) {
		
		@SuppressWarnings("unused")
		String result = "";
		List<ImportModel> resultModel = new ArrayList<ImportModel>();
		try {
			Workbook wb = null;
			wb = new HSSFWorkbook(input);
			Sheet sheet = wb.getSheetAt(0);// 第一个工作表
			resultModel = importData(sheet,zb_flag);
		} catch (FileNotFoundException e) {
			result = "导入失败！未找到上传文件";
			e.printStackTrace();
		} catch (IOException e) {
			result = "导入失败！" /*+ e.getLocalizedMessage()*/;
			e.printStackTrace();
		}
		return resultModel;
	}

}
