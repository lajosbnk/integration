package com.benko.integration;

import com.benko.integration.config.ApiProperties;
import com.flexionmobile.codingchallenge.integration.Integration;
import com.flexionmobile.codingchallenge.integration.Purchase;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class FlexionIntegration implements Integration {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlexionIntegration.class);
    private static final int OK = 200;

    private final ApiProperties props;
    private final String developerId;

    @Autowired
    public FlexionIntegration(ApiProperties apiProperties) {
        props = apiProperties;
        developerId = props.getDeveloperId();

        Unirest.setTimeouts(props.getTimeoutMillis(), props.getTimeoutMillis());
    }

    @Override
    public Purchase buy(String itemId) {
        LOGGER.info("Calling buy endpoint with developerId: [{}], itemId: [{}]", developerId, itemId);

        try {
            HttpResponse<JsonNode> response =
                    Unirest.post(props.getBaseUrl().toString() + props.getBuyEndpoint())
                            .routeParam("developerId", developerId)
                            .routeParam("itemId", itemId)
                            .asJson();

            if (response.getStatus() == OK) {
                Purchase purchase = parsePurchaseJson(response.getBody().getObject());
                LOGGER.info("Buy request with developerId: [{}], itemId: [{}] successful, purchase id: [{}]", developerId, itemId, purchase.getId());

                return purchase;
            } else {
                LOGGER.info("Buy request with developerId: [{}], itemId: [{}] failed: {} {}", developerId, itemId, response.getStatusText(), response.getStatusText());
            }
        } catch (Exception e) {
            LOGGER.error("Error calling buy endpoint with developerId: [{}], itemId: [{}]: {}", developerId, itemId, e.getMessage());
        }

        return null;
    }

    @Override
    public List<Purchase> getPurchases() {
        LOGGER.info("Calling getPurchases endpoint with developerId: [{}]", developerId);

        try {
            HttpResponse<JsonNode> response = Unirest.get(props.getBaseUrl().toString() + props.getAllEndpoint())
                    .routeParam("developerId", developerId)
                    .asJson();

            if (response.getStatus() == OK) {
                List<Purchase> purchases = parsePurchaseListJson(response.getBody().getObject().getJSONArray("purchases"));
                LOGGER.info("Get purchases request with developerId: [{}] successful, retrieved [{}] purchases", developerId, purchases.size());

                return purchases;
            } else {
                LOGGER.info("Get purchases request with developerId: [{}] failed: {} {}", developerId, response.getStatus(), response.getStatusText());
            }
        } catch (Exception e) {
            LOGGER.error("Error calling getPurchases endpoint with developerId: [{}]: {}", developerId, e.getMessage());
        }

        return Collections.emptyList();
    }

    @Override
    public void consume(Purchase purchase) {
        LOGGER.info("Calling consume endpoint with purchase id: [{}]", purchase.getId());

        try {
            HttpResponse<String> response = Unirest.post(props.getBaseUrl().toString() + props.getConsumeEndpoint())
                    .routeParam("developerId", developerId)
                    .routeParam("purchaseId", purchase.getId()).asString();

            if (response.getStatus() == OK) {
                LOGGER.info("Consume request with purchaseId: [{}] successful", purchase.getId());
            } else {
                LOGGER.info("Consume request with purchaseId: [{}] failed: {} {}", purchase.getId(), response.getStatus(), response.getStatusText());
            }
        } catch (Exception e) {
            LOGGER.error("Error calling consume endpoint with developerId: [{}]: {}", developerId, e.getMessage());
        }
    }

    private List<Purchase> parsePurchaseListJson(JSONArray purchases) {
        return StreamSupport.stream(purchases.spliterator(), false)
                .map(obj -> (JSONObject) obj)
                .map(this::parsePurchaseJson)
                .collect(Collectors.toList());
    }

    private Purchase parsePurchaseJson(JSONObject json) {
        boolean consumed = json.getBoolean("consumed");
        String id = json.getString("id");
        String itemId = json.getString("itemId");

        return new Purchase() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public boolean getConsumed() {
                return consumed;
            }

            @Override
            public String getItemId() {
                return itemId;
            }
        };
    }
}
