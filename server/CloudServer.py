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

@app.route("/energyDevice=<device>")
def getEnergyDevice(device):
  devToReturn=""
  for tmpDev in myHouseIstance.getDevices()["devices"]:
     if(device == tmpDev["devName"]):
        devToReturn= tmpDev["energy"]
        print("Measures {}".format(devToReturn))
  return devToReturn

@app.route("/addDevice")
def addDevice():
    newDev= dict(devName = request.args.get("devName"), devIp = request.args.get("devIp"), energy=dict(measures=[]))
    myHouseIstance.addDevice(newDev)
    return myHouseIstance.getDevices()

@app.route("/appMeasure")
def tmpMeasure():
   return energyStatusRequest()
   

#---------------------------------------------------------------------------------------------------#

def energyStatusRequest():
    payload={"cmnd":"Status 8"}
    try:    
        energy=""
        for dev in myHouseIstance.getDevices()["devices"]: 
            #print(f"current device {dev}")
            if(dev == None or dev ==""):
               return "{}"
            energy=requests.post("http://{}/cm".format(dev["devIp"]),params=payload).json()["StatusSNS"]
            dev["energy"]["measures"].append(energy)
            myHouseIstance.addPowerMeasure(energy)
        return energy
    except requests.exceptions.RequestException as e:
       print(f"Request failed {e}")
    return "{}"

def periodicalPowerMeasure():
    while True:
        print(energyStatusRequest())
        time.sleep(3)
   

  
if __name__ == "__main__":
  myHouseIstance = myHouse()
  threading.Thread(target=lambda:app.run(host="0.0.0.0", port=80, debug=True, use_reloader=False)).start()
  threading.Timer(3.0,periodicalPowerMeasure).start()