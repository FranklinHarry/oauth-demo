package cloudbees;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class Utils {
    public static final ObjectMapper om = new ObjectMapper().configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
}
