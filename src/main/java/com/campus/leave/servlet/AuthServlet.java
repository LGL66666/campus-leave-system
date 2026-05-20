package com.campus.leave.servlet;

import com.campus.leave.dao.UserDao;
import com.campus.leave.model.User;
import com.campus.leave.util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class AuthServlet extends BaseServlet {
    private final UserDao userDao = new UserDao();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (currentUser(request) != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        forward(request, response, "login.jsp");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = ParamUtil.trim(request.getParameter("username"));
        String password = ParamUtil.trim(request.getParameter("password"));
        try {
            User user = userDao.login(username, password);
            if (user == null) {
                request.setAttribute("error", "用户名或密码错误");
                forward(request, response, "login.jsp");
                return;
            }
            request.getSession().setAttribute("currentUser", user);
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (SQLException e) {
            throw new ServletException("登录失败", e);
        }
    }
}
