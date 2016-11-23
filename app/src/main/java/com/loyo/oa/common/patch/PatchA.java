package com.loyo.oa.common.patch;

import com.yzxtcp.tcp.TCPServer;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;

/**
 * Created by EthanGong on 2016/11/23.
 */

public final class PatchA extends IGGBaseRequest {
    PatchA() {
    }

    public final void onSendMessage() {
        TCPServer.obtainTCPService().sendPacket(600100, this);
    }
}