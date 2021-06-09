package com.uk.wednesday.minance.logic;

import com.uk.wednesday.minance.model.OrderHistoryModel;
import com.uk.wednesday.minance.model.TradeThresholdModel;
import com.uk.wednesday.minance.model.TradeVariableModel;
import com.uk.wednesday.minance.service.OrderHistoryService;
import com.uk.wednesday.minance.service.WebSocketService;
import quant.fans.Indicators;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DecisionEngine extends Thread {

    TradeVariableModel tradeVariableModel;
    WebSocketService webSocketService;
    OrderHistoryService orderHistoryService;

    private boolean coinOwned = false;
    private double basePrice;
    private double latestPrice;
    private int counter = 0;

    List<Double> rsiValues = new ArrayList<>();
    Indicators indicators = new Indicators();
    double[] priceArray = null;

    private double currentRSI = 0.0;
    private double buyOffer = 0.0;

    public DecisionEngine(TradeVariableModel model, WebSocketService webSocketService, OrderHistoryService orderHistoryService) {
        tradeVariableModel = model;
        this.webSocketService = webSocketService;
        this.orderHistoryService = orderHistoryService;
    }

    public void run()
    {
        try {
            // Displaying the thread that is running
            System.out.println(
                    "Thread " + Thread.currentThread().getId()
                            + " is running with var_id " + tradeVariableModel.getVarId());
            prepare();
            logic();
        }
        catch (Exception e) {
            // Throwing an exception
            System.out.println("Something went wrong!" + e);
        }
    }

    private void prepare() {
        //set base price and get latest price
        double price = getLatestPrice();
        System.out.println(price + "Current price for " + tradeVariableModel.getVarId());
    }

    private void newLogic() {

        double[] rsiArray = webSocketService.getRSIArray();

        //When rsi is within the threshold
        if (webSocketService.getCurrentRSI() < 30) {

//                    //Work out the drop into threshold. Bigger drop implies larger buy cut.
//                    if (isBetween(rsiDrop, 1.0, 1.2)) {
//                        buyOffer = currentPrice / 1.01;
//                    } else if (isBetween(rsiDrop, 1.2, 1.4)) {
//                        buyOffer = currentPrice / 1.015;
//                    } else if (isBetween(rsiDrop, 1.4, 5)) {
//                        buyOffer = currentPrice / 1.02;
//                    }


        }
    }



    private void logic() {

        if (basePrice == 0.0) {
            basePrice = getLatestPrice();
        }

        latestPrice = getLatestPrice();

        // Will always loop
        while (!coinOwned) {

            counterDetector();

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void counterDetector() {

        while (!coinOwned) {

            try
            {Thread.sleep(0);}
            catch (Exception e)
            {e.printStackTrace();}

            latestPrice = getLatestPrice();

            if (basePrice < latestPrice) {
                setBasePrice(latestPrice);
                counter++;

                int counterUpperLimit = 3;

                if (counter == counterUpperLimit) {
                    System.out.println("Purchase! at $" + basePrice + " using " + tradeVariableModel.getVarId());
                    try
                    {Thread.sleep(0);}
                    catch (Exception e)
                    {e.printStackTrace();}
                    buyTest(basePrice);
                    resetCounter();
                }

            } else if (basePrice > latestPrice) {
                setBasePrice(latestPrice);
                if (counter > 0) {
                    counter--;
                }
            }
        }
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void buyTest(double basePrice) {
        //Call buy api endpoint with buy order (probably need a model)
        // Return data via a responseEntity of the model

        coinOwned = true;
        TradeThresholdModel thresholdModel = new TradeThresholdModel(basePrice);

        // generate percentages passing in the purchasePrice
        generateLimits(tradeVariableModel, thresholdModel);
        sellDetectorTest(thresholdModel);
    }

    private void sellDetectorTest(TradeThresholdModel thresholdModel) {
        // Call sell api endpoint with sell order (probably need model)
        // Return data via a responseEntity of the model
        // Call

        Date date = new Date();
        boolean profitMade = false;

        OrderHistoryModel orderHistoryModel = null;
        double bestUpper = thresholdModel.getUpperSellLimit();

        while (coinOwned) {

            try
            {Thread.sleep(0);}
            catch (Exception e)
            {e.printStackTrace();}

            // Needed to work with the same value for upper and lower checks.
            latestPrice = getLatestPrice();

            double purchasePrice = thresholdModel.getPurchasePrice();
            double sellPrice = 0.0;

            if (bestUpper <= latestPrice) {

                bestUpper = latestPrice;
                continue;

            } else if ((thresholdModel.getUpperSellLimit() < bestUpper) ||
                    (latestPrice < thresholdModel.getLowerSellLimit())) {

                if (thresholdModel.getUpperSellLimit() < bestUpper) {
                    sellPrice = bestUpper;
                } else {
                    sellPrice = thresholdModel.getLowerSellLimit();
                }

                // Call sell function

                if (purchasePrice < sellPrice) {
                    profitMade = true;
                }

                double fees = generateFees(purchasePrice, bestUpper);

                double percentGain = ((sellPrice - fees) / (purchasePrice) - 1) * 100;
                percentGain = (double) Math.round(percentGain*100) / 100;

//                orderHistoryModel = new OrderHistoryModel(
//                        tradeVariableModel.getVarId(), date.getTime(), purchasePrice, sellPrice, percentGain);
                coinOwned = false;
            }

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // write to OrderHistoryModel
        assert orderHistoryModel != null;
        report(orderHistoryModel);
    }

    private void report(OrderHistoryModel orderHistoryModel) {

        orderHistoryService.createRecord(orderHistoryModel);

        System.out.println(orderHistoryModel.toString());

    }

    private void generateLimits(TradeVariableModel tradeVariableModel, TradeThresholdModel thresholdModel) {

        //double lowerSellPrice= thresholdModel.getPurchasePrice() * tradeVariableModel.getLowerLimit();
        //double upperSellPrice = thresholdModel.getPurchasePrice() * tradeVariableModel.getUpperLimit();

        //thresholdModel.setUpperSellLimit(upperSellPrice);
        //thresholdModel.setLowerSellLimit(lowerSellPrice);

        //System.out.println("Upper: $" + upperSellPrice + "    " + "Lower: $" + lowerSellPrice);
    }

    private double generateFees(double purchasePrice, double bestUpper) {

        double purchaseFee = ((purchasePrice * 1.00075) - purchasePrice);
        double sellFee = ((bestUpper * 1.00075) - bestUpper);

        return sellFee + purchaseFee;
    }

    private double getLatestPrice() {
        return webSocketService.getCurrentPrice();
    }

    private void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    private void setLatestPrice(double latestPrice) {
        this.latestPrice = latestPrice;
    }

    private void resetCounter() {
        counter = 0;
    }
}
