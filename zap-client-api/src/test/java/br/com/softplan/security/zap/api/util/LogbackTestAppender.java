package br.com.softplan.security.zap.api.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * Utility class to provide testing capabilities related to logs 
 * (e.g. to check if a warning message was logged).
 * 
 * @author pdsec
 */
public class LogbackTestAppender extends AppenderBase<ILoggingEvent> {

	private final List<ILoggingEvent> log = new ArrayList<>();

	public LogbackTestAppender() {
		setName(getClass().getSimpleName());
	}
	
	@Override
	protected void append(ILoggingEvent eventObject) {
		doAppend(eventObject);
	}

	@Override
	public synchronized void doAppend(ILoggingEvent eventObject) {
		log.add(eventObject);
	}
	
	public List<ILoggingEvent> getLog() {
		return Collections.unmodifiableList(log);
	}

	public boolean hasError() {
		return hasLevel(Level.ERROR);
	}

	public boolean hasWarn() {
		return hasLevel(Level.WARN);
	}

	public boolean hasLevel(Level level) {
		for (ILoggingEvent loggingEvent : log) {
			if (loggingEvent.getLevel().equals(level)) {
				return true;
			}
		}
		return false;
	}

	public void clearLog() {
		log.clear();
	}
	
}
