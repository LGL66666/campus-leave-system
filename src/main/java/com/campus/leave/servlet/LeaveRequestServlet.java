package com.campus.leave.servlet;

import com.campus.leave.dao.ClassDao;
import com.campus.leave.dao.LeaveRequestDao;
import com.campus.leave.model.ClassInfo;
import com.campus.leave.model.LeaveRequest;
import com.campus.leave.model.User;
import com.campus.leave.util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

public class LeaveRequestServlet extends BaseServlet {
    private final LeaveRequestDao leaveRequestDao = new LeaveRequestDao();
    private final ClassDao classDao = new ClassDao();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        String action = ParamUtil.trim(request.getParameter("action"));
        try {
            if ("new".equals(action)) {
                if (!requireRole(request, response, "STUDENT")) {
                    return;
                }
                Integer classId = user.getClassId();
                request.setAttribute("classInfo", classId == null ? null : classDao.findById(classId));
                forward(request, response, "leave/form.jsp");
                return;
            }
            request.setAttribute("classes", classDao.findAll(""));
            request.setAttribute("leaves", leaveRequestDao.findVisible(
                    user,
                    request.getParameter("studentKeyword"),
                    ParamUtil.getInteger(request.getParameter("classId")),
                    request.getParameter("status"),
                    request.getParameter("beginDate"),
                    request.getParameter("endDate")));
            forward(request, response, "leave/list.jsp");
        } catch (SQLException e) {
            throw new ServletException("请假记录处理失败", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = ParamUtil.trim(request.getParameter("action"));
        if ("delete".equals(action)) {
            delete(request, response);
            return;
        }
        if ("status".equals(action)) {
            updateStatus(request, response);
            return;
        }
        create(request, response);
    }

    private void create(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireRole(request, response, "STUDENT")) {
            return;
        }
        User user = currentUser(request);
        try {
            if (user.getClassId() == null) {
                request.setAttribute("error", "当前学生账号未关联班级，无法提交请假申请");
                forward(request, response, "leave/form.jsp");
                return;
            }
            ClassInfo classInfo = classDao.findById(user.getClassId());
            if (classInfo == null || classInfo.getHeadTeacherId() <= 0) {
                request.setAttribute("error", "当前班级未设置班主任审批人");
                request.setAttribute("classInfo", classInfo);
                forward(request, response, "leave/form.jsp");
                return;
            }

            Timestamp startTime = ParamUtil.parseDateTime(request.getParameter("startTime"));
            Timestamp endTime = ParamUtil.parseDateTime(request.getParameter("endTime"));
            if (!endTime.after(startTime)) {
                request.setAttribute("error", "结束时间必须晚于开始时间");
                request.setAttribute("classInfo", classInfo);
                forward(request, response, "leave/form.jsp");
                return;
            }

            LeaveRequest leave = new LeaveRequest();
            leave.setStudentId(user.getId());
            leave.setClassId(classInfo.getId());
            leave.setReviewerId(classInfo.getHeadTeacherId());
            leave.setReason(ParamUtil.trim(request.getParameter("reason")));
            leave.setStartTime(startTime);
            leave.setEndTime(endTime);
            leave.setDays(calculateDays(startTime, endTime));
            leaveRequestDao.create(leave);
            redirect(request, response, "/leaves", "请假申请已提交，请等待班主任审批");
        } catch (Exception e) {
            throw new ServletException("提交请假申请失败", e);
        }
    }

    private void updateStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireRole(request, response, "ADMIN")) {
            return;
        }
        try {
            int id = ParamUtil.getInt(request.getParameter("id"), 0);
            String status = ParamUtil.trim(request.getParameter("status"));
            String opinion = ParamUtil.trim(request.getParameter("opinion"));
            leaveRequestDao.updateStatusByAdmin(id, status, opinion);
            redirect(request, response, "/leaves", "请假状态已更新");
        } catch (SQLException e) {
            throw new ServletException("更新请假状态失败", e);
        }
    }

    private void delete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireRole(request, response, "ADMIN")) {
            return;
        }
        try {
            int id = ParamUtil.getInt(request.getParameter("id"), 0);
            leaveRequestDao.delete(id);
            redirect(request, response, "/leaves", "请假记录已删除");
        } catch (SQLException e) {
            throw new ServletException("删除请假记录失败", e);
        }
    }

    private double calculateDays(Timestamp startTime, Timestamp endTime) {
        double hours = (endTime.getTime() - startTime.getTime()) / 3600000.0;
        double days = hours / 24.0;
        return Math.round(Math.max(days, 0.5) * 10.0) / 10.0;
    }
}
