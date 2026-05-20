<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>登录 - 高校在线请假管理系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/style.css">
</head>
<body class="login-page">
<div class="login-box">
    <h1>高校在线请假管理系统</h1>
    <p>学生提交请假申请，班主任在线审批，管理员维护基础数据。</p>
    <% if (request.getAttribute("error") != null) { %>
    <div class="message error"><%= request.getAttribute("error") %></div>
    <% } %>
    <form method="post" action="<%= request.getContextPath() %>/login">
        <div class="field">
            <label>用户名</label>
            <input name="username" required autofocus>
        </div>
        <div class="field">
            <label>密码</label>
            <input name="password" type="password" required>
        </div>
        <button class="btn" type="submit">登录系统</button>
    </form>
    <p class="help">
        演示账号：admin / admin123，t_zhang / 123456，s_wang / 123456
    </p>
</div>
</body>
</html>
