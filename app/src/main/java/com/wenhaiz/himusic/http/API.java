package com.wenhaiz.himusic.http;

public class API {

    public static class XiaMi{
        static final String BASE = "https://www.xiami.com";

        public static final String GET_COLLECT_LIST = "/api/list/collect";

        public static final String GET_COLLECT_DETAIL = "/api/collect/initialize";

        public static final String GET_ALBUM_LIST = "/api/list/album";

        public static final String GET_ALBUM_DETAIL = "/api/album/initialize";

        public static final String GET_SONG_DETAIL = "/api/song/getSongDetails";
        public static final String GET_SONG_PLAY_INFO = "/api/song/getPlayInfo";

        public static String getSongDetailUrl(Long songId) {
            return BASE + "/song/playlist/id/" + songId + "/object_name/default/object_id/0/cat/json";
        }
    }

}