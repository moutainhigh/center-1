package com.cmall.groupcenter.homehas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmapper.PcProductflowMapper;
import com.cmall.dborm.txmapper.PcProductinfoMapper;
import com.cmall.dborm.txmapper.PcSkuinfoMapper;
import com.cmall.dborm.txmodel.PcProductflow;
import com.cmall.dborm.txmodel.PcProductinfoExample;
import com.cmall.dborm.txmodel.PcSkuinfoExample;
import com.cmall.dborm.txmodel.PcSkuinfoWithBLOBs;
import com.cmall.groupcenter.homehas.config.RsyncConfigSyncGoods;
import com.cmall.groupcenter.homehas.model.RsyncDateCheck;
import com.cmall.groupcenter.homehas.model.RsyncModelGoods;
import com.cmall.groupcenter.homehas.model.RsyncRequestSyncGoods;
import com.cmall.groupcenter.homehas.model.RsyncResponseSyncGoods;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.productcenter.common.SkuCommon;
import com.cmall.productcenter.model.PcProductdescription;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webclass.WarnCount;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 同步家有商品处理类
 * @author jl
 *
 */
public class RsyncSyncGoods extends RsyncHomeHas<RsyncConfigSyncGoods, RsyncRequestSyncGoods, RsyncResponseSyncGoods> {

	final static RsyncConfigSyncGoods RSYNC_CONFIG_SYNC_GOODS = new RsyncConfigSyncGoods();

	public RsyncConfigSyncGoods upConfig() {
		return RSYNC_CONFIG_SYNC_GOODS;
	}
	
	public RsyncRequestSyncGoods upRsyncRequest() {
		// 返回输入参数
		RsyncRequestSyncGoods request = new RsyncRequestSyncGoods();
		RsyncDateCheck rsyncDateCheck = upDateCheck(upConfig());
		request.setStart_day(rsyncDateCheck.getStartDate());
		request.setEnd_day(rsyncDateCheck.getEndDate());
		return request;
	}
	
