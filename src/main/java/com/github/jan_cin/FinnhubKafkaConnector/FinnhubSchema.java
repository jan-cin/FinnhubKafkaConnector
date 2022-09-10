package com.github.jan_cin.FinnhubKafkaConnector;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;

public class FinnhubSchema {
    // keys
    public static String NAME_SYMBOL = "symbol";
    public static String NAME_EPOCH_TIME = "epoch_time";

    // values
    public static String NAME_CLOSE = "close";
    public static String NAME_HIGH = "high";
    public static String NAME_LOW = "low";
    public static String NAME_OPEN = "open";
    public static String NAME_TIMESTAMP = "timestamp";
    public static String NAME_VOLUME = "volume";

    // key schema
    public static Schema KEY_SCHEMA = SchemaBuilder.struct().name(NAME_SYMBOL +"_"+NAME_EPOCH_TIME)
            .version(1)
            .field(NAME_SYMBOL, Schema.STRING_SCHEMA)
            .field(NAME_EPOCH_TIME, Schema.INT64_SCHEMA)
            .build();

    // value schema
            public static Schema VALUE_SCHEMA = SchemaBuilder.struct().name("")
            .version(1)
            .field(NAME_CLOSE, Schema.FLOAT32_SCHEMA)
            .field(NAME_HIGH, Schema.FLOAT32_SCHEMA)
            .field(NAME_LOW, Schema.FLOAT32_SCHEMA)
            .field(NAME_OPEN, Schema.FLOAT32_SCHEMA)
            .field(NAME_TIMESTAMP, Schema.INT64_SCHEMA)
            .field(NAME_VOLUME, Schema.INT64_SCHEMA)
            .field(NAME_SYMBOL, Schema.STRING_SCHEMA)
            .build();
}
