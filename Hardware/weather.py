import sys
import os
import requests
from PyQt5.QtWidgets import QApplication, QWidget, QLabel, QVBoxLayout, QHBoxLayout
from PyQt5.QtGui import QPixmap
from PyQt5.QtCore import QTimer, Qt

class WeatherApp(QWidget):
    def __init__(self):
        super().__init__()

        self.initUI()

        self.timer = QTimer(self)
        self.timer.timeout.connect(self.updateWeather)
        self.timer.start(1000)
        
        self.updateWeather()

    def initUI(self):
        self.setWindowTitle('Weather App')
        self.setGeometry(0, 0, 800, 480)

        self.image_label = QLabel(self)
        self.image_label.setObjectName("imageweather_label")
        self.image_label.setAlignment(Qt.AlignCenter)
        self.image_label.setMaximumSize(400, 400)

        self.temperature_label = QLabel(self)
        self.temperature_label.setObjectName("temperatureweather_label")
        self.temperature_label.setAlignment(Qt.AlignCenter)

        text_layout = QVBoxLayout()
        text_layout.addWidget(self.temperature_label, alignment=Qt.AlignCenter)

        image_layout = QVBoxLayout()
        image_layout.addWidget(self.image_label, alignment=Qt.AlignCenter)

        main_layout = QHBoxLayout()
        main_layout.addLayout(image_layout)
        main_layout.addLayout(text_layout)

        self.setLayout(main_layout)

    def updateWeather(self):
        api_key = '2254a08b2c794e0a2c7e72d1d52d4a5b'
        city = 'Goyang-si'
        url = f'http://api.openweathermap.org/data/2.5/weather?q={city}&appid={api_key}'

        try:
            response = requests.get(url)
            data = response.json()
            temperature_kelvin = data['main']['temp']
            temperature_celsius = temperature_kelvin - 273.15
            weather_description = data['weather'][0]['description']

            if 'rain' in weather_description.lower():
                image_path = '/home/bae/jol/icon3/rain.png'
            elif 'cloud' in weather_description.lower():
                image_path = '/home/bae/jol/icon3/cloud.png'
            elif 'snow' in weather_description.lower():
                image_path = '/home/bae/jol/icon3/snow.png'
            else:
                image_path = '/home/bae/jol/icon3/sun.png'

            if os.path.isfile(image_path):
                pixmap = QPixmap(image_path)
                pixmap = pixmap.scaled(400, 400, Qt.KeepAspectRatio)
                self.image_label.setPixmap(pixmap)
                self.image_label.setAlignment(Qt.AlignCenter)
            else:
                print(f'Error: Image file not found - {image_path}')

            self.temperature_label.setText(f'{temperature_celsius:.2f}°C')

        except Exception as e:
            print(f'Error: {e}')


if __name__ == '__main__':
    app = QApplication(sys.argv)

    with open("jolcss.css", "r") as f:
        app.setStyleSheet(f.read())

    weather_app = WeatherApp()
    weather_app.show()
    sys.exit(app.exec_())
