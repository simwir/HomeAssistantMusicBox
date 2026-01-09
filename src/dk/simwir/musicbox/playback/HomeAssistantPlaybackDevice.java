package dk.simwir.musicbox.playback;

import dk.simwir.musicbox.action.Song;
import dk.simwir.musicbox.exceptions.HomeAssistantClientException;
import dk.simwir.musicbox.exceptions.HttpStatusException;
import dk.simwir.musicbox.exceptions.PlaybackException;
import dk.simwir.musicbox.homeassistant.HomeAssistantClient;
import dk.simwir.musicbox.logging.LogUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeAssistantPlaybackDevice implements PlaybackDevice {

    public static final String MEDIA_CONTENT_TYPE_KEY = "media_content_type";
    public static final String MEDIA_CONTENT_TYPE_VALUE = "music";
    public static final String ENQUEUE_KEY = "enqueue";
    public static final String ENQUEUE_VALUE = "replace";
    private final HomeAssistantClient homeAssistantClient;
    private final String entityId;
    public static final String PLAY_ACTION = "api/services/media_player/play_media";
    public static final String STOP_ACTION = "api/services/media_player/media_stop";
    public static final String ENTITY_KEY = "entity_id";
    public static final String MEDIA_CONTENT_ID_KEY = "media_content_id";
    private static final Logger logger = LogUtil.getLogger("playback.HomeAssistantPlaybackDevice");

    public HomeAssistantPlaybackDevice(HomeAssistantClient homeAssistantClient, String entityId) {
        this.homeAssistantClient = homeAssistantClient;
        this.entityId = entityId;
    }


    @Override
    public void stop() throws PlaybackException, InterruptedException {
        JSONObject requestPayload = new JSONObject()
                .put(ENTITY_KEY, entityId);
        logger.info(() -> String.format("Stopping playback with payload: %s", requestPayload));
        post(STOP_ACTION, requestPayload);
    }

    @Override
    public void play(Song song) throws PlaybackException, InterruptedException {
        JSONObject requestPayload = new JSONObject()
                .put(ENTITY_KEY, entityId)
                .put(MEDIA_CONTENT_ID_KEY, song.key())
                .put(MEDIA_CONTENT_TYPE_KEY, MEDIA_CONTENT_TYPE_VALUE)
                .put(ENQUEUE_KEY, ENQUEUE_VALUE);
        logger.info(() -> String.format("Starting playback of: %s using payload: %s", song.title(), requestPayload));
        post(PLAY_ACTION, requestPayload);
    }

    private void post(String action, JSONObject requestPayload) throws PlaybackException, InterruptedException {
        try {
            homeAssistantClient.post(action, requestPayload);
        } catch (HttpStatusException e) {
            logger.log(Level.WARNING, e, () -> String.format("Post to Home assistant failed with response code %d", e.getStatusCode()) );
        } catch (HomeAssistantClientException | IOException e) {
            throw new PlaybackException(e);
        }
    }
}
