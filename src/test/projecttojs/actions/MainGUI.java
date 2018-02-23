package test.projecttojs.actions;

import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.prefs.Preferences;
//TODO rewrite this
public class MainGUI implements IDialogHandler {
	private IDialog _dialog;
    private Component _component;
    public JTextField _inputField1;
    public JButton closeButton;
    public Boolean activateGenerator = false;
	
	public Component getComponent(){
		
		JLabel testLabel = new JLabel("Choose workspace path");
		_inputField1 = new JTextField(50);
		Preferences prefs = Preferences.userRoot();
		_inputField1.setText(prefs.get("FileDir", ""));
		closeButton = new JButton("Generate Javascript");
		closeButton.addAncestorListener(new RequestFocusListener());
		JButton browseButton = new JButton("Browse");
		browseButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setDialogTitle("Select target directory");
				int returnVal = fileChooser.showOpenDialog(null);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					String fileDirectory = file.getAbsolutePath();
					_inputField1.setText(fileDirectory.replace("\\", "/"));
				}
			}
		});
		JPanel pane = new JPanel();
		pane.setLayout(new FlowLayout());
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				activateGenerator = true;
				_dialog.close();
			}
		});
		
		
		pane.add(testLabel);
		pane.add(_inputField1);
		pane.add(browseButton);
		pane.add(closeButton);
		
		this._component = pane;
		return pane;
	}
	public void prepare(IDialog dialog){
		this._dialog = dialog;
		dialog.setTitle("Generate Javascript");
		dialog.pack();
		
	}
	public void shown(){
		
	}
	public boolean canClosed(){
		return true;
	}
	
	static String readFile(String path, Charset encoding) throws IOException 
	{
		List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
		return lines.get(0);
	}
	
	public class RequestFocusListener implements AncestorListener
	{
		private boolean removeListener;

		/*
		 *  Convenience constructor. The listener is only used once and then it is
		 *  removed from the component.
		 */
		public RequestFocusListener()
		{
			this(true);
		}

		/*
		 *  Constructor that controls whether this listen can be used once or
		 *  multiple times.
		 *
		 *  @param removeListener when true this listener is only invoked once
		 *                        otherwise it can be invoked multiple times.
		 */
		public RequestFocusListener(boolean removeListener)
		{
			this.removeListener = removeListener;
		}

		@Override
		public void ancestorAdded(AncestorEvent e)
		{
			JComponent component = e.getComponent();
			component.requestFocusInWindow();

			if (removeListener)
				component.removeAncestorListener( this );
		}

		@Override
		public void ancestorMoved(AncestorEvent e) {}

		@Override
		public void ancestorRemoved(AncestorEvent e) {}
	}
}
