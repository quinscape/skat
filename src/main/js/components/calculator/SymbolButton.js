import React from "react"

import Card from "../Card";
import { SuitName } from "./Calculator";

export const GOLDEN_RATIO = (1 + Math.sqrt(5)) / 2;

export default function SymbolButton(props)
{
    const { index, onClick, value: jackPresent, type, disabled } = props;

    const correctedIndex = Math.abs(index) - 1;

    const label = (jackPresent ? "Jack of " : "No jack of " ) + SuitName[correctedIndex];
    return (
        <button
            type="button"
            className="btn btn-secondary p-0 m-0 border-0"
            aria-label={ label }
            title={ label }
            onClick={ onClick }
            data-jack={ index }
            disabled={ disabled }
            style={{
                backgroundColor: "transparent"
            }}
        >
            <svg
                width={ 60 }
                height={ (60 * GOLDEN_RATIO) | 0 }
                viewBox={ "6 6 68 " + ((68 * GOLDEN_RATIO) | 0) }
                style={{
                    backgroundColor: "transparent",
                    margin: 0
                }}
            >
                <Card
                    type={ type }
                    card={ index }
                    opacity={ jackPresent ? 1 : 0.2 }
                    x={ 0 }
                    y={ 0 }
                    width={ 80 }
                />
            </svg>
        </button>

    )
}
