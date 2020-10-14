package com.cmall.groupcenter.job;

import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.func.BeautyOrderUtil;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 同步E店宝订单状态和信息
 * 
 * @author shiyz 每一小时
 */
public class JobOrderForBeauty extends RootJob {

	public void doExecute(JobExecutionContext context) {

		// TODO Auto-generated method stub

		String sCode = WebHelper.upCode("CLOG");

		BeautyOrderUtil util = new BeautyOrderUtil();

		String xmlValue = util.getOrderStauts();

		if (!xmlValue.equals("error")) {

			Document doc = null;

			try {
				// 将字符串转为XML
				doc = DocumentHelper.parseText(xmlValue);
				// 获取根节点
				Element rootElt = doc.getRootElement();

				// 获取根节点下的子节点head
				Iterator iter = rootElt.elementIterator("Rows");

				MDataMap map = new MDataMap();

				while (iter.hasNext()) {

					Element recordEle = (Element) iter.next();
					// 拿到head节点下的子节点title值
					String out_tid = recordEle.elementTextTrim("out_tid");
					// 获取子节点head下的子节点script

					int count = DbUp.upTable("oc_orderinfo").count(
							"order_code", out_tid);

					String sSql = "select * from ordercenter.oc_orderinfo oc,logcenter.lc_beauty_order_log lc where lc.request_ordercode ='"
							+ out_tid
							+ "' and oc.order_code='"
							+ out_tid
							+ "' ";

					Map<String, Object> map2 = DbUp.upTable("oc_orderinfo")
							.dataSqlOne(sSql, new MDataMap());

					if (count != 0) {

						if (null == map2) {

							MDataMap mDataMap = new MDataMap();

							map.put("scode", sCode);

							map.put("request_ordercode", out_tid);

							map.put("flag_success", "0");

							map.put("request_time", FormatHelper.upDateTime());

							mDataMap.put("order_code", out_tid);

							/* 更新订单状态 */
							mDataMap.put("order_status", "4497153900010003");

							DbUp.upTable("oc_orderinfo").dataUpdate(mDataMap,
									"order_status", "order_code");

							/* 插入订单日志 */
							DbUp.upTable("lc_beauty_order_log").dataInsert(map);

						}
					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
