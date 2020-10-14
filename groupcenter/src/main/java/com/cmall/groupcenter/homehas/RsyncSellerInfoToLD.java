package com.cmall.groupcenter.homehas;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.cmall.groupcenter.groupface.IRsyncRequest;
import com.cmall.groupcenter.groupface.IRsyncResponse;
import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 
 * 描述: 同步商户信息到LD <br>
 * 作者: 赵俊岭 zhaojunling@huijiayou.cn<br>
 */
public class RsyncSellerInfoToLD extends RsyncHomeHas<RsyncSellerInfoToLD.TConfig, RsyncSellerInfoToLD.TRequest, RsyncSellerInfoToLD.TResponse> {

	private TRequest tRequest = new TRequest();
	private TResponse tResponse = new TResponse();
	
	public TConfig upConfig() {
		return new TConfig();
	}
	public TRequest upRsyncRequest() {
		return tRequest;
	}
	public TResponse upResponseObject() {
		return tResponse;
	}

	public RsyncResult doProcess(TRequest tRequest, TResponse tResponse) {
		this.tResponse = tResponse;
		
		RsyncResult mWebResult = new RsyncResult();
		if(!tResponse.success){
			mWebResult.setResultCode(0);
			mWebResult.setResultMessage("同步失败："+tResponse.message);
			return mWebResult;
		}
		
		if(tResponse.result != null){
			for(TResponse.DlrId dlrId : tResponse.result){
				if(StringUtils.isNotBlank(dlrId.dlr_id) && StringUtils.isNotBlank(dlrId.hjy_dlr_id)){
					MDataMap sellerInfo = DbUp.upTable("uc_sellerinfo").one("small_seller_code", dlrId.hjy_dlr_id);
					if(sellerInfo != null){
						sellerInfo.put("ld_dlr_id", dlrId.dlr_id);
						DbUp.upTable("uc_sellerinfo").update(sellerInfo);
					}
				}
			}
		}
		
		return mWebResult;
	}
	
