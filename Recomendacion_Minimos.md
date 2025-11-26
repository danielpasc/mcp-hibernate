# Propuesta de Ejercicio de Mínimos - RA3 Acceso a Datos

Para adaptar el ejercicio actual a una versión de "mínimos" que cubra estrictamente los criterios de evaluación del currículo oficial sin añadir complejidad avanzada, se recomienda realizar los siguientes cambios en `HibernateUserServiceImpl.java`.

## 1. Elementos a Eliminar (Nivel Avanzado/Profesional)
Estos conceptos son valiosos pero no estrictamente necesarios para aprobar el RA3.

*   ❌ **Introspección y Metadatos (Criterios a, b)**
    *   Eliminar `getEntityManagerInfo()`
    *   Eliminar `getHibernateInfo()`
    *   Eliminar `getEntityMetadata()`
    *   *Motivo:* El alumno debe saber configurar el ORM, pero no necesariamente inspeccionar esa configuración o el metamodelo programáticamente en tiempo de ejecución.

*   ❌ **Optimización de Rendimiento (Criterio g)**
    *   Eliminar `batchInsertUsers()`
    *   *Motivo:* El manejo de `flush()` y `clear()` para el caché de primer nivel es una optimización avanzada. Para entender transacciones, basta con el método `transferData`.

## 2. Elementos a Simplificar

*   ⚠️ **Consultas Dinámicas (Criterio f)**
    *   Simplificar `searchUsers(UserQueryDto)`
    *   *Cambio:* Eliminar el requisito de usar **Criteria API**.
    *   *Propuesta:* Implementar búsquedas sencillas usando **JPQL** estándar con parámetros (ej. `SELECT u FROM User u WHERE u.department = :dept`).

*   ⚠️ **Paginación (Opcional)**
    *   `findUsersWithPagination()` es muy recomendable, pero si se busca el mínimo absoluto, se puede eliminar, ya que `findAll()` cubre la recuperación de objetos.

## 3. Resultado: El Plan de Mínimos
El ejercicio resultante constaría de solo **4 métodos esenciales** para el alumno:

1.  **`deleteUser(Long id)`**
    *   **Objetivo:** Cargar y borrar una entidad.
    *   **Cubre:** Modificación y recuperación de objetos (Criterio e).
    *   **Lógica:** `find()` + `remove()`.

2.  **`executeCountByDepartment(String dept)`**
    *   **Objetivo:** Ejecutar una consulta de agregación.
    *   **Cubre:** Consultas JPQL (Criterio f).
    *   **Lógica:** JPQL simple `SELECT COUNT(u)...`.

3.  **`transferData(List<User> users)`**
    *   **Objetivo:** Guardar varios usuarios en una sola operación atómica.
    *   **Cubre:** Gestión de transacciones (Criterio g).
    *   **Lógica:** Bucle simple con `persist()`. Si falla uno, Spring hace rollback de todos.

4.  **`searchUsers` (Versión JPQL)**
    *   **Objetivo:** Buscar con filtros simples.
    *   **Cubre:** Consultas con parámetros (Criterio f).
    *   **Lógica:** `createQuery` con JPQL.

---
Esta reducción mantiene el cumplimiento del 100% de los criterios de evaluación eliminando la carga cognitiva asociada a APIs complejas (Criteria, Metamodel) y optimizaciones de memoria.
