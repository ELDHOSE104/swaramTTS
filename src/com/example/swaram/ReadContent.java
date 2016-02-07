package com.example.swaram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ReadContent extends ActionBarActivity implements TextWatcher {

	SharedPreferences preferences;
	int soundId = 0;
	EditText editText;
	Button buttonStartReading;
	InputMethodManager mgr;
	MediaPlayer mediaPlayer;
	ProgressDialog dialog;
	AssetManager assetManager;
	static String dirPath;
	String readData = "";
	FileReader fileReader;
	File consonentFile, vowelFile, signsFile, signsTwoFile;
	BufferedReader reader;
	static int charLength = 0;
	static int i = 0;
	Bundle savedInstanceState;
	ArrayList<String> wordList = new ArrayList<String>();
	AlertDialog.Builder builder;
	AlertDialog dialog2;

	static HashMap<String, String> consonentMap = new HashMap<String, String>();
	static HashMap<String, String> vowelMap = new HashMap<String, String>();
	static HashMap<String, String> signsMap = new HashMap<String, String>();
	static HashMap<String, String> signsTwoMap = new HashMap<String, String>();
	static HashMap<String, String> wordsMap = new HashMap<String, String>();
	static HashMap<String, Integer> learnedWordsMap = new HashMap<String, Integer>();
	static boolean prediction = true;
	private SoundPool soundPool;
	boolean loaded = false;
	float actVolume, maxVolume, volume;
	AudioManager audioManager;
	static boolean activityDeactivated = false;
	static final String CONSONENTS = "consonents.txt";
	static final String VOWELS = "vowels.txt";
	static final String SIGNS = "signs.txt";
	static final String SIGNSTWO = "signstwo.txt";
	String rootPath = "";
	AlertDialog.Builder dialogFileName;
	String[] texts;
	static ArrayList<String> learnedList = new ArrayList<String>();
	String currentWord, predictedWord = "";
	File trainFile;
	ArrayList<String> adapterList = new ArrayList<String>();
	AlertDialog.Builder saveDialogBuilder;
	AlertDialog saveDialog;

	public static HashMap<String, String> getWordsMap() {
		return wordsMap;
	}

	public static HashMap<String, Integer> getLearnedWordsMap() {
		return learnedWordsMap;
	}

	public static void setLearnedWordsMap(
			HashMap<String, Integer> learnedWordsMap) {
		ReadContent.learnedWordsMap = learnedWordsMap;
	}

	public static void setWordsMap(HashMap<String, String> wordsMap) {

		ReadContent.wordsMap = wordsMap;
		ReadContent.learnedList = new ArrayList<String>();

		ReadContent.learnedList.addAll(ReadContent.wordsMap.keySet());

	}

	public static HashMap<String, String> getConsonentMap() {
		return consonentMap;
	}

	public static void setConsonentMap(HashMap<String, String> consonentMap) {
		ReadContent.consonentMap = consonentMap;
	}

	public static HashMap<String, String> getVowelMap() {
		return vowelMap;
	}

	public static void setVowelMap(HashMap<String, String> vowelMap) {
		ReadContent.vowelMap = vowelMap;
	}

	public static HashMap<String, String> getSignsMap() {
		return signsMap;
	}

	public static void setSignsMap(HashMap<String, String> signsMap) {
		ReadContent.signsMap = signsMap;
	}

	public static HashMap<String, String> getSignsTwoMap() {
		return signsTwoMap;
	}

	public static void setSignsTwoMap(HashMap<String, String> signsTwoMap) {
		ReadContent.signsTwoMap = signsTwoMap;
	}

	public String getPathFromURI(Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, proj, null, null, null);
		if (cursor == null) {
			return "";
		} else {
			int column_index = cursor
					.getColumnIndex(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.read_content, menu);

		preferences = getSharedPreferences(Globals.PREFERENCE_NAME,
				MODE_PRIVATE);
		prediction = preferences.getBoolean(Globals.PREDICTION, false);

		MenuItem item = menu.findItem(R.id.prediction);

		if (prediction) {

			item.setTitle("Trun Off Prediction");
		} else {

			item.setTitle("Trun On Prediction");
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		activityDeactivated = true;
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		activityDeactivated = true;

		saveDialog.show();

		// super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// action with ID action_refresh was selected
		case R.id.select_keypad:
			if (mgr != null) {
				mgr.showInputMethodPicker();
			} else {
				mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				mgr.showInputMethodPicker();
			}
			break;

		case R.id.download_voice:
			AssetExtraction assetExtraction = new AssetExtraction(
					ReadContent.this);
			assetExtraction.execute();
			break;
		case R.id.save_file:

			dialogFileName = new AlertDialog.Builder(ReadContent.this);
			dialogFileName.setTitle("Enter File Name");
			final EditText editText1 = new EditText(ReadContent.this);

			dialogFileName.setView(editText1);
			dialogFileName.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String fileName = editText1.getText().toString();
							String path = rootPath + "/MR/Files";

							boolean saved = Globals.saveFile(path, fileName,
									editText.getText().toString());
							Log.d("[file_saved]", saved + "");

						}
					});
			dialogFileName.show();

			break;

		case R.id.open_file:
			showFileChooser();
			break;
		case R.id.clear:
			editText.setText("");
			break;

		case R.id.prediction:
			preferences = getSharedPreferences(Globals.PREFERENCE_NAME,
					MODE_PRIVATE);
			prediction = preferences.getBoolean(Globals.PREDICTION, false);
			SharedPreferences.Editor editor = preferences.edit();
			if (prediction) {
				editor.putBoolean(Globals.PREDICTION, false);
				item.setTitle("Turn On Prediction");
			} else {
				editor.putBoolean(Globals.PREDICTION, true);
				item.setTitle("Turn Off Prediction");
			}
			editor.commit();
		default:
			break;
		}

		return true;
	}

	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult(
					Intent.createChooser(intent, "Select a File"),
					Globals.OPEN_FILE_REQUEST_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(this, "Please install a File Manager.",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestcode, int resultcode,
			Intent intent) {
		// TODO Auto-generated method stub
		Uri uri = intent.getData();
		String selectedFilePath = getPathFromURI(uri);
		Log.d("[file_selected_org]", selectedFilePath);
		String text = Globals.readFromFile(selectedFilePath);
		editText.setText(text);
		super.onActivityResult(requestcode, resultcode, intent);
	}

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_content);

		builder = new AlertDialog.Builder(ReadContent.this);

		this.savedInstanceState = savedInstanceState;

		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

		actVolume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);

		maxVolume = (float) audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		dirPath = rootPath + "/MR/Voices";
		trainFile = new File(dirPath + "/words/"
				+ AssetExtraction.TRAINED_WORDS_FILENAME);

		AssetExtraction assetsExtracter = new AssetExtraction(ReadContent.this);
		assetsExtracter.execute();

		saveDialogBuilder = new AlertDialog.Builder(ReadContent.this);
		saveDialogBuilder.setTitle("Save all typed text to Dictionary?");
		saveDialogBuilder.setPositiveButton("Save",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						ObjectOutputStream objectOutputStream;
						try {
							objectOutputStream = new ObjectOutputStream(
									new FileOutputStream(trainFile));
							objectOutputStream.writeObject(learnedWordsMap);
							objectOutputStream.close();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							Toast.makeText(ReadContent.this,
									"Can't access file", Toast.LENGTH_LONG)
									.show();
							e.printStackTrace();
						} catch (IOException e) {
							Toast.makeText(ReadContent.this,
									"Can't access file", Toast.LENGTH_LONG)
									.show();
							e.printStackTrace();
						}

						saveDialog.dismiss();
						finish();
					}
				});
		saveDialogBuilder.setNegativeButton("Don't Save",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						saveDialog.dismiss();
						finish();

					}
				});

		saveDialog = saveDialogBuilder.create();

		File file = new File(dirPath);
		if (!file.exists()) {
			file.mkdirs();
		}

		Log.d("[path]:", dirPath);

		mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		String data = getIntent().getExtras().getString(Globals.CONTENT);

		editText = (EditText) findViewById(R.id.editTextReadContent);

		editText.addTextChangedListener(this);

		editText.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if (keyCode == KeyEvent.KEYCODE_SPACE) {
					String text = editText.getText().toString();
					int cursorPosition = text.length();
					int lastSpacePosition = text.lastIndexOf(" ") - 1;
					String lastWordTyped = text.substring(lastSpacePosition,
							cursorPosition);
					Log.d("[lastword_typed]:", lastWordTyped);
				}
				return false;
			}
		});

		buttonStartReading = (Button) findViewById(R.id.buttonStartReading);
		if (data != null) {
			data = StringUtils.removeInvalidCharacters(data);
		}
		Typeface font = Typeface.createFromAsset(getAssets(),
				"AnjaliOldLipi-0.730.ttf");
		editText.setTypeface(font);

		readData = data;

		editText.setText(data);

		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {

			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {

				loaded = true;

			}

		});

		buttonStartReading.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				preferences = getSharedPreferences(Globals.PREFERENCE_NAME,
						MODE_PRIVATE);
				boolean voiceDownloaded = preferences.getBoolean(
						Globals.VOICE_DOWNLOADED, false);

				Log.d("Readcontent_consonentMap", consonentMap.size() + "");

				readData = editText.getText().toString();

				charLength = editText.getText().toString().toCharArray().length;

				String text = readData;

				text = text.replace(".", " ");
				text = text.replace(",", " ");
				text = text.replace("  ", " ");

				texts = text.split(" ");

				for (String str : texts) {
					if (!wordList.contains(str)) {
						wordList.add(str);
					}
				}

				boolean flag = true;
				for (String s : wordList) {
					if (!wordsMap.containsKey(s)) {
						flag = false;
						break;
					}
				}

				i = 0;
				if (flag) {
					new ReadWords().execute(i);
				} else {
					new Reader().execute(i);
				}
			}

		});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// onSaveInstanceState(savedInstanceState);
		activityDeactivated = false;
		super.onResume();
	}

	private class Reader extends AsyncTask<Integer, Integer, String> {

		@Override
		protected String doInBackground(Integer... params) {

			String fileFirst = "";
			String fileSecond = "";
			String fileThird = "";
			String fileFour = "";
			int j = params[0];
			char c = 0, c1 = 0, c2 = 0, c4 = 0, c5 = 0, c6 = 0;
			if (readData.length() > j) {
				c = readData.charAt(j);
			}
			if (consonentMap.containsKey(c + "")) {
				fileFirst = consonentMap.get(c + "");

				if (readData.length() > j + 1) {
					c1 = readData.charAt(j + 1);
				}

				if (signsMap.containsKey(c1 + "")) {
					fileSecond = signsMap.get(c1 + "");
					i = i + 1;
					if (readData.length() > j + 2) {
						c2 = readData.charAt(j + 2);
					}
					if (signsMap.containsKey(c2 + "")) {
						fileThird = signsMap.get(c2 + "");
						i = i + 1;
					}
				} else if (signsTwoMap.containsKey(c1 + "")) {

					fileSecond = signsTwoMap.get(c1 + "");
					i = i + 1;

					if (readData.length() > j + 2) {
						c4 = readData.charAt(j + 2);
					}
					if (consonentMap.containsKey(c4 + "")) {
						fileThird = consonentMap.get(c4 + "");
						i = i + 1;

						if (readData.length() > j + 3) {
							c5 = readData.charAt(j + 3);
						}
						if (signsMap.containsKey(c5 + "")) {
							fileFour = signsMap.get(c5 + "");
							i = i + 1;
						}

					}

				}
			} else if (vowelMap.containsKey(c + "")) {
				fileFirst = vowelMap.get(c + "");
			}
			String filetoPlay = fileFirst + fileSecond + fileThird + fileFour;
			return filetoPlay.trim();
		}

		@Override
		protected void onPostExecute(String fileToPlay) {

			try {

				Log.e("[playing file]:", fileToPlay);

				File playingFile = new File(dirPath + "/" + fileToPlay + ".mp3");
				if (!playingFile.exists()) {
					playingFile = new File(dirPath + "/" + fileToPlay + ".wav");
				}
				// Log.e("[playing file]:", fileToPlay);
				if (fileToPlay == null) {
					Thread.currentThread();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.e("[playing]:", "null");
					i = i + 1;
					// Log.e("[i]:", i + "");
					if (i < charLength) {
						// Log.e("[charlength]:", "i is Lesser");

						new Reader().execute(i);
					}
				} else if (playingFile.getName().contains("space")
						|| playingFile.getName().isEmpty()) {

					Thread.currentThread();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.e("[playing]:", "[" + playingFile.getName() + "]_space");
					i = i + 1;
					// Log.e("[i]:", i + "");
					if (i < charLength) {
						// Log.e("[charlength]:", "i is Lesser");
						new Reader().execute(i);
					}
				} else if (!playingFile.exists()) {
					i = i + 1;
					Log.e("[playing]:", "[" + playingFile.getName()
							+ "]_file not found");
					if (i < charLength) {
						// Log.e("[charlength]:", "i is Lesser");
						new Reader().execute(i);
					}
				} else {

					try {
						Log.e("[playing]:", "[" + playingFile.getName()
								+ "]_done");
						soundId = soundPool.load(playingFile.getAbsolutePath(),
								1);
					} catch (Exception e) {
						Log.e("[Exception]:", "Exception");
					}

					soundPool
							.setOnLoadCompleteListener(new OnLoadCompleteListener() {

								@Override
								public void onLoadComplete(SoundPool soundPool,
										int sampleId, int status) {
									soundPool.play(soundId, maxVolume,
											maxVolume, 1, 0, 1f);
									Thread.currentThread();
									try {
										Thread.sleep(200);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									i = i + 1;
									// Log.e("[i]:", i + "");
									if (i < charLength) {
										// Log.e("[charlength]:",
										// "i is Lesser");
										if (!activityDeactivated) {
											new Reader().execute(i);
										}
									}
								}
							});
				}

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				Log.e("[exception]:", "IllegalArgumentException");
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				Log.e("[exception]:", "SecurityException");
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				Log.e("[exception]:", "IllegalStateException");
				e.printStackTrace();
			}

			super.onPostExecute(fileToPlay);
		}
	}

	public class ReadWords extends AsyncTask<Integer, Void, String> {

		@Override
		protected String doInBackground(Integer... params) {
			return texts[params[0]];

		}

		@Override
		protected void onPostExecute(String result) {

			String fileToPlay = wordsMap.get(result);
			File playingFile = new File(dirPath + "/words/" + fileToPlay
					+ ".wav");
			Log.d("[playing]:", playingFile.getAbsolutePath());
			MediaPlayer mp = new MediaPlayer();
			mp.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.release();
					i = i + 1;
					if (i < texts.length) {
						if (!activityDeactivated) {
							new ReadWords().execute(i);
						} else {
							mp.release();
						}
					}
				}
			});
			mp.reset();
			try {
				mp.setDataSource(playingFile.getAbsolutePath());
				mp.prepare();
				mp.start();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			super.onPostExecute(result);
		}

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

		// Log.d("[current_char]:", s + "");
		preferences = getSharedPreferences(Globals.PREFERENCE_NAME,
				MODE_PRIVATE);

		boolean prediction = preferences.getBoolean(Globals.PREDICTION, false);

		String textTyped = s.toString();

		int index = textTyped.length() - 1;
		if (index >= 0) {

			char currentChar = textTyped.charAt(index);

			if (currentChar == ' ') {

				String[] words = textTyped.split(" ");

				currentWord = words[words.length - 1];

				if (learnedWordsMap.containsKey(currentWord)) {

					int value = learnedWordsMap.get(currentWord) + 1;

					learnedWordsMap.put(currentWord, value);
				} else {
					learnedWordsMap.put(currentWord, 1);
				}

			} else if (prediction) {

				String[] words = textTyped.split(" ");

				currentWord = words[words.length - 1];

				HashMap<String, Integer> learnedWordsMap2 = new HashMap<String, Integer>();

				for (String word2 : learnedWordsMap.keySet()) {

					if (word2.startsWith(currentWord)) {
						learnedWordsMap2.put(word2, learnedWordsMap.get(word2));
					}
				}

				if (learnedWordsMap2 != null) {
					adapterList = new ArrayList<String>();
					adapterList = Globals.sortHashMap(learnedWordsMap2);
				}

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						ReadContent.this, android.R.layout.simple_list_item_1,
						adapterList);
				builder.setAdapter(adapter,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								StringBuilder builder = new StringBuilder(
										editText.getText().toString());

								int firstIndex = builder
										.lastIndexOf(currentWord);
								int lastIndex = builder.length();

								predictedWord = adapterList.get(which);

								Log.d("[predicted]:", predictedWord);

								builder.replace(firstIndex, lastIndex,
										predictedWord);

								Log.d("[replaced-builder]:", builder.toString());

								editText.setText(builder);

								editText.setSelection(editText.getText()
										.toString().length());

								dialog2.dismiss();

							}
						});

				builder.setNeutralButton("CANCEL",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								dialog2.dismiss();
							}
						});

				if (!adapterList.isEmpty()) {
					dialog2 = builder.create();
					dialog2.show();
				}

			}
		}

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

}
