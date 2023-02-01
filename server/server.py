from flask import Flask
from flask import request
import json
from enum import Enum
import threading 
import datetime
import requests
import time
import ast

app = Flask(__name__)

class SystemState(Enum):
    WORKING = 0 #System is normally working, manage comms & data elaborations
    IDLE = 1 #System in energy saving mode,  just manage comms
    ERROR = 2 #Somethings broke normal workflow or some components went in error


class myHouse():
  def __init__(self) -> None:
     self.myData = dict(generalState=0, sensors=dict(lux=0,hum=0,tmp=0), modules=[], devices=[], timestamp=datetime.datetime.now())
     '''
     modules{  ------------> IoT modules that communicates & sends data
      modName="",
      modIp="",
      modState="",
      modPower=""
      type="" -------------> Can be : {"SmartModule", "SolarModule"}
     }

     devices{ -------------> Simple devices or house forniture that can be controlled
      devName="",
      devIp="",
      devState="",
      devPower="",
      type=""  -----------> Can be : {"boiler", "shutter", "switch", "dimmer"}
     }
     '''
     self.energyData = dict(e_In=0, e_Out=0, e_Saved=0)
    
  def changeState(self, newState):
    self.myData["generalState"]=newState
    currentState=newState

  def getData(self):
    return self.myData #json.dumps(self.myData, indent=4)

  def getEnergyData(self):
    return self.energyData

  def updateDataSensors(self,newLux,newHum,newTmp):
    if newLux!=None : self.myData["sensors"]["lux"] = newLux
    if newHum!=None : self.myData["sensors"]["hum"] = newHum
    if newTmp!=None : self.myData["sensors"]["tmp"] = newTmp

  def addNewModule(self,newModule) :
    if newModule!= None : 
      for v in self.myData["modules"]:
        if v["modIP"] == newModule["modIP"]: return
      self.myData["modules"].append(newModule)

  def addNewDevice(self,newDevice):
    if newDevice!= None : 
      for v in self.myData["devices"]:
        if v["devIP"] == newDevice["devIP"]: return
      self.myData["devices"].append(newDevice)

  def updateModule(self,modIP,modState,modPower):
    for v in self.myData["modules"]:
        if v["modIP"] == modIP: 
          if modState!=None : v["modState"]=modState
          if modPower!=None : v["modPower"]=modPower

  def updateDevice(self,devIP,devState,devPower):
    for v in self.myData["devices"]:
        if v["devIP"] == devIP: 
          if devState!=None : v["devState"]=devState
          if devPower!=None : v["devPower"]=devPower

  def getModules(self):
    return self.myData["modules"]

  def getDevices(self):
    return self.myData["devices"]

  def updateTime(self, time):
    self.myData["timestamp"]=time
    

@app.route("/")
def test():
  myIstance.updateTime(datetime.datetime.now())
  return myIstance.getData()

@app.route("/sensorsData")
def updateData():
  newLux = request.args.get("lux")
  newHum = request.args.get("hum")
  newTmp = request.args.get("tmp")
  myIstance.updateDataSensors(newLux,newHum,newTmp)
  return myIstance.getData()

@app.route("/addModule")
def addModule():
  newModule = dict(modName=request.args.get("modName"),
                    modState=request.args.get("modState"),
                      modPower=request.args.get("modPower"),
                        modIP=request.args.get("modIP"))
  myIstance.addNewModule(newModule)
  return myIstance.getData()

@app.route("/updateModule")
def updateMod():
  myIstance.updateModule(request.args.get("modIP"),modState=request.args.get("modState"),modPower=request.args.get("modPower"))
  return myIstance.getData()

@app.route("/addDevice")
def addDevice():
  newDevice=dict(devName=request.args.get("devName"),
                    devState=request.args.get("devState"),
                      devPower=request.args.get("devPower"),
                        devIP=request.args.get("devIP"))
  myIstance.addNewDevice(newDevice)
  return myIstance.getData()

@app.route("/updateDevice")
def updateDev():
  myIstance.updateDevice(request.args.get("devIP"),devState=request.args.get("devState"),devPower=request.args.get("devPower"))
  return myIstance.getData()

@app.route("/solveError")
def solve():
  myIstance.changeState(SystemState.WORKING.value)
  myIstance.updateDataSensors(None,None,1)
  errorFlag= False
  return myIstance.getData()

@app.route("/getData",methods=["GET","POST"])
def sendData():
  print(errorFlag)
  if errorFlag: return "ERROR"
  else:
    myjson=json.dumps(myIstance.getData(), indent=4, sort_keys=True, default=str)
    return myjson


def systemThreadTask():
  while True:
    print("Current State = "+ str(SystemState(myIstance.getData()["generalState"])))

    payload={"cmnd":"Status"}

    data=requests.post("http://192.168.1.105/cm",params=payload).json()
    print(data)
    time.sleep(5)
    '''
    if myIstance.getData()["generalState"] == SystemState.WORKING.value:
      workingMethod()
    elif myIstance.getData()["generalState"] == SystemState.IDLE.value:
      idleMethod()
    elif myIstance.getData()["generalState"] == SystemState.ERROR.value:
      errorMethod()
    '''

def workingMethod(): 
  global errorFlag
  #print("WorkingMethod")
  myIstance.updateTime(datetime.datetime.now())
  if int(myIstance.getData()["sensors"]["lux"])<1: myIstance.changeState(SystemState.IDLE.value) 
  if int(myIstance.getData()["sensors"]["tmp"])>10: myIstance.changeState(SystemState.ERROR.value) ; errorFlag = True
  
def idleMethod():
  #print("IdleMethod")
  if int(myIstance.getData()["sensors"]["tmp"])>10: myIstance.changeState(SystemState.ERROR.value) ; errorFlag = True
  if lastRequest != myIstance.getData()["timestamp"] and int(myIstance.getData()["sensors"]["lux"])>1:
    myIstance.changeState(SystemState.WORKING.value)
    return

def errorMethod():
  #print("ErrorMethod")
  global errorFlag
  errorFlag = True


  
if __name__ == "__main__":
  myIstance = myHouse()
  currentState=SystemState.WORKING
  lastRequest=datetime.datetime.now()
  errorFlag = False
  threading.Thread(target=lambda:app.run(host="0.0.0.0", port=80, debug=True, use_reloader=False)).start()
  threading.Timer(3.0,systemThreadTask).start()


