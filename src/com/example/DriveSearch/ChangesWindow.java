package com.example.DriveSearch;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ChangesWindow extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	
	DefaultTableModel dtm;
	private JTable table;
	/**
	 * Create the frame.
	 */
	public ChangesWindow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		table = new JTable();
		dtm=new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"#", "Size", "Name"
				}
			);
		
		table.setModel(dtm);
		
		
		JScrollPane jScrollPane;
		jScrollPane=new JScrollPane();
		contentPane.add(jScrollPane, BorderLayout.CENTER);
		
		jScrollPane.setViewportView(table);
		
		
	}

}
