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
package com.indiebots.scada;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.indiebots.scada.data.DeviceData;
import com.indiebots.scada.data.DeviceDataChannel;
import com.indiebots.scada.data.DeviceProperty;
import com.indiebots.scada.device.Device;
import com.indiebots.scada.device.EventHandler;
import com.indiebots.scada.manager.Route;
import com.indiebots.scada.manager.Router;
import com.indiebots.scada.sensor.Sensor;

public class TestScada extends TestCase
{

    public void testScada() throws Exception
    {
        //
        // create two water tanks
        //
        WaterTank waterTank1 = new WaterTank("Water Tank #1");
        WaterTank waterTank2 = new WaterTank("Water Tank #2");

        //
        // create a sensor for each tank
        //
        final WaterTankSensor sensor1 = new WaterTankSensor("Sensor #1", 3000, waterTank1);
        final WaterTankSensor sensor2 = new WaterTankSensor("Sensor #2", 3000, waterTank2);

        //
        // create one pump
        //
        final WaterPump pump = new WaterPump("Water Pump #1");

        Router<WaterTank> router = new Router<WaterTank>()
        {
            
            public String getName()
            {
                return "Router";
            }

            public void addRoute(final Route<WaterTank, Sensor<WaterTank>, EventHandler<WaterTank>> route)
            {
                final DeviceDataChannel<WaterTank> channel = new DeviceDataChannel<WaterTank>("Channel<" + route.getName() + ">");
                route.getSensor().registerChannel(channel);

                Runnable r = new Runnable()
                {

                    public void run()
                    {
                        while (true)
                        {
                            try
                            {
                                System.out.println(getName() + " : Reading channel for data from [" + route.getSensor().getName() + "]");
                                DeviceData<WaterTank> data = channel.take();
                                System.out.println(getName() + " : Found data from [" + route.getSensor().getName() + "] [" + data + "]");
                                route.getHandler().handle(data);
                            }
                            catch (Exception e)
                            {
                            }
                        }
                    }
                };
                new Thread(r).start();
            }

        };

        //
        // create 2 routes between the sensor and the pump
        //
        router.addRoute(new AbstractRoute()
        {
            

            public Sensor<WaterTank> getSensor()
            {
                return sensor1;
            }

            public EventHandler<WaterTank> getHandler()
            {
                // TODO Auto-generated method stub
                return pump;
            }

        });

        router.addRoute(new AbstractRoute()
        {

            public Sensor<WaterTank> getSensor()
            {
                return sensor2;
            }

            public EventHandler<WaterTank> getHandler()
            {
                // TODO Auto-generated method stub
                return pump;
            }

        });

        //
        // finally start the probing
        //
        sensor1.beginProbe();
        sensor2.beginProbe();
        
        synchronized (router)
        {
            router.wait();
        }
    }

    private static class WaterTank implements Device
    {
        public static final String ALARM_LEVEL = "alarm level";
        public static final String FATAL = "fatal";
        public static final String HIGH_ALERT = "high alert";
        public static final String ALERT = "alert";
        private String name;
        private int capacity = 100;
        private int level = 100;
        private Runnable drain = new Runnable()
        {

            public void run()
            {
                while (true)
                {
                    if (level <= 0)
                        System.out.println(WaterTank.this.getName() + " is empty");
                    else
                    {
                        level--;
                    }
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (Exception e)
                    {

                    }
                }
            }
        };

        public WaterTank(String name)
        {
            super();
            this.name = name;
            new Thread(drain, "Drain[" + getName() + "]").start();
        }

        public String getName()
        {
            return name;
        }

        public int getLevel()
        {
            // TODO Auto-generated method stub
            return level;
        }

        public int getCapacity()
        {
            // TODO Auto-generated method stub
            return capacity;
        }

        public void addWater(int increment)
        {
            int toAdd = Math.min(increment, capacity - level);
            level += toAdd;
            System.out.println(getName() + " : current level = " + getLevel());
        }

    }

    private static class WaterTankSensor implements Sensor<WaterTank>
    {
        private String name;
        private long probeInterval;
        private WaterTank waterTank;
        private boolean running;
        private List<DeviceDataChannel<WaterTank>> channels;

        public WaterTankSensor(String name, long probeInterval, WaterTank waterTank)
        {
            super();
            this.name = name;
            this.probeInterval = probeInterval;
            this.waterTank = waterTank;
            this.channels = new ArrayList<DeviceDataChannel<WaterTank>>();
        }

        public WaterTank getDevice()
        {
            // TODO Auto-generated method stub
            return waterTank;
        }

        public String getName()
        {
            // TODO Auto-generated method stub
            return name;
        }

        public long getProbeInterval()
        {
            return probeInterval;
        }

        public void setProbeInterval(long probeInterval)
        {
            this.probeInterval = probeInterval;
        }

        public void beginProbe()
        {
            this.running = true;
            new Thread(this, getName()).start();
        }

        public void endProbe()
        {
            this.running = false;
        }

