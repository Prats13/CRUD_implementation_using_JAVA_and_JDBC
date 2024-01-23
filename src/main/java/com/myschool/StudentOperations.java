package com.myschool;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
// import java.util.Date;

public class StudentOperations {
    public static void updateStudentDetails(Connection connection, Student student) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO students(id, first_name, last_name, dob) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setString(1, student.getId());
            preparedStatement.setString(2, student.getFirstName());
            preparedStatement.setString(3, student.getLastName());
            preparedStatement.setDate(4, student.getDob());
            preparedStatement.executeUpdate();
        }
    }

    public static void updateStudentCourseDetails(Connection connection, List<StudentCourse> studentCourses) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO students_courses(s_id, c_id) VALUES (?, ?)")) {
            for (StudentCourse studentCourse : studentCourses) {
                preparedStatement.setString(1, studentCourse.getStudentId());
                preparedStatement.setString(2, studentCourse.getCourseId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
    }

    public static void displayStudentInfo(Connection connection, String studentId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM students WHERE id = ?")) {
            preparedStatement.setString(1, studentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Student ID: " + resultSet.getString("id"));
                    System.out.println("First Name: " + resultSet.getString("first_name"));
                    System.out.println("Last Name: " + resultSet.getString("last_name"));
                    System.out.println("Date of Birth: " + resultSet.getDate("dob"));
                } else {
                    System.out.println("Student not found with ID: " + studentId);
                }
            }
        }
    }

    public static void updateStudentInfo(Connection connection, String studentId) throws SQLException {
        String selectQuery = "SELECT * FROM students WHERE id = ?";
        String updateQuery = "UPDATE students SET %s = ? WHERE id = ?";
        
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setString(1, studentId);
            
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Existing student details
                    System.out.println("Current Student Details:");
                    System.out.println("Student ID: " + resultSet.getString("id"));
                    System.out.println("1. First Name: " + resultSet.getString("first_name"));
                    System.out.println("2. Last Name: " + resultSet.getString("last_name"));
                    System.out.println("3. Date of Birth: " + resultSet.getDate("dob"));
    
                    // Update options
                    System.out.println("Choose the information to update (1-3), or enter any other key to skip:");
                    Scanner scanner = new Scanner(System.in);
                    int updateChoice = scanner.nextInt();
    
                    if (updateChoice >= 1 && updateChoice <= 3) {
                        System.out.print("Enter the new value: ");
                        String newValue = scanner.next();
    
                        // Update the selected field
                        String columnName = "";
                        switch (updateChoice) {
                            case 1:
                                columnName = "first_name";
                                break;
                            case 2:
                                columnName = "last_name";
                                break;
                            case 3:
                                columnName = "dob";
                                break;
                            default:
                                System.out.println("Invalid choice. No updates performed.");
                                return;
                        }
    
                        try (PreparedStatement updateStatement = connection.prepareStatement(String.format(updateQuery, columnName))) {
                            if (updateChoice == 3) {
                                Date newDob = Date.valueOf(newValue);
                                updateStatement.setDate(1, newDob);
                            } else {
                                updateStatement.setString(1, newValue);
                            }
    
                            updateStatement.setString(2, studentId);
                            updateStatement.executeUpdate();
    
                            System.out.println("Student information updated successfully!");
                        }
                    } else {
                        System.out.println("No updates performed.");
                    }
                } else {
                    System.out.println("Student not found with ID: " + studentId);
                }
            }
        }
    }

    public static void deleteStudentInfo(Connection connection, String studentId) throws SQLException {
        String selectQuery = "SELECT * FROM students WHERE id = ?";
        String deleteQuery = "DELETE FROM students WHERE id = ?";
        String referenceDelete = "DELETE FROM students_courses WHERE s_id = ?";
    
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setString(1, studentId);
    
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Existing student details
                    System.out.println("Student Details to be Deleted:");
                    System.out.println("Student ID: " + resultSet.getString("id"));
                    System.out.println("First Name: " + resultSet.getString("first_name"));
                    System.out.println("Last Name: " + resultSet.getString("last_name"));
                    System.out.println("Date of Birth: " + resultSet.getDate("dob"));
    
                    // Confirm deletion
                    System.out.println("Are you sure you want to delete this student? (Y/N): ");
                    Scanner scanner = new Scanner(System.in);
                    String confirmDelete = scanner.next();
    
                    if (confirmDelete.equalsIgnoreCase("Y")) {
                        try(PreparedStatement deleteReference=connection.prepareStatement(referenceDelete)){
                            deleteReference.setString(1, studentId);
                            int rowsAffected = deleteReference.executeUpdate();
    
                            if (rowsAffected > 0) {
                                System.out.println("Student information deleted successfully from relation table!");
                            } else {
                                System.out.println("Error deleting student information.");
                            }
                        }
                        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                            deleteStatement.setString(1, studentId);
                            int rowsAffected = deleteStatement.executeUpdate();
    
                            if (rowsAffected > 0) {
                                System.out.println("Student information deleted successfully!");
                            } else {
                                System.out.println("Error deleting student information.");
                            }
                        }
                    } else {
                        System.out.println("Deletion canceled.");
                    }
                } else {
                    System.out.println("Student not found with ID: " + studentId);
                }
            }
        }
    }

}
