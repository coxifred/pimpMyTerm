package pimpmyterm.utils;

import org.eclipse.jetty.util.log.AbstractLogger;
import org.eclipse.jetty.util.log.Logger;

import pimpmyterm.core.Core;

public class JettyLogger extends AbstractLogger{
	
	public static boolean debug=false;

	@Override
	public String getName() {
		return "JettyLogger";
	}

	@Override
	public void warn(String msg, Object... args) {
		trace(format(msg, args));
		
	}

	@Override
	public void warn(Throwable thrown) {
		trace(thrown.getMessage());
		
	}

	@Override
	public void warn(String msg, Throwable thrown) {
		trace(msg + " " + thrown.getMessage());
		
	}

	@Override
	public void info(String msg, Object... args) {
		trace(format(msg, args));
		
	}

	@Override
	public void info(Throwable thrown) {
		trace(thrown.getMessage());
		
	}

	@Override
	public void info(String msg, Throwable thrown) {
		trace(msg + " " + thrown.getMessage());
		
	}

	@Override
	public boolean isDebugEnabled() {
		return debug;
	}

	@Override
	public void setDebugEnabled(boolean enabled) {

		
	}

	@Override
	public void debug(String msg, Object... args) {
		trace(format(msg, args));
		
	}

	@Override
	public void debug(Throwable thrown) {

		trace(thrown.getMessage());
		
	}

	@Override
	public void debug(String msg, Throwable thrown) {
		
		trace(msg + " " + thrown.getMessage());
		
	}

	@Override
	public void ignore(Throwable ignored) {

		trace(ignored.getMessage());
		
	}

	@Override
	protected Logger newLogger(String fullname) {
		return new JettyLogger();
	}

	private void trace(String message)
	{
		if ( Core.getInstance().getDebugJetty())
		{
			Fonctions.trace("DBG", message, "JETTY");
		}
	}
	
	private String format(String msg, Object... args)
    {
        msg = String.valueOf(msg); // Avoids NPE
        String braces = "{}";
        StringBuilder builder = new StringBuilder();
        int start = 0;
        for (Object arg : args)
        {
            int bracesIndex = msg.indexOf(braces, start);
            if (bracesIndex < 0)
            {
                builder.append(msg.substring(start));
                builder.append(" ");
                builder.append(arg);
                start = msg.length();
            }
            else
            {
                builder.append(msg.substring(start, bracesIndex));
                builder.append(String.valueOf(arg));
                start = bracesIndex + braces.length();
            }
        }
        builder.append(msg.substring(start));
        return builder.toString();
    }
}