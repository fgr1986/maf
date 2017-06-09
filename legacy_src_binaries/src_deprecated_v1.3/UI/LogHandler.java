package UI;

//import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

public class LogHandler extends Handler {
	private String messages;
//	private Formatter formatter = null;
//
//	private Level level = null;

	public LogHandler() {
		LogManager manager = LogManager.getLogManager();
		String className = this.getClass().getName();
		String level = manager.getProperty(className + ".level");
		setLevel(level != null ? Level.parse(level) : Level.INFO);
		messages = "";
	}

//	public static synchronized LogHandler getInstance() {
//		if (handler == null) {
//			handler = new LogHandler();
//		}
//		return handler;
//	}

	public synchronized void publish(LogRecord record) {
		if (!isLoggable(record))
			return;
		messages = getFormatter().format(record);
	}

	public void close() {
	}

	public void flush() {
	}

	public String getMessages() {
		return messages;
	}

	public void setMessages(String messages) {
		this.messages = messages;
	}
	
}
