package pimpmyterm.threads;

import pimpmyterm.beans.User;
import pimpmyterm.core.Core;
import pimpmyterm.utils.Fonctions;

public class ThreadSaver extends Thread {

	

	@Override
	public void run() {

		while (true) {
			Fonctions.trace("DBG", "Saving data now", "CORE");
			for (User aUser : Core.getInstance().getUsers().values()) {
				aUser.saveUser();
			}

			Fonctions.attendre(60000);
		}
	}

	
}
