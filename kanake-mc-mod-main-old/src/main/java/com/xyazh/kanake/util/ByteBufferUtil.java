package com.xyazh.kanake.util;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.PacketBuffer;

import java.nio.charset.StandardCharsets;

public class ByteBufferUtil {
    public static void writeString(ByteBuf buf, String str){
        byte[] aByte = str.getBytes(StandardCharsets.UTF_8);

        if (aByte.length > 32767)
        {
            throw new EncoderException("String too big (was " + aByte.length + " bytes encoded, max " + 32767 + ")");
        }
        else
        {
            buf.writeInt(aByte.length);
            buf.writeBytes(aByte);
        }
    }

    public static String readString(ByteBuf buf){
        int i = buf.readInt();
        if (i > 32767)
        {
            throw new EncoderException("String too big (was " + i + " bytes encoded, max " + 32767 + ")");
        }
        else
        {
            byte[] aByte = new byte[i];
            buf.readBytes(aByte);
            return new String(aByte, StandardCharsets.UTF_8);
        }
    }

    public static void writeIntArray(ByteBuf buf, int[] array){
        buf.writeInt(array.length);
        for (int i : array) {
            buf.writeInt(i);
        }
    }

    public static int[] readIntArray(ByteBuf buf){
        int length = buf.readInt();
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = buf.readInt();
        }
        return array;
    }
}