	public synchronized MWebResult insertGoods(RsyncModelGoods goods,String startime) {
		MWebResult mWebResult = new MWebResult();
		
		// 清除商品名称开头和末尾中可能包含的不可见字符
		goods.setGood_nm(StringUtils.deleteWhitespace(goods.getGood_nm()));
		
		String productStatus = "";		//商品原来的状态
		String productUID = "";			//商品的UID
		//此处加入爱奇艺对接逻辑
		String aq_good_id = goods.getAq_good_id();
		if(StringUtils.isNotBlank(aq_good_id)){
			MDataMap proMap = DbUp.upTable("pc_productinfo").one("product_code",aq_good_id);
			if(proMap!=null){
				productStatus = proMap.get("product_status");
				MDataMap extMap = DbUp.upTable("pc_productinfo_ext").one("product_code",aq_good_id);
				if(extMap!=null){//存在 修改
					DbUp.upTable("pc_productinfo_ext").dataUpdate(new MDataMap("ld_good_id",goods.getGood_id(),"zid",extMap.get("zid")), "", "zid");
				}else{
					DbUp.upTable("pc_productinfo_ext").dataInsert(new MDataMap("product_code_old",proMap.get("product_code_old"),"product_code",aq_good_id,"poffer","system","ld_good_id",goods.getGood_id()));
				}
			}
		}
		
		// 市场价是售价的固定1.2倍取整
		BigDecimal marketPrice = goods.getPrc().multiply(new BigDecimal("1.2")).setScale(0, BigDecimal.ROUND_HALF_UP);
		
		String uid=UUID.randomUUID().toString().replace("-", "");
		PcProductinfo productinfo=new PcProductinfo();
		productinfo.setProductCode(goods.getGood_id());
		productinfo.setUid(uid);
		productinfo.setProductCodeOld(goods.getGood_id());
		productinfo.setProductShortname(goods.getGood_nm());
		productinfo.setBrandName(goods.getBrand_nm());
		productinfo.setProdutName(goods.getGood_nm());
		productinfo.setMaxSellPrice(goods.getPrc());
		productinfo.setMinSellPrice(goods.getPrc());
		if (null != goods.getTax_rate() && BigDecimal.ZERO.compareTo(goods.getTax_rate()) < 0) {
			productinfo.setTaxRate(goods.getTax_rate().divide(new BigDecimal("100")));		//税率
		}
		
		productinfo.setVipdayFlag(goods.getCust_day());
		
		/////////////////////商品图片 在此不存储
//		if(goods.getImages()!=null&&goods.getImages().size()>0){//如果是多张图片，就取第一个
//			productinfo.setMainPicUrl(goods.getImages().get(0).getGood_image());
//		}
		
		productinfo.setCostPrice(goods.getCosts());
		productinfo.setMarketPrice(marketPrice);
		
		productinfo.setProductStatus("4497153900060003");//商品下架
		productinfo.setValidate_flag(goods.getValidate_flag());//新增字段，是否是虚拟商品
		if (StringUtils.isNotBlank(goods.getIs_low_good()) && "Y".equals(goods.getIs_low_good())) {
			productinfo.setLowGood("449747110002");
		}else{
			productinfo.setLowGood("449747110001");
		}
		
		// 提货券商品
		productinfo.setVoucherGood("449747110001");
		if("Y".equalsIgnoreCase(goods.getIs_bill())){
			productinfo.setVoucherGood("449747110002");
		}
		
		productinfo.setPrchCd(StringUtils.trimToEmpty(goods.getPrch_cd()));
		productinfo.setAccmYn(StringUtils.trimToEmpty(goods.getAccm_yn()));
		productinfo.setVlOrs(StringUtils.trimToEmpty(goods.getVl_ors()));
		productinfo.setDlrCharge(StringUtils.trimToEmpty(goods.getDlr_charge()));
		productinfo.setCspsFlag(StringUtils.trimToEmpty(goods.getIs_csps()));
		productinfo.setSoId(StringUtils.trimToEmpty(goods.getSo_id()));
		
		PcProductdescription productdescription=new PcProductdescription();
		productdescription.setUid(uid);
		productdescription.setProductCode(productinfo.getProductCode());
		productdescription.setDescriptionInfo(goods.getGood_func_expl());
		productdescription.setKeyword(goods.getSelling_point_desc());
		productinfo.setDescription(productdescription);
		
		
		ProductSkuInfo productSkuInfo=new ProductSkuInfo();
		productSkuInfo.setSkuCode(goods.getGood_id());
		productSkuInfo.setProductCode(goods.getGood_id());
		productSkuInfo.setSellPrice(goods.getPrc());
		productSkuInfo.setCostPrice(goods.getCosts());
		productSkuInfo.setMarketPrice(marketPrice);
//		productSkuInfo.setSkuPicUrl(goods.getImages());
		productSkuInfo.setSkuName(goods.getGood_nm());
		productSkuInfo.setSellProductcode(goods.getGood_id());//设置外部商品id
//		productSkuInfo.setSellProductcode(MemberConst.MANAGE_CODE_HOMEHAS);
		productSkuInfo.setSaleYn("N");//是否可卖
		
		List<ProductSkuInfo> productSkuInfoList=new ArrayList<ProductSkuInfo>(1);
		productSkuInfoList.add(productSkuInfo);
		productinfo.setProductSkuInfoList(productSkuInfoList);
		
		//同步过来的商品同时属于家有惠和惠家有,所以要循环两遍.
		//String [] apps = new String[]{MemberConst.MANAGE_CODE_HOMEHAS,MemberConst.MANAGE_CODE_HPOOL};
		String [] apps = new String[]{MemberConst.MANAGE_CODE_HOMEHAS};
		
		for (String app_code : apps) {
			
			productinfo.setSellProductcode(app_code);
			productinfo.setSellerCode(app_code);
			productSkuInfo.setSellerCode(app_code);
			
			if(MemberConst.MANAGE_CODE_HPOOL.equals(app_code)){ //家有惠的商品编号前要加9
				productinfo.setProductCode("9"+goods.getGood_id());
				productdescription.setProductCode(productinfo.getProductCode());
				productSkuInfo.setSkuCode(productinfo.getProductCode());
				productSkuInfo.setProductCode(productinfo.getProductCode());
				
				productinfo.setSmallSellerCode(app_code);
				productSkuInfo.setSkuAdv(goods.getSelling_point_desc());
			}else{
				productinfo.setProductCode(goods.getGood_id());
				productdescription.setProductCode(productinfo.getProductCode());
				productSkuInfo.setSkuCode(productinfo.getProductCode());
				productSkuInfo.setProductCode(productinfo.getProductCode());
				productinfo.setSmallSellerCode(app_code);
				productSkuInfo.setSkuAdv(goods.getGood_func_expl());
			}
			
			boolean statusChange = false; // 商品状态变更标识
			boolean flagMod = true;	//标识是否可以进行修改 ,当sellerCode为“SI2003”,smallSellerCode不是"SI2003时不会修改商品信息"
			List<Map<String, Object>> list= DbUp.upTable("pc_productinfo").dataSqlList("SELECT p.product_code,p.product_shortname,p.max_sell_price,p.min_sell_price,p.product_name,p.cost_price,p.tax_rate,p.low_good,d.description_info,p.update_time,p.small_seller_code,p.seller_code,p.voucher_good,p.vipday_flag,p.prch_cd,p.product_status,p.accm_yn,p.vl_ors,p.dlr_charge FROM pc_productinfo p LEFT JOIN pc_productdescription d on p.product_code=d.product_code where p.product_code_old=:product_code_old and p.seller_code=:seller_code and p.small_seller_code = :seller_code ", new MDataMap("product_code_old",goods.getGood_id(),"seller_code",app_code));
			if(list==null||list.size()<1){  //若果不存在，就添加
				ProductService productService=BeansHelper.upBean("bean_com_cmall_productcenter_service_ProductService");
				StringBuffer error=new StringBuffer();
				
				// 新商品默认仅支持在线支付，厂商收款除外
				if(!"Y".equalsIgnoreCase(goods.getDlr_charge())) {
					productinfo.setOnlinepayFlag("449747110002");
				}
				int resultCode=productService.AddProductTx(productinfo, error,"");
				mWebResult.setResultCode(resultCode);
				mWebResult.setResultMessage(error.toString());
			}else {
				
				//若存在，就更新   当更新 prc，good_nm 时，商品状态更新为待上架 4497153900060003
				//更新的字段有 prc ，good_nm ，costs  ，good_func_expl ，brand_nm 
				Map<String, Object> map= list.get(0);
				String product_code=(String) map.get("product_code");
				String product_shortname=(String) map.get("product_shortname");
				String product_name=(String) map.get("product_name");
				BigDecimal max_sell_price=(BigDecimal) map.get("max_sell_price");
//				BigDecimal min_sell_price=(BigDecimal) map.get("min_sell_price");
				BigDecimal cost_price=(BigDecimal) map.get("cost_price");
				String description_info=(String) map.get("description_info");
				String update_time=(String) map.get("update_time");
				BigDecimal tax_rate = (BigDecimal)map.get("tax_rate");
				String low_good=(String) map.get("low_good");
				String voucher_good=(String) map.get("voucher_good");
				String sellerCode = (String) map.get("seller_code");			
				String smallSellerCode = (String) map.get("small_seller_code");
				String vipdayFlag = map.get("vipday_flag")+"";
				String prchCd = map.get("prch_cd")+"";
				productStatus = map.get("product_status")+"";
				String accmYn = map.get("accm_yn")+"";
				String vl_ors = map.get("vl_ors")+"";
				String dlr_charge = map.get("dlr_charge")+"";
				String cspsFlag = map.get("csps_flag")+"";
				String so_id = map.get("so_id")+"";
				
				if (AppConst.MANAGE_CODE_HOMEHAS.equals(sellerCode) && !AppConst.MANAGE_CODE_HOMEHAS.equals(smallSellerCode)) {
					flagMod = false;
				}
				if (flagMod) {
					String text = ""; // 商品变更内容
					
					//特定校验 如果更新时间为空 则设定为一个很小的时间
					if(StringUtils.isBlank(update_time))
					{
						update_time="2010-01-01 00:00:00";
					}
					//请求的开始时间>更新时间 才开始更新
					if(compare(startime, update_time)>0){
						PcProductflowMapper ppfm = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductflowMapper");
						PcSkuinfoMapper pcsm = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcSkuinfoMapper");
						PcProductinfoMapper pcpm = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductinfoMapper");
						
						String now=DateUtil.getSysDateTimeString();
						boolean hflagp=false;
						boolean hflags=false;//判断sku是否修改
						com.cmall.dborm.txmodel.PcProductinfo pcProductinfou = new com.cmall.dborm.txmodel.PcProductinfo(); 
						PcSkuinfoWithBLOBs pcSkuinfo = new PcSkuinfoWithBLOBs();
						PcProductinfo plog=new PcProductinfo();
						//判断关键字段数据是否有变化
		//				if(!(goods.getPrc().compareTo(max_sell_price)==0)||!trim(goods.getGood_nm()).equals(product_shortname)||!trim(goods.getGood_nm()).equals(product_name)||!(goods.getPrc().compareTo(min_sell_price)==0)){
						//update by jlin 2015-04-23 16:11:35  商品的价格将不再更新
		//				if(!trim(goods.getGood_nm()).equals(product_shortname)||!trim(goods.getGood_nm()).equals(product_name)){
						
						/* 商品名称变更不再自动下架 */
						if(!trim(goods.getGood_nm()).equals(trim(product_shortname))){
//							pcProductinfou.setProductStatus("4497153900060003");
		//					pcProductinfou.setMaxSellPrice(goods.getPrc());
		//					pcProductinfou.setMinSellPrice(goods.getPrc());
							pcProductinfou.setMaxSellPrice(null);
							pcProductinfou.setMinSellPrice(null);
							pcProductinfou.setProductShortname(goods.getGood_nm());//此字段作为LD商品名称
							pcProductinfou.setProductName(StringUtils.deleteWhitespace(goods.getGood_nm()));
							
		//					pcSkuinfo.setSellPrice(goods.getPrc());
							pcSkuinfo.setSellPrice(null);
							pcSkuinfo.setSkuName(goods.getGood_nm());
							
							if(MemberConst.MANAGE_CODE_HPOOL.equals(app_code)){
								pcSkuinfo.setSkuAdv(goods.getSelling_point_desc());
							}
							
							
//							plog.setProductStatus("4497153900060003");
		//					plog.setMaxSellPrice(goods.getPrc());
							plog.setMaxSellPrice(null);
							plog.setProductShortname(goods.getGood_nm());
							plog.setProdutName(goods.getGood_nm());
							
							hflagp=true;
							hflags=true;
							
							text += "[名称变更："+product_shortname+" => "+goods.getGood_nm()+"]";
						}
						
						// 商品价格变更时，需要下架商品
						if(!(goods.getPrc().compareTo(max_sell_price)==0)){
							//pcProductinfou.setProductStatus("4497153900060003");
							//pcProductinfou.setMaxSellPrice(goods.getPrc());
							//pcProductinfou.setMinSellPrice(goods.getPrc());
							
							//plog.setMaxSellPrice(null);
							//pcSkuinfo.setSellPrice(goods.getPrc());
							
							//hflagp=true;
							//hflags=true;
							
							//text += "[售价变更: "+max_sell_price+" => "+goods.getPrc()+"]";
						}
						
						if(!(goods.getCosts().compareTo(cost_price)==0)){
							pcProductinfou.setCostPrice(goods.getCosts());
							pcSkuinfo.setCostPrice(goods.getCosts());
							
							plog.setCostPrice(goods.getCosts());
							hflagp=true;
							
							text += "[成本变更: "+cost_price+" => "+goods.getCosts()+"]";
						}
		//				if(!(goods.getCosts().compareTo(cost_price)==0)){
		//					pcProductinfou.setCostPrice(goods.getCosts());
		//					
		//					plog.setCostPrice(goods.getCosts());
		//					hflagp=true;
		//				}
						if (productinfo.getTaxRate().compareTo(tax_rate) != 0) {
							pcProductinfou.setTaxRate(productinfo.getTaxRate());
							hflagp=true;
							text += "[税率变更: "+tax_rate+" => "+productinfo.getTaxRate()+"]";
						}
						if (StringUtils.isNotBlank(goods.getIs_low_good()) && "Y".equals(goods.getIs_low_good())) {
							if (!"449747110002".equals(low_good)) {
								pcProductinfou.setLowGood("449747110002");
								hflagp = true;
							}
						}else{
							if (!"449747110001".equals(low_good)) {
								pcProductinfou.setLowGood("449747110001");
								hflagp = true;
							}
						}
						
						
						// 提货券商品判断
						if("Y".equalsIgnoreCase(goods.getIs_bill()) && !"449747110002".equals(voucher_good)){
							pcProductinfou.setVoucherGood("449747110002");
							hflagp = true;
						}
						if("N".equalsIgnoreCase(goods.getIs_bill()) && !"449747110001".equals(voucher_good)){
							pcProductinfou.setVoucherGood("449747110001");
							hflagp = true;
						}
						
						if(!vipdayFlag.equalsIgnoreCase(goods.getCust_day())){
							pcProductinfou.setVipdayFlag(goods.getCust_day());
							hflagp = true;
						}
						
						// 不计入毛利商品标识
						if(!prchCd.equalsIgnoreCase(productinfo.getPrchCd())){
							pcProductinfou.setPrchCd(productinfo.getPrchCd());
							hflagp = true;
						}
						
						// 商品是否赋予积分
						if(!accmYn.equalsIgnoreCase(productinfo.getAccmYn())){
							pcProductinfou.setAccmYn(productinfo.getAccmYn());
							hflagp = true;
						}
						
						// 是否一件代发
						if(!vl_ors.equalsIgnoreCase(productinfo.getVlOrs())){
							pcProductinfou.setVlOrs(productinfo.getVlOrs());
							hflagp = true;
						}
						// 是否厂商收款
						if(!dlr_charge.equalsIgnoreCase(productinfo.getDlrCharge())){
							pcProductinfou.setDlrCharge(productinfo.getDlrCharge());
							hflagp = true;
						}
						
						// 是否厂商配送
						if(!cspsFlag.equalsIgnoreCase(productinfo.getCspsFlag())){
							pcProductinfou.setCspsFlag(productinfo.getCspsFlag());
							hflagp = true;
						}
						
						// 商品归属
						if(!so_id.equalsIgnoreCase(productinfo.getSoId())){
							pcProductinfou.setSoId(productinfo.getSoId());
							hflagp = true;
						}
						
						if(hflagp){ 
							//修改商品表
							pcProductinfou.setUpdateTime(now);
							
							PcProductinfoExample pcProductinfoExample=new PcProductinfoExample();
							pcProductinfoExample.createCriteria().andProductCodeEqualTo(product_code);
							pcpm.updateByExampleSelective(pcProductinfou, pcProductinfoExample);
							
							if("4497153900060003".equals(pcProductinfou.getProductStatus()) && "4497153900060002".equals(productStatus)){
								text += "，[商品自动下架!!!]";
								statusChange = true;
							}
							
							if(StringUtils.isNotBlank(text)){
								sendWx("[LD商品信息变更]["+product_code+", "+product_name+"]"+text);
							}
						}
						
						if(hflags){
							
							//修改sku表
							PcSkuinfoExample pcSkuinfoExample =new PcSkuinfoExample();
							pcSkuinfoExample.createCriteria().andProductCodeEqualTo(product_code);
							pcsm.updateByExampleSelective(pcSkuinfo, pcSkuinfoExample);
						}
						
						//修改描述信息
						if(!trim(goods.getGood_func_expl()).equals(description_info)){
							DbUp.upTable("pc_productdescription").dataUpdate(new MDataMap("description_info",trim(goods.getGood_func_expl()),"product_code",product_code), "description_info", "product_code");
							
							PcProductdescription dlog = new PcProductdescription();
							dlog.setDescriptionInfo(goods.getGood_func_expl());
							plog.setDescription(dlog);
							
							hflagp=true;
						}
						
						//日中表中添加信息
						
						if (hflagp) {
							com.cmall.dborm.txmodel.PcProductflow ppf = new PcProductflow();
							ppf.setCreateTime(now);
							ppf.setCreator(app_code);//惠家有
							ppf.setFlowCode(WebHelper.upCode("PF"));
							ppf.setFlowStatus(SkuCommon.ProUpaOr);
							ppf.setProductCode(product_code);
							JsonHelper<PcProductinfo> pHelper = new JsonHelper<PcProductinfo>();
							ppf.setProductJson(pHelper.ObjToString(plog));
							ppf.setUid(UUID.randomUUID().toString().replace("-", ""));
							ppf.setUpdateTime(now);
							ppf.setUpdator(app_code);
							ppfm.insertSelective(ppf);
						}
					}
				}
			}
			if (flagMod) {
				
				//添加商品扩展信息
				MDataMap dataMap = new MDataMap();
				dataMap.put("product_code_old", goods.getGood_id());
				dataMap.put("product_code", productinfo.getProductCode());
				dataMap.put("prch_type", goods.getPrch_type());
				dataMap.put("dlr_id", goods.getDlr_id());
				dataMap.put("dlr_nm", goods.getDlr_nm());
//				dataMap.put("oa_site_no", goods.getOa_site_no());
				//2016-08-04修改，新增字段
				StringBuffer oa_site_no = new StringBuffer();
				if (null != goods.getSite_no_list()) {
					for (int i = 0; i < goods.getSite_no_list().size(); i++) {
						oa_site_no.append(goods.getSite_no_list().get(i).getSITE_NO());
						if (i < goods.getSite_no_list().size()-1) {
							oa_site_no.append(",");
						}
					}
				}
				dataMap.put("oa_site_no", oa_site_no.toString());
				dataMap.put("gross_profit", goods.getGross_profit());
				dataMap.put("accm_rng", String.valueOf(goods.getAccm_rng()));
				dataMap.put("validate_flag", goods.getValidate_flag());
				dataMap.put("ld_good_id",goods.getGood_id());
				
				if(StringUtils.isNotBlank(goods.getMd_id())){
					dataMap.put("md_id", goods.getMd_id());
				}
				
				if(StringUtils.isNotBlank(goods.getMd_nm())){
					dataMap.put("md_nm", goods.getMd_nm());
				}
				
				dataMap.put("no_gift",goods.getNo_gift()==null?"":goods.getNo_gift());
				dataMap.put("is_hwg",goods.getIs_hwg()==null?"":goods.getIs_hwg());
				//===5.2.4 违禁品禁止下单 begin===				
				dataMap.put("wg", goods.getWg()==null?"":goods.getWg());//重量 (kg)
				dataMap.put("is_unpack", goods.getIs_unpack()==null?"N":goods.getIs_unpack());//是否拆包件  Y/N
				dataMap.put("is_danger", goods.getIs_danger()==null?"N":goods.getIs_danger());//违禁品属性
				dataMap.put("check_danger", goods.getCheck_danger()==null?"N":goods.getCheck_danger());//是否需要检验商品是违禁品
				//===5.2.4 违禁品禁止下单 end===
				List<MDataMap> listd1 = new ArrayList<MDataMap>();
				boolean isReplace = false;
				if(goods.getIs_unpack() != null && "Y".equals(goods.getIs_unpack())) {
					listd1 = DbUp.upTable("pc_productinfo_ext").queryAll("uid,wd,dp,hg", "", "product_code=:product_code", new MDataMap("product_code",productinfo.getProductCode()));
					if(listd1 != null && listd1.size() > 0) {
						isReplace = RsyncSyncGoods.isReplaceWdDpHg(goods.getWd()==null?"":goods.getWd(), goods.getDp()==null?"":goods.getDp(), goods.getHg()==null?"":goods.getHg(), listd1.get(0));
					}					
				} else {					
					listd1 = DbUp.upTable("pc_productinfo_ext").queryAll("uid", "", "product_code=:product_code", new MDataMap("product_code",productinfo.getProductCode()));					
				}
				if(isReplace) {
					dataMap.put("wd", listd1.get(0).get("wd").toString());
					dataMap.put("dp", listd1.get(0).get("dp").toString());
					dataMap.put("hg", listd1.get(0).get("hg").toString());
				} else {
					dataMap.put("wd", goods.getWd()==null?"":goods.getWd());
					dataMap.put("dp", goods.getDp()==null?"":goods.getDp());
					dataMap.put("hg", goods.getHg()==null?"":goods.getHg());
				}
				if(listd1!=null&&listd1.size()>0){//存在 修改
					dataMap.put("uid",listd1.get(0).get("uid"));
					DbUp.upTable("pc_productinfo_ext").dataUpdate(dataMap, "", "uid");
				}else{
					DbUp.upTable("pc_productinfo_ext").dataInsert(dataMap);
				}
				
				
				PlusHelperNotice.onChangeProductInfo(productinfo.getProductCode());
				
				// 商品触发下架操作时再刷搜索的索引，避免无意义的刷新操作
				if(statusChange) {
					//触发消息队列
					ProductJmsSupport pjs = new ProductJmsSupport();
					pjs.onChangeForProductChangeAll(productinfo.getProductCode());
				}
			}
		}
		
		
		//TODO  商品大类  中类 小类 转换？？？？？ 暂时不存
//		"lclss_id" : 17,  //商品大类
//		"mclss_id" : 13,  //中类
//		"sclss_id" : 15,  //小类
//		"sale_cd" : "10",//销售方法
//		"dlv_cd" : "20",//配送方式
//		"accm_cd" : "10",//积分类别
//		"accm_qty" : null,////？？？？？？？？？？？
//		"dis_amt" : null,  ///？？？？？？？？？？？
//		"maker" : "中山市泛华精细化学品有限公司", //制造商
//		"org_rgn" : "中山", //产地
//		"cd_val_desc" : "箱", //商品计量单位
//		"as_std_desc" : null,   //维修及服务标准
//		"good_asse_expl" : null,  //商品组合及说明
//		"cs_dispose_mode" : "JL", //售后处理方式
//		"mdf_date" : "2014-05-12 15:49:25.0",  //商品上架更新时间
		return mWebResult;
	}
	
	
	public RsyncResult doProcess(RsyncRequestSyncGoods tRequest,
			RsyncResponseSyncGoods tResponse) {
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

				for (RsyncModelGoods goods : tResponse.getResult()) {
					MWebResult mResult = insertGoods(goods,tRequest.getStart_day());

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

			result.setStatusData(tRequest.getEnd_day());
		}

		return result;
	}

