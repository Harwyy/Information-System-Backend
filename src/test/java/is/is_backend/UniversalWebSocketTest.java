package is.is_backend;

import static is.is_backend.utils.FileReader.readJsonFile;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UniversalWebSocketTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String topic;
    private String apiEndpoint;
    private String requestJson;
    private String testName;

    private void configureForOrganization() {
        this.topic = "/topic/all";
        this.apiEndpoint = "/api/organization";
        this.requestJson = readJsonFile("requests/create-organization-request.json");
        this.testName = "Organization";
    }

    private void configureForLocation() {
        this.topic = "/topic/all";
        this.apiEndpoint = "/api/location";
        this.requestJson = readJsonFile("requests/create-location-request.json");
        this.testName = "Location";
    }

    private void configureForAddress() {
        this.topic = "/topic/all";
        this.apiEndpoint = "/api/address";
        this.requestJson = readJsonFile("requests/create-address-request.json");
        this.testName = "Address";
    }

    private void configureForCoordinates() {
        this.topic = "/topic/all";
        this.apiEndpoint = "/api/coordinates";
        this.requestJson = readJsonFile("requests/create-coordinates-request.json");
        this.testName = "Coordinates";
    }

    private String getWebSocketUri() {
        return "ws://localhost:" + port + "/ws";
    }

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    private void executeUniversalWebSocketTest() throws Exception {
        WebSocketStompClient stompClient = new WebSocketStompClient(
                new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));

        stompClient.setMessageConverter(new StringMessageConverter());

        CountDownLatch connectionLatch = new CountDownLatch(1);
        CountDownLatch messageLatch = new CountDownLatch(1);
        AtomicReference<String> receivedMessage = new AtomicReference<>();
        AtomicReference<Exception> executionError = new AtomicReference<>();

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                System.out.println("✅ Connected to WebSocket!");
                connectionLatch.countDown();

                session.subscribe(topic, new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        receivedMessage.set((String) payload);
                        messageLatch.countDown();
                    }
                });

                System.out.println("✅ Subscribed to " + topic);
                System.out.println("🔄 Automatically sending POST request...");
                boolean requestSent = sendCreateRequest();

                if (!requestSent) {
                    String errorMessage = String.format(
                            "%s Test FAILED: Failed to send POST request to %s. Check if: 1) Endpoint exists, 2) JSON is valid",
                            testName, apiEndpoint);
                    executionError.set(new RuntimeException(errorMessage));
                    messageLatch.countDown();
                }
            }

            @Override
            public void handleException(
                    StompSession session,
                    StompCommand command,
                    StompHeaders headers,
                    byte[] payload,
                    Throwable exception) {
                System.out.println("❌ Exception: " + exception.getMessage());
                executionError.set(new RuntimeException("STOMP Exception: " + exception.getMessage()));
                messageLatch.countDown();
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                if (exception.getMessage().contains("Connection closed")
                        || exception.getMessage().contains("close")) {
                    System.out.println("Connection closed normally");
                } else {
                    System.out.println("Transport error: " + exception.getMessage());
                    executionError.set(new RuntimeException("Transport error: " + exception.getMessage()));
                    messageLatch.countDown();
                }
            }
        };

        try {
            System.out.println("🔄 Connecting to: " + getWebSocketUri());
            StompSession session =
                    stompClient.connectAsync(getWebSocketUri(), sessionHandler).get(10, TimeUnit.SECONDS);

            assertTrue(
                    connectionLatch.await(10, TimeUnit.SECONDS), "Connection should be established within 10 seconds");

            boolean messageReceived = messageLatch.await(30, TimeUnit.SECONDS);

            if (executionError.get() != null) {
                throw executionError.get();
            }

            assertTrue(messageReceived, testName + " Test FAILED: No message received within 30 seconds");
            assertNotNull(receivedMessage.get(), "Received message should not be null");

            System.out.println("✅ " + testName + " Test PASSED! Received message: " + receivedMessage.get());

        } finally {
            stompClient.stop();
        }
    }

    private boolean sendCreateRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + apiEndpoint, request, String.class);

        boolean success = response.getStatusCode().equals(HttpStatus.OK);
        System.out.println("✅ " + testName + " POST request sent. Status: " + response.getStatusCode());

        return success;
    }

    @Test
    @DisplayName("Test WebSocket for Organization")
    public void testOrganizationWebSocket() throws Exception {
        configureForOrganization();
        executeUniversalWebSocketTest();
    }

    @Test
    @DisplayName("Test WebSocket for Location")
    public void testLocationWebSocket() throws Exception {
        configureForLocation();
        executeUniversalWebSocketTest();
    }

    @Test
    @DisplayName("Test WebSocket for Address")
    public void testAddressWebSocket() throws Exception {
        configureForAddress();
        executeUniversalWebSocketTest();
    }

    @Test
    @DisplayName("Test WebSocket for Coordinates")
    public void testCoordinatesWebSocket() throws Exception {
        configureForCoordinates();
        executeUniversalWebSocketTest();
    }
}
