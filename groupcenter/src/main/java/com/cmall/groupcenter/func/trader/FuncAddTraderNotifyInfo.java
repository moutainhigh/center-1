package com.cmall.groupcenter.func.trader;

import com.cmall.groupcenter.util.CalendarHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

import java.util.Date;

/**
 * 添加预警管理联系人
 *
 * @author lipengfei
 * @date 2015-12-30
 * @time 15:18
 */
public class FuncAddTraderNotifyInfo extends RootFunc {

    public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

        MWebOperate mOperate = WebUp.upOperate(sOperateUid);
        MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

        MWebResult mResult = new MWebResult();

        //创建人
        String createUserName = UserFactory.INSTANCE.create().getLoginName();

        MDataMap addDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

        recheckMapField(mResult, mPage, addDataMap);

        MDataMap mAddMaps = new MDataMap();

        if(mResult.upFlagTrue()){

            mAddMaps.put("trader_code",addDataMap.get("trader_code"));
            mAddMaps.put("user_name",addDataMap.get("user_name"));
            mAddMaps.put("email",addDataMap.get("email"));
            mAddMaps.put("phone",addDataMap.get("phone"));
            mAddMaps.put("create_time", CalendarHelper.Date2String(new Date(), "yyyy-MM-dd HH:mm:ss"));
            mAddMaps.put("create_user",createUserName);
            DbUp.upTable("gc_trader_notify_info").dataInsert(mAddMaps);

        }

        return mResult;
    }

}
