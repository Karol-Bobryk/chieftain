package com.chieftain.repositories;

import com.chieftain.controllers.admin.dto.SystemLogDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class AdminLogJdbcRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AdminLogJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Page<SystemLogDTO> findFilteredLogs(String domain, String severity, Pageable pageable) {
        StringBuilder baseSql = new StringBuilder("FROM v_system_logs WHERE 1=1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (domain != null && !domain.isBlank()) {
            baseSql.append("AND domain = :domain ");
            params.addValue("domain", domain);
        }

        if (severity != null && !severity.isBlank()) {
            baseSql.append("AND severity = :severity ");
            params.addValue("severity", severity);
        }

        String countSql = "SELECT COUNT(*) " + baseSql.toString();
        Integer totalElements = jdbcTemplate.queryForObject(countSql, params, Integer.class);

        String dataSql = "SELECT * " + baseSql.toString() +
                "ORDER BY created_at DESC LIMIT :limit OFFSET :offset";

        params.addValue("limit", pageable.getPageSize());
        params.addValue("offset", pageable.getOffset());

        List<SystemLogDTO> logs = jdbcTemplate.query(dataSql, params, (rs, rowNum) -> new SystemLogDTO(
                rs.getString("domain"),
                rs.getObject("entity_id", UUID.class),
                rs.getString("severity"),
                rs.getString("action"),
                rs.getString("description"),
                rs.getTimestamp("created_at").toLocalDateTime()
        ));

        return new PageImpl<>(logs, pageable, totalElements != null ? totalElements : 0);
    }
}