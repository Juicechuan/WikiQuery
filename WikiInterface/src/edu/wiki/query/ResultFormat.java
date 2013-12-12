package edu.wiki.query;

public class ResultFormat implements Comparable<Object> {
	double rank;
	int id;
	int low;
	int high;

	public ResultFormat(double rank, int id, int low, int high) {
		this.rank = rank;
		this.id = id;
		this.low = low;
		this.high = high;
	}

	public double getRank() {
		return this.rank;
	}

	public int getId() {
		return this.id;
	}

	public int getLow() {
		return this.low;
	}

	public int getHigh() {
		return this.high;
	}

	@Override
	public int compareTo(Object obj) {
		ResultFormat n = (ResultFormat) obj;
		if (rank == n.rank) {
			return id - n.id;
		} else {
			return (int) (rank - n.rank);
		}
	}
}