	public RsyncResponseSyncGoods upResponseObject() {
		return new RsyncResponseSyncGoods();
	}
	
	private String trim(Object obj){
		return obj==null?"":obj.toString().trim();
	}
	
	
	
	/**
	 * 比较两个时间
	 * 时间格式：2014-12-02 20:14:10
	 * <br>大于结束时间返回正数，等于 0，小于 负数
	 * @param start_time
	 * @param end_time
	 * @return
	 */
	public synchronized static int compare(String start_time,String end_time){
		try {
			Date date1=DateUtil.sdfDateTime.parse(start_time);
			Date date2=DateUtil.sdfDateTime.parse(end_time);
			return date1.compareTo(date2);
		} catch (Exception e) {
			return 1;
		}
	}
	
	private void sendMail(String product_name,String product_code){
		
		String receives[]= bConfig("groupcenter.offPro_sendMail_receives").split(",");
		String title= bConfig("groupcenter.offPro_sendMail_title");
		String content= bConfig("groupcenter.offPro_sendMail_content");
		
		for (String receive : receives) {
			if(StringUtils.isNotBlank(receive)){
				MailSupport.INSTANCE.sendMail(receive, FormatHelper.formatString(title,product_code,product_name), FormatHelper.formatString(content,product_code,product_name));
			}
		}
	}
	
