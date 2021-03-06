package gui.map;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.DirectionsPane;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.LatLongBounds;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.lynden.gmapsfx.service.directions.DirectionStatus;
import com.lynden.gmapsfx.service.directions.DirectionsRenderer;
import com.lynden.gmapsfx.service.directions.DirectionsRequest;
import com.lynden.gmapsfx.service.directions.DirectionsResult;
import com.lynden.gmapsfx.service.directions.DirectionsService;
import com.lynden.gmapsfx.service.directions.DirectionsServiceCallback;
import com.lynden.gmapsfx.service.directions.TravelModes;
import com.lynden.gmapsfx.shapes.Polyline;
import gui.driver.app.AbstractWindow;

import java.util.ArrayList;
import java.util.Locale;



import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

/*
 * @Autor Shay Segal
 */
public class PmMap extends  AbstractWindow implements MapComponentInitializedListener, DirectionsServiceCallback {
//extends Application

	protected GoogleMapView mapComponent;
	protected GoogleMap map;

	private Label lblCenter;
	private Label lblClick;
	private ComboBox<String> fromLocation;
	private ComboBox<String> toLocation;
	private ComboBox<MapTypeIdEnum> mapTypeCombo;
	private Button btnHideMarker;
	private Button btnDeleteMarker;
	private Button btnReturn;
	private Button btnShowMarkers;
	private Button btnSelectRoute;
	private Scene scene;
	private VBox markerVbox;
	private ScrollPane sp;
	ArrayList<Button> btns;
	ArrayList<Marker> markers;
	private VBox routeVbox;
	Polyline poly;
	protected DirectionsService directionsService;
    protected DirectionsPane directionsPane;
    protected LatLong fromLogic;
    protected LatLong toLogic;
    public PmMap(LatLong fromLogic,LatLong toLogic){
    	this.fromLogic=fromLogic;
    	this.toLogic=toLogic;
    }
    public PmMap(){
    	this.fromLogic=null;
    	this.toLogic=null;
    }
	public void display( Stage s) {
		btns = new ArrayList<Button>();
		markers = new ArrayList<Marker>();
		mapComponent = new GoogleMapView(Locale.getDefault().getLanguage(), null);
		mapComponent.addMapInializedListener(this);
		BorderPane bp = new BorderPane();
		ToolBar tb = new ToolBar();

		lblCenter = new Label();
		lblClick = new Label();

		mapTypeCombo = new ComboBox<>();
		mapTypeCombo.setOnAction(e -> {
			map.setMapType(mapTypeCombo.getSelectionModel().getSelectedItem());
		});
		mapTypeCombo.setDisable(true);

		Button btnType = new Button("Map type");
		btnType.setOnAction(e -> {
			map.setMapType(MapTypeIdEnum.HYBRID);
		});

		btnHideMarker = new Button("Hide Marker");
		btnHideMarker.setOnAction(e -> {
			hideMarker();
		});

		btnDeleteMarker = new Button("Delete Marker");
		btnDeleteMarker.setOnAction(e -> {
			deleteMarker();
		});

		btnReturn = new Button("Back");
		btnReturn.setOnAction(e -> {
			s.close();
			AbstractWindow.prevWindows.get(AbstractWindow.prevWindows.size()-1).window.show();
			AbstractWindow.prevWindows.remove(AbstractWindow.prevWindows.size()-1);
		});
		btnShowMarkers = new Button("Show/Hide markers");
		btnShowMarkers.setOnAction(e -> {
			if(bp.getRight()!=null)
				btnSelectRoute.fire();
			bp.setLeft(bp.getLeft() == null ? sp : null);
		});
		btnSelectRoute = new Button("Select Route");
		btnSelectRoute.setOnAction(e -> {
			if(bp.getLeft()!=null)
				btnShowMarkers.fire();
			bp.setRight(bp.getRight() != null ? null : routeVbox);
		});
		tb.getItems().addAll(new Label("MapType: "), mapTypeCombo, new Label("Coordinates: "), lblCenter,
				new Label("Click: "), lblClick, btnHideMarker, btnDeleteMarker, btnShowMarkers, btnSelectRoute,btnReturn);
		markerVbox = addVBox("Markers");
		markerVbox.setVisible(true);
		sp = new ScrollPane();
		sp.setContent(markerVbox);
		createRoutePane();
		bp.setTop(tb);
		bp.setCenter(mapComponent);
		scene = new Scene(bp);
		scene.getStylesheets().add(getClass().getResource("mapStyle.css").toExternalForm());
		s.setScene(scene);
		s.show();
	}

