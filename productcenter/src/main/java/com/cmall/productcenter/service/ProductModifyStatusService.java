package com.cmall.productcenter.service;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.common.DateUtil;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.xmassystem.enumer.EPlusScheduler;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.xmassystem.helper.PlusHelperScheduler;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * 
 * 项目名称：productcenter 
 * 类名称：     ProductModifyStatusService 
 * 类描述：     商品总览中修改商品是否可售状态
 * 创建人：     gaoy  
 * 创建时间：2013年9月23日下午2:25:04
 * 修改人：     ligj
 * 修改时间：2015年6月16日下午2:25:04
 * 修改备注：  
 *
 */
public class ProductModifyStatusService extends BaseClass implements IFlowFunc{
	
	public static final String UPDATE_PRODUCTINFO = "pc_productinfo";
	
	public RootResult BeforeFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus,MDataMap mSubMap) {
		RootResult ret = new RootResult();
		
//		String manageCode = "";
		
		MDataMap md = DbUp.upTable("pc_productinfo").one("uid",outCode);
		
//		if(md!=null){
//			manageCode = md.get("seller_code");
//		}
		MDataMap user = DbUp.upTable("za_userinfo").one("manage_code",md.get("small_seller_code"),"user_type_did","467721200003","flag_enable","0");
		/**
		 * 修改商户判断条件 2016-12-02 zhy
		 */
		String seller_type = WebHelper.getSellerType(md.get("small_seller_code"));
//		if(user!=null&&!user.isEmpty()&&toStatus.equals("4497153900060002")&&md.get("small_seller_code").startsWith("SF03")){
		if(user!=null&&!user.isEmpty()&&toStatus.equals("4497153900060002")&&StringUtils.isNotBlank(seller_type)){
			ret.setResultCode(941901110);
			ret.setResultMessage(bInfo(941901110));
			return ret;
		}
//		if("4497153900060002".equals(toStatus)){
//			ProductCheck pc = new ProductCheck();
//			//如果当前上架的商品超过某个数量，则把商品置成下架
//			if(pc.upSalesScopeType(manageCode).equals("")){
//				ret.setResultCode(941901050);
//				ret.setResultMessage(bInfo(941901050));
//				return ret;
//			}
//		}
		
		
		ret.setResultCode(1);
		
		return ret;
	}
	
	public RootResult afterFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus,MDataMap mSubMap) {
		RootResult ret = new RootResult();
		MDataMap updMap = new MDataMap();
		updMap.put("uid", outCode);
		//商品状态为“已上架”时，”是否可售状态“更新为“1”
		if("4497153900060002".equals(toStatus)){
			updMap.put("flag_sale", "1");
		}else{
			//其他状态时，”是否可售状态“更新为“0”
			updMap.put("flag_sale", "0");
		}
		
		updMap.put("update_time", DateUtil.getSysDateTimeString());
		MDataMap md = new MDataMap();
		md = DbUp.upTable("pc_productinfo").one("uid",outCode);
		String productCode = md.get("product_code");
		
		//如果商品下没有可售的sku时候，不允许商品上架
		if ("4497153900060002".equals(toStatus) && DbUp.upTable("pc_skuinfo").count("product_code",productCode,"sale_yn","Y")<1) {
			MUserInfo userInfo = UserFactory.INSTANCE.create();
			if (null == userInfo) {
				userInfo = new MUserInfo();
			} 
			String receives[]= bConfig("productcenter.compel_onShelves_sendMail_receives").split(",");
			String title= "商品没有可售sku，被人强制上架";
			String content= "商品没有可售sku，被人强制上架,操作人："+userInfo.getLoginName()+";商品编号为："+productCode;
			
			for (String receive : receives) {
				if(StringUtils.isNotBlank(receive)){
					MailSupport.INSTANCE.sendMail(receive, title, content);
				}
			}
			updMap.put("product_status", fromStatus);
			DbUp.upTable("pc_productinfo").dataUpdate(updMap,"product_status","uid");
			
			ret.setResultCode(941901144);
			ret.setResultMessage(bInfo(941901144));
		}else{
			DbUp.upTable(UPDATE_PRODUCTINFO).dataUpdate(updMap,"flag_sale,update_time","uid");
			ret.setResultCode(1);
		}
		
		// 加载商品图片宽高到缓存
		PlusHelperScheduler.sendSchedler(EPlusScheduler.ProductImageWidth, productCode, productCode);
		
		PlusHelperNotice.onChangeProductInfo(productCode);
		ProductJmsSupport pjs = new ProductJmsSupport();		//触发消息队列
		pjs.onChangeForProductChangeAll(productCode);
		return ret;
	}
}
