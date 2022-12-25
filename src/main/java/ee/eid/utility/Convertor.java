package ee.eid.utility;

import java.nio.ByteBuffer;

public class Convertor {
    public static byte[] convertIntToByteArray(int toConvert) {
        byte[] bytes = ByteBuffer.allocate(4).putInt(toConvert).array();

//        for (byte b : bytes) {
//            System.out.format("0x%x ", b);
//        }

        return bytes;

    }
}
