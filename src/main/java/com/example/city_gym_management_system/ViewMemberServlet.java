package com.example.city_gym_management_system;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Variant;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;

@WebServlet("/view-member")
public class ViewMemberServlet extends HttpServlet {

    // ======================
    // 🔥 VIEW (GET)
    // ======================
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fid = request.getParameter("fid");

        String name = "";
        String phone = "";
        String gender = "";
        int age = 0;
        String whatsapp = "";
        String address = "";
        String height = "";
        String weight = "";
        int months = 0;
        String startDate = "";
        String endDate = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/gym_system",
                    "root",
                    "1234"
            );

            String sql = "SELECT * FROM member_details md " +
                    "LEFT JOIN membership_details ms ON md.id = ms.member_id " +
                    "WHERE md.fingerprint_id=?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, fid);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                name = rs.getString("full_name");
                phone = rs.getString("phone");
                gender = rs.getString("gender");
                age = rs.getInt("age");
                whatsapp = rs.getString("whatsapp");
                address = rs.getString("address");
                height = rs.getString("height");
                weight = rs.getString("weight");

                months = rs.getInt("months");
                startDate = rs.getString("start_date");
                endDate = rs.getString("end_date");
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("fid", fid);
        request.setAttribute("name", name);
        request.setAttribute("phone", phone);
        request.setAttribute("gender", gender);
        request.setAttribute("age", age);
        request.setAttribute("whatsapp", whatsapp);
        request.setAttribute("address", address);
        request.setAttribute("height", height);
        request.setAttribute("weight", weight);
        request.setAttribute("months", months);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);

        request.getRequestDispatcher("view_member.jsp").forward(request, response);
    }

    // ======================
    // 🔥 UPDATE + DELETE (POST)
    // ======================
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String action = request.getParameter("action");
        String fid = request.getParameter("fid");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/gym_system",
                    "root",
                    "1234"
            );

            // ======================
            // 🔥 DELETE (DEVICE + DB)
            // ======================
            if ("delete".equals(action)) {

                try {
                    System.load("C:\\Windows\\System32\\jacob-1.21-x64.dll");

                    ActiveXComponent zk = new ActiveXComponent("zkemkeeper.ZKEM");

                    boolean isConnected = zk.invoke("Connect_Net",
                            new Variant("192.168.8.201"),
                            new Variant(4370)).getBoolean();

                    if (isConnected) {

                        // 🔥 DELETE FINGERPRINT
                        zk.invoke("SSR_DeleteEnrollData",
                                new Variant(1),
                                new Variant(fid),
                                new Variant(12) // ALL fingers
                        );

                        // 🔥 DELETE USER
                        zk.invoke("DeleteUserInfo",
                                new Variant(1),
                                new Variant(fid)
                        );

                        zk.invoke("RefreshData", new Variant(1));
                        zk.invoke("Disconnect");

                        System.out.println("✅ Deleted from DEVICE: " + fid);

                    } else {
                        System.out.println("❌ Device not connected!");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 🔥 DELETE FROM DB
                String q1 = "DELETE ms FROM membership_details ms " +
                        "JOIN member_details md ON ms.member_id=md.id " +
                        "WHERE md.fingerprint_id=?";
                PreparedStatement ps1 = con.prepareStatement(q1);
                ps1.setString(1, fid);
                ps1.executeUpdate();

                String q2 = "DELETE FROM member_details WHERE fingerprint_id=?";
                PreparedStatement ps2 = con.prepareStatement(q2);
                ps2.setString(1, fid);
                ps2.executeUpdate();

                response.sendRedirect("fingerprint-data?page=users");
                return;
            }

            // ======================
            // 🔥 UPDATE
            // ======================
            if ("update".equals(action)) {

                String name = request.getParameter("name");
                String phone = request.getParameter("phone");
                String gender = request.getParameter("gender");
                int age = Integer.parseInt(request.getParameter("age"));
                String whatsapp = request.getParameter("whatsapp");
                String address = request.getParameter("address");
                String height = request.getParameter("height");
                String weight = request.getParameter("weight");

                int months = Integer.parseInt(request.getParameter("months"));
                String start = request.getParameter("startDate");
                String end = request.getParameter("endDate");

                // update member
                String q1 = "UPDATE member_details SET full_name=?, phone=?, gender=?, age=?, whatsapp=?, address=?, height=?, weight=? WHERE fingerprint_id=?";
                PreparedStatement ps1 = con.prepareStatement(q1);

                ps1.setString(1, name);
                ps1.setString(2, phone);
                ps1.setString(3, gender);
                ps1.setInt(4, age);
                ps1.setString(5, whatsapp);
                ps1.setString(6, address);
                ps1.setString(7, height);
                ps1.setString(8, weight);
                ps1.setString(9, fid);

                ps1.executeUpdate();

                // update membership
                String q2 = "UPDATE membership_details ms " +
                        "JOIN member_details md ON ms.member_id=md.id " +
                        "SET ms.months=?, ms.start_date=?, ms.end_date=? " +
                        "WHERE md.fingerprint_id=?";
                PreparedStatement ps2 = con.prepareStatement(q2);

                ps2.setInt(1, months);
                ps2.setString(2, start);
                ps2.setString(3, end);
                ps2.setString(4, fid);

                ps2.executeUpdate();

                response.sendRedirect("view-member?fid=" + fid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}