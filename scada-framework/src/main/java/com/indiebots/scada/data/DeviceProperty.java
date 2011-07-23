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
package com.indiebots.scada.data;

import com.indiebots.scada.Named;

/**
 * A simple triplet containing <name, value, type> representing a snapshot of
 * the device property at the time of a probe A set of these properties
 * constitute the payload of a device data message
 */
public interface DeviceProperty<T> extends Named
{
    T getValue();

    Class<T> getType();
}
