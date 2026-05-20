package com.campus.leave.dao;

import com.campus.leave.model.LeaveRequest;
import com.campus.leave.model.StatItem;
import com.campus.leave.model.User;
import com.campus.leave.util.ParamUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LeaveRequestDao {
    public void create(LeaveRequest request) throws SQLException {
        String sql = "INSERT INTO leave_requests(student_id, class_id, reviewer_id, reason, start_time, end_time, days, status) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, request.getStudentId());
            ps.setInt(2, request.getClassId());
            ps.setInt(3, request.getReviewerId());
            ps.setString(4, request.getReason());
            ps.setTimestamp(5, request.getStartTime());
            ps.setTimestamp(6, request.getEndTime());
            ps.setDouble(7, request.getDays());
            ps.setString(8, LeaveRequest.STATUS_PENDING);
            ps.executeUpdate();
        }
    }

    public List<LeaveRequest> findVisible(User viewer, String studentKeyword, Integer classId,
                                          String status, String beginDate, String endDate) throws SQLException {
        StringBuilder sql = new StringBuilder(baseSql()).append(" WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        appendViewerScope(sql, params, viewer);
        appendFilters(sql, params, studentKeyword, classId, status, beginDate, endDate);
        sql.append(" ORDER BY lr.apply_time DESC, lr.id DESC");

        List<LeaveRequest> list = new ArrayList<LeaveRequest>();
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

    public List<LeaveRequest> findRecent(User viewer, int limit) throws SQLException {
        StringBuilder sql = new StringBuilder(baseSql()).append(" WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        appendViewerScope(sql, params, viewer);
        sql.append(" ORDER BY lr.apply_time DESC, lr.id DESC LIMIT ?");
        params.add(limit);

        List<LeaveRequest> list = new ArrayList<LeaveRequest>();
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

    public boolean review(int id, int teacherId, String status, String opinion) throws SQLException {
        String sql = "UPDATE leave_requests SET status=?, review_opinion=?, review_time=NOW() "
                + "WHERE id=? AND reviewer_id=? AND status=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, opinion);
            ps.setInt(3, id);
            ps.setInt(4, teacherId);
            ps.setString(5, LeaveRequest.STATUS_PENDING);
            return ps.executeUpdate() > 0;
        }
    }

    public int countByUserId(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM leave_requests WHERE student_id=? OR reviewer_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM leave_requests WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public boolean updateStatusByAdmin(int id, String status, String opinion) throws SQLException {
        String sql = "UPDATE leave_requests SET status=?, review_opinion=?, review_time=NOW() WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, opinion);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Map<String, Integer> countByStatus(User viewer) throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<String, Integer>();
        map.put(LeaveRequest.STATUS_PENDING, 0);
        map.put(LeaveRequest.STATUS_APPROVED, 0);
        map.put(LeaveRequest.STATUS_REJECTED, 0);

        StringBuilder sql = new StringBuilder("SELECT lr.status, COUNT(*) AS total FROM leave_requests lr "
                + "LEFT JOIN classes c ON lr.class_id=c.id WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        appendViewerScopeForSimpleTable(sql, params, viewer);
        sql.append(" GROUP BY lr.status");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            fill(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("status"), rs.getInt("total"));
                }
            }
        }
        return map;
    }

    public List<StatItem> statsByClass(User viewer, Integer classId, String beginDate, String endDate)
            throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT c.name AS name, COUNT(lr.id) AS total, COALESCE(SUM(lr.days), 0) AS days "
                + "FROM leave_requests lr "
                + "JOIN classes c ON lr.class_id=c.id WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        appendViewerScopeForSimpleTable(sql, params, viewer);
        appendSimpleFilters(sql, params, classId, beginDate, endDate);
        sql.append(" GROUP BY c.name ORDER BY total DESC, c.name ASC");
        return queryStats(sql.toString(), params);
    }

    public List<StatItem> statsByStatus(User viewer, Integer classId, String beginDate, String endDate)
            throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT lr.status AS name, COUNT(lr.id) AS total, COALESCE(SUM(lr.days), 0) AS days "
                + "FROM leave_requests lr "
                + "JOIN classes c ON lr.class_id=c.id WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        appendViewerScopeForSimpleTable(sql, params, viewer);
        appendSimpleFilters(sql, params, classId, beginDate, endDate);
        sql.append(" GROUP BY lr.status ORDER BY total DESC");
        return queryStats(sql.toString(), params);
    }

    private List<StatItem> queryStats(String sql, List<Object> params) throws SQLException {
        List<StatItem> list = new ArrayList<StatItem>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fill(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new StatItem(rs.getString("name"), rs.getInt("total"), rs.getDouble("days")));
                }
            }
        }
        return list;
    }

    private void appendViewerScope(StringBuilder sql, List<Object> params, User viewer) {
        if (viewer == null) {
            sql.append(" AND 1=0");
        } else if (viewer.isStudent()) {
            sql.append(" AND lr.student_id=?");
            params.add(viewer.getId());
        } else if (viewer.isTeacher()) {
            sql.append(" AND (lr.reviewer_id=? OR c.head_teacher_id=?)");
            params.add(viewer.getId());
            params.add(viewer.getId());
        }
    }

    private void appendViewerScopeForSimpleTable(StringBuilder sql, List<Object> params, User viewer) {
        if (viewer == null) {
            sql.append(" AND 1=0");
        } else if (viewer.isStudent()) {
            sql.append(" AND lr.student_id=?");
            params.add(viewer.getId());
        } else if (viewer.isTeacher()) {
            sql.append(" AND (lr.reviewer_id=? OR c.head_teacher_id=?)");
            params.add(viewer.getId());
            params.add(viewer.getId());
        }
    }

    private void appendFilters(StringBuilder sql, List<Object> params, String studentKeyword, Integer classId,
                               String status, String beginDate, String endDate) {
        String key = ParamUtil.trim(studentKeyword);
        if (key.length() > 0) {
            sql.append(" AND (stu.real_name LIKE ? OR stu.username LIKE ?)");
            params.add("%" + key + "%");
            params.add("%" + key + "%");
        }
        appendSimpleFilters(sql, params, classId, beginDate, endDate);
        String statusValue = ParamUtil.trim(status);
        if (statusValue.length() > 0) {
            sql.append(" AND lr.status=?");
            params.add(statusValue);
        }
    }

    private void appendSimpleFilters(StringBuilder sql, List<Object> params, Integer classId,
                                     String beginDate, String endDate) {
        if (classId != null && classId > 0) {
            sql.append(" AND lr.class_id=?");
            params.add(classId);
        }
        String begin = ParamUtil.trim(beginDate);
        if (begin.length() > 0) {
            sql.append(" AND lr.start_time>=?");
            params.add(Timestamp.valueOf(begin + " 00:00:00"));
        }
        String end = ParamUtil.trim(endDate);
        if (end.length() > 0) {
            sql.append(" AND lr.end_time<=?");
            params.add(Timestamp.valueOf(end + " 23:59:59"));
        }
    }

    private String baseSql() {
        return "SELECT lr.id, lr.student_id, lr.class_id, lr.reviewer_id, lr.reason, lr.start_time, lr.end_time, "
                + "lr.days, lr.status, lr.review_opinion, lr.apply_time, lr.review_time, "
                + "stu.real_name AS student_name, c.name AS class_name, co.name AS college_name, "
                + "teacher.real_name AS reviewer_name "
                + "FROM leave_requests lr "
                + "JOIN users stu ON lr.student_id=stu.id "
                + "JOIN classes c ON lr.class_id=c.id "
                + "LEFT JOIN colleges co ON c.college_id=co.id "
                + "LEFT JOIN users teacher ON lr.reviewer_id=teacher.id";
    }

    private LeaveRequest map(ResultSet rs) throws SQLException {
        LeaveRequest request = new LeaveRequest();
        request.setId(rs.getInt("id"));
        request.setStudentId(rs.getInt("student_id"));
        request.setClassId(rs.getInt("class_id"));
        request.setReviewerId(rs.getInt("reviewer_id"));
        request.setReason(rs.getString("reason"));
        request.setStartTime(rs.getTimestamp("start_time"));
        request.setEndTime(rs.getTimestamp("end_time"));
        request.setDays(rs.getDouble("days"));
        request.setStatus(rs.getString("status"));
        request.setReviewOpinion(rs.getString("review_opinion"));
        request.setApplyTime(rs.getTimestamp("apply_time"));
        request.setReviewTime(rs.getTimestamp("review_time"));
        request.setStudentName(rs.getString("student_name"));
        request.setClassName(rs.getString("class_name"));
        request.setCollegeName(rs.getString("college_name"));
        request.setReviewerName(rs.getString("reviewer_name"));
        return request;
    }

    private void fill(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }
}
