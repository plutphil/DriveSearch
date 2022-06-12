package com.example.DriveSearch;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.ProgressMonitor;

public class TreeIO {

	public TreeIO() {
		root=new Node("");
		if(System.getProperty("os.name").startsWith("Windows")) {
			windows=true;
		}
		// TODO Auto-generated constructor stub
	}
	public static int FILETYPE_FILE=1;
	public static int FILETYPE_LINK=2;
	public static int FILETYPE_DIRECTORY=3;
	public boolean windows;

	static boolean contains(ArrayList<String> list,String f) {

		return false;

	}
	public static int countLines(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}
	Node root;
	long total=1;
	long current=1;
	long dircount=0;
	long filecount=0;
	long dirmax=1;
	public void read(File f,String info) {
		total=1;
		current=1;
		dircount=0;
		filecount=0;
		dirmax=1;
		LinkedList<String> open=new LinkedList<>();
		//System.out.println(f.getAbsolutePath());
		open.add(f.getAbsolutePath());

		Node subnode=new Node(f.getAbsolutePath());
		subnode.info=info;
		while(!open.isEmpty()) {
			String now=open.poll();
			File fnow=new File(now);
			int x=0;
			try {
				if(fnow.isDirectory()&&!Files.isSymbolicLink(fnow.toPath())) {
					dircount++;
					File[] subfiles=new File(now).listFiles();
					
					//System.out.println();
					if(subfiles!=null)
						
						total+=subfiles.length;
						for(File i:subfiles) {

							String formattedpath= i.getAbsolutePath();
							//int sepoccurence=formattyedpath.length()-formattedpath.replaceAll(File.separator, "").length();
							Node edit;
							try {
								if(windows) {
									//System.out.println(formattedpath);
									edit=subnode.addPath(formattedpath.split(File.separator+File.separator));

								}else {
									edit=subnode.addPath(formattedpath.replaceFirst(f.getAbsolutePath(), "").split(File.separator));
								}
							}catch(Exception e) {
								edit=subnode.addPath(new String[] {formattedpath});
							}
							if(Files.isSymbolicLink(i.toPath())) {
								edit.val1=FILETYPE_LINK;
							}else {
								if(i.isDirectory()) {
									edit.val1=FILETYPE_DIRECTORY;
									dirmax++;
								}else {
									edit.val1=FILETYPE_FILE;
									edit.size=i.length();
									
									
									
									
								}
							}
							open.add(i.getAbsolutePath());
							x++;
							current++;
							subnode.subnodes++;
						}
				}else {
					filecount++;
				}
			}catch (Exception e) {
				System.out.println(fnow.getAbsolutePath());
				e.printStackTrace();
			}
		}
		root.children.add(subnode);
		
		//root.print();
	}
	public Node readToNode(File f,String info) {
		total=1;
		current=1;
		dircount=0;
		filecount=0;
		dirmax=1;
		LinkedList<String> open=new LinkedList<>();
		//System.out.println(f.getAbsolutePath());
		open.add(f.getAbsolutePath());

		Node subnode=new Node(f.getAbsolutePath());
		subnode.info=info;
		while(!open.isEmpty()) {
			String now=open.poll();
			File fnow=new File(now);
			int x=0;
			try {
				if(fnow.isDirectory()&&!Files.isSymbolicLink(fnow.toPath())) {
					dircount++;
					File[] subfiles=new File(now).listFiles();
					
					//System.out.println();
					if(subfiles!=null)
						
						total+=subfiles.length;
						for(File i:subfiles) {

							String formattedpath= i.getAbsolutePath();
							//int sepoccurence=formattyedpath.length()-formattedpath.replaceAll(File.separator, "").length();
							Node edit;
							try {
								String[] arrpath;
								if(windows) {
									//System.out.println(formattedpath);
									arrpath=formattedpath.split(File.separator+File.separator);
									

								}else {
									arrpath=formattedpath.replaceFirst(f.getAbsolutePath(), "").split(File.separator);
								}
								edit=subnode.addPath(arrpath);
							}catch(Exception e) {
								edit=subnode.addPath(new String[] {formattedpath});
							}
							if(Files.isSymbolicLink(i.toPath())) {
								edit.val1=FILETYPE_LINK;
							}else {
								if(i.isDirectory()) {
									edit.val1=FILETYPE_DIRECTORY;
									dirmax++;
								}else {
									edit.val1=FILETYPE_FILE;
									edit.size=i.length();
								}
							}
							open.add(i.getAbsolutePath());
							x++;
							current++;
							subnode.subnodes++;
						}
				}else {
					filecount++;
					
				}
			}catch (Exception e) {
				System.out.println(fnow.getAbsolutePath());
				e.printStackTrace();
			}
		}
		//root.childs.add(subnode);
		//subnode.print();
		return subnode;
		
		
		
	}
	PrintWriter outpw=null;
	public int save(File f) {
		try {
			outpw=new PrintWriter(new BufferedWriter( new FileWriter(f)));
			print(root);
			outpw.flush();
			outpw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0;
	}
	public int load(File f) {
		try {
			BufferedReader in=new BufferedReader(new FileReader(f));
			String line;
			Node current = null;
			int currenttab=0;
			Node last = null;

			int linecount=countLines(f.getAbsolutePath());
			root.subnodes=linecount;
			int linesnow=0;
			while((line=in.readLine())!=null) {
				linesnow++;
				if(line.equals("0")) {
					current=root;
					currenttab=0;
				}else {
					if(line.startsWith("!")) {
						current.info=line.substring(1);
					}else
						if(line.startsWith("#")) {
							current.comment=line.substring(1);
						}else {
							Node newnode=new Node(line.substring(line.lastIndexOf('\t')+1));
							newnode.val1=Integer.valueOf(""+line.charAt(0));
							int tabs=line.length()-line.replaceAll("\t", "").length();
							if(tabs==currenttab) {
								newnode.parent=current.parent;
								newnode.parent.children.add(newnode);
								current=newnode;
							}

							if(tabs==currenttab+1) {
								newnode.parent=current;
								current.children.add(newnode);
								current=newnode;
							}
							if(tabs<currenttab) {
								for(int i=0;i<currenttab-tabs;i++) {
									current=current.parent;
								}
								newnode.parent=current.parent;
								newnode.parent.children.add(newnode);
								current=newnode;

							}

							if(linesnow%100000==1) {
								int percent=(int) (100.0f/linecount*linesnow);
								System.out.println(""+percent+"%\t");

							}

							
							currenttab=tabs;
						}
				}
			}


		} catch (FileNotFoundException e) {

			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -2;
		}
		return 0;

	}
	byte[] toBytes(int i) {
		return(ByteBuffer.allocate(4).putInt(i).array());
	}
	public void saveSecMode(File f) {

		/*try {
			outpw=new PrintWriter(new BufferedWriter( new FileWriter(f)));

			LinkedList<Node> open=new LinkedList<>();
			open.add(root);
			int i=1;
			while(!open.isEmpty()) {
				Node nnow=open.poll();
				nnow.addr=i;
				i++;
				for(Node c:nnow.childs) {
					open.add(c);

				}
			}
			open.add(root);
			while(!open.isEmpty()) {
				Node nnow=open.poll();
				outpw.print(nnow.addr+"\t"+nnow.name);
				for(Node c:nnow.childs) {

					outpw.print("\t"+c.addr);
					open.add(c);
				}
				outpw.println();
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		try {
			BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(f));

			LinkedList<Node> open=new LinkedList<>();
			open.add(root);
			int i=1;
			while(!open.isEmpty()) {
				Node nnow=open.poll();
				nnow.addr=i;
				i++;
				for(Node c:nnow.children) {
					open.add(c);

				}
			}
			open.add(root);
			while(!open.isEmpty()) {
				Node nnow=open.poll();
				//outpw.print(nnow.addr+"\t"+nnow.name);

				bos.write(toBytes(nnow.addr));
				bos.write(nnow.name.getBytes());
				bos.write(0);
				for(Node c:nnow.children) {

					//outpw.print("\t"+c.addr);
					bos.write(c.addr);

					open.add(c);
				}
				bos.write(toBytes(0));
				//outpw.println();
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void saveBin(File f) {
		try {
			FileByteWriter fbw= new FileByteWriter(f);
			root.writeBin(fbw);
			fbw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void loadBin(File f) {
		try {
			FileByteReader fbr= new FileByteReader(f);
			root.readBin(fbr);
			fbr.close();
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void print(Node u) {
		//Node p=u.parent;
		/*while(p!=null) {
			System.out.print(p.name+"|");
			p=p.parent;
		}*/
		outpw.print(u.val1);
		outpw.println(u.name);
		for(Node n:u.children) {
			n.parent=u;
			this.print(n,1);
		}
	}
	public void print(Node u,int level) {
		outpw.print(u.val1);
		for(int x=0;x<level;x++) {
			outpw.print("\t");
		}
		outpw.println(u.name);
		if(u.info!="")outpw.println("!"+u.info);
		if(u.comment!="")outpw.println("#"+u.comment);
		
		for(Node n:u.children) {
			n.parent=u;
			this.print(n,level+1);
		}
	}
}
