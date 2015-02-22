/*
* This file is part of SuDonkey, an open-source Sudoku puzzle game generator and solver.
* Copyright (C) 2015 Vedran Matic
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*
*/

package com.matic.sudoku.guifx.window;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.controlsfx.control.StatusBar;

import com.matic.sudoku.Resources;
import com.matic.sudoku.guifx.action.FileActionEventHandler;
import com.matic.sudoku.guifx.action.GeneratorActionEventHandler;
import com.matic.sudoku.guifx.action.KeyActionEventHandler;
import com.matic.sudoku.guifx.action.UndoableActionEventHandler;
import com.matic.sudoku.guifx.action.WindowActionEventHandler;
import com.matic.sudoku.guifx.action.undo.UndoableGameBoardAction;
import com.matic.sudoku.guifx.board.ClassicGameBoard;
import com.matic.sudoku.guifx.board.GameBoard;
import com.matic.sudoku.io.KeyInputManager.SymbolType;

/**
 * The main game window, containing all of the GUI components that the player can interact with.
 * 
 * @author vedran
 *
 */
public class GameWindowFX extends Application {
	
	private static final int RECENT_FILE_LIST_MAX_SIZE = 5;
	
	private static final String RECENT_FILE_KEY_VALUE = "recent_file_";
		
	private static final String OPEN_RECENT_COMMAND = "game.open_recent";
	private static final String CLEAR_RECENT_FILES_COMMAND = "game.clear_recent_files";
	private static final String SAVE_COMMAND = "game.save";
	private static final String SAVE_AS_COMMAND = "game.save_as";
	private static final String EXPORT_AS_IMAGE_COMMAND = "game.export_as_image";
	private static final String EXPORT_TO_PDF_COMMAND = "game.export_to_pdf";
	private static final String GENERATE_AND_EXPORT_COMMAND = "game.generate_and_export";
	private static final String QUIT_COMMAND = "game.quit";
	
	private static final String FOCUS_COMMAND = "focus.button";
	
	private final ClassicGameBoard gameBoard = new ClassicGameBoard(GameBoard.DIMENSION_9x9,
			SymbolType.DIGITS);
	
	private final FlowPane symbolButtonToolbar = new FlowPane();
	private final FlowPane colorButtonToolbar = new FlowPane();	
	private final StatusBar statusBar = new StatusBar();
	
	private final Menu openRecentMenu = new Menu(Resources.getTranslation(OPEN_RECENT_COMMAND));
	private final MenuItem saveMenuItem = new MenuItem(Resources.getTranslation(SAVE_COMMAND));
	
	private final GeneratorActionEventHandler generatorActionEventHandler = new GeneratorActionEventHandler();
	private final UndoableActionEventHandler undoableActionEventHandler = new UndoableActionEventHandler();
	private final WindowActionEventHandler windowActionEventHandler = new WindowActionEventHandler();
	private final FileActionEventHandler fileActionEventHandler = new FileActionEventHandler();	
	private final KeyActionEventHandler keyActionEventHandler = new KeyActionEventHandler();	
	
	private Stage stage;
	
	/**
     * Main application execution entry point. Used when the application packaging is performed
     * by other means than by JavaFX
     *
     * @param args Application parameters
     */
    public static void main(final String[] args) {       
        launch(args);
    }

