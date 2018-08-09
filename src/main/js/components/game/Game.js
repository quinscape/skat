import React from "react"
import raf from "raf"
import cx from "classnames"
import { connect } from "react-redux"
import GameContainer from "./GameContainer";
import Card from "../Card";
import {
    getCalculatorData,
    getCurrentChannel,
    getUserConfig
} from "../../game/reducers/index";
import {
    joinGame,
    storeCalculatorSettings,
    storeUserConfig,
    pickUpSkat,
    declareGame,
    playCard
} from "../../game/actions/index";
import GameTable from "./GameTable";
import ScoreMeter from "./ScoreMeter";
import {
    ButtonToolbar,
    Modal,
    ModalBody
} from "reactstrap"

import Icon from "../Icon";
import Calculator, { GameType } from "../calculator/Calculator";
import SeatingTable from "./SeatingTable";
import ChatWidget from "./ChatWidget";
import Sidebar from "./Sidebar";
import UserToolbar from "./UserToolbar";
import { GOLDEN_RATIO } from "../calculator/SymbolButton";
import DeclaringWidget from "./DeclaringWidget";
import GameCards from "./GameCards";
import Vector from "../../util/vector";


const EMPTY_SEATING = [];

export const ALL_LEGAL = [
    true, true, true, true,
    true, true, true, true,
    true, true, true, true
];

const NONE_LEGAL = [
    false, false, false, false,
    false, false, false, false,
    false, false, false, false
];


export function countDropped(newSelection)
{
    if (!newSelection)
    {
        return 2;
    }
    
    let count = 0;
    for (let i = 0; i < newSelection.length; i++)
    {
        if (!newSelection[i])
        {
            count++;
        }
    }

    return count;
}

function matchTrump(trump)
{
    return function(c)
    {
        c = (Math.abs(c)-1);

        if ((c & 7) === 7)
        {
            return true;
        }
        return ( 3 - (c >> 3)) === trump;
    }
}

function matchSuit(suit, matchJacks)
{
    return function(c)
    {
        c = (Math.abs(c)-1);
        return ( 3 - (c >> 3)) === suit && (matchJacks || (c & 7) !== 7);
    }
}

function isJack(c)
{
    c = (Math.abs(c)-1);
    return ((c & 7) === 7);
}


class Game extends React.Component {

    state = {
        showCalculator: false,
        cardsSelection: null
    };

    static getDerivedStateFromProps(props, state)
    {
        const { currentChannel } = props;
        if (!currentChannel)
        {
            return null;
        }

        const { current  } = currentChannel;

        if (state.cardsSelection && current.phase !== "DECLARING")
        {
            return {
                cardsSelection: null
            };
        }

        const { hand } = current;

        //console.log("getDerivedStateFromProps: hand = ", hand, state);


        if (hand && hand.skat.length && !state.cardsSelection)
        {
            // do we have received a face-down skat?
            if (hand.skat.indexOf(33) < 0)
            {
                // our two skat cards that were mixed into the hand
                const skatA = hand.skat[0];
                const skatB = hand.skat[1];

                const selection = hand.cards.map(c => !(c === skatA || c === skatB));

                //console.log("getDerivedStateFromProps, cardsSelection = ", selection, hand);

                return {
                    cardsSelection: selection
                }
            }
        }

        return null;
    }

    componentDidMount()
    {
        const { match, joinGame, currentChannel } = this.props;

        if (!currentChannel)
        {
            joinGame(match.params.id);
        }
    }

    toggleCalc = () => this.setState({ showCalculator: !this.state.showCalculator });

    toggleCard = id =>this.setState( (prevState, props) => {
    
        const index = +id;

        const newSelection = prevState.cardsSelection.slice();


        // mark the given index as false
        newSelection[index] = !newSelection[index];

        let dropped = countDropped(newSelection);
        while (dropped > 2)
        {
            for (let i = 0; i < newSelection.length; i++)
            {
                if (i !== index && !newSelection[i])
                {
                    newSelection[i] = true;
                    dropped--;
                    break;
                }
            }
        }

        return {
            ... prevState,
            cardsSelection: newSelection
        }

    });

    storeCalculatorSettings = settings => {
        const { storeCalculatorSettings } = this.props;
        storeCalculatorSettings(settings);
    };

    toggleLockBidding = () => {

        const { userConfig, storeUserConfig } = this.props;

        storeUserConfig({
            ... userConfig,
            lockBidding: !userConfig.lockBidding
        });

    };


    pickUpSkat = () => {

        const { currentChannel, pickUpSkat } = this.props;

        pickUpSkat(currentChannel.id);

    };

    playCard = id => {

        const { currentChannel, playCard } = this.props;

        const { current : { hand }} = currentChannel;

        const index = +id;

        playCard(currentChannel.id,  hand.cards[index]);

    };

