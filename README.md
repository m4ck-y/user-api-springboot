# User API - Spring Boot

Esta es una aplicacion para manejar usuarios. Tiene funciones para crear, leer, actualizar y borrar usuarios, mas un login para autenticar. Usa validaciones especiales para el tax_id (formato RFC) y telefono (10 digitos). Las contrasenas se guardan encriptadas con AES. Los datos se guardan en memoria por ahora.

El codigo esta organizado en capas: modelos para los datos, servicios para la logica, y controladores para las APIs. Usa UUID para IDs unicos, y timestamps en la zona horaria de Madagascar.

## Endpoints principales:
- GET /users - lista usuarios, con opciones para ordenar y filtrar
- GET /users/{id} - obtiene un usuario
- POST /users - crea usuario
- PATCH /users/{id} - actualiza usuario
- DELETE /users/{id} - borra usuario
- POST /users/login - login con tax_id y password

## Ejecucion
(el desarrollo fue en linux):
1. Copiar .env.example a .env y poner la clave AES_KEY
2. export $(cat .env | xargs)
3. ./mvnw spring-boot:run
4. Ve a http://localhost:8080/swagger-ui/index.html

Con Docker:
1. docker build -t user-api .
2. docker run -p 8080:8080 --env-file .env user-api

Tests: ./mvnw test