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

import java.util.concurrent.LinkedBlockingQueue;

import com.indiebots.scada.Named;
import com.indiebots.scada.device.Device;

public class DeviceDataChannel<D extends Device> extends LinkedBlockingQueue<DeviceData<D>> implements Named
{
    protected String name;
    
    public DeviceDataChannel(String name)
    {
        super();
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
}
