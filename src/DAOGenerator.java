import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOGenerator {
    public static <T> T createDAO(Class<T> daoInterface, Class<?> entityClass, String connectionUrl, String user, String password) {
        return (T) Proxy.newProxyInstance(daoInterface.getClassLoader(), new Class<?>[] { daoInterface },
                (proxy, method, args) -> {

                    Connection conn = null;
                    PreparedStatement stmt = null;
                    ResultSet rs = null;
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        conn = DriverManager.getConnection(connectionUrl,user,password);
                        String methodName = method.getName();
                        String[] tokens = methodName.split("(?=[A-Z])"); // Split by camel case
                        String operation = tokens[0].toLowerCase(); // find, save, or delete

                        if (operation.equals("find")) {
                            // Construct SELECT query based on method name
                            StringBuilder query = new StringBuilder("SELECT * FROM ")
                                    .append(entityClass.getSimpleName().toLowerCase()) //Employee table
                                    .append(" WHERE ");
                            for (int i = 2; i < tokens.length; i += 2) {
                                if (i > 2) {
                                    query.append(" AND ");
                                }
                                String attributeName = tokens[i].toLowerCase();
                                query.append(attributeName).append("=?");
                            }

                            stmt = conn.prepareStatement(query.toString());
                            for (int i = 0; i < args.length; i++) {
                                stmt.setObject(i + 1, args[i]);
                            }

                            rs = stmt.executeQuery();
                            List<Object> resultList = new ArrayList<>();
                            while (rs.next()) {
                                // Map ResultSet to DTO object (entityClass)
                                Object dto = entityClass.getDeclaredConstructor().newInstance();
                                for (Field field : entityClass.getDeclaredFields()) {
                                    String fieldName = field.getName();
                                    Object value = rs.getObject(fieldName);
                                    field.setAccessible(true);
                                    field.set(dto, value);
                                }
                                resultList.add(dto);
                            }
                            return resultList;
                        } else if (operation.equals("delete")) {
                            // Implement delete logic
                        } else if (operation.equals("save")) {
                            // Implement save logic
                        }
                    } finally {
                        if (rs != null) rs.close();
                        if (stmt != null) stmt.close();
                        if (conn != null) conn.close();
                    }
                    return null;
                });
    }
}