	@Override
	public void mapInitialized() {
		// Once the map has been loaded by the Webview, initialize the map
		// details.
		LatLong center = new LatLong(32.777, 35.0225);
		mapComponent.addMapReadyListener(() -> {
			// This call will fail unless the map is completely ready.
			checkCenter(center);
		});
		lblClick.setText((center + ""));
		MapOptions options = new MapOptions();
		options.center(center).zoom(12).overviewMapControl(false).panControl(false).rotateControl(false)
				.scaleControl(true).streetViewControl(true).zoomControl(true).mapType(MapTypeIdEnum.ROADMAP);
		map = mapComponent.createMap(options, false);
		map.setHeading(123.2);
		map.fitBounds(new LatLongBounds(center, new LatLong(32.779032, 35.024663)));

		lblCenter.setText((map.getCenter() + ""));
		map.centerProperty().addListener((ObservableValue<? extends LatLong> obs, LatLong o, LatLong n) -> {
			lblCenter.setText((n + ""));
		});
		map.addUIEventHandler(UIEventType.click, (JSObject obj) -> {
			LatLong newLat = new LatLong((JSObject) obj.getMember("latLng"));
			newLat = new LatLong(newLat.getLatitude(),newLat.getLongitude());
			lblClick.setText((newLat + ""));
			map.addMarker(createMarker(newLat, "marker at " + newLat));
		});
		mapTypeCombo.setDisable(false);

		mapTypeCombo.getItems().addAll(MapTypeIdEnum.ALL);
        directionsService = new DirectionsService();
        directionsPane = mapComponent.getDirec();
		scene.getWindow().sizeToScene();
		if(fromLogic==null||toLogic==null)
			directionsService.getRoute((new DirectionsRequest("technion", "haifa university", TravelModes.DRIVING)),
					this, new DirectionsRenderer(true, mapComponent.getMap(), directionsPane));
	}

	private Marker createMarker(LatLong lat, String title) {
		MarkerOptions options = new MarkerOptions();
		options.position(lat).title(title).visible(true);
		Marker $ = new MyMarker(options,title,lat);
		markers.add($);
		fromLocation.getItems().add(title);
		toLocation.getItems().add(title);
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(8, 5, 8, 5));
		hbox.setSpacing(8);
		Label l = new Label(title);
		Button btn = new Button("remove");
		btn.setOnAction(e -> {
			map.removeMarker($);
			markerVbox.getChildren().remove(hbox);
			fromLocation.getItems().remove(title);
			toLocation.getItems().remove(title);
		});
		btns.add(btn);
		hbox.getChildren().addAll(l, btn);
		VBox.setMargin(hbox, new Insets(0, 0, 0, 8));
		markerVbox.getChildren().add(hbox);
		return $;

	}

	private void hideMarker() {
		for (Marker ¢ : markers)
			¢.setVisible(!¢.getVisible());
	}

	private void deleteMarker() {
		for (Button bt : btns)
			bt.fire();
	}

	private void checkCenter(LatLong center) {
		System.out.println("Testing fromLatLngToPoint using: " + center);
		Point2D p = map.fromLatLngToPoint(center);
		System.out.println("Testing fromLatLngToPoint result: " + p);
		System.out.println("Testing fromLatLngToPoint expected: " + mapComponent.getWidth() / 2 + ", "
				+ mapComponent.getHeight() / 2);
	}


	public VBox addVBox(String head) {
		VBox $ = new VBox();
		$.setPadding(new Insets(10, 10, 50, 10));
		$.setSpacing(8);

		Text title = new Text(head);
		title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		$.getChildren().add(title);
		return $;
	}

	public void createRoutePane() {
		routeVbox = addVBox("select route");
		fromLocation = new ComboBox<String>();
		fromLocation.setOnAction(e->{
			//delete line that drawn
			map.hideDirectionsPane();
		});
		toLocation = new ComboBox<String>();
		toLocation.setOnAction(e->{
			//delete line that drawn
				map.hideDirectionsPane();
		});
		Button btn =new Button("draw");
		btn.setOnAction(e->{
			if(toLocation.getSelectionModel().getSelectedItem()==null || fromLocation.getSelectionModel().getSelectedItem()==null)
				return;
			MyMarker to = getMarkerByTitle(toLocation.getSelectionModel().getSelectedItem());
			MyMarker from = getMarkerByTitle(fromLocation.getSelectionModel().getSelectedItem());
			directionsService.getRoute((new DirectionsRequest(from.lat.getLatitude()+", "+from.lat.getLongitude(),to.lat.getLatitude()+", "+to.lat.getLongitude(),TravelModes.DRIVING)), this,
					new DirectionsRenderer(true, mapComponent.getMap(), directionsPane));
		});
		Button removeBtn = new Button("remove line");
		removeBtn.setOnAction(e->{
			map.hideDirectionsPane();
		});
		routeVbox.getChildren().addAll(fromLocation, toLocation,btn,removeBtn);
		
	}

	public MyMarker getMarkerByTitle(String title){
		for(Marker ¢ : markers)
			if (((MyMarker) ¢).isTitle(title))
				return ((MyMarker) ¢);
		return null;
	}

	@Override
	public void directionsReceived(DirectionsResult __, DirectionStatus s) {
		
	}

}