package Client;

import Domain.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

public class CustomerDatabase {
    public CustomerDatabase(){

        Enumeration enu = null;
        Client client = new Client();
        client.sendAction("List Customers");
        Vector<Customer> cus = (Vector<Customer>) client.receiveObject();
        client.closeConnection();
        enu = cus.elements();
        Enumeration finalEnu = enu;

        JDialog dialog = new JDialog();
        JPanel panel = new JPanel();

        JLabel label = new JLabel("Customer Database");

        JButton viewCustomers = new JButton("View Customers");
        JButton registerCustomers = new JButton("Register Customer");
        JButton searchCustomer = new JButton("Search Customer");

        registerCustomers.addActionListener(e -> {
            try {
                Register reg = new Register();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            } //
        });

        viewCustomers.addActionListener(e -> {
            JDialog viewFrame = new JDialog();
            String[] columnNames ={"CUSTOMER ID","NAME","DOB","ADDRESS","Telephone","Email","Date of Membership","Date of Membership Expiry"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            while(finalEnu.hasMoreElements()){
                Vector<String> row = new Vector<String>();
                Customer customer = (Customer) finalEnu.nextElement();
                row.add(customer.getCusID());
                row.add(customer.getCusName());
                row.add(customer.getDob());
                row.add(customer.getAddress().getStreet() + " " + customer.getAddress().getTown() + " " + customer.getAddress().getParish());
                row.add(customer.getTelephone());
                row.add(customer.getEmail());
                row.add(customer.getDateOfMembership());
                row.add(customer.getDateOfMembershipExp());
                model.addRow(row);
            }
            JTable table = new JTable(model);
            table.setBounds(50, 50, 400, 400);
            JScrollPane sp=new JScrollPane(table);
            viewFrame.add(sp);
            viewFrame.setModal(true);
            viewFrame.setResizable(false);
            viewFrame.setSize(1000, 1000);
            viewFrame.setVisible(true);
            viewFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        });

        searchCustomer.addActionListener(e ->{
            String searchID = JOptionPane.showInputDialog(null, "Enter Customer ID", "ENTER INPUT", JOptionPane.QUESTION_MESSAGE);
            if(searchID != null){
                new CustomerDetails(searchID);
            }
        });

        label.setBounds(200,40,300,20);
        viewCustomers.setBounds(100,100,300,30);
        registerCustomers.setBounds(100,150,300,30);
        searchCustomer.setBounds(100,200,300,30);

        panel.setLayout(null);
        panel.setSize(500, 500);
        panel.add(label);
        panel.add(viewCustomers);
        panel.add(registerCustomers);
        panel.add(searchCustomer);

        dialog.add(panel);
        dialog.setLayout(null);
        dialog.setModal(true);
        dialog.setSize(500, 500);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        dialog.setVisible(true);
    }
}
