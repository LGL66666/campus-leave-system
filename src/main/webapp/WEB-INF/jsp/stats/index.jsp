<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.campus.leave.model.ClassInfo" %>
<%@ page import="com.campus.leave.model.StatItem" %>
<%@ page import="com.campus.leave.util.ParamUtil" %>
<%
    request.setAttribute("pageTitle", "请假统计");
%>
<%@ include file="../common/header.jsp" %>
<%
    List classes = (List) request.getAttribute("classes");
    List classStats = (List) request.getAttribute("classStats");
    List statusStats = (List) request.getAttribute("statusStats");
    String classParam = request.getParameter("classId") == null ? "" : request.getParameter("classId");
%>
<div class="panel">
    <form class="toolbar" method="get" action="<%= ctx %>/stats">
        <select style="max-width:220px;" name="classId">
            <option value="">全部班级</option>
            <% if (classes != null) {
                for (Object obj : classes) {
                    ClassInfo info = (ClassInfo) obj;
                    String id = String.valueOf(info.getId());
            %>
            <option value="<%= id %>" <%= id.equals(classParam) ? "selected" : "" %>><%= info.getName() %></option>
            <% }} %>
        </select>
        <input style="max-width:160px;" type="date" name="beginDate"
               value="<%= request.getParameter("beginDate") == null ? "" : request.getParameter("beginDate") %>">
        <input style="max-width:160px;" type="date" name="endDate"
               value="<%= request.getParameter("endDate") == null ? "" : request.getParameter("endDate") %>">
        <button class="btn" type="submit">统计</button>
        <a class="btn secondary" href="<%= ctx %>/stats">重置</a>
    </form>
</div>

<div class="panel">
    <h2>按班级统计</h2>
    <table>
        <thead>
        <tr>
            <th>班级</th>
            <th>请假次数</th>
            <th>请假天数合计</th>
        </tr>
        </thead>
        <tbody>
        <% if (classStats == null || classStats.isEmpty()) { %>
        <tr><td colspan="3">暂无统计数据</td></tr>
        <% } else {
            for (Object obj : classStats) {
                StatItem item = (StatItem) obj;
        %>
        <tr>
            <td><%= item.getName() %></td>
            <td><%= item.getCount() %></td>
            <td><%= item.getDays() %></td>
        </tr>
        <% }} %>
        </tbody>
    </table>
</div>

<div class="panel">
    <h2>按状态统计</h2>
    <table>
        <thead>
        <tr>
            <th>状态</th>
            <th>记录数</th>
            <th>天数合计</th>
        </tr>
        </thead>
        <tbody>
        <% if (statusStats == null || statusStats.isEmpty()) { %>
        <tr><td colspan="3">暂无统计数据</td></tr>
        <% } else {
            for (Object obj : statusStats) {
                StatItem item = (StatItem) obj;
        %>
        <tr>
            <td><%= ParamUtil.statusText(item.getName()) %></td>
            <td><%= item.getCount() %></td>
            <td><%= item.getDays() %></td>
        </tr>
        <% }} %>
        </tbody>
    </table>
</div>
<%@ include file="../common/footer.jsp" %>
