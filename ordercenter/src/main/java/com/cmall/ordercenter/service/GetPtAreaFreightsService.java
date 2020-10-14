package com.cmall.ordercenter.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.model.GetPtAreaFreightInput;
import com.cmall.ordercenter.model.GetPtAreaFreightResult;
import com.cmall.ordercenter.model.PtAreaFreight;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 根据传入的商品编码，获得商品到各区域的运费
 * @author huoqiangshou
 *
 */
public class GetPtAreaFreightsService extends BaseClass{
	
	/**
	 * 区域表
	 */
	private static String SC_GOV_DISTRICT ="sc_gov_district";
	
	/**
	 * 商品
	 */
	private static String PC_PRODUCTINFO = "pc_productinfo";
	
	/**
	 * 运费模板
	 */
	private static String UC_FREIGHT_TPL = "uc_freight_tpl";
	
	/**
	 * 运费模板明细
	 */
	private static String UC_FREIGHT_TPL_DETAIL = "uc_freight_tpl_detail";
	
	/**
	 * 省份
	 */
	private static String[] provinces = "110000=北京市,120000=天津市,130000=河北省,140000=山西省,150000=内蒙,210000=辽宁省,220000=吉林省,230000=黑龙江省,310000=上海市,320000=江苏省,330000=浙江省,340000=安徽省,350000=福建省,360000=江西省,370000=山东省,410000=河南省,420000=湖北省,430000=湖南省,440000=广东省,450000=广西,460000=海南省,500000=重庆市,510000=四川省,520000=贵州省,530000=云南省,540000=西藏,610000=陕西省,620000=甘肃省,630000=青海省,640000=宁夏,650000=新疆,710000=台湾省,810000=香港,820000=澳门".split(",");
	
	
	/**
	 *全国 
	 */
	private static String GLOBAL="global";
	
	/**
	 * 指定区域不可售
	 */
	private String SPE_AREA_NOT_SALE = "不可售";
	
	/**
	 * 返回
	 * @param input
	 * @return
	 */
	public GetPtAreaFreightResult doGetPtAreaFreights(GetPtAreaFreightInput input){
		GetPtAreaFreightResult result = new GetPtAreaFreightResult();
		//查询商品使用的模板
		List<MDataMap> transportList =   DbUp.upTable(PC_PRODUCTINFO).query("transport_template", "", "  product_code = '"+input.getProductCode()+"'", null, 0, 1);
		if(null==transportList||transportList.size()==0){ //没有模板返回
			result.setResultCode(1); 
			result.setResultMessage(bInfo(939301083));
			return result;
		}
		String transCode = transportList.get(0).get("transport_template");
		
		//免运费
		if("0".equals(transCode)){
			result.setResultCode(1); 
			result.setResultMessage(bInfo(939301083));
			return result;
		}
		
		//对商品单独设置 运费
		if(StringUtils.isNumeric(transCode)){
			result.setResultCode(1);
			result.setResultMessage(transCode);
			return result;
		}
		
		
		//模板是否可用
		transportList =   DbUp.upTable(UC_FREIGHT_TPL).query("isDisable", "", "  uid = '"+transCode+"'", null, 0, 1);
		if(null==transportList||transportList.size()==0){
			result.setResultCode(1);
			result.setResultMessage(bInfo(939301074));
			return result;
		}
		String transIsEnable = transportList.get(0).get("isDisable");
		if(!"449746250002".equals(transIsEnable)){
			result.setResultCode(1);
			result.setResultMessage(bInfo(939301074));
			return result;
		}
		
		Map<String,PtAreaFreight> ptMap = new HashMap<String, PtAreaFreight>();
		PtAreaFreight tmpPf = null;
		// 模板明细
		transportList =   DbUp.upTable(UC_FREIGHT_TPL_DETAIL).query("", "  sequence ", "  tpl_uid = '"+transCode+"'", null, 0, 0);
		//默认费用
		String money = transportList.get(0).get("express_Postage").toString();
		 //设置默认值
		for(String p:provinces){
			tmpPf = new PtAreaFreight();
			tmpPf.setAreaCode(p.split("=")[0]);
			tmpPf.setAreaName(p.split("=")[1]);
			tmpPf.setFee(money);
			ptMap.put(p.split("=")[0], tmpPf);
		}
		
		
		for(MDataMap map:transportList){
			if(GLOBAL.equals(map.get("area_Code"))){
				continue;
			}
			
			if("1".equals(map.get("isEnable"))){
				for(String areaCode:map.get("area_Code").split(",")){
					tmpPf = ptMap.get(areaCode);
					tmpPf.setFee(map.get("express_Postage"));
					ptMap.put(areaCode, tmpPf);
				}
			}else{  //不可用  不可售
				for(String areaCode:map.get("area_Code").split(",")){
					tmpPf = ptMap.get(areaCode);
					tmpPf.setFee(SPE_AREA_NOT_SALE);
					ptMap.put(areaCode, tmpPf);
				}
			}
			
		}
		
		result.setResultCode(1);
		result.setPtList(new ArrayList<PtAreaFreight>(ptMap.values()));
		
		return result;
	}
	
}
