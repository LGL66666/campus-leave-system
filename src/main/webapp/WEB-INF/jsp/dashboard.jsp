<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.campus.leave.model.LeaveRequest" %>
<%@ page import="com.campus.leave.util.ParamUtil" %>
<%
    request.setAttribute("pageTitle", "系统首页");
%>
<%@ include file="common/header.jsp" %>
<%
    Map statusCounts = (Map) request.getAttribute("statusCounts");
    Map roleCounts = (Map) request.getAttribute("roleCounts");
    List recentLeaves = (List) request.getAttribute("recentLeaves");
%>
<div class="grid">
    <div class="metric">
        <span>待审批</span>
        <strong><%= statusCounts == null ? 0 : statusCounts.get("PENDING") %></strong>
    </div>
    <div class="metric">
        <span>已通过</span>
        <strong><%= statusCounts == null ? 0 : statusCounts.get("APPROVED") %></strong>
    </div>
    <div class="metric">
        <span>已拒绝</span>
        <strong><%= statusCounts == null ? 0 : statusCounts.get("REJECTED") %></strong>
    </div>
    <div class="metric">
        <span>当前角色</span>
        <strong style="font-size:22px;"><%= ParamUtil.roleText(currentUser.getRole()) %></strong>
    </div>
</div>

<% if (roleCounts != null) { %>
<div class="panel">
    <h2>用户概况</h2>
    <div class="grid">
        <div class="metric"><span>管理员</span><strong><%= roleCounts.get("ADMIN") %></strong></div>
        <div class="metric"><span>班主任</span><strong><%= roleCounts.get("TEACHER") %></strong></div>
        <div class="metric"><span>学生</span><strong><%= roleCounts.get("STUDENT") %></strong></div>
        <div class="metric"><span>系统状态</span><strong style="font-size:22px;">正常</strong></div>
    </div>
</div>
<% } %>

<div class="panel">
    <div class="toolbar">
        <h2 style="margin:0;">最近请假记录</h2>
        <% if ("STUDENT".equals(role)) { %>
        <a class="btn" href="<%= ctx %>/leaves?action=new">提交请假</a>
        <% } else if ("TEACHER".equals(role)) { %>
        <a class="btn" href="<%= ctx %>/teacher/approvals">处理审批</a>
        <% } %>
    </div>
    <table>
        <thead>
        <tr>
            <th>学生</th>
            <th>班级</th>
            <th>请假时间</th>
            <th>天数</th>
            <th>状态</th>
        </tr>
        </thead>
        <tbody>
        <% if (recentLeaves == null || recentLeaves.isEmpty()) { %>
        <tr><td colspan="5">暂无记录</td></tr>
        <% } else {
            for (Object obj : recentLeaves) {
                LeaveRequest item = (LeaveRequest) obj;
                String badge = "PENDING".equals(item.getStatus()) ? "pending" : ("APPROVED".equals(item.getStatus()) ? "approved" : "rejected");
        %>
        <tr>
            <td><%= item.getStudentName() %></td>
            <td><%= item.getClassName() %></td>
            <td><%= item.getStartTime() %> 至 <%= item.getEndTime() %></td>
            <td><%= item.getDays() %></td>
            <td><span class="badge <%= badge %>"><%= ParamUtil.statusText(item.getStatus()) %></span></td>
        </tr>
        <% }} %>
        </tbody>
    </table>
</div>
<%@ include file="common/footer.jsp" %>
