package com.cmall.systemcenter.txservice;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmapper.ScStoreSkunumMapper;
import com.cmall.dborm.txmodel.ScStoreSkunum;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.service.StoreService;
import com.srnpr.xmassystem.support.PlusSupportStock;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbface.ITxService;
/**
 * 分库存操作
 * @author jlin
 *
 */
public class TxStockService extends BaseClass implements ITxService {

	/**
	 * 惠家有专用<br>
	 * 减少库存<br>
	 * 规则：C01 C02 C04 C10 一次从库中减去
	 * @param district_code 区域编码
	 * @param sku_code 
	 * @param stockNum 数量
	 * @throws Exception
	 */
	@Deprecated
	public String doReduceStock (String district_code,RootResult ret,String sku_code,long stockNum) throws Exception {
		
		
		//用sku_code 判断一下是否为虚拟商品 ，虚拟商品试用虚拟库存 C18
		String product_code=(String)DbUp.upTable("pc_skuinfo").dataGet("product_code", "sku_code=:sku_code", new MDataMap("sku_code",sku_code));
		String validate_flag=(String)DbUp.upTable("pc_productinfo").dataGet("validate_flag", "product_code=:product_code", new MDataMap("product_code",product_code));
		
		
		
		com.cmall.dborm.txmapper.ScStoreSkunumMapper scStoreSkunumMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_ScStoreSkunumMapper");
		StoreService storeService=BeansHelper.upBean("bean_com_cmall_systemcenter_service_StoreService");
		
		ScStoreSkunum scStoreSkunum=new ScStoreSkunum();
		scStoreSkunum.setSkuCode(sku_code);
		
		String stores []= null;
		
		
		if("Y".equals(validate_flag)){ //虚拟商品
			stores = new String [] {"C18"};
			scStoreSkunum.setStoreCode(stores[0]);
			
		}else{
			
			stores = new String [] {"C01","C02","C04","C10"};
			
			List<String>  list = storeService.getStores(district_code);
			if(list==null || list.size() < 1){
				ret.setResultCode(949705202);
				ret.setResultMessage(bInfo(949705202, district_code));
				throw new Exception(ret.getResultMessage());
			}
			scStoreSkunum.setStoreCode(list.get(0));
		}
		
		
		scStoreSkunumMapper.updateStockBylock(scStoreSkunum);//锁表 你懂得
		
		boolean bflag=true;
		StringBuffer sb = new StringBuffer ();
		for (String store : stores) { //按顺序依次扣除
//			if(list.contains(store)){ ////////////////////////////////////////////////////当前废弃区域设置，扣库存无区域限制 update by jlin 2014-10-15 11:56:00
				//查询一下对应的区域
				int stock = storeService.getStockNumByStore(store, sku_code);
				if(stock > 0){  //当有库存时，可以减
					scStoreSkunum.setStoreCode(store);
					long cc=stockNum-stock>=0?stock:stockNum;//要减的库存
					scStoreSkunum.setStockNum(cc);
					scStoreSkunumMapper.updateStock_num(scStoreSkunum); // 减库存
					sb.append(",").append(store).append("_").append(cc);
					stockNum = stockNum-stock;
				}
				
				if(stockNum <= 0){
					bflag=false;
					break;
				}
//			}
		}
		
		if(bflag){ // 不够减，异常回滚
			ret.setResultCode(949705203);
			ret.setResultMessage(bInfo(949705203));
			throw new Exception(ret.getResultMessage());
		}else{
			
			return sb.substring(1);
		}
	}
	
