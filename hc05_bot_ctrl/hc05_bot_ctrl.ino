// D9   >>>  Rx
// D8   >>>  Tx
#include <SoftwareSerial.h>// import the serial library
SoftwareSerial Genotronex(8, 9); // RX, TX
int ledpin=13; // led on D13 will show blink on / off
int BluetoothData; // the data given from Computer
int rf=17,rb=16,lf=14,lb=15;//rf-right motor front,rb-right motor back,lf-left motor front,lb-left motor back
void setup() {
  // put your setup code here, to run once:
  Genotronex.begin(9600);
  Serial.begin(9600);
  Genotronex.println("Bluetooth On please press 1 or 0 blink LED ..");
  for(int i=10;i<18;i=i==11?14:i+1)
  pinMode(i,OUTPUT);
  analogWrite(10,255);
  analogWrite(11,255);
}

void loop() {
  // put your main code here, to run repeatedly:
   if (Genotronex.available()){
BluetoothData=Genotronex.read();
Serial.println(BluetoothData);
   if(BluetoothData=='1')
     {
       digitalWrite(lf,HIGH);
       digitalWrite(lb,LOW);
       Serial.println("lfr");
     }
     else if(BluetoothData=='2')
     {
       digitalWrite(lb,HIGH);
       digitalWrite(lf,LOW);
       Serial.println("lba");
     }
     else if(BluetoothData=='x')
     {
       digitalWrite(lf,LOW);
       digitalWrite(lb,LOW);
       Serial.println("lstop");
     }
   else if(BluetoothData=='3')
     {
       digitalWrite(rf,HIGH);
       digitalWrite(rb,LOW);
       Serial.println("rfr");
     }
     else if(BluetoothData=='4')
     {
       digitalWrite(rb,HIGH);
       digitalWrite(rf,LOW);
       Serial.println("rba");
     }
     else if(BluetoothData=='y')
     {
       digitalWrite(rf,LOW);
       digitalWrite(rb,LOW);
       Serial.println("rstop");
     }
   else if(BluetoothData=='p')
   {
     int s=0;
     BluetoothData=Genotronex.read();
     while(BluetoothData!=10)
     {
       s=s*10+BluetoothData-48;
       BluetoothData=Genotronex.read();
     }
     Serial.println(s);
     analogWrite(10,s);
     analogWrite(11,s);
   }
   }
delay(10);// prepare for next data ...
}
