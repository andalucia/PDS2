import au.edu.jcu.v4l4j.CaptureCallback;
import au.edu.jcu.v4l4j.VideoFrame;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;


public class FrameHolder implements CaptureCallback {

	private VisionGUI guiWindow;
	
	@Override
	public void exceptionReceived(V4L4JException arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nextFrame(VideoFrame arg0) {
		// tell GUI to update image
		guiWindow.updateImage(arg0.getBufferedImage());
	}

}
