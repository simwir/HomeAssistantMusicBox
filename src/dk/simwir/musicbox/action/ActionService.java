package dk.simwir.musicbox.action;

import dk.simwir.musicbox.exceptions.PlaybackException;
import dk.simwir.musicbox.reader.Id;

public interface ActionService {
    Action getAction(Id id) throws PlaybackException;
}
