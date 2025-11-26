package com.dam.accesodatos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad JPA User para RA3 - Hibernate/JPA ORM
 *
 * RA3 - CRITERIO c) Definición de ficheros de mapeo:
 * Esta clase usa anotaciones JPA para mapear el objeto Java a la tabla 'users' de la BD.
 *
 * DIFERENCIAS vs RA2 (JDBC):
 * - RA2: POJO simple sin anotaciones, mapeo manual con ResultSet.getLong(), getString(), etc.
 * - RA3: Clase anotada con @Entity, @Table, @Column - Hibernate mapea automáticamente
 *
 * ANOTACIONES JPA UTILIZADAS:
 * - @Entity: Marca la clase como entidad JPA gestionada por Hibernate
 * - @Table: Mapea explícitamente a la tabla 'users' de la BD
 * - @Id: Marca el campo 'id' como clave primaria
 * - @GeneratedValue: El ID es autogenerado por la BD (IDENTITY strategy)
 * - @Column: Mapeo explícito de campos a columnas con restricciones
 * - @NotBlank, @Email: Validaciones de Bean Validation
 *
 * NOTA PEDAGÓGICA:
 * El constructor sin argumentos es OBLIGATORIO para JPA. Hibernate lo usa
 * para crear instancias mediante reflection al recuperar datos de la BD.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @Column(name = "department", nullable = false, length = 50)
    @NotBlank(message = "El departamento es obligatorio")
    private String department;

    @Column(name = "role", nullable = false, length = 50)
    @NotBlank(message = "El rol es obligatorio")
    private String role;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===== CONSTRUCTOR SIN ARGUMENTOS (OBLIGATORIO PARA JPA) =====

    /**
     * Constructor sin argumentos requerido por JPA.
     * Hibernate lo usa para instanciar objetos al recuperar datos de la BD.
     */
    public User() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ===== CONSTRUCTOR CON PARÁMETROS (OPCIONAL) =====

    public User(String name, String email, String department, String role) {
        this();  // Llama al constructor sin argumentos
        this.name = name;
        this.email = email;
        this.department = department;
        this.role = role;
    }

    // ===== GETTERS Y SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ===== EQUALS Y HASHCODE =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    // ===== TOSTRING =====

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", role='" + role + '\'' +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
