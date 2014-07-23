/**
 *  UV monitor
 *
 *  Copyright 2014 Alex Malikov
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "UV Monitor",
    namespace: "625alex",
    author: "Alex Malikov",
    description: "Notify when UV index rises to dangerous levels above 5.",
    category: "Health & Wellness",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/SafetyAndSecurity/App-MindYourHome.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/SafetyAndSecurity/App-MindYourHome@2x.png")

preferences {
	section("Check how often (minutes)?") {
		input name: "frequency", title: "Frequency", type: "number", defaultValue: 15
	}
    section ("Only report when UV is above...") {
    	input name: "reportMinimum", type: "number", defaultValue: 5
    }
    section ("Only report when UV changes by...") {
    	input name: "delta", type: "number", defaultValue: 1
    }
}


def installed() {
	initialize()
}

def updated() {
	unsubscribe()
	initialize()
}

def initialize() {
	log.debug "settings: $settings"
	checkUV()
}

def getUv() {
	def cond = getWeatherFeature("conditions")?.current_observation
	def uv = cond?.UV as Integer
    uv = uv ?: 0
    if (uv < 0) uv = 0
    
    def tf = new java.text.SimpleDateFormat("h:mm a")
    log.debug "TZ offcet: ${cond?.local_tz_offset}"
    tf.setTimeZone(TimeZone.getTimeZone("GMT${cond?.local_tz_offset ?: 0}"))
    def lastRun = "${tf.format(new Date())}"
    
    state.lastRun = lastRun
    
    log.debug "uv: $uv"
    uv
}

def checkUV() {
	def uv = getUv()
    
    if (state.lastUv != uv) {
        def risk
        if (uv >= 11) {
            risk = "Extreme"
        } else if (uv >= 8) {
        	risk = "Very high"
        } else if (uv >= 6) {
        	risk = "High"
        } else if (uv >= 3) {
        	risk = "Moderate"
        } else if (uv >= 1) {
        	risk = "Low"
        } else {
        	risk = "No"
        }
        
        state.risk = risk
        
        if (uv >= reportMinimum) {
        	if (Math.abs(state.lastUv ?: 0 - uv) >= delta) {
            	def message = "UV index at ${location.name} is $uv. " + risk + " risk of UV exposure."
            	sendPush(message)
            	log.debug message
                state.isLowReported = false
            }
        } else if (!state.isLowReported) {
            sendPush("UV is $uv or lower.")
            state.isLowReported = true
        }
        
    }
    
    state.lastUv = uv
    
    runIn(frequency * 60, checkUV, [overwrite: false])
}
