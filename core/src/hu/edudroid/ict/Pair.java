package hu.edudroid.ict;

public class Pair<S, T> {
	public final S first; 
	public final T second;
	public Pair (S first, T second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public int hashCode() {
		int hashCode = 0;
		if (first != null) {
			hashCode += first.hashCode();
		}
		if (second != null) {
			hashCode += second.hashCode();
		}
		return hashCode;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof Pair) {
			@SuppressWarnings("rawtypes")
			Pair p = (Pair) o;
			return ((((first == null && (p.first == null)) || first.equals(p.first))) &&
					(((second == null && (p.second == null)) || second.equals(p.second))));
		}
		return false;
	}
}
