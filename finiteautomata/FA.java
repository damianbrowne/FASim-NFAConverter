// package finiteautomata;
import java.util.*;

public class FA {
    ArrayList<String> states;
    ArrayList<String> alphabet;
    Map<String, String[][]> transitionFunction;
    String start;
    ArrayList<String> accept;
    HashMap<ArrayList<String>, ArrayList<String>> table;

    FA (ArrayList<String> statesList, ArrayList<String> alphabetList, Map<String,String[][]> statesTransitions,
            String startState, ArrayList<String> acceptStates) {
        states = statesList;
        alphabet = alphabetList;
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
            // for (int i = 1; i < state.length(); i++) {
            //     if (Character.isUpperCase(state.charAt(i))) { return false; }
            // }
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

    public HashMap<ArrayList<String>, ArrayList<String>> stateTransitionTableMaker() {
        HashMap<ArrayList<String>, ArrayList<String>> table = new HashMap<ArrayList<String>, ArrayList<String>>();
        for (String state : this.states) {
            HashMap<String, ArrayList<String>> transitions = this.transitionDict(state);
            for (String posInput : this.alphabet) {
                ArrayList<String> key = new ArrayList<String>(Arrays.asList(state,posInput));
                table.put(key, new ArrayList<String>());
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
        Iterator transitionsIterator = NFA1.table.entrySet().iterator();
        while (transitionsIterator.hasNext()) {
            Map.Entry transition = (Map.Entry) transitionsIterator.next();
            System.out.println(transition.getKey() + ": " + transition.getValue());
        }
        NFA1.convertToDFA();
    }

    public static void NFA2() {
        System.out.println("\nNFA2:");
        ArrayList<String> statesNFA2 = new ArrayList<String>(Arrays.asList("A", "B", "C", "D", "E"));
        ArrayList<String> alphabetNFA2 = new ArrayList<String>(Arrays.asList("0", "1"));
        Map<String, String[][]> stNFA2 = new HashMap<>();
        stNFA2.put("A", new String[][] {{"0", "B"}});
        stNFA2.put("B", new String[][] {{"0", "C"}});
        stNFA2.put("C", new String[][] {{"0", "C"},{"1", "C"},{"0", "D"}});
        stNFA2.put("D", new String[][] {{"1", "E"}});
        stNFA2.put("E", new String[][] {{"lambda", "A"}});
        String startNFA2 = "A";
        ArrayList<String> acceptNFA2 = new ArrayList<String>(Arrays.asList("E"));

        NFA NFA2 = new NFA(statesNFA2, alphabetNFA2, stNFA2, startNFA2, acceptNFA2);
        DFA convertedDFA2 = NFA2.convertToDFA();
        for (ArrayList<String> key : convertedDFA2.table.keySet()) {
            System.out.println(key + ": " + convertedDFA2.table.get(key));
        }
        System.out.println(convertedDFA2.isAcceptedString("0011111000101"));
        System.out.println(convertedDFA2.accept);
    }

    public static void main(String[] args) throws InterruptedException {
        //DFA1();
        //NFA1();
        NFA2();
    }
}
