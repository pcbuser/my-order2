package com.example.demo;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;


import javax.persistence.*;

@Entity
@Table(name="ORDER_TABLE")

public class Order {
    @Id @GeneratedValue
    long id;
    int qty;
    long productId;
    String productName;

    @PostPersist
    public void eventPublish(){
        OrderPlaced OrderPlaced = new OrderPlaced();
        OrderPlaced.setOrderId(this.getId());
        OrderPlaced.setProductId(this.getProductId());
        OrderPlaced.setProductName(this.getProductName());
        OrderPlaced.setQty(this.getQty());

        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;

        try{
            json = objectMapper.writeValueAsString(OrderPlaced);
         } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON format exeception", e);
        }

        Processor processor = DemoApplication.applicationContext.getBean(Processor.class);
        MessageChannel outputChannel = processor.output();

        outputChannel.send(MessageBuilder
                .withPayload(json)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
