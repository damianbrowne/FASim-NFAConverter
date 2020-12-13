package finiteautomata;
import java.util.*;

public class FA {
    Set<String> states;
    List<String> alphabet;
    Map<String, String[][]> transitionFunction;
    String start;
    List<String> accept;
    HashMap<List<String>, ArrayList<String>> table;

    public FA (List<String> alphabetList, Map<String,String[][]> statesTransitions,
            String startState, List<String> acceptStates) {
        states = statesTransitions.keySet();
        alphabet = new ArrayList<>(alphabetList);
        if (this.getClass() == NFA.class) {
            alphabet.add("lambda");
        }
        transitionFunction = statesTransitions;
        start = startState;
        accept = acceptStates;
        table = this.stateTransitionTableMaker();

    }

    public boolean isValidStateFormat() {
        for (String state: this.states) {
            if (!Character.isUpperCase(state.charAt(0))) { return false; }
            for (int i = 1; i < state.length(); i++) {
                if (Character.isUpperCase(state.charAt(i))) { return false; }
            }
        }
        return true;
    }
    
    public boolean isConnected(String initial, String goal) {
        return this.isConnectedHelper(initial, goal, new ArrayList<String>()) != null;
    }

    private List<String> isConnectedHelper(String curr, String goal, ArrayList<String> visited) {
        if (curr.equals(goal)) {
            return visited;
        }
        visited.add(curr);
        String[][] transitions = this.transitionFunction.get(curr);
        for (String[] transition : transitions) {
            if (!visited.contains(transition[1])) {
                return this.isConnectedHelper(transition[1], goal, visited);
            }
        }
        return null;
    }

    public boolean isEmpty() {
        if (this.accept.size() == 0) { return true; }
        for (String acceptState : this.accept) {
            if (this.isConnected(this.start, acceptState)) {
                return false;
            }
        }
        return true;
    }

    public HashMap<List<String>, ArrayList<String>> stateTransitionTableMaker() {
        HashMap<List<String>, ArrayList<String>> table = new HashMap<>();
        for (String state : this.states) {
            HashMap<String, ArrayList<String>> transitions = this.transitionDict(state);
            for (String posInput : this.alphabet) {
                ArrayList<String> key = new ArrayList<>(Arrays.asList(state,posInput));
                table.put(key, new ArrayList<>());
                ArrayList<String> transList = table.get(key);
                if (posInput.equals("lambda")) {
                    transList.add(state);
                }
                Iterator transitionsIterator = transitions.entrySet().iterator();
                while (transitionsIterator.hasNext()) {
                    Map.Entry transition = (Map.Entry) transitionsIterator.next();
                    if (posInput.equals((String) transition.getKey())) {
                        transList.addAll((ArrayList<String>) transition.getValue());
                    }
                }
            }
        }
        return table;
    }

    /**
     * public HashMap<String, ArrayList<String>> transitionDict(String state) {
     * HashMap<String, ArrayList<String>> transDict = new HashMap<>(); String[][]
     * transitions = this.transitionFunction.get(state); String[][]
     * emptyReferenceForComparison = new String[][] { {} };
     * 
     * System.out.println(state); if (Arrays.deepEquals(transitions,
     * emptyReferenceForComparison)) { for (String symbol: this.alphabet) { if
     * (!"lambda".equals(symbol)) { transDict.put(symbol, new
     * ArrayList<>(Arrays.asList("Dead"))); } } } else { for (String[] transition :
     * transitions) { ArrayList<String> val = new ArrayList<>();
     * transDict.put(transition[0], val); System.out.println("transition: " +
     * transition[0] + transition[1]); } for (String[] transition : transitions) {
     * transDict.get(transition[0]).add(transition[1]); } }
     * System.out.println(transDict.toString()); return transDict; }
     */

