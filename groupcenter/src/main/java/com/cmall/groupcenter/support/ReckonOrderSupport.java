package com.cmall.groupcenter.support;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.AccountRelation;
import com.cmall.groupcenter.model.GroupLevelInfo;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.baseface.IBaseInstance;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebTemp;

public class ReckonOrderSupport extends BaseClass implements IBaseInstance {

	public final static ReckonOrderSupport INSTANCE = new ReckonOrderSupport();

	/**
	 * 获取黑名单列表中的商品的价格
	 * 
	 * @param sManageCode
	 * @param sProductCode
	 * @param sOrderTime
	 * @param bProductPrice
	 * @return
	 */
	public BigDecimal upReckonProduct(String sManageCode, String sProductCode,
			String sOrderTime, BigDecimal bProductPrice) {
		BigDecimal bDecimal = bProductPrice;

		MDataMap mBlankMap = DbUp
				.upTable("gc_product_blacklist")
				.oneWhere(
						"",
						"",
						"product_code=:product_code and flag_enable=1 and begin_time<=:order_time and end_time>=:order_time",
						"product_code", sProductCode, "order_time", sOrderTime);

		// 如果黑名单中存在该商品 则按照黑名单中的比例来清分
		if (mBlankMap != null && mBlankMap.size() > 0) {

			bDecimal = bProductPrice.multiply(new BigDecimal(mBlankMap
					.get("scale_reckon")));

		}

		return bDecimal;

	}

	/**
	 * @param sLevelCode
	 * @param sManageCode
	 * @return
	 */
	public GroupLevelInfo upLevelInfo(String sLevelCode, String sManageCode) {

		GroupLevelInfo groupLevelInfo = new GroupLevelInfo();

		// 默认设置
		{
			MDataMap mNowLevelInfo = WebTemp.upTempDataMap("gc_group_level",
					"", "level_code", sLevelCode);

			groupLevelInfo.setLevelCode(mNowLevelInfo.get("level_code"));

			// 定义升级所需消费金额
			groupLevelInfo.setUpgradeConsume(new BigDecimal(mNowLevelInfo
					.get("upgrade_consume")));
			// 定义清分比例
			groupLevelInfo.setScaleReckon(new BigDecimal(mNowLevelInfo
					.get("scale_reckon")));
			// 定义升级所需社员数量
			groupLevelInfo.setUpgradeMembers(Integer.valueOf(mNowLevelInfo
					.get("upgrade_members")));
			// 定义清分深度
			groupLevelInfo.setDeepReckon(Integer.valueOf(mNowLevelInfo
					.get("deep_reckon")));
			// 定义活跃统计深度
			groupLevelInfo.setDeepConsume(Integer.valueOf(mNowLevelInfo
					.get("deep_consume")));
			// 定义级别名称
			groupLevelInfo.setLevelName(mNowLevelInfo.get("level_name"));
			// 定义级别类型
			groupLevelInfo.setLevelType(mNowLevelInfo.get("level_type"));
			// 定义下一级别编号
			groupLevelInfo.setNextLevel(mNowLevelInfo.get("next_level"));
		}

		// 加载扩展设置
		{
			MDataMap mExtendLevelInfo = WebTemp.upTempDataMap(
					"gc_level_extend", "", "level_code", sLevelCode,
					"manage_code", sManageCode, "flag_enable", "1");

			// 如果有扩展信息 则开始加载扩展信息表
			if (mExtendLevelInfo != null && mExtendLevelInfo.size() > 0) {

				groupLevelInfo.setScaleReckon(new BigDecimal(mExtendLevelInfo
						.get("extend_scale_reckon")));

			}

		}

		return groupLevelInfo;

	}

