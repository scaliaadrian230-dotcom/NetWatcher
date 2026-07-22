package com.netwatcher.dao;
import com.netwatcher.model.NetworkLog;
import java.sql.*; import java.util.*;
public class NetworkLogDao {
    public List<NetworkLog> findAll() throws SQLException { List<NetworkLog> r=new ArrayList<>(); String q="SELECT l.*, d.name deviceName FROM networkLogs l JOIN devices d ON d.id=l.deviceId ORDER BY l.date DESC";try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement(q);ResultSet x=p.executeQuery()){while(x.next())r.add(map(x));}return r; }
    public void save(NetworkLog l)throws SQLException{boolean n=l.getId()==0;String q=n?"INSERT INTO networkLogs(deviceId,date,issueType,resolution,severity) VALUES(?,?,?,?,?)":"UPDATE networkLogs SET deviceId=?,date=?,issueType=?,resolution=?,severity=? WHERE id=?";try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement(q)){set(p,l);if(!n)p.setInt(6,l.getId());p.executeUpdate();}}
    public void delete(int id)throws SQLException{try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("DELETE FROM networkLogs WHERE id=?")){p.setInt(1,id);p.executeUpdate();}}
    private void set(PreparedStatement p,NetworkLog l)throws SQLException{p.setInt(1,l.getDeviceId());p.setString(2,l.getDate());p.setString(3,l.getIssueType());p.setString(4,l.getResolution());p.setInt(5,l.getSeverity());}
    private NetworkLog map(ResultSet r)throws SQLException{return new NetworkLog(r.getInt("id"),r.getInt("deviceId"),r.getString("date"),r.getString("issueType"),r.getString("resolution"),r.getInt("severity"),r.getString("deviceName"));}
}
