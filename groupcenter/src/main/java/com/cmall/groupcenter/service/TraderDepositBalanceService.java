package com.cmall.groupcenter.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import kafka.controller.OnlinePartition;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cmall.dborm.txmapper.groupcenter.GcTraderDepositBalanceMapper;
import com.cmall.dborm.txmodel.groupcenter.GcTraderDepositBalance;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmethod.WebUpload;
import com.srnpr.zapweb.webmodel.MPageData;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 预存款结算相关
 * @author panwei
 *
 */
public class TraderDepositBalanceService extends BaseClass{

	/**
	 * 预存款结算
	 */
	public void doDepositBalance(){
		//当前时间减一个月获取上月时间,默认时间
		Timestamp time=DateUtil.addMonths(DateUtil.getSysDateTimestamp(), -1);
		//取一下配置时间，灵活调整
		MDataMap mDataMap=DbUp.upTable("gc_config_content").one("config_name","trader_month_balance_set_month");
		if(mDataMap!=null&&StringUtils.isNotBlank(mDataMap.get("config_content"))){
			time=DateUtil.toSqlTimestamp(mDataMap.get("config_content"));
		}
		//上个月第一天
		Timestamp startDate=DateUtil.getFirstDayOfMonth(time);
		//这个月最后一天
		Timestamp endDate=DateUtil.getFirstDayOfMonth(DateUtil.addMonths(time, 1));
		String balanceDate=DateUtil.getYearOfTimestamp(time)+"-"+DateUtil.getMonthOfTimestamp(time);
		
		
		List<MDataMap> traderList=DbUp.upTable("gc_trader_info").queryAll("", "", "", new MDataMap());
		for(MDataMap trader:traderList){
			//判断该商户该月是否有数据
			//该商户是否有数据或是否可再生成数据
			MDataMap dataMap=DbUp.upTable("gc_trader_deposit_balance").one("trader_code", trader.get("trader_code"),"balance_date",balanceDate,"balance_status","1");
			if(dataMap==null||dataMap.get("create_flag").equals("1")){
				MDataMap balanceMap=new MDataMap();
				balanceMap.put("start_date", DateUtil.toString(startDate));
				balanceMap.put("end_date", DateUtil.toString(endDate));
				balanceMap.put("trader_code", trader.get("trader_code"));
				balanceMap.put("trader_name", trader.get("trader_name"));
				
				
				//当月结算
				List<Map<String,Object>> balanceList=DbUp.upTable("gc_trader_deposit_log").dataSqlList("SELECT sum(deposit) deposit,count(DISTINCT(order_code)) order_count,trader_code,'4497472500040001' deposit_type "
						+ "FROM	gc_trader_deposit_log WHERE	create_time >=:start_date AND create_time < :end_date and flag_status=1 AND trader_code = :trader_code AND deposit_type in('4497472500040003','4497472500040001') "
						+" UNION SELECT sum(deposit) deposit,count(DISTINCT(order_code)) order_count,trader_code,deposit_type "
								+ "FROM	gc_trader_deposit_log WHERE	create_time >=:start_date AND create_time < :end_date and flag_status=1 AND trader_code = :trader_code AND deposit_type in('4497472500040002')", balanceMap);
				int orderCount=0;//订单总量
				int rebateCount=0;//返利订单数量
				int returnCount=0;//退货订单数量
				BigDecimal rebateSumMoney=BigDecimal.ZERO;//返利总金额
				BigDecimal rebateMoney=BigDecimal.ZERO;//返利金额
				BigDecimal returnMoney=BigDecimal.ZERO;//返还预存款金额
				for(Map<String,Object> balance:balanceList){
					if(balance.get("order_count")!=null&&Integer.parseInt(balance.get("order_count").toString())!=0){
						//返利+取消退货
						if(balance.get("deposit_type").equals("4497472500040001")){
							rebateCount+=Integer.parseInt(balance.get("order_count").toString());
							rebateMoney=rebateMoney.add(new BigDecimal(balance.get("deposit").toString()).abs());
						}
						//退货
						if(balance.get("deposit_type").equals("4497472500040002")){
							returnCount+=Integer.parseInt(balance.get("order_count").toString());
							returnMoney=returnMoney.add(new BigDecimal(balance.get("deposit").toString()).abs());
						}
					}
				}
				
				//人工调整部分
				String sSql="select sum(gurrantee_change_amount) deposit from gc_trader_founds_change_log where change_type=:change_type and trader_code=:trader_code and create_time >=:start_date AND create_time < :end_date";
				MDataMap mWhereMap=new MDataMap();
				//扣除预存款(返利)
				mWhereMap.put("start_date", DateUtil.toString(startDate));
				mWhereMap.put("end_date", DateUtil.toString(endDate));
				mWhereMap.put("change_type", "4497472500030006");
				mWhereMap.put("trader_code", trader.get("trader_code"));
				Map<String,Object> pRebate=DbUp.upTable("gc_trader_founds_change_log").dataSqlOne(sSql, mWhereMap);
				if(pRebate!=null&&pRebate.get("deposit")!=null){
					BigDecimal pRebateMoney=new BigDecimal(pRebate.get("deposit").toString()).abs();
					rebateMoney=rebateMoney.add(pRebateMoney);
				}
				
				//增加预存款
				mWhereMap.put("change_type", "4497472500030005");
				Map<String,Object> pReturn=DbUp.upTable("gc_trader_founds_change_log").dataSqlOne(sSql, mWhereMap);
				if(pReturn!=null&&pReturn.get("deposit")!=null){
					BigDecimal pReturnMoney=new BigDecimal(pReturn.get("deposit").toString()).abs();
					returnMoney=returnMoney.add(pReturnMoney);
				}
				
				orderCount=rebateCount+returnCount;
				rebateSumMoney=rebateMoney.subtract(returnMoney);
				if(orderCount>0){
					//详情数据
					String link=findDepositDetail(balanceMap);
					
					DbUp.upTable("gc_trader_deposit_balance").insert("uid",WebHelper.upUuid(),
							"trader_code",trader.get("trader_code"),
							"trader_name",trader.get("trader_name"),
							"balance_date",balanceDate,
							"order_count",String.valueOf(orderCount),
							"rebate_order_count",String.valueOf(rebateCount),
							"return_order_count",String.valueOf(returnCount),
							"rebate_sum_money",rebateSumMoney.toString(),
							"rebate_money",rebateMoney.toString(),
							"return_money",returnMoney.toString(),
							"detail_link",link,
							"balance_status","1",
							"create_flag","0",
							"create_time",FormatHelper.upDateTime());
					
					//如有旧有效数据，更改状态为无效
					if(dataMap!=null){
						MDataMap update=new MDataMap();
						update.put("zid", dataMap.get("zid"));
						update.put("balance_status", "0");
						update.put("create_flag", "0");
						DbUp.upTable("gc_trader_deposit_balance").dataUpdate(update, "balance_status,create_flag", "zid");
					}
				}
			}
		}
	}

