package dk.simwir.musicbox.playback;

import dk.simwir.musicbox.action.Song;
import dk.simwir.musicbox.exceptions.HomeAssistantClientException;
import dk.simwir.musicbox.exceptions.HttpStatusException;
import dk.simwir.musicbox.exceptions.PlaybackException;
import dk.simwir.musicbox.homeassistant.HomeAssistantClient;
import dk.simwir.musicbox.logging.LogUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeAssistantPlaybackDevice implements PlaybackDevice {

    public static final String MEDIA_CONTENT_TYPE_KEY = "media_content_type";
    public static final String MEDIA_CONTENT_TYPE_VALUE = "music";
    public static final String ENQUEUE_KEY = "enqueue";
    public static final String ENQUEUE_VALUE = "replace";
    public static final String ATTRIBUTES = "attributes";
    public static final String MEDIA_TITLE = "media_title";
    public static final String STATE = "state";
    public static final String PLAYING_STATE = "playing";
    private final HomeAssistantClient homeAssistantClient;
    private final String entityId;
    public static final String PLAY_ACTION = "api/services/media_player/play_media";
    public static final String STOP_ACTION = "api/services/media_player/media_stop";
    public static final String STATE_ACTION = "api/states/%s";
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
        if (isAlreadyPlaying(song)) {
            logger.info(() -> String.format("Already playing %s", song));
            return;
        }
        JSONObject requestPayload = new JSONObject()
                .put(ENTITY_KEY, entityId)
                .put(MEDIA_CONTENT_ID_KEY, song.key())
                .put(MEDIA_CONTENT_TYPE_KEY, MEDIA_CONTENT_TYPE_VALUE)
                .put(ENQUEUE_KEY, ENQUEUE_VALUE);
        logger.info(() -> String.format("Starting playback of: %s using payload: %s", song.title(), requestPayload));
        post(PLAY_ACTION, requestPayload);
        if (logger.isLoggable(Level.FINER)) {
            Thread.sleep(5000);
            logger.finer(String.format("Now playing: %s", getSongTitle(getMediaPlayerState()).orElse("N/A")));
        }
    }

    private boolean isAlreadyPlaying(Song song) throws PlaybackException, InterruptedException {
        JSONObject mediaPlayerState = getMediaPlayerState();

        if (!getState(mediaPlayerState).map(x -> x.equals(PLAYING_STATE)).orElse(false)) {
            logger.info("MediaPlayer state is not Playing");
            logger.finer(() -> String.format("MediaPlayer state: %s", getState(mediaPlayerState).orElse("N/A")));
            return false;
        }

        return getSongTitle(mediaPlayerState).map(string -> string.equals(song.title())).orElse(false);
    }

    private Optional<String> getSongTitle(JSONObject mediaPlayerState) {
        return Optional.ofNullable(mediaPlayerState.getJSONObject(ATTRIBUTES).optString(MEDIA_TITLE, null));
    }

    private Optional<String> getState(JSONObject mediaPlayerState) {
        return Optional.ofNullable(mediaPlayerState.optString(STATE, null));
    }

    private JSONObject getMediaPlayerState() throws PlaybackException, InterruptedException {
        try {
            return homeAssistantClient.get(String.format(STATE_ACTION, entityId));
        } catch (HttpStatusException e) {
            throw new PlaybackException(String.format("Get to Home assistant failed with response code %d", e.getStatusCode()), e);
        } catch (HomeAssistantClientException | IOException e) {
            throw new PlaybackException(e);
        }
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
