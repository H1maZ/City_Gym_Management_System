package com.example.city_gym_management_system;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Variant;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/fingerprint-data")
public class FingerprintDataServlet extends HttpServlet {

    private static Map<String, String> userMap = new HashMap<>();

    static {
        try {
            System.load("C:\\Windows\\System32\\jacob-1.21-x64.dll");
        } catch (UnsatisfiedLinkError e) {
            System.out.println("JACOB already loaded");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<String> logs = new ArrayList<>();
        List<String> users = new ArrayList<>();
        Set<String> savedMembers = new HashSet<>(); // 🔥 NEW

        ActiveXComponent zk = null;
        String page = request.getParameter("page");

        try {

            // =========================
            // 🔥 LOAD SAVED MEMBERS FROM DB
            // =========================
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/gym_system",
                    "root",
                    "1234"
            );

            String sql = "SELECT fingerprint_id FROM member_details";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                savedMembers.add(rs.getString("fingerprint_id"));
            }

            rs.close();
            ps.close();
            con.close();

            // =========================
            // 🔥 CONNECT DEVICE
            // =========================
            zk = new ActiveXComponent("zkemkeeper.ZKEM");

            boolean isConnected = zk.invoke("Connect_Net",
                    new Variant("192.168.8.201"),
                    new Variant(4370)).getBoolean();

            if (!isConnected) {
                logs.add("❌ Device connection failed!");
            } else {

                logs.add("✅ Device Connected Successfully!");
                userMap.clear();

                if (page == null || "users".equals(page)) {
                    readUsers(zk, users, logs);
                }

                if ("logs".equals(page)) {
                    readUsers(zk, users, logs);
                    readLogs(zk, logs);
                }

                zk.invoke("Disconnect");
            }

        } catch (Exception e) {
            logs.add("❌ Error: " + e.getMessage());
        }

        // =========================
        // 🔥 SEND DATA TO JSP
        // =========================
        request.setAttribute("logs", logs);
        request.setAttribute("users", users);
        request.setAttribute("savedMembers", savedMembers); // 🔥 IMPORTANT

        if ("logs".equals(page)) {
            request.getRequestDispatcher("attendance.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("members.jsp").forward(request, response);
        }
    }

    private void readUsers(ActiveXComponent zk, List<String> users, List<String> logs) {
        try {
            zk.invoke("ReadAllUserID", new Variant(1));

            Variant userId = new Variant("", true);
            Variant name = new Variant("", true);
            Variant password = new Variant("", true);
            Variant privilege = new Variant(0, true);
            Variant enabled = new Variant(false, true);

            while (true) {

                Variant result = zk.invoke("SSR_GetAllUserInfo",
                        new Variant(1),
                        userId,
                        name,
                        password,
                        privilege,
                        enabled
                );

                if (!result.getBoolean()) break;

                String id = userId.toString().trim();
                String userName = name.toString().trim();

                userMap.put(id, userName);

                users.add("👤 ID: " + id + " | Name: " + userName);
            }

        } catch (Exception e) {
            logs.add("❌ Error reading users: " + e.getMessage());
        }
    }

    private void readLogs(ActiveXComponent zk, List<String> logs) {
        try {
            zk.invoke("ReadGeneralLogData", new Variant(1));

            while (true) {

                Variant userID = new Variant("", true);
                Variant verifyMode = new Variant(0, true);
                Variant ioMode = new Variant(0, true);
                Variant year = new Variant(0, true);
                Variant month = new Variant(0, true);
                Variant day = new Variant(0, true);
                Variant hour = new Variant(0, true);
                Variant minute = new Variant(0, true);
                Variant second = new Variant(0, true);
                Variant workCode = new Variant(0, true);

                Variant result = zk.invoke("SSR_GetGeneralLogData",
                        new Variant(1),
                        userID,
                        verifyMode,
                        ioMode,
                        year,
                        month,
                        day,
                        hour,
                        minute,
                        second,
                        workCode
                );

                if (!result.getBoolean()) break;

                String id = userID.toString().trim();
                String name = userMap.getOrDefault(id, "Unknown");

                logs.add("📌 User: " + name + " (ID: " + id + ")");
            }

        } catch (Exception e) {
            logs.add("❌ Log error: " + e.getMessage());
        }
    }



    // =========================
    // New Fingerprint Users ID delete
    // =========================

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("deleteDeviceUser".equals(action)) {

            String fid = request.getParameter("fid");

            try {
                ActiveXComponent zk = new ActiveXComponent("zkemkeeper.ZKEM");

                boolean isConnected = zk.invoke("Connect_Net",
                        new Variant("192.168.8.201"),
                        new Variant(4370)).getBoolean();

                if (isConnected) {

                    // 🔥 DELETE FINGERPRINT
                    zk.invoke("SSR_DeleteEnrollData",
                            new Variant(1),
                            new Variant(fid),
                            new Variant(12)
                    );

                    // 🔥 DELETE USER
                    zk.invoke("DeleteUserInfo",
                            new Variant(1),
                            new Variant(fid)
                    );

                    zk.invoke("RefreshData", new Variant(1));
                    zk.invoke("Disconnect");

                    System.out.println("✅ Deleted from device: " + fid);

                } else {
                    System.out.println("❌ Device not connected!");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // 🔥 reload same page
            response.sendRedirect("fingerprint-data?page=users");
            return;
        }
    }


}