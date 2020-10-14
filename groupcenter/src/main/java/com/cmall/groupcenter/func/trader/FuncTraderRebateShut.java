package com.cmall.groupcenter.func.trader;

import com.cmall.groupcenter.txservice.TxTraderInfoCreateService;
import com.cmall.groupcenter.util.CalendarHelper;
import com.cmall.groupcenter.util.StringHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.Map;

/**
 *
 * 修改默认返利的终止和删除状态
 * 只有终止状态最后才能进行删除。
 * @author lipengfei
 * @date 2015-07-29
 * @time 17:17
 * @email:lipengfei217@163.com
 *
 */
public class FuncTraderRebateShut extends RootFunc{

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();

        MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

//        System.out.println("test");

        //如果flag_enable为有效，则本次操作是让数据变成失效状态，
        //如果数据已经失效则，本次操作是删除数据。

        Map<String, Object> map =  DbUp.upTable("gc_app_rebate_scale").dataSqlOne("select * from gc_app_rebate_scale where uid=:uid", mAddMaps);


        //如果数据库的值为空，则返回空
        String flag_enable= StringHelper.getStringFromMap(map,"flag_enable");

        String uid = StringHelper.getStringFromMap(map,"uid");

        if("1".equals(flag_enable) && StringUtils.isNotEmpty(uid)){//为有效状态，则将数据更改为失效状态。
            MDataMap updateMap = new MDataMap();
            updateMap.put("flag_enable","0");
            updateMap.put("uid",uid);
            DbUp.upTable("gc_app_rebate_scale").dataUpdate(updateMap,"flag_enable","uid");
        }else if ("0".equals(flag_enable) && StringUtils.isNotEmpty(uid)){//如果已经为无效状态，则此次操作是为了删除数据。
            MDataMap updateMap = new MDataMap();
            updateMap.put("delete_flag","0");
            updateMap.put("uid",uid);
            DbUp.upTable("gc_app_rebate_scale").dataUpdate(updateMap, "delete_flag", "uid");
        }else {
            mResult.setResultCode(0);
            mResult.setResultMessage("数据有效状态有误，请重新操作或联系管理员");
        }
        return mResult;


	}

}
