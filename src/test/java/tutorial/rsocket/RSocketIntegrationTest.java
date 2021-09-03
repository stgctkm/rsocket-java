package tutorial.rsocket;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import tutorial.rsocket.client.ChannelClient;
import tutorial.rsocket.client.FireAndForgetClient;
import tutorial.rsocket.client.RequestResponseClient;
import tutorial.rsocket.client.RequestStreamClient;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RSocketIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(RSocketIntegrationTest.class);

    private static Server server;

    @BeforeAll
    public static void setUpClass() {
        server = new Server();
    }

    @AfterAll
    public static void tearDownClass() {
        server.dispose();
    }

    @Test
    public void requestResponse() {
        RequestResponseClient client = new RequestResponseClient();
        String value = "Hello RSocket";

        assertEquals(value, client.callBlocking(value));

        client.dispose();
    }


    @Test
    public void requestStream() {
        FireAndForgetClient fireAndForgetClient = new FireAndForgetClient();
        RequestStreamClient requestStreamClient = new RequestStreamClient();

        List<Float> data = fireAndForgetClient.getData();
        List<Float> dataReceived = new ArrayList<>();

        Disposable subscription = requestStreamClient.getDataStream()
                .index()
                .subscribe(
                        tuple -> {
                            assertEquals(data.get(tuple.getT1().intValue()), tuple.getT2());
                            dataReceived.add(tuple.getT2());
                        },
                        error -> System.out.println(error.getMessage())
                );

        fireAndForgetClient.sendData();

        // wait a short time for the data to complete then dispose everything
        try {
            Thread.sleep(500);
        } catch (Exception x) {
        }
        subscription.dispose();
        fireAndForgetClient.dispose();

        assertEquals(data.size(), dataReceived.size());
    }

    @Test
    public void channel() {
        ChannelClient client = new ChannelClient();
        client.playGame();
        client.dispose();
    }
}
