import React from "react"
import cx from "classnames"
import { connect } from "react-redux"
import Icon from "../Icon";
import GameContainer from "./GameContainer";
import {
    getCurrentChannel
} from "../../game/reducers/index";
import {
    sendChatMessage
} from "../../game/actions/index";
import {
    CardShortName,
    SuitSymbol
} from "../../util/cards";


function CardLog(props)
{
    const { card } = props;


    const c = Math.abs(card) - 1;

    const suit = c >> 3;
    const face = c & 7;

    return (
        <span className="large">
            <span
                className="badge badge-light badge-pill"
            >
                <span
                    className={
                        cx(
                            suit < 2 ? "card-log-red" : "card-log-black"
                        )
                    }
                >
                    {
                        SuitSymbol[suit] + CardShortName[face]
                    }
                </span>
            </span>
        </span>
    )
}

class ChatWidget extends React.Component {

    state = {
        message: ""
    };

    sendChatMessage = () => {

        const {currentChannel, sendChatMessage} = this.props;
        sendChatMessage(currentChannel.id, this.state.message)
            .then(() => this.setState({message: ""}))
            .catch(err => console.error("Error sending chat message", err));
    };

    changeMessage = ev => this.setState({message: ev.target.value});

    render()
    {
        const {currentChannel} = this.props;
        const {message} = this.state;

        const {logEntries} = currentChannel;

        return (
            <GameContainer.Consumer>
                {
                    ({width, height, fullHeight, chatTop}) => (

                        <React.Fragment>
                            <div
                                id="chat-messages"
                                className={"small"}
                                style={
                                    fullHeight ? {
                                        height: height - 125 - chatTop,
                                        overflow: "auto"
                                    } : null
                                }
                            >
                                {
                                    !logEntries.length &&
                                    <span className="text-muted">No messages</span>
                                }
                                {
                                    logEntries.map((logEntry, idx) => (
                                        <div key={idx}>
                                            {
                                                logEntry.type === "TEXT" && (
                                                    <React.Fragment>
                                                        {
                                                            logEntry.user === "SYSTEM" ?
                                                                <Icon
                                                                    className="fa-asterisk text-info"
                                                                    title={logEntry.timestamp}
                                                                /> :
                                                                <span
                                                                    className="text-monospace">{"<" + logEntry.user + ">"}
                                                            </span>
                                                        }
                                                        {
                                                            " " + logEntry.message
                                                        }
                                                    </React.Fragment>
                                                )
                                            }
                                            {
                                                logEntry.type === "ACTION" &&
                                                <span
                                                    className="text-monospace"
                                                >
                                                    {
                                                        "*" + logEntry.user + " " + logEntry.message
                                                    }
                                                </span>
                                            }
                                            {
                                                (
                                                    logEntry.type === "WIN" ||
                                                    logEntry.type === "WIN_GAME"
                                                ) && (
                                                    <span
                                                        className="text-monospace"
                                                    >
                                                        {
                                                            "*" + logEntry.message
                                                        }
                                                    </span>
                                                )
                                            }
                                            {
                                                logEntry.type === "CARD" && (
                                                    <span
                                                        className="text-monospace"
                                                    >
                                                    <Icon
                                                        className="fa-asterisk text-info"
                                                        title={logEntry.timestamp}
                                                    />
                                                    {
                                                        " " + logEntry.user + " plays "
                                                    }
                                                    {
                                                        <CardLog card={ logEntry.card } />
                                                    }
                                                </span>
                                                )
                                            }
                                            {
                                                logEntry.type === "SKAT" && (
                                                    <span
                                                        className="text-monospace"
                                                    >
                                                    <Icon
                                                        className="fa-info-circle text-info"
                                                        title={logEntry.timestamp}
                                                    />
                                                        {
                                                        " Skat card "
                                                    }
                                                    {
                                                        <CardLog card={ logEntry.card } />
                                                    }
                                                </span>
                                                )
                                            }
                                        </div>

                                    ))
                                }
                            </div>
                            <form className="form form-horizontal" action="#" onSubmit={this.sendChatMessage}>
                                <div className="form-group">
                                    <input
                                        type="text"
                                        aria-label="Chat Input"
                                        className="form-control col-md-12 form-control-sm mt-2"
                                        value={message}
                                        onChange={this.changeMessage}
                                    />
                                </div>
                            </form>
                        </React.Fragment>
                    )}
            </GameContainer.Consumer>
        )
    }

}

const mapStateToProps = state => {
    return {
        currentChannel: getCurrentChannel(state)
    }
};

const mapDispatchToProps = ({
    sendChatMessage
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ChatWidget)
