import React from "react"
import PropTypes from "prop-types"
import { getLoadedSymbols } from "../services/loader"

//import SVG_SYMBOLS from "../../webapp/media/deck.svg"


class Card extends React.Component {

    static propTypes = {
        index: PropTypes.number.isRequired,
        x: PropTypes.number.isRequired,
        y: PropTypes.number.isRequired,
        width: PropTypes.number
    };

    static defaultProps = {
        width : -1
    };

    shouldComponentUpdate(nextProps, nextState)
    {
        const { index, x, y, width } = this.props;
        const { nextIndex, nextX, nextY, nextWidth } = nextProps;

        return index !== nextIndex || x !== nextX || y !== nextY || width !== nextWidth;
    }

    render()
    {
        const { index, x, y, width : widthFromProps} = this.props;
        const symbols = getLoadedSymbols();


        const symbol = symbols["card-" + (Math.abs(index) - 1)];

        const w = symbol.aabb.width;
        const h = symbol.aabb.height;

        const cx = (symbol.aabb.x);
        const cy = (symbol.aabb.y);

        let width;
        let scale;
        if (widthFromProps <= 0)
        {
            width = w;
            scale = 1;
        } else
        {
            width = widthFromProps;
            scale = width / w;
        }

        let rotate = "";
        // if (index < 0)
        // {
        //     rotate = " rotate( 180 " + (cx + width/2) + " " + (cy + h/2 * scale) + ")";
        // }


        const transform =
            symbol.transform +
            rotate +
            " translate(" + (x) + " " + ( y ) + ")" +
            " scale(" + scale + ") " +
            " translate(" + (-cx) + " " + (-cy) + ")";

        return <React.Fragment>
            <g
                transform={transform}
                dangerouslySetInnerHTML={ symbol.snippet }
            />
            <rect
                x={ x - 2}
                y={ y - 2}
                width={4}
                height={4}
                style={{
                    fill: "#f0f"
                }}
            >
            </rect>
        </React.Fragment>
    }
}

export default Card
