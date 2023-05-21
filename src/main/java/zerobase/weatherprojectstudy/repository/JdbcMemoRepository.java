package zerobase.weatherprojectstudy.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import zerobase.weatherprojectstudy.domain.Memo;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMemoRepository {
    private final JdbcTemplate jdbcTemplate;

    // application.properties에 담겨있는 정보들이 DataSource객체에 담겨있음
    @Autowired
    public JdbcMemoRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Memo save(Memo memo){
        String sql = "insert into memo values(?,?)";
        jdbcTemplate.update(sql, memo.getId(), memo.getText());
        return memo;
    }

    public List<Memo> findAll(){
        String sql = "select * from memo";
        return jdbcTemplate.query(sql, memoRowMapper());
    }

    public Optional<Memo> findById(int id){
        String sql = "select * from memo where id = ?";
        return jdbcTemplate.query(sql, memoRowMapper(), id).stream().findFirst();
    }

    // jdbc를 통해 mysql 의 데이터를 가져오면 이 가져온 데이터는 ResultSet 형식
    private RowMapper<Memo> memoRowMapper(){
        // {id = 1, text = "~~~~"}
        // ResultSet을 Memo형식으로 매핑
        return((rs, rowNum) -> new Memo(
                rs.getInt("id"),
                rs.getString("text")
        ));
    }

}
