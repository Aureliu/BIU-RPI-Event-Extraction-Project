package edu.cuny.qc.util;

import java.io.File;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LoggerUtils {
	public static final String LOG4J_PROPERTIES = "log4j.properties";

	public static Set<Appender> getAllAppendersIncludingParents(Logger logger)
	{
		Set<Appender> ret = new LinkedHashSet<Appender>();
		for (Category category = logger; category != null; category = category.getParent())
		{
			Enumeration<?> enumeration = category.getAllAppenders();
			while (enumeration.hasMoreElements())
			{
				// I have to use RTTI, since current version of log4j provides no
				// other alternative.
				Appender appender = (Appender) enumeration.nextElement();
				ret.add(appender);
			}
		}
		return ret;
	}
	
	public static Logger initLog(Class<?> cls) {
		
		// Use the file log4j.properties to initialize log4j
		PropertyConfigurator.configure(LOG4J_PROPERTIES);
		
		// Pick the logger, and start writing log messages
		Logger logger = Logger.getLogger(cls);
		
		// Register the log-file(s) (if exist(s)) as file(s) to be saved by ExperimentManager.
		for (Appender appender : getAllAppendersIncludingParents(logger))
		{
			// cannot avoid RTTI, since current implementation of log4j provides
			// no other alternative.
			if (appender instanceof FileAppender)
			{
				File file = new File(((FileAppender)appender).getFile());
				//ExperimentManager.getInstance().register(file);
			}
		}

		return logger;
	}

}