        public void run()
        {
            while (running)
            {
                final int level = waterTank.getLevel();
                DeviceData<WaterTank> alarm = null;
                if (level <= 0)
                {
                    alarm = createAlarm(level, WaterTank.FATAL);
                }
                else if (level <= 20)
                {
                    alarm = createAlarm(level, WaterTank.HIGH_ALERT);
                }
                else if (level <= 50)
                {
                    alarm = createAlarm(level, WaterTank.ALERT);
                }
                else
                {
                    alarm = createAlarm(level, "Info");
                }

                for (DeviceDataChannel<WaterTank> channel : channels)
                {
                    try
                    {
                        System.out.println(getName() + ": writing to channel : [" + alarm + "]");
                        channel.put(alarm);
                    }
                    catch (Exception e)
                    {
                    }
                }

                try
                {
                    Thread.sleep(probeInterval);
                }
                catch(Exception e){}
            }

        }

        private DeviceData<WaterTank> createAlarm(final int level, final String alarm)
        {
            return new DeviceData<WaterTank>()
            {
                private List<DeviceProperty<?>> data = getData();
                
                @Override
                public String toString()
                {
                    StringBuilder s = new StringBuilder();
                    for (DeviceProperty<?> p: getData())
                    {
                        s.append(p).append(", ");
                    }
                    return s.toString();
                }

                public List<DeviceProperty<?>> getData()
                {
                    if (data == null)
                    {
                        data = new ArrayList<DeviceProperty<?>>();
                        data.add(createAlarmProperty(alarm));
                        data.add(createWaterLevelProperty(level));
                    }
                    return data;
                }

                private DeviceProperty<String> createAlarmProperty(final String alarm)
                {
                    return new AbstractDeviceProperty<String>()
                    {

                        public String getValue()
                        {
                            return alarm;
                        }

                        public String getName()
                        {
                            // TODO Auto-generated method stub
                            return WaterTank.ALARM_LEVEL;
                        }

                        public Class<String> getType()
                        {
                            // TODO Auto-generated method stub
                            return String.class;
                        }

                    };
                }

                private DeviceProperty<Integer> createWaterLevelProperty(final int level)
                {
                    return new AbstractDeviceProperty<Integer>()
                    {

                        public String getName()
                        {
                            return "level";
                        }

                        public Class<Integer> getType()
                        {
                            // TODO Auto-generated method stub
                            return Integer.class;
                        }

                        public Integer getValue()
                        {
                            return level;
                        }

                    };

                }

                public WaterTank getDevice()
                {
                    return waterTank;
                }

            };
            
        }

        abstract class AbstractDeviceProperty<T> implements DeviceProperty<T>
        {

            @Override
            public String toString()
            {
                return new StringBuilder().append(getName()).append(":").append(getValue()).toString();
            }
            
        }

        public void registerChannel(DeviceDataChannel<WaterTank> channel)
        {
            System.out.println("Registering channel [" + channel + "]");
            this.channels.add(channel);
        }
    }

    private static class WaterPump implements EventHandler<WaterTank>
    {
        private String name;
        private boolean pumping = false;
        
        public WaterPump(String name)
        {
            super();
            this.name = name;
        }

        public String getName()
        {
            return name;
        }

        public void handle(DeviceData<WaterTank> deviceData)
        {
            System.out.println(getName() + " : got data [" + deviceData + "]");
            WaterTank waterTank = deviceData.getDevice();

            Map<String, Object> properties = new HashMap<String, Object>();
            for (DeviceProperty<?> p : deviceData.getData())
            {
                properties.put(p.getName(), p.getValue());
            }
            String alarmLevel = (String) properties.get(WaterTank.ALARM_LEVEL);
            if (WaterTank.FATAL.equals(alarmLevel))
            {
                pump(waterTank, 20);
            }
            else if (WaterTank.HIGH_ALERT.equals(alarmLevel))
            {
                pump(waterTank, 10);
            }
            else if (WaterTank.ALERT.equals(alarmLevel))
            {
                pump(waterTank, 5);
            }
        }

        private void pump(final WaterTank waterTank, final int increment)
        {
            
            Runnable pump = new Runnable()
            {

                public void run()
                {
                    WaterPump.this.pumping = true;
                    int deficit = waterTank.getCapacity() - waterTank.getLevel();
                    while (deficit >= 0)
                    {
                        System.out.println(getName() + " : pumping [" + increment + "] to [" + waterTank.getName() + "]");
                        waterTank.addWater(increment);
                        deficit -= increment;
                        try
                        {
                            Thread.sleep(3000);
                        }
                        catch (Exception e)
                        {
                        }
                    }
                    WaterPump.this.pumping = false;
                }

            };
            
            if (!pumping)
            {
                Thread t = new Thread(pump, getName());
                t.start();
            }
        }

    }
    
    public abstract class AbstractRoute implements Route<WaterTank,Sensor<WaterTank>,EventHandler<WaterTank>>
    {

        public String getName()
        {
            return "Route<" + getSensor().getName() + ", " + getHandler().getName() + ">";
        }

    }
}
