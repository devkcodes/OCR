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
![image](https://github.com/user-attachments/assets/fb1832f0-5103-44fa-96f3-e540510c02f8)

