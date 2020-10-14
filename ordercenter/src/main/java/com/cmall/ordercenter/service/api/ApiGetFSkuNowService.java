package com.cmall.ordercenter.service.api;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.FlashsalesSkuInfo;
import com.cmall.ordercenter.model.api.ApiGetFSkuNowInput;
import com.cmall.ordercenter.model.api.ApiGetFSkuNowResult;
import com.cmall.ordercenter.service.FlashsalesService;
import com.cmall.productcenter.service.ProductStoreService;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
/**
 * 获取今天和明天的闪购商品的信息
 * @author jl
 *
 */
public class ApiGetFSkuNowService extends RootApiForManage<ApiGetFSkuNowResult,ApiGetFSkuNowInput> {

	public ApiGetFSkuNowResult Process(ApiGetFSkuNowInput inputParam, MDataMap mRequestMap) {
		ApiGetFSkuNowResult result = new ApiGetFSkuNowResult();
		Map<String,FlashsalesSkuInfo> retMap=new HashMap<String, FlashsalesSkuInfo>();
		
		/*
		 * 查询闪购的栏目信息
		 */
		MDataMap cData = DbUp.upTable("fh_category").one("category_code",inputParam.getActivity());
		if(cData != null){
			//栏目图片地址
			result.setBanner_img(cData.get("line_head") ==null ? "" : cData.get("line_head"));
			//栏目名称
			result.setBanner_name(cData.get("category_name") ==null ? "" : cData.get("category_name"));
			//判断栏目类型
			if("449747030001".equals(cData.get("link_address"))) {
				result.setBanner_link(cData.get("link_url") ==null ? "" : cData.get("link_url"));
			} else {
				result.setBanner_link(cData.get("product_link") ==null ? "" : cData.get("product_link"));
			}
		}
		
		
		String sql= " SELECT k.sell_price,k.vip_price,k.sku_name,k.sku_code,k.start_time,k.end_time, k.on_status,k.activity_code,k.location from ( "+
				"(SELECT  s.sell_price,s.vip_price,s.sku_name,s.sku_code,a.start_time,a.end_time,0 as on_status,a.activity_code,s.location from oc_flashsales_skuInfo s LEFT JOIN oc_activity_flashsales a ON s.activity_code=a.activity_code where " +
				" a.activity_code=(select activity_code from oc_activity_flashsales where start_time<=:nowtime and end_time>=:nowtime and  status='449746740002' and app_code=:app_code order by end_time desc limit 0,1 )  " +
				" and s.status!='449746810002' and a.app_code=:app_code order by s.location asc,s.product_code asc ) " +
				" UNION all " +
				" (SELECT  s.sell_price,s.vip_price,s.sku_name,s.sku_code,a.start_time,a.end_time,1 as on_status,a.activity_code,s.location from oc_flashsales_skuInfo s LEFT JOIN oc_activity_flashsales a ON s.activity_code=a.activity_code where " +
				" a.activity_code=(select activity_code from oc_activity_flashsales where  end_time<:nowtime and  app_code=:app_code and status='449746740002'  order by end_time desc limit 0,1)  " +
				" and s.status!='449746810002' and a.app_code=:app_code order by s.location asc,s.product_code asc ) " +
				" UNION all " +
				" (SELECT  s.sell_price,s.vip_price,s.sku_name,s.sku_code,a.start_time,a.end_time,-1 as on_status,a.activity_code,s.location  from oc_flashsales_skuInfo s LEFT JOIN oc_activity_flashsales a ON s.activity_code=a.activity_code where " +
				" a.activity_code=(select activity_code from oc_activity_flashsales where start_time>:nowtime and  app_code=:app_code and status='449746740002' order by start_time asc limit 0,1) " +
				"  and s.status!='449746810002' and a.app_code=:app_code order by s.location asc,s.product_code asc  ) " +
				" ) k ORDER BY k.start_time  " ;
		
		List<Map<String, Object>> list=DbUp.upTable("oc_activity_flashsales").dataSqlList(sql, new MDataMap("nowtime",FormatHelper.upDateTime(),"app_code",MemberConst.MANAGE_CODE_HOMEHAS));
		if(list!=null&&list.size()>0){
			DecimalFormat  pricedf  = new DecimalFormat("######0");
			for (Map<String, Object> map : list) {
				FlashsalesSkuInfo skuInfo=new FlashsalesSkuInfo();
				skuInfo.setEnd_time((String)map.get("end_time"));
				skuInfo.setStart_time((String)map.get("start_time"));
				skuInfo.setSku_code((String)map.get("sku_code"));
				skuInfo.setSku_name((String)map.get("sku_name"));
				skuInfo.setVip_price((BigDecimal)map.get("vip_price"));
				skuInfo.setSell_price((BigDecimal)map.get("sell_price"));
				skuInfo.setOn_status(Integer.valueOf(map.get("on_status")+""));
				skuInfo.setDiscount("￥"+skuInfo.getSell_price().subtract(skuInfo.getVip_price()).doubleValue());
				skuInfo.setActivity_code((String)map.get("activity_code"));
				skuInfo.setLocation((Integer)map.get("location"));
				
				if(skuInfo.getSell_price().compareTo(BigDecimal.ZERO)>0&&skuInfo.getVip_price().compareTo(BigDecimal.ZERO)>0)
				{
					skuInfo.setDiscountRate(""+skuInfo.getVip_price().multiply(new BigDecimal(100)).divide(skuInfo.getSell_price(), 0, BigDecimal.ROUND_HALF_UP));
				}
				//通过sku_code 查询商品信息
				String product_status= "";
				List<Map<String, Object>> list1=DbUp.upTable("pc_skuinfo").dataSqlList("SELECT p.video_url,p.mainpic_url,p.product_status,p.market_price,p.product_code,p.product_name from pc_skuinfo s LEFT JOIN pc_productinfo p on s.product_code=p.product_code where s.sku_code=:sku_code", new MDataMap("sku_code",skuInfo.getSku_code()));
				if(list1!=null&&list1.size()>0){
					//设置商品信息
					Map<String, Object> pc=list1.get(0);
					String video_url=(String)pc.get("video_url");
					String mainpic_url=(String)pc.get("mainpic_url");
					product_status=(String)pc.get("product_status");
					BigDecimal market_price=(BigDecimal)pc.get("market_price");
					
					//过滤市场价为0的商品  ligj
					if (market_price.compareTo(new BigDecimal(0)) == 0) {
						continue;
					}
					
					if(!"4497153900060002".equals(product_status)){  //只要已上架的商品
						continue;
					}
					
					skuInfo.setSell_price(market_price);//这里把销售价格替换成市场价格
					
					if(video_url==null||"".equals(video_url)){
						skuInfo.setIs_video(0);
					}else{
						skuInfo.setIs_video(1);
						
					}
					skuInfo.setSell_count(0);//现在无此信息，返回值统一为0
					skuInfo.setImg_url(mainpic_url);
					skuInfo.setActivity_url("");//返回""值
					skuInfo.setFile_url(video_url);
					skuInfo.setGoods_link(FormatHelper.formatString(bConfig("ordercenter.product_detail"),skuInfo.getSku_code(),skuInfo.getSku_code()));
					
					
					//由原SKU信息变为商品信息 若需还原，将以下代码注掉即可
					//start
					String product_code=(String)pc.get("product_code");
					if(retMap.containsKey(product_code+"_"+skuInfo.getOn_status())){ //如果已经存在次商品，就不再添加,然后把sku的最小价格添加的商品属性中
						BigDecimal vip_price_new=skuInfo.getVip_price();
						FlashsalesSkuInfo sku_old=retMap.get(product_code+"_"+skuInfo.getOn_status());
						if(vip_price_new.compareTo(sku_old.getVip_price())<0){
							sku_old.setVip_price(vip_price_new);
						}
						continue;
					}
					
					skuInfo.setSku_code(product_code);
					skuInfo.setSku_name((String)pc.get("product_name"));
					skuInfo.setGoods_link(FormatHelper.formatString(bConfig("ordercenter.product_detail"),skuInfo.getSku_code(),skuInfo.getSku_code()));
					//end
					
					if(skuInfo.getSell_price().compareTo(BigDecimal.ZERO)>0&&skuInfo.getVip_price().compareTo(BigDecimal.ZERO)>0)
					{
					skuInfo.setDiscountRate(""+skuInfo.getVip_price().multiply(new BigDecimal(100)).divide(skuInfo.getSell_price(), 0, BigDecimal.ROUND_HALF_UP));
					}
					else {
						skuInfo.setDiscountRate("100");
					}
					retMap.put(skuInfo.getSku_code()+"_"+skuInfo.getOn_status(), skuInfo);
				}
				
				
			}
		}
		
		List<FlashsalesSkuInfo> skuList_1=new ArrayList<FlashsalesSkuInfo>();
		List<FlashsalesSkuInfo> skuList0=new ArrayList<FlashsalesSkuInfo>();
		List<FlashsalesSkuInfo> skuList1=new ArrayList<FlashsalesSkuInfo>();
		
		for (Map.Entry<String,FlashsalesSkuInfo> entry : retMap.entrySet()) {
			FlashsalesSkuInfo skuInfo=entry.getValue();
//			skuInfo.setSell_count(sellCount(skuInfo.getActivity_code(), skuInfo.getSku_code()));
//			skuInfo.setSales_num(salesNum1(skuInfo.getActivity_code(), skuInfo.getSku_code()));
			
			skuInfo.setSell_count(0);
			skuInfo.setSales_num(0);
			
			if(skuInfo.getOn_status()==1){
				skuList1.add(skuInfo);
			}else if(skuInfo.getOn_status()==-1){
				skuList_1.add(skuInfo);
			}else if(skuInfo.getOn_status()==0){
				skuList0.add(skuInfo);
			}
//			result.getList().add(skuInfo);
		}
		
		sortSkuList(skuList_1);
		sortSkuList(skuList0);
		sortSkuList(skuList1);
		
		
		result.getList().addAll(skuList1);
		result.getList().addAll(skuList0);
		result.getList().addAll(skuList_1);
//		result.setList(list1);
		result.setSystemTime(FormatHelper.upDateTime());
		return result;
	}
	
	
	private void sortSkuList(List<FlashsalesSkuInfo> list){
		
		Collections.sort(list, new Comparator<FlashsalesSkuInfo>() {
			public int compare(FlashsalesSkuInfo o1, FlashsalesSkuInfo o2) {
				String hits0 = lpad(4,o1.getLocation())+o1.getSku_code();
				String hits1 =   lpad(4,o2.getLocation())+o2.getSku_code();
				if (hits1.compareTo(hits0) < 0) {
					return 1;
				} else if (hits1.compareTo(hits0)==0) {
					return 0;
				} else {
					return -1;
				}
			}
		});
		
	}
	
