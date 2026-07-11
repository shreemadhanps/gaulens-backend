# GauLens — Image-Based Breed Recognition for Cattle & Buffaloes

A full-stack web application that identifies indigenous Indian cattle and buffalo breeds from a photograph, backed by a database of all 73 NBAGR-recognised breeds (53 cattle + 20 buffalo).

## 🌐 Live Demo

Try it directly, no setup needed: **https://gaulens-backend.onrender.com**

*(Hosted on Render's free tier — if the app has been idle, the first load may take 30-50 seconds while it wakes up. Subsequent loads are fast.)*

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-brightgreen)
![H2](https://img.shields.io/badge/Database-H2-blue)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5-purple)

---

## 1. Project Title

**GauLens — Image-Based Breed Recognition for Cattle & Buffaloes**

---

## 2. Problem Statement

India is home to 53 NBAGR-recognised indigenous cattle breeds and 20 recognised buffalo breeds, each suited to a specific region and climate. Despite this diversity, breed identification in the field is still done manually — by eye, or by cross-referencing printed breed charts. This process is slow, inconsistent between observers, and error-prone, particularly for visually similar breeds. Incorrect identification can affect vaccination schedules, breeding programmes, insurance claims, and government livestock scheme eligibility.

There is currently no easy-to-use digital tool that lets a farmer, field worker, or veterinary staff member identify a breed simply by photographing the animal.

---

## 3. Project Objectives

- Design and implement a web-based system that accepts a photograph of a cattle or buffalo and returns a predicted breed.
- Build a structured, queryable database covering all 73 NBAGR-recognised indigenous breeds, including origin and distinguishing traits.
- Provide a searchable, filterable breed gallery so users can browse and learn about breeds directly.
- Establish a complete, working client-server-database pipeline that can later be extended with a trained image-classification model.
- Log every prediction request for future analysis and model evaluation.

---

## 4. Module List

| # | Module | Description |
|---|--------|-------------|
| 1 | **Frontend UI Module** | Landing page, animated hero, responsive layout (HTML/CSS/Bootstrap) |
| 2 | **Image Upload & Scan Module** | Drag-and-drop / file picker upload, live scan animation, calls the prediction API |
| 3 | **Breed Gallery Module** | Fetches and displays all breeds; supports search and Cattle/Buffalo filtering |
| 4 | **Prediction Module (Backend)** | Accepts an uploaded image, returns a predicted breed with confidence and alternatives |
| 5 | **Breed Management Module (Backend)** | CRUD REST API for breed records (create, read, update, delete) |
| 6 | **Prediction History Module** | Logs every scan to the database; exposes recent history and aggregate stats |
| 7 | **Database Module** | H2 schema — `breeds`, `breed_traits`, `prediction_records` tables |
| 8 | **Exception Handling Module** | Centralised error handling returning structured JSON error responses |

---

## 5. Key Terms Explained (Beginner-Friendly)

If you're new to web development, here's what every technical word in this project actually means, in plain language.

### General concepts

| Term | What it means |
|---|---|
| **Frontend** | The part of the app you actually *see and click* in your browser — the buttons, text, images, colors. Built here using HTML, CSS, Bootstrap, and JavaScript. |
| **Backend** | The part of the app running behind the scenes on a server — it receives requests, processes data, and sends back answers. You never *see* it directly; it just responds when the frontend asks it something. Built here using Java and Spring Boot. |
| **Database** | Where all the information is permanently stored — in this project, the list of 73 breeds and every scan someone performs. Built here using H2. |
| **API (Application Programming Interface)** | A defined set of "doors" the frontend can knock on to ask the backend for something. For example, `/api/breeds` is a door that says "give me the list of all breeds." |
| **REST API** | A common style/pattern for designing APIs, using standard actions: GET (fetch data), POST (create data), PUT (update data), DELETE (remove data). |
| **Endpoint** | One specific "door" (URL) in an API. `/api/predict` is an endpoint; `/api/breeds` is a different endpoint. |
| **JSON** | A simple text format used to send data between frontend and backend, e.g. `{"name": "Gir", "type": "CATTLE"}`. Almost all web APIs speak JSON. |
| **HTTP Request** | A message sent from the frontend to the backend, e.g. "Hey, give me all breeds" or "Here's an image, tell me the breed." |
| **CRUD** | Short for Create, Read, Update, Delete — the four basic things you can do to any piece of data. |
| **Client-Server Architecture** | The overall pattern where one program (the client — your browser) asks another program (the server — the backend) for information over a network. |

### Technologies used in this project

| Term | What it means |
|---|---|
| **HTML** | The basic skeleton/structure of a webpage — headings, paragraphs, buttons, images. |
| **CSS** | Controls how the webpage *looks* — colors, spacing, fonts, animations. |
| **Bootstrap** | A ready-made CSS toolkit that gives you pre-styled buttons, grids, and layouts, so you don't have to design everything from scratch. |
| **JavaScript** | A programming language that runs inside the browser and makes the page *interactive* — e.g., reacting when you click "Upload," or fetching data without reloading the page. |
| **Fetch API** | A built-in JavaScript tool used to send requests to the backend and get a response back, without refreshing the page. |
| **Java** | A widely-used programming language. This project's entire backend is written in Java. |
| **Spring Boot** | A popular Java framework (a set of pre-built tools) that makes it much faster to build a backend server, without writing everything from scratch. |
| **Maven** | A tool that manages your Java project — it downloads the libraries your code needs and knows how to compile and run your project with one simple command (`mvn spring-boot:run`). |
| **H2 Database** | A lightweight database that can run entirely inside your Java application — no separate installation needed. Great for learning and small projects. |
| **JPA (Java Persistence API)** | A standard way for Java code to talk to a database using plain Java objects, instead of writing raw SQL by hand. |
| **Hibernate** | The actual tool that does the work behind JPA — it converts your Java objects into database rows and back again. |
| **Entity** | A Java class that represents one table in the database. For example, the `Breed` class in this project represents the `breeds` table. |
| **Repository** | A Java interface that gives you ready-made methods to fetch, save, or delete data from the database, without writing SQL yourself. |
| **Controller** | The part of the backend code that receives a request from the frontend (like "get all breeds") and decides what to do with it. |
| **Service** | The part of the backend code that contains the actual business logic — e.g., "pick a predicted breed and calculate its confidence." |
| **DTO (Data Transfer Object)** | A simple Java class used just to shape the data being sent back to the frontend as JSON. |

### Project-specific terms

| Term | What it means in this project |
|---|---|
| **Breed** | A distinct type of cattle or buffalo, e.g. Gir, Sahiwal, Murrah. |
| **NBAGR** | The National Bureau of Animal Genetic Resources — the official Indian government body that recognises and registers indigenous livestock breeds. |
| **Prediction** | The system's guess at which breed is shown in an uploaded photo. |
| **Confidence Score** | A percentage showing how sure the system is about its top prediction. |
| **Colour-Matching Heuristic** | The current version reads the uploaded photo's actual pixel colours (black, reddish-brown, white-grey, grey, or spotted) and matches that against each breed's recorded coat colour. This is real image analysis, but not full AI breed recognition — many breeds share a colour family, so it can't yet tell visually similar breeds apart by shape or horns. A trained AI model is the planned next step. |

---

## 6. Technology Stack

**Frontend:** HTML5, CSS3, Bootstrap 5, JavaScript (Fetch API)
**Backend:** Java 17, Spring Boot 3, Spring Web, Spring Data JPA, Hibernate
**Database:** H2 (in-memory, embedded)
**Build Tool:** Apache Maven
**IDE:** Visual Studio Code

---

## 7. System Architecture

```
Frontend (HTML/CSS/Bootstrap/JS)
        │  fetch()  HTTP/REST
        ▼
Backend (Spring Boot REST API)
        │  JPA / Hibernate
        ▼
Database (H2)
```

Three-tier architecture: presentation, application, and data layers are fully separated, so the colour-matching prediction logic can later be swapped for a real trained model without changing the frontend or database.

---

## 8. Features

- 📸 Upload a photo and get a predicted breed with a confidence score and top alternatives
- 🔍 Searchable, filterable gallery of all 73 breeds (Cattle / Buffalo)
- 📊 Live scan history logged to the database
- 🎨 Responsive, animated UI built with Bootstrap 5
- 🔌 REST API that can be consumed by any client (web, mobile, Postman, etc.)

---

## 9. Database Schema

**breeds**
| Column | Type | Description |
|---|---|---|
| id | BIGINT (PK) | Unique breed ID |
| name | VARCHAR, UNIQUE | Breed name |
| type | ENUM | CATTLE or BUFFALO |
| origin | VARCHAR | State(s) of origin |
| description | VARCHAR(1000) | Short description |

**breed_traits**
| Column | Type | Description |
|---|---|---|
| breed_id | BIGINT (FK) | References breeds.id |
| trait | VARCHAR | Short trait tag |

**prediction_records**
| Column | Type | Description |
|---|---|---|
| id | BIGINT (PK) | Unique record ID |
| image_name | VARCHAR | Uploaded filename |
| predicted_breed | VARCHAR | Top predicted breed |
| predicted_type | ENUM | CATTLE or BUFFALO |
| confidence | DOUBLE | Confidence percentage |
| alternatives | VARCHAR(500) | Runner-up breeds and scores |
| scanned_at | TIMESTAMP | Date/time of prediction |

---

## 10. API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/breeds` | List all breeds (`?type=`, `?search=` supported) |
| GET | `/api/breeds/{id}` | Get a single breed |
| POST | `/api/breeds` | Create a breed |
| PUT | `/api/breeds/{id}` | Update a breed |
| DELETE | `/api/breeds/{id}` | Delete a breed |
| POST | `/api/predict` | Upload an image, get a predicted breed |
| GET | `/api/predictions` | Recent scan history (last 20) |
| GET | `/api/predictions/stats` | Aggregate scan statistics |

---

## 11. Getting Started

### Option A — Just use the live version
No installation needed: **https://gaulens-backend.onrender.com**

### Option B — Run it yourself locally

**Prerequisites**
- Java 17+
- Apache Maven 3.6+

**Run it**
```bash
mvn spring-boot:run
```
Then open **http://localhost:8080** in your browser.

The H2 console is available at **http://localhost:8080/h2-console**
JDBC URL: `jdbc:h2:mem:gaulensdb` · Username: `sa` · Password: *(blank)*

---

## 12. Project Status / Known Limitation

This project currently uses a **colour-matching heuristic** — it genuinely analyses the uploaded photo's average pixel colour and matches it against each breed's documented coat colour, rather than picking randomly. This is a real, working improvement, but it is not yet true breed recognition, since colour alone cannot distinguish breeds that share a coat colour family. See **Future Scope** below for the plan to replace it with a trained CNN model.

---

## 13. Future Scope

- [ ] Train and integrate a real CNN model for genuine image-based classification
- [ ] Switch to a persistent database (PostgreSQL/MySQL) for production use
- [ ] Add user accounts and personal scan history
- [ ] Package as a mobile app for field use
- [ ] Add multi-language support (Hindi and regional languages)
- [ ] Expand dataset as NBAGR registers new breeds

---

