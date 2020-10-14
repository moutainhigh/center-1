package com.cmall.systemcenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.common.AppConst;
import com.srnpr.xmassystem.support.PlusSupportStock;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
/**
 * 分库存信息,未传入appcode或分库区的方法，默认为惠家有app的sku
 * @author jlin
 *
 */
public class StoreService extends BaseClass{

	/**
	 * 根据区域编码和sku_code 查看仓库库存<br>
	 * 一个区域对应多个仓库，把仓库数据相加
	 * @param district_code
	 * @param sku_code
	 * @return
	 */
	public int getStockNumByDistrict (String district_code,String sku_code){
		int stock=0;
		
		//根据区域编码查看仓库
//		String store_code=(String)DbUp.upTable("sc_store_district").dataGet("store_code", "district_code=:district_code", new MDataMap("district_code",district_code));
		//这里设置了仓库配送不配送的问题
		List<String> list = getStores(district_code);
		
		if(list==null || list.size()<1){
			return stock;
		}
		
		for (String store_code : list) {
			
			stock +=  getStockNumByStore(store_code, sku_code);
		}
		
		return stock ;
	}
	
	/**
	 * 仓库区域编码和sku_code 查看仓库库存
	 * @param store_code
	 * @param sku_code
	 * @return
	 */
	public int getStockNumByStore (String store_code,String sku_code){
		int stockNum=0;
		List<Map<String, Object>> list=DbUp.upTable("sc_store_skunum").dataSqlList("select stock_num from sc_store_skunum where sku_code=:sku_code and store_code=:store_code ", new MDataMap("store_code",store_code,"sku_code",sku_code));
		if(list!=null&&list.size()>0){
			stockNum = Integer.valueOf(String.valueOf(list.get(0).get("stock_num")));
		}
		
		return stockNum;
	}
	
	/**
	 * 根据区域查询仓库
	 * @param district_code
	 * @return
	 */
	public List<String> getStores(String district_code){
		/*List<Map<String, Object>> list = DbUp.upTable("sc_store_district").dataSqlList("SELECT store_code from sc_store_district where district_code=:district_code and express_type in ('10','30') ",  new MDataMap("district_code",district_code));
		if(list==null || list.size()<1){
			return null ;
		}
		
		List<String> storelist=new ArrayList<String>(list.size());
		for (Map<String, Object> map : list) {
			String store_code = (String)map.get("store_code");
			storelist.add(store_code);
		}*/
		List<String> storelist=new ArrayList<String>();
		return storelist;
	}
	
//	10	配送 货到付款
//	20	不配送 
//	30	款到发货 在线支付
	/**
	 * 根据区域查询 express_type
	 * @param district_code
	 * @return
	 */
	public String getExpress(String district_code){
		String express_type="20";
		/*List<Map<String, Object>>  list=DbUp.upTable("sc_store_district").dataSqlList("select express_type from sc_store_district where district_code=:district_code", new MDataMap("district_code",district_code));
		if (list!=null&&list.size()>0) {
			for (Map<String, Object> map : list) {
				String type=(String)map.get("express_type");
				if(type!=null){
					if("10".equals(type)){
						express_type="10";
						break;
					}else if("30".equals(type)){
						express_type=type;
					}
				}
			}
		}*/
		
		return express_type;
	}
	
	/***
	 * 查询一个sku 的所有仓库的库存
	 * @param sku_code
	 * @return
	 */
	public int getStockNumByStore (String sku_code){
//		Map<String, Object> data =DbUp.upTable("sc_store_skunum").dataSqlOne("SELECT SUM(stock_num) AS su from sc_store_skunum where sku_code=:sku_code ", new MDataMap("sku_code",sku_code));
//		BigDecimal num= (BigDecimal)data.get("su");
//		return num==null?0:Integer.valueOf(String.valueOf(num));
		return new PlusSupportStock().upAllStock(sku_code);
	}
	
	public int getStockNumByMax (String sku_code){
//		Map<String, Object> data =DbUp.upTable("sc_store_skunum").dataSqlOne("SELECT max(stock_num) AS su from sc_store_skunum where sku_code=:sku_code ", new MDataMap("sku_code",sku_code));
//		BigDecimal num= (BigDecimal)data.get("su");
//		return num==null?0:Integer.valueOf(String.valueOf(num));
		return new PlusSupportStock().upAllStock(sku_code);
	}
	
