package twg2.io.fileLoading;

/** A mutable container containing 'valid' and 'invalid' fields.
 * NOTE: mutable, public properties, not thread safe
 * @author TeamworkGuy2
 * @since 2015-6-8
 * @param <A> the valid type of data
 * @param <B> the invalid type of data
 */
public class ValidInvalid<A, B> {
	public A valid;
	public B invalid;


	public ValidInvalid(A valid, B invalid) {
		this.valid = valid;
		this.invalid = invalid;
	}


	public static <X, Y> ValidInvalid<X, Y> of(X valid, Y invalid) {
		return new ValidInvalid<>(valid, invalid);
	}

}