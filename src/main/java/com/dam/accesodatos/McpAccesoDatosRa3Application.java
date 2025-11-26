package com.dam.accesodatos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.server.annotation.EnableMcpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación principal para MCP Server RA3 - Hibernate/JPA
 *
 * RA3: Gestiona la persistencia de los datos identificando herramientas de mapeo objeto relacional (ORM)
 *
 * DIFERENCIAS vs RA2 (JDBC):
 * - RA2: Excluye DataSourceAutoConfiguration (usa JDBC puro con DriverManager)
 * - RA3: NO excluye DataSourceAutoConfiguration (necesario para Hibernate/JPA)
 *
 * CONFIGURACIÓN:
 * - Puerto: 8083 (RA1=8081, RA2=8082)
 * - Base de datos: H2 en memoria (ra3db)
 * - ORM: Hibernate/JPA con Spring Data JPA
 * - Servidor MCP: Habilitado con @EnableMcpServer
 *
 * INICIALIZACIÓN:
 * - schema.sql se ejecuta automáticamente (spring.sql.init.mode=always)
 * - data.sql carga 8 usuarios de prueba
 * - Hibernate está configurado para mostrar SQL (show-sql: true)
 */
@SpringBootApplication  // ← NO excluir DataSourceAutoConfiguration (necesario para JPA)
@EnableMcpServer
public class McpAccesoDatosRa3Application {

    private static final Logger logger = LoggerFactory.getLogger(McpAccesoDatosRa3Application.class);

    public static void main(String[] args) {
        logger.info("=".repeat(80));
        logger.info("Iniciando MCP Server RA3 - Hibernate/JPA");
        logger.info("Puerto: 8083");
        logger.info("Base de datos: H2 en memoria (ra3db)");
        logger.info("ORM: Hibernate/JPA con Spring Data JPA");
        logger.info("=".repeat(80));

        SpringApplication.run(McpAccesoDatosRa3Application.class, args);

        logger.info("=".repeat(80));
        logger.info("✓ Servidor MCP RA3 iniciado correctamente");
        logger.info("✓ H2 Console: http://localhost:8083/h2-console");
        logger.info("  - JDBC URL: jdbc:h2:mem:ra3db");
        logger.info("  - User: sa");
        logger.info("  - Password: (vacío)");
        logger.info("✓ Endpoints MCP disponibles en http://localhost:8083/mcp/tools");
        logger.info("=".repeat(80));
    }
}
