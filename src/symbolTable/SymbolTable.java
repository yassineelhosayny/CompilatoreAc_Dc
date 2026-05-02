package symbolTable;

import java.util.HashMap;
import ast.LangType;
import eccezioni.SematicException;

public class SymbolTable {

    private HashMap<String,SymbolTableEntry> table = new HashMap<>();
    

	public SymbolTableEntry lookup(String name) {
		return table.get(name);
	}
	public boolean entry(String name, LangType tipo) throws SematicException {
		if(dichLocale(name) && tipo != null)
			return false;
		this.table.put(name, new SymbolTableEntry(name, tipo));
		return true;
	}
	
	public boolean dichLocale(String name) throws SematicException {
		if(table.containsKey(name))
			throw new SematicException("'" + name +"' variabile già dichiarata!");
		return false;
	}
	
	
}
