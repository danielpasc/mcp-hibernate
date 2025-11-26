package com.dam.accesodatos.repository;

import com.dam.accesodatos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA para la entidad User
 *
 * RA3 - CRITERIOS d) y f):
 * - CE3.d: Mecanismos de persistencia - Spring Data JPA proporciona métodos CRUD automáticos
 * - CE3.f: Consultas JPQL - Permite definir consultas con @Query
 *
 * DIFERENCIAS vs RA2 (JDBC):
 * - RA2: Implementación manual con PreparedStatement, ResultSet, mapeo manual
 * - RA3: Interface que extiende JpaRepository - Spring genera implementación automáticamente
 *
 * FUNCIONALIDADES AUTOMÁTICAS (sin implementar):
 * - save(User user) - INSERT o UPDATE
 * - findById(Long id) - SELECT por ID
 * - findAll() - SELECT * FROM users
 * - deleteById(Long id) - DELETE
 * - count() - COUNT(*)
 * - existsById(Long id) - Verifica si existe
 *
 * MÉTODOS DERIVADOS:
 * Spring Data JPA genera la implementación automáticamente basándose en el nombre del método:
 * - findByDepartment → SELECT u FROM User u WHERE u.department = ?
 * - findByDepartmentAndActive → WHERE department = ? AND active = ?
 *
 * CONSULTAS JPQL PERSONALIZADAS:
 * Se pueden definir consultas JPQL con @Query cuando la convención de nombres no es suficiente.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ===== MÉTODOS DERIVADOS (Query Methods) =====

    /**
     * Busca usuarios por departamento.
     * Spring Data genera automáticamente: WHERE department = ?
     *
     * @param department Nombre del departamento
     * @return Lista de usuarios del departamento
     */
    List<User> findByDepartment(String department);

    /**
     * Busca usuarios por departamento y estado activo.
     * Spring Data genera: WHERE department = ? AND active = ?
     *
     * @param department Nombre del departamento
     * @param active Estado activo
     * @return Lista de usuarios que cumplen los criterios
     */
    List<User> findByDepartmentAndActive(String department, Boolean active);

    /**
     * Busca usuario por email (único).
     *
     * @param email Email del usuario
     * @return Usuario encontrado o null
     */
    User findByEmail(String email);

    // ===== CONSULTAS JPQL PERSONALIZADAS =====

    /**
     * Busca usuarios activos por departamento ordenados por nombre.
     *
     * NOTA PEDAGÓGICA:
     * JPQL usa nombres de entidades y atributos, NO nombres de tablas y columnas:
     * - Correcto: "FROM User u" (entidad)
     * - Incorrecto: "FROM users u" (tabla)
     *
     * @param dept Nombre del departamento
     * @return Lista de usuarios activos ordenados
     */
    @Query("SELECT u FROM User u WHERE u.department = :dept AND u.active = true ORDER BY u.name")
    List<User> findActiveUsersByDepartment(@Param("dept") String dept);

    /**
     * Cuenta usuarios activos por departamento.
     *
     * Ejemplo de JPQL con agregación (COUNT).
     *
     * @param dept Nombre del departamento
     * @return Número de usuarios activos en el departamento
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.department = :dept AND u.active = true")
    long countActiveUsersByDepartment(@Param("dept") String dept);

    /**
     * Busca usuarios cuyo nombre contiene un texto (case-insensitive).
     *
     * Ejemplo de búsqueda con LIKE en JPQL.
     *
     * @param name Texto a buscar en el nombre
     * @return Lista de usuarios que coinciden
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);
}
