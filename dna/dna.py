# Identify a person based on their DNA

# Import libraries
import csv
import sys


def main():

    # If the user provides the wrong number of arguments, give them a usage reminder
    if len(sys.argv) != 3:
        sys.exit("Usage: python dna.py DATABASE SEQUENCE")

    # Open the files based on the user's arguments
    database_file = open(sys.argv[1], "r")
    sequence_file = open(sys.argv[2], "r")

    # Initialize an array to keep track of each person
    people = []

    # Initialize an array to keep track of each STR
    subsequences = []

    # Append each person from database_file to the people array
    for row in csv.DictReader(database_file):
        people.append(row)

    # Define a variable to represent the DNA sequence
    sequence = sequence_file.readline()

    # Close the database and sequence files
    database_file.close()
    sequence_file.close()

    # Define a variable for each STR. Referenced [https://www.w3schools.com/python/ref_dictionary_keys.asp] to understand the keys() function
    STRs = list(people[0].keys())[1:]

    # For every STR, append a tuple containing the subsequence string and the length of the longest consecutive sequence of said STR
    for subsequence in STRs:
        longest_sequence = longest_match(sequence, subsequence)
        subsequences.append((subsequence, longest_sequence))

    # Define a variable to represent who's DNA the sequence matches. It is defaulted to "No match"
    matched = "No match"

    # For each person, check if their STR counts match the sequence's STR counts
    for person in people:
        # Initialize a variable to keep track of how many matches exist between the person and sequence
        total = 0
        # For each subsequence
        for subsequence, longest_sequence in subsequences:
            # If the person's STR count matches the sequence's STR count, increment the total
            if int(person[subsequence]) == longest_sequence:
                total += 1
        # If the number of matches is equal to the number of subsequences, set matched to the person's name
        if total == len(subsequences):
            matched = person["name"]

    # Print the matched person's name. If nobody was matched, the program will print "No match" by default
    print(matched)


def longest_match(sequence, subsequence):
    """Returns length of longest run of subsequence in sequence."""

    # Initialize variables
    longest_run = 0
    subsequence_length = len(subsequence)
    sequence_length = len(sequence)

    # Check each character in sequence for most consecutive runs of subsequence
    for i in range(sequence_length):

        # Initialize count of consecutive runs
        count = 0

        # Check for a subsequence match in a "substring" (a subset of characters) within sequence
        # If a match, move substring to next potential match in sequence
        # Continue moving substring and checking for matches until out of consecutive matches
        while True:

            # Adjust substring start and end
            start = i + count * subsequence_length
            end = start + subsequence_length

            # If there is a match in the substring
            if sequence[start:end] == subsequence:
                count += 1

            # If there is no match in the substring
            else:
                break

        # Update most consecutive matches found
        longest_run = max(longest_run, count)

    # After checking for runs at each character in seqeuence, return longest run found
    return longest_run


main()
