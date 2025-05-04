# ECI-Bienestar - Extracurricular Class Attendance Service

This microservice is part of the ECIBienestar platform at Escuela Colombiana de Ingeniería Julio Garavito. It manages user attendance for extracurricular classes, allowing registration, consultation, reporting, and notification features for all community members.

## 👥 Authors

- **Emily Noreña Cardozo**  
  GitHub: [EmilyNorena](https://github.com/EmilyNorena)

- **David Santiago Espinoza Rojas**  
  GitHub: [daviespr1406](https://github.com/daviespr1406)

- **Mayerlly Suárez Correa**  
  GitHub: [mayerllyyo](https://github.com/mayerllyyo)

## 📌 Project Overview

The Extracurricular Class Attendance Service is responsible for tracking and managing attendance records of users participating in extracurricular classes. This includes both manual and bulk registration, detailed attendance reports, automated notifications, and integration with other services within the ECIBienestar ecosystem.

## 🛠️ Technologies Used

- **Java 17**
- **Spring Boot 3.x** (Spring Security, Spring Web)
- **MongoDB** (NoSQL Database)
- **Maven**
- **Lombok**
- **JUnit 5 & Mockito** (for testing)
- **JaCoCo** (for code coverage)
- **SonarCloud** (for code quality)
- **JWT** (for secure authentication)

## 📂 Project Structure

```
bismuto-statistics-service/
├── pom.xml
├── .gitignore
├── README.md
├── assets/
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/example/edu/eci/
    │   │       ├── Application.java
    │   │       ├── controller/
    │   │       ├── exception/
    │   │       ├── model/
    │   │       ├── service/
    │   │       └── repository/
    │   └── resources/
    └── test/
        └── java/
            └── com/example/edu/eci/
                ├── Application.java
                ├── controller/       # Controller Tests
                ├── exception/        # Exception Tests
                ├── model/            # Model Tests
                ├── service/          # Service Tests
                └── repository/       # Utility Tests
```

## 🚀 How to Run the Project

### Prerequisites
- Install **Java 17**
- Install **Maven**
- Set up a **MongoDB** database

### Steps to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/ECIBienestar/opalo-extraclasses
   cd opalo-extraclasses
   ```

2. Configure database connection in `application.properties`:
   ```properties
   spring.data.mongodb.uri=
   spring.data.mongodb.database=
   ```

3. Build and run the application:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
---
## 🧩 Modelo de Datos (MongoDB)

```plaintext
┌────────────────────┐        ┌────────────────────────┐
│     usuarios       │        │  clasesExtracurriculares│
├────────────────────┤        ├────────────────────────┤
│ _id                │        │ _id                    │
│ nombreCompleto     │        │ nombre                 │
│ tipoUsuario        │        │ tipoActividad          │
│ identificacion     │        │ capacidadMaxima        │
│ email              │        │ fechaInicio/fechaFin   │
└────────────────────┘        │ recursos[]             │
         ▲                    └────────┬───────────────┘
         │                             │
         │                    ┌────────▼───────────────┐
         │                    │       asistencias      │
         │                    ├────────────────────────┤
         └────────────────────┤ usuarioId              │
                              │ claseId                │
                              │ fechaHora              │
                              │ confirmada             │
                              └────────────────────────┘
```

---

## 📄 Ejemplos de Documentos en MongoDB


---

### `clasesExtracurriculares`

```json
{
  "_id": ObjectId("644a8e64e77b5f001e5d15df"),
  "nombre": "Yoga al aire libre",
  "tipoActividad": "RELACION",
  "capacidadMaxima": 20,
  "fechaInicio": ISODate("2025-04-25T18:00:00Z"),
  "fechaFin": ISODate("2025-06-25T19:00:00Z"),
  "recursos": [
    {
      "nombre": "Colchonetas",
      "tipo": "implemento",
      "disponibilidad": {
        "2025-04-25": true,
        "2025-04-27": false
      }
    },
    {
      "nombre": "Salón 2",
      "tipo": "salon",
      "disponibilidad": {
        "2025-04-25": true
      }
    }
  ]
}
```

---

### `asistencias`

```json
{
  "_id": ObjectId("644a8ec8e77b5f001e5d15e0"),
  "usuarioId": "644a8e20e77b5f001e5d15de",
  "claseId": "644a8e64e77b5f001e5d15df",
  "fechaHora": ISODate("2025-04-25T18:00:00Z"),
  "confirmada": true
}
```

---

### `notificaciones`

```json
{
  "_id": ObjectId("644a8efee77b5f001e5d15e1"),
  "usuarioId": "644a8e20e77b5f001e5d15de",
  "tipo": "RECORDATORIO",
  "mensaje": "No olvides tu clase de Yoga a las 18:00",
  "fechaEnvio": ISODate("2025-04-25T12:00:00Z"),
  "enviado": true
}
```

---

### `reportes`

```json
{
  "_id": ObjectId("644a8f3ae77b5f001e5d15e2"),
  "tipo": "POR_USUARIO",
  "parametros": {
    "usuarioId": "644a8e20e77b5f001e5d15de",
    "rangoFechas": {
      "inicio": ISODate("2025-04-01T00:00:00Z"),
      "fin": ISODate("2025-04-30T23:59:59Z")
    }
  },
  "contenido": "https://api.universidad.edu/reportes/usuario/644a8e20e77b5f001e5d15de/abril2025.pdf",
  "fechaGeneracion": ISODate("2025-04-25T20:00:00Z")
}
```

---

## ⚙️ Componentes del Microservicio

```plaintext
┌────────────────────────────┐
│       API REST             │
├────────────────────────────┤
│ POST   /asistencias        │
│ GET    /asistencias/user   │
│ GET    /reportes           │
│ POST   /clases             │
│ POST   /notificaciones     │
└────────────────────────────┘

       ▼

┌────────────────────────────┐
│       Servicios            │
├────────────────────────────┤
│ AsistenciaService          │
│ ClaseService               │
│ NotificacionService        │
│ ReporteService             │
│ RecursoService             │
└────────────────────────────┘

       ▼

┌────────────────────────────┐
│     Repositorios (Mongo)   │
├────────────────────────────┤
│ UsuarioRepository          │
│ ClaseRepository            │
│ AsistenciaRepository       │
│ NotificacionRepository     │
│ ReporteRepository          │
└────────────────────────────┘
```

---
![Architecture](assets/1.png)

