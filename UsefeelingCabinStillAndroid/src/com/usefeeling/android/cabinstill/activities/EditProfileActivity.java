package com.usefeeling.android.cabinstill.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.api.Account;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.DataFacade;
import com.usefeeling.android.cabinstill.facades.MessagesFacade;
import com.usefeeling.android.cabinstill.helpers.DateTimeHelper;
import com.usefeeling.android.cabinstill.imageloader.ImageManager;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;
import com.usefeeling.android.cabinstill.tasks.ModifyAccount;
import com.usefeeling.android.cabinstill.values.ActivitiesIds;
import com.usefeeling.android.cabinstill.values.States;

/**
 * Activity de modificación de perfil del usuario.
 * @author Moisés Vilar.
 *
 */
public class EditProfileActivity extends SherlockActivity implements OnTaskCompleted {
	
	private ImageView qcbPicture;
	private EditText etName;
	private EditText etBirthDate;
	private EditText etGender;
	private EditText etEmail;
	private EditText etMobilePhone;
	private Bitmap mProfilePicture = null;
	private boolean widgetsAreSet = false;
	private ImageManager imageManager;
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
	private DataFacade dataFacade;
	private static boolean profileDraw = false;
	
	/**
	 * Listener de escucha cuando el usuario introduce su fecha de nacimiento.<br><br>
	 * Establece la fecha de nacimiento introducida en el <font face="courier new">EditText</font> correspondiente.
	 */
	private final OnDateSetListener onDateSetListener = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			if (etBirthDate == null) return;
			Calendar birthdate = Calendar.getInstance();
			birthdate.set(Calendar.YEAR, year);
			birthdate.set(Calendar.MONTH, month);
			birthdate.set(Calendar.DATE, day);
			etBirthDate.setText(sdf.format(birthdate.getTime()));
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.edit_profile);
		this.getSupportActionBar().setIcon(R.drawable.usefeeling_icon_transparent_background);
		this.getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.button_bar_gradient));
		this.getSupportActionBar().setDisplayShowTitleEnabled(false);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		this.imageManager = new ImageManager(this);
		this.dataFacade = new DataFacade(this);
		this.setWidgets();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.default_actionbar_menu, menu);
        return true;
    }
	
	@SuppressLint("InlinedApi")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		int homeId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.id.home : R.id.abs__home; 
        if (item.getItemId() == homeId) {
        	ApplicationFacade.goHome(this, MapActivity.class);
        }
        return true;
    }
	
	/**
	 * Establece los elementos de la interfaz de usuario.
	 */
	private void setWidgets() {
		if (this.widgetsAreSet) return;
		this.qcbPicture = (ImageView)this.findViewById(R.id.qcbProfilePicture);
		this.etName = (EditText)this.findViewById(R.id.etName);
		this.etBirthDate = (EditText)this.findViewById(R.id.etBirthDate);
		this.etGender = (EditText)this.findViewById(R.id.etGender);
		this.etEmail = (EditText)this.findViewById(R.id.etEmail);
		this.etMobilePhone = (EditText)this.findViewById(R.id.etMobilePhone);
		Account account = dataFacade.getAccount();
		this.imageManager.loadBitmap(account.getImageUrl(), qcbPicture, R.drawable.ic_contact_picture, true); 
		this.etName.setText(account.getName());
		this.etBirthDate.setText(this.sdf.format(new java.util.Date(account.getBirthDate())));
		this.etGender.setText(account.getGender());
		this.etMobilePhone.setText(account.getPhone());
		this.etEmail.setText(account.getEmail());
		this.widgetsAreSet = true;
	}

	@Override
	public void onRestoreInstanceState(Bundle inState) {
		super.onRestoreInstanceState(inState);
		this.setWidgets();
		this.etName.setText(inState.getString(States.NAME));
		this.etBirthDate.setText(inState.getString(States.BIRTH_DATE));
		this.etGender.setText(inState.getString(States.GENDER));
		this.etEmail.setText(inState.getString(States.EMAIL));
		this.etMobilePhone.setText(inState.getString(States.MOBILE_PHONE));
		this.mProfilePicture = (Bitmap)inState.getParcelable(States.PICTURE);
		if (this.mProfilePicture != null) this.setProfilePictureInImageView();
		dataFacade.setAccount((Account) inState.getSerializable(States.ACCOUNT));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(States.NAME, this.etName.getText().toString());
		outState.putString(States.BIRTH_DATE, this.etBirthDate.getText().toString());
		outState.putString(States.GENDER, this.etGender.getText().toString());
		outState.putString(States.EMAIL, this.etEmail.getText().toString());
		outState.putString(States.MOBILE_PHONE, this.etMobilePhone.getText().toString());
		if (this.mProfilePicture != null) outState.putParcelable(States.PICTURE, this.mProfilePicture);
		outState.putSerializable(States.ACCOUNT, dataFacade.getAccount());
		super.onSaveInstanceState(outState);
	}
	
	/**
	 * Manejador del evento <font face = "courier new">OnClick</font> del <font face = "courier new">EditText etBirtDate</font>.<br><br>
	 * Muestra un <font face = "courier new">DatePickerDialog</font> donde el usuario pueda introducir su fecha de nacimiento.<br><br>
	 * Si el <font face = "courier new">EditText etBirthDate</font> ya contenía una fecha de nacimiento introducida anteriormente, la establece como fecha
	 * en el <font face = "courier new">DatePickerDialog</font> a mostrar.<br><br>
	 * Utiliza el listener <font face = "courier new">CreateAccountActivity.onDateSetListener</font> para recoger la fecha introducida por el usuario.
	 * @param v
	 */
	public void etBirthDate_OnClick(View v) {
		if (v.getId() != R.id.etBirthDate) return;
		Calendar birthdate = Calendar.getInstance(Locale.getDefault());
		try {
			birthdate.setTime(this.sdf.parse(this.etBirthDate.getText().toString()));
		} catch (ParseException e) {
			birthdate = null;
		}
		DatePickerDialog dateDialog = new DatePickerDialog(this, this.onDateSetListener, DateTimeHelper.getYear(), DateTimeHelper.getMonth(), DateTimeHelper.getDay());
		dateDialog.setMessage(getString(R.string.set_birth_date));
		if (birthdate != null) {
			dateDialog.updateDate(birthdate.get(Calendar.YEAR), birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DATE));
		}
		dateDialog.show();
	}
	
	/**
	 * Manejador del evento <font face = "courier new">OnClick</font> del <font face = "courier new">QuickContactBadge qcbProfilePicture</font>.<br><br>
	 * Abre la galería para escoger una imagen que será la foto de perfil del usuario.
	 * @param v
	 */
	public void qcbProfilePicture_OnClick(View v) {
		if (v.getId() != R.id.qcbProfilePicture) return;
		Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Escoga una imagen"), ActivitiesIds.GALLERY);
	}
	
	/**
	 * Manejador del evento OnClick del EditText etGender.<br>
	 * Abre el diálogo de elección de género.
	 * @param v
	 */
	public void etGender_OnClick(View v) {
		if (v.getId() != R.id.etGender) return;
		final String[] options = getResources().getStringArray(R.array.gender_values);
		AlertDialog.Builder builder = MessagesFacade.createSingleChoiceDialog(this, "", options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which >= 0 && which < options.length) {
					etGender.setText(options[which]);
				}
				dialog.dismiss();
			}
		});
		builder.show();
	}
	
	/**
	 * Recoge los resultados de la galería, con los datos necesarios para recoger la imagen seleccionada por el usuario para utilizar como su foto de perfil
	 * en UseFeeling.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		try {
			if (requestCode == ActivitiesIds.GALLERY) {
				Uri selectedImageUri;
				String selectedImagePath;
				if (data == null){
					MessagesFacade.toastLong(EditProfileActivity.this, this.getResources().getString(R.string.gallery_error).toString());
					return;
				}
				if ((selectedImageUri = data.getData()) == null){
		    		MessagesFacade.toastLong(EditProfileActivity.this, this.getResources().getString(R.string.gallery_error).toString());
		    		return;
				}
		    	if ((selectedImagePath = getPath(selectedImageUri)) == null) {
		    		MessagesFacade.toastLong(EditProfileActivity.this, this.getResources().getString(R.string.gallery_error).toString());
		    		return;
		    	}
		    	if (BitmapFactory.decodeFile(selectedImagePath) == null){
		    		MessagesFacade.toastLong(EditProfileActivity.this, this.getResources().getString(R.string.gallery_error).toString());
		    		return;
		    	}
		    	this.mProfilePicture = BitmapFactory.decodeFile(selectedImagePath);
		    	if (mProfilePicture != null) this.setProfilePictureInImageView();
			}
		} catch (Exception e) {
			MessagesFacade.toastLong(EditProfileActivity.this, e.getMessage());
			return;
		} catch (OutOfMemoryError e) {
			MessagesFacade.toastLong(EditProfileActivity.this, e.getMessage());
		}
	}
	
	/**
	 * Establece la imagen de perfil en el ImageView.
	 */
	private void setProfilePictureInImageView() {
		ViewTreeObserver vto = this.qcbPicture.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
		    public boolean onPreDraw() {
		        int finalHeight = qcbPicture.getMeasuredHeight();
		        int finalWidth = qcbPicture.getMeasuredWidth();
		        if (!profileDraw) {
		        	Bitmap aux = ImageManager.decodeSampledBitmap(getResources(), mProfilePicture, finalWidth, finalHeight);
		        	qcbPicture.setImageBitmap(aux);
		        }
		        profileDraw = true;
		        return true;
		    }
		});
	}

	/**
	 * Obtiene la ruta de la imagen a través de la URI ofrecida por la activity de la galería.
	 * @param uri URI de la imagen.
	 * @return Ruta a la imagen.
	 */
	private String getPath(Uri uri){
		try {
			String[] projection = { MediaStore.Images.Media.DATA };
		    @SuppressWarnings("deprecation")
			Cursor cursor = managedQuery(uri, projection, null, null, null);
		    if (cursor == null) return null;
		    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    cursor.moveToFirst();
		    return cursor.getString(column_index);
		} catch (Exception e){
			return null;
		}
	}
	
	/**
	 * Comprueba que los datos introducidos por el usuario son correctos.<br><br>
	 * Realiza algunas comprobaciones básicas:
	 * <li>Que el usuario ha introducido texto en su nombre</li>
	 * <li>Que el usuario ha introducido text en su fecha de nacimiento</li>
	 * <li>Que la fecha de nacimiento introducida sigue el formato dd-MM-yyyy</li>
	 * <li>Que el usuario es mayor de edad, según la fecha de nacimiento que ha introducido.</li>
	 * <li>Que el usuario ha introducido una contraseña de al menos 6 caracteres de longitud.</li>
	 * <li>Que el usuario ha introducido texto en su dirección de correo electrónico.</li>
	 * @return <b>true</b> si los datos superan las comprobaciones básicas o <b>false</b> en caso contrario.
	 */
	private boolean checkData() {
		if (this.etName.getText().toString().trim().equals("")) {
			this.etName.setError(this.getResources().getString(R.string.error_name));
			return false;
		}
		if (this.etBirthDate.getText().toString().trim().equals("")) {
			this.etBirthDate.setError(this.getResources().getString(R.string.error_birth_date));
			return false;
		}
		try {
			sdf.parse(this.etBirthDate.getText().toString());
		} catch (ParseException e) {
			this.etBirthDate.setError(this.getResources().getString(R.string.error_birth_date));
			return false;
		}
		if (!this.checkAge()) {
			this.etBirthDate.setError(this.getResources().getString(R.string.error_age));
			return false;
		}
		if ((this.etEmail.getText().toString().trim()).equals("")){
			this.etEmail.setError(this.getResources().getString(R.string.error_email));
			return false;
		}
		return true;
	}
	
	/**
	 * Comprueba que el usuario sea mayor de edad.
	 * @return <b>true</b> si el usuario tiene más de 18 años o <b>false</b> en caso contrario.
	 */
	private boolean checkAge() {
		if (this.etBirthDate.getText().toString().trim().equals("")) return false;
		Calendar birthdate = Calendar.getInstance();
		try {
			birthdate.setTime(sdf.parse(this.etBirthDate.getText().toString()));
		} catch(ParseException e) {
			return false;
		}
		birthdate.add(Calendar.YEAR, 18);
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		if (birthdate.compareTo(now) > 0) return false;
		return true;
	}
	
	/**
	 * Manejador del evento OnClick del botón de modificar perfil.
	 * @param v
	 */
	public void bEditProfile_OnClick(View v) {
		if (v.getId() != R.id.bEditProfile) return;
		//Validamos datos
		if (!this.checkData()) return;
		//Creamos la cuenta de usuario
		new ModifyAccount(
				this, 
				this,
				this.etName.getText().toString().trim(),
				this.etBirthDate.getText().toString().trim(),
				this.etGender.getText().toString().trim(),
				this.etEmail.getText().toString().trim(),
				this.mProfilePicture,
				this.etMobilePhone.getText().toString().trim()
		).execute();
	}
	
	@Override
	public void onTaskCompleted(Object o) {
		//Obtenemos resultado
		Result result = (Result)o;
		//Mostramos resultado por pantalla
		if (result.getCode() != ResultCodes.Ok) {
			MessagesFacade.toastLong(this, result.getMessage());
			return;
		}
		Calendar birthdate = Calendar.getInstance();
		try {
			birthdate.setTime(sdf.parse(this.etBirthDate.getText().toString()));
		} catch(ParseException e) {
			MessagesFacade.toastLong(this, e.getMessage());
			return;
		}
		dataFacade.getAccount().setBirthDate(birthdate.getTime().getTime());
		dataFacade.getAccount().setName(this.etName.getText().toString());
		dataFacade.getAccount().setGender(this.etGender.getText().toString());
		dataFacade.getAccount().setEmail(this.etEmail.getText().toString());
		dataFacade.getAccount().setPhone(this.etMobilePhone.getText().toString());
		this.finish();
	}
}
