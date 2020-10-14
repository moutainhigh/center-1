package com.cmall.ordercenter.webfunc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webexport.RootExport;
import com.srnpr.zapweb.webmodel.MPageData;

/**
 * 导出E店宝所需要的订单
 * @author jlin
 *
 */
public class OrderForEExport extends RootExport {

	
	private MDataMap area3Map = new MDataMap();
	
	public void export(String sOperateId, HttpServletRequest request,HttpServletResponse response) {

		exportExcel(sOperateId, request, response);
		setExportName("order"+FormatHelper.upDateTime(new Date(), "yyMMddHHmmss"));//修改文件名
		
		//修改数据
		MPageData pageData=getPageData();
		
		
		List<String> head_list=pageData.getPageHead();
		//重新写入头
		head_list.clear();
		head_list.add("订单号");
		head_list.add("出库时间");
		head_list.add("通路");
		head_list.add("产品条码(商品)");
		head_list.add("商品名称");
		head_list.add("产品规格");
		head_list.add("商品单价");
		head_list.add("商品数量");
		head_list.add("商品总价");
		head_list.add("购买优惠信息");
		head_list.add("客户付邮");
		head_list.add("总金额");
		head_list.add("商品成本单价");
		head_list.add("商品成本总价");
		head_list.add("订单状态");
		head_list.add("物流公司");
		head_list.add("物流单号");
		head_list.add("运费");
		head_list.add("付款时间");
		head_list.add("买家昵称");
		head_list.add("买家编号");
		head_list.add("收货人姓名");
		head_list.add("收货地址-省市");
		head_list.add("收货地址-街道地址");
		head_list.add("收货人手机");
		
		
		if(area3Map.size()<1){
			setArea3();
		}
		
		//重写数据
		List<List<String>> pd=pageData.getPageData();
		List<List<String>> data = new ArrayList<List<String>>();
		
		for (List<String> ppd : pd) {
			
			String order_code=ppd.get(0);//订单编号
			String buyer_code=ppd.get(1);//买家id 
			String order_status=ppd.get(4);//订单状态 
			String pay_time =ppd.get(11);//付款时间
			String create_time = "";//订单出库时间
			String logisticse_name = "";//物流名称
			String waybill = "";//物流单号
			
			//查询订单详情
			List<MDataMap> order_detail_list=DbUp.upTable("oc_orderdetail").queryAll("sku_code,sku_price,sku_num", "", "order_code=:order_code", new MDataMap("order_code",order_code));
			if(order_detail_list==null||order_detail_list.size()<1){
				continue;
			}
			
			MDataMap orderMap = DbUp.upTable("oc_order_shipments").one("order_code",order_code);
			
			if(orderMap!=null){
				
				create_time = orderMap.get("create_time");
				
				logisticse_name = orderMap.get("logisticse_name");
				
				waybill= orderMap.get("waybill");
			}
			
			
			for (MDataMap order_detail : order_detail_list) {
				String sku_code = order_detail.get("sku_code");
				String sku_price = order_detail.get("sku_price");
				String sku_num = order_detail.get("sku_num");
				
				//查询商品信息
				MDataMap product_data = null;
				//查询买家昵称信息
				String nickname = null;
				//查询订单信息
				MDataMap order_data = null;
				//查询地址信息
				MDataMap address_data = null;
				//查询区域信息
				String area_data = null;
				
				String product_code= "";
				
				StringBuffer sb=new StringBuffer("");
				
				try {
					product_code=(String)DbUp.upTable("pc_skuinfo").dataGet("product_code", "sku_code=:sku_code", new MDataMap("sku_code",sku_code));//查询商品id
					
					product_data = DbUp.upTable("pc_productinfo").queryByWhere("product_code",product_code).get(0);
					
					order_data = DbUp.upTable("oc_orderinfo").queryByWhere("order_code",order_code).get(0);
					
					address_data = DbUp.upTable("oc_orderadress").queryByWhere("order_code",order_code).get(0);
					
					area_data = area3Map.get(address_data.get("area_code"));
					
				} catch (Exception e) {
					continue;//防止出现数据不完整的情况
				}
				
				try {
					nickname = (String)DbUp.upTable("mc_extend_info_star").dataGet("nickname", "member_code=:member_code", new MDataMap("member_code",StringUtils.trimToEmpty(buyer_code)));
				} catch (Exception e) {
					nickname="";
				}
				
				// 7 为  产品规格
				if(StringUtils.isNotBlank(product_code)){
					List<MDataMap> list=DbUp.upTable("pc_productproperty").queryAll("property_key,property_value", "", "product_code=:product_code", new MDataMap("product_code",product_code));
					if(list!=null&&list.size()>0){
						
						for (MDataMap mm : list) {
							String property_key=mm.get("property_key");
							String property_value=mm.get("property_value");
							sb.append(",").append(property_key).append("=").append(property_value);
						}
						
						if(sb.length()>0){
							sb=sb.deleteCharAt(0);
						}
						ppd.set(7, sb.toString());
					}
				}
				
				List<String> dd= new ArrayList<String>(34);
				dd.add(order_code);//订单编号
				dd.add(create_time);//出库时间
				dd.add("");//通路
				dd.add(product_data.get("sell_productcode"));//产品条码(商品)
				dd.add(product_data.get("product_name"));//商品名称
				dd.add(sb.toString());//产品规格
				dd.add(sku_price);//商品单价
				dd.add(sku_num);//商品数量
				dd.add(String.valueOf(Double.valueOf(sku_price)*Double.valueOf(sku_num)));//商品总价
				dd.add("");//购买优惠信息
				dd.add(order_data.get("transport_money"));//客户付邮
				dd.add(String.valueOf(Double.valueOf(sku_price)*Double.valueOf(sku_num)+Double.valueOf(order_data.get("transport_money"))));//总金额
				dd.add(String.valueOf(Double.valueOf(product_data.get("cost_price"))));//商品成本单价
				dd.add(String.valueOf(Double.valueOf(product_data.get("cost_price"))*Double.valueOf(product_data.get("cost_price"))));//商品成本总价
				dd.add(order_status);//订单状态
				dd.add(logisticse_name);//物流公司
				dd.add(waybill);//物流单号
				dd.add("");//运费
				dd.add(pay_time);//付款时间
				dd.add(nickname);//买家昵称
				dd.add(buyer_code);//买家编号
				dd.add(address_data.get("receive_person"));//收货人姓名
				dd.add(area_data);//收货地址-省市
				dd.add(address_data.get("address"));//收货地址-街道地址
				dd.add(address_data.get("mobilephone"));//收货人手机
				
				
				data.add(dd);
			}
		
		}
		
		pageData.setPageData(data);
		
		doExport();
	}
	
	private void setArea3(){
		List<MDataMap> list=DbUp.upTable("v_sc_gov_area3").queryByWhere();
		for (MDataMap mm : list) {
			area3Map.put(mm.get("area_code"), mm.get("name"));
		}
	}
	
}
