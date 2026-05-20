package com.campus.leave.dao;

import com.campus.leave.model.College;
import com.campus.leave.util.ParamUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CollegeDao {
    public List<College> findAll(String keyword) throws SQLException {
        String key = ParamUtil.trim(keyword);
        StringBuilder sql = new StringBuilder("SELECT id, code, name FROM colleges");
        List<Object> params = new ArrayList<Object>();
        if (key.length() > 0) {
            sql.append(" WHERE code LIKE ? OR name LIKE ?");
            params.add("%" + key + "%");
            params.add("%" + key + "%");
        }
        sql.append(" ORDER BY id DESC");
        List<College> list = new ArrayList<College>();
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

    public College findById(int id) throws SQLException {
        String sql = "SELECT id, code, name FROM colleges WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public void save(College college) throws SQLException {
        if (college.getId() > 0) {
            update(college);
        } else {
            insert(college);
        }
    }

    private void insert(College college) throws SQLException {
        String sql = "INSERT INTO colleges(code, name) VALUES(?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, college.getCode());
            ps.setString(2, college.getName());
            ps.executeUpdate();
        }
    }

    private void update(College college) throws SQLException {
        String sql = "UPDATE colleges SET code=?, name=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, college.getCode());
            ps.setString(2, college.getName());
            ps.setInt(3, college.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM colleges WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private College map(ResultSet rs) throws SQLException {
        College college = new College();
        college.setId(rs.getInt("id"));
        college.setCode(rs.getString("code"));
        college.setName(rs.getString("name"));
        return college;
    }

    private void fill(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }
}
