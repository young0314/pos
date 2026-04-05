import sys
from PyQt5.QtWidgets import QApplication, QWidget, QVBoxLayout, QStackedWidget
from PyQt5.QtCore import QTimer
from weather import WeatherApp
from windrainfall import WindRainfallApp
from dht11 import SensorApp
#from gps import GPSApp
from door import DOORApp
from dht11data import dht11dataApp

class AppSlider(QWidget):
    def __init__(self):
        super().__init__()

        self.initUI()

        self.timer = QTimer(self)
        self.timer.timeout.connect(self.showNextApp)
        self.timer.start(5000)

    def initUI(self):
        self.setWindowTitle('넘어가유')
        self.setGeometry(0, 0, 1450, 850)

        self.stacked_widget = QStackedWidget(self)
        self.stacked_widget.addWidget(WeatherApp())
        self.stacked_widget.addWidget(WindRainfallApp())
        self.stacked_widget.addWidget(SensorApp())
        #self.stacked_widget.addWidget(GPSApp())
        self.stacked_widget.addWidget(DOORApp())
        self.stacked_widget.addWidget(dht11dataApp())

        layout = QVBoxLayout()
        layout.addWidget(self.stacked_widget)
        self.setLayout(layout)

        self.current_index = 0

        with open("/home/bae/jol/jolcss.css", "r") as f:
            self.setStyleSheet(f.read())

    def showNextApp(self):
        self.current_index = (self.current_index + 1) % self.stacked_widget.count()
        self.stacked_widget.setCurrentIndex(self.current_index)

if __name__ == '__main__':
    app = QApplication(sys.argv)

    app_slider = AppSlider()
    app_slider.show()

    sys.exit(app.exec_())
