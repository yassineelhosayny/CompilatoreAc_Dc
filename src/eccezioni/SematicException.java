package eccezioni;



public class SematicException extends Exception{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SematicException(String msg){
        super("Sematic Error: "+ msg); 
    }
}
