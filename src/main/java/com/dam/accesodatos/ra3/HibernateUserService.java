package com.dam.accesodatos.ra3;

import com.dam.accesodatos.model.User;
import com.dam.accesodatos.model.UserCreateDto;
import com.dam.accesodatos.model.UserUpdateDto;
import com.dam.accesodatos.model.UserQueryDto;
import org.springframework.ai.mcp.server.annotation.Tool;

import java.util.List;
import java.util.Map;

/**
 * Interface de servicio para operaciones Hibernate/JPA con usuarios
 *
 * RA3: Gestiona la persistencia de los datos identificando herramientas de mapeo objeto relacional (ORM)
 *
 * VERSIÓN MÍNIMOS ESTRICTOS
 * Esta interface define 10 herramientas MCP (métodos @Tool) enfocadas en los criterios esenciales del RA3
 * usando Hibernate/JPA (EntityManager, JPQL, @Transactional, etc.)
 *
 * DIFERENCIAS vs RA2 (JDBC):
 * - RA2 usa: Connection, PreparedStatement, ResultSet, SQL puro
 * - RA3 usa: EntityManager, JPQL, @Transactional, Hibernate ORM
 *
 * Métodos organizados por criterios de evaluación RA3:
 * - CE3.a: Instalación y configuración ORM (1 método)
 * - CE3.d, CE3.e: Operaciones CRUD con Hibernate (5 métodos)
 * - CE3.f: Consultas JPQL (3 métodos - searchUsers simplificado a JPQL)
 * - CE3.g: Gestión de transacciones (1 método)
 *
 * Total: 10 métodos (6 ejemplos + 4 TODOs para estudiantes)
 */
public interface HibernateUserService {

    // ========== CE3.a: Configuración y Conexión ORM ==========

    /**
     * CE3.a: Prueba el EntityManager de Hibernate/JPA
     *
     * Implementación requerida:
     * - Verificar que EntityManager esté abierto con entityManager.isOpen()
     * - Ejecutar una query nativa simple: SELECT 1, DATABASE()
     * - Retornar información de conexión
     *
     * Clases JPA requeridas:
     * - jakarta.persistence.EntityManager (inyectado con @PersistenceContext)
     * - jakarta.persistence.Query para queries nativas
     *
     * DIFERENCIAS vs RA2:
     * - RA2: Connection.isClosed(), Statement.executeQuery()
     * - RA3: EntityManager.isOpen(), createNativeQuery()
     *
     * @return Mensaje indicando si EntityManager está activo
     * @throws RuntimeException si EntityManager está cerrado
     */
    @Tool(name = "test_entity_manager",
          description = "Prueba el EntityManager de Hibernate/JPA")
    String testEntityManager();

    // ========== CE3.d, CE3.e: Operaciones CRUD con Hibernate ==========

    /**
     * CE3.d, CE3.e: Persiste un nuevo usuario usando EntityManager.persist()
     *
     * Implementación requerida:
     * - Crear objeto User desde DTO
     * - Usar entityManager.persist(user)
     * - Anotar método con @Transactional
     * - Hibernate genera el SQL INSERT automáticamente
     * - Hibernate setea el ID autogenerado en el objeto
     *
     * Clases JPA requeridas:
     * - jakarta.persistence.EntityManager
     * - @org.springframework.transaction.annotation.Transactional
     *
     * DIFERENCIAS vs RA2:
     * - RA2: PreparedStatement con INSERT, setString(), getGeneratedKeys()
     * - RA3: entityManager.persist(user), Hibernate genera SQL automáticamente
     *
     * @param dto DTO con datos del usuario a crear
     * @return Usuario creado con ID generado
     * @throws RuntimeException si hay error o email duplicado
     */
    @Tool(name = "create_user",
          description = "Persiste un nuevo usuario usando EntityManager.persist() y @Transactional")
    User createUser(UserCreateDto dto);

