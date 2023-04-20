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
const unsigned int MAX_MESSAGE_LENGTH = 9;
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
  //if sensor is touched and the elapsed time is bigger than the given debounce time
  
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

     //store the current millis on the previous millis
    previousMillis = millis();
  }
  
  //Check to see if anything is available in the serial receive buffer
  while (Serial.available() > 0)
  {
    //Create a place to hold the incoming message
    static char stringMessage[MAX_MESSAGE_LENGTH];
    static unsigned int message_pos = 0, message=0;
    int bitReade;
 //calculate the elapsed seconds
    //Read the next available byte in the serial receive buffer
    char inByte = Serial.read();

    //Message coming in (check not terminating character) and guard for over message size
    if (message_pos < 8)
    {
      //Add the incoming byte to our message
      stringMessage[message_pos] = inByte;
      bitReade = inByte  - '0';
      if(bitReade == 1)
      {
          message += 1<<(7-message_pos);
      }
      message_pos++;
    }
    //Full message received...
    if(message_pos == 8)
    {
        if((message & LOCK) == LOCK )
        {
            digitalWrite(ledPin,HIGH);
            
           
            // Serial.println("Led is turned on\n");
        }
        if((message & UNLOCK) == UNLOCK)
        {
            digitalWrite(ledPin,LOW);
            // Serial.println("Led is turned off\n");
           
        }
        if((message & GET_STATUS) == GET_STATUS)
        {
          if(digitalRead(ledPin)==HIGH)
          {
             Serial.println(STATUS_LOCK);
          }
          else
          if(digitalRead(ledPin)==LOW)
          {
            Serial.println(STATUS_UNLOCK);
          }
        }
      //Reset for the next message
      message_pos = 0;
      message = 0;
    }
 
  }
 
}