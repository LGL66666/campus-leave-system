package com.campus.leave.servlet;

import com.campus.leave.dao.ClassDao;
import com.campus.leave.dao.LeaveRequestDao;
import com.campus.leave.model.User;
import com.campus.leave.util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class StatsServlet extends BaseServlet {
    private final LeaveRequestDao leaveRequestDao = new LeaveRequestDao();
    private final ClassDao classDao = new ClassDao();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        try {
            Integer classId = ParamUtil.getInteger(request.getParameter("classId"));
            String beginDate = request.getParameter("beginDate");
            String endDate = request.getParameter("endDate");
            request.setAttribute("classes", classDao.findAll(""));
            request.setAttribute("classStats", leaveRequestDao.statsByClass(user, classId, beginDate, endDate));
            request.setAttribute("statusStats", leaveRequestDao.statsByStatus(user, classId, beginDate, endDate));
            forward(request, response, "stats/index.jsp");
        } catch (SQLException e) {
            throw new ServletException("统计数据加载失败", e);
        }
    }
}
