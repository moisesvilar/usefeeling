package com.usefeeling.android.cabinstill.helpers;

/**
 * Clase para sincronización entre hilos.
 * @author Luisma Morán.
 *
 */
public class Synchronizer {
	
	/**
	 * El hilo que invoca el método suspende su ejecución hasta que sea notificado por otro hilo.
	 */
	public synchronized void espera() {
		try {
			this.wait();
		} catch (InterruptedException ie) {

		}
	}
	
	/**
	 * El hilo que invoca el método notifica a todos los hilos suspendidos que pueden
	 * restaurar su ejecución.
	 */
	public synchronized void notifica() {
		this.notify();
	}
}
