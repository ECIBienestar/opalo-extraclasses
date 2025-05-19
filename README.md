# 🎓 Microservice: Extracurricular Class Attendance

This microservice manages the registration, consultation, and monitoring of attendance to extracurricular classes at a university, as part of the University Wellness module.

---

## 👥 Authors

- *Emily Noreña Cardozo*  
  GitHub: [EmilyNorena](https://github.com/EmilyNorena)

- *David Santiago Espinosa Rojas*  
  GitHub: [daviespr1406](https://github.com/daviespr1406)

- *Mayerlly Suárez Correa*  
  GitHub: [mayerllyyo](https://github.com/mayerllyyo)

---

## 🧩 Data Model (MongoDB)

```plaintext
┌────────────────────┐        ┌────────────────────────┐
│     usuarios       │        │ extracurricularClasses │
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

## ▶️ Steps to Run

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

## 🛠️ Technologies Used

- *Java 17*
- *Spring Boot 3.x* (Spring Security, Spring Web)
- *MongoDB* (NoSQL Database)
- *Maven*
- *Lombok*
- *JUnit 5 & Mockito* (for testing)
- *JaCoCo* (for code coverage)
- *SonarCloud* (for code quality)
- *JWT* (for secure authentication)

---

## 📄 MongoDB Document Examples

### `clasesExtracurriculares`

```json
{
  {
    "id": "course-001",
    "name": "Painting for Beginners",
    "maxStudents": 20,
    "type": "Art",
    "startDate": "2025-06-01",
    "endDate": "2025-07-15",
    "sessions": [
      {
        "id": "session-001",
        "day": "Monday",
        "startTime": "10:00",
        "endTime": "12:00"
      },
      {
        "id": "session-002",
        "day": "Wednesday",
        "startTime": "10:00",
        "endTime": "12:00"
      }
    ],
    "resources": [
      {
        "name": "Canvas",
        "quantity": 20
      },
      {
        "name": "Paint Brushes",
        "quantity": 20
      },
      {
        "name": "Acrylic Paint Set",
        "quantity": 20
      }
    ],
    "instructorId": "instructor-123",
    "repetition": "WEEKLY",
    "endTimeRepetition": "2025-07-15"
  }
}
```
```

### `asistencias`

```json
[
  {
    "id": "attendance-001",
    "startTime": "2025-06-03T10:00:00.000Z",
    "userId": "user-456",
    "instructorId": "instructor-123",
    "classId": "course-001",
    "confirm": true,
    "sessionId": "session-001"
  }
]

---

## ⚙️ Microservice Components

```plaintext
┌────────────────────────────┐
│       REST API             │
├────────────────────────────┤
│ POST   /asistencias        │
│ GET    /asistencias/user   │
│ GET    /reportes           │
│ POST   /clases             │
│ POST   /notificaciones     │
└────────────────────────────┘

       ▼

┌────────────────────────────┐
│         Services           │
├────────────────────────────┤
│ AsistenciaService          │
│ ClaseService               │
│ NotificacionService        │
│ ReporteService             │
│ RecursoService             │
└────────────────────────────┘

       ▼

┌────────────────────────────┐
│      Mongo Repositories    │
├────────────────────────────┤
│ UsuarioRepository          │
│ ClaseRepository            │
│ AsistenciaRepository       │
│ NotificacionRepository     │
│ ReporteRepository          │
└────────────────────────────┘

       ▼

┌────────────────────────────┐
│    Scheduled Tasks         │
├────────────────────────────┤
│ Automatic reminders        │
│ Post-session confirmations │
│ Report generation          │
└────────────────────────────┘
```

![Architecture](assets/1.png)

---
### 📦 Integration with Other Modules

- Connect with central user service for authentication and user info
- Consume scheduling microservice for conflict validation
- Generate events for notification microservice

### 🏗️ Architecture Document

- [x] Explain layered structure
- [x] Show flow from controller to repository
- [x] Indicate external service interactions

### 📝 Requirements Analysis

- [ ] Functional requirements
- [ ] Non-functional requirements (performance, scalability, security)

### 📚 Class Diagram

![Class Diagram](assets/2.png)
This class diagram represents an extracurricular class attendance management module, designed to record, view, and manage user (student, teacher, etc.) participation in activities such as sports, cultural workshops, and so on.


Brief description of the classes:
- Class: Models an extracurricular class with: id (unique identifier), name (activity name), MaxStudents (maximum student capacity), StartTime/EndTime (start and end dates and times), and resources: List of required resources (supplies, equipment, etc.).
- Attendance: Records a user's attendance in a class with: userId (ID of the user who attended), classId (ID of the class attended), and confirm (attendance confirmation status).
- User: Represents a system user with: id (unique identifier), name (full name), type (user role: Student, Teacher, Administrative), identification (identification document), and email (email address).

### 🔁 Sequence Diagram

![Sequence Diagram](assets/6.png) 
- Process Start (User): The user initiates the action by requesting attendance confirmation using the confirmAssistance(userId, classId) operation from the web client interface.
- Web Client → Controller: The web client sends the request to the Assistance Controller, passing the userId and classId as parameters.
- Controller → Service: The Assistance Controller forwards the request to the Assistance Service, which handles the business logic related to the confirmation.
- Service → Repository: The Assistance Service queries the Assistance Repository using the findByUserIdAndClassId(userId, classId) method to verify if an attendance record exists for the user in the corresponding class.
- Repository → Service: The repository returns the found information to the Assistance Service.
- Service → Assistance Entity: If the record is found, the service marks the attendance as confirmed by calling the setConfirm(true) method on the Assistance entity.

Return result: If the confirmation is successful, the system returns the representative data to the web client. Otherwise, the controller returns an error message (ConfirmAssistanceFail) to the client.

### 📘 Swagger Documentation

- [Swagger on Azure](https://opalo-class-aae2hqc0fee9f7am.canadacentral-01.azurewebsites.net/swagger-ui/index.html)

### 😊 Happy Path

- [x] User enrolls in class
- [ ] Receives notification
- [x] Attends session
- [x] Attendance is confirmed

### ⚠️ Error Handling

- [x] Duplicate enrollment
- [x] Class full
- [x] Database errors
- [ ] Unauthorized access

### 🧪 Test Evidence

- [x] Unit test results
- [x] Integration test logs
- [x] JaCoCo coverage report
### JaCoCo Coverage Report
![CI/CD](assets/5.png)


### 🔄 CI/CD and Deployment Evidence

- [x] GitHub Actions workflows
- [ ] SonarCloud analysis link
- [x] Azure deployment logs/screenshots

### CI/CD Test Pipeline
![CI/CD](assets/4.png)
### CI/CD Production Pipeline
![CI/CD](assets/3.png)


### 📁 Project Structure

```plaintext
src/
├── config/
├── controllers/
├── dto/
├── exceptions/
├── models/
├── repositories/
├── security/
├── services/
└── utils/
```
