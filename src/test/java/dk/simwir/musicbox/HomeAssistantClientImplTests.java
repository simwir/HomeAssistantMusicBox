package dk.simwir.musicbox;

import dk.simwir.musicbox.exceptions.HomeAssistantClientException;
import dk.simwir.musicbox.exceptions.HttpStatusException;
import dk.simwir.musicbox.homeassistant.HomeAssistantClientImpl;
import dk.simwir.musicbox.playback.HomeAssistantPlaybackDevice;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HomeAssistantClientImplTests {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    private HomeAssistantClientImpl homeAssistantClient;

    @BeforeEach
    void setUp() throws URISyntaxException, MalformedURLException {
        homeAssistantClient = new HomeAssistantClientImpl(mockHttpClient, new URI("https://example.com").toURL(),
                null, Duration.ofSeconds(10));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "[{}]",
            "[{\"entity_id\":\"media_player\",\"state\":\"paused\",\"attributes\":{\"group_members\":[\"media_player.a\",\"media_player.b\"],\"volume_level\":0.08,\"is_volume_muted\":false,\"media_content_id\":\"isd\",\"media_content_type\":\"music\",\"media_duration\":191,\"media_position\":0,\"media_position_updated_at\":\"2026-06-07T11:58:44.915859+00:00\",\"media_title\":\"A\",\"media_artist\":\"B\",\"media_album_name\":\"C\",\"shuffle\":false,\"repeat\":\"off\",\"queue_position\":1,\"queue_size\":1,\"device_class\":\"speaker\",\"entity_picture\":\"D\",\"friendly_name\":\"E\",\"supported_features\":F},\"last_changed\":\"G\",\"last_reported\":\"2026-06-07T11:58:44.916694+00:00\",\"last_updated\":\"2026-06-07T11:58:44.916694+00:00\",\"context\":{\"id\":\"G\",\"parent_id\":null,\"user_id\":\"H\"}}]",
            "{\"entity_id\":\"media_player\",\"state\":\"paused\",\"attributes\":{\"group_members\":[\"media_player.a\",\"media_player.b\"],\"volume_level\":0.08,\"is_volume_muted\":false,\"media_content_id\":\"isd\",\"media_content_type\":\"music\",\"media_duration\":191,\"media_position\":0,\"media_position_updated_at\":\"2026-06-07T11:58:44.915859+00:00\",\"media_title\":\"A\",\"media_artist\":\"B\",\"media_album_name\":\"C\",\"shuffle\":false,\"repeat\":\"off\",\"queue_position\":1,\"queue_size\":1,\"device_class\":\"speaker\",\"entity_picture\":\"D\",\"friendly_name\":\"E\",\"supported_features\":F},\"last_changed\":\"G\",\"last_reported\":\"2026-06-07T11:58:44.916694+00:00\",\"last_updated\":\"2026-06-07T11:58:44.916694+00:00\",\"context\":{\"id\":\"G\",\"parent_id\":null,\"user_id\":\"H\"}}"
    })
    public void testJsonLineHAResponse(String jsonBody) throws IOException, InterruptedException, HomeAssistantClientException, HttpStatusException {
        when(mockHttpClient.send(any(), Mockito.eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonBody);

        Assertions.assertDoesNotThrow(() ->
                homeAssistantClient.post(HomeAssistantPlaybackDevice.PLAY_ACTION, new JSONObject())
        );
    }
}
