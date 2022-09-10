# Finnhub Kafka Connector
## Introduction
This is kafka connector for sourcing OHLCV<sup>1</sup> candles from Finnhub API into Kafka.\
It allows streaming OHLCV prices from Finnhub API into Kafka. 
It supports streaming from Finnhub API endpoints for market types:
* stock: https://finnhub.io/docs/api/stock-candles
* crypto: https://finnhub.io/docs/api/crypto-candles
* forex: https://finnhub.io/docs/api/forex-candles 

Supported candle resolutions are 1, 5, 15, 30 and 60 minutes.<br /><br /><br />
<sup><sub>1 - abbreviation for Open, High, Low, Close prices and Volume</sub></sup>

## Configuration

```json
{
	"name":"finnhub_connect_worker_1",
	"config":{
		"tasks.max":1,
		"connector.class":"FinnhubSourceConnector",
		
		"api.token":"set-your-api-token-here",
		"topic":"finnhub_candles",
		"market_type":"stock",
		"symbols":"AAPL,GOOGL,TSLA",
		"resolution":"1",
		"get.last.n.hours":24
		}
}
```

<br />

| Name             | Description                                                                     | Type | Default | Valid Values       | Importance |
|------------------|---------------------------------------------------------------------------------|------|--------:|--------------------|------------|
| tasks.max        | How many tasks can worker run, can't have more tasks.max than symbols           | int  |         |                    | High       |
| api.token        | Token for Finnhub API                                                           | str  |         |                    | High       |
| topic            | Topic to which data will be streamed                                            | str  |         |                    | High       |
| market_type      | Market type which symbols belong to                                             | str  |         | stock,crypto,forex | High       |
| symbols          | Tickers to pull data from, names from Finnhub API must be used, comma separated | list |         |                    | High       |
| resolution       | Resolution in minutes                                                           | str  | 1       | 1,5,15,30,60       | High       |
| get.last.n.hours | If it is first start, how much of historical candles should be pulled           | int  | 3       |                    | Low        |


## Example usage
1. Set `api.token` field with you Finnhub token in `config\finnhub_kafka_connector_example_config.json` file
2. Run `mvn clean package` in project folder to build connector into single jar file
3. `docker-compose up -d` in project folder to run docker containers, which:
   1. Starts zookeper, kafka, schema and kafka connect containers
   2. `config\register_config.sh` is run in kafka-connect container. It calls Connect REST Interface to
register FinnhubKafkaConnector worker  with sample settings (`config\finnhub_kafka_connector_example_config.json`)

## Sample record
```json
{
   "schema":{
      "type":"struct",
      "fields":[
         {
            "type":"float",
            "optional":false,
            "field":"close"
         },
         {
            "type":"float",
            "optional":false,
            "field":"high"
         },
         {
            "type":"float",
            "optional":false,
            "field":"low"
         },
         {
            "type":"float",
            "optional":false,
            "field":"open"
         },
         {
            "type":"int64",
            "optional":false,
            "field":"timestamp"
         },
         {
            "type":"int64",
            "optional":false,
            "field":"volume"
         },
         {
            "type":"string",
            "optional":false,
            "field":"symbol"
         }
      ],
      "optional":false,
      "name":"",
      "version":1
   },
   "payload":{
      "close":154.0289,
      "high":154.11,
      "low":153.98,
      "open":154.105,
      "timestamp":1662663300,
      "volume":89038,
      "symbol":"AAPL"
   }
}
```

## Development
To allow debugging, change `KAFKA_DEBUG:` option to `y` in `docker-compose.yml` for kafka-connect
