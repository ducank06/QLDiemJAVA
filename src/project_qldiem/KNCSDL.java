/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project_qldiem;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author LENOVO
 */
public class KNCSDL {
    private static final String URL = System.getProperty("qldiem.url",
            System.getenv().getOrDefault("QLDIEM_DB_URL", "jdbc:mysql://localhost:3306/qldiem"));
    private static final String USER = System.getProperty("qldiem.user",
            System.getenv().getOrDefault("QLDIEM_DB_USER", "root"));
    private static final String PASSWORD = System.getProperty("qldiem.password",
            System.getenv().getOrDefault("QLDIEM_DB_PASSWORD", ""));

    public static Connection getConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException ex) {
            throw new IllegalStateException("Không thể kết nối MySQL. Kiểm tra MySQL, schema qldiem và cấu hình tài khoản.", ex);
        }
    }
}
