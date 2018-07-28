import React from "react";

import { Provider } from "react-redux"

import {
    NavLink as RouterNavLink,
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
import AdminHome from "./routes/AdminHome";
import LogoutForm from "../components/LogoutForm";

class App extends React.Component {

    state = {
        isNavExpanded: false
    };

    toggle = () => this.setState({isNavExpanded: !this.state.isNavExpanded});

    render()
    {
        const {store, history} = this.props;

        return (
            <Provider store={ store }>
                <ConnectedRouter history={ history }>
                    <Container fluid={ false }>
                        <Navbar color="dark" dark expand="md">

                            {/* TODO: replace branding */}
                            <NavbarBrand href="/">skat</NavbarBrand>
                            <NavbarToggler onClick={ this.toggle }/>
                            <Collapse isOpen={ this.state.isNavExpanded } navbar>
                                <Nav className="ml-auto" navbar>
                                    <NavItem>
                                        <RouterNavLink
                                            className="nav-link"
                                            to="/admin/about/"
                                        >
                                            About
                                        </RouterNavLink>
                                    </NavItem>
                                </Nav>
                            </Collapse>
                        </Navbar>
                        <Switch>
                            <Route exact path="/admin/" component={ AdminHome }/>
                            <Route path="/admin/about" component={ About }/>
                        </Switch>
                        <hr/>
                        <div>
                            <LogoutForm/>
                        </div>

                    </Container>
                </ConnectedRouter>
            </Provider>
        )
    }
}


export default App

