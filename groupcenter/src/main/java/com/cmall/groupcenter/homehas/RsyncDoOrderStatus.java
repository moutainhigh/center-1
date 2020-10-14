package com.cmall.groupcenter.homehas;

import com.cmall.dborm.txmodel.groupcenter.GcExtendOrderStatusHomehas;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.homehas.config.RsyncConfigDoOrderStatus;
import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncCancelReturnOrder;
import com.cmall.groupcenter.homehas.model.*;
import com.cmall.groupcenter.util.CalendarHelper;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webface.IWebStatic;
import com.srnpr.zapweb.webmodel.MWebResult;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Created with
 * 4.16. 订单状态接口
 * 由于经常出现一些订单已经处于结束状态， 但惠家友中却没有进行相应的返利，
 * 因为在之前的同步过程中可能出现某种错漏而导致的此问题。因此本接口主要解决
 * 这种“漏网之鱼”
 *
 * @author lipengfei
 * @date 2015-09-10
 * @time 14:26
 */
public class RsyncDoOrderStatus extends
        RsyncHomeHas<RsyncConfigDoOrderStatus, RsyncRequestDoOrderStatus, RsyncResponseDoOrderStatus> {

    RsyncConfigDoOrderStatus configDoOrderStatus = new RsyncConfigDoOrderStatus();

    RsyncRequestDoOrderStatus request = new RsyncRequestDoOrderStatus();

    public RsyncConfigDoOrderStatus upConfig() {
        return configDoOrderStatus;
    }

    public RsyncRequestDoOrderStatus upRsyncRequest() {

//      希望不要受接口lc_rsyn_log的影响，而是从配置的初始的时间开始第一次的值,第一次插入static表中为该值。
        RsyncStatic rStatic=new RsyncStatic();
        rStatic.setCodeValue(this.getClass().getName());
        RsyncDateCheck rsyncDateCheck = upDateCheck(upConfig());

            request.setStart_time(rsyncDateCheck.getStartDate());
            request.setEnd_time(rsyncDateCheck.getEndDate());

        return request;
    }

    public RsyncResult doProcess(RsyncRequestDoOrderStatus tRequest, RsyncResponseDoOrderStatus tResponse) {
        RsyncResult result = new RsyncResult();
// 定义成功的数量合计
        int iSuccessSum = 0;

        if (result.upFlagTrue()) {
            if (tResponse != null && tResponse.getResult() != null) {
                result.setProcessNum(tResponse.getResult().size());
            } else {
                result.setProcessNum(0);

            }

        }

        // 开始循环处理结果数据
        if (result.upFlagTrue()) {

            // 判断有需要处理的数据才开始处理
            if (result.getProcessNum() > 0) {

                // 设置预期处理数量
                result.setProcessNum(tResponse.getResult().size());

                for (RsyncModelShipmentStat model : tResponse.getResult()) {

                    // 更新状态信息
                    GcExtendOrderStatusHomehas orderStatus = new GcExtendOrderStatusHomehas();
                    orderStatus.setOrderCode(model.getOrd_id());
                    orderStatus.setSendStatus(model.getCod_stat_cd());
                    // 有状态更新时间 则将此时间标记订单配送状态完成的时间
                    if(StringUtils.isNotBlank(model.getStat_date())){
                        orderStatus.setUpdateTime(model.getStat_date());
                    }else{
                        // 没有状态更新时间 所以只能将时间置为同步的结束时间 以标记如果订单配送完成的时间
                        orderStatus.setUpdateTime(tRequest.getEnd_time());
                    }

                    MWebResult mResult = insertOrderStatus(orderStatus);


                    // 如果成功则将成功计数加1
                    if (mResult.upFlagTrue()) {
                        iSuccessSum++;

                    } else {

                        if (result.getResultList() == null) {
                            result.setResultList(new ArrayList<Object>());
                        }

                        result.getResultList().add(mResult.getResultMessage());
                    }

                }

                result.setProcessData(bInfo(918501102, result.getProcessNum(),
                        iSuccessSum, result.getProcessNum() - iSuccessSum));

            }

        }

        // 如果操作都成功 则设置状态保存数据为同步结束时间 以方便下一轮调用
        if (result.upFlagTrue()) {
            // 设置处理成功数量
            result.setSuccessNum(iSuccessSum);
            result.setStatusData(tRequest.getEnd_time());
        }

        return result;
    }

    public RsyncResponseDoOrderStatus upResponseObject() {
        return new RsyncResponseDoOrderStatus();
    }

    /**
     * 插入订单状态表，且只插入最新状态到订单状态表
     */
    public MWebResult insertOrderStatus(GcExtendOrderStatusHomehas orderStatus) {
        MWebResult mWebResult = new MWebResult();


        // 定义清分类型
        String sReckon_Type = "";

        // 定义返利类型
        String rebateType = "";

        // 判断是否为null 如果为null则设置为空
        if (mWebResult.upFlagTrue()) {
            if (StringUtils.isEmpty(orderStatus.getChangeStatus())) {
                orderStatus.setChangeStatus("");
            }
            if (StringUtils.isEmpty(orderStatus.getOrderStatus())) {
                orderStatus.setOrderStatus("");
            }
            if (StringUtils.isEmpty(orderStatus.getSendStatus())) {
                orderStatus.setSendStatus("");
            }

            if (StringUtils.isEmpty(orderStatus.getUpdateTime())) {
                orderStatus.setUpdateTime("");
            }
        }

        if (mWebResult.upFlagTrue()) {
            MDataMap mDataMap = new MDataMap();
            mDataMap.inAllValues("order_code", orderStatus.getOrderCode(),
                    "order_status", orderStatus.getOrderStatus(),
                    "send_status", orderStatus.getSendStatus(),
                    "change_status", orderStatus.getChangeStatus());
            // 判断如果没有比这条更新的订单状态 则插入并判断
            if (DbUp.upTable("gc_extend_order_status_homehas").dataCount("",
                    mDataMap) <= 0) {

                DbUp.upTable("gc_extend_order_status_homehas").insert(
                        "order_code", orderStatus.getOrderCode(),
                        "order_status", orderStatus.getOrderStatus(),
                        "send_status", orderStatus.getSendStatus(),
                        "change_status", orderStatus.getChangeStatus(),
                        "update_time", orderStatus.getUpdateTime(),
                        "create_time", FormatHelper.upDateTime());
            }
        }
        return mWebResult;
    }



}
