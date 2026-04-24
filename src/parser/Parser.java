package parser;

import eccezioni.LexicalException;
import eccezioni.SyntacticException;
import scanner.Scanner;
import token.Token;
import token.TokenType;

public class Parser {
    private Scanner scanner ;

    public Parser(Scanner scanner){
        this.scanner = scanner;
    }


    private Token match(TokenType atteso) throws SyntacticException{
       Token t;
        try {
            t = scanner.peekToken();
        }catch(LexicalException e){
            throw new SyntacticException(e.getMessage());
        }
       if(atteso.equals(t.getType())){
        try {
            return scanner.nextToken();  
        }catch(LexicalException e){
                throw new SyntacticException(e.getMessage());
            }
        }else{
            throw new SyntacticException(atteso.toString(),t.getRiga(),t.getType());
        }
        
    }
    public void parse() throws SyntacticException{
        /*return */this.parsePrg();
    }
    //per ora restiusce void ma deve retiusce AST
    private void parsePrg()throws SyntacticException{
        Token t = null;
        try{
            t = scanner.peekToken();
        }catch(LexicalException e){
            throw new SyntacticException(e.getMessage());
        }
        switch(t.getType()){
            //Num_0: Prg → DSs $ predict{TYFLOAT,TYINT,ID,PRINT,EOF}
            case TYFLOAT,TYINT,ID,PRINT,EOF ->{
                parseDSs();
                match(TokenType.EOF);
                //return
            }
            default ->{
                match(TokenType.SEMI);
                parse();
                throw new SyntacticException("TYFLOAT,TYINT,ID,PRINT,EOF (Panic Mode: salto al prossimo ';')", t.getRiga(),t.getType());
            }
        }

    }

    private void parseDSs() throws SyntacticException{
        //Num_1_2_3
        Token t=null;
        try{
            t= scanner.peekToken();
        }catch(LexicalException e){
            throw new SyntacticException(e.getMessage());
        }
        switch(t.getType()){
            //DSs → Dcl DSs predict {TYFLOAT,TYINT }
            case TYFLOAT,TYINT ->{
                parseDcl();
                parseDSs();

                return;
            }
            //DSs → Stm DSs predict {ID,PRINT}    
            case ID,PRINT ->{
                parseStm();
                parseDSs();

                return;
            }
            // DSs → ϵ predict {EOF}
            case EOF->{
                return;
            }
            default->{
                throw new SyntacticException("TYFLOAT,TYINT,ID,PRINT o EOF",t.getRiga(),t.getType());
            }
        }

    }

    private void parseDcl() throws SyntacticException {
        //Num_4 . Dcl → Ty id DclP predict {TYFLOAT,TYINT}
        Token t = null;
       try{
            t= scanner.peekToken();
        }catch(LexicalException e){
            throw new SyntacticException(e.getMessage());
        }

        switch(t.getType()){
            case TYFLOAT,TYINT->{
                parseTy();
                match(TokenType.ID);
                parseDclP();
            }
            default->{
                throw new SyntacticException("TYFLOAT,TYINT",t.getRiga(),t.getType());
            }
        }
    }

    private void parseDclP() throws SyntacticException {
        Token t = null;
       try{
            t= scanner.peekToken();
        }catch(LexicalException e){
            throw new SyntacticException(e.getMessage());
        }

        switch(t.getType()){
        //Num_5 . DclP → ; predict {;}
            case SEMI ->{
                match(TokenType.SEMI);
            }
        //Num_6 . DclP → =Exp; predict {ASSIGN}
            case ASSIGN ->{
                match(TokenType.ASSIGN);
                parseExp();
                match(TokenType.SEMI);
            }
            default ->{
                throw new SyntacticException(";",t.getRiga(),t.getType());
            }
        }
    }

    private void parseStm() throws SyntacticException {
        Token t = null;
       try{
            t= scanner.peekToken();
        }catch(LexicalException e){
            throw new SyntacticException(e.getMessage());
        }
        switch(t.getType()){
            //Num_7 stm -> id op Exp;  predict {id}
            case ID ->{
                match(TokenType.ID);
                parseOp();
                parseExp();
                match(TokenType.SEMI);
            }
            //NUm_8 stm -> print id;  predict {print}
            case PRINT ->{
                match(TokenType.PRINT);
                match(TokenType.ID);
                match(TokenType.SEMI);
            }
            default ->{
                throw new SyntacticException("ID o PRINT",t.getRiga(),t.getType());
            }
        }
    }

    private void parseExp() throws SyntacticException {
        Token t = null;
       try{
            t= scanner.peekToken();
        }catch(LexicalException e){
            throw new SyntacticException(e.getMessage());
        }
        switch(t.getType()){
            //Num_9 Exp ->Tr ExpP predict {ID,FLOAT,INT}
            case ID,FLOAT,INT ->{
                parseTr();
                parseExpP();
            }
            default ->{
                throw new SyntacticException("ID,FLOAT,INT",t.getRiga(),t.getType());
            }
        }
    }

