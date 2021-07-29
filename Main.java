import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;

import org.json.JSONException;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application  {
	
	final int W = Toolkit.getDefaultToolkit().getScreenSize().width;
	final int H = Toolkit.getDefaultToolkit().getScreenSize().height;
	final Color BACKGROUND_COLOR = Color.rgb(255,164,27);
	final Color SIDE_COLOR = Color.rgb(0,80,130);
	final Color TEXT_COLOR = Color.rgb(0,8,57);
	final Color BUTTON_COLOR = Color.rgb(0,168,204);
	final Color TRANSPARENT_COLOR = Color.rgb(0,8,57,0.15);
	int currentIndex = 0;
	NumberAxis xAxis = null;
	NumberAxis yAxis = null;
	double maxPrice = 1.0;
	int tickInterval = (int) maxPrice/35;
	XYChart.Series series1 = null;
	XYChart.Series series2 = null;
	ScatterChart<Number,Number> sc = null;
	Label quotasLabel = null;
	StockPortfolio StockPortfolio = new StockPortfolio();
	String chartTitle = "";
	
	NumberAxis xAxis2 = null;
	NumberAxis yAxis2 = null;
	XYChart.Series series12 = null;
	XYChart.Series series22 = null;
	ScatterChart<Number,Number> sc2 = null;
	Label quotasLabel2 = null;
	StockPortfolio StockPortfolio2 = new StockPortfolio();
	String chartTitle2 = "";
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Scanner symbolsFileScanner;
		try {
			symbolsFileScanner = new Scanner(new File("bin/symbols.txt"));
			while(symbolsFileScanner.hasNext()) {
				StockPortfolio.symbols.add(symbolsFileScanner.next());
			}
			symbolsFileScanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		VBox quotas = new VBox();
		quotas.setSpacing(10);
		quotas.setLayoutX(W*0.275);
		quotas.setLayoutY(H*0.46);
				
		quotasLabel = new Label("Predicted Prices");
		quotasLabel.setFont(Font.font("Georgia",FontWeight.BOLD,25));
		quotasLabel.setTextFill(TEXT_COLOR);
		quotas.getChildren().add(quotasLabel);
		
		series1 = new XYChart.Series();
		xAxis = new NumberAxis();
		yAxis = new NumberAxis();
		xAxis.setAutoRanging(true);
		yAxis.setAutoRanging(true);
		xAxis.setLabel("Days since " + LocalDate.now().minusDays(21).toString());
		yAxis.setLabel("Price");
		sc = new ScatterChart<Number, Number>(xAxis, yAxis);
		sc.setLayoutX(W*0.26);
		sc.setLayoutY(H*0.13);
		sc.setPrefWidth(W*0.36);
	
		Image emptyImage = new Image("emptyIcon.png");
		ImageView emptyImageView = new ImageView(emptyImage);
		emptyImageView.setLayoutX(W*0.23);
				
		Group homeGroup = new Group();
		Group epg = new Group();
		Group helpGroup = new Group();
		Group infoGroup = new Group();
		Group quickAnalyzeGroup = new Group();
		
		Scene homeScene = new Scene(homeGroup);
		Scene epScene = new Scene(epg);
		Scene helpScene = new Scene(helpGroup);
		Scene infoScene = new Scene(infoGroup);
		Scene quickAnalyzeScene = new Scene(quickAnalyzeGroup);
		
		quickAnalyzeScene.setFill(BACKGROUND_COLOR);
		
		series12 = new XYChart.Series();
		xAxis2 = new NumberAxis();
		yAxis2 = new NumberAxis();
		yAxis2.setLabel("Price");
		xAxis2.setLabel("Days since " + LocalDate.now().minusDays(21).toString());
		xAxis2.setAutoRanging(true);
		yAxis2.setAutoRanging(true);
		sc2 = new ScatterChart<Number,Number>(xAxis2,yAxis2);
		sc2.setLayoutX(W*0.14);
		sc2.setLayoutY(H*0.12);
		sc2.setPrefWidth(W*0.36);
		
		VBox quotas2 = new VBox();
		quotas2.setSpacing(10);
		quotas2.setLayoutX(W*0.18);
		quotas2.setLayoutY(H*0.46);
				
		quotasLabel2 = new Label("Predicted Prices");
		quotasLabel2.setFont(Font.font("Georgia",FontWeight.BOLD,25));
		quotasLabel2.setTextFill(TEXT_COLOR);
		quotas2.getChildren().add(quotasLabel2);
		quotas2.setVisible(false);
		
		TextField symbolTextField = new TextField("Symbol");
		symbolTextField.setLayoutX(W*0.16);
		symbolTextField.setLayoutY(H*0.08);
		symbolTextField.setPrefWidth(W*0.1);
		symbolTextField.setPrefHeight(H*0.03);
		symbolTextField.setFont(Font.font("Georgia",20));
		
		Button GetForecast = new Button("Get Forecast");
		GetForecast.setLayoutX(W*0.275);
		GetForecast.setLayoutY(H*0.08);
		GetForecast.setPrefWidth(W*0.1);
		GetForecast.setPrefHeight(H*0.03);
		GetForecast.setStyle("-fx-background-color:rgb(0,168,204);");
		GetForecast.setTextFill(TEXT_COLOR);
		GetForecast.setFont(Font.font("Georgia",20));
		
		GetForecast.setOnMouseEntered(e-> {
			GetForecast.setStyle("-fx-background-color: rgb(0,140,204);");
		});
		
		GetForecast.setOnMouseExited(e-> {
			GetForecast.setStyle("-fx-background-color: rgb(0,168,204);");
		});
		
		GetForecast.setOnAction(e-> {
			quotas2.setVisible(false);
			series12.getData().clear();
			sc2.getData().clear();
			quotas2.getChildren().clear();
			quotasLabel2 = new Label("Predicted Prices");
			quotasLabel2.setFont(Font.font("Georgia",FontWeight.BOLD,25));
			quotasLabel2.setTextFill(TEXT_COLOR);
			quotas2.getChildren().add(quotasLabel2);
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.initOwner(primaryStage);
			alert.setTitle("Error");
			alert.setHeaderText("Error in finding symbol");
			alert.setContentText("The symbol " + symbolTextField.getText() + " does not exist");
			if(StockReader.isValidSymbol(symbolTextField.getText()) == false) {
				alert.showAndWait();
			} else {
				try {
					double[][] lastPrices = Stock.getLastPricesFor(symbolTextField.getText());
					double[] nextPrices = Stock.getNextPrices(5, symbolTextField.getText());
					for(int i = 0;i<lastPrices.length;i++) {
						double price = lastPrices[i][0];
						price = Double.parseDouble(new DecimalFormat("##.##").format(price));
						series12.getData().add(new XYChart.Data(i+1,price));
					}
					for(int i = 0;i<nextPrices.length;i++) {
						double price = lastPrices[i][0];
						price = Double.parseDouble(new DecimalFormat("##.##").format(price));
						String labelString = LocalDate.now().plusDays(i+1).toString() + " : " + Double.toString(price);
						Label currentLabel = new Label(labelString);
						currentLabel.setFont(Font.font("Georgia",20));
						currentLabel.setTextFill(TEXT_COLOR);
						quotas2.getChildren().add(currentLabel);
					}
				} catch (IOException | JSONException e1) {
					e1.printStackTrace();
				}
				String currentStock = symbolTextField.getText();
				String currentChartTitle = "";
				try {
					currentChartTitle = currentStock + "(" + StockReader.getCompanyName(currentStock) + ")" + " Previous Prices";
				} catch (IOException | JSONException e1) {
					e1.printStackTrace();
				}
				quotas2.setVisible(true);
				sc2.setTitle(currentChartTitle);
				sc2.getData().add(series12);
			}
		});
		
		Button homeButton4 = new Button("Home");
		homeButton4.setLayoutX(W*0.01);
		homeButton4.setLayoutY(H*0.01);
		homeButton4.setPrefWidth(W*0.078);
		homeButton4.setFont(Font.font("Georgia",FontWeight.BOLD,15));
		homeButton4.setStyle("-fx-background-color: rgb(0,168,204);");
		homeButton4.setTextFill(TEXT_COLOR);
		
		homeButton4.setOnMouseEntered(e-> {
			homeButton4.setStyle("-fx-background-color: rgb(0,140,204);");
		});
		
		homeButton4.setOnMouseExited(e-> {
			homeButton4.setStyle("-fx-background-color: rgb(0,168,204);");
		});
		
		homeButton4.setOnAction(e-> {
			primaryStage.setScene(homeScene);
			primaryStage.setFullScreen(true);
		});
		
		quickAnalyzeGroup.getChildren().addAll(sc2,homeButton4,symbolTextField,GetForecast,quotas2);
		
		homeGroup.getChildren().add(sc);
		
		infoScene.setFill(BACKGROUND_COLOR);
		
		Button homeButton3 = new Button("Home");
		homeButton3.setLayoutX(W*0.01);
		homeButton3.setLayoutY(H*0.01);
		homeButton3.setPrefWidth(W*0.078);
		homeButton3.setFont(Font.font("Georgia",FontWeight.BOLD,15));
		homeButton3.setStyle("-fx-background-color: rgb(0,168,204);");
		homeButton3.setTextFill(TEXT_COLOR);
		
		homeButton3.setOnMouseEntered(e-> {
			homeButton3.setStyle("-fx-background-color: rgb(0,140,204);");
		});
		
		homeButton3.setOnMouseExited(e-> {
			homeButton3.setStyle("-fx-background-color: rgb(0,168,204);");
		});
		
		homeButton3.setOnAction(e-> {
			primaryStage.setScene(homeScene);
			primaryStage.setFullScreen(true);
		});
		
		Label labelOne = new Label("This is a stock forecasting tool made by John Gunasar");
		labelOne.setLayoutX(W*0.01);
		labelOne.setLayoutY(H*0.05);
		labelOne.setFont(Font.font("Georgia",FontWeight.SEMI_BOLD,30));
		labelOne.setTextFill(SIDE_COLOR);
		
		Label labelTwo = new Label("It uses sophisticated mathematical methods to predict the next price of a stock");
		labelTwo.setLayoutX(W*0.01);
		labelTwo.setLayoutY(H*0.1);
		labelTwo.setFont(Font.font("Georgia",FontWeight.SEMI_BOLD,30));
		labelTwo.setTextFill(SIDE_COLOR);
		
		Label labelThree = new Label("based on the given stocks previous behavior");
		labelThree.setLayoutX(W*0.01);
		labelThree.setLayoutY(H*0.14);
		labelThree.setFont(Font.font("Georgia",FontWeight.SEMI_BOLD,30));
		labelThree.setTextFill(SIDE_COLOR);
		
		Label labelFour = new Label("DEVELOPER CONTACT INFORMATION: ");
		labelFour.setLayoutX(W*0.01);
		labelFour.setLayoutY(H*0.24);
		labelFour.setFont(Font.font("Georgia",FontWeight.BOLD,40));
		labelFour.setTextFill(SIDE_COLOR);
		
		Label labelFive = new Label("Email: tekingunasar@gmail.com");
		labelFive.setLayoutX(W*0.01);
		labelFive.setLayoutY(H*0.29);
		labelFive.setFont(Font.font("Georgia",FontWeight.SEMI_BOLD,40));
		labelFive.setTextFill(SIDE_COLOR);
		
		Label labelSix = new Label("Kaching or John Gunasar is not legally or fiscally responsible for any losses made with this forecasting tool");
		labelSix.setLayoutX(W*0.005);
		labelSix.setLayoutY(H*0.4);
		labelSix.setFont(Font.font("Georgia",FontWeight.EXTRA_BOLD,23));
		labelSix.setTextFill(SIDE_COLOR);
		
		Image logo3 = new Image("smallLogo.png");
		ImageView logo3view = new ImageView(logo3);
		logo3view.setLayoutX(W*0.25);
		logo3view.setLayoutY(H*0.5);
		
		infoGroup.getChildren().addAll(homeButton3,labelOne,labelTwo,labelThree,labelFour,labelFive,labelSix,logo3view);
		
		helpScene.setFill(BACKGROUND_COLOR);
		
		Button homeButton2 = new Button("Home");
		homeButton2.setLayoutX(W*0.01);
		homeButton2.setLayoutY(H*0.01);
		homeButton2.setPrefWidth(W*0.078);
		homeButton2.setFont(Font.font("Georgia",FontWeight.BOLD,15));
		homeButton2.setStyle("-fx-background-color: rgb(0,168,204);");
		homeButton2.setTextFill(TEXT_COLOR);
		
		Label editPortfolioTutorial = new Label("Getting Started");
		editPortfolioTutorial.setLayoutX(W*0.01);
		editPortfolioTutorial.setLayoutY(H*0.05);
		editPortfolioTutorial.setFont(Font.font("Georgia",FontWeight.BOLD,40));
		editPortfolioTutorial.setTextFill(SIDE_COLOR);
		
		Label step1 = new Label("1. To start forecasting, click the edit portfolio button on the home screen");
		step1.setLayoutX(W*0.01);
		step1.setLayoutY(W*0.01);
		step1.setLayoutY(H*0.1);
		step1.setFont(Font.font("Georgia",FontWeight.SEMI_BOLD,30));
		step1.setTextFill(SIDE_COLOR);
		
		Label step2 = new Label("2. In the input box, type the symbol of the company you would like to forecast");
		step2.setLayoutX(W*0.01);
		step2.setLayoutY(H*0.15);
		step2.setFont(Font.font("Georgia",FontWeight.SEMI_BOLD,30));
		step2.setTextFill(SIDE_COLOR);
		
		Label step3 = new Label("3. Click the home button and you will see the forecast of the first stock on your portfolio");
		step3.setLayoutX(W*0.01);
		step3.setLayoutY(H*0.2);
		step3.setFont(Font.font("Georgia",FontWeight.SEMI_BOLD,30));
		step3.setTextFill(SIDE_COLOR);
		
		Label step4 = new Label("4. The graph has previous prices in orange, and the predicted prices in red");
		step4.setLayoutX(W*0.01);
		step4.setLayoutY(H*0.25);
		step4.setFont(Font.font("Georgia",FontWeight.SEMI_BOLD,30));
		step4.setTextFill(SIDE_COLOR);
		
		Label step5 = new Label("Analyze a single stock");
		step5.setLayoutX(W*0.01);
		step5.setLayoutY(H*0.3);
		step5.setFont(Font.font("Georgia",FontWeight.BOLD,40));
		step5.setTextFill(SIDE_COLOR);
		
		Label step6 = new Label("You can also just quickly analyze a single stock without adding it to your portfolio");
		step6.setLayoutX(W*0.01);
		step6.setLayoutY(H*0.35);
		step6.setFont(Font.font("Georgia",FontWeight.SEMI_BOLD,30));
		step6.setTextFill(SIDE_COLOR);
		
		char quote = '"';
		String quickAnalyze = quote + "Quick Analyze" + quote;
		Label step7 = new Label("Click the button labeled " + quickAnalyze);
		step7.setLayoutX(W*0.01);
		step7.setLayoutY(H*0.4);
		step7.setFont(Font.font("Georgia",FontWeight.SEMI_BOLD,30));
		step7.setTextFill(SIDE_COLOR);
		
		String getForecast = quote + "Get Forecast" + quote;
		Label step8 = new Label("Enter the symbol you want a quick forecast of and press the button labeled " + getForecast);
		step8.setLayoutX(W*0.01);
		step8.setLayoutY(H*0.45);
		step8.setFont(Font.font("Georgia",FontWeight.SEMI_BOLD,30));
		step8.setTextFill(SIDE_COLOR);
		
		homeButton2.setOnMouseEntered(e-> {
			homeButton2.setStyle("-fx-background-color: rgb(0,140,204);");
		});
		
		homeButton2.setOnMouseExited(e-> {
			homeButton2.setStyle("-fx-background-color: rgb(0,168,204);");
		});
		
		homeButton2.setOnAction(e-> {
			primaryStage.setScene(homeScene);
			primaryStage.setFullScreen(true);
		});
		
		Image logo2 = new Image("smallLogo.png");
		ImageView logo2view = new ImageView(logo2);
		logo2view.setLayoutX(W*0.25);
		logo2view.setLayoutY(H*0.5);
		helpGroup.getChildren().addAll(homeButton2,editPortfolioTutorial,step1,step2,step3,step4,
				step5,step6,step7,step8,logo2view);
		
		epScene.setFill(BACKGROUND_COLOR);
		TextField symbol = new TextField("Symbol");
		symbol.setLayoutX(W*0.12);
		symbol.setLayoutY(H*0.01);
		symbol.setPrefWidth(W*0.104);
		symbol.setPrefHeight(H*0.046);
		symbol.setFont(Font.font("Georgia",20));
		
		VBox symbolsVBox = new VBox();
		symbolsVBox.setSpacing(H*0.005);
		symbolsVBox.setLayoutX(W*0.01);
		symbolsVBox.setLayoutY(H*0.15);
		
		VBox symbolsVBoxTwo = new VBox();
		symbolsVBoxTwo.setSpacing(H*0.005);
		symbolsVBoxTwo.setLayoutX(W*0.05);
		symbolsVBoxTwo.setLayoutY(H*0.2075);
				
		for(int i = 0;i<StockPortfolio.symbols.size();i++) {
			Label currentLabel = new Label(StockPortfolio.symbols.get(i));
			currentLabel.setFont(Font.font("Georgia",FontWeight.BOLD,40));
			currentLabel.setTextFill(TEXT_COLOR);
			if(i<10) {
				symbolsVBox.getChildren().add(currentLabel);
			} else {
				symbolsVBoxTwo.getChildren().add(currentLabel);
			}
		}
		
		Label none = new Label("Your portfolio is empty");
		none.setLayoutX(W*0.17);
		none.setLayoutY(H*0.25);
		none.setFont(Font.font("Georgia",60));
		none.setTextFill(TRANSPARENT_COLOR);
	
		Label titleLabel = new Label("Current Stocks: ");
		titleLabel.setFont(Font.font("Georgia",FontWeight.BOLD,50));
		titleLabel.setTextFill(TEXT_COLOR);
		titleLabel.setLayoutX(W*0.01);
		titleLabel.setLayoutY(H*0.1);
		
		Button add = new Button("Add");
		add.setLayoutX(W*0.12);
		add.setLayoutY(H*0.055);
		add.setStyle("-fx-background-color: rgb(0,80,130);");
		add.setTextFill(TEXT_COLOR);
		add.setPrefWidth(W*0.052);
		add.setPrefHeight(H*0.046);
		add.setFont(Font.font("Georgia",FontWeight.BOLD,15));
		add.setBorder(new Border(new BorderStroke(Color.BLACK, 
	            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		
		add.setOnAction(e-> {
			Alert alert = new Alert(AlertType.ERROR);
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.initOwner(primaryStage);
			alert.setTitle("Error");
			alert.setHeaderText("Error in finding symbol");
			alert.setContentText("The symbol " + symbol.getText() + " does not exist");
			
			Alert alert2 = new Alert(AlertType.ERROR);
			alert2.initModality(Modality.APPLICATION_MODAL);
			alert2.initOwner(primaryStage);
			alert.setTitle("Error");
			alert2.setHeaderText("Error in adding symbol");
			alert2.setContentText("The symbol " + symbol.getText() + " is already in your portfolio");
		
			if(StockReader.isValidSymbol(symbol.getText()) == false) {
				alert.showAndWait();
			} if(StockPortfolio.isInPortfolio(symbol.getText())==true) {
				alert2.showAndWait();
			} else {
				try {
					StockPortfolio.addStock(symbol.getText());
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
				if(StockPortfolio.symbols.size()>0) {
					none.setVisible(false);
				}
				
				String labelString = "";
				try {
					labelString = symbol.getText() + "(" + StockReader.getCompanyName(symbol.getText()) + ")";
				} catch (IOException | JSONException e1) {
					e1.printStackTrace();
				}
				Label currentLabel = new Label(labelString);
				currentLabel.setFont(Font.font("Georgia",FontWeight.BOLD,40));
				currentLabel.setTextFill(TEXT_COLOR);
				if(StockPortfolio.symbols.size()<10) {
					symbolsVBox.getChildren().add(currentLabel);
				} else {
					symbolsVBoxTwo.getChildren().add(currentLabel);
				}
			}
		});
		
		add.setOnMouseEntered(e-> {
			add.setStyle("-fx-background-color: rgb(0,50,130);");
		});
		
		add.setOnMouseExited(e-> {
			add.setStyle("-fx-background-color: rgb(0,80,130);");
		});
		
		Button remove = new Button("Remove");
		remove.setLayoutX(W*0.172);
		remove.setLayoutY(H*0.055);
		remove.setStyle("-fx-background-color: rgb(0,80,130);");
		remove.setTextFill(TEXT_COLOR);
		remove.setPrefWidth(W*0.052);
		remove.setPrefHeight(H*0.046);
		remove.setFont(Font.font("Georgia",FontWeight.BOLD,15));
		remove.setBorder(new Border(new BorderStroke(Color.BLACK, 
	            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		
		remove.setOnAction(e-> {
			Alert alert = new Alert(AlertType.ERROR);
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.initOwner(primaryStage);
			alert.setTitle("Error");
			alert.setHeaderText("Error in finding symbol to remove");
			alert.setContentText("The symbol " + symbol.getText() + " does not exist in your portfolio");
			if(StockPortfolio.isInPortfolio(symbol.getText())==false) {
				alert.showAndWait();
			} else {
				StockPortfolio.removeStock(symbol.getText());
				symbolsVBox.getChildren().clear();
				symbolsVBoxTwo.getChildren().clear();
				File prevFile = new File("bin/symbols/" + symbol.getText() + "prev.txt");
				File nextFile = new File("bin/symbols/" + symbol.getText() + ".txt");
				prevFile.delete();
				nextFile.delete();
				
				if(StockPortfolio.symbols.size()==0) {
					none.setVisible(true);
				} 
						
				for(int i = 0;i<StockPortfolio.symbols.size();i++) {
					String currentStock = StockPortfolio.symbols.get(i);
					String label = "";
					try {
						label = currentStock + "(" + StockReader.getCompanyName(currentStock) + ")";
					} catch(IOException e1) {
						e1.printStackTrace();
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					Label currentLabel = new Label(label);
					currentLabel.setFont(Font.font("Georgia",FontWeight.BOLD,40));
					currentLabel.setTextFill(TEXT_COLOR);
					if(i<10) {
						symbolsVBox.getChildren().add(currentLabel);
					} else {
						symbolsVBoxTwo.getChildren().add(currentLabel);
					}
				}
			}
		});
		
		remove.setOnMouseEntered(e-> {
			remove.setStyle("-fx-background-color: rgb(0,50,130);");
		});
		
		remove.setOnMouseExited(e-> {
			remove.setStyle("-fx-background-color: rgb(0,80,130);");
		});
		
		Button homeButton = new Button("Home");
		homeButton.setLayoutX(W*0.01);
		homeButton.setLayoutY(H*0.01);
		homeButton.setPrefWidth(W*0.078);
		homeButton.setFont(Font.font("Georgia",FontWeight.BOLD,15));
		homeButton.setStyle("-fx-background-color: rgb(0,168,204);");
		homeButton.setTextFill(TEXT_COLOR);
		
		homeButton.setOnMouseEntered(e-> {
			homeButton.setStyle("-fx-background-color: rgb(0,140,204);");
		});
		
		homeButton.setOnMouseExited(e-> {
			homeButton.setStyle("-fx-background-color: rgb(0,168,204);");
		});
		
		Label noStocksInPortfolio = new Label("");
		
		epg.getChildren().addAll(homeButton,none,symbol,add,remove,symbolsVBox,symbolsVBoxTwo,titleLabel);
		
		homeScene.setFill(BACKGROUND_COLOR);
		Rectangle menuRectangle = new Rectangle(0,0,W*0.22,H);
		menuRectangle.setFill(SIDE_COLOR);
		
		Image logo = new Image("logo.png");
		ImageView logoImageView = new ImageView(logo);
		logoImageView.setLayoutX(W*0.01);
		logoImageView.setLayoutY(H*-0.01);
		
		Button epb = new Button("Edit Portfolio");
		epb.setLayoutX(menuRectangle.getWidth()/4);
		epb.setLayoutY(H*0.3);
		epb.setPrefWidth(menuRectangle.getWidth()/2);
		epb.setPrefHeight(H*0.05);
		epb.setStyle("-fx-background-color: rgb(0,168,204);");
		epb.setFont(Font.font("Georgia",25));
		epb.setBorder(new Border(new BorderStroke(Color.BLACK, 
	            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		
		epb.setOnAction(e-> {
			
			symbolsVBox.getChildren().clear();
			symbolsVBoxTwo.getChildren().clear();
					
			if(StockPortfolio.symbols.size() > 0) {
				none.setVisible(false);
			} else {
				none.setVisible(true);
			}
			
			for(int i = 0;i<StockPortfolio.symbols.size();i++) {
				String currentStock = StockPortfolio.symbols.get(i);
				String label = "";
				try {
					label = currentStock + "(" + StockReader.getCompanyName(currentStock) + ")";
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Label currentLabel = new Label(label);
				currentLabel.setFont(Font.font("Georgia",FontWeight.BOLD,40));
				currentLabel.setTextFill(TEXT_COLOR);
				if(i<10) {
					symbolsVBox.getChildren().add(currentLabel);
				} else {
					symbolsVBoxTwo.getChildren().add(currentLabel);
				}
			}
			primaryStage.setScene(epScene);
			primaryStage.setFullScreen(true);
		});
	
		epb.setOnMouseEntered(e-> {
			epb.setStyle("-fx-background-color: rgb(0,140,204);");
		});
		
		epb.setOnMouseExited(e-> {
			epb.setStyle("-fx-background-color: rgb(0,168,204);");
		});
		
		Button help = new Button("Help");
		help.setLayoutX(menuRectangle.getWidth()/4);
		help.setLayoutY(H*0.4);
		help.setPrefWidth(menuRectangle.getWidth()/2);
		help.setPrefHeight(H*0.05);
		help.setStyle("-fx-background-color: rgb(0,168,204);");
		help.setFont(Font.font("Georgia",25));
		help.setBorder(new Border(new BorderStroke(Color.BLACK, 
	            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		
		help.setOnMouseEntered(e-> {
			help.setStyle("-fx-background-color: rgb(0,140,204);");
		});
		
		help.setOnMouseExited(e-> {
			help.setStyle("-fx-background-color: rgb(0,168,204);");
		});
		
		help.setOnAction(e-> {
			primaryStage.setScene(helpScene);
			primaryStage.setFullScreen(true);
		});
		
		Button info = new Button("Info");
		info.setLayoutX(menuRectangle.getWidth()/4);
		info.setLayoutY(H*0.5);
		info.setPrefWidth(menuRectangle.getWidth()/2);
		info.setPrefHeight(H*0.05);
		info.setStyle("-fx-background-color: rgb(0,168,204);");
		info.setFont(Font.font("Georgia",25));
		info.setBorder(new Border(new BorderStroke(Color.BLACK, 
	            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		
		info.setOnMouseEntered(e-> {
			info.setStyle("-fx-background-color: rgb(0,140,204);");
		});
		
		info.setOnMouseExited(e-> {
			info.setStyle("-fx-background-color: rgb(0,168,204);");
		});
		
		info.setOnAction(e-> {
			primaryStage.setScene(infoScene);
			primaryStage.setFullScreen(true);
		});
				
		Button quickAnalyzeButton = new Button("Quick Analyze");
		quickAnalyzeButton.setLayoutX(menuRectangle.getWidth()/4);
		quickAnalyzeButton.setLayoutY(H*0.6);
		quickAnalyzeButton.setPrefWidth(menuRectangle.getWidth()/2);
		quickAnalyzeButton.setPrefHeight(H*0.05);
		quickAnalyzeButton.setStyle("-fx-background-color: rgb(0,168,204);");
		quickAnalyzeButton.setFont(Font.font("Georgia",25));
		quickAnalyzeButton.setBorder(new Border(new BorderStroke(Color.BLACK, 
	            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		
		quickAnalyzeButton.setOnMouseEntered(e-> {
			quickAnalyzeButton.setStyle("-fx-background-color: rgb(0,140,204);");
		});
		
		quickAnalyzeButton.setOnMouseExited(e-> {
			quickAnalyzeButton.setStyle("-fx-background-color: rgb(0,168,204);");
		});
		
		quickAnalyzeButton.setOnAction(e-> {
			primaryStage.setScene(quickAnalyzeScene);
			primaryStage.setFullScreen(true);
		});
		
		Button next = new Button("Next");
		next.setLayoutX(W*0.5);
		next.setLayoutY(H*0.05);
		next.setPrefWidth(W*0.1);
		next.setPrefHeight(H*0.05);
		next.setFont(Font.font("Georgia",25));
		next.setStyle("-fx-background-color: rgb(0,168,204);");
		next.setTextFill(TEXT_COLOR);
		next.setBorder(new Border(new BorderStroke(Color.BLACK, 
	            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		next.setOnMouseEntered(e-> {
			next.setStyle("-fx-background-color: rgb(0,140,204);");
		});
		
		next.setOnMouseExited(e-> {
			next.setStyle("-fx-background-color: rgb(0,168,204);");
		});
		
		next.setOnAction(e-> {
			if(currentIndex+1 >= StockPortfolio.symbols.size()) {
				System.out.println();
			} else {
				quotas.getChildren().clear();
				quotasLabel = new Label("Predicted Prices");
				quotasLabel.setFont(Font.font("Georgia",FontWeight.BOLD,25));
				quotasLabel.setTextFill(TEXT_COLOR);
				quotas.getChildren().add(quotasLabel);
				currentIndex++;
				series1.getData().clear();
				sc.getData().clear();
				sc.setVisible(false);			
				if(StockPortfolio.symbols.size() > 0) {
					series1.getData().clear();
					String currentStock = StockPortfolio.symbols.get(currentIndex);
					try {
						chartTitle = currentStock + "(" + StockReader.getCompanyName(currentStock) + ")" + " Previous Prices";
						sc.setTitle(chartTitle);
					} catch (IOException e2) {
						e2.printStackTrace();
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					File prevFile = new File("bin/symbols/" + currentStock + "prev.txt");
					try {
						ArrayList<Double> prevPrices = new ArrayList<>();
						Scanner prevFileScanner = new Scanner(prevFile);
						while(prevFileScanner.hasNext()) {
							String currentLine = prevFileScanner.next();
							double currentPrice = Double.parseDouble(currentLine);
							prevPrices.add(currentPrice);
						}
						for(int i = 0;i<prevPrices.size();i++) {
							double day = i+1;
							double price = prevPrices.get(i);
							series1.getData().add(new XYChart.Data(day,price));
						}
						sc.getData().add(series1);
						sc.setVisible(true);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					ArrayList<Double> nextPrices = new ArrayList<>();
					File nextFile = new File("bin/symbols/" + currentStock +".txt");
					Scanner nextFileScanner = null;
					try {
						nextFileScanner = new Scanner(nextFile);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					while(nextFileScanner.hasNext()) {
						double price = Double.parseDouble(nextFileScanner.nextLine());
						price = Double.parseDouble(new DecimalFormat("##.##").format(price));
						nextPrices.add(price);
					}
						LocalDate today = LocalDate.now();
					for(int i = 0;i<nextPrices.size();i++) {
						String nextDate = today.plusDays(i+1).toString();
						String price = Double.toString(nextPrices.get(i));
						Label currentLabel = new Label(nextDate + " : " + price);
						currentLabel.setFont(Font.font("Georgia",20));
						currentLabel.setTextFill(TEXT_COLOR);
						quotas.getChildren().add(currentLabel);
					}
				}
			}
		});
			
		Button previous = new Button("Previous");
		previous.setLayoutX(W*0.275);
		previous.setLayoutY(H*0.05);
		previous.setPrefWidth(W*0.1);
		previous.setPrefHeight(H*0.05);
		previous.setFont(Font.font("Georgia",25));
		previous.setStyle("-fx-background-color: rgb(0,168,204);");
		previous.setTextFill(TEXT_COLOR);
		previous.setBorder(new Border(new BorderStroke(Color.BLACK, 
	            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		previous.setOnMouseEntered(e-> {
			previous.setStyle("-fx-background-color: rgb(0,140,204);");
		});
		
		previous.setOnMouseExited(e-> {
			previous.setStyle("-fx-background-color: rgb(0,168,204);");
		});
		
		previous.setOnAction(e-> {
			if(currentIndex-1 < 0) {
				System.out.println();
			} else {
				quotas.getChildren().clear();
				quotasLabel = new Label("Predicted Prices");
				quotasLabel.setFont(Font.font("Georgia",FontWeight.BOLD,25));
				quotasLabel.setTextFill(TEXT_COLOR);
				quotas.getChildren().add(quotasLabel);
				currentIndex--;
				series1.getData().clear();
				sc.getData().clear();
				sc.setVisible(false);			
				if(StockPortfolio.symbols.size() > 0) {
					series1.getData().clear();
					String currentStock = StockPortfolio.symbols.get(currentIndex);
					try {
						try {
							chartTitle = currentStock + "(" + StockReader.getCompanyName(currentStock) + ")";
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						sc.setTitle(chartTitle);
					} catch (IOException e2) {
						e2.printStackTrace();
					}
					
					File prevFile = new File("bin/symbols/" + currentStock + "prev.txt");
					try {
						ArrayList<Double> prevPrices = new ArrayList<>();
						Scanner prevFileScanner = new Scanner(prevFile);
						while(prevFileScanner.hasNext()) {
							String currentLine = prevFileScanner.next();
							double currentPrice = Double.parseDouble(currentLine);
							prevPrices.add(currentPrice);
						}
						for(int i = 0;i<prevPrices.size();i++) {
							double day = i+1;
							double price = prevPrices.get(i);
							series1.getData().add(new XYChart.Data(day,price));
						}
						sc.getData().add(series1);
						sc.setVisible(true);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					ArrayList<Double> nextPrices = new ArrayList<>();
					File nextFile = new File("bin/symbols/" + currentStock +".txt");
					Scanner nextFileScanner = null;
					try {
						nextFileScanner = new Scanner(nextFile);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					while(nextFileScanner.hasNext()) {
						double price = Double.parseDouble(nextFileScanner.nextLine());
						price = Double.parseDouble(new DecimalFormat("##.##").format(price));
						nextPrices.add(price);
					}
						LocalDate today = LocalDate.now();
					for(int i = 0;i<nextPrices.size();i++) {
						String nextDate = today.plusDays(i+1).toString();
						String price = Double.toString(nextPrices.get(i));
						Label currentLabel = new Label(nextDate + " : " + price);
						currentLabel.setFont(Font.font("Georgia",20));
						currentLabel.setTextFill(TEXT_COLOR);
						quotas.getChildren().add(currentLabel);
					}
				}
			}
		});
		
		if(StockPortfolio.symbols.size()>0) {
			
			ArrayList<String> symbolsClone = new ArrayList<>();
			for(int i = 0;i<StockPortfolio.symbols.size();i++) {
				String currentStock = StockPortfolio.symbols.get(i);
				symbolsClone.add(StockPortfolio.symbols.get(i));
				File currentPrevFile = new File("bin/symbols/" + currentStock + "prev.txt");
				File currentNextFile = new File("bin/symbols/" + currentStock + ".txt");
				currentPrevFile.delete();
				currentNextFile.delete();
			}
			StockPortfolio.symbols.clear();
			for(int i = 0;i<symbolsClone.size();i++) {
				Thread.sleep(100);
				StockPortfolio.addStock(symbolsClone.get(i));
			}
			
			emptyImageView.setVisible(false);
			sc.getData().clear();
			sc.setVisible(false);
			if(StockPortfolio.symbols.size() > 0) {
				series1.getData().clear();
				String currentStock = StockPortfolio.symbols.get(currentIndex);
				chartTitle = currentStock + "(" + StockReader.getCompanyName(currentStock) + ")" + " Previous Prices";
				sc.setTitle(chartTitle);
				File prevFile = new File("bin/symbols/" + currentStock + "prev.txt");
				try {
					ArrayList<Double> prevPrices = new ArrayList<>();
					Scanner prevFileScanner = new Scanner(prevFile);
					
					while(prevFileScanner.hasNext()) {
						String currentLine = prevFileScanner.next();
						double currentPrice = Double.parseDouble(currentLine);
						prevPrices.add(currentPrice);
					}
					for(int i = 0;i<prevPrices.size();i++) {
						double day = i+1;
						double price = prevPrices.get(i);
						series1.getData().add(new XYChart.Data(day,price));
					}
					sc.getData().add(series1);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				
				ArrayList<Double> nextPrices = new ArrayList<>();
				File nextFile = new File("bin/symbols/" + currentStock +".txt");
				Scanner nextFileScanner = new Scanner(nextFile);
				while(nextFileScanner.hasNext()) {
					double price = Double.parseDouble(nextFileScanner.nextLine());
					price = Double.parseDouble(new DecimalFormat("##.##").format(price));
					nextPrices.add(price);
				}
					LocalDate today = LocalDate.now();
				for(int i = 0;i<nextPrices.size();i++) {
					String nextDate = today.plusDays(i+1).toString();
					String price = Double.toString(nextPrices.get(i));
					Label currentLabel = new Label(nextDate + " : " + price);
					currentLabel.setFont(Font.font("Georgia",20));
					currentLabel.setTextFill(TEXT_COLOR);
					quotas.getChildren().add(currentLabel);
				}
				quotas.setVisible(true);
				sc.setVisible(true);
			}
		} else {
			next.setVisible(false);
			quotas.setVisible(false);
			previous.setVisible(false);
			sc.setVisible(false);
			emptyImageView.setVisible(true);
		}
		
		homeButton.setOnAction(e-> {
			sc.setVisible(false);
			if(StockPortfolio.symbols.size() > 0) {
				next.setVisible(true);
				previous.setVisible(true);
				emptyImageView.setVisible(false);
				quotas.getChildren().clear();
				quotasLabel = new Label("Predicted Prices");
				quotasLabel.setFont(Font.font("Georgia",FontWeight.BOLD,25));
				quotasLabel.setTextFill(TEXT_COLOR);
				quotas.getChildren().add(quotasLabel);
				series1.getData().clear();
				currentIndex = 0;
				String firstStock = StockPortfolio.symbols.get(0);
				try {
					chartTitle = firstStock + "(" + StockReader.getCompanyName(firstStock) + ")" + " Previous Prices";
				} catch (IOException | JSONException e2) {
					e2.printStackTrace();
				}
				File prevFile = new File("bin/symbols/" + firstStock + "prev.txt");
				try {
					ArrayList<Double> prevPrices = new ArrayList<>();
					Scanner prevFileScanner = new Scanner(prevFile);
					while(prevFileScanner.hasNext()) {
						String currentLine = prevFileScanner.next();
						double currentPrice = Double.parseDouble(currentLine);
						prevPrices.add(currentPrice);
					}
					for(int i = 0;i<prevPrices.size();i++) {
						double day = i+1;
						double price = prevPrices.get(i);
						series1.getData().add(new XYChart.Data(day,price));
					}
					sc.setTitle(chartTitle);
					sc.getData().add(series1);
					sc.setVisible(true);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				ArrayList<Double> nextPrices = new ArrayList<>();
				File nextFile = new File("bin/symbols/" + firstStock +".txt");
				Scanner nextFileScanner = null;
				try {
					nextFileScanner = new Scanner(nextFile);
				} catch (FileNotFoundException e1) {
				}
				while(nextFileScanner.hasNext()) {
					double price = Double.parseDouble(nextFileScanner.nextLine());
					price = Double.parseDouble(new DecimalFormat("##.##").format(price));
					nextPrices.add(price);
				}
				
					LocalDate today = LocalDate.now();
				for(int i = 0;i<nextPrices.size();i++) {
					String nextDate = today.plusDays(i+1).toString();
					String price = Double.toString(nextPrices.get(i));
					Label currentLabel = new Label(nextDate + " : " + price);
					currentLabel.setFont(Font.font("Georgia",20));
					currentLabel.setTextFill(TEXT_COLOR);
					quotas.getChildren().add(currentLabel);
				}
				quotas.setVisible(true);
			} else {
				emptyImageView.setVisible(true);
				next.setVisible(false);
				previous.setVisible(false);
				quotas.setVisible(false);
			}
			
			primaryStage.setScene(homeScene);
			primaryStage.setFullScreen(true);
		});
		
		Image exitIcon = new Image("exitIcon.png");
		ImageView exitIconView = new ImageView(exitIcon);
		exitIconView.setLayoutX(W*0.625);
		exitIconView.setLayoutY(H*0.01);
		Button exitButton = new Button("",exitIconView);
		exitButton.setPrefWidth(0);
		exitButton.setPrefHeight(0);
		exitButton.setLayoutX(W*0.625);
		exitButton.setLayoutY(H*0.01);
		exitButton.setOnAction(e-> {
			primaryStage.close();
		});
			
		homeGroup.getChildren().addAll(menuRectangle,epb,help,info,next,previous,logoImageView,emptyImageView,quotas,
				quickAnalyzeButton,exitButton);

		primaryStage.setScene(homeScene);
		primaryStage.setFullScreen(true);
		
		primaryStage.setOnCloseRequest(e-> {
			try {
				StockPortfolio.save();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		primaryStage.show();
	}
		 
	public static void main(String[] args) throws IOException {
		launch(args);
    	}
	}