	/**
	 * 第二版获取级别信息代码 增加按照SKU设置比例的模型设计
	 * 
	 * @param sLevelCode
	 * @param sManageCode
	 * @return
	 */
	public GroupLevelInfo upLevelInfoForTwo(String sLevelCode,
			String sManageCode, String sSkuCode,String sOrderCreateTime) {

		GroupLevelInfo groupLevelInfo = new GroupLevelInfo();
		
		//订单创建时间为空，取当前时间
        if(StringUtils.isBlank(sOrderCreateTime)){
        	sOrderCreateTime=FormatHelper.upDateTime();
        }
        
		// 默认设置
		{
			MDataMap mNowLevelInfo = WebTemp.upTempDataMap("gc_group_level",
					"", "level_code", sLevelCode);

			groupLevelInfo.setLevelCode(mNowLevelInfo.get("level_code"));

			// 定义升级所需消费金额
			groupLevelInfo.setUpgradeConsume(new BigDecimal(mNowLevelInfo
					.get("upgrade_consume")));
			// 定义清分比例
			groupLevelInfo.setScaleReckon(new BigDecimal(mNowLevelInfo
					.get("scale_reckon")));
			// 定义升级所需社员数量
			groupLevelInfo.setUpgradeMembers(Integer.valueOf(mNowLevelInfo
					.get("upgrade_members")));
			// 定义清分深度
			groupLevelInfo.setDeepReckon(Integer.valueOf(mNowLevelInfo
					.get("deep_reckon")));
			// 定义活跃统计深度
			groupLevelInfo.setDeepConsume(Integer.valueOf(mNowLevelInfo
					.get("deep_consume")));
			// 定义级别名称
			groupLevelInfo.setLevelName(mNowLevelInfo.get("level_name"));
			// 定义级别类型
			groupLevelInfo.setLevelType(mNowLevelInfo.get("level_type"));
			// 定义下一级别编号
			groupLevelInfo.setNextLevel(mNowLevelInfo.get("next_level"));
		}

		// 加载扩展设置
		{
			MDataMap mExtendLevelInfo = WebTemp.upTempDataMap(
					"gc_level_extend", "", "level_code", sLevelCode,
					"manage_code", sManageCode, "flag_enable", "1");

			// 如果有扩展信息 则开始加载扩展信息表
			if (mExtendLevelInfo != null && mExtendLevelInfo.size() > 0) {

				groupLevelInfo.setScaleReckon(new BigDecimal(mExtendLevelInfo
						.get("extend_scale_reckon")));

			}

		}

		// 开始加载SKU上的清分比例
		{
			MDataMap mSkuSetMap = DbUp
					.upTable("gc_sku_rebate_scale")
					.oneWhere(
							"rebate_scale",
							"",
							"sku_code=:sku_code and flag_enable=1 and status=1 and start_time<=:datenow and end_time>=:datenow",
							"sku_code", sSkuCode, "datenow",
							sOrderCreateTime);

			if (mSkuSetMap != null) {

				String[] sRebateSet = mSkuSetMap.get("rebate_scale").split(",");

				String[] sLevels = GroupConst.RECKON_LEVEL_LIST.split(",");

				for (int i = 0, j = sLevels.length; i < j; i++) {

					// 判断如果级别是序列中且设置中的比例长度小于级别的长度
					if (sLevels[i].equals(sLevelCode) && i < sRebateSet.length
							&& NumberUtils.isNumber(sRebateSet[i])) {

						BigDecimal bRebate = new BigDecimal(sRebateSet[i])
								.divide(new BigDecimal("100"));

						if (bRebate.compareTo(BigDecimal.ZERO) >= 0) {
							groupLevelInfo.setScaleReckon(bRebate);
						}
					}

				}

			}

		}

		return groupLevelInfo;

	}
	
