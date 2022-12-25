package ee.eid.manager;

public class Password {
    public byte[] data;

    public Password(short dataLen) {
        data = new byte[dataLen];
    }
}
