import React from "react"
import cx from "classnames"
import PropTypes from "prop-types"
import { getLoadedSymbols } from "../services/loader"
import { SuitName, CardName } from "../util/cards";

//import SVG_SYMBOLS from "../../webapp/media/deck.svg"


function findGroup(elem)
{
    while (elem)
    {
        if (elem.tagName === "g" && elem.className.baseVal === "card")
        {
            return elem;
        }

        elem = elem.parentNode;
    }
    return null;
}

/**
 * Human readable description of a card
 *
 * @param type      Different types of dispay ("jack" for Jacks, "
 * @param card
 * @return {string}
 */
export function getCardName(type, card)
{
    let correctedIndex = (Math.abs(card) - 1);

    if (correctedIndex === 32)
    {
        return "unknown Card";
    }

    if (type === "jack")
    {
        return "Jack of " + SuitName[correctedIndex];
    }
    else if (type === "suit")
    {
        return "Suit " + SuitName[correctedIndex];
    }
    else if (type === "marker")
    {
        return "Location Marker";
    }

    const suit = (correctedIndex/8)|0;
    const face = (correctedIndex & 7);
    return CardName[face] + " of " + SuitName[suit]
}

class Card extends React.Component {


    static defaultProps = {
        opacity: null,
        width : -1,
        type: "card"
    };

    static propTypes = {
        card: PropTypes.number.isRequired,
        x: PropTypes.number.isRequired,
        y: PropTypes.number.isRequired,
        width: PropTypes.number,
        opacity: PropTypes.number,
        type: PropTypes.string,
        onClick: PropTypes.func
    };

    shouldComponentUpdate(nextProps, nextState)
    {
        const { card, x, y, width, opacity, className } = this.props;
        const { nextCard, nextX, nextY, nextWidth, nextOpacity, nextClassName } = nextProps;

        return card !== nextCard || x !== nextX || y !== nextY || width !== nextWidth || opacity !== nextOpacity || className !== nextClassName;
    }


    onClick = ev => {
        const { onClick } = this.props;
        const target = findGroup(ev.target);

        if (onClick)
        {
            onClick( target.dataset.id);
        }
    };

    render()
    {
        const { id, card, x, y, width : widthFromProps, type, opacity, className } = this.props;
        const symbols = getLoadedSymbols();

        const correctedIndex = (Math.abs(card) - 1);
        const symbol = symbols[type + "-" + correctedIndex];

        const w = symbol.aabb.width;
        const h = symbol.aabb.height;

        const centerX = (symbol.aabb.x);
        const centerY = (symbol.aabb.y);

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
        // if (card < 0)
        // {
        //     rotate = " rotate( 180 " + (centerX + width/2) + " " + (centerY + h/2 * scale) + ")";
        // }


        const transform =
            symbol.transform +
            rotate +
            " translate(" + (x) + " " + ( y ) + ")" +
            " scale(" + scale + ") " +
            " translate(" + (-centerX) + " " + (-centerY) + ")";


        return(
            <g
                className={
                    cx(
                        className,
                        "card",
                        correctedIndex === 32 && "face-down"
                    )
                }
                aria-label={ getCardName(type, card) }
                transform={transform}
                dangerouslySetInnerHTML={ symbol.snippet }
                opacity={ opacity }
                onClick={ this.onClick }
                data-id={ id }
            />
        );
    }
}

export default Card