	/**
	 * 第三版获取级别、范围信息代码 增加按照商家SKU设置比例及范围的模型设计
	 * 
	 * @param sLevelCode
	 * @param sManageCode
	 * @return
	 */
	public GroupLevelInfo upLevelInfoForThird(String sLevelCode,
			String sManageCode, String sSkuCode,String sOrderCreateTime,String relationLevel) {
		//定义的参数一样，暂时匹配下
		String rangeString="";
		if(relationLevel.endsWith("0")){
			rangeString="4497472500020001";
		}
		else if(relationLevel.endsWith("1")){
			rangeString="4497472500020002";
		}
		else if(relationLevel.endsWith("2")){
			rangeString="4497472500020003";
		}

		GroupLevelInfo groupLevelInfo = new GroupLevelInfo();
		
		//订单创建时间为空，取当前时间
        if(StringUtils.isBlank(sOrderCreateTime)){
        	sOrderCreateTime=FormatHelper.upDateTime();
        }
        
		// 默认设置
		{
			MDataMap mNowLevelInfo = WebTemp.upTempDataMap("gc_group_level",
					"", "level_code", sLevelCode);

			groupLevelInfo.setLevelCode(mNowLevelInfo.get("level_code"));

			// 定义升级所需消费金额
			groupLevelInfo.setUpgradeConsume(new BigDecimal(mNowLevelInfo
					.get("upgrade_consume")));
			// 定义清分比例
			//改商家后没有默认比例了,设为0,特殊级别还是特殊级别
			if(mNowLevelInfo.get("level_code").equals("4497465200010006")){
				groupLevelInfo.setScaleReckon(new BigDecimal(mNowLevelInfo.get("scale_reckon")));
			}
			else{
				groupLevelInfo.setScaleReckon(BigDecimal.ZERO);
			}
			
			
			
			// 定义升级所需社员数量
			groupLevelInfo.setUpgradeMembers(Integer.valueOf(mNowLevelInfo
					.get("upgrade_members")));
			// 定义清分深度
			groupLevelInfo.setDeepReckon(Integer.valueOf(mNowLevelInfo
					.get("deep_reckon")));
			// 定义活跃统计深度
			groupLevelInfo.setDeepConsume(Integer.valueOf(mNowLevelInfo
					.get("deep_consume")));
			// 定义级别名称
			groupLevelInfo.setLevelName(mNowLevelInfo.get("level_name"));
			// 定义级别类型
			groupLevelInfo.setLevelType(mNowLevelInfo.get("level_type"));
			// 定义下一级别编号
			groupLevelInfo.setNextLevel(mNowLevelInfo.get("next_level"));
		}
		
		//取商家默认比例
		String traderCode="";
		{   MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",sManageCode);
		    MDataMap manageMap=null;
		    if(appMap!=null&&appMap.get("trade_code")!=null){
		    	manageMap=DbUp.upTable("gc_trader_rebate").one("trader_code",appMap.get("trade_code"),"delete_flag","0");
		    	traderCode=appMap.get("trade_code");
		    }
			
			if(manageMap!=null){
				
				String rebateRange=manageMap.get("rebate_range");
				
				String[] sRebateSet = manageMap.get("rebate_rate").split(",");

				String[] sLevels = GroupConst.RECKON_LEVEL_LIST.split(",");

				for (int i = 0, j = sLevels.length; i < j; i++) {

					// 判断如果级别是序列中且设置中的比例长度小于级别的长度
					if (sLevels[i].equals(sLevelCode) && i < sRebateSet.length
							&& NumberUtils.isNumber(sRebateSet[i])) {

						BigDecimal bRebate = new BigDecimal(sRebateSet[i])
								.divide(new BigDecimal("100"));

						if (bRebate.compareTo(BigDecimal.ZERO) >= 0) {
							//需在返现范围内
							if(rebateRange.contains(rangeString)){
								groupLevelInfo.setScaleReckon(bRebate);
							}
						}
					}

				}
			}
		}

		//商家应用比例
		MDataMap appMap =null;
		if(StringUtils.isNotBlank(traderCode)){
			appMap=DbUp.upTable("gc_app_rebate_scale")
			.oneWhere(
					"rebate_scale,rebate_range",
					"",
					"trader_code=:traderCode and  app_code=:app_code and delete_flag=1 and flag_enable=1 and start_time<=:datenow and end_time>=:datenow",
					"app_code", sManageCode, "datenow",sOrderCreateTime,"traderCode",traderCode);
		}
		
		if(appMap != null){
			
			String rebateRange=appMap.get("rebate_range");

			String[] sRebateSet = appMap.get("rebate_scale").split(",");

			String[] sLevels = GroupConst.RECKON_LEVEL_LIST.split(",");

			for (int i = 0, j = sLevels.length; i < j; i++) {

				// 判断如果级别是序列中且设置中的比例长度小于级别的长度
				if (sLevels[i].equals(sLevelCode) && i < sRebateSet.length
						&& NumberUtils.isNumber(sRebateSet[i])) {

					BigDecimal bRebate = new BigDecimal(sRebateSet[i])
							.divide(new BigDecimal("100"));

					if (bRebate.compareTo(BigDecimal.ZERO) >= 0) {
						//需在返现范围内
						if(rebateRange.contains(rangeString)){
							groupLevelInfo.setScaleReckon(bRebate);
						}
						else{
							groupLevelInfo.setScaleReckon(BigDecimal.ZERO);
						}
					}
				}
			}
		}
		
		// 开始加载SKU上的清分比例
		MDataMap mSkuSetMap = null;
		if(StringUtils.isNotBlank(traderCode)){
			mSkuSetMap=DbUp.upTable("gc_sku_rebate_scale")
			.oneWhere(
					"rebate_scale,rebate_range",
					"",
					"trader_code=:traderCode and  app_code=:app_code and  sku_code=:sku_code and flag_enable=1 and status=1 and start_time<=:datenow and end_time>=:datenow",
					"app_code", sManageCode,"sku_code", sSkuCode, "datenow",
					sOrderCreateTime,"traderCode",traderCode);
		}
		
		if (mSkuSetMap != null) {
			
			String rebateRange=mSkuSetMap.get("rebate_range");

			String[] sRebateSet = mSkuSetMap.get("rebate_scale").split(",");

			String[] sLevels = GroupConst.RECKON_LEVEL_LIST.split(",");

			for (int i = 0, j = sLevels.length; i < j; i++) {

				// 判断如果级别是序列中且设置中的比例长度小于级别的长度
				if (sLevels[i].equals(sLevelCode) && i < sRebateSet.length
						&& NumberUtils.isNumber(sRebateSet[i])) {

					BigDecimal bRebate = new BigDecimal(sRebateSet[i])
							.divide(new BigDecimal("100"));

					if (bRebate.compareTo(BigDecimal.ZERO) >= 0) {
						//需在返现范围内
						if(rebateRange.contains(rangeString)){
							groupLevelInfo.setScaleReckon(bRebate);
						}
						else{
							groupLevelInfo.setScaleReckon(BigDecimal.ZERO);
						}
						
					}
				}

			}

		}
			
		return groupLevelInfo;

	}
	
