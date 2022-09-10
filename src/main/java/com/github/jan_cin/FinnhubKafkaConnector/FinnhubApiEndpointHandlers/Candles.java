package com.github.jan_cin.FinnhubKafkaConnector.FinnhubApiEndpointHandlers;

import com.github.jan_cin.FinnhubKafkaConnector.Utils.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Candles {
    private static final Logger logger = LoggerFactory.getLogger(Candles.class);

    public static String getCandles(String marketType, String tickerSymbol, int resolution, Long epochTimeFrom, Long epochTimeTo, String apiToken) throws IOException {
        // build URL for request
        String urlStr = String.format("https://finnhub.io/api/v1/%s/candle?" +
                        "symbol=%s" +
                        "&resolution=%d" +
                        "&from=%d" +
                        "&to=%d" +
                        "&token=%s",
                marketType, tickerSymbol, resolution, epochTimeFrom, epochTimeTo, apiToken);
        // request data from FinnHub
        logger.info("Getting candles for: " + urlStr);
        return HttpHandler.getResponse(urlStr);
    }

    public static long findLastFinishedCandle(long epochTimeSeconds, int candleMinuteResolution) {
        return epochTimeSeconds - (epochTimeSeconds % (candleMinuteResolution * 60L));
    }

    public static long findNextCandleStart(long lastReadTimestamp, int candleResolutionMinutes) {
        long truncatedLastReadTimestamp = findLastFinishedCandle(lastReadTimestamp, candleResolutionMinutes);
        return truncatedLastReadTimestamp + 60L * candleResolutionMinutes;
    }


}