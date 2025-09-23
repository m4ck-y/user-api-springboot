# User API - Spring Boot

**User API** es una aplicación backend desarrollada con Java y Spring Boot que permite la gestión de usuarios, incluyendo autenticación segura, validaciones personalizadas y cifrado de contraseñas.

El proyecto sigue buenas prácticas en el diseño de APIs REST, utiliza herramientas modernas y gestiona los datos de manera eficiente en memoria.

## Ejecutar el proyecto

leer variables de entorno en .env
export $(cat .env | xargs)

```bash
./mvnw spring-boot:run
```

## Endpoint
http://localhost:8080/hello


se creo un .env para variables de entorno
pero como buena practica este no se suba al repo, se agrega al gitignore y se pone un .env.


test
 ./mvnw test