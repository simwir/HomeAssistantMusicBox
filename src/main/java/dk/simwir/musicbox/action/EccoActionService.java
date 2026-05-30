package dk.simwir.musicbox.action;

import dk.simwir.musicbox.reader.Id;

import java.util.Optional;

public class EccoActionService implements ActionService {
    @Override
    public Optional<Action> getAction(Id id) {
        if (id.id().equals("Stop")) {
            return Optional.of(new StopAction());
        } else {
            return Optional.of(new PlayAction(new Song(id.id(), id.id())));
        }
    }
}
