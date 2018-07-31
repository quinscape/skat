import React from "react"
import ResourceLoader from "../../components/ResourceLoader";
import media from "../media";
import config from "../../services/config";

import { connect } from "react-redux"
import { Link } from "react-router-dom"
import {
    ListGroup,
    ListGroupItem
} from "reactstrap"
import { getGameList } from "../reducers";
import Icon from "../../components/Icon";
import randomName from "../../util/randomName";
import {
    createGame,
    joinGame
} from "../actions";


class Lobby extends React.Component {

    randomChannel = () =>
    {
        const { gameList, joinGame } = this.props;


        if (gameList.rowCount === 0)
        {
            const name = randomName();
            this.props.createGame(name).then( () => {
                console.log("createGame finished")
                return joinGame(name);
            });
        }
        else
        {
            const { channels } = gameList;

            const skatGame = channels[(Math.random() * channels.length)|0];

            joinGame(skatGame.id);
        }
    };

    joinGame = ev => {

        const { joinGame } = this.props;

        joinGame(ev.target.dataset.gameId)
    };

    render()
    {
        const { gameList } = this.props;

        return (
            <div>
                <h1> Lobby </h1>
                <ResourceLoader
                    images={ media }
                >
                    <p className="text-md-center">
                        <img src={ config().contextPath + media.LOGO } />
                    </p>

                    <ListGroup>
                            <ListGroupItem className="bg-light">
                                <button
                                    type="button"
                                    className="btn btn-link"
                                    onClick={ this.randomChannel }
                                >
                                    <Icon className="fa-random"/>
                                    { " Random Game"}
                                </button>
                            </ListGroupItem>
                            {
                                gameList.channels.map(
                                    skatGame => (
                                    <ListGroupItem
                                        key={ skatGame.id }
                                    >
                                        <button
                                            type="button"
                                            className="btn btn-link"
                                            data-game-id={ skatGame.id }
                                            onClick={ this.joinGame  }
                                        >
                                        {
                                            skatGame.id
                                        }
                                        </button>

                                    </ListGroupItem>
                                    )
                                )
                            }
                            {
                                gameList.rowCount === 0 && <ListGroupItem>
                                    <em className="text-muted" >No Listed Games</em>
                                </ListGroupItem>
                            }
                    </ListGroup>
                    <div className="btn-toolbar float-right">
                        <button className="btn btn-secondary" type="button" onClick={ ev => console.log("addy")}>
                            <Icon className="fa-users"/>
                            { " Create private game" }
                        </button>
                    </div>
                    <br/>

                </ResourceLoader>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        gameList: getGameList(state)
    }
};

const mapDispatchToProps = ({
    createGame,
    joinGame
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Lobby)