    /**
     * CE3.e: Busca un usuario por su ID usando EntityManager.find()
     *
     * Implementación requerida:
     * - Usar entityManager.find(User.class, id)
     * - Hibernate genera SELECT automáticamente
     * - Hibernate mapea ResultSet a objeto User automáticamente
     * - Retorna null si no existe
     *
     * Clases JPA requeridas:
     * - jakarta.persistence.EntityManager
     * - Método: find(Class<T> entityClass, Object primaryKey)
     *
     * DIFERENCIAS vs RA2:
     * - RA2: PreparedStatement SELECT, rs.next(), mapeo manual con rs.getLong(), rs.getString()
     * - RA3: entityManager.find(User.class, id), mapeo automático por Hibernate
     *
     * @param id ID del usuario a buscar
     * @return Usuario encontrado o null si no existe
     * @throws RuntimeException si hay error de BD
     */
    @Tool(name = "find_user_by_id",
          description = "Busca un usuario por ID usando EntityManager.find()")
    User findUserById(Long id);

    /**
     * CE3.e: Actualiza un usuario existente usando EntityManager.merge()
     *
     * Implementación requerida:
     * - Buscar usuario existente con find()
     * - Aplicar cambios del DTO
     * - Usar entityManager.merge(user)
     * - Anotar con @Transactional
     * - Hibernate genera SQL UPDATE automáticamente
     *
     * Clases JPA requeridas:
     * - jakarta.persistence.EntityManager
     * - Método: merge(T entity)
     *
     * DIFERENCIAS vs RA2:
     * - RA2: PreparedStatement UPDATE users SET ... WHERE id = ?
     * - RA3: entityManager.merge(user), Hibernate detecta cambios y genera UPDATE
     *
     * @param id ID del usuario a actualizar
     * @param dto DTO con datos a actualizar (campos opcionales)
     * @return Usuario actualizado
     * @throws RuntimeException si el usuario no existe o hay error
     */
    @Tool(name = "update_user",
          description = "Actualiza un usuario existente usando EntityManager.merge() y @Transactional")
    User updateUser(Long id, UserUpdateDto dto);

    /**
     * CE3.e: Elimina un usuario usando EntityManager.remove()
     *
     * Implementación requerida:
     * - Buscar usuario con find()
     * - Usar entityManager.remove(user)
     * - Anotar con @Transactional
     * - Hibernate genera SQL DELETE automáticamente
     *
     * Clases JPA requeridas:
     * - jakarta.persistence.EntityManager
     * - Método: remove(Object entity)
     *
     * DIFERENCIAS vs RA2:
     * - RA2: PreparedStatement DELETE FROM users WHERE id = ?
     * - RA3: entityManager.remove(user), Hibernate genera DELETE
     *
     * @param id ID del usuario a eliminar
     * @return true si se eliminó, false si no existía
     * @throws RuntimeException si hay error de BD
     */
    @Tool(name = "delete_user",
          description = "Elimina un usuario usando EntityManager.remove() y @Transactional")
    boolean deleteUser(Long id);

    /**
     * CE3.e: Obtiene todos los usuarios usando Spring Data JPA Repository
     *
     * Implementación requerida:
     * - Usar userRepository.findAll()
     * - Spring Data genera "SELECT u FROM User u" automáticamente
     *
     * Clases JPA requeridas:
     * - org.springframework.data.jpa.repository.JpaRepository
     * - Método: findAll()
     *
     * DIFERENCIAS vs RA2:
     * - RA2: SELECT * FROM users, while(rs.next()), mapeo manual
     * - RA3: userRepository.findAll(), todo automático
     *
     * @return Lista de todos los usuarios
     * @throws RuntimeException si hay error
     */
    @Tool(name = "find_all_users",
          description = "Obtiene todos los usuarios usando JPA Repository.findAll()")
    List<User> findAll();

    // ========== CE3.f: Consultas JPQL/HQL ==========

