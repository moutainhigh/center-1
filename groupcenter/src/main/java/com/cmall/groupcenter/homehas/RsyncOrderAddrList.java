package com.cmall.groupcenter.homehas;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cmall.groupcenter.homehas.config.RsyncConfigOrderAddrList;
import com.cmall.groupcenter.homehas.model.RsyncRequestOrderAddrList;
import com.cmall.groupcenter.homehas.model.RsyncResponseOrderAddrList;
import com.cmall.groupcenter.homehas.model.RsyncResponseOrderAddrList.AddrInfo;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.srnpr.xmassystem.load.LoadAdressAreaCode;
import com.srnpr.xmassystem.modelproduct.PlusModelAreaQuery;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 同步所有可配送地址与不可配送地址
 * 
 * @author jlin
 *
 */
public class RsyncOrderAddrList extends
		RsyncHomeHas<RsyncConfigOrderAddrList, RsyncRequestOrderAddrList, RsyncResponseOrderAddrList> {

	final static RsyncConfigOrderAddrList RSYNC_CONFIG_ORDER_ADDR_LIST = new RsyncConfigOrderAddrList();

	@Override
	public RsyncConfigOrderAddrList upConfig() {
		return RSYNC_CONFIG_ORDER_ADDR_LIST;
	}

	private RsyncRequestOrderAddrList request = new RsyncRequestOrderAddrList();

	@Override
	public RsyncRequestOrderAddrList upRsyncRequest() {
		return request;
	}

	@Override
	public RsyncResult doProcess(RsyncRequestOrderAddrList tRequest, RsyncResponseOrderAddrList tResponse) {

		RsyncResult result = new RsyncResult();
		if (!tResponse.isSuccess()) {
			result.inErrorMessage(918501003);
			return result;
		}
		
		for (AddrInfo addrInfo : tResponse.getResult()) {

			String LRGN_CD = addrInfo.getLRGN_CD();
			String LRGN_NM = addrInfo.getLRGN_NM();
			String LRGN_SHOW_YN = addrInfo.getLRGN_SHOW_YN();
			String LRGN_VL_YN = addrInfo.getLRGN_VL_YN();
			String MRGN_CD = addrInfo.getMRGN_CD();
			String MRGN_NM = addrInfo.getMRGN_NM();
			String MRGN_SHOW_YN = addrInfo.getMRGN_SHOW_YN();
			String MRGN_VL_YN = addrInfo.getMRGN_VL_YN();
			String SRGN_CD = addrInfo.getSRGN_CD();
			String SRGN_NM = addrInfo.getSRGN_NM();
			String SRGN_SHOW_YN = addrInfo.getSRGN_SHOW_YN();
			String SRGN_VL_YN = addrInfo.getSRGN_VL_YN();
			String FRGN_CD = addrInfo.getFRGN_CD();
			String FRGN_NM = addrInfo.getFRGN_NM();
			String FRGN_SHOW_YN = addrInfo.getFRGN_SHOW_YN();
			String FRGN_VL_YN = addrInfo.getFRGN_VL_YN();
			String IS_SEND = addrInfo.getIS_SEND();
			String IS_DELAY = addrInfo.getIS_DELAY();
			if(StringUtils.isEmpty(IS_DELAY)) {
				IS_DELAY = "N";
			}
			
			if(null != LRGN_CD && !"".equals(LRGN_CD) && null != LRGN_NM && !"".equals(LRGN_NM)) {
				// 一级地址--省
				// 根据省级code查询，如果查不到直接插入
				List<MDataMap> porvList = DbUp.upTable("sc_tmp").queryByWhere("code",LRGN_CD);
				if (null == porvList || porvList.size() == 0) {
					DbUp.upTable("sc_tmp").insert("code", LRGN_CD, "name", LRGN_NM,"code_lvl","1","use_yn",LRGN_VL_YN,"show_yn",LRGN_SHOW_YN,"send_yn","Y","delay_yn",IS_DELAY);
				}else {
					// 如果能查到，比较数据库中的数据和传过来的数据是否相同，如果不相同则根据code更新
					MDataMap porvDataMap = porvList.get(0);
					if(LRGN_NM.equals(porvDataMap.get("name")) && LRGN_VL_YN.equals(porvDataMap.get("use_yn")) && "1".equals(porvDataMap.get("code_lvl")) 
							&& "Y".equals(porvDataMap.get("send_yn")) && LRGN_SHOW_YN.equals(porvDataMap.get("show_yn")) && "".equals(porvDataMap.get("p_code"))&& IS_DELAY.equals(porvDataMap.get("delay_yn"))) {
						
					}else {
						// 数据有误则更新
						DbUp.upTable("sc_tmp").dataUpdate(new MDataMap("code", LRGN_CD, "name", LRGN_NM,"code_lvl","1","use_yn",LRGN_VL_YN,"show_yn",LRGN_SHOW_YN,"send_yn","Y","delay_yn",IS_DELAY), "name,code_lvl,use_yn,show_yn,send_yn,delay_yn", "code");
					}
				}
			}

			if(null != MRGN_CD && !"".equals(MRGN_CD) && null != MRGN_NM && !"".equals(MRGN_NM)) {
				// 二级地址--市
				List<MDataMap> cityList = DbUp.upTable("sc_tmp").queryByWhere("code",MRGN_CD);
				if (null == cityList || cityList.size() == 0) {
					DbUp.upTable("sc_tmp").insert("code", MRGN_CD, "name", MRGN_NM,"p_code",LRGN_CD,"code_lvl","2","use_yn",MRGN_VL_YN,"show_yn",MRGN_SHOW_YN,"send_yn","Y","delay_yn",IS_DELAY);
				}else {
					MDataMap cityDataMap = cityList.get(0);
					if(MRGN_NM.equals(cityDataMap.get("name")) && MRGN_VL_YN.equals(cityDataMap.get("use_yn")) && "2".equals(cityDataMap.get("code_lvl")) 
							&& "Y".equals(cityDataMap.get("send_yn")) && MRGN_SHOW_YN.equals(cityDataMap.get("show_yn")) && LRGN_CD.equals(cityDataMap.get("p_code"))&& IS_DELAY.equals(cityDataMap.get("delay_yn"))) {
						
					}else {
						// 数据有误则更新
						DbUp.upTable("sc_tmp").dataUpdate(new MDataMap("code", MRGN_CD, "name", MRGN_NM,"p_code",LRGN_CD,"code_lvl","2","use_yn",MRGN_VL_YN,"show_yn",MRGN_SHOW_YN,"send_yn","Y","delay_yn",IS_DELAY), "name,p_code,code_lvl,use_yn,show_yn,send_yn,delay_yn", "code");
					}
				}
			}
			
			if(null != SRGN_CD && !"".equals(SRGN_CD) && null != SRGN_NM && !"".equals(SRGN_NM)) {
				// 三级地址--县区
				List<MDataMap> areaList = DbUp.upTable("sc_tmp").queryByWhere("code",SRGN_CD);
				if (null == areaList || areaList.size() == 0) {
					DbUp.upTable("sc_tmp").insert("code", SRGN_CD, "name", SRGN_NM,"p_code",MRGN_CD,"code_lvl","3","use_yn",SRGN_VL_YN,"show_yn",SRGN_SHOW_YN,"send_yn","Y","delay_yn",IS_DELAY);
				}else {
					MDataMap areaDataMap = areaList.get(0);
					if(SRGN_NM.equals(areaDataMap.get("name")) && SRGN_VL_YN.equals(areaDataMap.get("use_yn")) && "3".equals(areaDataMap.get("code_lvl")) 
							&& "Y".equals(areaDataMap.get("send_yn")) && SRGN_SHOW_YN.equals(areaDataMap.get("show_yn")) && MRGN_CD.equals(areaDataMap.get("p_code"))&& IS_DELAY.equals(areaDataMap.get("delay_yn"))) {
						
					}else {
						// 数据有误则更新
						DbUp.upTable("sc_tmp").dataUpdate(new MDataMap("code", SRGN_CD, "name", SRGN_NM,"p_code",MRGN_CD,"code_lvl","3","use_yn",SRGN_VL_YN,"show_yn",SRGN_SHOW_YN,"send_yn","Y","delay_yn",IS_DELAY), "name,p_code,code_lvl,use_yn,show_yn,send_yn,delay_yn", "code");
					}
				}
			}
			
			if(null != FRGN_CD && !"".equals(FRGN_CD) && null != FRGN_NM && !"".equals(FRGN_NM)) {
				// 四级地址--乡镇街道
				List<MDataMap> villageList = DbUp.upTable("sc_tmp").queryByWhere("code",FRGN_CD);
				if (null == villageList || villageList.size() == 0) {
					DbUp.upTable("sc_tmp").insert("code", FRGN_CD, "name", FRGN_NM,"p_code",SRGN_CD,"code_lvl","4","use_yn",FRGN_VL_YN,"show_yn",FRGN_SHOW_YN,"send_yn",IS_SEND,"delay_yn",IS_DELAY);
				}else {
					MDataMap villageDataMap = villageList.get(0);
					if(FRGN_NM.equals(villageDataMap.get("name")) && FRGN_VL_YN.equals(villageDataMap.get("use_yn")) && "4".equals(villageDataMap.get("code_lvl")) 
							&& IS_SEND.equals(villageDataMap.get("send_yn")) && FRGN_SHOW_YN.equals(villageDataMap.get("show_yn")) && SRGN_CD.equals(villageDataMap.get("p_code"))&& IS_DELAY.equals(villageDataMap.get("delay_yn"))) {
						
					}else {
						// 数据有误则更新
						DbUp.upTable("sc_tmp").dataUpdate(new MDataMap("code", FRGN_CD, "name", FRGN_NM,"p_code",SRGN_CD,"code_lvl","4","use_yn",FRGN_VL_YN,"show_yn",FRGN_SHOW_YN,"send_yn",IS_SEND,"delay_yn",IS_DELAY), "name,p_code,code_lvl,use_yn,show_yn,send_yn,delay_yn", "code");
					}
				}
			}
			
		}

		return result;
	}

	@Override
	public RsyncResponseOrderAddrList upResponseObject() {
		return new RsyncResponseOrderAddrList();
	}

}
