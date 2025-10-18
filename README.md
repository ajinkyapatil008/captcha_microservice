# captcha_microservice

#Follow the following commands to run the project in your local system after cloning:

🧮 CAPTCHA Service — Spring Boot Microservice

   A lightweight Spring Boot 3.x microservice that generates and validates mathematical CAPTCHAs (addition, subtraction, multiplication).
   This project can be reused across applications to prevent automated bot submissions in login, signup, or form APIs.

🚀 Features

   ✅ RESTful API endpoints (/captcha/generate, /captcha/validate)
   
   ✅ Arithmetic CAPTCHAs with difficulty levels (L1, L2, L3)
   
   ✅ Optional image-based CAPTCHA (Base64 PNG)
   
   ✅ Unique captchaId for each generated challenge
   
   ✅ TTL-based expiry (configurable)
   
   ✅ Redis-backed storage with in-memory fallback
   
   ✅ Built with Spring Boot 3, Java 17
   
   ✅ Easily deployable as a standalone microservice


🧰 Tech Stack

   Component	Technology
   Framework	Spring Boot 3.x
   Language	Java 17
   Build Tool	Maven
   Data Store	Redis / In-Memory
   Serialization	Jackson (JSON)
   Validation	Jakarta Validation API
   REST Testing	Postman / curl

   
📦 Folder Structure

   captcha/
   ├── pom.xml
   ├── README.md
   └── src
       └── main
           ├── java/com/example/captcha
           │   ├── CaptchaServiceApplication.java
           │   ├── controller/CaptchaController.java
           │   ├── service/
           │   │   ├── CaptchaService.java
           │   │   ├── CaptchaGenerator.java
           │   │   └── store/
           │   │       ├── CaptchaStore.java
           │   │       ├── RedisCaptchaStore.java
           │   │       └── InMemoryCaptchaStore.java
           │   ├── dto/ (request & response classes)
           │   ├── model/CaptchaEntry.java
           │   └── util/ImageUtil.java
           └── resources/application.yml

⚙️ Installation & Setup

           1️⃣ Clone the repository
           git clone https://github.com/<your-username>/captcha-service.git
           cd captcha

           2️⃣ Prerequisites
           
           Java 17 or higher installed
           Verify:
           java --version
           
           Maven installed
           Verify:
           mvn -v
           
           Redis if you want distributed caching (Optional)
           Start Redis using Docker:
           docker run --name captcha-redis -p 6379:6379 -d redis:7


🧩 Build & Run

     Run directly
     mvn spring-boot:run

     OR Build and run the jar
    
     mvn clean package -DskipTests
     java -jar target/captcha-service-0.0.1-SNAPSHOT.jar


 By default, the server runs on port 5000.


🧠 API Endpoints

    1️⃣ POST /captcha/generate
    
    Request:
    
    {
      "asImage": true,
      "difficulty": "L1"
    }
    
    
    Response:
    
    {
      "captchaId": "b7a1a6f0-xxxx-xxxx-xxxx-xxxxxxxx",
      "question": "5 + 3",
      "imageBase64": "iVBORw0KGgoAAAANSUhEUgAA...",
      "expiresIn": 180
    }

    2️⃣ POST /captcha/validate
    
    Request:
    
    {
      "captchaId": "b7a1a6f0-xxxx-xxxx-xxxx-xxxxxxxx",
      "answer": "8"
    }
    
    
    Response (Success):
    
    {
      "success": true,
      "message": "validated"
    }
    
    
    Response (Failure):
    
    {
      "success": false,
      "message": "incorrect"
    }
    
    
    Response (Expired):
    
    {
      "success": false,
      "message": "expired or not found"
    }

 🧪 Testing with Postman

    Generate CAPTCHA
    
    Method: POST
    
    URL: http://localhost:5000/captcha/generate
    
    Headers:
    Content-Type: application/json
    
    Body:
    
    { "asImage": true, "difficulty": "L1" }
    
    
    Copy captchaId and note the question (e.g., “3 + 5”).
    
    Validate CAPTCHA
    
    Method: POST
    
    URL: http://localhost:5000/captcha/validate
    
    Headers:
    Content-Type: application/json
    
    Body:
    
    { "captchaId": "paste-here", "answer": "8" }


  ✅ You should get { "success": true, "message": "validated" }


⚙️ Configuration

    File: src/main/resources/application.yml
    
    server:
      port: 5000
    
    captcha:
      ttl: 180   # in seconds
    
    redis:
      host: localhost
      port: 6379
    
    
    You can modify:
    
    Port → under server.port
    
    Captcha TTL → under captcha.ttl
    
    Redis connection → under redis.host & redis.port


 🧾 Error Handling

   Error Type	Cause	HTTP Code
   
   400	Wrong or expired captcha	400
   
   500	Server-side error / Redis failure	500
   
   Connection Refused	Service not running / wrong port	—

   <br>
   
 🧱 Troubleshooting
 
      Problem	Solution
      
      Port 5000 busy	Change server.port in application.yml
      
      Redis not running	Start Redis or let it fallback to in-memory
      
      Build errors	Run mvn clean install -U to refresh dependencies
      
      Java version error	Ensure Java 17+ is set as default (java -version)

