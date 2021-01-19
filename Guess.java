import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class for the codebreaker
 */
public class Guess {
    // Variable to save the number of guesses has been made
	private static int noOfGuesses = 0;
	// Generate a list ranging from 1000 to 9999
	private static List<Integer> potentialTargets = IntStream.range(1000, 10000).boxed().collect(Collectors.toList());
    // Initialize guess as 3478
	private static int currentGuess = 3478;

    /**
     * Main function to make the guess given the number of hits and strikes of the previous guess
     * @param hits
     * @param strikes
     * @return
     */
	public static int make_guess(int hits, int strikes) {
		noOfGuesses ++;

        /* ===== If is the first guess ===== */
		if (noOfGuesses == 1) return currentGuess;

		/* ===== If not the first guess ===== */
        // Update list of potential targets
        potentialTargets = filterTargets(strikes, hits);

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
            // If this target has larger number of partitions than the current max amount
            if (numberOfPartitions > maxPartitions){
                maxPartitions = numberOfPartitions; // Save the new max
                numberOfSwapMax ++; // Keep track of the number of swap max
                myGuess = guess; // Save the guess which has most partitions
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

            // Loop through 4 digits in all possible targets
            for (int i = 0; i < 4 && !notSpecial; i++) {
                char firstDigit = targets.get(0)[i]; // Get the first target
                boolean isMatch = firstDigit == targets.get(1)[i]; // Check if the first and the second digit is the same
                // If the first and the second digit is the same
                if (isMatch) {
                    // Check the rest digits if they are the same
                    for (int j = 2; j < targets.size() - 1; j++) {
                        if (targets.get(j)[i] != targets.get(j + 1)[i]) {
                            notSpecial = true;
                            break;
                        }
                    }
                } else { // If not match, the digit at this position is unknown / mismatch with others
                    mismatchPosition = i; // Save to mismatchPosition
                    numberOfMismatch += 1; // Count the number of unknown value
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

    /**
     * Process guess and target to get the number of strikes and hits
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

        return new Result(hits, strikes);
    }

    /**
     * Create special guess when all possible targets have three same digits which are strikes, and one unknown digit
     * @param potentialTargets
     * @param mismatchPosition
     * @return
     */
	private static int createSpecialGuess(List<char[]> potentialTargets, int mismatchPosition) {
        List<Character> remainSolutions = new ArrayList<>();
        for (char[] target: potentialTargets) {
            remainSolutions.add(target[mismatchPosition]);
        }
        // Get the first potential target as a sample
        char[] myGuess = potentialTargets.get(0);

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

    /**
     * Convert List of Integer to List of Char Array
     * Each character represents a digit
     * @param integers
     * @return
     */
    private static List<char[]> convertToCharArrayList(List<Integer> integers) {
        List<char[]> results = new ArrayList<>();
	    for (int integer: integers) { // Loop through each integer
	        // Convert integer to char array and append result
            results.add(Integer.toString(integer).toCharArray());
        }
	    return results;
    }


    /**
     * Function to filter out numbers that are impossible to be the target
     * @param strikes
     * @param hits
     * @return
     */
	private static List<Integer> filterTargets (int strikes, int hits) {
        List<Integer> targets = new ArrayList<>();
        // Among remaining possible targets, choose those have the same result as the previous guess
        for (int target: potentialTargets) { // Loop through all potential targets
            // Assume that the opponent target is this target, get result when inputting the current guess
            Result result = processGuess(target, currentGuess);
            if (result.getStrikes() == strikes && result.getHits() == hits) { // If the result is the same
                // This target is a potential one
                targets.add(target);
            }
        }
        return targets;
    }

    /**
     * Function to
     * (0 strike, 0 hit) -> 0
     * (0 strike, 1 hit) -> 1
     * (0 strike, 2 hits) -> 2
     * (0 strike, 3 hits) -> 3
     * (0 strike, 4 hits) -> 4
     * (1 strike, 0 hit) -> 5
     * (1 strike, 1 hit) -> 6
     * (1 strike, 2 hits) -> 7
     * (1 strike, 3 hits) -> 8
     * (2 strikes, 0 hit) -> 9
     * (2 strikes, 1 hit) -> 10
     * (2 strikes, 2 hits) -> 11
     * (3 strikes, 0 hit) -> 12
     * (4 strikes, 0 hit) -> 13
     * @param result
     * @return The hash value for a specific pair of (strikes and hits)
     */
	private static int hash(Result result) {
	    if (result.getStrikes() == 3) return 12;
	    if (result.getStrikes() == 4) return 13;
	    int value = result.getStrikes() * 5 + result.getHits();
	    if (result.getStrikes() == 2) value -= 1;
	    return value;
    }

}