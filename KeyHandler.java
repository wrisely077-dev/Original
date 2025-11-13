package trial2dgame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;

public class KeyHandler implements KeyListener{

	GamePanel gp;
	public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed;

	//DEBUG
	boolean showDebugText = false;
	
	public KeyHandler(GamePanel gp) {
		this.gp = gp;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		int code = e.getKeyCode();
		
		// TITLE STATE
		if(gp.gameState == gp.titleState) {titleState(code);}
		
		// PLAY STATE				
		else if(gp.gameState == gp.playState) {playState(code);
		
//		Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
//		System.out.println("=== Active Threads (" + threads.size() + ") ===");
//		for (Thread t : threads.keySet()) {
//		    System.out.println("Thread name: " + t.getName() + " | State: " + t.getState());
//		}
		
		}
		
		// PAUSE STATE
		else if(gp.gameState == gp.pauseState) {pauseState(code);}	
		
		// DIALOGUE STATE	
		else if(gp.gameState == gp.dialogueState) {dialogueState(code);}
		
		// CHARACTER STATE
		else if (gp.gameState == gp.characterState) {characterState(code);}
		
		// OPTIONS STATE
		else if (gp.gameState == gp.optionsState) {optionsState(code);}
		
		// GAME OVER STATE
		else if (gp.gameState == gp.gameOverState) {gameOverState(code);}
		
		// TRADE STATE
		else if (gp.gameState == gp.tradeState) {tradeState(code);}
		
		// MAP STATE
		else if (gp.gameState == gp.mapState) {mapState(code);}
		
		// BATTLE STATE
		else if (gp.gameState == gp.battleState) {battleState(code);}
							
	}
	
	public void titleState(int code) {
		if(code == KeyEvent.VK_W) {
			gp.ui.commandNum--;
			if(gp.ui.commandNum < 0) {
				gp.ui.commandNum = 2;
			}
		}
		if(code == KeyEvent.VK_S) {
			gp.ui.commandNum++;
			if(gp.ui.commandNum > 2) {
				gp.ui.commandNum = 0;
			}
		}
		if(code == KeyEvent.VK_ENTER) {
			if(gp.ui.commandNum == 0) { // NEW GAME
				gp.resetGame(true);
				gp.gameState = gp.playState;
				gp.playMusic(0);
			}
			if(gp.ui.commandNum == 1) { // LOAD GAME
				gp.saveLoad.load();
				gp.aSetter.setMonster();
				gp.gameState = gp.playState;
				gp.playMusic(0);
			}
			if(gp.ui.commandNum == 2) { // EXIT GAME
				System.exit(0);
			}
		}			
	}
	
	public void playState(int code) {
		if(code == KeyEvent.VK_W) {upPressed = true;}
		if(code == KeyEvent.VK_S) {downPressed = true;}
		if(code == KeyEvent.VK_A) {leftPressed = true;}
		if(code == KeyEvent.VK_D) {rightPressed = true;}
		
		if(code == KeyEvent.VK_P) {gp.gameState = gp.pauseState;}
		if(code == KeyEvent.VK_C) {gp.gameState = gp.characterState;}
		if(code == KeyEvent.VK_ENTER) {enterPressed = true;}
		if(code == KeyEvent.VK_ESCAPE) {gp.gameState = gp.optionsState;}
		if(code == KeyEvent.VK_M) {gp.gameState = gp.mapState;}
		if(code == KeyEvent.VK_X) {
			if(gp.map.miniMapOn == false) {
				gp.map.miniMapOn = true;
			}
			else {
				gp.map.miniMapOn = false;
			}
		}

		
//DEBUG
		if(code == KeyEvent.VK_T) {
			if(showDebugText == false) {
				showDebugText = true;
			}
			else if(showDebugText == true) {
				showDebugText = false;
			} 
		}
		
		if(code == KeyEvent.VK_R) {
			switch(gp.currentMap) {
			case 0: gp.tileM.loadMap("/maps/Beach (2).txt",0); break;
			case 1: gp.tileM.loadMap("/maps/House.txt",1); break;
			case 2: gp.tileM.loadMap("/maps/Library.txt",1); break;
			case 3: gp.tileM.loadMap("/maps/trialRoom.txt",1); break;
			}
		}
	}
	
