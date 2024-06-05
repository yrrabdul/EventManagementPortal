package com.eventmanagement.controllers;

import com.eventmanagement.database.SQLConnection;
import com.eventmanagement.entities.Customer;
import com.eventmanagement.entities.Event;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import com.eventmanagement.entities.Event;
import com.eventmanagement.entities.Customer;
import javafx.util.Callback;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class dashboardMainController implements Initializable {

    @FXML
    private TableView<Event> upcomingEventsTable;

    @FXML
    private TableColumn<Event, String> customerNameCol = new TableColumn<>("Customer Name");

    @FXML
    private TableColumn<Event, Time> endTimeCol;

    @FXML
    private TableColumn<Event, String> eventNameCol;

    @FXML
    private TableColumn<Event, Time> startTimeCol;

    @FXML
    private TableColumn<Event, String> venueCol;

    public void setTableData(){
        upcomingEventsTable.setItems(getObservableListForUpcomingEvents());
        /*customerNameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Event, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Event, String> event) {
                return new ReadOnlyObjectWrapper<>(event.getValue().getCustomer().getFname()+" "+event.getValue().getCustomer().getLname());
            }
        });*/
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customer"));
        eventNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        venueCol.setCellValueFactory(new PropertyValueFactory<>("venue"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));

    }

    //a method to get observable list of event objects from database to show on table view of upcoming events
    public ObservableList<Event> getObservableListForUpcomingEvents(){
        ObservableList<Event> upcomingEvents = FXCollections.observableArrayList();

        String query = "select Event.Event_Name,Customer.Customer_Fname,Customer.Customer_Lname,Event.Event_Venue,Event.Event_Date,Event_Start_time,Event.Event_End_Time from Event " +
                "inner join Customer on Event.Customer_ID = Customer.Customer_ID " +
                "where (Event.Event_Date  >= getdate()) and CONVERT(Time, GETDATE()) between Event.Event_Start_time and Event.Event_End_Time ";
        SQLConnection sqlConnection = new SQLConnection();
        Connection connection = sqlConnection.connection;
        try(Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                Event event = new Event();
                event.setName(resultSet.getString("Event_Name"));
                event.setCustomer(new Customer(resultSet.getString("Customer_Fname"),resultSet.getString("Customer_Lname")));
                event.setVenue(resultSet.getString("Event_Venue"));
                event.setStartTime(resultSet.getTime("Event_Start_time"));
                event.setEndTime(resultSet.getTime("Event_End_Time"));

                upcomingEvents.add(event);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                connection.close();
                System.out.println("Connection closed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        return upcomingEvents;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
