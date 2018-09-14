import React from "react"
import {
    CARD_SCORES,
    normalizeCard
} from "../../game/cards";

function VerticalLine(props)
{
    const { x, y, h, className, strokeDasharray } = props;

    return (
        <path
            className={ className }
            strokeDasharray={ strokeDasharray }
            d={
                "M" + x + " " + y +
                " L" + x + " " + (y + h)
            }
        />
    );
}


function reduceScore(a,b)
{
    return a + CARD_SCORES[normalizeCard(b) & 7];
}


class ScoreMeter extends React.Component {

    render()
    {
        const { current, containerWidth, containerHeight, defs } = this.props;

        const w = (containerWidth* 0.9)|0;
        const h = w / 40;

        const { bidding, trick } = current;
        const { currentPosition, declarerScore, oppositionScore, cards } = current.hand;

        const declarerWidth = (w * declarerScore/120)|0;
        const oppositionWidth = (w * oppositionScore/120)|0;

        //console.log("declarerScore", declarerScore, "oppositionScore", oppositionScore);

        const remaining = 120 - declarerScore - oppositionScore;

        const winPos = (w * 61 / 120)|0;
        const schneiderPos = (w * 90 / 120)|0;

        const hw = w/2;
        const hh = h/2;

        // center on winPos
        const xStart = 500 - winPos;
        const yStart = 50 - hh;

        const roundingRadius = w * 0.01;

        const isDeclarer = currentPosition === bidding.declarer;

        // if we're not the declarer, we need to estimate their points
        // what we have in hand or what is in the trick, the declarer can't have
        let scoreInHand;
        if (isDeclarer)
        {
            // doesn't matter, we know the score
            scoreInHand = 0;
        }
        else
        {
            // sum up score of hand and current trick
            scoreInHand = cards.reduce(reduceScore, 0) + trick.reduce(reduceScore, 0);
        }

        if (defs)
        {
            return (
                <clipPath
                    id="scoreMeterClip"
                >

                    <rect
                        className="score-meter"
                        x={ xStart }
                        y={ yStart }
                        width={ w }
                        height={ h }
                        rx={ roundingRadius }
                        ry={ roundingRadius }
                    />
                    
                </clipPath>
            );

        }

        return (
            <React.Fragment>
                <g
                    style={{
                        clipPath: "url(#scoreMeterClip)"
                    }}
                >

                    <rect
                        className="declarer"
                        x={ xStart }
                        y={ yStart }
                        width={ declarerWidth }
                        height={ h }
                    />
                    {
                        !isDeclarer && (
                            <rect
                                className="declarer-potential"
                                x={ xStart + declarerWidth}
                                y={ yStart }
                                width={ Math.min( xStart + declarerWidth + 22/120 * w, xStart + w - oppositionWidth - scoreInHand ) - (xStart + declarerWidth) }
                                height={ h }
                            />
                        )
                    }

                    <rect
                        className="opposition"
                        x={ xStart + w - oppositionWidth }
                        y={ yStart }
                        width={ oppositionWidth }
                        height={ h }
                    />
                    <rect
                        className="score-meter-fill"
                        x={ xStart }
                        y={ yStart }
                        width={ w }
                        height={ h }
                        rx={ roundingRadius }
                        ry={ roundingRadius }
                    />
                    <rect
                        className="score-meter"
                        x={ xStart }
                        y={ yStart }
                        width={ w }
                        height={ h }
                        rx={ roundingRadius }
                        ry={ roundingRadius }
                    />
                    <VerticalLine
                        className="win-line"
                        x={ xStart + winPos }
                        y={ yStart }
                        h={ h }

                    />
                    {
                        declarerWidth > winPos &&
                        <VerticalLine
                            className="schneider-line"
                            strokeDasharray="4 2"
                            x={ xStart + schneiderPos }
                            y={ yStart }
                            h={ h }

                        />
                    }
                </g>


            </React.Fragment>
        )
    }
}



export default ScoreMeter
