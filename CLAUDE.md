# CLAUDE.md — Contexto del Proyecto BancoFullStackFinal

Documento de contexto para continuar el desarrollo sin releer el historial completo.
El enfoque actual es **análisis de código estático** con SonarCloud.

---

## 1. Estructura del Repositorio

```
BancoFullStackFinal/
├── lab12025p/          # Backend: Spring Boot (Java 17, Maven)
├── banco-frontend/     # Frontend: React (Create React App)
└── .github/workflows/
    └── sonar.yml       # CI/CD: GitHub Actions → SonarCloud
```

---

## 2. Backend — `lab12025p/`

### Stack
- **Spring Boot 3.4.9**, Java 17
- **Base de datos**: MySQL (producción) / H2 (tests)
- **ORM**: Spring Data JPA + Hibernate
- **Mapeo DTO↔Entidad**: MapStruct 1.5.5
- **Boilerplate**: Lombok 1.18.30
- **Build**: Maven Wrapper (`./mvnw`)

### Paquete raíz: `com.udea.lab12025p`

| Capa | Clases |
|---|---|
| `entity/` | `Customer.java`, `Transaction.java` |
| `DTO/` | `CustomerDTO.java`, `TransactionDTO.java` |
| `repository/` | `CustomerRepository.java`, `TransactionRepository.java` |
| `service/` | `CustomerService.java`, `TransactionService.java` |
| `controller/` | `CustomerController.java`, `TransactionController.java` |
| `mapper/` | `CustomerMapper.java` (MapStruct), `TransactionMapper.java` |

### Endpoints REST

**Customers** (`/api/customers`):
- `GET /` — lista todos
- `GET /{id}` — uno por ID
- `POST /` — crear (valida que `balance` no sea null)
- `PUT /{id}` — actualizar (retorna 404 si no existe)
- `DELETE /{id}` — borrar (retorna 404 si no existe)

**Transactions** (`/api/transactions`):
- `POST /` — transferir dinero (retorna 400 con mensaje de error si falla)
- `GET /{accountNumber}` — historial de una cuenta

### Lógica de negocio clave (`TransactionService`)
Validaciones en orden:
1. Cuentas no nulas → `"Los numeros de cuenta del remitente y receptor son obligatorios"`
2. Monto > 0 → `"El monto a transferir debe ser mayor a cero"`
3. No autotransferencia → `"No puede transferir dinero a su propia cuenta"`
4. Remitente existe → `"La cuenta del remitente no existe"`
5. Receptor existe → `"La cuenta del receptor no existe"`
6. Saldo suficiente → `"El remitente no tiene saldo suficiente"`

### Configuración de aplicación
- **Producción**: `src/main/resources/application.properties` (MySQL, puerto 8080)
- **Tests**: perfil `test` con H2 en memoria; activo con `@ActiveProfiles("test")`

---

## 3. Testing del Backend

### Estructura de tests
```
src/test/java/com/udea/lab12025p/
├── Lab12025pApplicationTests.java   (contexto de Spring)
├── service/
│   ├── TransactionServiceTest.java  (9 tests Mockito)
│   └── CustomerServiceTest.java     (8 tests Mockito)
├── controller/
│   ├── CustomerControllerTest.java  (8 tests, MockMvc standalone)
│   └── TransactionControllerTest.java (4 tests, MockMvc standalone)
├── cucumber/
│   ├── CucumberTest.java            (Runner JUnit 5 Suite)
│   ├── CucumberSpringConfiguration.java
│   └── TransferenciaSteps.java
└── src/test/resources/
    ├── features/Transferencia.feature
    └── junit-platform.properties    (config global de Cucumber)
```

### Comandos clave

```bash
# Tests unitarios y de controller (excluye Cucumber)
./mvnw test

# Solo un test específico
./mvnw test -Dtest=TransactionServiceTest

# Solo Cucumber (BDD)
./mvnw test -Dtest=CucumberTest

# Todo (unitarios + Cucumber + JaCoCo report)
./mvnw verify

# Con análisis SonarCloud local
./mvnw verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=MiltonCuervo_BancoFullStackFinal
```

### Separación de engines (pom.xml — Surefire)
- **`mvn test`** → excluye `CucumberTest.java` → solo tests unitarios
- **`mvn verify`** → fase `integration-test` incluye `CucumberTest.java`
- Esta separación es intencional para evitar que el motor de Cucumber se active siempre de forma global

### Cobertura (JaCoCo)
- Plugin JaCoCo 0.8.10 configurado en `pom.xml`
- Reporte XML en: `target/site/jacoco/jacoco.xml`
- Reporte HTML en: `target/site/jacoco/index.html`
- Configuración en `pom.xml`: `<sonar.coverage.jacoco.xmlReportPaths>target/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>`
- Coverage actual: **~75-85%** (subido desde 50% tras añadir 29 nuevos tests)

