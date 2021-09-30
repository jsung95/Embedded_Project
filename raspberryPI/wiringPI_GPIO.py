import time
import Adafruit_DHT #온습도센서 라이브러리
import pymysql #SQL라이브러리

from bluetooth import * #블루투스 라이브러리
from wiringpi import *
wiringPiSetupGpio()


sensor = Adafruit_DHT.DHT11
conn = pymysql.connect(host="localhost", user="sql_user", passwd="암호", db="sql_db") #sql 접근

dht_pin = 20 #온습도센서
leds = [2,3,4,17] #LED
for led in leds:
    pinMode(led, OUTPUT)

#DC모터
cw = 22 
pinMode(cw, OUTPUT)
softPwmCreate(cw, 0, 100)

#서보모터
servo = 18
pinMode(servo, PWM_OUTPUT)
pwmSetMode(PWM_MODE_MS)
pwmSetClock(192)
pwmSetRange(2000)
angle = 40

#블루투스 연결 설정
server_sock = BluetoothSocket(RFCOMM)
server_sock.bind(('',PORT_ANY))
server_sock.listen(1)
uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"
advertise_service(server_sock, "BT", service_id = uuid, service_classes = [uuid, SERIAL_PORT_CLASS], profiles = [SERIAL_PORT_PROFILE])
port = server_sock.getsockname()[1]

print("Waiting bluetooth connect")

client_sock, client_info = server_sock.accept()
print("connected with -> ", client_info)
num = 1
try:
    with conn.cursor() as cur :
        sql = "insert into collect_data values(%s, %s, %s, %s)"
        while True :
            data = client_sock.recv(1024) #블루투스 입력 대기
            if(data[0] == 109): # ASCII "109" 입력받았을 시에 함수 동작
                print("[Start Auto Mode!!]")
                count = 1
                while (count <= 10):
                    humidity, temperature = Adafruit_DHT.read_retry(sensor, dht_pin) #온습도 변수 선언
                    if humidity is not None and temperature is not None:
                        result = (0.81*temperature)+0.01*humidity*(0.99*temperature-14.3)+46.3 #불쾌지수 공식
                        print('%d]\tTemp = %0.1f*C / Hum = %0.1f%% / result = %0.1f'%(num, temperature, humidity, result))
		
		# SQL_ MariaDB 데이터 삽입
                        cur.execute(sql, ('DHT11', time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()), temperature, humidity)) 
                        conn.commit()

		#불쾌지수 값에 따라 동작
                        if(result >= 80): #불쾌지수 값 80 이상
                            softPwmWrite(cw, 100) #모터 100% 속도
                            for led in leds: 
                                digitalWrite(led, HIGH) #모든 LED ON
                        elif(result >= 75 and result < 80): #불쾌지수 값 75~79
                            softPwmWrite(cw, 50) #모터 속도 50%
                            for led in leds:
                                digitalWrite(led, LOW)
                            digitalWrite(leds[0], HIGH)
                            digitalWrite(leds[1], HIGH)
                            digitalWrite(leds[2], HIGH) #LED 3개 ON
                        elif(result >= 68 and result < 75): #불쾌지수 값 68~74
                            softPwmWrite(cw, 15) #모터 속도 15%
                            for led in leds:
                                digitalWrite(led, LOW)
                            digitalWrite(leds[0], HIGH)
                            digitalWrite(leds[1], HIGH) #LED 2개 ON
                        else: #불쾌지수 값 68 미만
                            softPwmWrite(cw, 0) #모터 off
                            for led in leds:
                                digitalWrite(led, LOW)
                            digitalWrite(leds[0], HIGH) #LED 1개 ON

                    else:
                        print("fail")

                    
                    count += 1
                    num += 1
                    delay(1000)
                
	# 블루투스 ASCII 값 수신 대기
            elif(data[0] == 48):#0
                softPwmWrite(cw, 0)
                for led in leds:
                    digitalWrite(led, LOW)
                digitalWrite(leds[0], HIGH)

            elif(data[0] == 49):#1
                softPwmWrite(cw, 15)
                for led in leds:
                    digitalWrite(led, LOW)
                digitalWrite(leds[0], HIGH)
                digitalWrite(leds[1], HIGH)

            elif(data[0] == 50):#2
                softPwmWrite(cw, 50)
                for led in leds:
                    digitalWrite(led, LOW)
                digitalWrite(leds[0], HIGH)
                digitalWrite(leds[1], HIGH)
                digitalWrite(leds[2], HIGH)
                
            elif(data[0] == 51):#3
                softPwmWrite(cw, 100)
                for led in leds:
                    digitalWrite(led, LOW)
                digitalWrite(leds[0], HIGH)
                digitalWrite(leds[1], HIGH)
                digitalWrite(leds[2], HIGH)
                digitalWrite(leds[3], HIGH)

            elif(data[0] == 52): #4 <<LEFT 
                pwmWrite(servo, angle)
                angle -= 10
                if(angle < 40):
                    angle = 40
            
            elif(data[0] == 53): #5 >>RIGHT
                pwmWrite(servo, angle)
                angle += 10
                if(angle > 250):
                    angle = 250

except IOError:
    print("disconnected")
    client_sock.close()
    server_sock.close()
    softPwmWrite(cw, 0)
    for led in leds:
        digitalWrite(led, LOW)
    pwmWrite(servo, 0)
    exit()

except KeyboardInterrupt:
    print("disconnected")
    client_sock.close()
    server_sock.close()
    softPwmWrite(cw, 0)
    for led in leds:
        digitalWrite(led, LOW)
    pwmWrite(servo, 0)
    exit()

finally:
    conn.close()