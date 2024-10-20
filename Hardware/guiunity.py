# This file is part of a project using PyQt5 and various sensor modules.
# PyQt5 is licensed under the GPL or a commercial license.
# If any of these modules use open-source licenses, their respective licenses must be followed.
# This project also uses the 'requests' library, which is licensed under the Apache License 2.0.
# See: http://www.apache.org/licenses/LICENSE-2.0

import sys
import os
import requests
from PyQt5.QtWidgets import QApplication, QWidget, QVBoxLayout, QStackedWidget
from PyQt5.QtCore import QTimer, Qt
from PyQt5.QtGui import QPixmap
from weather import WeatherApp
from windrainfall import WindRainfallApp
from dht11 import SensorApp
from gps import GPSApp
from dht11data import dht11dataApp
from door import DOORApp

class AppSlider(QWidget):
    def __init__(self):
        super().__init__()

        self.initUI()

        self.timer = QTimer(self)
        self.timer.timeout.connect(self.showNextApp)
        self.timer.timeout.connect(self.captureAndSendImage)
        self.timer.start(3000)

    def initUI(self):
        self.setWindowTitle('넘어가유')
        self.setGeometry(0, 0, 800, 480)

        self.stacked_widget = QStackedWidget(self)
        self.stacked_widget.addWidget(WeatherApp())
        self.stacked_widget.addWidget(WindRainfallApp())
        self.stacked_widget.addWidget(SensorApp())
        self.stacked_widget.addWidget(GPSApp())
        self.stacked_widget.addWidget(dht11dataApp())
        self.stacked_widget.addWidget(DOORApp())

        layout = QVBoxLayout()
        layout.addWidget(self.stacked_widget)
        self.setLayout(layout)

        self.current_index = 0

        with open("/home/bae/jol/jolcss.css", "r") as f:
            self.setStyleSheet(f.read())

    def showNextApp(self):
        self.current_index = (self.current_index + 1) % self.stacked_widget.count()
        self.stacked_widget.setCurrentIndex(self.current_index)

    def captureAndSendImage(self):
        pixmap = self.grab()
        pixmap.save("guicap.jpg")

        files = {'file': open('guicap.jpg', 'rb')}
        url = 'http://192.168.137.113:8080/guipost'
        response = requests.post(url, files=files)

if __name__ == '__main__':
    app = QApplication(sys.argv)

    app_slider = AppSlider()
    app_slider.show()

    sys.exit(app.exec_())
