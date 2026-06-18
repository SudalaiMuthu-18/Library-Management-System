package com.library.dao;

import com.library.db.DBConnection;
import com.library.model.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the MEMBERS table.
 */
public class MemberDAO {

    // ─── Get All Members ──────────────────────────────────────────────────────

    public List<Member> getAllMembers() throws SQLException {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT MEMBER_ID, NAME, EMAIL, PHONE FROM MEMBERS ORDER BY MEMBER_ID";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ─── Search Members ───────────────────────────────────────────────────────

    public List<Member> searchMembers(String keyword) throws SQLException {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT MEMBER_ID, NAME, EMAIL, PHONE FROM MEMBERS " +
                     "WHERE UPPER(NAME) LIKE UPPER(?) OR UPPER(EMAIL) LIKE UPPER(?) " +
                     "ORDER BY MEMBER_ID";
        String kw = "%" + keyword + "%";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, kw);
            ps.setString(2, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ─── Get Member by ID ─────────────────────────────────────────────────────

    public Member getMemberById(int id) throws SQLException {
        String sql = "SELECT MEMBER_ID, NAME, EMAIL, PHONE FROM MEMBERS WHERE MEMBER_ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ─── Add Member ───────────────────────────────────────────────────────────

    public boolean addMember(Member member) throws SQLException {
        String sql = "INSERT INTO MEMBERS (NAME, EMAIL, PHONE) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            return ps.executeUpdate() > 0;
        }
    }

    // ─── Update Member ────────────────────────────────────────────────────────

    public boolean updateMember(Member member) throws SQLException {
        String sql = "UPDATE MEMBERS SET NAME=?, EMAIL=?, PHONE=? WHERE MEMBER_ID=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.setInt(4, member.getMemberId());
            return ps.executeUpdate() > 0;
        }
    }

    // ─── Delete Member ────────────────────────────────────────────────────────

    public boolean deleteMember(int id) throws SQLException {
        String sql = "DELETE FROM MEMBERS WHERE MEMBER_ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ─── Count Members ────────────────────────────────────────────────────────

    public int getCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM MEMBERS";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ─── Row mapper ───────────────────────────────────────────────────────────

    private Member mapRow(ResultSet rs) throws SQLException {
        return new Member(
            rs.getInt("MEMBER_ID"),
            rs.getString("NAME"),
            rs.getString("EMAIL"),
            rs.getString("PHONE")
        );
    }
}
