package com.github.jan_cin.FinnhubKafkaConnector.Validators;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TickersValidator implements ConfigDef.Validator {
    @Override
    public void ensureValid(String name, Object value) {
        String valueStr = String.valueOf(value);
        // remove spaces
        valueStr = valueStr.replaceAll("\\s+","");
        List<String> valueList = Arrays.asList(valueStr.split(","));
        // capitalize all
        for (int i = 0; i < valueList.size(); i++) {
            valueList.set(i, valueList.get(i).toUpperCase());
        }

        if (valueList.size() == 0) {
            throw new ConfigException(String.format("Config for '%s' list can't be empty", name));
        }

        for (String s : valueList) {
            if (s.isEmpty()) {
                throw new ConfigException(String.format("One of '%s' is empty", name));
            }
        }

        Set<String> valueSet = new HashSet<>(valueList);
        if (valueList.size() != valueSet.size()) {
            throw new ConfigException(String.format("Config for '%s' list must be unique", name));
        }
    }
}
