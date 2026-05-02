package symbolTable;

import ast.LangType;

public class SymbolTableEntry {
    private String name;
    private LangType tipo;
    
	public SymbolTableEntry(String name, LangType tipo) {
		this.name = name;
		this.tipo = tipo;
	}

	public String getName() {
		return name;
	}

	public LangType getTipo() {
		return tipo;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTipo(LangType tipo) {
		this.tipo = tipo;
	}

	@Override
	public String toString() {
		return "SymbolTableEntry[name=" + name + ", tipo=" + tipo + "]";
	}
    
    
}
