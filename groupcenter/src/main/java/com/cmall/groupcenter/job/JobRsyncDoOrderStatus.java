package com.cmall.groupcenter.job;

import com.cmall.groupcenter.homehas.RsyncDoOrderStatus;
import com.srnpr.zapweb.rootweb.RootJob;
import org.quartz.JobExecutionContext;

/**
 *4.16. 同步订单状态接口
 * 由于经常出现一些订单已经处于结束状态， 但惠家友中却没有进行相应的返利，
 * 因为在之前的同步过程中可能出现某种错漏而导致的此问题。因此本接口主要解决
 * 这种“漏网之鱼”
 *
 * @author lipengfei
 * @date 2016-02-18
 * @time 16:13
 */
public class JobRsyncDoOrderStatus extends RootJob {
    public void doExecute(JobExecutionContext jobExecutionContext) {
        RsyncDoOrderStatus rsyncDoOrderStatus = new RsyncDoOrderStatus();
        rsyncDoOrderStatus.doRsync();
    }
}
