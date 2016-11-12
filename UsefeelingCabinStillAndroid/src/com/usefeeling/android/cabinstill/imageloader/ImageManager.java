package com.usefeeling.android.cabinstill.imageloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.api.UseFeeling;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;

/**
 * Gestor de imágenes a mostrar sobre ImageViews.
 *
 */
public class ImageManager {
	
	private static final String CACHE_PATH = "data/usefeeling";
	private Activity mContext;
	private static File fileCache;
	private static LruCache<String, Bitmap> mMemoryCache;
	private static final int STROKE_WIDTH = 2;
	private static final float ROUND_WIDTH = 20.0f;
	public static final int COMPRESSION_LEVEL = 75;
	public static final int MARGIN = 2;
	private static final float SCALE_FACTOR = 100;
	
	/**
	 * Tamaños de imágenes.
	 * @author Moisés Vilar.
	 *
	 */
	private static final class ImagesSizes {
		public static final int MARKER_SIZE = 32;
		public static final float BORDER_SIZE = 20.0f;
	}
	
	/**
	 * Transforma la imagen en un array de bytes.
	 * @param picture Imagen.
	 * @return Un array de bytes o null si ha ocurrido algún error.
	 */
	public static byte[] toByteArray(Bitmap picture){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		if (picture.compress(CompressFormat.JPEG, COMPRESSION_LEVEL, bos)){
			return bos.toByteArray();
		}
		else {
			return null;
		}
	}
	
	/**
	 * Superpone dos imágenes en una.
	 * @param background Imagen que hará de fondo.
	 * @param overlay Imagen a superponer a la de fondo.
	 * @param left Posición izquierda de la imagen a superponer.
	 * @param top Posición superior de la imagen a superponer.
	 * @return La imagen superpuesta.
	 */
	public static Bitmap overlayBitmaps(Bitmap background, Bitmap overlay, float left, float top) {
		int rw = background.getWidth();
		int rh = background.getHeight();
		Bitmap result = Bitmap.createBitmap(rw, rh, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(background, 0, 0, null);
		canvas.drawBitmap(overlay, left, top, null);
		return result;
	}

	/**
	 * Añade un borde del color especificado a la imagen.
	 * @param bitmap Imagen.
	 * @param color Color del borde.
	 * @return Imagen con el borde añadido.
	 */
	public static Bitmap addStroke(Bitmap bitmap, int color) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		Paint paintStroke = new Paint();
		paintStroke.setStrokeWidth(STROKE_WIDTH);
		paintStroke.setStyle(Paint.Style.STROKE);
		paintStroke.setColor(color);
		paintStroke.setAntiAlias(true);
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, ROUND_WIDTH, ROUND_WIDTH, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		canvas.drawRoundRect(rectF, ROUND_WIDTH, ROUND_WIDTH, paintStroke);
		return output;
	}

