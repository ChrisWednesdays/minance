package com.uk.wednesday.minance.service;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.CandlestickEvent;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.uk.wednesday.minance.model.CoinPriceModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import quant.fans.Indicators;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class WebSocketService {

    private BinanceApiWebSocketClient socketClient;

    private double basePrice = 0.0;
    private double currentPrice = 0.0;
    private double buyPrice = 0.0;
    private double sellPrice = 0.0;
    private double gainPrice = 0.0;

    private double currentRSI = 0.0;

    private boolean underPositionRSI = false;
    private boolean overPositionRSI = false;
    private boolean coinOwned = false;
    private boolean inPositionMA = false;

    private int RSI_OVERBOUGHT = 65;
    private int RSI_OVERSOLD = 35;


    List<Double> rsiValues = new ArrayList<>();
    Indicators indicators = new Indicators();
    double[] priceArray = null;
    double[] rsiArray = null;

    MongoTemplate mongoTemplate;

    @Autowired
    public WebSocketService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        initiateSocketClient();
    }

    public void subscribe() {
        candleStickEventOneMinute();
    }

    public void candleStickEventOneMinute() {

        socketClient.onCandlestickEvent("btcusdt", CandlestickInterval.ONE_MINUTE, response -> {

            String currentPrice = response.getClose();
            setCurrentPrice(Double.parseDouble(currentPrice));

            if (response.getBarFinal()) {

                rsiValues.add(Double.parseDouble(response.getClose()));
                priceArray = rsiValues.stream().mapToDouble(Double::doubleValue).toArray();

                calculateRsiIndicator();

                createRecord(cleanseData(response));
            }
        });
    }

    public double[] getRSIArray() {

        return rsiArray;
    }

    public static boolean isBetween(double x, double lower, double upper) {
        return lower <= x && x <= upper;
    }

    private double calculateRsiIndicator() {

        double dropSize = 0.0;

        if (priceArray.length > 14) {

            rsiArray = indicators.rsi(priceArray, 14);

            double currentRSI = rsiArray[rsiArray.length - 1];
            double lastRSI = rsiArray[rsiArray.length - 2];

            dropSize = lastRSI / currentRSI;
            setCurrentRSI(currentRSI);
        }

        return dropSize;
    }

    public void setCurrentRSI(double currentRSI) {

        this.currentRSI = currentRSI;
    }

    public double getCurrentRSI() {
        return currentRSI;
    }

    private void maIndicator() {

        if (priceArray.length > 10) {

            double[] prices5 = indicators.sma(priceArray, 5);
            double currentSMA5 = prices5[prices5.length -1 ];
            double[] prices10 = indicators.sma(priceArray, 10);
            double currentSMA10 = prices10[prices10.length -1 ];

            inPositionMA = currentSMA5 > currentSMA10;

            //System.out.println("SMA5 : " + currentSMA5 + " SMA20 : " + currentSMA10);
        }
    }



    private void setCurrentPrice(double price) {

        currentPrice = price;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    private CoinPriceModel cleanseData(CandlestickEvent response) {

        String dateTime = formatDateTime(response.getOpenTime());
        double price = formatPrice(response.getOpen());
        double percentageGain = calculatePercentage(price);

        return new CoinPriceModel("1M", "BTCUSDT", dateTime, price, percentageGain);
    }

    private String formatDateTime(Long openTime) {

        Date currentDateTime = new Date(openTime);
        DateFormat df = new SimpleDateFormat("dd/MM/yy-HH:mm:ss");

        return df.format(currentDateTime);
    }

    private double formatPrice(String price) {

        return Double.parseDouble(price);
    }

    public ResponseEntity<CoinPriceModel> createRecord(CoinPriceModel coinPriceModel) {

        mongoTemplate.save(coinPriceModel, "coinPrice");
        return new ResponseEntity<>(coinPriceModel, HttpStatus.CREATED);
    }

    private double calculatePercentage(double currentPrice) {

        return ((currentPrice / basePrice) - 1) * 100;
    }

    private void initiateSocketClient() {

        socketClient = BinanceApiClientFactory.newInstance().newWebSocketClient();
    }
}
