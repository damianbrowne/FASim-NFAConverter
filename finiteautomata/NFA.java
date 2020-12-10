import java.util.*;

public class NFA extends FA {
    public boolean isAccepted;

    NFA (ArrayList<String> statesList, ArrayList<String> alphabetList, Map<String, String[][]> statesTransitions,
            String startState, ArrayList<String> acceptStates) {
        super(statesList, alphabetList, statesTransitions, startState, acceptStates);
        this.alphabet.add("lambda");
    }

    public boolean isValid() {
        if (!isValidStateFormat()) {
            System.out.println("ERR: Invalid state format. Use upper camel/Pascal case. Valid examples: A, Apple");
            return false;
        }
        for (String state : this.states) {
            String[][] transitions = transitionFunction.get(state);
            for (String[] transition : transitions) {
                if (!this.alphabet.contains(transition[0]) || !states.contains(transition[1])) {
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
        this.isAccepted = false;
        this.acceptStringHelper(this.start, query);
        return isAccepted;
    }

    private void acceptStringHelper(String curr, String queryRem) {
        if (queryRem.length() == 0) {
            this.isAccepted = this.accept.contains(curr);
            return;
        }
        String[][] transitions = this.transitionFunction.get(curr);
        for (String[] transition: transitions) {
            if (String.valueOf(queryRem.charAt(0)).equals(transition[0])) {
                this.acceptStringHelper(transition[1], queryRem.substring(1));
            }
            if (transition[0].equals("lambda")) {
                this.acceptStringHelper(transition[1], queryRem);
            }
        }
    }

    public DFA convertToDFA() {
        this.checkValidity();
        // Construct NFA state-transition table
        HashMap<String[], ArrayList<String>> tableNFA = this.stateTransitionTableMaker();
        return null;
    }
}