	/**
	 * Crea la imagen para ser representada como icono, con bordes redondeados y color en función del tipo de imagen a representar.
	 * @param context Contexto desde el que se invoca el método.
	 * @param bitmap Imagen original.
	 * @param url URL de la imagen, que contiene el tipo de imagen.
	 * @return El bitmap para el marcador a ser visualizado sobre el mapa.
	 */
	public static Bitmap createImageIcon(Context context, Bitmap bitmap, String url) {
		if (!url.startsWith("userid")) return bitmap;
		//Recortamos el bitmap para hacerlo cuadrado
		bitmap = squareShape(context, bitmap);
		if (bitmap == null) return bitmap;
		//Obtenemos el tamaño del bitmap cuadrado
		int size = bitmap.getHeight();
		//Acortamos al tamaño máximo del marcador.
		if (size > ImagesSizes.MARKER_SIZE) size = ImagesSizes.MARKER_SIZE;
		//Obtenemos el tamaño del bitmap en pixeles
		int size_px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, context.getResources().getDisplayMetrics());
		//Creamos el bitmap de salida
		Bitmap output = Bitmap.createBitmap(size_px, size_px, Bitmap.Config.ARGB_8888);
		//Obtenemos la imagen como drawable que contendrá la imagen redondeada
		Drawable roundDrawable = new BitmapDrawable(context.getResources(), bitmap);
		//Creamos el lienzo a partir del bitmap de salida
		Canvas canvas = new Canvas(output);
		//Creamos un cuadrado de lado size
		RectF outerRect = new RectF(0, 0, size_px, size_px);
		//Establecemos el radio de las esquinas redondeadas
		float cornerRadius = size_px / 18f;
		//Establecemos el flag AntiAliasing para el rectángulo redondeado
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		//Dibujamos el cuadrado redondeado en el lienzo
		canvas.drawRoundRect(outerRect, cornerRadius, cornerRadius, paint);
		//Se queda con la parte destino que interseca con la fuente
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		//Establecemos los límites del drawable
		roundDrawable.setBounds(0, 0, size_px, size_px);
		//Aplicamos color a la capa con el rectángulo redondeado
		canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG);
		//Pintamos el drawable en el lienzo redondeado
		roundDrawable.draw(canvas);
		//Restauramos el lienzo
		canvas.restore();
		//Establecemos el tamaño del borde
		float border = size_px / ImagesSizes.BORDER_SIZE;
		//Creo el rectángulo exterior del borde
		Bitmap framedOutput = Bitmap.createBitmap(size_px, size_px, Bitmap.Config.ARGB_8888);
		//Creo un lienzo a partir del rectángulo exterior del borde
		Canvas framedCanvas = new Canvas(framedOutput);
		//Creamos el rectángulo interior del borde
		RectF innerRect = new RectF(border, border, size_px - border, size_px - border);
		//Establecemos el flag AntiAliasing para el rectángulo interior del borde
		Paint innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		//Establecemos el color del borde
		innerPaint.setColor(findColorByUrl(url));
		//Dibujamos el rectángulo interior en el lienzo
		framedCanvas.drawRoundRect(innerRect, cornerRadius, cornerRadius, innerPaint);
		//Establecemos el flag AntiAlaising para el rectángulo exterior del borde
		Paint outerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		//Nos quedamos con la parte de destino que no está en la fuente
		outerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
		//Establecemos el color del rectángulo exterior
		outerPaint.setColor(findColorByUrl(url));
		//Establecemos el color del rectángulo exterior como translúcido.
		outerPaint.setAlpha(120);
		//Dibujamos el rectángulo exterior
		framedCanvas.drawRoundRect(outerRect, cornerRadius, cornerRadius, outerPaint);
		//Dibujo el borde en el lienzo original
		canvas.drawBitmap(framedOutput, 0f, 0f, null);
		//Devolvemos resultado
		return output;
	}
	
	/**
	 * Función que recorta del drawable el lado más grande para hacerlo del
	 * tamaño del más pequeño (un cuadrado).
	 * @param context Contexto desde el que se invoca este médoto.
	 * @param bitmap Contiene la imagen que haremos cuadrada
	 * @return Bitmap con la imagen cuadrada
	 */
	public static Bitmap squareShape(Context context, Bitmap bitmap) {
		try {
			//Si ya es cuadrado
			if (bitmap.getHeight() == bitmap.getWidth()) return bitmap;
			//Si más alto que ancho
			else if (bitmap.getHeight() > bitmap.getWidth()) {
				int x = 0;
				int y = bitmap.getHeight() / 2 - bitmap.getWidth() / 2;
				int width = bitmap.getWidth();
				int height = bitmap.getHeight()/2 + bitmap.getWidth()/2;
				return Bitmap.createBitmap(bitmap, x, y, width, height);
			}
			//Si más ancho que alto
			else {
				int x = bitmap.getWidth()/2 - bitmap.getHeight()/2;
				int y = 0;
				int width = bitmap.getWidth() / 2 + bitmap.getHeight() / 2;
				int height = bitmap.getHeight();
				return Bitmap.createBitmap(bitmap, x, y, width, height);
			}
		} catch (OutOfMemoryError e) {
			Log.e("UseFeeling", "ImageHelper.squareShape. OutOfMemoryError: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Reescala una imagen
	 * @param context Contexto desde el que se invoca el método.
	 * @param bitmap Imagen.
	 * @return La imagen reescalada.
	 */
	 public static Bitmap scaleDown(Context context, Bitmap bitmap) {
		 final float densityMultiplier = context.getResources().getDisplayMetrics().density;        
		 int h= (int) (SCALE_FACTOR * densityMultiplier);
		 int w= (int) (h * bitmap.getWidth()/((double) bitmap.getHeight()));
		 bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
		 return bitmap;
	 }
	
	/**
	 * Encuentra el color adecuado en función del tipo de imagen a representar.
	 * @param url URL de la imagen, que contiene el tipo de imagen: de perfil de usuario u otra.
	 * @return El color adecuado al tipo de imagen a representar.
	 */
	private static int findColorByUrl(String url) {
		if (url.startsWith("userid")) return Color.WHITE;
		else return Color.TRANSPARENT;
	}
	
	/**
	 * Constructor.
	 * @param context Contexto desde el que se instancia el gestor.
	 */
	public ImageManager(Activity context) {
		this.mContext = context;
		// Get max available VM memory, exceeding this amount will throw an
	    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
	    // int in its constructor.
	    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	    // Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = maxMemory / 8;
	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	        	ByteArrayOutputStream bao = new ByteArrayOutputStream();
	        	bitmap.compress(Bitmap.CompressFormat.PNG, 100, bao);
	        	byte[] ba = bao.toByteArray();
	        	int size = ba.length;
	            return size / 1024;
	        }
	    };
		String sdState = android.os.Environment.getExternalStorageState();
	    if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
	      File sdDir = context.getExternalCacheDir();    
	      fileCache = new File(sdDir,CACHE_PATH);
	    }
	    else {
	    	fileCache = context.getCacheDir();
	    }
	    if(!fileCache.exists()) {
	    	fileCache.mkdirs();
	    }
	}
	
	/**
	 * Añade una imagen a la caché en memoria.
	 * @param key Clave.
	 * @param bitmap Imagen.
	 */
	public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	    }
	}

	/**
	 * Recupera una imagen de la caché en memoria.
	 * @param key Clave.
	 * @return La imagen.
	 */
	public static Bitmap getBitmapFromMemCache(String key) {
	    return mMemoryCache.get(key);
	}
	
	/**
	 * Decodifica una imagen al tamaño requerido.
	 * @param res
	 * @param picture
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmap(Resources res, byte[] picture, int reqWidth, int reqHeight) {
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeByteArray(picture, 0, picture.length, options);
	    // Calculate inSampleSize
	    if (reqWidth > 0 && reqHeight > 0) options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeByteArray(picture, 0, picture.length, options);
	}
	
	/**
	 * Decodifica una imagen al tamaño requerido.
	 * @param res
	 * @param picture
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmap(Resources res, int picture, int reqWidth, int reqHeight) {
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, picture, options);
	    // Calculate inSampleSize
	    if (reqWidth > 0 && reqHeight > 0) options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(res, picture, options);
	}
	
	/**
	 * Decodifica una imagen al tamaño requerido.
	 * @param res
	 * @param picture
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmap(Resources res, Bitmap picture, int reqWidth, int reqHeight) {
		 // Convert Bitmap picture to byte array
	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
	    byte[] byteArray = stream.toByteArray();
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
	    // Calculate inSampleSize
	    if (reqWidth > 0 && reqHeight > 0) options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
	}
	
	/**
	 * Calcula el tamaño de una imagen según los parámetros requeridos.
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);
	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	
	    return inSampleSize;
	}
	
	/**
	 * Carga una imagen desde la URL especificada.
	 * @param url URL.
	 * @param view View donde se mostrará la imagen descargada.
	 * @param holderBitmap Bitmap que se mostrará mientras la tarea no finaliza.
	 */
	public void loadBitmap(String url, View view, int drawableId, boolean cut) {
		if (url == null) return;
	    if (cancelPotentialWork(url, view)) {
			final Bitmap bitmap = getBitmapFromMemCache(url);
			if (bitmap != null) {
				if (view instanceof ImageView) ((ImageView)view).setImageBitmap(bitmap);
				else if (view instanceof TextView) ((TextView)view).setCompoundDrawablesWithIntrinsicBounds(null, new BitmapDrawable(this.mContext.getResources(), bitmap), null, null);
				view.setBackgroundColor(Color.TRANSPARENT);
			}
			else {
				BitmapWorkerTask task;
				if (cut) task = new BitmapWorkerTask(this.mContext, view, view.getMeasuredWidth(), view.getMeasuredHeight());
				else task = new BitmapWorkerTask(this.mContext, view);
				final AsyncDrawable asyncDrawable = new AsyncDrawable(this.mContext.getResources(), BitmapFactory.decodeResource(this.mContext.getResources(), drawableId), task);
				if (view instanceof ImageView) ((ImageView)view).setImageDrawable(asyncDrawable);
				else if (view instanceof TextView) ((TextView)view).setCompoundDrawablesWithIntrinsicBounds(null, asyncDrawable, null, null);
				task.execute(url);
			}
	    }
	}
	
	/**
	 * Comprueba si hay una tarea ejecutándose para esta url y la cancela en caso afirmativo.
	 * @param url URL.
	 * @param imageView ImageView donde almacenar el resultado de la tarea.
	 * @return Si hay alguna tarea para esa URL en curso.
	 */
	public static boolean cancelPotentialWork(String url, Object imageView) {
	    final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

	    if (bitmapWorkerTask != null) {
	        final String bitmapUrl = bitmapWorkerTask.url;
	        if (bitmapUrl != url) {
	            // Cancel previous task
	            bitmapWorkerTask.cancel(true);
	        } else {
	            // The same work is already in progress
	            return false;
	        }
	    }
	    // No task associated with the ImageView, or an existing task was cancelled
	    return true;
	}
	
	/**
	 * Obtiene la tarea que está encargándose de la descarga de cierta imagen.
	 * @param imageView ImageView asociado a la tarea.
	 * @return La tarea, si existe, o null.
	 */
	public static BitmapWorkerTask getBitmapWorkerTask(Object imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView instanceof ImageView ? ((ImageView) imageView).getDrawable() : ((TextView)imageView).getCompoundDrawables()[1];
			if (drawable != null && drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

	/**
	 * Obtiene el identificador de la imagen de portada por defecto de un local según su tipo.
	 * @param iconName Tipo del local.
	 * @return El identificador de la imagen.
	 */
	public int getDefaultVenuePortraitByType(String iconName) {
		return ImageManager.getPortraitResIdByType(this.mContext, iconName);
	}
	
	/**
	 * Obtiene el identificador de la imagen de portada por defecto de un local según su tipo.
	 * @param context Contexto.
	 * @param iconName Tipo de local.
	 * @return Identificador de la imagen.
	 */
	public static int getPortraitResIdByType(Context context, String iconName) {
		String drawableName = "";
		if (iconName.contains("bar.png") || iconName.equals("cerveceria.png")) drawableName += "cerveceria.png";
		else if (iconName.contains("bar_coktail.png") || iconName.equals("copas.png")) drawableName += "copas.png";
		else if (iconName.contains("billiard_2.png") || iconName.equals("billares.png")) drawableName += "billares.png";
		else if (iconName.contains("bowling.png") || iconName.equals("bolera.png")) drawableName += "bolera.png";
		else if (iconName.contains("coffee.png") || iconName.equals("cafeteria.png")) drawableName += "cafeteria.png";
		else if (iconName.contains("dancinghall.png") || iconName.equals("discoteca.png")) drawableName += "discoteca.png";
		else if (iconName.contains("fast_food.png") || iconName.equals("hamburgueseria.png")) drawableName += "hamburgueseria.png";
		else if (iconName.contains("gourmet_0star.png") || iconName.equals("restaurante.png")) drawableName += "restaurante.png";
		else if (iconName.contains("restaurante_buffet.png") || iconName.equals("restaurante.png")) drawableName += "restaurante.png";
		else if (iconName.contains("restaurante_chinese.png") || iconName.equals("restaurante.png")) drawableName += "restaurante.png";
		else if (iconName.contains("restaurante_italian.png") || iconName.equals("restaurante.png")) drawableName += "restaurante.png";
		else if (iconName.contains("restaurante_mexican.png") || iconName.equals("restaurante.png")) drawableName += "restaurante.png";
		else if (iconName.contains("hotel_0star.png") || iconName.equals("hotel.png")) drawableName += "hotel.png";
		else if (iconName.contains("icecream.png") || iconName.equals("heladeria.png")) drawableName += "heladeria.png";
		else if (iconName.contains("music_live.png") || iconName.equals("discoteca.png")) drawableName += "discoteca.png";
		else if (iconName.contains("pizzaria.png") || iconName.equals("pizzeria.png")) drawableName += "pizzeria.png";
		else if (iconName.contains("restaurant.png") || iconName.equals("restaurante.png")) drawableName += "restaurante.png";
		else if (iconName.contains("sandwich_2.png") || iconName.equals("hamburgueseria.png")) drawableName += "hamburgueseria.png";
		else if (iconName.contains("theater.png") || iconName.equals("teatro.png")) drawableName += "teatro.png";
		else if (iconName.contains("winebar.png") || iconName.equals("vinoteca.png")) drawableName += "vinoteca.png";
		else drawableName += "discoteca.png";
		String packageName = context.getResources().getResourcePackageName(R.drawable.green_big_bowling);
    	String typeName = context.getResources().getResourceTypeName(R.drawable.green_big_bowling);
		int drawableId = ApplicationFacade.getResId(drawableName, packageName, typeName, context);
		if (drawableId < 1) return R.drawable.cerveceria;
		return drawableId;
	}
	
	/**
	 * Obtiene el identificador de la imagen de portada por defecto de un local según su tipo.
	 * @param context Contexto.
	 * @param iconName Tipo de local.
	 * @return La Imagen.
	 */
	public static byte[] getPortraitImageByType(Context context, String iconName) {
		int drawableId = ImageManager.getPortraitResIdByType(context, iconName);
		return ImageManager.toByteArray(BitmapFactory.decodeResource(context.getResources(), drawableId));
	}

	/**
	 * Obtiene la foto de portada por defecto para todas las notificaciones.
	 * @return
	 */
	public int getDefaultNotificationPortrait() {
		return R.drawable.ic_launcher;
	}

	/**
	 * Obtiene el identificador del recurso para la imagen de afinidad del local con el usuario.
	 * @param affinityStr
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public int getAffinityImageResId(String affinityStr) {
		if (affinityStr.toLowerCase(Locale.US).equals(UseFeeling.GREEN.toString())) return R.drawable.green_circle;
		else if (affinityStr.toLowerCase(Locale.US).equals(UseFeeling.YELLOW.toString())) return R.drawable.yellow_circle;
		else return R.drawable.red_circle;
	}

	/**
	 * Obtiene una imagen a partir de una URL.
	 * @param url URL.
	 * @return
	 */
	public static byte[] getPictureFromUrl(String urlStr) {
		try {
			URL url = new URL(urlStr);
			return ImageManager.toByteArray(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Transforma un imagen según el tipo especificado en su URL.
	 * @param context Contexto.
	 * @param bitmap Imagen original.
	 * @param url URL.
	 * @return Imagen transformada.
	 */
	public static Bitmap transformBitmapByUrl(Context context, Bitmap bitmap, String url) {
		if (url.startsWith("userid")) {
			return ImageManager.createImageIcon(context, bitmap, url);
		}
		else if (url.startsWith("eventid") || url.startsWith("promoid") || url.startsWith("portrait")) {
			return ImageManager.createPortrait(context, bitmap);
		}
		else if (url.startsWith("placeid")) {
			return bitmap;
		}
		else return bitmap;
	}

	/**
	 * Crea la imagen de portada.
	 * @param context Contexto.
	 * @param bitmap Imagen de portada original.
	 * @return Imagen de portada transformada.
	 */
	private static Bitmap createPortrait(Context context, Bitmap bitmap) {
		return bitmap;
	}

	/**
	 * Obtiene el icono de un local a partir de su nombre.
	 * @param context Contexto.
	 * @param iconName Nombre.
	 * @return El icono del local.
	 */
	public static byte[] getMapIconByName(Activity context, String iconName) {
		String packageName = context.getResources().getResourcePackageName(R.drawable.green_big_bowling);
    	String typeName = context.getResources().getResourceTypeName(R.drawable.green_big_bowling);
		int drawableId = ApplicationFacade.getResId(iconName, packageName, typeName, context);
		if (drawableId < 1) return ImageManager.toByteArray(BitmapFactory.decodeResource(context.getResources(), R.drawable.green_bar));
		return ImageManager.toByteArray(BitmapFactory.decodeResource(context.getResources(), drawableId));
	}

	/**
	 * Compress a picture in a JPG file.
	 * @param picture Original picture.
	 * @return Compressed picture.
	 */
	public static byte[] compress(Bitmap picture) {
		int size = picture.getWidth() * picture.getHeight();
		ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		picture.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_LEVEL, out);
		return out.toByteArray();
	}
}
