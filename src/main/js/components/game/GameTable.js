import React from "react"
import cx from "classnames"

import { Consumer } from "./GameContainer"
import PositionNames from "./PositionNames";


const TOP_PADDING = 120;

const width = 1000 - TOP_PADDING, height= 1000 - TOP_PADDING;

const centerX = (TOP_PADDING/2 + (width / 2) | 0);
const centerY = (TOP_PADDING + height / 2) | 0;
const tw = (width - 150) & ~1;
const th = (height - 150) & ~1;

const OFFSETS = [
    {
        circleX: centerX,
        circleY: centerY + th / 2,
        textX: centerX,
        textY: centerY + th / 2 + 14
    }, {
        circleX: centerX + tw / 2,
        circleY: centerY,
        textX: centerX + tw / 2,
        textY: centerY + 14
    },
    {

        circleX: centerX,
        circleY: centerY - th / 2,
        textX: centerX,
        textY: centerY - th / 2 + 14
    }, {

        circleX: centerX - tw / 2,
        circleY: centerY,
        textX: centerX - tw / 2,
        textY: centerY + 14
    }
];

function findCurrentIndex(seating, current)
{
    for (let i = 0; i < seating.length; i++)
    {
        const s = seating[i];
        if (s.name === current)
        {
            return i;
        }
    }
    throw new Error("Current seat '" + current + "' not found");
}

class GameTable extends React.Component {

    state = {
        height: 0
    };

    render()
    {
        const {
            seating,
            current,
            // number of allowed seats per game rule
            numberOfSeats
        } = this.props;


        // index of the current user in occupied
        const offset = current === null ? 0 : findCurrentIndex(seating, current);

        const { bidding } = current;

        const seats = new Array(numberOfSeats);


        for (let i=0; i < numberOfSeats; i++)
        {
            const off = OFFSETS[(i - offset) & 3];

            const name = seating[i] ? seating[i].name : null;
            seats[i] = (
                <React.Fragment key={i}>
                    <circle
                        className={
                            cx(
                                "seat",
                                bidding && bidding.declarer === PositionNames[i] && "current",
                                name === null || !seating[i].active ? "empty" : null
                            )
                        }
                        cx={off.circleX}
                        cy={off.circleY}
                        r={ 100 }
                        height={ 100 }
                    />
                    <text
                        x={off.textX}
                        y={off.textY}
                        textAnchor="middle"
                        fontSize={44}
                    >
                        {
                            name || "Seat " + (i + 1)
                        }
                    </text>

                </React.Fragment>
            );
        }

        //console.log("SEATING", seating);

        return (
            <React.Fragment>
                <rect className="table"
                      x={centerX - tw / 2}
                      y={centerY - th / 2}
                      rx={ 40 }
                      ry={ 40 }
                      width={tw}
                      height={th}
                />
                {
                    seats
                }
            </React.Fragment>
        );
    }
}

export default GameTable
