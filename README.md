# Spring Ollama Amount Extractor (PoC)

### Start OCR microservice (python)
```bash
cd python-ocr
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
python ocr_service.py
# service listens on http://localhost:5000/ocr
```


### Run Spring Boot app
```bash
mvn spring-boot:run
```

## Sample curl
Upload image:
```bash
curl -X POST "http://localhost:8080/api/amount/extract/file" -F "file=@sample_bill.jpg"
```
Send text:
```bash
curl -X POST "http://localhost:8080/api/amount/extract/text" -H "Content-Type: application/json" -d '"Total: INR 1200 | Paid: 1000 | Due: 200 | Discount: 10%"'
```

## python-ocr/ocr_service.py (sample OCR microservice)

Design notes:
- OCR microservice is kept separate for best library support (pytesseract).
- Ollama is used via its HTTP API `/api/generate` with prompt engineering to return JSON. The code extracts the first JSON object returned by the model and parses it.
- Robustness: The system includes multiple fallback paths. If Ollama fails, a simple heuristic fallback maps normalized amounts to likely types.
- Guardrails: endpoints return `{"status":"no_amounts_found","reason":"..."}` when no reliable amounts are discovered.
