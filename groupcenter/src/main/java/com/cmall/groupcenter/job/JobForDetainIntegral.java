package com.cmall.groupcenter.job;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.entity.StringEntity;
import org.quartz.JobExecutionContext;

import com.alibaba.fastjson.JSONObject;
import com.srnpr.xmassystem.invoke.ref.model.UpdateCustAmtInput;
import com.srnpr.xmassystem.service.PlusServiceAccm;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basemodel.WebClientRequest;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapcom.topdo.TopConst;
import com.srnpr.zapcom.topdo.TopDir;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 每天凌晨1点半处理退货挽留积分
 * @remark 
 * @author sunyan
 * @date 2019年8月25日
 */
public class JobForDetainIntegral extends RootJob {

	private final String JY_URL = bConfig("groupcenter.rsync_homehas_url");
	
	PlusServiceAccm plusServiceAccm = new PlusServiceAccm();
	
	public synchronized void doExecute(JobExecutionContext context) {
		//赋予积分--TV品订单
		String sql = "SELECT d.*,o.out_order_code,o.buyer_code FROM uc_detain_integral d LEFT JOIN ordercenter.oc_orderinfo o ON d.order_code = o.order_code WHERE o.small_seller_code = 'SI2003' AND d.give_status = '4497471600460002'";
		List<Map<String, Object>> dataList = DbUp.upTable("uc_detain_integral").dataSqlList(sql, new MDataMap());
		if(null != dataList && !dataList.isEmpty()) {
			for(Map<String, Object> map:dataList){
				String ord_id = map.get("out_order_code")==null?"":map.get("out_order_code").toString();
				try {
					JSONObject infoMap = new JSONObject();
					infoMap.put("ord_id", ord_id);
					String jyResult = getHttps(JY_URL + "getDetainStatus", infoMap.toString());
					JSONObject jyObject = JSONObject.parseObject(jyResult);
					boolean success = jyObject.getBoolean("success");
					if(success) {
						String giveStatus = jyObject.getString("giveStatus");
						MDataMap upMap = new MDataMap();
						upMap.put("give_status", giveStatus);
						upMap.put("uid", map.get("uid").toString());
						upMap.put("give_time", FormatHelper.upDateTime());
						if(giveStatus.equals("4497471600460004")){//赋予积分
							this.giveIntegral(new BigDecimal(map.get("integral").toString()), map.get("buyer_code").toString(), "", map.get("order_code").toString(),UpdateCustAmtInput.CurdFlag.DT);
							DbUp.upTable("uc_detain_integral").dataUpdate(upMap, "give_status,give_time", "uid");
						}else if(giveStatus.equals("4497471600460006")){							
							DbUp.upTable("uc_detain_integral").dataUpdate(upMap, "give_status", "uid");
						}
					}
				} catch(Exception e) {
					if(e.getMessage().contains("该客户暂未绑定LD客代")){
						continue;
					}else{
						e.printStackTrace();
					}
				}
			}
		}
		
		//赋予积分--商户品订单
		String ssql = "SELECT d.*, o.out_order_code,o.buyer_code,ifnull(r.rtn_cnt,0) rtn_cnt,IFNULL(rd.rd_cnt,0) rd_cnt,if((SELECT min(lo.create_time) from logcenter.lc_orderstatus lo where lo.now_status = '4497153900010005' AND d.order_code = lo.code)<DATE_SUB(NOW(), INTERVAL 1 WEEK),'Y','N') flag FROM uc_detain_integral d"+
					  " LEFT JOIN ordercenter.oc_orderinfo o ON d.order_code = o.order_code LEFT JOIN (SELECT r.order_code,count(r.return_code) rtn_cnt from ordercenter.oc_return_goods r GROUP BY r.order_code) r ON d.order_code = r.order_code"+
					  " LEFT JOIN (SELECT r.order_code,count(r.return_code) rd_cnt from ordercenter.oc_return_goods r where r.`status` in ('4497153900050001','4497153900050002','4497153900050004','4497153900050005') GROUP BY r.order_code) rd ON d.order_code = rd.order_code"+
					  " WHERE o.small_seller_code <> 'SI2003' AND d.give_status = '4497471600460002'";
		List<Map<String, Object>> dataLists = DbUp.upTable("uc_detain_integral").dataSqlList(ssql, new MDataMap());
		if(null != dataLists && !dataLists.isEmpty()) {
			for(Map<String, Object> map:dataLists){
				try {
					String giveStatus = "";
					if(Integer.parseInt(map.get("rtn_cnt").toString())>0){//存在退货单
						if(Integer.parseInt(map.get("rd_cnt").toString())>0){//退货完成
							giveStatus = "4497471600460006";
							MDataMap upMap = new MDataMap();
							upMap.put("give_status", giveStatus);
							upMap.put("uid", map.get("uid").toString());
							DbUp.upTable("uc_detain_integral").dataUpdate(upMap, "give_status", "uid");
						}else{
							if(map.get("flag").equals("Y")){//过了七天签收日期，赋予积分
								giveStatus = "4497471600460004";
								MDataMap upMap = new MDataMap();
								upMap.put("give_status", giveStatus);
								upMap.put("uid", map.get("uid").toString());
								upMap.put("give_time", FormatHelper.upDateTime());
								this.giveIntegral(new BigDecimal(map.get("integral").toString()), map.get("buyer_code").toString(), "", map.get("order_code").toString(),UpdateCustAmtInput.CurdFlag.DT);
								DbUp.upTable("uc_detain_integral").dataUpdate(upMap, "give_status,give_time", "uid");
							}
						}
					}else{//不存在退货单
						if(map.get("flag").equals("Y")){//过了七天签收日期，赋予积分
							giveStatus = "4497471600460004";
							MDataMap upMap = new MDataMap();
							upMap.put("give_status", giveStatus);
							upMap.put("uid", map.get("uid").toString());
							upMap.put("give_time", FormatHelper.upDateTime());
							this.giveIntegral(new BigDecimal(map.get("integral").toString()), map.get("buyer_code").toString(), "", map.get("order_code").toString(),UpdateCustAmtInput.CurdFlag.DT);
							DbUp.upTable("uc_detain_integral").dataUpdate(upMap, "give_status,give_time", "uid");
						}
					}
				} catch (Exception e) {
					if(e.getMessage().contains("该客户暂未绑定LD客代")){
						continue;
					}else{
						e.printStackTrace();
					}
				}
			}
		}
		
		//扣除积分--TV品订单
//		String kql = "SELECT d.* from uc_detain_integral d INNER JOIN ordercenter.oc_orderinfo o ON d.order_code = o.order_code INNER JOIN ordercenter.oc_return_goods_detail_ld ld ON o.order_code = ld.order_code"+
//					 " where o.small_seller_code = 'SI2003' AND d.give_status = 4497471600460004 AND ld.cod_stat_cd in (31,91) AND ld.stat_date >= date_add(CAST(NOW() AS date),INTERVAL - 1 DAY) AND ld.stat_date < CAST(NOW() AS date)";
		String kql = "SELECT d.*,o.out_order_code,o.buyer_code from uc_detain_integral d INNER JOIN ordercenter.oc_orderinfo o ON d.order_code = o.order_code where o.small_seller_code = 'SI2003' AND d.give_status = '4497471600460004'";
		List<Map<String, Object>> dataListk = DbUp.upTable("uc_detain_integral").dataSqlList(kql, new MDataMap());
		if(null != dataListk && !dataListk.isEmpty()) {
			for(Map<String, Object> map:dataListk){
				String ord_id = map.get("out_order_code")==null?"":map.get("out_order_code").toString();
				try {
					JSONObject infoMap = new JSONObject();
					infoMap.put("ord_id", ord_id);
					String jyResult = getHttps(JY_URL + "getDetainStatus", infoMap.toString());
					JSONObject jyObject = JSONObject.parseObject(jyResult);
					boolean success = jyObject.getBoolean("success");
					if(success) {
						String giveStatus = jyObject.getString("giveStatus");
						if(giveStatus.equals("4497471600460006")){
							this.giveIntegral(new BigDecimal(map.get("integral").toString()), map.get("buyer_code").toString(), "", map.get("order_code").toString(),UpdateCustAmtInput.CurdFlag.ZB);
							MDataMap upMap = new MDataMap();
							upMap.put("give_status", "4497471600460005");
							upMap.put("uid", map.get("uid").toString());
							DbUp.upTable("uc_detain_integral").dataUpdate(upMap, "give_status", "uid");
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		//扣除积分--商户品订单
		String ksql = "SELECT d.*,o.buyer_code from uc_detain_integral d INNER JOIN ordercenter.oc_orderinfo o ON d.order_code = o.order_code WHERE o.small_seller_code <> 'SI2003' AND d.give_status = '4497471600460004' AND d.order_code in ("+
					  " SELECT r.order_code from ordercenter.oc_return_goods r INNER JOIN logcenter.lc_return_goods_status rgs ON r.return_code = rgs.return_no and r.status = rgs.status WHERE r.status in ('4497153900050001','4497153900050002','4497153900050004','4497153900050005') AND rgs.create_time >= date_add(CAST(NOW() AS date),INTERVAL - 1 DAY) AND rgs.create_time < CAST(NOW() AS date))";
		List<Map<String, Object>> dataListks = DbUp.upTable("uc_detain_integral").dataSqlList(ksql, new MDataMap());
		if(null != dataListks && !dataListks.isEmpty()) {
			for(Map<String, Object> map:dataListks){
				String giveStatus = "4497471600460005";
				this.giveIntegral(new BigDecimal(map.get("integral").toString()), map.get("buyer_code").toString(), "", map.get("order_code").toString(),UpdateCustAmtInput.CurdFlag.ZB);
				MDataMap upMap = new MDataMap();
				upMap.put("give_status", giveStatus);
				upMap.put("uid", map.get("uid").toString());
				DbUp.upTable("uc_detain_integral").dataUpdate(upMap, "give_status", "uid");
			}
		}
	}
	
	private String getHttps(String sUrl, String sRequestString)
			throws ParseException, IOException, KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException, CertificateException {
		WebClientRequest webClientRequest = new WebClientRequest();

		String sDir = bConfig("groupcenter.homehas_key");

		if (StringUtils.isEmpty(sDir)) {
			TopDir topDir = new TopDir();
			sDir = topDir.upCustomPath("") + "tomcat.keystore";
		}
//		sDir = "C:/etc/zapsrnpr/c__users_bloodline/tomcat.keystore";
		
		webClientRequest.setFilePath(sDir);
		webClientRequest.setUrl(sUrl);

		HttpEntity httpEntity = new StringEntity(sRequestString,
				TopConst.CONST_BASE_ENCODING);

		webClientRequest.setConentType("application/json");

		webClientRequest
				.setPassword(bConfig("groupcenter.rsync_homehas_password"));


		webClientRequest.setHttpEntity(httpEntity);

		String sResponseString = WebClientSupport.upHttpsPost(webClientRequest);

		return sResponseString;
	}
	
	/**
	 * 赠送积分
	 */
	private void giveIntegral(BigDecimal giveMoney,String memberCode,String bigOrderCode,String orderCode,UpdateCustAmtInput.CurdFlag doType){
		giveMoney = plusServiceAccm.accmAmtToMoney(giveMoney,2);
		String custId = plusServiceAccm.getCustId(memberCode);// 家有客代号
		if(StringUtils.isBlank(custId)){
			throw new RuntimeException("该客户暂未绑定LD客代");
		}
		RootResult teamResult = plusServiceAccm.changeForAccmAmt(doType, giveMoney, custId, bigOrderCode, orderCode);
		// 记录积分变更日志  - 积分共享增加
		if(teamResult.getResultCode() == 1) {
			MDataMap mDataMap = new MDataMap();
			mDataMap.put("member_code", memberCode);
			mDataMap.put("cust_id", custId);
			mDataMap.put("change_type", "449748080011");
			mDataMap.put("change_money", giveMoney.toString());
			mDataMap.put("remark", orderCode);
			mDataMap.put("create_time", FormatHelper.upDateTime());
			DbUp.upTable("mc_member_integral_change").dataInsert(mDataMap);
		}
	}
	
}
