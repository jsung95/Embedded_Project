# RaspberryPI 불쾌지수 선풍기

## ■ 구동영상


## ■ 불쾌지수란 ?
- 온도, 습도 등 여러 조건에서 인간이 느끼는 불쾌한 정도나 스트레스를 수치화 한 것
`
불쾌지수 계산 공식 : 0.81 x 온도 + 0.01 x 습도（0.99 x 온도 - 14.3）+ 46.3
`

|지수범위|단계|설명|
|------|---|---|
|80 이상|매우 높음|전원 불쾌감을 느낌|
|75 ~ 80 미만|높음|50% 정도 불쾌감을 느낌|
|68 ~ 75 미만|보통|불쾌감을 나타내기 시작|
|68 미만|낮음|전원 쾌적함을 느낌|

## ■ About Project ...

- ***MariaDB, 아파치 웹 서버, 안드로이드 스마트폰***과의 연동을 통해 임베디드시스템 구축
- 라즈베리파이 GPIO를 활용해, 온습도센서(DHT11)로 수집된 온도와 습도 값을 불쾌지수 계산 공식에 의거해 불쾌지수 단계에 따라 자동으로 모터 동작 및 LED 점등.
- 구현한 안드로이드 앱에서 블루투스 연동을 통해 단계별 DC모터의 회전속도(풍량 조절) 및 서보모터 조정(풍향 조정) 제어 기능 구현.
- 앱 내에서 수집된 온도 및 습도 값을 실시간으로 확인.

**MariaDB 및 아파치 웹 서버를 사용하는 이유**
- 수집된 온도 및 습도 값은 구현한 DB에 저장되며, 
- 안드로이드에서 DB에 있는 데이터를 직접적으로 수신할 수 없기 때문에 
- 아파치 웹 PHP 서버의 SQL 질의를 통해 MariaDB 데이터를 JSON 형식으로 가공해 안드로이드 앱으로 파싱.
</br>
<img src="/README_IMG/ex01.png" width="70%" height="auto">
</br>

**구동 영상**
<iframe width="740" height="416" src="https://www.youtube.com/embed/xrFvP-lgjZg" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
<details>
<summary><b>Show</b></summary>
<div markdown="1">

</br>
<figure>
    <div><b><i>■ Launch Application</i></b></div>
    
</figure>

</div>
</details>

## ■ Tech Stack

#### RaspberryPI GPIO
- 온습도 센서(DHT11)
- LED 모듈
- Servo 모터
#### DB
- MariaDB

#### Server
- Apache PHP Web Server

#### 사용 언어
- Python
- JAVA
- PHP
- SQL

#### 사용 툴
- VS Code
- Android Studio (API 28 based)
- Adobe XD
