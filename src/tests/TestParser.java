package tests;

import grammar.*;
import parser.NonTerminalWithProduction;
import parser.Parser;

public class TestParser {
    Parser parser;
    Grammar grammar;

    public TestParser(){
        parser = new Parser("data/test_grammar.in", false);
        grammar = parser.getGrammar();

        testExpand();
        testAdvance();
        testEpsilonAdvance();
        testMomentaryInsuccess();
        testBack();
        testBackEpsilon();
        testSuccess();
        testAnotherTry();
        testAnotherTryNoAvailableProductions();
        testAnotherTryGivesError();
    }

    private void testExpand(){
        parser.init();
        parser.expand();
        assert parser.getCurrentState().equals(Parser.ParserState.NormalState);
        assert parser.getI() == 0;
        assert parser.getWorkingStack().size() == 1;
        assert parser.getInputStack().size() == 2;
        Symbol symbol = parser.getWorkingStack().pop();
        assert symbol instanceof NonTerminalWithProduction;
        NonTerminalWithProduction nonTerminalWithProduction = (NonTerminalWithProduction) symbol;
        assert nonTerminalWithProduction.getProductionId() == 0;
        assert nonTerminalWithProduction.getRepresentation().equals(grammar.getStartingSymbol().getRepresentation());
        Production production = grammar.getProductions().get(nonTerminalWithProduction.getProductionId());
        for(Symbol symbol1 : production.getRhs())
            assert symbol1.equals(parser.getInputStack().pop());
    }

    private void testAdvance(){
        parser.init();
        parser.expand();
        parser.advance();
        assert parser.getCurrentState().equals(Parser.ParserState.NormalState);
        assert parser.getI() == 1;
        assert parser.getWorkingStack().size() == 2;
        assert parser.getInputStack().size() == 1;
        Symbol symbol = parser.getWorkingStack().pop();
        assert symbol instanceof Terminal;
        assert symbol.getRepresentation().equals("a");
    }

    public void testEpsilonAdvance(){
        parser.init();
        parser.getInputStack().pop();
        parser.getInputStack().push(new Epsilon());
        parser.epsilonAdvance();
        assert parser.getCurrentState().equals(Parser.ParserState.NormalState);
        assert parser.getI() == 0;
        assert parser.getWorkingStack().size() == 1;
        assert parser.getInputStack().size() == 0;
        Symbol symbol = parser.getWorkingStack().pop();
        assert symbol instanceof Epsilon;
    }

    public void testMomentaryInsuccess(){
        parser.init();
        parser.getInputStack().pop();
        parser.momentaryInsuccess();
        assert parser.getCurrentState().equals(Parser.ParserState.BackState);
        assert parser.getI() == 0;
        assert parser.getWorkingStack().size() == 0;
        assert parser.getInputStack().size() == 0;
    }

    public void testBack(){
        parser.init();
        parser.setCurrentState(Parser.ParserState.BackState);
        parser.getInputStack().pop();
        parser.getWorkingStack().push(new Terminal("a"));
        parser.setI(1);
        parser.back();
        assert parser.getCurrentState().equals(Parser.ParserState.BackState);
        assert parser.getI() == 0;
        assert parser.getWorkingStack().size() == 0;
        assert parser.getInputStack().size() == 1;
    }

    public void testBackEpsilon(){
        parser.init();
        parser.setCurrentState(Parser.ParserState.BackState);
        parser.getInputStack().pop();
        parser.getWorkingStack().push(new Epsilon());
        parser.setI(1);
        parser.back();
        assert parser.getCurrentState().equals(Parser.ParserState.BackState);
        assert parser.getI() == 1;
        assert parser.getWorkingStack().size() == 0;
        assert parser.getInputStack().size() == 1;
    }

    public void testSuccess(){
     parser.init();
     parser.success();
     assert parser.getCurrentState().equals(Parser.ParserState.FinalState);
    }

    public void testAnotherTry(){
        parser.init();
        parser.expand();
        parser.momentaryInsuccess();
        parser.anotherTry();
        assert parser.getCurrentState().equals(Parser.ParserState.NormalState);
        assert parser.getI() == 0;
        assert parser.getWorkingStack().size() == 1;
        assert parser.getInputStack().size() == 2;
        Symbol symbol = parser.getWorkingStack().pop();
        assert symbol instanceof NonTerminalWithProduction;
        NonTerminalWithProduction nonTerminalWithProduction = (NonTerminalWithProduction) symbol;
        assert nonTerminalWithProduction.getProductionId() == 1L;
        Production production = grammar.getProductions().get(1L);
        for(Symbol symbol1 : production.getRhs())
            assert symbol1.equals(parser.getInputStack().pop());
    }

    public void testAnotherTryNoAvailableProductions(){
        parser.init();
        parser.expand();
        parser.advance();
        parser.expand();
        parser.anotherTry();
        parser.anotherTry();
        parser.anotherTry();
        parser.momentaryInsuccess();
        parser.anotherTry();

        assert parser.getCurrentState().equals(Parser.ParserState.BackState);
        assert parser.getI() == 1;
        assert parser.getWorkingStack().size() == 2;
        assert parser.getInputStack().size() == 1;
        assert parser.getInputStack().pop().getRepresentation().equals("B");
    }

    public void testAnotherTryGivesError(){
        parser.init();
        parser.expand();
        parser.anotherTry();
        parser.anotherTry();
        assert parser.getCurrentState().equals(Parser.ParserState.ErrorState);
        assert parser.getI() == 0;
        assert parser.getWorkingStack().size() == 0;
        assert parser.getInputStack().size() == 2;
    }
}
