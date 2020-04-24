package com.evildoer.player.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.MapCollection;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.Unique;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Desc: Folder
 */
@Table("folder")
public class Folder implements Parcelable {

    public static final String COLUMN_NAME = "name";

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    @Column(COLUMN_NAME)
    private String name;

    @Unique
    private String path;

    private int numOfVideos;

    @MapCollection(ArrayList.class)
    @Mapping(Relation.OneToMany)
    private List<Video> videos = new ArrayList<>();

    private Date createdAt;

    public Folder() {
        // Empty
    }

    public Folder(Parcel in) {
        readFromParcel(in);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Folder(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getNumOfVideos() {
        return numOfVideos;
    }

    public void setNumOfVideos(int numOfVideos) {
        this.numOfVideos = numOfVideos;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeInt(this.numOfVideos);
        dest.writeTypedList(this.videos);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
    }

    private void readFromParcel(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.path = in.readString();
        this.numOfVideos = in.readInt();
        this.videos = in.createTypedArrayList(Video.CREATOR);
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
    }

    public static final Creator<Folder> CREATOR = new Creator<Folder>() {
        @Override
        public Folder createFromParcel(Parcel source) {
            return new Folder(source);
        }

        @Override
        public Folder[] newArray(int size) {
            return new Folder[size];
        }
    };
}
