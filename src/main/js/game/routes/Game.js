import React from "react"
import { connect } from "react-redux"
import GameContainer from "../../components/GameContainer";
import Card from "../../components/Card";
import { getCurrentGame } from "../reducers";
import {
    joinGame
} from "../actions";
import Table from "../../components/Table";


class Game extends React.Component {

    componentDidMount()
    {
        const {match, current, joinGame} = this.props;

        if (!current)
        {
            joinGame(match.params.id);
        }
    }

    render()
    {
        const {match, current} = this.props;
        //console.log("current", current);

        return (
            <div className="row">
                <div
                    id="game-main"
                    className="col-md-8"
                >
                    <GameContainer>
                        <Table/>
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
                                        current &&
                                        current.current.hand.map((card, index) => (
                                                <Card
                                                    key={card}
                                                    x={xStart + index * cardWidth}
                                                    y={ 1000 - cardWidth / 0.6 }
                                                    width={ cardWidth }
                                                    index={card}/>
                                            )
                                        )
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
                    <h2>
                        {
                            "Game '" + match.params.id + "'"
                        }
                    </h2>
                </div>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        current: getCurrentGame(state)
    }
};

const mapDispatchToProps = ({
    joinGame
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Game)

/**
 * 00ff00ff
 * 25be79ff
 *
 * 008000ff
 * 008044ff
 *
 * ffff00
 * ffde00
 *
 * 000080
 * 1e1e77
 *
 * ff0000
 * d82727
 *
 * 808000
 * c48400
 *
 *
 * 1e1e77
 * 23238b
 */
