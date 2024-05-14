import java.util.List;

public class Main {
    public static void main(String[] args) {
        String connectionUrl = "jdbc:mysql://localhost:3306/employees";
        String user = "root";
        String password = "";
        EmployeeDAO employeeDAO = DAOGenerator.createDAO(EmployeeDAO.class, Employee.class, connectionUrl, user, password);

        List<Employee> foundEmployees = employeeDAO.findBySsn("100");
        for (Employee e: foundEmployees
             ) {
            System.out.println(e.getName());
        }
    }
}