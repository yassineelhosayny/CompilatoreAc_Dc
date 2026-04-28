package eccezioni;

import token.TokenType;

public class SyntacticException extends Exception{
    public SyntacticException(String msg){
        super("Syntactic Error: "+ msg); 
    }

    public SyntacticException(String atteso, int riga, TokenType ricevuto) {
        super("Syntactic Error -> riga: "+riga+", atteso: "+atteso+", ma è stato: "+ricevuto+".");
    }
}
