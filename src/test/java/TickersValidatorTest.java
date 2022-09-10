import com.github.jan_cin.FinnhubKafkaConnector.FinnhubSourceConfig;
import com.github.jan_cin.FinnhubKafkaConnector.Validators.TickersValidator;
import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class TickersValidatorTest {

    @Test
    void tickersValidatorCorrectTickersTest() {
        TickersValidator validator = new TickersValidator();

        List<String> okList = Arrays.asList(
                "AAA,BBB,CCC,DDD, EEE,DG",
                "AAA,BBB,ccc,d11d"
        );
        for (String s : okList) {
            Assertions.assertDoesNotThrow(() ->
                            validator.ensureValid(FinnhubSourceConfig.SYMBOLS_NAME, s),
                    String.format("Element '%s' throwed exception",s)
            );
        }
    }

    @Test
    void tickerValidtorWrongTickersTest() {
        TickersValidator validator = new TickersValidator();

        List<String> wrongList = Arrays.asList(
                "AAA,,BBB,CCC", //empty element
                "AAA,BBB,AAAA,AAA", // duplicated element (case insensitive)
                "AAA,BBB,ccc,aaa,d11d", //duplicated element (case sensitive)
                "", // empty list
                ",,,,," // empty elements
        );

        for (String s : wrongList) {
            Assertions.assertThrows(ConfigException.class,
                    () -> validator.ensureValid(FinnhubSourceConfig.SYMBOLS_NAME, s),
                    String.format("Element '%s' didn't throw exception",s)
            );
        }
    }


}
