from flask import Flask, request, send_file, jsonify
import json

app = Flask(__name__)

@app.route('/guipost', methods=['POST', 'GET'])
def handle_image():
    if request.method == 'POST':
        file = request.files['file']
        file.save('guicap.jpg')
        return 'Image received successfully'
    elif request.method == 'GET':
        return send_file('guicap.jpg', mimetype='image/jpg')

@app.route('/send_error', methods=['GET'])
def send_error():
    if request.method == 'GET':
        with open("server_response.json", "r") as f:
            error_data = json.load(f)
        return jsonify(error_data)

if __name__ == '__main__':
    app.run(host='192.168.137.113', port=8080, debug=True)