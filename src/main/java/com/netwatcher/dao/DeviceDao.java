package com.netwatcher.dao;
import com.netwatcher.model.Device;
import java.sql.*; import java.util.*;
public class DeviceDao {
    public List<Device> findAll() throws SQLException { List<Device> r=new ArrayList<>(); try(Connection c=Database.connect(); PreparedStatement p=c.prepareStatement("SELECT * FROM devices ORDER BY name"); ResultSet x=p.executeQuery()){while(x.next())r.add(map(x));}return r; }
    public void save(Device d) throws SQLException { boolean isNew=d.getId()==0; String q=isNew?"INSERT INTO devices(name,ipAddress,macAddress,type,notes) VALUES(?,?,?,?,?)":"UPDATE devices SET name=?,ipAddress=?,macAddress=?,type=?,notes=? WHERE id=?"; try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement(q)){ set(p,d); if(!isNew)p.setInt(6,d.getId()); p.executeUpdate();} }
    public void delete(int id) throws SQLException { try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("DELETE FROM devices WHERE id=?")){p.setInt(1,id);p.executeUpdate();} }
    private void set(PreparedStatement p,Device d)throws SQLException{p.setString(1,d.getName());p.setString(2,d.getIpAddress());p.setString(3,d.getMacAddress());p.setString(4,d.getType());p.setString(5,d.getNotes());}
    private Device map(ResultSet r)throws SQLException{return new Device(r.getInt("id"),r.getString("name"),r.getString("ipAddress"),r.getString("macAddress"),r.getString("type"),r.getString("notes"));}
}
