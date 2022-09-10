package com.github.jan_cin.FinnhubKafkaConnector;

import com.github.jan_cin.FinnhubKafkaConnector.Validators.MarketTypeValidator;
import com.github.jan_cin.FinnhubKafkaConnector.Validators.ResolutionValidator;
import com.github.jan_cin.FinnhubKafkaConnector.Validators.TickersValidator;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class FinnhubSourceConfig extends AbstractConfig {
    public static final String TOKEN_NAME = "api.token";
    private static final String TOKEN_DOC = "Finnhub API token";

    public static final String TOPIC_NAME = "topic";
    private static final String TOPIC_DOC = "Topic to write data to";

    public static final String MARKET_TYPE_NAME = "market_type";
    private static final String MARKET_TYPE_DEFAULT_VALUE = "stock";
    private static final String MARKET_TYPE_DOC = "Type of the market, can be one of: 'stock', 'crypto' or 'forex'.";

    public static final String SYMBOLS_NAME = "symbols";
    private static final String SYMBOLS_DEFAULT_VALUE = null; // turns out there is no method with validator but without default value
    private static final String SYMBOLS_DOC = "List of tickers to get OHLCV info, e.g.: 'AAPL,TSL,GOOGL'";

    public static final String RESOLUTION_NAME = "resolution";
    private static final int RESOLUTION_DEFAULT_VALUE = 1;
    private static final String RESOLUTION_DOC = "Timeframe of the candles, allowed values: 1,5,15,30,60";

    public static final String GET_LAST_N_HOURS_NAME = "get.last.n.hours";
    private static final int GET_LAST_N_HOURS_DEFAULT_VALUE = 3;
    private static final String GET_LAST_N_HOURS_DOC = "For how many hours we get the recent history prices";

    public FinnhubSourceConfig(Map<?, ?> originals) {
        super(configDef(), originals);
    }

    protected static ConfigDef configDef() {
        return new ConfigDef()
                .define(
                        TOKEN_NAME,
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        TOKEN_DOC)
                .define(TOPIC_NAME,
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        TOPIC_DOC)
                .define(MARKET_TYPE_NAME,
                        ConfigDef.Type.STRING,
                        MARKET_TYPE_DEFAULT_VALUE,
                        new MarketTypeValidator(),
                        ConfigDef.Importance.HIGH,
                        MARKET_TYPE_DOC)
                .define(SYMBOLS_NAME,
                        ConfigDef.Type.LIST,
                        SYMBOLS_DEFAULT_VALUE,
                        new TickersValidator(),
                        ConfigDef.Importance.HIGH,
                        SYMBOLS_DOC)
                .define(RESOLUTION_NAME,
                        ConfigDef.Type.INT,
                        RESOLUTION_DEFAULT_VALUE,
                        new ResolutionValidator(),
                        ConfigDef.Importance.HIGH,
                        RESOLUTION_DOC)
                .define(GET_LAST_N_HOURS_NAME,
                        ConfigDef.Type.INT,
                        GET_LAST_N_HOURS_DEFAULT_VALUE,
                        ConfigDef.Importance.LOW,
                        GET_LAST_N_HOURS_DOC);
    }
}
