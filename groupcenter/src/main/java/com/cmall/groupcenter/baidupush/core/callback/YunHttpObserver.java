package com.cmall.groupcenter.baidupush.core.callback;

import com.cmall.groupcenter.baidupush.core.event.YunHttpEvent;

public interface YunHttpObserver {

    public void onHandle(YunHttpEvent event);

}
