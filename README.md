# GauLens Backend

Spring Boot REST API for the image-based cattle & buffalo breed recognition project, backed by an H2 database.

## Requirements
- Java 17+
- Maven 3.6+ (or use the included `mvnw` wrapper if you generate one via `mvn -N io.takari:maven:wrapper`)

## Run it
```bash
cd gaulens-backend
mvn spring-boot:run
```
The API starts on **http://localhost:8080**.

## H2 Console
Open **http://localhost:8080/h2-console** in a browser.
- JDBC URL: `jdbc:h2:mem:gaulensdb`
- Username: `sa`
- Password: *(leave blank)*

The database is in-memory, so it reseeds itself from `src/main/resources/data.sql` every time the app restarts — 8 sample breeds (4 cattle, 4 buffalo) are loaded automatically.

## Endpoints

| Method | Path                     | Description                                      |
|--------|--------------------------|---------------------------------------------------|
| GET    | `/api/breeds`            | List all breeds. Supports `?type=CATTLE\|BUFFALO` and `?search=keyword` |
| GET    | `/api/breeds/{id}`       | Get one breed                                     |
| POST   | `/api/breeds`            | Create a breed (JSON body, see below)             |
| PUT    | `/api/breeds/{id}`       | Update a breed                                    |
| DELETE | `/api/breeds/{id}`       | Delete a breed                                    |
| POST   | `/api/predict`           | Upload an image (`multipart/form-data`, field name `image`) and get a predicted breed |
| GET    | `/api/predictions`       | Last 20 scan results                              |
| GET    | `/api/predictions/stats` | Total scans, average confidence, breeds seen      |

### Example: create a breed
```bash
curl -X POST http://localhost:8080/api/breeds \
  -H "Content-Type: application/json" \
  -d '{
        "name": "Kankrej",
        "type": "CATTLE",
        "origin": "Gujarat / Rajasthan border",
        "description": "A grey draught-and-dairy breed with lyre-shaped horns.",
        "traits": ["Lyre-shaped horns", "Grey coat", "Draught power"]
      }'
```

### Example: predict a breed from an image
```bash
curl -X POST http://localhost:8080/api/predict \
  -F "image=@/path/to/cow-photo.jpg"
```

## Important: this is a mock predictor, not a trained model
`PredictionService.predict()` currently picks a plausible-looking result from whatever
breeds exist in H2 — the same approach the frontend demo uses — so the whole stack
(upload → API → database → response) works end-to-end without a real model.

To make it real:
1. Train and export your CNN (e.g. as a `.h5`/`.onnx`/`SavedModel`).
2. Serve it behind a small inference microservice (Flask/FastAPI in Python is easiest).
3. In `PredictionService.predict()`, replace the mock logic with an HTTP call
   (`RestTemplate` or `WebClient`) that sends the image bytes to that microservice
   and parses back the real breed name + confidence + alternatives.
4. Keep the rest of the method (saving a `PredictionRecord`) unchanged.

## Connecting the HTML/Bootstrap frontend
In the frontend's `runScan()` function, replace the simulated `setInterval` progress
with a real `fetch('http://localhost:8080/api/predict', { method: 'POST', body: formData })`
call, and populate `predBreed` / `predConf` / `altList` from the JSON response instead
of the random sample. CORS is already open for all origins in `CorsConfig.java` so the
static HTML file (or a React dev server) can call this API directly. Tighten
`allowedOriginPatterns` before deploying anywhere public.

## Project structure
```
src/main/java/com/gaulens/backend/
  BreedRecognitionApplication.java   entry point
  model/         Breed, PredictionRecord, AnimalType (JPA entities)
  repository/    Spring Data JPA repositories
  service/       business logic (BreedService, PredictionService)
  controller/    REST endpoints
  dto/           request/response shapes
  config/        CorsConfig
  exception/     ResourceNotFoundException + global @RestControllerAdvice handler
src/main/resources/
  application.properties   H2 + JPA + upload size config
  data.sql                 seed data (8 sample breeds)
```
