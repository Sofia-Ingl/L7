package server.util.constants;

public final class QueryConstants {

    public static final String MOVIE_ID_SEQUENCE_CREATOR = "CREATE SEQUENCE IF NOT EXISTS "
            + DatabaseConstants.MOVIE_ID_SEQUENCE + " START 1;";

    public static final String SCREENWRITER_ID_SEQUENCE_CREATOR = "CREATE SEQUENCE IF NOT EXISTS "
            + DatabaseConstants.SCREENWRITER_ID_SEQUENCE + " START 1;";

    public static final String MOVIE_TABLE_CREATOR = "CREATE TABLE IF NOT EXISTS " + DatabaseConstants.MOVIE_TABLE + " (\n" +
            DatabaseConstants.MOVIE_ID_COLUMN_IN_MOVIES + " INTEGER PRIMARY KEY CHECK(" + DatabaseConstants.MOVIE_ID_COLUMN_IN_MOVIES + " > 0) DEFAULT nextval('" + DatabaseConstants.MOVIE_ID_SEQUENCE + "'),\n" +
            DatabaseConstants.MOVIE_NAME_COLUMN_IN_MOVIES + " TEXT NOT NULL,\n" +
            DatabaseConstants.X_COORDINATE_COLUMN_IN_MOVIES + " FLOAT CHECK(" + DatabaseConstants.X_COORDINATE_COLUMN_IN_MOVIES + " <= 326),\n" +
            DatabaseConstants.Y_COORDINATE_COLUMN_IN_MOVIES + " INTEGER NOT NULL CHECK(" + DatabaseConstants.Y_COORDINATE_COLUMN_IN_MOVIES + " <= 281),\n" +
            DatabaseConstants.OSCARS_COUNT_COLUMN_IN_MOVIES + " INTEGER NOT NULL CHECK(" + DatabaseConstants.OSCARS_COUNT_COLUMN_IN_MOVIES + " > 0),\n" +
            DatabaseConstants.PALMS_COUNT_COLUMN_IN_MOVIES + " BIGINT NOT NULL CHECK (" + DatabaseConstants.PALMS_COUNT_COLUMN_IN_MOVIES + " > 0),\n" +
            DatabaseConstants.TAGLINE_COLUMN_IN_MOVIES + " TEXT NOT NULL,\n" +
            DatabaseConstants.CREATION_DATE_COLUMN_IN_MOVIES + " TIMESTAMP NOT NULL,\n" +
            DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_MOVIES + " INTEGER REFERENCES " + DatabaseConstants.SCREENWRITER_TABLE + "(" + DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_SCREENWRITERS + "),\n" +
            DatabaseConstants.GENRE_COLUMN_IN_MOVIES + " TEXT,\n" +
            DatabaseConstants.USER_NAME_COLUMN_IN_MOVIES + " TEXT NOT NULL REFERENCES " + DatabaseConstants.USER_TABLE + "(" + DatabaseConstants.USER_NAME_COLUMN_IN_USERS + ")\n" +
            ")";

    public static final String SCREENWRITER_TABLE_CREATOR = "CREATE TABLE IF NOT EXISTS " + DatabaseConstants.SCREENWRITER_TABLE + " (\n" +
            DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_SCREENWRITERS + " INTEGER PRIMARY KEY DEFAULT nextval('" + DatabaseConstants.SCREENWRITER_ID_SEQUENCE + "'),\n" +
            DatabaseConstants.SCREENWRITER_NAME_COLUMN_IN_SCREENWRITERS + " TEXT NOT NULL CHECK(" + DatabaseConstants.SCREENWRITER_NAME_COLUMN_IN_SCREENWRITERS + " NOT LIKE ''),\n" +
            DatabaseConstants.HEIGHT_COLUMN_IN_SCREENWRITERS + " BIGINT NOT NULL CHECK(" + DatabaseConstants.HEIGHT_COLUMN_IN_SCREENWRITERS + ">0),\n" +
            DatabaseConstants.EYE_COLOR_COLUMN_IN_SCREENWRITERS + " TEXT,\n" +
            DatabaseConstants.NATION_COLUMN_IN_SCREENWRITERS + " TEXT\n" +
            ")";

    public static final String USER_TABLE_CREATOR = "CREATE TABLE IF NOT EXISTS " + DatabaseConstants.USER_TABLE + " (\n" +
            DatabaseConstants.USER_NAME_COLUMN_IN_USERS + " TEXT PRIMARY KEY,\n" +
            DatabaseConstants.USER_PASSWORD_COLUMN_IN_USERS + " TEXT NOT NULL\n" +
            ")";


    public static final String INSERT_SCREENWRITER = "INSERT INTO " + DatabaseConstants.SCREENWRITER_TABLE
            + " VALUES (nextval('" + DatabaseConstants.SCREENWRITER_ID_SEQUENCE + "'), ?, ?, ?, ?)";

    public static final String INSERT_MOVIE = "INSERT INTO " + DatabaseConstants.MOVIE_TABLE
            + " VALUES (nextval('" + DatabaseConstants.MOVIE_ID_SEQUENCE + "'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String INSERT_USER = "INSERT INTO " + DatabaseConstants.USER_TABLE + " VALUES (?, ?)";


    private QueryConstants() {
    }
}
