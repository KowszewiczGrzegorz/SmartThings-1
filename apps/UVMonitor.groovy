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
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


def installed() {
	initialize()
}

def updated() {
	unsubscribe()
	initialize()
}

def initialize() {
	checkUV()
	schedule("0 */30 * * * ?", "checkUV")
}

def getUv() {
	getWeatherFeature("conditions")?.current_observation?.UV as Integer
}

def checkUV() {
	def uv = getUv()
    log.debug "checkUv: $uv"
    if (uv && uv > 0) {
    	if (state.lastUv != uv) {
	        def risk
	        if (uv >= 11) {
	            risk = "Extreame"
	        } else if (uv >= 8) {
	        	risk = "Very high"
	        } else if (uv >= 6) {
	        	risk = "High"
	        }
	        
	        if (uv > 5) {
	        	sendPush("UV is $uv. " + risk + " risk of UV exposure.")
	        } else {
	        	if (state.risk) {
	            	sendPush("UV is $uv or lower.")
	            }
	        }
	        
	        state.risk = risk
	        
	    }
	state.lastUv = uv
    } 
    
    state.lastRun = new Date().toSystemFormat()
}
