package com.campus.leave.servlet;

import com.campus.leave.dao.LeaveRequestDao;
import com.campus.leave.dao.UserDao;
import com.campus.leave.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class DashboardServlet extends BaseServlet {
    private final LeaveRequestDao leaveRequestDao = new LeaveRequestDao();
    private final UserDao userDao = new UserDao();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        try {
            request.setAttribute("statusCounts", leaveRequestDao.countByStatus(user));
            request.setAttribute("recentLeaves", leaveRequestDao.findRecent(user, 6));
            if (user.isAdmin()) {
                request.setAttribute("roleCounts", userDao.countByRole());
            }
            forward(request, response, "dashboard.jsp");
        } catch (SQLException e) {
            throw new ServletException("加载首页失败", e);
        }
    }
}
