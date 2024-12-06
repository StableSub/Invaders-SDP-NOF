package engine.utility;

/**
 * Implements a high score record.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class Score implements Comparable<Score> {

	/** Score points. */
	private int score;

	/**
	 * Constructor.
	 *
	 * @param score
	 *            Player score.
	 */
	private String userId;

	public Score(String userId, int score) {
		this.userId = userId;
		this.score = score;
	}

	public String getUserId() {
		return userId;
	}

	/**
	 * Getter for the player's score.
	 * 
	 * @return High score.
	 */
	public final int getScore() {
		return this.score;
	}

	/**
	 * Orders the scores descending by score.
	 * 
	 * @param score
	 *            Score to compare the current one with.
	 * @return Comparison between the two scores. Positive if the current one is
	 *         smaller, positive if its bigger, zero if it is the same.
	 */
	@Override
	public final int compareTo(final Score score) {
		int comparison = this.score < score.getScore() ? 1 : this.score > score
				.getScore() ? -1 : 0;
		return comparison;
	}

	public static final int comboScore(int baseScore, int combo) {
		if (combo >= 5)
			return baseScore * (combo / 5 + 1);
		else
			return baseScore;
	}

}
