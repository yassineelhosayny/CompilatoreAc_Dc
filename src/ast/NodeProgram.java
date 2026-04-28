package ast;

import java.util.ArrayList;

public class NodeProgram extends NodeAst {
	
	private ArrayList<NodeDecSt> decSts;

	public NodeProgram(ArrayList<NodeDecSt> decSts) {
		super();
		this.decSts = decSts;
	}

	public ArrayList<NodeDecSt> getDecSts() {
		return decSts;
	}

	@Override
	public String toString() {
		return "PROGRAM[decSts=" + decSts + "]";
	}
	
	
}
