package com.eventmanagement.controllers;

import com.eventmanagement.database.SQLConnection;
import com.eventmanagement.entities.Customer;
import com.eventmanagement.entities.Event;
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
import java.time.LocalTime;
import java.util.ResourceBundle;

public class eventsController implements Initializable {

        @FXML
        private TextField eventNameTF;

        @FXML
        private TextField venueNameTF;

        @FXML
        private TextField venueAddressTF;

        @FXML
        private TextField eventExpenseTF;

        @FXML
        private TextField eventPriceTF;

        @FXML
        private TextField customerIDTF;

        @FXML
        private DatePicker eventDateField;

        @FXML
        private TextField startTimeTF;

        @FXML
        private TextField endTimeTF;

        @FXML
        private ComboBox<String> eventTypeComboBox;

        //table and column
        @FXML
        private TableView<Event> eventTableView;
        @FXML
        private TableColumn<?, ?> eventNameColumn;

        @FXML
        private TableColumn<?, ?> eventTypeColumn;

        @FXML
        private TableColumn<?, ?> venueColumn;

        @FXML
        private TableColumn<?, ?> dateColumn;

        @FXML
        private TableColumn<?, ?> startTimeColumn;

        @FXML
        private TableColumn<?, ?> endTimeColumn;

        @FXML
        private TableColumn<?, ?> eventExpenseColumn;

        @FXML
        private TableColumn<?, ?> eventPriceColumn;

        @FXML
        private TableColumn<Event, String> customerIdColumn;

        //a tray-notification variable to show all the r
        TrayNotification tray = new TrayNotification();

        //an int eventId variable to store the id of the selected event
        int eventID = 0;


        //methods to control actions with buttons on event page

