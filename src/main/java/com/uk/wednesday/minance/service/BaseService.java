package com.uk.wednesday.minance.service;

import com.uk.wednesday.minance.logic.DecisionEngine;
import com.uk.wednesday.minance.model.TradeVariableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;

@Service
public class BaseService {

    private static final String COMMA_DELIMITER = ",";
    DecisionEngine decisionEngine;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    OrderHistoryService orderHistoryService;

    @Autowired
    MongoTemplate mongoTemplate;

    public void start() throws InterruptedException {

        webSocketService = new WebSocketService(mongoTemplate);
        orderHistoryService = new OrderHistoryService(mongoTemplate);

        System.out.println("Preparing, please wait...");
        //wait for ping to get results
        sleep(5000);

        System.out.println("Current price: " + webSocketService.getCurrentPrice());

        System.out.println("Bot Ready....");

        // Extract csv
        List<TradeVariableModel> variableList= extractVariableList();
        //loop each and add to Constructor.
        for (TradeVariableModel tradeVariableModel : variableList) {

            decisionEngine = new DecisionEngine(tradeVariableModel, webSocketService, orderHistoryService);
            decisionEngine.start();
        }
    }

    List<TradeVariableModel> extractVariableList() {

        List<TradeVariableModel> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("tradeVariables.csv"))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                List<String> stringValues = Arrays.asList(values);

                TradeVariableModel model = new TradeVariableModel();
                model.setVarId(stringValues.get(0));
                records.add(model);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return records;
    }
}
