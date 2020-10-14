package com.cmall.groupcenter.baidupush.channel.client;

import com.cmall.groupcenter.baidupush.channel.exception.ChannelClientException;
import com.cmall.groupcenter.baidupush.channel.exception.ChannelServerException;
import com.cmall.groupcenter.baidupush.channel.model.DeleteAppIoscertRequest;
import com.cmall.groupcenter.baidupush.channel.model.DeleteTagRequest;
import com.cmall.groupcenter.baidupush.channel.model.FetchMessageRequest;
import com.cmall.groupcenter.baidupush.channel.model.FetchMessageResponse;
import com.cmall.groupcenter.baidupush.channel.model.FetchTagRequest;
import com.cmall.groupcenter.baidupush.channel.model.FetchTagResponse;
import com.cmall.groupcenter.baidupush.channel.model.InitAppIoscertRequest;
import com.cmall.groupcenter.baidupush.channel.model.PushBroadcastMessageRequest;
import com.cmall.groupcenter.baidupush.channel.model.PushBroadcastMessageResponse;
import com.cmall.groupcenter.baidupush.channel.model.PushTagMessageRequest;
import com.cmall.groupcenter.baidupush.channel.model.PushTagMessageResponse;
import com.cmall.groupcenter.baidupush.channel.model.PushUnicastMessageRequest;
import com.cmall.groupcenter.baidupush.channel.model.PushUnicastMessageResponse;
import com.cmall.groupcenter.baidupush.channel.model.QueryAppIoscertRequest;
import com.cmall.groupcenter.baidupush.channel.model.QueryAppIoscertResponse;
import com.cmall.groupcenter.baidupush.channel.model.QueryBindListRequest;
import com.cmall.groupcenter.baidupush.channel.model.QueryBindListResponse;
import com.cmall.groupcenter.baidupush.channel.model.QueryDeviceTypeRequest;
import com.cmall.groupcenter.baidupush.channel.model.QueryDeviceTypeResponse;
import com.cmall.groupcenter.baidupush.channel.model.QueryUserTagsRequest;
import com.cmall.groupcenter.baidupush.channel.model.QueryUserTagsResponse;
import com.cmall.groupcenter.baidupush.channel.model.SetTagRequest;
import com.cmall.groupcenter.baidupush.channel.model.UpdateAppIoscertRequest;
import com.cmall.groupcenter.baidupush.channel.model.VerifyBindRequest;

public interface BaiduChannel {

    public PushUnicastMessageResponse pushUnicastMessage(
            PushUnicastMessageRequest request) throws ChannelClientException,
            ChannelServerException;

    public PushTagMessageResponse pushTagMessage(PushTagMessageRequest request)
            throws ChannelClientException, ChannelServerException;

    public PushBroadcastMessageResponse pushBroadcastMessage(
            PushBroadcastMessageRequest request) throws ChannelClientException,
            ChannelServerException;

    public QueryBindListResponse queryBindList(QueryBindListRequest request)
            throws ChannelClientException, ChannelServerException;

    public void verifyBind(VerifyBindRequest request)
            throws ChannelClientException, ChannelServerException;

    public FetchMessageResponse fetchMessage(FetchMessageRequest request)
            throws ChannelClientException, ChannelServerException;

    public void setTag(SetTagRequest request) throws ChannelClientException,
            ChannelServerException;

    public void deleteTag(DeleteTagRequest request)
            throws ChannelClientException, ChannelServerException;

    public FetchTagResponse fetchTag(FetchTagRequest request)
            throws ChannelClientException, ChannelServerException;

    public QueryUserTagsResponse queryUserTags(QueryUserTagsRequest request)
            throws ChannelClientException, ChannelServerException;

    public void initAppIoscert(InitAppIoscertRequest request)
            throws ChannelClientException, ChannelServerException;

    public void updateAppIoscert(UpdateAppIoscertRequest request)
            throws ChannelClientException, ChannelServerException;

    public void deleteAppIoscert(DeleteAppIoscertRequest request)
            throws ChannelClientException, ChannelServerException;

    public QueryAppIoscertResponse queryAppIoscert(
            QueryAppIoscertRequest request) throws ChannelClientException,
            ChannelServerException;

    public QueryDeviceTypeResponse queryDeviceType(
            QueryDeviceTypeRequest request) throws ChannelClientException,
            ChannelServerException;

}