	/**
	 * 获取详情数据（生成excel上传服务器）
	 * @param balanceMap
	 * @return
	 */
	private String findDepositDetail(MDataMap balanceMap) {
		
		StringBuffer sqlStr=new StringBuffer("SELECT ");
		sqlStr.append("info.out_order_code 'out_order_code',gdl.order_code 'order_code',info.order_create_time 'order_create_time',");
		sqlStr.append("info.member_code 'member_code',gdl.account_code 'account_code',gdl.order_account_code 'order_account_code',");
		sqlStr.append("IF(gdl.relation_level='0','是','否') 'relation_level',IF(gdl.deposit_type='4497472500040002','返还预存款','扣减预存款') 'deposit_type',gdl.create_time 'create_time',gdl.sku_code 'sku_code',");
		sqlStr.append("gdl.deposit 'rebate_money',log.scale_reckon 'scale_reckon', ");
		sqlStr.append("case rebate.order_status  WHEN '4497153900010001' THEN '下单成功-未付款' WHEN '4497153900010002' THEN '下单成功-未发货' WHEN '4497153900010003' THEN '已发货' ");
		sqlStr.append("WHEN '4497153900010004' THEN '已收货' WHEN '4497153900010005' THEN '交易成功' WHEN '4497153900010006' THEN '交易失败' ELSE '' END 'order_status', ");
		sqlStr.append("FORMAT(log.rebate_money/log.scale_reckon,2) 'sum_reckon_money', ");
		sqlStr.append("channel.channel_name 'channel_name' ");
		sqlStr.append("FROM gc_trader_deposit_log gdl  LEFT JOIN gc_reckon_order_info info ON gdl.order_code = info.order_code ");
		sqlStr.append("LEFT JOIN gc_rebate_order rebate ON CONCAT(gdl.account_code,'_',gdl.order_code)=rebate.uq_code ");
		sqlStr.append("left JOIN gc_rebate_log log ON gdl.relation_code=log.log_code "
				+ "LEFT JOIN gc_channel_manage channel ON channel.channel_code=info.channel_code ");
		sqlStr.append("WHERE gdl.create_time >= :start_date AND gdl.create_time < :end_date AND gdl.trader_code = :trader_code and gdl.flag_status=1 ");
		//详情数据
		List<Map<String,Object>> depositDetailList=DbUp.upTable("gc_trader_deposit_log").dataSqlList(sqlStr.toString(), balanceMap);
		
		//人工调整数据
		StringBuffer sqlP=new StringBuffer("");
		sqlP.append("select info.out_order_code 'out_order_code',log.order_code 'order_code',NULL 'order_create_time',member.member_code 'member_code', ");
		sqlP.append("log.account_code 'account_code',NULL 'order_account_code',NULL 'relation_level',IF(log.change_type='4497472500030005','人工加钱','人工减钱') 'deposit_type', ");
		sqlP.append("log.create_time 'create_time',NULL 'sku_code',log.gurrantee_change_amount 'rebate_money',NULL 'scale_reckon',NULL 'order_status',NULL 'sum_reckon_money', ");
		sqlP.append("NULL 'channel_name' from gc_trader_founds_change_log log LEFT JOIN gc_reckon_order_info info ON log.order_code = info.order_code ");
		sqlP.append("LEFT JOIN membercenter.mc_member_info member on log.account_code=member.account_code and member.manage_code='SI2011' ");
		sqlP.append("where log.change_type in ('4497472500030005','4497472500030006')  and log.create_time>=:start_date and log.create_time<:end_date AND log.trader_code = :trader_code");
		
		List<Map<String,Object>> personDetailList=DbUp.upTable("gc_trader_founds_change_log").dataSqlList(sqlP.toString(), balanceMap);
		
		depositDetailList.addAll(personDetailList);
		if(depositDetailList!=null&&depositDetailList.size()>0){
			String exportName = balanceMap.get("trader_name")+"预存款对账明细" + "-"
					+ FormatHelper.upDateTime(new Date(), "yyyy-MM-dd-HH-mm-ss");
			MPageData pageData=new MPageData();
			//拼凑表头
			List<String> headList=new ArrayList<String>();
			headList.add("订单号");
			headList.add("清分编号");
			headList.add("订单状态");
			headList.add("下单时间");
			headList.add("用户编号");
			headList.add("渠道名称");
			headList.add("预计返利账号");
			headList.add("订单所属账户编号");
			headList.add("是否本人");
			headList.add("返利类型");
			headList.add("时间");
			headList.add("SKU编号");
			headList.add("计算返利金额");
			headList.add("返利金额");
			headList.add("返利比例");

			pageData.setPageHead(headList);
			
			List<List<String>> newDataList=new ArrayList<List<String>>();
			for(Map<String,Object> detail:depositDetailList){
				List<String> oneList=new ArrayList<String>();
				//订单号
				oneList.add(detail.get("out_order_code")==null?"":detail.get("out_order_code").toString());
				//清分编号
				oneList.add(detail.get("order_code")==null?"":detail.get("order_code").toString());
				//订单状态
				oneList.add(detail.get("order_status")==null?"":detail.get("order_status").toString());
				//下单时间
				oneList.add(detail.get("order_create_time")==null?"":detail.get("order_create_time").toString());
				//用户编号
				oneList.add(detail.get("member_code")==null?"":detail.get("member_code").toString());
				//渠道名称
				oneList.add(detail.get("channel_name")==null?"":detail.get("channel_name").toString());
				//预计返利账号
				oneList.add(detail.get("account_code")==null?"":detail.get("account_code").toString());
				//订单所属账户编号
				oneList.add(detail.get("order_account_code")==null?"":detail.get("order_account_code").toString());
				//是否本人
				oneList.add(detail.get("relation_level")==null?"":detail.get("relation_level").toString());
				//返利类型
				oneList.add(detail.get("deposit_type")==null?"":detail.get("deposit_type").toString());
				//时间
				oneList.add(detail.get("create_time")==null?"":detail.get("create_time").toString());
				//SKU编号
				oneList.add(detail.get("sku_code")==null?"":detail.get("sku_code").toString());
				//计算返利金额
				oneList.add(detail.get("sum_reckon_money")==null?"":detail.get("sum_reckon_money").toString());
				//返利金额
				oneList.add(detail.get("rebate_money")==null?"":detail.get("rebate_money").toString());
				//返利比例
				oneList.add(detail.get("scale_reckon")==null?"":detail.get("scale_reckon").toString());
				
				newDataList.add(oneList);
			}
			pageData.setPageData(newDataList);
			
			return exportExcel(exportName,pageData);
		}
		
		return null;
	}
	
