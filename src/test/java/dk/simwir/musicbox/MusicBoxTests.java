package dk.simwir.musicbox;

import dk.simwir.musicbox.action.ActionService;
import dk.simwir.musicbox.exceptions.JsonBodyException;
import dk.simwir.musicbox.exceptions.PlaybackException;
import dk.simwir.musicbox.exceptions.SocketReaderException;
import dk.simwir.musicbox.playback.PlaybackService;
import dk.simwir.musicbox.reader.Id;
import dk.simwir.musicbox.reader.IdReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MusicBoxTests {

    @Mock
    private IdReader mockIdReader;

    @Mock
    private ActionService mockActionService;

    @Mock
    private PlaybackService mockPlaybackService;

    @Mock
    private Id mockId;

    @InjectMocks
    private MusicBox musicBox;

    @ParameterizedTest
    @MethodSource("exceptionsProvider")
    void testUnhandledExceptions(Exception exception) throws InterruptedException, PlaybackException {
        when(mockIdReader.read()).thenReturn(mockId);
        when(mockActionService.getAction(any())).thenThrow(exception);

        Thread thread = new Thread(musicBox);
        thread.start();
        thread.join(1000);
        Assumptions.assumeFalse(thread.isAlive(), "Thread did not end in one second.");

        Assertions.assertNotNull(musicBox.getUncaughtException(), "No uncaught exceptions set, music box will not restart");
    }

    private static Stream<Exception> exceptionsProvider() {
        return Stream.of(new PlaybackException(), new JsonBodyException("", null, ""), new SocketReaderException(""));
    }
}
