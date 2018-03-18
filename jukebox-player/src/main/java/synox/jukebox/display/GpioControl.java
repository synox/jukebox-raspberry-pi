package synox.jukebox.display;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class GpioControl {

	private final GpioPinDigitalOutput led;
	private final GpioController gpio;
	private final GpioPinDigitalInput button;

	public GpioControl() {
		gpio = GpioFactory.getInstance();
		led = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_09, "MyLED", PinState.HIGH);
		// set shutdown state for this pin
		led.setShutdownOptions(true, PinState.LOW);

		button = gpio.provisionDigitalInputPin(RaspiPin.GPIO_08, PinPullResistance.PULL_DOWN);
	}

	public void addButtonListener(GpioPinListenerDigital listener) {
		button.addListener(listener);
	}

	public void ledOn() {
		led.high();
	}

	public void ledOff() {
		led.low();
	}

	public void ledPulse(int ms) {
		led.pulse(ms, true);
	}

	public void ledMultiPulse() {
		try {
			int duration = 100;
			led.pulse(duration, true);
			Thread.sleep(duration);
			led.pulse(duration, true);
			Thread.sleep(duration);
			led.pulse(duration, true);
			Thread.sleep(duration);
			led.pulse(duration, true);
			Thread.sleep(duration);
			led.pulse(2000, true);
		} catch (InterruptedException e) {
		}

	}

	public void stop() {
		gpio.shutdown();
	}

	public void initialize() {
		this.ledOff();

	}

}
