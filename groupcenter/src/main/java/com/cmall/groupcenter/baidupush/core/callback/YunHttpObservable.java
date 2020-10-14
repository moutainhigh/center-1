package com.cmall.groupcenter.baidupush.core.callback;

import java.util.List;

import com.cmall.groupcenter.baidupush.core.event.YunHttpEvent;

public interface YunHttpObservable {

    public void addHttpCallback(YunHttpObserver callback);

    public void addBatchHttpCallBack(List<YunHttpObserver> callbacks);

    public void removeCallBack(YunHttpObserver callback);

    public void notifyAndCallback(YunHttpEvent event);

}
