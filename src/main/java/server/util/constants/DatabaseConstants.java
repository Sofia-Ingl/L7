package server.util.constants;

public final class DatabaseConstants {

    public static final String MOVIE_TABLE = "movies";
    public static final String USER_TABLE = "users";
    public static final String SCREENWRITER_TABLE = "screenwriters";

    public static final String MOVIE_ID_COLUMN_IN_MOVIES = "movieId";
    public static final String MOVIE_NAME_COLUMN_IN_MOVIES = "movieName";
    public static final String X_COORDINATE_COLUMN_IN_MOVIES = "xCoord";
    public static final String Y_COORDINATE_COLUMN_IN_MOVIES = "yCoord";
    public static final String OSCARS_COUNT_COLUMN_IN_MOVIES = "oscarsCount";
    public static final String PALMS_COUNT_COLUMN_IN_MOVIES = "gPalmsCount";
    public static final String TAGLINE_COLUMN_IN_MOVIES = "tagline";
    public static final String CREATION_DATE_COLUMN_IN_MOVIES = "creationDate";
    public static final String CREATION_DATE_ZONE_COLUMN_IN_MOVIES = "creationDateZone";
    public static final String SCREENWRITER_ID_COLUMN_IN_MOVIES = "screenwriterId";
    public static final String GENRE_COLUMN_IN_MOVIES = "genreName";
    public static final String USER_NAME_COLUMN_IN_MOVIES = "userName";

    public static final String USER_NAME_COLUMN_IN_USERS = "userName";
    public static final String USER_PASSWORD_COLUMN_IN_USERS = "hashPass";

    public static final String SCREENWRITER_ID_COLUMN_IN_SCREENWRITERS = "screenwriterId";
    public static final String SCREENWRITER_NAME_COLUMN_IN_SCREENWRITERS = "screenwriterName";
    public static final String HEIGHT_COLUMN_IN_SCREENWRITERS = "height";
    public static final String EYE_COLOR_COLUMN_IN_SCREENWRITERS = "eyeColor";
    public static final String NATION_COLUMN_IN_SCREENWRITERS = "nation";

    public static final String MOVIE_ID_SEQUENCE = "movieIdGen";
    public static final String SCREENWRITER_ID_SEQUENCE = "scrIdGen";

    private DatabaseConstants() {}

    /*

CREATE TABLE IF NOT EXISTS movieTest (
  movieId INTEGER PRIMARY KEY CHECK(movieId > 0),
  movieName TEXT NOT NULL,
  xCoord FLOAT CHECK(xCoord <= 326),
  yCoord INTEGER NOT NULL CHECK(yCoord <= 281),
  oscarsCount INTEGER NOT NULL CHECK(oscarsCount > 0),
  gPalmsCount BIGINT NOT NULL CHECK (gPalmsCount > 0),
  tagline TEXT NOT NULL,
  creationDate TIMESTAMP NOT NULL,
  screenwriterId INTEGER REFERENCES scrTest(scrId),
  genreName TEXT,
  userName TEXT NOT NULL REFERENCES users(userName)
);

CREATE TABLE IF NOT EXISTS scrTest (
  screenwriterId INTEGER PRIMARY KEY,
  screenwriterName TEXT NOT NULL CHECK(scrName NOT LIKE ''),
  height BIGINT NOT NULL CHECK(height>0),
  eyeColor TEXT,
  nation TEXT
);

CREATE TABLE IF NOT EXISTS users (
  userName TEXT PRIMARY KEY,
  hashPass TEXT NOT NULL
);

     */
}
