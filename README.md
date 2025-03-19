# OCR Text Extractor

A full-stack application that extracts and structures text from images using OCR (Optical Character Recognition) technology, powered by multiple AI models.

## Features

- Upload and process images to extract text
- Support for multiple image formats (JPG, PNG, BMP, GIF)
- Choice between two AI models:
  - Ollama (Local) - Using Google's Gemma 3B for text structuring
  - OpenAI (GPT-4) - For enhanced text processing
- Real-time preview and processing
- Copy results to clipboard
- Responsive design with dark mode

## Tech Stack

### Frontend

- React.js with Create React App
- React Bootstrap for UI components
- Axios for API communication
- Modern CSS with animations and glassmorphism effects

### Backend

- Spring Boot 3.4
- Tesseract OCR for text extraction
- Integration with Ollama and OpenAI APIs
- Maven for dependency management
- Lombok for reducing boilerplate code

## Models Used

1. **Tesseract OCR**

   - Primary OCR engine for initial text extraction
   - Language support: English
   - Mode: Neural net LSTM engine

2. **Ollama (Local)**

   - Model: Gemma 3:12b
   - Used for text structuring and formatting
   - Self-hosted solution

3. **OpenAI GPT-4**
   - Alternative model for enhanced text processing
   - Requires API key
   - Better accuracy for complex documents

## Backend Setup

# Clone and enter directory

git clone <repository-url>
cd ocr-app

# Start Spring Boot application

./mvnw spring-boot:run

## Frontend Setup

# Navigate to client directory and install dependencies

cd client
npm install

# Start development server

npm start

# Install Ollama first from ollama.ai, then:

ollama pull gemma:3b

## Configuration (backend/src/main/resources/application.properties)

spring.application.name=ocr-app
server.port=8080

# Maximum file upload size

spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Ollama API configuration

ollama.api.url=http://localhost:11434/api/generate
ollama.model.name=gemma3:12b

# Tesseract configuration

# Windows

tesseract.data.path=C:/Program Files/Tesseract-OCR/tessdata

# Linux

# tesseract.data.path=/usr/share/tessdata

# OpenAI configuration

openai.api.url=https://api.openai.com/v1/chat/completions

# Logging configuration

logging.level.com.example.ocr=DEBUG
logging.level.com.example.ocr.service=DEBUG
