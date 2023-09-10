package it.unimib.fipavonline.util;

/**
 * Utility class to save constants used by the app.
 */
public class Constants {

    // Constants for SharedPreferences
    public static final String SHARED_PREFERENCES_FILE_NAME = "it.unimib.fipavonline.preferences";
    public static final String  SHARED_PREFERENCES_FIRST_LOADING = "first_loading";

    // Constants for EncryptedSharedPreferences
    public static final String ENCRYPTED_SHARED_PREFERENCES_FILE_NAME = "it.unimib.fipavonline.encrypted_preferences";
    public static final String EMAIL_ADDRESS = "email_address";
    public static final String PASSWORD = "password";
    public static final String ID_TOKEN = "google_token";

    // Constants for encrypted files
    public static final String ENCRYPTED_DATA_FILE_NAME = "it.unimib.fipavonline.encrypted_file.txt";

    // Constants for files contained in assets folder
    public static final String CAMPIONATO_API_TEST_JSON_FILE = "campionato-test.json";
    public static final String PARTITA_API_TEST_JSON_FILE = "partita-test.json";

    // Constants for API
    public static final String FIPAV_ONLINE_API_BASE_URL = "https://unimib.kmsolution.link/api/";
    public static final String CAMPIONATO_ENDPOINT = "campionato.php";

    // Constants for refresh rate of campionato
    public static final String LAST_UPDATE = "last_update";
    public static final int FRESH_TIMEOUT = 1000; // *60*5; // 5 minuti in millisecondi

    // Constants for Room database
    public static final String FIPAV_ONLINE_DATABASE_NAME = "fipavonline_db";
    public static final int DATABASE_VERSION = 1;

    // Constants for managing errors
    public static final String RETROFIT_ERROR = "retrofit_error";
    public static final String API_KEY_ERROR = "api_key_error";
    public static final String UNEXPECTED_ERROR = "unexpected_error";
    public static final String INVALID_USER_ERROR = "invalidUserError";
    public static final String INVALID_CREDENTIALS_ERROR = "invalidCredentials";
    public static final String USER_COLLISION_ERROR = "userCollisionError";
    public static final String WEAK_PASSWORD_ERROR = "passwordIsWeak";

    public static final int MINIMUM_PASSWORD_LENGTH = 6;

    // Constants for Firebase Realtime Database
    public static final String FIREBASE_REALTIME_DATABASE = "https://fipav-online-default-rtdb.europe-west1.firebasedatabase.app/";
    public static final String FIREBASE_USERS_COLLECTION = "users";
    public static final String FIREBASE_FAVORITE_CAMPIONATO_COLLECTION = "favorite_campionato";
}
