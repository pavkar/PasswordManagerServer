package ee.eid.manager;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class Password implements Serializable {
    @Getter
    public transient byte[] data;
    @Getter
    public String origin;
    @Setter
    @Getter
    public byte[] position;

    public Password(String origin, String data) {
        this.origin = origin;
        this.data = data.getBytes(StandardCharsets.UTF_8);
    }

}
