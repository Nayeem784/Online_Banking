

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionProvider {
    static Connection con; // Global Connection Object

    public static Connection getConnection() {
        try {
            // Load PostgreSQL JDBC Driver
            String postgresJDBCDriver = "org.postgresql.Driver";
            Class.forName(postgresJDBCDriver);

            // PostgreSQL connection URL
            String url = "jdbc:postgresql://localhost:5432/bank"; // make sure 'bank' DB exists
            String user = "postgres";    // your PostgreSQL username
            String pass = "your_password"; // your PostgreSQL password

            con = DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            System.out.println("Connection Failed!");
            e.printStackTrace(); // for debugging
        }
        return con;
    }
}
