<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="com.campus.leave.model.ClassInfo" %>
<%
    request.setAttribute("pageTitle", "提交请假申请");
%>
<%@ include file="../common/header.jsp" %>
<%
    ClassInfo classInfo = (ClassInfo) request.getAttribute("classInfo");
%>
<div class="panel">
    <form method="post" action="<%= ctx %>/leaves">
        <div class="form-grid">
            <div class="field">
                <label>申请学生</label>
                <input readonly value="<%= currentUser.getRealName() %>">
            </div>
            <div class="field">
                <label>所属班级</label>
                <input readonly value="<%= classInfo == null ? "未关联班级" : classInfo.getCollegeName() + " - " + classInfo.getName() %>">
            </div>
            <div class="field">
                <label>审批班主任</label>
                <input readonly value="<%= classInfo == null ? "未设置" : classInfo.getHeadTeacherName() %>">
            </div>
            <div class="field">
                <label>开始时间</label>
                <input name="startTime" type="datetime-local" required>
            </div>
            <div class="field">
                <label>结束时间</label>
                <input name="endTime" type="datetime-local" required>
            </div>
            <div class="field" style="grid-column:1 / -1;">
                <label>请假事由</label>
                <textarea name="reason" required maxlength="500" placeholder="请填写请假原因"></textarea>
            </div>
        </div>
        <br>
        <div class="actions">
            <button class="btn" type="submit">提交申请</button>
            <a class="btn secondary" href="<%= ctx %>/leaves">返回记录</a>
        </div>
    </form>
</div>
<%@ include file="../common/footer.jsp" %>
