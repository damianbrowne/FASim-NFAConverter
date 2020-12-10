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
        ArrayList<String> startLambda = new ArrayList<String>(Arrays.asList(this.getLambdaStatesAsString(start)));
        dfaTableMaker(dfaTable, startLambda);
        ArrayList<String> dfaStates = new ArrayList<String>();
        for (String state: getTableStates(dfaTable)) {
            dfaStates.add(formatState(state));
        }
        ArrayList dfaAlphabet = (ArrayList) this.alphabet.clone(); dfaAlphabet.remove("lambda");
        Map<String, String[][]> dfaStatesTransitions = this.stFromTable(dfaTable);
        String dfaStart = startLambda.get(0);
        ArrayList<String> dfaAccept = this.getAcceptStates(getTableStates(dfaTable), accept);
        return new DFA(dfaStates, dfaAlphabet, dfaStatesTransitions, dfaStart, dfaAccept);
    }

    private Map<String, String[][]> stFromTable(HashMap<ArrayList<String>, ArrayList<String>> table) {
        Map<String, String[][]> res = new HashMap<>();
        ArrayList<String> states = this.getTableStates(table);
        for (String state: states) {
            ArrayList<String[]> transitions = new ArrayList<String[]>();
            for (String symbol: this.alphabet) {
                if (!symbol.equals("lambda")) {
                    ArrayList<String> tableKey = new ArrayList<>(Arrays.asList(state, symbol));
                    String[] newTransition = {symbol, formatState(table.get(tableKey).get(0))};
                    transitions.add(newTransition);
                }
            }
            String[][] transitionsArray = new String[transitions.size()][2];
            for (int i = 0; i < transitions.size(); i++) {
                transitionsArray[i] = transitions.get(i);
            }
            res.put(formatState(state), transitionsArray);
        }
        return res;
    }

    private ArrayList<String> getAcceptStates(ArrayList<String> states, ArrayList<String> nfaAccept) {
        ArrayList<String> res = new ArrayList<String>();
        for (String state: states) {
            for (String nfaAcceptState: nfaAccept) {
                if (state.indexOf(nfaAcceptState) != -1) {
                    res.add(formatState(state));
                }
            }
        }
        return res;
    }

    private ArrayList<String> getTableStates(HashMap<ArrayList<String>, ArrayList<String>> table) {
        ArrayList<String> res = new ArrayList<String>();
        for (ArrayList<String> key: table.keySet()) {
            if (!res.contains(key.get(0))) {
                res.add(key.get(0));
            }
        }
        return res;
    }

    private String formatState (String state) {
        return state.substring(0,1).toUpperCase() + state.substring(1).toLowerCase();
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

    public HashMap<ArrayList<String>, ArrayList<String>> dfaTableMaker(HashMap<ArrayList<String>, ArrayList<String>> dfaTable, ArrayList<String> unfoundRowNames) {
        if (unfoundRowNames.size() == 0) {
            return dfaTable;
        }
        ArrayList<String> newUnfoundStates = new ArrayList<String>();
        for (String rowStateName: unfoundRowNames) {
            ArrayList<String> rowStates = stringToArrList(rowStateName);
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
                        newUnfoundStates.add(dfaTransition.get(0));
                    }
                }
            }
        }
        if (newUnfoundStates.contains("Dead")) {
            this.addDeadState(dfaTable);
            newUnfoundStates.remove("Dead");
        }
        return dfaTableMaker(dfaTable, newUnfoundStates);
    }

    private ArrayList<String> getTransitions(ArrayList<String> states, String symbol) {
        ArrayList<String> transitions = new ArrayList<String>();
        for (String state : states) {
            ArrayList<String> keyPerRowState = new ArrayList<String>(Arrays.asList(state, symbol));
            for (String transition : this.table.get(keyPerRowState)) {
                ArrayList<String> lambdaMoves = this.table.get(Arrays.asList(transition, "lambda"));
                for (String lambdaMove : lambdaMoves) {
                    if (!transitions.contains(lambdaMove)) {
                        transitions.add(lambdaMove);
                    }
                }
            }
        }
        if (transitions.size() == 0) {
            transitions.add("Dead");
        }
        return transitions;
    }

    private void addDeadState(HashMap<ArrayList<String>, ArrayList<String>> dfaTable) {
        for (String symbol : this.alphabet) {
            if (!symbol.equals("lambda")) {
                ArrayList<String> key = new ArrayList<String>(Arrays.asList("Dead", symbol));
                ArrayList<String> val = new ArrayList<String>(Arrays.asList("Dead"));
                dfaTable.put(key, val);
            }
        }
    }
}