package com.campus.leave.servlet;

import com.campus.leave.dao.CollegeDao;
import com.campus.leave.model.College;
import com.campus.leave.util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class CollegeServlet extends BaseServlet {
    private final CollegeDao collegeDao = new CollegeDao();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireRole(request, response, "ADMIN")) {
            return;
        }
        String action = ParamUtil.trim(request.getParameter("action"));
        try {
            if ("new".equals(action) || "edit".equals(action)) {
                Integer id = ParamUtil.getInteger(request.getParameter("id"));
                request.setAttribute("college", id == null ? new College() : collegeDao.findById(id));
                forward(request, response, "admin/college-form.jsp");
                return;
            }
            request.setAttribute("colleges", collegeDao.findAll(request.getParameter("keyword")));
            forward(request, response, "admin/college-list.jsp");
        } catch (SQLException e) {
            throw new ServletException("学院信息处理失败", e);
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
                collegeDao.delete(ParamUtil.getInt(request.getParameter("id"), 0));
                redirect(request, response, "/admin/colleges", "学院已删除");
                return;
            }
            College college = new College();
            college.setId(ParamUtil.getInt(request.getParameter("id"), 0));
            college.setCode(ParamUtil.trim(request.getParameter("code")));
            college.setName(ParamUtil.trim(request.getParameter("name")));
            collegeDao.save(college);
            redirect(request, response, "/admin/colleges", "学院信息已保存");
        } catch (SQLException e) {
            throw new ServletException("保存学院失败", e);
        }
    }
}
