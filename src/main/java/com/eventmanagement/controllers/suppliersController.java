package com.eventmanagement.controllers;

import com.eventmanagement.database.SQLConnection;
import com.eventmanagement.entities.StockItem;
import com.eventmanagement.entities.Supplier;
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



public class suppliersController  implements Initializable {

    @FXML
    private TextField firstNameTF;

    @FXML
    private TextField lastNameTF;

    @FXML
    private TextField phoneNumberTF;

    @FXML
    private TextField addressTF;

    @FXML
    private ComboBox<String> itemCB;



    int supplierID = -1;



    //table and column
    @FXML
    private TableView<Supplier> supplierTableView;
    @FXML
    private TableColumn<Supplier, String> firstNameColumn;


    @FXML
    private TableColumn<Supplier, String> lastNameColumn;

    @FXML
    private TableColumn<Supplier, String> phoneNumberColumn;

    @FXML
    private TableColumn<Supplier, String> addressColumn;

    @FXML
    private TableColumn<Supplier, String> supplierIdColumn;

    @FXML
    private TableColumn<Supplier,String> itemColumn;

    //a tray-notification variable to show all the r
    TrayNotification tray = new TrayNotification();




    //methods to control actions with buttons on event page

    @FXML
    void onAddBtnClick(ActionEvent event) {
        if(checkFields()) {
            //declaring the query variable
            String query = null;
            //add button will record all the information of the supplier, and store it in the database
            try {
                String firstName = firstNameTF.getText();
                String lastName = lastNameTF.getText();
                String phoneNumber = phoneNumberTF.getText();
                String address = addressTF.getText();
                String item = itemCB.getValue();


                //query to enter the data in ms sql database
                query = String.format(" insert into Supplier values ('%s','%s','%s','%s',(select Stock_Item_ID from Stock_Item where Stock_Item_Name like '%s'))",
                        firstName,lastName,phoneNumber,address,item);

                //making connection with the database
                Connection connection = new SQLConnection().connection;

                //making a statement to execute the query in a try catch block
                try (Statement st = connection.createStatement()) {
                    st.execute(query);
                    //showing a tray notification upon success
                    tray.setTray("Add Supplier", "Supplier was successfully added!", NotificationType.SUCCESS);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(2));


                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    tray.setTray("Add Supplier", "There was an error in adding the supplier!", NotificationType.ERROR);
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
                    setTableWithAllSuppliers();
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
        if (supplierID != -1) {
            //confirming if the user wants to delete the record
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to delete this record?",ButtonType.YES,ButtonType.CANCEL);
            alert.setTitle("Confirmation For Delete");
            alert.showAndWait();

            //if user says yes
            if(alert.getResult() == ButtonType.YES){
                //build query to delete the supplier
                //here supplierSelected is an int variable got when we double-click a record in table
                String query = String.format("Delete from Supplier where Supplier_ID = %d",supplierID);

                //create a connection object
                Connection con = new SQLConnection().connection;

                try(Statement st = con.createStatement()){
                    st.execute(query);
                    tray.setTray("Delete Supplier","Successfully deleted the supplier!",NotificationType.SUCCESS);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(2));


                }catch (NumberFormatException e){
                    Alert a = new Alert(Alert.AlertType.WARNING, "Please fill the integer fields correctly!");
                    a.show();
                }
                catch (SQLException throwables) {
                    throwables.printStackTrace();
                    tray.setTray("Delete Supplier","There was an error while deleting the supplier!",NotificationType.ERROR);
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
                    setTableWithAllSuppliers();
                    clearAllFields();
                    supplierID = -1;
                }
            }
        }
        else{
            Alert a = new Alert(Alert.AlertType.INFORMATION,"Please double click a supplier from the table to select it and then you delete it!");
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
            Alert a = new Alert(Alert.AlertType.INFORMATION,"Please fill in the suppliers number to search!");
            a.setTitle("Search In Suppliers");
            a.show();
            phoneNumberTF.requestFocus();
            //making border red if not filled
            phoneNumberTF.getStyleClass().add("error");

        }else { //if filled

            Connection con = new SQLConnection().connection;
            String query = null;
            try (Statement st = con.createStatement()) {
                //we will build query based on user input

                query = String.format("select * from Supplier where supplier_number like '%s'",phoneNumberTF.getText());

                //set table data with the searched result
                setTableData(getObservableListForSuppliers(query));
                //clearing all the fields
                clearAllFields();

            }catch(NumberFormatException e){ //for supplier id validation because we need an integer in it
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

    //a method to update a supplier in the database
    @FXML
    void onUpdateBtnClick(ActionEvent event) {
        if (supplierID != -1) {
            if(checkFields()) {
                //get updated fields from the text fields
                String firstName = firstNameTF.getText();
                String lastName = lastNameTF.getText();
                String number = phoneNumberTF.getText();
                String address = addressTF.getText();
                String item = itemCB.getValue();

                //build an update query based on these
                String query = String.format("update Supplier set Supplier_Fname = '%s', Supplier_Lname = '%s', Supplier_Number = '%s', Supplier_Address = '%s'," +
                        " Stock_Item_ID = (select Stock_Item_ID from Stock_Item where Stock_Item_Name like '%s') where Supplier_ID = %d", firstName, lastName, number, address,item, supplierID);

                //create a connection object
                Connection con = new SQLConnection().connection;

                try (Statement st = con.createStatement()) {
                    st.execute(query);
                    tray.setTray("Update Supplier", "Successfully updated the supplier!", NotificationType.SUCCESS);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(2));


                } catch (NumberFormatException e) {
                    Alert a = new Alert(Alert.AlertType.WARNING, "Please fill the integer fields correctly!");
                    a.show();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    tray.setTray("Update Supplier", "There was an error while updating the supplier!", NotificationType.ERROR);
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
                    setTableWithAllSuppliers();
                    clearAllFields();
                    supplierID = -1;
                }
            }

        }
        else{
            Alert a = new Alert(Alert.AlertType.INFORMATION,"Please double click a supplier from the table to select it and then you can update it!");
            a.setTitle("Update");
            a.show();
        }


    }

    //a method to load the supplier information into the text fields when it is double-clicked in the table
    @FXML
    void mouseClickedTableView(javafx.scene.input.MouseEvent event) {
        supplierTableView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent mouseSupplier) {
                if(mouseSupplier.isPrimaryButtonDown() && mouseSupplier.getClickCount() == 2){
                    //clearing the fields first
                    clearAllFields();

                    //getting the selected supplier object
                    Supplier supplier = supplierTableView.getSelectionModel().getSelectedItem();

                    firstNameTF.setText(supplier.getFirstName());
                    lastNameTF.setText( supplier.getLastName());
                    phoneNumberTF.setText(supplier.getPhoneNumber());
                    addressTF.setText(supplier.getAddress());
                    supplierID = supplier.getId();
                }
            }
        });

    }





