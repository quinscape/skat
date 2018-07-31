import React from "react"
import Card from "./Card";
import debounce from "lodash.debounce"

import GAME_CONTAINER_CSS from "./game-container.css"

const Context = React.createContext({
    width: 400,
    height: 400
});

class GameContainer extends React.Component {

    state = {
        width: 0,
        height: 0
    };

    onResize = debounce(() => {

            const main = document.getElementById("game-main").getBoundingClientRect();
            const side = document.getElementById("game-side").getBoundingClientRect();

            // if we're displayed side by side
            const width = main.width;
            let height;
            if (side.x >= main.x + main.width - 1)
            {
                // we use the available height minus footer
                height = window.innerHeight - 80;
            }
            else
            {
                // we use a square container
                // noinspection JSSuspiciousNameCombination
                height = main.width;
            }

            this.setState({
                width: width - 15,
                height
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

    registerSVG = c => this._svgContainer = c;

    render()
    {
        const { children } = this.props;
        const { width, height } = this.state;

        return (
            <svg className="game-container" ref={ this.registerSVG } width={ width } height={ height } viewBox="0 0 1000 1000" preserveAspectRatio="xMidYMid meet">
                {
                    height > 0 &&
                        <Context.Provider value={ this.state }>
                            {
                                children
                            }
                        </Context.Provider>
                }
            </svg>
        )
    }

    static Consumer = Context.Consumer;
}

export default GameContainer
