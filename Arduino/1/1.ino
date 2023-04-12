const unsigned int MAX_MESSAGE_LENGTH = 9;
int ledPin = 13;
void setup() {
 Serial.begin(9600);
 digitalWrite(ledPin,LOW);
}

void loop() {

 //Check to see if anything is available in the serial receive buffer
 while (Serial.available() > 0)
 {
   //Create a place to hold the incoming message
   static char message[MAX_MESSAGE_LENGTH];
   static unsigned int message_pos = 0;

   //Read the next available byte in the serial receive buffer
   char inByte = Serial.read();
    Serial.println(inByte);
   //Message coming in (check not terminating character) and guard for over message size
   if (message_pos < 8)
   {
     //Add the incoming byte to our message
     message[message_pos] = inByte;
     message_pos++;
   }
   //Full message received...
   if(message_pos == 8)
   {
     //Add null character to string
     //message[message_pos] = '\0';

     //Print the message (or do other things)
     Serial.println(message);
     Serial.println("nimic");
      if(strcmp(message,"01101000") == 0)
       {
          digitalWrite(ledPin,HIGH);
       }
      if(strcmp(message,"01101100") == 0)
      {
          digitalWrite(ledPin,LOW);
      }

     //Reset for the next message
     message_pos = 0;
   }
  
 }
}