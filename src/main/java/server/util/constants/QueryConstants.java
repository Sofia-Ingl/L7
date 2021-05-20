package server.util.constants;

public final class QueryConstants {

    /*
    CREATORS
     */
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
            DatabaseConstants.CREATION_DATE_ZONE_COLUMN_IN_MOVIES + " TEXT NOT NULL,\n" +
            DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_MOVIES + " INTEGER NOT NULL REFERENCES " + DatabaseConstants.SCREENWRITER_TABLE + "(" + DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_SCREENWRITERS + "),\n" +
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


    /*
    INSERTS
     */
    public static final String INSERT_SCREENWRITER = "INSERT INTO " + DatabaseConstants.SCREENWRITER_TABLE
            + " (" + DatabaseConstants.SCREENWRITER_NAME_COLUMN_IN_SCREENWRITERS + ", "
            + DatabaseConstants.HEIGHT_COLUMN_IN_SCREENWRITERS + ", "
            + DatabaseConstants.EYE_COLOR_COLUMN_IN_SCREENWRITERS + ", "
            + DatabaseConstants.NATION_COLUMN_IN_SCREENWRITERS + ") "
            + "VALUES (?, ?, ?, ?)";

    public static final String INSERT_MOVIE = "INSERT INTO " + DatabaseConstants.MOVIE_TABLE
            + " (" + DatabaseConstants.MOVIE_NAME_COLUMN_IN_MOVIES + ", "
            + DatabaseConstants.X_COORDINATE_COLUMN_IN_MOVIES + ", "
            + DatabaseConstants.Y_COORDINATE_COLUMN_IN_MOVIES + ", "
            + DatabaseConstants.CREATION_DATE_COLUMN_IN_MOVIES + ", "
            + DatabaseConstants.CREATION_DATE_ZONE_COLUMN_IN_MOVIES + ", "
            + DatabaseConstants.OSCARS_COUNT_COLUMN_IN_MOVIES + ", "
            + DatabaseConstants.PALMS_COUNT_COLUMN_IN_MOVIES + ", "
            + DatabaseConstants.TAGLINE_COLUMN_IN_MOVIES + ", "
            + DatabaseConstants.GENRE_COLUMN_IN_MOVIES + ", "
            + DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_MOVIES + ", "
            + DatabaseConstants.USER_NAME_COLUMN_IN_MOVIES + ") "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String INSERT_USER = "INSERT INTO " + DatabaseConstants.USER_TABLE + " VALUES (?, ?)";


    /*
    SELECTS
     */
    public static final String SELECT_ALL_MOVIES = "SELECT * FROM " + DatabaseConstants.MOVIE_TABLE;

    public static final String SELECT_USER_BY_NAME = "SELECT * FROM " + DatabaseConstants.USER_TABLE + " WHERE " + DatabaseConstants.USER_NAME_COLUMN_IN_USERS + " = ?";

    public static final String SELECT_USER_BY_NAME_AND_PASSWORD = "SELECT * FROM " + DatabaseConstants.USER_TABLE + " WHERE " + DatabaseConstants.USER_NAME_COLUMN_IN_USERS +
            " = ? AND " + DatabaseConstants.USER_PASSWORD_COLUMN_IN_USERS + " = ?";

    public static final String SELECT_SCREENWRITER_BY_ID = "SELECT * FROM " + DatabaseConstants.SCREENWRITER_TABLE + " WHERE " + DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_SCREENWRITERS + " = ?";

    public static final String SELECT_SCREENWRITER_ID_BY_NAME = "SELECT " + DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_SCREENWRITERS + " FROM "
            + DatabaseConstants.SCREENWRITER_TABLE + " WHERE LOWER(" + DatabaseConstants.SCREENWRITER_NAME_COLUMN_IN_SCREENWRITERS
            + ") LIKE LOWER(?)";

    public static final String SELECT_SCREENWRITER_ID_BY_MOVIE_ID_AND_USER = " SELECT " + DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_MOVIES
            + " FROM " + DatabaseConstants.MOVIE_TABLE + " WHERE " + DatabaseConstants.MOVIE_ID_COLUMN_IN_MOVIES
            + " = ? AND " + DatabaseConstants.USER_NAME_COLUMN_IN_MOVIES + " = ?";