<br>
📈 Future Enhancements


   Add distorted image CAPTCHAs (anti-OCR)
   
   Add audio CAPTCHAs for accessibility
   
   Add rate-limiting filter using Bucket4j
   
   Expose metrics via Spring Boot Actuator
   
   JWT-based stateless CAPTCHA tokens


<br>
👨‍💻 Contributing


   Pull requests are WELCOME.
   
   Steps:
   
   Fork the repository
   
   Create your feature branch (git checkout -b feature-name)
   
   Commit changes (git commit -m 'Add feature')
   
   Push to branch (git push origin feature-name)
   
   Open a Pull Request
   
<br>
📄 License

This project is licensed under the MIT License — free to use, modify, and distribute with attribution.


<br>
🧩 Author


Ajinkya Patil
Created as part of an R&D on Java Spring Boot microservices.
📧 patilajinkya148@gmail.com

<br>
<br>
<br>

 

The problem statement and guidelines and specifications I followed for this project are as follows:

 <b>Objectives:-</b>
 
   • Random arithmetic challenges (addition, subtraction, multiplication).
   
   • Unique tokens (captchaId) per request with short TTL (2–3 minutes).
   
   • Image (Base64 PNG) or plain-text question returned to clients.
   
   • Simple REST API: /captcha/generate and /captcha/validate.
   
   • Optional: JWT-signed token per CAPTCHA for stateless verification.
   
   • Production add-ons: Redis cache, rate limiting, circuit breaker on consumer

   

 <b>High-Level Architecture:- </b>
 
 The service is a standalone Spring Boot application that exposes two endpoints for generation and validation.
 Other Spring Boot apps call it over HTTP. Internally the service generates math problems, manages one-time
 tokens, and stores answers with a short expiry.
 
   • Endpoints: POST /captcha/generate and POST /captcha/validate.
   
   • Storage: short-lived cache holding answer by captchaId (Redis or local Caffeine).
   
   • TTL: 120–180 seconds; tokens are deleted on successful validation (one-time use).
   
   • Optional image rendering converts the question to a PNG and returns Base64.
   
   • Security: HTTPS, IP rate limit, network allow-list, minimal logging.



 
 <b>API Design</b>
 
   A) Generate CAPTCHA — POST /captcha/generate
   
       • Optional inputs: asImage (boolean), difficulty (L1, L2, L3).
       
       • Response fields: captchaId (UUID), question (string), imageBase64 (optional), expiresIn (seconds).
       
       • Errors: 429 (rate limit), 500 (internal error).


 
   B) Validate CAPTCHA — POST /captcha/validate
   
     • Inputs: captchaId, answer.
     
     • Response fields: success (boolean), message (string).
     
     • Behavior: on success, delete token; on failure, keep until TTL expires.
     
     • Errors: 400 (invalid request), 410 (expired/used), 429 (rate limit).



  <b>Logic for Mathematical CAPTCHA</b>

  <img width="618" height="103" alt="image" src="https://github.com/user-attachments/assets/4e8db67c-e5cd-495c-ac53-2351cd9c8a3a" />



<b>Configurable Difficulty:</b>

   • L1: single-digit operations (0–9).
   
   • L2: two-digit operations (10–99).
   
   • L3: simple algebra (e.g., 2x + 4 = 10, find x)


<b>Integration Guidance (Spring Boot Consumers)</b>

 • Call the service from server-side only; never trust client-side verification.
 
 • Create a generate-then-validate flow: generate on GET/view, validate on the protected POST.
 
 • Maintain captchaId in server session or hidden field; do not store the answer in the UI.
 
 • On successful validation, purge the used token to prevent replay.
 
 • Timeout UX: if TTL expires, ask the user to refresh CAPTCHA.


 
 <b>Security, Hardening & Rate Limiting</b>
 
 • Use HTTPS and restrict network access (service-to-service only).
 
 • Rate-limit by IP or client key to mitigate brute force (e.g., 60 req/min).
 
 • Avoid logging answers or full questions; log only minimal metadata.
 
 • Use separate Redis database or namespace for CAPTCHA keys.
 
 • Observability: expose metrics for generate/validate counts, hit rate, and expiries.


 
 <b>Deployment & Operations:</b>
 
 • Package as a Spring Boot JAR; containerize for consistent deployment.
 
 • Run with Redis for horizontal scale; fallback to in-memory cache for dev.
 
 • Expose port 5000; configure via environment variables (TTL, difficulty).
 
 • Provide health/readiness endpoints for probes; autoscale on CPU/RPS.
 
 • Disaster recovery: treat as stateless; redeploy nodes freely.

 
 <b>Testing Checklist & Runbook:</b>
 
 • Functional: generate returns captchaId and question/image; validate returns success on correct answer.
 
 • Negative: wrong answer; expired token; re-use after success; rate-limit exceeded.
 
 • Performance: sustain expected RPS with Redis; verify latency < 50 ms p95.
 
 • Security: TLS enabled; logs redacted; network restricted; headers sanitized.
 
 • Monitoring: create dashboards for success/failure ratio and expiries.

