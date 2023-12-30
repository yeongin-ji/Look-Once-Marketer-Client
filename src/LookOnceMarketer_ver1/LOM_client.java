package LookOnceMarketer_ver1;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;

public class LOM_client {

	Socket socket = null;
	InputStream in = null;
	OutputStream out = null;
	DataInputStream din = null;
	BufferedOutputStream bout = null;
	BufferedImage sendImg = null;
	
	public LOM_client() {
		//JButton btn = (JButton) e.getSource();// button text받아오기
		try {
			socket = new Socket("127.0.0.1", 90);
			
			in = socket.getInputStream();
			out = socket.getOutputStream();
			bout = new BufferedOutputStream(out);
			din = new DataInputStream(in);
		} catch (Exception e2) {
			// TODO: handle exception
		}
	}
	
	public void imageSend() {
		try {
			sendImg = toBufferedImage(LOM_inform.socketSendImg);
			ImageIO.write(sendImg, "png", bout);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			
		}
	}
	
	public String[] imageReceive() {
		String receiveStr = null;
		String[] splitStr = null;
		try {
			//BufferedReader bf = new BufferedReader(new InputStreamReader(in));
			while((receiveStr = din.readUTF()) == null) {System.out.println("waiting..");}
			splitStr = receiveStr.split(" ");
			System.out.println(splitStr);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("[Client]: Image receive error!");
		}
		return splitStr;
	}
	
	public void closeSocket() {
		try {
			bout.close();
		} catch (Exception e3) {
			// TODO: handle exception
		}
		try {
			out.close();
		} catch (Exception e3) {
			// TODO: handle exception
		}
	}

	public static BufferedImage toBufferedImage(Mat m) {
		// Code from
		// http://stackoverflow.com/questions/15670933/opencv-java-load-image-to-gui

		// Check if image is grayscale or color
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}

		// Transfer bytes from Mat to BufferedImage
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}
}