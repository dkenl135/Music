package com.windsoft.means;

import android.content.SharedPreferences;

/**
 * Created by dongkyu on 2015-06-14.
 */
public class Global {

    public static final String DB_NAME = "Music.db";

    public static final String NEW_MUSIC = "http://www.genie.co.kr/newest/f_song.asp?GenreCode=hot&pg=";
    public static final String SONG_BALAD = "http://www.genie.co.kr/genre/f_genre.asp?genrecode=L0101&pg=";
    public static final String SONG_DANCE = "http://www.genie.co.kr/genre/f_genre.asp?genrecode=L0102&pg=";
    public static final String SONG_CLUB = "http://www.genie.co.kr/genre/f_genre.asp?genrecode=L0106&pg=";
    public static final String SONG_ROCK = "http://www.genie.co.kr/genre/f_genre.asp?genrecode=L0105&pg=";
    public static final String SONG_RNB = "http://www.genie.co.kr/genre/f_genre.asp?genrecode=L0103&pg=";
    public static final String SONG_HIPHOP = "http://www.genie.co.kr/genre/f_genre.asp?genrecode=L0104&pg=";
    public static final String SONG_INDI = "http://www.genie.co.kr/genre/f_genre.asp?genrecode=L0109&pg=";
    public static final String SONG_TROT = "http://www.genie.co.kr/genre/f_genre.asp?genrecode=L0107&pg=";

    public static final String POP = "http://www.genie.co.kr/genre/f_genre.asp?genrecode=L0201&pg=";
    public static final String POP_ROCK = "http://www.genie.co.kr/genre/f_genre.asp?genrecode=L0202&pg=";
    public static final String POP_RNB = "http://www.genie.co.kr/genre/f_genre.asp?genrecode=L0203&pg=";
    public static final String POP_CLUB = "http://www.genie.co.kr/genre/f_genre.asp?genrecode=L0205&pg=";
    public static final String POP_HIPHOP = "http://www.genie.co.kr/genre/f_genre.asp?genrecode=L0204&pg=";

    public static final String OST = "http://www.genie.co.kr/genre/f_genre.asp?genrecode=M0300&pg=";
    public static final String JPOP = "http://www.genie.co.kr/genre/f_genre.asp?genrecode=M0400&pg=";
    public static final String CLASSIC = "http://www.genie.co.kr/genre/f_genre.asp?genrecode=M0600&pg=";
    public static final String JAZZ = "http://www.genie.co.kr/genre/f_genre.asp?genrecode=M0500&pg=";
    public static final String CHILD = "http://www.genie.co.kr/genre/f_genre.asp?genrecode=L0901&pg=";

    public static final String[] SONG_LIST = {
            NEW_MUSIC,
            SONG_BALAD,
            SONG_DANCE,
            SONG_CLUB,
            SONG_ROCK,
            SONG_RNB,
            SONG_HIPHOP,
            SONG_INDI,
            SONG_TROT,
            POP,
            POP_CLUB,
            POP_HIPHOP,
            POP_RNB,
            POP_ROCK,
            OST,
            JPOP,
            JAZZ,
            CLASSIC,
            CHILD
    };

    public static String SONG_INDEX = "songIndex";
    public static String PAGE_INDEX = "pageIndex";

    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;
    public static final String PREF_KEY = "muzik";
    public static final String COUNT_KEY = "count";

    public static final String QUERY_NAVER_MUSIC = "http://music.naver.com/search/search.nhn?query=";
    public static final String QUERY_GENIE_MUSIC = "http://www.genie.co.kr/Search/f_Search_Main.asp?query=";

    public static DBManager manager;

    public static final int ERROR = 0;
    public static final int SUCCESS = 1;

    public static final String COMMAND_KEY = "command";
    public static final String ID_KEY = "id";
    public static final String SONG_NAME_KEY = "songName";
    public static final String ARTIST_KEY = "artist";
    public static final String SONG_SCORE_KEY = "songScore";
    public static final String LOGIN_ID_KEY = "loginId";
    public static final String LOGIN_KEY = "login";
    public static final String CHECK_DB = "checkDB";
    public static final String REQ_DB = "reqDB";
    public static final String RES_DB = "resDB";
    public static final String GET_MUSIC_KEY = "getMusic";
    public static final String GET_PHOTO_KEY = "getphoto";
    public static final String MUSIC_NAME_KEY = "musicName";
    public static final String PHOTO_NAME_KEY = "photoName";
    public static final String CONNECT_SERVER = "connectServer";
    public static final String SERVER_URL = "http://muzik-server.herokuapp.com";
    public static final String PHOTO_SRC_KEY = "src";

    public static final String USER_ID = "id";
    public static final String USER_SONG = "song";
    public static final String USER_SCORE = "score";

    public static final String SONG_NAME = "name";
    public static final String SONG_USER_ID = "userId";
    public static final String SONG_SCORE = "score";

    public static final String GOOGLE_AUTH_KEY = "186735483118-ur14q3iatdlqgncco4l4g9nhobn71o2p.apps.googleusercontent.com";

    public static final String COND = "cond";

    public static String ID = null;

    public static final int NULL = 10;
    public static final int GOOD = 1;
    public static final int SOSO = 0;
    public static final int BAD = -1;

}
