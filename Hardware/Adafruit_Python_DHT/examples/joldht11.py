import time
import Adafruit_DHT
sensor = Adafruit_DHT.DHT11
pin = 4
try:
    while True :
        h, t = Adafruit_DHT.read_retry(sensor, pin)
        if h is not None and t is not None :
            print("Temperature = {0:0.1f}*C Humidity = {1:0.1f}%".format(t, h))
        else :
            print('error')
        time.sleep(3)
except KeyboardInterrupt:
    print("hahahaha")