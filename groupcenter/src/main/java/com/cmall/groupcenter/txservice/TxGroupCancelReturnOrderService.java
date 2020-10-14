package com.cmall.groupcenter.txservice;

import com.cmall.dborm.txmapper.groupcenter.GcRebateLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcTraderDepositLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcTraderFoundsChangeLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcTraderInfoMapper;
import com.cmall.dborm.txmodel.groupcenter.*;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.GroupLevelInfo;
import com.cmall.groupcenter.model.ReckonStep;
import com.cmall.groupcenter.support.GroupReckonSupport;
import com.cmall.groupcenter.support.ReckonOrderSupport;
import com.cmall.groupcenter.util.StringHelper;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

import org.apache.commons.lang.StringUtils;

import javax.jws.WebResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 取消退货清分流程的service
 *
 * @author lipengfei
 * @date 2015-09-24
 * @time 15:47
 * @email:lipengfei217@163.com
 */
public class TxGroupCancelReturnOrderService extends BaseClass {


    /**
     * 做一些取消退货流程的更新数据库表的操作，比如uqcode等。
     *
     * @param reckonStep
     * @param orderCode
     * @param flagSuccess 该清分是否执行成功，如果执行未成功，是不需要重新创建一个正向清分记录的
     */
    public MWebResult doUpdateCancelReturnOrder(ReckonStep reckonStep, String orderCode,String flagSuccess) {

        MWebResult result=null;

//如果执行流程为否的话就只记录日志，而不做任何操作了。
        if("1".equals(flagSuccess)){

//        修改uqcode等
            updateUqCode(orderCode);
//                修改清分日志
            updateLog(orderCode);

            //修改商户预存款 删除扣除商户预存款操作 20160218 gaoyang 
//            updateTraderFounds(orderCode);
            //插入预返利流程,在此流程中扣除预存款
			ReckonStep newReckonStep = new ReckonStep();
			newReckonStep.setOrderCode(orderCode);
			newReckonStep.setExecType(GroupConst.REBATE_ORDER_EXEC_TYPE_IN);
			newReckonStep.setAccountCode(reckonStep.getAccountCode());
			result = createReckonStep(newReckonStep);
            
            if(result.upFlagTrue()){
            	 result = createReckonStep(reckonStep);
            }
            
        }else {

            faildCancelReturnProcess(orderCode);

            result = new MWebResult();
            result.inErrorMessage(918548004,orderCode);
        }
        return result;
    }

    /**
     * 1.将库中的orderCOde对应的数据的uqcode以及execType的值均update，
     * 即在原有的值的基础上加上一个后缀 "_c"
     *
     * @param orderCode 订单编号
     */
    public MWebResult updateUqCode(String orderCode) {
        MWebResult mWebResult = new MWebResult();
                MDataMap mWhereMap = new MDataMap();
        mWhereMap.put("order_code", orderCode);
//            将订单号为orderCode的数据的execType和uqCode均加上后缀,其中取消订单退货流程的数据不加后缀，保持原状。

        DbUp.upTable("gc_reckon_order_step").dataExec(" UPDATE gc_reckon_order_step step " +
                " SET step.exec_type=CONCAT(step.exec_type,'_" + GroupConst.GROUP_REKON_CANCELRETURNORDER_UQCODE_FLAG + "'),step.uqcode=CONCAT(step.uqcode,'_" + GroupConst.GROUP_REKON_CANCELRETURNORDER_UQCODE_FLAG + "') " +
                " WHERE step.order_code=:order_code and step.exec_type<>'"+GroupConst.GROUP_REKON_CANCELRETURNORDER_TYPE+"'", mWhereMap);


        return mWebResult;
    }


    /**
     * 插入正向清分的相应数据
     *
     * @param reckonStep
     */
    public MWebResult createReckonStep(ReckonStep reckonStep) {
        GroupReckonSupport support = new GroupReckonSupport();
        MWebResult mWebResult = support.createReckonStep(reckonStep);
        return mWebResult;
    }

