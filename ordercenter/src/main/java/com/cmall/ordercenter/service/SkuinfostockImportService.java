package com.cmall.ordercenter.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.model.ScFlowMain;
import com.cmall.systemcenter.service.FlowService;
import com.srnpr.xmassystem.support.PlusSupportStock;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class SkuinfostockImportService extends RootFunc {
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap1) {
		String fileRemoteUrl = mDataMap1.get("zw_f_upload_url");
		MWebResult mWebResult = new MWebResult();
		List<Map<String, String>> infoList = new ArrayList<Map<String, String>>();
		try {
			List<Map<String, String>> dataLists = this.downloadAndAnalysisFile(fileRemoteUrl);
			if (dataLists != null && dataLists.size() > 0) {
				Map<String, String> tempMap = dataLists.get(0);
				if(!tempMap.get("sell_productcode").equals("货号")||!tempMap.get("product_code").equals("商品编号")||!tempMap.get("sku_code").equals("sku编号")
						||!tempMap.get("sku_name").equals("sku名称")||!tempMap.get("stock_num").equals("库存")
						||!tempMap.get("stock_num_new").equals("修改后库存")){
					mWebResult.setResultCode(-1);
					mWebResult.setResultMessage("导入数据错误");
					return mWebResult;
				}else{
					dataLists.remove(0);
				}
				
				
				/**
				 * 过滤excel中导入的重复数据
				 */
				Map<String, Map<String, String>> infoMap = new HashMap<String, Map<String, String>>();
				for (int i =0;i<dataLists.size() ; i++) {
					String code = dataLists.get(i).get("sku_code");
					if(infoMap.containsKey(code)) {
						dataLists.remove(i);
						i--;
					} else {
						infoMap.put(code, dataLists.get(i));
					}
				}
				
				for (Entry<String, Map<String, String>> e: infoMap.entrySet()) {
					if (StringUtils.isEmpty(e.getValue().get("stock_num_new")))
						continue;
					int num = new PlusSupportStock().upLockStock(e.getValue().get("sku_code"));
					if(num>0){
						mWebResult.setResultCode(-1);
						mWebResult.setResultMessage("商品"+e.getValue().get("sku_code")+"被活动绑定，锁定库存");
						return mWebResult;
					}
					infoList.add(e.getValue());
				}
				
				for(Map<String, String> map:infoList){
					MDataMap parmap = new MDataMap();
					parmap.put("sku_code", map.get("sku_code"));
					String skusql = "select * from pc_skuinfo where sku_code=:sku_code";
					List<Map<String, Object>> skulist = DbUp.upTable("pc_skuinfo").dataSqlList(skusql, parmap);
					if(skulist.size()==0){
						mWebResult.setResultCode(-1);
						mWebResult.setResultMessage("未找到sku编号："+map.get("sku_code"));
						return mWebResult;
					}else{
						String sku_uid = skulist.get(0).get("uid").toString();
						map.put("sku_uid", sku_uid);
					}
					try {
//						int stocknum = Integer.parseInt(map.get("stock_num_new"));
//						int stocknum_old = Integer.parseInt(map.get("stock_num"));
						int stocknum = 0;
						int stocknum_old = 0;
						if(map.get("stock_num_new") != null && !"".equals(map.get("stock_num_new").toString())) {
							stocknum = Integer.parseInt(map.get("stock_num_new"));
						}
						if(map.get("stock_num") != null && !"".equals(map.get("stock_num").toString())) {
							stocknum_old = Integer.parseInt(map.get("stock_num"));
						}
						if(stocknum<0||stocknum>100000){
							mWebResult.setResultCode(-1);
							mWebResult.setResultMessage("导入数据错误");
							return mWebResult;
						}
						if(stocknum>=stocknum_old){
							map.put("option", "0");
						}else{
							map.put("option", "1");
						}
					} catch (Exception e1) {
						mWebResult.setResultCode(-1);
						mWebResult.setResultMessage("导入数据错误");
						return mWebResult;
					}
				}
				
				List<String> templList = new ArrayList();
				for(Map<String, String> map:infoList){
					String product_code = map.get("product_code");
					if(templList.contains(product_code)){
						continue;
					}
					MDataMap param = new MDataMap();
					param.put("uid", sOperateUid);
					param.put("product_code", product_code);
					
					for(Map<String, String> map1:infoList){
						if(map1.get("product_code").equals(product_code)){
							param.put("option_"+map1.get("sku_code"), map1.get("option"));
							param.put("skuuid_"+map1.get("sku_code"), map1.get("sku_uid"));
							param.put("skuNum_"+map1.get("sku_code"), map1.get("stock_num_new"));
						}else{
							continue;
						}
					}
					mWebResult = this.createFlow(sOperateUid, param);
					
					templList.add(product_code);
				}				
				
			}else {
				mWebResult.setResultCode(-1);
				mWebResult.setResultMessage("导入文件失败：没有数据");
			}
		} catch (Exception e) {
			e.printStackTrace();
			mWebResult.setResultCode(-1);
			mWebResult.setResultMessage("导入文件失败：" + e.getMessage());
		}		
		return mWebResult;
	}
	
	/**
	 * @description: 抛弃上面的烂代码  
	 *
	 * @author Yangcl
	 * @date 2017年7月6日 下午5:02:55 
	 * @version 1.0.0.1
	 */
	private List<Map<String, String>> downloadAndAnalysisFile(String fileUrl){
		List<Map<String, String>> list = new ArrayList<>();
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
				for (int i = firstRowIndex; i <= lastRowIndex; i++) {
					Row row = sheet.getRow(i);
					if (row != null) {
						Map<String, String> e = new HashMap<>();
						e.put("sell_productcode", getCellValue(row.getCell(0)));
						e.put("product_code", getCellValue(row.getCell(1)));
						e.put("sku_code", getCellValue(row.getCell(2)));
						e.put("sku_name", getCellValue(row.getCell(3)));
						e.put("stock_num", getCellValue(row.getCell(4)));
						e.put("stock_num_new", getCellValue(row.getCell(5)));
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
	
	public MWebResult createFlow(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap;
		MUserInfo userInfo = UserFactory.INSTANCE.create();
//		if(null == userInfo || StringUtils.isEmpty(userInfo.getManageCode()) || !userInfo.getManageCode().startsWith("SF03")){
		/**
		 * 修改商户判断条件 2016-12-02 zhy
		 */
		String seller_type = WebHelper.getSellerType(userInfo.getManageCode());
		if(null == userInfo || StringUtils.isEmpty(userInfo.getManageCode()) || StringUtils.isBlank(seller_type)){
			mResult.inErrorMessage(941901061, bInfo(941901064));
			return mResult;
		}
		Iterator<String> rtKey = mAddMaps.keySet().iterator();
		int skuFlag = 0;
		while(rtKey.hasNext()){
			String myKey = rtKey.next();
			String kenEnd = myKey.substring(myKey.indexOf("_")+1);
			if(myKey.startsWith("skuNum")){
				Iterator<String> rtKey3 = mAddMaps.keySet().iterator();
				String option2 = "";
				while(rtKey3.hasNext()){
					String myKey1 = rtKey3.next();
					String kenEnd1 = myKey1.substring(myKey1.indexOf("_")+1);
					if(kenEnd.equals(kenEnd1)){
						if(myKey1.startsWith("option")){
							option2 = mAddMaps.get(myKey1);
						}
					}
				}
				String stockNum = mAddMaps.get(myKey);
				if("1".equals(option2)){	//减少库存
					PlusSupportStock st = new PlusSupportStock();
					int remainStock = st.upAllStock(kenEnd);
					if(Integer.parseInt(stockNum) > remainStock){
						mResult.setResultCode(941901130);
						mResult.setResultMessage(bInfo(941901130, kenEnd));
						return mResult;
					}
				}
				if(StringUtils.isEmpty(stockNum)){
					continue;
				}
				skuFlag ++;
			}
		}
		if(skuFlag == 0){
			mResult.inErrorMessage(941901126);
			return mResult;
		}
		String userCode = userInfo.getUserCode();
		String productCode = mAddMaps.get("product_code");
		String preViewUrl = bConfig("productcenter.PreviewCheckProductUrl")+productCode+"_1";
		String create_user = UserFactory.INSTANCE.create().getLoginName();
		String createTime = DateUtil.getSysDateTimeString();
		String flowCode = "";
		try {
			//加入审批的流程
			ScFlowMain flow = new ScFlowMain();
			flow.setCurrentStatus("4497172300120001");
			String title = bInfo(941901127, productCode);
			flow.setFlowTitle(productCode);
			flow.setFlowType("449717230012");
			flow.setFlowUrl(preViewUrl);
			flow.setCreator(userCode);
			flow.setOuterCode(productCode);
			flow.setFlowRemark(title);
			//创建的审批流程
			RootResult ret = (new FlowService()).CreateFlow(flow);
			if(ret.getResultCode() == 1){
				flowCode = ret.getResultMessage();
			}else{
				mResult.inErrorMessage(ret.getResultCode());
				return mResult;
			}
		} catch (Exception e) {
		}
		Iterator<String> rtKey2 = mAddMaps.keySet().iterator();
		MDataMap sp = new MDataMap();
		sp.put("flow_code", flowCode);
		while(rtKey2.hasNext()){
			String myKey = rtKey2.next();
			if(myKey.startsWith("skuNum")){
				String stockNum = mAddMaps.get(myKey);
				if(StringUtils.isEmpty(stockNum)){
					continue;
				}
				String kenEnd = myKey.substring(myKey.indexOf("_")+1);
				String option = "";
				String skuuid = "";
				Iterator<String> rtKey1 = mAddMaps.keySet().iterator();
				while(rtKey1.hasNext()){
					String myKey1 = rtKey1.next();
					String kenEnd1 = myKey1.substring(myKey1.indexOf("_")+1);
					if(kenEnd.equals(kenEnd1)){
						if(myKey1.startsWith("option")){
							option = mAddMaps.get(myKey1);
						}
						if(myKey1.startsWith("skuuid")){
							skuuid = mAddMaps.get(myKey1);
						}
					}
				}
				sp.put("product_code", productCode);
				sp.put("sku_uid", skuuid);
				sp.put("sku_code", kenEnd);
				if("0".equals(option)){	//增加库存
					sp.put("operate_type", "1");
				}else if("1".equals(option)){	//减少库存
					sp.put("operate_type", "2");
					//减少库存
					sp.put("operate_type", "2");
				}
				sp.put("change_num", stockNum);
				sp.put("deal_status", "4497471600230001");//待审批
				sp.put("create_time", createTime);
				sp.put("create_user", create_user);
				sp.put("update_ime", createTime);
				sp.put("update_user", create_user);
				try {
					DbUp.upTable("sc_skunum_change").dataInsert(sp);//插入到商品sku库存变化记录表
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		return mResult;
	}

}