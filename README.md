# URL Shortener Application

A full-stack URL Shortener application built with **Spring Boot 3.x** (Java 21) and **React** (TypeScript). The application allows users to create shortened URLs with custom or auto-generated aliases, manage their links, and redirect to original URLs.

---

## Features

- Shorten long URLs with auto-generated aliases
- Custom alias support for personalized short URLs
- View all shortened URLs in a responsive table
- Delete shortened URLs
- Redirect to original URL via alias
- URL validation (supports http/https, with/without www)
- Persistent storage with PostgreSQL
- Database migrations with Liquibase
- RESTful API with Swagger/OpenAPI documentation
- Fully containerized with Docker

---

## Prerequisites

- **Docker** & **Docker Compose** (recommended for running the full stack)
- **Java 21** (for local backend development)
- **Maven 3.9+** (for local backend development)
- **Node.js 24+** (for local frontend development)
- **PostgreSQL 16** (if running without Docker)

---

## Running with Docker (Recommended)

The easiest way to run the entire application stack:

### 1. Clone the repository

```bash
git clone https://github.com/tpximpact/code-test-instructions.git
cd code-test-instructions
```

### 2. Build and start all services

```bash
docker-compose up --build
```

## Services

This will start:

| Service       | Port | Description                  |
|---------------|------|------------------------------|
| PostgreSQL    | 5432 | Database                     |
| Backend API   | 8080 | Spring Boot REST API         |
| Frontend      | 3000 | React UI served via Nginx    |


### 3. Access the application

| Resource | URL |
|----------|-----|
| Frontend UI | http://localhost:3000 |
| Swagger API Docs | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/api-docs |

### 4. Stop the application

```bash
docker-compose down
```
---

## 💻 Running Locally (Development)

### Backend

#### 1. Start PostgreSQL

Using Docker:

```bash
docker run -d \
  --name urlshortener-db \
  -e POSTGRES_DB=urlshortener \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:16-alpine
```

#### 2. Run the backend

```bash
cd url-shortener-backend

# Using Maven wrapper
./mvnw spring-boot:run

# Or using Maven directly
mvn spring-boot:run
```

Backend available at: **http://localhost:8080**

### Frontend

#### 1. Install dependencies

```bash
cd url-shortener-frontend
npm install
```

#### 2. Start development server

```bash
npm run dev
```

Frontend available at: **http://localhost:3000**

---

## 📘 Example Usage

### Create a shortened URL (auto-generated alias)

```bash
curl --request POST \
  --url http://localhost:8080/api/v1/shorten \
  --header 'content-type: application/json' \
  --data '{
  "originalUrl": "facebook.com"
}'
```

**Response (201 Created):**

```json
{
  "id": 1,
  "alias": "VGmjGM",
  "shortUrl": "http://localhost:8080/VGmjGM",
  "originalUrl": "https://facebook.com",
  "createdAt": "2026-01-18T21:40:26.335533"
}
```

### Create a shortened URL (custom alias)

```bash
curl --request POST \
  --url http://localhost:8080/api/v1/shorten \
  --header 'content-type: application/json' \
  --data '{
  "originalUrl": "facebook.com",
  "customAlias": "my-alias"
}'
```

**Response (201 Created):**

```json
{
  "id": 4,
  "alias": "my-alias",
  "shortUrl": "http://localhost:8080/my-alias",
  "originalUrl": "https://facebook.com",
  "createdAt": "2026-01-19T05:31:04.70586"
}
```

### List all URLs

```bash
curl --request GET \
  --url http://localhost:8080/api/v1/urls
```

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "alias": "EdyHo5",
    "shortUrl": "http://localhost:8080/EdyHo5",
    "originalUrl": "https://instagram.com",
    "createdAt": "2026-01-19T03:01:31.694346"
  },
  {
    "id": 2,
    "alias": "custom-alias",
    "shortUrl": "http://localhost:8080/custom-alias",
    "originalUrl": "https://instagram.com",
    "createdAt": "2026-01-19T03:01:35.540635"
  },
  {
    "id": 4,
    "alias": "my-alias",
    "shortUrl": "http://localhost:8080/my-alias",
    "originalUrl": "https://facebook.com",
    "createdAt": "2026-01-19T05:31:04.70586"
  }
]
```

### Delete a shortened URL

```bash
curl --request DELETE \
  --url http://localhost:8080/api/v1/sA5lmY
```

**Response:** `204 No Content`

### Redirect to original URL

```bash
curl --request GET \
  --url http://localhost:8080/EdyHo5
# Redirects (302) to https://instagram.com
```

---

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/urlshortener` | PostgreSQL JDBC URL |
| `DATABASE_USERNAME` | `postgres` | Database username |
| `DATABASE_PASSWORD` | `postgres` | Database password |
| `SERVER_PORT` | `8080` | Backend server port |
| `APP_BASE_URL` | `http://localhost:8080` | Base URL for short URLs |
| `JPA_SHOW_SQL` | `false` | Enable SQL logging |

---

## 🧪 Running Tests

### Backend Unit & Integration Tests

```bash
cd url-shortener-backend
./mvnw test
```
---

## Assumptions & Design Decisions

### Architecture

| Decision | Rationale |
|----------|-----------|
| **Monorepo** | Frontend and backend in same repo for easier development, but deployed as separate containers |
| **API Versioning** | All endpoints under `/api/v1/` to support backward compatibility |

### Backend

| Decision | Rationale |
|----------|-----------|
| **Alias Generation** | Random 6-character alphanumeric string using SecureRandom with Base62 character set (A-Z, a-z, 0-9) for URL-safe, collision-resistant aliases |
| **Duplicate URLs Allowed** | Same URL can be shortened multiple times with different aliases |
| **Primary Key: Long** | Auto-increment Long provides better DB performance than UUID for this use case |
| **PostgreSQL** | ACID compliance, persistent storage, production-ready, efficient indexing |
| **Liquibase** | Version-controlled database schema migrations |

### URL Validation

| Rule | Description |
|------|-------------|
| Protocol optional | URLs work with or without `http://` or `https://` |
| www optional | Accepts URLs with or without `www.` subdomain |
| Auto-prepend | If no protocol provided, `https://` is added for redirect |
| Valid domain required | Must be a valid domain structure |

### Alias Constraints

| Constraint | Value                                                                         |
|------------|-------------------------------------------------------------------------------|
| Uniqueness | Must be unique across all URLs                                                |
| Allowed characters | Alphanumeric, hyphens (`-`), underscores (`_`) for user provided custom alias |
| Max length | 50 characters                                                                 |

### Frontend

| Decision | Rationale |
|----------|-----------|
| **TanStack Query** | Server state management with caching, refetching, optimistic updates |
| **Bootstrap** | Rapid responsive UI development |
| **Nginx (production)** | Serves static files, proxies API, handles SPA routing |

### Docker

| Decision | Rationale |
|----------|-----------|
| **Multi-stage builds** | Minimizes final image size |
| **Non-root user** | Backend runs as non-root for security |

---