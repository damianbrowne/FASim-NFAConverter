// package finiteautomata;


import java.util.ArrayList;
import java.util.Map;
import java.util.*;

public class FA {
    ArrayList<String> states;
    ArrayList<String> alphabet;
    Map<String, String[][]> transitionFunction;
    String start;
    ArrayList<String> accept;

    FA (ArrayList<String> statesList, ArrayList<String> alphabetList, Map<String,String[][]> statesTransitions,
            String startState, ArrayList<String> acceptStates) {
        states = statesList;
        alphabet = alphabetList;
        transitionFunction = statesTransitions;
        start = startState;
        accept = acceptStates;

    }

    public boolean isValidStateFormat() {
        if (states.contains("Dead")) {
            System.out.println("ERR: 'Dead' is a reserved state name. Please define a different name.");
            return false;
        }
        for (String state: this.states) {
            if (!Character.isUpperCase(state.charAt(0))) { return false; }
            for (int i = 1; i < state.length(); i++) {
                if (Character.isUpperCase(state.charAt(i))) { return false; }
            }
        }
        return true;
    }
    
    public boolean isConnected(String initial, String goal) {
        ArrayList<String> visited = new ArrayList<String>();
        return this.isConnectedHelper(initial, goal, visited) != null;
    }

    private ArrayList<String> isConnectedHelper(String curr, String goal, ArrayList<String> visited) {
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

    public HashMap<String[], ArrayList<String>> stateTransitionTableMaker() {
        HashMap<String[], ArrayList<String>> table = new HashMap<String[], ArrayList<String>>();
        for (String state : this.states) {
            HashMap<String, ArrayList<String>> transitions = this.transitionDict(state);
            for (String posInput : this.alphabet) {
                String[] key = new String[] { state, posInput };
                table.put(key, new ArrayList<String>());
                ArrayList<String> transList = table.get(key);
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

    public HashMap<String, ArrayList<String>> transitionDict(String state) {
        HashMap<String, ArrayList<String>> transDict = new HashMap<String, ArrayList<String>>();
        String[][] transitions = this.transitionFunction.get(state);
        for (String[] transition : transitions) {
            ArrayList<String> val = new ArrayList<String>();
            transDict.put(transition[0], val);
        }
        for (String[] transition : transitions) {
            transDict.get(transition[0]).add(transition[1]);
        }
        return transDict;
    }

    public static void DFA1() {
        System.out.println("DFA1:");
        ArrayList<String> statesDFA1 = new ArrayList<String>(Arrays.asList("A", "B"));
        ArrayList<String> alphabetDFA1 = new ArrayList<String>(Arrays.asList("0", "1"));
        Map<String, String[][]> stDFA1 = new HashMap<>();
        String[][] aDFA1 = {{ "0", "B" }, { "1", "A" }};
        String[][] bDFA1 = {{ "0", "B" }, { "1", "A" }};
        stDFA1.put("A", aDFA1);
        stDFA1.put("B", bDFA1);
        String startDFA1 = "A";
        ArrayList<String> acceptDFA1 = new ArrayList<String>(Arrays.asList("B"));

        DFA DFA1 = new DFA(statesDFA1, alphabetDFA1, stDFA1, startDFA1, acceptDFA1);
        //System.out.println(DFA1.isValid());
        //System.out.println(DFA1.isConnected("A", "B"));
        //System.out.println(DFA1.alphabet);
        //System.out.println(!DFA1.isEmpty());
        //System.out.println(DFA1.isAcceptedString("10101010101100010101001010"));
        Iterator transitionsIterator = DFA1.stateTransitionTableMaker().entrySet().iterator();
        while (transitionsIterator.hasNext()) {
            Map.Entry transition = (Map.Entry) transitionsIterator.next();
            System.out.println(Arrays.toString((String[]) transition.getKey()) + ": " + transition.getValue());
        }
    }

    public static void NFA1() {
        System.out.println("NFA1:");
        ArrayList<String> statesNFA1 = new ArrayList<String>(Arrays.asList("A", "B"));
        ArrayList<String> alphabetNFA1 = new ArrayList<String>(Arrays.asList("0", "1"));
        Map<String, String[][]> stNFA1 = new HashMap<>();
        String[][] aNFA1 = { { "0", "A" }, { "1", "A" }, { "0", "B" } };
        String[][] bNFA1 = {};
        stNFA1.put("A", aNFA1);
        stNFA1.put("B", bNFA1);
        String startNFA1 = "A";
        ArrayList<String> acceptNFA1 = new ArrayList<String>(Arrays.asList("B"));

        NFA NFA1 = new NFA(statesNFA1, alphabetNFA1, stNFA1, startNFA1, acceptNFA1);
        //System.out.println(NFA1.isValid());
        //System.out.println(NFA1.isConnected("A", "B"));
        //System.out.println(NFA1.alphabet);
        //System.out.println(!NFA1.isEmpty());
        //System.out.println(NFA1.isAcceptedString("10101010101100010101001010"));
        Iterator transitionsIterator = NFA1.stateTransitionTableMaker().entrySet().iterator();
        while (transitionsIterator.hasNext()) {
            Map.Entry transition = (Map.Entry) transitionsIterator.next();
            System.out.println(Arrays.toString((String[]) transition.getKey()) + ": " + transition.getValue());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DFA1();
        NFA1();
    }
}
