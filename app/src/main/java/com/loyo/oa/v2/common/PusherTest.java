package com.loyo.oa.v2.common;

import com.loyo.oa.v2.tool.LogUtil;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

/**
 * Created by xeq on 16/3/25.
 */
public class PusherTest {
    public static void appTest() {
        PusherOptions options = new PusherOptions();
        options.setHost("192.168.31.131");
//        options.setCluster("xx-xx");
        options.setWsPort(8888);
        options.setWssPort(8888);
        options.setEncrypted(false);
        Pusher pusher = new Pusher("loyocloud_web", options);
        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                LogUtil.d(change.getPreviousState() + "pusher链接【改变】" + change.getCurrentState());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                LogUtil.d("pusher链接【错误】" + message.toString() + "<-->" + code + "<--->" + e.toString());
            }
        }, ConnectionState.ALL);


        Channel channel = pusher.subscribe("myPusher");
        channel.bind("myEvent", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, String data) {
                LogUtil.d("pusher链接【接受信息】" + data);
            }
        });
    }
}
