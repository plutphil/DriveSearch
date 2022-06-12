package com.example.DriveSearch;

import java.util.ArrayList;
import java.util.LinkedList;

public class NodeSearcher {
	int process;
	int procmax;
	Node innode;
	String request;
	ArrayList<Node> res= new ArrayList<>();
	boolean compFunc(String a) {
		return a.toLowerCase().startsWith(request.toLowerCase());
		
	}
	Thread th =new Thread(new Runnable() {
		
		@Override
		public void run() {
			process=0;
			LinkedList<Node> open=new LinkedList<>();
			open.add(innode);
			while(!open.isEmpty()) {
				Node nnow =open.pop();
				if(compFunc(nnow.name))res.add(nnow);
				process++;
				for(Node n:nnow.children) {
					n.parent=nnow;
					open.add(n);
					
				}
				if(res.size()>1000)open.clear();
			}
		}
	});
	public NodeSearcher() {
	}
	public int start(Node n,String q) {
		this.innode=n;
		this.request=q;
		this.procmax=innode.subnodes;
		if(th.isAlive()) {
			return(-1);
		}else {
			th.start();
			return(0);
		}
	}
	public boolean isFinished(){
			return !th.isAlive();
	}
	public ArrayList<Node> 	getResults(){
		return res;
	}
	public int getProcess() {
		return process;
	}
	public void setProcess(int process) {
		this.process = process;
	}
	public int getProcmax() {
		return procmax;
	}
	
}