	/**
	 * 预返利比例
	 * @param sLevelCode
	 * @param sManageCode
	 * @return
	 */
	public GroupLevelInfo upRebateLevelInfo(String sLevelCode, String sManageCode,String relationLevel) {

		GroupLevelInfo groupLevelInfo = new GroupLevelInfo();
		
		//定义的参数一样，暂时匹配下
		String rangeString="";
		if(relationLevel.endsWith("0")){
			rangeString="4497472500020001";
		}
		else if(relationLevel.endsWith("1")){
			rangeString="4497472500020002";
		}
		else if(relationLevel.endsWith("2")){
			rangeString="4497472500020003";
		}

		// 默认设置
		{
			MDataMap mNowLevelInfo = WebTemp.upTempDataMap("gc_group_level",
					"", "level_code", sLevelCode);

			groupLevelInfo.setLevelCode(mNowLevelInfo.get("level_code"));

			// 定义升级所需消费金额
			groupLevelInfo.setUpgradeConsume(new BigDecimal(mNowLevelInfo
					.get("upgrade_consume")));
			// 定义清分比例
			if(mNowLevelInfo.get("level_code").equals("4497465200010006")){
				groupLevelInfo.setScaleReckon(new BigDecimal(mNowLevelInfo.get("scale_reckon")));
			}
			else{
				groupLevelInfo.setScaleReckon(BigDecimal.ZERO);
			}
			// 定义升级所需社员数量
			groupLevelInfo.setUpgradeMembers(Integer.valueOf(mNowLevelInfo
					.get("upgrade_members")));
			// 定义清分深度
			groupLevelInfo.setDeepReckon(Integer.valueOf(mNowLevelInfo
					.get("deep_reckon")));
			// 定义活跃统计深度
			groupLevelInfo.setDeepConsume(Integer.valueOf(mNowLevelInfo
					.get("deep_consume")));
			// 定义级别名称
			groupLevelInfo.setLevelName(mNowLevelInfo.get("level_name"));
			// 定义级别类型
			groupLevelInfo.setLevelType(mNowLevelInfo.get("level_type"));
			// 定义下一级别编号
			groupLevelInfo.setNextLevel(mNowLevelInfo.get("next_level"));
		}

		// 加载扩展设置
		{
			MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",sManageCode);
			MDataMap traderLevelInfo=null;
			if(appMap!=null&&appMap.get("trade_code")!=null){
				traderLevelInfo=DbUp.upTable("gc_trader_rebate").one("trader_code",appMap.get("trade_code"),"delete_flag","0");
			}
			
			// 如果有扩展信息 则开始加载扩展信息表
			if (traderLevelInfo!=null&&traderLevelInfo.get("rebate_rate")!=null) {
				String[] sRebateSet = traderLevelInfo.get("rebate_rate").split(",");
				String rebateRange=traderLevelInfo.get("rebate_range");
				String[] sLevels = GroupConst.RECKON_LEVEL_LIST.split(",");

				for (int i = 0, j = sLevels.length; i < j; i++) {

					// 判断如果级别是序列中且设置中的比例长度小于级别的长度
					if (sLevels[i].equals(sLevelCode) && i < sRebateSet.length
							&& NumberUtils.isNumber(sRebateSet[i])) {

						BigDecimal bRebate = new BigDecimal(sRebateSet[i])
								.divide(new BigDecimal("100"));

						if (bRebate.compareTo(BigDecimal.ZERO) >= 0) {
							//需在返现范围内
							if(rebateRange.contains(rangeString)){
								groupLevelInfo.setScaleReckon(bRebate);
							}
							
						}
					}

				}

			}

		}

		return groupLevelInfo;

	}
	

