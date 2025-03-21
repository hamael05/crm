package site.easy.to.build.crm.service.reset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResetService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ResetService(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional
    public void resetDatabase() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0"); // Désactiver les clés étrangères

        List<String> tables = new ArrayList<>();
        tables.add("contract_settings");
        tables.add("email_template");
        tables.add("file");
        tables.add("google_drive_file");
        tables.add("lead_action");
        tables.add("lead_settings");
        tables.add("ticket_settings");
        tables.add("trigger_contract");
        tables.add("trigger_lead");
        tables.add("trigger_ticket");

        for (String table : tables) {
            jdbcTemplate.execute("TRUNCATE TABLE " + table); // Vider la table et réinitialiser AUTO_INCREMENT
        }

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1"); // Réactiver les clés étrangères
    }
}

