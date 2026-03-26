package token;

public class Token {

     private TokenType tipo;
     private int riga;
     private String val;


    public Token(TokenType tipo, String val,int riga){
        this.tipo= tipo;
        this.val = val;
        this.riga= riga;
    }
    public Token(TokenType tipo,int riga){
        this.tipo= tipo;
        this.val = null;
        this.riga= riga;
    }
    public TokenType getType() {
        return this.tipo;
    }

    public String getVal() {
        return this.val;
    }

    public int getRiga() {
        return this.riga;
    }

    public String toString() {
        return "Token: in Riga= "+this.riga+" | Tipo = "+getType().toString()+(this.val != null ?  " | Value= < "+this.val+" >" : "");
    }
}
