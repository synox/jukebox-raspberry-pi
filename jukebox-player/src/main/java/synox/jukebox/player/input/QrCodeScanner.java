package synox.jukebox.player.input;

import com.github.sarxos.webcam.*;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class QrCodeScanner implements CodeScanner {
	static {
		Webcam.setDriver(new V4l4jDriver());
	}

	private String lastCode;

	private final List<CodeInputListener> listeners = new ArrayList<>();

	private WebcamMotionDetector motionDetector;
	private Webcam webcam;

	@Override
	public void addCodeListener(CodeInputListener listener) {
		listeners.add(listener);
	}

	@Override
	public void start() {
		new Thread() {
			@Override
			public void run() {
				startWebcam();
			}
		}.start();
	}

	private String decodeQrCode(BufferedImage image) {
		BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
		try {
			Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap);
			return qrCodeResult.getText();
		} catch (NotFoundException e) {
			// return null when no barcode is found.
			return null;
		}
	}

	private void fireNewCodeDetected(String code) {
		for (CodeInputListener listener : listeners) {
			listener.newCodeDetected(code);
		}

	}

	private void parseImage() {
		BufferedImage image = webcam.getImage();
		String code = decodeQrCode(image);
		if (code != null) {
			// Avoid duplicate scan
			if (!code.equals(lastCode)) {
				lastCode = code;
				fireNewCodeDetected(code);
			}
		}
	}

	private void startWebcam() {
		webcam = Webcam.getDefault();
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		webcam.open();

		motionDetector = new WebcamMotionDetector(webcam);
		motionDetector.setInterval(500); // one check per X ms
		motionDetector.addMotionListener(new WebcamMotionListener() {
			@Override
			public void motionDetected(WebcamMotionEvent arg0) {
				parseImage();
			}
		});
		motionDetector.setAreaThreshold(5);
		motionDetector.start();
	}

}
