package ast;

public class NodeId extends NodeAst {
	
	private String name;
	
	public NodeId(String name) {
		 this.name = name;
	}
	public String getNome() {
		return name;
	}

	@Override
	public String toString() {
		return "ID[" + name + "]";
	}

}
