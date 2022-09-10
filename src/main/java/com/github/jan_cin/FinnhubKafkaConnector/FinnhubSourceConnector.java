package com.github.jan_cin.FinnhubKafkaConnector;

import com.github.jan_cin.FinnhubKafkaConnector.Utils.VersionUtil;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinnhubSourceConnector extends SourceConnector {
    private FinnhubSourceConfig config;

    @Override
    public void start(Map<String, String> map) {
        this.config = new FinnhubSourceConfig(map);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return FinnhubSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        // if maxTasks is greater than size of tickers, then we can't have all tasks allocated
        List<String> tickers = config.getList(FinnhubSourceConfig.SYMBOLS_NAME);
        int tickersListSize = config.getList(FinnhubSourceConfig.SYMBOLS_NAME).size();
        maxTasks = Math.min(maxTasks, tickersListSize);

        int partitionSize = (int) Math.ceil((double) tickersListSize / maxTasks);

        ArrayList<Map<String, String>> configs = new ArrayList<>();

        for (int i = 0; i < tickersListSize; i += partitionSize) {
            Map<String, String> c = new HashMap<>(config.originalsStrings());
            List<String> tickersSublist = tickers.subList(i, Math.min(i + partitionSize, tickersListSize));
            c.put(
                    FinnhubSourceConfig.SYMBOLS_NAME,
                    String.join(",", tickersSublist));
            configs.add(c);
        }
        return configs;
    }

    @Override
    public void stop() {
    }

    @Override
    public ConfigDef config() {
        return FinnhubSourceConfig.configDef();
    }

    @Override
    public String version() {
        return VersionUtil.getVersion();
    }
}
