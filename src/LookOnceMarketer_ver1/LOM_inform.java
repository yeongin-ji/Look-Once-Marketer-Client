package LookOnceMarketer_ver1;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class LOM_inform {
	static {
		// Load the native OpenCV library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	static LOM_client client = new LOM_client();
	Mat img = new Mat();
	static Mat socketSendImg = new Mat();
	MyFrame f;
	VideoCapture cap = new VideoCapture(0);

	public LOM_inform(String title) {
		f = new MyFrame(title);
		f.setVisible(true);

		// Check if video capturing is enabled
		if (!cap.isOpened()) {
			System.exit(-1);
		}
		WebcamStreamThread camThread = new WebcamStreamThread(this);
		camThread.start();
	}

}

class WebcamStreamThread extends Thread {
	LOM_inform inform;

	public WebcamStreamThread(LOM_inform inform) {
		this.inform = inform;
	}

	public void run() {
		while (true) {
			inform.cap.read(inform.img);
			Mat resizeimage = new Mat();
			Size sz = new Size(480, 270);
			Size modelInputSz = new Size(640, 480);
			Imgproc.resize(inform.img, inform.socketSendImg, modelInputSz);
			Imgproc.resize(inform.img, resizeimage, sz);
			if (!inform.img.empty()) {
				inform.f.render(resizeimage);
			} else {
				System.out.println("[Client]: No captured frame!");
			}
		}
	}
}

class MyFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private MyPanel webcamPanel;
	private Container con;
	JButton btn = new JButton("Get Apple Marketability!");

	JPanel panel = new JPanel();
	MyLabel label1 = new MyLabel("[Marketability]");
	MyLabel label2 = new MyLabel("[Result]");

	public MyFrame(String title) {
		con = getContentPane();
		con.setLayout(new GridBagLayout());
		con.add(btn);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(title);
		webcamPanel = new MyPanel();

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(10, 10, 10, 10);
		con.add(webcamPanel, c);

		panel.add(label1);
        panel.add(label2);
        
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 10, 10, 10);
        con.add(btn, c);

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.CENTER;
        con.add(panel, c);
        
        con.setBackground(new Color(255, 239, 213));
		
		btn.addActionListener(this);
		pack();
	}

	public void render(Mat img) {
		webcamPanel.render(img);
	}

	public void actionPerformed(ActionEvent e) {
		System.out.println("Button clicked");
		LOM_inform.client.imageSend();
		String[] receive = LOM_inform.client.imageReceive();
		label1.setText(receive[0]);
		label2.setText(receive[1]);
	}
}

class MyPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage image;

	public void render(Mat mat) {
		int width = mat.cols();
		int height = mat.rows();
		int channels = mat.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		mat.get(0, 0, sourcePixels);

		image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

		repaint();
	}

	@Override
	protected void paintComponent(java.awt.Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			g.drawImage(image, 0, 0, null);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(480, 270);
	}
}

class MyLabel extends JLabel {
	public MyLabel(String title) {
		super(title);
		setBackground(new Color(255, 239, 213));
	}
}
