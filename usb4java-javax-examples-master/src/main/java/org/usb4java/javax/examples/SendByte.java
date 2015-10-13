/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usb4java.javax.examples;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.usb.UsbConfiguration;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbDisconnectedException;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbInterface;
import javax.usb.UsbInterfacePolicy;
import javax.usb.UsbIrp;
import javax.usb.UsbNotActiveException;
import javax.usb.UsbPipe;
import javax.usb.UsbServices;
import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;
import javax.usb.event.UsbPipeListener;
import static sun.audio.AudioDevice.device;

/**
 *
 * @author GabrielAntonio
 */
public class SendByte {
   
    public static UsbDevice findDevice(UsbHub hub, short vendorId, short productId)
{
    for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices())
    {
        UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
        if (desc.idVendor() == vendorId && desc.idProduct() == productId){ System.out.println("found"); return device;}
        else System.out.println("not found");
        if (device.isUsbHub())
        {
            device = findDevice((UsbHub) device, vendorId, productId);
            
            if (device != null){
                System.out.println("found");
                return device;
            }
        }
    }
    return null;
}

public static void main (String[] args)
{
    try
    {
    
             
        UsbServices services = UsbHostManager.getUsbServices();
        UsbDevice device = findDevice(services.getRootUsbHub(), (short)0x13AD,(short)0x9CA5 );
        UsbConfiguration configuration = device.getActiveUsbConfiguration();
        UsbInterface iface = configuration.getUsbInterface((byte) 0);
        iface.claim(new UsbInterfacePolicy()
        {
        @Override
        public boolean forceClaim(UsbInterface usbinterface)
        {
            return true;
        }
        });
        try
        {
            UsbEndpoint endpoint = iface.getUsbEndpoint((byte)0x81);
            UsbPipe pipe = endpoint.getUsbPipe();
            pipe.open();
            if (pipe.isActive()){ 
            System.out.println("Pipe is actived!"); 
            } 
            if (pipe.isOpen()){ 
            System.out.println("Pipe is opened!"); 
            }
            try
            {   
                /*byte[] data = new byte[]{8,4}; 
                UsbIrp i = pipe.createUsbIrp(); 
                i.setData(data); 
                pipe.syncSubmit(i); //*** error here *** */
                
                final byte[] bytes = new byte[2];
                for(int i=0;i<2;i++)
                bytes[i] = (byte)0x07;
                

                /*UsbIrp sent=pipe.asyncSubmit(bytes);
                //int sent = pipe.syncSubmit(new byte[] { 1, 2});
                sent.waitUntilComplete(10000);
                System.out.println(sent+" bytes sent");*/
                
                
                byte[] bytesToRead = new byte[2];
                UsbIrp irpRead = pipe.createUsbIrp();
                irpRead.setData(bytesToRead);
                while(pipe.isActive()){                       
                pipe.asyncSubmit(irpRead); //Read some bytes
                irpRead.waitUntilComplete(10000);
                System.out.println(bytesToRead[0]+" bytes received");
                }
                
                /*while(pipe.isActive())
                {    
                pipe.addUsbPipeListener(new UsbPipeListener()
                {            
                 @Override
                 public void errorEventOccurred(UsbPipeErrorEvent event)
                {
                UsbException error = event.getUsbException();
        
                }
    
                @Override
                public void dataEventOccurred(UsbPipeDataEvent event)
                {
                byte[] data = event.getData();
                System.out.println(data + " bytes received");
                }
                });
                }
                /*
                byte[] datum = new byte[0];
                int received = pipe.syncSubmit(datum);
                System.out.println(received + " bytes received");*/
            }
            finally
            {
                //pipe.close();
            }
        }
            
        finally
        {
            
           //iface.release();
            
        }
    }
    catch(UsbException e){System.out.println("Error:"+e.getMessage());}
        
        
        
            
}
    
            
    
}
