package com.cmall.groupcenter.homehas;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cmall.groupcenter.homehas.config.RsyncConfigSyncContraband;
import com.cmall.groupcenter.homehas.model.RsyncModelContraband;
import com.cmall.groupcenter.homehas.model.RsyncRequestSyncContraband;
import com.cmall.groupcenter.homehas.model.RsyncResponseSyncContraband;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 违禁品配置信息同步
 * @author cc
 *
 */
public class RsyncSyncContraband extends RsyncHomeHas<RsyncConfigSyncContraband, RsyncRequestSyncContraband, RsyncResponseSyncContraband>{

	final static RsyncConfigSyncContraband rsyncConfigSyncContraband = new RsyncConfigSyncContraband();
	@Override
	public RsyncConfigSyncContraband upConfig() {
		return rsyncConfigSyncContraband;
	}

	@Override
	public RsyncRequestSyncContraband upRsyncRequest() {
		return new RsyncRequestSyncContraband();
	}

	public synchronized MWebResult insertContrabands(RsyncModelContraband contraband) {
		MWebResult mWebResult = new MWebResult();
		String lrgn_cd = contraband.getLrgn_cd() == null ? "" : contraband.getLrgn_cd();
		String mrgn_cd = contraband.getMrgn_cd() == null ? "" : contraband.getMrgn_cd();
		String srgn_cd = contraband.getSrgn_cd() == null ? "" : contraband.getSrgn_cd();
		String danger_type = contraband.getDanger_type() == null ? "N" : contraband.getDanger_type();
		String toplimit = contraband.getToplimit() == null ? "0" : contraband.getToplimit().toString();
		if("".equals(toplimit) || "null".equals(toplimit)) {
			toplimit = "0";
		}
		String vl_yn = contraband.getVl_yn() == null ? "Y" : contraband.getVl_yn();
		MDataMap dataMap = new MDataMap();
		dataMap.put("uid", UUID.randomUUID().toString().replace("-", ""));
		dataMap.put("lrgn_cd", lrgn_cd);
		dataMap.put("mrgn_cd", mrgn_cd);
		dataMap.put("srgn_cd", srgn_cd);
		dataMap.put("danger_type", danger_type);
		dataMap.put("toplimit", toplimit);
		dataMap.put("vl_yn", vl_yn);
		DbUp.upTable("pc_product_contraband").dataInsert(dataMap);
			
		return mWebResult;
	}
	
	public void testRsyncSyncContraband() {
		RsyncRequestSyncContraband request = new RsyncRequestSyncContraband();
		RsyncResponseSyncContraband tResponse = new RsyncResponseSyncContraband();		
		List<RsyncModelContraband> result = new ArrayList<RsyncModelContraband>();
		RsyncModelContraband contraband1 = new RsyncModelContraband();
		contraband1.setLrgn_cd("540000");
		contraband1.setMrgn_cd("540100");
		contraband1.setSrgn_cd("");
		contraband1.setDanger_type("E");
		contraband1.setToplimit("100");
		contraband1.setVl_yn("Y");
		RsyncModelContraband contraband2 = new RsyncModelContraband();
		contraband2.setLrgn_cd("540000");
		contraband2.setMrgn_cd("540100");
		contraband2.setSrgn_cd("");
		contraband2.setDanger_type("D");
		contraband2.setToplimit("20");
		contraband2.setVl_yn("Y");
		RsyncModelContraband contraband3 = new RsyncModelContraband();
		contraband3.setLrgn_cd("540000");
		contraband3.setMrgn_cd("540100");
		contraband3.setSrgn_cd("");
		contraband3.setDanger_type("A");
		contraband3.setToplimit("null");
		contraband3.setVl_yn("Y");
		RsyncModelContraband contraband4 = new RsyncModelContraband();
		contraband4.setLrgn_cd("540000");
		contraband4.setMrgn_cd("540100");
		contraband4.setSrgn_cd("");
		contraband4.setDanger_type("F");
		contraband4.setToplimit("0");
		contraband4.setVl_yn("Y");
		result.add(contraband1);
		result.add(contraband2);
		result.add(contraband3);
		result.add(contraband4);
		tResponse.setResult(result);
		new RsyncSyncContraband().doProcess(request, tResponse);
	}
	
	@Override
	public RsyncResult doProcess(RsyncRequestSyncContraband tRequest, RsyncResponseSyncContraband tResponse) {
		RsyncResult result = new RsyncResult();

		// 定义成功的数量合计
		int iSuccessSum = 0;

		if (result.upFlagTrue()) {
			if (tResponse != null && tResponse.getResult() != null) {
				result.setProcessNum(tResponse.getResult().size());
			} else {
				result.setProcessNum(0);
			}
		}

		// 开始循环处理结果数据
		if (result.upFlagTrue()) {

			// 判断有需要处理的数据才开始处理
			if (result.getProcessNum() > 0) {
				
				result.setProcessNum(tResponse.getResult().size());
				try {
					DbUp.upTable("pc_product_contraband").dataExec("delete from pc_product_contraband", new MDataMap());
				} catch(EmptyResultDataAccessException e) {
					e.printStackTrace();
				}				
				for (RsyncModelContraband contraband : tResponse.getResult()) {
					MWebResult mResult = insertContrabands(contraband);

					// 如果成功则将成功计数加1
					if (mResult.upFlagTrue()) {
						iSuccessSum++;

					} else {
						if (result.getResultList() == null) {
							result.setResultList(new ArrayList<Object>());
						}
						result.getResultList().add(mResult.getResultMessage());
					}
				}

				// 设置处理信息
				result.setProcessData(bInfo(918501102, result.getProcessNum(),
						iSuccessSum, result.getProcessNum() - iSuccessSum));
			}
		}

		// 如果操作都成功 则设置状态保存数据为同步结束时间 以方便下一轮调用
		if (result.upFlagTrue()) {
			result.setSuccessNum(iSuccessSum);
		}

		return result;
	}

	@Override
	public RsyncResponseSyncContraband upResponseObject() {
		RsyncResponseSyncContraband response = new RsyncResponseSyncContraband();
		return response;
	}

}
