import React from "react";

import {
    Route,
    Switch
} from "react-router-dom"

import { ConnectedRouter } from "connected-react-router"
import {
    Collapse,
    Container,
    Nav,
    Navbar,
    NavbarBrand,
    NavbarToggler,
    NavItem,
} from "reactstrap"

import About from "./routes/About";
import Home from "./routes/Home";
import Game from "./routes/Game";
import Admin from "./routes/Admin";
import LogoutForm from "../components/LogoutForm";


class SkatCardsGame extends React.Component {

    state = {
        isNavExpanded: false
    };

    toggle = () => this.setState({isNavExpanded: !this.state.isNavExpanded});

    render()
    {
        const {store, history} = this.props;
        const { foos, isNavExpanded } = this.state;

        return (
            <ConnectedRouter history={ history }>
                <Container fluid={ false }>
                    <Navbar color="dark" dark expand="md">

                        <NavbarBrand href="/">
                            Skat-Cards
                        </NavbarBrand>
                        <NavbarToggler onClick={ this.toggle }/>
                        <Collapse isOpen={ isNavExpanded } navbar>
                            <Nav className="ml-auto" navbar>
                                <NavItem>
                                    <a
                                        className="btn nav-link"
                                        href={ "/admin/" }
                                    >
                                        Admin
                                    </a>
                                </NavItem>
                            </Nav>
                        </Collapse>
                    </Navbar>
                    <Switch>
                        <Route exact path="/game/" component={ Home }/>
                        <Route path="/game/:id:/" component={ Game }/>
                        <Route path="/game/about" component={ About }/>
                    </Switch>
                    <hr/>
                    <div>
                        <LogoutForm/>
                    </div>

                </Container>
            </ConnectedRouter>
        )
    }
}


export default SkatCardsGame

