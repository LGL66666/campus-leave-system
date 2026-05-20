<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.campus.leave.model.User" %>
<%@ page import="com.campus.leave.util.ParamUtil" %>
<%
    request.setAttribute("pageTitle", "用户信息管理");
%>
<%@ include file="../common/header.jsp" %>
<%
    List users = (List) request.getAttribute("users");
    String roleParam = request.getParameter("role") == null ? "" : request.getParameter("role");
%>
<div class="panel">
    <form class="toolbar" method="get" action="<%= ctx %>/admin/users">
        <input style="max-width:240px;" name="keyword" placeholder="用户名、姓名或电话"
               value="<%= request.getParameter("keyword") == null ? "" : request.getParameter("keyword") %>">
        <select style="max-width:160px;" name="role">
            <option value="">全部角色</option>
            <option value="ADMIN" <%= "ADMIN".equals(roleParam) ? "selected" : "" %>>管理员</option>
            <option value="TEACHER" <%= "TEACHER".equals(roleParam) ? "selected" : "" %>>班主任</option>
            <option value="STUDENT" <%= "STUDENT".equals(roleParam) ? "selected" : "" %>>学生</option>
        </select>
        <button class="btn" type="submit">查询</button>
        <a class="btn secondary" href="<%= ctx %>/admin/users">重置</a>
        <a class="btn" href="<%= ctx %>/admin/users?action=new">新增用户</a>
    </form>
    <table>
        <thead>
        <tr>
            <th>用户名</th>
            <th>姓名</th>
            <th>角色</th>
            <th>电话</th>
            <th>班级</th>
            <th style="width:180px;">操作</th>
        </tr>
        </thead>
        <tbody>
        <% if (users == null || users.isEmpty()) { %>
        <tr><td colspan="6">暂无用户信息</td></tr>
        <% } else {
            for (Object obj : users) {
                User item = (User) obj;
        %>
        <tr>
            <td><%= item.getUsername() %></td>
            <td><%= item.getRealName() %></td>
            <td><%= ParamUtil.roleText(item.getRole()) %></td>
            <td><%= item.getPhone() == null ? "" : item.getPhone() %></td>
            <td><%= item.getClassName() == null ? "" : item.getClassName() %></td>
            <td>
                <div class="actions">
                    <a class="btn secondary" href="<%= ctx %>/admin/users?action=edit&id=<%= item.getId() %>">编辑</a>
                    <form method="post" action="<%= ctx %>/admin/users" onsubmit="return confirm('确定删除该用户吗？');">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="<%= item.getId() %>">
                        <button class="btn danger" type="submit">删除</button>
                    </form>
                </div>
            </td>
        </tr>
        <% }} %>
        </tbody>
    </table>
</div>
<%@ include file="../common/footer.jsp" %>
