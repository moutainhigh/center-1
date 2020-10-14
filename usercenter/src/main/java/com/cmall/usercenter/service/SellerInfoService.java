package com.cmall.usercenter.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.model.OcActivity;
import com.cmall.ordercenter.service.ActivityService;
import com.cmall.productcenter.model.Category;
import com.cmall.productcenter.service.CategoryService;
import com.cmall.productcenter.service.ProductService;
import com.cmall.usercenter.model.CollectionSellerModel;
import com.cmall.usercenter.model.SellerInfoExtend;
import com.cmall.usercenter.model.UcSellerInfo;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * 项目名称：usercenter 类名称：SellerInfoService 类描述： 创建人：yanzj 创建时间：2013-11-1 下午5:04:51
 * 修改人：yanzj 修改时间：2013-11-1 下午5:04:51 修改备注：
 * 
 * @version
 * 
 */
public class SellerInfoService extends BaseClass {

	/**
	 * 修改卖家状态
	 * 
	 * @param sSellerUid
	 *            卖家UID
	 * @param sToStatus
	 *            状态编号
	 * @param sUserCode
	 *            操作人
	 * @return
	 */
	public MWebResult changeStatusByUid(String sSellerUid, String sToStatus,
			String sUserCode) {

		MWebResult mResult = new MWebResult();

		if (StringUtils.isNotEmpty(sSellerUid)) {

			MDataMap mSellerMap = DbUp.upTable("uc_sellerinfo").one("uid",
					sSellerUid);

			if (mSellerMap != null) {
				mSellerMap.put("seller_status", sToStatus);

				DbUp.upTable("uc_sellerinfo").dataUpdate(mSellerMap, "seller_status",
						"uid");

				MDataMap mLogMap = new MDataMap();
				mLogMap.put("code", mSellerMap.get("seller_code"));
				mLogMap.put("info", bInfo(954901050, mSellerMap.get("seller_code"),sToStatus));
				mLogMap.put("create_user", sUserCode);
				mLogMap.put("create_time", FormatHelper.upDateTime());

				DbUp.upTable("lc_seller_log").dataInsert(mLogMap);

			}
		}

		return mResult;

	}

	/**
	 * 获取 商家收藏店铺的列表信息
	 * 
	 * @param sellerCodes
	 * @return
	 */
	public List<CollectionSellerModel> getCollectionSellerInfos(
			String sellerCodes) {
		List<CollectionSellerModel> ret = new ArrayList<CollectionSellerModel>();

		List<UcSellerInfo> sellerlist = getSellerInfo(sellerCodes);

		if (sellerlist == null || sellerlist.size() == 0)
			return ret;
		else {

			ActivityService as = new ActivityService();
			ProductService ps = new ProductService();
			CategoryService cs = new CategoryService();

			List<OcActivity> activityList = as
					.getActivityListForSellers(sellerCodes);
			List<MDataMap> categoryMaps = cs
					.getSellerCategoryListBySellerCodes(sellerCodes);
			List<Category> allCategroyList = cs
					.getCategoryListBySellerCategoryRelation(categoryMaps);

			for (UcSellerInfo usi : sellerlist) {
				String sellerCode = usi.getSeller_code();
				CollectionSellerModel csm = new CollectionSellerModel();

				csm.setSeller_code(sellerCode);
				csm.setSeller_name(usi.getSeller_name());
				csm.setSeller_logo(usi.getSellerPic());
				csm.setQrcode_link(usi.getQrcode_link());
				csm.setSecond_level_domain(usi.getSecond_level_domain());
				csm.setCategoryList(cs.getSellerCategroyList(categoryMaps,
						allCategroyList, sellerCode));
				csm.setProductList(ps.getProductListForSeller(sellerCode));

				if (activityList != null) {
					List<OcActivity> sellerList = new ArrayList<OcActivity>();

					for (OcActivity oa : activityList) {
						if (oa.getSellerCode().equals("sellerCode")) {
							sellerList.add(oa);
						}
					}
					csm.setActivityList(sellerList);
				}

				ret.add(csm);
			}
		}
		return ret;
	}

