package com.evildoer.player.utils;

import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import com.evildoer.player.data.model.Folder;
import com.evildoer.player.data.model.Video;

import java.io.File;
import java.io.FileFilter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/3/16
 * Time: 11:11 PM
 * Desc: FileUtils
 */
public class FileUtils {

    private static final String UNKNOWN = "unknown";

    /**
     * http://stackoverflow.com/a/5599842/2290191
     *
     * @param size Original file size in byte
     * @return Readable file size in formats
     */
    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"b", "kb", "M", "G", "T"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static boolean isMusic(File file) {
        final String REGEX = "(.*/)*.+\\.(mp3|m4a|ogg|wav|aac)$";
        return file.getName().matches(REGEX);
    }

    public static boolean isLyric(File file) {
        return file.getName().toLowerCase().endsWith(".lrc");
    }

    public static List<Video> musicFiles(File dir) {
        List<Video> videos = new ArrayList<>();
        if (dir != null && dir.isDirectory()) {
            final File[] files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File item) {
                    return item.isFile() && isMusic(item);
                }
            });
            for (File file : files) {
                Video video = fileToMusic(file);
                if (video != null) {
                    videos.add(video);
                }
            }
        }
        if (videos.size() > 1) {
            Collections.sort(videos, new Comparator<Video>() {
                @Override
                public int compare(Video left, Video right) {
                    return left.getTitle().compareTo(right.getTitle());
                }
            });
        }
        return videos;
    }

    public static Video fileToMusic(File file) {
        if (file.length() == 0) return null;

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(file.getAbsolutePath());

        final int duration;

        String keyDuration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        // ensure the duration is a digit, otherwise return null video
        if (keyDuration == null || !keyDuration.matches("\\d+")) return null;
        duration = Integer.parseInt(keyDuration);

        final String title = extractMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_TITLE, file.getName());
        final String displayName = extractMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_TITLE, file.getName());
        final String artist = extractMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_ARTIST, UNKNOWN);
        final String album = extractMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_ALBUM, UNKNOWN);

        final Video video = new Video();
        video.setTitle(title);
        video.setDisplayName(displayName);
        video.setAuthors(artist);
        video.setPath(file.getAbsolutePath());
        video.setPertain(album);
        video.setDuration(duration);
        video.setSize((int) file.length());
        return video;
    }

    public static Folder folderFromDir(File dir) {
        Folder folder = new Folder(dir.getName(), dir.getAbsolutePath());
        List<Video> videos = musicFiles(dir);
        folder.setVideos(videos);
        folder.setNumOfVideos(videos.size());
        return folder;
    }

    private static String extractMetadata(MediaMetadataRetriever retriever, int key, String defaultValue) {
        String value = retriever.extractMetadata(key);
        if (TextUtils.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }
}
