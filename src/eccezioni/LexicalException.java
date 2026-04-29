package eccezioni;

public class LexicalException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Costruttori
	public LexicalException(String line, int riga,char error,String msg){
		super(msg+" '"+error+"' "+" alla Riga: "+riga+"\n\tcodice valido fino a : '"+riga+"| "+line+"'");
	}
	public LexicalException(String line, int riga,String msg){
		super(msg+" alla Riga: "+riga+"\n\tcodice valido fino a : '"+riga+"| "+line+"'");
	}



}
