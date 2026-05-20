package com.campus.leave.dao;

import com.campus.leave.model.User;
import com.campus.leave.util.ParamUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserDao {
    public User login(String username, String password) throws SQLException {
        String sql = baseSql() + " WHERE u.username=? AND u.password=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ParamUtil.trim(username));
            ps.setString(2, ParamUtil.trim(password));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public User findById(int id) throws SQLException {
        String sql = baseSql() + " WHERE u.id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<User> findAll(String keyword, String role) throws SQLException {
        String key = ParamUtil.trim(keyword);
        String roleValue = ParamUtil.trim(role);
        StringBuilder sql = new StringBuilder(baseSql()).append(" WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        if (key.length() > 0) {
            sql.append(" AND (u.username LIKE ? OR u.real_name LIKE ? OR u.phone LIKE ?)");
            params.add("%" + key + "%");
            params.add("%" + key + "%");
            params.add("%" + key + "%");
        }
        if (roleValue.length() > 0) {
            sql.append(" AND u.role=?");
            params.add(roleValue);
        }
        sql.append(" ORDER BY u.id DESC");
        List<User> list = new ArrayList<User>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            fill(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public List<User> findTeachers() throws SQLException {
        return findAll("", "TEACHER");
    }

    public void save(User user) throws SQLException {
        if (user.getId() > 0) {
            update(user);
        } else {
            insert(user);
        }
    }

    private void insert(User user) throws SQLException {
        String sql = "INSERT INTO users(username, password, real_name, role, phone, class_id) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindUser(ps, user, false);
            ps.executeUpdate();
        }
    }

    private void update(User user) throws SQLException {
        String sql = "UPDATE users SET username=?, password=?, real_name=?, role=?, phone=?, class_id=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindUser(ps, user, true);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Map<String, Integer> countByRole() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<String, Integer>();
        map.put("ADMIN", 0);
        map.put("TEACHER", 0);
        map.put("STUDENT", 0);
        String sql = "SELECT role, COUNT(*) AS total FROM users GROUP BY role";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("role"), rs.getInt("total"));
            }
        }
        return map;
    }

    private void bindUser(PreparedStatement ps, User user, boolean includeId) throws SQLException {
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getRealName());
        ps.setString(4, user.getRole());
        ps.setString(5, user.getPhone());
        if (user.getClassId() == null) {
            ps.setNull(6, Types.INTEGER);
        } else {
            ps.setInt(6, user.getClassId());
        }
        if (includeId) {
            ps.setInt(7, user.getId());
        }
    }

    private String baseSql() {
        return "SELECT u.id, u.username, u.password, u.real_name, u.role, u.phone, u.class_id, "
                + "c.name AS class_name, co.name AS college_name "
                + "FROM users u "
                + "LEFT JOIN classes c ON u.class_id=c.id "
                + "LEFT JOIN colleges co ON c.college_id=co.id";
    }

    private User map(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRealName(rs.getString("real_name"));
        user.setRole(rs.getString("role"));
        user.setPhone(rs.getString("phone"));
        int classId = rs.getInt("class_id");
        user.setClassId(rs.wasNull() ? null : classId);
        user.setClassName(rs.getString("class_name"));
        user.setCollegeName(rs.getString("college_name"));
        return user;
    }

    private void fill(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }
}