	/**
	 * 惠美丽查询库存
	 * @param sku_code
	 * @return
	 */
	public int getStockNumByMaxFor7 (String sku_code){
		Map<String, Object> data =DbUp.upTable("sc_store_skunum").dataSqlOne("SELECT max(stock_num) AS su from sc_store_skunum where sku_code=:sku_code and store_code=:store_code ", new MDataMap("sku_code",sku_code,"store_code",AppConst.CAPP_STORE_CODE));
		BigDecimal num= (BigDecimal)data.get("su");
		return num==null?0:Integer.valueOf(String.valueOf(num));
	}
	
	/***
	 * 小时代查询库存
	 * @param sku_code
	 * @return
	 */
	public int getStockNumByMaxFor13 (String sku_code){
		Map<String, Object> data =DbUp.upTable("sc_store_skunum").dataSqlOne("SELECT max(stock_num) AS su from sc_store_skunum where sku_code=:sku_code and store_code=:store_code ", new MDataMap("sku_code",sku_code,"store_code",AppConst.CYOUNG_STORE_CODE));
		BigDecimal num= (BigDecimal)data.get("su");
		return num==null?0:Integer.valueOf(String.valueOf(num));
	}
	
	/**
	 * 查询所有库存的库存数，
	 * @param sku_code
	 * @return 数组顺序：[总数 ...]
	 */
	public Object[] getAllStockNumByStore (String sku_code){
		MDataMap mDataMap  = DbUp.upTable("pc_skuinfo").one("sku_code",sku_code);
		if(mDataMap!=null&&!mDataMap.isEmpty()&&StringUtils.isNotBlank(mDataMap.get("sku_code_old"))){
			sku_code=mDataMap.get("sku_code_old");
		}
		List<Map<String, Object>> list =DbUp.upTable("sc_store_skunum").dataSqlList("SELECT store_code,stock_num from sc_store_skunum where sku_code=:sku_code order by store_code,stock_num ", new MDataMap("sku_code",sku_code));
		Map<String, Integer> mmap= new LinkedHashMap<String, Integer>(5);
		int ac= 0;
		for (Map<String, Object> map : list) {
			String store_code= (String)map.get("store_code");
			BigDecimal stock_num= (BigDecimal)map.get("stock_num");
			if(store_code!=null){
				stock_num= (BigDecimal)(stock_num==null?0:stock_num);
				int stock=Integer.valueOf(String.valueOf(stock_num));
				mmap.put(store_code, stock);
				ac+=stock;
			}
		}
		
		//这里需求提出必须显示所有仓库库存，所有，看一下有没有不存在仓库的
		String stores []= new String [] {"C01","C02","C04","C10","C18","C22"};
		for (String store : stores) {
			if(!mmap.containsKey(store)){
				mmap.put(store, 0);
			}
		}
		return new Object[]{ac,mmap};
	}
	
	/**
	 * 查询所有仓库信息
	 * @param app_code
	 * @return
	 */
	public MDataMap storeMap(String app_code){
		List<MDataMap> list=DbUp.upTable("sc_store").queryAll("store_code,store_name", "store_code", "app_code=:app_code", new MDataMap("app_code",app_code));
		MDataMap map = new MDataMap();
		for (MDataMap mDataMap : list) {
			map.put(mDataMap.get("store_code"), mDataMap.get("store_name"));
		}
		return map;
	}
	
	/***
	 * 查询传入的skuCode的所有仓库的库存
	 * @param skuCodes  格式（'codes1','codes2','codes3'...）
	 * @return
	 * @author ligj
	 */
	public Map<String,Integer> getStockNumByStoreMulti (String skuCodes){
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		Map<String,Integer> result = new HashMap<String, Integer>();
		if (StringUtils.isNotEmpty(skuCodes)) {
			data =DbUp.upTable("sc_store_skunum").dataSqlList("SELECT SUM(stock_num) AS su,sku_code AS sku_code from sc_store_skunum where sku_code in ("+skuCodes+") GROUP BY sku_code", null);
		}
		for (Map<String, Object> map : data) {
			result.put(String.valueOf(map.get("sku_code")),Integer.parseInt(String.valueOf(map.get("su") == null ? "0" : map.get("su"))));
		}
		return result;
	}
	
}
