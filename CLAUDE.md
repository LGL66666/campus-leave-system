# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

高校在线请假管理系统 — 传统 Java EE Web 应用（Servlet 2.5 + JSP + JDBC），Maven WAR 工程，部署到 Tomcat 8/9。

## 构建与运行

```bash
# 编译打包
mvn clean package
# 输出: target/campus-leave-system.war

# 数据库初始化（在 MySQL 客户端执行）
source database/schema.sql;
```

部署 WAR 到 Tomcat `webapps/` 目录后访问 `http://localhost:8080/campus-leave-system`。

**数据库连接**硬编码在 `src/main/java/com/campus/leave/dao/DBUtil.java`，默认 `root/123456`。可通过 JVM 参数覆盖：
```
-Dleave.db.url=jdbc:mysql://localhost:3306/campus_leave
-Dleave.db.user=root
-Dleave.db.password=你的密码
```

本项目无自动化测试框架、无 lint 工具。

## 架构

**MVC 模式，无 Service 层** — Servlet 直接调用 DAO，业务逻辑分布在 Servlet 和 DAO 中。

```
src/main/java/com/campus/leave/
├── servlet/       # 控制器 — 所有 Servlet 继承 BaseServlet
│   └── BaseServlet.java  # 基类：提供 requireRole()、forward()、redirect()
├── dao/           # 数据访问 — 纯 JDBC（PreparedStatement + try-with-resources）
│   └── DBUtil.java       # 连接工具，无连接池
├── model/         # POJO 实体（User, College, ClassInfo, LeaveRequest, StatItem）
├── filter/        # EncodingFilter（UTF-8）+ AuthFilter（登录检查）
└── util/          # ParamUtil 参数解析
```

**视图层**：`src/main/webapp/WEB-INF/jsp/` 下的 JSP 页面，使用 scriptlet 和表达式渲染，无前端框架。全部页面通过 `common/header.jsp` 和 `common/footer.jsp` 组合。

**路由（web.xml）**：

| URL | Servlet | 权限 |
|-----|---------|------|
| `/login` | AuthServlet | 公开 |
| `/dashboard` | DashboardServlet | 需登录 |
| `/admin/colleges` | CollegeServlet | ADMIN |
| `/admin/classes` | ClassServlet | ADMIN |
| `/admin/users` | UserServlet | ADMIN |
| `/leaves` | LeaveRequestServlet | 需登录 |
| `/teacher/approvals` | ApprovalServlet | TEACHER |
| `/stats` | StatsServlet | 需登录 |

## 认证与授权

- **基于 HttpSession**：登录后在 session 中存储 `currentUser`（User 对象）
- **AuthFilter** 拦截所有请求，公开路径（`/login`、`/`、`/assets/*`）除外，未登录重定向到 `/login`
- **角色检查**：`BaseServlet.requireRole()` 校验 `user.role` 是否为允许的角色，不匹配返回 403
- **密码明文存储**（培训项目，非生产用途）

三种角色：`ADMIN`、`TEACHER`（班主任）、`STUDENT`

## 数据库关系

`colleges` 1→N `classes` → 每个班级有 `head_teacher_id`（指向 users）和 `college_id`
`users` 中 STUDENT 属于 `class_id`
`leave_requests` 关联 student → class → reviewer（班主任），审批流为：学生提交 → 本班班主任审批

## UI 样式

全局样式在 `src/main/webapp/assets/style.css`，Warm Scholar（暖学者）风格：暖灰褐主色 + 深暖炭顶栏 + 象牙白背景。CSS 变量驱动，零渐变/零玻璃态，适配现代浏览器。
