import React from "react"
import cx from "classnames"

import {
    GameType,
    GameTypeName,
    getJackGameType,
    SuitName
} from "./Calculator";


export default function GameTypeRadios( props )
{
    const { values, handleChange, handleBlur } = props;

    const { gameType } = values;

    const jackGameType = getJackGameType(values.jacks);

    const jackMultiplier = jackGameType !== null ? (jackGameType & 3) + 1 : 0;

    return (
        <React.Fragment>
            {
                SuitName.map((suit, index) => {

                    const id = "suit-game" + index;
                    const strValue = String(index);

                    return (
                        <div
                            key={ suit }
                            className="form-check"
                        >


                            <input
                                id={ id }
                                type="radio"
                                className="form-check-input"
                                name="gameType"
                                value={ strValue }
                                checked={ gameType === strValue }
                                onChange={ handleChange }
                                onBlur={ handleBlur }
                                disabled={ jackGameType === null }
                            />
                            < label
                                htmlFor={ id }
                                className="form-check-label"

                            >
                                <strong
                                    className={
                                        cx(
                                            index < 2 ?
                                                "suit-black" :
                                                "suit-red",
                                            jackGameType === null && "text-muted"
                                        )}>
                                    {
                                        jackMultiplier > 0 ? GameTypeName[jackGameType] + ", " + suit : "No " + suit + " Game"
                                    }
                                </strong>
                            </label>
                        </div>
                    )
                })
            }
            <div className="form-check">
                <input
                    id="game-type-null"
                    className="form-check-input"
                    type="radio"
                    name="gameType"
                    value={ String(GameType.NULL) }
                    checked={ +gameType === GameType.NULL }
                    onChange={ handleChange }
                    onBlur={ handleBlur }
                />
                <label
                    className="form-check-label"
                    htmlFor="game-type-null"
                >
                    Null
                </label>
            </div>
            <div className="form-check">
                <input
                    id="game-type-grand"
                    className="form-check-input"
                    type="radio"
                    name="gameType"
                    value={ String(GameType.GRAND) }
                    checked={ +gameType === GameType.GRAND }
                    onChange={ handleChange }
                    onBlur={ handleBlur }
                    disabled={ jackGameType === null }
                />
                <label
                    className="form-check-label"
                    htmlFor="game-type-grand"
                >
                    {
                        jackGameType === null ? "No Grand" : "Grand"
                    }
                </label>
            </div>

        </React.Fragment>
    );
}
