from flask import Flask
from flask import request
import json
from enum import Enum
import threading 
import datetime
import requests
import time

app = Flask(__name__)

#-------------------------------------< MYHOUSE CLASS >-------------------------------------------#
class myHouse():
    def __init__(self) -> None:
        #devName = dict(devIp = "0.0.0.0", state=0)
       self.myDevices = dict(devices = [])
       self.energyData = dict(measures = [])
    
    def getDevices(self):
        return self.myDevices
    
    def getPowerMeasures(self):
        return self.energyData

    def addDevice(self, newDev):
        self.myDevices["devices"].append(newDev)
    
    def addPowerMeasure(self,data):
       self.energyData["measures"].append(data)

@app.route("/")
def test():
  return myHouseIstance.getDevices()

@app.route("/energy")
def getEnergy():
  return myHouseIstance.getPowerMeasures()

@app.route("/addDevice")
def addDevice():
    newDev= dict(devName = request.args.get("devName"), devIp = request.args.get("devIp"), 
                 state = request.args.get("state"), powerMeter = request.args.get("powerMeter"))
    myHouseIstance.addDevice(newDev)
    return myHouseIstance.getDevices()

@app.route("/appMeasure")
def tmpMeasure():
   return energyStatusRequest()
   

#---------------------------------------------------------------------------------------------------#

def energyStatusRequest():
    payload={"cmnd":"Status 8"}
    energy=requests.post("http://192.168.1.105/cm",params=payload).json()["StatusSNS"]["ENERGY"]
    myHouseIstance.addPowerMeasure(energy)
    return energy

def periodicalPowerMeasure():
    while True:
        energyData=energyStatusRequest()
        
        print(energyData)
        time.sleep(60)
   

  
if __name__ == "__main__":
  myHouseIstance = myHouse()
  threading.Thread(target=lambda:app.run(host="0.0.0.0", port=80, debug=True, use_reloader=False)).start()
  threading.Timer(3.0,periodicalPowerMeasure).start()