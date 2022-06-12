package com.example.DriveSearch;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;

import com.example.DriveSearch.NodeComparison.CompType;

public class Node{
	ArrayList<Node> children=new ArrayList<>();
	Node parent=null;
	String name;
	String path;
	String info="";
	String comment="";
	long size=0;
	int subnodes=0;
	int val1;
	int addr=0;
	public Node() {
	}
	public Node(String name) {
		this.name=name;
	}
	public Node(String name,Node parent) {
		this.name=name;
		this.parent=null;
	}
	public Node get(String name) {
		for(Node n:children) {
			if(n.name.equalsIgnoreCase(name)) {
				return(n);
			}
		}
		return null;
	}
	public Node getr(String name) {
		Node n;
		for (int i = children.size()-1; i >=0 ; i--) {
			n=children.get(i);
			if(n.name.equalsIgnoreCase(name)) {
				return(n);
			}
		}
		
		return null;
	}
	String getParentPath() {
		Node p=this;
		String out="";
		while(p!=null) {
			out="/"+p.name+out;
			p=p.parent;
		}
		return(out);
	}
	public Node getOrAdd(String name) {
		Node out;
		if((out=get(name))!=null) {
			return out;
		}
		out=new Node(name,this);
		out.parent=this;
		children.add(out);
		return out;
	}
	public Node addPath(String[] path) {
		
		Node nnow=this;
		for(int i=1;i<path.length;i++) {
			nnow=nnow.getOrAdd(path[i]);
		}
		return(nnow);
	}
	public void delete(String name) {
		for(int i=0;i<children.size();i++) {
			if(name.equalsIgnoreCase(children.get(i).name)) {
				children.remove(i);
				i=children.size();
			}
		}
	}
	public void delete(int index) {
		children.remove(index);
	}
	public ArrayList<Node> searchStartsWith(String q) {
		ArrayList<Node> res= new ArrayList<>();
		LinkedList<Node> open=new LinkedList<>();
		open.add(this);
		while(!open.isEmpty()) {
			Node nnow =open.pop();
			if(nnow.name.toLowerCase().startsWith(q.toLowerCase()))res.add(nnow);
			for(Node n:nnow.children) {
				n.parent=nnow;
				open.add(n);
			}
		}
		
		return res;
	}
	
	public ArrayList<Node> searchStartsRepl(String q) {
		ArrayList<Node> res= new ArrayList<>();
		LinkedList<Node> open=new LinkedList<>();
		open.add(this);
		while(!open.isEmpty()) {
			Node nnow =open.pop();
			
			if(!nnow.name.toLowerCase().startsWith(q.toLowerCase()))if(nnow.name.toLowerCase().replaceAll(q.toLowerCase(),"").length()<nnow.name.length())res.add(nnow);
			for(Node n:nnow.children) {
				n.parent=nnow;
				open.add(n);
			}
			if(res.size()>1000)open.clear();
		}
		
		return res;
	}
	public void print() {
		Node p=this.parent;
		/*while(p!=null) {
			System.out.print(p.name+"|");
			p=p.parent;
		}*/
		System.out.println(this.name);
		for(Node n:this.children) {
			n.parent=this;
			n.print(1);
		}
	}
	public void print(int level) {
		for(int x=0;x<level;x++) {
			System.out.print("-");
		}
		System.out.println(this.name);
		for(Node n:this.children) {
			n.parent=this;
			n.print(level+1);
		}
	}
	public String toString() {
		if(info!="")return(name+" "+info+" "+comment);
		return(name);
	}
	public Node getByPath(String[] path) {
		Node now=this;
		for(String str:path) {
			now=now.get(str);
		}
		return null;
	}
	public boolean compareName(Node other) {
		return name==other.name;
	}
	public ArrayList<NodeComparison> compare(Node other) {
		ArrayList<NodeComparison> out=new ArrayList<NodeComparison>();
		for(Node child:children) {
			Node nch;
			if((nch=other.get(child.name))==null) {
				out.add(new NodeComparison(child, other,CompType.CMPT_NOTFOUND));
			}else {
				if(child.size==nch.size) {
					out.addAll(child.compare(nch));
				}else {
					out.add(new NodeComparison(child,nch,CompType.CMPT_SIZECHANGED));
				}
			}
		}
		return  out;
	}
	void writeBin(FileByteWriter fbw) throws IOException {
		fbw.writeString(name);
		fbw.writeLong(size);
		fbw.writeLong(children.size());
		for(Node child:children) {
			child.writeBin(fbw);
		}
	}
	void readBin(FileByteReader fbr) throws IOException {
		name=fbr.readString();
		size=fbr.readLong();
		int chlen=(int)fbr.readLong();
		for (int i = 0; i < chlen; i++) {
			Node n;
			n=new Node();
			n.readBin(fbr);
			children.add(n);
		}
		
	}
}
