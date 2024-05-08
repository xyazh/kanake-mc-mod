package com.xyazh.kanake.network;

import io.netty.buffer.ByteBuf;

public interface IEntityDataParameter {
    int readData(ByteBuf buf);
}