	public void pauseState(int code) {
		if(code == KeyEvent.VK_P) {
			gp.gameState = gp.playState;
		}
	}
	
	public void dialogueState(int code) {
		if (code == KeyEvent.VK_ENTER) {
            enterPressed = true; // Set enterPressed to true when Enter is pressed
        }
	}
	
	public void characterState(int code) { // status and inventory
		if(code == KeyEvent.VK_C) {
			gp.gameState = gp.playState;
		}
		if(code == KeyEvent.VK_ENTER) {
			handleCharacter();
		}
		playerInventory(code);
	}
	
	public void optionsState(int code) {
		
		if(code == KeyEvent.VK_ESCAPE) {
			gp.gameState = gp.playState; // go back to game
		}
		if(code == KeyEvent.VK_ENTER) {
			handleEnter();
		}
		
		int maxCommandNum = 0;
		
		switch(gp.ui.subState) {
		case 0: maxCommandNum = 6; break;
		case 2: maxCommandNum = 1; break; // YES/NO EXIT CONFIRMATION
		}

		
		if(code == KeyEvent.VK_W) {
			gp.ui.commandNum--;
			gp.playSE(7); //change this sound
			if(gp.ui.commandNum < 0) {
				gp.ui.commandNum = maxCommandNum;
			}
		}
		if(code == KeyEvent.VK_S) {
			gp.ui.commandNum++;
			gp.playSE(7); // change this sound
			if(gp.ui.commandNum > maxCommandNum) {
				gp.ui.commandNum = 0;
			}
		}
		
		if(code == KeyEvent.VK_A) {
			if(gp.ui.subState == 0) {
				if(gp.ui.commandNum == 2 && gp.music.volumeScale > 0) {
					gp.music.volumeScale--;
					gp.music.checkVolume();
					if(gp.music.volumeScale == 0) {
		                gp.music.stop();
		                gp.musicPlaying = false;
		            }
		            gp.playSE(7);
				}
	            
				if(gp.ui.commandNum == 3 && gp.se.volumeScale > 0) {
					gp.se.volumeScale--;
					gp.se.checkVolume();
		            if(gp.se.volumeScale == 0) {
		                gp.se.stop(); // You need to have a stop method for SE too
		            }	
					gp.playSE(7);	
				}
			}
		}
		if(code == KeyEvent.VK_D) {
			if(gp.ui.subState == 0) {
				if(gp.ui.commandNum == 2 && gp.music.volumeScale < 5) {
					gp.music.volumeScale++;
					gp.music.checkVolume();
					if(gp.music.volumeScale > 0 && !gp.musicPlaying) {
						gp.playMusic(gp.currentMusicIndex);
						gp.musicPlaying = true;  // Set to true when music starts
			        }
					gp.playSE(7);	
				}
				if(gp.ui.commandNum == 3 && gp.se.volumeScale < 5) {
					gp.se.volumeScale++;
					gp.se.checkVolume();
					gp.playSE(7);	
				}
			}
		}
		
		System.out.println("commandNum = " + gp.ui.commandNum);
		System.out.println("maxCommandNum = " + maxCommandNum);
		System.out.println("enterPressed = " + enterPressed);
		System.out.println("subState = " + gp.ui.subState + "\n");

	}
	

	public void gameOverState(int code){
		
		if(code == KeyEvent.VK_W) {
			gp.ui.commandNum--;
			if(gp.ui.commandNum < 0) {
				gp.ui.commandNum = 1;
			}
			gp.playSE(10); //CHANGE LATER
		}
		if(code == KeyEvent.VK_S) {
			gp.ui.commandNum++;
			if(gp.ui.commandNum > 1) {
				gp.ui.commandNum = 0;
			}
			gp.playSE(10); //CHANGE LATER
		}
		if(code == KeyEvent.VK_ENTER) {
			handleGameOver();
		}
		
	}
	
