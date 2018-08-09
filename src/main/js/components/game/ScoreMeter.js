import React from "react"

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


class ScoreMeter extends React.Component {

    render()
    {
        const { current, containerWidth, containerHeight, defs} = this.props;

        const w = (containerWidth* 0.9)|0;
        const h = w / 40;

        const { declarerScore, oppositionScore } = current.hand;

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

        if (defs)
        {
            return (
                <clipPath
                    id="scoreMeterClip"
                >
                    <rect
                        className="score-meter"
                        x={xStart}
                        y={yStart}
                        width={w}
                        height={h}
                        rx={roundingRadius}
                        ry={roundingRadius}
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
