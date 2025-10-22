package learning.creational.abstractfactory.uses;

import java.sql.*;

public class Demo {
  public static void main(String[] args) throws SQLException {
    // DriverManager acts as an abstract factory selector
    Connection conn =
        DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "user", "pass");

    // Different drivers are different factories:
    // - MySQL driver creates MySQL-specific Connection, Statement, ResultSet
    // - PostgreSQL driver creates PostgreSQL-specific versions
    // - Oracle driver creates Oracle-specific versions

    Statement stmt = conn.createStatement(); // Factory method
    PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users");
    CallableStatement cstmt = conn.prepareCall("{call myProcedure}");
  }
}
