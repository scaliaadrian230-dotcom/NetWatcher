package com.netwatcher.controller;

import com.netwatcher.dao.*;
import com.netwatcher.model.*;
import com.netwatcher.util.Validation;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class NetWatcherController {
    private final DeviceDao deviceDao=new DeviceDao(); private final NetworkLogDao logDao=new NetworkLogDao();
    private final ObservableList<Device> devices=FXCollections.observableArrayList(); private final ObservableList<NetworkLog> logs=FXCollections.observableArrayList();
    private final TableView<Device> deviceTable=new TableView<>(); private final TableView<NetworkLog> logTable=new TableView<>();
    private final TextField name=new TextField(), ip=new TextField(), mac=new TextField(), type=new TextField(); private final TextArea notes=new TextArea();
    private final ComboBox<Device> logDevice=new ComboBox<>(); private final DatePicker logDate=new DatePicker(LocalDate.now()); private final TextField issue=new TextField(), resolution=new TextField(); private final Spinner<Integer> severity=new Spinner<>(1,5,3);
    private final ComboBox<String> deviceFilter=new ComboBox<>(), issueFilter=new ComboBox<>(), severityFilter=new ComboBox<>();
    private Device selectedDevice; private NetworkLog selectedLog;

    public NetWatcherController() { setupTables(); refresh(); }
    public TabPane createView() { TabPane tabs=new TabPane(); tabs.getTabs().addAll(new Tab("Devices", devicesTab()),new Tab("Issue Logs", logsTab()),new Tab("Insights", insightsTab())); tabs.getTabs().forEach(t->t.setClosable(false)); return tabs; }

    private Node devicesTab(){
        TextField search=new TextField(); search.setPromptText("Search devices..."); FilteredList<Device> filtered=new FilteredList<>(devices,d->true); search.textProperty().addListener((o,a,b)->filtered.setPredicate(d->contains(d.getName(),b)||contains(d.getIpAddress(),b)||contains(d.getType(),b))); deviceTable.setItems(filtered);
        Button save=new Button("Save device"), clear=new Button("Clear"), delete=new Button("Delete selected"); save.setOnAction(e->saveDevice()); clear.setOnAction(e->clearDevice()); delete.setOnAction(e->deleteDevice());
        GridPane form=grid(); add(form,"Name *",name,0); add(form,"IP address *",ip,1); add(form,"MAC address *",mac,2); add(form,"Type *",type,3); notes.setPrefRowCount(3); add(form,"Notes",notes,4); HBox actions=new HBox(8,save,clear,delete); form.add(actions,1,5);
        VBox left=new VBox(12,new Label("Device inventory"),search,deviceTable); left.setPadding(new Insets(16)); VBox.setVgrow(deviceTable,Priority.ALWAYS); VBox right=new VBox(12,new Label("Device details"),form);right.setPadding(new Insets(16));right.setPrefWidth(360); SplitPane p=new SplitPane(left,right);p.setDividerPositions(.65);return p;
    }
    private Node logsTab(){
        deviceFilter.setPromptText("All devices"); issueFilter.setPromptText("All issue types"); severityFilter.getItems().setAll("All severities","1 - Low","2","3 - Medium","4","5 - Critical"); severityFilter.setValue("All severities"); HBox filters=new HBox(8,new Label("Filter:"),deviceFilter,issueFilter,severityFilter);
        FilteredList<NetworkLog> filtered=new FilteredList<>(logs,l->true); Runnable apply=()->filtered.setPredicate(l->{String d=deviceFilter.getValue(),i=issueFilter.getValue(),s=severityFilter.getValue();return (d==null||d.equals("All devices")||l.getDeviceName().equals(d))&&(i==null||i.equals("All issue types")||l.getIssueType().equals(i))&&(s==null||s.equals("All severities")||l.getSeverity()==Integer.parseInt(s.substring(0,1)));}); deviceFilter.setOnAction(e->apply.run());issueFilter.setOnAction(e->apply.run());severityFilter.setOnAction(e->apply.run());logTable.setItems(filtered);
        Button save=new Button("Save log"),clear=new Button("Clear"),delete=new Button("Delete selected");save.setOnAction(e->saveLog());clear.setOnAction(e->clearLog());delete.setOnAction(e->deleteLog());
        GridPane form=grid(); add(form,"Device *",logDevice,0);add(form,"Date *",logDate,1);add(form,"Issue type *",issue,2);add(form,"Resolution *",resolution,3);add(form,"Severity *",severity,4);form.add(new HBox(8,save,clear,delete),1,5);
        VBox left=new VBox(12,new Label("Network issue log"),filters,logTable);left.setPadding(new Insets(16));VBox.setVgrow(logTable,Priority.ALWAYS);VBox right=new VBox(12,new Label("Log details"),form);right.setPadding(new Insets(16));right.setPrefWidth(360);SplitPane p=new SplitPane(left,right);p.setDividerPositions(.65);return p;
    }
    private Node insightsTab(){ PieChart issueChart=new PieChart(); CategoryAxis x=new CategoryAxis();NumberAxis y=new NumberAxis(1,5,1);y.setLabel("Severity (1–5)");LineChart<String,Number> trend=new LineChart<>(x,y);trend.setLegendVisible(false); trend.setCreateSymbols(true); Button refresh=new Button("Refresh charts");refresh.setOnAction(e->populateCharts(issueChart,trend)); populateCharts(issueChart,trend); VBox box=new VBox(16,new Label("Network health insights"),refresh,new HBox(20,issueChart,trend));box.setPadding(new Insets(20));issueChart.setPrefSize(430,350);trend.setPrefSize(520,350);return box; }
    private void setupTables(){
        deviceTable.getColumns().addAll(col("Name","name"),col("IP Address","ipAddress"),col("MAC Address","macAddress"),col("Type","type"),col("Notes","notes"));deviceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);deviceTable.getSelectionModel().selectedItemProperty().addListener((o,a,d)->{if(d!=null){selectedDevice=d;name.setText(d.getName());ip.setText(d.getIpAddress());mac.setText(d.getMacAddress());type.setText(d.getType());notes.setText(d.getNotes());}});
        logTable.getColumns().addAll(col("Date","date"),col("Device","deviceName"),col("Issue type","issueType"),col("Resolution","resolution"),col("Severity","severity"));logTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);logTable.getSelectionModel().selectedItemProperty().addListener((o,a,l)->{if(l!=null){selectedLog=l;logDevice.getSelectionModel().select(devices.stream().filter(d->d.getId()==l.getDeviceId()).findFirst().orElse(null));logDate.setValue(LocalDate.parse(l.getDate()));issue.setText(l.getIssueType());resolution.setText(l.getResolution());severity.getValueFactory().setValue(l.getSeverity());}});
    }
    private <T> TableColumn<T,?> col(String title,String property){TableColumn<T,Object> c=new TableColumn<>(title);c.setCellValueFactory(new PropertyValueFactory<>(property));return c;}
    private void refresh(){try{devices.setAll(deviceDao.findAll());logs.setAll(logDao.findAll());logDevice.setItems(devices);deviceFilter.getItems().setAll("All devices");deviceFilter.getItems().addAll(devices.stream().map(Device::getName).toList());issueFilter.getItems().setAll("All issue types");issueFilter.getItems().addAll(logs.stream().map(NetworkLog::getIssueType).filter(Objects::nonNull).distinct().sorted().toList());}catch(SQLException e){error("Database error",e.getMessage());}}
    private void saveDevice(){try{Validation.device(name.getText(),ip.getText(),mac.getText(),type.getText());Device d=selectedDevice==null?new Device():selectedDevice;d.setName(name.getText().trim());d.setIpAddress(ip.getText().trim());d.setMacAddress(mac.getText().trim());d.setType(type.getText().trim());d.setNotes(notes.getText().trim());deviceDao.save(d);clearDevice();refresh();}catch(IllegalArgumentException e){error("Invalid device",e.getMessage());}catch(SQLException e){error("Database error",e.getMessage());}}
    private void saveLog(){try{Device d=logDevice.getValue();if(d==null)throw new IllegalArgumentException("A device is required.");Validation.required(issue.getText(),"Issue type");Validation.required(resolution.getText(),"Resolution");NetworkLog l=selectedLog==null?new NetworkLog():selectedLog;l.setDeviceId(d.getId());l.setDate(logDate.getValue().toString());l.setIssueType(issue.getText().trim());l.setResolution(resolution.getText().trim());l.setSeverity(severity.getValue());logDao.save(l);clearLog();refresh();}catch(IllegalArgumentException e){error("Invalid log",e.getMessage());}catch(SQLException e){error("Database error",e.getMessage());}}
    private void deleteDevice(){if(selectedDevice!=null&&confirm("Delete this device and its issue logs?"))try{deviceDao.delete(selectedDevice.getId());clearDevice();refresh();}catch(SQLException e){error("Database error",e.getMessage());}}
    private void deleteLog(){if(selectedLog!=null&&confirm("Delete this issue log?"))try{logDao.delete(selectedLog.getId());clearLog();refresh();}catch(SQLException e){error("Database error",e.getMessage());}}
    private void clearDevice(){selectedDevice=null;deviceTable.getSelectionModel().clearSelection();name.clear();ip.clear();mac.clear();type.clear();notes.clear();} private void clearLog(){selectedLog=null;logTable.getSelectionModel().clearSelection();logDevice.setValue(null);logDate.setValue(LocalDate.now());issue.clear();resolution.clear();severity.getValueFactory().setValue(3);}
    private void populateCharts(PieChart pie,LineChart<String,Number> line){pie.getData().setAll(logs.stream().collect(Collectors.groupingBy(NetworkLog::getIssueType,Collectors.counting())).entrySet().stream().map(e->new PieChart.Data(e.getKey()+" ("+e.getValue()+")",e.getValue())).toList());XYChart.Series<String,Number> s=new XYChart.Series<>();logs.stream().sorted(Comparator.comparing(NetworkLog::getDate)).forEach(l->s.getData().add(new XYChart.Data<>(l.getDate(),l.getSeverity())));line.getData().setAll(s);}
    private GridPane grid(){GridPane g=new GridPane();g.setVgap(10);g.setHgap(8);ColumnConstraints c=new ColumnConstraints();c.setMinWidth(95);g.getColumnConstraints().addAll(c,new ColumnConstraints());return g;} private void add(GridPane g,String label,Node field,int row){if(field instanceof Region r)r.setMaxWidth(Double.MAX_VALUE);g.add(new Label(label),0,row);g.add(field,1,row);GridPane.setHgrow(field,Priority.ALWAYS);} private boolean contains(String text,String q){return q==null||q.isBlank()||text.toLowerCase().contains(q.toLowerCase());} private boolean confirm(String q){return new Alert(Alert.AlertType.CONFIRMATION,q,ButtonType.YES,ButtonType.NO).showAndWait().orElse(ButtonType.NO)==ButtonType.YES;}private void error(String h,String m){new Alert(Alert.AlertType.ERROR,m,ButtonType.OK).showAndWait();}
}
