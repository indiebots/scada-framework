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
package com.indiebots.scada.device;

import com.indiebots.scada.data.DeviceData;

/**
 * A special kind of device that is capable of handling messages sent by a 
 * sensor when the sensor probes a remote device
 * 
 * EventHandlers are themselves devices which means they can be probed themselves
 * by other sensors and have their events be handled by other EventHandlers
 */
public interface EventHandler<D extends Device> extends Device
{
    void handle(DeviceData<D> deviceData);
}