    public HashMap<String, ArrayList<String>> transitionDict(String state) {
        HashMap<String, ArrayList<String>> transDict = new HashMap<>();
        String[][] transitions = this.transitionFunction.get(state);
        for (String[] transition : transitions) {
            if (transition.length > 0) {
                ArrayList<String> val = new ArrayList<>();
                transDict.put(transition[0], val);
            }
        }
        for (String[] transition : transitions) {
            if (transition.length > 0) {
                transDict.get(transition[0]).add(transition[1]);
            }
        }
        return transDict;
    }

    public boolean isAcceptedString(String query) {
        return this.isAcceptedString(query);
    }

    /**
     * lameEquals returns true if all the properties of this FA are equal to the
     * properties in the query FA. This method CAN return false when two FAs accept
     * the exact same set of strings. Ex. when state names are different; when one
     * of the FAs is a minimized version of the other. This method should not be
     * used as an end-all be-all
     * 
     * @param query FA that 'this' is being compared to
     * @return True if (this.props.equals(query).props) for all props
     */
    public boolean lameEquals(FA query) {
        List<String> thisAlphabet = new ArrayList<>(this.alphabet);
        if (thisAlphabet.contains("lambda")) {
            thisAlphabet.remove("lambda");
        }
        List<String> queryAlphabet = new ArrayList<>(query.alphabet);
        if (queryAlphabet.contains("lambda")) {
            queryAlphabet.remove("lambda");
        }
        return thisAlphabet.equals(queryAlphabet) && this.transitionFunction.equals(query.transitionFunction)
                && this.start.equals(query.start) && this.accept.equals(query.accept);
    }

    public static void NFA2() {
        System.out.println("\nNFA2:");
        List<String> alphabetNFA2 = Arrays.asList("0", "1");
        Map<String, String[][]> stNFA2 = new HashMap<>();
        stNFA2.put("A", new String[][] {{"0", "B"}});
        stNFA2.put("B", new String[][] {{"0", "C"}});
        stNFA2.put("C", new String[][] {{"0", "C"},{"1", "C"},{"0", "D"}});
        stNFA2.put("D", new String[][] {{"1", "E"}});
        stNFA2.put("E", new String[][] {{"lambda", "A"}});
        String startNFA2 = "A";
        List<String> acceptNFA2 = Arrays.asList("E");

        NFA NFA2 = new NFA(alphabetNFA2, stNFA2, startNFA2, acceptNFA2);
        DFA convertedDFA2 = NFA2.convertToDFA();
        for (List<String> key : convertedDFA2.table.keySet()) {
            System.out.println(key + ": " + convertedDFA2.table.get(key));
        }
        System.out.println(convertedDFA2.isAcceptedString("0011111000101"));
        System.out.println(convertedDFA2.accept);
    }

    public static void nfa3() {
        Map<String, String[][]> st = new HashMap<>();
        st.put("A", new String[][] { { "a", "A" }, { "b", "A" }, { "c", "A" }, { "b", "B" }, { "a", "C" }, { "c", "D" } });
        st.put("B", new String[][] { { "c", "E" } });
        st.put("C", new String[][] { { "b", "F" } });
        st.put("D", new String[][] { { "a", "G" } });
        st.put("E", new String[][] { {} });
        st.put("F", new String[][] { {} });
        st.put("G", new String[][] { {} });
        NFA nfa3 = new NFA(Arrays.asList("a", "b", "c"), st, "A", Arrays.asList("E", "F", "G"));

        DFA convertedDFA3 = nfa3.convertToDFA();
        for (List<String> key : convertedDFA3.table.keySet()) {
            System.out.println(key + ": " + convertedDFA3.table.get(key));
        }
        System.out.println(convertedDFA3.isAcceptedString("abcbabcabcbcbcabaabcbbcccbc"));
        System.out.println(convertedDFA3.isAcceptedString("bccabbabcbcabbcabcabcabcca"));
        System.out.println(convertedDFA3.accept);
    }

    public static void main(String[] args) throws InterruptedException {
        nfa3();
    }
}
