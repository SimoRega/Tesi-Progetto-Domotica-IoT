#include "Scheduler.h"
#include "Arduino.h"
#include "WorkingTask.h"

Scheduler sched;

void setup() {

  pinMode(32, OUTPUT);

  sched.init(50);
  Serial.begin(9600);

  Task* t0 = new WorkingTask();
  sched.addTask(t0);
}

void loop() {
  sched.schedule();
}