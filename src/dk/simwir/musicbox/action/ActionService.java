package dk.simwir.musicbox.action;

import dk.simwir.musicbox.exceptions.PlaybackException;
import dk.simwir.musicbox.reader.Id;

import java.util.Optional;

public interface ActionService {
    Optional<Action> getAction(Id id) throws PlaybackException;
}
