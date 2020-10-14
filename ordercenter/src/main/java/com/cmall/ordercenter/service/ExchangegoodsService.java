package com.cmall.ordercenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.ApiAddExchangegoodsInput;
import com.cmall.ordercenter.model.ApiGetExchangegoodsResult;
import com.cmall.ordercenter.model.ExchangegoodsDetailModel;
import com.cmall.ordercenter.model.ExchangegoodsDetailModelChild;
import com.cmall.ordercenter.model.ExchangegoodsModelChild;
import com.cmall.ordercenter.model.ExchangegoodsStatusLogModel;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 
 * 项目名称：ordercenter 
 * 类名称：     ExchangegoodsService 
 * 类描述：     换货处理逻辑
 * 创建人：     gaoy  
 * 创建时间：2013年9月10日下午8:02:39 
 * 修改人：     gaoy
 * 修改时间：2013年9月10日下午8:02:39
 * 修改备注：  
 * @version
 *
 */
public class ExchangegoodsService extends BaseClass{
	
	/**
	 * 以买家编号为条件查询换货信息
	 * @param buyerCode 买家编号
	 * @return 换货信息对象
	 */
	public List<ExchangegoodsModelChild> searchExchangegoods(String buyerCode){
		
		//换货信息
		List<ExchangegoodsModelChild> exGoodList = new ArrayList<ExchangegoodsModelChild>();
		//换货主信息
		ExchangegoodsModelChild exGoods = new ExchangegoodsModelChild();
		
		//查询该用户所有的换货单主信息
		MDataMap mp = new MDataMap();
		mp.put("buyer_code", buyerCode);
		List<MDataMap> exgData = new ArrayList<MDataMap>();
		exgData = DbUp.upTable("oc_exchange_goods").queryAll("", "", "", mp);
		
		for(int i=0 ;i<exgData.size();i++){
			//换货主信息
			exGoods =  new SerializeSupport<ExchangegoodsModelChild>().serialize(exgData.get(i),new ExchangegoodsModelChild());
			//换货单号
			String tempExchangeNo  = exgData.get(i).get("exchange_no");
			List<MDataMap> exGoodsDetail = getExgoodsDetailByExchangeCode(tempExchangeNo);
			//明细信息列表
			List<ExchangegoodsDetailModelChild> segDetialList = new ArrayList<ExchangegoodsDetailModelChild>();
			for(int j=0;j<exGoodsDetail.size();j++)
			{
				segDetialList.add(new SerializeSupport<ExchangegoodsDetailModelChild>().serialize(exGoodsDetail.get(j),new ExchangegoodsDetailModelChild()));
			}
			exGoods.setExgDetailListChild(segDetialList);
			exGoodList.add(exGoods);
		}
		
		return exGoodList;
	}
	
	/**
	 * 根据换货单号获取换货详细信息
	 * @param tempExchangeCode 换货单号
	 * @return
	 */
	private List<MDataMap> getExgoodsDetailByExchangeCode(String tempExchangeCode) {
		MDataMap mp = new MDataMap();
		mp.put("exchange_no", tempExchangeCode);
		return  DbUp.upTable("oc_exchange_goods_detail").queryAll("", "", "", mp);
	}


