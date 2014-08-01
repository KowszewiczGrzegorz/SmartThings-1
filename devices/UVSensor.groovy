/**
 *  UV Sensor
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
metadata {
	definition (name: "UV Sensor", namespace: "625alex", author: "Alex Malikov") {
		capability "Polling"
		capability "Refresh"
		capability "Sensor"
        
		attribute "lastUpdated", "string"
		attribute "uv", "number"
        
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		// TODO: define your main and details tiles here
        valueTile("uv", "device.uv", height: 2, width: 2) {
			state "default", label:'UV: ${currentValue}',
            backgroundColors:[
					[value: 2, color: "#289500"],
					[value: 3, color: "#F7E400"],
					[value: 6, color: "#F85900"],
					[value: 8, color: "#D8001D"],
					[value: 11, color: "#6B49C8"]
				]
		}
        standardTile("refresh", "device.weather", decoration: "flat") {
			state "default", label: "", action: "refresh", icon:"st.secondary.refresh"
		}
        valueTile("lastUpdated", "device.lastUpdated", decoration: "flat") {
			state "default", label:'${currentValue}'
		}
        
        main(["uv"])
		details(["uv", "lastUpdated", "refresh"])
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'uv' attribute
}

// handle commands
def poll() {
	log.debug "Executing 'poll'"
    def obs = getWeatherFeature("conditions")?.current_observation
    if (obs) {
    	def uv = Math.max(obs.UV as Integer ?: 0, 0)
    	sendEvent(name: "uv", value: uv, isStateChange: device.currentValue("uv") != uv)
        
        def tf = new java.text.SimpleDateFormat("h:mm a")
        tf.setTimeZone(TimeZone.getTimeZone("GMT${obs.local_tz_offset}"))
        sendEvent(name: "lastUpdated", value: "${tf.format(new Date())}")
    }
}

def refresh() {
	log.debug "Executing 'refresh'"
    poll()
}