	@Override
	public void start(final Stage stage) throws Exception {
		this.stage = stage;
		
		final VBox northPane = new VBox();
		northPane.getChildren().addAll(buildMenuBar(), setupColorButtonToolbar(colorButtonToolbar));
		
		final Pane gameBoardPane = new Pane();
		gameBoardPane.getChildren().add(gameBoard);
				
		statusBar.setPadding(new Insets(Resources.Gui.LAYOUT_PADDING,
				Resources.Gui.LAYOUT_PADDING, Resources.Gui.LAYOUT_PADDING, 
				Resources.Gui.LAYOUT_PADDING));
		statusBar.setStyle("-fx-base: #1F1F1F;");	
		statusBar.setText("Not classified");
		//statusBar.getRightItems().add(new Text(" 0:00:00"));
		//statusBar.setProgress(0.2);
		
		final VBox southPane = new VBox();
		southPane.getChildren().addAll(setupSymbolButtonToolbar(symbolButtonToolbar), statusBar);
		
		final BorderPane mainPane = new BorderPane();
		mainPane.setTop(northPane);
		mainPane.setCenter(gameBoardPane);
		mainPane.setBottom(southPane);
		
		gameBoard.widthProperty().bind(gameBoardPane.widthProperty());
        gameBoard.heightProperty().bind(gameBoardPane.heightProperty());
        gameBoard.setFocusTraversable(true);        
        gameBoard.setOnKeyPressed(event -> keyActionEventHandler.onKeyTyped(event, gameBoard));
        gameBoard.setOnMouseMoved(event -> gameBoard.onMouseMoved(event.getX(), event.getY()));
        gameBoard.setOnMouseClicked(event -> {        	
        	//TODO: Use actual parameter values
        	final UndoableGameBoardAction undoableAction = gameBoard.onMouseClicked(
        			event, true, false);
        	undoableActionEventHandler.handleUndoableAction(undoableAction);
        });        
        gameBoard.focusedProperty().addListener((observable, oldValue, newValue) -> {
			Platform.runLater(new Runnable(){
                public void run() {
                    gameBoard.requestFocus();
                }
            });			
        });
               
		final Scene scene = new Scene(mainPane, 800, 600);
		//TODO: Read application name from properties
		stage.setTitle("SuDonkeyFX");
        stage.setScene(scene);
        
        stage.setOnCloseRequest(event -> windowActionEventHandler.onWindowClose(event, stage));
        stage.centerOnScreen();
        stage.show();     
        
        gameBoard.draw(true, true);
	}
	
	public final void onUpdateRecentFileList(final String openedFilePath) {				
		final ObservableList<MenuItem> menuItems = openRecentMenu.getItems();		
		final List<MenuItem> existingFileItems = menuItems.stream().
				filter(menuItem -> {
					final String menuItemId = menuItem.getId();
					return menuItemId != null && menuItemId.equals(openedFilePath);
				}).collect(Collectors.toList());		
		
		if(existingFileItems.isEmpty()) {
			final int fileListSize = menuItems.size() - 2;
			if(fileListSize == RECENT_FILE_LIST_MAX_SIZE) {
				menuItems.remove(fileListSize - 3);
			}
			if(fileListSize == 0) {
				openRecentMenu.setDisable(false);
			}
			final String fileName = Paths.get(openedFilePath).getFileName().toString();
			final MenuItem pathMenuItem = new MenuItem(fileName + " [" + openedFilePath + "]");
			pathMenuItem.setOnAction(event -> onUpdateRecentFileList(openedFilePath));
			pathMenuItem.setId(openedFilePath);
			menuItems.add(0, pathMenuItem);			
		}
		else {
			final MenuItem pathMenuItem = existingFileItems.get(0);			
			final int existingIndex = menuItems.indexOf(pathMenuItem);
			if(existingIndex == 0) {
				return;
			}
			menuItems.remove(existingIndex);
			menuItems.add(0, pathMenuItem);
		}
		storeRecentFileList();
	}
	
	private Pane setupColorButtonToolbar(final FlowPane colorButtonToolbar) {		
		configureToolbarStyle(colorButtonToolbar);	
		final ToggleButton demoButton = new ToggleButton("Demo Color Button");
		demoButton.setFocusTraversable(false);
		colorButtonToolbar.getChildren().add(demoButton);
		
		return colorButtonToolbar;
	}
	
	private Pane setupSymbolButtonToolbar(final FlowPane symbolButtonToolbar) {
		configureToolbarStyle(symbolButtonToolbar);
		final ToggleButton demoButton = new ToggleButton("Demo Symbol Input Button");
		demoButton.setFocusTraversable(false);
		symbolButtonToolbar.getChildren().add(demoButton);
		
		return symbolButtonToolbar;
	}
	
	private void configureToolbarStyle(final FlowPane toolbar) {
		toolbar.setAlignment(Pos.CENTER);		
		toolbar.setStyle("-fx-background-color: #5F5F5F;");
		toolbar.setPadding(new Insets(Resources.Gui.LAYOUT_PADDING, 
				Resources.Gui.LAYOUT_PADDING, Resources.Gui.LAYOUT_PADDING, 
				Resources.Gui.LAYOUT_PADDING));
		toolbar.setHgap(Resources.Gui.LAYOUT_PADDING);
		toolbar.setVgap(Resources.Gui.LAYOUT_PADDING);
	}
	
