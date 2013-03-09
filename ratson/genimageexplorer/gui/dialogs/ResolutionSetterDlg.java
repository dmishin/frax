package ratson.genimageexplorer.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ResolutionSetterDlg extends JDialog {
	
	private JTextField fldWidth;
	private JTextField fldHeight;
	private JCheckBox boxMaintainProportions;
	private Action actnOk, actnCancel;

	private int width, height;
	private JButton btnOk,btnCancel;
	private boolean isEventsDisabled=false;
	
	public ResolutionSetterDlg(Frame owner, String title, String description){
		super(owner, title);
//		setSize(400,240);
		initActions();
		initComponents();
		bindEvents();
		
		getRootPane().setDefaultButton(btnOk);
	}

	private void initActions() {
		actnOk=new AbstractAction("OK"){
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
			
		};
		actnCancel=new AbstractAction("Cancel"){
			public void actionPerformed(ActionEvent e) {
				width=-1;
				height=-1;
				setVisible(false);
			}
			
		};
	}


	private void bindEvents(){

		fldWidth.getDocument().addDocumentListener(new DocumentListener(){
			private void changed(){
				widthChanged();
			}
			public void changedUpdate(DocumentEvent e) {}
			public void insertUpdate(DocumentEvent e) {changed();}
			public void removeUpdate(DocumentEvent e) {changed();}
			
		});
		fldHeight.getDocument().addDocumentListener(new DocumentListener(){
			private void changed(){
				heightChanged();
			}
			public void changedUpdate(DocumentEvent e) {}
			public void insertUpdate(DocumentEvent e) {changed();}
			public void removeUpdate(DocumentEvent e) {changed();}
			
		});
		
		
		this.addWindowListener(new WindowListener(){

			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});

	}
	

	protected void heightChanged() {
		if (isEventsDisabled)
			return;
		int hgt=-1;
		try{
			hgt=Integer.parseInt(fldHeight.getText());
		}catch (NumberFormatException e){
			return;
		}
		if (hgt<=0)
			return;
		
		int oldHgt=height;
		height=hgt;
		if (boxMaintainProportions.isSelected()){
			float k=(float)width/(float)oldHgt;
			int newWidth=Math.round(k*height);
			width=newWidth;
			isEventsDisabled=true;
			fldWidth.setText(String.valueOf(newWidth));
			isEventsDisabled=false;

		}
	}

	protected void widthChanged() {
		if (isEventsDisabled)
			return;
		int wdt=-1;
		try{
			wdt=Integer.parseInt(fldWidth.getText());
		}catch (NumberFormatException e){
			return;
		}
		if (wdt<=0)
			return;
		
		int oldWdt=width;
		width=wdt;
		if (boxMaintainProportions.isSelected()){
			float k=(float)height/(float)oldWdt;
			height=Math.round(k*width);
			isEventsDisabled=true;
			fldHeight.setText(String.valueOf(height));
			isEventsDisabled=false;
		}
	}
	
	public boolean doModal(int w, int h){
		width=w;
		height=h;
		fldWidth.setText(String.valueOf(w));
		fldHeight.setText(String.valueOf(h));
		boxMaintainProportions.setSelected(true);
		
		setModal(true);
		setVisible(true);
		
		return width>=0 && height>=0;
	}

	public int getResolutionY() {
		return height;
	}

	public int getResolutionX() {
		return width;
	}
	
    private void initComponents() {

        btnOk = new javax.swing.JButton(actnOk);
        btnCancel = new javax.swing.JButton(actnCancel);
        panelFrame = new javax.swing.JPanel();
        lblWidth = new javax.swing.JLabel("Width");
        fldWidth = new javax.swing.JTextField();
        fldHeight = new javax.swing.JTextField();
        lblX = new javax.swing.JLabel("X");
        lblHeight = new javax.swing.JLabel("Height");
        boxMaintainProportions = new javax.swing.JCheckBox("Constrain proportions");

        setResizable(false);

        panelFrame.setBorder(new TitledBorder("Resolution"));

        javax.swing.GroupLayout panelFrameLayout = new javax.swing.GroupLayout(panelFrame);
        panelFrame.setLayout(panelFrameLayout);
        panelFrameLayout.setHorizontalGroup(
            panelFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFrameLayout.createSequentialGroup()
                        .addGroup(panelFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelFrameLayout.createSequentialGroup()
                                .addComponent(fldWidth, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblX))
                            .addComponent(lblWidth))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fldHeight, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblHeight)))
                    .addComponent(boxMaintainProportions))
                .addGap(39, 39, 39))
        );
        panelFrameLayout.setVerticalGroup(
            panelFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFrameLayout.createSequentialGroup()
                .addGroup(panelFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblWidth)
                    .addComponent(lblHeight))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fldWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblX)
                    .addComponent(fldHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(boxMaintainProportions)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelFrame, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelFrame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(btnCancel)))
        );

        pack();
    }
    
    private javax.swing.JLabel lblWidth;
    private javax.swing.JLabel lblX;
    private javax.swing.JLabel lblHeight;
    private javax.swing.JPanel panelFrame;


}
