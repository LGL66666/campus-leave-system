package com.campus.leave.servlet;

import com.campus.leave.dao.ClassDao;
import com.campus.leave.dao.CollegeDao;
import com.campus.leave.dao.UserDao;
import com.campus.leave.model.ClassInfo;
import com.campus.leave.util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class ClassServlet extends BaseServlet {
    private final ClassDao classDao = new ClassDao();
    private final CollegeDao collegeDao = new CollegeDao();
    private final UserDao userDao = new UserDao();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireRole(request, response, "ADMIN")) {
            return;
        }
        String action = ParamUtil.trim(request.getParameter("action"));
        try {
            if ("new".equals(action) || "edit".equals(action)) {
                Integer id = ParamUtil.getInteger(request.getParameter("id"));
                request.setAttribute("classInfo", id == null ? new ClassInfo() : classDao.findById(id));
                request.setAttribute("colleges", collegeDao.findAll(""));
                request.setAttribute("teachers", userDao.findTeachers());
                forward(request, response, "admin/class-form.jsp");
                return;
            }
            request.setAttribute("classes", classDao.findAll(request.getParameter("keyword")));
            forward(request, response, "admin/class-list.jsp");
        } catch (SQLException e) {
            throw new ServletException("班级信息处理失败", e);
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
                classDao.delete(ParamUtil.getInt(request.getParameter("id"), 0));
                redirect(request, response, "/admin/classes", "班级已删除");
                return;
            }
            ClassInfo info = new ClassInfo();
            info.setId(ParamUtil.getInt(request.getParameter("id"), 0));
            info.setCollegeId(ParamUtil.getInt(request.getParameter("collegeId"), 0));
            info.setName(ParamUtil.trim(request.getParameter("name")));
            info.setHeadTeacherId(ParamUtil.getInt(request.getParameter("headTeacherId"), 0));
            classDao.save(info);
            redirect(request, response, "/admin/classes", "班级信息已保存");
        } catch (SQLException e) {
            throw new ServletException("保存班级失败", e);
        }
    }
}
