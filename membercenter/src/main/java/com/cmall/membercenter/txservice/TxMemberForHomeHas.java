package com.cmall.membercenter.txservice;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmapper.membercenter.McExtendInfoHomehasMapper;
import com.cmall.dborm.txmapper.membercenter.McExtendInfoStarMapper;
import com.cmall.dborm.txmodel.membercenter.McExtendInfoHomehas;
import com.cmall.dborm.txmodel.membercenter.McMemberInfo;
import com.cmall.membercenter.model.MLoginInput;
import com.cmall.membercenter.model.MLoginInputHomehas;
import com.cmall.membercenter.model.MReginsterResult;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 家有会员相关
 * 
 * @author srnpr
 * 
 */
public class TxMemberForHomeHas extends TxMemberBase {

	/**
	 * @param mLoginInput
	 * @return
	 */
	public MReginsterResult createMemberInfo(MLoginInputHomehas mLoginInput) {
		MReginsterResult mReginsterResult = new MReginsterResult();

		MReginsterResult mDoResult = null;

		// 开始判断用户是否存在
		if (mReginsterResult.upFlagTrue()) {

			mDoResult = doUserReginster(mLoginInput);

			mReginsterResult.inOtherResult(mDoResult);
			
			if(mDoResult.upFlagTrue())
			{
				mReginsterResult.setMemberInfo(mDoResult.getMemberInfo());
			}
			
			
			

		}

		if (mReginsterResult.upFlagTrue()) {

			McExtendInfoHomehasMapper mcExtendInfoHomehasMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_McExtendInfoHomehasMapper");

			McExtendInfoHomehas mcExtendInfoHomehas = new McExtendInfoHomehas();

			mcExtendInfoHomehas.setUid(WebHelper.upUuid());

			mcExtendInfoHomehas.setMemberCode(mDoResult.getMemberInfo()
					.getMemberCode());

			mcExtendInfoHomehas.setHomehasCode(mLoginInput.getHomeHasCode());

			mcExtendInfoHomehas.setMemberName(mLoginInput.getMemberName());

			if (StringUtils.isNotEmpty(mLoginInput.getMemberSign())) {
				mcExtendInfoHomehas.setMemberSign(mLoginInput.getMemberSign());
			}

			if (StringUtils.isNotEmpty(mLoginInput.getOldCode())) {
				mcExtendInfoHomehas.setOldCode(mLoginInput.getOldCode());
			}

			// 插入数据库
			mcExtendInfoHomehasMapper.insertSelective(mcExtendInfoHomehas);

		}

		return mReginsterResult;
	}

}
