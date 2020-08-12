package pimpmyterm.threads;

import pimpmyterm.utils.Fonctions;

public class ThreadMemory extends Thread {

	@Override
	public void run() {

		while (true) {
			Fonctions.trace("DBG", "Total memory:" + Fonctions.getTotalMemory() + ",Used memory:"
					+ Fonctions.getUsedMemory() + ",Free memory:" + Fonctions.getFreeMemory(), "CORE");

			Fonctions.attendre(60000);
		}
	}

}
