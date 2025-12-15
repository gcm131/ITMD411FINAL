package javaapplication1;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class Tickets extends JFrame implements ActionListener {

	// class level member objects
	Dao dao = new Dao(); // for CRUD operations
	Boolean chkIfAdmin = null;

	// Main menu object items
	private JMenu mnuFile = new JMenu("File");
	private JMenu mnuAdmin = new JMenu("Admin");
	private JMenu mnuTickets = new JMenu("Tickets");

	// Sub menu item objects for all Main menu item objects
	JMenuItem mnuItemExit;
	JMenuItem mnuItemUpdate;
	JMenuItem mnuItemDelete;
	JMenuItem mnuItemOpenTicket;
	JMenuItem mnuItemViewTicket;
	JMenuItem mnuItemCloseTicket;

	public Tickets(Boolean isAdmin) {

		chkIfAdmin = isAdmin;
		createMenu();
		prepareGUI();

	}

	private void createMenu() {

		/* Initialize sub menu items **************************************/

		// initialize sub menu item for File main menu
		mnuItemExit = new JMenuItem("Exit");
		// add to File main menu item
		mnuFile.add(mnuItemExit);

		// initialize first sub menu items for Admin main menu
		mnuItemUpdate = new JMenuItem("Update Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemUpdate);

		// initialize second sub menu items for Admin main menu
		mnuItemDelete = new JMenuItem("Delete Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemDelete);
		
		// initialize third sub menu items for Admin main menu
		mnuItemCloseTicket = new JMenuItem("Close Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemCloseTicket);

		// initialize first sub menu item for Tickets main menu
		mnuItemOpenTicket = new JMenuItem("Open Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemOpenTicket);

		// initialize second sub menu item for Tickets main menu
		mnuItemViewTicket = new JMenuItem("View Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemViewTicket);

		// initialize any more desired sub menu items below

		/* Add action listeners for each desired menu item *************/
		mnuItemExit.addActionListener(this);
		mnuItemUpdate.addActionListener(this);
		mnuItemDelete.addActionListener(this);
		mnuItemOpenTicket.addActionListener(this);
		mnuItemViewTicket.addActionListener(this);
		mnuItemCloseTicket.addActionListener(this);

		 /*
		  * continue implementing any other desired sub menu items (like 
		  * for update and delete sub menus for example) with similar 
		  * syntax & logic as shown above
		 */

 
	}

	private void prepareGUI() {

		// create JMenu bar
		JMenuBar bar = new JMenuBar();
		bar.add(mnuFile); // add main menu items in order, to JMenuBar
		bar.add(mnuAdmin);
		bar.add(mnuTickets);
		// add menu bar components to frame
		setJMenuBar(bar);

		addWindowListener(new WindowAdapter() {
			// define a window close operation
			public void windowClosing(WindowEvent wE) {
				System.exit(0);
			}
		});
		// set frame options
		setSize(400, 400);
		getContentPane().setBackground(Color.LIGHT_GRAY);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	// helper method to refresh table view
	private void refreshTicketsTable() {
	    try {
	        JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords()));
	        jt.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	        jt.setFillsViewportHeight(true);

	        JScrollPane sp = new JScrollPane(jt);
	        sp.setBounds(20, 50, 350, 250);

	        getContentPane().removeAll();   
	        getContentPane().add(sp);       
	        revalidate();                   
	        repaint();                      
	    } catch (SQLException e1) {
	        JOptionPane.showMessageDialog(null, "Error refreshing tickets: " + e1.getMessage());
	    }
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// implement actions for sub menu items
		if (e.getSource() == mnuItemExit) {
			System.exit(0);
		} else if (e.getSource() == mnuItemOpenTicket) {

			// get ticket information
			String ticketName = JOptionPane.showInputDialog(null, "Enter your name");
			String ticketDesc = JOptionPane.showInputDialog(null, "Enter a ticket description");

			// insert ticket information to database

			int id = dao.insertRecords(ticketName, ticketDesc);

			// display results if successful or not to console / dialog box
			if (id != 0) {
				System.out.println("Ticket ID : " + id + " created successfully!!!");
				JOptionPane.showMessageDialog(null, "Ticket id: " + id + " created");
				refreshTicketsTable();
			} else
				System.out.println("Ticket cannot be created!!!");
		}

		else if (e.getSource() == mnuItemViewTicket) {
		    // retrieve all tickets details for viewing in JTable
		    try {
		        JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords()));
		        jt.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		        jt.setFillsViewportHeight(true);

		        JScrollPane sp = new JScrollPane(jt);
		        sp.setBounds(20, 50, 350, 250);

		        getContentPane().removeAll();   
		        getContentPane().add(sp);       
		        revalidate();                   
		        repaint();                      

		    } catch (SQLException e1) {
		        e1.printStackTrace();
		        JOptionPane.showMessageDialog(null, "Error retrieving tickets: " + e1.getMessage());
		    }
		}

		else if (e.getSource() == mnuItemUpdate) {
		    if (chkIfAdmin) {
		        String idStr = JOptionPane.showInputDialog(null, "Enter Ticket ID to update");
		        String newDesc = JOptionPane.showInputDialog(null, "Enter new description");
		        try {
		            dao.updateRecord(Integer.parseInt(idStr), newDesc);
		            JOptionPane.showMessageDialog(null, "Ticket updated successfully!");
		            refreshTicketsTable();
		        } catch (Exception ex) {
		            JOptionPane.showMessageDialog(null, "Error updating ticket: " + ex.getMessage());
		        }
		    } else {
		        JOptionPane.showMessageDialog(null, "Access denied. Admins only.");
		    }
		}

		else if (e.getSource() == mnuItemDelete) {
		    if (chkIfAdmin) {
		        String idStr = JOptionPane.showInputDialog(this, "Enter Ticket ID to delete");
		        if (idStr != null && !idStr.trim().isEmpty()) {
		            int ticketId = Integer.parseInt(idStr);

		            // confirmation dialog showing the ticket number
		            int confirm = JOptionPane.showConfirmDialog(
		                this,
		                "Are you sure you want to delete Ticket #" + ticketId + "?",
		                "Confirm Delete",
		                JOptionPane.YES_NO_OPTION
		            );

		            if (confirm == JOptionPane.YES_OPTION) {
		                try {
		                    dao.deleteRecord(ticketId);
		                    JOptionPane.showMessageDialog(this, "Ticket #" + ticketId + " deleted successfully!");
		                    refreshTicketsTable(); // refresh view after delete
		                } catch (Exception ex) {
		                    JOptionPane.showMessageDialog(this, "Error deleting ticket: " + ex.getMessage());
		                }
		            } else {
		                JOptionPane.showMessageDialog(this, "Delete cancelled for Ticket #" + ticketId);
		            }
		        }
		    } else {
		        JOptionPane.showMessageDialog(this, "Access denied. Admins only.");
		    }
		}

		
		else if (e.getSource() == mnuItemCloseTicket) {
			if (chkIfAdmin) {
		    String idStr = JOptionPane.showInputDialog(null, "Enter Ticket ID to close");
		    dao.closeTicket(Integer.parseInt(idStr));
		    JOptionPane.showMessageDialog(null, "Ticket closed!");
		    refreshTicketsTable();
			} else {
		        JOptionPane.showMessageDialog(null, "Access denied. Admins only.");
		    }
		}


	}
	


}
