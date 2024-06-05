package com.eventmanagement.controllers;

import com.eventmanagement.database.SQLConnection;
import com.eventmanagement.entities.Employee;
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
import java.time.LocalDate;
import java.util.Collections;
import java.util.ResourceBundle;



public class employeesController  implements Initializable {

    @FXML
    private TextField employeeNameTF;

    @FXML
    private TextField hourlyWageTF;

    @FXML
    private TextField phoneNumberTF;

    @FXML
    private TextField addressTF;

    @FXML
    private ComboBox<String> employeeTypeCB;

    @FXML
    private DatePicker dateOfBirthDF;


    int employeeID = -1;

    Employee employee;

    //table and column
    @FXML
    private TableView<Employee> employeeTableView;
    @FXML
    private TableColumn<Employee, String> employeeNameColumn;

    @FXML
    private TableColumn<Employee, LocalDate> dateOfBirthColumn;

    @FXML
    private TableColumn<Employee, Float> hourlyWageColumn;

    @FXML
    private TableColumn<Employee,String> employeeTypeColumn;

    @FXML
    private TableColumn<Employee, String> phoneNumberColumn;

    @FXML
    private TableColumn<Employee, String> addressColumn;

    @FXML
    private TableColumn<Employee, Integer> employeeIdColumn;

    //a tray-notification variable to show all the r
    TrayNotification tray = new TrayNotification();




    //methods to control actions with buttons on event page

