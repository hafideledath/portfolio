from enum import Enum
from math import floor
import numpy as np
from textwrap import dedent


class CardColor(Enum):
    """Enum list of card colors"""
    RED = 1
    GREEN = 2
    YELLOW = 3
    BLUE = 4

    @classmethod
    def random_color(cls):
        return np.random.choice(list(CardColor))


class CardValue(Enum):
    """Enum list of card values"""
    ONE = "One"
    TWO = "Two"
    THREE = "Three"
    FOUR = "Four"
    FIVE = "Five"
    SIX = "Six"
    SEVEN = "Seven"
    EIGHT = "Eight"
    NINE = "Nine"
    ZERO = "Zero"
    SKIP = "Skip"
    REVERSE = "Reverse"
    PLUS_TWO = "+ Two"
    COLOR_CHANGE = "Wildcard"
    PLUS_FOUR = "+ Four"

    @classmethod
    def random_value(cls):
        return np.random.choice(list(CardValue), p=[i/27 for i in [2] * 9 + [1] + [2] * 3 + [1, 1]])

    @property
    def is_wild(self):
        return self in [CardValue.COLOR_CHANGE, CardValue.PLUS_FOUR]


class Card:
    """Class for each card, with a color and value which defaults to random"""
    def __init__(self, value=None, color=None):
        if value:
            self._value = value
            self._color = color
        else:
            self.randomize_card

    def card_str(self, front_facing=True, size=8):
        row_space_count = floor((0.35 * size) + 0.7) - 1
        rows = "\n".join([f"│{' ' * size}│"] * row_space_count)
        if front_facing:
            result = f"""
┌{"─" * size}┐
{rows}
│{self.value.value:^{size}}│
{rows}
└{"─" * size}┘""".strip().split("\n")

            for i, line in enumerate(result):
                result[i] = f"\33[30;{self.color_ansi_index + 10}m" + line + "\33[0m"
            return "\n".join(result)
        else:
            return dedent("""
            \33[0;40m┏━━━━━━━━━┓\33[0m
            \33[0;40m┃\33[30;40m█████████\33[0m┃
            \33[0;40m┃\33[30;40m█████████\33[0m┃
            \33[0;40m┃\33[30;40m██\33[31m▄████▄\33[30m█\33[0m┃
            \33[0;40m┃\33[30;40m█\33[31m██\33[1;3;33;41mUNO\33[0;31;40m██\33[30m█\33[0m┃
            \33[0;40m┃\33[30;40m█\33[31m▀████▀\33[30m██\33[0m┃
            \33[0;40m┃\33[30;40m█████████\33[0m┃
            \33[0;40m┃\33[30;40m█████████\33[0m┃
            \33[0;40m┗━━━━━━━━━┛\33[0m""")

    @property
    def randomize_card(self):
        value = CardValue.random_value()
        if value.is_wild:
            self._value, self._color = (value, None)
        else:
            self._value, self._color = (value, CardColor.random_color())

    @property
    def value(self):
        return self._value

    @property
    def color(self):
        return self._color

    @property
    def color_ansi_index(self):
        return self.color.value + 90 if self.color else 90

    @color.setter
    def color(self, color):
        if self.value.is_wild:
            self._color = color


class Deck:
    """Class to represent a deck of cards"""
    def __init__(self, card_count):
        self._card_set = self.generate_set(card_count)

    def deck_str(self, front_facing=True, height=10):
        result = [""] * height
        for card in self.card_set:
            for i, line in enumerate(card.card_str(front_facing).split("\n")):
                result[i] += line + ("  " if front_facing else " ")
        return "\n".join(result)

    def generate_set(self, card_count):
        card_set = []
        for i in range(card_count):
            card_set.append(Card())
        return card_set

    @property
    def card_set(self):
        return self._card_set

    def add_card(self, card=None):
        if card:
            self._card_set.append(card)
        else:
            self._card_set.append(Card())


class DiscardPile(Deck):
    """Class to represent a discard pile"""
    def __init__(self):
        super().__init__(1)

    def __repr__(self):
        return self.top_card.card_str(size=8)

    @property
    def top_card(self):
        return self.card_set[-1]

    def add_card(self, card, new_color=None):
        if self.is_valid(card) and not card.value.is_wild:
            super().add_card(card)
        elif card.value.is_wild:
            card.color = new_color
            super().add_card(card)
        else:
            raise ValueError("Invalid Card.")

    def is_valid(self, card):
        return self.top_card.color == None or card.color in [self.top_card.color, None] or self.top_card.value == card.value


class Player(Deck):
    """Class to represent a player"""
    def __init__(self, discard_pile, successive_player=None):
        super().__init__(7)
        self._discard_pile = discard_pile
        self._successive_player = successive_player

    # Returns whose turn it is
    def play_card(self, index, is_user=True):
        card = self.card_set[index]

        if card.value.is_wild:
            if is_user:
                self._discard_pile.add_card(card, self.get_color())
            else:
                self._discard_pile.add_card(card, CardColor.random_color())
        else:
            self._discard_pile.add_card(card)
        self._card_set.pop(index)

        if card.value in [CardValue.PLUS_TWO, CardValue.PLUS_FOUR]:
            count = 2
            if (card.value == CardValue.PLUS_FOUR):
                count = 4
            for _ in range(count):
                self._successive_player.add_card(Card())
            return self
        elif card.value in [CardValue.REVERSE, CardValue.SKIP]:
            return self
        return self._successive_player

    @property
    def successive_player(self):
        return self._successive_player

    @successive_player.setter
    def successive_player(self, successive_player):
        self._successive_player = successive_player

    def get_color(self):
        color_char = ""
        while (not color_char in list("RGYB")):
            color_char = input(dedent("""
            What color would you like to set the deck to?
                Red: 'R'
                Green: 'G'
                Yellow: 'Y'
                Blue: 'B'
            """)).upper()
        return list(CardColor)["RGYB".index(color_char)]