	/**
	 * 预返利比例 第二版返利比例规则与清分时的规则相同,获取级别,范围信息代码,按照商家SKU设置比例,商家应用比例,及范围的模型设计
	 * @param sLevelCode
	 * @param sManageCode
	 * @param sSkuCode
	 * @param sOrderCreateTime
	 * @param relationLevel
	 * @return
	 */
	public GroupLevelInfo upRebateLevelInfoTwo(String sLevelCode, String sManageCode,String sSkuCode,String sOrderCreateTime,String relationLevel) {

		GroupLevelInfo groupLevelInfo = new GroupLevelInfo();
		
		//定义的参数一样，暂时匹配下
		String rangeString="";
		if(relationLevel.endsWith("0")){
			rangeString="4497472500020001";
		}
		else if(relationLevel.endsWith("1")){
			rangeString="4497472500020002";
		}
		else if(relationLevel.endsWith("2")){
			rangeString="4497472500020003";
		}

		//订单创建时间为空，取当前时间
        if(StringUtils.isBlank(sOrderCreateTime)){
        	sOrderCreateTime=FormatHelper.upDateTime();
        }
        
		// 默认设置
		{
			MDataMap mNowLevelInfo = WebTemp.upTempDataMap("gc_group_level",
					"", "level_code", sLevelCode);

			groupLevelInfo.setLevelCode(mNowLevelInfo.get("level_code"));

			// 定义升级所需消费金额
			groupLevelInfo.setUpgradeConsume(new BigDecimal(mNowLevelInfo
					.get("upgrade_consume")));
			// 定义清分比例
			if(mNowLevelInfo.get("level_code").equals("4497465200010006")){
				groupLevelInfo.setScaleReckon(new BigDecimal(mNowLevelInfo.get("scale_reckon")));
			}
			else{
				groupLevelInfo.setScaleReckon(BigDecimal.ZERO);
			}
			// 定义升级所需社员数量
			groupLevelInfo.setUpgradeMembers(Integer.valueOf(mNowLevelInfo
					.get("upgrade_members")));
			// 定义清分深度
			groupLevelInfo.setDeepReckon(Integer.valueOf(mNowLevelInfo
					.get("deep_reckon")));
			// 定义活跃统计深度
			groupLevelInfo.setDeepConsume(Integer.valueOf(mNowLevelInfo
					.get("deep_consume")));
			// 定义级别名称
			groupLevelInfo.setLevelName(mNowLevelInfo.get("level_name"));
			// 定义级别类型
			groupLevelInfo.setLevelType(mNowLevelInfo.get("level_type"));
			// 定义下一级别编号
			groupLevelInfo.setNextLevel(mNowLevelInfo.get("next_level"));
		}

		// 取商家默认比例
		String traderCode="";
		{
			MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",sManageCode);
			MDataMap traderLevelInfo=null;
			if(appMap!=null&&appMap.get("trade_code")!=null){
				traderLevelInfo=DbUp.upTable("gc_trader_rebate").one("trader_code",appMap.get("trade_code"),"delete_flag","0");
				traderCode=appMap.get("trade_code");
			}
			
			// 如果有扩展信息 则开始加载扩展信息表
			if (traderLevelInfo!=null&&traderLevelInfo.get("rebate_rate")!=null) {
				String[] sRebateSet = traderLevelInfo.get("rebate_rate").split(",");
				String rebateRange=traderLevelInfo.get("rebate_range");
				String[] sLevels = GroupConst.RECKON_LEVEL_LIST.split(",");

				for (int i = 0, j = sLevels.length; i < j; i++) {

					// 判断如果级别是序列中且设置中的比例长度小于级别的长度
					if (sLevels[i].equals(sLevelCode) && i < sRebateSet.length
							&& NumberUtils.isNumber(sRebateSet[i])) {

						BigDecimal bRebate = new BigDecimal(sRebateSet[i])
								.divide(new BigDecimal("100"));

						if (bRebate.compareTo(BigDecimal.ZERO) >= 0) {
							//需在返现范围内
							if(rebateRange.contains(rangeString)){
								groupLevelInfo.setScaleReckon(bRebate);
							}
						}
					}
				}
			}
		}
		
		// 取商家应用比例
		{
			MDataMap appMap =null;
			if(StringUtils.isNotBlank(traderCode)){
				appMap=DbUp.upTable("gc_app_rebate_scale")
				.oneWhere(
						"rebate_scale,rebate_range",
						"",
						"trader_code=:traderCode and app_code=:app_code and delete_flag=1 and flag_enable=1 and start_time<=:datenow and end_time>=:datenow",
						"app_code", sManageCode, "datenow",sOrderCreateTime,"traderCode",traderCode);
			}
			
			if(appMap != null){
				
				String rebateRange=appMap.get("rebate_range");
	
				String[] sRebateSet = appMap.get("rebate_scale").split(",");
	
				String[] sLevels = GroupConst.RECKON_LEVEL_LIST.split(",");
	
				for (int i = 0, j = sLevels.length; i < j; i++) {
	
					// 判断如果级别是序列中且设置中的比例长度小于级别的长度
					if (sLevels[i].equals(sLevelCode) && i < sRebateSet.length
							&& NumberUtils.isNumber(sRebateSet[i])) {
	
						BigDecimal bRebate = new BigDecimal(sRebateSet[i])
								.divide(new BigDecimal("100"));
	
						if (bRebate.compareTo(BigDecimal.ZERO) >= 0) {
							//需在返现范围内
							if(rebateRange.contains(rangeString)){
								groupLevelInfo.setScaleReckon(bRebate);
							}
							else{
								groupLevelInfo.setScaleReckon(BigDecimal.ZERO);
							}
						}
					}
				}
			}
		}
		
		// 开始加载SKU上的清分比例
		{
			MDataMap mSkuSetMap = null;
			if(StringUtils.isNotBlank(traderCode)){
				mSkuSetMap=DbUp.upTable("gc_sku_rebate_scale")
				.oneWhere(
						"rebate_scale,rebate_range",
						"",
						"trader_code=:traderCode and app_code=:app_code and sku_code=:sku_code and flag_enable=1 and status=1 and start_time<=:datenow and end_time>=:datenow",
						"app_code", sManageCode,"sku_code", sSkuCode, "datenow",
						sOrderCreateTime,"traderCode",traderCode);
			}
			
			if (mSkuSetMap != null) {
				
				String rebateRange=mSkuSetMap.get("rebate_range");
	
				String[] sRebateSet = mSkuSetMap.get("rebate_scale").split(",");
	
				String[] sLevels = GroupConst.RECKON_LEVEL_LIST.split(",");
	
				for (int i = 0, j = sLevels.length; i < j; i++) {
	
					// 判断如果级别是序列中且设置中的比例长度小于级别的长度
					if (sLevels[i].equals(sLevelCode) && i < sRebateSet.length
							&& NumberUtils.isNumber(sRebateSet[i])) {
	
						BigDecimal bRebate = new BigDecimal(sRebateSet[i])
								.divide(new BigDecimal("100"));
	
						if (bRebate.compareTo(BigDecimal.ZERO) >= 0) {
							//需在返现范围内
							if(rebateRange.contains(rangeString)){
								groupLevelInfo.setScaleReckon(bRebate);
							}
							else{
								groupLevelInfo.setScaleReckon(BigDecimal.ZERO);
							}
						}
					}
				}
			}
		}
		
		return groupLevelInfo;

	}
	