	public void tradeState(int code) {
		
		if(gp.ui.subState == 0) {	
			if(code == KeyEvent.VK_ENTER) {
				handleTrade();
			}
			if(code == KeyEvent.VK_W) {
				gp.ui.commandNum--;
				if(gp.ui.commandNum < 0) {
					gp.ui.commandNum = 2;
				}
				gp.playSE(7); //cursor
			}
			if(code == KeyEvent.VK_S) {
				gp.ui.commandNum++;
				if(gp.ui.commandNum > 2) {
					gp.ui.commandNum = 0;
				}
				gp.playSE(7); //cursor
			}
		}
		
		else if(gp.ui.subState == 1) {
			npcInventory(code);
			playerInventory(code);
			if(code == KeyEvent.VK_ESCAPE) {
				gp.ui.subState = 0;
			}
			if(code == KeyEvent.VK_ENTER) {
				handleTrade();
			}
		}
		
		else if(gp.ui.subState == 2) {
			playerInventory(code);
			if(code == KeyEvent.VK_ESCAPE) {
				gp.ui.subState = 0;
			}
			if(code == KeyEvent.VK_ENTER) {
				handleTrade();
			}
		}
		
	}
	
	public void mapState(int code){	
		if(code == KeyEvent.VK_M) {
			gp.gameState = gp.playState;
		}
	}
	
	public void battleState(int code){
	    int choiceCount = gp.ui.currentQuiz.get(gp.ui.quizIndex).getChoices().size();
		
		if(code == KeyEvent.VK_A) {
			gp.ui.commandNum--;
			gp.playSE(7); //change this sound
			if(gp.ui.commandNum < 0) {
				gp.ui.commandNum = choiceCount--;
			}
		}
		if(code == KeyEvent.VK_D) {
			gp.ui.commandNum++;
			gp.playSE(7); // change this sound
			if(gp.ui.commandNum >= choiceCount) {
				gp.ui.commandNum = 0;
			}
		}
		
		if (code == KeyEvent.VK_ENTER) {
			enterPressed = true; // Set enterPressed to true when Enter is pressed
		}
	}
	
	public void playerInventory(int code){
		if(code == KeyEvent.VK_W) {
			if(gp.ui.playerSlotRow != 0) {
				gp.ui.playerSlotRow--;
				gp.playSE(7);
			}	
		}
		if(code == KeyEvent.VK_A) {
			if(gp.ui.playerSlotCol != 0) {
				gp.ui.playerSlotCol--;
				gp.playSE(7);
			}
		}
		if(code == KeyEvent.VK_S) {
			if(gp.ui.playerSlotRow != 3) {
				gp.ui.playerSlotRow++;
				gp.playSE(7);	
			}	
		}
		if(code == KeyEvent.VK_D) {
			if(gp.ui.playerSlotCol != 4) {
				gp.ui.playerSlotCol++;
				gp.playSE(7);	
			}
		}
	}

	public void npcInventory(int code){
		if(code == KeyEvent.VK_W) {
			if(gp.ui.npcSlotRow != 0) {
				gp.ui.npcSlotRow--;
				gp.playSE(7);
			}	
		}
		if(code == KeyEvent.VK_A) {
			if(gp.ui.npcSlotCol != 0) {
				gp.ui.npcSlotCol--;
				gp.playSE(7);
			}
		}
		if(code == KeyEvent.VK_S) {
			if(gp.ui.npcSlotRow != 3) {
				gp.ui.npcSlotRow++;
				gp.playSE(7);	
			}	
		}
		if(code == KeyEvent.VK_D) {
			if(gp.ui.npcSlotCol != 4) {
				gp.ui.npcSlotCol++;
				gp.playSE(7);	
			}
		}
	}
	
	private void handleEnter() {
	    if (gp.gameState == gp.dialogueState) {handleDialogue();}
	    else if (gp.gameState == gp.characterState) {handleCharacter();}
	    else if (gp.gameState == gp.optionsState) {handleOptions();}
	    else if (gp.gameState == gp.gameOverState) {handleGameOver();}
	    else if (gp.gameState == gp.tradeState) {handleTrade();}
	}

	private void handleDialogue() {}
	
	private void handleCharacter() {
		gp.player.selectItem();
	}
	
