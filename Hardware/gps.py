import requests
import random
import sys
from PyQt5.QtWidgets import QApplication, QWidget, QLabel, QVBoxLayout, QHBoxLayout
from PyQt5.QtGui import QPixmap
from PyQt5.QtCore import QTimer, Qt

class GPSApp(QWidget):
    def __init__(self):
        super().__init__()

        self.initUI()

        self.timer = QTimer(self)
        self.timer.timeout.connect(self.updateGPS)
        self.timer.start(1000)

        self.updateGPS()

    def initUI(self):
        self.setWindowTitle('GPS App')
        self.setGeometry(0, 0, 800, 480)

        self.latitude_label = QLabel('Latitude: ')
        self.latitude_label.setObjectName("latitude_label")

        self.longitude_label = QLabel('Longitude: ')
        self.longitude_label.setObjectName("longitude_label")

        self.image_gps_label = QLabel(self)
        self.image_gps_label.setObjectName("image_gps_label")
        self.image_gps_label.setAlignment(Qt.AlignCenter)
        self.image_gps_label.setMaximumSize(400, 400)

        image_gps_path = '/home/bae/jol/icon3/gps2.png'

        pixmap_gps = QPixmap(image_gps_path)
        pixmap_gps = pixmap_gps.scaled(200, 200, Qt.KeepAspectRatio)
        self.image_gps_label.setPixmap(pixmap_gps)

        text_layout = QVBoxLayout()
        text_layout.addWidget(self.latitude_label, alignment=Qt.AlignCenter)
        text_layout.addWidget(self.longitude_label, alignment=Qt.AlignCenter)

        image_layout = QVBoxLayout()
        image_layout.addWidget(self.image_gps_label, alignment=Qt.AlignCenter)

        main_layout = QHBoxLayout()
        main_layout.addLayout(image_layout)
        main_layout.addLayout(text_layout)

        self.setLayout(main_layout)

    def updateGPS(self):
        ssid = "KT_GiGA_5G_2F83"
        latitude, longitude = estimate_location(ssid, current_latitude, current_longitude)
        if latitude is not None and longitude is not None:
            self.latitude_label.setText(f'{latitude:.6f}')
            self.longitude_label.setText(f'{longitude:.6f}')
        else:
            self.latitude_label.setText(f'{ssid} network error')
            self.longitude_label.setText('')

def estimate_location(ssid, current_latitude, current_longitude):
    try:
        url = f'https://location.services.mozilla.com/v1/geolocate?key=test'
        payload = {
            "wifiAccessPoints": [
                {
                    "macAddress": "94-E7-0B-0D-AA-CF",
                    "ssid": ssid,
                    "signalStrength": -30
                }
            ]
        }
        response = requests.post(url, json=payload)
        data = response.json()
        location = data.get('location')
        if location:
            latitude = location['lat']
            longitude = location['lng']
            
            corrected_latitude, corrected_longitude = correct_location(latitude, longitude, current_latitude, current_longitude)
            
            return corrected_latitude, corrected_longitude
        else:
            return None, None
    except Exception as e:
        print("error:", e)
        return None, None

def correct_location(latitude, longitude, current_latitude, current_longitude):
    delta_lat = latitude - current_latitude
    delta_lng = longitude - current_longitude
    
    corrected_latitude = latitude - delta_lat
    corrected_longitude = longitude - delta_lng
    
    return corrected_latitude, corrected_longitude

current_latitude = 37.71354524397209
current_longitude = 126.8900230533711

if __name__ == '__main__':
    app = QApplication(sys.argv)

    with open("/home/bae/jol/jolcss.css", "r") as f:
        app.setStyleSheet(f.read())

    gps_app = GPSApp()
    gps_app.show()
    sys.exit(app.exec_())