	/**
	 * 预返利比例 第一版返 通过层级好友在此商户的消费金额获取返利比例
	 * @param sLevelCode
	 * @param sManageCode
	 * @param sSkuCode
	 * @param sOrderCreateTime
	 * @param relationLevel
	 * @param listRelations 层级好友
	 * @param traderCode 订单所属商户编号
	 * @param sOrderCode 订单号
	 * @param orderMoney 订单金额
	 * @return
	 */
	public GroupLevelInfo upRebateScaleByMoneyForOne(String sManageCode,String sSkuCode,String sOrderCreateTime,String relationLevel,
			String sAccountCode,String sOrderCode, BigDecimal orderMoney) {

		GroupLevelInfo groupLevelInfo = new GroupLevelInfo();
		groupLevelInfo.setScaleReckon(BigDecimal.ZERO);//默认比例为0
		String rebateType = "";//商户的返利方式
		
		//定义的参数一样，暂时匹配下
		String rangeString="";
		if(relationLevel.endsWith("0")){
			rangeString="4497472500020001";
		}
		else if(relationLevel.endsWith("1")){
			rangeString="4497472500020002";
		}
		else if(relationLevel.endsWith("2")){
			rangeString="4497472500020003";
		}

		//订单创建时间为空，取当前时间
        if(StringUtils.isBlank(sOrderCreateTime)){
        	sOrderCreateTime=FormatHelper.upDateTime();
        }

		// 取商家默认比例
		String traderCode="";
		{
			MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",sManageCode);
			MDataMap traderLevelInfo=null;
			if(appMap!=null&&appMap.get("trade_code")!=null){
				traderCode=appMap.get("trade_code");
				traderLevelInfo=DbUp.upTable("gc_trader_rebate").one("trader_code",appMap.get("trade_code"),"delete_flag","0");
			}
			
			// 如果有扩展信息 则开始加载扩展信息表
			if (traderLevelInfo!=null && StringUtils.isNotBlank(traderCode) 
					&& StringUtils.isNotBlank(traderLevelInfo.get("money_rebate_grade")) 
					&& StringUtils.isNotBlank(traderLevelInfo.get("money_rebate_scale"))) {
				
				String[] sRebateGrade = traderLevelInfo.get("money_rebate_grade").split(",");
				String[] sRebateScale = traderLevelInfo.get("money_rebate_scale").split(",");
				String rebateRange = traderLevelInfo.get("money_rebate_range");
				
				if(sRebateGrade.length == sRebateScale.length){
					//计算返利比例
					groupLevelInfo.setScaleReckon(ShowScaleRebate(sAccountCode,sOrderCode,traderCode,orderMoney,sRebateGrade,sRebateScale,rebateRange,rangeString));
				}else{
					groupLevelInfo.setScaleReckon(BigDecimal.ZERO);
				}
			}
		}
		
		//获取商户返利方式
		if(StringUtils.isNotBlank(traderCode)){
			MDataMap tMap=DbUp.upTable("gc_trader_info").one("trader_code",traderCode);
			if(tMap != null && StringUtils.isNotBlank(tMap.get("rebate_type"))){
				rebateType = String.valueOf(tMap.get("rebate_type"));
			}
		}
		
		// 取商家应用比例
		{
			MDataMap appMap =null;
			if(StringUtils.isNotBlank(traderCode) && StringUtils.isNotBlank(rebateType)){
				appMap=DbUp.upTable("gc_app_rebate_scale_money")
				.oneWhere(
						"money_rebate_grade,money_rebate_scale,money_rebate_range",
						"",
						"trader_code=:traderCode and app_code=:app_code and delete_flag=1 and flag_enable=1 and rebate_type=:rebate_type and (start_time<=:datenow and end_time>=:datenow)",
						"traderCode",traderCode,"app_code", sManageCode,"rebate_type",rebateType,"datenow",sOrderCreateTime);
			}
			
			if (appMap!=null && StringUtils.isNotBlank(appMap.get("money_rebate_grade")) && StringUtils.isNotBlank(appMap.get("money_rebate_scale"))){
				String[] sRebateGrade = appMap.get("money_rebate_grade").split(",");
				String[] sRebateScale = appMap.get("money_rebate_scale").split(",");
				String rebateRange = appMap.get("money_rebate_range");
				if(sRebateGrade.length == sRebateScale.length){
					//计算返利比例
					groupLevelInfo.setScaleReckon(ShowScaleRebate(sAccountCode,sOrderCode,traderCode,orderMoney,sRebateGrade,sRebateScale,rebateRange,rangeString));
				}else{
					groupLevelInfo.setScaleReckon(BigDecimal.ZERO);
				}
			}
			
		}
		
		
		// 开始加载SKU上的清分比例
		{
			MDataMap mSkuSetMap = null;
			if(StringUtils.isNotBlank(traderCode) && StringUtils.isNotBlank(rebateType)){
				mSkuSetMap=DbUp.upTable("gc_sku_rebate_scale_money")
				.oneWhere(
						"money_rebate_grade,money_rebate_scale,money_rebate_range",
						"",
						"trader_code=:traderCode and app_code=:app_code and sku_code=:sku_code and flag_enable=1 and status=1 "
						+ "and rebate_type=:rebate_type and (start_time<=:datenow and end_time>=:datenow)",
						"traderCode",traderCode,"app_code", sManageCode,"sku_code", sSkuCode,"rebate_type",rebateType, "datenow",sOrderCreateTime);
			}
			if (mSkuSetMap!=null && StringUtils.isNotBlank(mSkuSetMap.get("money_rebate_grade")) && StringUtils.isNotBlank(mSkuSetMap.get("money_rebate_scale"))){
				String[] sRebateGrade = mSkuSetMap.get("money_rebate_grade").split(",");
				String[] sRebateScale = mSkuSetMap.get("money_rebate_scale").split(",");
				String rebateRange = mSkuSetMap.get("money_rebate_range");
				if(sRebateGrade.length == sRebateScale.length){
					//计算返利比例
					groupLevelInfo.setScaleReckon(ShowScaleRebate(sAccountCode,sOrderCode,traderCode,orderMoney,sRebateGrade,sRebateScale,rebateRange,rangeString));
				}else{
					groupLevelInfo.setScaleReckon(BigDecimal.ZERO);
				}
			}
			
		}
		
		return groupLevelInfo;

	}

