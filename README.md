# 高校在线请假管理系统

这是根据《Java EE 编程基础实训》指导书第 27 题完成的 Servlet + JSP + JDBC Web 项目。系统包含学院管理、班级管理、用户管理、请假申请、班主任审批、请假记录查询、状态维护和统计功能。

## 技术栈

- JDK 8+，当前项目可用 JDK 17 编译
- Servlet 2.5，JSP
- JDBC + MySQL 8
- Maven WAR 工程，适合部署到 Tomcat 8/9（本项目使用 `javax.servlet`，不建议直接部署到 Tomcat 10/11）

## 目录说明

- `src/main/java`：Servlet、DAO、JavaBean、过滤器和工具类
- `src/main/webapp`：JSP 页面、`web.xml`、CSS 样式
- `database/schema.sql`：建库建表与演示数据
- `docs/training-report.md`：实训报告初稿

## 数据库初始化

1. 启动 MySQL。
2. 用 MySQL 客户端执行：

```sql
source database/schema.sql;
```

默认数据库名为 `campus_leave`。

系统默认连接配置在 `com.campus.leave.dao.DBUtil` 中：

- URL：`jdbc:mysql://localhost:3306/campus_leave`
- 用户名：`root`
- 密码：`123456`

如果本机密码不同，可以修改 `DBUtil`，也可以用 JVM 参数覆盖：

```bash
-Dleave.db.user=root -Dleave.db.password=你的密码
```

## 编译打包

```bash
mvn clean package
```

生成的 WAR 文件位于：

```text
target/campus-leave-system.war
```

把该 WAR 部署到 Tomcat 8/9 的 `webapps` 目录，访问：

```text
http://localhost:8080/campus-leave-system
```

## 演示账号

| 角色 | 用户名 | 密码 |
| --- | --- | --- |
| 管理员 | `admin` | `admin123` |
| 班主任 | `t_zhang` | `123456` |
| 学生 | `s_wang` | `123456` |

## 主要功能

- 管理员：维护学院、班级、学生/教师用户，查询请假记录，维护异常状态，查看统计。
- 学生：提交请假申请，查看本人请假申请和审批结果。
- 班主任：查看本班学生请假申请，填写审批意见并同意或拒绝，查询班级记录和统计。
