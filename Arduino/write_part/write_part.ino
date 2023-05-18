/**
Binary encoding for communication:
 0 0       0 0         0 0       0 0     - 8 bits variable
SOURCE  DESTINATION  COMMAND   PAYLOAD

SOURCE
01 - ANDROID

10 - ARDUINO
00 - ERORR

COMMAND
00 - STATUS 
01 - GET_STATUS |
10 - LOCK       | + PAYLOAD 00 
11 - UNLOCK     |

STATUS + PAYLOAD  
10 => STATUS_LOCK
11 => STATUS_UNLOCK
*/

int ledPin = 7;
int touchPin = 8;
unsigned long previousMillis = 0;
unsigned long elapsedMillis = 0;
int debounceTime = 1000;
int ledState = LOW;

#define GET_STATUS B01100100
#define LOCK B01101000
#define UNLOCK B01101100
#define STATUS_LOCK B10010010
#define STATUS_UNLOCK B10010011


void setup() {
 Serial.begin(9600);
 pinMode(ledPin , OUTPUT);
 pinMode(touchPin , INPUT);
 digitalWrite(ledPin,LOW);
}

void loop() 
{
  elapsedMillis = millis() - previousMillis;

 if(digitalRead(touchPin)==HIGH && elapsedMillis > debounceTime)
  {
    if(ledState == HIGH)
    {
      ledState = LOW;
       Serial.println(STATUS_UNLOCK);
    }
    else
    {
      ledState = HIGH;
       Serial.println(STATUS_LOCK);
    }
    digitalWrite(ledPin,ledState);

    previousMillis = millis();
  }
  

  while (Serial.available() > 0)
  {
    static unsigned int message_pos = 0, message=0;
    int bitRead;
 
    char inByte = Serial.read();

    if (message_pos < 8)
    {
     
      bitRead = inByte  - '0';
      if(bitRead == 1)
      {
          message += 1<<(7-message_pos);
      }
      message_pos++;
    }
   
    if(message_pos == 8)
    {
        if((message & LOCK) == LOCK )
        {
            ledState = HIGH;
            digitalWrite(ledPin,HIGH);
        }
        if((message & UNLOCK) == UNLOCK)
        {
          ledState = LOW;
            digitalWrite(ledPin,LOW);
        }
        if((message & GET_STATUS) == GET_STATUS)
        {
          if(ledState == HIGH)
          {
             Serial.println(STATUS_LOCK);
          }
          else
          if(ledState == LOW)
          {
            Serial.println(STATUS_UNLOCK);
          }
        }
      message_pos = 0;
      message = 0;
    }
 
  }
 
}