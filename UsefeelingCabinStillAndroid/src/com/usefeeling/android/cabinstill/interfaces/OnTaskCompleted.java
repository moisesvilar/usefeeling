package com.usefeeling.android.cabinstill.interfaces;

/**
 * Interfaz para operaciones completadas por parte de tareas asíncronas.
 * Esta interfaz la implementan, sobre todo, Activities que lanzan tareas asíncronas,
 * para que puedan establecer resultados tras la ejecución de las operaciones solicitadas.<br/><br/>
 * Por ejemplo:<br/><br/>
 * <pre><font face="courier new">public MyActivity implements OnTaskCompleted {
 * 	//your activity
 * 	public void onTaskCompleted(Object result) {
 * 		//your stuff
 * 	}
 * } 
 * </font></pre>
 * @author Moisés Vilar.
 *
 */
public interface OnTaskCompleted {
	void onTaskCompleted(Object result);
}
