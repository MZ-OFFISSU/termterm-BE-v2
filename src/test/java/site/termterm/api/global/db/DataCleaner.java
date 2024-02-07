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

    /*
     * MySQL DBMS 에서 테스트를 수행할 것이라면 아래의 코드 사
     * 용
     * private static final String FOREIGN_KEY_CHECK_FORMAT = "SET FOREIGN_KEY_CHECKS %d";
     * private static final String TRUNCATE_FORMAT = "TRUNCATE TABLE %s";
     */

    private static final String FOREIGN_KEY_CHECK_FORMAT = "SET REFERENTIAL_INTEGRITY %s";
    private static final String TRUNCATE_FORMAT = "TRUNCATE TABLE %s";
    private static final String COLUMN_ID_RESTART_FORMAT = "ALTER TABLE %s ALTER COLUMN %S_ID RESTART WITH 1";

    private final List<String> tableNames = new ArrayList<>();

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void findDatabaseTableNames() {
        List<Object[]> tableInfos = entityManager.createNativeQuery("SHOW TABLES").getResultList();
        for (Object[] tableInfo : tableInfos) {
            String tableName = (String) tableInfo[0];
            tableNames.add(tableName);
        }
    }

    @Transactional
    public void clear() {
        entityManager.clear();
        truncate();
    }

    private void truncate() {
        entityManager.createNativeQuery(String.format(FOREIGN_KEY_CHECK_FORMAT, "FALSE")).executeUpdate();
        for (String tableName : tableNames) {
            entityManager.createNativeQuery(String.format(TRUNCATE_FORMAT, tableName)).executeUpdate();
            entityManager.createNativeQuery(String.format(COLUMN_ID_RESTART_FORMAT, tableName, tableName)).executeUpdate();
        }
        entityManager.createNativeQuery(String.format(FOREIGN_KEY_CHECK_FORMAT, "TRUE")).executeUpdate();
    }
}