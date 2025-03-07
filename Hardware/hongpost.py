import Adafruit_DHT
import requests
import json
import RPi.GPIO as GPIO
import time

sensor = Adafruit_DHT.DHT11
pin = 17
door_pin = 4

server_url = "http://192.168.137.106:8080/container/state"
container_number = 123456

GPIO.setmode(GPIO.BCM)
GPIO.setup(door_pin, GPIO.IN, pull_up_down=GPIO.PUD_UP)

def read_server_response(file_path):
    try:
        with open(file_path, 'r') as file:
            data = json.load(file)
            prediction = data.get("prediction", "")
            lifespan = prediction.split(",")[0]
            errorStatus = prediction.split(",")[1].split(":")[1].strip()
            return lifespan, errorStatus
    except Exception as e:
        print("error:", e)
        return None, None

def send_sensor_data(temperature, humidity, doorStatus, lifespan, errorStatus, containNumber):
    data = {
        "temperature": temperature,
        "humidity": humidity,
        "doorStatus": doorStatus,
        "errorStatus": errorStatus,
        "lifespan": lifespan,
        "containNumber": containNumber
    }
    try:
        response = requests.post(server_url, json=data)
        print("success:", response.text)
    except Exception as e:
        print("send error:", e)

def update_sensor_data():
    humidity, temperature = Adafruit_DHT.read_retry(sensor, pin)
    doorStatus = GPIO.input(door_pin)
    doorStatus = 1 if doorStatus else 0

    lifespan, errorStatus = read_server_response('/home/bae/rhdahwjs/server_response.json')

    if humidity is not None and temperature is not None:
        if lifespan and errorStatus:
            print(f'Temperature={temperature:.1f}°C  Humidity={humidity:.1f}%  Door Status:{doorStatus} Lifespan:{lifespan}, errorStatus:{errorStatus} container:{container_number}')
        else:
            print(f'Temperature={temperature:.1f}°C  Humidity={humidity:.1f}%  Door Status:{doorStatus} container:{container_number}')

        send_sensor_data(temperature, humidity, doorStatus, lifespan, errorStatus, container_number)

while True:
    update_sensor_data()
    time.sleep(600)