# Finite Automata Sim and NFA -> DFA Converter
Simulates Deterministic and Non-Deterministic Finite Automata and can convert NFAs to DFAs. You can query both DFAs and NFAs with strings.

This project uses Maven to run JUnit tests in the FATests.java file.

\
Both DFAs and NFAs are constructed with the following parameters:

```List<String> alphabet```:                      a List of strings containing the symbols in the language's alphabet
  
```Map<String, String[][]> stateTransitions```:   a Map containing the transitions of each state where a key-value pair's key is its state label/name the value of the dict element is 2D Array of Strings where each Array within the 2D Array has a transition symbol at index 0 and a transition state at index 1

```String start```:                               a String of the FA's start state label/name

```List<String> accept```:                        a List containing the names of the accept states of the FA
\

\
Both DFAs and NFAs can be queried with a string using the syntax:
```
nfaOrDfa.isAcceptedString(query);
```

\
NFAs can be converted to DFAs using the syntax:
```
DFA newConvertedDfa = nfa.convertToDFA();
```

\
Example:

Constructing a NFA that only accepts binary strings ending in 0:
```
List<String> binaryAlphabet = Arrays.asList("0","1");

Map<String, String[][]) nfaTransitions = new HashMap<>();
st.put("A", new String[][] {{"0", "A"},{"1", "A"},{"0", "B"}});
st.put("B", new String[][] {});

String startState = "A";

List<String> acceptStates = Arrays.asList("B");

NFA nfaEndingInZero = new NFA(binaryAlphabet, nfaTransitions, startState, acceptStates);
```

Converting NFA that only accepts binary strings ending in 0 to a DFA that only accepts binary strings ending in 0:
```
DFA dfaEndingInZero = nfaEndingInZero.convertToDFA();
```

Testing query strings on both FAs using JUnit:
```
for (Fa fa: new FA[] {nfaEndingInZero, dfaEndingInZero}) {
    assertTrue(fa.isAcceptedString("111001010"));
    assertTrue(fa.isAcceptedString("10"));
    assertTrue(!fa.isAcceptedString("111"));
    assertTrue(!fa.isAcceptedString("empty"));
    assertTrue(!fa.isAcceptedString("lambda"));
```
