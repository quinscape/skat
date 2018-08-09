import React from "react"
import Card from "../Card";
import debounce from "lodash.debounce"

import GAME_CONTAINER_CSS from "../game-layout.css"

const Context = React.createContext({
    width: 400,
    height: 400
});

class GameContainer extends React.Component {

    state = {
        width: 400,
        height: 400,
        chatTop: 100,
        fullHeight: true
    };

    onResize = debounce(() => {

            const main = document.getElementById("game-main").getBoundingClientRect();
            const side = document.getElementById("game-side").getBoundingClientRect();

            const width = main.width;
            let fullHeight = false;
            const chatTop = document.getElementById("chat-messages").getBoundingClientRect().y;
            let height;

            // if we're displayed side by side
            if (side.x >= main.x + main.width - 1)
            {
                // we use the available height
                height = window.innerHeight - 1;
                fullHeight = true;
            }
            else
            {
                // we use a square container
                // noinspection JSSuspiciousNameCombination
                height = main.width;
            }

//            console.log("full-height", fullHeight);

            this.setState({
                width: width - 15,
                height,
                chatTop,
                fullHeight
            });

        },
        100,
        {
            // simulate superior reactivity ;)
            leading: true
        }
    );

    componentDidMount()
    {
        window.addEventListener("resize", this.onResize, true)

        this.onResize();
    }

    componentWillUnmount()
    {
        window.removeEventListener("resize", this.onResize, true)
    }

    registerContainer = c => this._container = c;

    render()
    {

        const { children } = this.props;
        const { height } = this.state;

        return (
            <div ref={ this.registerContainer } className="row game-container">
                {
                    <Context.Provider value={ this.state }>
                        {
                            children
                        }
                    </Context.Provider>
                }
            </div>
        )
    }

    static Consumer = Context.Consumer;
}

export default GameContainer
