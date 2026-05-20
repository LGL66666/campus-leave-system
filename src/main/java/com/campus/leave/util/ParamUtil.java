package com.campus.leave.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public final class ParamUtil {
    private static final String[] DATE_TIME_PATTERNS = {
            "yyyy-MM-dd'T'HH:mm",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm"
    };

    private ParamUtil() {
    }

    public static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    public static Integer getInteger(String value) {
        String trimmed = trim(value);
        if (trimmed.length() == 0) {
            return null;
        }
        return Integer.valueOf(trimmed);
    }

    public static int getInt(String value, int defaultValue) {
        Integer parsed = getInteger(value);
        return parsed == null ? defaultValue : parsed;
    }

    public static Timestamp parseDateTime(String value) throws ParseException {
        String trimmed = trim(value);
        ParseException last = null;
        for (String pattern : DATE_TIME_PATTERNS) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(pattern);
                format.setLenient(false);
                return new Timestamp(format.parse(trimmed).getTime());
            } catch (ParseException e) {
                last = e;
            }
        }
        throw last == null ? new ParseException(trimmed, 0) : last;
    }

    public static String statusText(String status) {
        if ("APPROVED".equals(status)) {
            return "已通过";
        }
        if ("REJECTED".equals(status)) {
            return "已拒绝";
        }
        return "待审批";
    }

    public static String roleText(String role) {
        if ("ADMIN".equals(role)) {
            return "管理员";
        }
        if ("TEACHER".equals(role)) {
            return "班主任";
        }
        return "学生";
    }
}
