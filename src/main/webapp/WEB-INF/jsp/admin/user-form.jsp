<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.campus.leave.model.User" %>
<%@ page import="com.campus.leave.model.ClassInfo" %>
<%
    request.setAttribute("pageTitle", "用户信息编辑");
%>
<%@ include file="../common/header.jsp" %>
<%
    User user = (User) request.getAttribute("user");
    if (user == null) {
        user = new User();
        user.setRole("STUDENT");
    }
    String userRole = user.getRole() == null ? "STUDENT" : user.getRole();
    List classes = (List) request.getAttribute("classes");
%>
<div class="panel">
    <form method="post" action="<%= ctx %>/admin/users">
        <input type="hidden" name="id" value="<%= user.getId() %>">
        <div class="form-grid">
            <div class="field">
                <label>用户名</label>
                <input name="username" required maxlength="40" value="<%= user.getUsername() == null ? "" : user.getUsername() %>">
            </div>
            <div class="field">
                <label>登录密码</label>
                <input name="password" required maxlength="40" value="<%= user.getPassword() == null ? "" : user.getPassword() %>">
            </div>
            <div class="field">
                <label>姓名</label>
                <input name="realName" required maxlength="40" value="<%= user.getRealName() == null ? "" : user.getRealName() %>">
            </div>
            <div class="field">
                <label>联系电话</label>
                <input name="phone" maxlength="30" value="<%= user.getPhone() == null ? "" : user.getPhone() %>">
            </div>
            <div class="field">
                <label>用户角色</label>
                <select name="role" required>
                    <option value="ADMIN" <%= "ADMIN".equals(userRole) ? "selected" : "" %>>管理员</option>
                    <option value="TEACHER" <%= "TEACHER".equals(userRole) ? "selected" : "" %>>班主任</option>
                    <option value="STUDENT" <%= "STUDENT".equals(userRole) ? "selected" : "" %>>学生</option>
                </select>
            </div>
            <div class="field">
                <label>所属班级</label>
                <select name="classId">
                    <option value="">无</option>
                    <% if (classes != null) {
                        for (Object obj : classes) {
                            ClassInfo info = (ClassInfo) obj;
                            boolean selected = user.getClassId() != null && user.getClassId() == info.getId();
                    %>
                    <option value="<%= info.getId() %>" <%= selected ? "selected" : "" %>>
                        <%= info.getCollegeName() %> - <%= info.getName() %>
                    </option>
                    <% }} %>
                </select>
                <span class="help">学生账号必须选择班级，班主任和管理员可留空。</span>
            </div>
        </div>
        <br>
        <div class="actions">
            <button class="btn" type="submit">保存</button>
            <a class="btn secondary" href="<%= ctx %>/admin/users">返回</a>
        </div>
    </form>
</div>
<%@ include file="../common/footer.jsp" %>
