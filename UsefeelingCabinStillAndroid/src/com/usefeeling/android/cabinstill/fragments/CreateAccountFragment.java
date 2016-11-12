package com.usefeeling.android.cabinstill.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;
import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.activities.SplashActivity;
import com.usefeeling.android.cabinstill.api.FacebookUser;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.DeviceFacade;
import com.usefeeling.android.cabinstill.facades.MessagesFacade;
import com.usefeeling.android.cabinstill.facades.SharedPreferencesFacade;
import com.usefeeling.android.cabinstill.helpers.DateTimeHelper;
import com.usefeeling.android.cabinstill.imageloader.ImageManager;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;
import com.usefeeling.android.cabinstill.tasks.CreateAccount;
import com.usefeeling.android.cabinstill.values.ActivitiesIds;
import com.usefeeling.android.cabinstill.values.States;

/**
 * Fragment de creacción de cuenta.
 * @author Moisés Vilar.
 *
 */
public class CreateAccountFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor>, OnTaskCompleted {

	private static final int REAUTH_ACTIVITY_CODE = 100;
	private static final Gson gson = new Gson();
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
	private final SimpleDateFormat facebookSdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
	
	private static boolean profileDraw = false;
	
	private ImageManager imageManager;
	private Uri selectedImageUri;
	private	String selectedImagePath;
	private Bitmap mProfilePicture = null;
	private String facebookid = "";
	private boolean pictureSet = false;
	
