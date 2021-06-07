package springbook.user.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class DUserDao extends UserDao {

    public Connection getConnection() throws SQLException {
        // D 사 DB connection 생성코드
        return null;
    }
}
