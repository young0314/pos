# This file is part of a Flask application that uses the Adafruit_DHT library for reading DHT11 sensor data.
# Flask is licensed under the BSD License. See: https://github.com/pallets/flask/blob/main/LICENSE
# Adafruit_DHT is licensed under the MIT License. See: https://github.com/adafruit/Adafruit_Python_DHT/blob/master/LICENSE

# This application runs a web server that provides temperature and humidity data from a DHT11 sensor.
# Ensure to follow the respective licenses of the libraries used in this application.

# Created by [hoyoyongyong].

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
