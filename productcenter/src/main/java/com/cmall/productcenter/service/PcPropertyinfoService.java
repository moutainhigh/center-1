/**
 * Project Name:productcenter
 * File Name:PcPropertyinfoService.java
 * Package Name:com.cmall.productcenter.service
 * Date:2013年10月17日上午9:59:34
 *
 */
package com.cmall.productcenter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebTemp;

/**
 * ClassName:PcPropertyinfoService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2013年10月17日 上午9:59:34 <br/>
 * 
 * @author hexd
 * @version
 * @since JDK 1.6
 * @see
 */
public class PcPropertyinfoService extends BaseClass {
	/**
	 * getFirstProperty:(获取一级类目). <br/>
	 * 
	 * @author hxd
	 * @param paramCode
	 * @return
	 * @since JDK 1.6
	 */
	public static List<MDataMap> getFirstProperty(String paramCode) {
		MDataMap dataMap = new MDataMap();
		String[] property_code = paramCode.split(",");

		List<String> listWhere = new ArrayList<String>();

		for (int i = 0; i < property_code.length; i++) {
			dataMap.put("property_code" + String.valueOf(i), property_code[i]);

			listWhere.add(" property_code=:property_code" + String.valueOf(i)
					+ " ");

		}
		List<MDataMap> sfsListMap = null;
		try {
			String sWhereString = StringUtils.join(listWhere, " or ");

			sfsListMap = WebTemp.upTempDataList("pc_propertyinfo",
					"property_code,property_name,parent_code", "",
					sWhereString, dataMap.upStrings());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sfsListMap;
	}

	/**
	 * 获取二级分类
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getSecondCategory() {
		String sql = "select * from pc_categoryinfo where  parent_code in (select category_code "
				+ "from pc_categoryinfo where parent_code in "
				+ "(select category_code from pc_categoryinfo where parent_code = 44971603))";
		List<Map<String, Object>> sfsListMap = DbUp.upTable("pc_categoryinfo")
				.dataSqlList(sql, new MDataMap());
		return sfsListMap;
	}


}