---

## 4. Análisis de Código Estático — SonarCloud

### Configuración
- **Plataforma**: SonarCloud (https://sonarcloud.io)
- **Organización**: `miltoncuervo`
- **Project Key**: `MiltonCuervo_BancoFullStackFinal`
- **Token**: Secreto de GitHub Actions → `SONAR_TOKEN`
- **Modo**: CI Analysis (NO Automatic Analysis — deben ser mutuamente excluyentes)

### CI/CD (`.github/workflows/sonar.yml`)
- **Trigger**: push a `main` o pull_request
- **JDK**: 17 (Zulu)
- **Comando**:
  ```bash
  cd lab12025p
  mvn -B verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
    -Dsonar.projectKey=MiltonCuervo_BancoFullStackFinal
  ```

### Configuración en `pom.xml` (properties)
```xml
<sonar.projectKey>MiltonCuervo_BancoFullStackFinal</sonar.projectKey>
<sonar.organization>miltoncuervo</sonar.organization>
<sonar.host.url>https://sonarcloud.io</sonar.host.url>
<sonar.java.binaries>target/classes</sonar.java.binaries>
<sonar.coverage.jacoco.xmlReportPaths>target/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
```

### Error conocido de SonarCloud
Si el job falla con `"You are running CI analysis while Automatic Analysis is enabled"`:
→ Ir a SonarCloud → Administration → Analysis Method → **deshabilitar Automatic Analysis**

---

## 5. Frontend — `banco-frontend/`

### Stack
- React (Create React App)
- Axios para llamadas HTTP al backend (puerto 8080)
- Cypress para tests E2E

### Componentes React
| Archivo | Responsabilidad |
|---|---|
| `CustomerList.js` | Lista de clientes con acciones edit/delete |
| `CustomerEditForm.js` | Formulario de edición de cliente |
| `TransactionForm.js` | Formulario de transferencia |
| `TransactionHistory.js` | Historial de transacciones por cuenta |
| `HomePage.js` | Página de inicio |

### Tests E2E (Cypress)
Archivo: `cypress/e2e/bank_transactions.cy.js`
- **Caso 1**: Transferencia exitosa (flujo completo Frontend → Backend → DB)
- **Caso 2**: Validación HTML5 de campos requeridos vacíos
- **Caso 3**: Error visual cuando el backend rechaza la operación

```bash
# Ejecutar Cypress (requiere backend corriendo en :8080 y frontend en :3000)
npx cypress open
npx cypress run  # headless
```

---

## 6. Ramas Git

| Rama | Estado | Descripción |
|---|---|---|
| `main` | ✅ Activa | Código de la compañera + fixes de Milton |
| `MiltonFix` | 🔧 En desarrollo | Rama personal para nuevas mejoras |
| `fixing_cucumber` | Mergeada | Rama original de la compañera (fue base del main actual) |

---

## 7. Próximos Pasos (Análisis Estático)

El foco es mejorar la calidad del código según los reportes de SonarCloud:
- Revisar **Code Smells** reportados por Sonar
- Revisar **Security Hotspots** (ej. CORS abierto, credenciales hardcoded)
- Revisar **Duplications**
- Aplicar correcciones y observar cómo cambia la **Quality Gate**

### Issues conocidos / candidatos a mejora
1. `@CrossOrigin(origins = "http://localhost:3000")` en controllers — hardcoded, debería externalizarse
2. `CommandLineRunner` en `Lab12025pApplication.java` inicializa la BD con datos de prueba en producción
3. Las entidades no tienen validaciones `@NotNull` / `@Size` de Bean Validation aprovechadas
4. `TransactionService` no usa un mapper (convierte manualmente), mientras `CustomerService` sí usa MapStruct

---

## 8. Notas Técnicas Importantes

- **MockMvc en tests de controller**: Se usa `MockMvcBuilders.standaloneSetup()` (NO `@WebMvcTest`) para evitar que Spring intente levantar el ApplicationContext completo con beans de repositorio.
- **Cucumber vs motor JUnit**: El motor de Cucumber se activa automáticamente si detecta `junit-platform.properties`. Por eso se separó en fase `integration-test` del pom.
- **H2 en tests**: Se activa con el perfil `test`. Los tests de Cucumber usan `@ActiveProfiles("test")` en `CucumberSpringConfiguration`.
- **MapStruct + Lombok**: El orden de `annotationProcessorPaths` importa: Lombok debe ir **antes** que MapStruct.
