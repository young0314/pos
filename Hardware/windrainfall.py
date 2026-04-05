import sys
from PyQt5.QtWidgets import QApplication, QWidget, QLabel, QVBoxLayout, QHBoxLayout
from PyQt5.QtGui import QPixmap
from PyQt5.QtCore import QTimer, Qt
import requests

class WindRainfallApp(QWidget):
    def __init__(self):
        super().__init__()

        self.initUI()

        self.timer = QTimer(self)
        self.timer.timeout.connect(self.update_weather)
        self.timer.start(1000)

        self.update_weather()

    def initUI(self):
        self.setWindowTitle('windrainfall')
        self.setGeometry(0, 0, 800, 480)

        self.label_rainfall = QLabel('rainfall: ')
        self.label_rainfall.setObjectName("rainfall_label")

        self.label_wind = QLabel('wind: ')
        self.label_wind.setObjectName("wind_label")

        self.image_rainfall_label = QLabel(self)
        self.image_rainfall_label.setObjectName("image_rainfall_label")
        self.image_rainfall_label.setAlignment(Qt.AlignCenter)
        self.image_rainfall_label.setMaximumSize(400, 400)

        self.image_wind_label = QLabel(self)
        self.image_wind_label.setObjectName("image_wind_label")
        self.image_wind_label.setAlignment(Qt.AlignCenter)
        self.image_wind_label.setMaximumSize(200, 200)

        image_rainfall_path = '/home/bae/jol/icon3/rainfall.png'
        image_wind_path = '/home/bae/jol/icon3/wind.png'

        pixmap_rainfall = QPixmap(image_rainfall_path)
        pixmap_rainfall = pixmap_rainfall.scaled(200, 200, Qt.KeepAspectRatio)
        self.image_rainfall_label.setPixmap(pixmap_rainfall)

        pixmap_wind = QPixmap(image_wind_path)
        pixmap_wind = pixmap_wind.scaled(200, 200, Qt.KeepAspectRatio)
        self.image_wind_label.setPixmap(pixmap_wind)

        text_layout = QVBoxLayout()
        text_layout.addWidget(self.label_rainfall, alignment=Qt.AlignCenter)
        text_layout.addWidget(self.label_wind, alignment=Qt.AlignCenter)

        image_layout = QVBoxLayout()
        image_layout.addWidget(self.image_rainfall_label, alignment=Qt.AlignCenter)
        image_layout.addWidget(self.image_wind_label, alignment=Qt.AlignCenter)

        main_layout = QHBoxLayout()
        main_layout.addLayout(image_layout)
        main_layout.addLayout(text_layout)

        self.setLayout(main_layout)

    def update_weather(self):
        api_key = '2254a08b2c794e0a2c7e72d1d52d4a5b'
        city = 'Goyang-si'
        url = f'http://api.openweathermap.org/data/2.5/weather?q={city}&appid={api_key}'

        try:
            response = requests.get(url)
            data = response.json()
            rainfall = data['rain']['1h'] if 'rain' in data else 0
            wind = data['wind']['speed'] if 'wind' in data else 0
            rainfall_str = f'{rainfall} mm'
            wind_str = f'{wind} m/s'

        except Exception as e:
            print(f'Error: {e}')
            rainfall_str = 'Error'
            wind_str = ''

        self.label_rainfall.setText(f'{rainfall_str}')
        self.label_wind.setText(f'{wind_str}')

if __name__ == '__main__':
    app = QApplication(sys.argv)

    with open("jolcss.css", "r") as f:
        app.setStyleSheet(f.read())

    wind_rainfall_app = WindRainfallApp()
    wind_rainfall_app.show()
    sys.exit(app.exec_())
