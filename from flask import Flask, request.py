from flask import Flask, request
import requests

app = Flask(__name__)

@app.route("/", methods=["GET", "POST"])
def index():
    if request.method == "POST":
        switch_state = request.json.get("switch")
        print("Received switch state:", switch_state)
        
        url = "http://192.168.1.150:8081/zeroconf/switch"
        headers = { "Content-Type": "application/json" }
        body = { "deviceid": "", "data": { "switch": switch_state } }

        response = requests.post(url, headers=headers, json=body)
        print("Response from server:", response.content)

        return "Switch state received and sent to the server", 200

    return '''
        <button onclick="sendPostRequest()">Send POST Request</button>

        <script>
            function sendPostRequest() {
                var xhr = new XMLHttpRequest();
                xhr.open("POST", "http://192.168.1.150:8081/zeroconf/switch", true);
                xhr.setRequestHeader("Content-Type", "application/json");
                
                xhr.onreadystatechange = function() {
                    if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
                        console.log("Response: " + xhr.responseText);
                    }
                };
                
                var data = JSON.stringify({ 
    "deviceid": "", 
    "data": {
        "switch": "off" 
    } 
 });
                xhr.send(data);
            }
        </script>
    '''

if __name__ == "__main__":
    app.run(host="0.0.0.0")