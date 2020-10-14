package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncCancelReturnOrder;
import com.cmall.groupcenter.homehas.model.*;
import com.cmall.groupcenter.model.ReckonStep;
import com.cmall.groupcenter.txservice.TxGroupCancelReturnOrderService;
import com.cmall.groupcenter.util.CalendarHelper;
import com.cmall.groupcenter.util.StringHelper;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * Created with
 * 取消退货的流程
 * @author lipengfei
 * @date 2015-09-10
 * @time 14:26
 *
 */
public class RsyncDoCancelReturnOrder extends
        RsyncHomeHas<RsyncConfigRsyncCancelReturnOrder, RsyncRequestSyncCancelReturnOrder, RsyncResponseSyncCancelReturnOrder>{

    RsyncConfigRsyncCancelReturnOrder RSYNC_CANCEL_RETURN_ORDER = new RsyncConfigRsyncCancelReturnOrder();

    RsyncRequestSyncCancelReturnOrder request = new RsyncRequestSyncCancelReturnOrder();

    public RsyncConfigRsyncCancelReturnOrder upConfig() {
        return RSYNC_CANCEL_RETURN_ORDER;
    }

    public RsyncRequestSyncCancelReturnOrder upRsyncRequest() {

        RsyncDateCheck rsyncDateCheck = upDateCheck(upConfig());

        Date startDate;
        Date endDate;

        List list = DbUp.upTable("lc_rsync_log").dataSqlList(" SELECT * FROM lc_rsync_log WHERE rsync_target='getCancelReturnOrderList'  AND flag_success='1'  ", new MDataMap());

        //如果没有一条数据是成功的， 则从BaseStartTime开始执行
        if (list==null || list.size()==0){
            startDate =  CalendarHelper.String2Date(upConfig().getBaseStartTime(), "yyyy-MM-dd HH:mm:ss");
            endDate = new Date();//默认到今天的数据
        }else{
            startDate = CalendarHelper.String2Date(rsyncDateCheck.getStartDate(),"yyyy-MM-dd HH:mm:ss");
            endDate = CalendarHelper.String2Date(rsyncDateCheck.getEndDate(), "yyyy-MM-dd HH:mm:ss");
        }




        //转换日期的输出格式
        request.setBeginTime(CalendarHelper.Date2String(startDate));
        request.setEndTime(CalendarHelper.Date2String(endDate));

        request.setLimit("100000");
        request.setStart("0");
        request.setSubsystem("1");
    //app是指APP通路，web是指网站，wap是指网站WAP，scan是指扫码购，wechat是指微信购物商城。
    // 通路码可传1个或多个，当传多个的时候，请用英文逗号分隔。
        request.setMedi_mclss_id("app,web,wap,scan,wechat");
        return request;

    }

    public RsyncResult doProcess(RsyncRequestSyncCancelReturnOrder tRequest, RsyncResponseSyncCancelReturnOrder tResponse) {

        RsyncResult result = new RsyncResult();


        List<String> noOrderList = new ArrayList<String>();
        List<Map<String,String>> errorList = new ArrayList<Map<String,String>>();

        int totalNum=tResponse.getResult().size();


        if("true".equals(tResponse.getSuccess())){


            //这里不确定会返回几条订单数据
            RsyncSyncGoods rsyncSyncGoods = new RsyncSyncGoods();
            for (RsyncModelCancelReturnOrder returnOrder: tResponse.getResult()) {

                //查询该订单数据是否存在
                if(checkOrderIfexist(returnOrder.getORDERID())){
                    try {

                        //进行取消退货的相关逻辑操作
                        insertIntoStep(returnOrder.getORDERID());

                    }catch (Exception e){

                        Map<String,String> errorMap = new HashMap<String, String>();
                        errorMap.put("orderId",returnOrder.getORDERID());
                        errorList.add(errorMap);
                    }
                }else {//该订单在本系统中的step表不存在，故而报错
                    noOrderList.add(returnOrder.getORDERID());
                    }
                }
        }


        if (result.upFlagTrue()) {
            // 如果操作都成功 则设置状态保存数据为同步结束时间 以方便下一轮调用
            if (noOrderList.size()>0 || errorList.size()>0){
                    result.setSuccessNum(totalNum - noOrderList.size()-errorList.size());
                    result.setStatusData(tRequest.getEndTime() + " 00:00:01");

                result.setProcessNum(totalNum);
                //获取取消退货的订单过程中，该订单号在step表中不存在。
                StringBuffer noOrder = new StringBuffer();
                for (String orderCode:noOrderList){
                    noOrder.append(orderCode);
                    noOrder.append(",");
                }

                //将错误的执行的订单号写入日志
                StringBuffer errorOrder = new StringBuffer();
                for (Map<String,String> map:errorList){
                    errorOrder.append(map.get("orderId"));
                    errorOrder.append(",");
                }

                if(errorList.size()>0||noOrderList.size()>0){
                    //有错误信息的话才写错误日志
                    String errorMessage = bInfo(918548005, noOrder.toString())+"--分割线--"+ bInfo(918548006, errorOrder.toString());
                    result.setProcessData(errorMessage);
                }
                result.setResultCode(1);
            }else {
                result.setSuccessNum(totalNum);
                result.setStatusData(tRequest.getEndTime()+" 00:00:01");
            }
        }


        return result;
    }

    public RsyncResponseSyncCancelReturnOrder upResponseObject() {
        return new RsyncResponseSyncCancelReturnOrder();
    }

    public static void main(String[] args) {


        RsyncDoCancelReturnOrder RsyncGetCancelReturnOrder = new RsyncDoCancelReturnOrder();
        RsyncGetCancelReturnOrder.doRsync();
    }


    /**
     * 将查询到的订单再在gc_reckon_order_step表中添加相应的步骤
     * @param orderCode
     */
    public void insertIntoStep(String orderCode){

        MDataMap mWhereMap = new MDataMap();

        //先找到其的正向清分的数据
        mWhereMap.put("exec_type", GroupConst.RECKON_ORDER_EXEC_TYPE_IN);
        mWhereMap.put("order_code", orderCode);


        List<Map<String, Object>> list =  DbUp.upTable("gc_reckon_order_step").dataSqlList(" SELECT * FROM gc_reckon_order_step step " +
                "WHERE step.order_code=:order_code  ",mWhereMap);

        if(list!=null && list.size()>0){

            //随便获取到一个流程的数据，便于拿到accountCode
            Map stepData = list.get(0);

            //该数据若存在，才开始插入取消退货流程
            if(stepData!=null){
                String stepCode = WebHelper.upCode("GCROS");
                //插入步骤执行表
                String uqCode=GroupConst.GROUP_REKON_CANCELRETURNORDER_TYPE+ WebConst.CONST_SPLIT_DOWN+orderCode;

                DbUp.upTable("gc_reckon_order_step").insert("step_code",
                        stepCode, "order_code",
                        orderCode, "exec_type",
                        GroupConst.GROUP_REKON_CANCELRETURNORDER_TYPE, "create_time",
                        FormatHelper.upDateTime(), "account_code",
                        stepData.get("account_code").toString(), "uqcode", uqCode);
            }
        }
    }

    public boolean checkOrderIfexist(String orderCode){
        boolean exist = true;

        MDataMap whereMap = new MDataMap();

        whereMap.put("order_code",orderCode);

        List<Map<String, Object>> stepList = DbUp.upTable("gc_reckon_order_step").dataSqlList(" SELECT * FROM gc_reckon_order_step step WHERE order_code=:order_code ",whereMap);

        if(stepList==null || stepList.size()==0){
            exist=false;//代表该订单相关的数据不存在
        }

        return exist;
    }



}