	/**
	 * 传入商户编号，设置同步所需的请求参数
	 * @param smallSellerCode
	 * @return
	 */
	public TRequest buildTRequest(String smallSellerCode){
		MDataMap sellerInfo = DbUp.upTable("uc_sellerinfo").one("small_seller_code", smallSellerCode);
		MDataMap sellerInfoExt = DbUp.upTable("uc_seller_info_extend").one("small_seller_code", smallSellerCode);
		
		if(sellerInfo == null || sellerInfoExt == null) return null;
		
		// 只把普通商户和平台入驻同步给LD
		if(!ArrayUtils.contains(new String[]{"4497478100050001","4497478100050004"}, sellerInfoExt.get("uc_seller_type"))){
			return null;
		}
		
		String[] phoneTexts = null;
		tRequest.dlr_info[0] = new Dlr();
		tRequest.dlr_info[0].dlr_nm = "惠家有_"+StringUtils.trimToEmpty(sellerInfo.get("seller_company_name"));
		tRequest.dlr_info[0].dlr_no = StringUtils.trimToEmpty(sellerInfoExt.get("registration_number"));
		tRequest.dlr_info[0].chief_nm = StringUtils.trimToEmpty(sellerInfoExt.get("legal_person"));
		tRequest.dlr_info[0].bclss_nm = cutBClssNm(sellerInfoExt.get("business_scope"));
		tRequest.dlr_info[0].btype_nm = "其他";
		tRequest.dlr_info[0].dlr_lvl_cd = "40";
		
		phoneTexts = splitPhone(StringUtils.trimToEmpty(sellerInfoExt.get("company_phone")));
		tRequest.dlr_info[0].dlr_teld = StringUtils.trimToEmpty(phoneTexts[0]);
		tRequest.dlr_info[0].dlr_telh = StringUtils.trimToEmpty(phoneTexts[1]);
		tRequest.dlr_info[0].dlr_teln = StringUtils.trimToEmpty(phoneTexts[2]);
		tRequest.dlr_info[0].dlr_teli = StringUtils.trimToEmpty(phoneTexts[3]);
		tRequest.dlr_info[0].dlr_faxd = "";
		tRequest.dlr_info[0].dlr_faxh = "";
		tRequest.dlr_info[0].dlr_faxn = "";
		tRequest.dlr_info[0].dlr_zip_no = "";
		tRequest.dlr_info[0].dlr_addr_1 = StringUtils.trimToEmpty(sellerInfoExt.get("company_datail_address"));
		tRequest.dlr_info[0].dlr_addr_2 = "";
		tRequest.dlr_info[0].dlr_homepage = StringUtils.trimToEmpty(sellerInfo.get("seller_url"));
		
		String build_ym = StringUtils.trimToEmpty(sellerInfoExt.get("establishment_date").replace("-", "").replace("年", "").replace("月", "").replace("日", "").replace("/", ""));
		tRequest.dlr_info[0].build_ym = build_ym.length() > 6 ? build_ym.substring(0, 6) : build_ym;
		tRequest.dlr_info[0].acct_owner_nm = StringUtils.trimToEmpty(sellerInfo.get("seller_company_name"));
		tRequest.dlr_info[0].bank_cd = getBankCd(sellerInfoExt.get("bank_name"))[0];
		tRequest.dlr_info[0].chrg_teld = "";
		tRequest.dlr_info[0].chrg_telh = "";
		tRequest.dlr_info[0].chrg_teln = "";
		tRequest.dlr_info[0].chrg_teli = "";
		
		phoneTexts = splitPhone(sellerInfoExt.get("business_person_phone").replace("-", ""));
		tRequest.dlr_info[0].chrg_hp_teld = StringUtils.trimToEmpty(phoneTexts[0]);
		tRequest.dlr_info[0].chrg_hp_telh = StringUtils.trimToEmpty(phoneTexts[1]);
		tRequest.dlr_info[0].chrg_hp_teln = StringUtils.trimToEmpty(phoneTexts[2]);
		tRequest.dlr_info[0].chrg_nm = StringUtils.trimToEmpty(sellerInfoExt.get("business_person"));
		tRequest.dlr_info[0].chrg_part = "销售";
		tRequest.dlr_info[0].chrg_pstn = "";
		tRequest.dlr_info[0].chrg_mail_id = "";
		tRequest.dlr_info[0].cent_teld = "";
		tRequest.dlr_info[0].cent_telh = "";
		tRequest.dlr_info[0].cent_teln = "";
		tRequest.dlr_info[0].cent_teli = "";
		tRequest.dlr_info[0].cent_faxd = "";
		tRequest.dlr_info[0].cent_faxh = "";
		tRequest.dlr_info[0].cent_faxn = "";
		tRequest.dlr_info[0].cent_zip_no = "";
		tRequest.dlr_info[0].cent_addr_1 = "";
		tRequest.dlr_info[0].cent_addr_2 = "";
		tRequest.dlr_info[0].cent_nm = "";
		tRequest.dlr_info[0].cent_hp_teld = "";
		tRequest.dlr_info[0].cent_hp_telh = "";
		tRequest.dlr_info[0].cent_hp_teln = "";
		
		phoneTexts = splitPhone(StringUtils.trimToEmpty(sellerInfoExt.get("after_sale_phone")));
		tRequest.dlr_info[0].as_teld = StringUtils.trimToEmpty(phoneTexts[0]);
		tRequest.dlr_info[0].as_telh = StringUtils.trimToEmpty(phoneTexts[1]);
		tRequest.dlr_info[0].as_teln = StringUtils.trimToEmpty(phoneTexts[2]);
		tRequest.dlr_info[0].as_teli = StringUtils.trimToEmpty(phoneTexts[3]);
		tRequest.dlr_info[0].as_chrg_nm = StringUtils.trimToEmpty(sellerInfoExt.get("after_sale_person"));
		tRequest.dlr_info[0].scm_use_yn = "N";
		tRequest.dlr_info[0].scm_pswd = "";
		tRequest.dlr_info[0].bank_nm = StringUtils.trimToEmpty(sellerInfoExt.get("branch_name"));
		tRequest.dlr_info[0].account_code = StringUtils.trimToEmpty(sellerInfoExt.get("bank_account"));
		tRequest.dlr_info[0].dlr_level = "";
		tRequest.dlr_info[0].dlr_paypercent = "";
		tRequest.dlr_info[0].register_money = registerMoney(StringUtils.trimToEmpty(sellerInfoExt.get("register_money")));
		tRequest.dlr_info[0].money_kind = "人民币";
		tRequest.dlr_info[0].bank_no = StringUtils.trimToEmpty(sellerInfoExt.get("joint_number"));
		tRequest.dlr_info[0].cont_nm = StringUtils.trimToEmpty(sellerInfoExt.get("contract_rerurn_person"));
		tRequest.dlr_info[0].cont_teld = "";
		tRequest.dlr_info[0].cont_telh = "";
		tRequest.dlr_info[0].cont_teln = "";
		tRequest.dlr_info[0].cont_teli = "";
		
		phoneTexts = splitPhone(StringUtils.trimToEmpty(sellerInfoExt.get("contract_rerurn_phone")));
		tRequest.dlr_info[0].cont_hp = StringUtils.trimToEmpty(phoneTexts[0])+StringUtils.trimToEmpty(phoneTexts[1])+StringUtils.trimToEmpty(phoneTexts[2]);
		tRequest.dlr_info[0].cont_addr = StringUtils.trimToEmpty(sellerInfoExt.get("contract_return_address"));
		tRequest.dlr_info[0].rcpt_nm = StringUtils.trimToEmpty(sellerInfoExt.get("invoice_return_person"));
		tRequest.dlr_info[0].rcpt_teld = "";
		tRequest.dlr_info[0].rcpt_telh = "";
		tRequest.dlr_info[0].rcpt_teln = "";
		tRequest.dlr_info[0].rcpt_teli = "";
		
		phoneTexts = splitPhone(StringUtils.trimToEmpty(sellerInfoExt.get("invoice_return_phone")));
		tRequest.dlr_info[0].rcpt_hp = StringUtils.trimToEmpty(phoneTexts[0])+StringUtils.trimToEmpty(phoneTexts[1])+StringUtils.trimToEmpty(phoneTexts[2]);
		tRequest.dlr_info[0].rcpt_addr = StringUtils.trimToEmpty(sellerInfoExt.get("invoice_return_address"));
		tRequest.dlr_info[0].hjy_dlr_id = smallSellerCode;
		tRequest.dlr_info[0].bank_prov = getProvName(StringUtils.trimToEmpty(sellerInfoExt.get("branch_area_address")));
		tRequest.dlr_info[0].bank_city = getCityName(StringUtils.trimToEmpty(sellerInfoExt.get("branch_area_address")));
		
		// 直辖市的情况，省市都传市
		if("市辖区".equals(tRequest.dlr_info[0].bank_city )){
			tRequest.dlr_info[0].bank_city = tRequest.dlr_info[0].bank_prov;
		}
		return tRequest;
	}
	
