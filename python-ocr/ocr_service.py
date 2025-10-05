from flask import Flask, request,jsonify
from PIL import Image
import pytesseract
import io

pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'

app = Flask(__name__)

@app.route('/ocr',methods=['POST'])
def ocr():
    if 'file' in request.files:
        f = request.files['file']
        img = Image.open(f.stream).convert('L')
        # basic preprocessing could be added here
        text = pytesseract.image_to_string(img,lang='eng')
        return jsonify({'text':text})
    else:
        return jsonify({'text':''})
if __name__ == '__main__':
    app.run(host='0.0.0.0',port = 5000)

