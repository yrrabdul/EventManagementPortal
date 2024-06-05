package com.eventmanagement.controllers;

import com.eventmanagement.database.SQLConnection;
import com.eventmanagement.entities.Customer;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.*;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;



public class customersController  implements Initializable {

    @FXML
    private TextField firstNameTF;

    @FXML
    private TextField lastNameTF;

    @FXML
    private TextField phoneNumberTF;

    @FXML
    private TextField addressTF;



    int customerID = -1;



    //table and column
    @FXML
    private TableView<Customer> customerTableView;
    @FXML
    private TableColumn<Customer, String> firstNameColumn;


    @FXML
    private TableColumn<Customer, String> lastNameColumn;

    @FXML
    private TableColumn<Customer, String> phoneNumberColumn;

    @FXML
    private TableColumn<Customer, String> addressColumn;

    @FXML
    private TableColumn<Customer, String> customerIdColumn;

    //a tray-notification variable to show all the r
    TrayNotification tray = new TrayNotification();




    //methods to control actions with buttons on event page

    @FXML
    void onAddBtnClick(ActionEvent event) {
        if(checkFields()) {
            //declaring the query variable
            String query = null;
            //add button will record all the information of the customer, and store it in the database
            try {
                String firstName = firstNameTF.getText();
                String lastName = lastNameTF.getText();
                String phoneNumber = phoneNumberTF.getText();
                String address = addressTF.getText();


                //query to enter the data in ms sql database
                query = String.format("insert into Customer(Customer_Fname,Customer_Lname,Customer_Number,Customer_Address) " +
                        "values ('%s','%s','%s','%s')", firstName, lastName, phoneNumber, address);

                //making connection with the database
                Connection connection = new SQLConnection().connection;

                //making a statement to execute the query in a try catch block
                try (Statement st = connection.createStatement()) {
                    st.execute(query);
                    //showing a tray notification upon success
                    tray.setTray("Add Customer", "Customer was successfully added!", NotificationType.SUCCESS);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(2));


                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    tray.setTray("Add Customer", "There was an error in adding the customer!", NotificationType.ERROR);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(2));

                } finally {
                    //closing the connection
                    try {
                        connection.close();
                        System.out.println("Connection closed");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    //do these finally
                    setTableWithAllCustomers();
                    clearAllFields();
                }

            } catch (NumberFormatException e) {

                Alert a = new Alert(Alert.AlertType.WARNING, "Please fill the integer fields correctly!");
                a.show();
            }
        }


    }

    //method to delete a record from database upon clicking the delete button
    @FXML
    void onDeleteBtnClick(ActionEvent event) {
        if (customerID != -1) {
            //confirming if the user wants to delete the record
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to delete this record?",ButtonType.YES,ButtonType.CANCEL);
            alert.setTitle("Confirmation For Delete");
            alert.showAndWait();

            //if user says yes
            if(alert.getResult() == ButtonType.YES){
                //build query to delete the customer
                //here customerSelected is an int variable got when we double-click a record in table
                String query = String.format("Delete from Customer where Customer_ID = %d",customerID);

                //create a connection object
                Connection con = new SQLConnection().connection;

                try(Statement st = con.createStatement()){
                    st.execute(query);
                    tray.setTray("Delete Customer","Successfully deleted the customer!",NotificationType.SUCCESS);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(2));


                }catch (NumberFormatException e){
                    Alert a = new Alert(Alert.AlertType.WARNING, "Please fill the integer fields correctly!");
                    a.show();
                }
                catch (SQLException throwables) {
                    throwables.printStackTrace();
                    tray.setTray("Delete Customer","There was an error while deleting the customer!",NotificationType.ERROR);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(2));
                }finally {
                    //closing the connection
                    try {
                        con.close();
                        System.out.println("Connection closed");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    //do these finally
                    setTableWithAllCustomers();
                    clearAllFields();
                    customerID = -1;
                }
            }
        }
        else{
            Alert a = new Alert(Alert.AlertType.INFORMATION,"Please double click a customer from the table to select it and then you delete it!");
            a.setTitle("Delete");
            a.show();
        }

    }

    @FXML
    void onSearchBtnClick(ActionEvent event) {
        //clearing all red fields if any before we proceed
        removeErrorStyleFromAll();

        //checking if the required fields are filled or not
        if(phoneNumberTF.getText().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.INFORMATION,"Please fill in the customers number to search!");
            a.setTitle("Search In Customers");
            a.show();
            phoneNumberTF.requestFocus();
            //making border red if not filled
            phoneNumberTF.getStyleClass().add("error");

        }else { //if filled

            Connection con = new SQLConnection().connection;
            String query = null;
            try (Statement st = con.createStatement()) {
                //we will build query based on user input

                query = String.format("select * from Customer where customer_number like '%s'",phoneNumberTF.getText());

                //set table data with the searched result
                setTableData(getObservableListForCustomers(query));
                //clearing all the fields
                clearAllFields();

            }catch(NumberFormatException e){ //for customer id validation because we need an integer in it
                Alert a = new Alert(Alert.AlertType.WARNING, "Please fill the integer fields correctly!");
                a.show();

            }catch (SQLException throwables) {
                throwables.printStackTrace();
            }finally {
                //closing the connection
                try {
                    con.close();
                    System.out.println("Connection closed");
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    //a method to update a customer in the database
    @FXML
    void onUpdateBtnClick(ActionEvent event) {
        if (customerID != -1) {
            if(checkFields()) {
                //get updated fields from the text fields
                String firstName = firstNameTF.getText();
                String lastName = lastNameTF.getText();
                String number = phoneNumberTF.getText();
                String address = addressTF.getText();

                //build an update query based on these
                String query = String.format("update Customer set Customer_Fname = '%s', Customer_Lname = '%s', Customer_Number = '%s', Customer_Address = '%s' " +
                        "where Customer_ID = %d", firstName, lastName, number, address, customerID);

                //create a connection object
                Connection con = new SQLConnection().connection;

                try (Statement st = con.createStatement()) {
                    st.execute(query);
                    tray.setTray("Update Customer", "Successfully updated the customer!", NotificationType.SUCCESS);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(2));


                } catch (NumberFormatException e) {
                    Alert a = new Alert(Alert.AlertType.WARNING, "Please fill the integer fields correctly!");
                    a.show();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    tray.setTray("Update Customer", "There was an error while updating the customer!", NotificationType.ERROR);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(2));
                } finally {
                    //closing the connection
                    try {
                        con.close();
                        System.out.println("Connection closed");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    //do these finally
                    setTableWithAllCustomers();
                    clearAllFields();
                    customerID = -1;
                }
            }

        }
        else{
            Alert a = new Alert(Alert.AlertType.INFORMATION,"Please double click a customer from the table to select it and then you can update it!");
            a.setTitle("Update");
            a.show();
        }


    }

    //a method to load the customer information into the text fields when it is double-clicked in the table
    @FXML
    void mouseClickedTableView(javafx.scene.input.MouseEvent event) {
        customerTableView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent mouseCustomer) {
                if(mouseCustomer.isPrimaryButtonDown() && mouseCustomer.getClickCount() == 2){
                    //clearing the fields first
                    clearAllFields();

                    //getting the selected customer object
                    Customer customer = customerTableView.getSelectionModel().getSelectedItem();

                    firstNameTF.setText(customer.getFname());
                    lastNameTF.setText( customer.getLname());
                    phoneNumberTF.setText(customer.getNumber());
                    addressTF.setText(customer.getAddress());
                    customerID = customer.getId();
                }
            }
        });

    }





    //a method to get all the customers
    public ObservableList<Customer> getObservableListForCustomers(String query) {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        String qr = query;
        SQLConnection sqlConnection = new SQLConnection();
        Connection connection = sqlConnection.connection;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(qr);
            while (resultSet.next()) {
                //making a customer object
                Customer customer = new Customer(resultSet.getInt("Customer_ID"),resultSet.getString("Customer_Fname"),
                        resultSet.getString("Customer_Lname"),resultSet.getString("Customer_Number"),resultSet.getString("Customer_Address"));


                //adding that object to the list
                customers.add(customer);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                connection.close();
                System.out.println("Connection closed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return customers;
    }

    //a method to load the customers into the table
    public void setTableData(ObservableList<Customer> list){
        //setting the list of the table
        customerTableView.setItems(list);
        //setting the columns with attributes
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("Fname"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("Lname"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

    }

    //a method to clear all the fields
    public void clearAllFields(){
        removeErrorStyleFromAll();
        firstNameTF.clear();
        phoneNumberTF.clear();
        lastNameTF.clear();
        addressTF.clear();

    }

    //a method to set the table data with all available data
    public void setTableWithAllCustomers(){
        String query = "select * from Customer";
        setTableData(getObservableListForCustomers(query));
    }

    //a method to check if the fields are filled correctly
    boolean checkFields(){
        //taking a boolean variable that checks if any field is empty or not
        boolean allOK = true;

        //checking if the field is empty or not
        if(firstNameTF.getText().isEmpty()){
            //if it is empty we will mark allOK false
            allOK = false;
            //and we will make the border of that field red to indicate which field is missing
            //this method actually uses a property of our css file and add it to the field styleClass
            if(!firstNameTF.getStyleClass().contains("error")){
                firstNameTF.getStyleClass().add("error");
            }
        }else{
            //if the field is not empty we will remove the error property
            firstNameTF.getStyleClass().remove("error");
        }

        //do above step for rest of the fields
        if(lastNameTF.getText().isEmpty()){
            allOK = false;
            if(!lastNameTF.getStyleClass().contains("error")){
                lastNameTF.getStyleClass().add("error");
            }
        }else{
            lastNameTF.getStyleClass().remove("error");
        }

        if(phoneNumberTF.getText().isEmpty()){
            allOK = false;
            if(!phoneNumberTF.getStyleClass().contains("error")){
                phoneNumberTF.getStyleClass().add("error");
            }
        }else{
            phoneNumberTF.getStyleClass().remove("error");
        }

        if(addressTF.getText().isEmpty()){
            allOK = false;
            if(!addressTF.getStyleClass().contains("error")){
                addressTF.getStyleClass().add("error");
            }
        }else{
            addressTF.getStyleClass().remove("error");
        }

        //if any one of the field remains empty we will show a warning
        if(!allOK){
            Alert a = new Alert(Alert.AlertType.WARNING, "Please fill in all the fields!");
            a.show();
        }

        return allOK;
    }

    //a method to remove the red border from all the fields
     public void removeErrorStyleFromAll(){
        firstNameTF.getStyleClass().remove("error");
        lastNameTF.getStyleClass().remove("error");
        phoneNumberTF.getStyleClass().remove("error");
        addressTF.getStyleClass().remove("error");
     }


    //a method that will load everything in it as soon as this file is loaded
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTableWithAllCustomers();

    }

}