	/**
	 * 结算数据上传服务器
	 * @param exportName
	 * @param pageData
	 * @return
	 */
	public String exportExcel(String exportName,MPageData pageData){
			
        if (StringUtils.isEmpty(exportName)) {
            exportName = "export-"
                    + FormatHelper
                    .upDateTime(new Date(), "yyyy-MM-dd-HH-mm-ss");
        }


        XSSFWorkbook wb = new XSSFWorkbook();// 建立新HSSFWorkbook对象

        Sheet sheet = wb.createSheet("excel");

        int iNowRow = 0;

        Row headRow = sheet.createRow(iNowRow);


        //定义表头样式
        CellStyle hHeaderStyle=wb.createCellStyle();
        Font font = wb.createFont();
        //加粗
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//加粗

        hHeaderStyle.setFont(font);

        for (int i = 0, j = pageData.getPageHead().size(); i < j; i++) {
            Cell hCell = headRow.createCell(i);
            hCell.setCellValue(pageData.getPageHead().get(i));
            hCell.setCellStyle(hHeaderStyle);

        }

        for (List<String> lRow : pageData.getPageData()) {
            iNowRow++;
            Row hRow = sheet.createRow(iNowRow);
            for (int i = 0, j = lRow.size(); i < j; i++) {
                Cell hCell = hRow.createCell(i);
                hCell.setCellValue(lRow.get(i));
            }

        }
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			   wb.write(os);
			   byte[]  b=os.toByteArray();

				String sTarget = "productScale";	
				WebUpload webUpload = new WebUpload();
				String sDate = DateUtil.toString(new Date(), DateUtil.DATE_FORMAT_DATEONLYNOSP);

				MWebResult mBigFile = webUpload.remoteUploadCustom(
						"text.xlsx",
						b, sTarget, sDate,exportName);
		        return mBigFile.getResultObject().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


        return null;
	}
	
	
//	public static void main(String args[]){
//		BigDecimal test1=new BigDecimal("12.11");
//		BigDecimal test2=new BigDecimal("0");
//		System.out.print(test2.compareTo(BigDecimal.ZERO));;
//	}
}
