package com.campus.leave.servlet;

import com.campus.leave.dao.ClassDao;
import com.campus.leave.dao.LeaveRequestDao;
import com.campus.leave.dao.UserDao;
import com.campus.leave.model.User;
import com.campus.leave.util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class UserServlet extends BaseServlet {
    private final UserDao userDao = new UserDao();
    private final ClassDao classDao = new ClassDao();
    private final LeaveRequestDao leaveRequestDao = new LeaveRequestDao();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireRole(request, response, "ADMIN")) {
            return;
        }
        String action = ParamUtil.trim(request.getParameter("action"));
        try {
            if ("new".equals(action) || "edit".equals(action)) {
                Integer id = ParamUtil.getInteger(request.getParameter("id"));
                request.setAttribute("user", id == null ? new User() : userDao.findById(id));
                request.setAttribute("classes", classDao.findAll(""));
                forward(request, response, "admin/user-form.jsp");
                return;
            }
            request.setAttribute("users", userDao.findAll(request.getParameter("keyword"), request.getParameter("role")));
            forward(request, response, "admin/user-list.jsp");
        } catch (SQLException e) {
            throw new ServletException("用户信息处理失败", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireRole(request, response, "ADMIN")) {
            return;
        }
        String action = ParamUtil.trim(request.getParameter("action"));
        try {
            if ("delete".equals(action)) {
                int id = ParamUtil.getInt(request.getParameter("id"), 0);
                if (currentUser(request).getId() == id) {
                    redirect(request, response, "/admin/users", "当前登录账号不能删除");
                    return;
                }
                if (leaveRequestDao.countByUserId(id) > 0) {
                    redirect(request, response, "/admin/users", "该用户还有请假记录未删除，请先到请假记录页面删除相关记录后再删除用户");
                    return;
                }
                userDao.delete(id);
                redirect(request, response, "/admin/users", "用户已删除");
                return;
            }
            User user = new User();
            user.setId(ParamUtil.getInt(request.getParameter("id"), 0));
            user.setUsername(ParamUtil.trim(request.getParameter("username")));
            user.setPassword(ParamUtil.trim(request.getParameter("password")));
            user.setRealName(ParamUtil.trim(request.getParameter("realName")));
            user.setRole(ParamUtil.trim(request.getParameter("role")));
            user.setPhone(ParamUtil.trim(request.getParameter("phone")));
            if ("STUDENT".equals(user.getRole())) {
                user.setClassId(ParamUtil.getInteger(request.getParameter("classId")));
            }
            userDao.save(user);
            redirect(request, response, "/admin/users", "用户信息已保存");
        } catch (SQLException e) {
            throw new ServletException("保存用户失败", e);
        }
    }
}