    @FXML
    void onAddBtnClick(ActionEvent event) {
        if(checkFields()) {
            //declaring the query variable
            String query = null;
            //add button will record all the information of the employee, and store it in the database
            try {
                String name = employeeNameTF.getText();
                float hourlyWage = Float.valueOf(hourlyWageTF.getText());
                String phoneNumber = phoneNumberTF.getText();
                String address = addressTF.getText();
                String type = employeeTypeCB.getValue();
                String dob = String.valueOf(dateOfBirthDF.getValue());


                //query to enter the data in ms sql database
                query = String.format("insert into Employee values ('%s','%s','%s','%s',%.2f,'%s')", name, phoneNumber, address, type, hourlyWage, dob);

                //making connection with the database
                Connection connection = new SQLConnection().connection;

                //making a statement to execute the query in a try catch block
                try (Statement st = connection.createStatement()) {
                    st.execute(query);
                    //showing a tray notification upon success
                    tray.setTray("Add Employee", "Employee was successfully added!", NotificationType.SUCCESS);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(2));


                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    tray.setTray("Add Employee", "There was an error in adding the employee!", NotificationType.ERROR);
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
                    setTableWithAllEmployees();
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
        //check if an employee is selected or not
        if (employeeID != -1) {

            //confirming if the user wants to delete the record
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this record?", ButtonType.YES, ButtonType.CANCEL);
            alert.setTitle("Confirmation For Delete");
            alert.showAndWait();

            //if user says yes
            if (alert.getResult() == ButtonType.YES) {
                //build query to delete the employee
                //here employeeSelected is an int variable got when we double-click a record in table
                String query = String.format("Delete from Employee where Employee_ID = %d", employeeID);

                //create a connection object
                Connection con = new SQLConnection().connection;

                try (Statement st = con.createStatement()) {
                    st.execute(query);
                    tray.setTray("Delete Employee", "Successfully deleted the employee!", NotificationType.SUCCESS);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(2));


                } catch (NumberFormatException e) {
                    Alert a = new Alert(Alert.AlertType.WARNING, "Please fill the integer fields correctly!");
                    a.show();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    tray.setTray("Delete Employee", "There was an error while deleting the employee!", NotificationType.ERROR);
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
                    setTableWithAllEmployees();
                    clearAllFields();
                    employeeID = -1;
                }
            }

        }
        else{
            Alert a = new Alert(Alert.AlertType.INFORMATION,"Please double click a employee from the table to select it and then you delete it!");
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
            Alert a = new Alert(Alert.AlertType.INFORMATION,"Please fill in the employee's number to search!");
            a.setTitle("Search In Employees");
            a.show();
            phoneNumberTF.requestFocus();
            phoneNumberTF.getStyleClass().add("error");

        }else { //if filled
            Connection con = new SQLConnection().connection;
            String query = null;
            try (Statement st = con.createStatement()) {
                //we will build query based on user input

                query = String.format("select * from Employee where employee_number like '%s'",phoneNumberTF.getText());

                //set table data with the searched result
                setTableData(getObservableListForEmployees(query));
                //clearing all the fields
                clearAllFields();

            }catch(NumberFormatException e){ //for employee id validation because we need an integer in it
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

    //a method to update a employee in the database
    @FXML
    void onUpdateBtnClick(ActionEvent event) {
        //check if an employee is selected or not
        if (employeeID != -1) {
            //we will check if all the fields are filled or not
            if(checkFields()) {
                //get updated fields from the text fields
                String name = employeeNameTF.getText();
                float hourlyWage = Float.valueOf(hourlyWageTF.getText());
                String number = phoneNumberTF.getText();
                String address = addressTF.getText();
                String type = employeeTypeCB.getValue();
                String dob = String.valueOf(dateOfBirthDF.getValue());

                //build an update query based on these
                String query = String.format("update Employee set Employee_Name = '%s', Employee_Type = '%s', Employee_Number = '%s', Employee_Address = '%s', " +
                        "Employee_Hourly_Wage = '%.2f', Employee_DOB = '%s' where Employee_ID = %d", name, type, number, address, hourlyWage, dob, employeeID);

                //create a connection object
                Connection con = new SQLConnection().connection;

                try (Statement st = con.createStatement()) {
                    st.execute(query);
                    tray.setTray("Update Employee", "Successfully updated the employee!", NotificationType.SUCCESS);
                    tray.setAnimationType(AnimationType.POPUP);
                    tray.showAndDismiss(Duration.seconds(2));


                } catch (NumberFormatException e) {
                    Alert a = new Alert(Alert.AlertType.WARNING, "Please fill the integer fields correctly!");
                    a.show();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    tray.setTray("Update Employee", "There was an error while updating the employee!", NotificationType.ERROR);
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
                    setTableWithAllEmployees();
                    clearAllFields();
                    employeeID = -1;
                }
            }
        }else{
            Alert a = new Alert(Alert.AlertType.INFORMATION,"Please double click a employee from the table to select it and then you can update it!");
            a.setTitle("Update");
            a.show();
        }


    }

    //a method to load the employee information into the text fields when it is double-clicked in the table
    @FXML
    void mouseClickedTableView(javafx.scene.input.MouseEvent event) {
        employeeTableView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent mouseEmployee) {
                if(mouseEmployee.isPrimaryButtonDown() && mouseEmployee.getClickCount() == 2){
                    //clearing all the style on fields
                    removeErrorStyleFromAll();

                    //getting the selected employee object
                    Employee employee = employeeTableView.getSelectionModel().getSelectedItem();

                    employeeNameTF.setText(employee.getName());
                    hourlyWageTF.setText(String.valueOf(employee.getHourlyWage()));
                    phoneNumberTF.setText(employee.getPhoneNumber());
                    addressTF.setText(employee.getAddress());
                    dateOfBirthDF.setValue(employee.getDateOfBirth());
                    employeeTypeCB.setValue(employee.getType());
                    employeeID = employee.getId();
                }
            }
        });

    }




    //a method to check if a text field is empty
    public void checkIfEmpty(TextField tf) {
        if (tf.getText() == "") {
            tf.requestFocus();
            Alert a = new Alert(Alert.AlertType.WARNING, "Please fill in all the fields!");
            a.show();

        }
    }

    //a method to get all the employees
    public ObservableList<Employee> getObservableListForEmployees(String query) {
        ObservableList<Employee> employees = FXCollections.observableArrayList();
        String qr = query;
        SQLConnection sqlConnection = new SQLConnection();
        Connection connection = sqlConnection.connection;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(qr);
            while (resultSet.next()) {
                //making a employee object
                Employee employee = new Employee(resultSet.getInt("Employee_ID"),resultSet.getString("Employee_Name"),
                        resultSet.getString("Employee_Number"),resultSet.getString("Employee_Address"),resultSet.getString("Employee_Type"),
                        resultSet.getFloat("Employee_Hourly_Wage"),resultSet.getDate("Employee_DOB").toLocalDate());


                //adding that object to the list
                employees.add(employee);
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

        return employees;
    }

    //a method to load the employees into the table
    public void setTableData(ObservableList<Employee> list){
        //setting the list of the table
        employeeTableView.setItems(list);
        //setting the columns with attributes
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        hourlyWageColumn.setCellValueFactory(new PropertyValueFactory<>("hourlyWage"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        employeeTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateOfBirthColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

    }

    //a method to clear all the fields
    public void clearAllFields(){
        removeErrorStyleFromAll();
        employeeNameTF.clear();
        phoneNumberTF.clear();
        hourlyWageTF.clear();
        addressTF.clear();
        dateOfBirthDF.getEditor().clear();
        employeeTypeCB.getEditor().clear();

    }

    //a method to set the table data with all available data
    public void setTableWithAllEmployees(){
        String query = "select * from Employee";
        setTableData(getObservableListForEmployees(query));
    }

    //a method to set types of employees in the combo box
    public void setEmployeeTypeComboBox(){
        ObservableList<String> employeeTypes = FXCollections.observableArrayList();
        employeeTypes.addAll("Manager","Cook","Waiter","Decorator","Sweeper","Other");
        employeeTypeCB.setItems(employeeTypes);
    }



    //a method to check if the fields are filled correctly
    public boolean checkFields(){
        //taking a boolean variable that checks if any field is empty or not
        boolean allOK = true;

        //checking if the field is empty or not
        if(employeeNameTF.getText().isEmpty()){
            //if it is empty we will mark allOK false
            allOK = false;
            //and we will make the border of that field red to indicate which field is missing
            //this method actually uses a property of our css file and add it to the field styleClass
            if(!employeeNameTF.getStyleClass().contains("error")){
                employeeNameTF.getStyleClass().add("error");
            }
        }else{
            //if the field is not empty we will remove the error property
            employeeNameTF.getStyleClass().remove("error");
        }

        //do above step for rest of the fields
        if(hourlyWageTF.getText().isEmpty()){
            allOK = false;
            if(!hourlyWageTF.getStyleClass().contains("error")){
                hourlyWageTF.getStyleClass().add("error");
            }
        }else{
            hourlyWageTF.getStyleClass().remove("error");
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

        if(dateOfBirthDF.getEditor().getText().isEmpty()){
            allOK = false;
            if(!dateOfBirthDF.getStyleClass().contains("error")){
                dateOfBirthDF.getStyleClass().add("error");
            }
        }else{
            dateOfBirthDF.getStyleClass().remove("error");
        }

        if(employeeTypeCB.getSelectionModel().isEmpty()){
            allOK = false;
            if(!employeeTypeCB.getStyleClass().contains("error")){
                employeeTypeCB.getStyleClass().add("error");
            }
        }else{
            employeeTypeCB.getStyleClass().remove("error");
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
        employeeNameTF.getStyleClass().remove("error");
        hourlyWageTF.getStyleClass().remove("error");
        phoneNumberTF.getStyleClass().remove("error");
        addressTF.getStyleClass().remove("error");
        dateOfBirthDF.getStyleClass().remove("error");
        employeeTypeCB.getStyleClass().remove("error");
    }

    //a method that will load everything in it as soon as this file is loaded
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTableWithAllEmployees();
        setEmployeeTypeComboBox();

    }

}


