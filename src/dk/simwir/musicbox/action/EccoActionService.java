package dk.simwir.musicbox.action;

import dk.simwir.musicbox.reader.Id;

public class EccoActionService implements ActionService {
    @Override
    public Action getAction(Id id) {
        if (id.id().equals("Stop")) {
            return new StopAction();
        } else {
            return new PlayAction(new Song(id.id(), id.id()));
        }
    }
}
