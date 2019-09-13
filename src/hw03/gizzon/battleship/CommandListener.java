package hw03.gizzon.battleship;

import javax.swing.JOptionPane;

import hw03.gizzon.battleship.Battleship.mode;

public class CommandListener implements Runnable{

	@Override
	public void run() {
		while(true)
		{
			try
			{
				Thread.sleep(10);
			}
			catch(Exception e)
			{
				
			}
			String localCommand = "";
			String localResponse = "";
			String remoteResponse = "";
			
			//server
			if (Battleship.protocol.getState() == BattleshipStates.PLAYER1)
			{			    
			    if(Battleship.mode1 == mode.SERVER)
			    {   
			        if(Battleship.clicked)
			        {
			            if (Battleship.timerEmpty) {
			                localCommand = "win PLAYER2";
			                remoteResponse = Battleship.sendRecv(Battleship.peer, Battleship.protocol, localCommand);
			                JOptionPane.showMessageDialog(null,Battleship.protocol.getGame().getPlayer2() + " won!");
	                        Battleship.clicked = false;
	                        return;
			            } else {
			                localCommand = "shot " + Battleship.clickedRow + " " + Battleship.clickedCol;
	                        do {
	                            remoteResponse = Battleship.sendRecv(Battleship.peer, Battleship.protocol, localCommand);
	                            
	                        }while(!remoteResponse.equals("shot ok") && !remoteResponse.equals("win PLAYER1") && !remoteResponse.equals("win PLAYER2"));
			            }
			            
			            
			            Battleship.clicked = false;
			        }
			        
			        if(remoteResponse.equals("win PLAYER1"))
                    {
                        JOptionPane.showMessageDialog(null,Battleship.protocol.getGame().getPlayer1() + " won!");
                        Battleship.clicked = false;
                        return;
                    }
			    }
			    else if(Battleship.mode1 == mode.CLIENT)
			    {
			        do
			        {
			            localResponse = Battleship.recvSend(Battleship.peer, Battleship.protocol);
			        }while(!localResponse.equals("shot ok") && !localResponse.equals("win PLAYER1") && !localResponse.equals("win PLAYER2"));
			        
			        if (localResponse.equals("win PLAYER2")) 
			        {
			            JOptionPane.showMessageDialog(null,Battleship.protocol.getGame().getPlayer2() + " won!");
			            return;
			        }
			        else if(localResponse.equals("win PLAYER1"))
                    {
                        JOptionPane.showMessageDialog(null,Battleship.protocol.getGame().getPlayer1() + " won!");
                        return;
                    }
			    }
			}
			//client
			else
			{
			    if(Battleship.mode1 == mode.CLIENT)
                {
                    if(Battleship.clicked)
                    {
                        if (Battleship.timerEmpty) {
                            localCommand = "win PLAYER1";
                            remoteResponse = Battleship.sendRecv(Battleship.peer, Battleship.protocol, localCommand);
                            JOptionPane.showMessageDialog(null,Battleship.protocol.getGame().getPlayer1() + " won!");
                            Battleship.clicked = false;
                            return;
                        } else {
                            localCommand = "shot " + Battleship.clickedRow + " " + Battleship.clickedCol;
                            do {
                                remoteResponse = Battleship.sendRecv(Battleship.peer, Battleship.protocol, localCommand);
                                
                            }while(!remoteResponse.equals("shot ok") && !remoteResponse.equals("win PLAYER2") && !remoteResponse.equals("win PLAYER1"));
                        }
                        
                        Battleship.clicked = false;
                    } 
                    if(remoteResponse.equals("win PLAYER2"))
                    {
                        JOptionPane.showMessageDialog(null,Battleship.protocol.getGame().getPlayer2() + " won!");
                        Battleship.clicked = false;
                        return;
                    }
                }
                else if(Battleship.mode1 == mode.SERVER)
                {
                    do
                    {
                        localResponse = Battleship.recvSend(Battleship.peer, Battleship.protocol);
                    }while(!localResponse.equals("shot ok") && !localResponse.equals("win PLAYER2") && !localResponse.equals("win PLAYER1"));
                    
                    if (localResponse.equals("win PLAYER1"))
                    {
                        JOptionPane.showMessageDialog(null,Battleship.protocol.getGame().getPlayer1() + " won!");
                        return;
                    }
                    else if(localResponse.equals("win PLAYER2"))
                    {
                        JOptionPane.showMessageDialog(null,Battleship.protocol.getGame().getPlayer2() + " won!");
                        return;
                    }
                }
			}
		}
		
	}
	

}
