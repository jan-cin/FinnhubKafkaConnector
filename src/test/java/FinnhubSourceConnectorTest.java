import com.github.jan_cin.FinnhubKafkaConnector.FinnhubSourceConfig;
import com.github.jan_cin.FinnhubKafkaConnector.FinnhubSourceConnector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinnhubSourceConnectorTest {
    @Test
    public void taskConfigsTest() {
        String testingTickersList = "AAA,BBB,CCC,DDD,EEE";
        String configTickersName = FinnhubSourceConfig.SYMBOLS_NAME;

        Map<String, String> cfg = new HashMap<>();
        cfg.put(configTickersName, testingTickersList);
        cfg.put(FinnhubSourceConfig.RESOLUTION_NAME, "5");
        cfg.put(FinnhubSourceConfig.TOPIC_NAME, "topic_test");
        cfg.put(FinnhubSourceConfig.TOKEN_NAME, "tokenname");
        cfg.put(FinnhubSourceConfig.MARKET_TYPE_NAME, "stock");

        FinnhubSourceConnector fh = new FinnhubSourceConnector() ;
        fh.start(cfg);


        List<Map<String, String>> res1 = fh.taskConfigs(1);
        List<Map<String, String>> res2 = fh.taskConfigs(2);
        List<Map<String, String>> res5 = fh.taskConfigs(5);
        List<Map<String, String>> res99 = fh.taskConfigs(99);

        // assert number of tasks
        Assertions.assertEquals(res1.size(), 1);
        Assertions.assertEquals(res2.size(), 2);
        Assertions.assertEquals(res5.size(), 5);
        Assertions.assertEquals(res99.size(), 5);

        // assert that we all tickers are present and in expected order
        Assertions.assertEquals(res1.get(0).get(configTickersName), testingTickersList);

        Assertions.assertEquals(res2.get(0).get(configTickersName), "AAA,BBB,CCC");
        Assertions.assertEquals(res2.get(1).get(configTickersName), "DDD,EEE");

        Assertions.assertEquals(res5.get(0).get(configTickersName), "AAA");
        Assertions.assertEquals(res5.get(1).get(configTickersName), "BBB");
        Assertions.assertEquals(res5.get(2).get(configTickersName), "CCC");
        Assertions.assertEquals(res5.get(3).get(configTickersName), "DDD");
        Assertions.assertEquals(res5.get(4).get(configTickersName), "EEE");

        Assertions.assertEquals(res99.get(0).get(configTickersName), "AAA");
        Assertions.assertEquals(res99.get(1).get(configTickersName), "BBB");
        Assertions.assertEquals(res99.get(2).get(configTickersName), "CCC");
        Assertions.assertEquals(res99.get(3).get(configTickersName), "DDD");
        Assertions.assertEquals(res99.get(4).get(configTickersName), "EEE");
    }
}
