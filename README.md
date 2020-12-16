# Finite Automata Sim and NFA -> DFA Converter
Simulates Deterministic and Non-Deterministic Finite Automata and can convert NFAs to DFAs. You can query both DFAs and NFAs with strings.

\Both DFAs and NFAs are constructed with the same following parameters:

```List<String> alphabet```:                      a List of strings containing the symbols in the language's alphabet
  
```Map<String, String[][]> stateTransitions```:   a Map containing the transitions of each state where a key-value pair's key is its state label/name the value of the dict element is 2D Array of Strings where each Array within the 2D Array has a transition symbol at index 0 and a transition state at index 1

```String start```:                               a String of the FA's start state label/name

```List<String> accept```:                        a List containing the names of the accept states of the FA


\Both DFAs and NFAs can be queried with a string using the syntax:
```
nfaOrDfa.isAcceptedString(query);
```

\NFAs can be converted to DFAs using the syntax:
```
DFA newConvertedDfa = nfa.convertToDFA();
```