	/**
	 * 计算返利比例
	 * @param sAccountCode 账户编号
	 * @param sOrderCode 订单编号
	 * @param traderCode 订单所属商户编号
	 * @param orderMoney 订单金额
	 * @param sRebateGrade 设定的金额范围
	 * @param sRebateScale 范围对应的比例
	 * @param rebateRange 返利层级
	 * @param rangeString 账户所在的层级
	 * @return 计算得出的返利比例
	 */
	private BigDecimal ShowScaleRebate(String sAccountCode, String sOrderCode,
			String traderCode, BigDecimal orderMoney, String[] sRebateGrade, String[] sRebateScale,
			String rebateRange, String rangeString) {
		
		BigDecimal bRebateScale = BigDecimal.ZERO;
		boolean upLevelFlag = false;//是否升级标记，默认否
		Calendar cal = Calendar.getInstance();//避免凌晨计算时出现日期不一致 所以此处统一声明
		//统计从本月初1号至现在 包含自己,下级,下下级,但是不包含本次订单的总消费
		BigDecimal sumMonthMoney=GetFridendMonthConsume(sAccountCode,sOrderCode,traderCode,cal);
		
		//统计上月整月 包含自己,下级,下下级的总消费
		BigDecimal sumLastMonthMoney=GetFridendAgoMonthConsume(sAccountCode,traderCode,cal,-1);
		
		//判断是否升级(累计本次的订单的消费金额),如果达到升级标准 则按照最新的比例返利
		BigDecimal sumNewMoney = sumMonthMoney.add(orderMoney);
		
		//获取上月最终的返利比例 作为本月开始的返利比例
		HashMap<BigDecimal, BigDecimal> bScaleMap = GetRebateScale(sRebateGrade,sRebateScale,sumLastMonthMoney);
		Iterator<Entry<BigDecimal, BigDecimal>> reIt = bScaleMap.entrySet().iterator();
		while(reIt.hasNext()){
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) reIt.next();
			Object key = entry.getKey();//最高消费额
			Object val = entry.getValue();//比例
			BigDecimal lastHighMoney = new BigDecimal(String.valueOf(key));
			bRebateScale = new BigDecimal(String.valueOf(val));//上个月最终的返利比例
			//如果本月消费金额+订单金额>上月级别的最大金额 则升级,使用用升级后的新比例返利
			if(sumNewMoney.compareTo(lastHighMoney) >0){
				upLevelFlag = true;//升级
			}
			break;
		}
		
		//升级   获取升级后的返利比例
		if(upLevelFlag){
			HashMap<BigDecimal, BigDecimal> bScaleNewMap = GetRebateScale(sRebateGrade,sRebateScale,sumNewMoney);
			Iterator<Entry<BigDecimal, BigDecimal>> reItNew = bScaleNewMap.entrySet().iterator();
			while(reItNew.hasNext()){
				@SuppressWarnings("rawtypes")
				Map.Entry entryNew = (Map.Entry) reItNew.next();
				Object newVal = entryNew.getValue();
				bRebateScale = new BigDecimal(String.valueOf(newVal));//最新的返利比例
				break;
			}
		}
		