	private TextView tvProfilePicture;
	private EditText etName;
	private EditText etBirthdate;
	private EditText etGender;
	private EditText etEmail;
	private EditText etPhone;
	private EditText etPassword;
	private CheckBox cbShowPassword;
	private CheckBox cbTos;
	private Button bCreateAccount;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, final SessionState state, final Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	
	/**
	 * Listener de escucha cuando el usuario activa o desactiva el <font face="courier new">CheckBox cbShowPassword</font><br><br>
	 * Establece el tipo de entrada del <font face="courier new">EditText etPassword</font> a <font face="courier new">InputType.TYPE_TEXT_VARIATION_PASSWORD</font>,
	 * si el usuario activa el <font face="courier new">CheckBox</font>, o a <font face="courier new">InputType.TYPE_CLASS_TEXT</font> si el usuario
	 * desactiva el <font face="courier new">CheckBox</font>. 
	 */
	private final OnCheckedChangeListener onShowPasswordChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton view, boolean isChecked) {
			if (isChecked) etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
			else etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}
	};
	
	/**
	 * Listener de escucha cuando el usuario introduce su fecha de nacimiento.<br><br>
	 * Establece la fecha de nacimiento introducida en el <font face="courier new">EditText</font> correspondiente.
	 */
	private final OnDateSetListener onDateSetListener = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			if (etBirthdate == null) return;
			Calendar birthdate = Calendar.getInstance();
			birthdate.set(Calendar.YEAR, year);
			birthdate.set(Calendar.MONTH, month);
			birthdate.set(Calendar.DATE, day);
			etBirthdate.setText(sdf.format(birthdate.getTime()));
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.create_account, container, false);
		this.tvProfilePicture = (TextView)view.findViewById(R.id.tvInfoProfilePicture);
		this.tvProfilePicture.setOnClickListener(this.tvProfilePictureOnClickListener);
		this.etName = (EditText)view.findViewById(R.id.etName);
		this.etBirthdate = (EditText)view.findViewById(R.id.etBirthDate);
		this.etBirthdate.setOnClickListener(this.etBirthdateOnClickListener);
		this.etGender = (EditText)view.findViewById(R.id.etGender);
		this.etGender.setOnClickListener(this.etGenderOnClickListener);
		this.etEmail = (EditText)view.findViewById(R.id.etEmail);
		this.etEmail.setText(DeviceFacade.getGoogleAccount(this.getActivity()));
		this.etPhone = (EditText)view.findViewById(R.id.etMobilePhone);
		this.etPhone.setText(DeviceFacade.getWhatsappAccount(getActivity()));
		this.etPassword = (EditText)view.findViewById(R.id.etPassword);
		this.cbShowPassword = (CheckBox)view.findViewById(R.id.cbShowPassword);
		this.cbShowPassword.setOnCheckedChangeListener(this.onShowPasswordChangeListener);
		this.cbTos = (CheckBox)view.findViewById(R.id.cbTos);
		this.cbTos.setText(Html.fromHtml(this.getString(R.string.html_accept_tos)));
		this.cbTos.setMovementMethod(LinkMovementMethod.getInstance());
		this.bCreateAccount = (Button)view.findViewById(R.id.bCreateAccount);
		this.bCreateAccount.setOnClickListener(this.bCreateAccountOnClickListener);
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			makeMeRequest(session);
		}
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.imageManager = new ImageManager(this.getActivity());
		this.uiHelper = new UiLifecycleHelper(getActivity(), callback);
		this.uiHelper.onCreate(savedInstanceState);
	}
	
	/**
	 * Manejador del evento <font face = "courier new">OnClick</font> del <font face = "courier new">EditText etBirtDate</font>.<br><br>
	 * Muestra un <font face = "courier new">DatePickerDialog</font> donde el usuario pueda introducir su fecha de nacimiento.<br><br>
	 * Si el <font face = "courier new">EditText etBirthDate</font> ya contenía una fecha de nacimiento introducida anteriormente, la establece como fecha
	 * en el <font face = "courier new">DatePickerDialog</font> a mostrar.<br><br>
	 * Utiliza el listener <font face = "courier new">CreateAccountActivity.onDateSetListener</font> para recoger la fecha introducida por el usuario.
	 */
	private OnClickListener etBirthdateOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			etBirthDate_OnClick(v);
		}
	};
	private void etBirthDate_OnClick(View v) {
		if (v.getId() != R.id.etBirthDate) return;
		Calendar birthdate = Calendar.getInstance(Locale.getDefault());
		try {
			birthdate.setTime(this.sdf.parse(this.etBirthdate.getText().toString()));
		} catch (ParseException e) {
			birthdate = null;
		}
		DatePickerDialog dateDialog = new DatePickerDialog(this.getActivity(), this.onDateSetListener, DateTimeHelper.getYear(), DateTimeHelper.getMonth(), DateTimeHelper.getDay());
		dateDialog.setMessage(getString(R.string.set_birth_date));
		if (birthdate != null) {
			dateDialog.updateDate(birthdate.get(Calendar.YEAR), birthdate.get(Calendar.MONTH), birthdate.get(Calendar.DATE));
		}
		dateDialog.show();
	}
	
	/**
	 * Manejador del evento OnClick del EditText etGender.<br>
	 * Abre el diálogo de elección de género.
	 * @param v
	 */
	private OnClickListener etGenderOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			etGender_OnClick(v);
		}
		
	};
	public void etGender_OnClick(View v) {
		if (v.getId() != R.id.etGender) return;
		final String[] options = getResources().getStringArray(R.array.gender_values);
		AlertDialog.Builder builder = MessagesFacade.createSingleChoiceDialog(this.getActivity(), "", options, new DialogInterface.OnClickListener() {
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
	 * Manejador del evento <font face = "courier new">OnClick</font> del <font face = "courier new">ImageView qcbProfilePicture</font>.<br><br>
	 * Abre la galería para escoger una imagen que será la foto de perfil del usuario.
	 * @param v
	 */
	private OnClickListener tvProfilePictureOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			qcbProfilePicture_OnClick(v);
		}
		
	};
	@SuppressLint("InlinedApi")
	public void qcbProfilePicture_OnClick(View v) {
		if (v.getId() != R.id.qcbProfilePicture && v.getId() != R.id.tvInfoProfilePicture) return;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent, ""), ActivitiesIds.GALLERY);
		} else {
			Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		    intent.addCategory(Intent.CATEGORY_OPENABLE);
		    intent.setType("image/*");
		    startActivityForResult(intent, ActivitiesIds.GALLERY_KITKAT);
		}
	}
	
	/**
	 * Manejador del evento onClick del botón de creación de cuenta.
	 * @param v
	 */
	private OnClickListener bCreateAccountOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			bCreateAccount_OnClick(v);
		}
	};
	public void bCreateAccount_OnClick(View v) {
		if (v.getId() != R.id.bCreateAccount) return;
		//Validamos datos
		if (!this.checkData()) return;
		//Creamos la cuenta de usuario
		new CreateAccount(
				this.getActivity(), 
				this,
				this.etName.getText().toString().trim(),
				this.etBirthdate.getText().toString().trim(),
				this.etGender.getText().toString().trim(),
				this.etEmail.getText().toString().trim(),
				this.etPassword.getText().toString().trim(),
				this.mProfilePicture,
				this.facebookid.trim(),
				this.etPhone.getText().toString().trim()
		).execute();
	}
	
	/**
	 * Request the user data.
	 * 
	 * @param session
	 */
	private void makeMeRequest(final Session session) {
		final ProgressDialog progress = ProgressDialog.show(this.getActivity(), "", getString(R.string.wait_please));
		Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (session == Session.getActiveSession()) {
					if (user != null) {
						String json = user.getInnerJSONObject().toString();
						FacebookUser facebookUser = (FacebookUser)gson.fromJson(json, FacebookUser.class);
						String urlPicture = CreateAccountFragment.this.getString(R.string.facebook_profile_picture_url).replace("$USERID", facebookUser.getId());
						CreateAccountFragment.this.imageManager.loadBitmap(urlPicture, tvProfilePicture, R.drawable.ic_contact_picture, false);
						CreateAccountFragment.this.pictureSet = true;
						etName.setText(facebookUser.getName());
						try {
							etBirthdate.setText(sdf.format(facebookSdf.parse(facebookUser.getBirthday())));
						} catch (ParseException e) {
							System.out.println(e);
						}
						etGender.setText(facebookUser.getGender().equalsIgnoreCase("male") ? getString(R.string.male) : getString(R.string.female));
						etEmail.setText(facebookUser.getEmail());
						facebookid = facebookUser.getId();
					}
				}
				if (response.getError() != null) {
					MessagesFacade.toastLong(getActivity(), response.getError().getErrorMessage());
				}
				progress.dismiss();
			}
		});
		request.executeAsync();
	}
	
	/**
	 * Respond to session changes and call the makeMeRequest() method if the
	 * session's open
	 * 
	 * @param session
	 * @param state
	 * @param exception
	 */
	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
		if (session != null && session.isOpened()) {
			makeMeRequest(session);
		}
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REAUTH_ACTIVITY_CODE) {
			this.uiHelper.onActivityResult(requestCode, resultCode, data);
		}
		else if (requestCode == ActivitiesIds.GALLERY || requestCode == ActivitiesIds.GALLERY_KITKAT) {
			if (data == null){
				MessagesFacade.toastLong(CreateAccountFragment.this.getActivity(), this.getResources().getString(R.string.gallery_error).toString());
				return;
			}
			if ((selectedImageUri = data.getData()) == null){
	    		MessagesFacade.toastLong(CreateAccountFragment.this.getActivity(), this.getResources().getString(R.string.gallery_error).toString());
	    		return;
			}
			if (requestCode == ActivitiesIds.GALLERY_KITKAT) {
				final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
				this.getActivity().getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);
			}
			this.getActivity().getSupportLoaderManager().initLoader(0, null, this);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		this.uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		bundle.putString(States.NAME, this.etName.getText().toString());
		bundle.putString(States.BIRTH_DATE, this.etBirthdate.getText().toString());
		bundle.putString(States.GENDER, this.etGender.getText().toString());
		bundle.putString(States.EMAIL, this.etEmail.getText().toString());
		bundle.putString(States.MOBILE_PHONE, this.etPhone.getText().toString());
		bundle.putString(States.PASSWORD, this.etPassword.getText().toString());
		if (this.mProfilePicture != null) bundle.putParcelable(States.PICTURE, this.mProfilePicture);
		bundle.putBoolean(States.SHOW_PASSWORD, this.cbShowPassword.isChecked());
		bundle.putString(States.FACEBOOK_ID, this.facebookid);
		super.onSaveInstanceState(bundle);
		this.uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
		super.onPause();
		this.uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.uiHelper.onDestroy();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { MediaStore.Images.Media.DATA };
	    CursorLoader cursorLoader = new CursorLoader(this.getActivity(), this.selectedImageUri, projection, null, null, null);
	    return cursorLoader;
	}

	@SuppressLint("NewApi")
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (data != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
	        int columnIndex = data.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	        data.moveToFirst();
	        this.selectedImagePath = data.getString(columnIndex);
		} else if (data != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			String wholeID = DocumentsContract.getDocumentId(this.selectedImageUri);
			String id = wholeID.split(":")[1];
			String[] column = { MediaStore.Images.Media.DATA };
			String sel = MediaStore.Images.Media._ID + "=?";
			Cursor cursor = this.getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{ id }, null);
			int columnIndex = cursor.getColumnIndex(column[0]);
			if (cursor.moveToFirst()) this.selectedImagePath = cursor.getString(columnIndex);
			cursor.close();
	    } else if (data == null) {
	        this.selectedImagePath = this.selectedImageUri.getPath();
	    }
		if (this.selectedImagePath == null) {
			MessagesFacade.toastLong(CreateAccountFragment.this.getActivity(), this.getResources().getString(R.string.gallery_error).toString());
			return;
		}
		if (BitmapFactory.decodeFile(selectedImagePath) == null){
    		MessagesFacade.toastLong(CreateAccountFragment.this.getActivity(), this.getResources().getString(R.string.gallery_error).toString());
    		return;
    	}
    	this.mProfilePicture = BitmapFactory.decodeFile(selectedImagePath);
    	if (mProfilePicture != null) this.setProfilePictureInImageView();
    	CreateAccountFragment.this.pictureSet = true;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {}
	
	/**
	 * Establece la imagen de perfil en el ImageView.
	 */
	private void setProfilePictureInImageView() {
		ViewTreeObserver vto = this.tvProfilePicture.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
		    public boolean onPreDraw() {
		        int finalHeight = tvProfilePicture.getMeasuredHeight();
		        int finalWidth = tvProfilePicture.getMeasuredWidth();
		        if (!profileDraw) {
		        	Bitmap aux = ImageManager.decodeSampledBitmap(getResources(), mProfilePicture, finalWidth, finalHeight);
		        	tvProfilePicture.setCompoundDrawablesWithIntrinsicBounds(null, new BitmapDrawable(getResources(), aux), null, null);
		        }
		        profileDraw = true;
		        return true;
		    }
		});
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
		if (this.etBirthdate.getText().toString().trim().equals("")) {
			this.etBirthdate.setError(this.getResources().getString(R.string.error_birth_date));
			return false;
		}
		try {
			sdf.parse(this.etBirthdate.getText().toString());
		} catch (ParseException e) {
			this.etBirthdate.setError(this.getResources().getString(R.string.error_birth_date));
			return false;
		}
		if (!this.checkAge()) {
			this.etBirthdate.setError(this.getResources().getString(R.string.error_age));
			return false;
		}
		if ((this.etEmail.getText().toString().trim()).equals("")){
			this.etEmail.setError(this.getResources().getString(R.string.error_email));
			return false;
		}
		if ((this.etPassword.getText().toString().trim()).equals("") || this.etPassword.getText().toString().length() < 6){
			this.etPassword.setError(this.getResources().getString(R.string.error_password));
			return false;
		}
		if (!this.cbTos.isChecked()) {
			this.cbTos.setError(this.getResources().getString(R.string.error_tos));
			return false;
		}
		if (this.mProfilePicture == null && this.pictureSet) this.mProfilePicture = ((BitmapDrawable)(this.tvProfilePicture.getCompoundDrawables()[1])).getBitmap();
		return true;
	}
	
	/**
	 * Comprueba que el usuario sea mayor de edad.
	 * @return <b>true</b> si el usuario tiene más de 18 años o <b>false</b> en caso contrario.
	 */
	private boolean checkAge() {
		if (this.etBirthdate.getText().toString().trim().equals("")) return false;
		Calendar birthdate = Calendar.getInstance();
		try {
			birthdate.setTime(sdf.parse(this.etBirthdate.getText().toString()));
		} catch(ParseException e) {
			return false;
		}
		birthdate.add(Calendar.YEAR, 18);
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		if (birthdate.compareTo(now) > 0) return false;
		return true;
	}

	@Override
	public void onTaskCompleted(Object o) {
		//Obtenemos resultado
		Result result = (Result)o;
		//Si ha ocurrido algún error, mostramos por pantalla y salimos
		if (result == null) MessagesFacade.toastLong(this.getActivity(), getString(R.string.unknown_error));
		else if (result.getCode() != ResultCodes.Ok) MessagesFacade.toastLong(this.getActivity(), result.getMessage());
		//Si no ha ocurrido ningún error, almacenamos los datos del usuario en las preferencias compartidas y lanzamos la pantalla de mapa principal
		else {
			SharedPreferencesFacade prefs = new SharedPreferencesFacade(this.getActivity());
			prefs.setUserId(Long.parseLong(result.getPayload()));
			prefs.setFacebookId(this.facebookid);
			prefs.setPassword(this.etPassword.getText().toString());
			prefs.setSessionStarted(true);
			ApplicationFacade.registerGcm(this.getActivity());
			ApplicationFacade.startCheckinService(this.getActivity());
			ApplicationFacade.startActivity(this.getActivity(), SplashActivity.class);
			this.getActivity().finish();
		}
	}
}
