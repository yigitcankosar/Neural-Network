package MnistDb;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class MnistPanel extends JPanel {


	private MnistImage image;
	private MnistLabel label;
	
	public int[] image_buffer = new int[784];
	public int label_buffer = -1;

	public MnistPanel(MnistImage image, MnistLabel label) {
		super();
		this.setImage(image);
		this.setLabel(label);
	}

	public void nextImage() {
		try {
			this.image.next();
			this.label.next();
			
			for(int i = 0; i < 784; i++){
				image_buffer[i] = image.read();
			}
			label_buffer = label.readLabel();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public MnistImage getImage() {
		return image;
	}

	public void setImage(MnistImage image) {
		this.image = image;
	}

	public MnistLabel getLabel() {
		return label;
	}

	public void setLabel(MnistLabel label) {
		this.label = label;
	}
	
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		
		int w = this.getWidth();
		int h = this.getHeight();
		System.out.println(w+ "   " + h);
		g.clearRect(0, 0, w, h);
		for(int i = 0; i < 784; i++){
			int x = (i % 28) * w / 28;
			int y = ((int)i / (int)28) * h / 28;
			g2d.setColor(new Color(image_buffer[i]));
			g2d.fillRect(x, y, w / 28 + 1, h / 28 + 1);
		}
		Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .3f);
	    g2d.setComposite(comp);
	    g2d.setPaint(Color.red);
	    g2d.drawString(""+label_buffer,h / 2, w /2);
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException{
		
		String path = new File("").getAbsolutePath();

		MnistImage m = new MnistImage(path + "/src/trainImage.idx3-ubyte", "rw");
		MnistLabel l = new MnistLabel(path + "/src/trainLabel.idx1-ubyte", "rw");
		
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBounds(100, 100, 450, 300);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		
		MnistPanel p =new MnistPanel(m,l);
		p.nextImage();
		
		contentPane.add(p, BorderLayout.CENTER);
		
		f.setContentPane(contentPane);
		f.setVisible(true);
		
		for(int i = 0; i < 4000; i++){
			Thread.sleep(500);
			p.nextImage();
			f.repaint();
		}
	}

}
