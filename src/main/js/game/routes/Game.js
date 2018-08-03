import React from "react"
import cx from "classnames"
import { connect } from "react-redux"
import GameContainer from "../../components/GameContainer";
import Card from "../../components/Card";
import InputSchema from "domainql-form/lib/InputSchema";
import { getCurrentChannel } from "../reducers";
import {
    joinGame,
    reshuffle,
    deal,
    accept,
    pass
} from "../actions";
import Table from "../../components/Table";
import {
    ListGroup,
    ListGroupItem
} from "reactstrap"
import Icon from "../../components/Icon";

const EMPTY_SEATING = [];

const DEAL = 0;
const RESPOND = 1;
const BID = 2;
const CONTINUE = 3;

const Position = [
    "DEAL",
    "RESPOND",
    "BID",
    "CONTINUE"
];

class Game extends React.Component {

    componentDidMount()
    {
        const { match, joinGame, currentChannel } = this.props;

        if (!currentChannel)
        {
            joinGame(match.params.id);
        }
    }

    reshuffle = () => {

        const  { reshuffle, currentChannel } = this.props;

        reshuffle(currentChannel.id);

    };

    deal = () => {

        const  { deal , currentChannel } = this.props;

        deal(currentChannel.id);
    };

    accept = () => {

        const  { accept , currentChannel } = this.props;

        const { bidding, hand : { currentPosition }} = currentChannel.current;

        const isBiddingTurn = bidding.bids.length === 0 || bidding.bids[0].position === bidding.responder;
        let currentBid;
        if (currentPosition === BID)
        {
            currentBid = bidding.nextValue;
        }
        else if (currentPosition === RESPOND)
        {
            if (isBiddingTurn)
            {
                return;
            }

            currentBid = bidding.bids[0].value;
        }
        else
        {
            return;
        }

        console.log("accept currentBid=", currentBid, "isBiddingTurn = ", isBiddingTurn, "bidding = ", bidding)

        accept(currentChannel.id, currentBid);
    };

    pass = () => {

        const  { pass , currentChannel } = this.props;

        pass(currentChannel.id);
    };

