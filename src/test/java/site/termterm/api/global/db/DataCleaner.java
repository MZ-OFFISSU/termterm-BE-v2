package site.termterm.api.global.db;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("test")
public class DataCleaner {

    private static final String FOREIGN_KEY_CHECK_FORMAT = "SET @@FOREIGN_KEY_CHECKS = %s";
    private static final String TRUNCATE_FORMAT = "TRUNCATE TABLE %s";
    private static final String COLUMN_ID_RESTART_FORMAT = "ALTER TABLE %s AUTO_INCREMENT 1";

    private final List<String> tableNames = new ArrayList<>();

    private final List<String> compositeKeyTables = List.of("COMMENT_LIKE", "TERM_BOOKMARK", "CURATION_BOOKMARK");
    private final List<String> memberKeyTables = List.of("CURATION_PAID", "DAILY_TERM");

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void findDatabaseTableNames() {
        List<String> tableNames = entityManager.createNativeQuery("SHOW TABLES").getResultList();
        this.tableNames.addAll(tableNames);
    }

    @Transactional
    public void clear() {
        entityManager.clear();
        truncate();
    }

    private void truncate() {
        entityManager.createNativeQuery(String.format(FOREIGN_KEY_CHECK_FORMAT, 0)).executeUpdate();
        for (String tableName : tableNames) {
            entityManager.createNativeQuery(String.format(TRUNCATE_FORMAT, tableName)).executeUpdate();

            // 복합키 엔티티일 경우 따로 처리
            if (compositeKeyTables.contains(tableName)){
                entityManager.createNativeQuery(String.format("ALTER TABLE %s ALTER COLUMN %s_ID RESTART WITH 1", tableName, tableName.split("_")[0])).executeUpdate();
                entityManager.createNativeQuery(String.format("ALTER TABLE %s ALTER COLUMN MEMBER_ID RESTART WITH 1", tableName)).executeUpdate();
            }
            else if(memberKeyTables.contains(tableName)){
                entityManager.createNativeQuery(String.format("ALTER TABLE %s ALTER COLUMN %s_ID RESTART WITH 1", tableName, "MEMBER")).executeUpdate();
            }
            else if(tableName.equals("HOME_SUBTITLE")){
            }
            else {
                entityManager.createNativeQuery(String.format(COLUMN_ID_RESTART_FORMAT, tableName)).executeUpdate();
            }
        }
        entityManager.createNativeQuery(String.format(FOREIGN_KEY_CHECK_FORMAT, 1)).executeUpdate();
    }
}