#include "Scheduler.h"
#include "Arduino.h"
#include "WorkingTask.h"
#include "IdleTask.h"
#include "CommTask.h"

Scheduler sched;

void setup() {

  pinMode(32, OUTPUT);

  sched.init(50);
  Serial.begin(9600);

  Task* t0 = new WorkingTask();
  sched.addTask(t0);

  Task* t1 = new IdleTask();
  sched.addTask(t1);

  Task* t2 = new CommTask();
  sched.addTask(t2);
}

void loop() {
  sched.schedule();
}