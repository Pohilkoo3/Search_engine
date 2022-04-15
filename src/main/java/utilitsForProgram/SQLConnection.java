package utilitsForProgram;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class SQLConnection

{
    private static Connection connection;

    private static String dbName = "search_engine2";
    private static String dbUser = "root";
    private static String dbPass = "Oleg09061979$";


    public static Connection getConnectionDataBase() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + dbName +
                                "?user=" + dbUser + "&password=" + dbPass);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static Connection getConnectionServer() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" +
                                "?user=" + dbUser + "&password=" + dbPass);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static String getDbName() {
        return dbName;
    }
}
