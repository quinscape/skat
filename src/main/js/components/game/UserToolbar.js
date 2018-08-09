import React from "react"
import cx from "classnames"
import { connect } from "react-redux"
import Icon from "../Icon";
import {
    getCurrentChannel,
    getUserConfig
} from "../../game/reducers/index";
import {
    accept,
    deal,
    joinGame,
    pass,
    reshuffle,
    startNewRound
} from "../../game/actions/index";
import { getCalculatorResult } from "../../game/reducers";

const DEAL = 0;
const RESPOND = 1;
const BID = 2;
const CONTINUE = 3;


class UserToolbar extends React.Component {

    reshuffle = () => {

        const { reshuffle, currentChannel } = this.props;

        reshuffle(currentChannel.id);

    };

    deal = () => {

        const { deal, currentChannel } = this.props;

        deal(currentChannel.id);
    };

    accept = () => {

        const { accept, currentChannel } = this.props;

        const { bidding, hand: {currentPosition }} = currentChannel.current;

        const isBiddingTurn = bidding.bidTurn;
        let currentBid;
        if (currentPosition === bidding.bidder)
        {
            // can overbid / raise
            currentBid = bidding.nextValue;
        }
        else if (currentPosition === bidding.responder)
        {
            // can only respond in turn
            if (isBiddingTurn)
            {
                return;
            }

            currentBid = bidding.currentBid;
        }
        else
        {
            return;
        }

//        console.log("accept currentBid=", currentBid, "isBiddingTurn = ", isBiddingTurn, "bidding = ", bidding)

        accept(currentChannel.id, currentBid);
    };

    pass = () => {

        const { pass, currentChannel } = this.props;

        pass(currentChannel.id);
    };


    areAllSeatsOccupied()
    {
        const { currentChannel } = this.props;

        const { current } = currentChannel;

        const { seating, numberOfSeats } = current;

        let allSeatsOccupied = true;

        for (let i = 0; i < numberOfSeats; i++)
        {
            const seatOccupied = i < seating.length;
            if (!seatOccupied)
            {
                allSeatsOccupied = false;
            }
        }

        return allSeatsOccupied;

    }

    startNewRound = () => {

        const { currentChannel, startNewRound } = this.props;

        startNewRound(currentChannel.id);
    };
    render()
    {
        const { currentChannel, calculatorResult, userConfig } = this.props;

//        console.log("UserToolbar, result = ", calculatorResult);

        if (!currentChannel)
        {
            return false;
        }

        const { current } = currentChannel;

        const { phase, bidding, hand, currentDealer } = current;

        // are we part of the current game?
        const doesParticipate = !!hand;

        const currentPosition = doesParticipate ? hand.currentPosition : null;
        const isBiddingTurn = bidding.bidTurn;
        const currentBid = isBiddingTurn ? bidding.nextValue : bidding.currentBid;
                                                                
        const allSeatsOccupied = this.areAllSeatsOccupied();

        return (
            <React.Fragment>
                {
                    (
                        (phase === "OPEN" && currentPosition === "DEAL")
                    ) &&
                    <React.Fragment>
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
                            disabled={ !allSeatsOccupied }
                        >
                            <Icon className="fa-play"/>
                            { " Deal" }
                        </button>
                    </React.Fragment>
                }
                {
                    phase === "BIDDING" && currentPosition === bidding.bidder &&
                    <React.Fragment>

                        <button
                            type="button"
                            className="btn btn-secondary"
                            onClick={ this.pass }
                            disabled={ !isBiddingTurn }
                        >
                            <Icon className="fa-times"/>
                            { " Pass" }
                        </button>
                        { "\u00a0" }
                        <div
                            className="input-group"
                        >
                            <div
                                className="input-group-prepend"
                            >

                                <button
                                    type="button"
                                    className={
                                        cx(
                                            "btn",
                                            calculatorResult !== null ? (bidding.nextValue > calculatorResult ? "btn-danger" : "btn-success") : "btn-secondary"
                                        )
                                    }
                                    onClick={ this.accept }
                                    disabled={
                                        userConfig.lockBidding &&
                                        bidding.nextValue > calculatorResult
                                    }

                                >
                                    <Icon className="fa-angle-double-up"/>
                                    { (isBiddingTurn ? " Bid " : " Raise ") + bidding.nextValue }
                                </button>
                                {
                                    calculatorResult !== null && (
                                        <span className={
                                            cx(
                                                "form-control-plaintext",
                                                bidding.nextValue > calculatorResult ? "text-danger" : "text-success"
                                            )
                                        }>
                                            { "\u00a0Limit: " + calculatorResult }
                                        </span>
                                    )
                                }
                            </div>
                        </div>
                    </React.Fragment>
                }
                {
                    phase === "BIDDING" && currentPosition === bidding.responder &&
                    <React.Fragment>

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

                        <div
                            className="input-group"
                        >
                            <div
                                className="input-group-prepend"
                            >

                                <button
                                    type="button"
                                    className={
                                        cx(
                                            "btn",
                                            calculatorResult !== null ? (currentBid > calculatorResult ? "btn-danger" : "btn-success") : "btn-secondary"
                                        )
                                    }
                                    disabled={
                                        isBiddingTurn ||
                                        (
                                            userConfig.lockBidding &&
                                            currentBid > calculatorResult
                                        )
                                    }
                                    onClick={this.accept}
                                >
                                    <Icon className="fa-check"/>
                                    {" Accept " + currentBid}
                                </button>
                                {
                                    calculatorResult !== null && (
                                        <span className={
                                            cx(
                                                "form-control-plaintext",
                                                currentBid > calculatorResult ? "text-danger" : "text-success"
                                            )
                                        }>
                                            { "\u00a0Limit: " + calculatorResult }
                                        </span>
                                    )
                                }
                            </div>
                        </div>
                    </React.Fragment>
                }
                {
                    phase === "FINISHED" &&
                    <button
                        type="button"
                        className="btn btn-secondary"
                        onClick={ this.startNewRound }
                    >
                        <Icon className="fa-recycle"/>
                        { " Start new round" }
                    </button>
                }
            </React.Fragment>

        );
    }
}

const mapStateToProps = state => {
    return {
        currentChannel: getCurrentChannel(state),
        calculatorResult: getCalculatorResult(state),
        userConfig: getUserConfig(state)
    }
};

const mapDispatchToProps = ({
    joinGame,
    reshuffle,
    deal,
    accept,
    pass,
    startNewRound
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(UserToolbar)
