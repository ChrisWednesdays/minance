package com.uk.wednesday.minance.service;

import com.uk.wednesday.minance.model.CoinPriceModel;
import com.uk.wednesday.minance.model.OrderHistoryModel;
import com.uk.wednesday.minance.model.TestDataModel;
import com.uk.wednesday.minance.model.TradeVariableModel;
import com.uk.wednesday.minance.model.VariableStatsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class StatsService extends BaseService{

    MongoTemplate mongoTemplate;

    @Autowired
    public void StatService (MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostMapping("/orderHistory")
    public void analyseStats() {

        Date date2 = new Date();
        Date date = new Date(date2.getTime());
        DateFormat df = new SimpleDateFormat("dd/MM/yy");

        String collectionName = "variableResults-" + df.format(date);

        mongoTemplate.dropCollection(collectionName);

        List<TradeVariableModel> tradeVariableModelList = extractVariableList();

        for (TradeVariableModel tradeVariableModel : tradeVariableModelList) {

            String varId = tradeVariableModel.getVarId();
            String orderCollectionName = "orderHistory:"+varId;

            Query query = new Query();
            query.addCriteria(
                    new Criteria().andOperator(
                            Criteria.where("time").regex("^" + df.format(date))
                    )
            );

            List<OrderHistoryModel> orderHistoryList = mongoTemplate.find(query, OrderHistoryModel.class, orderCollectionName);

            double totalPercent = 0.0;

            for (OrderHistoryModel orderHistory : orderHistoryList) {

                totalPercent += orderHistory.getPercentGainLoss();
            }

            VariableStatsModel variableStatsModel = new VariableStatsModel(tradeVariableModel.getVarId(), totalPercent, orderHistoryList.size());
            mongoTemplate.save(variableStatsModel, collectionName);
        }

        List<CoinPriceModel> coinPriceList = mongoTemplate.findAll(CoinPriceModel.class, "coinPrice");
        CoinPriceModel coinPriceModel = coinPriceList.get(coinPriceList.size() - 1);

        mongoTemplate.save(coinPriceModel, "coinPriceDaily");

    }

    @PostMapping("/orderHistoryTotal")
    public void analyseTotalStats() throws IOException {

        double lastCoinPercent = 1.0;
        double priceChange = 0.0;

        File file = new File("orderHistoryMaster.txt");
        file.delete();

        List<TradeVariableModel> tradeVariableModelList = extractVariableList();

        for (TradeVariableModel tradeVariableModel : tradeVariableModelList) {

            String varId = tradeVariableModel.getVarId();
            String orderCollectionName = "testOrderHistory:"+varId;

            List<OrderHistoryModel> orderHistoryList = mongoTemplate.findAll(OrderHistoryModel.class, orderCollectionName);

            double totalPercent = 0.0;

            for (OrderHistoryModel orderHistory : orderHistoryList) {

                if (!varId.equals("0")) {
                    totalPercent = totalPercent + orderHistory.getPercentGainLoss();
                } else {
                    totalPercent = orderHistory.getPercentGainLoss();
                }
                    TestDataModel testDataModel
                        = new TestDataModel(orderHistory.getVarId(), orderHistory.getTime(), String.valueOf(totalPercent));
                    mongoTemplate.save(testDataModel, "orderHistoryMaster");

            }

            if (varId.equals("0")) {
                List<CoinPriceModel> coinPriceModels = mongoTemplate.findAll(CoinPriceModel.class, "coinPrice");
                totalPercent = coinPriceModels.get(coinPriceModels.size() - 1).getPercentChange();
            }

            VariableStatsModel variableStatsModel = new VariableStatsModel(tradeVariableModel.getVarId(), totalPercent, orderHistoryList.size());
            mongoTemplate.save(variableStatsModel, "orderHistoryTotals");
        }

        saveTotalsToCSV();
    }

    private void saveTotalsToCSV() throws IOException {

        FileWriter pw = new FileWriter("orderHistoryMaster.csv", true);

        List<TestDataModel> totalsMaster = mongoTemplate.findAll(TestDataModel.class, "orderHistoryMaster");

        pw.append("varId,dateTime,percentChange");
        pw.append("\n");
        for (TestDataModel record: totalsMaster) {

            pw.append(record.getVarId());
            pw.append(",");
            pw.append(record.getDate());
            pw.append(",");
            pw.append(String.valueOf(record.getPercent()));
            pw.append("\n");
            pw.flush();
        }
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderHistoryModel> createRecord(@RequestBody OrderHistoryModel orderHistoryModel) {

        String collectionName = getCollectionFormatted(orderHistoryModel.getVarId());

        mongoTemplate.save(orderHistoryModel, collectionName);
        return new ResponseEntity<>(orderHistoryModel, HttpStatus.CREATED);
    }

    private String getCollectionFormatted(String varId) {

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        return df.format(date) + "-" + "var" + varId;
    }
}
