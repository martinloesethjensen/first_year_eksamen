package com.eksamen.eksamen;

import com.eksamen.eksamen.Base.Location;
import com.eksamen.eksamen.Base.Session;
import com.eksamen.eksamen.Base.Staff;
import com.eksamen.eksamen.Handler.DatabaseHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StaffService {
  public static int getIdForStaff = 0;

  // Method to get all the locations in the database into an array.
  public static ArrayList<Location> getLocations() {
    ArrayList<Location> locations = new ArrayList<>();

    ResultSet resultSet = DatabaseHandler.getInstance().querySelect("SELECT location_id, location_name\n" +
        "FROM location INNER JOIN staff_location l ON location.location_id = l.fk_location_id\n" +
        "INNER JOIN staff s ON l.fk_staff_id = s.staff_id\n" +
        "WHERE staff_id = " + Session.getId());

    try {
      //adding the locations to an arraylist
      while (resultSet.next()) {
        locations.add(new Location(resultSet.getInt("location_id"), resultSet.getString("location_name")));
      }
      resultSet.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return locations;
  }


   // Method that get the employee id through a query select that searches on the email.

  public static int getEmployeeId(String email) {

    ResultSet resultSet = DatabaseHandler.getInstance().querySelect(
        "SELECT staff_id " +
            "FROM staff " +
            "WHERE " +
            "email = '" + email + "'"
    );

    try {
      resultSet.next();

      //Sets the static field
      getIdForStaff = resultSet.getInt("staff_id");

      resultSet.close();

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return getIdForStaff;
  }

  // A method that gets staff information.
  public static Staff getStaff(String email) {
    Staff staffEdit = new Staff();

    getEmployeeId(email);

    ResultSet resultSet = DatabaseHandler.getInstance().querySelect(
        "SELECT firstname, lastname, phone, email, fk_staff_niveau_id " +
            "FROM " +
            "staff " +
            "WHERE " +
            "email = '" + email + "'"
    );

    try {
      resultSet.next();

      staffEdit.setFirstName(resultSet.getString("firstname"));
      staffEdit.setLastName(resultSet.getString("lastname"));
      staffEdit.setPhonenumber(resultSet.getInt("phone"));
      staffEdit.setEmail(resultSet.getString("email"));
      staffEdit.setStaffNiveau(resultSet.getInt("fk_staff_niveau_id"));

      resultSet.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return staffEdit;
  }

  // Removes employee from database
  public static void deleteForEmployee(String table, String condition){
    DatabaseHandler.getInstance().delete(
        "DELETE FROM "+
            table+" "+
            "WHERE "+condition+"= "+getIdForStaff+"; "
    );
  }

  // Method to get the employees locations
  public static ArrayList getCurrentLocations(){
    ArrayList<Integer> currentLocations = new ArrayList<>();

    ResultSet resultSet = DatabaseHandler.getInstance().querySelect(
        "SELECT fk_location_id "+
        "FROM staff_location "+
        "WHERE fk_staff_id = "+getIdForStaff
    );

    try {
      while (resultSet.next()) {
        currentLocations.add(resultSet.getInt("fk_location_id"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return currentLocations;
  }

  //An arraylist with true/false on the locations the employee has
  public static ArrayList booleanCheckArray() {
    ArrayList<Boolean> boo = new ArrayList<>(); // Boolean array with same length as amount of locations, true is locations an employee have
    boolean bb = false; // The variable that get added to the boo array

    // nested loop that check if any location id match any current location id, and add result to array
    for (int i = 0; i < StaffService.getLocations().size(); i++){
      for (Object j : StaffService.getCurrentLocations())
        if(StaffService.getLocations().get(i).getLocationId() == Integer.parseInt(j.toString())){
          bb = true;

        }
      boo.add(bb);
      bb = false; //resetting to false value
    }
    return boo;
  }
}