        @FXML
        void onAddBtnClick(ActionEvent event) {
                //checking if all the fields are filled or not
                if(checkFields()) {
                        //declaring the query variable
                        String query = null;
                        //add button will record all the information of the event, and store it in the database
                        try {
                                String eventName = eventNameTF.getText();
                                String venueName = venueNameTF.getText();
                                String venueAddress = venueAddressTF.getText();
                                String eventType = eventTypeComboBox.getValue();
                                int eventExpense = Integer.parseInt(eventExpenseTF.getText());
                                int eventPrice = Integer.parseInt(eventPriceTF.getText());
                                int customerID = Integer.parseInt(customerIDTF.getText());
                                String eventDate = eventDateField.getValue().toString();
                                String startTime = startTimeTF.getText();
                                String endTime = endTimeTF.getText();

                                if (checkTimeAndDate(startTime, endTime, eventDate)) {

                                        //query to enter the data in ms sql database
                                        query = String.format("insert into Event(Customer_ID,Event_Name,Event_Type,Event_Expense,Event_Price_decided,Event_Venue,Event_Venue_Address,Event_Date,Event_Start_time,Event_End_Time) " +
                                                "values(%d,'%s','%s',%d,%d,'%s','%s','%s','%s','%s')", customerID, eventName, eventType, eventExpense, eventPrice, venueName, venueAddress, eventDate, startTime, endTime);

                                        //making connection with the database
                                        Connection connection = new SQLConnection().connection;

                                        //making a statement to execute the query in a try catch block
                                        try (Statement st = connection.createStatement()) {
                                                st.execute(query);

                                                //showing a tray notification upon success
                                                tray.setTray("Add Event", "Event was successfully added!", NotificationType.SUCCESS);
                                                tray.setAnimationType(AnimationType.POPUP);
                                                tray.showAndDismiss(Duration.seconds(2));


                                        } catch (SQLException throwables) {
                                                throwables.printStackTrace();
                                                tray.setTray("Add Event", "There was an error in adding the event!", NotificationType.ERROR);
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
                                                setTableWithAllEvents();
                                                clearAllFields();
                                                eventID = 0;
                                        }
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
                if (eventID != 0) {

                        //confirming if the user wants to delete the record
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this record?", ButtonType.YES, ButtonType.CANCEL);
                        alert.setTitle("Confirmation For Delete");
                        alert.showAndWait();

                        //if user says yes
                        if (alert.getResult() == ButtonType.YES) {
                                //build query to delete the event
                                //here eventID is an int variable got when we double-click a record in table
                                String query = String.format("Delete from Event where Event_ID = %d", eventID);

                                //create a connection object
                                Connection con = new SQLConnection().connection;

                                try (Statement st = con.createStatement()) {
                                        st.execute(query);
                                        tray.setTray("Delete Event", "Successfully deleted the event!", NotificationType.SUCCESS);
                                        tray.setAnimationType(AnimationType.POPUP);
                                        tray.showAndDismiss(Duration.seconds(2));


                                } catch (NumberFormatException e) {
                                        Alert a = new Alert(Alert.AlertType.WARNING, "Please fill the integer fields correctly!");
                                        a.show();
                                } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                        tray.setTray("Delete Event", "There was an error while deleting the event!", NotificationType.ERROR);
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
                                        setTableWithAllEvents();
                                        clearAllFields();
                                        eventID = 0;
                                }
                        }

                }
                else{
                        Alert a = new Alert(Alert.AlertType.INFORMATION,"Please double click an event from the table to select it and then you can delete it!");
                        a.setTitle("Delete");
                        a.show();
                }

        }

        @FXML
        void onSearchBtnClick(ActionEvent event) {

                //clearing all red fields if any before we proceed
                removeErrorStyleFromAll();

                //checking if the required fields are filled or not
                if(eventNameTF.getText().equals("") && customerIDTF.getText().equals("")) {
                        Alert a = new Alert(Alert.AlertType.INFORMATION,"Please fill in the Customer ID or Event Name to search!");
                        a.setTitle("Search In Events");
                        a.show();
                        eventNameTF.requestFocus();
                        eventNameTF.getStyleClass().add("error");


                }else { //if filled
                        Connection con = new SQLConnection().connection;
                        String query = null;
                        try (Statement st = con.createStatement()) {
                                //we will build query based on user input

                                //if user wants to search by customer id
                                if(eventNameTF.getText().trim().isEmpty()) {
                                         query = String.format("Select * from Event where Customer_ID = %d", Integer.parseInt(customerIDTF.getText()));
                                }
                                //if user want s to search by event name
                                else if(customerIDTF.getText().trim().isEmpty()){
                                        query = String.format(("Select * from Event where Event_Name = '%s'"),eventNameTF.getText());
                                }
                                //if user wants to search by both customer id and event name
                                else{
                                        query = String.format("Select * from Event where Customer_ID = %d AND Event_Name like '%s'",Integer.parseInt(customerIDTF.getText()), eventNameTF.getText());
                                }

                                //set table data with the searched result
                                setTableData(getObservableListForEvents(query));
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

        //a method to update an event in the database
        @FXML
        void onUpdateBtnClick(ActionEvent event) {
                if (eventID != 0) {
                        //checking if all the fields are filled or not
                        if(checkFields()) {
                                //get updated fields from the text fields
                                String name = eventNameTF.getText();
                                String type = String.valueOf(eventTypeComboBox.getValue());
                                String venue = venueNameTF.getText();
                                String venueAddress = venueAddressTF.getText();
                                int eventExpense = Integer.parseInt(eventExpenseTF.getText());
                                int eventPrice = Integer.parseInt(eventPriceTF.getText());
                                int customerID = Integer.parseInt(customerIDTF.getText());
                                String date = eventDateField.getValue().toString();
                                String startTime = startTimeTF.getText();
                                String endTime = endTimeTF.getText();

                                //build an update query based on these
                                String query = String.format("update Event set Event_Name = '%s',Customer_ID = %d,Event_Type = '%s'," +
                                        "Event_Expense = %d,Event_Price_Decided = %d,Event_Date = '%s',Event_Start_Time = '%s',Event_End_Time = '%s'," +
                                        "Event_Venue = '%s', Event_Venue_Address = '%s' where Event_ID = %d", name, customerID, type, eventExpense, eventPrice, date, startTime, endTime, venue, venueAddress, eventID);

                                //create a connection object
                                Connection con = new SQLConnection().connection;

                                try (Statement st = con.createStatement()) {
                                        st.execute(query);
                                        tray.setTray("Update Event", "Successfully updated the event!", NotificationType.SUCCESS);
                                        tray.setAnimationType(AnimationType.POPUP);
                                        tray.showAndDismiss(Duration.seconds(2));


                                } catch (NumberFormatException e) {
                                        Alert a = new Alert(Alert.AlertType.WARNING, "Please fill the integer fields correctly!");
                                        a.show();
                                } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                        tray.setTray("Update Event", "There was an error while updating the event!", NotificationType.ERROR);
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
                                        setTableWithAllEvents();
                                        clearAllFields();
                                        eventID = 0;
                                }
                        }

                }else{
                        Alert a = new Alert(Alert.AlertType.INFORMATION,"Please double click an event from the table to select it and then you can update it!");
                        a.setTitle("Update");
                        a.show();
                }


        }

        //a method to load the event information into the text fields when it is double-clicked in the table
        @FXML
        void mouseClickedTableView(javafx.scene.input.MouseEvent event) {
                //removing all the styling from the fields
                removeErrorStyleFromAll();

                eventTableView.setOnMousePressed(new EventHandler<javafx.scene.input.MouseEvent>() {
                        @Override
                        public void handle(javafx.scene.input.MouseEvent mouseEvent) {
                                if(mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() == 2){
                                        //getting the selected event object
                                        Event event = eventTableView.getSelectionModel().getSelectedItem();
                                        //setting the fields
                                        eventID = event.getId();
                                        eventNameTF.setText(event.getName());
                                        venueNameTF.setText(event.getVenue());
                                        venueAddressTF.setText(event.getVenueAddress());
                                        eventTypeComboBox.setValue(event.getType());
                                        eventExpenseTF.setText(String.valueOf(event.getExpense()));
                                        eventPriceTF.setText(String.valueOf(event.getPriceDecided()));
                                        customerIDTF.setText(String.valueOf(event.getCustomer().getId()));
                                        eventDateField.setValue(event.getDate());
                                        startTimeTF.setText(String.format("%.5s",event.getStartTime()));
                                        endTimeTF.setText(String.format("%.5s",event.getEndTime()));
                                }
                        }
                });

                eventTableView.setOnMouseEntered(new EventHandler<javafx.scene.input.MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {

                        }

                });

        }


        //a method to set event type combo box with value
        public void setEventTypeComboBox() {
                ObservableList<String> options = FXCollections.observableArrayList();
                options.addAll("Birthday Party", "Wedding", "Concert");
                eventTypeComboBox.setItems(options);
        }


        //a method to get all the events
        public ObservableList<Event> getObservableListForEvents(String query) {
                ObservableList<Event> events = FXCollections.observableArrayList();
                String qr = query;
                SQLConnection sqlConnection = new SQLConnection();
                Connection connection = sqlConnection.connection;
                try (Statement statement = connection.createStatement()) {
                        ResultSet resultSet = statement.executeQuery(qr);
                        while (resultSet.next()) {
                                //making a customer object to add in the event object
                                Customer customer = new Customer();
                                customer.setId(resultSet.getInt("Customer_ID"));

                                //creating a new event object
                                Event event = new Event(resultSet.getInt("Event_ID"), resultSet.getString("Event_Name"),
                                        resultSet.getString("Event_Type"), resultSet.getString("Event_Venue"), resultSet.getString("Event_Venue_Address"),
                                        resultSet.getDate("Event_Date").toLocalDate(), resultSet.getInt("Event_Expense"), resultSet.getInt("Event_Price_decided"),
                                        resultSet.getTime("Event_Start_time"), resultSet.getTime("Event_End_Time"), customer);

                                //adding that object to the list
                                events.add(event);
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

                return events;
        }

        //a method to load the events into the table
        public void setTableData(ObservableList<Event> list){
                //setting the list of the table
                eventTableView.setItems(list);
                //setting the columns with attributes
                eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                eventTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
                venueColumn.setCellValueFactory(new PropertyValueFactory<>("venue"));
                dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
                startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
                endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
                eventExpenseColumn.setCellValueFactory(new PropertyValueFactory<>("expense"));
                eventPriceColumn.setCellValueFactory(new PropertyValueFactory<>("priceDecided"));
                customerIdColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Event, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue<String> call(TableColumn.CellDataFeatures<Event, String> eventStringCellDataFeatures) {
                                return new ReadOnlyObjectWrapper(eventStringCellDataFeatures.getValue().getCustomer().getId());
                        }
                });

        }

        //a method to clear all the fields
        public void clearAllFields(){
                removeErrorStyleFromAll();
                eventNameTF.clear();
                venueAddressTF.clear();
                venueNameTF.clear();
                eventTypeComboBox.setValue(eventTypeComboBox.getPromptText());
                eventDateField.getEditor().clear();
                eventExpenseTF.clear();
                eventPriceTF.clear();
                customerIDTF.clear();
                startTimeTF.clear();
                endTimeTF.clear();

        }

        //a method to set the table data with all available data
        public void setTableWithAllEvents(){
                String query = "select * from Event";
                setTableData(getObservableListForEvents(query));
        }


        //a method to check if the fields are filled correctly
        public boolean checkFields(){
                //taking a boolean variable that checks if any field is empty or not
                boolean allOK = true;

                //checking if the field is empty or not
                if(eventNameTF.getText().trim().isEmpty()){
                        //if it is empty we will mark allOK false
                        allOK = false;
                        //and we will make the border of that field red to indicate which field is missing
                        //this method actually uses a property of our css file and add it to the field styleClass
                        if(!eventNameTF.getStyleClass().contains("error")){
                                eventNameTF.getStyleClass().add("error");
                        }
                }else{
                        //if the field is not empty we will remove the error property
                        eventNameTF.getStyleClass().remove("error");
                }

                //do above step for rest of the fields

                if(endTimeTF.getText().trim().isEmpty()){
                        allOK = false;
                        if(!endTimeTF.getStyleClass().contains("error")){
                                endTimeTF.getStyleClass().add("error");
                        }
                }else{
                        endTimeTF.getStyleClass().remove("error");
                }

                if(startTimeTF.getText().trim().isEmpty()){
                        allOK = false;
                        if(!startTimeTF.getStyleClass().contains("error")){
                                startTimeTF.getStyleClass().add("error");
                        }
                }else{
                        startTimeTF.getStyleClass().remove("error");
                }

                if(venueNameTF.getText().trim().isEmpty()){
                        allOK = false;
                        if(!venueNameTF.getStyleClass().contains("error")){
                                venueNameTF.getStyleClass().add("error");
                        }
                }else{
                        venueNameTF.getStyleClass().remove("error");
                }

                if(venueAddressTF.getText().trim().isEmpty()){
                        allOK = false;
                        if(!venueAddressTF.getStyleClass().contains("error")){
                                venueAddressTF.getStyleClass().add("error");
                        }
                }else{
                        venueAddressTF.getStyleClass().remove("error");
                }

                if(eventDateField.getEditor().getText().trim().isEmpty()){
                        allOK = false;
                        if(!eventDateField.getStyleClass().contains("error")){
                                eventDateField.getStyleClass().add("error");
                        }
                }else{
                        eventDateField.getStyleClass().remove("error");
                }

                if(eventTypeComboBox.getSelectionModel().isEmpty()){
                        allOK = false;
                        if(!eventTypeComboBox.getStyleClass().contains("error")){
                                eventTypeComboBox.getStyleClass().add("error");
                        }
                }else{
                        eventTypeComboBox.getStyleClass().remove("error");
                }

                if(eventPriceTF.getText().trim().isEmpty()){
                        allOK = false;
                        if(!eventPriceTF.getStyleClass().contains("error")){
                                eventPriceTF.getStyleClass().add("error");
                        }
                }else{
                        eventPriceTF.getStyleClass().remove("error");
                }

                if(eventExpenseTF.getText().trim().isEmpty()){
                        allOK = false;
                        if(!eventExpenseTF.getStyleClass().contains("error")){
                                eventExpenseTF.getStyleClass().add("error");
                        }
                }else{
                        eventExpenseTF.getStyleClass().remove("error");
                }

                if(customerIDTF.getText().trim().isEmpty()){
                        allOK = false;
                        if(!customerIDTF.getStyleClass().contains("error")){
                                customerIDTF.getStyleClass().add("error");
                        }
                }else{
                        customerIDTF.getStyleClass().remove("error");
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
                eventNameTF.getStyleClass().remove("error");
                venueNameTF.getStyleClass().remove("error");
                venueAddressTF.getStyleClass().remove("error");
                eventPriceTF.getStyleClass().remove("error");
                eventDateField.getStyleClass().remove("error");
                eventTypeComboBox.getStyleClass().remove("error");
                eventExpenseTF.getStyleClass().remove("error");
                startTimeTF.getStyleClass().remove("error");
                endTimeTF.getStyleClass().remove("error");
                customerIDTF.getStyleClass().remove("error");
        }

        //a function to check date and time availability
        public boolean checkTimeAndDate(String startTime, String endTime, String date){
                boolean pass = true;

                //first we check if the start time is not smaller than the end time
                //compareTo function returns 0 when start time is equal to end time
                //returns positive value if start time is greater than end time
                //return negative value if start time is smaller than end time
                if(LocalTime.parse(startTime).compareTo(LocalTime.parse(endTime)) >= 0){
                        pass = false;
                        Alert a = new Alert(Alert.AlertType.WARNING,"Please check the start and end time values!");
                        a.showAndWait();
                        startTimeTF.getStyleClass().add("error");
                        endTimeTF.getStyleClass().add("error");
                        return pass;

                }

                //check if the slot is available or not
                String query = String.format("select * from Event where (Event_Date = '%s') and (('%s' between Event_Start_time and Event_End_Time) or ('%s' between Event_Start_time and Event_End_Time))",date,startTime,endTime);
                Connection con = new SQLConnection().connection;

                try(Statement st = con.createStatement()){
                        ResultSet rs = st.executeQuery(query);
                        while(rs.next()){
                                pass = false;
                                String msg = String.format("The time from %.5s to %.5s is already booked please choose another slot.",rs.getString("Event_Start_Time"),rs.getString("Event_End_Time"));
                                Alert a = new Alert(Alert.AlertType.WARNING,msg);
                                a.show();
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


                return pass;


        }


        //a method that will load everything in it as soon as this file is loaded
        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
                setTableWithAllEvents();
                setEventTypeComboBox();

        }

}
