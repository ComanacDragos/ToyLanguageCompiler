package fa;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FiniteAutomaton {
    //states of the FA
    List<String> states;
    //final states of the FA
    List<String> finalStates;
    //the alphabet
    List<String> alphabet;
    //Map of transitions: the key is composed of the start state and the character
    // and the value is the destination state
    Map<Map.Entry<String, String>, String> transitions = new HashMap<>();
    //initial state
    String initialState;

    //Constructor that reads the FA from a file
    public FiniteAutomaton(String file){
        List<String> lines = readFile(file);
        states = Arrays.stream(lines.get(0).split(",")).collect(Collectors.toList());
        alphabet = Arrays.stream(lines.get(1).split(",")).collect(Collectors.toList());
        initialState = lines.get(2);
        finalStates = Arrays.stream(lines.get(3).split(",")).collect(Collectors.toList());
        for (int i = 4; i < lines.size(); i++) {
            String[] tokens = lines.get(i).split(",");
            String[] characters = tokens[1].split("\\.");
            Arrays.stream(characters).forEach(c -> transitions.put(
                    Map.entry(tokens[0], c), tokens[2]
            ));
        }
    }

    //Wrapper that calls the recursive function that accepts the sequence with the initial state
    public Boolean isAccepted(String sequence){
        return isAcceptedRecursive(sequence, initialState);
    }

    //Recursive function that accepts the sequence
    //if the sequence is empty and the current state is in the final states it returns true, otherwise false
    //if there is no transition available from the current state then returns false
    Boolean isAcceptedRecursive(String sequence, String currentState){
        if(sequence.equals("")){
            return finalStates.contains(currentState);
        }
        String nextChar = String.valueOf(sequence.charAt(0));
        Map.Entry<String, String> key = Map.entry(currentState, nextChar);
        if(transitions.containsKey(key)){
            return isAcceptedRecursive(sequence.substring(1), transitions.get(key));
        }
        return false;
    }

    public List<String> getStates() {
        return states;
    }

    public List<String> getFinalStates() {
        return finalStates;
    }

    public List<String> getAlphabet() {
        return alphabet;
    }

    public Map<Map.Entry<String, String>, String> getTransitions() {
        return transitions;
    }

    public String getInitialState() {
        return initialState;
    }

    //read the lines from a file
    public List<String> readFile(String file){
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            return reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
