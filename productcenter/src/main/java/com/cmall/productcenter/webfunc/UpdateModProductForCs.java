package com.cmall.productcenter.webfunc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.UcSellercategoryProductRelation;
import com.cmall.productcenter.service.ProductService;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 惠家友后台（商户商品菜单专用）修改商品
 *
 * @author ligj
 * @version 1.0
 * 
 */
public class UpdateModProductForCs extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		try {
			if (mResult.upFlagTrue()) {
				// 添加考拉商品资质审核过滤判断
				ProductService pService = new ProductService();
				MDataMap mSubDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
				PcProductinfo pp = new PcProductinfo();
				pp = new JsonHelper<PcProductinfo>().StringToObj(mSubDataMap.get("json"), pp);
				// 更新商品保障authorityLogoUid by wangmeng
				String authorityLogoUid = mDataMap.get("zw_f_authority_logo");
				if (StringUtils.isEmpty(authorityLogoUid)) {
					mResult.setResultCode(-1);
					mResult.setResultMessage("商品保障不得为空");
					return mResult;
				} else {
					String productCode = pp.getPcProductinfoExt().getProductCode();
					MDataMap updateMDataMap = new MDataMap();
					updateMDataMap.put("authority_logo_uid", authorityLogoUid);
					updateMDataMap.put("product_code", productCode);
					DbUp.upTable("pc_product_authority_logo").dataUpdate(updateMDataMap, "authority_logo_uid",
							"product_code");
				}
				// 添加考拉资质审核过滤
				String qcc = mDataMap.get("zw_f_qualification_category_code");
				if (StringUtils.isBlank(qcc) && !"SF03WYKLPT".equals(pp.getSmallSellerCode())
						&& !"SF031JDSC".equals(pp.getSmallSellerCode())) {
					mResult.inErrorMessage(941901149); // 资质品类不得为空！
					return mResult;
				}

				StringBuffer error = new StringBuffer();
				String sc = pp.getSellerCode();// 商品所属店铺编号
				MUserInfo uc = UserFactory.INSTANCE.create();// 当前用户所属店铺编号
				if (uc == null) {
					mResult.inErrorMessage(941901065, bInfo(941901064));
				} else if (sc != null && !"".equals(sc)) {
					PcProductinfo pro = pService.getProduct(pp.getProductCode());
					pp.getProductSkuInfoList().clear();
					pp.setProductSkuInfoList(pro.getProductSkuInfoList());

					// 商品虚类 ------>惠家有后台商户商品菜单修改商品的特殊逻辑（不加此逻辑会还原商品的虚类，所以要实时从数据库里面读取）
					List<MDataMap> categoryProductRelationMap = DbUp.upTable("uc_sellercategory_product_relation")
							.queryAll("", "", "product_code='" + pp.getProductCode() + "'", null);
					List<UcSellercategoryProductRelation> categoryProductRelationList = new ArrayList<UcSellercategoryProductRelation>();
					SerializeSupport<UcSellercategoryProductRelation> ss = new SerializeSupport<UcSellercategoryProductRelation>();
					for (MDataMap usprMap : categoryProductRelationMap) {
						UcSellercategoryProductRelation categoryProductRelation = new UcSellercategoryProductRelation();
						ss.serialize(usprMap, categoryProductRelation);
						categoryProductRelationList.add(categoryProductRelation);
					}
					pp.getUsprList().clear();
					pp.setUsprList(categoryProductRelationList);

					/**
					 * 查询是否有法务待审批流程，如果存在提示正在审批中
					 */
					MDataMap flowIsExists = DbUp.upTable("sc_flow_main").one("outer_code", pp.getProductCode(),
							"flow_type", "449717230016", "flow_isend", "0", "current_status", "4497172300160011");
					if (flowIsExists != null) {
						mResult.setResultCode(-1);
						mResult.setResultMessage("商品修改正在审批中");
						return mResult;
					}

					// 资质品类判断
					if (StringUtils.isNotBlank(pp.getQualificationCategoryCode())) {
						MDataMap map = new MDataMap();
						map.put("small_seller_code", pp.getSmallSellerCode());
						List<MDataMap> userList = DbUp.upTable("uc_seller_info_extend").queryAll("uc_seller_type ", "",
								"small_seller_code=:small_seller_code", map);
						if (userList != null && userList.size() != 0) {
							String ustype = userList.get(0).get("uc_seller_type"); // 非跨境类商户进行资质效验 - Yangcl
							if (StringUtils.isNotBlank(ustype) && (ustype.equals("4497478100050001")
									|| ustype.equals("4497478100050004") || ustype.equals("4497478100050000"))) {
								map.put("brand_code", pp.getBrandCode());
								map.put("category_code", pp.getQualificationCategoryCode());
								List<MDataMap> mDataMapList = DbUp.upTable("pc_seller_qualification").queryAll(
										"qualification_name,DATEDIFF(SYSDATE(),end_time) expired", "", "", map);
								if (mDataMapList.isEmpty()) {
									mResult.inErrorMessage(941901147);
									return mResult;
								}

								List<String> vs = new ArrayList<String>();
								for (MDataMap mData : mDataMapList) {
									if (NumberUtils.toInt(mData.get("expired")) > 0) {
										vs.add(mData.get("qualification_name"));
									}
								}

								if (!vs.isEmpty()) {
									mResult.inErrorMessage(941901148, StringUtils.join(vs, "、"));
									return mResult;
								}
							}
						}
					}

					pService.updateProduct(pp, error);
					if (StringUtils.isEmpty(error.toString())) {
						mResult.setResultMessage(bInfo(941901097));
						/**
						 * =================修改商品后提交到商品审批流程，到达节点法务待审批 start==============
						 */
						// 加入审批的流程
						// ScFlowMain flow = new ScFlowMain();
						// flow.setCreator(uc.getUserCode());
						// flow.setCurrentStatus("4497172300160011");
						// String title = "修改商品"+pp.getProductCode()+"信息，待法务审批";
						// flow.setFlowTitle(pp.getProductCode());
						// // flow.setFlowType("449717230011");
						// // 修改添加商品跳转节点 2016-06-24 zhy
						// flow.setFlowType("449717230016");
						// String preViewUrl = bConfig("productcenter.PreviewCheckProductUrl") +
						// pp.getProductCode() + "_1";
						// flow.setFlowUrl(preViewUrl);
						// flow.setCreator(uc.getUserCode());
						// flow.setOuterCode(pp.getProductCode());
						// flow.setFlowRemark(title);
						// FlowService flowService = new FlowService();
						// flowService.CreateFlow(flow);
						/**
						 * ================= end ==============
						 */

						if (StringUtils.equals(mSubDataMap.get("savetype"), "submit")) {
							/**
							 * 商品上架
							 */
							MDataMap prodMap = DbUp.upTable("pc_productinfo").oneWhere("uid,product_status", "", "",
									"product_code", pp.getProductCode());
							// 查询是否有可售SKU
							int saleYCount = DbUp.upTable("pc_skuinfo").dataCount(
									"product_code = :product_code and sale_yn = 'Y'",
									new MDataMap("product_code", pp.getProductCode()));
							// 有可售SKU的情况下商品才会上架
							if (prodMap != null && StringUtils.isNotBlank(prodMap.get("uid")) && saleYCount > 0) {
								// 商品上架
								String flowBussinessUid = prodMap.get("uid"); // 商品Uid
								String toStatus = "4497153900060002"; // 更改到的状态
								String flowType = "449715390006"; // 流程类型449715390006：商家后台商品状态
								String remark = "一般信息修改，提交上架";
								String userCode = UserFactory.INSTANCE.create().getUserCode();

								FlowBussinessService fs = new FlowBussinessService();
								fs.ChangeFlow(flowBussinessUid, flowType, prodMap.get("product_status"), toStatus,
										userCode, remark, new MDataMap());
							}
						}

					} else {
						mResult.inErrorMessage(941901098, error.toString());
					}
				} else {
					mResult.inErrorMessage(941901099);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(941901099);
		}
		return mResult;
	}
}
