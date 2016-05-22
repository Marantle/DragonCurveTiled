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

public class DragonCurveTiled extends Application {
    // private static final int restartDelay = 1000;
    // private static final double resetDelay = 5;
    // private static final int redrawDelay = 1000;
    // private static final int fractalDelay = 2000;

    private static final double restartDelay = 100;
    private static final double resetDelay = 100;
    private static final double redrawDelay = 100;
    private static final double fractalDelay = 100;
    int size = 1000;
    int yos = 175;
    int xos = 175;
    double currentSplit = 2;
    int pixels = 128;
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

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();
        generateRelocatePoints();
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
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        root.getChildren().add(getFinalFractalCoordinates());
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
//		System.out.println("draw pixels");
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
                crcl.setFill(Color.BLACK);
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
//		System.out.println("magic");
        butt.setDisable(true);
        vertical = !vertical;
        double minX = recs.stream().min((x1, x2) -> Double.compare(x1.getLayoutX(), x2.getLayoutX())).get()
                .getLayoutX();
        double maxX = recs.stream().max((x1, x2) -> Double.compare(x1.getLayoutX(), x2.getLayoutX())).get()
                .getLayoutX();
        double width = maxX - minX + 1;
        double minY = recs.stream().min((y1, y2) -> Double.compare(y1.getLayoutY(), y2.getLayoutY())).get()
                .getLayoutY();
        double maxY = recs.stream().max((y1, y2) -> Double.compare(y1.getLayoutY(), y2.getLayoutY())).get()
                .getLayoutY();
        double height = maxY - minY;

//		System.out.println("minX: " + minX);
//		System.out.println("maxX: " + maxX);
//		System.out.println("width: " + width);
//		System.out.println("minY: " + minY);
//		System.out.println("maxY: " + maxY);
//		System.out.println("height: " + height);
        int xx = 0, yy = 0;
        double prevX = 0;
        double prevY = 0;
//		System.out.println("Vertical slide? " + vertical);
        double bp;
        if (vertical) {
            sort();
            bp = pixels / currentSplit;
//			System.out.println("bp is " + bp);
        } else {
            sort2();
            bp = pixels / currentSplit;
//			System.out.println("bp is " + bp);
        }
        if (bp > 1) {
            currentSplit = currentSplit * 2;
            for (Circle rec : recs) {
                double y = rec.getLayoutY();
                double x = rec.getLayoutX();
                if (vertical) {
                    if (prevY < y && prevY > 0) {
                        yy++;
                    }
                    prevY = y;
                    if (yy / bp >= 2) {
                        yy = 0;
                    }

                    if (yy < bp/* && goLeft.size() < pixels*pixels/2 */) {
                        goLeft.add(rec);
                    } else {
                        goRight.add(rec);
                    }

                } else {
                    if (prevX < x && prevX > 0) {
                        xx++;
                    }
                    prevX = x;
                    if (xx / bp >= 2) {
                        xx = 0;
                    }
                    if (xx < bp /* && goDown.size() < pixels*pixels/2 */) {
                        goDown.add(rec);
                    } else {
                        goUp.add(rec);
                    }
                }

            }
//			System.out.println("animations start!");
            long start = System.currentTimeMillis();
            double move = (bp / 2);
            timeline = new Timeline();
            ObservableList<KeyFrame> ls = FXCollections.observableArrayList();
            if (goLeft.size() > 0) {
                System.err.println("foreachLeft " + goLeft.size());
                for (Circle rec : goLeft) {
                    rec.setFill(colors.get(colorI));
                    final KeyValue kv = new KeyValue(rec.layoutXProperty(), rec.getLayoutX() - move);
                    final KeyFrame kf = new KeyFrame(Duration.millis(fractalDelay), kv);
                    ls.add(kf);
                }
            }
            if (goRight.size() > 0) {
                System.err.println("foreachRight " + goRight.size());
                colorI++;
                for (Circle rec : goRight) {
                    rec.setFill(colors.get(colorI));
                    final KeyValue kv = new KeyValue(rec.layoutXProperty(), rec.getLayoutX() + move);
                    final KeyFrame kf = new KeyFrame(Duration.millis(fractalDelay), kv);

                    ls.add(kf);
                }
            }
            if (goUp.size() > 0) {
                System.err.println("foreachUp" + goUp.size());
                for (Circle rec : goUp) {
                    rec.setFill(colors.get(colorI));
                    final KeyValue kv = new KeyValue(rec.layoutYProperty(), rec.getLayoutY() - move);
                    final KeyFrame kf = new KeyFrame(Duration.millis(fractalDelay), kv);
                    ls.add(kf);
                }
            }
            System.err.println("foreachDown " + goDown.size());
            if (goDown.size() > 0) {
                colorI++;
                for (Circle rec : goDown) {
                    rec.setFill(colors.get(colorI));
                    final KeyValue kv = new KeyValue(rec.layoutYProperty(), rec.getLayoutY() + move);
                    final KeyFrame kf = new KeyFrame(Duration.millis(fractalDelay), kv);
                    ls.add(kf);
                }
            }
            timeline.getKeyFrames().setAll(ls);
            timeline.setOnFinished(ae -> {
                for (Circle rec : recs) {
                    rec.setFill(colors.get(colorI));
                }
                long end = System.currentTimeMillis() - start;
//				System.out.println("Animations end! took " + end / 1000 + " seconds.");
                timeline.getKeyFrames().clear();
                butt.setDisable(false);
                Timeline delay = new Timeline(new KeyFrame(Duration.millis(redrawDelay), e -> {
                    magic();
                }));
                delay.play();
            });
            timeline.play();
            System.err.println("timeline played");

            goRight.clear();
            goLeft.clear();
            goDown.clear();
            goUp.clear();
        } else {
            currentSplit = 2;
            vertical = false;
            System.err.println("count of points: " + points.size());
            ObservableList<KeyFrame> ls = FXCollections.observableArrayList();
            List<Point2D> pnts = new ArrayList<Point2D>(points);
            timeline = new Timeline();
            for (Pane rec : panes) {
//				double x = Double.parseDouble(rec.getId().split(",")[0]);
//				double y = Double.parseDouble(rec.getId().split(",")[1]);
                final KeyValue kvy;
                final KeyValue kvx;

//				System.out.println("take ss");
//				ImageView iv = toImg(rec);
//				System.out.println("clear children");
//				rec.getChildren().clear();
//				System.out.println("add ss");
//				rec.getChildren().add(iv);
                if (relocatePoints.size() > 0) {
//					System.out.println("relocate");
                    kvy = new KeyValue(rec.translateYProperty(), relocatePoints.get(0).getY());
                    kvx = new KeyValue(rec.translateXProperty(), relocatePoints.get(0).getX());
                } else {
                    System.out.println("return");
                    return;
                }
                // final KeyValue kvc = new KeyValue(rec.fillProperty(),
                // Color.BLACK);
                final KeyFrame kf = new KeyFrame(Duration.millis(resetDelay), kvy, kvx);
                ls.add(kf);
            }
            relocatePoints.remove(0);
            moves++;
            movesTo++;
            timeline.getKeyFrames().setAll(ls);
            timeline.play();
            timeline.setOnFinished(ae -> {
                timeline.getKeyFrames().clear();
                butt.setDisable(false);
                Timeline delay = new Timeline(new KeyFrame(Duration.millis(restartDelay), e -> {
                    drawPixels();
                    magic();
                }));
                delay.play();
            });
        }
    }

    private void createPainter(SVGPath svg, int time) {
        Circle c1 = new Circle(3);
//		c1.setVisible(false);
        c1.translateXProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Point2D p = new Point2D((int) c1.getTranslateX(), (int) c1.getTranslateY());
                if (!points.contains(p)) {
                    points.add(p);
                }
            }
        });
        c1.translateYProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Point2D p = new Point2D((int) c1.getTranslateX(), (int) c1.getTranslateY());
                if (!points.contains(p)) {
                    points.add(p);
                }
            }
        });
        PathTransition pthTrs1 = new PathTransition();
        pthTrs1.setDuration(Duration.seconds(time));
        pthTrs1.setPath(svg);
        pthTrs1.setNode(c1);
        root.getChildren().add(c1);
        pthTrs1.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pthTrs1.setCycleCount(Timeline.INDEFINITE);
        pthTrs1.setAutoReverse(true);

        pthTrs1.play();
        // transitions.add(pthTrs1);
    }

    private void sort() {
        Comparator<Circle> comparator = Comparator.comparing(Circle::getLayoutY).thenComparing(Circle::getLayoutX);
        recs = recs.stream().sorted(comparator).collect(Collectors.toList());
    }

    private void sort2() {
        Comparator<Circle> comparator = Comparator.comparing(Circle::getLayoutX).thenComparing(Circle::getLayoutY);
        recs = recs.stream().sorted(comparator).collect(Collectors.toList());
    }

    private Label getFinalFractalCoordinates() {
        SVGPath svg = new SVGPath();
        List<String> paths = new ArrayList<>();
        paths.add(
                "M5910 14333 c-209 -14 -422 -61 -640 -142 -47 -18 -394 -186 -771 -373 l-687 -341 -12 -40 c-11 -37 -10 -41 17 -70 15 -16 38 -41 50 -54 12 -14 26 -30 33 -36 6 -7 25 -25 41 -42 17 -16 37 -42 45 -57 12 -24 19 -27 57 -24 49 3 67 11 67 32 0 8 14 20 30 26 26 9 63 55 65 81 0 4 70 41 155 83 85 41 168 86 186 99 38 29 845 377 965 416 171 56 278 72 474 73 99 1 207 -3 240 -7 l60 -8 23 43 c29 55 130 176 162 193 14 7 33 27 43 44 l18 30 -49 15 c-165 48 -389 72 -572 59z");
        paths.add(
                "M6578 14185 c-14 -24 -39 -52 -55 -62 -15 -9 -35 -27 -43 -38 -8 -11 -31 -38 -51 -60 -20 -22 -43 -53 -51 -69 l-15 -29 77 -27 c163 -57 328 -165 451 -298 160 -174 441 -656 543 -932 67 -184 81 -275 81 -540 0 -196 -4 -259 -23 -380 -46 -291 -130 -643 -292 -1227 -208 -745 -370 -1256 -809 -2543 -215 -631 -211 -610 -211 -1070 0 -474 9 -531 85 -557 40 -14 281 -41 288 -32 3 2 7 65 11 138 3 74 10 137 15 140 5 4 11 80 14 171 2 91 12 210 21 265 72 424 127 668 216 962 96 314 190 544 361 885 65 131 144 297 175 370 348 825 595 2236 571 3258 -9 345 -34 551 -93 750 -51 169 -86 214 -324 412 -316 264 -569 427 -806 522 -50 20 -95 36 -100 36 -5 0 -21 -20 -36 -45z");
        paths.add(
                "M3240 13396 c-265 -47 -469 -167 -597 -352 -74 -105 -133 -274 -153 -438 -18 -143 -8 -385 25 -573 25 -146 26 -154 11 -213 -31 -119 -163 -373 -333 -637 -77 -120 -83 -134 -72 -155 18 -34 28 -38 99 -38 36 0 77 -6 92 -14 14 -8 43 -19 65 -26 21 -7 43 -16 48 -20 6 -4 28 -13 50 -20 22 -7 45 -16 51 -21 6 -5 33 -13 62 -19 28 -6 56 -18 63 -26 7 -9 32 -14 73 -14 36 0 73 -7 91 -16 30 -16 745 -154 1735 -334 719 -131 950 -169 1098 -181 68 -5 155 -14 195 -19 145 -18 282 -22 342 -11 58 11 105 35 113 59 23 69 -1244 420 -2623 726 -416 92 -711 151 -871 174 l-121 17 100 298 c83 251 102 297 116 292 9 -4 79 -22 156 -40 157 -38 268 -55 356 -55 56 0 59 1 59 25 0 13 6 28 13 32 7 4 23 23 36 41 26 37 129 124 237 198 l73 51 -68 7 c-67 6 -267 53 -326 76 l-30 12 55 22 c266 109 451 295 506 507 18 67 23 207 11 296 -8 57 -42 112 -118 188 -22 22 -75 78 -119 125 l-79 85 -173 2 c-108 1 -201 -3 -248 -11z");
        paths.add(
                "M4215 13113 c-11 -3 -31 -15 -45 -28 -14 -12 -32 -25 -41 -28 -10 -3 -16 -17 -17 -36 0 -17 -3 -39 -7 -50 -3 -10 8 -49 24 -88 17 -39 42 -110 57 -159 24 -79 28 -106 28 -239 1 -142 0 -153 -25 -206 -30 -63 -81 -119 -137 -149 -132 -72 -429 -295 -474 -355 -13 -17 -33 -40 -44 -52 -39 -38 -88 -151 -92 -214 -6 -75 1 -101 40 -144 80 -92 227 -126 608 -141 250 -10 488 17 593 66 138 65 138 148 1 286 -90 89 -163 134 -220 134 -61 0 -66 -12 -54 -116 5 -48 8 -90 6 -92 -9 -8 -146 -21 -231 -22 -136 0 -205 27 -205 82 0 31 72 95 179 162 106 65 396 212 556 281 61 26 123 59 138 74 36 33 65 88 72 138 8 50 -26 114 -81 155 l-42 30 -151 -67 c-83 -37 -154 -66 -156 -63 -2 2 1 22 7 44 7 21 12 86 11 144 -1 165 -45 323 -150 543 l-57 117 -35 -1 c-20 -1 -45 -3 -56 -6z");
        paths.add(
                "M5978 12509 c-351 -37 -624 -306 -778 -766 -37 -112 -80 -290 -95 -396 -5 -42 -3 -51 19 -77 13 -16 53 -79 87 -140 92 -161 115 -180 290 -241 195 -68 397 -124 559 -156 177 -34 416 -38 525 -9 135 36 220 85 319 183 60 59 156 179 156 195 0 5 -10 12 -22 17 -13 5 -40 21 -61 35 -45 31 -64 33 -72 6 -10 -33 -103 -108 -161 -129 -192 -72 -434 -46 -844 92 l-154 52 69 2 c218 8 376 42 521 114 269 132 354 356 269 709 -14 57 -34 114 -44 125 -10 11 -40 64 -66 118 -26 53 -51 100 -56 103 -9 5 -42 73 -53 110 -5 17 -20 24 -64 33 -56 11 -238 32 -257 29 -5 0 -44 -4 -87 -9z");
        paths.add(
                "M6496 12428 c3 -13 10 -24 15 -26 9 -4 38 -63 59 -119 7 -18 36 -56 65 -86 136 -138 276 -436 316 -671 6 -38 9 -113 7 -166 l-5 -98 34 -13 c18 -7 41 -21 50 -31 9 -10 21 -18 26 -18 6 0 19 -7 29 -17 10 -9 20 -14 22 -12 26 33 95 146 122 202 55 117 77 223 71 342 -6 122 -31 198 -96 298 -112 170 -311 303 -590 397 -66 22 -123 40 -127 40 -3 0 -2 -10 2 -22z");
        paths.add(
                "M1935 10702 c-49 -108 -144 -316 -211 -462 -225 -490 -397 -864 -450 -982 l-53 -118 -8 -317 c-13 -525 -10 -2049 5 -2283 25 -402 59 -630 132 -890 37 -134 83 -319 146 -594 80 -345 306 -694 644 -991 120 -105 219 -185 231 -185 4 0 10 12 14 27 13 52 46 113 61 113 8 0 17 8 21 18 19 55 58 133 74 148 18 17 16 21 -84 123 -476 488 -781 1273 -881 2266 -27 268 -36 464 -36 811 0 555 41 1088 121 1556 21 129 -16 37 599 1451 100 230 179 425 176 433 -3 8 -13 14 -23 14 -10 0 -45 14 -78 30 -57 29 -66 30 -185 30 l-125 -1 -90 -197z");
        paths.add(
                "M5180 9026 c-36 -19 -116 -82 -172 -135 -6 -6 -29 -109 -123 -571 -286 -1395 -404 -1878 -517 -2110 -37 -77 -55 -99 -182 -224 -78 -76 -197 -184 -265 -240 -68 -55 -129 -105 -135 -111 -64 -57 -96 -151 -76 -224 27 -101 146 -175 327 -201 l80 -12 57 -114 c89 -180 202 -284 308 -284 79 0 147 37 238 129 72 74 173 217 193 274 2 7 43 -26 91 -71 141 -135 209 -151 289 -68 23 24 84 113 136 199 l94 157 -5 62 c-9 105 -53 182 -248 428 -143 180 -154 200 -145 249 11 58 105 297 288 726 509 1200 607 1468 607 1668 0 168 -82 257 -225 245 -83 -7 -104 -27 -131 -128 -12 -47 -59 -202 -105 -345 -140 -444 -209 -667 -229 -747 -21 -81 -55 -165 -140 -343 -233 -491 -325 -802 -306 -1036 13 -158 58 -253 182 -384 145 -153 204 -259 204 -364 0 -50 -5 -66 -32 -106 -18 -26 -47 -56 -64 -66 l-30 -19 -71 108 c-88 132 -130 181 -190 221 -48 32 -98 40 -133 21 -31 -16 -67 -88 -94 -187 -41 -146 -75 -228 -113 -272 -80 -94 -146 -26 -162 166 -7 83 -11 98 -34 121 -29 29 -35 29 -262 21 -102 -4 -113 -2 -153 21 -103 58 -47 141 165 245 83 41 272 118 347 142 16 5 33 16 38 24 15 27 354 1104 507 1612 183 605 295 1011 337 1217 10 47 18 126 18 177 1 78 -2 96 -20 120 -28 38 -82 41 -144 9z");
        paths.add(
                "M11075 8554 c-33 -8 -100 -34 -150 -59 -215 -107 -414 -296 -950 -904 -459 -521 -848 -956 -854 -956 -5 0 -31 27 -59 59 -101 119 -247 226 -417 307 -465 222 -1034 234 -1767 38 l-38 -10 0 -114 c0 -63 -5 -135 -12 -160 -7 -24 -9 -47 -6 -51 4 -3 65 8 137 25 239 58 379 74 636 74 191 0 250 -4 348 -21 282 -52 528 -150 769 -308 53 -35 100 -64 105 -64 4 0 30 -11 56 -24 26 -14 65 -30 85 -37 20 -7 51 -20 67 -30 17 -11 41 -23 55 -29 14 -6 35 -16 47 -22 20 -10 31 -3 110 68 220 197 421 384 573 530 91 88 178 171 193 185 43 36 483 500 576 607 179 204 359 446 457 613 31 52 82 73 163 66 166 -12 312 -121 372 -277 103 -268 -73 -684 -471 -1115 -73 -79 -445 -468 -621 -650 -230 -238 -504 -527 -507 -535 -8 -21 359 -239 403 -240 12 0 39 17 60 38 22 21 58 51 80 67 22 17 45 35 50 41 6 6 27 23 47 39 30 22 37 32 32 50 -5 19 -1 24 17 27 30 4 1061 1547 1148 1718 154 303 183 551 84 732 -34 62 -133 156 -208 196 -95 52 -236 109 -312 126 -87 19 -216 20 -298 0z");
        paths.add(
                "M7292 6609 c-38 -11 -37 -13 10 -146 51 -142 136 -250 212 -272 99 -27 246 74 246 169 0 75 -67 149 -195 217 -63 33 -79 37 -155 40 -47 1 -100 -2 -118 -8z");
        paths.add(
                "M8721 6209 c-143 -138 -133 -133 -215 -109 -38 11 -125 30 -195 42 -179 32 -549 32 -756 0 -252 -39 -528 -116 -733 -203 l-43 -19 20 -39 c32 -62 104 -118 184 -142 68 -21 75 -21 358 -9 471 20 800 -12 1094 -107 487 -157 811 -522 934 -1053 45 -190 51 -430 16 -594 -14 -68 -14 -71 10 -123 14 -29 32 -53 39 -53 8 0 49 -18 92 -39 122 -60 143 -65 169 -41 13 12 31 38 41 58 10 20 26 48 35 62 23 35 38 88 40 135 1 22 13 92 27 155 23 103 26 137 27 320 0 164 -4 222 -19 289 -70 322 -240 615 -512 885 -131 130 -331 276 -452 330 -23 10 -42 22 -42 26 0 4 47 49 105 100 72 62 103 96 98 105 -16 26 -94 84 -124 90 -17 4 -42 13 -57 21 -15 8 -29 14 -32 14 -3 -1 -52 -46 -109 -101z");
        paths.add(
                "M10700 5650 c-19 -16 -39 -29 -45 -30 -5 0 -38 -25 -72 -56 -49 -43 -63 -62 -63 -83 0 -26 5 -30 58 -44 71 -20 167 -37 203 -37 48 0 141 -41 185 -82 57 -52 94 -128 94 -190 0 -89 -41 -147 -135 -190 -44 -20 -65 -23 -185 -22 -145 0 -234 17 -435 83 -59 19 -91 26 -94 18 -2 -6 41 -54 95 -106 393 -382 534 -586 534 -773 -1 -81 -24 -132 -85 -186 -123 -108 -408 -126 -717 -46 -76 19 -75 19 -114 -105 -14 -47 -32 -101 -40 -119 -8 -18 -14 -38 -14 -45 0 -39 234 -72 510 -71 200 0 222 2 313 27 229 61 338 166 399 384 30 110 31 262 1 358 -37 119 -134 245 -240 313 -24 15 -43 30 -43 33 0 3 26 16 58 28 147 60 329 171 423 259 159 149 164 277 14 401 -27 23 -131 83 -230 133 -99 50 -215 110 -257 134 -43 24 -79 44 -81 44 -1 0 -18 -13 -37 -30z");
        paths.add(
                "M7745 5223 c-54 -14 -158 -117 -239 -238 -53 -79 -21 -135 97 -170 58 -17 263 -42 272 -33 4 3 6 61 6 128 0 175 -23 269 -73 304 -23 16 -29 17 -63 9z");
        paths.add(
                "M7045 4444 c-76 -39 -127 -86 -160 -145 -28 -53 -28 -54 -17 -141 7 -56 19 -104 34 -131 20 -36 28 -42 60 -45 44 -4 79 13 175 85 92 70 123 126 123 223 0 154 -90 218 -215 154z");
        paths.add(
                "M2590 4118 c-11 -18 -23 -44 -26 -58 -10 -47 -43 -108 -61 -114 -12 -4 -25 -27 -36 -63 l-18 -58 59 -55 c32 -30 79 -65 105 -78 26 -13 51 -31 56 -40 5 -9 40 -33 78 -52 37 -19 151 -85 251 -147 101 -62 187 -113 192 -113 4 0 30 -18 58 -40 27 -22 54 -40 59 -40 6 0 18 -9 28 -20 10 -11 25 -20 33 -20 8 0 40 -15 71 -34 324 -195 924 -396 1576 -528 110 -22 214 -40 231 -39 18 1 74 -5 125 -14 99 -16 206 -19 281 -9 42 6 48 10 67 50 64 128 28 204 -134 280 -122 58 -277 101 -679 190 -188 41 -402 91 -476 110 -523 137 -917 313 -1382 617 -159 104 -387 267 -407 291 -19 23 -29 20 -51 -16z");
        paths.add(
                "M6990 3808 c-40 -22 -57 -83 -43 -164 19 -107 74 -148 198 -147 69 0 124 20 160 59 62 67 0 157 -158 229 -68 31 -125 39 -157 23z");
        paths.add(
                "M9315 3713 c-11 -2 -31 -17 -45 -32 -30 -33 -266 -231 -470 -393 -550 -436 -822 -586 -1125 -620 -121 -14 -280 0 -690 60 -148 22 -291 43 -317 46 l-48 6 0 -53 c0 -29 -9 -77 -20 -107 -11 -31 -20 -80 -20 -111 l0 -56 188 -47 c375 -92 548 -119 757 -119 187 -1 241 12 435 103 80 37 172 76 205 85 188 54 501 266 953 643 275 230 452 390 469 428 16 32 15 35 -3 55 -21 23 -74 54 -87 51 -12 -3 -82 35 -96 52 -13 15 -44 19 -86 9z");
        paths.add(
                "M2376 3638 c-49 -84 -294 -449 -369 -550 -89 -119 -195 -228 -221 -228 -9 0 -25 18 -36 40 -47 93 -209 266 -391 420 -219 184 -378 275 -482 275 -72 0 -88 -16 -116 -113 -66 -225 -55 -379 59 -841 80 -322 203 -580 411 -858 93 -123 259 -290 321 -323 l48 -25 36 20 c109 59 503 442 1070 1041 602 636 572 599 519 648 -14 13 -25 28 -25 34 0 18 -50 39 -106 45 l-52 6 -89 -97 c-336 -362 -807 -830 -1032 -1024 -199 -172 -346 -268 -412 -268 -123 0 -310 337 -453 818 -74 248 -136 534 -136 628 0 55 18 70 62 51 76 -33 173 -129 609 -602 91 -99 185 -193 208 -209 l42 -30 35 19 c75 40 325 323 660 748 79 100 147 193 150 206 7 25 5 27 -76 93 -9 7 -37 23 -63 35 -26 13 -47 26 -47 30 0 13 -35 31 -71 38 -31 6 -36 3 -53 -27z");
        paths.add(
                "M7333 3186 c-17 -14 -36 -39 -43 -56 -11 -27 -9 -37 23 -102 64 -135 131 -199 190 -184 65 16 84 161 35 268 -31 66 -61 89 -127 96 -40 3 -52 0 -78 -22z");
        paths.add(
                "M6207 2790 c-59 -11 -101 -36 -93 -57 10 -26 -180 -594 -317 -943 -232 -594 -445 -952 -589 -991 -150 -40 -368 258 -554 756 -50 137 -154 467 -149 474 2 2 16 -6 32 -17 59 -41 367 -205 410 -218 104 -32 192 -4 280 87 61 64 112 143 223 344 45 83 102 178 126 213 l43 62 -72 0 c-95 0 -305 28 -339 45 -16 8 -28 10 -28 6 0 -18 -102 -164 -145 -206 -50 -50 -74 -55 -146 -30 -162 58 -350 95 -479 95 -201 0 -299 -76 -308 -240 -8 -130 74 -525 144 -705 119 -300 386 -706 619 -941 127 -127 191 -166 278 -167 100 -1 161 48 351 282 216 265 315 456 650 1251 150 355 269 610 285 610 18 0 61 108 78 195 9 47 14 87 12 89 -10 10 -266 15 -312 6z");
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
        svg.setScaleX(0.0005);
        svg.setScaleY(0.0005);
        svg.setRotate(180);
        Label label = new Label();
        label.setShape(svg);
        label.setBackground(new Background(new BackgroundFill(Color.HOTPINK, CornerRadii.EMPTY, Insets.EMPTY)));
        
        return label;
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
//		snapshot = new WritableImage(snapshot.getPixelReader(), xoffset, 0, width, height);
//		Image img = snapshot;
        ImageView iv2 = new ImageView(snapshot);
//		iv2.setImage(img);
        return iv2;
//		iv2.setFitWidth(width / 2);
//		iv2.setFitHeight(height / 2);
//		iv2.setPreserveRatio(true);
//		iv2.setSmooth(true);
//		iv2.setCache(true);
//		snapshot = iv2.snapshot(new SnapshotParameters(), null);
//		File path = new File("/var/mp/" + PrjNumHolder.getPrjNum() + "/images/" + getClass().getSimpleName() + ".png");
//		System.err.println(path.getAbsolutePath());
//		try {
//			ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", path);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
    }
}
