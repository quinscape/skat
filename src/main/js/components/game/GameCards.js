import React from "react"
import raf from "raf"
import cx from "classnames"
import { GOLDEN_RATIO } from "../calculator/SymbolButton";
import Card from "../Card";
import { ALL_LEGAL } from "./Game";
import { easeOutCubic } from "../../util/easing";
import Vector from "../../util/vector";
import quadraticBezier from "../../util/quadraticBezier";


const ANIMATION_TIME = 750;
const ANIMATION_START_X = -300;
const ANIMATION_START_Y = -300;

const TIME_PER_CARD = 220;
const TIME_PER_CARD_OFF_SCREEN = 40;

const GROUP2_OFFSET = TIME_PER_CARD_OFF_SCREEN * 8;
const GROUP3_OFFSET = GROUP2_OFFSET + TIME_PER_CARD_OFF_SCREEN * 8;

const CARD_DELAYS = [
    0,
    TIME_PER_CARD,
    TIME_PER_CARD * 2,

    TIME_PER_CARD * 3 + GROUP2_OFFSET,
    TIME_PER_CARD * 4 + GROUP2_OFFSET,
    TIME_PER_CARD * 5 + GROUP2_OFFSET,
    TIME_PER_CARD * 6 + GROUP2_OFFSET,

    TIME_PER_CARD * 7 + GROUP3_OFFSET,
    TIME_PER_CARD * 8 + GROUP3_OFFSET,
    TIME_PER_CARD * 9 + GROUP3_OFFSET
];

const ANIMATION_END_OFFSET = CARD_DELAYS[CARD_DELAYS.length - 1] + ANIMATION_TIME;

console.log({ANIMATION_END_OFFSET});

/**
 * We're going to animate the cards along eased bezier paths, all with a common start point at the top, a random
 * control point created here and the final destination.
 */
function createAnimationControlPoints()
{

    const pt0 = new Vector(
        (ANIMATION_START_X) * 0.5 - 400 + Math.random() * 400,
        (1000 + ANIMATION_START_Y) * 0.3
    );

    const pt1 = new Vector(
        (300 + ANIMATION_START_X) * 0.5 - 200 + Math.random() * 400,
        (1000 + ANIMATION_START_Y) * 0.7
    );

    const pt2 = new Vector(
        (700 + ANIMATION_START_X) * 0.5 + 400 + Math.random() * 400,
        (1000 + ANIMATION_START_Y) * 0.3
    );


    return [
        pt0,
        pt0,
        pt0,

        pt1,
        pt1,
        pt1,
        pt1,

        pt2,
        pt2,
        pt2,

        pt2,
        pt2
    ];
}

const vCurrent = new Vector(0,0);

function getInitialAnimationState()
{
    const start = Date.now() + 300;
    return {
        start,
        controlPoints: createAnimationControlPoints(),
        done: false
    }
}

/**
 * Renders and animates the current users hand if they are participating in the match.
 * 
 */
class GameCards extends React.Component {

    state = {
        animation: null
    };

    static getDerivedStateFromProps(props, state)
    {
        const { phase } = props;

        if (state.animation && phase === "OPEN")
        {
            return {
                animation: null
            };
        }

        if (!state.animation)
        {
            // initialize our animation data structure
            return {
                animation: getInitialAnimationState()
            }
        }

        return null;
    }

    animationUpdate = () => this.forceUpdate();

    componentDidUpdate()
    {
        const { animation } = this.state;

        if (animation && !animation.done)
        {
            const now = Date.now();

            if (now - animation.start > ANIMATION_END_OFFSET)
            {
                this.setState({
                    animation: {
                        ... animation.start,
                        done: true
                    }
                })
            }
            else
            {
                raf(this.animationUpdate)
            }
        }
    }

    render()
    {
        const { currentChannel, width, height, cardsSelection, scale, cardWidth, toggleCard, playCard, getLegalPlays } = this.props;
        const { animation } = this.state;

        if (!currentChannel)
        {
            return false;
        }

        const { current } = currentChannel;

        const { bidding, hand, phase, nextPlayer } = current;

        // are we part of the current game?
        const doesParticipate = !!hand;

        const currentPosition = doesParticipate ? hand.currentPosition : -1;

        const isDeclaring = phase === "DECLARING" && currentPosition === bidding.declarer;

        const isPlaying = phase === "PLAYING" && currentPosition === nextPlayer;

        const canToggleCards = isDeclaring && bidding.skatPickedUp;

        const cardInteraction =
            canToggleCards ?
                toggleCard :
                isPlaying ? playCard :
                    null;

        let legalPlays = ALL_LEGAL;
        if (phase === "PLAYING" && currentPosition === current.nextPlayer)
        {
            legalPlays = getLegalPlays();
            //console.log({ legalPlays });
        }


        let xStart;
        if (width > height)
        {
            xStart = -((width - 100 - height) / 2 * scale);
        }
        else
        {
            xStart = 5 * scale;
        }

        const now = Date.now();

        const inAnimationPhase = !!animation && !animation.done;
        const controlPoints = inAnimationPhase && animation.controlPoints;


        return (
            <React.Fragment>
                {
                    phase !== "OPEN" && doesParticipate && hand.cards.map((card, index) => {
                            const isDropped = phase === "DECLARING" && cardsSelection && !cardsSelection[index];

                            let cardX = xStart + index * cardWidth;
                            let cardY = (1000 - cardWidth / 0.6) - (isDropped ? cardWidth / GOLDEN_RATIO : 0);

                            if (inAnimationPhase)
                            {
                                const controlPoint = controlPoints[index];
                                
                                const t = easeOutCubic(
                                    Math.max(
                                        0,
                                        Math.min(
                                            1,
                                            (now - animation.start - CARD_DELAYS[index]) / ANIMATION_TIME
                                        )
                                    )
                                );

                                quadraticBezier(
                                    ANIMATION_START_X,
                                    ANIMATION_START_Y,
                                    controlPoint.x,
                                    controlPoint.y,
                                    cardX,
                                    cardY,
                                    t,
                                    vCurrent
                                );

                                cardX = vCurrent.x;
                                cardY = vCurrent.y;
                            }

                            return (
                                <Card
                                    key={ index }
                                    id={ index }
                                    x={ cardX }
                                    y={ cardY }
                                    className={
                                        cx(!legalPlays[index] && "disabled")
                                    }
                                    width={ cardWidth }
                                    card={ card }
                                    onClick={ !isPlaying || legalPlays[index] ? cardInteraction : null }
                                    opacity={
                                        isDropped ?
                                            0.5 :
                                            legalPlays[index] ?
                                                1 :
                                                0.5
                                    }
                                />
                            );
                        }
                    )
                }
                {
                    <text
                        x={ 0 }
                        y={ 20 }
                        fontSize={ 20}
                        textAnchor="left"
                        style={{
                            fill: "#ffffff",
                            cursor: "pointer"
                        }}
                        onClick={ () => this.setState({ animation: getInitialAnimationState() })}
                    >Repeat Anim</text>
                }
            </React.Fragment>
        )
    }
}

export default GameCards
