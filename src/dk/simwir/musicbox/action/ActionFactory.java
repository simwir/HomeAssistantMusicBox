package dk.simwir.musicbox.action;

import dk.simwir.musicbox.logging.LogUtil;
import dk.simwir.musicbox.reader.Id;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ActionFactory {

    private ActionFactory() {}

    public static final String COMMA_DELIMITER = ",";
    private static final Logger logger = LogUtil.getLogger("action.ActionFactory");
    public static final String STOP = "Stop";

    public static ActionService getActionServiceFromFile(File file) throws IOException {
        logger.info(() -> String.format("Getting ActionService from file %s", file));
        Map<Id, Action> actions = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String finalLine = line;
                logger.finer(() -> String.format("Read line: %s", finalLine));
                String[] values = finalLine.split(COMMA_DELIMITER);
                Action action;
                if (values[2].equals(STOP)) {
                    action = new StopAction();
                } else {
                    action = new PlayAction(new Song(values[1], values[2]));
                }
                Id id = new Id(values[0]);
                logger.finer(() -> String.format("Adding key: %s, value: %s", id, action));
                actions.put(id, action);
            }
        }
        logger.info(() -> String.format("Read %d actions", actions.size()));
        return new ActionServiceImpl(actions);
    }
}
