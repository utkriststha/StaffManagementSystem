package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    //Creating label for the status
    Label status = new Label();

    //Creating text field for all the fields
    TextField idTF = new TextField();
    TextField lNameTF = new TextField();
    TextField fNameTF = new TextField();
    TextField mInitialTF = new TextField();
    TextField addressTF = new TextField();
    TextField cityTF = new TextField();
    TextField stateTF = new TextField();
    TextField telephoneTF = new TextField();

    //Creating button for the options
    Button view = new Button("View");
    Button insert = new Button("Insert");
    Button update = new Button("Update");
    Button clear = new Button("Clear");
    Button pieChart = new Button("Pie Chart");

    //Statement object to processing queries
    Statement statement;

    @Override
    public void start(Stage primaryStage) {

        //Creating a text field format to input data in a horizontal line
        HBox hBox1 = new HBox(5);
        hBox1.getChildren().addAll(new Label("ID"), idTF);
        HBox hBox2 = new HBox(5);
        hBox2.getChildren().addAll(new Label("Last Name"), lNameTF, new Label("First Name"), fNameTF, new Label("MI"), mInitialTF);
        lNameTF.setPrefColumnCount(10);
        fNameTF.setPrefColumnCount(10);
        mInitialTF.setPrefColumnCount(1);
        HBox hBox3 = new HBox(5);
        hBox3.getChildren().addAll(new Label("Address"), addressTF);
        HBox hBox4 = new HBox(5);
        hBox4.getChildren().addAll(new Label("City"), cityTF, new Label("State"), stateTF);
        HBox hBox5 = new HBox(5);
        hBox5.getChildren().addAll(new Label("Telephone"), telephoneTF);

        //Aligning the HBox in the Vertical pane
        VBox vBox = new VBox(5);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(hBox1, hBox2, hBox3, hBox4, hBox5);
        vBox.setMargin(hBox1, new Insets(5));
        vBox.setMargin(hBox2, new Insets(5));
        vBox.setMargin(hBox3, new Insets(5));
        vBox.setMargin(hBox4, new Insets(5));
        vBox.setMargin(hBox5, new Insets(5));

        //Creating format for the options
        HBox hBoxOptions = new HBox(5);
        hBoxOptions.getChildren().addAll(view, insert, update, clear,pieChart);
        hBoxOptions.setAlignment(Pos.CENTER);
        hBoxOptions.setMargin(view,new Insets(0,10,10,10));
        hBoxOptions.setMargin(insert,new Insets(0,10,10,10));
        hBoxOptions.setMargin(update,new Insets(0,10,10,10));
        hBoxOptions.setMargin(clear,new Insets(0,10,10,10));
        hBoxOptions.setMargin(pieChart,new Insets(0,10,10,10));

        HBox hBoxHeading = new HBox(5);
        hBoxHeading.setAlignment(Pos.CENTER);
        hBoxHeading.getChildren().addAll(status);
        hBoxHeading.setMargin(status,new Insets(10));

        BorderPane pane = new BorderPane();
        pane.setTop(hBoxHeading);
        pane.setCenter(vBox);
        pane.setBottom(hBoxOptions);


        // Create a scene with pane
        Scene scene = new Scene(pane, 500, 300);
        // Setting the stage title
        primaryStage.setTitle("Staff");
        // Placing the scene in the stage
        primaryStage.setScene(scene);
        // Displaying the stage
        primaryStage.show();
        //Making the stage non-resizable
        primaryStage.setResizable(true);

        //Connecting ot the data base
        connectJDBC();

        //Setting an event driven program for the options
        view.setOnAction(e -> view());
        insert.setOnAction(e -> insert());
        update.setOnAction(e -> update());
        clear.setOnAction(e -> clear());
        pieChart.setOnAction(e -> {
            try {
                StaffPieChart.display();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
            }
        });
    }

    private void connectJDBC() {
        try {
            //Loading the driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver Loaded");

            //Connecting to the database jdbc:mysql://127.0.0.1:3306/?user=root
            Connection connect = DriverManager.getConnection
                    ("jdbc:mysql://127.0.0.1:3306/A3?useSSL=false&serverTimezone=UTC", "root", "password");
            System.out.println("Connection established");

            //Setting the default status
            status.setText("Hello! View, Insert or Update staff data");

            //Create a SQL statement
            statement = connect.createStatement();
        } catch (Exception ex) {
            status.setText("Failed to Connect: " + ex);
        }
    }

    //View record by ID
    private void view() {
        // Executing a SQL SELECT statement
        String query = "SELECT * FROM Staff WHERE ID = "
                + "'" + idTF.getText().trim() + "'";

        try {
            // Execute query
            ResultSet result = statement.executeQuery(query);
            displayStaff(result);
        } catch (SQLException ex) {
            status.setText("Failed to select " );
            errorStage("Failed to select", ex.toString());
        }
    }

    //Display the staff in the window
    private void displayStaff(ResultSet result) throws SQLException {
        if (result.next()) {
            lNameTF.setText(result.getString(2));
            fNameTF.setText(result.getString(3));
            mInitialTF.setText(result.getString(4));
            addressTF.setText(result.getString(5));
            cityTF.setText(result.getString(6));
            stateTF.setText(result.getString(7));
            telephoneTF.setText(result.getString(8));
            status.setText("Record found");
        } else
            status.setText("Record not found");
    }

    //Insert data to the database
    private void insert() {
        //Date is enter only if the name field is filled, state is valid, telephone number is valid
        if (!lNameTF.getText().isEmpty() && !fNameTF.getText().isEmpty()) {
            if (checkState()) {
                if (checkTel()) {
                    String insert =
                            "INSERT INTO A3.Staff (Lastname,FirstName,MI,Address,City,State,Telephone)" +
                                    "VALUES('"+
                                    lNameTF.getText().trim().substring(0, 1).toUpperCase() +
                                    lNameTF.getText().trim().substring(1) + "','" +
                                    fNameTF.getText().trim().substring(0, 1).toUpperCase() +
                                    fNameTF.getText().trim().substring(1) + "','" +
                                    mInitialTF.getText().trim().toUpperCase() + "','" +
                                    addressTF.getText().trim() + "','" +
                                    cityTF.getText().trim().substring(0, 1).toUpperCase() +
                                    cityTF.getText().trim().substring(1) + "','" +
                                    stateTF.getText().trim() + "'," +
                                    telephoneTF.getText().trim() + ");";

                    try {
                        statement.executeUpdate(insert);
                        status.setText("Record inserted");
                    } catch (SQLException ex) {
                        status.setText("Failed to insert: ");
                        errorStage("Failed to insert", ex.toString());
                    }
                } else {
                    errorStage("Failed to insert", "Telephone number is not correct");
                }
            } else {
                errorStage("Failed to insert", "State name is not correct");
            }
        } else{
            errorStage("Failed to insert", "Name field should not be left blank");
        }
        //Error windows pops up is there is any error
    }

    //Updates the current dataase
    private void update() {
        //Date is enter only if the name field is filled, state is valid, telephone number is valid
        if (!lNameTF.getText().isEmpty() && !fNameTF.getText().isEmpty()) {
            if (checkState()) {
                if (checkTel()) {
                    String update = "UPDATE A3.Staff " +
                            "SET LastName = '" + lNameTF.getText().trim().substring(0, 1).toUpperCase() +
                            lNameTF.getText().trim().substring(1) + "'," +
                            "FirstName = '" + fNameTF.getText().trim().substring(0, 1).toUpperCase() +
                            fNameTF.getText().trim().substring(1) + "'," +
                            "MI = '" + mInitialTF.getText().trim().toUpperCase() + "'," +
                            "Address = '" + addressTF.getText().trim() + "'," +
                            "City = '" + cityTF.getText().trim().substring(0, 1).toUpperCase() +
                            cityTF.getText().trim().substring(1) + "'," +
                            "State = '" + stateTF.getText().trim() + "'," +
                            "Telephone = " + telephoneTF.getText().trim() + " " +
                            "WHERE ID = " + idTF.getText().trim() + "";

                    try {
                        statement.executeUpdate(update);
                        status.setText("Record updated");
                    } catch (SQLException ex) {
                        status.setText("Failed to update: ");
                        errorStage("Failed to update", ex.toString());
                    }
                } else {
                    errorStage("Failed to update", "Telephone number is not correct");
                }
            } else {
                errorStage("Failed to update", "State name not correct");
            }
        } else{
            errorStage("Failed to update", "Name field should not be left blank");
        }
        //Error windows pops up is there is any error
    }

    //Clears out the text field
    private void clear() {
        idTF.setText(null);
        lNameTF.setText(null);
        fNameTF.setText(null);
        mInitialTF.setText(null);
        addressTF.setText(null);
        cityTF.setText(null);
        stateTF.setText(null);
        telephoneTF.setText(null);
        status.setText("Hello! View, Insert or Update staff data");
    }

    //Function to check if the state is valid
    private boolean checkState() {
        //ACT, NSW, NT, QLD, SA, TAS, VIC, WA
        return stateTF.getText().equals("NSW") || stateTF.getText().equals("ACT") || stateTF.getText().equals("NT") ||
                stateTF.getText().equals("QTD") || stateTF.getText().equals("SA") || stateTF.getText().equals("TAS") ||
                stateTF.getText().equals("VIC") || stateTF.getText().equals("WA");
    }

    //Function to check if the telephone number is valid
    private boolean checkTel() {
        if (telephoneTF.getText().length() == 9) {
            for (int i = 0; i < telephoneTF.getText().length(); i++) {
                if (Character.isDigit(telephoneTF.getText().charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    //Alert stage to display error and hold
    private void errorStage(String header, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    //Launch JavaFX
    public static void main(String[] args) {
        launch(args);
    }


}