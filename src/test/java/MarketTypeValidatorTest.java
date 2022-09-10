import com.github.jan_cin.FinnhubKafkaConnector.FinnhubSourceConfig;
import com.github.jan_cin.FinnhubKafkaConnector.Validators.MarketTypeValidator;
import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class MarketTypeValidatorTest {
    @Test
    public void ensureValidCorrectSymbolsTest() {
        MarketTypeValidator validator = new MarketTypeValidator();

        List<String> okList = Arrays.asList("stock","crypto","forex");

        for (String s : okList) {
            Assertions.assertDoesNotThrow(() ->
                            validator.ensureValid(FinnhubSourceConfig.MARKET_TYPE_NAME, s),
                    String.format("Element '%s' throwed exception",s)
            );
        }
    }

    @Test
    public void ensureValidWrongSymbolsTest() {
        MarketTypeValidator validator = new MarketTypeValidator();

        List<String> wrongList = Arrays.asList("this","are","wrong","types",null,"");

        for (String s : wrongList) {
            Assertions.assertThrows(ConfigException.class,
                    () -> validator.ensureValid(FinnhubSourceConfig.MARKET_TYPE_NAME, s),
                    String.format("Element '%s' didn't throw exception",s)
            );
        }
    }
}
