package com.usefeeling.android.cabinstill.dao;

import java.io.IOException;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.usefeeling.android.cabinstill.facades.ApplicationFacade;

/**
 * Clase de acceso a la base de datos local de la aplicación móvil.
 * @author Moisés Vilar.
 *
 */
public class DataBaseHelper extends SQLiteOpenHelper {

	private Context context;
	private final static String SQL_DIR = "sql";
	private final static String CREATE_FILE = "create.sql";
	private final static String UPGRADE_FILE_PREFIX = "upgrade-";
	private final static String UPGRADE_FILE_SUFFIX = ".sql";
	
	/**
	 * Constructor.
	 * @param context Contexto.
	 */
	public DataBaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			this.executeSqlScript(db, CREATE_FILE);
		} catch (IOException e) {
			throw new RuntimeException("Database creation failed", e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
            for( String sqlFile : ApplicationFacade.listAssets(SQL_DIR, this.context.getAssets())) {
                if ( sqlFile.startsWith(UPGRADE_FILE_PREFIX)) {
                    int fileVersion = Integer.parseInt(sqlFile.substring( UPGRADE_FILE_PREFIX.length(),  sqlFile.length() - UPGRADE_FILE_SUFFIX.length())); 
                    if ( fileVersion >= oldVersion) {
                    	executeSqlScript(db, sqlFile);
                    }
                }
            }
        } catch(IOException e) {
            throw new RuntimeException("Database upgrade failed", e);
        }
	}
	
	/**
	 * Ejecuta en la base de datos las instrucciones almacenadas en un script SQL.
	 * @param db Base de datos.
	 * @param sqlFile Nombre del script SQL.
	 * @throws SQLException
	 * @throws IOException
	 */
	private void executeSqlScript(SQLiteDatabase db, String sqlFile) throws SQLException, IOException {
		for( String sqlInstruction : SqlParser.parseSqlFile( SQL_DIR + "/" + sqlFile, this.context.getAssets())) {
            db.execSQL(sqlInstruction);
        }
	}

}
