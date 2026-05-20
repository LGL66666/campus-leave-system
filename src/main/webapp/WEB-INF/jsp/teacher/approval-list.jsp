<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.campus.leave.model.ClassInfo" %>
<%@ page import="com.campus.leave.model.LeaveRequest" %>
<%@ page import="com.campus.leave.util.ParamUtil" %>
<%
    request.setAttribute("pageTitle", "请假审批");
%>
<%@ include file="../common/header.jsp" %>
<%
    List leaves = (List) request.getAttribute("leaves");
    List classes = (List) request.getAttribute("classes");
    String statusParam = (String) request.getAttribute("status");
    String classParam = request.getParameter("classId") == null ? "" : request.getParameter("classId");
%>
<div class="panel">
    <form class="toolbar" method="get" action="<%= ctx %>/teacher/approvals">
        <input style="max-width:200px;" name="studentKeyword" placeholder="学生姓名或学号"
               value="<%= request.getParameter("studentKeyword") == null ? "" : request.getParameter("studentKeyword") %>">
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
        <select style="max-width:150px;" name="status">
            <option value="">全部状态</option>
            <option value="PENDING" <%= "PENDING".equals(statusParam) ? "selected" : "" %>>待审批</option>
            <option value="APPROVED" <%= "APPROVED".equals(statusParam) ? "selected" : "" %>>已通过</option>
            <option value="REJECTED" <%= "REJECTED".equals(statusParam) ? "selected" : "" %>>已拒绝</option>
        </select>
        <input style="max-width:160px;" type="date" name="beginDate"
               value="<%= request.getParameter("beginDate") == null ? "" : request.getParameter("beginDate") %>">
        <input style="max-width:160px;" type="date" name="endDate"
               value="<%= request.getParameter("endDate") == null ? "" : request.getParameter("endDate") %>">
        <button class="btn" type="submit">查询</button>
        <a class="btn secondary" href="<%= ctx %>/teacher/approvals">待审批</a>
    </form>
    <table>
        <thead>
        <tr>
            <th>学生</th>
            <th>班级</th>
            <th>请假时间</th>
            <th>天数</th>
            <th>事由</th>
            <th>状态</th>
            <th style="width:260px;">审批操作</th>
        </tr>
        </thead>
        <tbody>
        <% if (leaves == null || leaves.isEmpty()) { %>
        <tr><td colspan="7">暂无需要处理的请假申请</td></tr>
        <% } else {
            for (Object obj : leaves) {
                LeaveRequest item = (LeaveRequest) obj;
                String badge = "PENDING".equals(item.getStatus()) ? "pending" : ("APPROVED".equals(item.getStatus()) ? "approved" : "rejected");
        %>
        <tr>
            <td><%= item.getStudentName() %></td>
            <td><%= item.getClassName() %></td>
            <td><%= item.getStartTime() %><br>至 <%= item.getEndTime() %></td>
            <td><%= item.getDays() %></td>
            <td><%= item.getReason() %></td>
            <td><span class="badge <%= badge %>"><%= ParamUtil.statusText(item.getStatus()) %></span></td>
            <td>
                <% if ("PENDING".equals(item.getStatus())) { %>
                <form method="post" action="<%= ctx %>/teacher/approvals">
                    <input type="hidden" name="id" value="<%= item.getId() %>">
                    <textarea name="opinion" placeholder="审批意见" required></textarea>
                    <div class="actions">
                        <button class="btn success" name="decision" value="approve" type="submit">同意</button>
                        <button class="btn danger" name="decision" value="reject" type="submit">拒绝</button>
                    </div>
                </form>
                <% } else { %>
                <%= item.getReviewOpinion() == null ? "" : item.getReviewOpinion() %>
                <% } %>
            </td>
        </tr>
        <% }} %>
        </tbody>
    </table>
</div>
<%@ include file="../common/footer.jsp" %>
