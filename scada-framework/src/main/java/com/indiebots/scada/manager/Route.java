/*
   Copyright [2011] [indiebots.com]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.indiebots.scada.manager;

import com.indiebots.scada.Named;
import com.indiebots.scada.device.Device;
import com.indiebots.scada.device.EventHandler;
import com.indiebots.scada.sensor.Sensor;

/**
 * A route represents a connection between a sensor of a device and an EventHandler 
 * for that same device.
 * The most common usage of a route would be to hook up the channel to which the Sensor
 * is writing probe data, to the EventHandler configured to handle device data.
 */
public interface Route<D extends Device, S extends Sensor<D>, E extends EventHandler<D>> extends Named
{

    S getSensor();

    E getHandler();
}
