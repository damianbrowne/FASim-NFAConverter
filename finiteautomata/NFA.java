import java.util.*;

public class NFA extends FA {
    public boolean isAccepted;

    NFA (ArrayList<String> statesList, ArrayList<String> alphabetList, Map<String, String[][]> statesTransitions,
            String startState, ArrayList<String> acceptStates) {
        super(statesList, alphabetList, statesTransitions, startState, acceptStates);
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
        HashMap<ArrayList<String>, ArrayList<String>> dfaTable = new HashMap<ArrayList<String>, ArrayList<String>>();
        dfaTableMaker(dfaTable, this.getLambdaStatesAsString(start));
        return null;
    }

    public String getLambdaStatesAsString(String stateName) {
        return arrListToString(getLambdaStates(stateName));
    }

    private ArrayList<String> getLambdaStates(String stateName) {
        return this.table.get(new ArrayList<String>(Arrays.asList(stateName, "lambda")));
    }

    private String arrListToString(ArrayList<String> arrList) {
        return String.join("", arrList);
    }

    private ArrayList<String> arrListToStringInArrList(ArrayList<String> arrList) {
        return new ArrayList<String>(Arrays.asList((arrListToString(arrList))));
    }

    private boolean finishedDfaTable(HashMap<ArrayList<String>, ArrayList<String>> dfaTable, String newRowQuery) {
        ArrayList<String> queryKey = new ArrayList<String>(Arrays.asList(newRowQuery, this.alphabet.get(0)));
        return dfaTable.size() > 0 && dfaTable.get(queryKey) != null;
    }

    private ArrayList<String> getTransitions(ArrayList<String> states, String symbol) {
        ArrayList<String> transitions = new ArrayList<String>();
        for (String state : states) {
            ArrayList<String> keyPerRowState = new ArrayList<String>(Arrays.asList(state, symbol));
            for (String transition : this.table.get(keyPerRowState)) {
                ArrayList<String> lambdaMoves = this.table.get(Arrays.asList(transition, "lambda"));
                for (String lambdaMove: lambdaMoves) {
                    if (!transitions.contains(lambdaMove)) {
                        transitions.add(lambdaMove);
                    }
                }
            }
        }
        if (transitions.size() == 0 ) {
            transitions.add("lambda");
        }
        return transitions;
    }

    public static ArrayList<String> stringToArrList(String s) {
        ArrayList<String> res = new ArrayList<String>();
        for (int i = 0; i < s.length(); i++) {
            Character ch = s.charAt(i);
            if (Character.isUpperCase(ch)) {
                res.add(s.substring(i, i+1));
            } else {
                res.get(res.size() - 1).concat((s.substring(i, i+1)));
            }
        }
        return res;
    }

    public HashMap<ArrayList<String>, ArrayList<String>> dfaTableMaker(HashMap<ArrayList<String>, ArrayList<String>> dfaTable, String rowStateName) {
        ArrayList<String> rowStates = stringToArrList(rowStateName);
        if (finishedDfaTable(dfaTable, rowStateName)) {
            return dfaTable;
        }
        ArrayList<String> newStateNames = new ArrayList<String>();
        for (String symbol: this.alphabet) {
            if (!symbol.equals("lambda")) {
                ArrayList<String> dfaKey = new ArrayList<String>(Arrays.asList(rowStateName, symbol));
                ArrayList<String> dfaTransition = arrListToStringInArrList(getTransitions(rowStates, symbol));
                dfaTable.put(dfaKey, dfaTransition);
                ArrayList<String> explored = new ArrayList<String>();
                for (ArrayList<String> key: dfaTable.keySet()) {
                    explored.add(key.get(0));
                }
                if (!explored.contains(dfaTransition.get(0))) {
                    newStateNames.add(dfaTransition.get(0));
                }
            }
        }
        for (String newStateName: newStateNames) {
            return dfaTableMaker(dfaTable, newStateName);
        }
        System.out.println("Uh oh.");
        return dfaTable;
    }
}