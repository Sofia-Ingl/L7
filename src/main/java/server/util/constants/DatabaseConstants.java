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

}
