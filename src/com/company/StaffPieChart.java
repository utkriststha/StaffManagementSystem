package com.company;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.*;

public class StaffPieChart {
    //Initiating statement and observable list
    public static Statement statement;
    public static ObservableList observableList;

    public static void connectJDBC() throws ClassNotFoundException, SQLException{
        //Calling the driver
        Class.forName("com.mysql.cj.jdbc.Driver");
        //Connecting the database and setting it as an object for statement jdbc:mysql://127.0.0.1:3306/?user=root
        Connection connect = DriverManager.getConnection
                ("jdbc:mysql://127.0.0.1:3306/A3?useSSL=false&serverTimezone=UTC", "root", "password");
        statement = connect.createStatement();
    }

    public static void display() throws SQLException, ClassNotFoundException {
        //Calls module to connect to database
        connectJDBC();

        Stage window = new Stage();

        //Creates pie chart object
        PieChart pieChart = new PieChart();
        //calling method the get pie chart data from the database
        chartData();
        //Setting the title for the pie chart
        pieChart.setTitle("Staff Located by State");
        //Getting data for the pie chart from the observable list
        pieChart.getData().addAll(observableList);

        //Creating a scene with pie chart
        Scene scene = new Scene(pieChart,900,900);
        //Setting the title of the pie chart
        window.initModality(Modality.APPLICATION_MODAL);
        //Setting the scene for the stage
        window.setScene(scene);
        //Displaying the stage
        window.showAndWait();


    }
    public static void chartData(){
        //Setting observable list as arraylist
        observableList = FXCollections.observableArrayList();
        //initiating variables
        double totalCount =0;
        String legendData;
        //Initiating query for the database in a sting
        String query = "select Count(State) as Count, State from A3.Staff group by State";

        //Executing query to get the total count of the states
        try {
            ResultSet result = statement.executeQuery(query);
            while(result.next()){
                totalCount= totalCount+ result.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Executing query to add the data from the database to the list
        // And editing the string for the pie chart at the same time
        try {
            ResultSet result = statement.executeQuery(query);
            while(result.next()){
                legendData =(result.getString(2)+" - "+(int)((result.getDouble(1)/totalCount)*100)+"%");
                observableList.add(new PieChart.Data(legendData, result.getDouble(1)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