	/**
	 * 增加换货信息
	 * @param edm 换货信息
	 * @return 换货信息对象
	 */
	public ApiGetExchangegoodsResult addExchangegoods(ApiAddExchangegoodsInput egi){
		
		//返回的换货信息对象
		ExchangegoodsModelChild egmc = new ExchangegoodsModelChild();
		ApiGetExchangegoodsResult apiAddResult = new ApiGetExchangegoodsResult();
		
		try{
			//从传入的参数中获取订单编号
			String orderCode = egi.getOrderCode();
			
			//获取换货明细信息
			List<ExchangegoodsDetailModel> egDetialList = new ArrayList<ExchangegoodsDetailModel>();
			egDetialList = egi.getExgDetailListInput();
			
			//对换货明细信息中的换货产品的流水号和换货数量进行判断,如有空的情况,不进行换货的增加处理,并给页面返回提示信息
			for(ExchangegoodsDetailModel segDetail: egDetialList){
				if(StringUtils.isBlank(segDetail.getSerialNumber())){
					//换货明细信息中的换货产品的流水号为空时
					apiAddResult.setResultCode(939301040);
					apiAddResult.setResultMessage(bInfo(939301040));
					return apiAddResult;
				}else if((StringUtils.isBlank(String.valueOf(segDetail.getCount()))) || (segDetail.getCount() <= 0)){
					//换货明细信息中的换货数量为空或是负数时
					apiAddResult.setResultCode(939301061);
					apiAddResult.setResultMessage(bInfo(939301061));
					return apiAddResult;
				}
			}
			
			//判断换货订单的交易是否已完成
			if(!getOrderCodeStatus(orderCode)){
				apiAddResult.setResultCode(939301068);
				apiAddResult.setResultMessage(bInfo(939301068));
				return apiAddResult;
			}
			
			//换后详细信息存在时，进行下面的换货数量判断
			if(egDetialList.size() != 0){
				MDataMap insDatamap = new MDataMap();
				//客户申请的换货数量与订单中的产品数量进行比较判断
				for(ExchangegoodsDetailModel segDetail: egDetialList){
					//获取页面上用户申请的换货数量
					int customerExSum = segDetail.getCount();
					//获取换货产品流水号
					String zid = segDetail.getSerialNumber();
					//以产品流水号为条件获取该订单下的产品数量
					MDataMap orderDetailData = DbUp.upTable("oc_orderdetail").one("zid", zid);
					int orderDetailSum = 0;
					if(orderDetailData != null && orderDetailData.size() >0){
						String tempOrderDetailSum = orderDetailData.get("sku_num");
						if(StringUtils.isNotBlank(tempOrderDetailSum)){
							orderDetailSum = Integer.parseInt(tempOrderDetailSum);
						}
					}
					//判断处理：客户申请的换货数量不能大于现有订单中的产品数量
					if((customerExSum > orderDetailSum)){
						apiAddResult.setResultCode(939301055);
						apiAddResult.setResultMessage(bInfo(939301055));
						return apiAddResult;
					}
					//以产品流水号为条件获取该订单下的换货数量
					MDataMap exDetailMp = new MDataMap();
					exDetailMp.put("serial_number", zid);
					List<MDataMap> exDetailData = new ArrayList<MDataMap>();
					exDetailData = DbUp.upTable("oc_exchange_goods_detail").queryAll("", "", "", exDetailMp);
					int exDetailSum = 0;
					for(int i=0 ;i<exDetailData.size();i++){
						String tempExDetailSum = exDetailData.get(i).get("count");
						if(StringUtils.isNotBlank(tempExDetailSum)){
							exDetailSum += Integer.parseInt(tempExDetailSum);
						}
					}
					//判断处理：客户申请的换货数量与已有的换货数量之和不能大于现有订单中的数量
					if((customerExSum+exDetailSum) > orderDetailSum){
						apiAddResult.setResultCode(939301056);
						apiAddResult.setResultMessage(bInfo(939301056));
						return apiAddResult;
					}
				}
				
				//换货数据的插入处理
				//生成换后编号
				String exchangeNo = WebHelper.upCode("HH");
				//根据订单号在订单表中获取卖家编号
				MDataMap oneMap = DbUp.upTable("oc_orderinfo").one("order_code",orderCode);
				String sellerCode= oneMap.get("seller_code");
				//换货主信息设置
				insDatamap.put("exchange_no", exchangeNo);
				insDatamap.put("order_code", egi.getOrderCode());
				insDatamap.put("buyer_code", egi.getBuyerCode());
				insDatamap.put("seller_code", sellerCode);
				insDatamap.put("exchange_reason", egi.getExchangeReason());
				insDatamap.put("status", "4497153900020001");//默认换货流转状态初始值
				//换货运费设置
				BigDecimal tm = new BigDecimal(egi.getTransportMoney()); 
				tm = tm.setScale(2, BigDecimal.ROUND_HALF_UP);
				insDatamap.put("transport_money", String.valueOf(tm));
				insDatamap.put("contacts", egi.getContacts());
				insDatamap.put("mobile", egi.getMobile());
				insDatamap.put("address", egi.getAddress());
				insDatamap.put("pic_url", egi.getPicUrl());
				insDatamap.put("description", egi.getDescription());
				//设置日期格式
				insDatamap.put("create_time", DateUtil.getSysDateTimeString());
				//换货主信息增加
				DbUp.upTable("oc_exchange_goods").dataInsert(insDatamap);
				
				//换货明细表信息设置
				for(ExchangegoodsDetailModel segDetail: egDetialList){
					insDatamap.clear();
					//根据订单流水号(ZID)获取订单详细表中数据的其他信息，如sku_code,sku_name,sku_price
					MDataMap orderDetailMap = DbUp.upTable("oc_orderdetail").one("zid", segDetail.getSerialNumber());
					insDatamap.put("exchange_no", exchangeNo);
					insDatamap.put("sku_code", orderDetailMap.get("sku_code"));
					insDatamap.put("sku_name", orderDetailMap.get("sku_name"));
					insDatamap.put("count", String.valueOf(segDetail.getCount()));
					//当前价格设置
					BigDecimal cp = new BigDecimal(orderDetailMap.get("sku_price"));
					cp = cp.setScale(2, BigDecimal.ROUND_HALF_UP);
					insDatamap.put("current_price", String.valueOf(cp));
					insDatamap.put("serial_number", segDetail.getSerialNumber());
					insDatamap.put("product_picurl", orderDetailMap.get("product_picurl"));
					//换货明细信息增加
					DbUp.upTable("oc_exchange_goods_detail").dataInsert(insDatamap);
				}
				
				//增加状态日志信息
				egmc.setExchangeNo(exchangeNo);
				egmc.setStatus("4497153900020001");//默认换货流转状态初始值
				egmc.setCreateUser(egi.getCreateUser());
				boolean updFlag = this.updExchangegoodsStatusLog(egmc,"");
				if(!updFlag){
					apiAddResult.setResultCode(939301007);
					apiAddResult.setResultMessage(bInfo(939301007,"状态日志增加处理在","增加状态日志"));
					return apiAddResult;
				}
			} else {
				apiAddResult.setResultCode(939301013);
				apiAddResult.setResultMessage(bInfo(939301013,"换货详细信息"));
				return apiAddResult;
			}
		} catch (Exception ex){
			apiAddResult.setResultCode(939301007);
			apiAddResult.setResultMessage(bInfo(939301007,"增加","增加"));
			return apiAddResult;
		}
		return apiAddResult;
	}

