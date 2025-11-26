package com.dam.accesodatos.ra3;

import com.dam.accesodatos.model.User;
import com.dam.accesodatos.model.UserCreateDto;
import com.dam.accesodatos.model.UserUpdateDto;
import com.dam.accesodatos.model.UserQueryDto;
import com.dam.accesodatos.repository.UserRepository;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementación del servicio Hibernate/JPA para gestión de usuarios
 *
 * ESTRUCTURA DE IMPLEMENTACIÓN:
 * - ✅ 6 MÉTODOS IMPLEMENTADOS (ejemplos para estudiantes)
 * - ❌ 9 MÉTODOS TODO (estudiantes deben implementar)
 *
 * MÉTODOS IMPLEMENTADOS (Ejemplos):
 * 1. testEntityManager() - Ejemplo básico de EntityManager
 * 2. createUser() - INSERT con persist() y @Transactional
 * 3. findUserById() - SELECT con find()
 * 4. updateUser() - UPDATE con merge()
 * 5. findAll() - SELECT all con Repository
 * 6. findUsersByDepartment() - JPQL básico
 *
 * MÉTODOS TODO (Estudiantes implementan):
 * 1. getEntityManagerInfo() - EntityManagerFactory properties
 * 2. deleteUser() - EntityManager.remove()
 * 3. searchUsers() - Criteria API dinámica
 * 4. findUsersWithPagination() - Pageable
 * 5. transferData() - Transacción múltiple
 * 6. batchInsertUsers() - Batch con flush()
 * 7. getHibernateInfo() - SessionFactory Statistics
 * 8. getEntityMetadata() - Metamodel
 * 9. executeCountByDepartment() - JPQL COUNT
 */
