package com.usefeeling.android.cabinstill.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.usefeeling.android.cabinstill.facades.ApplicationFacade;

/**
 * Clase abstracta de la que heredan todos los Data Access Object.
 * @author Moisés Vilar.
 *
 */
public abstract class AbstractDao {

	public final static String DATABASE_NAME = "usefeeling.db";
	protected DataBaseHelper dbHelper;
	protected SQLiteDatabase db;
	protected Context context;
	private boolean isUpgraded;
	
	/**
	 * Constructor.
	 * @param context Contexto.
	 */
	public AbstractDao(Context context) {
		this.context = context;
		this.dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, ApplicationFacade.getVersionCode(context));
		this.isUpgraded = false;
	}
	
	/**
	 * Abre la base de datos.
	 * @return Si la base de datos se ha abierto correctamente.
	 */
	protected boolean open() {
		try {
			this.db = this.dbHelper.getWritableDatabase();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Cierra la base de datos.
	 */
	protected void close() {
		try {
			synchronized (this.db) {
				this.db.close();
			}
		} catch (Exception e) {}
	}
	
	/**
	 * Actualiza la base de datos.
	 * @throws Exception 
	 */
	protected void upgrade() throws Exception {
		if (this.isUpgraded) throw new Exception("Database alreade upgraded");
		try {
			this.dbHelper.onUpgrade(db, 0, ApplicationFacade.getVersionCode(this.context));
			this.isUpgraded = true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
}
