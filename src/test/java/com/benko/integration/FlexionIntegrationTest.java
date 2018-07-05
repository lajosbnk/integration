package com.benko.integration;

import com.benko.integration.config.ApiProperties;
import com.flexionmobile.codingchallenge.integration.Purchase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class FlexionIntegrationTest {

    private static ClientAndServer mockServer;

    @Autowired
    private FlexionIntegration integration;

    @Autowired
    private ApiProperties props;

    @BeforeClass
    public static void startServer() {
        mockServer = ClientAndServer.startClientAndServer(9999);
    }

    @Test
    public void whenPurchaseIsSuccessful_returnsUnconsumedPurchaseWithPurchaseId() {
        mockServer.when(request()
                .withMethod("POST")
                .withPath(buyPath(props.getDeveloperId(), "item1")), exactly(1))
                .respond(response()
                        .withStatusCode(200)
                        .withBody(purchaseJson("abcdef", "item1")));

        Purchase purchase = integration.buy("item1");

        assertThat(purchase).isNotNull();
        assertThat(purchase.getConsumed()).isEqualTo(false);
        assertThat(purchase.getId()).isEqualTo("abcdef");
        assertThat(purchase.getItemId()).isEqualTo("item1");
    }

    @Test
    public void whenPurchaseIsNotSuccessful_returnsNullPurchase() {
        mockServer.when(request()
                .withMethod("POST")
                .withPath(buyPath(props.getDeveloperId(), "item1")), exactly(1))
                .respond(response()
                        .withStatusCode(404));

        Purchase purchase = integration.buy("item1");

        assertThat(purchase).isNull();
    }

    @Test
    public void whenCallToGetPurchasesEndpointIsSuccessful_returnsListOfPurchases() {
        mockServer.when(request()
                .withMethod("GET")
                .withPath(getPurchasesPath(props.getDeveloperId())), exactly(1))
                .respond(response()
                        .withStatusCode(200)
                        .withBody(purchasesJson()));

        List<Purchase> purchases = integration.getPurchases();

        assertThat(purchases).hasSize(2);
    }

    @Test
    public void whenCallToGetPurchasesEndpointIsNotSuccessful_returnsEmptyList() {
        mockServer.when(request()
                .withMethod("GET")
                .withPath(getPurchasesPath(props.getDeveloperId())), exactly(1))
                .respond(response()
                        .withStatusCode(404));

        List<Purchase> purchases = integration.getPurchases();

        assertThat(purchases).hasSize(0);
    }

    @Test
    public void callsConsumeEndpointExactlyOnce() {
        mockServer.when(request()
                .withMethod("POST")
                .withPath(consumePath(props.getDeveloperId(), "abcdef")), exactly(1))
                .respond(response()
                        .withStatusCode(200));

        integration.consume(purchase(true, "abcdef", "item1"));

        mockServer.verify(request().withPath(consumePath(props.getDeveloperId(), "abcdef")), VerificationTimes.once());
    }

    private Purchase purchase(boolean consumed, String id, String itemId) {
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

    private String purchaseJson(String purchaseId, String itemId) {
        return String.format("{\"consumed\":false,\"id\":\"%s\",\"itemId\":\"%s\"}", purchaseId, itemId);
    }

    private String purchasesJson() {
        return String.format("{\"purchases\": [%s,%s]}",
                purchaseJson("abc", "item1"),
                purchaseJson("def", "item2"));
    }

    private String buyPath(String developerId, String itemId) {
        return String.format("/javachallenge/rest/developer/%s/buy/%s", developerId, itemId);
    }

    private String getPurchasesPath(String developerId) {
        return String.format("/javachallenge/rest/developer/%s/all", developerId);
    }

    private String consumePath(String developerId, String purchaseId) {
        return String.format("/javachallenge/rest/developer/%s/consume/%s", developerId, purchaseId);
    }

    @AfterClass
    public static void stopServer() {
        mockServer.stop();
    }
}