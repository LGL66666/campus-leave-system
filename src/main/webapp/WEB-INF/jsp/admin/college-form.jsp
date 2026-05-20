<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="com.campus.leave.model.College" %>
<%
    request.setAttribute("pageTitle", "学院信息编辑");
%>
<%@ include file="../common/header.jsp" %>
<%
    College college = (College) request.getAttribute("college");
    if (college == null) {
        college = new College();
    }
%>
<div class="panel">
    <form method="post" action="<%= ctx %>/admin/colleges">
        <input type="hidden" name="id" value="<%= college.getId() %>">
        <div class="form-grid">
            <div class="field">
                <label>学院编号</label>
                <input name="code" required maxlength="30" value="<%= college.getCode() == null ? "" : college.getCode() %>">
            </div>
            <div class="field">
                <label>学院名称</label>
                <input name="name" required maxlength="80" value="<%= college.getName() == null ? "" : college.getName() %>">
            </div>
        </div>
        <br>
        <div class="actions">
            <button class="btn" type="submit">保存</button>
            <a class="btn secondary" href="<%= ctx %>/admin/colleges">返回</a>
        </div>
    </form>
</div>
<%@ include file="../common/footer.jsp" %>
