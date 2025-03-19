import React, { useState } from 'react';
import {
  Container,
  Row,
  Col,
  Form,
  Button,
  Card,
  Spinner,
  Alert,
  ToggleButton,
  ToggleButtonGroup,
} from 'react-bootstrap';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';

function App() {
  // State variables
  const [file, setFile] = useState(null);
  const [preview, setPreview] = useState(null);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);
  const [modelType, setModelType] = useState('ollama');
  const [apiKey, setApiKey] = useState('');

  // Handle file selection
  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile) {
      setFile(selectedFile);
      setPreview(URL.createObjectURL(selectedFile));
      setResult(null);
      setError(null);
    }
  };

  // Handle model type change
  const handleModelTypeChange = (val) => {
    setModelType(val);
    setError(null);
  };

  // Handle API key change
  const handleApiKeyChange = (e) => {
    setApiKey(e.target.value);
    setError(null);
  };

  // Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!file) {
      setError('Please select an image file');
      return;
    }

    if (modelType === 'openai' && !apiKey) {
      setError('Please enter your OpenAI API key');
      return;
    }

    setLoading(true);
    setError(null);

    const formData = new FormData();
    formData.append('image', file);
    formData.append('modelType', modelType);

    if (modelType === 'openai') {
      formData.append('apiKey', apiKey);
    }

    try {
      // Send the image to the backend for processing
      const response = await axios.post(
        'http://localhost:8080/api/ocr/process',
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        }
      );
      setResult(response.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Error processing image');
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="app-container glow-container">
      <div className="glow-effect glow-blue"></div>
      <div className="glow-effect glow-purple"></div>

      <Container className="py-5">
        <Row className="mb-4">
          <Col className="text-center">
            <h1 className="app-title">OCR Text Extractor</h1>
            <p className="text-muted">
              Upload an image to extract and structure text using OCR
            </p>
          </Col>
        </Row>

        {!result ? (
          <Row className="justify-content-center">
            <Col md={10} lg={8} xl={6}>
              <Card className="shadow-sm card-dark">
                <Card.Header className="card-header-dark">
                  Upload Image
                </Card.Header>
                <Card.Body>
                  <Form onSubmit={handleSubmit}>
                    <Form.Group className="mb-3">
                      <Form.Label>Select Image</Form.Label>
                      <Form.Control
                        type="file"
                        accept="image/*"
                        onChange={handleFileChange}
                        className="form-control-dark"
                      />
                      <Form.Text>
                        Supported formats: JPG, PNG, BMP, GIF
                      </Form.Text>
                    </Form.Group>

                    <Form.Group className="mb-3">
                      <Form.Label>Select Model</Form.Label>
                      <div>
                        <ToggleButtonGroup
                          type="radio"
                          name="modelType"
                          value={modelType}
                          onChange={handleModelTypeChange}
                          className="w-100"
                        >
                          <ToggleButton
                            id="ollama"
                            value="ollama"
                            variant={
                              modelType === 'ollama'
                                ? 'primary'
                                : 'outline-secondary'
                            }
                            className="w-50"
                          >
                            Ollama (Local)
                          </ToggleButton>
                          <ToggleButton
                            id="openai"
                            value="openai"
                            variant={
                              modelType === 'openai'
                                ? 'primary'
                                : 'outline-secondary'
                            }
                            className="w-50"
                          >
                            OpenAI (GPT-4o)
                          </ToggleButton>
                        </ToggleButtonGroup>
                      </div>
                    </Form.Group>

                    {modelType === 'openai' && (
                      <Form.Group className="mb-3">
                        <Form.Label>OpenAI API Key</Form.Label>
                        <Form.Control
                          type="password"
                          placeholder="Enter your OpenAI API key"
                          value={apiKey}
                          onChange={handleApiKeyChange}
                          className="form-control-dark"
                        />
                        <Form.Text>
                          Your API key is sent securely and not stored on our
                          servers
                        </Form.Text>
                      </Form.Group>
                    )}

                    {preview && (
                      <div className="mb-3 text-center">
                        <img
                          src={preview}
                          alt="Preview"
                          className="img-fluid img-thumbnail preview-image"
                        />
                      </div>
                    )}

                    <Button
                      variant="primary"
                      type="submit"
                      disabled={!file || loading}
                      className="w-100 btn-custom"
                    >
                      {loading ? (
                        <>
                          <Spinner
                            as="span"
                            animation="border"
                            size="sm"
                            role="status"
                            aria-hidden="true"
                          />
                          <span className="ms-2">Processing...</span>
                        </>
                      ) : (
                        'Extract Text'
                      )}
                    </Button>
                  </Form>
                </Card.Body>
              </Card>

              {error && (
                <Alert variant="danger" className="mt-3 alert-custom">
                  <i className="bi bi-exclamation-triangle-fill"></i>
                  {error}
                </Alert>
              )}
            </Col>
          </Row>
        ) : (
          <Row>
            <Col lg={6} className="mb-4">
              <Card className="shadow-sm card-dark">
                <Card.Header className="card-header-dark">
                  <i className="bi bi-image me-2"></i>
                  Uploaded Image
                </Card.Header>
                <Card.Body className="text-center">
                  {preview && (
                    <img
                      src={preview}
                      alt="Uploaded"
                      className="img-fluid img-thumbnail preview-image"
                    />
                  )}
                  <Button
                    variant="outline-secondary"
                    size="sm"
                    onClick={() => {
                      setFile(null);
                      setPreview(null);
                      setResult(null);
                    }}
                    className="mt-3 btn-copy"
                  >
                    <i className="bi bi-arrow-left me-1"></i>
                    Upload Another Image
                  </Button>
                </Card.Body>
              </Card>
            </Col>

            <Col lg={6}>
              <Card className="shadow-sm card-dark">
                <Card.Header className="card-header-dark">
                  <i className="bi bi-file-text me-2"></i>
                  Extracted Results
                  <span className="badge bg-info ms-2">
                    {modelType === 'ollama' ? 'Ollama' : 'OpenAI'}
                  </span>
                </Card.Header>
                <Card.Body>
                  <h5 className="result-title">
                    <i className="bi bi-text-paragraph"></i>
                    Raw Text:
                  </h5>
                  <pre className="result-box">
                    {result.extractedText || 'No text extracted'}
                  </pre>

                  <h5 className="result-title mt-4">
                    <i className="bi bi-file-earmark-text"></i>
                    Structured Text:
                  </h5>
                  <pre className="result-box">
                    {result.structuredText || 'No structured text available'}
                  </pre>

                  <div className="d-flex justify-content-end mt-3">
                    <Button
                      variant="outline-secondary"
                      size="sm"
                      onClick={() => {
                        navigator.clipboard.writeText(result.structuredText);
                      }}
                      className="btn-copy"
                    >
                      <i className="bi bi-clipboard me-1"></i>
                      Copy to Clipboard
                    </Button>
                  </div>
                </Card.Body>
              </Card>
            </Col>
          </Row>
        )}

        <footer className="text-center">
          <p>OCR Text Extractor &copy; {new Date().getFullYear()}</p>
        </footer>
      </Container>
    </div>
  );
}

export default App;
