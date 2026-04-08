package com.example.city_gym_management_system;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/save-member")
public class SaveMemberServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // ======================
        // 🔥 GET + CLEAN FID
        // ======================
        String fid = request.getParameter("userId");

        if (fid != null) {
            fid = fid.replaceAll("[^0-9]", "").trim();
        }

        System.out.println("FINAL FID: [" + fid + "]");

        // ❌ STOP if invalid
        if (fid == null || fid.isEmpty()) {
            System.out.println("❌ ERROR: FID EMPTY!");
            response.sendRedirect("fingerprint-data?page=users");
            return;
        }

        // ======================
        // 🔥 GET FORM DATA
        // ======================
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String gender = request.getParameter("gender");
        String whatsapp = request.getParameter("whatsapp");
        String address = request.getParameter("address");
        String height = request.getParameter("height");
        String weight = request.getParameter("weight");

        int age = 0;
        int months = 0;

        try {
            age = Integer.parseInt(request.getParameter("age"));
            months = Integer.parseInt(request.getParameter("months"));
        } catch (Exception e) {
            System.out.println("⚠️ Age or months parse error");
        }

        String start = request.getParameter("startDate");
        String end = request.getParameter("endDate");

        Connection con = null;
        PreparedStatement psCheck = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/gym_system",
                    "root",
                    "1234"
            );

            // ======================
            // 🔥 DUPLICATE CHECK
            // ======================
            String checkSql = "SELECT id FROM member_details WHERE fingerprint_id=?";
            psCheck = con.prepareStatement(checkSql);
            psCheck.setString(1, fid);

            rs = psCheck.executeQuery();

            if (rs.next()) {
                System.out.println("⚠️ Already exists: " + fid);
                response.sendRedirect("fingerprint-data?page=users");
                return;
            }

            // ======================
            // 🔥 INSERT MEMBER
            // ======================
            String q1 = "INSERT INTO member_details (fingerprint_id, full_name, phone, gender, age, whatsapp, address, height, weight) VALUES (?,?,?,?,?,?,?,?,?)";

            ps1 = con.prepareStatement(q1, Statement.RETURN_GENERATED_KEYS);

            ps1.setString(1, fid);
            ps1.setString(2, name);
            ps1.setString(3, phone);
            ps1.setString(4, gender);
            ps1.setInt(5, age);
            ps1.setString(6, whatsapp);
            ps1.setString(7, address);
            ps1.setString(8, height);
            ps1.setString(9, weight);

            int rows = ps1.executeUpdate();

            if (rows == 0) {
                throw new SQLException("❌ Member insert failed!");
            }

            ResultSet keyRs = ps1.getGeneratedKeys();
            int memberId = 0;

            if (keyRs.next()) {
                memberId = keyRs.getInt(1);
            }

            if (memberId == 0) {
                throw new SQLException("❌ Member ID not generated!");
            }

            System.out.println("✅ Member ID: " + memberId);

            // ======================
            // 🔥 INSERT MEMBERSHIP
            // ======================
            String q2 = "INSERT INTO membership_details (member_id, months, start_date, end_date) VALUES (?,?,?,?)";

            ps2 = con.prepareStatement(q2);

            ps2.setInt(1, memberId);
            ps2.setInt(2, months);
            ps2.setString(3, start);
            ps2.setString(4, end);

            ps2.executeUpdate();

            System.out.println("✅ Membership saved!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (psCheck != null) psCheck.close(); } catch (Exception e) {}
            try { if (ps1 != null) ps1.close(); } catch (Exception e) {}
            try { if (ps2 != null) ps2.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }

        // ======================
        // 🔥 REDIRECT
        // ======================
        response.sendRedirect("fingerprint-data?page=users");
    }
}