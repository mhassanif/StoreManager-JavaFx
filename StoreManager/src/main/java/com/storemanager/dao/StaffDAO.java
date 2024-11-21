package com.storemanager.dao;

import com.storemanager.db.DBconnector;
import com.storemanager.model.users.User;
import com.storemanager.model.users.Admin;
import com.storemanager.model.users.Manager;
import com.storemanager.model.users.WarehouseStaff;

import java.sql.*;

public class StaffDAO {

    public User read(int staffId) {
        User user = null;

        String staffQuery = "SELECT s.staff_id, s.user_id, s.position, u.name, u.email, u.password, u.role, u.address, u.phone "
                + "FROM STAFF s "
                + "INNER JOIN USERS u ON s.user_id = u.user_id "
                + "WHERE s.staff_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(staffQuery)) {
            stmt.setInt(1, staffId);
            ResultSet rs = stmt.executeQuery();

            // If the result set contains a record, process it
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String role = rs.getString("role");
                String address = rs.getString("address");
                String phone = rs.getString("phone");
                String position = rs.getString("position");

                // Instantiate the user object based on the position from STAFF table
                user = switch (position) {
                    case "Admin" -> new Admin(userId, name, email, password, address, phone);
                    case "Manager" -> new Manager(userId, name, email, password, address, phone);
                    case "WarehouseStaff" -> new WarehouseStaff(userId, name, email, password, address, phone);
                    default -> throw new IllegalArgumentException("Unknown position: " + position);
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;  // Return the Staff object (could be Admin, Manager, or WarehouseStaff)
    }
}
