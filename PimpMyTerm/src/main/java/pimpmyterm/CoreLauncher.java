package pimpmyterm;

import java.io.File;
import java.io.FileInputStream;

import com.thoughtworks.xstream.XStream;

import pimpmyterm.core.Core;
import pimpmyterm.utils.Fonctions;

public class CoreLauncher {

	public static void main(String[] args) {
		String configCore = null;

		try {

			configCore = args[0];

			if (configCore == null) {
				printUsage(configCore);
			}
			// Try to load Core from file
			XStream xs = new XStream();
			Fonctions.trace("INF", "Loading CORE File " + configCore, "CORE");
			Core aCore = (Core) xs.fromXML(new FileInputStream(new File(configCore)));

			Core.setInstance(aCore);

			aCore.launch();

		} catch (Exception e) {
			e.printStackTrace();
			printUsage(configCore);
			System.exit(1);
		}

	}

	private static void printUsage(String receivedCoreConfig) {
		Fonctions.trace("WNG", "I received CoreConfigFile=" + receivedCoreConfig, "CORE");
		Fonctions.trace("DEAD", "Usage java -jar pimpMyTerm.jar <CoreConfigFile>", "CORE");
	}

}