	/**
	 * 判断换货订单的交易状态是否已完成
	 * @param orderCode 换货订单编码
	 * @return 
	 */
	private boolean getOrderCodeStatus(String orderCode) {
		MDataMap oneDataMap = DbUp.upTable("oc_orderinfo").one("order_code",orderCode);
		if(oneDataMap != null){
			String orderStatus = oneDataMap.get("order_status");
			if("4497153900010005".equals(orderStatus)){
				return true;
			}
		}
		return false;
	}

	/**
	 * 更新换货状态
	 * @param edm 换货信息
	 * @return 换货信息对象
	 */
	public ApiGetExchangegoodsResult updExchangegoods(ExchangegoodsModelChild segm){
		
		ApiGetExchangegoodsResult apiAddResult = new ApiGetExchangegoodsResult();
		
		try{
			//更新前的换货信息当前状态
			String strNowStatus = "";
			MDataMap updDatamap = new MDataMap();
			
			//日志创建人
			String createUser = segm.getCreateUser();
			
			//日志创建人设定
			segm.setCreateUser(createUser);
			
			//获取更新前的换货信息当前状态
			strNowStatus = this.getNowStatus(segm.getExchangeNo());
			
			//对获取的换货信息当前状态做非空判断（不存在的换货单号）
			if(strNowStatus == null || "".equals(strNowStatus)){
				//上面的更新换货信息状态是否要回滚 待定??
				apiAddResult.setResultCode(939301014);
				apiAddResult.setResultMessage(bInfo(939301014,segm.getExchangeNo()));
			} else {
				//以"换货单号"为单位更新
				updDatamap.put("exchange_no", segm.getExchangeNo());
				updDatamap.put("status", segm.getStatus());
				DbUp.upTable("oc_exchange_goods").dataUpdate(updDatamap, "status", "exchange_no");
				
				//更新换货状态日志
				boolean updLogFlag = this.updExchangegoodsStatusLog(segm,strNowStatus);
				if(!updLogFlag){
					//上面的更新换货信息状态是否要回滚 待定??
					apiAddResult.setResultCode(939301008);
					apiAddResult.setResultMessage(bInfo(939301008));
				}
			}
			
		} catch (Exception e){
			apiAddResult.setResultCode(939301008);
			apiAddResult.setResultMessage(bInfo(939301008));
			return apiAddResult;
		}
		
		return apiAddResult;
	}
	
	/**
	 * 更新换货状态日志
	 * @param egm 换货信息
	 * @param strNowStatus 获取到更新前的上次日志中的新状态
	 */
	private boolean updExchangegoodsStatusLog(ExchangegoodsModelChild segm,
			String strNowStatus) {
		
		ExchangegoodsStatusLogService egLogService = new ExchangegoodsStatusLogService();
		ExchangegoodsStatusLogModel egLogModel = new ExchangegoodsStatusLogModel();	
		
		//换货状态日志设定
		egLogModel.setExchangeNo(segm.getExchangeNo());
		//信息内容待定??
		egLogModel.setInfo("");
		//把获取到更新前的上次日志中的新状态设置为本次日志的旧状态
		egLogModel.setOldStatus(strNowStatus);
		//本次日志的新状态
		egLogModel.setNowStatus(segm.getStatus());
		//创建人
		egLogModel.setCreateUser(segm.getCreateUser());
		//把系统时间设置为创建时间
		egLogModel.setCreateTime(DateUtil.getSysDateTimeString());
		
		try{
			//更新换货状态日志
			boolean flag = egLogService.addExchangegoodsStatusLogService(egLogModel);
			//更新失败时
			if(!flag){
				return false;
			}
			
		} catch (Exception ex){
			return false;
		}
		
		return true;
	}
	
	/**
	 * 获取本次换货状态更新前记录的新状态
	 * @param exchangeNo 换货单号
	 * @return 获取的新状态
	 */
	private String getNowStatus(String exchangeNo) {
		
		//更新前记录的新状态
		String newStatus = "";
		
		//设置查询条件
		MDataMap paramMap = new MDataMap();
		//以"换货单号"为单位,以创建时间为降序进行查询
		paramMap.put("exchangeNo", exchangeNo);
		String sqlwhere = "exchange_no=:exchangeNo ";
		//获取的新状态
		for (MDataMap md : DbUp.upTable("lc_exchangegoods").query("now_status", "-create_time", sqlwhere, paramMap, -1, -1)){
			//取第一条数据的新状态，也就是时间最新的记录的新状态，即本次更新前  上一条记录的新状态
			newStatus = md.get("now_status");
			break;
		}
		//返回获取的新状态
		return newStatus;
	}
}
