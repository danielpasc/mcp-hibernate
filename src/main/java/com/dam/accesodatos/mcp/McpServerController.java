package com.dam.accesodatos.mcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.dam.accesodatos.ra3.HibernateUserService;
import com.dam.accesodatos.model.User;
import com.dam.accesodatos.model.UserCreateDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST que expone las herramientas MCP via HTTP para operaciones Hibernate/JPA.
 *
 * CAMBIOS vs RA2:
 * - RA2: Usa DatabaseUserService (JDBC)
 * - RA3: Usa HibernateUserService (Hibernate/JPA)
 *
 * Proporciona endpoints para que los LLMs puedan:
 * - Listar herramientas Hibernate/JPA disponibles
 * - Ejecutar operaciones ORM específicas
 * - Obtener información sobre el servidor MCP
 */
@RestController
@RequestMapping("/mcp")
@CrossOrigin(origins = "*")
public class McpServerController {

    private static final Logger logger = LoggerFactory.getLogger(McpServerController.class);

    @Autowired
    private HibernateUserService hibernateUserService;  // ← CAMBIO: HibernateUserService en lugar de DatabaseUserService

    @Autowired
    private McpToolRegistry toolRegistry;

    /**
     * Endpoint de health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> getHealth() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "MCP Server RA3 Hibernate/JPA");  // ← CAMBIO

        return ResponseEntity.ok(health);
    }

    /**
     * Endpoint para listar todas las herramientas MCP disponibles
     */
    @GetMapping("/tools")
    public ResponseEntity<Map<String, Object>> getTools() {
        logger.debug("Solicitadas herramientas MCP Hibernate/JPA disponibles");

        List<McpToolRegistry.McpToolInfo> tools = toolRegistry.getRegisteredTools();

        List<Map<String, String>> toolsList = tools.stream()
                .map(tool -> {
                    Map<String, String> toolMap = new HashMap<>();
                    toolMap.put("name", tool.getName());
                    toolMap.put("description", tool.getDescription());
                    return toolMap;
                })
                .collect(java.util.stream.Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("tools", toolsList);
        response.put("count", toolsList.size());
        response.put("server", "MCP Server - RA3 Hibernate/JPA DAM");  // ← CAMBIO
        response.put("version", "1.0.0");

        return ResponseEntity.ok(response);
    }

    // ========== HIBERNATE/JPA OPERATION ENDPOINTS ==========

    /**
     * Prueba el EntityManager de Hibernate/JPA
     */
    @PostMapping("/test_entity_manager")  // ← CAMBIO: test_entity_manager en lugar de test_connection
    public ResponseEntity<Map<String, Object>> testEntityManager() {
        logger.debug("Probando EntityManager");

        try {
            String result = hibernateUserService.testEntityManager();

            Map<String, Object> response = new HashMap<>();
            response.put("tool", "test_entity_manager");
            response.put("result", result);
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error probando EntityManager", e);

            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error probando EntityManager: " + e.getMessage());
            error.put("tool", "test_entity_manager");
            error.put("status", "error");

            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Crea un nuevo usuario usando persist()
     */
    @PostMapping("/create_user")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, String> request) {
        logger.debug("Creando usuario con Hibernate");

        try {
            String name = request.get("name");
            String email = request.get("email");
            String department = request.get("department");
            String role = request.get("role");

            UserCreateDto dto = new UserCreateDto(name, email, department, role);
            User user = hibernateUserService.createUser(dto);

            Map<String, Object> response = new HashMap<>();
            response.put("tool", "create_user");
            response.put("result", user);
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creando usuario", e);

            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error creando usuario: " + e.getMessage());
            error.put("tool", "create_user");
            error.put("status", "error");

            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Busca un usuario por ID usando find()
     */
    @PostMapping("/find_user_by_id")
    public ResponseEntity<Map<String, Object>> findUserById(@RequestBody Map<String, Object> request) {
        logger.debug("Buscando usuario por ID");

        try {
            Long userId = ((Number) request.get("userId")).longValue();
            User user = hibernateUserService.findUserById(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("tool", "find_user_by_id");
            response.put("result", user);
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error buscando usuario", e);

            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error buscando usuario: " + e.getMessage());
            error.put("tool", "find_user_by_id");
            error.put("status", "error");

            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Obtiene todos los usuarios usando JPA Repository
     */
    @PostMapping("/find_all_users")
    public ResponseEntity<Map<String, Object>> findAllUsers() {
        logger.debug("Obteniendo todos los usuarios");

        try {
            List<User> users = hibernateUserService.findAll();

            Map<String, Object> response = new HashMap<>();
            response.put("tool", "find_all_users");
            response.put("result", users);
            response.put("count", users.size());
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error obteniendo usuarios", e);

            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error obteniendo usuarios: " + e.getMessage());
            error.put("tool", "find_all_users");
            error.put("status", "error");

            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Busca usuarios por departamento usando JPQL
     */
    @PostMapping("/find_users_by_department")
    public ResponseEntity<Map<String, Object>> findUsersByDepartment(@RequestBody Map<String, String> request) {
        logger.debug("Buscando usuarios por departamento");

        try {
            String department = request.get("department");
            List<User> users = hibernateUserService.findUsersByDepartment(department);

            Map<String, Object> response = new HashMap<>();
            response.put("tool", "find_users_by_department");
            response.put("result", users);
            response.put("count", users.size());
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error buscando usuarios por departamento", e);

            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error buscando usuarios por departamento: " + e.getMessage());
            error.put("tool", "find_users_by_department");
            error.put("status", "error");

            return ResponseEntity.status(500).body(error);
        }
    }
}
