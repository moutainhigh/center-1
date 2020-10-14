package com.cmall.groupcenter.job;

import java.util.List;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.unionpay.WalletPay;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 微公社商户给用户提现
 * @author GaoYang
 *
 */
public class JobWalletPayMoney extends RootJob{

	@Override
	public void doExecute(JobExecutionContext context) {
		
		WalletPay wPay = new WalletPay();
		//获取审核通过的提现单
		List<MDataMap> list=DbUp.upTable("gc_wallet_withdraw_info").queryByWhere("withdraw_status","4497476000010002");
		for(MDataMap mDataMap:list){
			wPay.PayMoney(mDataMap);
		}
	}
	
	public static void main(String args[]){
//		JobWalletPayMoney payMoney=new JobWalletPayMoney();
//		payMoney.doExecute(null);
		/*try {
			query();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

}
