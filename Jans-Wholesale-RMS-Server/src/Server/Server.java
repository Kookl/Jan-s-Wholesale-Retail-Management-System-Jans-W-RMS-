package Server;

import Domain.*;
import format.Address;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.sql.*;
import java.util.Vector;

public class Server {
    private ObjectOutputStream objOs;
    private ObjectInputStream objIs;
    private ServerSocket serverSocket;
    private Socket connectionSocket;
    private static Connection dBConn = null;
    private Statement stmt;
    private ResultSet result = null;
    private ResultSet secondResult = null;

    private static Logger logger = LogManager.getLogger(Server.class);

    public Server(){
        this.createConnection();
        this.waitForRequest();
    }

    private void createConnection() {
        try {
            serverSocket = new ServerSocket(8888);
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private void waitForRequest(){

    }

    private void configureStreams(){
        try {
            objOs = new ObjectOutputStream(connectionSocket.getOutputStream());
            objIs = new ObjectInputStream(connectionSocket.getInputStream());
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private static Connection getDatabaseConnection() {
        if(dBConn == null){
            try {
                String url = "jbdc:mysql://localhost:3306/jans";
                dBConn = DriverManager.getConnection(url, "root", "admin");
                JOptionPane.showMessageDialog(null, "Database Connection Successful", "CONNECTION STATUS", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex){
                JOptionPane.showMessageDialog(null, "Could not connect to database", "CONNECTION FAILURE", JOptionPane.ERROR_MESSAGE);
            }
        }
        return dBConn;
    }

    private void closeConnection(){
        try {
            objOs.close();
            objIs.close();
            connectionSocket.close();
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    // CUSTOMER ACTIONS
    public void insertIntoCustomer(Customer cus){
        String cusUrl = "INSERT INTO customer (CustomerID,Name,DOB,Telephone,EmailAddress,DateOfMembership,MembershipExpiryDate) " +
                "VALUES ('"+cus.getCusID()+"','"+cus.getCusName()+"','"+cus.getDob()+"','"+cus.getTelephone()+"','"+cus.getEmail()+"'," +
                "'"+cus.getDateOfMembership()+"','"+cus.getDateOfMembershipExp()+"')";
        String addressUrl = "Insert into address (CustomerID,Street,Town,Parish) VALUES ('"+cus.getCusID()+"','" +
                ""+cus.getAddress().getStreet()+"','"+cus.getAddress().getTown()+"','"+cus.getAddress().getParish()+"')";
        try {
            stmt = dBConn.createStatement();

            if(stmt.executeUpdate(cusUrl) == 1 && stmt.executeUpdate(addressUrl) == 1){
                objOs.writeObject(true);
                logger.info("Customer added to the database");
            }
            else
                objOs.writeObject(false);
        } catch (IOException | SQLException ex){
            ex.printStackTrace();
        }
    }

    public void deleteCustomer(String id){
        String query = "DELETE FROM customer WHERE CustomerID = " + id;
        try {
            stmt = dBConn.createStatement();
            if(stmt.executeUpdate(query) == 1)
                objOs.writeObject(true);
            else
                objOs.writeObject(false);
        } catch (IOException | SQLException ex){
            ex.printStackTrace();
        }
    }

    public Customer findCustomerById(String id){
        Customer customer = new Customer();
        String query = "SELECT * FROM customer WHERE CustomerID = " + id;
        String addQuery = "SELECT * FROM address WHERE CustomerID = " + id;
        try{
            stmt = dBConn.createStatement();
            result = stmt.executeQuery(query);
            if (result.next()){
                customer.setCusID(result.getString("CustomerID"));
                customer.setCusName(result.getString("Name"));
                customer.setDob(result.getString("DOB"));
                customer.setTelephone(result.getString("Telephone"));
                customer.setEmail(result.getString("EmailAddress"));
                customer.setDateOfMembership(result.getString("DateOfMembership"));
                customer.setGetDateOfMembershipExp(result.getString("MembershipExpiryDate"));
            }
            result = stmt.executeQuery(addQuery);
            if (result.next()){
                customer.getAddress().setStreet(result.getString("Street"));
                customer.getAddress().setCity(result.getString("Town"));
                customer.getAddress().setParish(result.getString("Parish"));
            }
        } catch (SQLException ex){
            ex.printStackTrace();
        }
        return customer;
    }

    public Vector<Customer> showCustomers(){
        Vector<Customer> customerList = new Vector<>();
        Customer customer = new Customer();
        String query = "SELECT * FROM customer";
        String query2 = "SELECT * FROM address";
        try{
            stmt = dBConn.createStatement();
            result = stmt.executeQuery(query);
            secondResult = stmt.executeQuery(query2);
            while (result.next() && secondResult.next()){
                customer.setCusID(result.getString("CustomerID"));
                customer.setCusName(result.getString("Name"));
                customer.setDob(result.getString("DOB"));
                customer.setTelephone(result.getString("Telephone"));
                customer.setEmail(result.getString("EmailAddress"));
                customer.setDateOfMembership(result.getString("DateOfMembership"));
                customer.setGetDateOfMembershipExp(result.getString("MembershipExpiryDate"));

                customer.getAddress().setStreet(secondResult.getString("Street"));
                customer.getAddress().setCity(secondResult.getString("Town"));
                customer.getAddress().setParish(secondResult.getString("Parish"));

                customerList.add(customer);
            }
        } catch (SQLException ex){
            ex.printStackTrace();
        }
        return customerList;
    }

    // INVENTORY ACTIONS
    public void insertIntoInventory(Products pro){
        String url = "INSERT INTO inventory Values ('"+pro.getProdCode()+"','"+pro.getProdName()+"','"+pro.getProdShortDesc()+"','" +
                ""+pro.getProdLongDesc()+"','"+pro.getProdStock()+"','"+pro.getUnitPrice()+"')";
        try {
            stmt = dBConn.createStatement();

            if(stmt.executeUpdate(url) == 1){
                objOs.writeObject(true);
                logger.info("Product added to the database");
            }
            else
                objOs.writeObject(false);
        } catch (IOException | SQLException ex){
            ex.printStackTrace();
        }
    }

    public void deleteProduct(String id){
        String query = "DELETE FROM inventory WHERE ProductCode = " + id;
        try {
            stmt = dBConn.createStatement();
            if(stmt.executeUpdate(query) == 1)
                objOs.writeObject(true);
            else
                objOs.writeObject(false);
        } catch (IOException | SQLException ex){
            ex.printStackTrace();
        }
    }

    public Products findProductById(String id){
        Products product = new Products();
        String query = "SELECT * FROM inventory WHERE ProductCode = " + id;
        try{
            stmt = dBConn.createStatement();
            result = stmt.executeQuery(query);
            if (result.next()){
                product.setProdCode(result.getString("ProductCode"));
                product.setProdName(result.getString("Name"));
                product.setProdShortDesc(result.getString("ShortDesc"));
                product.setProdLongDesc(result.getString("LongDesc"));
                product.setProdStock(result.getInt("ItemsInStock"));
                product.setUnitPrice(result.getFloat("UnitPrice"));
            }
        } catch (SQLException ex){
            ex.printStackTrace();
        }
        return product;
    }

    public Vector<Products> showInventory(){
        Vector<Products> inventory = new Vector<>();
        Products product = new Products();
        String query = "SELECT * FROM inventory";
        try{
            stmt = dBConn.createStatement();
            result = stmt.executeQuery(query);
            while (result.next() && secondResult.next()){
                product.setProdCode(result.getString("ProductCode"));
                product.setProdName(result.getString("Name"));
                product.setProdShortDesc(result.getString("ShortDesc"));
                product.setProdLongDesc(result.getString("LongDesc"));
                product.setProdStock(result.getInt("ItemsInStock"));
                product.setUnitPrice(result.getFloat("UnitPrice"));

                inventory.add(product);
            }
        } catch (SQLException ex){
            ex.printStackTrace();
        }
        return inventory;
    }

    // STAFF ACTIONS
    public void insertIntoStaff(Staff staff){
        String url = "INSERT INTO Staff Values ('"+staff.getStaffID()+"','"+staff.getName()+"','"+staff.getPosition()+"','" +
                ""+staff.getDepartment()+"','"+staff.getDateOfBirth().toString()+"')";
        try {
            stmt = dBConn.createStatement();

            if(stmt.executeUpdate(url) == 1){
                objOs.writeObject(true);
                logger.info("Staff added to the database");
            }
            else
                objOs.writeObject(false);
        } catch (IOException | SQLException ex){
            ex.printStackTrace();
        }
    }

    public void deleteStaff(String id){
        String query = "DELETE FROM staff WHERE StaffID = " + id;
        try {
            stmt = dBConn.createStatement();
            if(stmt.executeUpdate(query) == 1)
                objOs.writeObject(true);
            else
                objOs.writeObject(false);
        } catch (IOException | SQLException ex){
            ex.printStackTrace();
        }
    }

    public Staff findStaffById(String id){
        Staff staff = new Staff();
        String query = "SELECT * FROM staff WHERE StaffID = " + id;
        try{
            stmt = dBConn.createStatement();
            result = stmt.executeQuery(query);
            if (result.next()){
                staff.setStaffID(result.getString("StaffID"));
                staff.setName(result.getString("Name"));
                staff.setPosition(result.getString("Position"));
                staff.setDepartment(result.getString("Department"));
                staff.setDateOfBirth(result.getString("DOB"));
            }
        } catch (SQLException ex){
            ex.printStackTrace();
        }
        return staff;
    }

    public Vector<Staff> showStaff() {
        Vector<Staff> staffList = new Vector<>();
        Staff staff = new Staff();
        String query = "SELECT * FROM staff";
        try {
            stmt = dBConn.createStatement();
            result = stmt.executeQuery(query);
            while (result.next() && secondResult.next()) {
                staff.setStaffID(result.getString("StaffID"));
                staff.setName(result.getString("Name"));
                staff.setPosition(result.getString("Position"));
                staff.setDepartment(result.getString("Department"));
                staff.setDateOfBirth(result.getString("DOB"));

                staffList.add(staff);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return staffList;
    }

    // INVOICE ACTIONS
    public void insertIntoInvoices(Invoice invoice){
        String url = "INSERT INTO Invoice Values ('" + invoice.getInvoiceNo() + "','" + invoice.getBillingDate() + "','" +
                invoice.getItem() + "','" + invoice.getQuantity() + "','" + invoice.getCashierName() + "','" + invoice.getCustomerName() + "')";
        try {
            stmt = dBConn.createStatement();

            if(stmt.executeUpdate(url) == 1){
                objOs.writeObject(true);
                logger.info("Invoice added to the database");
            }
            else
                objOs.writeObject(false);
        } catch (IOException | SQLException ex){
            ex.printStackTrace();
        }
    }

    public Invoice findInvoiceById(String id){
        Invoice invoice = new Invoice();
        String query = "SELECT * FROM invoice WHERE InvoiceNumber = " + id;
        try{
            stmt = dBConn.createStatement();
            result = stmt.executeQuery(query);
            if (result.next()){
                invoice.setInvoiceNo(result.getInt("InvoiceNumber"));
                invoice.setBillingDate(result.getString("BillingDate"));
                invoice.setItem(result.getString("Item"));
                invoice.setQuantity(result.getInt("Quantity"));
                invoice.setCashierName(result.getString("Cashier"));
                invoice.setCustomerName(result.getString("Customer"));
            }
        } catch (SQLException ex){
            ex.printStackTrace();
        }
        return invoice;
    }
}
