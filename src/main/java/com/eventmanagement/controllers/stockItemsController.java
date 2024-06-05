package com.eventmanagement.controllers;

import com.eventmanagement.database.SQLConnection;
import com.eventmanagement.entities.StockItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.*;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;



public class stockItemsController  implements Initializable {

    @FXML
    private TextField itemNameTF;

    @FXML
    private TextField itemDetailTF;

    @FXML
    private TextField itemQuantityTF;

    @FXML
    private TextField unitPriceTF;


    


    int itemID = -1;



    //table and column
    @FXML
    private TableView<StockItem> itemTableView;
    @FXML
    private TableColumn<StockItem, String> itemNameColumn;


    @FXML
    private TableColumn<StockItem, String> itemDetailColumn;

    @FXML
    private TableColumn<StockItem, Integer> itemQuantityColumn;


    @FXML
    private TableColumn<StockItem, Integer> itemIdColumn;

    @FXML
    private TableColumn<StockItem,Integer> unitPriceColumn;

    //a tray-notification variable to show all the r
    TrayNotification tray = new TrayNotification();






    @FXML
    void onAddBtnClick(ActionEvent event) {
        if(checkFields()) {
            //declaring the query variable
            String query = null;
            //add button will record all the information of the item, and store it in the database
            try {
                String itemName = itemNameTF.getText();
                String itemDetail = itemDetailTF.getText();
                int itemQuantity = Integer.parseInt(itemQuantityTF.getText());
                int unitPrice = Integer.parseInt(unitPriceTF.getText());



                //query to enter the data in ms sql database
                query = String.format("Insert into Stock_Item values ('%s','%s',%d,%d)",itemName,itemDetail,itemQuantity,unitPrice);

                //making connection with the database
                Connection connection = new SQLConnection().connection;

                //making a statement to execute the query in a try catch block
                try (Statement st = connection.createStatement()) {
                    st.execute(query);
                    //showing a tray notification upon success
                    tray.setTray("Add Item", "Item was successfully added!", NotificationType.SUCCESS);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(2));


                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    tray.setTray("Add Item", "There was an error in adding the item!", NotificationType.ERROR);
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
                    setTableWithAllItems();
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
        if (itemID != -1) {
            //confirming if the user wants to delete the record
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to delete this record?",ButtonType.YES,ButtonType.CANCEL);
            alert.setTitle("Confirmation For Delete");
            alert.showAndWait();

            //if user says yes
            if(alert.getResult() == ButtonType.YES){
                //build query to delete the item
                //here itemSelected is an int variable got when we double-click a record in table
                String query = String.format("Delete from Stock_Item where Stock_Item_ID = %d",itemID);

                //create a connection object
                Connection con = new SQLConnection().connection;

                try(Statement st = con.createStatement()){
                    st.execute(query);
                    tray.setTray("Delete Item","Successfully deleted the item!",NotificationType.SUCCESS);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(2));


                }catch (NumberFormatException e){
                    Alert a = new Alert(Alert.AlertType.WARNING, "Please fill the integer fields correctly!");
                    a.show();
                }
                catch (SQLException throwables) {
                    throwables.printStackTrace();
                    tray.setTray("Delete Item","There was an error while deleting the item!",NotificationType.ERROR);
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
                    setTableWithAllItems();
                    clearAllFields();
                    itemID = -1;
                }
            }
        }
        else{
            Alert a = new Alert(Alert.AlertType.INFORMATION,"Please double click an item from the table to select it and then you delete it!");
            a.setTitle("Delete");
            a.show();
        }

    }

    @FXML
    void onSearchBtnClick(ActionEvent event) {
        //clearing all red fields if any before we proceed
        removeErrorStyleFromAll();

        //checking if the required fields are filled or not
        if(itemNameTF.getText().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.INFORMATION,"Please fill in the item's name to search!");
            a.setTitle("Search In Items");
            a.show();
            itemNameTF.requestFocus();
            //making border red if not filled
            itemNameTF.getStyleClass().add("error");

        }else { //if filled

            Connection con = new SQLConnection().connection;
            String query = null;
            try (Statement st = con.createStatement()) {
                //we will build query based on user input

                query = String.format("select * from Stock_Item where Stock_Item_Name like '%s'", itemNameTF.getText());

                //set table data with the searched result
                setTableData(getObservableListForItems(query));
                //clearing all the fields
                clearAllFields();

            }catch(NumberFormatException e){ //for item id validation because we need an integer in it
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

    //a method to update a item in the database
    @FXML
    void onUpdateBtnClick(ActionEvent event) {
        if (itemID != -1) {
            if(checkFields()) {
                //get updated fields from the text fields
                String itemName = itemNameTF.getText();
                String itemDetail = itemDetailTF.getText();
                int itemQuantity = Integer.parseInt(itemQuantityTF.getText());
                int unitPrice = Integer.parseInt(unitPriceTF.getText());


                //build an update query based on these
                String query = String.format("update Stock_Item set Stock_Item_Name = '%s', Stock_Item_Detail = '%s', Stock_Item_Quantity = %d, " +
                                "Stock_Item_Unit_Price = %d where Stock_Item_ID = %d",itemName,itemDetail,itemQuantity,unitPrice,itemID);

                //create a connection object
                Connection con = new SQLConnection().connection;

                try (Statement st = con.createStatement()) {
                    st.execute(query);
                    tray.setTray("Update Item", "Successfully updated the item!", NotificationType.SUCCESS);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(2));


                } catch (NumberFormatException e) {
                    Alert a = new Alert(Alert.AlertType.WARNING, "Please fill the integer fields correctly!");
                    a.show();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    tray.setTray("Update Item", "There was an error while updating the item!", NotificationType.ERROR);
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
                    setTableWithAllItems();
                    clearAllFields();
                    itemID = -1;
                }
            }

        }
        else{
            Alert a = new Alert(Alert.AlertType.INFORMATION,"Please double click a item from the table to select it and then you can update it!");
            a.setTitle("Update");
            a.show();
        }


    }

    //a method to load the item information into the text fields when it is double-clicked in the table
    @FXML
    void mouseClickedTableView(javafx.scene.input.MouseEvent event) {
        itemTableView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent mouseItem) {
                if(mouseItem.isPrimaryButtonDown() && mouseItem.getClickCount() == 2){
                    //clearing the fields first
                    clearAllFields();

                    //getting the selected item object
                    StockItem item = itemTableView.getSelectionModel().getSelectedItem();

                    itemNameTF.setText(item.getName());
                    itemDetailTF.setText( item.getDetail());
                    itemQuantityTF.setText(String.valueOf(item.getQuantity()));
                    unitPriceTF.setText(String.valueOf(item.getUnitPrice()));
                    itemID = item.getId();
                }
            }
        });

    }





    //a method to get all the items
    public ObservableList<StockItem> getObservableListForItems(String query) {
        ObservableList<StockItem> items = FXCollections.observableArrayList();
        String qr = query;
        SQLConnection sqlConnection = new SQLConnection();
        Connection connection = sqlConnection.connection;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(qr);
            while (resultSet.next()) {
                //making a item object
                StockItem item = new StockItem(resultSet.getInt("Stock_Item_ID"),resultSet.getString("Stock_Item_Name"),
                        resultSet.getString("Stock_Item_Detail"),resultSet.getInt("Stock_Item_Quantity"),resultSet.getInt("Stock_Item_Unit_Price"));

                //adding that object to the list
                items.add(item);
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

        return items;
    }

    //a method to load the items into the table
    public void setTableData(ObservableList<StockItem> list){
        //setting the list of the table
        itemTableView.setItems(list);
        //setting the columns with attributes
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        itemDetailColumn.setCellValueFactory(new PropertyValueFactory<>("detail"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

    }

    //a method to clear all the fields
    public void clearAllFields(){
        removeErrorStyleFromAll();
        itemNameTF.clear();
        itemQuantityTF.clear();
        itemDetailTF.clear();
        unitPriceTF.clear();


    }

    //a method to set the table data with all available data
    public void setTableWithAllItems(){
        String query = "select * from Stock_Item";
        setTableData(getObservableListForItems(query));
    }

    //a method to check if the fields are filled correctly
    boolean checkFields(){
        //taking a boolean variable that checks if any field is empty or not
        boolean allOK = true;

        //checking if the field is empty or not
        if(itemNameTF.getText().isEmpty()){
            //if it is empty we will mark allOK false
            allOK = false;
            //and we will make the border of that field red to indicate which field is missing
            //this method actually uses a property of our css file and add it to the field styleClass
            if(!itemNameTF.getStyleClass().contains("error")){
                itemNameTF.getStyleClass().add("error");
            }
        }else{
            //if the field is not empty we will remove the error property
            itemNameTF.getStyleClass().remove("error");
        }

        //do above step for rest of the fields
        if(itemDetailTF.getText().isEmpty()){
            allOK = false;
            if(!itemDetailTF.getStyleClass().contains("error")){
                itemDetailTF.getStyleClass().add("error");
            }
        }else{
            itemDetailTF.getStyleClass().remove("error");
        }

        if(itemQuantityTF.getText().isEmpty()){
            allOK = false;
            if(!itemQuantityTF.getStyleClass().contains("error")){
                itemQuantityTF.getStyleClass().add("error");
            }
        }else{
            itemQuantityTF.getStyleClass().remove("error");
        }

        if(unitPriceTF.getText().isEmpty()){
            allOK = false;
            if(!unitPriceTF.getStyleClass().contains("error")){
                unitPriceTF.getStyleClass().add("error");
            }
        }else{
            unitPriceTF.getStyleClass().remove("error");
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
        itemNameTF.getStyleClass().remove("error");
        itemDetailTF.getStyleClass().remove("error");
        itemQuantityTF.getStyleClass().remove("error");
        unitPriceTF.getStyleClass().remove("error");

    }


    //a method that will load everything in it as soon as this file is loaded
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTableWithAllItems();

    }

}

