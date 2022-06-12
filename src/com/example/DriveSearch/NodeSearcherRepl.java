package com.example.DriveSearch;

class NodeSearcherRepl extends NodeSearcher{
	@Override
	boolean compFunc(String a) {
		if(!super.compFunc(a))
			if(a.toLowerCase().replaceAll(request.toLowerCase(),"").length()<a.length())
				return(true);
		return(false);
	}
}
