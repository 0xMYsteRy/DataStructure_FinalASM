import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class GuessRunner {

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
//		System.out.printf("\t");
//		if (strikes==4)	{ // game over
//			System.out.printf("4 strikes - Game over\n");
//			return new Result(hits, strikes);
//		}
//		if (hits==0 && strikes==0)
//			System.out.printf("Miss\n");
//		else if(hits>0 && strikes==0)
//			System.out.printf("%d hits\n", hits);
//		else if(hits==0 && strikes>0)
//			System.out.printf("%d strikes\n", strikes);
//		else if(hits>0 && strikes>0)
//			System.out.printf("%d strikes and %d hits\n", strikes, hits);
		
		return new Result(hits, strikes);
	}

	public static void main(String[] args) {
		int guess_cnt = 0;
		/* A dummy value, you need to code here
		 * to get a target number for your oponent
		 * should be a random number between [1000-9999]
		 */
		int startTarget = 1000;
		int endTarget = 10000;
		HashMap<Integer, List<Integer>> testResult = new HashMap<>();
		int target;
		for (target = startTarget; target < endTarget; target++) {
			Result res = new Result();

			while(res.getStrikes() < 4) {
				/* take a guess from user provided class
				 * the user provided class must be a Guess.class file
				 * that has implemented a static function called make_guess()
				 */
				int guess = Guess.make_guess(res.getHits(), res.getStrikes());
//				System.out.printf("%d\n", guess);


				if (guess == -1) {	// user quits
//					System.out.printf("you quit: %d\n", target);
					return;
				}
				guess_cnt++;

				/* You need to code this method to process a guess
				 * provided by your oponent
				 */
				res = processGuess(target, guess);
//				System.out.println("strike " + res.getStrikes() + " hits " + res.getHits());
		}
			Guess.refresh();
			System.out.printf("Target: %d - Number of guesses: %d\n", target, guess_cnt);
			if (!testResult.containsKey(guess_cnt)) {
				testResult.put(guess_cnt, new ArrayList<>());
			}
			List<Integer> currentList = testResult.get(guess_cnt);
			currentList.add(target);
			testResult.put(guess_cnt, currentList);
			guess_cnt = 0;
		}
		System.out.println(testResult);
		int sum = 0;
		for (int i = 1; i < 10; i++) {
			if (testResult.containsKey(i)) {
				sum += i * testResult.get(i).size();
				System.out.println(i + " steps : " + testResult.get(i).size() + " numbers");
			}
		}
		double average = sum / ((double) endTarget - startTarget);
		System.out.println("Average: " + average + " steps");

	}

}
