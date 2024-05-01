package com.xyazh.kanake.network;

import io.netty.buffer.ByteBuf;

public interface IEntityDataParameter {
    void readData(ByteBuf buf);
    void writeData(ByteBuf buf);
}
