package com.myschool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;


public class CourseOperations {
    public static void updateCourseDetails(Connection connection, List<Course> courses) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO courses(id, name) VALUES (?, ?)")) {
            for (Course course : courses) {
                preparedStatement.setString(1, course.getId());
                preparedStatement.setString(2, course.getName());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
    }

    public static String getCourseId(Connection connection, String courseName) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT id FROM courses WHERE name = ?")) {
            preparedStatement.setString(1, courseName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("id");
                } else {
                    return null; // Course not found
                }
            }
        }
    }
    
    public static void displayCourseInfo(Connection connection, String courseId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM courses WHERE id = ?")) {
            preparedStatement.setString(1, courseId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Course ID: " + resultSet.getString("id"));
                    System.out.println("Course Name: " + resultSet.getString("name"));
                } else {
                    System.out.println("Course not found with ID: " + courseId);
                }
            }
        }
    }
    
    public static void updateCourseInfo(Connection connection, String courseId) throws SQLException {
        String selectQuery = "SELECT * FROM courses WHERE id = ?";
        String updateQuery = "UPDATE courses SET name = ? WHERE id = ?";
    
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setString(1, courseId);
    
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Existing course details
                    System.out.println("Current Course Details:");
                    System.out.println("Course ID: " + resultSet.getString("id"));
                    System.out.println("1. Course Name: " + resultSet.getString("name"));
    
                    // Update options
                    System.out.println("Choose the information to update (1), or enter any other key to skip:");
                    Scanner scanner = new Scanner(System.in);
                    int updateChoice = scanner.nextInt();
    
                    if (updateChoice == 1) {
                        scanner.nextLine();
                        System.out.print("Enter the new value: ");
                        String newValue = scanner.nextLine();
    
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setString(1, newValue);
                            updateStatement.setString(2, courseId);
                            updateStatement.executeUpdate();
    
                            System.out.println("Course information updated successfully!");
                        }
                    } else {
                        System.out.println("No updates performed.");
                    }
                } else {
                    System.out.println("Course not found with ID: " + courseId);
                }
            }
        }
    }    
    
    public static void deleteCourseInfo(Connection connection, String courseId) throws SQLException {
        String selectQuery = "SELECT * FROM courses WHERE id = ?";
        String deleteQuery = "DELETE FROM courses WHERE id = ?";
        String referenceDelete = "DELETE FROM students_courses WHERE c_id = ?";
    
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setString(1, courseId);
    
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Existing course details
                    System.out.println("Course Details to be Deleted:");
                    System.out.println("Course ID: " + resultSet.getString("id"));
                    System.out.println("Course Name: " + resultSet.getString("name"));
    
                    // Confirm deletion
                    System.out.println("Are you sure you want to delete this course? (Y/N): ");
                    Scanner scanner = new Scanner(System.in);
                    String confirmDelete = scanner.next();
    
                    if (confirmDelete.equalsIgnoreCase("Y")) {
                        try(PreparedStatement deleteReference=connection.prepareStatement(referenceDelete)){
                            deleteReference.setString(1, courseId);
                            int rowsAffected = deleteReference.executeUpdate();
    
                            if (rowsAffected > 0) {
                                System.out.println("Course information deleted successfully from relation table!");
                            } else {
                                System.out.println("Error deleting course information.");
                            }
                        }
                        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                            deleteStatement.setString(1, courseId);
                            int rowsAffected = deleteStatement.executeUpdate();
    
                            if (rowsAffected > 0) {
                                System.out.println("Course information deleted successfully!");
                            } else {
                                System.out.println("Error deleting course information.");
                            }
                        }
                    } else {
                        System.out.println("Deletion canceled.");
                    }
                } else {
                    System.out.println("Course not found with ID: " + courseId);
                }
            }
        }
    }
}
