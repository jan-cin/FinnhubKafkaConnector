import com.github.jan_cin.FinnhubKafkaConnector.FinnhubApiEndpointHandlers.Candles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class CandlesTest {
    @Test
    public void findNextCandleStartTest() {
        Map<Long, Map<Integer, Long>> testingDict = new HashMap<>();
        //// all timestamp should truncate to 0 seconds
        // 2022-08-31 23:59:00 truncates to:
        //      resolution  1: 2022-09-01 00:00
        //      resolution  5: 2022-09-01 00:00
        //      resolution 15: 2022-09-01 00:00
        //      resolution 30: 2022-09-01 00:00
        //      resolution 60: 2022-09-01 00:00
        testingDict.put(1661990340L, new HashMap<Integer,Long>());
        testingDict.get(1661990340L).put( 1, 1661990400L);
        testingDict.get(1661990340L).put( 5, 1661990400L);
        testingDict.get(1661990340L).put(15, 1661990400L);
        testingDict.get(1661990340L).put(30, 1661990400L);
        testingDict.get(1661990340L).put(60, 1661990400L);
        // 2022-08-31 23:59:45 truncates to:
        //      resolution  1: 2022-09-01 00:00
        //      resolution  5: 2022-09-01 00:00
        //      resolution 15: 2022-09-01 00:00
        //      resolution 30: 2022-09-01 00:00
        //      resolution 60: 2022-09-01 00:00
        testingDict.put(1661990385L, new HashMap<Integer,Long>());
        testingDict.get(1661990385L).put( 1, 1661990400L);
        testingDict.get(1661990385L).put( 5, 1661990400L);
        testingDict.get(1661990385L).put(15, 1661990400L);
        testingDict.get(1661990385L).put(30, 1661990400L);
        testingDict.get(1661990385L).put(60, 1661990400L);
        // 2022-09-01 00:00:00 truncates to:
        //      resolution  1: 2022-09-01 00:01
        //      resolution  5: 2022-09-01 00:05
        //      resolution 15: 2022-09-01 00:15
        //      resolution 30: 2022-09-01 00:30
        //      resolution 60: 2022-09-01 01:00
        testingDict.put(1661990400L, new HashMap<Integer,Long>());
        testingDict.get(1661990400L).put( 1, 1661990460L);
        testingDict.get(1661990400L).put( 5, 1661990700L);
        testingDict.get(1661990400L).put(15, 1661991300L);
        testingDict.get(1661990400L).put(30, 1661992200L);
        testingDict.get(1661990400L).put(60, 1661994000L);
        // 2022-09-01 00:00:15 truncates to:
        //      resolution  1: 2022-09-01 00:01
        //      resolution  5: 2022-09-01 00:05
        //      resolution 15: 2022-09-01 00:15
        //      resolution 30: 2022-09-01 00:30
        //      resolution 60: 2022-09-01 01:00
        testingDict.put(1661990415L, new HashMap<Integer,Long>());
        testingDict.get(1661990415L).put( 1, 1661990460L);
        testingDict.get(1661990415L).put( 5, 1661990700L);
        testingDict.get(1661990415L).put(15, 1661991300L);
        testingDict.get(1661990415L).put(30, 1661992200L);
        testingDict.get(1661990415L).put(60, 1661994000L);
        // 2022-09-01 01:02:03 truncates to:
        //      resolution  1: 2022-09-01 01:03
        //      resolution  5: 2022-09-01 01:05
        //      resolution 15: 2022-09-01 01:15
        //      resolution 30: 2022-09-01 01:30
        //      resolution 60: 2022-09-01 02:00
        testingDict.put(1661994123L, new HashMap<Integer,Long>());
        testingDict.get(1661994123L).put( 1, 1661994180L);
        testingDict.get(1661994123L).put( 5, 1661994300L);
        testingDict.get(1661994123L).put(15, 1661994900L);
        testingDict.get(1661994123L).put(30, 1661995800L);
        testingDict.get(1661994123L).put(60, 1661997600L);


        // iterate over timestamps from which we want to find next timestamp
        // norm
        for (Long timestampsKey : testingDict.keySet()) {
            // then iterate over different resolutions
            for (Map.Entry<Integer, Long> resolutionsEntry : testingDict.get(timestampsKey).entrySet()) {
                long nextCandleStart = Candles.findNextCandleStart(timestampsKey, resolutionsEntry.getKey());
                Assertions.assertEquals(
                        nextCandleStart,
                        resolutionsEntry.getValue(),
                        String.format("Failed findLastFinishedCandle(%s,%s)", timestampsKey, resolutionsEntry.getKey()));
            }
        }
    }

    @Test
    public void findLastFinishedCandleTest() {
        Map<Long, Map<Integer, Long>> testingDict = new HashMap<>();
        //// all timestamp should truncate to 0 seconds
        // 2022-08-31 23:59:00 truncates to:
        //      resolution  1: 2022-08-31 23:59
        //      resolution  5: 2022-08-31 23:55
        //      resolution 15: 2022-08-31 23:45
        //      resolution 30: 2022-08-31 23:30
        //      resolution 60: 2022-08-31 23:00
        testingDict.put(1661990340L, new HashMap<Integer,Long>());
        testingDict.get(1661990340L).put( 1, 1661990340L);
        testingDict.get(1661990340L).put( 5, 1661990100L);
        testingDict.get(1661990340L).put(15, 1661989500L);
        testingDict.get(1661990340L).put(30, 1661988600L);
        testingDict.get(1661990340L).put(60, 1661986800L);
        // 2022-08-31 23:59:45 truncates to:
        //      resolution  1: 2022-08-31 23:59
        //      resolution  5: 2022-08-31 23:55
        //      resolution 15: 2022-08-31 23:45
        //      resolution 30: 2022-08-31 23:30
        //      resolution 60: 2022-08-31 23:00
        testingDict.put(1661990385L, new HashMap<Integer,Long>());
        testingDict.get(1661990385L).put( 1, 1661990340L);
        testingDict.get(1661990385L).put( 5, 1661990100L);
        testingDict.get(1661990385L).put(15, 1661989500L);
        testingDict.get(1661990385L).put(30, 1661988600L);
        testingDict.get(1661990385L).put(60, 1661986800L);
        // 2022-09-01 00:00:00 truncates to:
        //      resolution  1: 2022-09-01 00:00
        //      resolution  5: 2022-09-01 00:00
        //      resolution 15: 2022-09-01 00:00
        //      resolution 30: 2022-09-01 00:00
        //      resolution 60: 2022-09-01 00:00
        testingDict.put(1661990400L, new HashMap<Integer,Long>());
        testingDict.get(1661990400L).put( 1, 1661990400L);
        testingDict.get(1661990400L).put( 5, 1661990400L);
        testingDict.get(1661990400L).put(15, 1661990400L);
        testingDict.get(1661990400L).put(30, 1661990400L);
        testingDict.get(1661990400L).put(60, 1661990400L);
        // 2022-09-01 00:00:15 truncates to:
        //      resolution  1: 2022-09-01 00:00
        //      resolution  5: 2022-09-01 00:00
        //      resolution 15: 2022-09-01 00:00
        //      resolution 30: 2022-09-01 00:00
        //      resolution 60: 2022-09-01 00:00
        testingDict.put(1661990415L, new HashMap<Integer,Long>());
        testingDict.get(1661990415L).put( 1, 1661990400L);
        testingDict.get(1661990415L).put( 5, 1661990400L);
        testingDict.get(1661990415L).put(15, 1661990400L);
        testingDict.get(1661990415L).put(30, 1661990400L);
        testingDict.get(1661990415L).put(60, 1661990400L);
        // 2022-09-01 01:02:03 truncates to:
        //      resolution  1: 2022-09-01 01:02
        //      resolution  5: 2022-09-01 01:00
        //      resolution 15: 2022-09-01 01:00
        //      resolution 30: 2022-09-01 01:00
        //      resolution 60: 2022-09-01 01:00
        testingDict.put(1661994123L, new HashMap<Integer,Long>());
        testingDict.get(1661994123L).put( 1, 1661994120L);
        testingDict.get(1661994123L).put( 5, 1661994000L);
        testingDict.get(1661994123L).put(15, 1661994000L);
        testingDict.get(1661994123L).put(30, 1661994000L);
        testingDict.get(1661994123L).put(60, 1661994000L);

        // iterate over timestamps that we want to truncate
        for (Long timestampsKey : testingDict.keySet()) {
            // then iterate over different resolutions
            for (Map.Entry<Integer, Long> resolutionsEntry : testingDict.get(timestampsKey).entrySet()) {
                long truncRes = Candles.findLastFinishedCandle(timestampsKey, resolutionsEntry.getKey());
                Assertions.assertEquals(
                        truncRes,
                        resolutionsEntry.getValue(),
                        String.format("Failed findLastFinishedCandle(%s,%s)", timestampsKey, resolutionsEntry.getKey()));
            }
        }
    }
}
