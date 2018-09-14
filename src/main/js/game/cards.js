import media from "./media";


export const Suite = {
    CLUBS: 0,
    SPADES: 1,
    HEARTS: 2,
    DIAMONDS: 3,
};

export const CARD = {
    SEVEN: 0,
    EIGHT: 1,
    NINE: 2,
    QUEEN: 3,
    KING: 4,
    TEN: 5,
    ACE: 6,
    JACK: 7
};


export const CARD_SCORES = [0,0,0,3,4,10,11,2];

export function cardIndex(suite, card)
{

    if ( suite < 0 || suite > 3 || typeof suite !== "number")
    {
        throw new Error("Invalid suite: " + suite);
    }
    if ( card < 0 || card > 7|| typeof card !== "number")
    {
        throw new Error("Invalid card: " + card);
    }

    return suite * 8 + card;
}


export function cardURI(suite, card)
{
    return media.CARDS[cardIndex(suite, card)];
}

export function normalizeCard(card)
{
    return Math.abs(card) - 1;
}
