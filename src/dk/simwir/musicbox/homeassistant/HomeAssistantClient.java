package dk.simwir.musicbox.homeassistant;

import dk.simwir.musicbox.exceptions.HomeAssistantClientException;
import dk.simwir.musicbox.exceptions.HttpStatusException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Optional;

public interface HomeAssistantClient {
    Optional<JSONObject> post(String action, JSONObject requestPayload) throws HomeAssistantClientException, HttpStatusException, IOException, InterruptedException;
    JSONObject get(String action);
}
