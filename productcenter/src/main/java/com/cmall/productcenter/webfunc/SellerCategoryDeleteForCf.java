package com.cmall.productcenter.webfunc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cmall.productcenter.common.DateUtil;
import com.cmall.systemcenter.dcb.PushSkuStatusService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * ClassName: 删除店铺私有类目（以及类目与对应商品的关系）<br/>
 * @author   jack
 * @version  1.0
 */
public class SellerCategoryDeleteForCf extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult  mResult = new MWebResult();
		MWebOperate mOperate = WebUp.upOperate(sOperateUid);
		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());
		MDataMap mDelMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		if (mResult.upFlagTrue()) {
			if (mDelMaps.containsKey("uid")) {
				MDataMap mThisMap=null;
				// 循环所有结构
				for (MWebField mField : mPage.getPageFields()) {
					if (mField.getFieldTypeAid().equals("104005003")) {
						if(mThisMap==null)
						{
							mThisMap=DbUp.upTable(mPage.getPageTable()).one("uid",mDelMaps.get("uid"));
						}
						WebUp.upComponent(mField.getSourceCode()).inDelete(mField,
								mThisMap);
					}
				}
				List<MDataMap> codes = DbUp.upTable(mPage.getPageTable()).query("seller_code,category_code", "", "", mDelMaps,0,0);
				DbUp.upTable(mPage.getPageTable()).delete("uid",mDelMaps.get("uid"));
				for(int i=0;i<codes.size();i++){
					DbUp.upTable("uc_sellercategory_pre").delete("category_code", codes.get(i).get("category_code"), "seller_code", "SI2003", "level", "4");
					
					//删除缤纷佣金配置记录
					List<Map<String, Object>> configMapList = DbUp.upTable("fh_bf_charge_config").listByWhere("charge_type", codes.get(i).get("category_code"));
					if(configMapList.size() > 0) {
						DbUp.upTable("fh_bf_charge_config").delete("charge_type", codes.get(i).get("category_code"));
						
						for(Map<String, Object> configMap : configMapList) {
							String chargeName = configMap.get("charge_name").toString();
							if(!"449748060003".equals(chargeName)) {
								String charge_type = configMap.get("charge_type").toString();
								List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
								String sql = "select sku.*, skuinfo.sku_name from pc_bf_skuinfo sku, pc_skuinfo skuinfo, pc_productinfo product, usercenter.uc_sellercategory_product_relation rela "
												+ "where sku.product_code = product.product_code and product.product_code = rela.product_code and rela.category_code = '" + charge_type 
												+ "' and rela.seller_code = 'SI2003' and sku.sku_code = skuinfo.sku_code and (sku.sku_status = '1' or sku.sku_status = '2' or sku.sku_status = '10' or sku.sku_status = '20')";
								List<Map<String, Object>> list1 = DbUp.upTable("pc_bf_skuinfo").dataSqlList(sql, new MDataMap());
								list.addAll(list1);
								list = getAllSkuInfo(charge_type, list);
								
								Set<Map<String, Object>> set = new HashSet<Map<String, Object>>();
								set.addAll(list);
								
								list.clear();
								list.addAll(set);
								for(Map<String, Object> map : list) {
									String newStatus = "";
									String remark = "";
									String sku_code = map.get("sku_code").toString();
									String sku_status = map.get("sku_status").toString();
									if("10".equals(sku_status)) {//已上架
										//调用多彩宝接口，让多彩宝对应的sku下架
										PushSkuStatusService pushSkuStatusService = new PushSkuStatusService();
										pushSkuStatusService.pushSkuStatus(sku_code, "N", 0, "");
										
										newStatus = "30";
										remark = "佣金配置删除导致强制下架";
									}else {//其他状态
										newStatus = "50";
										remark = "佣金配置删除自动驳回";
									}
									
									//更新pc_bf_skuinfo表状态
									MDataMap skuUpdateMap = new MDataMap();
									skuUpdateMap.put("zid", map.get("zid").toString());
									skuUpdateMap.put("uid", map.get("uid").toString());
									skuUpdateMap.put("sku_status", newStatus);
									DbUp.upTable("pc_bf_skuinfo").update(skuUpdateMap);
									
									//添加pc_bf_review_log 日志表
									String createTime = DateUtil.getSysDateTimeString();
									DbUp.upTable("pc_bf_review_log").insert("sku_code", sku_code, "sku_name", map.get("sku_name").toString(), "operate_status", "佣金配置变更", "operator", "系统", 
											"operate_time", createTime, "remark", remark);
								}
							}
						}
					}
					
					DbUp.upTable("uc_sellercategory_product_relation").delete("category_code",codes.get(i).get("category_code"),"seller_code",codes.get(i).get("seller_code"));
					DbUp.upTable("uc_sellercategory_brand_relation").delete("category_code",codes.get(i).get("category_code"),"seller_code",codes.get(i).get("seller_code"));
				}
			}
		}
		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;
	}
	
	private List<Map<String, Object>> getAllSkuInfo(String charge_type, List<Map<String, Object>> list) {
		List<Map<String, Object>> sellerCategoryList = DbUp.upTable("uc_sellercategory").listByWhere("seller_code", "SI2003", "parent_code", charge_type);
		for(Map<String, Object> sellerCategory : sellerCategoryList) {
			String sql1 = "select sku.*, skuinfo.sku_name from pc_bf_skuinfo sku, pc_skuinfo skuinfo, pc_productinfo product, usercenter.uc_sellercategory_product_relation rela "
							+ "where sku.product_code = product.product_code and product.product_code = rela.product_code and rela.category_code = '" + sellerCategory.get("category_code").toString()
							+ "' and rela.seller_code = 'SI2003' and sku.sku_code = skuinfo.sku_code and (sku.sku_status = '1' or sku.sku_status = '2' or sku.sku_status = '10' or sku.sku_status = '20')";
			List<Map<String, Object>> list2 = DbUp.upTable("pc_bf_skuinfo").dataSqlList(sql1, new MDataMap());
			list.addAll(list2);
			
			list.addAll(getAllSkuInfo(sellerCategory.get("category_code").toString(), list2));
		}
		return list;
	}
}

