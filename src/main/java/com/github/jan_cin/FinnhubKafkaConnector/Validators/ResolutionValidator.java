package com.github.jan_cin.FinnhubKafkaConnector.Validators;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.util.Arrays;
import java.util.List;

public class ResolutionValidator  implements ConfigDef.Validator {
    @Override
    public void ensureValid(String name, Object value) {

        int valueInt;
        try {
            valueInt = Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException numE) {
            throw new ConfigException("Resolution should be int with one of values: 1, 5, 15, 30, 60");
        }

        List<Integer> allowedResolutions = Arrays.asList(1,5,15,30,60);
        if (!allowedResolutions.contains(valueInt)) {
            throw new ConfigException("Resolution should be int with one of values: 1, 5, 15, 30, 60");
        }
    }
}
