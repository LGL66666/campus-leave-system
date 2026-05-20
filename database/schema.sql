DROP DATABASE IF EXISTS campus_leave;
CREATE DATABASE campus_leave DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_leave;

CREATE TABLE colleges (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(80) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(40) NOT NULL UNIQUE,
    password VARCHAR(40) NOT NULL,
    real_name VARCHAR(40) NOT NULL,
    role ENUM('ADMIN', 'TEACHER', 'STUDENT') NOT NULL,
    phone VARCHAR(30),
    class_id INT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE classes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    college_id INT NOT NULL,
    name VARCHAR(80) NOT NULL,
    head_teacher_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_class_college FOREIGN KEY (college_id) REFERENCES colleges(id),
    CONSTRAINT fk_class_teacher FOREIGN KEY (head_teacher_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE users
    ADD CONSTRAINT fk_user_class FOREIGN KEY (class_id) REFERENCES classes(id);

CREATE TABLE leave_requests (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    class_id INT NOT NULL,
    reviewer_id INT NOT NULL,
    reason VARCHAR(500) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    days DECIMAL(5,1) NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    review_opinion VARCHAR(500),
    apply_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    review_time DATETIME NULL,
    CONSTRAINT fk_leave_student FOREIGN KEY (student_id) REFERENCES users(id),
    CONSTRAINT fk_leave_class FOREIGN KEY (class_id) REFERENCES classes(id),
    CONSTRAINT fk_leave_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(id),
    INDEX idx_leave_status(status),
    INDEX idx_leave_class_time(class_id, start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO colleges(code, name) VALUES
('AI', '人工智能学院'),
('CS', '计算机学院'),
('SE', '软件工程学院');

INSERT INTO users(username, password, real_name, role, phone) VALUES
('admin', 'admin123', '系统管理员', 'ADMIN', '13800000000'),
('t_zhang', '123456', '张老师', 'TEACHER', '13800000001'),
('t_li', '123456', '李老师', 'TEACHER', '13800000002');

INSERT INTO classes(college_id, name, head_teacher_id) VALUES
(1, '人工智能2301班', 2),
(2, '计算机科学2302班', 3);

INSERT INTO users(username, password, real_name, role, phone, class_id) VALUES
('s_wang', '123456', '王同学', 'STUDENT', '13900000001', 1),
('s_chen', '123456', '陈同学', 'STUDENT', '13900000002', 1),
('s_zhao', '123456', '赵同学', 'STUDENT', '13900000003', 2);

INSERT INTO leave_requests(student_id, class_id, reviewer_id, reason, start_time, end_time, days, status, review_opinion, review_time) VALUES
(4, 1, 2, '因感冒发热需要到医院就诊。', '2026-05-06 08:00:00', '2026-05-06 18:00:00', 0.5, 'APPROVED', '同意，请按时返校并补交病假证明。', '2026-05-06 09:20:00'),
(5, 1, 2, '家中有事需请假回家处理。', '2026-05-07 08:00:00', '2026-05-08 18:00:00', 1.4, 'PENDING', NULL, NULL),
(6, 2, 3, '参加学校安排的竞赛培训。', '2026-05-09 08:00:00', '2026-05-09 20:00:00', 0.5, 'APPROVED', '同意，注意安全。', '2026-05-06 10:00:00');