    private void parseExpP() throws SyntacticException {
        Token t = null;
       try{
            t= scanner.peekToken();
        }catch(LexicalException e){
            throw new SyntacticException(e.getMessage());
        }
        switch(t.getType()){
            //Num_10 ExpP → +Tr ExpP ,predict {+}
            case PLUS -> {
                match(TokenType.PLUS);
                parseTr();
                parseExpP();
            }
            //Num_11 ExpP → -Tr ExpP ,predict {-}
            case MINUS -> {
                match(TokenType.MINUS);
                parseTr();
                parseExpP();
            }

            //Num_12. ExpP → ϵ ,predict{;}
            case SEMI ->{
                return;
            }
            default ->{
                throw new SyntacticException("'+' , '-' o ';'",t.getRiga(),t.getType());
            }
        }
    }


    private void parseTr() throws SyntacticException {
        Token t = null;
       try{
            t= scanner.peekToken();
        }catch(LexicalException e){
            throw new SyntacticException(e.getMessage());
        }
        switch(t.getType()){
            //Num_13. Tr → Val TrP predict {{ID,FLOAT,INT}}
            case ID,FLOAT,INT->{
                parseVal();
                parseTrP();
            }
            default ->{
                throw new SyntacticException("'ID' , 'FLOAT' o 'INT'",t.getRiga(),t.getType());
            }
        }
    }

    private void parseTrP() throws SyntacticException {
       Token t = null;
       try{
            t= scanner.peekToken();
        }catch(LexicalException e){
            throw new SyntacticException(e.getMessage());
        }
        switch(t.getType()){
            //Num_14. TrP → /Val TrP predict {/}
            case DIVID->{
                match(TokenType.DIVID);
                parseVal();
                parseTrP();
            }
            //Num_15. TrP → *Val TrP predict {*}
            case MULTI->{
                match(TokenType.MULTI);
                parseVal();
                parseTrP();
            }
            //Num_16. TrP → ϵ predict {MINUS,PLUS,SEMI}
            case MINUS,PLUS,SEMI ->{
                return;
            }
            default ->{
                throw new SyntacticException("'/' , '*'",t.getRiga(),t.getType());
            }
        }

    }

    private void parseTy() throws SyntacticException {
       Token t = null;
       try{
            t= scanner.peekToken();
        }catch(LexicalException e){
            throw new SyntacticException(e.getMessage());
        }
        switch(t.getType()){
            //Num_17. Ty → float predict {TYFLOAT}
            case TYFLOAT -> {
                match(TokenType.TYFLOAT);
            }
            //Num_18. Ty → int predict {TYINT}
            case TYINT -> {
                match(TokenType.TYINT);
            }
            default ->{
                throw new SyntacticException("'TYFLOAT' , 'TYINT'",t.getRiga(),t.getType());
            }
        }
    }
    private void parseVal() throws SyntacticException {
       Token t = null;
       try{
            t= scanner.peekToken();
        }catch(LexicalException e){
            throw new SyntacticException(e.getMessage());
        }
        switch(t.getType()){
            //Num_19. Val → intVal predict {INT}
            case INT->{
                match(TokenType.INT);
            }
            //Num_20. Val → floatVal predict {FLOAT}
            case FLOAT->{
                match(TokenType.FLOAT);
            }
            //Num_21. Val → id  predict {ID}
            case ID->{
                match(TokenType.ID);
            }
            default ->{
                throw new SyntacticException("'FLOAT' , 'INT' , 'ID'",t.getRiga(),t.getType());
            }

        }
    }
    private void parseOp() throws SyntacticException {
       Token t = null;
       try{
            t= scanner.peekToken();
        }catch(LexicalException e){
            throw new SyntacticException(e.getMessage());
        }
        switch(t.getType()){
            //Num_22. op → ASSIGN predict{=}
            case ASSIGN ->{
                match(TokenType.ASSIGN);
            }
            //Num_23. op → PLUS_ASSIGN predict{+=}
            case PLUS_ASSIGN ->{
                match(TokenType.PLUS_ASSIGN);
            }
            //Num_24. op → PLUS_ASSIGN predict{-=}
            case MINUS_ASSIGN ->{
                match(TokenType.MINUS_ASSIGN);
            }
            //Num_25. op → PLUS_ASSIGN predict{*=}
            case MULTI_ASSIGN ->{
                match(TokenType.MULTI_ASSIGN);
            }
            //Num_26. op → PLUS_ASSIGN predict{/=}
            case DIVID_ASSIGN ->{
                match(TokenType.DIVID_ASSIGN);
            }
            default ->{
                throw new SyntacticException("'=' , '+=' , '-=' , '*=' , '/='",t.getRiga(),t.getType());
            }
        }
    }

}
