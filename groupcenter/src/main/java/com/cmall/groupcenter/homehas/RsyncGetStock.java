package com.cmall.groupcenter.homehas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.homehas.config.RsyncConfigGetStock;
import com.cmall.groupcenter.homehas.model.RsyncRequestGetStock;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetStock;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetStock.Stockinfo;
import com.cmall.membercenter.memberdo.MemberConst;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.xmassystem.support.PlusSupportStock;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 同步商品可销售量
 * @author jl
 *
 */
public class RsyncGetStock extends RsyncHomeHas<RsyncConfigGetStock, RsyncRequestGetStock, RsyncResponseGetStock> {

	private final static RsyncConfigGetStock rsyncConfigGetStock = new RsyncConfigGetStock();

	public RsyncConfigGetStock upConfig() {
		return rsyncConfigGetStock;
	}

	private RsyncRequestGetStock rsyncRequestGetStock = new RsyncRequestGetStock();

	public RsyncRequestGetStock upRsyncRequest() {
		
		return rsyncRequestGetStock;
	}

	private boolean responseSu=true;
	private List<String> oList=new ArrayList<String>();
	
	public RsyncResult doProcess(RsyncRequestGetStock tRequest, RsyncResponseGetStock tResponse) {
		RsyncResult mWebResult = new RsyncResult();
		responseSu=tResponse.isSuccess();
		
		if(!tResponse.isSuccess()){	
			mWebResult.setResultCode(918501003);
			mWebResult.setResultMessage(bInfo(918501003));
			return mWebResult;		
		}
		
		List<Stockinfo> stocklist = tResponse.getResult();
		
		String product_code_sync = "";
		int product_stock_old = 0; //商品原库存
		int product_stock_sync = 0; //商品同步库存
		if(stocklist!=null && stocklist.size()>0)
		{
			//获取商品原库存
			PlusSupportStock plusStock = new PlusSupportStock();
			product_code_sync = stocklist.get(0).getGood_id();
			product_stock_old = plusStock.upAllStockForProduct(product_code_sync);
		}
		for (Stockinfo stockinfo : stocklist) {
			String good_id = stockinfo.getGood_id();
//			String good_nm = stockinfo.getGood_nm();
			String color_id = stockinfo.getColor_id();
//			String color_desc = stockinfo.getColor_desc();
			String style_id = stockinfo.getStyle_id(); 
//			String style_desc = stockinfo.getStyle_desc();
			String site_no = stockinfo.getSite_no();
//			String site_nm = stockinfo.getSite_nm();
			String stock_num = stockinfo.getStock_num();
			
			String ok=color_id+style_id+site_no;
			if(oList.contains(ok)){  //重复数据跳过
				continue;
			}
			
			String key="color_id="+color_id+"&style_id="+style_id;
			
			//商品同步库存计算
			product_stock_sync = product_stock_sync + Integer.parseInt(stock_num);
			
			//同步过来的商品同时属于家有惠和惠家有,所以要循环两遍.
			String [] apps = new String[]{MemberConst.MANAGE_CODE_HOMEHAS,MemberConst.MANAGE_CODE_HPOOL};
			for (String app_code : apps) {
				String productCode = stockinfo.getGood_id();
				if(MemberConst.MANAGE_CODE_HPOOL.equals(app_code)){ //家有惠的商品编号前要加9
					productCode = ("9"+productCode);
				}
				
				//查出符合属性的 sku_code
				List<Map<String, Object>> slist= DbUp.upTable("pc_skuinfo").dataSqlList("SELECT sku_code from pc_skuinfo where product_code=:product_code and seller_code=:seller_code and sku_key=:sku_key and flag_enable=1 ", new MDataMap("product_code",productCode,"seller_code",app_code,"sku_key",key));
				if(slist==null || slist.size()<1){
					mWebResult.setResultCode(918501002);
					mWebResult.setResultMessage(bInfo(918501002, good_id,key));
					continue;
				}
				
				String sku_code=(String)slist.get(0).get("sku_code"); //获取到sku_code
				
				//查看是否有重复数据
				int count=DbUp.upTable("sc_store_skunum").dataCount("store_code=:store_code and sku_code=:sku_code ", new MDataMap("store_code",site_no,"sku_code",sku_code));
				if(count>0){ //若存在，执行更新操作
					DbUp.upTable("sc_store_skunum").dataExec("update sc_store_skunum set stock_num=:stock_num where  store_code=:store_code and sku_code=:sku_code ", new MDataMap("store_code",site_no,"sku_code",sku_code,"stock_num",stock_num));
				}else{ //不存在，执行添加操作
					DbUp.upTable("sc_store_skunum").dataInsert(new MDataMap("store_code",site_no,"sku_code",sku_code,"stock_num",stock_num));
				}
								
				PlusHelperNotice.onChangeSkuStock(sku_code);							
			}
			
			oList.add(ok);
		}
		
		//判断是否刷新商品solr索引：原商品库存为0，新同步商品库存不为0时
		if(!"".equals(product_code_sync) && product_stock_old==0 && product_stock_sync>0)
		{
			MDataMap dataMap = new MDataMap();
			dataMap.put("productCode", product_code_sync);
			try
			{
				WebClientSupport.upPost(TopUp.upConfig("productcenter.webclienturladdone"), dataMap);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}		
		return mWebResult;
	}

	public RsyncResponseGetStock upResponseObject() {

		return new RsyncResponseGetStock();
	}

	public boolean responseSucc(){
		
		return responseSu;
	} 
	
}
