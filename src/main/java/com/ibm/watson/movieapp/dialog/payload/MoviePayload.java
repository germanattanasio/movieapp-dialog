/* Copyright IBM Corp. 2015
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.watson.movieapp.dialog.payload;

import java.util.Date;

/**
 * <P>
 * Various attributes of a movie extracted using themoviedb.org API.
 * <P>
 * This is a payload class which carries all the info associated with a movie. It is instantiated in {@code SearchTheMovieDbProxyResource} and
 * subsequently passed onto {@code WDSBlueMixProxyResource} for sending across to the client-side for rendering movie information.
 * 
 * @author Ashima Arora
 */

public class MoviePayload {
    private Date releaseDate;
    private String releaseDateStr;
    private String genre;
    private Integer genreId;
    private Integer movieId;
    private String certification;
    private String certificationCountry;
    private Double popularity;
    private String movieName;
    private String overview;
    private Integer runtime;
    private String homepageUrl;
    private String posterPath;
    private String trailerUrl;

    /**
     * @return the name of the movie
     */
    public String getMovieName() {
        return movieName;
    }

    /**
     * @param movieName  the name of the movie
     */
    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    /**
     * @return  the brief overview of the movie
     */
    public String getOverview() {
        return overview;
    }

    /**
     * @param overview  the brief overview of the movie
     */
    public void setOverview(String overview) {
        this.overview = overview;
    }

    /**
     * @return  the movie runtime (in minutes)
     */
    public Integer getRuntime() {
        return runtime;
    }

    /**
     * @param runtime  the movie runtime (in minutes)
     */
    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    /**
     * @return  the URL to the movie home page
     */
    public String getHomepageUrl() {
        return homepageUrl;
    }

    /**
     * @param homepageUrl  the URL to the movie home page
     */
    public void setHomepageUrl(String homepageUrl) {
        this.homepageUrl = homepageUrl;
    }

    /**
     * @return  the path to the movie poster image
     */
    public String getPosterPath() {
        return posterPath;
    }

    /**
     * @param posterPath  the path to the movie poster image
     */
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    /**
     * @return the URL for the movie trailer
     */
    public String getTrailerUrl() {
        return trailerUrl;
    }

    /**
     * @param trailerUrl  the URL for the movie trailer
     */
    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    /**
     * @return  the movie popularity (Double out of 10)
     */
    public Double getPopularity() {
        return popularity;
    }

    /**
     * @param popularity  the movie popularity (Double out of 10)
     */
    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    /**
     * @return  the id for the movie's genre(extracted from themoviedb.org API)
     */
    public Integer getGenreId() {
        return genreId;
    }

    /**
     * @param genreId  the id for the movie's genre(extracted from themoviedb.org API)
     */
    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    /**
     * @return  the movie certification("R", "PG", etc.)
     */
    public String getCertification() {
        return certification;
    }

    /**
     * @param certification  the movie certification("R", "PG", etc.)
     */
    public void setCertification(String certification) {
        this.certification = certification;
    }

    /**
     * @return  the country corresponding to the movie's certification
     */
    public String getCertificationCountry() {
        return certificationCountry;
    }

    /**
     * @param certificationCountry  the country corresponding to the movie's certification
     */
    public void setCertificationCountry(String certificationCountry) {
        this.certificationCountry = certificationCountry;
    }

    /**
     * @return  the movie id(extracted from themoviedb.org API)
     */
    public Integer getMovieId() {
        return movieId;
    }

    /**
     * @param movieId  the movie id(extracted from themoviedb.org API)
     */
    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    /**
     * @return  the movie genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * @param genre  the movie genre
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * @return  the movie release date(in string format)
     */
    public String getReleaseDateStr() {
        return releaseDateStr;
    }

    /**
     * @param releaseDateStr  the movie release date(in String format)
     */
    public void setReleaseDateStr(String releaseDateStr) {
        this.releaseDateStr = releaseDateStr;
    }

    /**
     * @return  the movie release date(in Date format)
     */
    public Date getReleaseDate() {
        return releaseDate;
    }

    /**
     * @param releaseDate  the movie release date(in Date format)
     */
    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

}
