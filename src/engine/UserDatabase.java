package engine;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class UserDatabase { //아이디,비밀번호 찾기에서 사용하기 위해 public으로 변경

    // 데이터베이스 연결 메서드
    public Connection connect() {
        String url = "jdbc:sqlite:user_data.db";
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }

    // 비밀번호 해시화 메서드 (SHA-256 사용)
    public String hashPassword(String password) {//아이디,비밀번호 찾기에서 사용하기 위해 public으로 변경
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes); // Base64로 인코딩
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: SHA-256 algorithm not found", e);
        }
    }

    // users 테이블 생성 메서드
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
                + "    id TEXT NOT NULL PRIMARY KEY,\n"
                + "    password TEXT NOT NULL,\n"
                + "    email TEXT\n"
                + ");";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 사용자 등록 메서드
    public boolean registerUser(String id, String password, String email) {
        String sql = "INSERT INTO users(id, password, email) VALUES(?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, hashPassword(password)); // 비밀번호 해시화 후 저장
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("ID already exists or registration failed.");
            return false;
        }
    }

    // 사용자 로그인 메서드
    public boolean loginUser(String id, String password) {
        String sql = "SELECT id, password FROM users WHERE id = ? AND password = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, hashPassword(password)); // 입력된 비밀번호를 해시화 후 비교
            ResultSet rs = pstmt.executeQuery();

            return rs.next(); // 결과가 있으면 true 반환
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}