# This file is part of a project using PyQt, Adafruit_DHT, and RPi.GPIO.
# PyQt is licensed under the GPL or a commercial license.
# Adafruit_DHT and RPi.GPIO are licensed under the MIT License.

from PyQt5.QtWidgets import QWidget, QVBoxLayout, QLabel, QHBoxLayout, QApplication
from PyQt5.QtGui import QPixmap
from PyQt5.QtCore import QTimer, Qt
import Adafruit_DHT
import sys
import RPi.GPIO as GPIO

class SensorApp(QWidget):
    def __init__(self):
        super().__init__()

        self.initUI()

        self.timer = QTimer(self)
        self.timer.timeout.connect(self.update_sensor_data)
        self.timer.start(1000)

        self.fan_pin = 14
        GPIO.setmode(GPIO.BCM)
        GPIO.setup(self.fan_pin, GPIO.OUT)
        self.fan_status = False

    def initUI(self):
        self.setWindowTitle('dht11')
        self.setGeometry(0, 0, 800, 480)

        self.label_temperature = QLabel('Temperature: ')
        self.label_temperature.setObjectName("temperature_label")

        self.label_humidity = QLabel('Humidity: ')
        self.label_humidity.setObjectName("humidity_label")

        self.image_temperature_label = QLabel(self)
        self.image_temperature_label.setObjectName("image_temperature_label")
        self.image_temperature_label.setAlignment(Qt.AlignCenter)
        self.image_temperature_label.setMaximumSize(200, 200)

        self.image_humidity_label = QLabel(self)
        self.image_humidity_label.setObjectName("image_humidity_label")
        self.image_humidity_label.setAlignment(Qt.AlignCenter)
        self.image_humidity_label.setMaximumSize(200, 200)

        temperature_image_path = "/home/bae/jol/icon3/temp.png"
        humidity_image_path = "/home/bae/jol/icon3/hudi.png"

        pixmap_temperature = QPixmap(temperature_image_path)
        pixmap_temperature = pixmap_temperature.scaled(200, 200, Qt.KeepAspectRatio)
        self.image_temperature_label.setPixmap(pixmap_temperature)

        pixmap_humidity = QPixmap(humidity_image_path)
        pixmap_humidity = pixmap_humidity.scaled(200, 200, Qt.KeepAspectRatio)
        self.image_humidity_label.setPixmap(pixmap_humidity)

        text_layout = QVBoxLayout()
        text_layout.addWidget(self.label_temperature, alignment=Qt.AlignCenter)
        text_layout.addWidget(self.label_humidity, alignment=Qt.AlignCenter)

        image_layout = QVBoxLayout()
        image_layout.addWidget(self.image_temperature_label, alignment=Qt.AlignCenter)
        image_layout.addWidget(self.image_humidity_label, alignment=Qt.AlignCenter)

        main_layout = QHBoxLayout()
        main_layout.addLayout(image_layout)
        main_layout.addLayout(text_layout)

        self.setLayout(main_layout)

    def update_sensor_data(self):
        sensor = Adafruit_DHT.DHT11
        pin = 17

        humidity, temperature = Adafruit_DHT.read_retry(sensor, pin)

        if humidity is not None and temperature is not None:
            self.label_temperature.setText(f'{int(temperature)} °C')
            self.label_humidity.setText(f'{int(humidity)} %')

            if temperature >= 19 and not self.fan_status:
                GPIO.output(self.fan_pin, GPIO.HIGH)
                self.fan_status = True
            elif temperature < 19 and self.fan_status:
                GPIO.output(self.fan_pin, GPIO.LOW)
                self.fan_status = False
        else:
            self.label_temperature.setText('Failed to read sensor data')
            self.label_humidity.setText('')

if __name__ == "__main__":
    app = QApplication(sys.argv)

    with open("jolcss.css", "r") as f:
        app.setStyleSheet(f.read())

    window = SensorApp()
    window.show()
    sys.exit(app.exec_())
