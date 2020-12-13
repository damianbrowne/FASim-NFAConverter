package finiteautomata;
import java.util.List;
import java.util.Map;

public class DFA extends FA {
    
    public DFA (List<String> alphabetList, Map<String, String[][]> statesTransitions,
            String startState, List<String> acceptStates) {
        super(alphabetList, statesTransitions, startState, acceptStates);
    }

    public boolean isValid() {
        if (!isValidStateFormat()) {
            System.out.println("ERR: Invalid state format. Use upper camel/Pascal case. Valid examples: A, Apple");
            return false;
        }
        if (transitionFunction.size() != this.states.size()) {
            return false;
        }
        for (String state : this.states) {
            String[][] transitions = transitionFunction.get(state);
            for (String[] transition : transitions) {
                if (transitions.length != this.alphabet.size() || !this.alphabet.contains(transition[0])
                        || !states.contains(transition[1])) {
                    return false;
                }
            }
        }
        return true;
    }

    private void checkValidity() {
        if (!this.isValid()) {
            throw new IllegalArgumentException("The Finite Automata you entered is invalid. Try again.");
        }
    }
    
    public boolean isAcceptedString(String query) {
        this.checkValidity();
        if (query == "empty") { return this.isEmpty(); }
        else if (query == "lambda") { return this.accept.contains(this.start); }
        else if (this.isEmpty()) { return false; }
        return this.acceptStringHelper(this.start, query);
    }

    public boolean acceptStringHelper(String curr, String queryRem) {
        if (queryRem.length() == 0) { return this.accept.contains(curr); }
        for (String[] transition : this.transitionFunction.get(curr)) {
            if (String.valueOf(queryRem.charAt(0)).equals(transition[0])) {
                return this.acceptStringHelper(transition[1], queryRem.substring(1));
            }
        }
        return false;
    }
}
