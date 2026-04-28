package ast;

public class NodeAssing extends NodeStm {
	private NodeId id;
	private NodeExpr expr;
	public NodeAssing(NodeId id, NodeExpr expr) {
		this.id = id;
		this.expr = expr;
	}
	public NodeId getId() {
		return id;
	}
	public NodeExpr getExpr() {
		return expr;
	}
	@Override
	public String toString() {
		return "ASSIGN[id=" + id + ",expr=" + expr + "]";
	}
	
}