	private MenuBar buildMenuBar() {
        final MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-base: #1F1F1F;");
        menuBar.getMenus().addAll(buildGameMenu(), buildEditMenu(), buildViewMenu());
      
        return menuBar;
    }
	
	private Menu buildGameMenu() {
		final Menu gameMenu = new Menu(Resources.getTranslation("menubar.game"));       
        gameMenu.setMnemonicParsing(true);    
        
        final String newPuzzleCommand = Resources.getTranslation("game.new");
        final MenuItem newMenuItem = new MenuItem(newPuzzleCommand);
        newMenuItem.setOnAction(event -> generatorActionEventHandler
        		.onGenerateNewPuzzle(stage, gameBoard));
        newMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        newMenuItem.setId(newPuzzleCommand);        
        
        final String openPuzzleCommand = Resources.getTranslation("game.open");
        final MenuItem openMenuItem = new MenuItem(openPuzzleCommand);
        openMenuItem.setOnAction(event -> fileActionEventHandler.onOpenFileAction(stage, this));
        openMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        openMenuItem.setId(openPuzzleCommand); 
        
        openRecentMenu.setId(OPEN_RECENT_COMMAND); 
        buildRecentMenuItems(openRecentMenu, loadRecentFileList());
        
        final MenuItem clearRecentFilesMenuItem = new MenuItem(Resources.getTranslation(CLEAR_RECENT_FILES_COMMAND));
        clearRecentFilesMenuItem.setOnAction(event -> onClearRecentFiles());
        clearRecentFilesMenuItem.setId(CLEAR_RECENT_FILES_COMMAND);
        openRecentMenu.getItems().addAll(new SeparatorMenuItem(), clearRecentFilesMenuItem);
        
        saveMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        saveMenuItem.setId(SAVE_COMMAND);
        saveMenuItem.setDisable(true);        
        
        final MenuItem saveAsMenuItem = new MenuItem(Resources.getTranslation(SAVE_AS_COMMAND));
        saveAsMenuItem.setId(SAVE_AS_COMMAND);
        
        final MenuItem exportAsImageMenuItem = new MenuItem(Resources.getTranslation(EXPORT_AS_IMAGE_COMMAND));
        exportAsImageMenuItem.setOnAction(event -> fileActionEventHandler.onExportToImageAction(stage));
        exportAsImageMenuItem.setId(EXPORT_AS_IMAGE_COMMAND);
        
        final MenuItem exportToPdfMenuItem = new MenuItem(Resources.getTranslation(EXPORT_TO_PDF_COMMAND));
        exportToPdfMenuItem.setOnAction(event -> fileActionEventHandler.onExportToPdfAction(stage));
        exportToPdfMenuItem.setId(EXPORT_TO_PDF_COMMAND);
        
        final MenuItem generateAndExportMenuItem = new MenuItem(
        		Resources.getTranslation(GENERATE_AND_EXPORT_COMMAND));
        generateAndExportMenuItem.setOnAction(event -> 
        	generatorActionEventHandler.onExportPuzzles(stage));        
        generateAndExportMenuItem.setId(GENERATE_AND_EXPORT_COMMAND);
        
        final MenuItem quitMenuItem = new MenuItem(Resources.getTranslation(QUIT_COMMAND));
        quitMenuItem.setOnAction(event -> windowActionEventHandler.onWindowClose(event, stage));
        quitMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
        quitMenuItem.setId(QUIT_COMMAND);                
        
        gameMenu.getItems().addAll(newMenuItem, new SeparatorMenuItem(), openMenuItem, openRecentMenu, new SeparatorMenuItem(), 
        		saveMenuItem, saveAsMenuItem, new SeparatorMenuItem(), exportAsImageMenuItem, exportToPdfMenuItem, 
        		new SeparatorMenuItem(), generateAndExportMenuItem, new SeparatorMenuItem(), quitMenuItem);
        
        return gameMenu;
	}
	
	private void buildRecentMenuItems(final Menu recentFileMenu, final List<String> filePaths) {
		filePaths.stream().map(path -> {
			final String fileName = Paths.get(path).getFileName().toString();
			final MenuItem pathMenuItem = new MenuItem(fileName + " [" + path + "]");
			pathMenuItem.setOnAction(event -> onUpdateRecentFileList(path));
			pathMenuItem.setId(path);			
			
			return pathMenuItem;
		}).forEach(menuItem -> recentFileMenu.getItems().add(menuItem));		
		recentFileMenu.setDisable(recentFileMenu.getItems().isEmpty());
	}
	