		BigDecimal bRebate = bRebateScale.divide(new BigDecimal("100"));
		if(bRebate.compareTo(BigDecimal.ZERO) > 0){
			//需在返现范围内
			if(rebateRange.contains(rangeString)){
				return bRebate;
			}
		}
		return BigDecimal.ZERO;//没有计算出返利比例 则返回0
	}

	/**
	 * 统计指定月份(上月，上上月)的消费金额
	 * @param sAccountCode
	 * @param cal 
	 * @param sOrderCode
	 * @param traderCode
	 * @param n
	 * @return
	 */
	private BigDecimal GetFridendAgoMonthConsume(String sAccountCode,String sTraderCode, Calendar cal, int n) {
		
		BigDecimal acTotalConsume = BigDecimal.ZERO;
		try{
			//获取月第一天
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			if(cal==null){
				Calendar cal1 = Calendar.getInstance();
				cal=cal1;
			}
			
			cal.add(Calendar.MONTH, n);
			cal.set(Calendar.DAY_OF_MONTH,1);//设置为1号
			String firstDay = format.format(cal.getTime());
			//获取月最后一天
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			String lastDay = format.format(cal.getTime());
			
			String acTotalConsumeSql = "select IFNULL(sum(consume_money),0) as acTotalConsume "
					+ " from gc_active_log "
					+ " where account_code =:account_code "
					+ " and trader_code =:trader_code "
					+ " and (date(active_time) >=:first_day and date(active_time) <=:last_day) "
					+ " and consume_money>=0 ";
			
			MDataMap acTotalConsumeMap = new MDataMap();
			acTotalConsumeMap.put("account_code", sAccountCode);
			acTotalConsumeMap.put("trader_code", sTraderCode);
			acTotalConsumeMap.put("first_day", firstDay);
            acTotalConsumeMap.put("last_day", lastDay);
			
			List<Map<String, Object>> acTotalConsumeList=DbUp.upTable("gc_active_log").dataSqlList(acTotalConsumeSql, acTotalConsumeMap);
			if(acTotalConsumeList != null){
				acTotalConsume = new BigDecimal(String.valueOf((acTotalConsumeList.get(0).get("acTotalConsume"))));
			}
		}catch(Exception e){
			acTotalConsume = BigDecimal.ZERO;
		}
		return acTotalConsume;
	}

	/**
	 * 通过判断消费金额来获取返利比例
	 * @param sRebateGrade 设置的返现金额
	 * @param sRebateScale 返现金额对应的返现比例
	 * @param sumMoney 消费金额
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private HashMap<BigDecimal, BigDecimal> GetRebateScale(String[] sRebateGrade,
			String[] sRebateScale, BigDecimal sumMoney) {
		//默认返利比例
		Map<BigDecimal,BigDecimal> scaleMap = new HashMap<BigDecimal,BigDecimal>();
		BigDecimal bRebate = BigDecimal.ZERO;
		for(int i=0;i<sRebateGrade.length;i++){
			String gradeAry = sRebateGrade[i];
			String gradeRegion[] = gradeAry.split("-");//消费区间
			BigDecimal low = new BigDecimal(0);//消费区间低值
			BigDecimal high = new BigDecimal(0);//消费区间高值
 			if(gradeRegion.length >0 ){
 				if(gradeRegion.length == 1){
					low=new BigDecimal(gradeRegion[0]);
					//消费金额>=最后一个区间值
					if((sumMoney.compareTo(low)>=0)){
						bRebate = new BigDecimal(sRebateScale[i]);
						scaleMap.put(low, bRebate);
						break;
					}
				}else{
					low=new BigDecimal(gradeRegion[0]);
					high=new BigDecimal(gradeRegion[1]);
					//如果满足区间判断 low<=sumMoney<high,则获取比例
					if((sumMoney.compareTo(low)>=0) && (sumMoney.compareTo(high)<0) ){
						bRebate = new BigDecimal(sRebateScale[i]);
						scaleMap.put(high, bRebate);
						break;
					}
				}
			}
		}
		return (HashMap) scaleMap;
	}

	/**
	 * 账户总消费(统计从月初至现在 包含自己,下级,下下级,但是不包含本次订单的总消费)
	 * @param sAccountCode
	 * @param sOrderCode 本次订单编号
	 * @param cal 
	 * @param traderCode 订单所属商户编号
	 * @return
	 */
	private BigDecimal GetFridendMonthConsume(String sAccountCode,String sOrderCode, String sTraderCode, Calendar cal) {
		
		BigDecimal acTotalConsume = BigDecimal.ZERO;
		
		try{
			//获取月第一天
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			if(cal==null){
				Calendar cal1 = Calendar.getInstance();
				cal=cal1;
			}
			
			cal.add(Calendar.MONTH, 0);
			cal.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
			String firstDay = format.format(cal.getTime());
			//获取月最后一天
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			String lastDay = format.format(cal.getTime());
			
			String acTotalConsumeSql = "select IFNULL(sum(consume_money),0) as acTotalConsume "
					+ " from gc_active_log "
					+ " where account_code =:account_code "
					+ " and trader_code =:trader_code "
					+ "	and order_code !=:order_code "
					+ " and (date(active_time) >=:first_day and date(active_time) <=:last_day) "
					+ " and consume_money>=0 ";
			
			MDataMap acTotalConsumeMap = new MDataMap();
			acTotalConsumeMap.put("account_code", sAccountCode);
			acTotalConsumeMap.put("trader_code", sTraderCode);
			acTotalConsumeMap.put("order_code", sOrderCode);
			acTotalConsumeMap.put("first_day", firstDay);
			acTotalConsumeMap.put("last_day", lastDay);
			
			List<Map<String, Object>> acTotalConsumeList=DbUp.upTable("gc_active_log").dataSqlList(acTotalConsumeSql, acTotalConsumeMap);
			if(acTotalConsumeList != null){
				acTotalConsume = new BigDecimal(String.valueOf((acTotalConsumeList.get(0).get("acTotalConsume"))));
			}
		}catch(Exception e){
			acTotalConsume = BigDecimal.ZERO;
		}

		return acTotalConsume;
	}

}
