import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class BaseRepository {
    private Connection conn;
    private String url = "jdbc:mysql://localhost:3306/employees";
    private String user = "root";
    private String password = "";

    public BaseRepository() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conn = DriverManager.getConnection(url,user,password);
            System.out.println("Connection established");

        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Connection failes");
        }
    }
}
