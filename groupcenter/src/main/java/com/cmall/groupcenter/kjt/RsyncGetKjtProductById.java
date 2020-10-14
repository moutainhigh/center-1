package com.cmall.groupcenter.kjt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.homehas.model.RsyncDateCheck;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.groupcenter.kjt.config.RsyncConfigGetKjtProductById;
import com.cmall.groupcenter.kjt.model.RsyncModelGetKjtProduct;
import com.cmall.groupcenter.kjt.request.RsyncRequestGetKjtProductById;
import com.cmall.groupcenter.kjt.response.RsyncResponseGetKjtProductById;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.productcenter.common.SkuCommon;
import com.cmall.productcenter.model.PcProductdescription;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.PcProductinfoExt;
import com.cmall.productcenter.model.ProductChangeFlag;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 根据商品编号获取商品信息
 * 
 * @author xiegj
 * 
 */
public class RsyncGetKjtProductById
		extends
		RsyncKjt<RsyncConfigGetKjtProductById, RsyncRequestGetKjtProductById, RsyncResponseGetKjtProductById> {

	final static RsyncConfigGetKjtProductById CONFIG_GET_TV_BY_ID = new RsyncConfigGetKjtProductById();
	private static String ProductHead = "6016";
	private static String SKUHead = "6019";
	private static String ProductFlowHead = "PF";

	public RsyncConfigGetKjtProductById upConfig() {
		return CONFIG_GET_TV_BY_ID;
	}

	private RsyncRequestGetKjtProductById RsyncRequestGetKjtProductById = new RsyncRequestGetKjtProductById();
	public RsyncRequestGetKjtProductById upRsyncRequest() {

		return RsyncRequestGetKjtProductById;
	}

	public RsyncResult doProcess(RsyncRequestGetKjtProductById tRequest,
			RsyncResponseGetKjtProductById tResponse) {

		RsyncResult result = new RsyncResult();

		// 定义成功的数量合计
		int iSuccessSum = 0;

		if (result.upFlagTrue()) {
			if (tResponse != null && tResponse.getData() != null) {
				result.setProcessNum(tResponse.getData().getProductList().size());
			} else {
				result.setProcessNum(0);

			}

		}

		// 开始循环处理结果数据
		if (result.upFlagTrue()) {

			// 判断有需要处理的数据才开始处理
			if (result.getProcessNum() > 0) {

				// 设置预期处理数量
				result.setProcessNum(tResponse.getData().getProductList().size());

				for (RsyncModelGetKjtProduct info : tResponse.getData().getProductList()) {
					MWebResult mResult = saveProductData(info);

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

				result.setProcessData(bInfo(918501102, result.getProcessNum(),
						iSuccessSum, result.getProcessNum() - iSuccessSum));

			}

		}

		// 如果操作都成功 则设置状态保存数据为同步结束时间 以方便下一轮调用
		if (result.upFlagTrue()) {
			// 设置处理成功数量
			result.setSuccessNum(iSuccessSum);
			// 特殊处理 由于时间格式不对 状态数据需要切换掉
			RsyncDateCheck rsyncDateCheck = upDateCheck(upConfig());
			result.setStatusData(rsyncDateCheck.getEndDate());
		}

		return result;

	}

	private MWebResult saveProductData(RsyncModelGetKjtProduct info) {
		MWebResult result = new MWebResult();
		try {
			//处理info数据逻辑在此写
			PcProductinfo productinfo=new PcProductinfo();
			String productId = info.getProductID();
			List<Map<String, Object>> list= DbUp.upTable("pc_productinfo").dataSqlList("SELECT p.product_code,p.product_shortname,p.max_sell_price,p.min_sell_price,p.product_name,p.cost_price,p.tax_rate,d.description_info,p.update_time FROM pc_productinfo p LEFT JOIN pc_productdescription d on p.product_code=d.product_code where p.product_code_old=:product_code_old and p.seller_code=:seller_code ", new MDataMap("product_code_old",productId,"seller_code",MemberConst.MANAGE_CODE_HOMEHAS));
			if(list==null||list.size()<1){  //若果不存在，就添加
				setNewProductInfo(productinfo, info);//设置商品实体
				ProductService productService=BeansHelper.upBean("bean_com_cmall_productcenter_service_ProductService");
				StringBuffer error=new StringBuffer();
				com.cmall.productcenter.model.PcProductflow pcProdcutflow = new com.cmall.productcenter.model.PcProductflow();
				pcProdcutflow.setFlowCode(WebHelper.upCode(ProductFlowHead));
				pcProdcutflow.setFlowStatus(SkuCommon.ProAddOr);
				productinfo.setPcProdcutflow(pcProdcutflow);
				int resultCode=productService.AddProductTx(productinfo, error,"");
				
				PlusHelperNotice.onChangeProductInfo(productinfo.getProductCode());
				//触发消息队列
				ProductJmsSupport pjs = new ProductJmsSupport();
				pjs.onChangeForProductChangeAll(productinfo.getProductCode());
				
				result.setResultCode(resultCode);
				result.setResultMessage(error.toString());
			}else {
				productinfo = new ProductService().getProduct(list.get(0).get("product_code").toString());
				
				String now=DateUtil.getSysDateTimeString();
				String cost = list.get(0).get("cost_price").toString();
				String taxRate = list.get(0).get("tax_rate").toString();
				
				String rsyncCost = info.getPrice().setScale(2,BigDecimal.ROUND_HALF_UP).toString();//同步过来的商品价格
				String rsyncTaxRate = info.getProductEntryInfo().getTariffRate().toString();//同步过来的商品价格
				
				boolean downFlag = false;		//标注商品是否下架
				String mailContent = "";
				String mailTitle = "";
				//修改商品表
				if(!cost.equals(rsyncCost)){//价格变化时商品下架
					productinfo.setCostPrice(new BigDecimal(rsyncCost));
					try {
						productinfo.getProductSkuInfoList().get(0).setCostPrice(new BigDecimal(rsyncCost));
					} catch (Exception e) {
						e.printStackTrace();
					}
					mailTitle="同步跨境通商品成本价格变更下架";
					mailContent="跨境通商品的成本价格由"+cost+"变更为"+rsyncCost+",商品下架";
					downFlag = true;
				}
				if (!taxRate.equals(rsyncTaxRate)) {
					productinfo.setTaxRate(info.getProductEntryInfo().getTariffRate());//更新税率
					if (StringUtils.isNotBlank(mailTitle)) {
						mailTitle="同步跨境通商品税率变更下架";
						mailContent="跨境通商品的税率由"+taxRate+"变更为"+rsyncTaxRate+",商品下架";
					}else{
						mailTitle="同步跨境通商品成本价格与税率变更下架";
						mailContent="跨境通商品的成本价格由"+cost+"变更为"+rsyncCost+",税率由"+taxRate+"变更为"+rsyncTaxRate+",商品下架";
					}
					downFlag = true;
				}
				if (downFlag) {
					//下架逻辑
					if ("4497153900060002".equals(productinfo.getProductStatus())) {
						String flowBussinessUid=productinfo.getUid();
						String fromStatus= "4497153900060002";
						String toStatus="4497153900060003";
						String flowType = "449715390006";
						String userCode = "jobsystem";
						String remark=mailContent;
						RootResult ret =
						new FlowBussinessService().ChangeFlow(flowBussinessUid, flowType, fromStatus, toStatus, userCode, remark, new MDataMap());
						if (ret.getResultCode() == 1) {
							//发送邮件
							String receives[]= bConfig("groupcenter.kjt_dowProduct_sendMail_receives").split(",");
							for (String receive : receives) {
								if(StringUtils.isNotBlank(receive)){
									MailSupport.INSTANCE.sendMail(receive, mailTitle,mailContent);
								}
							}
						}
						 
						productinfo.setProductStatus("4497153900060003");//设置为下架
						productinfo.setFlagSale(0);
					}
				}
				
				productinfo.setUpdateTime(now);
				productinfo.getPcProductinfoExt().setProductTradeType(String.valueOf(info.getProductTradeType()));
				productinfo.getPcProductinfoExt().setProductStoreType(String.valueOf(info.getProductEntryInfo().getProductStoreType()));
				productinfo.getPcProductinfoExt().setKjtSellerCode(info.getStoreSysNo());
				new ProductService().UpdateProductTx(productinfo, new StringBuffer(), "jobsystem",  new ProductChangeFlag());
				
				PlusHelperNotice.onChangeProductInfo(productinfo.getProductCode());
				//触发消息队列
				ProductJmsSupport pjs = new ProductJmsSupport();
				pjs.onChangeForProductChangeAll(productinfo.getProductCode());
			}
		} catch (Exception e) {
			result.inErrorMessage(918519034, info.toString());
			e.printStackTrace();
		}

		return result;
	}
	
	/** 
	* @Description:把商品信息赋值到待插入实体
	* @param productinfo 上插入商品实体
	* @param info 商品信息
	* @author 张海生
	* @date 2015-7-14 下午3:48:00
	* @throws 
	*/
	public void setNewProductInfo(PcProductinfo productinfo, RsyncModelGetKjtProduct info){
		// 根据商户编码查询商户类型 2016-11-14 zhy
		MDataMap seller = DbUp.upTable("uc_seller_info_extend").oneWhere("uc_seller_type", "", "", "small_seller_code",
				bConfig("familyhas.seller_code_KJT"));
		String productName = info.getProductName().replaceAll("</?[^>]+>", "");//过滤html标签
		productinfo.setProductCode(WebHelper.upCode(RsyncGetKjtProductById.ProductHead));
		String uid=UUID.randomUUID().toString().replace("-", "");
		productinfo.setUid(uid);
		productinfo.setProductCodeOld(info.getProductID());
		productinfo.setProductShortname(productName);
		productinfo.setBrandCode(String.valueOf(info.getBrandID()));
		productinfo.setBrandName(info.getBrandName());
		productinfo.setProdutName(productName);
		productinfo.setMaxSellPrice(info.getPrice());
		productinfo.setMinSellPrice(info.getPrice());
		productinfo.setCostPrice(info.getPrice());
		productinfo.setMainPicUrl(info.getDefaultImage());	
		productinfo.setMarketPrice(info.getPrice());
		String manageCode = MemberConst.MANAGE_CODE_HOMEHAS;
		productinfo.setSellerCode(manageCode);
		productinfo.setSmallSellerCode(bConfig("familyhas.seller_code_KJT"));
		productinfo.setProductStatus("4497153900060003");//商品下架
		productinfo.setValidate_flag("Y");//新增字段，是否是虚拟商品
		productinfo.setTaxRate(info.getProductEntryInfo().getTariffRate());
		productinfo.setProductWeight(new BigDecimal(info.getWeight()));
		productinfo.setTransportTemplate("0");//运费模板默认为卖家包邮
		
		PcProductdescription productdescription=new PcProductdescription();
		productdescription.setUid(uid);
		productdescription.setProductCode(productinfo.getProductCode());
		String descriptionInfo = info.getProductDescLong().replaceAll("(<script).*(script>)", "");//过滤js
		productdescription.setDescriptionInfo(descriptionInfo);
		productdescription.setDescriptionPic(info.getProductPhotoDesc());
		String keyWord = "";
		if(StringUtils.isNotEmpty(info.getKeyWords())){
			String regEx = "[' ']+"; // 一个或多个空格  
			keyWord = info.getKeyWords().trim().replaceAll(regEx, ",");
		}
		productdescription.setKeyword(keyWord);
		productinfo.setDescription(productdescription);
		
		
		ProductSkuInfo productSkuInfo=new ProductSkuInfo();
		productSkuInfo.setSkuCode(WebHelper.upCode(RsyncGetKjtProductById.SKUHead));
		productSkuInfo.setProductCode(info.getProductID());
		productSkuInfo.setSellPrice(info.getPrice());
		productSkuInfo.setMarketPrice(info.getPrice());
		productSkuInfo.setCostPrice(info.getPrice());//设置sku的成本价
//		productSkuInfo.setSkuPicUrl(goods.getImages());
		productSkuInfo.setSkuName(productName);//过滤html标签
		productSkuInfo.setSellProductcode(info.getProductID());//设置外部商品id
//		productSkuInfo.setSellProductcode(MemberConst.MANAGE_CODE_HOMEHAS);
		productSkuInfo.setSaleYn("Y");//是否可卖为可买
		productSkuInfo.setFlagEnable("1");//是否可用为可用
		productSkuInfo.setSellerCode(manageCode);
		productSkuInfo.setSkuKey("color_id=0&style_id=0");
		productSkuInfo.setSkuValue("颜色属性=共同&规格属性=共同");
		
		List<ProductSkuInfo> productSkuInfoList=new ArrayList<ProductSkuInfo>(1);
		productSkuInfoList.add(productSkuInfo);
		productinfo.setProductSkuInfoList(productSkuInfoList);
		PcProductinfoExt pcProductinfoExt = new PcProductinfoExt();//设置扩展信息
		pcProductinfoExt.setProductCodeOld(info.getProductID());
		pcProductinfoExt.setProductCode(productinfo.getProductCode());
		pcProductinfoExt.setPrchType("10");
		pcProductinfoExt.setDlrId(String.valueOf(info.getVendorID()));
		pcProductinfoExt.setDlrNm(info.getVendorName());
		pcProductinfoExt.setValidateFlag("Y");
		pcProductinfoExt.setProductTradeType(String.valueOf(info.getProductTradeType()));
		pcProductinfoExt.setProductStoreType(String.valueOf(info.getProductEntryInfo().getProductStoreType()));
		pcProductinfoExt.setPoffer("jobsystem");
		pcProductinfoExt.setSettlementType("4497471600110003");//服务费结算
		pcProductinfoExt.setKjtSellerCode(info.getStoreSysNo());//跨境通店铺编号
		productinfo.setPcProductinfoExt(pcProductinfoExt);
	}
	/** 
	* @Description:插入商品历史流水信息
	* @param productinfo 商品信息实体
	* @param productCode 
	* @author 张海生
	* @date 2015-8-7 下午4:18:28
	* @return void 
	* @throws 
	*/
//	public void saveHistoryFlow(PcProductinfo productinfo,String productCode){
//		//设置商品历史流水信息
//				MUserInfo userInfo = null;
//				String userCode = "";
//				if (UserFactory.INSTANCE != null) {
//					try {
//						userInfo = UserFactory.INSTANCE.create();
//					} catch (Exception e) {
//						// TODO: handle exception
//					}
//					if (userInfo != null) {
//						userCode = userInfo.getUserCode();
//					}
//				}
//				String createTime = DateUtil.getSysDateTimeString();
//				com.cmall.dborm.txmodel.PcProductflow ppf = new PcProductflow();
//				
//				ppf.setCreateTime(createTime);
//				ppf.setCreator(userCode);
//				ppf.setFlowCode(WebHelper.upCode(ProductFlowHead));
//				ppf.setFlowStatus(SkuCommon.ProUpaOr);
//				ppf.setProductCode(productCode);
//				ppf.setUid(UUID.randomUUID().toString().replace("-", ""));
//				ppf.setUpdateTime(createTime);
//				ppf.setUpdator(userCode);
//				
//				JsonHelper<PcProductinfo> pHelper=new JsonHelper<PcProductinfo>();
//				ppf.setProductJson(pHelper.ObjToString(productinfo));
//				com.cmall.dborm.txmapper.PcProductflowMapper ppfm = 
//						BeansHelper.upBean("bean_com_cmall_dborm_txmapper_PcProductflowMapper");
//				ppfm.insertSelective(ppf);
//	}
	public RsyncResponseGetKjtProductById upResponseObject() {

		return new RsyncResponseGetKjtProductById();
	}
}
