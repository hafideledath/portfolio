from helpers import *

import sys
from textwrap import dedent

import time

discard_pile = DiscardPile()
player = Player(discard_pile)
opponent = Player(discard_pile, player)

def main():
    print("\033[96mHello! Let's play a game of UNO.")
    time.sleep(2)

    player.successive_player = opponent

    next_player = user_turn()

    while True:
        if next_player == player:
            next_player = user_turn()
        else:
            next_player = opponent_turn()

def user_turn():
    print(opponent.deck_str(False), end="\n\n")
    print(str(discard_pile), end="\n\n")
    print(player.deck_str())

    options = []

    option_index = 1
    conversion_dict = {'0': None}

    for i, card in enumerate(player.card_set):
        if discard_pile.is_valid(card):
            options.append(f"{option_index}: \33[{card.color_ansi_index}m{card.value.value}\33[0m")
            conversion_dict[str(option_index)] = i
            option_index += 1

    card_index = get_card_index(options, conversion_dict)

    if card_index != None:
        card = player.card_set[card_index]
        card_name = (["Red", "Green", "Yellow", "Blue"][card.color.value - 1] + " " if card.color else "") + card.value.value
        next_player = player.play_card(card_index)

        card_set_length = len(player.card_set)
        if card_set_length == 1:
            print(f"\n\033[96mYou play a {card_name}. It's your penultimate card. UNO!")
        elif card_set_length == 0:
            game_over(True)
        else:
            print(f"\033[96mYou play a {card_name}.")

        return next_player
    player.add_card()
    print("\033[96mYou draw a card.")
    return opponent


def opponent_turn():
    for i, card in enumerate(opponent.card_set):
        if discard_pile.is_valid(card):
            next_player = opponent.play_card(i, False)
            card_name = (["Red", "Green", "Yellow", "Blue"][card.color.value - 1] + " " if card.color else "") + card.value.value
            print(f"\033[96mI play a {card_name}.")
            time.sleep(1.5)
            card_set_length = len(opponent.card_set)
            if card_set_length == 1:
                print("\033[96mI have played my penultimate card. UNO!")
                time.sleep(1.5)
            elif card_set_length == 0:
                game_over(False)
            return next_player
    opponent.add_card()
    print("\033[96mI draw a card.")
    time.sleep(1.5)
    return player


def get_card_index(options, conversion_dict):
    formatted_options = "\n\t".join(options)
    card_index = -1
    while not (card_index in [str(x) for x in range(len(options) + 1)]):
        card_index = input(dedent(
            f"""What would you like to do?
        0: Draw a card
        {formatted_options}
        """))
    return conversion_dict[card_index]


def get_card_index(options, conversion_dict):
    formatted_options = "\n\t".join(options)
    card_index = -1
    while not (card_index in [str(x) for x in range(len(options) + 1)]):
        card_index = input(dedent(f"""
        What would you like to do?
        0: Draw a card
        {formatted_options}
        """))
    return conversion_dict[card_index]

def game_over(user_won):
    if user_won:
        print("\033[92mGreat job! You won!")
        sys.exit()
    print("Your opponent won.")
    sys.exit()


if __name__ == "__main__":
    main()