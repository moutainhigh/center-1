package com.cmall.productcenter.webfunc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.service.FlowService;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 编辑新品提交
 *
 * @author ligj
 * @version 1.0
 * 
 */
public class UpdateEditNewProduct extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		try {
			if (mResult.upFlagTrue()) {

				ProductService pService = new ProductService();
				MDataMap mSubDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
				PcProductinfo pp = new PcProductinfo();
				pp = new JsonHelper<PcProductinfo>().StringToObj(mSubDataMap.get("json"), pp);
				// 更新商品保障authorityLogoUid by wangmeng
				// System.out.println(JSON.toJSON(mDataMap));
				String authorityLogoUid = mDataMap.get("zw_f_authority_logo");
				if (StringUtils.isEmpty(authorityLogoUid)) {
					mResult.setResultCode(-1);
					mResult.setResultMessage("商品保障不得为空");
					return mResult;
				} else {
					String productCode = pp.getProductCode();
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
				MDataMap productInfo = DbUp.upTable("pc_productinfo").oneWhere("product_status", "", "", "product_code",
						pp.getProductCode());
				if ("".equals(productInfo) || null == productInfo) {
					mResult.inErrorMessage(941901128);
					return mResult;
				} else if (!"4497153900060001".equals(productInfo.get("product_status"))) {// 如果该商品状态不是待上架
					mResult.inErrorMessage(941901129);
					return mResult;
				}

				// 资质品类判断
				if (StringUtils.equals(mSubDataMap.get("savetype"), "submit")
						&& StringUtils.isNotBlank(pp.getQualificationCategoryCode())) {
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
							List<MDataMap> mDataMapList = DbUp.upTable("pc_seller_qualification")
									.queryAll("qualification_name,DATEDIFF(SYSDATE(),end_time) expired", "", "", map);
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

				if (uc == null) {
					mResult.inErrorMessage(941901065, bInfo(941901064));
				} else if (sc != null && !"".equals(sc)) {
					boolean flag = false;
					pp.setSellerCode(AppConst.MANAGE_CODE_HOMEHAS);
					List<String> skuKey = new ArrayList<String>();
					List<String> skuKeyValue = new ArrayList<String>();
					if (pp.getProductSkuInfoList() != null) {
						for (int i = 0; i < pp.getProductSkuInfoList().size(); i++) {
							ProductSkuInfo sku = pp.getProductSkuInfoList().get(i);
							sku.setProductCode(pp.getProductCode());
							sku.setSkuCode(WebHelper.upCode(ProductService.SKUHead)); // 添加商品时候需要自动生成skucode
							skuKey.add(sku.getSkuKey());
							skuKeyValue.add(sku.getSkuKeyvalue());
						}
					}
					// 检查sku规格是否存在重复
					if (1 == new ProductService().checkRepeatSku(skuKey, skuKeyValue)) {
						mResult.inErrorMessage(941901124);
						return mResult;
					}
					pp.setProductStatus(bConfig("productcenter.OnProductStatus"));
					// List<MDataMap> skuCodeList =
					// DbUp.upTable("pc_skuinfo").queryAll("", "",
					// "product_code=:product_code", new
					// MDataMap("product_code",pp.getProductCode()));
					// if (null != skuCodeList) {
					// List<ProductSkuInfo> skuInfoList =
					// pp.getProductSkuInfoList();
					// SerializeSupport<ProductSkuInfo> ss = new
					// SerializeSupport<ProductSkuInfo>();
					// for (int i = 0; i < skuInfoList.size(); i++) {
					// skuInfoList.get(i).setSkuCode("DSF"+skuInfoList.get(i).getSkuCode());
					// //在skucode前加上DSF，为了标志商品新增时不会自动生成商品编号，存库前需要去掉次标志
					// }
					// for (MDataMap skuMap : skuCodeList) {
					// int flag = 0;
					// for (int i = 0; i < skuInfoList.size(); i++) {
					// ProductSkuInfo skuInfo = skuInfoList.get(i);
					// if (skuInfo.getSkuKey().equals(skuMap.get("sku_key"))) {
					// flag = 1;
					// pp.getProductSkuInfoList().get(i).setSkuCode(skuMap.get("sku_code"));
					// }
					// }
					// if (flag == 0) {
					// ProductSkuInfo skuInfo = new ProductSkuInfo();
					// ss.serialize(skuMap, skuInfo);
					// skuInfo.setSaleYn("N");
					// pp.getProductSkuInfoList().add(skuInfo);
					// }
					// }
					// }
					pp.getProductSkuInfoList().clear();
					// 取得商品的sku信息
					List<MDataMap> productSkuInfoListMap = DbUp.upTable("pc_skuinfo").queryAll("", "",
							"product_code=:product_code", new MDataMap("product_code", pp.getProductCode()));
					List<ProductSkuInfo> productSkuInfoList = null;
					if (productSkuInfoListMap != null) {
						int size = productSkuInfoListMap.size();
						productSkuInfoList = new ArrayList<ProductSkuInfo>();
						SerializeSupport<ProductSkuInfo> ss = new SerializeSupport<ProductSkuInfo>();

						for (int i = 0; i < size; i++) {
							ProductSkuInfo pic = new ProductSkuInfo();
							ss.serialize(productSkuInfoListMap.get(i), pic);
							pic.setSkuValue(productSkuInfoListMap.get(i).get("sku_keyvalue"));
							productSkuInfoList.add(pic);
							// 存在可售的sku即可上架
							if ("Y".equals(pic.getSaleYn())) {
								flag = true;
							}
						}
					}
					pp.setProductSkuInfoList(productSkuInfoList);

					// 取得商品的扩展信息
					MDataMap productExtMap = DbUp.upTable("pc_productinfo_ext").oneWhere("md_id,md_nm", "", "",
							"product_code", pp.getProductCode());
					if (null != productExtMap) {
						pp.getPcProductinfoExt().setMdId(productExtMap.get("md_id"));
						pp.getPcProductinfoExt().setMdNm(productExtMap.get("md_nm"));
					}
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
					// 开始更新商品，添加是否为三方商品，从而进行商品货号的同步
					this.reAssign(pp);
					pService.updateProduct(pp, error);
					/**
					 * 判断网站编辑保存还是提交修改
					 */
					if (StringUtils.isEmpty(error.toString())) {
						if (StringUtils.equals(mSubDataMap.get("savetype"), "submit")) {
							/**
							 * ===================添加审批跳转 start 2017-01-20 zhy =====================
							 */
							// 判断三方商品是否仍处于上架状态
							if (this.checkSanFangProductState(pp.getSmallSellerCode(), flag)) {
								// 查询是否存在审批流程
								String flowMainSql = "select flow_code,current_status from systemcenter.sc_flow_main where flow_type='449717230016' AND current_status='4497172300160013' and outer_code=:outer_code";
								Map<String, Object> flowMain = DbUp.upTable("sc_flow_main").dataSqlOne(flowMainSql,
										new MDataMap("outer_code", pp.getProductCode()));
								if (flowMain != null) {
									MDataMap _flowMain = new MDataMap(flowMain);
									/**
									 * 根据审批类型和当前审批状态获取下一审批状态
									 */
									Map<String, Object> statusChange = DbUp.upTable("sc_flow_statuschange").dataSqlOne(
											"select to_status from systemcenter.sc_flow_statuschange where from_status=:from_status and flow_type='449717230016' order by zid desc",
											new MDataMap("from_status", _flowMain.get("current_status")));
									String flowCode = _flowMain.get("flow_code");
									String flowFromStatus = _flowMain.get("current_status");
									String flowToStatus = statusChange.get("to_status").toString();
									String flowUserCode = uc.getUserCode();
									String flowRoleCode = uc.getUserRole();
									// String flowRemark = "编辑待审批提交到法务待审批";
									String flowRemark = "编辑待审批提交到审核通过";
									MDataMap flowMSubMap = new MDataMap("product_code", pp.getProductCode());
									FlowService flow = new FlowService();
									flow.ChangeFlow(flowCode, flowFromStatus, flowToStatus, flowUserCode, flowRoleCode,
											flowRemark, flowMSubMap);
								}
							} else {
								mResult.setResultCode(-1);
								mResult.setResultMessage("商品无可售的sku！");
								return mResult;
							}
							/**
							 * ===================添加审批跳转 end =====================
							 */
							/**
							 * 法务审批通过后商品上架
							 */
							// MDataMap uidMap =
							// DbUp.upTable("pc_productinfo").oneWhere("uid", "",
							// "", "product_code",
							// pp.getProductCode());
							// if (uidMap != null &&
							// StringUtils.isNotBlank(uidMap.get("uid"))) {
							// // 商品上架
							// String flowBussinessUid = uidMap.get("uid"); // 商品Uid
							// String toStatus = "4497153900060002"; // 更改到的状态
							// String flowType = "449715390006"; //
							// 流程类型449715390006：商家后台商品状态
							// String remark = "编辑新品商品上架";
							// // 更新状态
							// MDataMap updMap = new MDataMap();
							// updMap.put("uid", flowBussinessUid);
							// updMap.put("flag_sale", "0");
							// updMap.put("update_time",
							// DateUtil.getSysDateTimeString());
							// updMap.put("product_status", toStatus);
							// int retcode =
							// DbUp.upTable("pc_productinfo").dataUpdate(updMap,
							// "flag_sale,update_time,product_status", "uid");
							// if (1 == retcode) {
							// MDataMap insertDatamap = new MDataMap();
							// insertDatamap.put("uid",
							// UUID.randomUUID().toString().replace("-", ""));
							// insertDatamap.put("flow_code", flowBussinessUid);
							// insertDatamap.put("flow_type", flowType);
							// insertDatamap.put("creator", uc.getUserCode());
							// insertDatamap.put("create_time",
							// DateUtil.getSysDateTimeString());
							// insertDatamap.put("flow_remark", remark);
							// insertDatamap.put("current_status", toStatus);
							// DbUp.upTable("sc_flow_bussiness_history").dataInsert(insertDatamap);
							//
							// PlusHelperNotice.onChangeProductInfo(pp.getProductCode());
							// // 触发消息队列
							// ProductJmsSupport pjs = new ProductJmsSupport();
							// pjs.onChangeForProductChangeAll(pp.getProductCode());
							//
							//// String[]roles = uc.getUserRole().replace("|",
							// ",").split(",");
							//// if
							// (Arrays.asList(roles).contains("4677031800050004")) {
							// }
							// mResult.setResultMessage(bInfo(941901102));
							// }
						}
					} else {
						mResult.inErrorMessage(941901046, error.toString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(941901099);
		}
		return mResult;
	}

	private boolean checkSanFangProductState(String smallSellerCode, boolean flag) {
		// 三方上架否的判断,其他商品按原逻辑不处理
		if ("SF031JDSC".equals(smallSellerCode) || "SF03WYKLPT".equals(smallSellerCode)) {
			if (flag) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	private void reAssign(PcProductinfo pp) {
		// TODO Auto-generated method stub
		// 对象中没有sell_productcode值，会把同步过来的三方商品编码给冲掉，故在冲掉前取出来在赋予对象
		if ("SF03WYKLPT".equals(pp.getSmallSellerCode()) || "SF031JDSC".equals(pp.getSmallSellerCode())) {
			Map<String, Object> map = DbUp.upTable("pc_productinfo").dataSqlOne(
					"select sell_productcode from pc_productinfo where product_code=:product_code",
					new MDataMap("product_code", pp.getProductCode()));
			pp.setSellProductcode(map.get("sell_productcode").toString());
		}
	}
}
