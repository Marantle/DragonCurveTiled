package com.marantle.dragoncurve;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ColorBomb extends Application {
	// private static final int restartDelay = 1000;
	// private static final double resetDelay = 5;
	// private static final int redrawDelay = 1000;
	// private static final int fractalDelay = 2000;

	private static final double restartDelay = 200;
	private static final double resetDelay = 500;
	private static final double redrawDelay = 200;
	private static final double fractalDelay = 10000;
	int size = 1000;
	int yos = 175;
	int xos = 475;
	double currentSplit = 2;
	int pixels = 256;
	List<Point2D> relocatePoints = new ArrayList<>();
	List<Circle> recs = new ArrayList<>();
	List<Pane> panes = new ArrayList<>();
	List<Circle> goUp = new ArrayList<>();
	List<Circle> goDown = new ArrayList<>();
	List<Circle> goRight = new ArrayList<>();
	List<Circle> goLeft = new ArrayList<>();
	private Timeline timeline = new Timeline();
	boolean vertical = false;
	int colorI = 0;
	Set<Point2D> points = new HashSet<>();
	Set<Integer> yPoint = new HashSet<>();
	List<Color> colors = new ArrayList<>();
	// List<PathTransition> transitions = new ArrayList<>();
	private Button butt = new Button("Start");
	protected double initialX;
	protected double initialY;
	Random rand = new Random();
	private Pane root;
	private int moves = 0;
	private int movesTo = 0;
	private List<PathTransition> transitions = new ArrayList<>();

	@Override
	public void start(Stage primaryStage) {
		root = new Pane();
//		generateRelocatePoints();
		for (int i = 0; i < 10000; i++) {
			float r = rand.nextFloat();
			float g = rand.nextFloat();
			float b = rand.nextFloat();

			colors.add(Color.color(r, g, b, 1.0));
		}
		Button butt2 = new Button("Hold to drag");
		Button butt3 = new Button("Close");
		butt3.setOnAction(e -> {
			System.exit(0);
		});
		addDraggableNode(butt2);
		butt.setTranslateX(50);
		butt2.setTranslateX(150);
		butt3.setTranslateX(250);
		root.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		SVGPath svg = getFinalFractalCoordinates();
		svg.setVisible(false);
		root.getChildren().add(svg);
		int time = 1;
		createPainter(svg, 1000*time++);
		createPainter(svg, 1000*time++);
		createPainter(svg, 1000*time++);
		createPainter(svg, 1000*time++);
		createPainter(svg, 1000*time++);
		createPainter(svg, 1000*time++);
		createPainter(svg, 1000*time++);
		createPainter(svg, 1000*time++);
		createPainter(svg, 1000*time++);
		createPainter(svg, 1000*time++);
		int i = 1;

		try {
			Scene scene = new Scene(root);
			// root.setScaleX(2);
			//
			// root.setScaleY(2);
			// root.setTranslateX(500);
			// root.setTranslateY(500);
			drawPixels();
			// root.getChildren().add(svg);

			butt.setOnAction(e -> {
				magic();
			});

			root.getChildren().add(butt);
			root.getChildren().add(butt2);
			root.getChildren().add(butt3);

			// root.getChildren().add(butt2);
			primaryStage.setScene(scene);
			primaryStage.setMaximized(true);
			// primaryStage.initStyle(StageStyle.TRANSPARENT);
			// scene.setFill(null);

			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generateRelocatePoints() {
		int xCount = 1500 / pixels;
		int yCount = 800 / pixels;
		for (int y = 0; y < yCount; y++) {
			for (int x = 0; x < xCount; x++) {
				relocatePoints.add(new Point2D(x * pixels, y * pixels));
			}

		}
		Collections.shuffle(relocatePoints, new Random(System.nanoTime()));

	}

	private void drawPixels() {
		// System.out.println("draw pixels");
		recs.clear();
		panes.clear();
		Pane pane = new Pane();
		pane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
		pane.setPrefWidth(0);
		pane.setPrefHeight(0);
		pane.setMinWidth(0);
		pane.setMinHeight(0);
		pane.setMaxWidth(0);
		pane.setMaxHeight(0);
		for (int y = 0; y < pixels; y++) {
			for (int x = 0; x < pixels; x++) {
				Circle crcl = new Circle(0.5);

				float r = rand.nextFloat();
				float g = rand.nextFloat();
				float b = rand.nextFloat();

				crcl.setFill(Color.color(r, g, b, 1.0));
				crcl.relocate(x + xos, y + yos);
				crcl.setId(crcl.getLayoutX() + "," + crcl.getLayoutY());
				pane.getChildren().add(crcl);
				recs.add(crcl);
			}
		}
		panes.add(pane);
		root.getChildren().add(pane);
	}

	private void addDraggableNode(final Node node) {

		node.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				if (me.getButton() != MouseButton.MIDDLE) {
					initialX = me.getSceneX();
					initialY = me.getSceneY();
				}
			}
		});

		node.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				if (me.getButton() != MouseButton.MIDDLE) {
					node.getScene().getWindow().setX(me.getScreenX() - initialX);
					node.getScene().getWindow().setY(me.getScreenY() - initialY);
				}
			}
		});
	}

	private void magic() {
		// System.out.println("magic");
		transitions.forEach(e -> {
			e.stop();
		});
		butt.setDisable(true);
		vertical = !vertical;
		ObservableList<KeyFrame> ls = FXCollections.observableArrayList();
		Point2D[] pointAr = new Point2D[10];
		pointAr = points.toArray(pointAr);
		int pointCount = pointAr.length;
		System.out.println("total points " + pointCount);
		int i = 0;
		for (Circle dot : recs) {
			i++;
			Point2D point = pointAr[i % pointCount];
			final KeyValue kv1 = new KeyValue(dot.layoutXProperty(), point.getX());
			final KeyValue kv2 = new KeyValue(dot.layoutYProperty(), point.getY());
			final KeyValue kv3 = new KeyValue(dot.radiusProperty(), 3);
			final KeyFrame kf = new KeyFrame(Duration.millis(fractalDelay), kv1, kv2, kv3);
			ls.add(kf);
		}
		timeline.getKeyFrames().addAll(ls);
		timeline.play();
	}

	public static int randInt(int min, int max) {

		// NOTE: This will (intentionally) not run as written so that folks
		// copy-pasting have to think about how to initialize their
		// Random instance. Initialization of the Random instance is outside
		// the main scope of the question, but some decent options are to have
		// a field that is initialized once and then re-used as needed or to
		// use ThreadLocalRandom (if using at least Java 1.7).
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	private void createPainter(SVGPath svg, int time) {
		Circle c1 = new Circle(3);
		c1.setVisible(false);
		c1.translateXProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				Point2D p = new Point2D((int) c1.getTranslateX(), (int) c1.getTranslateY());
				if (!points.contains(p)) {
					System.err.println(p);
					points.add(p);
				}
			}
		});
		c1.translateYProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				Point2D p = new Point2D((int) c1.getTranslateX(), (int) c1.getTranslateY());
				if (!points.contains(p)) {
					System.err.println(p);
					points.add(p);
				}
			}
		});
		PathTransition pthTrs1 = new PathTransition();
		pthTrs1.setDuration(Duration.millis(time));
		pthTrs1.setPath(svg);
		pthTrs1.setNode(c1);
		root.getChildren().add(c1);
		pthTrs1.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
		pthTrs1.setCycleCount(Timeline.INDEFINITE);
		pthTrs1.setAutoReverse(true);

		pthTrs1.play();
		 transitions.add(pthTrs1);
	}

	private void sort() {
		Comparator<Circle> comparator = Comparator.comparing(Circle::getLayoutY).thenComparing(Circle::getLayoutX);
		recs = recs.stream().sorted(comparator).collect(Collectors.toList());
	}

	private void sort2() {
		Comparator<Circle> comparator = Comparator.comparing(Circle::getLayoutX).thenComparing(Circle::getLayoutY);
		recs = recs.stream().sorted(comparator).collect(Collectors.toList());
	}

	private SVGPath getFinalFractalCoordinates() {
		SVGPath svg = new SVGPath();
		List<String> paths = new ArrayList<>();
		paths.add("M487.083,225.514l-75.08-75.08V63.704c0-15.682-12.708-28.391-28.413-28.391c-15.669,0-28.377,12.709-28.377,28.391v29.941L299.31,37.74c-27.639-27.624-75.694-27.575-103.27,0.05L8.312,225.514c-11.082,11.104-11.082,29.071,0,40.158c11.087,11.101,29.089,11.101,40.172,0l187.71-187.729c6.115-6.083,16.893-6.083,22.976-0.018l187.742,187.747c5.567,5.551,12.825,8.312,20.081,8.312c7.271,0,14.541-2.764,20.091-8.312C498.17,254.586,498.17,236.619,487.083,225.514zM257.561,131.836c-5.454-5.451-14.285-5.451-19.723,0L72.712,296.913c-2.607,2.606-4.085,6.164-4.085,9.877v120.401c0,28.253,22.908,51.16,51.16,51.16h81.754v-126.61h92.299v126.61h81.755c28.251,0,51.159-22.907,51.159-51.159V306.79c0-3.713-1.465-7.271-4.085-9.877L257.561,131.836z");
				
		StringBuilder sb = new StringBuilder("");
		paths.forEach(p -> {
			sb.append(p);
			sb.append(" ");
		});
		// System.out.println(sb.toString());
		svg.setContent(sb.toString());
		// svg.setScaleX(0.01);
		// svg.setScaleY(0.01);
		svg.setStroke(Color.HOTPINK);
		svg.setFill(Color.DEEPPINK);
		svg.setScaleX(1);
		svg.setScaleY(1);
//		svg.setRotate(180);
		svg.relocate(500, 500);
		Label label = new Label();
		label.setShape(svg);
		label.setBackground(new Background(new BackgroundFill(Color.DEEPPINK, CornerRadii.EMPTY, Insets.EMPTY)));

		return svg;
	}

	public static void main(String[] args) {
		launch(args);
	}

	private ImageView toImg(Node n) {
		final int width = 224;
		final int height = 224;
		final int xoffset = 100;

		WritableImage size = new WritableImage(xoffset + width, height);
		WritableImage snapshot = n.getScene().getRoot().snapshot(new SnapshotParameters(), size);
		// snapshot = new WritableImage(snapshot.getPixelReader(), xoffset, 0,
		// width, height);
		// Image img = snapshot;
		ImageView iv2 = new ImageView(snapshot);
		// iv2.setImage(img);
		return iv2;
		// iv2.setFitWidth(width / 2);
		// iv2.setFitHeight(height / 2);
		// iv2.setPreserveRatio(true);
		// iv2.setSmooth(true);
		// iv2.setCache(true);
		// snapshot = iv2.snapshot(new SnapshotParameters(), null);
		// File path = new File("/var/mp/" + PrjNumHolder.getPrjNum() +
		// "/images/" + getClass().getSimpleName() + ".png");
		// System.err.println(path.getAbsolutePath());
		// try {
		// ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", path);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}
}