    /**
     * 如果执行成功，则修改日志记录表。
     */
    public MWebResult updateLog(String orderCode) {

        MWebResult mWebResult = new MWebResult();

        MDataMap mWhereMap = new MDataMap();
        mWhereMap.put("order_code", orderCode);

//        将订单号为orderCode的数据的execType和uqCode均加上后缀
        DbUp.upTable("gc_reckon_log").dataExec(" UPDATE gc_reckon_log theLog " +
                " SET  flag_status=0 " +
                " WHERE theLog.order_code=:order_code ", mWhereMap);


        //        将订单号为orderCode的数据的execType和uqCode均加上后缀
        DbUp.upTable("gc_rebate_log").dataExec(" UPDATE gc_rebate_log theLog " +
                " SET  flag_status=0 " +
                " WHERE theLog.order_code=:order_code ", mWhereMap);

        return mWebResult;
    }

    /**
     *将订单的某个流程的状态设为失败
     * @param orderCode
     * @param processType 流程类型，某一个订单的一个流程只可能有一条数据存在。
     * @return
     */
    public MWebResult updateProcessToFaild(String orderCode,String processType){

        MWebResult result = new MWebResult();
        MDataMap mWhereMap = new MDataMap();

        mWhereMap.put("order_code",orderCode);
        mWhereMap.put("exec_type", processType);
        DbUp.upTable("gc_reckon_order_step").dataExec(" UPDATE gc_reckon_order_step step " +
                " SET flag_success=0 " +
                " WHERE step.order_code=:order_code " +
                " AND step.exec_type=:exec_type ",mWhereMap);

        return null;
    }



    /**
     *将取消退货流程的状态设置为失败
     * @param orderCode 订单编号
     * @return
     */
    public MWebResult faildCancelReturnProcess(String orderCode){
        return updateProcessToFaild(orderCode,GroupConst.GROUP_REKON_CANCELRETURNORDER_TYPE);
    }


    /**
     * 扣除商户预存款，即在取消退货成功后又需要扣去。
     * @param orderCode 订单编号
     * @return
     */
    public MWebResult updateTraderFounds(String orderCode){

        TxRebateOrderService txRebateOrderService = BeansHelper
                .upBean("bean_com_cmall_groupcenter_txservice_TxRebateOrderService");

        TxGroupAccountService txGroupAccountService = BeansHelper
                .upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");

        GcTraderInfo gcTraderInfo=null;

        GcReckonOrderInfo gcReckonOrderInfo = txRebateOrderService.upGcReckonOrderInfo(orderCode);
        MDataMap appMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",gcReckonOrderInfo.getManageCode());

        //获取traderInfo的相关信息
        if(appMap!=null&&appMap.get("trade_code")!=null){
            gcTraderInfo=txGroupAccountService.getTraderInfo(appMap.get("trade_code"));
        }

        MWebResult result = new MWebResult();

        MDataMap mWhereMap = new MDataMap();

        mWhereMap.put("order_code",orderCode);
        mWhereMap.put("rebate_change_type","4497465200140001");

        List<Map<String, Object>> listRebateLog =  DbUp.upTable("gc_rebate_log").dataSqlList(" SELECT * FROM gc_rebate_log theLog " +
                " WHERE theLog.order_code=:order_code and theLog.rebate_change_type=:rebate_change_type ", mWhereMap);

        String traderCode = gcTraderInfo.getTraderCode();

        if(gcTraderInfo!=null && StringUtils.isNotEmpty(traderCode)){

            //开始扣除商户预存款
            for (Map<String, Object> rebateLog : listRebateLog){

                BigDecimal changeAmount = new BigDecimal(StringHelper.getStringFromMap(rebateLog,"rebate_money"));
                Integer relationLevel = Integer.valueOf(StringHelper.getStringFromMap(rebateLog,"relation_level"));

                GcTraderFoundsChangeLog gcTraderFoundsChangeLog=new GcTraderFoundsChangeLog();
                gcTraderFoundsChangeLog.setTraderCode(traderCode);
                gcTraderFoundsChangeLog.setAccountCode(gcTraderInfo.getAccountCode());//商户的accountCode
                gcTraderFoundsChangeLog.setGurranteeChangeAmount(changeAmount.negate());
                gcTraderFoundsChangeLog.setChangeType("4497472500030007");//取消退货订单扣减
                gcTraderFoundsChangeLog.setRelationCode(StringHelper.getStringFromMap(rebateLog, "log_code"));
                gcTraderFoundsChangeLog.setOrderCode(orderCode);
                txRebateOrderService.updateTraderDeposit(gcTraderFoundsChangeLog);

                //添加保证金订单日志
                GcTraderDepositLog gcTraderDepositLog=new GcTraderDepositLog();
                gcTraderDepositLog.setOrderCode(orderCode);
                gcTraderDepositLog.setAccountCode(StringHelper.getStringFromMap(rebateLog,"account_code"));
                gcTraderDepositLog.setOrderAccountCode(StringHelper.getStringFromMap(rebateLog, "order_account_code"));
                gcTraderDepositLog.setRelationLevel(relationLevel);
                gcTraderDepositLog.setSkuCode(StringHelper.getStringFromMap(rebateLog, "sku_code"));
                gcTraderDepositLog.setDeposit(changeAmount.negate());
                gcTraderDepositLog.setDepositType("4497472500040003");//取消退货扣减
                gcTraderDepositLog.setTraderCode(traderCode);
                gcTraderDepositLog.setRelationCode(StringHelper.getStringFromMap(rebateLog, "log_code"));
                txRebateOrderService.addTraderDepositOrderLog(gcTraderDepositLog);
            }
        }
        return result;
    }

