package com.cmall.ordercenter.job;

import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.webwx.WxGateSupport;

/**
 * 用户下单未发货，每日上午9：00和下午3：00定时发送提醒
 * 微信通知内容
 * 您有订单未发货：XX个订单。（XX为订单数量）
 * @author cc
 *
 */
public class SendDeliverGoodsJob extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		List<Map<String,Object>> list = DbUp.upTable("v_sellororder").dataSqlList("SELECT small_seller_code, count(0) as num from v_sellororder where order_status='4497153900010002' and collage_status != '449748300001' GROUP BY small_seller_code", new MDataMap());
		if(list != null && list.size() > 0) {
			for (Map<String, Object> map : list) {
				sendWxGZHNoticeDeliverGoods(map.get("small_seller_code").toString(),map.get("num").toString());
			}
		}		
	}

	/**
	 * 发送微信公众号通知商家发货
	 * @param small_seller_code
	 * @param num
	 */
	private void sendWxGZHNoticeDeliverGoods(String small_seller_code, String num) {
		String sql = "SELECT openid from uc_wechat_account WHERE manage_code=:manage_code and flag_enable=1";
		List<Map<String, Object>> mapList  = DbUp.upTable("uc_wechat_account").dataSqlList(sql, new MDataMap("manage_code",small_seller_code));
		if(mapList != null && mapList.size() > 0) {
			for(Map<String, Object> mapWechat : mapList){
				String openid = mapWechat.get("openid")==null?"":mapWechat.get("openid").toString();
				if(openid != "") {
					//发送微信消息
					WxGateSupport wxGateSupport = new WxGateSupport(bConfig("ordercenter.wx_url"), bConfig("ordercenter.merchant_key"), bConfig("ordercenter.merchant_id"), bConfig("ordercenter.word_color"));
					String receivers = openid + "|" + bConfig("ordercenter.send_notice_template_num") +"||";
					StringBuffer orderCode = new StringBuffer();
					String orderCodes = "";
					List<Map<String, Object>> list = DbUp.upTable("oc_orderinfo").dataSqlList("SELECT order_code from oc_orderinfo where order_status='4497153900010002' and small_seller_code=:small_seller_code ", new MDataMap("small_seller_code", small_seller_code));
					if(list != null && list.size() > 0) {
						for(Map<String, Object> map : list) {
							String status = "";
							String order_code = map.get("order_code").toString();
							MDataMap collageItemMap = DbUp.upTable("sc_event_collage_item").one("collage_ord_code",order_code);
							if(collageItemMap != null && !collageItemMap.isEmpty()) {//非空证明是拼团单。需要查询是否拼团成功，如果不成功则不发通知。
								String collageCode = collageItemMap.get("collage_code");
								MDataMap collageMap = DbUp.upTable("sc_event_collage").one("collage_code",collageCode);
								if(collageMap !=null && !collageMap.isEmpty()) {
									status = collageMap.get("collage_status");
								}
							}
							if("".equals(status)||"449748300002".equals(status)) {//为空或是为拼团成功的订单有发货通知
								orderCode.append("\n").append(order_code).append(",");
							}
						}
					}
					if(orderCode.length() > 0) {
						orderCodes = orderCode.substring(1, orderCode.length() - 1);
					}
					String sendResult = wxGateSupport.sendOrderNoticeByGzh9(receivers, "您有订单需要发货", orderCodes, num + "笔", "");
					bLogInfo(0, "调用微信公众号发送提醒发货成功通知响应结果:" + sendResult);
				}
			}
		}
	}
}
