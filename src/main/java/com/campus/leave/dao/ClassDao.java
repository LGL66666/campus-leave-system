package com.campus.leave.dao;

import com.campus.leave.model.ClassInfo;
import com.campus.leave.util.ParamUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClassDao {
    public List<ClassInfo> findAll(String keyword) throws SQLException {
        String key = ParamUtil.trim(keyword);
        StringBuilder sql = new StringBuilder(
                "SELECT c.id, c.college_id, c.name, c.head_teacher_id, co.name AS college_name, "
                        + "u.real_name AS head_teacher_name "
                        + "FROM classes c "
                        + "LEFT JOIN colleges co ON c.college_id=co.id "
                        + "LEFT JOIN users u ON c.head_teacher_id=u.id");
        List<Object> params = new ArrayList<Object>();
        if (key.length() > 0) {
            sql.append(" WHERE c.name LIKE ? OR co.name LIKE ? OR u.real_name LIKE ?");
            params.add("%" + key + "%");
            params.add("%" + key + "%");
            params.add("%" + key + "%");
        }
        sql.append(" ORDER BY c.id DESC");
        List<ClassInfo> list = new ArrayList<ClassInfo>();
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

    public ClassInfo findById(int id) throws SQLException {
        String sql = "SELECT c.id, c.college_id, c.name, c.head_teacher_id, co.name AS college_name, "
                + "u.real_name AS head_teacher_name "
                + "FROM classes c "
                + "LEFT JOIN colleges co ON c.college_id=co.id "
                + "LEFT JOIN users u ON c.head_teacher_id=u.id "
                + "WHERE c.id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public void save(ClassInfo info) throws SQLException {
        if (info.getId() > 0) {
            update(info);
        } else {
            insert(info);
        }
    }

    private void insert(ClassInfo info) throws SQLException {
        String sql = "INSERT INTO classes(college_id, name, head_teacher_id) VALUES(?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, info.getCollegeId());
            ps.setString(2, info.getName());
            ps.setInt(3, info.getHeadTeacherId());
            ps.executeUpdate();
        }
    }

    private void update(ClassInfo info) throws SQLException {
        String sql = "UPDATE classes SET college_id=?, name=?, head_teacher_id=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, info.getCollegeId());
            ps.setString(2, info.getName());
            ps.setInt(3, info.getHeadTeacherId());
            ps.setInt(4, info.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM classes WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private ClassInfo map(ResultSet rs) throws SQLException {
        ClassInfo info = new ClassInfo();
        info.setId(rs.getInt("id"));
        info.setCollegeId(rs.getInt("college_id"));
        info.setName(rs.getString("name"));
        info.setHeadTeacherId(rs.getInt("head_teacher_id"));
        info.setCollegeName(rs.getString("college_name"));
        info.setHeadTeacherName(rs.getString("head_teacher_name"));
        return info;
    }

    private void fill(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }
}