    /**
     * 增加商户保证金订单对账日志
     * @param gcTraderDepositLog
     */
    private void addTraderDepositOrderLog(GcTraderDepositLog gcTraderDepositLog) {
        GcTraderDepositLogMapper gcTraderDepositLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderDepositLogMapper");
        if(StringUtils.isBlank(gcTraderDepositLog.getLogCode())){
            gcTraderDepositLog.setLogCode(WebHelper.upCode("TDO"));
        }
        if(StringUtils.isBlank(gcTraderDepositLog.getUid())){
            gcTraderDepositLog.setUid(WebHelper.upUuid());
        }
        if(StringUtils.isBlank(gcTraderDepositLog.getCreateTime())){
            gcTraderDepositLog.setCreateTime(FormatHelper.upDateTime());
        }
        gcTraderDepositLogMapper.insertSelective(gcTraderDepositLog);
    }

    /**
     * 更新商户保证金账户
     * @param gcTraderFoundsChangeLog
     */
    private void updateTraderDeposit(
            GcTraderFoundsChangeLog gcTraderFoundsChangeLog) {
        GcTraderInfoMapper gcTraderInfoMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderInfoMapper");
        GcTraderInfoExample gcTraderInfoExample=new GcTraderInfoExample();
        gcTraderInfoExample.createCriteria().andTraderCodeEqualTo(gcTraderFoundsChangeLog.getTraderCode());
        GcTraderInfo gcTraderInfo=gcTraderInfoMapper.selectByExample(gcTraderInfoExample).get(0);
        if(gcTraderInfo!=null){
            //添加保证金变动日志
            GcTraderFoundsChangeLogMapper gcTraderFoundsChangeLogMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcTraderFoundsChangeLogMapper");
            gcTraderFoundsChangeLog.setUid(WebHelper.upUuid());
            gcTraderFoundsChangeLog.setGurranteeBalanceBefore(gcTraderInfo.getGurranteeBalance());
            gcTraderFoundsChangeLog.setGurranteeBalanceAfter(gcTraderInfo.getGurranteeBalance().add(gcTraderFoundsChangeLog.getGurranteeChangeAmount()));
            gcTraderFoundsChangeLog.setCreateTime(FormatHelper.upDateTime());
            gcTraderFoundsChangeLogMapper.insertSelective(gcTraderFoundsChangeLog);

            //更新保证金金额
            GcTraderInfo updateGcTraderInfo=new GcTraderInfo();
            updateGcTraderInfo.setGurranteeBalance(gcTraderInfo.getGurranteeBalance().add(gcTraderFoundsChangeLog.getGurranteeChangeAmount()));
            gcTraderInfoMapper.updateByExampleSelective(updateGcTraderInfo, gcTraderInfoExample);
        }
    }

}


