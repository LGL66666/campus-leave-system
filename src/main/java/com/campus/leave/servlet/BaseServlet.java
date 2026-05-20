package com.campus.leave.servlet;

import com.campus.leave.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

public abstract class BaseServlet extends HttpServlet {
    protected User currentUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute("currentUser");
    }

    protected boolean requireRole(HttpServletRequest request, HttpServletResponse response, String... roles)
            throws IOException {
        User user = currentUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        for (String role : roles) {
            if (role.equals(user.getRole())) {
                return true;
            }
        }
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "没有权限访问该功能");
        return false;
    }

    protected void forward(HttpServletRequest request, HttpServletResponse response, String jsp)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/" + jsp).forward(request, response);
    }

    protected void redirect(HttpServletRequest request, HttpServletResponse response, String path, String message)
            throws IOException {
        String url = request.getContextPath() + path;
        if (message != null && message.length() > 0) {
            url += (url.indexOf('?') >= 0 ? "&" : "?") + "msg=" + URLEncoder.encode(message, "UTF-8");
        }
        response.sendRedirect(url);
    }
}
