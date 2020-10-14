package com.cmall.groupcenter.job;

import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncGetStock;
import com.cmall.membercenter.memberdo.MemberConst;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 同步家有库存任务
 * @author jl
 *
 */
public class JobGetStock extends RootJob {

	public void doExecute(JobExecutionContext context) {
		
		//家有惠的商品和惠家有的商品为同一份，更新库存时更新两份，请求的时候只发惠家有的即可
		//查询家有商品的good_id
		List<Map<String, Object>> list = DbUp.upTable("pc_productinfo").dataSqlList("SELECT DISTINCT product_code_old from pc_productinfo where product_status='4497153900060002' and seller_code in('SI2003') ", new MDataMap());
//		List<Map<String, Object>> list = DbUp.upTable("pc_productinfo").dataSqlList("SELECT DISTINCT product_code_old from seller_code=:seller_code1 or  seller_code=:seller_code3 ", new MDataMap("seller_code3",MemberConst.MANAGE_CODE_HOMEHAS,"seller_code1",MemberConst.MANAGE_CODE_APP));
		if(list!=null){
			for (Map<String, Object> map : list) {
				String good_id = (String)map.get("product_code_old");
				RsyncGetStock rsyncGetStock=new RsyncGetStock();
				rsyncGetStock.upRsyncRequest().setGood_id(good_id);
				rsyncGetStock.doRsync();
			}
		}
	}
	
}
