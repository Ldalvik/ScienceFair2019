#include <SoftwareSerial.h>
#define L1 9
#define L2 10
#define R1 3
#define R2 6
#define bluetoothTx 12
#define bluetoothRx 13
#define LED1 8
#define LED2 14
#define RGB1 11
#define RGB2 5

String content = "";
int trimLeft;
int trimRight;
bool isConnected = false;
SoftwareSerial bluetooth(bluetoothTx, bluetoothRx);

void setup()
{
  pinMode(L1, OUTPUT);
  pinMode(L2, OUTPUT);
  pinMode(R1, OUTPUT);
  pinMode(R2, OUTPUT);
  pinMode(LED1, OUTPUT);
  pinMode(RGB1, OUTPUT);
  pinMode(RGB2, OUTPUT);
  Serial.begin(57600);
  bluetooth.begin(57600);
  rgb(0, 255);
  headlights(LOW);
  delay(50);
  rgb(0, 0);
  headlights(HIGH);
  delay(100);
  rgb(255, 0);
  headlights(LOW);
  delay(50);
  rgb(0, 0);
  headlights(HIGH);
  delay(100);
  rgb(0, 255);
  headlights(LOW);
  delay(50);
  rgb(0, 0);
  headlights(HIGH);
  delay(100);
  rgb(255, 0);
  headlights(LOW);
  delay(50);
  rgb(0, 0);
  headlights(HIGH);
  delay(50);
  rgb(0, 255);
  headlights(LOW);
  delay(50);
  rgb(0, 0);
  headlights(HIGH);
  delay(50);
  rgb(255, 0);
  headlights(LOW);
}

void loop()
{
  /*if (isConnected == false) {
    rgb(255, 0);
    delay(500);
    rgb(0, 0);
    }*/
  if (bluetooth.available()) {
    isConnected = true;
    char character = (char) bluetooth.read();
    //Serial.println(character);
    if (character == '/') {
      Serial.println(content);
      if (strstr(content.c_str(), "trim")) {
        motorTrim(content);
      } else if (strstr(content.c_str(), "rgb")) {
        int equal = content.indexOf("=") + 1;
        content.remove(0, equal);
        int r = split(content, ',', 0).toInt();
        int g = split(content, ',', 1).toInt();
        rgb(r, g);
      } else if (content == "lightsOn") {
        headlights(HIGH);
      } else if (content == "lightsOff") {
        headlights(LOW);
      } else {
        motors(content);
      }
      content = "";
    } else {
      content.concat(character);
    }
  }
}

void motors(String content) {
  int left = split(content, ',', 0).toInt();
  int right = split(content, ',', 1).toInt();
  rightWheels(right);
  leftWheels(left);
}

void motorTrim(String content) {
  if (content == "trimLeft+") {
    trimLeft++;
  }
  if (content == "trimLeft-") {
    trimLeft--;
  }
  if (content == "trimRight+") {
    trimRight++;
  }
  if (content == "trimRight-") {
    trimRight--;
  }
  if (content == "trimLeftReset") {
    trimLeft = 0;
  }
  if (content == "trimRightReset") {
    trimRight = 0;
  }
}

void rightWheels(int rightWheel) {
  if (rightWheel >= 250) {
    analogWrite(R1, rightWheel - 250 + trimRight);
    digitalWrite(R2, LOW);
  }
  if (rightWheel < 250) {
    digitalWrite(R1, LOW);
    analogWrite(R2, 250 - rightWheel + trimRight);
  }
}

void leftWheels(int leftWheel) {
  if (leftWheel >= 250) {
    analogWrite(L1, leftWheel - 250 + trimLeft);
    digitalWrite(L2, LOW);
  }
  if (leftWheel < 250) {
    digitalWrite(L1, LOW);
    analogWrite(L2, 250 - leftWheel + trimLeft);
  }
}

void rgb(int r, int g) {
  analogWrite(RGB1, r);
  analogWrite(RGB2, g);
}

void headlights(int mode) {
  digitalWrite(LED1, mode);
  digitalWrite(LED2, mode);
}

String split(String data, char separator, int index) {
  int found = 0;
  int strIndex[] = {0, -1};
  int maxIndex = data.length() - 1;

  for (int i = 0; i <= maxIndex && found <= index; i++) {
    if (data.charAt(i) == separator || i == maxIndex) {
      found++;
      strIndex[0] = strIndex[1] + 1;
      strIndex[1] = (i == maxIndex) ? i + 1 : i;
    }
  }

  return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}