	/**
	 * 扣库存<br>
	 * 家有惠、惠家有专用<br>
	 * 规则：虚拟商品扣除 C18 库存，一地入库扣除  storeCode 库存，四地入库 依次扣除 C01、C02、C04、C10 四地库存
	 * @param sku_code
	 * @param stockNum
	 * @param validateFlag 虚拟商品标示   Y：是  N：否 默认为N
	 * @param prchType 一地入库类型    00-非一地入库   10-商品中心一地入库 20-网站一地入库，默认为00
	 * @param oaSiteNo  入库仓库编号 
	 * @param ret
	 * @return
	 * @throws Exception
	 */
	public String doReduceStock (String sku_code,int stockNum,String validateFlag,String prchType,String oaSiteNo,RootResult ret) throws Exception {
		
//		ScStoreSkunumMapper scStoreSkunumMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_ScStoreSkunumMapper");
//		StoreService storeService=BeansHelper.upBean("bean_com_cmall_systemcenter_service_StoreService");
//		谢冠杰修改--减库存走缓存
//		String stores []= null;
//		
//		//判断是否为虚拟商品
//		if("Y".equals(validateFlag)){//是虚拟商品 直接扣除虚拟仓库C18 的库存
//			if(StringUtils.isNotBlank(oaSiteNo)&&!"C18".equals(oaSiteNo)&&!"null".equals(oaSiteNo)){
//				stores = new String [] {oaSiteNo};
//			}else{
//				stores = new String [] {"C18"};
//			}
//		}else{
//			
//			//判断是否为一地入库
//			if("10".equals(prchType)||"20".equals(prchType)){
//				stores = new String [] {oaSiteNo};
//			}else{//非一地入库
//				stores = new String [] {"C01","C02","C04","C10"};
//			}
//		}
//		
//		ScStoreSkunum scStoreSkunum=new ScStoreSkunum();
//		scStoreSkunum.setSkuCode(sku_code);
//		scStoreSkunum.setStoreCode(stores[0]);
//		scStoreSkunumMapper.updateStockBylock(scStoreSkunum);//锁表 你懂得
//		
//		boolean bflag=true;
//		StringBuffer sb = new StringBuffer ();
//		for (String store : stores) { //按顺序依次扣除
//			//查询一下对应的区域
//			int stock = storeService.getStockNumByStore(store, sku_code);
//			if(stock > 0){  //当有库存时，可以减
//				scStoreSkunum.setStoreCode(store);
//				long cc=stockNum-stock>=0?stock:stockNum;//要减的库存
//				scStoreSkunum.setStockNum(cc);
//				scStoreSkunumMapper.updateStock_num(scStoreSkunum); // 减库存
//				sb.append(",").append(store).append("_").append(cc);
//				stockNum = stockNum-stock;
//			}
//			
//			if(stockNum <= 0){
//				bflag=false;
//				break;
//			}
//		}
//		
//		if(bflag){ // 不够减，异常回滚
//			ret.setResultCode(949705203);
//			ret.setResultMessage(bInfo(949705203));
//			throw new Exception(ret.getResultMessage());
//		}else{
//			return sb.substring(1);
//		}
		return new PlusSupportStock().subtractSkuStock("",sku_code, stockNum);
	}
	
	/**
	 * 分仓库 单个仓库专用<br>
	 * 更新库存 [在原库存基础上减库存，若加库存，请填入 负值]
	 * @param ret
	 * @param stockNum
	 * @param skuCode
	 * @param store_code
	 * @return
	 * @throws Exception
	 */
	public String doChangeStock (RootResult ret,long stockNum,String skuCode,String store_code) throws Exception {

		com.cmall.dborm.txmapper.ScStoreSkunumMapper scStoreSkunumMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_ScStoreSkunumMapper");
		
		ScStoreSkunum scStoreSkunum=new ScStoreSkunum();
		scStoreSkunum.setSkuCode(skuCode);
		scStoreSkunum.setStoreCode(store_code);
		scStoreSkunum.setStockNum(stockNum);
		scStoreSkunumMapper.updateStockBylock(scStoreSkunum);//锁表
		
		//查看表中是否已经存在该记录，若存在则修改，若无则新增
		List<Map<String, Object>> list= DbUp.upTable("sc_store_skunum").dataQuery("stock_num", "", "store_code=:store_code and sku_code=:sku_code ", new MDataMap("sku_code",skuCode,"store_code",store_code), 0, 1);
		if(list==null||list.size()<1){//没有数据的情况下 新增
			if(-stockNum<0){ //小于0 不能再减
				ret.setResultCode(949705204);
				ret.setResultMessage(bInfo(949705204, skuCode));
				throw new Exception(ret.getResultMessage());
			}
			
			//开始新增库存记录
			scStoreSkunum.setStockNum(-stockNum);
			scStoreSkunum.setUid(UUID.randomUUID().toString().replace("-", ""));
			scStoreSkunumMapper.insertSelective(scStoreSkunum);
			
		}else{
			long stockNum_o=Long.valueOf(String.valueOf(list.get(0).get("stock_num")));
			if((stockNum_o-stockNum)<0){
				ret.setResultCode(949705204);
				ret.setResultMessage(bInfo(949705204, skuCode));
				throw new Exception(ret.getResultMessage());
			}
			
			//开始更新库存
			scStoreSkunumMapper.updateStock_num(scStoreSkunum);
		}
		
		return store_code+"_"+stockNum;
	
	}
	
	/***
	 * 
	 * 惠美丽 现一个仓库专用<br>
	 * 更新库存 [在原库存基础上减库存，若加库存，请填入 负值]
	 * @param ret
	 * @param stockNum
	 * @param skuCode
	 * @throws Exception
	 */
	public String doChangeStock (RootResult ret,long stockNum,String skuCode) throws Exception {
		return doChangeStock(ret, stockNum, skuCode,AppConst.CAPP_STORE_CODE);
	}
}