    render()
    {
        const {match, currentChannel, formConfig} = this.props;

        if (!currentChannel)
        {
            return false;
        }

        const { current, users, chatMessages } = currentChannel;


        const { seating, numberOfSeats, currentDealer, phase } = current;

        const { bidding, hand } = current;
        const participate = !!hand;

        const seats = [];

        const seated = {};

        let allSeatsOccupied = true;

        for (let i=0; i < numberOfSeats; i++)
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

            seats.push(
                <tr
                    key={ "s" + i }
                    className={
                        cx(
                            "seat",
                            !isActive && "text-muted"
                        )
                    }
                >
                    <td>
                        <Icon className="fa-couch"/>
                        {
                            ": "
                        }
                        {
                            participate &&
                            <span
                                className={ cx(current.hand.gameUser.name === name && "current") }>
                            {
                                name
                            }
                        </span>
                        }
                        <span className="pos text-muted fa-pull-right">
                        {
                            " ( " + pos + " ) "
                        }
                    </span>
                    </td>
                    <td>
                    <div className="fa-pull-right">
                        {
                            !seatOccupied && <button
                                type="button"
                                className="btn btn-secondary"
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

        for (let i=0; i < users.length; i++)
        {
            const user = users[i];
            if (!seated[user.name])
            {
                seats.push(
                    <tr
                        key={ i }
                        className={
                            !user.active && "text-muted"
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


        const currentPosition = participate ? hand.currentPosition : -1;
        const isBiddingTurn = bidding.bids.length === 0 || bidding.bids[0].position === bidding.responder;
        const currentBid = isBiddingTurn ? bidding.nextValue : bidding.bids[0].value;

        return (
            <div className="row">
                <div
                    id="game-main"
                    className="col-md-8"
                >
                    <GameContainer>
                        <GameContainer.Consumer>
                            {
                                ({width, height}) => {


                                    let scale, xStart, effectiveWidth;
                                    if (width > height)
                                    {
                                        scale = (1000 / height);

                                        xStart = -( (width- 100 -height)/2  * scale);
                                        effectiveWidth = ( width - 100 )* scale;
                                    }
                                    else
                                    {
                                        scale = (1000 / width);
                                        xStart = 5 * scale;
                                        effectiveWidth = (width - 10) * scale;
                                    }

                                    const cardWidth = effectiveWidth / 10;

                                    return (
                                        <React.Fragment>
                                            <Table
                                                seating={ current.seating || EMPTY_SEATING }
                                                numberOfSeats={ current.numberOfSeats }
                                                current={ participate ? hand.gameUser.name : null }
                                            />
                                            {
                                                participate && <React.Fragment>
                                                    {
                                                        hand.cards.map((card, index) => (
                                                                <Card
                                                                    key={index}
                                                                    x={xStart + index * cardWidth}
                                                                    y={ 1000 - cardWidth / 0.6 }
                                                                    width={ cardWidth }
                                                                    index={ card }/>
                                                            )
                                                        )
                                                    }
                                                </React.Fragment>
                                            }

                                        </React.Fragment>

                                    );
                                }
                            }

                        </GameContainer.Consumer>

                    </GameContainer>
                </div>
                <div
                    id="game-side"
                    className="col-md-4"
                    style={
                        {
                            backgroundColor: "#eee"
                        }
                    }
                >
                    <h3>
                        {
                            "Game '" + match.params.id + "' (" + current.phase + ")"
                        }
                    </h3>
                    <table className="seating table table-hover table-responsive-md">
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
                    <hr/>
                    {
                        phase === "OPEN" && currentPosition === DEAL &&
                        <div className="btn-toolbar">

                            <button
                                type="button"
                                className="btn btn-secondary"
                                onClick={ this.reshuffle }
                            >
                                <Icon className="fa-random"/>
                                { " Reshuffle" }
                            </button>
                            { "\u00a0" }
                            <button
                                type="button"
                                className="btn btn-primary"
                                onClick={ this.deal }
                                disabled={!allSeatsOccupied}
                            >
                                <Icon className="fa-play"/>
                                { " Deal" }
                            </button>
                        </div>
                    }
                    {
                        phase === "BIDDING" && currentPosition === bidding.bidder &&
                        <div className="btn-toolbar">

                            <button
                                type="button"
                                className="btn btn-danger"
                                onClick={ this.pass }
                                disabled={ !isBiddingTurn }
                            >
                                <Icon className="fa-times"/>
                                { " Pass" }
                            </button>
                            { "\u00a0" }
                            <button
                                type="button"
                                className="btn btn-success"
                                onClick={ this.accept }
                            >
                                <Icon className="fa-angle-double-up"/>
                                { (isBiddingTurn ? " Bid " : " Raise ") + bidding.nextValue }
                            </button>
                        </div>
                    }
                    {
                        phase === "BIDDING" && currentPosition === bidding.responder &&
                        <div className="btn-toolbar">

                            <button
                                type="button"
                                className="btn btn-danger"
                                disabled={ isBiddingTurn }
                                onClick={ this.pass }
                            >
                                <Icon className="fa-times"/>
                                { " Pass" }
                            </button>
                            { "\u00a0" }
                            <button
                                type="button"
                                className="btn btn-success"
                                disabled={ isBiddingTurn }
                                onClick={ this.accept }
                            >
                                <Icon className="fa-check"/>
                                { " Accept " + currentBid }
                            </button>
                        </div>
                    }
                    {
                        <div className="chat-messages">
                            {
                                !chatMessages.length && <span className="text-muted">No messages</span>
                            }
                            {
                                chatMessages.map( (chatMessage, idx) => (
                                    <div key={ idx }>
                                        {
                                            chatMessage.user === "SYSTEM" ?
                                                <Icon className="fa-asterisk text-info" title={ chatMessage.timestamp }/> : <pre>{ "<" + chatMessage.user +">" }</pre>
                                        }
                                        {
                                            " " + chatMessage.message
                                        }
                                    </div>

                                ))
                            }

                        </div>
                    }
                </div>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        currentChannel: getCurrentChannel(state)
    }
};

const mapDispatchToProps = ({
    joinGame,
    reshuffle,
    deal,
    accept,
    pass
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Game)
