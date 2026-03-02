# ⚰️ API Graveyard

> **Be the first to know when your APIs are at risk — BEFORE your app breaks.**

API Graveyard is a full-stack developer tool that automatically monitors the health of third-party APIs you depend on. Get instant alerts when APIs go down, degrade, or behave unexpectedly.

![Dashboard](https://img.shields.io/badge/Status-Active-green) 
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-green)
![React](https://img.shields.io/badge/React-18-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)

---

## 🚀 The Problem

Developers lose hours debugging when third-party APIs they depend on:
- Go down unexpectedly
- Return unexpected responses
- Get deprecated without notice

API Graveyard solves this by **automatically monitoring your APIs 24/7** and alerting you before your users notice.

---

## ✨ Features

- 🔐 **JWT Authentication** — Secure register/login system
- 🔌 **API Registration** — Add any HTTP endpoint to monitor
- ⚡ **Automated Health Checks** — Scheduler pings your APIs every N minutes
- 🚨 **Smart Alert System** — Alerts fire after 3 consecutive failures (avoids false positives)
- 📊 **Live Dashboard** — Real-time overview of all your APIs
- 🔔 **Alert Center** — View, filter and resolve alerts
- 🔄 **Manual Check** — Trigger an instant health check anytime

---

## 🛠️ Tech Stack

### Backend
| Technology | Purpose |
|---|---|
| Java 17 | Core language |
| Spring Boot 4.x | REST API framework |
| Spring Security + JWT | Authentication |
| Spring Data JPA | Database ORM |
| MySQL 8.0 | Database |
| Hibernate | SQL generation |
| Maven | Build tool |

### Frontend
| Technology | Purpose |
|---|---|
| React 18 | UI framework |
| Vite | Build tool |
| React Router | Client-side routing |
| TanStack Query | Server state management |
| Axios | HTTP client |
| Tailwind CSS | Styling |

---

## 📐 Architecture
```
┌─────────────────────────────────────┐
│         React Frontend (5173)        │
│  Dashboard | APIs | Alerts | Auth    │
└──────────────┬──────────────────────┘
               │ REST API (Axios + JWT)
┌──────────────▼──────────────────────┐
│      Spring Boot Backend (8080)      │
│  Controllers → Services → JPA        │
│  + Scheduled Health Check Engine    │
└──────────┬───────────────┬──────────┘
           │               │
┌──────────▼───────┐  ┌────▼──────────┐
│   MySQL Database  │  │ External APIs │
│  6 Tables         │  │ (HTTP Pings)  │
└───────────────────┘  └───────────────┘
```

---

## 🗄️ Database Schema
```
users
  └── tracked_apis (one user → many APIs)
        ├── health_checks (one API → many checks)
        └── alerts (one API → many alerts)
              └── notifications
  └── notification_preferences (one user → one preference)
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+
- Node.js 18+

### 1. Clone the repository
```bash
git clone https://github.com/vaishnavikharat22/api-graveyard.git
cd api-graveyard
```

### 2. Set up MySQL Database
```sql
CREATE DATABASE api_graveyard;
CREATE USER 'api_graveyard_user'@'localhost' IDENTIFIED BY 'apigraveyard123';
GRANT ALL PRIVILEGES ON api_graveyard.* TO 'api_graveyard_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Run the Backend
```bash
cd backend
mvn spring-boot:run
```
Backend starts on `http://localhost:8080`

### 4. Run the Frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend starts on `http://localhost:5173`

### 5. Open the App
Visit `http://localhost:5173` → Register → Start monitoring!

---

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/auth/register` | Register new user |
| POST | `/api/v1/auth/login` | Login and get JWT token |

### API Management
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/tracked-apis` | List all tracked APIs |
| POST | `/api/v1/tracked-apis` | Add new API to monitor |
| GET | `/api/v1/tracked-apis/{id}` | Get API details |
| PUT | `/api/v1/tracked-apis/{id}` | Update API config |
| DELETE | `/api/v1/tracked-apis/{id}` | Remove API |
| POST | `/api/v1/tracked-apis/{id}/check-now` | Manual health check |

### Monitoring
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/tracked-apis/{id}/health-history` | Health check history |
| GET | `/api/v1/dashboard/summary` | Dashboard stats |

### Alerts
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/alerts` | List all alerts |
| PATCH | `/api/v1/alerts/{id}/resolve` | Resolve an alert |
| GET | `/api/v1/alerts/unread-count` | Unread alert count |

---

## 🧠 How the Health Check Engine Works
```
Every 60 seconds:
  1. Scheduler finds all APIs where nextCheckAt < now
  2. Thread pool (10 threads) pings each API in parallel
  3. Records: status code, response time, response hash
  4. Updates nextCheckAt = now + checkInterval

Alert Logic (3-Strike Rule):
  - 1st failure  → status = DEGRADED
  - 3rd failure  → status = DOWN + CRITICAL alert created
  - Recovery     → status = ACTIVE + recovery alert created
```

---

## 📁 Project Structure
```
api-graveyard/
├── backend/
│   └── src/main/java/com/apigraveyard/backend/
│       ├── config/          # Security, CORS, RestTemplate
│       ├── controller/      # REST endpoints
│       ├── dto/             # Request/Response objects
│       ├── exception/       # Global error handling
│       ├── model/           # JPA entities + enums
│       ├── repository/      # Spring Data JPA repos
│       ├── scheduler/       # Health check automation
│       ├── security/        # JWT filter + utils
│       └── service/         # Business logic
└── frontend/
    └── src/
        ├── api/             # Axios API calls
        ├── components/      # Reusable UI components
        ├── context/         # Auth context (JWT)
        ├── pages/           # Full page components
        └── utils/           # Helper functions
```

---

## 🔮 Roadmap

- [ ] Email notifications via SendGrid
- [ ] Response time charts (Recharts)
- [ ] API detail page with health timeline
- [ ] Deprecation detection via HTTP headers
- [ ] Deploy to Railway + Vercel
- [ ] Team workspaces

---

## 👩‍💻 Author

**Vaishnavi Kharat**  
[![GitHub](https://img.shields.io/badge/GitHub-vaishnavikharat22-black)](https://github.com/vaishnavikharat22)

---

## 📄 License

MIT License — feel free to use this project for learning and portfolio purposes.
