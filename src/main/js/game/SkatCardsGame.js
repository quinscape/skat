import React from "react";
import { connect } from "react-redux"

import {
    Route,
    Switch
} from "react-router-dom"

import { ConnectedRouter } from "connected-react-router"
import { Container, } from "reactstrap"

import About from "./routes/About";
import Lobby from "./routes/Lobby";
import Game from "./routes/Game";
import LogoutForm from "../components/LogoutForm";
// noinspection ES6UnusedImports
import GAME_CSS from "./game.css"
import { } from "./reducers";
import { flushGames } from "./actions";


class SkatCardsGame extends React.Component {

    state = {
        isNavExpanded: false
    };

    toggle = () => this.setState({isNavExpanded: !this.state.isNavExpanded});

    render()
    {
        const {store, history, flushGames } = this.props;

        return (
            <ConnectedRouter history={ history }>
                <Container fluid={ true }>
                        <Switch>
                            <Route exact path="/game/:id" component={ Game }/>
                            <Route path="/game/about" component={ About }/>
                            <Route exact path="/game/" component={ Lobby }/>
                        </Switch>
                    <hr/>
                    <div id="footer" className="clearfix">
                        <button
                            type="button"
                            className="btn btn-link"
                            onClick={ flushGames }
                        >
                            <strong>
                                DEBUG: Flush Games
                            </strong>
                        </button>
                        <LogoutForm/>
                    </div>

                </Container>
            </ConnectedRouter>
        )
    }
}


const mapStateToProps = state => {
    return {
    }
};

const mapDispatchToProps = ({
    flushGames
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(SkatCardsGame)


