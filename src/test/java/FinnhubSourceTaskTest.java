import com.github.jan_cin.FinnhubKafkaConnector.FinnhubSourceTask;
import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FinnhubSourceTaskTest {
    @Test
    public void findMaxTimestampTest() {
        FinnhubSourceTask fht = new FinnhubSourceTask();
        Assertions.assertEquals(5L,fht.findMaxTimestamp(new JSONArray("[1,2,3,4,5]")));
        Assertions.assertEquals(-1L,fht.findMaxTimestamp(new JSONArray("[-1,-2,-99]")));
        Assertions.assertEquals(0L,fht.findMaxTimestamp(new JSONArray("[0,0,0,0,0]")));
        Assertions.assertEquals(0L,fht.findMaxTimestamp(new JSONArray("[]")));
        Assertions.assertEquals(1661994126L,fht.findMaxTimestamp(new JSONArray("[1661994123, 1661994124, 1661994125, 1661994126]")));
    }
}
