package com.example.DriveSearch;

public class NodeComparison {
	Node a;
	Node b;
	public enum CompType{CMPT_NOTFOUND,CMPT_EQUAL,CMPT_ADDED,CMPT_DELETED,CMPT_SIZECHANGED};
	CompType comptype;
	public NodeComparison(Node a, Node b, CompType comptype) {
		super();
		this.a = a;
		this.b = b;
		this.comptype = comptype;
	}
	
}
