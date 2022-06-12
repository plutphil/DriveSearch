package com.example.DriveSearch;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.ProgressMonitor;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.example.DriveSearch.icons.IcoLoad;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.JTextPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JProgressBar;

public class Main extends JFrame {
	JComboBox comboBox = new JComboBox();
	JTree tree = new JTree();
	TreeIO tt = new TreeIO();
	JList<Node> list = new JList<Node>();
	ProgressMonitor progmon;
	Thread ccnth;
	DefaultMutableTreeNode root;
	JProgressBar progressBar;
	Thread sbupdater=null;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	private void readDrives() {
		DefaultComboBoxModel<Node> model = new DefaultComboBoxModel<>();
		System.out.println("Running on os:"+System.getProperty("os.name"));
		if(System.getProperty("os.name").equalsIgnoreCase("Linux")) {
			System.out.println("Running on Linux");
			ArrayList<File> paths = new ArrayList<>();
			File media = new File("/media/"+System.getProperty("user.name")+"");
			if(media.exists()) {
				paths.addAll(Arrays.asList(media.listFiles()));

			}
			File home = new File("/home"+"");
			if(home.exists()) {
				paths.addAll(Arrays.asList(home.listFiles()));
			}
			for(File f:paths) {
				model.addElement(new Node(f.getAbsolutePath()));
			}
			
		}
		
		if(System.getProperty("os.name").startsWith("Windows")) {
			ArrayList<File> paths = new ArrayList<>();

			//paths.addAll(Arrays.asList(File.listRoots()));
			FileSystemView fsv = FileSystemView.getFileSystemView();
			for(File path:File.listRoots())
			{
				Node test=new Node();
			    // prints file and directory paths
			    System.out.println("Drive Name: "+path);
			    System.out.println("Description: "+fsv.getSystemDisplayName(path));
			    test.name=path.getAbsolutePath();
			    test.info=fsv.getSystemDisplayName(path);
			    model.addElement(test);
			}
			
		}
		//for(Entry<Object, Object> p:System.getProperties().entrySet())
		//System.out.println(p);
		comboBox.setModel(model);
	}
	public class CreateChildNodes implements Runnable {

		private DefaultMutableTreeNode root;



		public CreateChildNodes(DefaultMutableTreeNode root) {

			this.root = root;
		}

		@Override
		public void run() {

			createChildren(tt.root, root);
			tree.expandRow(0);
			//expandAllNodes(tree, 0, tree.getRowCount());
			///System.out.println(tree.getModel().getRoot());


		}

		private void createChildren(Node file, DefaultMutableTreeNode node) {
			if (file.children.isEmpty()) return;

			for (Node n : file.children) {
				DefaultMutableTreeNode childNode = new FileNode(n);
				node.add(childNode);
				if (!n.children.isEmpty()) {
					createChildren(n, childNode);
				}
			}

		}

	}
	public void setTree(Node treeroot) {
		root=new DefaultMutableTreeNode(new FileNode(treeroot));

		tree.setModel(new DefaultTreeModel(root));
		CreateChildNodes ccn = new CreateChildNodes( root);
		if(ccnth!=null)if(ccnth.isAlive()) {
			ccnth.interrupt();

		}
		ccnth=new Thread(ccn);
		ccnth.start();
	}
	public class FileNode extends DefaultMutableTreeNode{

		public Node file;

		public FileNode(Node file) {
			super();
			this.file = file;
		}

		@Override
		public String toString() {

			return file.toString();

		}
	}
	private void expandAllNodes(JTree tree, int startingIndex, int rowCount){
		for(int i=startingIndex;i<rowCount;++i){
			tree.expandRow(i);
		}

		if(tree.getRowCount()!=rowCount){
			expandAllNodes(tree, rowCount, tree.getRowCount());
		}
	}
	
