package scanner;

public class LexicalException extends Exception {
	
	// Costruttori
	public LexicalException(String line, int riga,String msg){
		super(msg+" nella Riga: "+riga+":-->'"+line+"'");
	}

}
