<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.campus.leave.model.College" %>
<%
    request.setAttribute("pageTitle", "学院信息管理");
%>
<%@ include file="../common/header.jsp" %>
<%
    List colleges = (List) request.getAttribute("colleges");
%>
<div class="panel">
    <form class="toolbar" method="get" action="<%= ctx %>/admin/colleges">
        <input style="max-width:260px;" name="keyword" placeholder="学院编号或名称"
               value="<%= request.getParameter("keyword") == null ? "" : request.getParameter("keyword") %>">
        <button class="btn" type="submit">查询</button>
        <a class="btn secondary" href="<%= ctx %>/admin/colleges">重置</a>
        <a class="btn" href="<%= ctx %>/admin/colleges?action=new">新增学院</a>
    </form>
    <table>
        <thead>
        <tr>
            <th>编号</th>
            <th>学院名称</th>
            <th style="width:180px;">操作</th>
        </tr>
        </thead>
        <tbody>
        <% if (colleges == null || colleges.isEmpty()) { %>
        <tr><td colspan="3">暂无学院信息</td></tr>
        <% } else {
            for (Object obj : colleges) {
                College item = (College) obj;
        %>
        <tr>
            <td><%= item.getCode() %></td>
            <td><%= item.getName() %></td>
            <td>
                <div class="actions">
                    <a class="btn secondary" href="<%= ctx %>/admin/colleges?action=edit&id=<%= item.getId() %>">编辑</a>
                    <form method="post" action="<%= ctx %>/admin/colleges" onsubmit="return confirm('确定删除该学院吗？');">
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
