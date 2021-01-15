import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
 * You need to implement an algorithm to make guesses
 * for a 4-digits number in the method make_guess below
 * that means the guess must be a number between [1000-9999]
 * PLEASE DO NOT CHANGE THE NAME OF THE CLASS AND THE METHOD
 */
public class Guess {

	private static int noOfGuesses = 0;
	private static List<Integer> potentialTargets = IntStream.range(1000, 10000).boxed().collect(Collectors.toList());
    // Initialize guess as 1123
	private static int currentGuess = 1123;

	public static void refresh() {
	    currentGuess = 1123;
        potentialTargets = IntStream.range(1000, 10000).boxed().collect(Collectors.toList());
        noOfGuesses = 0;
    }
	
	public static int make_guess(int hits, int strikes) {
		noOfGuesses ++;

        /* ===== If is the first guess ===== */
		if (noOfGuesses == 1) return currentGuess;

		/* ===== If not the first guess ===== */
        // Update list of potential targets
        potentialTargets = filterTargets(strikes, hits);
//        System.out.println("pos targets left "+ potentialTargets.size());

        int maxPartitions = -1;
        int myGuess = -1;
        int numberOfSwapMax = 0;

        // Implement Max-Part algorithm (to choose 1 potential target among all potential targets)
        for (int guess: potentialTargets) {
            // Hash map of (result, number of targets that have that result given the guess)
            HashMap<Integer, Integer> resultMap = new HashMap<>();
            int numberOfPartitions = 0;
            for (int target: potentialTargets) {
                Result result = processGuess(target, guess);
                int hashKey = hash(result); // Convert result to key

                // For each new result, update the number of partitions for this pair of (target, guess)
                if (!resultMap.containsKey(hashKey)) {
                    resultMap.put(hashKey, 0);
                    numberOfPartitions ++;
                }
                resultMap.put(hashKey, resultMap.get(hashKey) + 1);
            }

            if (numberOfPartitions == 14) { // Reach the maximum number of partitions
                currentGuess = guess;
                return guess; // Return the guess has the largest number of partitions
            }
            if (numberOfPartitions > maxPartitions){
                maxPartitions = numberOfPartitions;
                numberOfSwapMax ++;
                myGuess = guess;
            }
        }

        // Handle special case: when all potential targets have the same number of partitions
        if (numberOfSwapMax == 1 && potentialTargets.size() >= 3 && potentialTargets.size() <= 10) {
            // Convert to char array so that we can access and process each digit in target
            List<char[]> targets = convertToCharArrayList(potentialTargets);

            // Support variables
            int mismatchPosition = 0;
            int numberOfMismatch = 0;
            boolean notSpecial = false;

            for (int i = 0; i < 4 && !notSpecial; i++) {
                char firstDigit = targets.get(0)[i];
                boolean isMatch = firstDigit == targets.get(1)[i];
                if (isMatch) {
                    for (int j = 2; j < targets.size() - 1; j++) {
                        if (targets.get(j)[i] != targets.get(j + 1)[i]) {
                            notSpecial = true;
                            break;
                        }
                    }
                } else {
                    mismatchPosition = i;
                    numberOfMismatch += 1;
                }
            }

            // If all potential targets have 3 same digits, only 1 digit is different
            if (numberOfMismatch == 1) {
                myGuess = createSpecialGuess(targets, mismatchPosition);

            }
        }
        currentGuess = myGuess;
		return currentGuess;
	}

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

        return new Result(hits, strikes);
    }

	private static int createSpecialGuess(List<char[]> potentialTargets, int mismatchPosition) {
        List<Character> remainSolutions = new ArrayList<>();
        for (char[] target: potentialTargets) {
            remainSolutions.add(target[mismatchPosition]);
        }

        char[] myGuess = potentialTargets.get(0);

        // Keep track of the number of digits that have taken from the sample target
        int numberOfDigits = 0;
        for (int i = 0; i < myGuess.length; i++) {
            if (i == mismatchPosition) {
                // If mismatch, replace with the first solutions
                myGuess[i] = remainSolutions.get(0);
                continue;
            } else if (numberOfDigits == 1) {
                myGuess[i] = '9';
            } else if (numberOfDigits == 2) {
                // For the second replacement, replace with the second remain solutions
                myGuess[i] = remainSolutions.get(1);
            }
            numberOfDigits ++;
        }
        return Integer.parseInt(new String(myGuess));
    }

    private static List<char[]> convertToCharArrayList(List<Integer> integers) {
        List<char[]> results = new ArrayList<>();
	    for (int integer: integers) {
            results.add(Integer.toString(integer).toCharArray());
        }
	    return results;
    }


	// Function to filter out numbers that are impossible to be the target
	private static List<Integer> filterTargets (int strikes, int hits) {
        List<Integer> targets = new ArrayList<>();
        // Among remaining possible targets, choose those have the same result as the previous guess
        for (int target: potentialTargets) {

            Result result = processGuess(target, currentGuess);
            if (result.getStrikes() == strikes && result.getHits() == hits) {
                targets.add(target);
            }
        }
        return targets;
    }

	private static int hash(Result result) {
	    if (result.getStrikes() == 3) return 12;
	    if (result.getStrikes() == 4) return 13;
	    int value = result.getStrikes() * 5 + result.getHits();
	    if (result.getStrikes() == 2) value -= 1;
	    return value;
    }

}