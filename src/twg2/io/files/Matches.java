package twg2.io.files;

import java.util.List;

/** A collection containing matching and non-matching (failed matches) elements
 * @author TeamworkGuy2
 * @since 2016-08-18
 * @param <E> the type of matches/failed matches
 */
public interface Matches<E> {

	public List<E> getMatches();

	public List<E> getFailedMatches();

}