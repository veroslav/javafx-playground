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

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
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

/**
 * The main game window, containing all of the GUI components that the player can interact with.
 * 
 * @author vedran
 *
 */
public class GameWindow extends Application {
	
	private static final String GAME_MENU_COMMAND = Resources.getTranslation("menubar.game");	
	private static final String NEW_PUZZLE_COMMAND = Resources.getTranslation("game.new");
	private static final String OPEN_PUZZLE_COMMAND = Resources.getTranslation("game.open");
	private static final String GENERATE_AND_EXPORT_COMMAND = "game.generate_and_export";
	private static final String QUIT_COMMAND = "game.quit";
	
	private static final String EDIT_MENU_COMMAND = Resources.getTranslation("menubar.edit");
	
	private final ClassicGameBoard gameBoard = new ClassicGameBoard(GameBoard.DIMENSION_9x9);
	
	private final GeneratorActionEventHandler generatorActionEventHandler = new GeneratorActionEventHandler();
	private final UndoableActionEventHandler undoableActionEventHandler = new UndoableActionEventHandler();
	private final WindowActionEventHandler windowActionEventHandler = new WindowActionEventHandler();
	private final FileActionEventHandler fileActionEventHandler = new FileActionEventHandler();
	
	private KeyActionEventHandler keyActionEventHandler;	
	
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
		
		//System.setProperty("prism.lcdtext", "false");
		
		final BorderPane northPane = new BorderPane();
		northPane.setTop(buildMenuBar());
		
		final Pane gameBoardPane = new Pane();
		gameBoardPane.getChildren().add(gameBoard);
		
		final StatusBar statusBar = new StatusBar();
		statusBar.setStyle("-fx-base: #1F1F1F;");	
		statusBar.setText("Not verified");
		//statusBar.getRightItems().add(new Text(" 0:00:00"));
		//statusBar.setProgress(0.2);
		
		final BorderPane mainPane = new BorderPane();
		mainPane.setTop(northPane);
		mainPane.setCenter(gameBoardPane);
		mainPane.setBottom(statusBar);
		
		gameBoard.widthProperty().bind(gameBoardPane.widthProperty());
        gameBoard.heightProperty().bind(gameBoardPane.heightProperty());
        gameBoard.setFocusTraversable(true);
        
        keyActionEventHandler = new KeyActionEventHandler(gameBoard);
        gameBoard.setOnKeyPressed(event -> onUndoableAction(
        		keyActionEventHandler.onKeyTyped(event)));
        gameBoard.setOnMouseMoved(event -> gameBoard.onMouseMoved(event.getX(), event.getY()));
        gameBoard.setOnMouseClicked(event -> {        	
        	/*final UndoableGameBoardAction undoableAction = gameBoard.onMouseClicked(
        			event, !puzzle.isSolved(), focusButton.isSelected());*/
        	//TODO: Use actual parameter values
        	final UndoableGameBoardAction undoableAction = gameBoard.onMouseClicked(
        			event, true, false);
        	onUndoableAction(undoableAction);
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
		stage.setTitle("SuDonkey");
        stage.setScene(scene);
        
        stage.setOnCloseRequest(event -> windowActionEventHandler.onWindowClose(event));
        stage.centerOnScreen();
        stage.show();     
        
        gameBoard.draw(true, true);
	}
	
	private MenuBar buildMenuBar() {
        final MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-base: #1F1F1F;");
        menuBar.getMenus().addAll(buildGameMenu());
        menuBar.getMenus().addAll(buildEditMenu());
      
        return menuBar;
    }
	
	private Menu buildGameMenu() {
		final Menu gameMenu = new Menu(GAME_MENU_COMMAND);       
        gameMenu.setMnemonicParsing(true);    
        
        final MenuItem newMenuItem = new MenuItem(NEW_PUZZLE_COMMAND);
        newMenuItem.setOnAction(event -> generatorActionEventHandler
        		.onGenerateNewPuzzle(event, stage, gameBoard));
        newMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        newMenuItem.setId(NEW_PUZZLE_COMMAND);        
        
        final MenuItem openMenuItem = new MenuItem(OPEN_PUZZLE_COMMAND);
        openMenuItem.setOnAction(event -> fileActionEventHandler.onOpenMenuItemSelected(event, stage));
        openMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        openMenuItem.setId(OPEN_PUZZLE_COMMAND); 
        
        final MenuItem generateAndExportMenuItem = new MenuItem(
        		Resources.getTranslation(GENERATE_AND_EXPORT_COMMAND));
        generateAndExportMenuItem.setOnAction(event -> 
        	generatorActionEventHandler.onExportPuzzles(stage));        
        generateAndExportMenuItem.setId(GENERATE_AND_EXPORT_COMMAND);
        
        final MenuItem quitMenuItem = new MenuItem(Resources.getTranslation(QUIT_COMMAND));
        quitMenuItem.setOnAction(event -> windowActionEventHandler.onWindowClose(event));
        quitMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
        quitMenuItem.setId(QUIT_COMMAND);                
        
        gameMenu.getItems().addAll(newMenuItem, openMenuItem, new SeparatorMenuItem(),
        		generateAndExportMenuItem, new SeparatorMenuItem(), quitMenuItem);
        
        return gameMenu;
	}
	
	private Menu buildEditMenu() {
		final Menu editMenu = new Menu(EDIT_MENU_COMMAND);       
        editMenu.setMnemonicParsing(true);
        
        return editMenu;
	}
	
	private void onUndoableAction(final UndoableGameBoardAction action) {
		undoableActionEventHandler.handleUndoableAction(action);
	}
}