@Service
@Transactional(readOnly = true)  // Transacciones de solo lectura por defecto
public class HibernateUserServiceImpl implements HibernateUserService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    // ========== CE3.a: Configuración y Conexión ORM ==========

    /**
     * ✅ EJEMPLO IMPLEMENTADO 1/6: Prueba EntityManager
     *
     * Este método muestra el patrón fundamental de JPA:
     * 1. Verificar que EntityManager esté abierto
     * 2. Ejecutar una query nativa simple
     * 3. Procesar resultados
     *
     * DIFERENCIAS vs RA2 (JDBC):
     * - RA2: Connection.isClosed(), Statement.executeQuery("SELECT 1")
     * - RA3: EntityManager.isOpen(), createNativeQuery()
     */
    @Override
    public String testEntityManager() {
        if (!entityManager.isOpen()) {
            throw new RuntimeException("EntityManager está cerrado");
        }

        // Ejecutar query nativa simple (SQL directo, no JPQL)
        Query query = entityManager.createNativeQuery("SELECT 1 as test, DATABASE() as db_name");
        Object[] result = (Object[]) query.getSingleResult();

        return String.format("✓ EntityManager activo | Base de datos: %s | Test: %s",
                result[1], result[0]);
    }

    @Override
    public Map<String, String> getEntityManagerInfo() {
        // TODO CE3.a, CE3.b: Implementar getEntityManagerInfo()
        //
        // Guía de implementación:
        // 1. Obtener EntityManagerFactory:
        //    EntityManagerFactory emf = entityManager.getEntityManagerFactory();
        //
        // 2. Obtener propiedades:
        //    Map<String, Object> properties = emf.getProperties();
        //
        // 3. Crear mapa con información clave:
        //    - hibernate.dialect
        //    - jakarta.persistence.jdbc.url
        //    - jakarta.persistence.jdbc.driver
        //    - hibernate.show_sql
        //    - hibernate.format_sql
        //
        // 4. Retornar Map<String, String> con la información
        //
        // Ejemplo:
        // Map<String, String> info = new HashMap<>();
        // info.put("dialect", properties.get("hibernate.dialect").toString());
        // return info;

        throw new UnsupportedOperationException("TODO CE3.a: Implementar getEntityManagerInfo() - " +
                "Usar EntityManagerFactory.getProperties() para obtener configuración de Hibernate");
    }

    // ========== CE3.d, CE3.e: Operaciones CRUD ==========

    /**
     * ✅ EJEMPLO IMPLEMENTADO 2/6: INSERT con persist()
     *
     * Muestra cómo Hibernate simplifica INSERT:
     * - NO necesitas escribir SQL INSERT
     * - NO necesitas mapear parámetros manualmente
     * - NO necesitas getGeneratedKeys()
     * - Hibernate lo hace todo automáticamente
     *
     * DIFERENCIAS vs RA2 (JDBC):
     * - RA2: PreparedStatement.setString(), executeUpdate(), getGeneratedKeys()
     * - RA3: entityManager.persist(user), todo automático
     *
     * IMPORTANTE: @Transactional es obligatorio para operaciones que modifican BD
     */
    @Override
    @Transactional  // ← CRÍTICO: Modifica BD, necesita transacción
    public User createUser(UserCreateDto dto) {
        // Crear entidad desde DTO
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setDepartment(dto.getDepartment());
        user.setRole(dto.getRole());
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // persist() guarda la entidad en el contexto de persistencia
        // Hibernate genera automáticamente:
        // INSERT INTO users (name, email, department, role, active, created_at, updated_at)
        // VALUES (?, ?, ?, ?, ?, ?, ?)
        entityManager.persist(user);

        // Al finalizar el método, Spring hace commit automáticamente
        // Hibernate ejecuta el INSERT y setea el ID generado
        return user;  // El ID ya está seteado
    }

    /**
     * ✅ EJEMPLO IMPLEMENTADO 3/6: SELECT por ID con find()
     *
     * Muestra la forma más simple de recuperar una entidad por ID.
     *
     * DIFERENCIAS vs RA2 (JDBC):
     * - RA2: PreparedStatement con "SELECT * FROM users WHERE id = ?", mapeo manual de ResultSet
     * - RA3: entityManager.find(User.class, id), todo automático
     *
     * NOTA: find() retorna null si no existe (no lanza excepción)
     */
    @Override
    public User findUserById(Long id) {
        // find() es la forma más directa de buscar por ID
        // Hibernate genera: SELECT ... FROM users WHERE id = ?
        // y mapea automáticamente las columnas a los atributos de User
        return entityManager.find(User.class, id);
    }

    /**
     * ✅ EJEMPLO IMPLEMENTADO 4/6: UPDATE con merge()
     *
     * Muestra cómo actualizar una entidad existente.
     *
     * DIFERENCIAS vs RA2 (JDBC):
     * - RA2: PreparedStatement con "UPDATE users SET ... WHERE id = ?"
     * - RA3: entityManager.merge(user), Hibernate detecta cambios automáticamente
     *
     * PATRÓN IMPORTANTE:
     * 1. Buscar entidad existente
     * 2. Modificar atributos
     * 3. merge() sincroniza cambios con BD
     */
    @Override
    @Transactional  // ← Modifica BD
    public User updateUser(Long id, UserUpdateDto dto) {
        // 1. Buscar entidad existente
        User existing = findUserById(id);
        if (existing == null) {
            throw new RuntimeException("No se encontró usuario con ID " + id);
        }

        // 2. Aplicar cambios del DTO
        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            existing.setEmail(dto.getEmail());
        }
        if (dto.getDepartment() != null) {
            existing.setDepartment(dto.getDepartment());
        }
        if (dto.getRole() != null) {
            existing.setRole(dto.getRole());
        }
        if (dto.getActive() != null) {
            existing.setActive(dto.getActive());
        }
        existing.setUpdatedAt(LocalDateTime.now());

        // 3. merge() actualiza la entidad
        // Hibernate detecta qué campos cambiaron y genera UPDATE solo de esos campos
        return entityManager.merge(existing);
        // Al finalizar, Spring hace commit y Hibernate ejecuta el UPDATE
    }

    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        // TODO CE3.e: Implementar deleteUser()
        //
        // Guía de implementación:
        // 1. Buscar usuario: User user = findUserById(id);
        //
        // 2. Verificar si existe:
        //    if (user == null) return false;
        //
        // 3. Eliminar con remove():
        //    entityManager.remove(user);
        //
        // 4. Retornar true
        //
        // IMPORTANTE: remove() requiere que la entidad esté managed (en contexto de persistencia)
        // Por eso primero la buscamos con find()
        //
        // DIFERENCIA vs RA2:
        // - RA2: DELETE FROM users WHERE id = ?
        // - RA3: entityManager.remove(user)

        throw new UnsupportedOperationException("TODO CE3.e: Implementar deleteUser() - " +
                "Usar find() para buscar y remove() para eliminar");
    }

    /**
     * ✅ EJEMPLO IMPLEMENTADO 5/6: SELECT all con Repository
     *
     * Muestra cómo usar Spring Data JPA Repository.
     * La forma más simple de obtener todas las entidades.
     *
     * DIFERENCIAS vs RA2 (JDBC):
     * - RA2: while(rs.next()) { mapResultSetToUser(rs); }
     * - RA3: userRepository.findAll(), todo automático
     */
    @Override
    public List<User> findAll() {
        // Spring Data JPA genera automáticamente:
        // SELECT u FROM User u
        // y mapea resultados a List<User>
        return userRepository.findAll();
    }

    // ========== CE3.f: Consultas JPQL ==========

    /**
     * ✅ EJEMPLO IMPLEMENTADO 6/6: JPQL básico
     *
     * Muestra cómo escribir consultas JPQL (Java Persistence Query Language).
     *
     * DIFERENCIAS vs RA2 (JDBC):
     * - RA2: SQL "SELECT * FROM users WHERE department = ?"
     * - RA3: JPQL "SELECT u FROM User u WHERE u.department = :dept"
     *
     * IMPORTANTE: JPQL usa nombres de entidades y atributos, NO tablas y columnas
     * - Correcto: "FROM User u" (entidad), "u.department" (atributo)
     * - Incorrecto: "FROM users u" (tabla), "u.department_name" (columna)
     */
    @Override
    public List<User> findUsersByDepartment(String department) {
        // JPQL: Query language orientado a objetos
        // - User (entidad) en lugar de users (tabla)
        // - u.department (atributo) en lugar de department (columna)
        String jpql = "SELECT u FROM User u WHERE u.department = :dept AND u.active = true ORDER BY u.name";

        // TypedQuery garantiza type-safety
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("dept", department);

        // getResultList() retorna List<User>
        return query.getResultList();
    }

    @Override
    public List<User> searchUsers(UserQueryDto queryDto) {
        // TODO CE3.f: Implementar searchUsers() con Criteria API
        //
        // Guía de implementación:
        // 1. Obtener CriteriaBuilder:
        //    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        //
        // 2. Crear CriteriaQuery:
        //    CriteriaQuery<User> cq = cb.createQuery(User.class);
        //
        // 3. Definir Root (FROM):
        //    Root<User> user = cq.from(User.class);
        //
        // 4. Construir predicates dinámicos:
        //    List<Predicate> predicates = new ArrayList<>();
        //    if (queryDto.getDepartment() != null) {
        //        predicates.add(cb.equal(user.get("department"), queryDto.getDepartment()));
        //    }
        //    if (queryDto.getRole() != null) {
        //        predicates.add(cb.equal(user.get("role"), queryDto.getRole()));
        //    }
        //    if (queryDto.getActive() != null) {
        //        predicates.add(cb.equal(user.get("active"), queryDto.getActive()));
        //    }
        //
        // 5. Aplicar WHERE:
        //    cq.where(cb.and(predicates.toArray(new Predicate[0])));
        //
        // 6. Ejecutar query:
        //    return entityManager.createQuery(cq).getResultList();
        //
        // VENTAJA vs JDBC: Type-safe, sin concatenar strings de SQL

        throw new UnsupportedOperationException("TODO CE3.f: Implementar searchUsers() - " +
                "Usar Criteria API para construir query dinámica con filtros opcionales");
    }

    @Override
    public List<User> findUsersWithPagination(int page, int size) {
        // TODO CE3.e: Implementar findUsersWithPagination()
        //
        // Guía de implementación:
        // 1. Crear Pageable:
        //    Pageable pageable = PageRequest.of(page, size);
        //
        // 2. Usar repository con paginación:
        //    Page<User> result = userRepository.findAll(pageable);
        //
        // 3. Retornar contenido:
        //    return result.getContent();
        //
        // DIFERENCIA vs RA2:
        // - RA2: LIMIT y OFFSET manual en SQL
        // - RA3: Pageable abstraction, Spring genera LIMIT/OFFSET automáticamente

        throw new UnsupportedOperationException("TODO CE3.e: Implementar findUsersWithPagination() - " +
                "Usar PageRequest.of() y userRepository.findAll(pageable)");
    }

    // ========== CE3.g: Transacciones ==========

    @Override
    @Transactional
    public boolean transferData(List<User> users) {
        // TODO CE3.g: Implementar transferData()
        //
        // Guía de implementación:
        // 1. Iterar sobre usuarios:
        //    for (User user : users) {
        //        entityManager.persist(user);
        //    }
        //
        // 2. Si todo OK, Spring hace commit automáticamente al finalizar el método
        //
        // 3. Si hay error (excepción), Spring hace rollback automáticamente
        //
        // 4. Retornar true
        //
        // DIFERENCIA vs RA2:
        // - RA2: conn.setAutoCommit(false), try-catch con commit()/rollback() manual
        // - RA3: @Transactional maneja todo automáticamente
        //
        // NOTA PEDAGÓGICA:
        // Esto demuestra la potencia de @Transactional de Spring:
        // - No necesitas setAutoCommit(false)
        // - No necesitas commit() manual
        // - No necesitas rollback() manual en catch
        // - Spring lo hace automáticamente según el resultado del método

        throw new UnsupportedOperationException("TODO CE3.g: Implementar transferData() - " +
                "Usar @Transactional con múltiples persist(), Spring maneja commit/rollback automáticamente");
    }

    @Override
    @Transactional
    public int batchInsertUsers(List<User> users) {
        // TODO CE3.g: Implementar batchInsertUsers()
        //
        // Guía de implementación:
        // 1. Configurar batch size (ya está en application.yml: hibernate.jdbc.batch_size=20)
        //
        // 2. Iterar con flush() periódico:
        //    int batchSize = 20;
        //    for (int i = 0; i < users.size(); i++) {
        //        entityManager.persist(users.get(i));
        //
        //        if (i % batchSize == 0 && i > 0) {
        //            entityManager.flush();   // Ejecuta INSERTs en batch
        //            entityManager.clear();   // Limpia contexto de persistencia
        //        }
        //    }
        //    entityManager.flush();  // Flush final
        //
        // 3. Retornar users.size()
        //
        // ¿POR QUÉ flush() y clear()?
        // - flush(): Ejecuta las operaciones pendientes en BD (envía batch)
        // - clear(): Limpia el contexto de persistencia para liberar memoria
        //
        // DIFERENCIA vs RA2:
        // - RA2: PreparedStatement.addBatch(), executeBatch()
        // - RA3: persist() + flush() periódico con batch_size configurado

        throw new UnsupportedOperationException("TODO CE3.g: Implementar batchInsertUsers() - " +
                "Usar persist() en bucle con flush() y clear() cada 20 registros");
    }

    // ========== CE3.b: Metadatos ==========

    @Override
    public String getHibernateInfo() {
        // TODO CE3.b: Implementar getHibernateInfo()
        //
        // Guía de implementación:
        // 1. Unwrap EntityManager a Session de Hibernate:
        //    Session session = entityManager.unwrap(org.hibernate.Session.class);
        //
        // 2. Obtener SessionFactory:
        //    SessionFactory sessionFactory = session.getSessionFactory();
        //
        // 3. Obtener Statistics (si está habilitado):
        //    Statistics stats = sessionFactory.getStatistics();
        //
        // 4. Construir String con información:
        //    - stats.getEntityCount() - Número de entidades
        //    - stats.getQueryExecutionCount() - Queries ejecutadas
        //    - stats.getConnectCount() - Conexiones abiertas
        //    - etc.
        //
        // NOTA: Necesitas habilitar statistics en application.yml:
        // hibernate.generate_statistics: true (ya está configurado)
        //
        // Ejemplo:
        // return "Entidades: " + stats.getEntityCount() +
        //        ", Queries: " + stats.getQueryExecutionCount();

        throw new UnsupportedOperationException("TODO CE3.b: Implementar getHibernateInfo() - " +
                "Usar Session.getSessionFactory().getStatistics() para obtener información de Hibernate");
    }

    @Override
    public Map<String, Object> getEntityMetadata() {
        // TODO CE3.b: Implementar getEntityMetadata()
        //
        // Guía de implementación:
        // 1. Obtener Metamodel:
        //    Metamodel metamodel = entityManager.getMetamodel();
        //
        // 2. Obtener EntityType de User:
        //    EntityType<User> entityType = metamodel.entity(User.class);
        //
        // 3. Obtener atributos:
        //    Set<Attribute<? super User, ?>> attributes = entityType.getAttributes();
        //
        // 4. Construir mapa con información de cada atributo:
        //    Map<String, Object> metadata = new HashMap<>();
        //    for (Attribute<? super User, ?> attr : attributes) {
        //        Map<String, Object> attrInfo = new HashMap<>();
        //        attrInfo.put("name", attr.getName());
        //        attrInfo.put("javaType", attr.getJavaType().getSimpleName());
        //        attrInfo.put("persistentAttributeType", attr.getPersistentAttributeType().toString());
        //        metadata.put(attr.getName(), attrInfo);
        //    }
        //
        // 5. Retornar metadata
        //
        // DIFERENCIA vs RA2:
        // - RA2: DatabaseMetaData.getColumns() - info de BD
        // - RA3: Metamodel - info de entidades JPA

        throw new UnsupportedOperationException("TODO CE3.b: Implementar getEntityMetadata() - " +
                "Usar Metamodel.entity(User.class) para obtener metadatos de la entidad");
    }

    @Override
    public long executeCountByDepartment(String department) {
        // TODO CE3.f: Implementar executeCountByDepartment()
        //
        // Guía de implementación:
        // 1. Crear JPQL COUNT query:
        //    String jpql = "SELECT COUNT(u) FROM User u WHERE u.department = :dept AND u.active = true";
        //
        // 2. Crear TypedQuery<Long>:
        //    TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        //
        // 3. Setear parámetro:
        //    query.setParameter("dept", department);
        //
        // 4. Ejecutar y retornar:
        //    return query.getSingleResult();
        //
        // DIFERENCIA vs RA2:
        // - RA2: CallableStatement para stored procedure
        // - RA3: JPQL COUNT query directo (más simple)

        throw new UnsupportedOperationException("TODO CE3.f: Implementar executeCountByDepartment() - " +
                "Usar JPQL 'SELECT COUNT(u) FROM User u WHERE u.department = :dept'");
    }
}
