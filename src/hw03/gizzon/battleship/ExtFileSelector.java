package hw03.gizzon.battleship;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;


/**
* The ExtFileSelector class is created so a file can be selected by a user 
* during the process of connecting to a server. This class will ask a user
* to enter a file name and it will save their input to a member variable.
 * @author NicoleGizzo
 *
 */
public class ExtFileSelector implements Runnable{
	private File user_input = null;

	/*
	* This function will open a filechooser so the user can select a file.
	*
	*/
	public void run() {
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		File workingDirectory = new File(System.getProperty("user.dir"));
		jfc.setCurrentDirectory(workingDirectory);
		int returnValue = jfc.showOpenDialog(null);
		
		user_input = jfc.getSelectedFile();
	}
	
	/**
	* This function will return the file that the user selected.
	* @return The user's selected file
	*
	*/
	public File getFile() {
		return user_input;
	}
	
}

