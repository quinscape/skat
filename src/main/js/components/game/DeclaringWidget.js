import React from "react"
import cx from "classnames"
import Calculator from "../calculator/Calculator";
import Icon from "../Icon";
import { ButtonToolbar } from "reactstrap"
import { countDropped } from "../game/Game"

import { SuitName, CardName } from "../../util/cards";

function cardName(card)
{
    card = Math.abs(card) - 1;

    return CardName[card & 7] + " of " + SuitName[card >> 3];
}


function findDropped(cards, newSelection)
{
    const array = [];
    for (let i = 0; i < newSelection.length; i++)
    {
        if (!newSelection[i])
        {
            array.push(cards[i]);
        }
    }
    return array;
}

class DeclaringWidget extends React.Component {

    state = {
        gameValue: {
            result: -1
        }
    };

    changeGameValue = gameValue => {
        //console.log("GV-STATE", gameValue);
        this.setState({gameValue});
    };

    declareGame = () => {

        const { currentChannel, declareGame, cardsSelection } = this.props;
        const { gameValue } = this.state;

        const { current : { hand  } } = currentChannel;

        const skat = findDropped(hand.cards, cardsSelection);

        //console.log("SKAT", skat.map(cardName));

        if (skat.length === 2)
        {
            declareGame(currentChannel.id, gameValue, skat[0], skat[1]);
        }

    };

    render()
    {
        const { currentChannel, calculator, width, pickUpSkat, cardsSelection } = this.props;
        const { gameValue } = this.state;

        if (!currentChannel)
        {
            return false;
        }

        const { current } = currentChannel;

        const { bidding, hand } = current;

        // are we part of the current game?
        const doesParticipate = !!hand;

        const { phase } = current;

        const currentPosition = doesParticipate ? hand.currentPosition : -1;

        const isDeclaring = phase === "DECLARING" && currentPosition === bidding.declarer;

        const canToggleCards = isDeclaring && bidding.skatPickedUp;

        const dropped = canToggleCards ? countDropped(cardsSelection) : 2;

        const belowCurrent = gameValue.result > 0 && gameValue.result < bidding.currentBid;

        const declareError =
            belowCurrent ?
                "Selected game value must be higher than current bid of " + bidding.currentBid :
                dropped !== 2 ?
                    "You must drop exactly 2 cards" :
                    "";

        return (
            <div
                id="declaring-widget"
                className="pt-4 pb-4"
                style={{
                    left: width/2 - 400,
                    top: 90
                }}
            >
                <div className="container-fluid">
                    <div className="row">
                        <div className="col">
                            <h5>Declare Game</h5>
                            <hr className="p-0 mt-0 mb-3"/>
                            <Calculator
                                hand={ hand }
                                calculator={ calculator }
                                onSubmit={ this.changeGameValue }
                                multiplier={ false }
                            />
                            <p className={ cx(declareError && "text-danger") }>
                                {
                                    declareError || "\u00a0"
                                }
                            </p>
                            <ButtonToolbar>
                                <button
                                    type="button"
                                    className="btn btn-danger"
                                    onClick={ pickUpSkat }
                                >
                                    <Icon className="fa-eye"/>
                                    { " Pick up Skat" }
                                </button>
                                { "\u00a0" }
                                <button
                                    type="button"
                                    className="btn btn-success"
                                    disabled={ dropped !== 2 || gameValue.result < 0 || belowCurrent }
                                    onClick={ this.declareGame }
                                >
                                    <Icon
                                        className="fa-exclamation-circle"/>
                                    { " Declare Game" }
                                </button>

                            </ButtonToolbar>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default DeclaringWidget