    /**
     * CE3.f: Busca usuarios por departamento usando JPQL
     *
     * Implementación requerida:
     * - Crear TypedQuery con JPQL: "SELECT u FROM User u WHERE u.department = :dept"
     * - Setear parámetro con setParameter("dept", department)
     * - Ejecutar con getResultList()
     * - IMPORTANTE: JPQL usa nombres de entidades (User), no tablas (users)
     *
     * Clases JPA requeridas:
     * - jakarta.persistence.TypedQuery
     * - entityManager.createQuery(jpql, User.class)
     *
     * DIFERENCIAS vs RA2:
     * - RA2: SQL "SELECT * FROM users WHERE department = ?"
     * - RA3: JPQL "SELECT u FROM User u WHERE u.department = :dept"
     *
     * @param department Nombre del departamento
     * @return Lista de usuarios del departamento
     * @throws RuntimeException si hay error
     */
    @Tool(name = "find_users_by_department",
          description = "Busca usuarios por departamento usando JPQL")
    List<User> findUsersByDepartment(String department);

    /**
     * CE3.f: Busca usuarios con filtros dinámicos usando JPQL
     *
     * VERSIÓN SIMPLIFICADA: Usa JPQL en lugar de Criteria API
     *
     * Implementación requerida:
     * - Construir JPQL dinámicamente según filtros presentes en queryDto
     * - Ejemplo: "SELECT u FROM User u WHERE 1=1" + condiciones dinámicas
     * - Si queryDto.getDepartment() != null: añadir "AND u.department = :dept"
     * - Si queryDto.getRole() != null: añadir "AND u.role = :role"
     * - Si queryDto.getActive() != null: añadir "AND u.active = :active"
     * - Crear TypedQuery y setear parámetros solo para los filtros presentes
     *
     * Clases JPA requeridas:
     * - jakarta.persistence.TypedQuery
     * - entityManager.createQuery(jpql, User.class)
     *
     * DIFERENCIAS vs RA2:
     * - RA2: StringBuilder para construir SQL dinámico
     * - RA3: JPQL con parámetros nombrados
     *
     * @param query DTO con filtros opcionales
     * @return Lista de usuarios que cumplen los criterios
     * @throws RuntimeException si hay error
     */
    @Tool(name = "search_users",
          description = "Busca usuarios con filtros dinámicos usando JPQL")
    List<User> searchUsers(UserQueryDto query);

    // ========== CE3.g: Gestión de Transacciones ==========

    /**
     * CE3.g: Inserta múltiples usuarios en una transacción con @Transactional
     *
     * Implementación requerida:
     * - Anotar con @Transactional
     * - En bucle: entityManager.persist(user)
     * - Si hay error, Spring hace rollback automáticamente
     * - Si todo OK, Spring hace commit automáticamente
     *
     * Clases JPA requeridas:
     * - @Transactional de Spring
     * - EntityManager
     *
     * DIFERENCIAS vs RA2:
     * - RA2: conn.setAutoCommit(false), commit(), rollback() manual
     * - RA3: @Transactional, todo automático
     *
     * @param users Lista de usuarios a insertar en transacción
     * @return true si la transacción fue exitosa
     * @throws RuntimeException si hay error y se hace rollback
     */
    @Tool(name = "transfer_data",
          description = "Inserta múltiples usuarios en una transacción usando @Transactional")
    boolean transferData(List<User> users);

    /**
     * CE3.f: Ejecuta consulta COUNT por departamento usando JPQL
     *
     * Implementación requerida:
     * - Crear TypedQuery con JPQL: "SELECT COUNT(u) FROM User u WHERE u.department = :dept"
     * - Setear parámetro
     * - Ejecutar con getSingleResult()
     * - Retornar Long con el count
     *
     * Clases JPA requeridas:
     * - jakarta.persistence.TypedQuery<Long>
     *
     * DIFERENCIAS vs RA2:
     * - RA2: CallableStatement para stored procedure
     * - RA3: JPQL COUNT query directo
     *
     * @param department Departamento a contar
     * @return Número de usuarios activos en el departamento
     * @throws RuntimeException si hay error
     */
    @Tool(name = "execute_count_by_department",
          description = "Ejecuta consulta COUNT usando JPQL")
    long executeCountByDepartment(String department);
}
