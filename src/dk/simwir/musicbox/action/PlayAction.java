package dk.simwir.musicbox.action;

public class PlayAction extends Action {
    private final Song song;

    public PlayAction(Song song) {
        super();
        this.song = song;
    }

    public Song getSong() {
        return song;
    }
}
