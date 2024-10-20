# This file is part of a project using PyQt5 and RPi.GPIO.
# PyQt5 is licensed under the GPL or a commercial license.
# RPi.GPIO is licensed under the MIT License.

import sys
import RPi.GPIO as GPIO
from PyQt5.QtWidgets import QApplication, QWidget, QLabel, QVBoxLayout, QHBoxLayout
from PyQt5.QtGui import QPixmap
from PyQt5.QtCore import QTimer, Qt

class DOORApp(QWidget):
    def __init__(self):
        super().__init__()

        GPIO.setmode(GPIO.BCM)
        GPIO.setup(4, GPIO.IN, pull_up_down=GPIO.PUD_UP)

        self.pin = 4

        self.initUI()
        self.timer = QTimer(self)
        self.timer.timeout.connect(self.updateImage)
        self.timer.start(1000)

        self.updateImage()

    def initUI(self):
        self.setWindowTitle('DoorApp')
        self.setGeometry(0, 0, 800, 480)

        self.door_image_label = QLabel(self)
        self.door_image_label.setObjectName("door_image_label")
        self.door_image_label.setAlignment(Qt.AlignCenter)
        self.door_image_label.setMaximumSize(800, 800)

        image_layout = QVBoxLayout()
        image_layout.addWidget(self.door_image_label, alignment=Qt.AlignCenter)

        main_layout = QHBoxLayout()
        main_layout.addLayout(image_layout)

        self.setLayout(main_layout)

    def updateImage(self):
        if GPIO.input(self.pin) == 0:
            door_image_path = "/home/bae/jol/icon3/open.png"
        else:
            door_image_path = "/home/bae/jol/icon3/close.png"
        
        pixmap_door = QPixmap(door_image_path)
        pixmap_door = pixmap_door.scaled(500, 500, Qt.KeepAspectRatio)
        self.door_image_label.setPixmap(pixmap_door)

if __name__ == '__main__':
    app = QApplication(sys.argv)

    with open("/home/bae/jol/jolcss.css", "r") as f:
        app.setStyleSheet(f.read())

    door_app = DOORApp()
    door_app.show()
    sys.exit(app.exec_())
