package com.netwatcher.util;
public final class Validation {
 private Validation(){} private static final String IP="^(?:(?:25[0-5]|2[0-4]\\d|1?\\d?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|1?\\d?\\d)$"; private static final String MAC="^(?i)([0-9a-f]{2}[:-]){5}[0-9a-f]{2}$";
 public static void required(String value,String name){if(value==null||value.isBlank())throw new IllegalArgumentException(name+" is required.");}
 public static void device(String name,String ip,String mac,String type){required(name,"Name");required(ip,"IP address");required(mac,"MAC address");required(type,"Device type");if(!ip.matches(IP))throw new IllegalArgumentException("Enter a valid IPv4 address.");if(!mac.matches(MAC))throw new IllegalArgumentException("Enter a valid MAC address (AA:BB:CC:DD:EE:FF).");}
}
