package springbook.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import springbook.user.domain.User;

public class UserDao {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }

    public void add(final User user) {
        this.jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)",
            user.getId(), user.getName(), user.getPassword());
    }

    public void deleteAll() {
        this.jdbcTemplate.update("delete from users");
    }

    // 이렇게 콜백을 jdbcTemplate update 메서드에 넘겨줘도 된다.
    /*
    public void deleteAll() {
        this.jdbcTemplate.update(
            new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    return con.prepareStatement("delete from users"); }
            } );
    }
    */

    public User get(String id) {
        return this.jdbcTemplate.queryForObject("select * from users where id = ?",
            new Object[]{id},
            new RowMapper<User>() {
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setPassword(rs.getString("password"));
                    return user;
                }
            });
    }

    public int getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
    }

    // 두 개의 콜백을 템플릿으로 던져서 count 기능을 구현하는 방법
    /*
    public int getCount() {
        return this.jdbcTemplate.query(new PreparedStatementCreator() { // 첫 번째 콜백. Statement 생성
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                return con.prepareStatement("select count(*) from users");
            }
        }, new ResultSetExtractor<Integer>() { // 두 번째 콜백.ResultSet으로부터 값 추출
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                rs.next();
                return rs.getInt(1); }
        }); }
     */

}