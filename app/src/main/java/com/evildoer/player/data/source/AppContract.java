package com.evildoer.player.data.source;

import com.evildoer.player.data.model.Folder;
import com.evildoer.player.data.model.PlayList;
import com.evildoer.player.data.model.Video;

import java.util.List;

import rx.Observable;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/10/16
 * Time: 4:52 PM
 * Desc: AppContract
 */
/* package */ interface AppContract {

    // Play List

    Observable<List<PlayList>> playLists();

    List<PlayList> cachedPlayLists();

    Observable<PlayList> create(PlayList playList);

    Observable<PlayList> update(PlayList playList);

    Observable<PlayList> delete(PlayList playList);

    // Folder

    Observable<List<Folder>> folders();

    Observable<Folder> create(Folder folder);

    Observable<List<Folder>> create(List<Folder> folders);

    Observable<Folder> update(Folder folder);

    Observable<Folder> delete(Folder folder);

    // Video

    Observable<List<Video>> insert(List<Video> videos);

    Observable<Video> update(Video video);

    Observable<Video> setVideoAsFavorite(Video video, boolean favorite);

}
