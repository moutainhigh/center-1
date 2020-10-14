package com.cmall.groupcenter.baidupush.channel.client;

import java.util.concurrent.Future;

import com.cmall.groupcenter.baidupush.channel.exception.ChannelClientException;
import com.cmall.groupcenter.baidupush.channel.exception.ChannelServerException;
import com.cmall.groupcenter.baidupush.channel.model.PushBroadcastMessageRequest;
import com.cmall.groupcenter.baidupush.channel.model.PushTagMessageRequest;
import com.cmall.groupcenter.baidupush.channel.model.PushUnicastMessageRequest;
import com.cmall.groupcenter.baidupush.channel.model.PushUnicastMessageResponse;
import com.cmall.groupcenter.baidupush.channel.model.QueryBindListRequest;
import com.cmall.groupcenter.baidupush.channel.model.QueryBindListResponse;
import com.cmall.groupcenter.baidupush.channel.model.QueryUserTagsRequest;
import com.cmall.groupcenter.baidupush.channel.model.QueryUserTagsResponse;
import com.cmall.groupcenter.baidupush.channel.model.VerifyBindRequest;

public interface BaiduChannelAsync {

    public Future<PushUnicastMessageResponse> pushUnicastMessageAsync(
            final PushUnicastMessageRequest request)
            throws ChannelClientException, ChannelServerException;

    public Future<Void> pushTagMessageAsync(final PushTagMessageRequest request)
            throws ChannelClientException, ChannelServerException;

    public Future<Void> pushBroadcastMessageAsync(
            final PushBroadcastMessageRequest request)
            throws ChannelClientException, ChannelServerException;

    public Future<QueryBindListResponse> queryBindListAsync(
            final QueryBindListRequest request) throws ChannelClientException,
            ChannelServerException;

    public Future<Void> verifyBindAsync(final VerifyBindRequest request)
            throws ChannelClientException, ChannelServerException;

    public Future<QueryUserTagsResponse> queryUserTagsAsync(
            final QueryUserTagsRequest request) throws ChannelClientException,
            ChannelServerException;

}
