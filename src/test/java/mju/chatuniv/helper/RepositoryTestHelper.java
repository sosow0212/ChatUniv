package mju.chatuniv.helper;

import java.util.List;
import mju.chatuniv.global.config.JpaAuditingConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@Import(JpaAuditingConfig.class)
@DataJpaTest
public class RepositoryTestHelper {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void initDatabase() {
        List<String> truncateAllTablesQuery = jdbcTemplate.queryForList(
                "SELECT CONCAT('TRUNCATE TABLE ', TABLE_NAME, ' RESTART IDENTITY', ';') AS q FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'",
                String.class);
        truncateAllTables(truncateAllTablesQuery);
    }

    private void truncateAllTables(final List<String> truncateAllTablesQuery) {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");

        for (String truncateQuery : truncateAllTablesQuery) {
            jdbcTemplate.execute(truncateQuery);
        }

        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }
}