    getLegalPlays = () =>
    {
        const { currentChannel : { current } } = this.props;

        const { gameDeclaration: { gameType }, trick, hand } = current;
        if (trick.length === 0 || trick.length === 3)
        {
            return ALL_LEGAL;
        }

        if (!hand)
        {
            return NONE_LEGAL;
        }

        const norm = Math.abs(trick[0]) - 1;
        const suit = 3 - (norm >> 3);

        let legal;

        const firstIsJack = (norm & 7) === 7;
        if (gameType <= 3)
        {
            const trump = gameType;

            if (suit === trump || firstIsJack)
            {
                legal = hand.cards.map( matchTrump(trump) )
            }
            else
            {
                legal = hand.cards.map( matchSuit(suit, false) )
            }
        }
        else if (gameType === GameType.NULL || gameType === GameType.RAMSCH)
        {
            legal = hand.cards.map( matchSuit(suit, true) )
        }
        else if (gameType === GameType.GRAND)
        {
            if (firstIsJack)
            {
                legal = hand.cards.map( isJack )
            }
            else
            {
                legal = hand.cards.map( matchSuit(suit, false) )
            }
        }

        // if there are no legal cards, the player does no longer have to follow suit and can play all cards
        return legal.indexOf(true) < 0 ? ALL_LEGAL : legal;
    }

