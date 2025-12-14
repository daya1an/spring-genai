# Spring Boot Gen AI with Google Gemini

A production-grade Spring Boot REST API integrating Google Gemini 2.5-flash AI model with six distinct use cases (text generation, summarization, Q&A, translation, code generation, analysis); implemented comprehensive configuration management via application.properties for model selection, temperature tuning, safety settings (HARM_CATEGORY validations), token limits, and dynamic API key injection using environment variables.

## 📋 Table of Contents

- [Features](#features)
- [Project Overview](#project-overview)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Installation & Setup](#installation--setup)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Usage Examples](#usage-examples)
- [API Response Format](#api-response-format)
- [Error Handling](#error-handling)
- [Gradle Commands](#gradle-commands)
- [IDE Setup](#ide-setup)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

- **Text Generation**: Generate creative and informative text based on prompts
- **Text Summarization**: Create concise summaries of long texts
- **Q&A System**: Answer questions with detailed explanations
- **Text Translation**: Translate text into multiple languages
- **Code Generation**: Generate clean, documented code from requirements
- **Text Analysis**: Analyze text for themes, sentiment, and insights
- **Interactive Chat**: Engage in contextual conversations
- **Configuration-Driven**: All settings configurable via `application.properties`
- **Error Handling**: Comprehensive error handling and logging
- **RESTful API**: Clean, intuitive REST endpoints
- **Validation**: Request validation using Jakarta Validation

## 🎯 Project Overview

This project provides a wrapper around Google Gemini API through Spring AI, offering a simplified interface for integrating Gen AI capabilities into your Spring Boot applications. It demonstrates:

- Modern Spring Boot 3.5.8 practices
- Java 21 features
- Gradle Groovy DSL
- Dependency injection and configuration
- RESTful API design
- Error handling and logging
- Property-based configuration

## 🛠️ Technology Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| Java | 21 | Programming language |
| Spring Boot | 3.5.8 | Application framework |
| Spring AI | 1.1.2 | AI integration library |
| Google Gemini | 2.5-flash | AI model |
| Gradle | 8.4 | Build tool |
| Lombok | Latest | Boilerplate reduction |
| Jakarta Validation | Latest | Input validation |
| SLF4J + Logback | Latest | Logging |

## 📋 Prerequisites

Before you begin, ensure you have:

- **Java 21+** installed
  ```bash
  java -version
  ```
- **Gradle 8.4+** installed (or use Gradle Wrapper)
  ```bash
  gradle -v
  ```
- **Google Gemini API Key** from [Google AI Studio](https://aistudio.google.com/app/apikey)
- **Git** for version control
- **IDE** (IntelliJ IDEA, VS Code, or Eclipse recommended)

### Verify Java Installation
```bash
java -version
# Should output: openjdk 21.x.x or higher
```

## 📁 Project Structure

```
spring-boot-genai/
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/com/genai/
│   │   │   ├── GenAIApplication.java           # Main application entry point
│   │   │   ├── config/
│   │   │   │   ├── AIConfiguration.java        # Spring configuration
│   │   │   │   └── AIProperties.java           # Configuration properties
│   │   │   ├── controller/
│   │   │   │   └── GenAIController.java        # REST endpoints
│   │   │   ├── dto/
│   │   │   │   ├── AIRequest.java              # Request DTO
│   │   │   │   └── AIResponse.java             # Response DTO
│   │   │   └── service/
│   │   │       └── GenAIService.java           # Business logic
│   │   └── resources/
│   │       └── application.properties          # Configuration file
│   └── test/
│       └── java/com/genai/
│           └── GenAIApplicationTests.java      # Integration tests
├── build.gradle                                 # Gradle build script
├── settings.gradle                              # Gradle settings
├── gradlew                                      # Gradle wrapper (Linux/Mac)
├── gradlew.bat                                  # Gradle wrapper (Windows)
└── README.md                                    # This file
```

## 🚀 Installation & Setup

### Step 1: Clone or Create Project

```bash
# Create project directory
mkdir spring-boot-genai
cd spring-boot-genai
```

### Step 2: Initialize Gradle Project

```bash
# Initialize Gradle project structure
gradle init --type java-application
```

### Step 3: Copy Project Files

Copy all provided files to your project:
- `build.gradle`
- `settings.gradle`
- `gradlew` and `gradlew.bat`
- `gradle/wrapper/gradle-wrapper.properties`
- `src/` directory structure

### Step 4: Make Gradle Wrapper Executable (Linux/Mac)

```bash
chmod +x gradlew
```

### Step 5: Create Directory Structure

```bash
# Create package structure
mkdir -p src/main/java/com/genai/{config,controller,dto,service}
mkdir -p src/main/resources
mkdir -p src/test/java/com/genai

# Create test file
touch src/test/java/com/genai/GenAIApplicationTests.java
```

### Step 6: Create Java Source Files

Create all Java files in their respective directories with provided code.

### Step 7: Download Dependencies

```bash
./gradlew build
```

## ⚙️ Configuration

### Getting Google Gemini API Key

1. Visit [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Click **"Create API Key"**
3. Copy the generated API key
4. Keep it secure - don't commit it to version control

### Environment Setup

#### Option 1: Environment Variable (Recommended)

**Linux/Mac:**
```bash
export GOOGLE_GEMINI_API_KEY=your-api-key-here
./gradlew bootRun
```

**Windows (PowerShell):**
```powershell
$env:GOOGLE_GEMINI_API_KEY="your-api-key-here"
./gradlew bootRun
```

**Windows (CMD):**
```cmd
set GOOGLE_GEMINI_API_KEY=your-api-key-here
gradlew bootRun
```

#### Option 2: application.properties File

Edit `src/main/resources/application.properties`:
```properties
spring.ai.google.generativeai.api-key=your-api-key-here
```

### Configuration File Reference

**application.properties** contains:

```properties
# Server Configuration
server.port=8080                                    # Server port
server.servlet.context-path=/api                   # API context path

# Application Name
spring.application.name=Spring Boot Gen AI with Google Gemini

# Google Gemini API Configuration
spring.ai.google.generativeai.api-key=${GOOGLE_GEMINI_API_KEY:your-key}
spring.ai.google.generativeai.project-id=${GOOGLE_PROJECT_ID:your-project}

# Model Configuration
spring.ai.google.generativeai.chat.options.model=gemini-2.5-flash
spring.ai.google.generativeai.chat.options.temperature=0.7
spring.ai.google.generativeai.chat.options.max-output-tokens=1000
spring.ai.google.generativeai.chat.options.top-p=0.95
spring.ai.google.generativeai.chat.options.top-k=40

# Safety Settings
spring.ai.google.generativeai.chat.options.safety-settings[0].category=HARM_CATEGORY_HARASSMENT
spring.ai.google.generativeai.chat.options.safety-settings[0].threshold=BLOCK_NONE
# ... (4 more safety settings)

# Embedding Configuration
spring.ai.google.generativeai.embedding.options.model=text-embedding-004

# Request Timeout
app.ai.request.timeout=30

# Logging Configuration
logging.level.root=INFO
logging.level.com.genai=DEBUG
logging.level.org.springframework.ai=DEBUG

# JSON Formatting
spring.jackson.serialization.indent-output=true

# Actuator
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
```

### Available Models

- `gemini-2.5-flash` - Fast, lightweight model (recommended)
- `gemini-2.5-pro` - Most capable model
- `gemini-pro` - Standard model
- `gemini-1.5-flash` - Previous generation flash
- `gemini-1.5-pro` - Previous generation pro

### Valid Harm Categories

- `HARM_CATEGORY_HARASSMENT`
- `HARM_CATEGORY_HATE_SPEECH`
- `HARM_CATEGORY_SEXUALLY_EXPLICIT`
- `HARM_CATEGORY_DANGEROUS_CONTENT`
- `HARM_CATEGORY_CIVIC_INTEGRITY`

### Threshold Options

- `BLOCK_NONE` - Allow all content
- `BLOCK_ONLY_HIGH` - Block only high-risk content
- `BLOCK_MEDIUM_AND_ABOVE` - Block medium and high risk (default)
- `BLOCK_LOW_AND_ABOVE` - Block most content (most restrictive)

## 🔌 API Endpoints

### Base URL
```
http://localhost:8080/api/v1/ai
```

### 1. Generate Response
Generate creative and informative text.

**Endpoint:** `POST /generate`

**Request:**
```json
{
  "prompt": "What is Spring Boot?"
}
```

**Response:**
```json
{
  "content": "Spring Boot is an open-source...",
  "model": "gemini-2.5-flash",
  "temperature": 0.7,
  "maxTokens": 1000,
  "success": true,
  "timestamp": "2025-12-14T17:37:25"
}
```

---

### 2. Summarize Text
Create concise summaries of long texts.

**Endpoint:** `POST /summarize`

**Request:**
```json
{
  "prompt": "Long text to summarize..."
}
```

**Response:** Same format as Generate Response

---

### 3. Answer Question
Get detailed answers to questions.

**Endpoint:** `POST /question`

**Request:**
```json
{
  "prompt": "What is machine learning?"
}
```

**Response:** Same format as Generate Response

---

### 4. Translate Text
Translate text into different languages.

**Endpoint:** `POST /translate`

**Request:**
```json
{
  "prompt": "Hello World",
  "targetLanguage": "Spanish"
}
```

**Response:** Same format as Generate Response

---

### 5. Generate Code
Generate clean, documented code.

**Endpoint:** `POST /generate-code`

**Request:**
```json
{
  "prompt": "Create a Java method to calculate factorial"
}
```

**Response:** Same format as Generate Response

---

### 6. Analyze Text
Analyze text for themes, sentiment, and insights.

**Endpoint:** `POST /analyze`

**Request:**
```json
{
  "prompt": "Text to analyze..."
}
```

**Response:** Same format as Generate Response

---

### 7. Chat
Engage in contextual conversations.

**Endpoint:** `POST /chat`

**Request:**
```json
{
  "prompt": "Tell me a joke"
}
```

**Response:** Same format as Generate Response

---

### 8. Health Check
Verify service status.

**Endpoint:** `GET /health`

**Response:**
```
Google Gemini AI Service is running
```

---

### 9. Model Information
Get configured model information.

**Endpoint:** `GET /model-info`

**Response:**
```json
{
  "provider": "Google Gemini",
  "model": "gemini-2.5-flash",
  "version": "2.5",
  "status": "ACTIVE",
  "description": "Dynamically configured Google Gemini model"
}
```

---

## 📝 Usage Examples

### Using cURL

#### 1. Generate Response
```bash
curl -X POST http://localhost:8080/api/v1/ai/generate \
  -H "Content-Type: application/json" \
  -d '{"prompt":"What is Spring Boot?"}'
```

#### 2. Summarize Text
```bash
curl -X POST http://localhost:8080/api/v1/ai/summarize \
  -H "Content-Type: application/json" \
  -d '{
    "prompt":"Spring Boot is an open-source Java framework that makes it easy to create stand-alone, production-grade Spring-based applications that you can run. It takes an opinionated view of the Spring platform and third-party libraries, so you can get started with minimum fuss. Most Spring Boot applications need minimal Spring configuration."
  }'
```

#### 3. Answer Question
```bash
curl -X POST http://localhost:8080/api/v1/ai/question \
  -H "Content-Type: application/json" \
  -d '{"prompt":"What is Gradle?"}'
```

#### 4. Translate Text
```bash
curl -X POST http://localhost:8080/api/v1/ai/translate \
  -H "Content-Type: application/json" \
  -d '{
    "prompt":"Hello World",
    "targetLanguage":"Spanish"
  }'
```

#### 5. Generate Code
```bash
curl -X POST http://localhost:8080/api/v1/ai/generate-code \
  -H "Content-Type: application/json" \
  -d '{"prompt":"Create a Java method to calculate factorial of a number"}'
```

#### 6. Analyze Text
```bash
curl -X POST http://localhost:8080/api/v1/ai/analyze \
  -H "Content-Type: application/json" \
  -d '{"prompt":"Analyze this text for sentiment and key themes"}'
```

#### 7. Chat
```bash
curl -X POST http://localhost:8080/api/v1/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"prompt":"Tell me an interesting fact about AI"}'
```

#### 8. Health Check
```bash
curl http://localhost:8080/api/v1/ai/health
```

#### 9. Model Info
```bash
curl http://localhost:8080/api/v1/ai/model-info
```

### Using Postman

1. **Create a new POST request**
2. **Set URL:** `http://localhost:8080/api/v1/ai/generate`
3. **Set Headers:**
    - Key: `Content-Type`
    - Value: `application/json`
4. **Set Body (raw JSON):**
   ```json
   {
     "prompt": "What is Spring Boot?"
   }
   ```
5. **Click Send**

### Using Java RestTemplate

```java
RestTemplate restTemplate = new RestTemplate();

AIRequest request = AIRequest.builder()
    .prompt("What is Spring Boot?")
    .build();

HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.APPLICATION_JSON);

HttpEntity<AIRequest> entity = new HttpEntity<>(request, headers);

ResponseEntity<AIResponse> response = restTemplate.exchange(
    "http://localhost:8080/api/v1/ai/generate",
    HttpMethod.POST,
    entity,
    AIResponse.class
);

System.out.println(response.getBody().getContent());
```

## 📤 API Response Format

### Success Response
```json
{
  "content": "Response content from Gemini",
  "model": "gemini-2.5-flash",
  "temperature": 0.7,
  "maxTokens": 1000,
  "success": true,
  "timestamp": "2025-12-14T17:37:25",
  "errorMessage": null
}
```

### Error Response
```json
{
  "content": "Error: Failed to generate content",
  "model": "gemini-2.5-flash",
  "success": false,
  "timestamp": "2025-12-14T17:37:25",
  "errorMessage": "Failed to generate content"
}
```

### HTTP Status Codes

| Code | Meaning |
|------|---------|
| 200 | Success |
| 400 | Bad Request (validation error) |
| 401 | Unauthorized (invalid API key) |
| 429 | Rate Limited (too many requests) |
| 500 | Internal Server Error |

## ❌ Error Handling

The application includes comprehensive error handling:

1. **Validation Errors** - Prompts are validated as non-blank
2. **API Errors** - Caught and returned in response
3. **Configuration Errors** - Safety settings must use valid categories
4. **Timeout Errors** - Configurable timeout for requests
5. **Logging** - All errors logged with DEBUG level

### Common Issues & Solutions

#### Issue: Safety Settings Error
```
element predicate failed: $.category in (HarmCategory.HARM_CATEGORY_HATE_SPEECH, ...)
```

**Solution:** Use valid harm categories in `application.properties`:
```properties
spring.ai.google.generativeai.chat.options.safety-settings[0].category=HARM_CATEGORY_HARASSMENT
```

#### Issue: API Key Not Found
```
Invalid API key provided
```

**Solution:** Set environment variable:
```bash
export GOOGLE_GEMINI_API_KEY=your-key
```

#### Issue: Model Not Found
```
The model does not exist or you do not have access to it
```

**Solution:** Verify model name and access in application.properties

#### Issue: Rate Limiting
```
429 Too Many Requests
```

**Solution:** Implement retry logic or reduce request frequency

## 🔨 Gradle Commands

### Build Commands

```bash
# Clean and build
./gradlew clean build

# Build without tests
./gradlew build -x test

# View project dependencies
./gradlew dependencies

# Run specific task
./gradlew bootRun
```

### Run Commands

```bash
# Run Spring Boot application
./gradlew bootRun

# Run with custom properties
./gradlew bootRun --args='--spring.ai.google.generativeai.api-key=your-key'

# Build executable JAR
./gradlew bootJar

# Run JAR file
java -jar build/libs/spring-boot-genai-1.0.0.jar

# Run JAR with API key
java -Dspring.ai.google.generativeai.api-key=your-key \
  -jar build/libs/spring-boot-genai-1.0.0.jar
```

### Test Commands

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests GenAIApplicationTests

# Run tests with output
./gradlew test --info
```

### Utility Commands

```bash
# Show Gradle version
./gradlew --version

# List all tasks
./gradlew tasks

# Clear Gradle cache
./gradlew clean --refresh-dependencies

# Build and generate IDE config
./gradlew idea    # For IntelliJ IDEA
./gradlew eclipse # For Eclipse
```

## 🖥️ IDE Setup

### IntelliJ IDEA

1. **Open Project**
    - File → Open → Select project folder
    - IDE auto-detects Gradle configuration

2. **Gradle Tool Window**
    - Appears on the right side
    - Run tasks from there

3. **Run Application**
    - Right-click `GenAIApplication.java`
    - Select "Run 'GenAIApplication.main()'"

### VS Code

1. **Install Extensions**
    - "Extension Pack for Java"
    - "Gradle for Java"
    - "Thunder Client" or "REST Client" (for API testing)

2. **Open Project**
    - File → Open Folder → Select project folder

3. **Run Application**
    - Terminal → New Terminal
    - Run: `./gradlew bootRun`

### Eclipse

1. **Generate Eclipse Configuration**
   ```bash
   ./gradlew eclipse
   ```

2. **Import Project**
    - File → Import → Gradle → Existing Gradle Project
    - Select project folder

3. **Run Application**
    - Right-click project → Run As → Java Application

## 🐛 Troubleshooting

### Issue: Gradle Wrapper Permission Error

**Error:**
```
./gradlew: Permission denied
```

**Solution:**
```bash
chmod +x gradlew
```

### Issue: Java Version Mismatch

**Error:**
```
Could not determine java version from 17.0.1
```

**Solution:**
Ensure Java 21+ is installed:
```bash
java -version
# Update JAVA_HOME if needed
export JAVA_HOME=/path/to/java-21
```

### Issue: API Key Not Found

**Error:**
```
API key not provided
```

**Solution:**
```bash
export GOOGLE_GEMINI_API_KEY=your-api-key-here
./gradlew bootRun
```

### Issue: Connection Timeout

**Error:**
```
java.net.SocketTimeoutException: Connection timed out
```

**Solution:**
- Check internet connection
- Verify API key is valid
- Check firewall settings
- Increase timeout in application.properties:
  ```properties
  app.ai.request.timeout=60
  ```

### Issue: Model Not Found

**Error:**
```
The model does not exist or you do not have access to it
```

**Solution:**
- Verify model name in `application.properties`
- Check Google AI Studio for available models
- Ensure model is available in your region

### Issue: Port Already in Use

**Error:**
```
Address already in use: bind
```

**Solution:**
Change port in `application.properties`:
```properties
server.port=8081
```

Or kill the process using the port:
```bash
# Linux/Mac
lsof -i :8080 | grep LISTEN | awk '{print $2}' | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

## 🤝 Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Write tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see LICENSE file for details.

## 📚 Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/3.5.8/reference/)
- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [Google Generative AI Documentation](https://ai.google.dev/docs)
- [Gradle Documentation](https://docs.gradle.org)
- [Java 21 Features](https://docs.oracle.com/en/java/javase/21/)

## 🆘 Support

For issues or questions:

1. Check the **Troubleshooting** section
2. Review error logs in console
3. Check Google Gemini API status
4. Open an issue on GitHub

## ✅ Quick Checklist

- [ ] Java 21+ installed
- [ ] Google Gemini API key obtained
- [ ] Environment variable set or properties configured
- [ ] Dependencies downloaded (`./gradlew build`)
- [ ] Application started (`./gradlew bootRun`)
- [ ] Health check passed (`curl http://localhost:8080/api/v1/ai/health`)
- [ ] API endpoints tested with curl or Postman

---

**Last Updated:** December 14, 2025

**Maintainer:** Your Organization

**Version:** 1.0.0