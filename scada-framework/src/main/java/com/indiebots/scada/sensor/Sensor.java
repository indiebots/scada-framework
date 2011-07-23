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
package com.indiebots.scada.sensor;

import com.indiebots.scada.Named;
import com.indiebots.scada.data.DeviceDataChannel;
import com.indiebots.scada.device.Device;

/**
 * Sensor probes a device and extracts data about the state of the device.
 * Implementations of Sensor are expected to probe at "probeInterval" intervals and
 * report the findings on each of the registered channels.
 * A sensor is a device in its own right and hence can be probed by other
 * sensors, to create a hierarchy of sensors
 */
public interface Sensor<D extends Device> extends Device, Runnable
{
    void setProbeInterval(long milliseconds);

    long getProbeInterval();

    void beginProbe();

    void endProbe();

    D getDevice();

    void registerChannel(DeviceDataChannel<D> channel);
}
