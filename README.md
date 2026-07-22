# NetWatcher

NetWatcher is a JavaFX desktop app for maintaining a home-network device inventory and recording network issues. It uses an SQLite database created automatically as `netwatcher.db` beside the project.

**Course:** COP2800-2265-9721 | Java Programming

## Student

Adrian Scalia

## Features

- Full add, edit, and delete workflows for devices and issue logs.
- IPv4, MAC-address, required-field, and severity validation with user-friendly error alerts.
- Device search; issue-log filters for device, issue type, and severity; sortable tables.
- Issue-type frequency pie chart and chronological severity-trend line chart.
- SQLite foreign keys: deleting a device also removes its associated logs.

## Run

Requirements: JDK 21 and Maven.

```bash
mvn javafx:run
```

The first launch creates the database and both tables:

```text
devices(id, name, ipAddress, macAddress, type, notes)
networkLogs(id, deviceId, date, issueType, resolution, severity)
```
