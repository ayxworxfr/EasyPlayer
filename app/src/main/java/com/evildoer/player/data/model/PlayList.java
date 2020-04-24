package com.evildoer.player.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.evildoer.player.player.PlayMode;
import com.litesuits.orm.db.annotation.*;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Desc: PlayList
 */
@Table("playlist")
public class PlayList implements Parcelable {

    // Play List: Favorite
    public static final int NO_POSITION = -1;
    public static final String COLUMN_FAVORITE = "favorite";

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    private String name;

    private int numOfVideos;

    @Column(COLUMN_FAVORITE)
    private boolean favorite;

    private Date createdAt;

    private Date updatedAt;

    @MapCollection(ArrayList.class)
    @Mapping(Relation.OneToMany)
    private List<Video> videos = new ArrayList<>();

    @Ignore
    private int playingIndex = -1;

    /**
     * Use a singleton play mode
     */
    private PlayMode playMode = PlayMode.LOOP;

    public PlayList() {
        // EMPTY
    }

    public PlayList(Video video) {
        videos.add(video);
        numOfVideos = 1;
    }

    public PlayList(Parcel in) {
        readFromParcel(in);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfVideos() {
        return numOfVideos;
    }

    public void setNumOfVideos(int numOfVideos) {
        this.numOfVideos = numOfVideos;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @NonNull
    public List<Video> getVideos() {
        if (videos == null) {
            videos = new ArrayList<>();
        }
        return videos;
    }

    public void setVideos(@Nullable List<Video> videos) {
        if (videos == null) {
            videos = new ArrayList<>();
        }
        this.videos = videos;
    }

    public int getPlayingIndex() {
        return playingIndex;
    }

    public void setPlayingIndex(int playingIndex) {
        this.playingIndex = playingIndex;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }

    // Parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.numOfVideos);
        dest.writeByte(this.favorite ? (byte) 1 : (byte) 0);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeTypedList(this.videos);
        dest.writeInt(this.playingIndex);
        dest.writeInt(this.playMode == null ? -1 : this.playMode.ordinal());
    }

    public void readFromParcel(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.numOfVideos = in.readInt();
        this.favorite = in.readByte() != 0;
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.videos = in.createTypedArrayList(Video.CREATOR);
        this.playingIndex = in.readInt();
        int tmpPlayMode = in.readInt();
        this.playMode = tmpPlayMode == -1 ? null : PlayMode.values()[tmpPlayMode];
    }

    public static final Creator<PlayList> CREATOR = new Creator<PlayList>() {
        @Override
        public PlayList createFromParcel(Parcel source) {
            return new PlayList(source);
        }

        @Override
        public PlayList[] newArray(int size) {
            return new PlayList[size];
        }
    };

    // Utils

    public int getItemCount() {
        return videos == null ? 0 : videos.size();
    }

    public void addVideo(@Nullable Video video) {
        if (video == null) return;

        videos.add(video);
        numOfVideos = videos.size();
    }

    public void addVideo(@Nullable Video video, int index) {
        if (video == null) return;

        videos.add(index, video);
        numOfVideos = videos.size();
    }

    public void addVideo(@Nullable List<Video> videos, int index) {
        if (videos == null || videos.isEmpty()) return;

        this.videos.addAll(index, videos);
        this.numOfVideos = this.videos.size();
    }

    public boolean removeVideo(Video video) {
        if (video == null) return false;

        int index;
        if ((index = videos.indexOf(video)) != -1) {
            if (videos.remove(index) != null) {
                numOfVideos = videos.size();
                return true;
            }
        } else {
            for (Iterator<Video> iterator = videos.iterator(); iterator.hasNext(); ) {
                Video item = iterator.next();
                if (video.getPath().equals(item.getPath())) {
                    iterator.remove();
                    numOfVideos = videos.size();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Prepare to play
     */
    public boolean prepare() {
        if (videos.isEmpty()) return false;
        if (playingIndex == NO_POSITION) {
            playingIndex = 0;
        }
        return true;
    }

    /**
     * The current video being played or is playing based on the {@link #playingIndex}
     */
    public Video getCurrentVideo() {
        if (playingIndex != NO_POSITION) {
            return videos.get(playingIndex);
        }
        return null;
    }

    public boolean hasLast() {
        return videos != null && videos.size() != 0;
    }

    public Video last() {
        switch (playMode) {
            case LOOP:
            case LIST:
            case SINGLE:
                int newIndex = playingIndex - 1;
                if (newIndex < 0) {
                    newIndex = videos.size() - 1;
                }
                playingIndex = newIndex;
                break;
            case SHUFFLE:
                playingIndex = randomPlayIndex();
                break;
        }
        return videos.get(playingIndex);
    }

    /**
     * @return Whether has next video to play.
     * <p/>
     * If this query satisfies these conditions
     * - comes from media player's complete listener
     * - current play mode is PlayMode.LIST (the only limited play mode)
     * - current video is already in the end of the list
     * then there shouldn't be a next video to play, for this condition, it returns false.
     * <p/>
     * If this query is from user's action, such as from play controls, there should always
     * has a next video to play, for this condition, it returns true.
     */
    public boolean hasNext(boolean fromComplete) {
        if (videos.isEmpty()) return false;
        if (fromComplete) {
            if (playMode == PlayMode.LIST && playingIndex + 1 >= videos.size()) return false;
        }
        return true;
    }

    /**
     * Move the playingIndex forward depends on the play mode
     *
     * @return The next video to play
     */
    public Video next() {
        switch (playMode) {
            case LOOP:
            case LIST:
            case SINGLE:
                int newIndex = playingIndex + 1;
                if (newIndex >= videos.size()) {
                    newIndex = 0;
                }
                playingIndex = newIndex;
                break;
            case SHUFFLE:
                playingIndex = randomPlayIndex();
                break;
        }
        return videos.get(playingIndex);
    }

    private int randomPlayIndex() {
        int randomIndex = new Random().nextInt(videos.size());
        // Make sure not play the same video twice if there are at least 2 videos
        if (videos.size() > 1 && randomIndex == playingIndex) {
            randomPlayIndex();
        }
        return randomIndex;
    }

    public static PlayList fromFolder(@NonNull Folder folder) {
        PlayList playList = new PlayList();
        playList.setName(folder.getName());
        playList.setVideos(folder.getVideos());
        playList.setNumOfVideos(folder.getNumOfVideos());
        return playList;
    }
}