	private void collapseAllNodes(JTree tree, int startingIndex, int rowCount,boolean close) {
		if(close) {
			for(int i=startingIndex;i<rowCount;++i){
				tree.collapseRow(i);
			}
		}
		if(tree.getRowCount()!=rowCount){
			collapseAllNodes(tree, rowCount, tree.getRowCount(),true);
		}
	}
	/**
	 * Create the frame.
	 */
	ChangesWindow cw;
	public Main() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 500);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc = new JFileChooser();
				jfc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				jfc.setFileFilter(new FileNameExtensionFilter("Tree", "tree","tre"));
				jfc.setSelectedFile(new File("new.tree"));
				jfc.showSaveDialog(contentPane);
				tt.save(jfc.getSelectedFile());
			}
		});
		mnFile.add(mntmSave);

		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc = new JFileChooser();
				jfc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				jfc.setFileFilter(new FileNameExtensionFilter("Tree", "tree","tre"));
				//jfc.setSelectedFile(new File("new.tree"));
				jfc.showOpenDialog(contentPane);
				tt.root.children.clear();
				tt.load(jfc.getSelectedFile());
				
				setTree(tt.root);
			}
		});
		mnFile.add(mntmOpen);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JLabel lblNewLabel = new JLabel("Drive");



		readDrives();

		JButton btnRefresh = new JButton("refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				readDrives();
			}
		});
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});




		JButton btnExpandAll = new JButton("expand all");

		JLabel lblSearch = new JLabel("Search");
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				DefaultComboBoxModel<String> cbmodel=new DefaultComboBoxModel<>();
				cbmodel.addElement((String)comboBox_1.getSelectedItem());
				for(int i=0;i<comboBox_1.getModel().getSize();i++)
					if(!((String)comboBox_1.getModel().getElementAt(i)).equalsIgnoreCase((String)comboBox_1.getSelectedItem()))
						cbmodel.addElement((String)comboBox_1.getModel().getElementAt(i));
				comboBox_1.setModel(cbmodel);
				NodeSearcher ns=new NodeSearcher();
				NodeSearcher nsrp= new NodeSearcherRepl();
				if(sbupdater!=null) {
					if(sbupdater.isAlive()) {
				
						return;
					}
				}
				ns.start(tt.root, (String)comboBox_1.getSelectedItem());
				nsrp.start(tt.root, (String)comboBox_1.getSelectedItem());
				sbupdater=new Thread(new Runnable() {
					@Override
					public void run() {
						DefaultListModel<Node> model= new DefaultListModel<Node>();
						int procmax=ns.procmax+nsrp.procmax;
						progressBar.setMaximum(procmax);
						System.out.println("Search started:"+ns.request);
						while(!ns.isFinished()) {
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							int proc=ns.getProcess()+nsrp.getProcess();
							progressBar.setValue(proc);
							String meta=(100.0/procmax*proc)+"%";
							progressBar.setString(meta);
							System.out.println("Search:"+meta);
							model.clear();
							for(Node n:ns.getResults()) {
								model.addElement(n);
							}
							for(Node n:nsrp.getResults()) {
								model.addElement(n);
							}
							list.setModel(model);
						}
						
						
						progressBar.setString("100%");
						System.out.println("Search finished:"+ns.request);
					}
				});
				sbupdater.start();
				//System.out.println("new search");
				
				
				
			}
		});
		comboBox_1.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {

			}

		});


		comboBox_1.setEditable(true);
		
		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {

					@Override
					public void run() {

						comboBox.setEnabled(false);
						btnLoad.setEnabled(false);
						btnRefresh.setEnabled(false);
						Node fn=(Node)comboBox.getSelectedItem();
						int ret=0;
						Node found;
						if((found=tt.root.getr(fn.name))!=null) {
							ret=JOptionPane.showOptionDialog(
									Main.this,
									"you have already loaded"+fn.name+"\n decide create new node or integrate with existing node",
									"Choose wisely!",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.WARNING_MESSAGE, 
									null,
									new String[]{"new one","integrate","cancel"}, 
									0);
							
						}
						if(ret==0) {
							Thread th = new Thread(new Runnable() {
								
								@Override
								public void run() {
									
										tt.read(new File(fn.name),fn.info);
									
								}
							});
							th.start();
							int vmax=10000;
							progressBar.setMaximum(vmax);
							while(th.isAlive()) {
								int val=(int) (tt.dircount*vmax/tt.dirmax);
								progressBar.setValue(val);
								progressBar.setString(""+(val/100.0f));
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								
							}
							progressBar.setString("done");
							progressBar.setMaximum(1);
							progressBar.setValue(1);
							setTree(tt.root);
							//tt.save(new File("bin.tree"));
							tt.saveBin(new File("tree.bin"));
							//tt.saveSecMode(new File("bin.tre1"));
							btnLoad.setEnabled(true);
							comboBox.setEnabled(true);
							btnRefresh.setEnabled(true);
						}else
						if(ret==1) {
							Thread th = new Thread(new Runnable() {
								
								@Override
								public void run() {
									
										Node subnode=tt.readToNode(new File(fn.name),fn.info);
										
										EventQueue.invokeLater(new Runnable() {
											public void run() {
												Main.this.cw= new ChangesWindow();
												Main.this.cw.setVisible(true);
											}
										});
										
										String type="";
										for(NodeComparison n:subnode.compare(found)) {
											System.out.println("added:"+n.a.getParentPath());
											type="";
											switch (n.comptype) {
											case CMPT_NOTFOUND:
												type="+";
												break;
											case CMPT_SIZECHANGED:
												if(n.a.size<n.b.size) {
													type="<";
												}else {
													type=">";
												}
												break;

											default:
												break;
											}
											Main.this.cw.dtm.addRow(new String[] {type,n.a.name,""+n.a.size});
											
										}
										for(NodeComparison n:found.compare(subnode)) {
											type="";
											switch (n.comptype) {
											case CMPT_NOTFOUND:
												type="+";
												break;
											case CMPT_SIZECHANGED:
												if(n.a.size<n.b.size) {
													type="<";
												}else {
													type=">";
												}
												break;

											default:
												break;
											}
											System.out.println("deleted:"+n.a.getParentPath());
											Main.this.cw.dtm.addRow(new String[] {type,n.a.name,""+n.a.size});
										}
										System.out.println("done");
									
								}
							});
							th.start();
							int vmax=10000;
							progressBar.setMaximum(vmax);
							while(th.isAlive()) {
								int val=(int) (tt.dircount*vmax/tt.dirmax);
								progressBar.setValue(val);
								progressBar.setString(""+(val/100.0f));
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								
							}
							progressBar.setString("done");
							progressBar.setMaximum(1);
							progressBar.setValue(1);
							setTree(tt.root);
							tt.save(new File("bin.tree"));
							//tt.saveSecMode(new File("bin.tre1"));
							btnLoad.setEnabled(true);
							comboBox.setEnabled(true);
							btnRefresh.setEnabled(true);
						}
					}
				}).start();
			}
		});

		JSplitPane splitPane = new JSplitPane();

		JLabel lblLabel_Path = new JLabel("");
		lblLabel_Path.setHorizontalAlignment(SwingConstants.LEFT);
		lblLabel_Path.setVerticalAlignment(SwingConstants.TOP);
		//lblLabel_Path.setIcon(new ImageIcon(Main.class.getResource("/com/sun/java/swing/plaf/gtk/icons/File.gif")));

		JTextPane textPane = new JTextPane();
		textPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				textPane.setSelectionStart(0);
				textPane.setSelectionEnd(textPane.getText().length());
			}
		});
		textPane.setEditable(false);

		JButton btnOpen = new JButton("Open");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String path = "";
				Node firstnode = null;
				for(int i=1;i<tree.getSelectionPath().getPathCount();i++) {
					FileNode fn=(FileNode) tree.getSelectionPath().getPathComponent(i);
					if(i==1)firstnode=fn.file;
					path+=fn.file.name+File.separator;
					//System.out.println("-"+tree.getSelectionPath().getPathComponent(i).toString()+"-");
				}
				path=path.replaceAll("\\\\\\\\", "\\\\");
				if(path.endsWith("\\"))path=path.substring(0, path.length()-1);
				if(path.endsWith("/"))path=path.substring(0, path.length()-1);
				if(new File(path).exists()) {
					System.out.println(path+"exist");
					try {
						Desktop.getDesktop().open(new File(path));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else {
					if(tt.windows) {
						FileSystemView fsv = FileSystemView.getFileSystemView();
						for(File f:File.listRoots()){
							if(fsv.getSystemDisplayName(f)==firstnode.info) {
								path=path.replaceAll(firstnode.name, "");
								path=f.getAbsolutePath()+path;
							}
						}
						System.out.println(path);
						for(char c='A';c<='Z';c++) {
							
						}
					}
					System.out.println(path+" does not exist");
				}
			}
		});

		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(JOptionPane.OK_OPTION==
						JOptionPane.showConfirmDialog(
								Main.this, "Confirm deleting "+tree.getSelectionPath().getLastPathComponent()+"?")) {
				TreePath path=tree.getSelectionPath();
				Node now=tt.root;
				System.out.println(path);
				for(int i=1;i<path.getPathCount();i++) {
					now=now.get(((FileNode)path.getPathComponent(i)).file.name);
				}
				
				String name=now.name;
				System.out.println(name);
				if(now.parent!=null) {

					now.parent.delete(name);
				}else {
					tt.root.delete(name);
				}
				//tt.save(new File("bin.tree"));
				tt.saveBin(new File("tree.bin"));
				setTree(tt.root);
				}
			}
		});
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		
		JButton btnCollapseall = new JButton("collapseall");
		btnCollapseall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				collapseAllNodes(tree, 0, tree.getRowCount(),false);
			}
		});

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblSearch)
						.addComponent(lblNewLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(comboBox, 0, 558, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnLoad)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnRefresh, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnDelete))))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblLabel_Path, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
						.addComponent(textPane, GroupLayout.PREFERRED_SIZE, 263, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 258, Short.MAX_VALUE)
					.addComponent(btnOpen)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnCollapseall)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnExpandAll))
				.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 784, Short.MAX_VALUE)
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnLoad)
						.addComponent(btnRefresh)
						.addComponent(lblNewLabel)
						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblSearch)
							.addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnDelete, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnExpandAll, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnOpen)
							.addComponent(btnCollapseall))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblLabel_Path)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
		);

		JPanel panel = new JPanel();
		splitPane.setLeftComponent(panel);

		JScrollPane scrollPane_1 = new JScrollPane();
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if(!list.isSelectionEmpty()) {
					textPane.setText(list.getSelectedValue().val1+""+list.getSelectedValue().getParentPath());

				Node nnow=list.getSelectedValue();
				LinkedList<Node> pathlist=new LinkedList<>();
				pathlist.add(nnow);
				while((nnow=nnow.parent)!=null) {
					pathlist.push(nnow);
				}
				ArrayList<DefaultMutableTreeNode> fnpath=new ArrayList<>();
				DefaultMutableTreeNode fnnow=(DefaultMutableTreeNode) tree.getModel().getRoot();
				fnpath.add(fnnow);
				for(Node n:pathlist) {
					for(int i=0;i<fnnow.getChildCount();i++) {
						FileNode fn=(FileNode) fnnow.getChildAt(i);
						if(fn.file.name==n.name) {
							fnpath.add(fn);
							fnnow.remove(i);
							fnnow.insert(fn, 0);
							fnnow=fn;
							
							i=fnnow.getChildCount();
						}
					}
				}

				tree.setSelectionPath(new TreePath(fnpath.toArray()));
				}
			}
		});


		scrollPane_1.setViewportView(list);

		JLabel lblSearchresults = new JLabel("SearchResults");
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
				gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
						.addComponent(lblSearchresults)
						.addContainerGap(17, Short.MAX_VALUE))
				.addComponent(scrollPane_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
				);
		gl_panel.setVerticalGroup(
				gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
						.addGap(6)
						.addComponent(lblSearchresults)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE))
				);
		panel.setLayout(gl_panel);

		JPanel panel_1 = new JPanel();
		splitPane.setRightComponent(panel_1);


		JScrollPane scrollPane = new JScrollPane();


		scrollPane.setViewportView(tree);

		JLabel lblFiletree = new JLabel("FileTree");
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
				gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
						.addComponent(lblFiletree)
						.addContainerGap())
				.addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
				);
		gl_panel_1.setVerticalGroup(
				gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
						.addGap(6)
						.addComponent(lblFiletree)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE))
				);
		panel_1.setLayout(gl_panel_1);

		btnExpandAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				expandAllNodes(tree, 0, tree.getRowCount());
			}
		});
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				String path = "";
				if(tree.getSelectionPath()!=null) {
					for(int i=1;i<tree.getSelectionPath().getPathCount();i++) {
						FileNode n=(FileNode) tree.getSelectionPath().getPathComponent(i);
						path+=n.file.name+File.separator;
						//System.out.println("-"+tree.getSelectionPath().getPathComponent(i).toString()+"-");
					}
					path=path.replaceAll("\\\\\\\\", "\\\\");
					if(path.endsWith("\\"))path=path.substring(0, path.length()-1);
					String ext=path.substring(path.lastIndexOf('.')+1);
					if(ext.length()<4) {
						lblLabel_Path.setIcon(IcoLoad.getIcon(ext));
					}else {
						lblLabel_Path.setIcon(IcoLoad.getIcon("_blank"));
					}
					
					textPane.setText(path);
				}else {

					textPane.setText("");
				}
			}
		});
		tree.setCellRenderer(new DefaultTreeCellRenderer() {

			@Override
			public Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean selected, boolean expanded,
					boolean isLeaf, int row, boolean focused) {

				Component c = super.getTreeCellRendererComponent(tree, value,
						selected, expanded, isLeaf, row, focused);
				this.setIcon(IcoLoad.getIcon("_blank"));
				if(value instanceof FileNode) {
					FileNode test=(FileNode)value;
					String path=test.toString();
					int pi=path.lastIndexOf('.');
					if(pi!=-1) {String ext=path.substring(pi);
						
						ImageIcon ii=IcoLoad.getIcon(ext);
						if(ii!=null)this.setIcon(ii);
					}

					
				}
				
				return c;
			}
		});
		//File fileRoot=new File("/home/philipp/Dokumente");
		//tt.load(new File("bin.tree"));
		tt.loadBin(new File("tree.bin"));
		//tt.read(fileRoot);

		setTree(tt.root);


		contentPane.setLayout(gl_contentPane);
	}
}
