package tg.state;

import org.javatuples.Pair;

import java.util.HashMap;

public class Transition {
    private final HashMap<Pair<Condition, String>, Pair<Condition, String>> trans = new HashMap<>();

    public Transition() {
        trans.put(new Pair<>(Condition.BEGIN, Alphabet.START), new Pair<>(Condition.HALL, Answer.Intro));

        trans.put(new Pair<>(Condition.HALL, Alphabet.HELP), new Pair<>(Condition.HALL, Answer.Help));
        trans.put(new Pair<>(Condition.HALL, Alphabet.ROOMS), new Pair<>(Condition.ROOMS, Answer.RoomsMock));

        trans.put(new Pair<>(Condition.ROOMS, Alphabet.CANCEL), new Pair<>(Condition.HALL, Answer.Intro));
        trans.put(new Pair<>(Condition.ROOMS, Alphabet.GOTO), new Pair<>(Condition.FILES, Answer.GoToMsg));

        trans.put(new Pair<>(Condition.FILES, Alphabet.CANCEL), new Pair<>(Condition.HALL, Answer.Intro));

        trans.put(new Pair<>(Condition.FILES_GET, Alphabet.CANCEL), new Pair<>(Condition.FILES, Answer.GoToMsg));
        trans.put(new Pair<>(Condition.FILES_SEND, Alphabet.CANCEL), new Pair<>(Condition.FILES, Answer.GoToMsg));
    }

    public HashMap<Pair<Condition, String>, Pair<Condition, String>> getTransitions() {
        return trans;
    }
}