	private void sendWx(String text){
		
		String receices[] = bConfig("groupcenter.offPro_sendWx_receives").split(",");
		
		for (String receive : receices) {
			if(StringUtils.isNotBlank(receive)){
				WarnCount count = new WarnCount();
				count.sendWx(receive , text);
			}
		}
		
	}
	
	public synchronized static boolean isInteger(String str) {  
		if("".equals(str))
			return false;
		Pattern pattern = Pattern.compile("[0-9]*");
	    return pattern.matcher(str).matches();  
	}
	
	private synchronized static boolean isReplaceWdDpHg(String wd, String dp, String hg, MDataMap oldData) {
		int old_wd = 0;
		if(RsyncSyncGoods.isInteger(oldData.get("wd").toString())) {
			old_wd = Integer.parseInt(oldData.get("wd").toString());
		}
		int old_dp = 0;
		if(RsyncSyncGoods.isInteger(oldData.get("dp").toString())) {
			old_dp = Integer.parseInt(oldData.get("dp").toString());
		}
		int old_hg = 0;
		if(RsyncSyncGoods.isInteger(oldData.get("hg").toString())) {
			old_hg = Integer.parseInt(oldData.get("hg").toString());
		}
		int max = (old_wd > old_dp) ? old_wd : old_dp;
		max = (max > old_hg) ? max : old_hg;
		int new_wd = 0;
		int new_dp = 0;
		int new_hg = 0;
		if(RsyncSyncGoods.isInteger(wd)) {
			new_wd = Integer.parseInt(wd);
		}
		if(RsyncSyncGoods.isInteger(dp)) {
			new_dp = Integer.parseInt(dp);
		}
		if(RsyncSyncGoods.isInteger(hg)) {
			new_hg = Integer.parseInt(hg);
		}
		if(max < new_wd || max < new_dp || max < new_hg) {
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		
		String wd = "4";
		String dp = "6";
		String hg = "9";
		MDataMap oldData = new MDataMap("wd","12","dp","3","hg","5");
		boolean flag = RsyncSyncGoods.isReplaceWdDpHg(wd, dp, hg, oldData);
		System.out.println(flag);
	}
}
