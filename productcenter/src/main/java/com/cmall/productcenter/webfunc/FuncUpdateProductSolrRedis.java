package com.cmall.productcenter.webfunc;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 传入商品编号 solr索引库和redis缓存库数据都会删除 重新创建
 * @author zhouguohui
 *
 */
public class FuncUpdateProductSolrRedis extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		
		if (mResult.upFlagTrue()) {
			if(StringUtils.isEmpty(mAddMaps.get("product_code"))){
				mResult.setResultMessage("你的网络被高智慧生物屏蔽了，稍后再试试");
			}else{
				String[] pro = mAddMaps.get("product_code").split(",");
				for(int j=0;j<pro.length;j++){
					MDataMap dataMap = new MDataMap();
					dataMap.put("productCode", pro[j]);
					try {
						WebClientSupport.upPost(TopUp.upConfig("productcenter.webclienturladdone"), dataMap);
						PlusHelperNotice.onChangeProductInfo(pro[j]);
						
					} catch (Exception e) {
						e.printStackTrace();
						mResult.setResultMessage("你的网络被高智慧生物屏蔽了，稍后再试试");
					}
				}
				
			}
		}
		
		

		return mResult;
	}
	
	public static void main(String[] args) {
		MDataMap map = new MDataMap();
		FuncUpdateProductSolrRedis f = new FuncUpdateProductSolrRedis();
		String sql = "select product_code from pc_productinfo where product_status='4497153900060002'";
		List<Map<String, Object>> list = DbUp.upTable("pc_productinfo").dataSqlList(sql, new MDataMap());
		for(Map<String, Object> lt : list) {
			String pc = lt.get("product_code") == null ? "" : lt.get("product_code").toString();
			if(StringUtils.isNotEmpty(pc)) {
				map.put("zw_f_product_code", pc);
				f.funcDo("bd7263b8e40e11e5a337005056925439", map);
			}
		}
	}

}