    //a method to get all the suppliers
    public ObservableList<Supplier> getObservableListForSuppliers(String query) {
        ObservableList<Supplier> suppliers = FXCollections.observableArrayList();
        String qr = query;
        SQLConnection sqlConnection = new SQLConnection();
        Connection connection = sqlConnection.connection;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(qr);
            while (resultSet.next()) {
                //making a supplier object
                //make a stock item object to get stock item name and id
                StockItem item = new StockItem();
                item.setId(resultSet.getInt("Stock_Item_ID"));
                item.setName(resultSet.getString("Stock_Item_Name"));
                Supplier supplier = new Supplier(resultSet.getInt("Supplier_ID"),resultSet.getString("Supplier_Fname"),
                        resultSet.getString("Supplier_Lname"),resultSet.getString("Supplier_Number"),resultSet.getString("Supplier_Address"),
                        item);


                //adding that object to the list
                suppliers.add(supplier);
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

        return suppliers;
    }

    //a method to load the suppliers into the table
    public void setTableData(ObservableList<Supplier> list){
        //setting the list of the table
        supplierTableView.setItems(list);
        //setting the columns with attributes
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        supplierIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("ItemName"));

    }

    //a method to clear all the fields
    public void clearAllFields(){
        removeErrorStyleFromAll();
        firstNameTF.clear();
        phoneNumberTF.clear();
        lastNameTF.clear();
        addressTF.clear();
        itemCB.getSelectionModel().clearSelection();

    }

    //a method to set the table data with all available data
    public void setTableWithAllSuppliers(){
        String query = "select Supplier_ID, Supplier_Fname, Supplier_Lname,Supplier_Number,Supplier_Address,Supplier.Stock_Item_ID,Stock_Item_Name" +
                " from Supplier inner join Stock_Item on Supplier.Stock_Item_ID = Stock_Item.Stock_Item_ID ";
        setTableData(getObservableListForSuppliers(query));
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

        if(itemCB.getSelectionModel().isEmpty()){
            allOK = false;
            if(!itemCB.getStyleClass().contains("error")){
                itemCB.getStyleClass().add("error");
            }
        }else{
            itemCB.getStyleClass().remove("error");
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
        itemCB.getStyleClass().remove("error");

    }

    //a method to set the item cb with values
    public void setItemCB(){
        //we will fetch the stock item from the database and make an observable list to show the values in combo box
        Connection con = new SQLConnection().connection;
        String query = "Select Stock_Item_Name from Stock_Item";
        ObservableList<String> items = FXCollections.observableArrayList();

        try(Statement st = con.createStatement()){
            ResultSet rs = st.executeQuery(query);
            while(rs.next()){
                items.add(rs.getString("Stock_Item_Name"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                con.close();
                System.out.println("Connection closed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        itemCB.setItems(items);

    }

    //a method that will load everything in it as soon as this file is loaded
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTableWithAllSuppliers();
        setItemCB();

    }

}

