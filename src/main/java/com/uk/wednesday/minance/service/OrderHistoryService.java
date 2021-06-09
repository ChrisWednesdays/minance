package com.uk.wednesday.minance.service;

import com.uk.wednesday.minance.model.OrderHistoryModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class OrderHistoryService {

    MongoTemplate mongoTemplate;

    @Autowired
    public OrderHistoryService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderHistoryModel> createRecord(@RequestBody OrderHistoryModel orderHistoryModel) {

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        String collectionName = df.format(date) + "-" + "var" + orderHistoryModel.getVarId();

        mongoTemplate.save(orderHistoryModel, collectionName);
        return new ResponseEntity<>(orderHistoryModel, HttpStatus.CREATED);
    }
}