	private Menu buildEditMenu() {
		final Menu editMenu = new Menu(Resources.getTranslation("menubar.edit"));       
        editMenu.setMnemonicParsing(true);
        
        return editMenu;
	}
	
	private Menu buildViewMenu() {
		final Menu viewMenu = new Menu(Resources.getTranslation("menubar.view"));
		viewMenu.setMnemonicParsing(true);
		
		final CheckMenuItem colorToolbarMenuItem = new CheckMenuItem(
				Resources.getTranslation("view.cell_colors"));
		colorToolbarMenuItem.setSelected(true);
		colorToolbarMenuItem.setOnAction(event -> {
			final BorderPane root = (BorderPane)stage.getScene().getRoot();
			final ObservableList<Node> topPaneContent = ((VBox)root.getTop()).getChildren();
			if(colorToolbarMenuItem.isSelected()) {
				topPaneContent.add(colorButtonToolbar);				
			}
			else {
				topPaneContent.remove(colorButtonToolbar);				
			}			
		});
		
		final CheckMenuItem symbolToolbarMenuItem = new CheckMenuItem(
				Resources.getTranslation("view.symbol_entry"));
		symbolToolbarMenuItem.setSelected(true);
		symbolToolbarMenuItem.setOnAction(event -> {
			final BorderPane root = (BorderPane)stage.getScene().getRoot();
			final ObservableList<Node> bottomPaneContent = ((VBox)root.getBottom()).getChildren();
			if(symbolToolbarMenuItem.isSelected()) {
				bottomPaneContent.add(0, symbolButtonToolbar);				
			}
			else {
				bottomPaneContent.remove(symbolButtonToolbar);				
			}			
		});
		
		final CheckMenuItem statusToolbarMenuItem = new CheckMenuItem(
				Resources.getTranslation("view.status_toolbar"));
		statusToolbarMenuItem.setSelected(true);
		statusToolbarMenuItem.setOnAction(event -> {
			final BorderPane root = (BorderPane)stage.getScene().getRoot();
			final ObservableList<Node> bottomPaneContent = ((VBox)root.getBottom()).getChildren();
			if(statusToolbarMenuItem.isSelected()) {
				bottomPaneContent.add(statusBar);				
			}
			else {
				bottomPaneContent.remove(statusBar);				
			}			
		});
		
		final CheckMenuItem focusMenuItem = new CheckMenuItem(
				Resources.getTranslation(FOCUS_COMMAND));
		focusMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+F"));
		focusMenuItem.setOnAction(event -> System.out.println("Focus ON? " +
				focusMenuItem.isSelected()));
		
		viewMenu.getItems().addAll(colorToolbarMenuItem, symbolToolbarMenuItem,
				statusToolbarMenuItem, new SeparatorMenuItem(), focusMenuItem);
		
		return viewMenu;
	}
	
	private void onClearRecentFiles() {
		final ObservableList<MenuItem> menuItems = openRecentMenu.getItems(); 
		menuItems.remove(0, menuItems.size() - 2);
		openRecentMenu.setDisable(true);
		storeRecentFileList();
	}
	
	private void storeRecentFileList() {
		final ObservableList<MenuItem> recentFileMenuItems = openRecentMenu.getItems();
		
		for(int i = 0; i < RECENT_FILE_LIST_MAX_SIZE; ++i) {
			if(i < recentFileMenuItems.size() - 2) {
				final MenuItem menuItem = recentFileMenuItems.get(i);
				Resources.setProperty(RECENT_FILE_KEY_VALUE + i, menuItem.getId());
			}
			else {
				Resources.setProperty(RECENT_FILE_KEY_VALUE + i, null);
			}
		}
	}
	
	private List<String> loadRecentFileList() {
		final List<String> recentFileList = new ArrayList<>();
		
		int index = 0;
		String filePath = Resources.getProperty(RECENT_FILE_KEY_VALUE + index, null);
		
		while(filePath != null) {
			recentFileList.add(filePath);
			filePath = Resources.getProperty(RECENT_FILE_KEY_VALUE + (++index), null);
		}
		
		return recentFileList;
	}
}
