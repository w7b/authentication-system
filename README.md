# Authentication System
### JWT, OAuth2

## .env ( _Dotenv_ )
```dotenv
DB_CONNECTION=
DB_USER=
DB_PASSWORD=

SERVER_PORT=8080

GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=

GITHUB_CLIENT_ID=
GITHUB_CLIENT_SECRET=
```

***

## Bibliotecas usadas ( _Libs used_ )
- [x] Spring Starter Web
- [x] Auth0 JWT
- [x] Spring Security
- [x] Spring Data JPA
- [x] Postgres SQL
- [x] Spring OAuth2 Client
- [x] Spring OAuth2 Resource Server
- [x] Mapstruct
- [x] Lombok
- [x] Dotenv
- [x] Hibernate Validator

***

## Algoritmo RSA256 ( _RSA256 Algorithm_ )
Generate private and public keys:
```bash
openssl genpkey -algorithm RSA -out private_key.pem -pkeyopt rsa_keygen_bits:4096
```

```bash
openssl rsa -in private_key.pem -pubout -out public_key.pem
```
