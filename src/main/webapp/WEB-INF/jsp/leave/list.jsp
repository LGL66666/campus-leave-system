<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.campus.leave.model.ClassInfo" %>
<%@ page import="com.campus.leave.model.LeaveRequest" %>
<%@ page import="com.campus.leave.util.ParamUtil" %>
<%
    request.setAttribute("pageTitle", "请假记录查询");
%>
<%@ include file="../common/header.jsp" %>
<%
    List leaves = (List) request.getAttribute("leaves");
    List classes = (List) request.getAttribute("classes");
    String statusParam = request.getParameter("status") == null ? "" : request.getParameter("status");
    String classParam = request.getParameter("classId") == null ? "" : request.getParameter("classId");
%>
<div class="panel">
    <form class="toolbar" method="get" action="<%= ctx %>/leaves">
        <% if (!"STUDENT".equals(role)) { %>
        <input style="max-width:200px;" name="studentKeyword" placeholder="学生姓名或学号"
               value="<%= request.getParameter("studentKeyword") == null ? "" : request.getParameter("studentKeyword") %>">
        <select style="max-width:220px;" name="classId">
            <option value="">全部班级</option>
            <% if (classes != null) {
                for (Object obj : classes) {
                    ClassInfo info = (ClassInfo) obj;
                    String id = String.valueOf(info.getId());
            %>
            <option value="<%= id %>" <%= id.equals(classParam) ? "selected" : "" %>>
                <%= info.getName() %>
            </option>
            <% }} %>
        </select>
        <% } %>
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
        <a class="btn secondary" href="<%= ctx %>/leaves">重置</a>
        <% if ("STUDENT".equals(role)) { %>
        <a class="btn" href="<%= ctx %>/leaves?action=new">提交请假</a>
        <% } %>
    </form>
    <table>
        <thead>
        <tr>
            <th>学生</th>
            <th>学院/班级</th>
            <th>请假时间</th>
            <th>天数</th>
            <th>事由</th>
            <th>审批人</th>
            <th>状态</th>
            <th>审批意见</th>
            <% if ("ADMIN".equals(role)) { %><th style="width:340px;">状态维护</th><% } %>
        </tr>
        </thead>
        <tbody>
        <% if (leaves == null || leaves.isEmpty()) { %>
        <tr><td colspan="<%= "ADMIN".equals(role) ? 9 : 8 %>">暂无请假记录</td></tr>
        <% } else {
            for (Object obj : leaves) {
                LeaveRequest item = (LeaveRequest) obj;
                String badge = "PENDING".equals(item.getStatus()) ? "pending" : ("APPROVED".equals(item.getStatus()) ? "approved" : "rejected");
        %>
        <tr>
            <td><%= item.getStudentName() %></td>
            <td><%= item.getCollegeName() %><br><span class="help"><%= item.getClassName() %></span></td>
            <td><%= item.getStartTime() %><br>至 <%= item.getEndTime() %></td>
            <td><%= item.getDays() %></td>
            <td><%= item.getReason() %></td>
            <td><%= item.getReviewerName() %></td>
            <td><span class="badge <%= badge %>"><%= ParamUtil.statusText(item.getStatus()) %></span></td>
            <td><%= item.getReviewOpinion() == null ? "" : item.getReviewOpinion() %></td>
            <% if ("ADMIN".equals(role)) { %>
            <td>
                <div class="actions">
                    <form method="post" action="<%= ctx %>/leaves">
                        <input type="hidden" name="action" value="status">
                        <input type="hidden" name="id" value="<%= item.getId() %>">
                        <select name="status">
                            <option value="PENDING" <%= "PENDING".equals(item.getStatus()) ? "selected" : "" %>>待审批</option>
                            <option value="APPROVED" <%= "APPROVED".equals(item.getStatus()) ? "selected" : "" %>>已通过</option>
                            <option value="REJECTED" <%= "REJECTED".equals(item.getStatus()) ? "selected" : "" %>>已拒绝</option>
                        </select>
                        <input name="opinion" placeholder="处理意见" value="<%= item.getReviewOpinion() == null ? "" : item.getReviewOpinion() %>">
                        <button class="btn secondary" type="submit">更新</button>
                    </form>
                    <form method="post" action="<%= ctx %>/leaves" onsubmit="return confirm('确定删除该请假记录吗？');">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="<%= item.getId() %>">
                        <button class="btn danger" type="submit">删除</button>
                    </form>
                </div>
            </td>
            <% } %>
        </tr>
        <% }} %>
        </tbody>
    </table>
</div>
<%@ include file="../common/footer.jsp" %>