	private void handleOptions() {	    
		if(gp.ui.subState == 0) {
	        switch(gp.ui.commandNum) {
	            case 0: 
	            	gp.saveLoad.save(); // Save Game
	            	gp.aSetter.setMonster();
	                gp.ui.triggerSavingMessage();
	                break;
	            case 1: 
	            	gp.saveLoad.load(); // Load Game
	            	gp.aSetter.setMonster();
	                gp.ui.triggerLoadingMessage();
	                break;
	            case 4:
	                gp.ui.subState = 1; // Go to controls screen
	                gp.ui.commandNum = 0;
	                break;
	            case 5:
	                gp.ui.subState = 2; // Go to exit confirmation
	                gp.ui.commandNum = 0;
	                break;
	            case 6:
	                gp.gameState = gp.playState; // Back to playing
	                break;
	        }
	    } 
	    else if(gp.ui.subState == 1) { 	// In controls screen
	        if(gp.ui.commandNum == 0) {
	            gp.ui.subState = 0; 	// Back to main options
	            gp.ui.commandNum = 4;
	        }
	    }
	    else if(gp.ui.subState == 2) { 	// In exit confirmation
	        if(gp.ui.commandNum == 0) {
	            gp.stopMusic();
	            gp.gameState = gp.titleState;
	            gp.ui.subState = 0;
	        }
	        if(gp.ui.commandNum == 1) {
	            gp.ui.subState = 0;
	            gp.ui.commandNum = 5;
	        }
	    }
	}	

		
	private void handleGameOver() {
		if(gp.ui.commandNum == 0) {
			gp.gameState = gp.playState;
			gp.resetGame(false); //should instead go back to load
			gp.playMusic(0);
		}
		else if(gp.ui.commandNum == 1) {
			gp.gameState = gp.titleState;
			gp.resetGame(true);
			
		}
	}
	
	private void handleTrade() {
		switch (gp.ui.subState) {
        case 0: // trade_select
            switch (gp.ui.commandNum) {
                case 0: // Buy
                    gp.ui.subState = 1;
                    gp.ui.commandNum = 0;
                    break;
                case 1: // Sell
                    gp.ui.subState = 2;
                    gp.ui.commandNum = 0;
                    break;
                case 2: // Leave
                    gp.ui.commandNum = 0;
                    gp.ui.npc.startDialogue(gp.ui.npc, 1);
                    break;
            }
            break;

        case 1: // trade_buy
        	
        	int itemIndex = gp.ui.getItemIndexOnSlot(gp.ui.npcSlotCol, gp.ui.npcSlotRow);
        	
        	if(gp.ui.npc.inventory.get(itemIndex).price > gp.player.coin) {
        		gp.ui.subState = 0; // back to trade_select [buy, sell, leave]
        		gp.ui.commandNum = 0;
        		gp.ui.npc.startDialogue(gp.ui.npc, 2);
           	}
        	else {
        		if(gp.player.canObtainItem(gp.ui.npc.inventory.get(itemIndex)) == true) {
        			gp.player.coin -= gp.ui.npc.inventory.get(itemIndex).price;
        		}
        		else {
        			gp.ui.subState = 0; // back to trade_select [buy, sell, leave]
        			gp.ui.npc.startDialogue(gp.ui.npc, 3);
        			gp.ui.commandNum = 0;
        		}
        	}

            break;

        case 2: // Sell Screen
        	
        	itemIndex = gp.ui.getItemIndexOnSlot(gp.ui.playerSlotCol, gp.ui.playerSlotRow);
        	
        	if(gp.player.inventory.get(itemIndex) == gp.player.currentWeapon ||
        	   gp.player.inventory.get(itemIndex) == gp.player.currentShield ||
        	   gp.player.inventory.get(itemIndex) == gp.player.currentLight) {
        		gp.ui.commandNum = 0;
                gp.ui.subState = 0; // Return to trade_select
        		gp.ui.npc.startDialogue(gp.ui.npc, 4);
        	}
        	else {
        		if(gp.player.inventory.get(itemIndex).amount > 1) {
        			gp.player.inventory.get(itemIndex).amount--;
        		}
        		else {
      				gp.player.inventory.remove(itemIndex);
        		}
        		gp.player.coin += gp.player.inventory.get(itemIndex).price;
        	}        
        	break;

		}
	    enterPressed = false;
	}
	
	
	
	@Override
	public void keyReleased(KeyEvent e) {
		
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_ENTER) {
	        enterPressed = false; // Reset enterPressed when Enter is released
	    }
		if(code == KeyEvent.VK_W) {upPressed = false;}
		if(code == KeyEvent.VK_S) {downPressed = false;}
		if(code == KeyEvent.VK_A) {leftPressed = false;}
		if(code == KeyEvent.VK_D) {rightPressed = false;}

	}
	
}
