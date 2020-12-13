package finiteautomata;
import java.util.*;

public class NFA extends FA {
    private boolean isAccepted;

    public NFA (List<String> alphabetList, Map<String, String[][]> statesTransitions,
            String startState, List<String> acceptStates) {
        super(alphabetList, statesTransitions, startState, acceptStates);
    }

    /**
     * 
     * @return
     */
    public boolean isValid() {
        if (!isValidStateFormat()) {
            System.out.println("ERR: Invalid state format. Use upper camel/Pascal case. Valid examples: A, Apple");
            return false;
        }
        for (String state : this.states) {
            String[][] transitions = transitionFunction.get(state);
            for (String[] transition : transitions) {
                if (transition.length > 0 && (!this.alphabet.contains(transition[0]) || !states.contains(transition[1]))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 
     */
    private void checkValidity() {
        if (!this.isValid()) {
            throw new IllegalArgumentException("The Finite Automata you entered is invalid. Try again.");
        }
    }

    /**
     * 
     * @param query
     * @return
     */
    public boolean isAcceptedString(String query) {
        this.checkValidity();
        if (query.equals("empty")) {
            return this.isEmpty();
        }
        else if (query.equals("lambda")) {
            return this.accept.contains(this.start);
        }
        else if (this.isEmpty()) {
            return false;
        }
        this.isAccepted = false;
        this.acceptStringHelper(this.start, query);
        return isAccepted;
    }

    /**
     * 
     * @param curr
     * @param queryRem
     */
    private void acceptStringHelper(String curr, String queryRem) {
        if (queryRem.length() == 0) {
            this.isAccepted = this.accept.contains(curr);
            return;
        }
        String[][] transitions = this.transitionFunction.get(curr);
        String[][] empty = new String[][] {{}};
        if (Arrays.deepEquals(transitions, empty)) {
            return;
        }
        for (String[] transition: transitions) {
            if (String.valueOf(queryRem.charAt(0)).equals(transition[0])) {
                this.acceptStringHelper(transition[1], queryRem.substring(1));
            }
            if (transition[0].equals("lambda")) {
                this.acceptStringHelper(transition[1], queryRem);
            }
        }
    }

    /**
     * 
     * @return
     */
    public DFA convertToDFA() {
        this.checkValidity();
        // Construct NFA state-transition table
        HashMap<List<String>, List<String>> dfaTable = new HashMap<>();
        List<String> startLambda = Arrays.asList(this.getLambdaStatesAsString(start));
        dfaTableMaker(dfaTable, startLambda);
        List<String> dfaAlphabet = new ArrayList<>(this.alphabet); dfaAlphabet.remove("lambda");
        Map<String, String[][]> dfaStatesTransitions = this.stFromTable(dfaTable);
        String dfaStart = startLambda.get(0);
        List<String> dfaAccept = this.getAcceptStates(getTableStates(dfaTable), accept);
        return new DFA(dfaAlphabet, dfaStatesTransitions, dfaStart, dfaAccept);
    }

    /**
     * 
     * @param table
     * @return
     */
    private Map<String, String[][]> stFromTable(HashMap<List<String>, List<String>> table) {
        Map<String, String[][]> res = new HashMap<>();
        List<String> states = this.getTableStates(table);
        for (String state: states) {
            List<String[]> transitions = new LinkedList<>();
            for (String symbol: this.alphabet) {
                if (!symbol.equals("lambda")) {
                    List<String> tableKey = Arrays.asList(state, symbol);
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

    /**
     * 
     * @param states
     * @param nfaAccept
     * @return
     */
    private List<String> getAcceptStates(List<String> states, List<String> nfaAccept) {
        List<String> res = new LinkedList<>();
        for (String state: states) {
            for (String nfaAcceptState: nfaAccept) {
                if (state.indexOf(nfaAcceptState) != -1) {
                    res.add(formatState(state));
                }
            }
        }
        return res;
    }

    /**
     * 
     * @param table
     * @return
     */
    private List<String> getTableStates(HashMap<List<String>, List<String>> table) {
        List<String> res = new ArrayList<>();
        for (List<String> key: table.keySet()) {
            if (!res.contains(key.get(0))) {
                res.add(key.get(0));
            }
        }
        return res;
    }

    /**
     * 
     */
    private String formatState (String state) {
        return state.substring(0,1).toUpperCase() + state.substring(1).toLowerCase();
    }

    /**
     * 
     * @param stateName
     * @return
     */
    public String getLambdaStatesAsString(String stateName) {
        return arrListToString(getLambdaStates(stateName));
    }

    /**
     * 
     * @param stateName
     * @return
     */
    private List<String> getLambdaStates(String stateName) {
        return this.table.get(Arrays.asList(stateName, "lambda"));
    }

    /**
     * Inverse method of stringToArrList
     * Joins elements of an input list together to
     * form a single string. Used for combining multiple
     * state names into a single state name.
     * @param arrList - List, usually a List of state names
     * @return String of all elements in arrList combined
     */
    private String arrListToString(List<String> arrList) {
        return String.join("", arrList);
    }



    /**
     * Inverse method of arrListToString
     * Takes an input string and converts it to an
     * ArrayList<String> that can be interpretted as
     * individual state. States names must follow
     * UpperCamelCase for this method to work correctly.
     * @param s - input string that will be interpretted
     * as an ArrayList<String>
     * @return res - s interpretted as an ArrayList<String>
     * of state names.
     */
    public static List<String> stringToArrList(String s) {
        List<String> res = new ArrayList<>();
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

    /**
     * dfaTableMaker is a recursive helper method for
     * convertToDFA() that takes an empty HashMap as an
     * initial input, and recurisvely adds states and 
     * transitions to it, returning completed dfaTable
     * when finished.
     * @param dfaTable - a HashMap<> that is recurisvely
     * added to to generate a dfaTable from this NFA
     * @param unfoundRowNames - used to find the next
     * row(s) of the dfaTable
     * @return dfaTable - used recursively but eventually
     * returns the complete, filled dfaTable
     */
    private HashMap<List<String>, List<String>> dfaTableMaker(HashMap<List<String>, List<String>> dfaTable, List<String> unfoundRowNames) {
        if (unfoundRowNames.size() == 0) {
            return dfaTable;
        }
        List<String> newUnfoundStates = new ArrayList<>();
        for (String rowStateName: unfoundRowNames) {
            List<String> rowStates = stringToArrList(rowStateName);
            for (String symbol: this.alphabet) {
                if (!symbol.equals("lambda")) {
                    List<String> dfaKey = Arrays.asList(rowStateName, symbol);
                    List<String> dfaTransition = Arrays.asList(arrListToString(getTransitions(rowStates, symbol)));
                    dfaTable.put(dfaKey, dfaTransition);
                    List<String> explored = new ArrayList<>();
                    for (List<String> key: dfaTable.keySet()) {
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

    /**
     * getTransitions is a helper method for dfaTableMaker
     * that takes a list of states (which will be later 
     * interpretted as a single state in the dfaTable) and
     * a symbol from the alphabet, and returns a list of 
     * the states that the input state(s) transitions to
     * (via a move using the input symbol and any lambda moves)
     * @param states - input list of states (will be interpretted
     * as single state in the dfaTable)
     * @param symbol - input symbol from the alphabet
     * @return transitions - list of states (will be interpretted
     * as single state in the dfaTable)
     */
    private List<String> getTransitions(List<String> states, String symbol) {
        List<String> transitions = new ArrayList<>();
        for (String state : states) {
            List<String> keyPerRowState = Arrays.asList(state, symbol);
            for (String transition : this.table.get(keyPerRowState)) {
                List<String> lambdaMoves = this.table.get(Arrays.asList(transition, "lambda"));
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

    /**
     * ashahsd
     * @param dfaTable
     */
    private void addDeadState(Map<List<String>, List<String>> dfaTable) {
        for (String symbol : this.alphabet) {
            if (!"lambda".equals(symbol)) {
                List<String> key = Arrays.asList("Dead", symbol);
                List<String> val = Arrays.asList("Dead");
                dfaTable.put(key, val);
            }
        }
    }
}