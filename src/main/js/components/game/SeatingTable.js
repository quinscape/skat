import React from "react"
import cx from "classnames"
import Icon from "../Icon";
import Calculator from "../calculator/Calculator";

import PositionNames from "./PositionNames";



const Position = [
    "DEAL",
    "RESPOND",
    "BID",
    "CONTINUE"
];


class SeatingTable extends React.Component {

    render()
    {
        const { currentChannel, currentUser } = this.props;

        if (!currentChannel)
        {
            return false;
        }


        const { current, users } = currentChannel;
        const { seating, numberOfSeats, currentDealer, hand, bidding, phase, gameDeclaration } = current;
        const participate = !!hand;

        const seats = [];

        const seated = {  };

        let allSeatsOccupied = true;

        for (let i = 0; i < numberOfSeats; i++)
        {
            const pos = Position[i + currentDealer % numberOfSeats];
            let name, isActive;
            const seatOccupied = i < seating.length;
            if (seatOccupied)
            {
                name = seating[i].name;
                seated[name] = true;
                isActive = seating[i].active;
            }
            else
            {
                name = "---";
                isActive = false;
                allSeatsOccupied = false;
            }

            let icon, className;

            const isDeclarer = PositionNames[i] === bidding.declarer;
            if  (isDeclarer)
            {
                icon = phase === "DECLARING" ? "fa-comment-dots" : "fa-comment";
                className="text-info"
            }
            else if  (PositionNames[i] === bidding.bidder)
            {
                icon = "fa-angle-double-up";
                className="text-success"
            }
            else if  (PositionNames[i] === bidding.responder)
            {
                icon = "fa-thumbs-up";
                className="text-success"
            }

            seats.push(
                <tr
                    key={ "s" + i }
                    className={
                        cx(
                            "seat",
                            !isActive && "text-muted",
                            currentUser === name && "current"
                        )
                    }
                >
                    <td>
                        <Icon className="fa-couch"/>
                        <span className={ className }>
                            {
                                participate && " " + name + "\u00a0\u00a0"
                            }
                            {
                                icon && <Icon className={ icon }/>
                            }
                            {
                                isDeclarer && phase === "PLAYING" && "\u00a0" + Calculator.getGameValue(gameDeclaration, true).name
                            }
                        </span>
                    </td>
                    <td>
                        <div className="fa-pull-right">
                            {
                                !seatOccupied && <button
                                    type="button"
                                    className="btn btn-link"
                                >
                                    <Icon className="fa-angle-double-down"/>
                                    { " Sit" }
                                </button>
                            }

                        </div>
                    </td>
                </tr>
            )
        }

        for (let i = 0; i < users.length; i++)
        {
            const user = users[i];
            if (!seated[user.name])
            {
                seats.push(
                    <tr
                        key={ i }
                        className={
                            cx(!user.active && "text-muted")
                        }
                    >
                        <td>
                            user.name
                        </td>
                        <td>
                            ...
                        </td>
                    </tr>
                )
            }
        }

        return (
            <table className="seating-table table table-bordered table-responsive-md">
                <thead className="sr-only">
                <tr>
                    <th>Name</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                {
                    seats
                }
                </tbody>
            </table>
        )
    }
}

export default SeatingTable