	 private String lpad(int length, int number) {
         String f = "%0" + length + "d";
         return String.format(f, number);
     }

	
	private int sellCount(String activity_code,String product_code){
		
		ProductStoreService productStoreService = new ProductStoreService();
		
		
		FlashsalesService flashsalesService = new FlashsalesService();
		
		MDataMap activityMap=DbUp.upTable("oc_activity_flashsales").one("activity_code",activity_code);
		String now=DateUtil.getSysDateTimeString();
		String start_time=activityMap.get("start_time");
		String end_time=activityMap.get("end_time");
		
		List<MDataMap> fskuList=DbUp.upTable("oc_flashsales_skuInfo").queryByWhere("activity_code",activity_code,"product_code",product_code,"status","449746810001");
		
//		闪购已抢百分比=商品已上闪购时长/闪购总时长-随机数（0-20%）+该商品闪购销量/该商品闪购销量上限*（1-商品已上闪购时长/闪购总时长+随机数（与之前随机数相同））
		int allNum=0;
		for (MDataMap fskuMap : fskuList) {
			
			String update_time=fskuMap.get("update_time");
			String sku_code=fskuMap.get("sku_code");
//			String purchase_limit_day_num=fskuMap.get("purchase_limit_day_num");
			String sales_num=fskuMap.get("sales_num");
			
			
			if(DateUtil.compareTime(start_time, update_time, DateUtil.DATE_FORMAT_DATETIME)>0){
				update_time=start_time;
			}
			
			try {
				
				BigDecimal time1=new BigDecimal(String.valueOf(DateUtil.subtime(update_time, now, DateUtil.DATE_FORMAT_DATETIME)));//商品已上闪购时长
				BigDecimal time2=new BigDecimal(String.valueOf(DateUtil.subtime(start_time, end_time, DateUtil.DATE_FORMAT_DATETIME)));//闪购总时长
				BigDecimal random=new BigDecimal(String.valueOf(random()));//随机数
//				BigDecimal random=new BigDecimal("0.17");
				BigDecimal saleNum1=new BigDecimal(String.valueOf(flashsalesService.salesNum(activity_code, sku_code, MemberConst.MANAGE_CODE_HOMEHAS)));//该商品闪购销量
				BigDecimal saleNum2=new BigDecimal(sales_num);//该商品闪购销量上限
				
				bLogInfo(0, "惠家有-测试-一闪购已抢百分比-随机数："+random+"|sku_code:"+sku_code+"|activity_code:"+activity_code);
				
//				如果活动销售量==促销库存 ，则卖出数量=活动销售量
				if(saleNum1.compareTo(saleNum2)>=0){
					allNum+=saleNum2.intValue();
					continue;
				}
				
				
				int stock_num=productStoreService.getStockNumBySku(sku_code);
				if(stock_num==0&&BigDecimal.ZERO.compareTo(saleNum1)==0){
					continue;
				}
				
				if(stock_num==0&&BigDecimal.ZERO.compareTo(saleNum1)<0){
					saleNum2=saleNum1;
				}
				
				//-------------------------
				BigDecimal prdfixNum=time1.divide(time2,2,BigDecimal.ROUND_HALF_UP).subtract(random);
				
				BigDecimal res=(saleNum1.divide(saleNum2,2,BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.ONE.subtract(prdfixNum)).add(prdfixNum)).multiply(saleNum2);
				
				int i=res.intValue();
				
				if(i<0){
					i=0;
				}
				allNum+=i;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		return allNum;
	}
	
	private double random() {
		Random random = new Random();
		int i=random.nextInt(20);
		double ran=i/100.00;
		return ran;
	}
	
	private int salesNum(String activity_code,String product_code){
		Map<String, Object>  map=DbUp.upTable("oc_flashsales_skuInfo").dataSqlOne("SELECT sum(sales_num) num from oc_flashsales_skuInfo where status='449746810001' and activity_code=:activity_code and product_code=:product_code", new MDataMap("activity_code",activity_code,"product_code",product_code));
		if(map!=null&&map.size()>0){
			
			BigDecimal num=(BigDecimal)map.get("num");
			if(num!=null){
				return num.intValue();
			}
		}
		return 0;
	}
	
	
	private int salesNum1(String activity_code,String product_code){
		
		ProductStoreService productStoreService = new ProductStoreService();
		int num=0;
		
		List<MDataMap> list=DbUp.upTable("oc_flashsales_skuInfo").queryAll("sales_num,sku_code", "", "status='449746810001' and activity_code=:activity_code and product_code=:product_code", new MDataMap("activity_code",activity_code,"product_code",product_code));
		if(list!=null&&list.size()>0){
			for (MDataMap mDataMap : list) {
				String sku_code=mDataMap.get("sku_code");
				int sales_num=Integer.valueOf(mDataMap.get("sales_num"));
				//判断库存是否为0
				if(productStoreService.getStockNumBySku(sku_code)<=0){
					continue;
				}
				
				num+=sales_num;
			}
			
		}
		
		return num;
	}
	
	
	public static void main(String[] args) {
		ApiGetFSkuNowService apiGetFSkuNowService = new ApiGetFSkuNowService();
		//System.out.println(apiGetFSkuNowService.sellCount("SG150526100016", "132041"));
	}
	
}
