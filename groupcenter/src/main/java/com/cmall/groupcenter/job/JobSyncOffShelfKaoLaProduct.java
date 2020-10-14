package com.cmall.groupcenter.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.util.Hash;
import org.quartz.JobExecutionContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.homehas.RsyncKaoLaSupport;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.TopConfig;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webwx.WxGateSupport;


/** 
 * 
 *<p>Description:同步处理下架考拉商品池中不存在的商品 <／p> 
 * @author zb
 * @date 2020年6月29日
 *
 */

public class JobSyncOffShelfKaoLaProduct extends RootJob {

	public void doExecute(JobExecutionContext context) {
		//调用封装的考拉接口进行数据获取(只同步一般贸易importType:3)
				TreeMap<String,String> treeMap = new TreeMap<String, String>();
				String string = RsyncKaoLaSupport.doPostRequest("queryAllGoodsIdAndSkuId","channelId",treeMap);
				JSONObject jb = JSON.parseObject(string);
				
				if(jb == null || jb.getIntValue("recCode") != 200){
					LogFactory.getLog(getClass()).error("定时JobSyncOffShelfKaoLaProduct调用 考拉接口调用 queryAllGoodsIdAndSkuId失败 ");
				}
				
				else {
					JSONObject goodsInfoList = jb.getJSONObject("goodsInfo");
					Set<String> keys = goodsInfoList.keySet();
	                //考拉商品池中推荐的所有商品
					List<Map<String, Object>> dataSqlList = DbUp.upTable("pc_productinfo").dataSqlList("select uid,product_code,product_code_old from pc_productinfo where small_seller_code=:small_seller_code and product_status='4497153900060002' ", new MDataMap("small_seller_code",TopConfig.Instance.bConfig("familyhas.seller_code_KL")));
					List<String> messageList = new ArrayList<>();
					Map<String,String> uidMap = new HashMap<String, String>();
					for (Map<String, Object> map : dataSqlList) {
						if(!keys.contains(map.get("product_code_old"))) {
							messageList.add(map.get("product_code").toString());
							uidMap.put(map.get("product_code").toString(), map.get("uid").toString());
						}
					}
					if(messageList.size()>0) {
						/*String whereStr = " product_code in  ('"+StringUtils.join(messageList,"','")+"') ";
						DbUp.upTable("pc_productinfo").dataExec("update pc_productinfo set product_status='4497153900060003' where "+whereStr,null );*/
						ProductJmsSupport productJmsSupport = new  ProductJmsSupport();
						FlowBussinessService fs = new FlowBussinessService();
						for (String productCode : messageList) {
							//添加批量下架审核记录
							/*UUID uuid2 = UUID.randomUUID();
							MDataMap insertDatamap = new MDataMap();
							insertDatamap.put("uid", uuid2.toString().replace("-", ""));
							insertDatamap.put("flow_code", uidMap.get(productCode).toString());
							insertDatamap.put("flow_type", "449717230011");
							insertDatamap.put("creator", TopConfig.Instance.bConfig("familyhas.seller_code_KL"));
							insertDatamap.put("create_time", com.cmall.ordercenter.common.DateUtil.getSysDateTimeString());
							insertDatamap.put("flow_remark", "考拉商品池商品下架-->同步定时批量下架");
							insertDatamap.put("current_status", "4497153900060003");
							DbUp.upTable("sc_flow_bussiness_history").dataInsert(insertDatamap);*/
							//修改状态变化方法
							fs.ChangeFlow(uidMap.get(productCode).toString(), "449715390006", "4497153900060002", "4497153900060003","system", "考拉商品池商品下架-->同步定时批量下架", new MDataMap());
							PlusHelperNotice.onChangeProductInfo(productCode);
							productJmsSupport.updateSolrData(productCode);
						}				
						// 发送通知
						WxGateSupport support = new WxGateSupport();
						String receivers = support.bConfig("groupcenter.jd_notice_receives_product");
						List<String> list = support.queryOpenId(receivers);
						for (String receiver : list) {
							support.sendWarnCountMsg("商品变更通知", "考拉商品池数据变更", receiver, "商品池下架商品编号为：{"+StringUtils.join(messageList,",")+"}");
						}
						
					}
				}
	}
}
