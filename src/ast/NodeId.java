package ast;

import symbolTable.SymbolTableEntry;

public class NodeId extends NodeAst {
	
	private String name;
	private SymbolTableEntry entry;
	
	public NodeId(String name) {
		 this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setEntry(SymbolTableEntry entry) {
		this.entry = entry;
	}
	
	public SymbolTableEntry getEntry() {
		return entry;
	}

	@Override
	public String toString() {
		return "ID[" + name + "]";
	}

}
