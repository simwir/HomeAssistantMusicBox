package dk.simwir.musicbox.homeassistant;

import dk.simwir.musicbox.exceptions.HomeAssistantClientException;
import dk.simwir.musicbox.exceptions.HttpStatusException;
import dk.simwir.musicbox.exceptions.JsonBodyException;
import dk.simwir.musicbox.exceptions.NotImplementedException;
import dk.simwir.musicbox.logging.LogUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.logging.Logger;

public class HomeAssistantClientImpl implements HomeAssistantClient {

    private final HttpClient httpClient;
    private final URL baseUrl;
    private final String bearerToken;
    private final Duration timeout;
    private static final Logger logger = LogUtil.getLogger("homeassistant.HomeAssistantClientImpl");

    public HomeAssistantClientImpl(HttpClient httpClient, URL baseUrl, String bearerToken, Duration timeout) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
        this.bearerToken = bearerToken;
        this.timeout = timeout;
    }

    @Override
    public Optional<JSONObject> post(String action, JSONObject requestPayload) throws HomeAssistantClientException, HttpStatusException, IOException, InterruptedException {
        String requestString = requestPayload.toString();
        if (requestString == null) {
            logger.severe("JSONObject for request body is not valid json. Request not sent");
            throw new HomeAssistantClientException("JSONObject for request body is not valid json.");
        } else {
            logger.finer(() -> String.format("Posting with request body: %s", requestString));
        }
        HttpRequest httpRequest = getRequestBuilder(action)
                .POST(HttpRequest.BodyPublishers.ofString(requestString))
                .build();

        HttpResponse<String> httpResponse = send(httpRequest);
        String bodyString = httpResponse.body();
        Optional<JSONObject> responseBody;
        if (bodyString.equals("[]")) {
            responseBody = Optional.empty();
            logger.info("Post completed with empty response body.");
        } else {
            try {
                responseBody = Optional.of(new JSONObject(bodyString));
                logger.info(() -> String.format("Post completed with response body: %s",
                        responseBody.get().toString(2)));
            } catch (JSONException e) {
                throw new JsonBodyException(String.format("Response body not valid JSON. Body %s", bodyString), e, bodyString);
            }
        }
        return responseBody;
    }

    private HttpResponse<String> send(HttpRequest httpRequest) throws IOException, InterruptedException, HttpStatusException {
        logger.finer(() -> String.format("Sending request: %s", httpRequest.toString()));
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        int statusCode = httpResponse.statusCode();
        if (statusCode / 100 == 2) {
            logger.info(() -> String.format("Request successful with response code: %d", statusCode));
        } else {
            logger.warning(() -> String.format("Request failed with response code: %s", statusCode));
            throw new HttpStatusException(statusCode);
        }
        return httpResponse;
    }

    @Override
    public JSONObject get(String action) {
        throw new NotImplementedException();
    }

    private URI getUri(String action) {
        return URI.create(baseUrl + "/" + action);
    }

    private HttpRequest.Builder getRequestBuilder(String action) {
        return HttpRequest.newBuilder()
                .uri(getUri(action))
                .timeout(timeout)
                .header("Authorization", "Bearer " + bearerToken)
                .headers("Content-Type", "application/json");
    }
}
