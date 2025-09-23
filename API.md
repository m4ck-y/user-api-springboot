## Endpoints

### GET /users
Lista usuarios. Parámetros opcionales:
- `sortedBy`: Ordenar por `id`, `email`, `name`, `phone`, `tax_id`, `created_at`
- `filter`: Filtrar con formato `atributo operador valor` (ej. `name co user`)

Ejemplos:
```bash
# Todos los usuarios
curl -X GET "http://localhost:8080/users"

# Ordenados por email
curl -X GET "http://localhost:8080/users?sortedBy=email"

# Filtrar nombres que contienen "user"
curl -X GET "http://localhost:8080/users?filter=name+co+user"

# Filtrar emails que terminan en "mail.com"
curl -X GET "http://localhost:8080/users?filter=email+ew+mail.com"

# Filtrar teléfonos que empiezan con "555"
curl -X GET "http://localhost:8080/users?filter=phone+sw+555"

# Filtrar tax_id exacto
curl -X GET "http://localhost:8080/users?filter=tax_id+eq+AARR990101XXX"
```

### GET /users/{id}
Obtiene un usuario por ID.

```bash
curl -X GET "http://localhost:8080/users/123e4567-e89b-12d3-a456-426614174000"
```

### POST /users
Crea un usuario. Valida tax_id (RFC único), teléfono (10 dígitos).

```bash
curl -X POST "http://localhost:8080/users" \
-H "Content-Type: application/json" \
-d '{
  "email": "user@example.com",
  "name": "User Name",
  "phone": "+1234567890",
  "password": "pass123",
  "taxId": "XAXX010101000",
  "addresses": [{"name": "home", "street": "123 St", "countryCode": "US"}]
}'
```

### PATCH /users/{id}
Actualiza usuario parcialmente.

```bash
curl -X PATCH "http://localhost:8080/users/123e4567-e89b-12d3-a456-426614174000" \
-H "Content-Type: application/json" \
-d '{"name": "New Name"}'
```

### DELETE /users/{id}
Elimina usuario.

```bash
curl -X DELETE "http://localhost:8080/users/123e4567-e89b-12d3-a456-426614174000"
```

### POST /users/login
Autentica con tax_id y password.

```bash
curl -X POST "http://localhost:8080/users/login" \
-H "Content-Type: application/json" \
-d '{"taxId": "XAXX010101000", "password": "pass123"}'
```

## Modelos

### UserDTO
```json
{
  "id": "uuid",
  "email": "string",
  "name": "string",
  "phone": "string",
  "taxId": "string",
  "createdAt": "dd-MM-yyyy HH:mm",
  "addresses": [{"id": "uuid", "name": "string", "street": "string", "countryCode": "string"}]
}
```

### Error
```json
{"error": "Mensaje"}
```

### Login Success
```json
{"message": "Login successful"}
```

## Notas
- Contraseñas encriptadas AES-256.
- Timestamps en Madagascar timezone.
- Tax_id RFC único, teléfono 10 dígitos.
- 3 usuarios precargados.

Ejecutar: `./mvnw spring-boot:run`