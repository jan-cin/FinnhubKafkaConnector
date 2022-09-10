package com.github.jan_cin.FinnhubKafkaConnector;

import com.github.jan_cin.FinnhubKafkaConnector.FinnhubApiEndpointHandlers.Candles;
import com.github.jan_cin.FinnhubKafkaConnector.Utils.VersionUtil;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinnhubSourceTask extends SourceTask {

    private static final Logger logger = LoggerFactory.getLogger(FinnhubSourceTask.class);

    private FinnhubSourceConfig config;
    private String apiToken;
    private String topic;
    private String marketType;
    private List<String> tickers;
    private int resolution;
    private String currentTicker;
    private int getLastHours;
    private int currentIndex;
    private Map<String, Long> lastReceivedTimestamps;
    private Map<String, Long> lastCalledTimestamps;

    @Override
    public String version() {
        return VersionUtil.getVersion();
    }

    @Override
    public void start(Map<String, String> map) {
        this.config = new FinnhubSourceConfig(map);
        this.apiToken = this.config.getString(FinnhubSourceConfig.TOKEN_NAME);
        this.topic = this.config.getString(FinnhubSourceConfig.TOPIC_NAME);
        this.marketType = this.config.getString(FinnhubSourceConfig.MARKET_TYPE_NAME);
        this.tickers = this.config.getList(FinnhubSourceConfig.SYMBOLS_NAME);
        // capitalize tickers and clean from whitespaces
        for (int i = 0; i < tickers.size(); i++) {
            this.tickers.set(
                    i,
                    this.tickers.get(i).toUpperCase().replaceAll("\\s+",""));
        }

        this.resolution = this.config.getInt(FinnhubSourceConfig.RESOLUTION_NAME);
        this.getLastHours = this.config.getInt(FinnhubSourceConfig.GET_LAST_N_HOURS_NAME);

        this.currentIndex = 0;
        this.currentTicker = tickers.get(currentIndex);

        // set timestamps
        this.lastReceivedTimestamps = new HashMap<>();
        this.lastCalledTimestamps = new HashMap<>();
        for (String ticker : tickers) {
            long from = initializeLastTimestamp(ticker);
            lastReceivedTimestamps.put(ticker, from);
            lastCalledTimestamps.put(ticker, 0L);
        }
    }

    private long initializeLastTimestamp(String ticker) {
        Map<String, Object> sourcePartition = createSourcePartition(ticker);

        Map<String, Object> lastSourceOffset;
        lastSourceOffset = context.offsetStorageReader().offset(sourcePartition); // ticker is sourcePartition
        if (lastSourceOffset != null) {
            if (lastSourceOffset.containsKey(FinnhubSchema.NAME_EPOCH_TIME)) {
                return Long.parseLong(String.valueOf(lastSourceOffset.get(FinnhubSchema.NAME_EPOCH_TIME)));
            }
        }
        // if offset is missing, then create it, based on
        long nHoursBack = Instant.now()
                .minus(this.getLastHours, ChronoUnit.HOURS)
                .minus(this.resolution, ChronoUnit.MINUTES) // substract 1 unit of resolution, so we start from the exact -n horus
                .atZone(ZoneOffset.UTC)
                .withSecond(0).toEpochSecond();
        return nHoursBack;
    }

    @Override
    public List<SourceRecord> poll() {
        try {
            logger.info("Polling " + currentTicker);
            // find next candle after the last received for ticker
            long from = Candles.findNextCandleStart(lastReceivedTimestamps.get(currentTicker), this.resolution);
            // find last closed candle
            long nowTs = Instant.now().atZone(ZoneOffset.UTC).toEpochSecond();
            long to = Candles.findLastFinishedCandle(nowTs, this.resolution);

            // `from` can be greater than `to`, when we
            if (from >= to) {
                Thread.sleep(1000);
                lastCalledTimestamps.put(currentTicker, nowTs);
                moveToNextSymbol();
                return null;
            }

            // check if we already asked in current candle
            // if yes, then probably there is no new data and wait for the current candle to finish
            // and then poll again for the same ticker
            long lastCall = lastCalledTimestamps.get(currentTicker);
            if (to <= lastCall) {
                // sleep until end of the current candle
                Thread.sleep(((to + 60L * this.resolution) - nowTs)*1000);
                Thread.sleep(2000); // sleep additional time
                return null;
            }

            // poll data
            String response = Candles.getCandles(marketType, currentTicker, resolution, from, to, apiToken);
            lastCalledTimestamps.put(currentTicker, nowTs);
            JSONObject responseJson = new JSONObject(response);
            // most likely, error means that we hit rate limit. Sleep for 10 seconds
            if (responseJson.has("error")) {
                String errorMessage = responseJson.getString("error");
                logger.error("Found error in response. The message was: " + errorMessage);
                Thread.sleep(10 * 1000);
            }

            String status = "";
            if (responseJson.has("s")) {
                status = responseJson.getString("s");
            } else {
                logger.error("Status was: " + responseJson);
            }
            // check status of the response, if it is not 'ok' then don't send data and move to next ticker
            if (!status.equals("ok")) {
                logger.error(String.format("Status response for ticker '%s' was: '%s'. Skipping to next ticker", currentTicker, status));
                moveToNextSymbol();
                return null;
            }

            // parse response
            JSONArray opens = responseJson.getJSONArray("o");
            JSONArray highs = responseJson.getJSONArray("h");
            JSONArray lows = responseJson.getJSONArray("l");
            JSONArray closes = responseJson.getJSONArray("c");
            JSONArray volumes = responseJson.getJSONArray("v");
            JSONArray timestamps = responseJson.getJSONArray("t");
            int numOfRecords = opens.length();

            // create source partition and source offset
            Map<String, Object> sourcePartition = createSourcePartition(currentTicker);
            Long maxReceivedTimestamp = findMaxTimestamp(timestamps);
            Map<String, Object> sourceOffset = createSourceOffset(maxReceivedTimestamp);

            // create SourceRecords
            List<SourceRecord> sourceRecords = new ArrayList<>();
            for (int i = 0; i < numOfRecords; i++) {
                // create source record
                SourceRecord sr = new SourceRecord(
                        sourcePartition,
                        sourceOffset,
                        topic,
                        null,
                        FinnhubSchema.KEY_SCHEMA,
                        new Struct(FinnhubSchema.KEY_SCHEMA)
                                .put(FinnhubSchema.NAME_SYMBOL, currentTicker)
                                .put(FinnhubSchema.NAME_EPOCH_TIME, timestamps.getLong(i)),
                        FinnhubSchema.VALUE_SCHEMA,
                        new Struct(FinnhubSchema.VALUE_SCHEMA)
                                .put(FinnhubSchema.NAME_OPEN, opens.getBigDecimal(i).floatValue())
                                .put(FinnhubSchema.NAME_HIGH, highs.getBigDecimal(i).floatValue())
                                .put(FinnhubSchema.NAME_LOW, lows.getBigDecimal(i).floatValue())
                                .put(FinnhubSchema.NAME_CLOSE, closes.getBigDecimal(i).floatValue())
                                .put(FinnhubSchema.NAME_VOLUME, volumes.getLong(i))
                                .put(FinnhubSchema.NAME_TIMESTAMP, timestamps.getLong(i))
                                .put(FinnhubSchema.NAME_SYMBOL, currentTicker),
                        timestamps.getLong(i)
                );
                sourceRecords.add(sr);
            }
            lastReceivedTimestamps.put(currentTicker, maxReceivedTimestamp);
            moveToNextSymbol();
            return sourceRecords;
        }
        catch (Exception e) {
            logger.error(String.format("Exception in %s: %s", currentIndex, currentTicker), e);
            moveToNextSymbol();
            return null;
        }
    }

    private void moveToNextSymbol() {
        currentIndex = currentIndex + 1 == tickers.size() ? 0 : currentIndex + 1;
        currentTicker = tickers.get(currentIndex);
    }

    public long findMaxTimestamp(JSONArray timestamps) {
        return timestamps.toList().stream()
                .mapToLong(x -> Long.parseLong(String.valueOf(x)))
                .max()
                .orElse(0L);
    }

    private Map<String, Object> createSourcePartition(String ticker) {
        return new HashMap<String, Object>() {{
            put(FinnhubSchema.NAME_SYMBOL, ticker);
        }};
    }

    private Map<String, Object> createSourceOffset(Long maxReceivedTimestamp) {
        return new HashMap<String,Object>() {{
            put(FinnhubSchema.NAME_EPOCH_TIME, maxReceivedTimestamp);
        }};
    }

    @Override
    public void stop() {
    }
}
