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

        employeeDAO.deleteByNameAndSurname("Ana", "Popa");

        Employee e = new Employee();
        e.setAge(24);
        e.setSsn(7);
        e.setName("Ioana");
        e.setSurname("Maria");
        employeeDAO.save(e);

        BookDAO bookDAO = DAOGenerator.createDAO(BookDAO.class, Book.class, connectionUrl, user, password);
        List<Book> books = bookDAO.findByYearAndAuthor(2000, "auth2000");
        for(Book b: books){
            System.out.println(b.getTitle());
        }


    }
}