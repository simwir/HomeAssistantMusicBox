import dk.simwir.musicbox.MusicBox;
import dk.simwir.musicbox.action.EccoActionService;
import dk.simwir.musicbox.playback.LoggerPlaybackDevice;
import dk.simwir.musicbox.playback.PlaybackServiceImpl;
import dk.simwir.musicbox.reader.StandardReader;

void main() {
    MusicBox musicBox = new MusicBox(
            new StandardReader(),
            new EccoActionService(),
            new PlaybackServiceImpl(new LoggerPlaybackDevice())
    );
    musicBox.run();
}
