package parser;

import java.util.ArrayList;

import ast.AssignOper;
import ast.LangOper;
import ast.LangType;
import ast.NodeAssing;
import ast.NodeBinOp;
import ast.NodeCost;
import ast.NodeDecSt;
import ast.NodeDecl;
import ast.NodeDeref;
import ast.NodeExpr;
import ast.NodeId;
import ast.NodePrint;
import ast.NodeProgram;
import ast.NodeStm;
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
    public NodeProgram parse() throws SyntacticException{
        return this.parsePrg();
    }
    //per ora restiusce void ma deve retiusce AST
    private NodeProgram parsePrg()throws SyntacticException{
        
    	Token t = null;
        try{
            t = scanner.peekToken();
        }catch(LexicalException e){
            throw new SyntacticException(e.getMessage());
        }
        switch(t.getType()){
            //Num_0: Prg → DSs $ predict{TYFLOAT,TYINT,ID,PRINT,EOF}
            case TYFLOAT,TYINT,ID,PRINT,EOF ->{
                NodeProgram DSs = new NodeProgram(parseDSs());
                match(TokenType.EOF);
                return DSs;
            }
            default ->{
                match(TokenType.SEMI);
                parse();
                throw new SyntacticException("TYFLOAT,TYINT,ID,PRINT,EOF (Panic Mode: salto al prossimo ';')", t.getRiga(),t.getType());
            }
        }

    }

    private ArrayList<NodeDecSt> parseDSs() throws SyntacticException{
        //Num_1_2_3
        Token t = null;
        try{
            t= scanner.peekToken();
        }catch(LexicalException e){
            throw new SyntacticException(e.getMessage());
        }
        switch(t.getType()){
            //DSs → Dcl DSs predict {TYFLOAT,TYINT }
            case TYFLOAT,TYINT ->{
                NodeDecl decl = parseDcl();
                ArrayList<NodeDecSt> decSt = parseDSs();
                decSt.add(0,decl);
                return decSt;
            }
            //DSs → Stm DSs predict {ID,PRINT}    
            case ID,PRINT ->{
                NodeStm stm = parseStm();
                ArrayList<NodeDecSt> decSt = parseDSs();
                decSt.add(0,stm);
                return decSt;

            }
            // DSs → ϵ predict {EOF}
            case EOF->{
                return new ArrayList<NodeDecSt>();
            }
            default->{
                throw new SyntacticException("TYFLOAT,TYINT,ID,PRINT o EOF",t.getRiga(),t.getType());
            }
        }

    }

    private NodeDecl parseDcl() throws SyntacticException {
        //Num_4 . Dcl → Ty id DclP predict {TYFLOAT,TYINT}
        Token t = null;
       try{
            t= scanner.peekToken();
        }catch(LexicalException e){
            throw new SyntacticException(e.getMessage());
        }

        switch(t.getType()){
            case TYFLOAT,TYINT->{
            	LangType ty = parseTy();
                NodeId id = new NodeId(match(TokenType.ID).getVal());
                NodeExpr init = parseDclP();
                return new NodeDecl(id,ty,init);
            }
            default->{
                throw new SyntacticException("TYFLOAT,TYINT",t.getRiga(),t.getType());
            }
        }
    }

    private NodeExpr parseDclP() throws SyntacticException {
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
                return null; 
            }
        //Num_6 . DclP → =Exp; predict {ASSIGN}
            case ASSIGN ->{
                match(TokenType.ASSIGN);
                NodeExpr exp = parseExp();
                match(TokenType.SEMI);
                return exp; 
            }
            default ->{
                throw new SyntacticException("';' oppure '='",t.getRiga(),t.getType());
            }
        }
    }
    private LangOper convertToLangOper(AssignOper op) {
    	switch(op) {
    	   case PLUS_ASSIGN ->{return LangOper.PLUS;}
    	   case MINUS_ASSIGN ->{ return LangOper.MINUS;}
    	   case MULTI_ASSIGN ->{ return LangOper.TIMES;}
    	   case DIVID_ASSIGN ->{ return LangOper.DIVID; }
    	   default ->{ return null;}
    	}
    }

    private NodeStm parseStm() throws SyntacticException {
        Token t = null;
       try{
            t= scanner.peekToken();
        }catch(LexicalException e){
            throw new SyntacticException(e.getMessage());
        }
        switch(t.getType()){
            //Num_7 stm -> id op Exp;  predict {id} AssignOper.ASSIGN
            case ID ->{
                NodeId id = new NodeId(match(TokenType.ID).getVal());
                AssignOper op = parseOp();
                NodeExpr init = parseExp();
                match(TokenType.SEMI);
               return op == AssignOper.ASSIGN ? 
				  	new NodeAssing(id,init) :
					new NodeAssing(id,new NodeBinOp(convertToLangOper(op), new NodeDeref(id),init) );
              
            }
            //NUm_8 stm -> print id;  predict {print}
            case PRINT ->{
                match(TokenType.PRINT);
                String id = match(TokenType.ID).getVal();
                match(TokenType.SEMI);
                return new NodePrint(new NodeId(id));
            }
            default ->{
                throw new SyntacticException("ID o PRINT",t.getRiga(),t.getType());
            }
        }
    }

    private NodeExpr parseExp() throws SyntacticException {
        Token t = null;
       try{
            t= scanner.peekToken();
        }catch(LexicalException e){
            throw new SyntacticException(e.getMessage());
        }
        switch(t.getType()){
            //Num_9 Exp ->Tr ExpP predict {ID,FLOAT,INT}
            case ID,FLOAT,INT ->{
                NodeExpr tr = parseTr();
                NodeExpr expP = parseExpP(tr);
                return expP; 
            }
            default ->{
                throw new SyntacticException("ID,FLOAT,INT",t.getRiga(),t.getType());
            }
        }
    }

    private NodeExpr parseExpP(NodeExpr left) throws SyntacticException { 
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
                NodeExpr tr =  parseTr();
                NodeExpr nuovoLeft = new NodeBinOp(LangOper.PLUS,left,tr);
                return parseExpP(nuovoLeft);  //costruzione della parte a destra prima, es. 3 + 5 - 5 diventa (3 + 5) - 5
                
            }
            //Num_11 ExpP → -Tr ExpP ,predict {-}
            case MINUS -> {
                match(TokenType.MINUS);
                NodeExpr tr= parseTr();
                NodeExpr nuovoLeft = new NodeBinOp(LangOper.MINUS,left,tr);
                return parseExpP(nuovoLeft);
            }

            //Num_12. ExpP → ϵ ,predict{;}
            case SEMI ->{
                return left;
            }
            default ->{
                throw new SyntacticException("'+' , '-' o ';'",t.getRiga(),t.getType());
            }
        }
    }


    private NodeExpr parseTr() throws SyntacticException { 
        Token t = null;
       try{
            t= scanner.peekToken();
        }catch(LexicalException e){
            throw new SyntacticException(e.getMessage());
        }
        switch(t.getType()){
            //Num_13. Tr → Val TrP predict {{ID,FLOAT,INT}}
            case ID,FLOAT,INT->{
                NodeExpr val= parseVal();
                NodeExpr trp= parseTrP(val);
                return trp; 
            }
            default ->{
                throw new SyntacticException("'ID' , 'FLOAT' o 'INT'",t.getRiga(),t.getType());
            }
        }
    }

    private NodeExpr parseTrP(NodeExpr left) throws SyntacticException { 
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
               NodeExpr val = parseVal();
               NodeExpr nuovoLeft = new NodeBinOp(LangOper.DIVID,left,val);
               return parseTrP(nuovoLeft);
            }
            //Num_15. TrP → *Val TrP predict {*}
            case MULTI->{
                match(TokenType.MULTI);
                NodeExpr val = parseVal();
                NodeExpr nuovoLeft = new NodeBinOp(LangOper.TIMES,left,val); 
                return parseTrP(nuovoLeft);
            }
            //Num_16. TrP → ϵ predict {MINUS,PLUS,SEMI}
            case MINUS,PLUS,SEMI ->{
            	return left;
            }
            default ->{
                throw new SyntacticException("'/' , '*', '+', '-', ';'",t.getRiga(),t.getType());
            }
        }

    }

    private LangType parseTy() throws SyntacticException { 
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
                return LangType.FLOAT; 
            }
            //Num_18. Ty → int predict {TYINT}
            case TYINT -> {
                match(TokenType.TYINT);
                return LangType.INT;
            }
            default ->{
                throw new SyntacticException("'TYFLOAT' , 'TYINT'",t.getRiga(),t.getType());
            }
        }
    }
    private NodeExpr parseVal() throws SyntacticException { 
       Token t = null;
       try{
            t= scanner.peekToken();
        }catch(LexicalException e){
            throw new SyntacticException(e.getMessage());
        }
        switch(t.getType()){
            //Num_19. Val → intVal predict {INT}
            case INT->{
               Token t_int= match(TokenType.INT);
                return new NodeCost(t_int.getVal(),LangType.INT);
            }
            //Num_20. Val → floatVal predict {FLOAT}
            case FLOAT->{
               String t_val= match(TokenType.FLOAT).getVal();
                return new NodeCost(t_val,LangType.FLOAT);
            }
            //Num_21. Val → id  predict {ID}
            case ID->{
                String id = match(TokenType.ID).getVal();
                return new NodeDeref(new NodeId(id)); 
            }
            default ->{
                throw new SyntacticException("'FLOAT' , 'INT' , 'ID'",t.getRiga(),t.getType());
            }

        }
    }
    private AssignOper parseOp() throws SyntacticException {
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
                return AssignOper.ASSIGN; 
                }
            //Num_23. op → PLUS_ASSIGN predict{+=}
            case PLUS_ASSIGN ->{
                match(TokenType.PLUS_ASSIGN);
                return AssignOper.PLUS_ASSIGN;
            }
            //Num_24. op → MINUS_ASSIGN predict{-=}
            case MINUS_ASSIGN ->{
                match(TokenType.MINUS_ASSIGN);
                return AssignOper.MINUS_ASSIGN;
            }
            //Num_25. op → MULTI_ASSIGN predict{*=}
            case MULTI_ASSIGN ->{
                match(TokenType.MULTI_ASSIGN);
                return AssignOper.MULTI_ASSIGN;
            }
            //Num_26. op → DIVID_ASSIGN predict{/=}
            case DIVID_ASSIGN ->{
                match(TokenType.DIVID_ASSIGN);
                return AssignOper.DIVID_ASSIGN;
            }
            default ->{
                throw new SyntacticException("'=' , '+=' , '-=' , '*=' , '/='",t.getRiga(),t.getType());
            }
        }
    }

}
