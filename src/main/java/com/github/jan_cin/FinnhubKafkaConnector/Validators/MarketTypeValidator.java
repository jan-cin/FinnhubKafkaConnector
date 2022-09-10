package com.github.jan_cin.FinnhubKafkaConnector.Validators;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.util.Arrays;
import java.util.List;

public class MarketTypeValidator implements ConfigDef.Validator  {
    @Override
    public void ensureValid(String name, Object value) {
        String valueStr = String.valueOf(value);
        List<String> allowedMarketTypes = Arrays.asList("stock","crypto","forex");
        if (!allowedMarketTypes.contains(valueStr)) {
            throw new ConfigException(name + " should be on of the values: 'stock', 'crypto' or 'forex'");
        }
    }
}
