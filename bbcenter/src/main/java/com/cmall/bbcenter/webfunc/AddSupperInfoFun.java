package com.cmall.bbcenter.webfunc;

import com.cmall.bbcenter.service.SupplierBalanceService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * Created by zhaoshuli on 14-4-17.
 */
public class AddSupperInfoFun  extends RootFunc {

    public MWebResult funcDo(String s, MDataMap mDataMap) {
        MWebResult mResult = null;
       
        SupplierBalanceService service = new SupplierBalanceService();
        mResult = service.doSaveSupperlierBalance(mDataMap);
        

        return mResult;
    }
}
