from flask import Flask, jsonify
import Adafruit_DHT

app = Flask(__name__)
sensor = Adafruit_DHT.DHT11
pin = 4

@app.route('/hong_babo', methods=['GET'])
def get_sensor_data():
    humidity, temperature = Adafruit_DHT.read_retry(sensor, pin)
    if humidity is not None and temperature is not None:
        return jsonify({'temperature': temperature, 'humidity': humidity})
    else:
        return jsonify({'error': 'Failed to read sensor data'})

if __name__ == '__main__':
    app.run(host='192.168.0.90', port=8080)