package pimpmyterm.webserver.websocket;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class JettyWebSocketServlet extends WebSocketServlet
{
  

	/**
	 * 
	 */
	private static final long serialVersionUID = 5285839573089090765L;

	@Override
	public void configure(WebSocketServletFactory factory) {
		 // register a socket class as default
		factory.getPolicy().setIdleTimeout(1000 * 360000);
        factory.register(AdminWebSocket.class);
		
	}

}