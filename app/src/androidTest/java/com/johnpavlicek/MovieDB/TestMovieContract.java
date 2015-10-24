package com.johnpavlicek.MovieDB;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by johnpavlicek on 10/24/15.
 */
public class TestMovieContract extends AndroidTestCase {

    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final String TEST_MOVIE = "Star Wars";

    /*
        Students: Uncomment this out to test your movie function.
     */
    public void testBuildMovie() {
        Uri movieUri = MovieContract.MovieEntry.buildMovie(TEST_MOVIE);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovie in " +
                        "MovieContract.",
                movieUri);
        assertEquals("Error: Movie name not properly appended to the end of the Uri",
                TEST_MOVIE, movieUri.getLastPathSegment());
        assertEquals("Error: Movie Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.johnpavlicek.MovieDB/movie/Star%20Wars");
    }
}

