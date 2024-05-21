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
                        checkMethodName(method);
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        conn = DriverManager.getConnection(connectionUrl,user,password);
                        String methodName = method.getName();
                        String[] tokens = methodName.split("(?=[A-Z])"); // Split by camel case
                        String operation = tokens[0].toLowerCase(); // find, save, or delete

                        if (operation.equals("find")) {
                            StringBuilder query = new StringBuilder("SELECT * FROM ")
                                    .append(entityClass.getSimpleName().toLowerCase())
                                    .append(" WHERE ");
                            //Select * From TABLE where ... =? AND ...=?
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
                            StringBuilder query = new StringBuilder("DELETE FROM ")
                                    .append(entityClass.getSimpleName().toLowerCase()) //Table name
                                    .append(" WHERE ");
                            //Delete from Table name where...

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

                            int rowsAffected = stmt.executeUpdate();
                            System.out.println("Rows affected: " + rowsAffected);

                        } else if (operation.equals("save")) {
                            Object entity = args[0];
                            Field[] fields = entityClass.getDeclaredFields();
                            StringBuilder query = new StringBuilder("INSERT INTO ")
                                    .append(entityClass.getSimpleName().toLowerCase()) // Table name
                                    .append(" (");
                            StringBuilder values = new StringBuilder("VALUES (");

                            for (int i = 0; i < fields.length; i++) {
                                Field field = fields[i];
                                field.setAccessible(true);
                                if (i > 0) {
                                    query.append(", ");
                                    values.append(", ");
                                }
                                query.append(field.getName());
                                values.append("?");
                            }
                            query.append(") ");
                            values.append(")");
                            query.append(values);

                            stmt = conn.prepareStatement(query.toString());

                            for (int i = 0; i < fields.length; i++) {
                                Field field = fields[i];
                                stmt.setObject(i + 1, field.get(entity));
                            }
                            int rowsAffected = stmt.executeUpdate();
                            System.out.println("Rows affected: " + rowsAffected);
                            }
                    } catch(Exception e){
                       e.printStackTrace();
                    }finally {
                        if (rs != null) rs.close();
                        if (stmt != null) stmt.close();
                        if (conn != null) conn.close();
                    }
                    return null;
                });
    }
    public static void checkMethodName(Method method){

        String methodName = method.getName();
        String[] tokens = methodName.split("(?=[A-Z])"); // Split by camel case
        for (int i = 0; i < tokens.length; i++) {
            String lowerCaseToken = tokens[i].toLowerCase();
            tokens[i] = lowerCaseToken;
        }
        String operation = tokens[0];
        if(tokens.length == 1 && operation.equals("save"))
            return;
        Boolean correct_operation = operation.equals("find") | operation.equals("delete");
        if(!correct_operation)
            throw new UnsupportedOperationException();
        if(!tokens[1].equals("by"))
            throw new UnsupportedOperationException();
        for (int i = 3; i < tokens.length; i += 2) {
            String attributeName = tokens[i].toLowerCase();
            if(!attributeName.equals("and"))
                throw new UnsupportedOperationException();
        }

    }
}
