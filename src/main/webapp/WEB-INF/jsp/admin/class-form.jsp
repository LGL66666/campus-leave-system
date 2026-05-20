<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.campus.leave.model.ClassInfo" %>
<%@ page import="com.campus.leave.model.College" %>
<%@ page import="com.campus.leave.model.User" %>
<%
    request.setAttribute("pageTitle", "班级信息编辑");
%>
<%@ include file="../common/header.jsp" %>
<%
    ClassInfo classInfo = (ClassInfo) request.getAttribute("classInfo");
    if (classInfo == null) {
        classInfo = new ClassInfo();
    }
    List colleges = (List) request.getAttribute("colleges");
    List teachers = (List) request.getAttribute("teachers");
%>
<div class="panel">
    <form method="post" action="<%= ctx %>/admin/classes">
        <input type="hidden" name="id" value="<%= classInfo.getId() %>">
        <div class="form-grid">
            <div class="field">
                <label>所属学院</label>
                <select name="collegeId" required>
                    <option value="">请选择学院</option>
                    <% if (colleges != null) {
                        for (Object obj : colleges) {
                            College college = (College) obj;
                    %>
                    <option value="<%= college.getId() %>" <%= college.getId() == classInfo.getCollegeId() ? "selected" : "" %>>
                        <%= college.getName() %>
                    </option>
                    <% }} %>
                </select>
            </div>
            <div class="field">
                <label>班级名称</label>
                <input name="name" required maxlength="80" value="<%= classInfo.getName() == null ? "" : classInfo.getName() %>">
            </div>
            <div class="field">
                <label>班主任</label>
                <select name="headTeacherId" required>
                    <option value="">请选择班主任</option>
                    <% if (teachers != null) {
                        for (Object obj : teachers) {
                            User teacher = (User) obj;
                    %>
                    <option value="<%= teacher.getId() %>" <%= teacher.getId() == classInfo.getHeadTeacherId() ? "selected" : "" %>>
                        <%= teacher.getRealName() %>
                    </option>
                    <% }} %>
                </select>
            </div>
        </div>
        <br>
        <div class="actions">
            <button class="btn" type="submit">保存</button>
            <a class="btn secondary" href="<%= ctx %>/admin/classes">返回</a>
        </div>
    </form>
</div>
<%@ include file="../common/footer.jsp" %>
