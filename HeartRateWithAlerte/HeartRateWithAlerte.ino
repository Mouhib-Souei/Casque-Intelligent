#include <Wire.h>
#include "MAX30105.h"
#include "heartRate.h"
#include <BluetoothSerial.h>

#define SDA_PIN 26
#define SCL_PIN 25
#define BUZZER_PIN 22 // pin number for the buzzer



BluetoothSerial SerialBT;

MAX30105 particleSensor;

const byte RATE_SIZE = 4;
byte rates[RATE_SIZE];
byte rateSpot = 0;
long lastBeat = 0;

float beatsPerMinute;
int beatAvg;

unsigned long previousMillis = 0; //variable to store last time data was sent
const long interval = 15000; //interval at which to send data (in milliseconds)

void setup()
{
  Serial.begin(115200);
  SerialBT.begin("ESP32test"); //Name of the Bluetooth device
  Wire.begin(SDA_PIN, SCL_PIN);
  pinMode(BUZZER_PIN, OUTPUT); // set buzzer pin as output
  

  if (!particleSensor.begin(Wire, I2C_SPEED_FAST)) {
    Serial.println("MAX30105 was not found. Please check wiring/power. ");
    while (1);
  }
  Serial.println("Place your index finger on the sensor with steady pressure.");

  particleSensor.setup();
  particleSensor.setPulseAmplitudeRed(0x0A);
  particleSensor.setPulseAmplitudeGreen(0);
}

void loop()
{
  long irValue = particleSensor.getIR();

  if (checkForBeat(irValue) == true) {
    long delta = millis() - lastBeat;
    lastBeat = millis();

    beatsPerMinute = 60 / (delta / 1000.0);

    if (beatsPerMinute < 255 && beatsPerMinute > 20) {
      rates[rateSpot++] = (byte)beatsPerMinute;
      rateSpot %= RATE_SIZE;

      
      for (byte x = 0 ; x < RATE_SIZE ; x++)
        beatAvg += rates[x];
      beatAvg /= RATE_SIZE;

      // check if BPM value is greater than 90, if yes, then turn on the buzzer
      if (beatAvg > 99 ) {
        digitalWrite(BUZZER_PIN, HIGH);
     
        delay(10000);
        
      }
    }
  } else {
    digitalWrite(BUZZER_PIN, LOW); // turn off the buzzer if there is no beat detected
     
  }

  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) {
    previousMillis = currentMillis;

    SerialBT.print("IR=");
    SerialBT.print(irValue);
    SerialBT.print(", BPM=");
    SerialBT.print(beatsPerMinute);
    SerialBT.print(", Avg BPM=");
    SerialBT.print(beatAvg);

    if (irValue < 50000)
      SerialBT.print(" No finger ?");

    SerialBT.println();
  }
}
