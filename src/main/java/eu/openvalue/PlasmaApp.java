package eu.openvalue;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritablePixelFormat;
import javafx.stage.Stage;

public class PlasmaApp extends Application {

    public static double time = 0;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("JavaFX canvas plasma");
        int width = 640;
        int height = 480;
        GraphicsContext context = createCanvasContext(width, height, stage);
        stage.show();

        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                time += 0.15;
                int[] plasmaImage = renderSinglePlasmaImage(width, height, time);
                writeImageToCanvas(plasmaImage, context, width, height);
            }
        }.start();
    }

    private GraphicsContext createCanvasContext(int width, int height, Stage stage) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext context = canvas.getGraphicsContext2D();
        Group root = new Group();
        stage.setScene(new Scene(root));
        root.getChildren().add(context.getCanvas());
        return context;
    }

    private int colorToRgb(double color) {
        int alpha = 255;
        int r = 255;
        int g = (int) ((255 + 255 * Math.cos(color * Math.PI * 2)) / 2.25);
        int b = (int) ((255 + 255 * Math.sin(color * Math.PI * 2)) / 2.25);

        // pack rgb into single argb int value
        return alpha << 24 | r << 16 | g << 8 | b;
    }

    private void writeImageToCanvas(int[] plasmaImage, GraphicsContext context, int width, int height) {
        context.getPixelWriter().setPixels(0, 0, width, height, WritablePixelFormat.getIntArgbInstance(), plasmaImage, 0, width);
    }

    private int[] renderSinglePlasmaImage(int width, int height, double timeOffset) {
        int[] imageMatrix = new int[width*height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double color = calcPlasmaPixel(width, x, y, timeOffset);
                int rgb = colorToRgb((float)color);
                imageMatrix[y* width +x] = rgb;
            }
        }
        return imageMatrix;
    }

    double calcPlasmaPixel(int width, int x, int y, double time) {
        double size = width/15;
        double result = 0;

        result += Math.sin(x/size+time);
        result += Math.sin((y/size+time)/2.0);
        result += Math.sin((x/size+y/size+time)/2.0);

        double cx=x/size/10+0.8*Math.sin(time/4);
        double cy=y/size/10+0.8*Math.cos(time/3);
        result+=Math.sin(Math.sqrt(100*(cx*cx+cy*cy+1))+time);

        return result;
    }

}
