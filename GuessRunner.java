public class GuessRunner {

	/**
	 * Calculate the number of strikes and hits given the target and the opponent's guess
	 * and print out the result
	 * @param target
	 * @param guess
	 * @return
	 */
	static Result processGuess(int target, int guess) {
		char des[] = Integer.toString(target).toCharArray();
		char src[] = Integer.toString(guess).toCharArray();
		int hits=0;
		int strikes=0;

		// process strikes
		for (int i=0; i<4; i++) {
			if (src[i] == des[i]) {
				strikes++;
				des[i] = 'a';
				src[i] = 'a';
			}
		}
		// process hits
		for (int i=0; i<4; i++) {
			for (int j=0; j<4; j++) {
				if (src[i]!='a') {
					if (src[i]==des[j]) {
						hits++;
						des[j] = 'a';
						break;
					}
				}
			}
		}
		System.out.printf("\t");
		if (strikes==4)	{ // game over
			System.out.printf("4 strikes - Game over\n");
			return new Result(hits, strikes);
		}
		if (hits==0 && strikes==0)
			System.out.printf("Miss\n");
		else if(hits>0 && strikes==0)
			System.out.printf("%d hits\n", hits);
		else if(hits==0 && strikes>0)
			System.out.printf("%d strikes\n", strikes);
		else if(hits>0 && strikes>0)
			System.out.printf("%d strikes and %d hits\n", strikes, hits);

		return new Result(hits, strikes);
	}

	/**
	 * Get from the list of worst case
	 * @return a random integer to be target to guess
	 */
	private static int getTarget() {
		// A list of expected worst case
		int [] targetList = { 9683, 9684, 9688, 9701, 9702, 9703, 9704, 9705, 9706, 9709, 9710, 9711, 9712, 9713, 9714, 9717, 9718, 9719, 9721, 9724, 9725, 3488, 3747, 3767, 3886, 3981, 4219, 4309, 4656, 4682, 4970, 5035, 5435, 5938, 5973, 6036, 6039, 6089, 6303, 6306, 6634, 6645, 6673, 6963, 7055, 7077, 7207, 7417, 7419, 7702, 7736, 7811, 7911, 7992, 8202, 8209, 8299, 8458, 8507, 8584, 8681, 8695, 8711, 8712, 8750, 8780, 8874, 8911, 8913, 9043, 9047, 9086, 9189, 9580, 9674, 9682, 9792, 9871, 9876, 9881, 9891, 9913, 9927, 9930};
		// Get a random index from 0 to target list's length - 1
		int index = (int) (Math.random() * (targetList.length));
		return targetList[index]; // Return random target
	}

	/**
	 * Main program of the game
	 * @param args
	 */
	public static void main(String[] args) {
		int guess_cnt = 0;
		// Get the target randomly from the target list
		int target = getTarget();
		Result res = new Result();
		System.out.println("Guess\tResponse\n");

		// Continue to guess if the strikes are less than 4
		while(res.getStrikes() < 4) {
			// The codebreaker makes guess
			int guess = Guess.make_guess(res.getHits(), res.getStrikes());
			System.out.printf("%d\n", guess);

			if (guess == -1) {	// user quits
				System.out.printf("you quit: %d\n", target);
				return;
			}
			// Increase the number of guess
			guess_cnt++;
			res = processGuess(target, guess);
		}
		System.out.printf("Target: %d - Number of guesses: %d\n", target, guess_cnt);
	}
}
