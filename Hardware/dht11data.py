import sys
import requests
from PyQt5.QtWidgets import QWidget, QVBoxLayout, QLabel, QHBoxLayout, QApplication
from PyQt5.QtGui import QPixmap, QFont
from PyQt5.QtCore import QTimer, Qt
import Adafruit_DHT
import threading
import json

class dht11dataApp(QWidget):
    def __init__(self):
        super().__init__()

        self.initUI()
        self.timer = QTimer(self)
        self.timer.timeout.connect(self.update_sensor_data)
        self.timer.start(10000)

    def initUI(self):
        self.setWindowTitle('datadht11')
        self.setGeometry(0, 0, 800, 480)

        font = QFont("Arial", 70)

        self.label_temperature = QLabel(self)
        self.label_temperature.setFont(font)
        self.label_humidity = QLabel(self)
        self.label_humidity.setFont(font)
        self.label_errordata = QLabel(self)
        self.label_errordata.setFont(font)
        self.label_lifespan = QLabel(self)
        self.label_lifespan.setFont(font)

        self.image_errordata_label = QLabel(self)
        self.image_errordata_label.setObjectName("image_errordata_label")
        self.image_errordata_label.setAlignment(Qt.AlignCenter)
        self.image_errordata_label.setMaximumSize(400, 400)

        errordata_image_path = "/home/bae/jol/icon3/ok.png"

        pixmap_errordata = QPixmap(errordata_image_path)
        pixmap_errordata = pixmap_errordata.scaled(400, 400, Qt.KeepAspectRatio)
        self.image_errordata_label.setPixmap(pixmap_errordata)

        image_layout = QVBoxLayout()
        image_layout.addWidget(self.image_errordata_label, alignment=Qt.AlignCenter)

        text_layout = QVBoxLayout()
        text_layout.addWidget(self.label_lifespan, alignment=Qt.AlignCenter)

        main_layout = QHBoxLayout()
        main_layout.addLayout(image_layout)
        main_layout.addLayout(text_layout)

        self.setLayout(main_layout)

        self.current_index = 0

        with open("/home/bae/jol/jolcss.css", "r") as f:
            self.setStyleSheet(f.read())

    def update_sensor_data(self):
        sensor = Adafruit_DHT.DHT11
        pin = 17

        humidity, temperature = Adafruit_DHT.read_retry(sensor, pin)

        if humidity is not None and temperature is not None:
            print(f'Temperature: {temperature}°C Humidity: {humidity}%')

            if temperature >= 25:
                error_name = 1
                post_url = "http://192.168.137.172:8080/rep_error_name"
                data = {'error_number': error_name}
                response = requests.post(post_url, json=data)
                if response.status_code == 200:
                    print("post success")
                    server_response = response.json()
                    print("Server response:", server_response)

                    with open("server_response.json", "w") as f:
                        json.dump(server_response, f)

                    prediction_text = server_response.get('prediction', '')
                    error_type = None
                    lifespan = None
                    try:
                        lifespan = prediction_text.split('수명:')[1].split(',')[0].strip()
                        error_type = int(float(prediction_text.split('고장유형:')[1].strip()))
                    except (IndexError, ValueError) as e:
                        print("Error parsing prediction:", e)

                    if error_type is not None:
                        error_image_path = f"/home/bae/jol/icon3/error{error_type}.png"
                        pixmap_error = QPixmap(error_image_path)
                        pixmap_error = pixmap_error.scaled(400, 400, Qt.KeepAspectRatio)
                        self.image_errordata_label.setPixmap(pixmap_error)
                        if lifespan is not None:
                            self.label_lifespan.setText(f'Life\n{lifespan}')
                        else:
                            self.label_lifespan.setText('Normal')
                    else:
                        print("Error: Prediction value not found in server response")
                else:
                    print("post error")
            else:
                ok_image_path = "/home/bae/jol/icon3/ok.png"
                pixmap_ok = QPixmap(ok_image_path)
                pixmap_ok = pixmap_ok.scaled(400, 400, Qt.KeepAspectRatio)
                self.image_errordata_label.setPixmap(pixmap_ok)
                self.label_lifespan.setText('Normal')
        else:
            print('Failed to read sensor data')
            self.label_errordata.setText('Failed to read sensor data')
            self.label_humidity.setText('')

if __name__ == "__main__":
    app_pyqt = QApplication(sys.argv)
    window = dht11dataApp()
    window.show()

    update_thread = threading.Thread(target=window.update_sensor_data)
    update_thread.start()

    sys.exit(app_pyqt.exec_())
