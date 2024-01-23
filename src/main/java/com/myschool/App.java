package com.myschool;

import java.sql.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class DatabaseConnection {
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/school";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }
}

class Student {
    private String id;
    private String firstName;
    private String lastName;
    private Date dob;

    public Student(String id, String firstName, String lastName, Date dob) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
    }

    // Getter methods

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getDob() {
        return dob;
    }
}

class Course {
    private String id;
    private String name;

    Course(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getter methods

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

class StudentCourse {
    private String studentId;
    private String courseId;

    StudentCourse(String studentId, String courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    // Getter methods

    public String getStudentId() {
        return studentId;
    }

    public String getCourseId() {
        return courseId;
    }
}




public class App {
    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            Scanner scanner = new Scanner(System.in);
            int choice;
            do{
                System.out.println("Choose an option:");
                System.out.println("1. Enter new student details");
                System.out.println("2. Enter new course details");
                System.out.println("3. See student info");
                System.out.println("4. See course info");
                System.out.println("5. Update student info");
                System.out.println("6. Update course info");
                System.out.println("7. Delete student info");
                System.out.println("8. Delete course info");
                System.out.println("9. End system");
                choice = scanner.nextInt();
            
            if (choice == 1) {
                // Enter student details
                System.out.println("Enter student details:");
                System.out.print("ID: ");
                String studentId = scanner.next();
                System.out.print("First Name: ");
                String firstName = scanner.next();
                System.out.print("Last Name: ");
                String lastName = scanner.next();
                System.out.print("Date of Birth (YYYY-MM-DD): ");
                String dobStr = scanner.next();
                Date dob = Date.valueOf(dobStr);
                
                Student student = new Student(studentId, firstName, lastName, dob);
                
                // Enter course details
                BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));
                
                System.out.println("Enter the list of course names (comma-separated) that the student wants to enroll:");
                // String courseNamesInput = scanner.next();
                String[] courseNames = bi.readLine().split(",");
                
                List<StudentCourse> studentCourses = new ArrayList<>();
                
                for (String courseName : courseNames) {
                    // Map course name to get the corresponding course ID from the courses table
                    String courseId = CourseOperations.getCourseId(connection, courseName.trim());
                    
                    // Check if the course exists
                    if (courseId != null) {
                        StudentCourse studentCourse = new StudentCourse(studentId, courseId);
                        studentCourses.add(studentCourse);
                    } else {
                        System.out.println("Course not found: " + courseName);
                    }
                }

                // Update database
                StudentOperations.updateStudentDetails(connection, student);
                StudentOperations.updateStudentCourseDetails(connection, studentCourses);

            } else if (choice == 2) {
                // Enter course details
                System.out.println("Enter course details:");
                System.out.print("ID: ");
                String courseId = scanner.next();
                System.out.print("Name: ");
                String courseName = scanner.next();
                
                Course course = new Course(courseId, courseName);

                // Update database
                CourseOperations.updateCourseDetails(connection, List.of(course));
            }else if (choice == 3) {
                // See student info
                System.out.print("Enter student ID to see details: ");
                String studentId = scanner.next();
                StudentOperations.displayStudentInfo(connection, studentId);
            } else if (choice == 4) {
                // See course info
                System.out.print("Enter course ID to see details: ");
                String courseId = scanner.next();
                CourseOperations.displayCourseInfo(connection, courseId);
            } else if (choice == 5) {
                // Update student info
                System.out.print("Enter student ID to update details: ");
                String studentId = scanner.next();
                StudentOperations.updateStudentInfo(connection, studentId);
            } else if (choice == 6) {
                // Update course info
                System.out.print("Enter course ID to update details: ");
                String courseId = scanner.next();
                CourseOperations.updateCourseInfo(connection, courseId);
            } else if (choice == 7) {
                // Delete student info
                System.out.print("Enter student ID to delete details: ");
                String studentId = scanner.next();
                StudentOperations.deleteStudentInfo(connection, studentId);
            } else if (choice == 8) {
                // Delete course info
                System.out.print("Enter course ID to delete details: ");
                String courseId = scanner.next();
                CourseOperations.deleteCourseInfo(connection, courseId);
            } else if (choice == 9) {
                // End system
                System.out.println("Exiting system.");
                break;
            } else {
                System.out.println("Invalid choice. Exiting.");
            }
        }while(choice!=9);
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    

    
}