	public static class TConfig extends RsyncConfigRsyncBase{
		@Override
		public String getRsyncTarget() {
			return "DlrImport";
		}
	}
	
	public static class TResponse implements IRsyncResponse{
		public boolean success;
		public String message;
		public DlrId[] result;
		
		public static class DlrId{
			public String dlr_id;
			public String hjy_dlr_id;
		}
	}
	
	public static class TRequest implements IRsyncRequest{
		public String etr_id = "hjy";
		public Dlr[] dlr_info = new Dlr[1];
	}
	
	public static class Dlr{
		/** 供应商名称 */
		public String dlr_nm = "";
		/** 供应商分类 */
		public String dlr_clss_cd = "10";  // 10 法人  20 个人
		/** 企业注册号码 */
		public String dlr_no = "";
		/** 负责人姓名 */
		public String chief_nm = "";
		/** 行业种类 */
		public String bclss_nm = "";
		/** 行业形态 */
		public String btype_nm = "";
		/** 供应商等级 */
		public String dlr_lvl_cd = "";  
		/** 供应商联系电话（区号） */
		public String dlr_teld;  // 010
		/** 供应商联系电话（前码） */
		public String dlr_telh;
		/** 供应商联系电话 */
		public String dlr_teln;
		/** 供应商联系电话（分机） */
		public String dlr_teli; 
		/** 供应商传真（区号） */
		public String dlr_faxd;
		/** 供应商传真（前码） */
		public String dlr_faxh;
		/** 供应商传真 */
		public String dlr_faxn;
		/** 供应商邮编 */
		public String dlr_zip_no = "";
		/** 供应商地址_1 */
		public String dlr_addr_1 = "";
		/** 供应商地址_2 */
		public String dlr_addr_2;
		/** 供应商网址  */
		public String dlr_homepage;
		/** 成立年度 */
		public String build_ym = ""; // 201609
		/** 存款人 */
		public String acct_owner_nm = "";  // 浙江朗博飞日用品有限公司
		/** 银行编码 */
		public String bank_cd = "";
		/** 负责人联系电话（区号） */
		public String chrg_teld;
		/** 负责人联系电话（前码） */
		public String chrg_telh;
		/** 负责人联系电话 */
		public String chrg_teln;
		/** 负责人联系电话（分机） */
		public String chrg_teli;
		/** 负责人手机 */
		public String chrg_hp_teld = "";
		/** 负责人手机 */
		public String chrg_hp_telh = "";
		/** 负责人手机 */
		public String chrg_hp_teln = "";
		/** 负责人姓名 */
		public String chrg_nm = "";
		/** 负责人部署 */
		public String chrg_part;
		/** 负责人职称 */
		public String chrg_pstn;
		/** 负责人邮箱 */
		public String chrg_mail_id;
		/** 物流中心电话（区号） */
		public String cent_teld;
		/** 物流中心电话（前码） */
		public String cent_telh;
		/** 物流中心电话 */
		public String cent_teln;
		/** 物流中心电话（分机） */
		public String cent_teli;
		/** 物流中心传真（区号） */
		public String cent_faxd;
		/** 物流中心传真（前码） */
		public String cent_faxh;
		/** 物流中心传真 */
		public String cent_faxn;
		/** 物流中心邮编 */
		public String cent_zip_no;
		/** 物流中心地址_1 */
		public String cent_addr_1;
		/** 物流中心地址_2 */
		public String cent_addr_2;
		/** 物流中心负责人 */
		public String cent_nm;
		/** 物流中心联系人手机 */
		public String cent_hp_teld = "";
		/** 物流中心联系人手机 */
		public String cent_hp_telh = "";
		/** 物流中心联系人手机 */
		public String cent_hp_teln = "";
		/** 维修服务电话（区号） */
		public String as_teld;
		/** 维修服务电话（前码） */
		public String as_telh;
		/** 维修服务电话 */
		public String as_teln;
		/** 维修服务电话（分机 */
		public String as_teli;
		/** 售后负责人 */
		public String as_chrg_nm;
		/** 是否使用SCM */
		public String scm_use_yn = ""; // Y 、 N
		/** SCM密码 */
		public String scm_pswd;
		/** 银行名称 */
		public String bank_nm = "";
		/** 银行帐号 */
		public String account_code = "";
		public String dlr_level;
		public String dlr_paypercent;
		/** 注册资金 */
		public String register_money;
		/** 币种 */
		public String money_kind;
		/** 银联号 */
		public String bank_no = "";
		/** 合同回寄收件人 */
		public String cont_nm = "";
		/** 合同回寄收件人座机（区号） */
		public String cont_teld;
		/** 合同回寄收件人座机（前码） */
		public String cont_telh;
		/** 合同回寄收件人座机 */
		public String cont_teln;
		/** 合同回寄收件人座机（分机） */
		public String cont_teli;
		/** 合同回寄人手机 */
		public String cont_hp = "";
		/** 合同回寄地址 */
		public String cont_addr = "";
		/** 发票回寄人 */
		public String rcpt_nm = "";
		/** 发票回寄收件人座机（区号） */
		public String rcpt_teld;
		/** 发票回寄收件人座机（前码）	 */
		public String rcpt_telh;
		/** 发票回寄收件人座机 */
		public String rcpt_teln;
		/** 发票回寄收件人座机（分机) */
		public String rcpt_teli;
		/** 发票回寄人手机 */
		public String rcpt_hp = "";
		/** 发票回寄地址 */
		public String rcpt_addr = "";
		/** 惠家有供应商编号 */
		public String hjy_dlr_id = "";
		/** 开户行所在省份 */
		public String bank_prov = "";
		/** 开户行所在城市 */
		public String bank_city = "";
	}
	
