package com.cmall.systemcenter.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.fusesource.hawtbuf.BufferInputStream;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.GovDistrict;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapcom.topdo.TopConst;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 行政区划 管理
 * 
 * @author huoqiangshou
 * 
 */
public class GovdistrictService extends BaseClass {

	private static String GOV_DISTRICT_TABLE_NAME = "sc_gov_district";
	private static String GOV_FILE_NAME = "/InitSource/行政区划.xls";

	/**
	 * 初始化数据
	 */
	public RootResult initData() {
		RootResult rt = new RootResult();
		InputStream is;
		try {
			is = this.getClass().getResourceAsStream(GOV_FILE_NAME);
			HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);

			MDataMap mDataMap;
			for (int numSheet = 0; numSheet < 1; numSheet++) {
				HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
				if (hssfSheet == null) {
					continue;
				}
				// 循环行Row
				for (int rowNum = 0; rowNum < 3510; rowNum++) {
					HSSFRow hssfRow = hssfSheet.getRow(rowNum);
					BigDecimal b = new BigDecimal(hssfRow.getCell(0)
							.getNumericCellValue());
					b.setScale(0);

					mDataMap = new MDataMap();
					mDataMap.put("code", b.toString());
					mDataMap.put("name", hssfRow.getCell(1)
							.getStringCellValue());
					DbUp.upTable(GOV_DISTRICT_TABLE_NAME).dataInsert(mDataMap);
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rt.setResultCode(949701028);
			rt.setResultMessage(bInfo(949701028, GOV_FILE_NAME));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rt.setResultCode(949701029);
			rt.setResultMessage(bInfo(949701029, GOV_FILE_NAME));
		}
		return rt;
	}

	/**
	 * 生成行政区划json文件
	 * 
	 * 6位：前两位：省 中间两位：市 后两位：区
	 * 
	 * @return
	 */
	public RootResult generateGovJson() {
		RootResult rt = new RootResult();

		JsonHelper<GovDistrict> json = new JsonHelper<GovDistrict>();
		List<GovDistrict> list = new ArrayList<GovDistrict>();
		GovDistrict gd;
		gd = new GovDistrict();
		gd.setCode("global");
		gd.setName("全国");
		gd.setChildren(getChildrendGovDistrict(list, ""));
		rt.setResultCode(1);
		String result = json.ObjToString(gd);
		//System.out.println(result);

		String filepath = TopConst.CONST_TOP_DIR_SERVLET
				+ "/resources/cmanage/json/address.json";
		try {
			OutputStream fos = new FileOutputStream(filepath);
			FileWriter jsonFile = new FileWriter(filepath);
			byte[] tempbytes = new byte[100];
			int byteread = 0;

			InputStream in = new BufferInputStream(result.getBytes());
			// 读入多个字节到字节数组中，byteread为一次读入的字节数
			while ((byteread = in.read(tempbytes)) != -1) {
				fos.write(tempbytes,0,byteread);
			}
			fos.close();
//			for (int b = 0; b < result.length(); b++) {
//				char c = result.charAt(b);
//				jsonFile.write(c);
//			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rt;
	}

	private List<GovDistrict> getChildrendGovDistrict(List<GovDistrict> list,
			String whereStr) {
		String queryStr = "";
		if (StringUtils.isNotBlank(whereStr)) {

			if (whereStr.length() == 2) {
				queryStr = " code like '" + whereStr + "%00' and code <> '"
						+ whereStr + "0000'       ";
			} else {
				queryStr = " code like '" + whereStr + "%' and code <> '"
						+ whereStr + "00'       ";
			}
		} else {
			queryStr = " code like '%0000'  ";
		}

		List<Map<String, Object>> listObjs = DbUp.upTable(
				GOV_DISTRICT_TABLE_NAME).dataQuery(" name,code ", "", queryStr,
				null, 0, 0);
		GovDistrict gd;
		for (Map<String, Object> map : listObjs) {
			gd = new GovDistrict();
			gd.setName((String) map.get("name"));
			gd.setCode((String) map.get("code"));
			if (whereStr.length() < 4) {
				gd.setChildren(getChildrendGovDistrict(gd.getChildren(), gd
						.getCode().substring(0, whereStr.length() + 2)));
			}
			list.add(gd);
		}

		return list;
	}

	public static void main(String[] args) {

		String filepath = GovdistrictService.class.getResource("/WEB-INF")
				.toString();

		//System.out.println(filepath);
	}
}
