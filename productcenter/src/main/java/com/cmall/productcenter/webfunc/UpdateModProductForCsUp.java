package com.cmall.productcenter.webfunc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.UcSellercategoryProductRelation;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.load.LoadProductInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelProductQuery;
import com.srnpr.xmassystem.top.LoadTop;
import com.srnpr.xmassystem.up.XmasKv;
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
 * 惠家友后台（已发货商户商品菜单专用）修改商品
 *
 * @author ligj
 * @version 1.0
 * 
 */
public class UpdateModProductForCsUp extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		String sProductCode = "";
		try {
			if (mResult.upFlagTrue()) {
				// 更新商品保障authorityLogoUid by wangmeng
				String authorityLogoUid = mDataMap.get("zw_f_authority_logo");
				if (StringUtils.isEmpty(authorityLogoUid)) {
					mResult.setResultCode(-1);
					mResult.setResultMessage("商品保障不得为空");
					return mResult;
				} else {
					String productCode = mDataMap.get("zw_f_uid");
					sProductCode = productCode;
					MDataMap updateMDataMap = new MDataMap();
					updateMDataMap.put("authority_logo_uid", authorityLogoUid);
					updateMDataMap.put("product_code", productCode);
					DbUp.upTable("pc_product_authority_logo").dataUpdate(updateMDataMap, "authority_logo_uid",
							"product_code");
				}
				// 添加考拉商品资质审核过滤判断
				ProductService pService = new ProductService();
				MDataMap mSubDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
				PcProductinfo pp = new PcProductinfo();
				pp = new JsonHelper<PcProductinfo>().StringToObj(mSubDataMap.get("json"), pp);

				// 添加考拉资质审核过滤
				String qcc = mDataMap.get("zw_f_qualification_category_code");
				if (StringUtils.isBlank(qcc) && !"SF03WYKLPT".equals(pp.getSmallSellerCode())) {
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
					pService.updateProduct(pp, error);
				} else {
					mResult.inErrorMessage(941901099);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(941901099);
		}
		//刷新主库缓存
		if(StringUtils.isNotEmpty(sProductCode)) {
			XmasKv.upFactory(EKvSchema.Product).del(sProductCode);
			LoadProductInfo load = new LoadProductInfo();
			PlusModelProductQuery tQuery = new PlusModelProductQuery(sProductCode);
			load.upInfoByCode(tQuery);
		}
		return mResult;
	}
}