    render()
    {
        const { match, currentChannel, userConfig, calculator, declareGame } = this.props;
        const { showCalculator, cardsSelection } = this.state;


        if (!currentChannel)
        {
            return false;
        }

        const { current } = currentChannel;

        const { bidding, hand, phase, trick, nextPlayer } = current;


        const isBiddingTurn = bidding.bidTurn;

        // are we part of the current game?
        const doesParticipate = !!hand;

        const currentPosition = doesParticipate ? hand.currentPosition : -1;

        const isDeclaring = phase === "DECLARING" && currentPosition === bidding.declarer;

        const isPlaying = phase === "PLAYING" && currentPosition === nextPlayer;

        const canToggleCards = isDeclaring && bidding.skatPickedUp;

        //console.log("isPlaying", isPlaying, "nextPlayer", nextPlayer);

        const cardInteraction =
            canToggleCards ?
                this.toggleCard :
                    isPlaying ? this.playCard :
                        null;


        let legalPlays = ALL_LEGAL;
        if (phase === "PLAYING" && currentPosition === current.nextPlayer )
        {
            legalPlays = this.getLegalPlays();
            //console.log({legalPlays});
        }

        return (

            <React.Fragment>
                <GameContainer>
                    <GameContainer.Consumer>
                        {
                            ({ width, height }) => {

                                const numCards = hand.cards.length;

                                let scale, effectiveWidth;
                                if (width > height)
                                {
                                    scale = (1000 / height);

                                    effectiveWidth = (width - 100) * scale;
                                }
                                else
                                {
                                    scale = (1000 / width);
                                    effectiveWidth = (width - 10) * scale;
                                }

                                const cardWidth = effectiveWidth / (numCards > 10 ? 12 : 10);


                                return (
                                    <React.Fragment>
                                        <div
                                            id="game-main"
                                            className="col-md-9"
                                        >
                                            <svg
                                                width={ width }
                                                height={ height - 80 }
                                                viewBox="0 0 1000 1000"
                                                preserveAspectRatio="xMidYMid meet"
                                            >
                                                <defs>
                                                    <ScoreMeter
                                                        defs={ true }
                                                        current={ current }
                                                        containerWidth={ effectiveWidth }
                                                        containerHeight={ height }
                                                    />
                                                </defs>

                                                <GameTable
                                                    seating={ current.seating || EMPTY_SEATING }
                                                    numberOfSeats={ current.numberOfSeats }
                                                    current={ doesParticipate ? hand.gameUser.name : null }
                                                />
                                                <ScoreMeter
                                                    current={ current }
                                                    containerWidth={ effectiveWidth }
                                                    containerHeight={ height }
                                                />
                                                <GameCards
                                                    phase={ phase }
                                                    currentChannel={ currentChannel }
                                                    width={ width }
                                                    height={ height }
                                                    cardsSelection={ cardsSelection }
                                                    scale={ scale }
                                                    cardWidth={ cardWidth }
                                                    playCard={ this.playCard }
                                                    toggleCard={ this.toggleCard }
                                                    getLegalPlays={ this.getLegalPlays }
                                                />
                                                {
                                                    phase === "PLAYING" && (
                                                        <React.Fragment>
                                                            <Card
                                                                id="m1"
                                                                x={ 500 - cardWidth * 2 }
                                                                y={ 500 - cardWidth * 0.8 }
                                                                width={ cardWidth * 1.2 }
                                                                type="marker"
                                                                card={ 1 }
                                                                opacity={ 0.4 }
                                                            />
                                                            {
                                                                trick.length > 0 &&
                                                                <Card
                                                                    id="mc1"
                                                                    x={ 500 - cardWidth * 1.9 }
                                                                    y={ 500 - cardWidth * 0.7 }
                                                                    width={ cardWidth }
                                                                    card={ trick[0] }
                                                                />
                                                            }
                                                            <Card
                                                                id="m2"
                                                                x={ 500 - cardWidth * 0.6 }
                                                                y={ 500 - cardWidth * 0.8 }
                                                                width={ cardWidth * 1.2 }
                                                                type="marker"
                                                                card={ 1 }
                                                                opacity={ 0.4 }
                                                            />
                                                            {
                                                                trick.length > 1 &&
                                                                <Card
                                                                    id="mc1"
                                                                    x={ 500 - cardWidth * 0.5 }
                                                                    y={ 500 - cardWidth * 0.7 }
                                                                    width={ cardWidth  }
                                                                    card={ trick[1] }
                                                                />
                                                            }
                                                            <Card
                                                                id="m3"
                                                                x={ 500 + cardWidth * 0.8 }
                                                                y={ 500 - cardWidth * 0.8 }
                                                                width={ cardWidth * 1.2 }
                                                                type="marker"
                                                                card={ 1 }
                                                                opacity={ 0.4 }
                                                            />
                                                            {
                                                                trick.length > 2 &&
                                                                <Card
                                                                    id="mc3"
                                                                    x={ 500 + cardWidth * 0.9 }
                                                                    y={ 500 - cardWidth * 0.7 }
                                                                    width={ cardWidth  }
                                                                    card={ trick[2] }
                                                                />
                                                            }
                                                        </React.Fragment>
                                                    )
                                                }
                                                {
                                                    isPlaying &&
                                                        <text
                                                            x={ 500 }
                                                            y={ 250 }
                                                            textAnchor="middle"
                                                            fontSize={ 50 }
                                                        >
                                                            Pick Card to play
                                                        </text>
                                                }
                                            </svg>
                                        </div>
                                        {
                                            isDeclaring &&
                                                <DeclaringWidget
                                                    {... this.props }
                                                    width={ width }
                                                    height={ height }
                                                    pickUpSkat={ this.pickUpSkat }
                                                    declareGame={ declareGame }
                                                    cardsSelection={ cardsSelection }

                                                />
                                        }
                                        <Sidebar>
                                            <h3>
                                                {
                                                    "Game '" + match.params.id + "' (" + phase + ")"
                                                }
                                            </h3>
                                            <SeatingTable
                                                currentChannel={ currentChannel }
                                                currentUser={ doesParticipate && hand.gameUser.name }
                                            />

                                            <ButtonToolbar>

                                                <button
                                                    type="button"
                                                    disabled={ isDeclaring }
                                                    className={
                                                        cx(
                                                            "btn btn-success",
                                                            showCalculator && "active"
                                                        )
                                                    }
                                                    aria-label={
                                                        "Toggle calculator"
                                                    }
                                                    onClick={
                                                        this.toggleCalc
                                                    }
                                                >
                                                    <Icon
                                                        className="fa-calculator"
                                                    />
                                                </button>
                                                {
                                                    "\u00a0"
                                                }
                                                <UserToolbar/>

                                            </ButtonToolbar>

                                            <ChatWidget/>
                                            
                                        </Sidebar>
                                        <Modal
                                            isOpen={ showCalculator && !isDeclaring }
                                            toggle={ this.toggleCalc }
                                            modalTransition={ { timeout: 75 }}
                                            size="lg"
                                            backdropClassName="calc-backdrop"
                                        >
                                            <ModalBody>
                                                <Calculator
                                                    hand={ hand }
                                                    calculator={ calculator }
                                                    onSubmit={ this.storeCalculatorSettings }
                                                />
                                                <ButtonToolbar>
                                                    <button
                                                        type="button"
                                                        className={
                                                            cx(
                                                                "btn",
                                                                userConfig.lockBidding ? "active btn-success" : "btn-danger"
                                                            )
                                                        }
                                                        title={ userConfig.lockBidding ?
                                                            "Click to allow overbidding calculated game value" :
                                                            "Click to limit bids to calculated game value"
                                                        }
                                                        onClick={ this.toggleLockBidding }
                                                    >
                                                        <Icon
                                                            className={
                                                                userConfig.lockBidding ? "fa-lock" : "fa-lock-open"
                                                            }
                                                        />
                                                        {
                                                            userConfig.lockBidding ? " Unlock" : " Lock"
                                                        }
                                                    </button>
                                                </ButtonToolbar>
                                            </ModalBody>
                                        </Modal>
                                    </React.Fragment>
                                );
                            }
                        }
                    </GameContainer.Consumer>
                </GameContainer>
            </React.Fragment>
        )
    }

}

const mapStateToProps = state => {
    return {
        currentChannel: getCurrentChannel(state),
        userConfig: getUserConfig(state),
        calculator: getCalculatorData(state),
    }
};

const mapDispatchToProps = ({
    joinGame,
    storeCalculatorSettings,
    storeUserConfig,
    pickUpSkat,
    declareGame,
    playCard
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Game)
