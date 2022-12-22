#include "IdleTask.h"
#include "Arduino.h"

void IdleTask::tick(){
  Serial.println("Idle");
}