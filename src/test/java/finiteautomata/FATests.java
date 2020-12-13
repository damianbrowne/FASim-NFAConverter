package finiteautomata;
import java.util.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FATests {
    public DFA dfa1() {
        Map<String, String[][]> st = new HashMap<>();
        st.put("A", new String[][] {{ "0", "B" }, { "1", "A" }});
        st.put("B", new String[][] {{ "0", "B" }, { "1", "A" }});
        return new DFA(Arrays.asList("0", "1"), st, "A", Arrays.asList("B"));
    }

    public NFA nfa1() {
        Map<String, String[][]> st = new HashMap<>();
        st.put("A", new String[][] { { "0", "A" }, { "1", "A" }, { "0", "B" } });
        st.put("B", new String[][] {});
        return new NFA(Arrays.asList("0", "1"), st, "A", Arrays.asList("B"));
    }

    public NFA nfa2() {
        Map<String, String[][]> st = new HashMap<>();
        st.put("A", new String[][] { { "0", "B" } });
        st.put("B", new String[][] { { "0", "C" } });
        st.put("C", new String[][] { { "0", "C" }, { "1", "C" }, { "0", "D" } });
        st.put("D", new String[][] { { "1", "E" } });
        st.put("E", new String[][] { { "lambda", "A" } });

        return new NFA(Arrays.asList("0", "1"), st, "A", Arrays.asList("E"));
    }

    public NFA nfa3() {
        Map<String, String[][]> st = new HashMap<>();
        st.put("A", new String[][] { { "a", "A"}, {"b", "A"}, {"c", "A"}, {"b","B"}, {"a", "C"}, {"c", "D"}});
        st.put("B", new String[][] { {"c", "E"} });
        st.put("C", new String[][] { {"b", "F"} });
        st.put("D", new String[][] { {"a", "G"} });
        st.put("E", new String[][] { {} });
        st.put("F", new String[][] { {} });
        st.put("G", new String[][] { {} });
        return new NFA(Arrays.asList("a", "b", "c"), st, "A", Arrays.asList("E", "F", "G"));
    }

    @Test
    void test1() {
        DFA dfa1 = dfa1();
        NFA nfa1 = nfa1();
        DFA nfa1Converted = nfa1.convertToDFA();
        for (FA fa: new FA[]{dfa1, nfa1, nfa1Converted}) {
            assertTrue(fa.start.equals("A"));
            assertTrue(fa.isAcceptedString("0"));
            assertTrue(fa.isAcceptedString("110"));
            assertTrue(fa.isAcceptedString("0000"));
            assertTrue(fa.isAcceptedString("1010101001100101101111001010101010100100"));
            assertFalse(fa.isAcceptedString("empty"));
            assertFalse(fa.isAcceptedString("1"));
            assertFalse(fa.isAcceptedString("lambda"));
        }
    }
    @Test
    void test2() {
        NFA nfa2 = nfa2();
        DFA nfa2Converted = nfa2.convertToDFA();
        for (FA fa : new FA[] { nfa2, nfa2Converted }) {
            assertTrue(fa.isAcceptedString("000101"));
            assertTrue(fa.isAcceptedString("00010001"));
            assertTrue(fa.isAcceptedString("0011101011010010011001"));
            assertFalse(fa.isAcceptedString("empty"));
            assertFalse(fa.isAcceptedString("lambda"));
            assertFalse(fa.isAcceptedString("10110001"));
            assertFalse(fa.isAcceptedString("01001101"));
        }
    }

    @Test
    void test3() {
        NFA nfa3 = nfa3();
        DFA nfa3Converted = nfa3.convertToDFA();
        assertEquals(nfa3Converted.alphabet.size(),3);
        assertTrue(nfa3.isAcceptedString("abcabcbabcbca"));
        for (FA fa: new FA[] {nfa3, nfa3Converted}) {
            assertTrue(fa.isAcceptedString("abcabcbabca"));
            assertTrue(fa.isAcceptedString("bc"));
            assertTrue(fa.isAcceptedString("aaab"));
            assertFalse(fa.isAcceptedString("empty"));
            assertFalse(fa.isAcceptedString("lambda"));
            assertFalse(fa.isAcceptedString("ac"));
            assertFalse(fa.isAcceptedString("a"));
            assertFalse(fa.isAcceptedString("acb"));
            assertFalse(fa.isAcceptedString("0"));
        }

    }
}    