import com.github.jan_cin.FinnhubKafkaConnector.FinnhubSourceConfig;
import com.github.jan_cin.FinnhubKafkaConnector.Validators.ResolutionValidator;
import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class ResolutionValidatorTest {
    @Test
    void resolutionValidatorCorrectResolutionTest() {
        ResolutionValidator validator = new ResolutionValidator();

        List<Integer> okList = Arrays.asList(1,5,15,30,60);

        for (Integer i : okList) {
            Assertions.assertDoesNotThrow(() ->
                            validator.ensureValid(FinnhubSourceConfig.RESOLUTION_NAME, i),
                    String.format("Element '%s' throwed exception",i)
            );
        }
    }

    @Test
    void resolutionValidatorWrongResolutionTest() {
        ResolutionValidator validator = new ResolutionValidator();

        List<Object> wrongLIst = Arrays.asList(2,3,6,-1,-500,500,100,1.5);

        for (Object i : wrongLIst) {
            Assertions.assertThrows(
                    ConfigException.class,
                    () -> validator.ensureValid(FinnhubSourceConfig.RESOLUTION_NAME, i),
                    String.format("Element '%s' didn't throw exception",i)
            );
        }
    }
}
