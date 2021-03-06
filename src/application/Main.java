package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Main extends Application {
	private int highestNum = 0;
	private VBox vbox = new VBox();
	private ArrayList<XYChart.Data> data = new ArrayList<XYChart.Data>();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void start(Stage stage) throws FileNotFoundException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open CSV File");
		Button fileImporter = new Button();
		fileImporter.setText("Import CSV");
		ExtensionFilter filter = new ExtensionFilter("Data Files", "*.csv");
		fileChooser.getExtensionFilters().addAll(filter);

		VBox right = new VBox();
		Text text = new Text();
		Text rtext = new Text();
		Text mtext = new Text();
		Text btext = new Text();
		HBox rbox = new HBox();
		HBox mbox = new HBox();
		HBox bbox = new HBox();
		rtext.setText("The r-squared value is: ");
		mtext.setText("The slope value is: ");
		btext.setText("The intercept value is: ");
		TextField r = new TextField();
		TextField m = new TextField();
		TextField b = new TextField();
		rbox.getChildren().addAll(rtext, r);
		mbox.getChildren().addAll(mtext, m);
		bbox.getChildren().addAll(btext, b);
		mtext.setFont(new Font(16));
		btext.setFont(new Font(16));
		r.setEditable(false);
		m.setEditable(false);
		b.setEditable(false);
		Button submit = new Button();
		submit.setText("Submit All Info");
		text.setText("Graph Information");
		text.setFont(Font.font(24));
		rtext.setText("The r-squared value is:");
		rtext.setFont(Font.font(16));
		TextField xAxisTitle = new TextField();
		TextField yAxisTitle = new TextField();
		TextField chartTitle = new TextField();
		xAxisTitle.setPromptText("X Axis Title");
		yAxisTitle.setPromptText("Y Axis Title");
		chartTitle.setPromptText("Chart Title");
		right.getChildren().addAll(text, xAxisTitle, yAxisTitle, chartTitle, submit, rbox, mbox, bbox, fileImporter);
		right.setSpacing(15);
		vbox.setPrefSize(DataInput.USE_COMPUTED_SIZE, 0);
		GridPane root = new GridPane();
		BorderPane holder = new BorderPane();
		ScrollPane scroll = new ScrollPane();
		scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		scroll.setContent(vbox);
		holder.setLeft(scroll);
		LineChart<Number, Number> sc = new LineChart<>(new NumberAxis(), new NumberAxis());
		holder.setCenter(sc);
		holder.setRight(right);
		DataInput initial = new DataInput(0);
		vbox.getChildren().add(initial);
		root.getChildren().add(holder);
//		HBox hbox = new HBox();
		XYChart.Series series1 = new XYChart.Series();

		XYChart.Series series2 = new XYChart.Series();
		series1.setName("Data Points");
		series2.setName("Line of Best Fit");

		sc.setAnimated(false);
		sc.setCreateSymbols(true);

		sc.getData().addAll(series1, series2);
		fileImporter.setOnAction(e -> {
			File selectedFile = fileChooser.showOpenDialog(stage);
			vbox.getChildren().clear();
			vbox.getChildren().add(initial);
			initial.x.setText("");
			initial.y.setText("");
			try {
				BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
				String line = reader.readLine();
				String[] lineArray = line.split(",");
				if (lineArray.length != 2) {
					sc.getXAxis().setLabel("Incorrect Format");
					sc.getYAxis().setLabel("Incorrect Format");
					r.setText("");
					m.setText("");
					b.setText("");
					xAxisTitle.setText("");
					yAxisTitle.setText("");
					chartTitle.setText("");
					sc.setTitle("");
					sc.getData().clear();
					highestNum = 0;
					return;
				}
				lineArray[0] = lineArray[0].substring(3);
				sc.getXAxis().setLabel(lineArray[0]);
				sc.getYAxis().setLabel(lineArray[1]);
				xAxisTitle.setText(lineArray[0]);
				yAxisTitle.setText(lineArray[1]);
				chartTitle.setText(lineArray[1] + " vs " + lineArray[0]);

				line = reader.readLine();
				if (line != null) {
					lineArray = line.split(",");
					((DataInput) (vbox.getChildren().get(0))).x.setText(lineArray[0]);
					((DataInput) (vbox.getChildren().get(0))).y.setText(lineArray[1]);

				}
				line = reader.readLine();
				while (line != null) {
					lineArray = line.split(",");
					highestNum++;
					vbox.getChildren().add(new DataInput(highestNum, lineArray[0], lineArray[1]));
					line = reader.readLine();

				}
				submit.fire();
				reader.close();
			} catch (FileNotFoundException e1) {
				sc.getXAxis().setLabel("FILE NOT FOUND");
				sc.getYAxis().setLabel("FILE NOT FOUND");
				r.setText("");
				m.setText("");
				b.setText("");
				xAxisTitle.setText("");
				yAxisTitle.setText("");
				chartTitle.setText("");
				sc.setTitle("");
				sc.getData().clear();
				highestNum = 0;
				return;
			} catch (IOException e1) {
				sc.getXAxis().setLabel("IO EXCEPTION");
				sc.getYAxis().setLabel("IO EXCEPTION");
				r.setText("");
				m.setText("");
				b.setText("");
				xAxisTitle.setText("");
				yAxisTitle.setText("");
				chartTitle.setText("");
				sc.setTitle("");
				sc.getData().clear();
				highestNum = 0;
				return;
			} catch (NullPointerException e1) {
				r.setText("");
				m.setText("");
				b.setText("");
				xAxisTitle.setText("");
				yAxisTitle.setText("");
				chartTitle.setText("");
				sc.setTitle("");
				sc.getData().clear();
				highestNum = 0;

			}

		});
		Scene scene = new Scene(root, 1200, 400);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("Linear Regression Calculator");
		stage.show();
		stage.getIcons().add(new Image(new FileInputStream("src/application/favicon.png")));

		submit.setOnAction(e -> {

			series1.getData().clear();
			series2.getData().clear();
//			sc.getData().clear();
			sc.getXAxis().setLabel(xAxisTitle.getText());
			sc.getYAxis().setLabel(yAxisTitle.getText());
			sc.setTitle(chartTitle.getText());
			ArrayList xValues = new ArrayList<Integer>();
			ArrayList yValues = new ArrayList<Integer>();
			boolean error = false;

			sc.getData().clear();
			if (vbox.getChildren().size() <= 1) {
				error = true;
				sc.getXAxis().setLabel("NOT ENOUGH DATA. TRY AGAIN");
				sc.getYAxis().setLabel("NOT ENOUGH DATA. TRY AGAIN");
				r.setText("");
				m.setText("");
				b.setText("");
			}
			if (error) {
				return;
			}
			for (Node node : vbox.getChildren()) {
				try {
					((DataInput) node).x.setStyle("-fx-control-inner-background: #FFFFFF ");
					((DataInput) node).y.setStyle("-fx-control-inner-background: #FFFFFF ");
					series1.getData().add(new XYChart.Data(Double.parseDouble(((DataInput) node).x.getText()),
							Double.parseDouble(((DataInput) node).y.getText())));
					xValues.add(Double.parseDouble(((DataInput) node).x.getText()));
					yValues.add(Double.parseDouble(((DataInput) node).y.getText()));
				} catch (Exception exception) {

					((DataInput) node).x.setStyle("-fx-control-inner-background: #FF9494 ");
					((DataInput) node).y.setStyle("-fx-control-inner-background: #FF9494 ");
					sc.getXAxis().setLabel("DATA IS EMPTY. TRY AGAIN");
					sc.getYAxis().setLabel("DATA IS EMPTY. TRY AGAIN");
					error = true;
				}
			}
			if (error) {
				return;
			}
			sc.getData().addAll(series1, series2);

			int n = xValues.size();

			double sumOfX = 0;
			for (int i = 0; i < xValues.size(); i++) {
				sumOfX += (double) xValues.get(i);
			}
			double sumOfY = 0;
			for (int i = 0; i < yValues.size(); i++) {
				sumOfY += (double) yValues.get(i);
			}

			double sumOfXSquared = 0;
			for (int i = 0; i < xValues.size(); i++) {
				sumOfXSquared += Math.pow((double) xValues.get(i), 2);
			}

			double sumOfYSquared = 0;
			for (int i = 0; i < yValues.size(); i++) {
				sumOfYSquared += Math.pow((double) yValues.get(i), 2);
			}

			double sumOfXY = 0;
			for (int i = 0; i < xValues.size(); i++) {
				sumOfXY += (double) xValues.get(i) * (double) yValues.get(i);
			}

			double rValue = (n * sumOfXY - sumOfX * sumOfY) / (Math
					.sqrt((n * sumOfXSquared - Math.pow(sumOfX, 2)) * (n * sumOfYSquared - Math.pow(sumOfY, 2))));

			double averageX = sumOfX / n;
			double averageY = sumOfY / n;

			double numerator = 0;
			for (int i = 0; i < xValues.size(); i++) {
				numerator += ((double) xValues.get(i) - averageX) * ((double) yValues.get(i) - averageY);
			}
			double denominator = 0;
			for (int i = 0; i < xValues.size(); i++) {
				denominator += Math.pow(((double) xValues.get(i) - averageX), 2);
			}

			double mValue = numerator / denominator;
			double bValue = averageY - mValue * averageX;
//			System.out.println(mValue);
//			System.out.println(bValue);

			r.setText("" + Math.pow(rValue, 2));
			m.setText("" + mValue);
			b.setText("" + bValue);

			double max = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < xValues.size(); i++) {
				max = Math.max(max, (double) xValues.get(i));
			}
			series2.getData().add(new XYChart.Data(0, bValue));
			series2.getData().add(new XYChart.Data(max, mValue * max + bValue));
//			series2.getData().add(new XYChart.Data(6.4, 15.6));
		});
		NumberBinding maxScale = Bindings.min(root.widthProperty().divide(1200), root.heightProperty().divide(400));

		final int width = 1200;
		final int height = 400;
		Scale scale = new Scale(1, 1, 0, 0);

		scale.xProperty().bind(root.widthProperty().divide(width));
		scale.yProperty().bind(root.heightProperty().divide(height));
		root.getTransforms().add(scale);
		stage.setMinHeight(400);
		stage.setMinWidth(1200);
	}

	public class DataInput extends HBox {
		TextField x = new TextField();
		TextField y = new TextField();
		Button add = new Button();
		Button remove = new Button();
		Button empty = new Button();

		int num;

		public DataInput(int num) {
			if (num == 0) {
				remove.setVisible(false);
				remove.disableProperty().set(false);
			}
			empty.setVisible(false);
			empty.disableProperty().set(false);

			this.getChildren().addAll(x, y, add, remove, empty);
			add.setText("+");
			remove.setText("X");
			this.num = num;
			x.setPromptText("x" + this.num);
			y.setPromptText("y" + this.num);

			add.setOnAction(e -> {
				highestNum++;
				vbox.getChildren().add(new DataInput(highestNum));

			});
			remove.setOnAction(e -> {
				highestNum--;
				vbox.getChildren().remove(this.num);
				Node node = null;
				for (int i = 0; i < vbox.getChildren().size(); i++) {
					node = vbox.getChildren().get(i);
					if (((DataInput) node).num != 0 && ((DataInput) node).num >= this.num) {
						((DataInput) node).num--;
					}

					((DataInput) node).x.setPromptText("x" + ((DataInput) node).num);
					((DataInput) node).y.setPromptText("y" + ((DataInput) node).num);
				}
				x = null;
				y = null;
				add = null;
				remove = null;

			});
		}

		public DataInput(int num, String xInput, String yInput) {
			if (num == 0) {
				remove.setVisible(false);
				remove.disableProperty().set(false);
			}
			empty.setVisible(false);
			empty.disableProperty().set(false);

			this.getChildren().addAll(x, y, add, remove, empty);
			add.setText("+");
			remove.setText("X");
			this.num = num;
			x.setPromptText("x" + this.num);
			y.setPromptText("y" + this.num);
			x.setText((xInput));
			y.setText((yInput));
			add.setOnAction(e -> {
				highestNum++;
				vbox.getChildren().add(new DataInput(highestNum));

			});
			remove.setOnAction(e -> {
				highestNum--;
				vbox.getChildren().remove(this.num);
				Node node = null;
				for (int i = 0; i < vbox.getChildren().size(); i++) {
					node = vbox.getChildren().get(i);
					if (((DataInput) node).num != 0 && ((DataInput) node).num >= this.num) {
						((DataInput) node).num--;
					}

					((DataInput) node).x.setPromptText("x" + ((DataInput) node).num);
					((DataInput) node).y.setPromptText("y" + ((DataInput) node).num);
				}
				x = null;
				y = null;
				add = null;
				remove = null;

			});
		}

	}

	public static void main(String[] args) {
		launch(args);
	}
}