    /*
    DELETES
    */
    public static final String DELETE_MOVIES_BY_USER = "DELETE FROM " + DatabaseConstants.MOVIE_TABLE
            + " WHERE " + DatabaseConstants.USER_NAME_COLUMN_IN_MOVIES + " = ?";

    public static final String DELETE_SCREENWRITERS_WHICH_ARE_NOT_USED = "DELETE FROM " + DatabaseConstants.SCREENWRITER_TABLE
            + " WHERE ( SELECT COUNT(*) FROM " + DatabaseConstants.MOVIE_TABLE + " WHERE "
            + DatabaseConstants.MOVIE_TABLE + "." + DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_MOVIES + " = "
            + DatabaseConstants.SCREENWRITER_TABLE + "." + DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_SCREENWRITERS + ") = 0";

    /*
    public static final String DELETE_MOVIES_BY_SCREENWRITER_NAME = "DELETE FROM " + DatabaseConstants.MOVIE_TABLE
            + " WHERE LOWER( SELECT " + DatabaseConstants.SCREENWRITER_NAME_COLUMN_IN_SCREENWRITERS + " FROM "
            + DatabaseConstants.SCREENWRITER_TABLE + " WHERE " + DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_SCREENWRITERS
            + " = " + DatabaseConstants.MOVIE_TABLE + "." + DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_MOVIES
            + " ) LIKE LOWER( ? )";
     */

    public static final String DELETE_MOVIES_BY_SCREENWRITER_ID_AND_USER = "DELETE FROM " + DatabaseConstants.MOVIE_TABLE
            + " WHERE " + DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_MOVIES + " = ? AND "
            + DatabaseConstants.USER_NAME_COLUMN_IN_MOVIES + " = ?";

    public static final String DELETE_MOVIES_BY_ID_AND_USER = "DELETE FROM " + DatabaseConstants.MOVIE_TABLE
            + " WHERE " + DatabaseConstants.MOVIE_ID_COLUMN_IN_MOVIES + " = ? AND "
            + DatabaseConstants.USER_NAME_COLUMN_IN_MOVIES + " = ?";

    /*
    UPDATES
     */
    public static final String UPDATE_MOVIE_BY_ID = "UPDATE " + DatabaseConstants.MOVIE_TABLE + " SET "
            + DatabaseConstants.MOVIE_NAME_COLUMN_IN_MOVIES + " = ?, "
            + DatabaseConstants.X_COORDINATE_COLUMN_IN_MOVIES + " = ?, "
            + DatabaseConstants.Y_COORDINATE_COLUMN_IN_MOVIES + " = ?, "
            + DatabaseConstants.OSCARS_COUNT_COLUMN_IN_MOVIES + " = ?, "
            + DatabaseConstants.PALMS_COUNT_COLUMN_IN_MOVIES + " = ?, "
            + DatabaseConstants.TAGLINE_COLUMN_IN_MOVIES + " = ?, "
            + DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_MOVIES + " = ?, "
            + DatabaseConstants.GENRE_COLUMN_IN_MOVIES + " = ?"
            + " WHERE " + DatabaseConstants.MOVIE_ID_COLUMN_IN_MOVIES + " = ? AND "
            + DatabaseConstants.USER_NAME_COLUMN_IN_MOVIES + " = ?";

    public static final String UPDATE_SCREENWRITER_BY_ID = "UPDATE " + DatabaseConstants.SCREENWRITER_TABLE + " SET "
            + DatabaseConstants.SCREENWRITER_NAME_COLUMN_IN_SCREENWRITERS + " = ?, "
            + DatabaseConstants.HEIGHT_COLUMN_IN_SCREENWRITERS + " = ?, "
            + DatabaseConstants.EYE_COLOR_COLUMN_IN_SCREENWRITERS + " = ?, "
            + DatabaseConstants.NATION_COLUMN_IN_SCREENWRITERS  + " = ?"
            + " WHERE " + DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_SCREENWRITERS + " = ?";

    /*
    COUNTS
     */
    public static final String COUNT_SCREENWRITER_USAGES = "SELECT COUNT(*) FROM " + DatabaseConstants.MOVIE_TABLE
            + " WHERE " + DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_MOVIES + " = ?";

    private QueryConstants() {}
}