	private String getProvName(String areaCode){
		if(StringUtils.isBlank(areaCode) || areaCode.length() < 2) return "";
		List<MDataMap> dataList = DbUp.upTable("sc_gov_district").queryByWhere("code" , areaCode.subSequence(0, 2)+"0000");
		if(dataList.isEmpty()) return "";
		return dataList.get(0).get("name");
	}
	
	private String getCityName(String areaCode){
		if(StringUtils.isBlank(areaCode) || areaCode.length() < 4) return "";
		String cityCode = areaCode.subSequence(0, 4)+"00";
		// 9000结尾是省直辖县级行政区划，不能直接使用
		if(cityCode.endsWith("9000")){
			cityCode = areaCode;
		}
		List<MDataMap> dataList = DbUp.upTable("sc_gov_district").queryByWhere("code" , cityCode);
		if(dataList.isEmpty()) return "";
		return dataList.get(0).get("name");
	}
	
	private String registerMoney(String money){
		try {
			return new BigDecimal(money).multiply(new BigDecimal(10000)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 截取商户经营范围
	 */
	private String cutBClssNm(String business_scope){
		business_scope = business_scope.replaceAll("；", ",");
		business_scope = business_scope.replaceAll("、", ",");
		business_scope = business_scope.replaceAll("，", ",");
		business_scope = business_scope.replaceAll("：", ",");
		business_scope = business_scope.replaceAll("。", ",");
		business_scope = business_scope.replaceAll("\\.", ",");
		business_scope = business_scope.replaceAll("\\s+", ",");
		String[] texts = business_scope.split(",");
		
		StringBuilder build = new StringBuilder();
		for(String text : texts){
			if(StringUtils.isBlank(text)) continue;
			// 长度不能超过30个字节的限制，LD那边接收一个中文按照2个字节计算
			if((build.length() + text.trim().length() + 1) > 15){
				break;
			}
			if(build.length() > 0) build.append("、");
			build.append(text.trim());
		}
		
		if(build.length() > 15){
			return build.toString().substring(0, 15);
		}else{
			return build.toString();
		}
	}
	
	/**
	 * 把手机号或者固话分解成多段
	 * @param phone
	 * @return 长度为4的字符串数组，数组里面元素可能为null
	 */
	private String[] splitPhone(String phone){
		String[] texts = new String[4];
		if(StringUtils.isBlank(phone)) return texts;
		
		phone = phone.split("\\s+")[0];
		phone = phone.split("/")[0];
		phone = phone.replaceAll("\\s+", "");
		if(!phone.matches("[\\d-]+")) return texts;
		
		// 无区号带分机的情况,补充一个空区号
		if(phone.matches("\\d{6,}-\\d{1,}")){
			phone = "-"+phone;
		}
		
		String[] vals = phone.split("-");
		if(vals.length == 1){ // 手机号
			if(phone.length() >= 3){
				texts[0] = phone.substring(0,  3);
			}
			if(phone.length() >= 7){
				texts[1] = phone.substring(3,  7);
			}
			if(phone.length() >= 11){
				texts[2] = phone.substring(7,  11);
			}
		} else if(vals.length == 2){ // 带区号固话
			texts[0] = vals[0].length() > 4 ? vals[0].substring(0, 4) : vals[0]; // 区号
			if(vals[1].length() >= 4){
				texts[1] = vals[1].substring(0,  4);
			}
			if(vals[1].length() >= 8){
				texts[2] = vals[1].substring(4,  8);
			}
		} else if(vals.length == 3){ // 带区号和分机固话
			texts[0] = vals[0].length() > 4 ? vals[0].substring(0, 4) : vals[0]; // 区号
			if(vals[1].length() >= 4){
				texts[1] = vals[1].substring(0,  4);
			}
			if(vals[1].length() >= 8){
				texts[2] = vals[1].substring(4,  8);
			}
			texts[3] = vals[2].length() > 4 ? vals[2].substring(0, 4) : vals[2]; // 分机
		}
		
		return texts;
	}
	
	private String[] getBankCd(String name){
		MDataMap bankInfo = DbUp.upTable("uc_bankinfo").one("bank_code", name);
		if(bankInfo == null) return new String[]{"",""};
		return new String[]{StringUtils.trimToEmpty(bankInfo.get("ld_code")),StringUtils.trimToEmpty(bankInfo.get("bank_name"))};
//		if(StringUtils.isBlank(name)) return new String[]{"",""};
//		if(name.contains("工商银行")){
//			return new String[]{"01","中国工商银行"};
//		} else if(name.contains("中国银行")){
//			return new String[]{"02","中国银行"};
//		} else if(name.contains("建设银行")){
//			return new String[]{"04","中国建设银行"};
//		} else if(name.contains("农业银行")){
//			return new String[]{"03","中国农业银行"};
//		} else if(name.contains("招商银行")){
//			return new String[]{"05","招商银行"};
//		} else if(name.contains("交通银行")){
//			return new String[]{"06","交通银行"};
//		} else if(name.contains("中信银行")){
//			return new String[]{"07","中信银行"};
//		} else if(name.contains("民生银行")){
//			return new String[]{"08","中国民生银行"};
//		} else if(name.contains("广东发展银行") || name.contains("广发银行")){
//			return new String[]{"09","广东发展银行"};
//		} else if(name.contains("浦东发展银行") || name.contains("浦发银行")){
//			return new String[]{"10","上海浦东发展银行"};
//		} else if(name.contains("深圳发展银行")){
//			return new String[]{"11","深圳发展银行"};
//		} else if(name.contains("华夏银行")){
//			return new String[]{"12","华夏银行"};
//		} else if(name.contains("光大银行")){
//			return new String[]{"13","中国光大银行"};
//		} else if(name.contains("兴业银行")){
//			return new String[]{"14","兴业银行"};
//		} else if(name.contains("恒丰银行")){
//			return new String[]{"15","恒丰银行"};
//		} else if(name.contains("邮政储蓄")){
//			return new String[]{"16","邮政储蓄"};
//		} else if(name.contains("北京银行")){
//			return new String[]{"17","北京银行"};
//		} else if(name.contains("平安银行")){
//			return new String[]{"18","平安银行"};
//		} else if(name.contains("天津银行")){
//			return new String[]{"20","天津银行"};
//		} else if(name.contains("上海银行")){
//			return new String[]{"21","上海银行"};
//		} else if(name.contains("南京银行")){
//			return new String[]{"23","南京银行"};
//		} else if(name.contains("宁波银行")){
//			return new String[]{"24","宁波银行"};
//		} else if(name.contains("徽商银行")){
//			return new String[]{"26","徽商银行"};
//		} else if(name.contains("温州银行")){
//			return new String[]{"28","温州银行"};
//		} else if(name.contains("重庆银行")){
//			return new String[]{"31","重庆银行"};
//		} else if(name.contains("青岛银行")){
//			return new String[]{"34","青岛银行"};
//		} else if(name.contains("农业发展银行")){
//			return new String[]{"41","中国农业发展银行"};
//		} else if(name.contains("花旗中国银行")){
//			return new String[]{"43","花旗中国银行"};
//		} else if(name.contains("渣打中国银行")){
//			return new String[]{"45","渣打中国银行"};
//		} else if(name.contains("香港汇丰银行")){
//			return new String[]{"46","香港汇丰银行"};
//		} else if(name.contains("恒生银行")){
//			return new String[]{"50","恒生银行"};
//		} else if(name.contains("广州银行")){
//			return new String[]{"63","广州银行"};
//		} else if(name.contains("友利银行")){
//			return new String[]{"57","友利银行"};
//		} else if(name.contains("农村商业银行") || name.contains("农商银行")){
//			return new String[]{"69","农村商业银行"};
//		} else if(name.contains("农村合作银行")){
//			return new String[]{"70","农村合作银行"};
//		} else if(name.contains("农村信用合作")){
//			return new String[]{"71","农村信用合作联社"};
//		}
//		return new String[]{"61", name};
	}	
}
