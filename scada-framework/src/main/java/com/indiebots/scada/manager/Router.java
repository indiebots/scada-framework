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

import com.indiebots.scada.device.Device;
import com.indiebots.scada.device.EventHandler;
import com.indiebots.scada.sensor.Sensor;

/**
 * A router facilitates routes between sensors and event handlers.
 * A router is a Device in its own right and can also be monitored
 * by a sensor
 */
public interface Router<D extends Device> extends Device
{
    void addRoute(Route<D, Sensor<D>, EventHandler<D>> route);
}
