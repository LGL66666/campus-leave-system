package com.campus.leave.servlet;

import com.campus.leave.dao.ClassDao;
import com.campus.leave.dao.LeaveRequestDao;
import com.campus.leave.model.LeaveRequest;
import com.campus.leave.model.User;
import com.campus.leave.util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class ApprovalServlet extends BaseServlet {
    private final LeaveRequestDao leaveRequestDao = new LeaveRequestDao();
    private final ClassDao classDao = new ClassDao();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireRole(request, response, "TEACHER")) {
            return;
        }
        User user = currentUser(request);
        String status = ParamUtil.trim(request.getParameter("status"));
        if (status.length() == 0) {
            status = LeaveRequest.STATUS_PENDING;
        }
        try {
            request.setAttribute("classes", classDao.findAll(""));
            request.setAttribute("status", status);
            request.setAttribute("leaves", leaveRequestDao.findVisible(
                    user,
                    request.getParameter("studentKeyword"),
                    ParamUtil.getInteger(request.getParameter("classId")),
                    status,
                    request.getParameter("beginDate"),
                    request.getParameter("endDate")));
            forward(request, response, "teacher/approval-list.jsp");
        } catch (SQLException e) {
            throw new ServletException("审批列表加载失败", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireRole(request, response, "TEACHER")) {
            return;
        }
        String decision = ParamUtil.trim(request.getParameter("decision"));
        String status = "approve".equals(decision) ? LeaveRequest.STATUS_APPROVED : LeaveRequest.STATUS_REJECTED;
        try {
            boolean ok = leaveRequestDao.review(
                    ParamUtil.getInt(request.getParameter("id"), 0),
                    currentUser(request).getId(),
                    status,
                    ParamUtil.trim(request.getParameter("opinion")));
            redirect(request, response, "/teacher/approvals", ok ? "审批已提交" : "该申请已处理或无权限审批");
        } catch (SQLException e) {
            throw new ServletException("提交审批失败", e);
        }
    }
}
