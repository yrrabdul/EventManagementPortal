package com.eventmanagement.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLConnection {
    public Connection  connection;
    public Statement statement;
    public String url = "jdbc:sqlserver://DESKTOP-Q5QAJS0;databaseName=EventManagement;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";

    public SQLConnection(){
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url);
            System.out.println("Connection established");
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

}

