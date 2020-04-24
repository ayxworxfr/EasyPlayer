package com.evildoer.player.player;

import androidx.annotation.Nullable;

import com.evildoer.player.data.model.PlayList;
import com.evildoer.player.data.model.Video;


/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/5/16
 * Time: 6:02 PM
 * Desc: IPlayer
 */
public interface IPlayback {

    void setPlayList(PlayList list);

    boolean play();

    boolean play(PlayList list);

    boolean play(PlayList list, int startIndex);

    boolean play(Video video);

    boolean playLast();

    boolean playNext();

    boolean pause();

    boolean isPlaying();

    int getProgress();

    Video getPlayingVideo();

    boolean seekTo(int progress);

    void setPlayMode(PlayMode playMode);

    void registerCallback(Callback callback);

    void unregisterCallback(Callback callback);

    void removeCallbacks();

    void releasePlayer();

    interface Callback {

        void onSwitchLast(@Nullable Video last);

        void onSwitchNext(@Nullable Video next);

        void onComplete(@Nullable Video next);

        void onPlayStatusChanged(boolean isPlaying);
    }
}