	/**
	 * 获取代理商信息
	 * 
	 * @param sellerCodes
	 *            代理商编号，用逗号分隔
	 * @return
	 */
	public List<UcSellerInfo> getSellerInfo(String sellerCodes) {
		List<UcSellerInfo> list = new ArrayList<UcSellerInfo>();

		if (sellerCodes == null || sellerCodes.equals("")) {
			return list;
		} else {
			String[] sellerCodeAry = sellerCodes.split(",");
			MDataMap urMapParam = new MDataMap();
			String whereStr = "";
			int i = 0;
			for (String dm : sellerCodeAry) {
				urMapParam.put("seller_code" + i, dm);
				whereStr += " seller_code=:seller_code" + i + " or";
				i++;
			}

			if (whereStr.length() > 2)
				whereStr = whereStr.substring(0, whereStr.length() - 2);

			List<MDataMap> sellerList = DbUp
					.upTable("uc_sellerinfo")
					.query("seller_code,seller_name,seller_pic,seller_area,seller_telephone,seller_company_name,seller_return_address,seller_status,second_level_domain,qrcode_link",
							"", whereStr, urMapParam, -1, -1);
			UcSellerInfo c = new UcSellerInfo();
			for (MDataMap m : sellerList) {
				c = new UcSellerInfo();
				c.setSeller_code(m.get("seller_code"));
				c.setSeller_name(m.get("seller_name"));
				c.setSellerPic(m.get("seller_pic"));
				// seller_area,seller_telephone,seller_company_name,seller_return_address
				c.setSellerArea(m.get("seller_area"));
				c.setSellerReturnAddress(m.get("seller_return_address"));
				c.setSellerReturnTelephone(m.get("seller_telephone"));
				c.setSellerCompanyName(m.get("seller_company_name"));
				c.setSeller_status(m.get("seller_status"));
				c.setSecond_level_domain(m.get("second_level_domain"));
				c.setQrcode_link(m.get("qrcode_link"));

				list.add(c);
			}
		}

		return list;
	}

	
	/**
	 * 获取代理商信息
	 * 
	 * @param sellerCodes
	 *            代理商编号，用逗号分隔
	 * @return
	 */
	public UcSellerInfo getSellerInfoBydomain(String domain) {
		
		UcSellerInfo c = null;

		MDataMap urMapParam = new MDataMap();
		String whereStr = "second_level_domain=:second_level_domain";
		urMapParam.put("second_level_domain", domain);


		List<MDataMap> sellerList = DbUp
				.upTable("uc_sellerinfo")
				.query("seller_code,seller_name,seller_pic,seller_area,seller_telephone,seller_company_name,seller_return_address,seller_status,second_level_domain,qrcode_link",
						"", whereStr, urMapParam, -1, -1);
		for (MDataMap m : sellerList) {
			c = new UcSellerInfo();
			c.setSeller_code(m.get("seller_code"));
			c.setSeller_name(m.get("seller_name"));
			c.setSellerPic(m.get("seller_pic"));
			// seller_area,seller_telephone,seller_company_name,seller_return_address
			c.setSellerArea(m.get("seller_area"));
			c.setSellerReturnAddress(m.get("seller_return_address"));
			c.setSellerReturnTelephone(m.get("seller_telephone"));
			c.setSellerCompanyName(m.get("seller_company_name"));
			c.setSeller_status(m.get("seller_status"));
			c.setSecond_level_domain(m.get("second_level_domain"));
			c.setQrcode_link(m.get("qrcode_link"));

			break;
		}
	

		return c;
	}
	
	/**
	 * 查询商户扩展信息
	 * @param small_seller_code
	 * @return
	 */
	public SellerInfoExtend getSellerInfoExtend(String small_seller_code){
		
		SellerInfoExtend infoExtend =  null;
		
		if (StringUtils.isNotBlank(small_seller_code)) {
			MDataMap dataMap = DbUp.upTable("uc_seller_info_extend").one("small_seller_code", small_seller_code);
			if (dataMap!=null) {
				
				SerializeSupport<SellerInfoExtend> ss = new SerializeSupport<SellerInfoExtend>();
				infoExtend = new SellerInfoExtend();
				ss.serialize(dataMap, infoExtend);
			}
		}
		return infoExtend;
	}
	
	/**
	 * 获取商户公司简称
	 * @param small_seller_code
	 * @return
	 */
	public String getSellerShortName(String small_seller_code){
		
		SellerInfoExtend sellerInfoExtend = getSellerInfoExtend(small_seller_code);
		if(sellerInfoExtend!=null){
			return sellerInfoExtend.getSeller_short_name();
		}else{
			return "";
		}
		
		
	}
	
	public String getSellerName(String small_seller_code){
		MDataMap mDataMap = DbUp.upTable("uc_sellerinfo").oneWhere("seller_name", "", "small_seller_code=:small_seller_code","small_seller_code", small_seller_code);
		return mDataMap.get("seller_name");
		
	}
	
	
	/**
	 * 获取卖家编号seller_code
	 * @param small_seller_code
	 */
	public String getSellerCode(String small_seller_code){
		MDataMap mDataMap = DbUp.upTable("uc_sellerinfo").oneWhere("seller_code", "", "","small_seller_code", small_seller_code);
		String sellerCode = mDataMap.get("seller_code");
		return sellerCode;
	}
	
	/**
	 * 获取商户类型(普通商户和跨境商户)
	 * @param smallSellerCode
	 * @return
	 */
	public String getSellerType(String smallSellerCode) {
		MDataMap mDataMap = DbUp.upTable("uc_seller_info_extend").oneWhere("uc_seller_type", "", "","small_seller_code", smallSellerCode);
		String sellerType = "";
		if(mDataMap!=null && StringUtils.isNotEmpty(mDataMap.get("uc_seller_type"))) {
			sellerType = mDataMap.get("uc_seller_type");
		}
		return sellerType;
	}
}
