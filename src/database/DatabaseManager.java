package database;

import entity.Achievement;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.*;

public class DatabaseManager { //아이디,비밀번호 찾기에서 사용하기 위해 public으로 변경 // Connection 객체를 클래스 멤버로 정의

    private Connection conn;
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());

    // 데이터베이스 연결 메서드
    public static Connection connect() {
        String url = "jdbc:sqlite:user_data.db";
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Database connection failed", e);
        }
        return conn;
    }

    // 비밀번호&이메일 해시화 메서드 (SHA-256 사용)
    public String hashValue(String password) {//아이디,비밀번호 찾기에서 사용하기 위해 public으로 변경
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes); // Base64로 인코딩
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: SHA-256 algorithm not found", e);
        }
    }

    // user_account 테이블 생성 메서드
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS user_account (\n"
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

    public void createUsersTable(String userId) {

        // CREATE TABLE 쿼리 정의
        String sqlCreate_ach = "CREATE TABLE IF NOT EXISTS user_ach (\n"
                + "    id TEXT NOT NULL PRIMARY KEY,\n"
                + "    HighScore INT DEFAULT 0,\n"
                + "    TotalScore INT DEFAULT 0,\n"
                + "    TotalPlaytime INT DEFAULT 0,\n"
                + "    PerfectStage INT DEFAULT 0,\n"
                + "    Accuracy  NUMERIC(4,2) DEFAULT 0.00,\n"
                + "    MaxCombo INT DEFAULT 0,\n"
                + "    FlawlessFailure BOOLEAN DEFAULT FALSE,\n"
                + "    Ach_2 BOOLEAN DEFAULT FALSE\n"
                + ");";
        String sqlCreate_wallet = "CREATE TABLE IF NOT EXISTS user_wallet (\n"
                + "    id TEXT NOT NULL PRIMARY KEY,\n"
                + "    Coin INT DEFAULT 0,\n"
                + "    BulletSpeed INT DEFAULT 0,\n"
                + "    ShotInterval INT DEFAULT 0,\n"
                + "    AdditionalLife INT DEFAULT 0,\n"
                + "    CoinGain INT DEFAULT 0\n"
                + ");";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlCreate_ach); // 테이블 생성 실행
            stmt.execute(sqlCreate_wallet); // 테이블 생성 실행
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 먼저 해당 userId가 이미 존재하는지 확인
        String checkUserQuery1 = "SELECT id FROM user_ach WHERE id = ?";
        String checkUserQuery2 = "SELECT id FROM user_wallet WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmtCheck = conn.prepareStatement(checkUserQuery1)) {
            pstmtCheck.setString(1, userId);
            ResultSet rs = pstmtCheck.executeQuery();

            // userId가 이미 존재하면 INSERT 하지 않음
            if (!rs.next()) {
                // userId가 없으면 INSERT 실행
                String sqlInsert = "INSERT INTO user_ach (id) VALUES (?)";
                try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                    pstmtInsert.setString(1, userId); // 해당 userId 값을 삽입
                    pstmtInsert.executeUpdate(); // 실행
                }
            } else {
                LOGGER.info("User " + userId + " is already exist in user_ach");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection conn = this.connect();
             PreparedStatement pstmtCheck = conn.prepareStatement(checkUserQuery2)) {
            pstmtCheck.setString(1, userId);
            ResultSet rs = pstmtCheck.executeQuery();

            // userId가 이미 존재하면 INSERT하지 않음
            if (!rs.next()) {
                // userId가 없으면 INSERT 실행
                String sqlInsert = "INSERT INTO user_wallet (id) VALUES (?)";
                try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                    pstmtInsert.setString(1, userId); // 해당 userId 값을 삽입
                    pstmtInsert.executeUpdate(); // 실행
                }
            } else {
                LOGGER.info("User " + userId + " is already exist in user_wallet");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 실수가 있을때 table 삭제
    public void deleteUserTable(String userId) {
        String tableName = "user_ach";  // 유저별 테이블 이름 설정

        // DELETE 쿼리 작성
        String sqlDrop = "DROP TABLE IF EXISTS"+ tableName;

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlDrop);  // 테이블 삭제 실행
            LOGGER.info("Table " + tableName + " has been deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 사용자 등록 메서드
    public boolean registerUser(String id, String password, String email) {
        String sql = "INSERT INTO user_account(id, password, email) VALUES(?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, hashValue(password)); // 비밀번호 해시화 후 저장
            pstmt.setString(3, hashValue(email));
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOGGER.info(id + " is already exists , registration failed.");
            return false;
        }
    }

    // 사용자 로그인 메서드
    public boolean loginUser(String id, String password) {
        String sql = "SELECT id, password FROM user_account WHERE id = ? AND password = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, hashValue(password)); // 입력된 비밀번호를 해시화 후 비교
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // 결과가 있으면 true 반환
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Achievement loadData(String userID) {
        String sql = "SELECT * FROM user_ach WHERE id = ?";
        Achievement userData = null;

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userID); // 쿼리 매개변수 설정

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int highScore = rs.getInt("HighScore");
                    int totalScore = rs.getInt("TotalScore");
                    int totalPlayTime = rs.getInt("TotalPlaytime");
                    int perfectStage = rs.getInt("PerfectStage");
                    double accuracy = rs.getDouble("Accuracy");
                    int maxCombo = rs.getInt("MaxCombo");
                    boolean flawlessFailure = rs.getBoolean("FlawlessFailure");

                    // UserDataLoader 객체 생성 및 반환 준비
                    userData = new Achievement(userID, totalScore, totalPlayTime, perfectStage, accuracy, maxCombo, flawlessFailure);
                    LOGGER.log(Level.SEVERE, "Data loaded successfully for user ID: " + userID);
                } else {
                    LOGGER.log(Level.SEVERE,"No data found for user ID: " + userID);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage());
        }

        // 데이터가 없으면 기본값으로 초기화된 객체 반환
        if (userData == null) {
            userData = new Achievement(null, 0, 0, 0, 0.0, 0, false);
            LOGGER.log(Level.SEVERE,"User data not found for user ID: " + userID);
        }
        return userData;
    }

    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close(); // 연결 닫기
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

