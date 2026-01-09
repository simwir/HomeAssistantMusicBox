package dk.simwir.musicbox.action;

import dk.simwir.musicbox.exceptions.PlaybackException;
import dk.simwir.musicbox.logging.LogUtil;
import dk.simwir.musicbox.reader.Id;

import java.util.Map;
import java.util.logging.Logger;

public class ActionServiceImpl implements ActionService {

    private final Map<Id, Action> actions;
    private static final Logger logger = LogUtil.getLogger("action.ActionServiceImpl");

    public ActionServiceImpl(Map<Id, Action> actions) {
        this.actions = actions;
    }

    @Override
    public Action getAction(Id id) throws PlaybackException {
        Action action = actions.get(id);
        if (action == null) {
            throw new PlaybackException(String.format("No action matching Id %s", id));
        }
        logger.info(() -> String.format("Got action: %s from id: %s", action, id));
        return action;
    }
}
