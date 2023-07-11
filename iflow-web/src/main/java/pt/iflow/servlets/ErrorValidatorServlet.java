package pt.iflow.servlets;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ErrorValidatorServlet extends HttpServlet {

    private static final long serialVersionUID = -3535202556875137862L;

    public ErrorValidatorServlet() {
    }

    public void init() throws ServletException {
    }

    public static String getHttpStatusDescription(String status) {
        Map<Integer, String> httpStatuses = new HashMap<>();
        httpStatuses.put(100, "Continue");
        httpStatuses.put(101, "Switching Protocols");
        httpStatuses.put(200, "OK");
        httpStatuses.put(201, "Created");
        httpStatuses.put(202, "Accepted");
        httpStatuses.put(203, "Non-Authoritative Information");
        httpStatuses.put(204, "No Content");
        httpStatuses.put(205, "Reset Content");
        httpStatuses.put(206, "Partial Content");
        httpStatuses.put(300, "Multiple Choices");
        httpStatuses.put(301, "Moved Permanently");
        httpStatuses.put(302, "Found");
        httpStatuses.put(303, "See Other");
        httpStatuses.put(304, "Not Modified");
        httpStatuses.put(305, "Use Proxy");
        httpStatuses.put(307, "Temporary Redirect");
        httpStatuses.put(400, "Bad Request");
        httpStatuses.put(401, "Unauthorized");
        httpStatuses.put(402, "Payment Required");
        httpStatuses.put(403, "Forbidden");
        httpStatuses.put(404, "Not Found");
        httpStatuses.put(405, "Method Not Allowed");
        httpStatuses.put(406, "Not Acceptable");
        httpStatuses.put(407, "Proxy Authentication Required");
        httpStatuses.put(408, "Request Timeout");
        httpStatuses.put(409, "Conflict");
        httpStatuses.put(410, "Gone");
        httpStatuses.put(411, "Length Required");
        httpStatuses.put(412, "Precondition Failed");
        httpStatuses.put(413, "Request Entity Too Large");
        httpStatuses.put(414, "Request-URI Too Long");
        httpStatuses.put(415, "Unsupported Media Type");
        httpStatuses.put(416, "Requested Range Not Satisfiable");
        httpStatuses.put(417, "Expectation Failed");
        httpStatuses.put(500, "Internal Server Error");
        httpStatuses.put(501, "Not Implemented");
        httpStatuses.put(502, "Bad Gateway");
        httpStatuses.put(503, "Service Unavailable");
        httpStatuses.put(504, "Gateway Timeout");
        httpStatuses.put(505, "HTTP Version Not Supported");

        return httpStatuses.get(Integer.parseInt(status));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String status = req.getParameter("status");

        if (status == null || status.isEmpty()) {
            status = "404";
        }

        String description = getHttpStatusDescription(status);

        req.setAttribute("status", status);
        req.setAttribute("description", description);

        req.getRequestDispatcher("/error.jsp").forward(req, resp);
    }

}
