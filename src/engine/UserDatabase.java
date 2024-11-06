package engine;
import java.sql.*; // SQL 관련 클래스 가져오기

public class UserDatabase {

    // 데이터베이스 연결 메서드
    private Connection connect() {
        String url = "jdbc:sqlite:user_data.db"; // SQLite 데이터베이스 파일 경로
        Connection conn = null; // 연결 객체 초기화
        try {
            Class.forName("org.sqlite.JDBC"); // SQLite JDBC 드라이버 로드
            conn = DriverManager.getConnection(url); // 데이터베이스 연결 생성
        } catch (SQLException | ClassNotFoundException e) { // 예외 처리
            e.printStackTrace(); // 예외 메시지 출력
        }
        return conn; // 연결 반환
    }

    // users 테이블 생성 메서드
    public void createTable() {
        // 테이블을 생성하는 SQL 명령어
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
                + "    id TEXT PRIMARY KEY,\n" // 기본 키 ID 컬럼
                + "    password TEXT NOT NULL,\n" // NOT NULL 제약 조건이 있는 비밀번호 컬럼
                + "    email TEXT\n" // 이메일 컬럼
                + ");";

        try (Connection conn = this.connect(); // 데이터베이스 연결 열기
             Statement stmt = conn.createStatement()) { // SQL 명령어 실행 준비
            stmt.execute(sql); // SQL 명령어 실행 (테이블 생성)
        } catch (SQLException e) { // 예외 처리
            e.printStackTrace();
        }
    }

    // 사용자 등록 메서드
    public boolean registerUser(String id, String password, String email) {
        // 사용자 정보를 삽입하는 SQL 명령어
        String sql = "INSERT INTO users(id, password, email) VALUES(?,?,?)";

        try (Connection conn = this.connect(); // 데이터베이스 연결 열기
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // SQL 명령어 준비
            pstmt.setString(1, id); // ID 설정
            pstmt.setString(2, password); // 비밀번호 설정
            pstmt.setString(3, email); // 이메일 설정
            pstmt.executeUpdate(); // 데이터베이스에 정보 삽입
            return true; // 성공적으로 삽입하면 true 반환
        } catch (SQLException e) { // 예외 처리
            System.out.println("ID already exists or registration failed.");
            return false; // 실패 시 false 반환
        }
    }

    // 사용자 로그인 메서드
    public boolean loginUser(String id, String password) {
        // 사용자 ID와 비밀번호를 확인하는 SQL 명령어
        String sql = "SELECT id, password FROM users WHERE id = ? AND password = ?";

        try (Connection conn = this.connect(); // 데이터베이스 연결 열기
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // SQL 명령어 준비
            pstmt.setString(1, id); // ID 설정
            pstmt.setString(2, password); // 비밀번호 설정
            ResultSet rs = pstmt.executeQuery(); // 쿼리 실행 및 결과 받기

            return rs.next(); // 결과가 있으면 true 반환
        } catch (SQLException e) { // 예외 처리
            e.printStackTrace();
            return false; // 실패 시 false 반환
        }
    }
}
