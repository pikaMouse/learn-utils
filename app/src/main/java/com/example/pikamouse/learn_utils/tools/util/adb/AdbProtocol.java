package com.example.pikamouse.learn_utils.tools.util.adb;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author: jiangfeng
 * @date: 2019/1/14
 */
public class AdbProtocol {
    public static final int ADB_HEADER_LENGTH = 24;
    public static final int CMD_SYNC = 1129208147;
    public static final int CMD_CNXN = 1314410051;
    public static final int CONNECT_VERSION = 16777216;
    public static final int CONNECT_MAXDATA = 4096;
    public static byte[] CONNECT_PAYLOAD;
    public static final int CMD_AUTH = 1213486401;
    public static final int AUTH_TYPE_TOKEN = 1;
    public static final int AUTH_TYPE_SIGNATURE = 2;
    public static final int AUTH_TYPE_RSA_PUBLIC = 3;
    public static final int CMD_OPEN = 1313165391;
    public static final int CMD_OKAY = 1497451343;
    public static final int CMD_CLSE = 1163086915;
    public static final int CMD_WRTE = 1163154007;
    static {
        try {
            CONNECT_PAYLOAD = "host::\u0000".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public AdbProtocol() {
    }

    private static int getPayloadChecksum(byte[] payload) {
        int checksum = 0;
        byte[] var5 = payload;
        int var4 = payload.length;
        for(int var3 = 0; var3 < var4; ++var3) {
            byte b = var5[var3];
            if (b >= 0) {
                checksum += b;
            } else {
                checksum += b + 256;
            }
        }
        return checksum;
    }

    public static boolean validateMessage(AdbMessage msg) {
        if (msg.command != ~msg.magic) {
            return false;
        } else {
            return msg.payloadLength == 0 || getPayloadChecksum(msg.payload) == msg.checksum;
        }
    }

    public static byte[] generateMessage(int cmd, int arg0, int arg1, byte[]payload) {
        ByteBuffer message;
        if (payload != null) {
            message = ByteBuffer.allocate(24 + payload.length).order(ByteOrder.LITTLE_ENDIAN);
        } else {
            message = ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN);
        }
        message.putInt(cmd);
        message.putInt(arg0);
        message.putInt(arg1);
        if (payload != null) {
            message.putInt(payload.length);
            message.putInt(getPayloadChecksum(payload));
        } else {
            message.putInt(0);
            message.putInt(0);
        }
        message.putInt(~cmd);
        if (payload != null) {
            message.put(payload);
        }
        return message.array();
    }

    public static byte[] generateConnect() {
        return generateMessage(AdbProtocol.CMD_CNXN, AdbProtocol.CONNECT_VERSION, AdbProtocol.CONNECT_MAXDATA, CONNECT_PAYLOAD);
    }

    public static byte[] generateAuth(int type, byte[]data) {
        return generateMessage(AdbProtocol.CMD_AUTH, type, 0, data);
    }

    public static byte[] generateOpen(int localId, String destination) throws UnsupportedEncodingException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(destination.length() + 1);
        byteBuffer.put(destination.getBytes("UTF-8"));
        byteBuffer.put((byte) 0);
        return generateMessage(AdbProtocol.CMD_OPEN, localId, 0, byteBuffer.array());
    }

    public static byte[] generateWrite(int localId, int remoteId, byte[] data) {
        return generateMessage(AdbProtocol.CMD_WRTE, localId, remoteId, data);
    }

    public static byte[] generateClose(int localId, int remoteId) {
        return generateMessage(AdbProtocol.CMD_CLSE, localId, remoteId, null);
    }

    public static byte[] generateReady(int localId, int remoteId) {
        return generateMessage(AdbProtocol.CMD_OKAY, localId, remoteId, null);
    }

    static final class AdbMessage {
        public int command;
        public int arg0;
        public int arg1;
        public int payloadLength;
        public int checksum;
        public int magic;
        public byte[] payload;

        AdbMessage() {
        }

        public static AdbMessage parseAdbMessage(InputStream inputStream) throws IOException {
            AdbMessage msg = new AdbMessage();
            //缓冲区
            ByteBuffer header = ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN);
            //读头部(24字节)
            int byteRead = 0;
            int alreadyRead = 0;
            do {
                byteRead = inputStream.read(header.array(), alreadyRead, 24 - alreadyRead);
                if (byteRead < 0) {
                    throw new IOException("AdbProtocol: Stream is closed!");
                }
                alreadyRead += byteRead;
            } while (alreadyRead < 24);

            msg.command = header.getInt();
            msg.arg0 = header.getInt();
            msg.arg1 = header.getInt();
            msg.payloadLength = header.getInt();
            msg.checksum = header.getInt();
            msg.magic = header.getInt();
            //读数据
            if (msg.payloadLength > 0) {
                msg.payload = new byte[msg.payloadLength];
                alreadyRead = 0;
                do {
                    byteRead = inputStream.read(msg.payload, alreadyRead, msg.payloadLength - alreadyRead);
                    if (byteRead < 0) {
                        throw new IOException("AdbProtocol: Stream is closed!");
                    }
                    alreadyRead += byteRead;
                } while (alreadyRead < msg.payloadLength);
            }

            return msg;
        }
    }

}
