


# Laboratorio 1

Este repositorio contiene el código fuente de una aplicación bancaria completa, desarrollada como parte del Laboratorio Nro. 1 de la materia Arquitectura de Software. La aplicación permite gestionar clientes, realizar transferencias de dinero entre cuentas y consultar historiales de transacciones.

## Características Implementadas

La aplicación cuenta con un ciclo completo de funcionalidades CRUD (Crear, Leer, Actualizar, Borrar) para la gestión de clientes, además de las operaciones de transacciones.

* **Gestión de Clientes:**
    * Visualización de todos los clientes registrados.
    * Creación de nuevos clientes con un saldo inicial.
    * **Actualización** de la información de los clientes (nombre y apellido).
    * **Eliminación** de clientes con diálogo de confirmación.
* **Transacciones:**
    * Realización de transferencias de dinero entre dos cuentas existentes.
    * Validación de saldo suficiente en la cuenta de origen.
* **Historial:**
    * Consulta del historial completo de transacciones (enviadas y recibidas) para un número de cuenta específico.
* **Frontend Moderno:**
    * Interfaz de usuario limpia y profesional construida con React.
    * Navegación fluida entre vistas sin recargar la página (Single Page Application).
    * Diseño responsivo con menú desplegable para una correcta visualización en dispositivos móviles.

## Estructura del Proyecto

Este es un monorepo que contiene tanto el backend como el frontend en un solo lugar.

/
├── banco-backend/      <-- Servidor Backend (Spring Boot)
├── banco-frontend/     <-- Aplicación Cliente (React)
└── .gitignore          <-- Reglas para ignorar archivos (node_modules, target, etc.)
└── README.md           <-- Esta documentación

## Tecnologías Utilizadas

### Backend (`banco-backend`)
* **Lenguaje:** Java 21
* **Framework:** Spring Boot
* **Base de Datos:** MySQL
* **Gestión de Dependencias:** Maven
* **Librerías Clave:** Spring Web, Spring Data JPA, Lombok.

### Frontend (`banco-frontend`)
* **Librería:** React JS
* **Gestor de Paquetes:** npm
* **Librerías Clave:** `axios` (peticiones HTTP), `react-router-dom` (navegación), `react-icons` (iconografía).

## Guía de Instalación y Ejecución

Para ejecutar este proyecto en tu máquina local, sigue los siguientes pasos.

### Prerrequisitos
* Java JDK 17 o superior.
* Maven 3.5 o superior.
* Node.js (que incluye npm).
* Un servidor de base de datos MySQL en funcionamiento.

### 1. Clonar el Repositorio
```bash
git clone https://github.com/MiltonCuervo/BancoFullStackFinal.git
cd BancoFullStackFinal


### 2. Configurar el Backend
1.  **Crear la Base de Datos:** Abre tu cliente de MySQL y ejecuta el siguiente comando:
    ```sql
    CREATE DATABASE banco2025;
    ```
2.  **Configurar la Conexión:** Navega a la carpeta `banco-backend/src/main/resources/` y abre el archivo `application.properties`. Asegúrate de que las credenciales (`spring.datasource.username` y `spring.datasource.password`) coincidan con tu configuración local de MySQL.

3.  **Ejecutar el Servidor Backend:** Abre una **primera terminal** en la carpeta raíz del proyecto y ejecuta:
    ```bash
    # Navega a la carpeta del backend
    cd lab12025p

    # Ejecuta el servidor (usa mvnw.cmd en Windows cmd)
    ./mvnw spring-boot:run
    ```
    El servidor backend se iniciará en `http://localhost:8080`.

### 3. Configurar el Frontend
1.  **Instalar Dependencias:** Abre una **segunda terminal** en la carpeta raíz del proyecto y ejecuta:
    ```bash
    # Navega a la carpeta del frontend
    cd banco-frontend

    # Instala todas las librerías necesarias
    npm install
    ```
2.  **Ejecutar la Aplicación Frontend:** En la misma terminal del frontend, ejecuta:
    ```bash
    npm start
    ```
    La aplicación se abrirá automáticamente en tu navegador en `http://localhost:3000`.
