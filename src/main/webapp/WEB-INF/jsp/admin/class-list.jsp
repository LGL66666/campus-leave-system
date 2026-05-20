<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.campus.leave.model.ClassInfo" %>
<%
    request.setAttribute("pageTitle", "班级信息管理");
%>
<%@ include file="../common/header.jsp" %>
<%
    List classes = (List) request.getAttribute("classes");
%>
<div class="panel">
    <form class="toolbar" method="get" action="<%= ctx %>/admin/classes">
        <input style="max-width:260px;" name="keyword" placeholder="班级、学院或班主任"
               value="<%= request.getParameter("keyword") == null ? "" : request.getParameter("keyword") %>">
        <button class="btn" type="submit">查询</button>
        <a class="btn secondary" href="<%= ctx %>/admin/classes">重置</a>
        <a class="btn" href="<%= ctx %>/admin/classes?action=new">新增班级</a>
    </form>
    <table>
        <thead>
        <tr>
            <th>所属学院</th>
            <th>班级名称</th>
            <th>班主任</th>
            <th style="width:180px;">操作</th>
        </tr>
        </thead>
        <tbody>
        <% if (classes == null || classes.isEmpty()) { %>
        <tr><td colspan="4">暂无班级信息</td></tr>
        <% } else {
            for (Object obj : classes) {
                ClassInfo item = (ClassInfo) obj;
        %>
        <tr>
            <td><%= item.getCollegeName() %></td>
            <td><%= item.getName() %></td>
            <td><%= item.getHeadTeacherName() %></td>
            <td>
                <div class="actions">
                    <a class="btn secondary" href="<%= ctx %>/admin/classes?action=edit&id=<%= item.getId() %>">编辑</a>
                    <form method="post" action="<%= ctx %>/admin/classes" onsubmit="return confirm('确定删除该班级吗？');">
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
